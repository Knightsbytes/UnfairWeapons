package unfairweapons;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayCastHelper {

    public static Vec3 raycast(Player player, double maxDistance, boolean hitFluids) {
        Level level = player.level();

        // Get the start position (player's eyes)
        Vec3 eyePos = player.getEyePosition(1.0F);

        // Get the look direction
        Vec3 lookVec = player.getViewVector(1.0F);

        // Calculate the end position
        Vec3 endPos = eyePos.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        // Create the raycast context
        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE, // What blocks to hit
                hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, // Whether to hit fluids
                player
        );

        // Perform the raycast
        BlockHitResult blockHit = level.clip(context);

        // Check if we hit something
        if (blockHit.getType() != HitResult.Type.MISS) {
            return blockHit.getLocation();
        }

        return null;
    }

    /**
     * Raycast including entities
     */
    public static Vec3 raycastWithEntities(Player player, double maxDistance) {
        Level level = player.level();
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        // Raycast for blocks
        ClipContext context = new ClipContext(
                eyePos,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        );
        BlockHitResult blockHit = level.clip(context);

        // Raycast for entities
        EntityHitResult entityHit = raycastEntity(player, eyePos, endPos, maxDistance);

        // Return whichever is closer
        if (entityHit != null && blockHit.getType() != HitResult.Type.MISS) {
            double entityDist = eyePos.distanceTo(entityHit.getLocation());
            double blockDist = eyePos.distanceTo(blockHit.getLocation());
            return entityDist < blockDist ? entityHit.getLocation() : blockHit.getLocation();
        } else if (entityHit != null) {
            return entityHit.getLocation();
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            return blockHit.getLocation();
        }

        return null;
    }

    private static EntityHitResult raycastEntity(Player player, Vec3 start, Vec3 end, double maxDistance) {
        Level level = player.level();
        Vec3 direction = end.subtract(start).normalize();

        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        double closestDistance = maxDistance;

        for (Entity entity : level.getEntities(player, player.getBoundingBox().expandTowards(direction.scale(maxDistance)).inflate(1.0))) {
            if (entity == player) continue;

            Vec3 hitPos = entity.getBoundingBox().clip(start, end).orElse(null);
            if (hitPos != null) {
                double distance = start.distanceTo(hitPos);
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestHitPos = hitPos;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity != null ? new EntityHitResult(closestEntity, closestHitPos) : null;
    }
}