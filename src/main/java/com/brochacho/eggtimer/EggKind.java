package com.brochacho.eggtimer;

import com.brochacho.eggtimer.compat.AlexsCavesSupport;
import com.brochacho.eggtimer.compat.AlexsMobsSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public enum EggKind {
    REPTILE, TERRAPIN, TRIOPS, CAVES_DINO;

    private static Boolean mobsLoaded;
    private static Boolean cavesLoaded;

    public static boolean mobsLoaded() {
        if (mobsLoaded == null) mobsLoaded = ModList.get().isLoaded("alexsmobs");
        return mobsLoaded;
    }

    public static boolean cavesLoaded() {
        if (cavesLoaded == null) cavesLoaded = ModList.get().isLoaded("alexscaves");
        return cavesLoaded;
    }

    @Nullable
    public static EggKind of(BlockState state) {
        Block b = state.getBlock();
        if (mobsLoaded()) {
            EggKind k = AlexsMobsSupport.kindOf(b);
            if (k != null) return k;
        }
        if (cavesLoaded()) {
            return AlexsCavesSupport.kindOf(b);
        }
        return null;
    }

    public int totalTicks() {
        return switch (this) {
            case REPTILE -> EggTimerConfig.REPTILE_TICKS.get();
            case TERRAPIN -> EggTimerConfig.TERRAPIN_TICKS.get();
            case TRIOPS -> EggTimerConfig.TRIOPS_TICKS.get();
            case CAVES_DINO -> EggTimerConfig.CAVES_DINO_TICKS.get();
        };
    }

    /** Ticks que dura cada etapa (grieta). Triops tiene una sola. */
    public int stageTicks() {
        return this == TRIOPS ? totalTicks() : Math.max(1, totalTicks() / 3);
    }

    /** Ticks que faltan hasta eclosionar, dado el contador de la etapa actual. */
    public int totalRemaining(BlockState state, int stageLeft) {
        if (this == TRIOPS) return stageLeft;
        int hatch = state.getValue(BlockStateProperties.HATCH);
        return stageLeft + Math.max(0, 2 - hatch) * stageTicks();
    }

    public boolean hasHabitat(BlockGetter level, BlockPos pos, BlockState state) {
        Block b = state.getBlock();
        return switch (this) {
            case REPTILE, TERRAPIN -> AlexsMobsSupport.habitat(b, level, pos);
            case CAVES_DINO -> AlexsCavesSupport.habitat(b, level, pos);
            case TRIOPS -> true;
        };
    }
}
