package com.brochacho.eggtimer.client;

import com.brochacho.eggtimer.net.EggTimersPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class ClientEggTimers {
    private static ResourceLocation dimension;
    /** pos -> {ticks restantes al recibir, gameTime del cliente al recibir} */
    private static final Map<Long, long[]> TIMERS = new HashMap<>();

    private ClientEggTimers() {}

    public static void update(EggTimersPacket msg) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        long now = level.getGameTime();
        dimension = msg.dimension;
        TIMERS.clear();
        for (int i = 0; i < msg.positions.length; i++) {
            TIMERS.put(msg.positions[i], new long[]{msg.ticks[i], now});
        }
    }

    /** Ticks restantes estimados en tiempo real, o -1 si el servidor aún no los ha enviado. */
    public static int remaining(Level level, BlockPos pos) {
        if (dimension == null || !level.dimension().location().equals(dimension)) return -1;
        long[] e = TIMERS.get(pos.asLong());
        if (e == null) return -1;
        long left = e[0] - (level.getGameTime() - e[1]);
        return (int) Math.max(0L, left);
    }
}
