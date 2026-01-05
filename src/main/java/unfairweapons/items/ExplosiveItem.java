package unfairweapons.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplosiveItem extends Item {
    private final float explosionPower;
    private final boolean createsFire;
    private final boolean breaksBlocks;

    public ExplosiveItem(Properties properties, float explosionPower, boolean createsFire, boolean breaksBlocks) {
        super(properties);
        this.explosionPower = explosionPower;
        this.createsFire = createsFire;
        this.breaksBlocks = breaksBlocks;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = target.level();

        if (!level.isClientSide()) {
            // Get the target's position
            Vec3 pos = target.position();

            // Create explosion at the target's location
            level.explode(
                    attacker,                                          // Entity causing explosion
                    pos.x,                                             // X position
                    pos.y,                                             // Y position
                    pos.z,                                             // Z position
                    explosionPower,                                    // Power (4.0F is TNT strength)
                    createsFire,                                       // Whether it creates fire
                    breaksBlocks ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE  // Destruction type
            );
        }

        super.hurtEnemy(stack, target, attacker);
    }
}
