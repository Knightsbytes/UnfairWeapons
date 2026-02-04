package unfairweapons.mixin.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unfairweapons.FeatureRenderer;
import unfairweapons.ModelLayers;
import unfairweapons.models.StableEldritchHorns;

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
        System.out.println("ADDING HORNS TO PLAYER");
        EntityModelSet modelSet = context.getModelSet();
        StableEldritchHorns horns = new StableEldritchHorns(
                modelSet.bakeLayer(ModelLayers.CUSTOM_HORNS)
        );
        this.addLayer(new FeatureRenderer(this, horns));
    }
}