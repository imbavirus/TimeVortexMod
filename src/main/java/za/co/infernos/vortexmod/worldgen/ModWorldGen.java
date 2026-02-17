package za.co.infernos.vortexmod.worldgen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import za.co.infernos.vortexmod.worldgen.biome.ModBiomes;
import za.co.infernos.vortexmod.worldgen.dimension.ModDimensions;
import za.co.infernos.vortexmod.worldgen.utils.ModNoiseGenerator;

/**
 * Central registry for worldgen data
 */
public class ModWorldGen {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.NOISE_SETTINGS, ModNoiseGenerator::bootstrap)
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem);
}
