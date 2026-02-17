package za.co.infernos.vortexmod.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import za.co.infernos.vortexmod.VortexMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, VortexMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> DEMAT_SOUND = registerSoundEvents("demat_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> FLIGHT_SOUND = registerSoundEvents("flight_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> EUC_FLIGHT_SOUND = registerSoundEvents("euc_flight_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAT_SOUND = registerSoundEvents("remat_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> THROTTLE_SOUND = registerSoundEvents("throttle_sound");

    public static final DeferredHolder<SoundEvent, SoundEvent> MONITOR_TOGGLE_SOUND = registerSoundEvents("monitor_toggle_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOTI_UPGRADE_SOUND = registerSoundEvents("boti_upgrade_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> COORDINATE_KEYPAD_SET_SOUND = registerSoundEvents("coordinate_keypad_set_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIOSEC_PLAYER_REMOVED_SOUND = registerSoundEvents("biosec_player_removed_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIOSEC_PLAYER_ADDED_SOUND = registerSoundEvents("biosec_player_added_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> DESIGNATOR_SWITCH_SOUND = registerSoundEvents("designator_switch_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> DESIGNATOR_BUTTON_SOUND = registerSoundEvents("designator_button_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> EQUALIZER_PLACE_SOUND = registerSoundEvents("equalizer_place_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> AUTO_SURFACE_PLACE_SOUND = registerSoundEvents("auto_surface_place_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCANNER_SOUND = registerSoundEvents("scanner_sound");

    public static final DeferredHolder<SoundEvent, SoundEvent> DALEK_MOVE_SOUND = registerSoundEvents("dalek_move_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> DALEK_SHOOT_SOUND = registerSoundEvents("dalek_shoot_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> DALEK_DEATH_SOUND = registerSoundEvents("dalek_death_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> DALEK_EXTERMINATE_SOUND = registerSoundEvents("dalek_exterminate_sound");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String sound) {
        return SOUND_EVENTS.register(sound, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, sound)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
