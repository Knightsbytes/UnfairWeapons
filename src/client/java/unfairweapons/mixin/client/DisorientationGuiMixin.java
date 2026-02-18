package unfairweapons.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

import static net.minecraft.client.gui.Gui.getMobEffectSprite;
import static unfairweapons.UnfairWeapons.INCAPACITATION_EFFECT;
import static unfairweapons.UnfairWeapons.MOD_ID;

@Mixin(targets = "net.minecraft.client.gui.Gui")
public abstract class DisorientationGuiMixin {

    public Minecraft minecraft;
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_SPRITE = Identifier.withDefaultNamespace("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/effect_background");
    private static final Identifier EFFECT_DISORIENTED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "mob_effect/disorientation");

    public DisorientationGuiMixin(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

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

    @Inject(
            method = "renderEffects",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (!collection.isEmpty() && (this.minecraft.screen == null || !this.minecraft.screen.showsActiveEffects())) {
            int i = 0;
            int j = 0;

            for (MobEffectInstance mobEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                Holder<MobEffect> holder = mobEffectInstance.getEffect();
                if (mobEffectInstance.showIcon()) {
                    int k = guiGraphics.guiWidth();
                    int l = 1;
                    if (this.minecraft.isDemo()) {
                        l += 15;
                    }

                    if (((MobEffect)holder.value()).isBeneficial()) {
                        i++;
                        k -= 25 * i;
                    } else {
                        j++;
                        k -= 25 * j;
                        l += 26;
                    }

                    float f = 1.0F;
                    if (mobEffectInstance.isAmbient()) {
                        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_SPRITE, k, l, 24, 24);
                    } else {
                        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SPRITE, k, l, 24, 24);
                        if (mobEffectInstance.endsWithin(200)) {
                            int m = mobEffectInstance.getDuration();
                            int n = 10 - m / 20;
                            f = Mth.clamp(m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos(m * (float) Math.PI / 5.0F) * Mth.clamp(n / 10.0F * 0.25F, 0.0F, 0.25F);
                            f = Mth.clamp(f, 0.0F, 1.0F);
                        }
                    }
                    if (this.minecraft.player.hasEffect(INCAPACITATION_EFFECT)) {
                        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_DISORIENTED_SPRITE, k + 3, l + 3, 18, 18, ARGB.white(f));
                    }
                    else{
                        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getMobEffectSprite(holder), k + 3, l + 3, 18, 18, ARGB.white(f));
                    }
                }
            }
        }
    }
}
