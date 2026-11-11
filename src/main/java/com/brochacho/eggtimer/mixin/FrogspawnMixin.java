package com.brochacho.eggtimer.mixin;

import com.brochacho.eggtimer.EggTimers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Los huevos de triops extienden FrogspawnBlock; solo tocamos los BlockTriopsEggs. */
@Mixin(FrogspawnBlock.class)
public abstract class FrogspawnMixin {

    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void aet$onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        if (EggTimers.triopsOnPlace(level, pos, (Block) (Object) this)) {
            ci.cancel();
        }
    }
}
