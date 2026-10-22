package de.cucuking.axiomtinkerfilter.mixin;

import de.cucuking.axiomtinkerfilter.TinkerFilter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void axiomTinkerFilter$addButtons(CallbackInfo ci) {
        int buttonWidth = Math.min(150, Math.max(100, this.width / 3));
        this.addRenderableWidget(Button.builder(woolLabel(), button -> {
            TinkerFilter.toggleWool();
            button.setMessage(woolLabel());
        }).bounds(5, this.height - 50, buttonWidth, 20).build());
        this.addRenderableWidget(Button.builder(concreteLabel(), button -> {
            TinkerFilter.toggleConcrete();
            button.setMessage(concreteLabel());
        }).bounds(5, this.height - 25, buttonWidth, 20).build());
    }

    private static Component woolLabel() {
        return Component.literal("Tinker Wolle: " + (TinkerFilter.woolEnabled() ? "AN" : "AUS"));
    }

    private static Component concreteLabel() {
        return Component.literal("Tinker Concrete: " + (TinkerFilter.concreteEnabled() ? "AN" : "AUS"));
    }
}
