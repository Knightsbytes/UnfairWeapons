package unfairweapons;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

import static unfairweapons.UnfairWeapons.MOD_ID;

public class ModelLayers {
    public static final ModelLayerLocation CUSTOM_HORNS = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(MOD_ID, "stable_eldritch_horns"),
            "main"
    );
}