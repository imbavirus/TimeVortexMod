package za.co.infernos.vortexmod.mapdata;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class LocationMapData extends SavedData {
    private static final String DATA_NAME = "tardis_locations";
    private final HashMap<String, BlockPos> dataMap = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
        CompoundTag dataTag = new CompoundTag();

        for (Map.Entry<String, BlockPos> entry : dataMap.entrySet()) {
            dataTag.put(entry.getKey(), NbtUtils.writeBlockPos(entry.getValue()));
        }
        pCompoundTag.put(DATA_NAME, dataTag);

        return pCompoundTag;
    }

    public HashMap<String, BlockPos> getDataMap() {
        return dataMap;
    }

    public static LocationMapData load(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
        LocationMapData savedData = new LocationMapData();
        CompoundTag dataTag = pCompoundTag.getCompound(DATA_NAME);
        for (String key : dataTag.getAllKeys()) {
            BlockPos pos = NbtUtils.readBlockPos(dataTag, key).orElse(null);
            if (pos != null) {
                savedData.dataMap.put(key, pos);
            }
        }
        return savedData;
    }

    public static LocationMapData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(LocationMapData::new, LocationMapData::load), DATA_NAME);
    }
}
