package za.co.infernos.vortexmod.events;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.entity.ModBlockEntities;
import za.co.infernos.vortexmod.entities.ModEntities;
import za.co.infernos.vortexmod.entities.custom.*;

@EventBusSubscriber(modid = VortexMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.VORTEX_INTERFACE_BE.get(),
                (be, side) -> be.getEnergy()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SIZE_MANIPULATOR_BE.get(),
                (be, side) -> be.itemHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SCANNER_BE.get(),
                (be, side) -> be.itemHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.TARDIS_KEYPAD_BE.get(),
                (be, side) -> be.itemHandler
        );

        if (ModList.get().isLoaded("computercraft")) {
            // Reflect so CC classes are not loaded when the mod is absent.
            try {
                Class.forName("za.co.infernos.vortexmod.compat.computercraft.CCCompat")
                        .getMethod("registerCapabilities", RegisterCapabilitiesEvent.class)
                        .invoke(null, event);
            } catch (ReflectiveOperationException e) {
                VortexMod.LOGGER.error("Failed to register ComputerCraft peripherals", e);
            }
        }
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BLUE_TRADER.get(), LostTravelerEntity.createAttributes().build());
        event.put(ModEntities.ORANGE_TRADER.get(), LostTravelerEntity.createAttributes().build());
        event.put(ModEntities.PURPLE_TRADER.get(), LostTravelerEntity.createAttributes().build());
        event.put(ModEntities.BLACK_TRADER.get(), LostTravelerEntity.createAttributes().build());

        event.put(ModEntities.GOLD_DALEK.get(), DalekEntity.createAttributes().build());
        event.put(ModEntities.SILVER_DALEK.get(), DalekEntity.createAttributes().build());
        event.put(ModEntities.BLACK_DALEK.get(), DalekEntity.createAttributes().build());
        event.put(ModEntities.SILVER_BLACK_DALEK.get(), DalekEntity.createAttributes().build());

        event.put(ModEntities.TARDIS.get(), TardisEntity.createAttributes().build());

        event.put(ModEntities.ANGEL.get(), AngelEntity.createAttributes().build());

        event.put(ModEntities.RIFT.get(), RiftEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacement(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.GOLD_DALEK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
                ModEntities.SILVER_DALEK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                DalekEntity::canSpawn,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
                ModEntities.BLACK_DALEK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                DalekEntity::canSpawn,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
        event.register(
                ModEntities.SILVER_BLACK_DALEK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                DalekEntity::canSpawn,
                RegisterSpawnPlacementsEvent.Operation.AND
        );
    }

}
