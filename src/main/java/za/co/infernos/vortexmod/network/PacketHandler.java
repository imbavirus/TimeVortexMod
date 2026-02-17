package za.co.infernos.vortexmod.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import za.co.infernos.vortexmod.VortexMod;

/**
 * NeoForge 1.21+ networking uses vanilla CustomPacketPayload + StreamCodec, registered via RegisterPayloadHandlersEvent.
 */
@EventBusSubscriber(modid = VortexMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class PacketHandler {
    private PacketHandler() {}

    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // Serverbound
        registrar.playToServer(ServerboundTargetPacket.TYPE, ServerboundTargetPacket.STREAM_CODEC, ServerboundTargetPacket::handle);
        registrar.playToServer(ServerboundSaveTargetPacket.TYPE, ServerboundSaveTargetPacket.STREAM_CODEC, ServerboundSaveTargetPacket::handle);
        registrar.playToServer(ServerboundDeleteTargetPacket.TYPE, ServerboundDeleteTargetPacket.STREAM_CODEC, ServerboundDeleteTargetPacket::handle);
        registrar.playToServer(ServerboundAngelSeenPacket.TYPE, ServerboundAngelSeenPacket.STREAM_CODEC, ServerboundAngelSeenPacket::handle);

        // Clientbound
        registrar.playToClient(ClientboundTargetMapPacket.TYPE, ClientboundTargetMapPacket.STREAM_CODEC, ClientboundTargetMapPacket::handle);
        registrar.playToClient(ClientboundDimListPacket.TYPE, ClientboundDimListPacket.STREAM_CODEC, ClientboundDimListPacket::handle);
        registrar.playToClient(ClientboundMonitorDataPacket.TYPE, ClientboundMonitorDataPacket.STREAM_CODEC, ClientboundMonitorDataPacket::handle);
        registrar.playToClient(ClientboundIncrementPacket.TYPE, ClientboundIncrementPacket.STREAM_CODEC, ClientboundIncrementPacket::handle);
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }

    public static void sendToPlayer(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    public static void sendToAllClients(CustomPacketPayload msg) {
        PacketDistributor.sendToAllPlayers(msg);
    }
}
