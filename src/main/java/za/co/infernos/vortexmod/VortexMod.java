package za.co.infernos.vortexmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import za.co.infernos.vortexmod.block.custom.VortexInterfaceBlock;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import za.co.infernos.vortexmod.block.ModBlocks;
import za.co.infernos.vortexmod.block.entity.*;
import za.co.infernos.vortexmod.entities.ModEntities;
import za.co.infernos.vortexmod.entities.client.renderers.DalekRenderer;
import za.co.infernos.vortexmod.entities.client.renderers.LaserRenderer;
import za.co.infernos.vortexmod.entities.client.renderers.LostTravelerRenderer;
import za.co.infernos.vortexmod.entities.client.renderers.TardisRenderer;
import za.co.infernos.vortexmod.item.ModCreativeModeTabs;
import za.co.infernos.vortexmod.item.ModItems;
import za.co.infernos.vortexmod.screen.custom.screen.KeypadScreen;
import za.co.infernos.vortexmod.screen.custom.screen.ScannerScreen;
import za.co.infernos.vortexmod.sound.ModSounds;
import za.co.infernos.vortexmod.screen.ModMenuTypes;
import za.co.infernos.vortexmod.screen.custom.screen.SizeManipulatorScreen;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(VortexMod.MODID)
public class VortexMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vortexmod";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public VortexMod(IEventBus modEventBus) {
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        // TicketController is created statically in VortexInterfaceBlock
        // It should be automatically registered when first used
        LOGGER.info("Mod initialized - TicketController will be available when needed");

        // Register worldgen data
        // In NeoForge 1.21.1, worldgen data must be provided through datapacks
        // The RegistrySetBuilder is used by datagen to generate the datapack files
        // For now, we'll need to generate datapack files or use a different
        // registration method

        // Register TicketController on the Mod Event Bus
        modEventBus.addListener(this::registerTicketControllers);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    public void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(VortexInterfaceBlock.getTicketController());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting logic
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            EntityRenderers.register(ModEntities.BLUE_TRADER.get(), LostTravelerRenderer::new);
            EntityRenderers.register(ModEntities.ORANGE_TRADER.get(), LostTravelerRenderer::new);
            EntityRenderers.register(ModEntities.PURPLE_TRADER.get(), LostTravelerRenderer::new);
            EntityRenderers.register(ModEntities.BLACK_TRADER.get(), LostTravelerRenderer::new);

            EntityRenderers.register(ModEntities.GOLD_DALEK.get(), DalekRenderer::new);
            EntityRenderers.register(ModEntities.SILVER_DALEK.get(), DalekRenderer::new);
            EntityRenderers.register(ModEntities.BLACK_DALEK.get(), DalekRenderer::new);
            EntityRenderers.register(ModEntities.SILVER_BLACK_DALEK.get(), DalekRenderer::new);

            EntityRenderers.register(ModEntities.LASER_ENTITY.get(), LaserRenderer::new);

            EntityRenderers.register(ModEntities.TARDIS.get(), TardisRenderer::new);

            // Menu screen registration is handled in ModEventBusClientEvents.onClientSetup
        }
    }
}
