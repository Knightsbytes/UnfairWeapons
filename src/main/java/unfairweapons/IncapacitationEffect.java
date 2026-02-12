package unfairweapons;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IncapacitationEffect extends MobEffect {
    protected IncapacitationEffect() {
        super(MobEffectCategory.HARMFUL, 0x59FF6C);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier){
        return super.applyEffectTick(world, entity, amplifier);
    }
}
