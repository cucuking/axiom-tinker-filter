package de.cucuking.axiomtinkerfilter.mixin;

import de.cucuking.axiomtinkerfilter.TinkerFilter;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.moulberry.axiom.capabilities.Tinker", remap = false)
public class TinkerMixin {
    @Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true, remap = false)
    private static void axiomTinkerFilter$skipExcludedBlocks(CallbackInfoReturnable<InteractionResult> cir) {
        if (TinkerFilter.shouldSkipTinker()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
