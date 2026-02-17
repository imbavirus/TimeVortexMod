package za.co.infernos.vortexmod.block.entity;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import za.co.infernos.vortexmod.VortexMod;

public class TardisBlockEntity extends BlockEntity {
    public final ContainerData data;

    public UUID owner = null;
    public int locked = 0;
    public int bio_sec = 0;

    public TardisBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TARDIS_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> 0; // Deprecated owner sync
                    case 1 -> TardisBlockEntity.this.locked;
                    case 2 -> TardisBlockEntity.this.bio_sec;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> { } // Deprecated owner sync
                    case 1 -> TardisBlockEntity.this.locked = pValue;
                    case 2 -> TardisBlockEntity.this.bio_sec = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        CompoundTag vortexModData = pTag.getCompound(VortexMod.MODID);

        if (vortexModData.hasUUID("owner")) {
            this.owner = vortexModData.getUUID("owner");
        }
        this.locked = vortexModData.getInt("locked");
        this.bio_sec = vortexModData.getInt("bio_sec");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        CompoundTag vortexModData = new CompoundTag();

        if (this.owner != null) {
            vortexModData.putUUID("owner", this.owner);
        }
        vortexModData.putInt("locked", this.locked);
        vortexModData.putInt("bio_sec", this.bio_sec);

        pTag.put(VortexMod.MODID, vortexModData);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            return;
        }

        setChanged(pLevel, pPos, pState);
    }
}
