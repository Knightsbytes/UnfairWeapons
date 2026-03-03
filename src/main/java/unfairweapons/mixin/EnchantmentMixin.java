package unfairweapons.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Unique
    Enchantment self = (Enchantment)(Object)this;
    @Inject(method = "modifyDamageProtection", at = @At("HEAD"), cancellable = true)
    private void reverseDamageProtection(ServerLevel serverLevel, int i, ItemStack itemStack, Entity entity, DamageSource damageSource, MutableFloat mutableFloat, CallbackInfo ci) {

        LootContext lootContext = Enchantment.damageContext(serverLevel, i, entity, damageSource);

        for (ConditionalEffect<EnchantmentValueEffect> conditionalEffect : self.getEffects(EnchantmentEffectComponents.DAMAGE_PROTECTION)) {
            if (conditionalEffect.matches(lootContext)) {
                mutableFloat.setValue(conditionalEffect.effect().process(-i, entity.getRandom(), mutableFloat.floatValue()));
            }
        }

        ci.cancel();
    }

    @Inject(method = "modifyDurabilityChange", at = @At("HEAD"), cancellable = true)
    public void modifyDurabilityChange(ServerLevel serverLevel, int i, ItemStack itemStack, MutableFloat mutableFloat, CallbackInfo ci) {
        self.modifyItemFilteredCount(EnchantmentEffectComponents.ITEM_DAMAGE, serverLevel, -i * 50, itemStack, mutableFloat);
        ci.cancel();
    }
}
