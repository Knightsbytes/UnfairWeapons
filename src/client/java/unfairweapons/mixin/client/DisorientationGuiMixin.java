package unfairweapons.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static unfairweapons.UnfairWeapons.INCAPACITATION_EFFECT;
import static unfairweapons.UnfairWeapons.MOD_ID;

@Mixin(targets = "net.minecraft.client.gui.Gui")
public abstract class DisorientationGuiMixin {

    public Identifier FOOD_EMPTY_INCAPACITATED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/hunger/food_empty_incapacitated");
    public Identifier FOOD_HALF_INCAPACITATED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/hunger/food_half_incapacitated");
    public Identifier FOOD_FULL_INCAPACITATED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/sprites/hunger/food_full_incapacitated");

    @Inject(
            method = "renderFood",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getNewHungerSprite(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci){
        if (player.hasEffect(INCAPACITATION_EFFECT)) {
            ci.cancel();
        }
    }


}
