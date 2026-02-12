package unfairweapons.weather;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import unfairweapons.PetrificationEffect;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class EldritchRain {
    private int duration;
    private int ticksActive;

    public EldritchRain(int duration) {
        this.duration = duration;
        this.ticksActive = 0;
    }

    public void tick(ServerLevel level) {
        this.ticksActive++;

        RandomSource random = level.getRandom();

        level.players().forEach(player -> {
            if (player.hasEffect(PETRIFICATION_EFFECT)){
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 10, 1, false, false, true));
            }

        });
    }

    public boolean isFinished() {
        return ticksActive >= duration;
    }

    public int getTicksActive() {
        return ticksActive;
    }

    public int getDuration() {
        return duration;
    }
}