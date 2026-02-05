package unfairweapons.mixin.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unfairweapons.FeatureRenderer;
import unfairweapons.ModelLayers;
import unfairweapons.models.StableEldritchHorns;

import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

@Mixin(net.minecraft.client.renderer.entity.player.AvatarRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer {

    // Mixin requires this constructor even though it won't be called
    public PlayerRendererMixin(EntityRendererProvider.Context context, net.minecraft.client.model.EntityModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void unfairweapons$addHorns(
            EntityRendererProvider.Context context,
            boolean slim,
            CallbackInfo ci
    ) {
        //AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;

        //var effect = player.getEffect(PETRIFICATION_EFFECT);

        //if (effect == null || effect.getAmplifier() <= 1) {
        //    return;
        //}
        EntityModelSet modelSet = context.getModelSet();
        StableEldritchHorns horns = new StableEldritchHorns(
                modelSet.bakeLayer(ModelLayers.CUSTOM_HORNS)
        );
        this.addLayer(new FeatureRenderer(this, horns));
    }
}