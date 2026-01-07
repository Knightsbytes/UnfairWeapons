package unfairweapons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

// Target the private inner class directly using $ notation
@Mixin(targets = "net.minecraft.client.gui.Gui$HeartType")
public abstract class HeartTypeMixin {

    /**
     * Intercept the getSprite method to return custom textures
     * when player has your custom effect
     */
    @Inject(
            method = "getSprite",
            at = @At("HEAD"),
            cancellable = true
    )
    private void useCustomHeartSprite(boolean hardcore, boolean half, boolean blinking,
                                      CallbackInfoReturnable<Identifier> cir) {

        // Check if the player has your custom effect
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(PETRIFICATION_EFFECT)) {

            // Skip if this is CONTAINER - let vanilla handle empty hearts
            String thisType = this.toString();
            if (thisType.equals("CONTAINER")) {
                return; // Use default container
            }

            // Build the custom sprite path for filled hearts
            String spriteName;
            if (hardcore) {
                spriteName = half ? "custom_hardcore_half" : "custom_hardcore_full";
            } else {
                spriteName = half ? "custom_half" : "custom_full";
            }

            if (blinking) {
                spriteName += "_blinking";
            }

            // Return your custom sprite
            Identifier customSprite = Identifier.fromNamespaceAndPath(
                    MOD_ID,
                    "hud/heart/" + spriteName
            );

            cir.setReturnValue(customSprite);
        }
    }
}