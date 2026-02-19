package unfairweapons.mixin.client;

import com.google.common.collect.Ordering;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;

import static net.minecraft.client.gui.Gui.getMobEffectSprite;
import static unfairweapons.UnfairWeapons.INCAPACITATION_EFFECT;
import static unfairweapons.UnfairWeapons.MOD_ID;

@Mixin(targets = "net.minecraft.client.gui.Gui")
public abstract class DisorientationGuiMixin {

    public Minecraft minecraft;
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_SPRITE = Identifier.withDefaultNamespace("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/effect_background");
    private static final Identifier EFFECT_DISORIENTED_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "mob_effect/disorientation");
    private static final Identifier CROSSHAIR_SPRITE = Identifier.withDefaultNamespace("hud/crosshair");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_progress");

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
            at = @At("RETURN"),
            cancellable = true
    )
    private void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        assert this.minecraft.player != null;
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

    @Inject(
            method = "renderCrosshair",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            assert this.minecraft.gameMode != null;
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
                    guiGraphics.nextStratum();
                    int i = 15;
                    assert this.minecraft.player != null;
                    if (this.minecraft.player.hasEffect(INCAPACITATION_EFFECT)){
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, (guiGraphics.guiWidth() - 55) / 2, (guiGraphics.guiHeight() - 15) / 2, 15, 15);
                    }
                    else{
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 15, 15);
                    }
                    if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                        float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                        boolean bl = false;
                        if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                            bl = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                            bl &= this.minecraft.crosshairPickEntity.isAlive();
                            AttackRange attackRange = this.minecraft.player.getActiveItem().get(DataComponents.ATTACK_RANGE);
                            bl &= attackRange == null || attackRange.isInRange(this.minecraft.player, Objects.requireNonNull(this.minecraft.hitResult).getLocation());
                        }

                        int j = guiGraphics.guiHeight() / 2 - 7 + 16;
                        int k = guiGraphics.guiWidth() / 2 - 8;
                        if (bl) {
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
                        } else if (f < 1.0F) {
                            int l = (int)(f * 17.0F);
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
                        }
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(
            method = "renderItemHotbar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci){
        assert this.minecraft.player != null;
        if (this.minecraft.player.hasEffect(INCAPACITATION_EFFECT)){ci.cancel();}
    }
}
