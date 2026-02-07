package unfairweapons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unfairweapons.RenderingEntityHolder;

@Mixin(LivingEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    private void captureEntity(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        RenderingEntityHolder.setEntity(entity);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"))
    private void clearEntity(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        RenderingEntityHolder.clear();
    }
}