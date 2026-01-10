package unfairweapons.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class PetrificationNeedleItem extends Item {
    public PetrificationNeedleItem(Properties properties){
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand){
        ItemStack stack = player.getItemInHand(interactionHand);

        MobEffectInstance petrification2 = new MobEffectInstance(
                PETRIFICATION_EFFECT,
                90000,
                1
        );

        player.addEffect(petrification2);

        if (!player.isCreative()){
            stack.consume(1, player);
        }

        return InteractionResult.SUCCESS;
    }
}
