package za.co.infernos.vortexmod.recipe;

import za.co.infernos.vortexmod.VortexMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, VortexMod.MODID);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}