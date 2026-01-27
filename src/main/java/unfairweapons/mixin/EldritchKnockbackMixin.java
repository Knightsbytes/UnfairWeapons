package unfairweapons.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(LivingEntity.class)
public abstract class EldritchKnockbackMixin {

    @Inject(
            method = "knockback",
            at = @At("HEAD"),
            cancellable = true
    )
    private void unfairweapons$cancelKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(PETRIFICATION_EFFECT)) {
            ci.cancel();
        }
    }
}