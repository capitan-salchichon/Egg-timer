package com.brochacho.eggtimer.mixin;

import com.brochacho.eggtimer.EggTimers;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
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

@Mixin(BlockReptileEgg.class)
public abstract class ReptileEggMixin {

    /** Anula el random tick original: el addon avanza las etapas con su propio temporizador. */
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void aet$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (EggTimers.shouldCancelRandomTick(level, pos, state)) {
            ci.cancel();
        }
    }

    /** Registra el temporizador al colocar / poner el huevo. */
    @Inject(method = "onPlace", at = @At("TAIL"))
    private void aet$onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        EggTimers.onPlace(level, pos, state, oldState);
    }

    /** Cuando el addon fuerza el random tick, canGrow siempre es true (sin azar ni ventana nocturna). */
    @Inject(method = "canGrow", at = @At("HEAD"), cancellable = true, remap = false)
    private void aet$canGrow(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (EggTimers.forceGrow) {
            cir.setReturnValue(true);
        }
    }
}
