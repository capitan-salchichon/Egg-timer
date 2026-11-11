package com.brochacho.eggtimer;

import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.block.BlockTerrapinEgg;
import com.github.alexthe666.alexsmobs.block.BlockTriopsEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public enum EggKind {
    REPTILE, TERRAPIN, TRIOPS;

    @Nullable
    public static EggKind of(BlockState state) {
        Block b = state.getBlock();
        if (b instanceof BlockReptileEgg) return REPTILE;
        if (b instanceof BlockTerrapinEgg) return TERRAPIN;
        if (b instanceof BlockTriopsEggs) return TRIOPS;
        return null;
    }

    public int totalTicks() {
        return switch (this) {
            case REPTILE -> EggTimerConfig.REPTILE_TICKS.get();
            case TERRAPIN -> EggTimerConfig.TERRAPIN_TICKS.get();
            case TRIOPS -> EggTimerConfig.TRIOPS_TICKS.get();
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

    public boolean hasHabitat(BlockGetter level, BlockPos pos) {
        return switch (this) {
            case REPTILE -> BlockReptileEgg.hasProperHabitat(level, pos);
            case TERRAPIN -> BlockTerrapinEgg.hasProperHabitat(level, pos);
            case TRIOPS -> true;
        };
    }
}
