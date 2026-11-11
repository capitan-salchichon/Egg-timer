package com.brochacho.eggtimer.jade;

import com.brochacho.eggtimer.AlexsEggTimer;
import com.brochacho.eggtimer.EggKind;
import com.brochacho.eggtimer.client.ClientEggTimers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum EggComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation(AlexsEggTimer.MODID, "egg_timer");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (EggKind.of(accessor.getBlockState()) == null) return;

        int ticks = ClientEggTimers.remaining(accessor.getLevel(), accessor.getPosition());
        if (ticks < 0) {
            tooltip.add(Component.translatable("tooltip.alexs_egg_timer.calculating"));
        } else if (ticks == 0) {
            tooltip.add(Component.translatable("tooltip.alexs_egg_timer.hatching"));
        } else {
            tooltip.add(Component.translatable("tooltip.alexs_egg_timer.hatch_in", format(ticks)));
        }
    }

    private static String format(int ticks) {
        int totalSeconds = (ticks + 19) / 20;
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return h > 0 ? String.format("%d:%02d:%02d", h, m, s) : String.format("%d:%02d", m, s);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
