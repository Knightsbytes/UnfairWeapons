package unfairweapons.items;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CutlassItem extends Item {
    public CutlassItem(Properties properties){
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MobEffectInstance effectInstance = new MobEffectInstance(
                MobEffects.SLOWNESS,
                20,
                254,
                false,
                true,
                true
        );

        if (target != attacker) {
            target.addEffect(effectInstance);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        Holder<SoundEvent> holder = (Holder<SoundEvent>) EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                .orElse(SoundEvents.TRIDENT_THROW);

        //Don't question the variable names I copied this from the obfuscated code
        float f = 3;

        float g = player.getYRot();
        float h = player.getXRot();
        float k = -Mth.sin(g * (float) (Math.PI / 180.0)) * Mth.cos(h * (float) (Math.PI / 180.0));
        float l = -Mth.sin(h * (float) (Math.PI / 180.0));
        float m = Mth.cos(g * (float) (Math.PI / 180.0)) * Mth.cos(h * (float) (Math.PI / 180.0));
        float n = Mth.sqrt(k * k + l * l + m * m);
        k *= f / n;
        l *= f / n;
        m *= f / n;
        player.push(k, l, m);
        player.startAutoSpinAttack(20, 8.0F, stack);
        if (player.onGround()) {
            float o = 1.1999999F;
            player.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
        }

        level.playSound(null, player, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!player.isCreative()){
            player.getCooldowns().addCooldown(stack, 2);
        }


        return InteractionResult.PASS;
    }
}
