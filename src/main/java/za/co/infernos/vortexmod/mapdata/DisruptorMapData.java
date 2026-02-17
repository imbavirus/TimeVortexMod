package za.co.infernos.vortexmod.mapdata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class DisruptorMapData extends SavedData {
    private static final String DATA_NAME = "tardis_disruptors";
    private final HashMap<String, Integer> dataMap = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
        CompoundTag dataTag = new CompoundTag();

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            dataTag.putInt(entry.getKey(), entry.getValue());
        }
        pCompoundTag.put(DATA_NAME, dataTag);

        return pCompoundTag;
    }

    public HashMap<String, Integer> getDataMap() {
        return dataMap;
    }

    public static DisruptorMapData load(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
        DisruptorMapData savedData = new DisruptorMapData();
        CompoundTag dataTag = pCompoundTag.getCompound(DATA_NAME);
        for (String key : dataTag.getAllKeys()) {
            savedData.dataMap.put(key, dataTag.getInt(key));
        }
        return savedData;
    }

    public static DisruptorMapData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(DisruptorMapData::new, DisruptorMapData::load), DATA_NAME);
    }
}
