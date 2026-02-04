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

@Mixin(net.minecraft.client.renderer.entity.player.AvatarRenderer.class)
public abstract class PlayerRendererMixin {

    @Shadow
    protected abstract boolean addLayer(RenderLayer<?, ?> layer);

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void unfairweapons$addHorns(
            EntityRendererProvider.Context context,
            boolean slim,
            CallbackInfo ci
    ) {
        System.out.println("ADDING HORNS TO PLAYER");

        EntityModelSet modelSet = context.getModelSet();

        StableEldritchHorns horns =
                new StableEldritchHorns(
                        modelSet.bakeLayer(ModelLayers.CUSTOM_HORNS)
                );

        this.addLayer(new FeatureRenderer(
                (LivingEntityRenderer)(Object)this,
                horns
        ));
    }
}