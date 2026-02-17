package za.co.infernos.vortexmod.entities.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import za.co.infernos.vortexmod.VortexMod;

public class ModModelLayers {
    public static final ModelLayerLocation LOST_TRAVELER_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "lost_traveler_layer"), "main");
    public static final ModelLayerLocation DALEK_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "dalek_layer"), "main");
    public static final ModelLayerLocation LASER_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "laser_layer"), "main");
    public static final ModelLayerLocation TARDIS_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "tardis_layer"), "main");
    public static final ModelLayerLocation ANGEL_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "angel_layer"), "main");
    public static final ModelLayerLocation RIFT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "rift_layer"), "main");
}
