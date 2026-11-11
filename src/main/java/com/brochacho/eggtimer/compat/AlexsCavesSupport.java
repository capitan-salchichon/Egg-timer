package com.brochacho.eggtimer.compat;

import com.brochacho.eggtimer.EggKind;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.block.TremorzillaEggBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

/** Única clase que toca clases de Alex's Caves; solo se carga si el mod está presente. */
public final class AlexsCavesSupport {
    private AlexsCavesSupport() {}

    @Nullable
    public static EggKind kindOf(Block b) {
        // El huevo de Tremorzilla nunca eclosiona por random tick (canHatchAt = false): no se toca.
        if (b instanceof TremorzillaEggBlock) return null;
        if (b instanceof DinosaurEggBlock) return EggKind.CAVES_DINO;
        return null;
    }

    public static boolean habitat(Block b, BlockGetter level, BlockPos pos) {
        return b instanceof DinosaurEggBlock egg && egg.canHatchAt(level, pos);
    }
}
