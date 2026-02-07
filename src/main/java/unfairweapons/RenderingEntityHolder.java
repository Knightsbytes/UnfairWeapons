package unfairweapons;

import net.minecraft.world.entity.LivingEntity;

public class RenderingEntityHolder {
    private static final ThreadLocal<LivingEntity> CURRENT_ENTITY = new ThreadLocal<>();

    public static void setEntity(LivingEntity entity) {
        CURRENT_ENTITY.set(entity);
    }

    public static LivingEntity getEntity() {
        return CURRENT_ENTITY.get();
    }

    public static void clear() {
        CURRENT_ENTITY.remove();
    }
}