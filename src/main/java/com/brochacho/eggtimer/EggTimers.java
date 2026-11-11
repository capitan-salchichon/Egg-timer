package com.brochacho.eggtimer;

import com.brochacho.eggtimer.data.EggTimerData;
import com.brochacho.eggtimer.net.EggNetwork;
import com.github.alexthe666.alexsmobs.block.BlockTriopsEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Hooks llamados desde los mixins + tick del servidor. */
@Mod.EventBusSubscriber(modid = AlexsEggTimer.MODID)
public final class EggTimers {
    /** true solo mientras el addon ejecuta el randomTick original de forma forzada. */
    public static boolean forceGrow = false;

    private EggTimers() {}

    /** Llamado desde onPlace (TAIL) de los huevos reptil/terrapin. */
    public static void onPlace(Level level, BlockPos pos, BlockState state, BlockState oldState) {
        if (!EggTimerConfig.ENABLED.get() || !(level instanceof ServerLevel sl)) return;
        EggKind kind = EggKind.of(state);
        if (kind == null || kind == EggKind.TRIOPS) return;
        EggTimerData data = EggTimerData.get(sl);
        if (oldState.is(state.getBlock())) {
            data.registerIfAbsent(pos, kind.stageTicks()); // cambio de HATCH/EGGS: conserva el timer
        } else {
            data.set(pos, kind.stageTicks());              // huevo nuevo
        }
    }

    /** Llamado desde randomTick (HEAD). true = cancelar el random tick original. */
    public static boolean shouldCancelRandomTick(ServerLevel level, BlockPos pos, BlockState state) {
        if (!EggTimerConfig.ENABLED.get() || forceGrow) return false;
        EggKind kind = EggKind.of(state);
        if (kind != null && kind != EggKind.TRIOPS) {
            // huevos que ya existían antes del addon: se registran la primera vez que reciben random tick
            EggTimerData.get(level).registerIfAbsent(pos, kind.stageTicks());
        }
        return true;
    }

    /** Llamado desde FrogspawnBlock.onPlace (HEAD). true = cancelar el scheduleTick original. */
    public static boolean triopsOnPlace(Level level, BlockPos pos, Block block) {
        if (!EggTimerConfig.ENABLED.get() || !(block instanceof BlockTriopsEggs) || !(level instanceof ServerLevel sl)) {
            return false;
        }
        int delay = EggKind.TRIOPS.totalTicks();
        level.scheduleTick(pos, block, delay);
        EggTimerData.get(sl).set(pos, delay);
        return true;
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        if (!EggTimerConfig.ENABLED.get()) return;
        EggTimerData data = EggTimerData.get(level);
        data.tick(level);
        if (level.getGameTime() % 20L == 0L) {
            EggNetwork.syncPlayers(level, data);
        }
    }
}
