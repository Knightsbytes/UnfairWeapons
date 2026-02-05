package unfairweapons.mixin.client;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class ScrollWheelRemover {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void disableHotbarScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        // Cancel the scroll event entirely
        ci.cancel();
    }
}