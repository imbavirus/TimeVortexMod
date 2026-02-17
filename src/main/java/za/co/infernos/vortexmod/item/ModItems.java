package za.co.infernos.vortexmod.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.entities.ModEntities;
import za.co.infernos.vortexmod.item.custom.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VortexMod.MODID);

    public static final DeferredHolder<Item, Item> CHEESE = ITEMS.register("cheese",
            () -> new Cheese(new Item.Properties().food(ModFoods.CHEESE)));

    public static final DeferredHolder<Item, Item> SIZE_UPGRADE = ITEMS.register("size_upgrade",
            () -> new SizeUpgrade(new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, Item> EUCLIDEAN_UPGRADE = ITEMS.register("euclidean_upgrade",
            () -> new EuclideanUpgrade(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> WRENCH = ITEMS.register("wrench",
            () -> new Wrench(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> CORE = ITEMS.register("vortex_core",
            () -> new VortexCore(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> TARDIS_KEY = ITEMS.register("tardis_key",
            () -> new TardisKey(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> SIZE_DESIGNATOR = ITEMS.register("size_designator",
            () -> new SizeDesignator(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> BLUE_TRADER_SPAWN_EGG = ITEMS.register("blue_trader_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BLUE_TRADER, 0x0d3f8f, 0xa67f46, new Item.Properties()));

    public static final DeferredHolder<Item, Item> ORANGE_TRADER_SPAWN_EGG = ITEMS.register("orange_trader_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ORANGE_TRADER, 0xc77e0a, 0xa67f46, new Item.Properties()));

    public static final DeferredHolder<Item, Item> PURPLE_TRADER_SPAWN_EGG = ITEMS.register("purple_trader_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PURPLE_TRADER, 0x690ac7, 0xa67f46, new Item.Properties()));
    public static final DeferredHolder<Item, Item> BLACK_TRADER_SPAWN_EGG = ITEMS.register("black_trader_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BLACK_TRADER, 0x3e3d40, 0xa67f46, new Item.Properties()));

    public static final DeferredHolder<Item, Item> GOLD_DALEK_SPAWN_EGG = ITEMS.register("gold_dalek_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GOLD_DALEK, 0xc49943, 0x403f3f, new Item.Properties()));
    public static final DeferredHolder<Item, Item> SILVER_DALEK_SPAWN_EGG = ITEMS.register("silver_dalek_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SILVER_DALEK, 0x82807d, 0x403f3f, new Item.Properties()));
    public static final DeferredHolder<Item, Item> BLACK_DALEK_SPAWN_EGG = ITEMS.register("black_dalek_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BLACK_DALEK, 0x3e3d40, 0x403f3f, new Item.Properties()));
    public static final DeferredHolder<Item, Item> SILVER_BLACK_DALEK_SPAWN_EGG = ITEMS.register("silver_black_dalek_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SILVER_BLACK_DALEK, 0x262626, 0x403f3f, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
