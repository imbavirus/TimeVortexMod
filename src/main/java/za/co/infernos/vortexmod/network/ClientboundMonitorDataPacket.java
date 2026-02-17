package za.co.infernos.vortexmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import za.co.infernos.vortexmod.block.entity.MonitorBlockEntity;
import za.co.infernos.vortexmod.VortexMod;

import java.util.HashMap;
import java.util.Map;

public record ClientboundMonitorDataPacket(BlockPos targetPos, Map<Integer, Integer> fromTag, String targetDim,
        String currentDim) implements CustomPacketPayload {
    public static final Type<ClientboundMonitorDataPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "clientbound_monitor_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMonitorDataPacket> STREAM_CODEC = StreamCodec
            .of(ClientboundMonitorDataPacket::encode, ClientboundMonitorDataPacket::decode);

    public static ClientboundMonitorDataPacket decode(RegistryFriendlyByteBuf buffer) {
        BlockPos targetPos = buffer.readBlockPos();
        int mapSize = buffer.readVarInt();
        Map<Integer, Integer> fromTag = new HashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            fromTag.put(buffer.readVarInt(), buffer.readVarInt());
        }
        return new ClientboundMonitorDataPacket(
                targetPos,
                fromTag,
                buffer.readUtf(),
                buffer.readUtf());
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ClientboundMonitorDataPacket packet) {
        buffer.writeBlockPos(packet.targetPos);
        buffer.writeVarInt(packet.fromTag.size());
        for (Map.Entry<Integer, Integer> entry : packet.fromTag.entrySet()) {
            buffer.writeVarInt(entry.getKey());
            buffer.writeVarInt(entry.getValue());
        }
        buffer.writeUtf(packet.targetDim);
        buffer.writeUtf(packet.currentDim);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel clientLevel = client.level;
            if (clientLevel == null)
                return;

            MonitorBlockEntity monitorBlockEntity = (MonitorBlockEntity) clientLevel.getBlockEntity(this.targetPos);
            if (monitorBlockEntity == null)
                return;

            for (Map.Entry<Integer, Integer> entry : this.fromTag.entrySet()) {
                monitorBlockEntity.data.set(entry.getKey(), entry.getValue());
            }

            monitorBlockEntity.target_dimension = this.targetDim;
            monitorBlockEntity.current_dimension = this.currentDim;
        });
    }
}
