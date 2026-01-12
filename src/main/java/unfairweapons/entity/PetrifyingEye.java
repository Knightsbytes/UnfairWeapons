package unfairweapons.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
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
import unfairweapons.PetrificationEffect;

import java.util.*;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class PetrifyingEye extends Entity {

    private int maxLifespan;
    private int currentLifespan;
    private final Set<UUID> affectedPlayers = new HashSet<>();

    public PetrifyingEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.maxLifespan = 1200; //In ticks
        this.currentLifespan = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // Define any synced data here
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        // Load data from ValueInput
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        // Save data to ValueOutput
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            spawnParticleRing(serverLevel);
        }

        this.currentLifespan += 1;

        if (this.currentLifespan >= this.maxLifespan){
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false; // Invulnerable
    }

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

    private Set<UUID> getPlayersInRange(ServerLevel level) {
        Set<UUID> inRange = new HashSet<>();

        for (Player player : level.getEntitiesOfClass(
                Player.class,
                new AABB(this.getOnPos()).inflate(10.0)
        )) {
            inRange.add(player.getUUID());
        }

        return inRange;
    }

    private void applyToPlayer(Player player){


        List<MobEffectInstance> effectInstances = List.of(
                new MobEffectInstance(MobEffects.SLOWNESS, 10, 5),
                new MobEffectInstance(MobEffects.WEAKNESS, 10, 5),
                new MobEffectInstance(MobEffects.WITHER, 10, 5),
                new MobEffectInstance(MobEffects.MINING_FATIGUE, 10, 5),
                new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 5)
        );

        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) {
            gravity.setBaseValue(10000D);
        }

        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale != null) {
            scale.setBaseValue(1.0D);
        }

        scale.removeModifiers();

        for (MobEffectInstance effectInstance : effectInstances){
            player.addEffect(effectInstance);
        }
    }

    private void resetPlayer(Player player) {
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) gravity.setBaseValue(0.08D);

        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        if (scale != null) scale.setBaseValue(1.0D);
    }

    private void tickEffect(ServerLevel level) {
        Set<UUID> currentlyInRange = getPlayersInRange(level);

        // Players who just entered
        for (UUID uuid : currentlyInRange) {
            if (!affectedPlayers.contains(uuid)) {
                Player player = level.getPlayerByUUID(uuid);
                if (player != null) {
                    applyToPlayer(player);
                    affectedPlayers.add(uuid);
                }
            }
        }

        // Players who just left
        Iterator<UUID> it = affectedPlayers.iterator();
        while (it.hasNext()) {
            UUID uuid = it.next();
            if (!currentlyInRange.contains(uuid)) {
                Player player = level.getPlayerByUUID(uuid);
                if (player != null) {
                    resetPlayer(player);
                }
                it.remove();
            }
        }
    }
}