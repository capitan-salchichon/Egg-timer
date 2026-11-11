package com.brochacho.eggtimer.compat;

import com.brochacho.eggtimer.EggKind;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.block.BlockTerrapinEgg;
import com.github.alexthe666.alexsmobs.block.BlockTriopsEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/** Única clase que toca clases de Alex's Mobs; solo se carga si el mod está presente. */
public final class AlexsMobsSupport {
    private AlexsMobsSupport() {}

    @Nullable
    public static EggKind kindOf(Block b) {
        if (b instanceof BlockReptileEgg) return EggKind.REPTILE;
        if (b instanceof BlockTerrapinEgg) return EggKind.TERRAPIN;
        if (b instanceof BlockTriopsEggs) return EggKind.TRIOPS;
        return null;
    }

    public static boolean habitat(Block b, BlockGetter level, BlockPos pos) {
        if (b instanceof BlockReptileEgg) return BlockReptileEgg.hasProperHabitat(level, pos);
        if (b instanceof BlockTerrapinEgg) return BlockTerrapinEgg.hasProperHabitat(level, pos);
        return true;
    }

    public static boolean isTriops(Block b) {
        return b instanceof BlockTriopsEggs;
    }
}
