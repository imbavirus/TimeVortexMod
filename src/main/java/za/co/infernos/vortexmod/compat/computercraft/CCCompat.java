package za.co.infernos.vortexmod.compat.computercraft;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import za.co.infernos.vortexmod.block.entity.ModBlockEntities;

/**
 * CC: Tweaked registration entrypoint. Only referenced when computercraft is loaded.
 */
public final class CCCompat {
    private CCCompat() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                PeripheralCapability.get(),
                ModBlockEntities.VORTEX_INTERFACE_BE.get(),
                (be, side) -> new VortexInterfacePeripheral(be)
        );
    }
}
