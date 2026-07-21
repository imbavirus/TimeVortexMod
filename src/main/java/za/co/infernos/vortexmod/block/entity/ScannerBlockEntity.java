package za.co.infernos.vortexmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import za.co.infernos.vortexmod.entities.custom.TardisEntity;
import za.co.infernos.vortexmod.screen.custom.menu.ScannerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {
    public final ContainerData data;
    public int exitBlockPosX = 0;
    public int exitBlockPosY = 0;
    public int exitBlockPosZ = 0;
    public int blockTargetBackX = 0;
    public int blockTargetBackY = 0;
    public int blockTargetBackZ = 0;
    public int decidedDimensionID = 0;
    public int blockPosUpdated = 0;
    private int is_active = 0;
    public final ItemStackHandler itemHandler = new ItemStackHandler(6);
    public ScannerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SCANNER_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ScannerBlockEntity.this.is_active;

                    case 1 -> ScannerBlockEntity.this.exitBlockPosX;
                    case 2 -> ScannerBlockEntity.this.exitBlockPosY;
                    case 3 -> ScannerBlockEntity.this.exitBlockPosZ;

                    case 4 -> ScannerBlockEntity.this.blockTargetBackX;
                    case 5 -> ScannerBlockEntity.this.blockTargetBackY;
                    case 6 -> ScannerBlockEntity.this.blockTargetBackZ;

                    case 7 -> ScannerBlockEntity.this.decidedDimensionID;

                    case 8 -> ScannerBlockEntity.this.blockPosUpdated;

                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ScannerBlockEntity.this.is_active = pValue;

                    case 1 -> ScannerBlockEntity.this.exitBlockPosX = pValue;
                    case 2 -> ScannerBlockEntity.this.exitBlockPosY = pValue;
                    case 3 -> ScannerBlockEntity.this.exitBlockPosZ = pValue;

                    case 4 -> ScannerBlockEntity.this.blockTargetBackX = pValue;
                    case 5 -> ScannerBlockEntity.this.blockTargetBackY = pValue;
                    case 6 -> ScannerBlockEntity.this.blockTargetBackZ = pValue;

                    case 7 -> ScannerBlockEntity.this.decidedDimensionID = pValue;

                    case 8 -> ScannerBlockEntity.this.blockPosUpdated = pValue;
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.vortexmod.scanner_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ScannerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            return;
        }

        setChanged(pLevel, pPos, pState);
    }
}
