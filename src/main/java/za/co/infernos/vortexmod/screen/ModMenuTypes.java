package za.co.infernos.vortexmod.screen;

import net.minecraft.core.registries.Registries;
import za.co.infernos.vortexmod.VortexMod;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import za.co.infernos.vortexmod.screen.custom.menu.KeypadMenu;
import za.co.infernos.vortexmod.screen.custom.menu.ScannerMenu;
import za.co.infernos.vortexmod.screen.custom.menu.SizeManipulatorMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, VortexMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SizeManipulatorMenu>> SIZE_MANIPULATOR_MENU =
            registerMenuType("size_manipulator_menu", SizeManipulatorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<KeypadMenu>> KEYPAD_MENU =
            registerMenuType("keypad_menu", KeypadMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ScannerMenu>> SCANNER_MENU =
            registerMenuType("scanner_menu", ScannerMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}