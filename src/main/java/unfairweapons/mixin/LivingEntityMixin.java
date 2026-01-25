package unfairweapons.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void modifyDamageWithEffect(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasEffect(PETRIFICATION_EFFECT)) {
            MobEffectInstance effectInstance = entity.getEffect(PETRIFICATION_EFFECT);
            int amplifier = effectInstance.getAmplifier();

            float modifiedAmount = amount * (1.0f - (0.2f * (amplifier + 1)));

            if (amount > 5){
                amount = 5;
            }
        }
    }
}
