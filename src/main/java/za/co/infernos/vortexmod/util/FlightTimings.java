package za.co.infernos.vortexmod.util;

/**
 * TARDIS travel phase lengths locked to shipped OGG durations (ffprobe).
 *
 * <pre>
 * demat_sound.ogg   ≈ 20.062 s → 401 ticks @ 20 TPS
 * flight_sound.ogg  ≈ 20.062 s → 401 ticks
 * remat_sound.ogg   ≈ 24.007 s → 480 ticks
 * euc_flight_sound  ≈ 1.806 s  → 36 ticks (ambient blip interval)
 * </pre>
 *
 * Simple auto-land trip ≈ demat + flight + remat ≈ 64 s wall-clock at 20 TPS
 * (not multi-minute distance jump stacks).
 */
public final class FlightTimings {
    private FlightTimings() {
    }

    public static final int TICKS_PER_SECOND = 20;

    /** demat_sound.ogg */
    public static final double DEMAT_SECONDS = 20.062;
    /** flight_sound.ogg */
    public static final double FLIGHT_SECONDS = 20.062;
    /** remat_sound.ogg */
    public static final double REMAT_SECONDS = 24.007;
    /** euc_flight_sound.ogg — mid-flight blip, not total flight length */
    public static final double EUC_FLIGHT_BLIP_SECONDS = 1.806;

    public static final int DEMAT_TICKS = secondsToTicks(DEMAT_SECONDS);
    public static final int FLIGHT_TICKS = secondsToTicks(FLIGHT_SECONDS);
    public static final int REMAT_TICKS = secondsToTicks(REMAT_SECONDS);
    public static final int EUC_FLIGHT_BLIP_TICKS = secondsToTicks(EUC_FLIGHT_BLIP_SECONDS);

    /** Interface timer from throttle-on until ready-to-land (demat + flight). */
    public static final int READY_TO_LAND_TICKS = DEMAT_TICKS + FLIGHT_TICKS;

    /** Proto path: soft distance add, hard cap so hops cannot balloon to minutes. */
    public static final int PROTO_JUMP_BLOCKS = 250;
    public static final double PROTO_SECONDS_PER_JUMP = 2.0;
    public static final double PROTO_JUMP_TIME_CAP_SECONDS = 30.0;

    public static int secondsToTicks(double seconds) {
        return Math.max(1, (int) Math.round(seconds * TICKS_PER_SECOND));
    }

    public static double ticksToSeconds(int ticks) {
        return ticks / (double) TICKS_PER_SECOND;
    }

    /**
     * Extra flight seconds from horizontal distance (proto path only).
     * Capped so long-range does not become multi-minute by default.
     */
    public static double protoJumpExtraSeconds(double horizontalDistance) {
        if (horizontalDistance <= PROTO_JUMP_BLOCKS) {
            return 0;
        }
        int jumps = (int) Math.ceil(horizontalDistance / (double) PROTO_JUMP_BLOCKS) - 1;
        double raw = jumps * PROTO_SECONDS_PER_JUMP;
        return Math.min(raw, PROTO_JUMP_TIME_CAP_SECONDS);
    }
}
