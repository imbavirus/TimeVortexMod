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

public class ServerboundSaveTargetPacket implements CustomPacketPayload {
    public static final Type<ServerboundSaveTargetPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "serverbound_save_target"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSaveTargetPacket> STREAM_CODEC =
            StreamCodec.of(ServerboundSaveTargetPacket::encode, ServerboundSaveTargetPacket::decode);

    private final BlockPos from_pos;
    private final String save_name;
    private final Boolean is_save;
    private final Boolean targetScreen;

    public ServerboundSaveTargetPacket(BlockPos from_pos, String save_pos, Boolean is_save, Boolean targetScreen) {
        this.from_pos = from_pos;
        this.save_name = save_pos;
        this.is_save = is_save;
        this.targetScreen = targetScreen;
    }

    public static ServerboundSaveTargetPacket decode(RegistryFriendlyByteBuf buffer) {
        return new ServerboundSaveTargetPacket(buffer.readBlockPos(), buffer.readUtf(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void encode(RegistryFriendlyByteBuf buffer, ServerboundSaveTargetPacket packet) {
        buffer.writeBlockPos(packet.from_pos);
        buffer.writeUtf(packet.save_name);
        buffer.writeBoolean(packet.is_save);
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

            if (this.is_save) {
                ServerLevel tardis_dim = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);
                ServerLevel vortex = minecraftserver.getLevel(ModDimensions.vortexDIM_LEVEL_KEY);
                if (tardis_dim == null || vortex == null) return;
                LocationMapData coord_data = LocationMapData.get(vortex);
                RotationMapData rotation_data = RotationMapData.get(vortex);
                DimensionMapData dim_data = DimensionMapData.get(tardis_dim);
                ServerLevel level = player.serverLevel();

                boolean core_found = false;

                BlockPos corePos = this.from_pos;
                VortexInterfaceBlockEntity vortexInterfaceBlockEntity = null;

                for (int _x = -16; _x <= 16 && !core_found; _x++) {
                    for (int _y = -16; _y <= 16 && !core_found; _y++) {
                        for (int _z = -16; _z <= 16 && !core_found; _z++) {
                            BlockPos currentPos = this.from_pos.offset(_x, _y, _z);

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
                    int dim_hash = vortexInterfaceBlockEntity.data.get(9);

                    Iterable<ServerLevel> serverLevels = minecraftserver.getAllLevels();
                    ServerLevel currentLevel = level;

                    for (ServerLevel cLevel : serverLevels) {
                        if (cLevel.dimension().location().getPath().hashCode() == dim_hash) {
                            currentLevel = cLevel;
                        }
                    }

                    BlockPos currentVec = new BlockPos(vortexInterfaceBlockEntity.data.get(6), vortexInterfaceBlockEntity.data.get(7), vortexInterfaceBlockEntity.data.get(8));

                    coord_data.getDataMap().put(player.getScoreboardName() + this.save_name, currentVec);
                    rotation_data.getDataMap().put(player.getScoreboardName() + this.save_name, vortexInterfaceBlockEntity.data.get(12));
                    dim_data.getDataMap().put(player.getScoreboardName() + this.save_name, currentLevel.dimension().location().getPath());

                    player.displayClientMessage(Component.literal("Saving your current location (" + currentVec.getX() + " " + currentVec.getY() + " " + currentVec.getZ() + " | " + currentLevel.dimension().location().getPath() + ") as " + this.save_name), false);
                    coord_data.setDirty();
                    rotation_data.setDirty();
                    dim_data.setDirty();
                    keypadBlockEntity.coordData = coord_data.getDataMap();
                    keypadBlockEntity.dimData = dim_data.getDataMap();
                    PacketHandler.sendToAllClients(new ClientboundTargetMapPacket(level.dimension().location().getPath(), this.from_pos, coord_data.getDataMap(), dim_data.getDataMap()));
                } else {
                    if (!core_found) {
                        player.displayClientMessage(Component.literal("Core is not in range.").withStyle(ChatFormatting.RED), false);
                    }
                    if (!has_components) {
                        player.displayClientMessage(Component.literal("Coordinate components not in range. (Keypad and Designator)").withStyle(ChatFormatting.RED), false);
                    }
                }
            } else {
                ServerLevel tardis_dim = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);
                ServerLevel vortex = minecraftserver.getLevel(ModDimensions.vortexDIM_LEVEL_KEY);
                if (tardis_dim == null || vortex == null) return;
                LocationMapData coord_data = LocationMapData.get(vortex);
                DimensionMapData dim_data = DimensionMapData.get(tardis_dim);
                ServerLevel level = player.serverLevel();

                boolean core_found = false;

                BlockPos corePos = this.from_pos;
                VortexInterfaceBlockEntity vortexInterfaceBlockEntity = null;

                for (int _x = -16; _x <= 16 && !core_found; _x++) {
                    for (int _y = -16; _y <= 16 && !core_found; _y++) {
                        for (int _z = -16; _z <= 16 && !core_found; _z++) {
                            BlockPos currentPos = this.from_pos.offset(_x, _y, _z);

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

                for (int _x = -16; _x <= 16 && !has_components; _x++) {
                    for (int _y = -16; _y <= 16 && !has_components; _y++) {
                        for (int _z = -16; _z <= 16 && !has_components; _z++) {
                            BlockPos currentPos = corePos.offset(_x, _y, _z);

                            BlockState blockState = level.getBlockState(currentPos);
                            if (blockState.getBlock() == ModBlocks.KEYPAD_BLOCK.get()) {
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
                    if (coord_data.getDataMap().containsKey(player.getScoreboardName() + this.save_name)) {
                        BlockPos savedPos = coord_data.getDataMap().get(player.getScoreboardName() + this.save_name);
                        String savedDimName = dim_data.getDataMap().get(player.getScoreboardName() + this.save_name);
                        int savedDimHash = 0;
                        Iterable<ServerLevel> serverLevels = minecraftserver.getAllLevels();

                        for (ServerLevel cLevel : serverLevels) {
                            if (cLevel.dimension().location().getPath().equals(savedDimName)) {
                                savedDimHash = cLevel.dimension().location().getPath().hashCode();
                            }
                        }

                        vortexInterfaceBlockEntity.data.set(14, 1);
                        vortexInterfaceBlockEntity.data.set(15, savedPos.getX());
                        vortexInterfaceBlockEntity.data.set(16, savedPos.getY());
                        vortexInterfaceBlockEntity.data.set(17, savedPos.getZ());
                        vortexInterfaceBlockEntity.data.set(18, savedDimHash);

                        player.displayClientMessage(Component.literal("Loading " + this.save_name + " to the designator. (" + savedPos.getX() + " " + savedPos.getY() + " " + savedPos.getZ() + " | " + savedDimName + ")"), false);
                    } else {
                        player.displayClientMessage(Component.literal("You do not have a saved destination called " + this.save_name + ", you can list your destinations with /tardis list"), false);
                    }
                } else {
                    if (!core_found) {
                        player.displayClientMessage(Component.literal("Core is not in range.").withStyle(ChatFormatting.RED), false);
                    }
                    if (!has_components) {
                        player.displayClientMessage(Component.literal("Coordinate components not in range. (Keypad and Designator)").withStyle(ChatFormatting.RED), false);
                    }
                }
            }
        });
    }
}
