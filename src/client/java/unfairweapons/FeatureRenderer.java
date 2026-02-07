package unfairweapons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import unfairweapons.models.StableEldritchHorns;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class FeatureRenderer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/player/stableeldritchhorns.png");
    private final StableEldritchHorns customModel;

    public FeatureRenderer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> renderer, StableEldritchHorns model) {
        super(renderer);
        this.customModel = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       HumanoidRenderState renderState, float netHeadYaw, float headPitch) {

        LivingEntity entity = RenderingEntityHolder.getEntity();
        if (entity == null) {
            return;
        }
        if (!entity.hasEffect(PETRIFICATION_EFFECT)) {
            return;
        }

        // Rest of your rendering code...
        poseStack.pushPose();
        poseStack.translate(0.0, -1.5, 0.0);
        this.customModel.setupAnim(renderState);
        submitNodeCollector.submitModelPart(
                this.customModel.root(),
                poseStack,
                this.customModel.renderType(TEXTURE),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                null
        );
        poseStack.popPose();
    }
}