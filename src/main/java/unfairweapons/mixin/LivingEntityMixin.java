package unfairweapons.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(Entity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(
            method = "hurtServer",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private float modifyDamageAmount(float amount, ServerLevel level, DamageSource source) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            if (livingEntity.hasEffect(PETRIFICATION_EFFECT)) {
                MobEffectInstance effectInstance = livingEntity.getEffect(PETRIFICATION_EFFECT);
                int amplifier = effectInstance.getAmplifier();

                amount = amount * (1.0f - (0.2f * (amplifier + 1)));

                if (amount > 5) {
                    amount = 5;
                }
            }
        }
        return amount;
    }
}