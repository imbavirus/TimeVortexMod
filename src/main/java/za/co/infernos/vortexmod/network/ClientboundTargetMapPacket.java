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

public record ClientboundTargetMapPacket(String dimension, BlockPos targetPos, Map<String, BlockPos> coordData, Map<String, String> dimData) implements CustomPacketPayload {
    public static final Type<ClientboundTargetMapPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "clientbound_target_map"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTargetMapPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundTargetMapPacket::encode, ClientboundTargetMapPacket::decode);

    public static ClientboundTargetMapPacket decode(RegistryFriendlyByteBuf buffer) {
        String dimension = buffer.readUtf();
        BlockPos targetPos = buffer.readBlockPos();

        int coordSize = buffer.readVarInt();
        Map<String, BlockPos> coordData = new HashMap<>(coordSize);
        for (int i = 0; i < coordSize; i++) {
            coordData.put(buffer.readUtf(), buffer.readBlockPos());
        }

        int dimSize = buffer.readVarInt();
        Map<String, String> dimData = new HashMap<>(dimSize);
        for (int i = 0; i < dimSize; i++) {
            dimData.put(buffer.readUtf(), buffer.readUtf());
        }

        return new ClientboundTargetMapPacket(
                dimension,
                targetPos,
                coordData,
                dimData
        );
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ClientboundTargetMapPacket packet) {
        buffer.writeUtf(packet.dimension);
        buffer.writeBlockPos(packet.targetPos);

        buffer.writeVarInt(packet.coordData.size());
        for (Map.Entry<String, BlockPos> entry : packet.coordData.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeBlockPos(entry.getValue());
        }

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
                keypadBlockEntity.coordData = this.coordData;
                keypadBlockEntity.dimData = this.dimData;
            }
        });
    }
}
