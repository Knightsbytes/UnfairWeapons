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

import static net.minecraft.world.effect.MobEffects.*;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class ImmortalityEffect extends MobEffect {
    protected ImmortalityEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x59FF6C);
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
            if (entity.getHealth() <= entity.getMaxHealth()){
                entity.heal(entity.getMaxHealth());
            }
        }

        return super.applyEffectTick(world, entity, amplifier);
    }
}