package za.co.infernos.vortexmod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import za.co.infernos.vortexmod.VortexMod;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> PLACEHOLDER = tag("placeholder");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> PLACEHOLDER = tag("placeholder");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, name));
        }
    }
}
