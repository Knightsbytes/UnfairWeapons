package unfairweapons.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.*;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class PetrifyingEye extends Entity {

    private int maxLifespan;
    private int currentLifespan;

    private static final Map<UUID, Integer> affectedPlayerCounts = new HashMap<>();

    public PetrifyingEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.maxLifespan = 1200;
        this.currentLifespan = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void readAdditionalSaveData(ValueInput input) {}

    @Override
    public void addAdditionalSaveData(ValueOutput output) {}

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            spawnParticleRing(serverLevel);
        }

        this.currentLifespan++;
        if (this.currentLifespan >= this.maxLifespan) {
            this.discard();
        }
    }

    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { return false;}

    private void spawnParticleRing(ServerLevel level) {
        double radius = 5;
        int particleCount = 20;

        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i) / particleCount;
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    this.getX() + xOffset,
                    this.getY() + 0.5,
                    this.getZ() + zOffset,
                    1,
                    0, 0, 0,
                    0.0
            );
        }
    }

    private Set<Player> getPlayersInRange(ServerLevel level) {
        return new HashSet<>(level.getEntitiesOfClass(
                Player.class,
                new AABB(this.getOnPos()).inflate(5)
        ));
    }

    private void applyAttributes(Player player) {
        if (!player.hasEffect(PETRIFICATION_EFFECT)) {
            AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
            if (gravity != null) gravity.setBaseValue(10000);

            AttributeInstance scale = player.getAttribute(Attributes.SCALE);
            if (scale != null) scale.setBaseValue(1.0D);
        }
    }

    private void resetAttributes(Player player) {
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) gravity.setBaseValue(0.08D);

        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale != null) scale.setBaseValue(1.0D);
    }

    private void tickEffect(ServerLevel level) {
        Set<Player> playersInRange = getPlayersInRange(level);

        for (Player player : playersInRange) {
            UUID uuid = player.getUUID();
            int count = affectedPlayerCounts.getOrDefault(uuid, 0);
            if (count == 0) {
            }
            affectedPlayerCounts.put(uuid, count + 1);
        }

        Iterator<Map.Entry<UUID, Integer>> it = affectedPlayerCounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            UUID uuid = entry.getKey();
            Player player = level.getPlayerByUUID(uuid);

            if (!playersInRange.contains(player)) {
                int newCount = entry.getValue() - 1;
                if (newCount <= 0) {
                    if (player != null) resetAttributes(player);
                    it.remove();
                } else {
                    entry.setValue(newCount);
                }
            }
        }
    }

    private void applyPotionEffects(ServerLevel level) {
        for (Player player : level.getEntitiesOfClass(
                Player.class,
                new AABB(this.getOnPos()).inflate(10.0)
        )) {
            if (!player.hasEffect(PETRIFICATION_EFFECT)) {
                for (var effectType : List.of(
                        MobEffects.SLOWNESS,
                        MobEffects.WEAKNESS,
                        MobEffects.WITHER,
                        MobEffects.MINING_FATIGUE,
                        MobEffects.SLOW_FALLING
                )) {
                    player.addEffect(new MobEffectInstance(effectType, 40, 5, false, false, true));
                }
            }
        }
    }
}