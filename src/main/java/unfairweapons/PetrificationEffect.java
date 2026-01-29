package unfairweapons;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.minecraft.world.effect.MobEffects.*;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class PetrificationEffect extends MobEffect {
    protected PetrificationEffect() {
        super(MobEffectCategory.HARMFUL, 0x59FF6C);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (entity.isOnFire()){
                entity.clearFire();
            }

            if (entity.getHealth() <= 20){
                MobEffectInstance HealthRegen = new MobEffectInstance(REGENERATION, 200, 3);
                entity.addEffect(HealthRegen);
            }

            player.getFoodData().eat(1, 1.0F);

            if (!player.onGround()){
                player.fallDistance = 999;
            }

            entity.removeEffect(NAUSEA);
            entity.removeEffect(POISON);
            entity.removeEffect(WEAKNESS);
            entity.removeEffect(SLOWNESS);
            entity.removeEffect(BLINDNESS);
            entity.removeEffect(JUMP_BOOST);
            entity.removeEffect(WITHER);
            entity.removeEffect(MINING_FATIGUE);
            entity.removeEffect(OOZING);
            entity.removeEffect(DARKNESS);

            entity.isInPowderSnow = false;

            AttributeMap entityAttributes = entity.getAttributes();

            entityAttributes.resetBaseValue(Attributes.GRAVITY);
            entityAttributes.resetBaseValue(Attributes.SCALE);
            entityAttributes.resetBaseValue(Attributes.CAMERA_DISTANCE);
            entityAttributes.resetBaseValue(Attributes.ATTACK_KNOCKBACK);
            entityAttributes.resetBaseValue(Attributes.MOVEMENT_EFFICIENCY);


        }

        return super.applyEffectTick(world, entity, amplifier);
    }

}