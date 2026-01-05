package unfairweapons.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static unfairweapons.ItemsRegister.BULLET;

public class ShotgunPellet extends ThrowableItemProjectile {
    public ShotgunPellet(EntityType<? extends net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball> entityType, Level level) {
        super(entityType, level);
    }

    public ShotgunPellet(Level level, LivingEntity livingEntity, ItemStack itemStack) {
        super(EntityType.SNOWBALL, livingEntity, level, itemStack);
    }

    public ShotgunPellet(Level level, double d, double e, double f, ItemStack itemStack) {
        super(EntityType.SNOWBALL, d, e, f, level, itemStack);
    }

    @Override
    protected Item getDefaultItem() {
        return BULLET;
    }

    private ParticleOptions getParticle() {
        ItemStack itemStack = this.getItem();
        return (ParticleOptions)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, itemStack));
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 3) {
            ParticleOptions particleOptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleOptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int damage = 10;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }
}
