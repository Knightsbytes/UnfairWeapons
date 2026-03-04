package unfairweapons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import unfairweapons.models.StableEldritchHorns;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class EldritchHornsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/entity/player/eldritch_horns.png");
    private final StableEldritchHorns model;

    public EldritchHornsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.model = new StableEldritchHorns(entityModelSet.bakeLayer(EldritchHornsLayer.LAYER_LOCATION));
    }

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(MOD_ID, "eldritch_horns"), "main"
    );

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, AvatarRenderState state, float f, float g) {
        if (Minecraft.getInstance().level == null) return;
        var player = Minecraft.getInstance().level.getEntity(state.id);
        if (!(player instanceof Player p) || p.getEffect(PETRIFICATION_EFFECT) == null) return;

        PlayerModel parentModel = this.getParentModel();
        this.model.copyHeadRotation(parentModel.head);

        renderColoredCutoutModel(this.model, TEXTURE, poseStack, collector, light, state, -1, 0);
    }
}