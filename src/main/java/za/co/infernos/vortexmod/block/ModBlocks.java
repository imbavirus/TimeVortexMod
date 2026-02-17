package za.co.infernos.vortexmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.block.custom.*;
import za.co.infernos.vortexmod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VortexMod.MODID);

    // TARDIS PARTS
    public static final DeferredHolder<Block, Block> THROTTLE_BLOCK = registerBlock("throttle_block",
            () -> new ThrottleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> INTERFACE_BLOCK = registerBlock("interface_block",
            () -> new VortexInterfaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noOcclusion()));
    public static final DeferredHolder<Block, Block> COORDINATE_BLOCK = registerBlock("coordinate_block",
            () -> new CoordinateDesignatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> KEYPAD_BLOCK = registerBlock("keypad_block",
            () -> new KeypadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> SIZE_MANIPULATOR_BLOCK = registerBlock("size_manipulator_block",
            () -> new SizeManipulatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> EQUALIZER_BLOCK = registerBlock("equalizer_block",
            () -> new EqualizerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> DISRUPTOR_BLOCK = registerBlock("disruptor_block",
            () -> new DisruptorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> TARDIS_BLOCK = registerBlock("tardis_block",
            () -> new TardisBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noOcclusion()));
    public static final DeferredHolder<Block, Block> DOOR_BLOCK = registerBlock("door_block",
            () -> new DoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> SCANNER_BLOCK = registerBlock("scanner_block",
            () -> new ScannerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> GROUNDING_BLOCK = registerBlock("grounding_block",
            () -> new GroundingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> BIOMETRIC_BLOCK = registerBlock("biometric_module",
            () -> new BiometricBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> TARDIS_SIGN_BLOCK = registerBlock("tardis_sign_block",
            () -> new TardisSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> MONITOR_BLOCK = registerBlock("monitor_block",
            () -> new MonitorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    // ROUNDELS
    public static final DeferredHolder<Block, Block> OAK_ROUNDEL = registerBlock("oak_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> SPRUCE_ROUNDEL = registerBlock("spruce_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> ACACIA_ROUNDEL = registerBlock("acacia_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> BIRCH_ROUNDEL = registerBlock("birch_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> CHERRY_ROUNDEL = registerBlock("cherry_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> DARK_OAK_ROUNDEL = registerBlock("dark_oak_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> JUNGLE_ROUNDEL = registerBlock("jungle_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> MANGROVE_ROUNDEL = registerBlock("mangrove_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> CRIMSON_ROUNDEL = registerBlock("crimson_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final DeferredHolder<Block, Block> WARPED_ROUNDEL = registerBlock("warped_roundel_block",
            () -> new RoundelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    // Skaro Blocks
    public static final DeferredHolder<Block, Block> SKARO_SAND = registerBlock("skaro_sand_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredHolder<Block, Block> SKARO_SAND_STONE = registerBlock("skaro_sand_stone_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredHolder<Item, Item> registerBlockItem(String name, DeferredHolder<Block, T> block) {
        if (name.equals("door_block") || name.equals("tardis_sign_block") || name.contains("roundel")) {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        } else {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }
}
