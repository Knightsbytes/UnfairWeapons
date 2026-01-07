package unfairweapons;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.world.effect.MobEffects.REGENERATION;
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
        if (entity instanceof Player) {
            if (entity.isOnFire()){
                entity.clearFire();
            }

            if (entity.getHealth() <= 4){
                MobEffectInstance HealthRegen = new MobEffectInstance(REGENERATION, 2, 0);
                entity.addEffect(HealthRegen);
            }
        }

        return super.applyEffectTick(world, entity, amplifier);
    }
}