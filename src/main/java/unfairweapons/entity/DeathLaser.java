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

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        Level level = level();

        for (int i = level.getMinY(); i <= level.getMaxY(); i += 5) {
            //level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
            //        this.getX(),
            //        i,
            //        this.getZ(),
            //        1D,
            //        1D,
            //        1D
            //);
            for (int x = 0; x < 50; x++) {

                double angle = (2 * Math.PI * x) / 50;
                int xOffset = (int) Math.cos(angle) * 10;
                int zOffset = (int) Math.sin(angle) * 10;

                level.setBlock(this.getOnPos().offset(xOffset, i, zOffset), Blocks.AIR.defaultBlockState(), 1);
            }
        }


    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);

    }
}
