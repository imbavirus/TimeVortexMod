package za.co.infernos.vortexmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

import java.util.*;

import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.MemoryServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.ModBlocks;
import za.co.infernos.vortexmod.block.entity.*;
import za.co.infernos.vortexmod.entities.ModEntities;
import za.co.infernos.vortexmod.entities.custom.TardisEntity;
import za.co.infernos.vortexmod.item.ModItems;
import za.co.infernos.vortexmod.mapdata.LocationMapData;
import za.co.infernos.vortexmod.sound.ModSounds;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class VortexInterfaceBlock extends BaseEntityBlock {
    public static final MapCodec<VortexInterfaceBlock> CODEC = BlockBehaviour.simpleCodec(VortexInterfaceBlock::new);
    private static final TicketController CHUNK_TICKET_CONTROLLER =
            new TicketController(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "chunk_tickets"));
    
    public static TicketController getTicketController() {
        return CHUNK_TICKET_CONTROLLER;
    }

    public VortexInterfaceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MapCodec<VortexInterfaceBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        return doUse(pState, pLevel, pPos, pPlayer, InteractionHand.MAIN_HAND, ItemStack.EMPTY, pHit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        InteractionResult result = doUse(pState, pLevel, pPos, pPlayer, pHand, pStack, pHit);
        if (result.consumesAction()) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private InteractionResult doUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, ItemStack holdingItem, BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            MinecraftServer minecraftserver = serverLevel.getServer();
            ServerLevel overworld = minecraftserver.getLevel(Level.OVERWORLD);
            ServerLevel tardisDimension = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);
            LocationMapData data = LocationMapData.get(overworld);
            if (holdingItem.is(ModItems.WRENCH.get())) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(pPos);

                if (blockEntity != null) {
                    ItemStack droppedItem = new ItemStack(pState.getBlock()); // Create an ItemStack of the block
                    blockEntity.saveToItem(droppedItem, serverLevel.registryAccess());
                    serverLevel.removeBlock(pPos, false); // Remove the block

                    // Spawn the ItemStack in the world
                    double x = pPos.getX() + 0.5;
                    double y = pPos.getY() + 0.5;
                    double z = pPos.getZ() + 0.5;
                    ItemEntity droppedEntity = new ItemEntity(serverLevel, x, y, z, droppedItem);
                    serverLevel.addFreshEntity(droppedEntity);
                }

                return InteractionResult.CONSUME;
            }

            VortexInterfaceBlockEntity localBlockEntity = (VortexInterfaceBlockEntity) serverLevel.getBlockEntity(pPos);
            UUID ownerCode = localBlockEntity.getOwner();
            if (ownerCode == null) {
                localBlockEntity.setOwner(pPlayer.getUUID());
                pPlayer.displayClientMessage(Component.literal("TARDIS owner set to " + pPlayer.getScoreboardName()).withStyle(ChatFormatting.AQUA), true);
                return InteractionResult.CONSUME;
            }

            if (holdingItem.is(ModItems.EUCLIDEAN_UPGRADE.get()) && pLevel != tardisDimension) {
                serverLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.BOTI_UPGRADE_SOUND.get(), SoundSource.BLOCKS, 1, 1, 0);

                Set<String> keyList = data.getDataMap().keySet();

                int greatest_x_coordinate = -1000000;
                int greatest_z_coordinate = -1000000;
                
                // First, try to find existing entry for this owner
                String ownerKey = ownerCode.toString();
                if (data.getDataMap().containsKey(ownerKey)) {
                    BlockPos interiorPos = data.getDataMap().get(ownerKey);
                    greatest_x_coordinate = interiorPos.getX();
                    greatest_z_coordinate = interiorPos.getZ();
                } else {
                    // If no entry for owner, find the maximum coordinates from all entries
                    for (String key : keyList) {
                        BlockPos interiorPos = data.getDataMap().get(key);
                        int x_coordinate = interiorPos.getX();
                        int z_coordinate = interiorPos.getZ();
                        if (x_coordinate > greatest_x_coordinate) {
                            greatest_x_coordinate = x_coordinate;
                        }
                        if (z_coordinate > greatest_z_coordinate) {
                            greatest_z_coordinate = z_coordinate;
                        }
                    }
                    
                    // If still no coordinates found, use a default location
                    if (greatest_x_coordinate == -1000000 && greatest_z_coordinate == -1000000) {
                        greatest_x_coordinate = 0;
                        greatest_z_coordinate = 0;
                    }
                }

                localBlockEntity.data.set(6, pPos.getX());
                localBlockEntity.data.set(7, pPos.getY());
                localBlockEntity.data.set(8, pPos.getZ());

                BlockPos tardisTarget = new BlockPos(greatest_x_coordinate + 10000, -128, greatest_z_coordinate + 10000);

                int size = 1;

                for (int x = -size; x <= size; x++) {
                    for (int y = -1; y <= size; y++) {
                        for (int z = -size; z <= size; z++) {
                            BlockPos currentPos = pPos.offset(x, y, z);
                            if (currentPos == pPos) {
                                continue;
                            }

                            var blockEntity = pLevel.getBlockEntity(currentPos);

                            if (blockEntity instanceof SizeManipulatorBlockEntity sizeManipulatorBlockEntity) {
                                size += sizeManipulatorBlockEntity.data.get(0);
                            }
                        }
                    }
                }

                if (size <= 0) {
                    size = 1;
                }

                int y_size = 5;

                if (size < 5) {
                    y_size = size;
                }

                int door_distance = 10 + size;

                BlockPos interfacePos = null;
                List<BlockPos> toBeRemoved = new ArrayList<>();

                for (int x = -size; x <= size; x++) {
                    for (int y = -1; y <= y_size + (y_size - 1); y++) {
                        for (int z = -size; z <= size; z++) {
                            BlockPos currentPos = pPos.offset(x, y, z);
                            BlockPos currentTargetPos = tardisTarget.offset(x + door_distance, y, z);

                            BlockEntity blockEntity = serverLevel.getBlockEntity(currentPos);

                            if (blockEntity != null) {
                                if (blockEntity instanceof VortexInterfaceBlockEntity vortexInterfaceBlockEntity) {
                                    interfacePos = currentTargetPos;
                                }
                            }

                            BlockState blockState = serverLevel.getBlockState(currentPos);

                            CompoundTag nbtData = null;
                            if (blockEntity != null) {
                                nbtData = blockEntity.saveWithFullMetadata(serverLevel.registryAccess());
                            }

                            tardisDimension.setBlockAndUpdate(currentTargetPos, blockState);

                            if (nbtData != null) {
                                BlockEntity newBlockEntity = tardisDimension.getBlockEntity(currentTargetPos);
                                if (newBlockEntity != null) {
                                    newBlockEntity.loadWithComponents(nbtData, serverLevel.registryAccess());
                                }
                            }

                            if (pLevel.getBlockState(currentPos).getBlock() instanceof DoorBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TrapDoorBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TorchBlock || pLevel.getBlockState(currentPos).getBlock() instanceof PressurePlateBlock || pLevel.getBlockState(currentPos).getBlock() instanceof ButtonBlock || pLevel.getBlockState(currentPos).getBlock() instanceof LeverBlock || pLevel.getBlockState(currentPos).getBlock() instanceof RedStoneWireBlock || pLevel.getBlockState(currentPos).getBlock() instanceof RedstoneTorchBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TrapDoorBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TallGrassBlock || pLevel.getBlockState(currentPos).getBlock() instanceof SeagrassBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TallSeagrassBlock || pLevel.getBlockState(currentPos).getBlock() instanceof FlowerBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TorchflowerCropBlock || pLevel.getBlockState(currentPos).getBlock() instanceof ChorusFlowerBlock || pLevel.getBlockState(currentPos).getBlock() instanceof TallFlowerBlock || pLevel.getBlockState(currentPos).getBlock() instanceof FlowerPotBlock || pLevel.getBlockState(currentPos).getBlock() instanceof ThrottleBlock || pLevel.getBlockState(currentPos).getBlock() instanceof RedStoneWireBlock || pLevel.getBlockState(currentPos).getBlock() instanceof BedBlock || pLevel.getBlockState(currentPos).getBlock() instanceof CarpetBlock || pLevel.getBlockState(currentPos).getBlock() instanceof VineBlock) {
                                serverLevel.removeBlock(currentPos, false);
                                serverLevel.removeBlockEntity(currentPos);
                            }
                            else {
                                if (serverLevel.getBlockState(currentPos).getBlock() != Blocks.BEDROCK && serverLevel.getBlockState(currentPos).getBlock() != Blocks.END_PORTAL && serverLevel.getBlockState(currentPos).getBlock() != Blocks.END_PORTAL_FRAME) {
                                    if (currentPos != pPos) {
                                        toBeRemoved.add(currentPos);
                                    }
                                }
                            }
                        }
                    }
                }

                for (BlockPos positionToBeRemoved : toBeRemoved) {
                    BlockEntity blockEntity = serverLevel.getBlockEntity(positionToBeRemoved);
                    if (blockEntity != null) {
                        blockEntity.loadWithComponents(new CompoundTag(), serverLevel.registryAccess());
                    }
                    serverLevel.removeBlock(positionToBeRemoved, false);
                    serverLevel.removeBlockEntity(positionToBeRemoved);
                }

                for (int x = -1; x < door_distance; x++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos augmentedPos = tardisTarget.offset(x, -1, z);
                        BlockState blockAt = tardisDimension.getBlockState(augmentedPos);
                        if (blockAt.getBlock() == Blocks.AIR) {
                            tardisDimension.setBlockAndUpdate(augmentedPos, Blocks.STONE.defaultBlockState());
                        }
                    }
                }

                tardisDimension.setBlockAndUpdate(tardisTarget, ModBlocks.DOOR_BLOCK.get().defaultBlockState());

                // Ensure interfacePos is set - if not found in loop, use the position where the interface was copied
                if (interfacePos == null) {
                    // Find the interface position in the tardis dimension (where we copied the interface block)
                    for (int x = -size; x <= size; x++) {
                        for (int y = -1; y <= y_size + (y_size - 1); y++) {
                            for (int z = -size; z <= size; z++) {
                                BlockPos currentTargetPos = tardisTarget.offset(x + door_distance, y, z);
                                BlockEntity blockEntity = tardisDimension.getBlockEntity(currentTargetPos);
                                if (blockEntity instanceof VortexInterfaceBlockEntity) {
                                    interfacePos = currentTargetPos;
                                    break;
                                }
                            }
                            if (interfacePos != null) break;
                        }
                        if (interfacePos != null) break;
                    }
                    // If still not found, use a position near the door
                    if (interfacePos == null) {
                        interfacePos = tardisTarget.offset(door_distance, 0, 0);
                    }
                }

                if (interfacePos == null) {
                    pPlayer.displayClientMessage(Component.literal("Error: Could not determine interface position").withStyle(ChatFormatting.RED), false);
                    return InteractionResult.CONSUME;
                }

                VortexInterfaceBlockEntity interfaceBlockEntity = (VortexInterfaceBlockEntity) tardisDimension.getBlockEntity(interfacePos);
                
                if (interfaceBlockEntity == null) {
                    // Try to create the interface block if it doesn't exist
                    tardisDimension.setBlockAndUpdate(interfacePos, ModBlocks.INTERFACE_BLOCK.get().defaultBlockState());
                    interfaceBlockEntity = (VortexInterfaceBlockEntity) tardisDimension.getBlockEntity(interfacePos);
                    if (interfaceBlockEntity == null) {
                        pPlayer.displayClientMessage(Component.literal("Error: Could not create interface in TARDIS dimension").withStyle(ChatFormatting.RED), false);
                        return InteractionResult.CONSUME;
                    }
                }

                // Check if there's already a TardisEntity - reuse it instead of creating a new one
                UUID oldExtUUID = localBlockEntity.getExtUUID();
                TardisEntity tardisMob = null;
                
                if (oldExtUUID != null) {
                    // Search all dimensions for existing TardisEntity
                    Iterable<ServerLevel> allLevels = minecraftserver.getAllLevels();
                    for (ServerLevel level : allLevels) {
                        TardisEntity existingTardis = (TardisEntity) level.getEntity(oldExtUUID);
                        if (existingTardis != null) {
                            tardisMob = existingTardis;
                            // Update the existing TardisEntity's position if needed
                            if (tardisMob.level() != serverLevel) {
                                tardisMob.teleportToWithTicket(serverLevel, pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5, tardisMob.getYRot(), tardisMob.getXRot());
                            } else {
                                tardisMob.setPos(pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5);
                            }
                            break;
                        }
                    }
                }
                
                // Only create a new TardisEntity if one doesn't exist
                if (tardisMob == null) {
                    tardisMob = ModEntities.TARDIS.get().spawn(serverLevel, pPos, MobSpawnType.NATURAL);
                    if (tardisMob != null && !tardisMob.isRemoved()) {
                        // Check if entity was already added (avoid duplicates)
                        if (serverLevel.getEntity(tardisMob.getUUID()) == null) {
                            serverLevel.addFreshEntity(tardisMob);
                        } else {
                            tardisMob = (TardisEntity) serverLevel.getEntity(tardisMob.getUUID());
                        }
                        tardisMob.setOwnerID(ownerCode);
                        interfaceBlockEntity.setExtUUID(tardisMob.getUUID());
                        data.getDataMap().put(tardisMob.getUUID().toString(), tardisTarget);
                    }
                } else {
                    // Update the existing TardisEntity's owner and link
                    if (!tardisMob.isRemoved()) {
                        tardisMob.setOwnerID(ownerCode);
                        interfaceBlockEntity.setExtUUID(tardisMob.getUUID());
                        // Update the location map data
                        data.getDataMap().put(tardisMob.getUUID().toString(), tardisTarget);
                    }
                }

                ChunkPos chunkPos = tardisDimension.getChunkAt(interfacePos).getPos();
                try {
                    CHUNK_TICKET_CONTROLLER.forceChunk(tardisDimension, interfacePos, chunkPos.x, chunkPos.z, true, true);
                } catch (IllegalArgumentException e) {
                    VortexMod.LOGGER.warn("Failed to force chunk for TARDIS dimension: {}", e.getMessage());
                }
                chunkPos = serverLevel.getChunkAt(pPos).getPos();
                try {
                    CHUNK_TICKET_CONTROLLER.forceChunk(serverLevel, pPos, chunkPos.x, chunkPos.z, true, true);
                } catch (IllegalArgumentException e) {
                    VortexMod.LOGGER.warn("Failed to force chunk for server level: {}", e.getMessage());
                }

                pPlayer.setItemInHand(pHand, new ItemStack(ModItems.TARDIS_KEY.get(), 1));

                handleLightningStrikes(serverLevel, pPos);

                data.setDirty();

                serverLevel.removeBlock(pPos, false);
            }
        }

        return InteractionResult.CONSUME;
    }

    private void handleLightningStrikes(Level pLevel, BlockPos targetPosition) {
        List<Connection> connectionList = pLevel.getServer().getConnection().getConnections();
        for (Connection pConnection : connectionList) {
            ClientboundAddEntityPacket entityPacket = new ClientboundAddEntityPacket(new LightningBolt(EntityType.LIGHTNING_BOLT, pLevel), 0, targetPosition);
            if (pConnection.isConnected()) {
                if (pConnection.getPacketListener().isAcceptingMessages() && (pConnection.getPacketListener() instanceof ServerGamePacketListenerImpl)) {
                    pConnection.send(entityPacket);
                }
            }
        }
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        VortexInterfaceBlockEntity localBlockEntity = (VortexInterfaceBlockEntity) pLevel.getBlockEntity(pPos);
        return localBlockEntity.data.get(21);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (pLevel instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = serverLevel.getChunkAt(pPos).getPos();
            try {
                CHUNK_TICKET_CONTROLLER.forceChunk(serverLevel, pPos, chunkPos.x, chunkPos.z, true, true);
            } catch (IllegalArgumentException e) {
                VortexMod.LOGGER.warn("Failed to force chunk on place: {}", e.getMessage());
            }
        }

        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VortexInterfaceBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.VORTEX_INTERFACE_BE.get(),
                ((pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.vortexmod.interface_block.tooltip"));
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
    }
}
