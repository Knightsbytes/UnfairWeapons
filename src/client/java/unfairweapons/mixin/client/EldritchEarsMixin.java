package unfairweapons.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class EldritchEarsMixin {
    @Inject(
            method = "showExtraEars",
            at = @At("RETURN"),
            cancellable = true
    )
    public void unfiairweapons$addears(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
}
