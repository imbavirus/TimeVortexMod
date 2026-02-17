package za.co.infernos.vortexmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import za.co.infernos.vortexmod.block.entity.CoordinateDesignatorBlockEntity;
import za.co.infernos.vortexmod.VortexMod;

public record ClientboundIncrementPacket(BlockPos fromPos, int increment, String levelName) implements CustomPacketPayload {
    public static final Type<ClientboundIncrementPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "clientbound_increment"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundIncrementPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundIncrementPacket::encode, ClientboundIncrementPacket::decode);

    public static ClientboundIncrementPacket decode(RegistryFriendlyByteBuf buffer) {
        return new ClientboundIncrementPacket(buffer.readBlockPos(), buffer.readInt(), buffer.readUtf());
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ClientboundIncrementPacket packet) {
        buffer.writeBlockPos(packet.fromPos);
        buffer.writeInt(packet.increment);
        buffer.writeUtf(packet.levelName);
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

            if (clientLevel.dimension().location().getPath().equals(this.levelName)) {
                CoordinateDesignatorBlockEntity designatorBlockEntity =
                        (CoordinateDesignatorBlockEntity) clientLevel.getBlockEntity(this.fromPos);
                if (designatorBlockEntity != null) {
                    designatorBlockEntity.data.set(4, this.increment);
                }
            }
        });
    }
}
