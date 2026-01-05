package unfairweapons.items;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import unfairweapons.projectile.ShotgunPellet;

import static unfairweapons.ItemsRegister.BULLET;

public class GunItem extends Item implements ProjectileItem {
    public static float PROJECTILE_SHOOT_POWER = 5F;
    public static float SPREAD_RADIUS = 1.5F; // 2 block radius

    public GunItem(Properties properties){
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();

            // Get player's look direction
            Vec3 lookVec = player.getLookAngle();

            for (int i = 0; i < 15; i++) {
                // Generate random offset within 2 block radius
                double offsetX = (random.nextDouble() - 0.5) * SPREAD_RADIUS * 2;
                double offsetY = (random.nextDouble() - 0.5) * SPREAD_RADIUS * 2;
                double offsetZ = (random.nextDouble() - 0.5) * SPREAD_RADIUS * 2;

                // Calculate spawn position (3 blocks in front of player + random offset)
                Vec3 spawnPos = player.position()
                        .add(0, player.getEyeHeight(), 0)  // Start at eye level
                        .add(lookVec.scale(0))           // 3 blocks forward
                        .add(offsetX, offsetY, offsetZ);   // Add random offset

                // Create the projectile at the offset position
                ShotgunPellet pellet = new ShotgunPellet(
                        serverLevel,
                        spawnPos.x,
                        spawnPos.y,
                        spawnPos.z,
                        new ItemStack(BULLET)
                );

                // Set the projectile's velocity toward the look direction
                pellet.setOwner(player);
                pellet.shootFromRotation(
                        player,
                        player.getXRot(),
                        player.getYRot(),
                        0.0F,
                        PROJECTILE_SHOOT_POWER,
                        0.0F  // No additional inaccuracy since we already offset position
                );

                serverLevel.addFreshEntity(pellet);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        if (!player.isCreative()) {
            player.getCooldowns().addCooldown(itemStack, 100);
        }

        return InteractionResult.PASS;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        return new ShotgunPellet(level, position.x(), position.y(), position.z(), itemStack);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.CROSSBOW;
    }
}