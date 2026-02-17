package za.co.infernos.vortexmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import za.co.infernos.vortexmod.block.entity.KeypadBlockEntity;
import za.co.infernos.vortexmod.VortexMod;

import java.util.HashMap;
import java.util.Map;

public record ClientboundDimListPacket(String dimension, BlockPos targetPos, Map<String, String> dimData) implements CustomPacketPayload {
    public static final Type<ClientboundDimListPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "clientbound_dim_list"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDimListPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundDimListPacket::encode, ClientboundDimListPacket::decode);

    public static ClientboundDimListPacket decode(RegistryFriendlyByteBuf buffer) {
        String dimension = buffer.readUtf();
        BlockPos targetPos = buffer.readBlockPos();

        int dimSize = buffer.readVarInt();
        Map<String, String> dimData = new HashMap<>(dimSize);
        for (int i = 0; i < dimSize; i++) {
            dimData.put(buffer.readUtf(), buffer.readUtf());
        }

        return new ClientboundDimListPacket(
                dimension,
                targetPos,
                dimData
        );
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ClientboundDimListPacket packet) {
        buffer.writeUtf(packet.dimension);
        buffer.writeBlockPos(packet.targetPos);
        buffer.writeVarInt(packet.dimData.size());
        for (Map.Entry<String, String> entry : packet.dimData.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeUtf(entry.getValue());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel clientLevel = client.level;
            if (clientLevel == null) return;

            KeypadBlockEntity keypadBlockEntity = (KeypadBlockEntity) clientLevel.getBlockEntity(this.targetPos);
            if (keypadBlockEntity != null) {
                keypadBlockEntity.serverLevels = this.dimData.values().stream().toList();
            }
        });
    }
}
