package com.brochacho.eggtimer.mixin;

import com.brochacho.eggtimer.EggTimers;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Huevos de dinosaurio de Alex's Caves (DinosaurEggBlock y MultipleDinosaurEggsBlock). */
@Mixin(DinosaurEggBlock.class)
public abstract class CavesEggMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void aet$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (EggTimers.shouldCancelRandomTick(level, pos, state)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlace", at = @At("TAIL"))
    private void aet$onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        EggTimers.onPlace(level, pos, state, oldState);
    }

    /** canGrow(Level, BlockState): la probabilidad 1/20 (1/10 sobre fern thatch) se ignora cuando el addon fuerza la etapa. */
    @Inject(method = "canGrow", at = @At("HEAD"), cancellable = true, remap = false)
    private void aet$canGrow(Level level, BlockState stateBelow, CallbackInfoReturnable<Boolean> cir) {
        if (EggTimers.forceGrow) {
            cir.setReturnValue(true);
        }
    }
}
