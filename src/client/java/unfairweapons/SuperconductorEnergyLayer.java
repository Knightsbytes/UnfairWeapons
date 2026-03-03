package unfairweapons;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SuperconductorEnergyLayer extends EnergySwirlLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final PlayerModel model;

    public SuperconductorEnergyLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, EntityModelSet entityModelSet) {
        super(parent);
        this.model = new PlayerModel(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
    }

    @Override
    protected boolean isPowered(AvatarRenderState state) {
        return true;
    }

    @Override
    protected float xOffset(float f) {
        return f * 0.01F;
    }

    @Override
    protected Identifier getTextureLocation() {
        return TEXTURE;
    }

    @Override
    protected PlayerModel model() {
        return this.model;
    }
}