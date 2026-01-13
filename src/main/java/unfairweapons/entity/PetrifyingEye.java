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

    private static final double EFFECT_RADIUS = 5.0; //Actually diameter
    private static final int MAX_LIFESPAN = 1200;

    private static final Map<UUID, Integer> affectedPlayerCounts = new HashMap<>();
    private final Set<UUID> playersInRangeLastTick = new HashSet<>();

    private int lifespan = 0;

    public PetrifyingEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void readAdditionalSaveData(ValueInput input) {}

    @Override
    public void addAdditionalSaveData(ValueOutput output) {}

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { return false;}

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            spawnParticleRing(serverLevel);
            tickAttributes(serverLevel);
            applyPotionEffects(serverLevel);
        }

        lifespan++;
        if (lifespan >= MAX_LIFESPAN) {
            this.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

        if (this.level() instanceof ServerLevel level) {
            for (UUID uuid : playersInRangeLastTick) {
                Player player = level.getPlayerByUUID(uuid);
                decrementAndMaybeReset(player, uuid);
            }
        }
    }

    private AABB getEffectAABB() {
        return new AABB(this.position(), this.position()).inflate(EFFECT_RADIUS);
    }

    private void applyAttributes(Player player) {
        if (!player.hasEffect(PETRIFICATION_EFFECT)) {
            AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
            if (gravity != null) gravity.setBaseValue(1000000000);

            AttributeInstance scale = player.getAttribute(Attributes.SCALE);
            if (scale != null) scale.removeModifiers();
            if (scale != null) scale.setBaseValue(1.0D);
        }
    }

    private void resetAttributes(Player player) {
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) gravity.setBaseValue(0.08D);

        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale != null) scale.setBaseValue(1.0D);
    }

    private void decrementAndMaybeReset(Player player, UUID uuid) {
        int count = affectedPlayerCounts.getOrDefault(uuid, 0) - 1;

        if (count <= 0) {
            affectedPlayerCounts.remove(uuid);
            if (player != null) resetAttributes(player);
        } else {
            affectedPlayerCounts.put(uuid, count);
        }
    }

    private void tickAttributes(ServerLevel level) {
        Set<Player> playersNow = new HashSet<>(
                level.getEntitiesOfClass(Player.class, getEffectAABB())
        );

        Set<UUID> playersNowIds = new HashSet<>();
        for (Player player : playersNow) {
            playersNowIds.add(player.getUUID());
        }
        for (UUID uuid : playersNowIds) {
            if (!playersInRangeLastTick.contains(uuid)) {
                Player player = level.getPlayerByUUID(uuid);
                if (player != null) {
                    int count = affectedPlayerCounts.getOrDefault(uuid, 0);
                    if (count == 0) {
                        applyAttributes(player);
                    }
                    affectedPlayerCounts.put(uuid, count + 1);
                }
            }
        }

        for (UUID uuid : playersInRangeLastTick) {
            if (!playersNowIds.contains(uuid)) {
                Player player = level.getPlayerByUUID(uuid);
                decrementAndMaybeReset(player, uuid);
            }
        }

        playersInRangeLastTick.clear();
        playersInRangeLastTick.addAll(playersNowIds);
    }

    private void applyPotionEffects(ServerLevel level) {
        for (Player player : level.getEntitiesOfClass(Player.class, getEffectAABB())) {
            if (!player.hasEffect(PETRIFICATION_EFFECT)) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 5, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 5, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 5, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 40, 5, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 5, false, false, true));
            }
        }
    }

    private void spawnParticleRing(ServerLevel level) {
        double radius = 5.0;
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
}
