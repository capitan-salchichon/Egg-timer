package com.brochacho.eggtimer;

import net.minecraftforge.common.ForgeConfigSpec;

public final class EggTimerConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue REPTILE_TICKS;
    public static final ForgeConfigSpec.IntValue TERRAPIN_TICKS;
    public static final ForgeConfigSpec.IntValue TRIOPS_TICKS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        ENABLED = b.comment("Si es false, los huevos vuelven a usar el random tick original de Alex's Mobs.")
                .define("enabled", true);
        REPTILE_TICKS = b.comment("Ticks totales de eclosión de huevos de cocodrilo, caimán y ornitorrinco (20 ticks = 1 s). Se divide en 3 etapas (grietas).")
                .defineInRange("reptileEggHatchTicks", 9000, 60, 1_728_000);
        TERRAPIN_TICKS = b.comment("Ticks totales de eclosión de huevos de terrapin. Se divide en 3 etapas.")
                .defineInRange("terrapinEggHatchTicks", 9000, 60, 1_728_000);
        TRIOPS_TICKS = b.comment("Ticks de eclosión de huevos de triops (una sola etapa).")
                .defineInRange("triopsEggHatchTicks", 7800, 20, 1_728_000);
        SPEC = b.build();
    }

    private EggTimerConfig() {}
}
