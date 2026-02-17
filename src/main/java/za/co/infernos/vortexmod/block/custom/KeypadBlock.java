package za.co.infernos.vortexmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;
import za.co.infernos.vortexmod.block.entity.ModBlockEntities;
import za.co.infernos.vortexmod.block.entity.KeypadBlockEntity;
import za.co.infernos.vortexmod.mapdata.DimensionMapData;
import za.co.infernos.vortexmod.mapdata.LocationMapData;
import za.co.infernos.vortexmod.network.ClientboundDimListPacket;
import za.co.infernos.vortexmod.network.ClientboundTargetMapPacket;
import za.co.infernos.vortexmod.network.PacketHandler;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KeypadBlock extends FaceAttachedHorizontalDirectionalBlockEntity {
    public static final MapCodec<KeypadBlock> CODEC = BlockBehaviour.simpleCodec(KeypadBlock::new);

    protected static final VoxelShape NORTH_AABB = Block.box(0, 0, 12, 16, 16, 16);
    protected static final VoxelShape SOUTH_AABB = Block.box(0, 0, 0, 16, 16, 4);
    protected static final VoxelShape WEST_AABB = Block.box(12, 0, 0, 16, 16, 16);
    protected static final VoxelShape EAST_AABB = Block.box(0, 0, 0, 4, 16, 16);
    protected static final VoxelShape UP_AABB_Z = Block.box(0, 0, 0, 16, 4, 16);
    protected static final VoxelShape UP_AABB_X = Block.box(0, 0, 0, 16, 4, 16);
    protected static final VoxelShape DOWN_AABB_Z = Block.box(0, 12, 0, 16, 16, 16);
    protected static final VoxelShape DOWN_AABB_X = Block.box(0, 12, 0, 16, 16, 16);

    public KeypadBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.EAST));
    }

    @Override
    public MapCodec<KeypadBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch ((AttachFace)pState.getValue(FACE)) {
            case FLOOR:
                switch (pState.getValue(FACING).getAxis()) {
                    case X:
                        return UP_AABB_X;
                    case Z:
                    default:
                        return UP_AABB_Z;
                }
            case WALL:
                switch ((Direction)pState.getValue(FACING)) {
                    case EAST:
                        return EAST_AABB;
                    case WEST:
                        return WEST_AABB;
                    case SOUTH:
                        return SOUTH_AABB;
                    case NORTH:
                    default:
                        return NORTH_AABB;
                }
            case CEILING:
            default:
                switch (pState.getValue(FACING).getAxis()) {
                    case X:
                        return DOWN_AABB_X;
                    case Z:
                    default:
                        return DOWN_AABB_Z;
                }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for(Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
            } else {
                blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
            }

            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof KeypadBlockEntity entity)) {
            return InteractionResult.PASS;
        }

        MinecraftServer minecraftserver = serverLevel.getServer();
        if (minecraftserver == null) {
            return InteractionResult.PASS;
        }

        ServerLevel tardis_dim = minecraftserver.getLevel(ModDimensions.tardisDIM_LEVEL_KEY);
        ServerLevel vortex = minecraftserver.getLevel(ModDimensions.vortexDIM_LEVEL_KEY);
        
        if (tardis_dim == null || vortex == null) {
            return InteractionResult.PASS;
        }

        LocationMapData coord_data = LocationMapData.get(vortex);
        DimensionMapData dim_data = DimensionMapData.get(tardis_dim);

        // Clear previous data
        entity.serverLevels.clear();
        entity.coordData.clear();
        entity.dimData.clear();

        // Store list of levels in entity.Levels or something, use this to select in KeypadScreen
        Iterable<ServerLevel> allServerLevels = minecraftserver.getAllLevels();
        for (ServerLevel level : allServerLevels) {
            entity.serverLevels.add(level.dimension().location().getPath());
        }

        Set<String> coordKeys = coord_data.getDataMap().keySet();
        for (String coordKey : coordKeys) {
            BlockPos pointPos = coord_data.getDataMap().get(coordKey);
            entity.coordData.put(coordKey, pointPos);
            String pointDimension = dim_data.getDataMap().get(coordKey);
            entity.dimData.put(coordKey, pointDimension);
        }

        Map<String, String> levelMap = new HashMap<>();
        for (String levelString : entity.serverLevels) {
            levelMap.put(levelString, levelString);
        }

        PacketHandler.sendToAllClients(new ClientboundTargetMapPacket(pLevel.dimension().location().getPath(), pPos, entity.coordData, entity.dimData));
        PacketHandler.sendToAllClients(new ClientboundDimListPacket(pLevel.dimension().location().getPath(), pPos, levelMap));

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(entity, (RegistryFriendlyByteBuf buf) -> buf.writeBlockPos(pPos));
        }
        
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pLevel instanceof ServerLevel serverLevel) {
            serverLevel.removeBlockEntity(pPos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new KeypadBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.TARDIS_KEYPAD_BE.get(),
                ((pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.vortexmod.keypad_block.tooltip"));
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACE, FACING);
        super.createBlockStateDefinition(pBuilder);
    }
}
