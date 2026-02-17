package za.co.infernos.vortexmod.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import za.co.infernos.vortexmod.block.ModBlocks;
import za.co.infernos.vortexmod.block.entity.ModBlockEntities;
import za.co.infernos.vortexmod.block.entity.TardisBlockEntity;
import za.co.infernos.vortexmod.block.entity.VortexInterfaceBlockEntity;
import za.co.infernos.vortexmod.entities.custom.TardisEntity;
import za.co.infernos.vortexmod.item.ModItems;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

public class DoorBlock extends Block {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 32, 16);

    public DoorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        return doUse(pState, pLevel, pPos, pPlayer, ItemStack.EMPTY, pHit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        InteractionResult result = doUse(pState, pLevel, pPos, pPlayer, pStack, pHit);
        if (result.consumesAction()) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private InteractionResult doUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, ItemStack heldStack, BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            MinecraftServer minecraftserver = serverLevel.getServer();
            ServerLevel overworldDimension = minecraftserver.getLevel(Level.OVERWORLD);
            Iterable<ServerLevel> serverLevels = minecraftserver.getAllLevels();
            ServerLevel tardisDimension = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);

            if (serverLevel == tardisDimension) {
                for (int x = -100; x <= 100; x++) {
                    for (int y = -100; y <= 100; y++) {
                        for (int z = -100; z <= 100; z++) {
                            BlockPos currentPos = pPos.offset(x, y, z);
                            var blockEntity = pLevel.getBlockEntity(currentPos);
                            if (blockEntity instanceof VortexInterfaceBlockEntity vortexInterfaceBlockEntity) {
                                ServerLevel targetDimension = overworldDimension;

                                for (ServerLevel cLevel : serverLevels) {
                                    if (cLevel.dimension().location().getPath().hashCode() == vortexInterfaceBlockEntity.data.get(9)) {
                                        targetDimension = cLevel;
                                    }
                                }

                                BlockPos blockExitPos = new BlockPos(vortexInterfaceBlockEntity.data.get(6), vortexInterfaceBlockEntity.data.get(7), vortexInterfaceBlockEntity.data.get(8));
                                
                                // Search all dimensions for the TARDIS entity
                                TardisEntity tardisEntity = null;
                                for (ServerLevel cLevel : serverLevels) {
                                    TardisEntity foundEntity = (TardisEntity) cLevel.getEntity(vortexInterfaceBlockEntity.getExtUUID());
                                    if (foundEntity != null) {
                                        tardisEntity = foundEntity;
                                        targetDimension = cLevel;
                                        break;
                                    }
                                }
                                
                                if (tardisEntity == null) {
                                    pPlayer.displayClientMessage(Component.literal("Cannot find TARDIS exterior. It may be respawning.").withStyle(ChatFormatting.RED), true);
                                    return InteractionResult.CONSUME;
                                }
                                
                                if (heldStack.is(ModItems.TARDIS_KEY.get())) {
                                    UUID ownerCode = tardisEntity.getOwnerID();
                                    if (ownerCode != null && ownerCode.equals(pPlayer.getUUID())) {
                                        if (!tardisEntity.isLocked()) {
                                            tardisEntity.setLocked(true);
                                            pPlayer.displayClientMessage(Component.literal("Locking TARDIS").withStyle(ChatFormatting.GREEN), true);
                                        } else {
                                            tardisEntity.setLocked(false);
                                            pPlayer.displayClientMessage(Component.literal("Unlocking TARDIS").withStyle(ChatFormatting.AQUA), true);
                                        }
                                    } else {
                                        pPlayer.displayClientMessage(Component.literal("This TARDIS is not yours.").withStyle(ChatFormatting.RED), true);
                                    }
                                } else {
                                    if (!tardisEntity.isRemat() && !tardisEntity.isInFlight() && !tardisEntity.isDemat() && tardisEntity.getAlpha() >= 0.01f) {
                                        int yaw = (int) tardisEntity.getYRot();
                                        Vec3 exitPosition;

                                        double distance = 1.4; // Distance from the root position

                                        double yawRadians = Math.toRadians(yaw);

                                        double newX = blockExitPos.getX() + distance * Math.sin(yawRadians);
                                        double newZ = blockExitPos.getZ() - distance * Math.cos(yawRadians);

                                        exitPosition = new Vec3(newX, blockExitPos.getY(), newZ);

                                        if (pPlayer.getVehicle() != null) {
                                            Entity rootEntity = pPlayer.getRootVehicle();
                                            rootEntity.setYRot(yaw + 180f);
                                            rootEntity.changeDimension(new DimensionTransition(targetDimension, exitPosition, Vec3.ZERO, rootEntity.getYRot(), rootEntity.getXRot(), DimensionTransition.DO_NOTHING));
                                        }
                                        else {
                                            pPlayer.setYRot(yaw + 180f);
                                            pPlayer.changeDimension(new DimensionTransition(targetDimension, exitPosition, Vec3.ZERO, pPlayer.getYRot(), pPlayer.getXRot(), DimensionTransition.DO_NOTHING));
                                        }
                                    }
                                    else {
                                        pPlayer.displayClientMessage(Component.literal("You cannot exit while in flight.").withStyle(ChatFormatting.RED), true);
                                    }
                                }
                                return InteractionResult.CONSUME;
                            }
                        }
                    }
                }
            }
            else {
                pPlayer.displayClientMessage(Component.literal("Door is not in the TARDIS dimension.").withStyle(ChatFormatting.RED), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.vortexmod.door_block.tooltip"));
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
    }
}
