package unfairweapons.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Player.class)
public class PlayerEntityMixin {
    //@Inject(
    //        method = "getDamageAfterMagicAbsorb",
    //        at = @At("RETURN"),
    //        cancellable = true
    //)
    //private void unfairWeaponsEldritch$Inventory
}
