package za.co.infernos.vortexmod.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.ModBlocks;

import java.util.function.BiConsumer;

public class ModBlockLootTables implements LootTableSubProvider {
    private final HolderLookup.Provider registries;

    public ModBlockLootTables(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Generate loot tables for all our blocks
        // MODULES
        generateLootTable(consumer, ModBlocks.THROTTLE_BLOCK.get());
        generateLootTable(consumer, ModBlocks.INTERFACE_BLOCK.get());
        generateLootTable(consumer, ModBlocks.COORDINATE_BLOCK.get());
        generateLootTable(consumer, ModBlocks.KEYPAD_BLOCK.get());
        generateLootTable(consumer, ModBlocks.SIZE_MANIPULATOR_BLOCK.get());
        generateLootTable(consumer, ModBlocks.EQUALIZER_BLOCK.get());
        generateLootTable(consumer, ModBlocks.DISRUPTOR_BLOCK.get());
        generateLootTable(consumer, ModBlocks.TARDIS_BLOCK.get());
        generateLootTable(consumer, ModBlocks.DOOR_BLOCK.get());
        generateLootTable(consumer, ModBlocks.TARDIS_SIGN_BLOCK.get());
        generateLootTable(consumer, ModBlocks.SCANNER_BLOCK.get());
        generateLootTable(consumer, ModBlocks.GROUNDING_BLOCK.get());
        generateLootTable(consumer, ModBlocks.BIOMETRIC_BLOCK.get());
        generateLootTable(consumer, ModBlocks.MONITOR_BLOCK.get());

        // ROUNDELS
        generateLootTable(consumer, ModBlocks.OAK_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.SPRUCE_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.ACACIA_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.BIRCH_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.CHERRY_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.DARK_OAK_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.JUNGLE_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.MANGROVE_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.CRIMSON_ROUNDEL.get());
        generateLootTable(consumer, ModBlocks.WARPED_ROUNDEL.get());

        // SKARO BLOCKS
        generateLootTable(consumer, ModBlocks.SKARO_SAND.get());
        generateLootTable(consumer, ModBlocks.SKARO_SAND_STONE.get());
    }

    private void generateLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, Block block) {
        // Get the block's registry key to create the proper loot table key
        ResourceKey<Block> blockKey = block.builtInRegistryHolder().key();
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, 
                ResourceLocation.fromNamespaceAndPath(blockKey.location().getNamespace(), "blocks/" + blockKey.location().getPath()));
        LootTable.Builder builder = LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)));
        consumer.accept(key, builder);
    }
}
