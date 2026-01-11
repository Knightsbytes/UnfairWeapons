package unfairweapons.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PetrifyingEye extends Entity {

    public PetrifyingEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
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
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false; // Invulnerable
    }
}