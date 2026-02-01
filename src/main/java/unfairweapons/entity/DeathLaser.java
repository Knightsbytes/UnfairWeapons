package unfairweapons.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class DeathLaser extends Entity {
    public DeathLaser(EntityType<?> type, Level level) {
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

    boolean hasRun = false;

    @Override
    public void tick() {
        super.tick();

        // Stop movement
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        // Run only once
        if (!this.level().isClientSide() && !hasRun) {
            hasRun = true;

            ServerLevel level = (ServerLevel) this.level();
            BlockPos base = this.blockPosition();

            int minY = level.getMinY();
            int maxY = level.getMaxY() - 1;
            int radius = 10;

            for (int y = minY; y <= maxY; y++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {

                        if (dx * dx + dz * dz <= radius * radius) {
                            BlockPos pos = base.offset(dx, y - base.getY(), dz);

                            if (!level.isLoaded(pos)) continue;

                            BlockState state = level.getBlockState(pos);

                            if (!state.isAir()) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                }


                level.sendParticles(
                        ParticleTypes.EXPLOSION_EMITTER,
                        this.getX(),
                        y,
                        this.getZ(),
                        1,
                        1, 1, 1,
                        0.1
                );
            }
        }
        else{
            discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

    }
}
