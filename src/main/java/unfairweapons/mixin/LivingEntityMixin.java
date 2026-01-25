package unfairweapons.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyDamageWithPetrification(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(PETRIFICATION_EFFECT)) {
            MobEffectInstance effectInstance = entity.getEffect(PETRIFICATION_EFFECT);
            int amplifier = effectInstance.getAmplifier();

            float finalDamage = cir.getReturnValue();
            finalDamage = finalDamage * (1.0f - (0.2f * (amplifier + 1)));

            if (finalDamage > 5) {
                finalDamage = 5;
            }

            cir.setReturnValue(finalDamage);
        }
    }
}