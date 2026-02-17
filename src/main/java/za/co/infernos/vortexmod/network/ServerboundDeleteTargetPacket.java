package za.co.infernos.vortexmod.network;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.ModBlocks;
import za.co.infernos.vortexmod.block.entity.CoordinateDesignatorBlockEntity;
import za.co.infernos.vortexmod.block.entity.KeypadBlockEntity;
import za.co.infernos.vortexmod.block.entity.VortexInterfaceBlockEntity;
import za.co.infernos.vortexmod.mapdata.DimensionMapData;
import za.co.infernos.vortexmod.mapdata.LocationMapData;
import za.co.infernos.vortexmod.mapdata.RotationMapData;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;

public record ServerboundDeleteTargetPacket(BlockPos fromPos, String saveName, boolean targetScreen) implements CustomPacketPayload {
    public static final Type<ServerboundDeleteTargetPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "serverbound_delete_target"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDeleteTargetPacket> STREAM_CODEC =
            StreamCodec.of(ServerboundDeleteTargetPacket::encode, ServerboundDeleteTargetPacket::decode);

    public static ServerboundDeleteTargetPacket decode(RegistryFriendlyByteBuf buffer) {
        return new ServerboundDeleteTargetPacket(buffer.readBlockPos(), buffer.readUtf(), buffer.readBoolean());
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ServerboundDeleteTargetPacket packet) {
        buffer.writeBlockPos(packet.fromPos);
        buffer.writeUtf(packet.saveName);
        buffer.writeBoolean(packet.targetScreen);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (this.targetScreen) return;
            if (!(context.player() instanceof ServerPlayer player)) return;

            MinecraftServer minecraftserver = player.getServer();
            if (minecraftserver == null) return;

            ServerLevel tardis_dim = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);
            ServerLevel vortex = minecraftserver.getLevel(ModDimensions.vortexDIM_LEVEL_KEY);
            if (tardis_dim == null || vortex == null) return;

            LocationMapData coord_data = LocationMapData.get(vortex);
            RotationMapData rotation_data = RotationMapData.get(vortex);
            DimensionMapData dim_data = DimensionMapData.get(tardis_dim);
            ServerLevel level = player.serverLevel();
            boolean core_found = false;

            BlockPos corePos = this.fromPos;
            VortexInterfaceBlockEntity vortexInterfaceBlockEntity = null;

            for (int _x = -16; _x <= 16 && !core_found; _x++) {
                for (int _y = -16; _y <= 16 && !core_found; _y++) {
                    for (int _z = -16; _z <= 16 && !core_found; _z++) {
                        BlockPos currentPos = this.fromPos.offset(_x, _y, _z);

                        BlockState blockState = level.getBlockState(currentPos);
                        if (blockState.getBlock() == ModBlocks.INTERFACE_BLOCK.get()) {
                            vortexInterfaceBlockEntity = (VortexInterfaceBlockEntity) level.getBlockEntity(currentPos);
                            core_found = true;
                            corePos = currentPos;
                        }
                    }
                }
            }

            boolean has_components = false;
            boolean has_keypad = false;
            boolean has_designator = false;

            CoordinateDesignatorBlockEntity designatorEntity = null;
            KeypadBlockEntity keypadBlockEntity = null;

            for (int _x = -16; _x <= 16 && !has_components; _x++) {
                for (int _y = -16; _y <= 16 && !has_components; _y++) {
                    for (int _z = -16; _z <= 16 && !has_components; _z++) {
                        BlockPos currentPos = corePos.offset(_x, _y, _z);

                        BlockState blockState = level.getBlockState(currentPos);
                        if (blockState.getBlock() == ModBlocks.KEYPAD_BLOCK.get()) {
                            keypadBlockEntity = (KeypadBlockEntity) level.getBlockEntity(currentPos);
                            has_keypad = true;
                        } else if (blockState.getBlock() == ModBlocks.COORDINATE_BLOCK.get()) {
                            designatorEntity = (CoordinateDesignatorBlockEntity) level.getBlockEntity(currentPos);
                            has_designator = true;
                        }
                        if (has_keypad && has_designator) {
                            has_components = true;
                        }
                    }
                }
            }

            if (core_found && has_components && designatorEntity != null) {
                String dataKey = player.getScoreboardName() + this.saveName;
                if (coord_data.getDataMap().containsKey(dataKey)) {
                    BlockPos savedPos = coord_data.getDataMap().get(dataKey);
                    String savedDimName = dim_data.getDataMap().get(dataKey);

                    coord_data.getDataMap().remove(dataKey);
                    rotation_data.getDataMap().remove(dataKey);
                    dim_data.getDataMap().remove(dataKey);
                    player.displayClientMessage(Component.literal("Deleting " + this.saveName + ". (" + savedPos.getX() + " " + savedPos.getY() + " " + savedPos.getZ() + " | " + savedDimName + ")"), false);

                    coord_data.setDirty();
                    rotation_data.setDirty();
                    dim_data.setDirty();

                    keypadBlockEntity.coordData = coord_data.getDataMap();
                    keypadBlockEntity.dimData = dim_data.getDataMap();
                    PacketHandler.sendToAllClients(new ClientboundTargetMapPacket(level.dimension().location().getPath(), this.fromPos, coord_data.getDataMap(), dim_data.getDataMap()));
                } else {
                    player.displayClientMessage(Component.literal("You do not have a saved destination called " + this.saveName + ", you can list your destinations with /tardis list"), false);
                }
            } else {
                if (!core_found) {
                    player.displayClientMessage(Component.literal("Core is not in range.").withStyle(ChatFormatting.RED), false);
                }
                if (!has_components) {
                    player.displayClientMessage(Component.literal("Coordinate components not in range. (Keypad and Designator)").withStyle(ChatFormatting.RED), false);
                }
            }
        });
    }
}
