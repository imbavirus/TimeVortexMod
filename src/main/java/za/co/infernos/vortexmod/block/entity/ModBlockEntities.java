package za.co.infernos.vortexmod.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VortexMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VortexInterfaceBlockEntity>> VORTEX_INTERFACE_BE =
            BLOCK_ENTITIES.register("vortex_interface_be", () ->
                    BlockEntityType.Builder.of(VortexInterfaceBlockEntity::new,
                            ModBlocks.INTERFACE_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoordinateDesignatorBlockEntity>> COORDINATE_DESIGNATOR_BE =
            BLOCK_ENTITIES.register("coordinate_designator_be", () ->
                    BlockEntityType.Builder.of(CoordinateDesignatorBlockEntity::new,
                            ModBlocks.COORDINATE_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlockEntity>> TARDIS_KEYPAD_BE =
            BLOCK_ENTITIES.register("tardis_keypad_be", () ->
                    BlockEntityType.Builder.of(KeypadBlockEntity::new,
                            ModBlocks.KEYPAD_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SizeManipulatorBlockEntity>> SIZE_MANIPULATOR_BE =
            BLOCK_ENTITIES.register("size_manipulator_be", () ->
                    BlockEntityType.Builder.of(SizeManipulatorBlockEntity::new,
                            ModBlocks.SIZE_MANIPULATOR_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EqualizerBlockEntity>> EQUALIZER_BE =
            BLOCK_ENTITIES.register("equalizer_be", () ->
                    BlockEntityType.Builder.of(EqualizerBlockEntity::new,
                            ModBlocks.EQUALIZER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TardisBlockEntity>> TARDIS_BE =
            BLOCK_ENTITIES.register("tardis_be", () ->
                    BlockEntityType.Builder.of(TardisBlockEntity::new,
                            ModBlocks.TARDIS_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerBlockEntity>> SCANNER_BE =
            BLOCK_ENTITIES.register("scanner_be", () ->
                    BlockEntityType.Builder.of(ScannerBlockEntity::new,
                            ModBlocks.SCANNER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BiometricBlockEntity>> BIOMETRIC_BLOCK_BE =
            BLOCK_ENTITIES.register("biometric_be", () ->
                    BlockEntityType.Builder.of(BiometricBlockEntity::new,
                            ModBlocks.BIOMETRIC_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MonitorBlockEntity>> MONITOR_BE =
            BLOCK_ENTITIES.register("monitor_be", () ->
                    BlockEntityType.Builder.of(MonitorBlockEntity::new,
                            ModBlocks.MONITOR_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
