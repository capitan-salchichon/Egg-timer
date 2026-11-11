package com.brochacho.eggtimer;

import com.brochacho.eggtimer.net.EggNetwork;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlexsEggTimer.MODID)
public class AlexsEggTimer {
    public static final String MODID = "alexs_egg_timer";

    public AlexsEggTimer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EggTimerConfig.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(EggNetwork::register);
    }
}
