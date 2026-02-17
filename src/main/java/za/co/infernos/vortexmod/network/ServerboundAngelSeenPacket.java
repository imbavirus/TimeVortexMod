package za.co.infernos.vortexmod.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import za.co.infernos.vortexmod.entities.custom.AngelEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import za.co.infernos.vortexmod.VortexMod;

import java.util.UUID;

public record ServerboundAngelSeenPacket(UUID angelUuid, String fromDimension) implements CustomPacketPayload {
    public static final Type<ServerboundAngelSeenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "serverbound_angel_seen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAngelSeenPacket> STREAM_CODEC =
            StreamCodec.of(ServerboundAngelSeenPacket::encode, ServerboundAngelSeenPacket::decode);

    public static ServerboundAngelSeenPacket decode(RegistryFriendlyByteBuf buffer) {
        return new ServerboundAngelSeenPacket(buffer.readUUID(), buffer.readUtf());
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ServerboundAngelSeenPacket packet) {
        buffer.writeUUID(packet.angelUuid);
        buffer.writeUtf(packet.fromDimension);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof net.minecraft.server.level.ServerPlayer sender)) return;
            MinecraftServer minecraftServer = sender.getServer();
            if (minecraftServer == null) return;

            ServerLevel level = null;
            for (ServerLevel cLevel : minecraftServer.getAllLevels()) {
                if (cLevel.dimension().location().getPath().equals(this.fromDimension)) {
                    level = cLevel;
                    break;
                }
            }
            if (level == null) return;

            AngelEntity angelEntity = (AngelEntity) level.getEntity(this.angelUuid);
            // TODO: implement "angel seen" behavior (packet was previously a no-op).
        });
    }
}
