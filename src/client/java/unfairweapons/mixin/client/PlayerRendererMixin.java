package unfairweapons.mixin.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unfairweapons.FeatureRenderer;
import unfairweapons.ModelLayers;
import unfairweapons.models.StableEldritchHorns;

@Mixin(LivingEntityRenderer.class)
public abstract class PlayerRendererMixin {

    @Shadow
    protected abstract boolean addLayer(RenderLayer<?, ?> renderLayer);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomFeature(EntityRendererProvider.Context context, CallbackInfo ci) {
        // Check the class name to ensure we only add to player renderers
        String className = this.getClass().getSimpleName();
        if (className.contains("Player")) {
            EntityModelSet modelSet = context.getModelSet();

            StableEldritchHorns hornsModel = new StableEldritchHorns(modelSet.bakeLayer(ModelLayers.CUSTOM_HORNS));
            this.addLayer(new FeatureRenderer((LivingEntityRenderer)(Object)this, hornsModel));
        }
    }
}