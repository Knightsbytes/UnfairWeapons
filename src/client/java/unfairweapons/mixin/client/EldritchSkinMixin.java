package unfairweapons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(AbstractClientPlayer.class)
public abstract class EldritchSkinMixin {

    @Inject(
            method = "getSkin",
            at = @At("RETURN"),
            cancellable = true
    )
    private void unfairweapons$overrideSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        Minecraft mc = Minecraft.getInstance();


        if (mc.level == null) {
            return;
        }

        var effect = self.getEffect(PETRIFICATION_EFFECT);

        if (effect == null || effect.getAmplifier() <= 1) {
            return;
        }

        PlayerSkin original = cir.getReturnValue();
        if (original == null) {
            return;
        }


        Identifier eldritchTexture = Identifier.fromNamespaceAndPath(
                MOD_ID,
                "entity/player/eldritch"
        );

        //System.out.println("Trying to load texture: " + eldritchTexture);

        ClientAsset.ResourceTexture texture = new ClientAsset.ResourceTexture(eldritchTexture);

        PlayerSkin.Patch patch = new PlayerSkin.Patch(
                Optional.of(texture),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        cir.setReturnValue(original.with(patch));
    }
}