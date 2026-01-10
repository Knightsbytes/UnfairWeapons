package unfairweapons;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static unfairweapons.UnfairWeapons.MOD_ID;

public class UnfairWeaponsClient implements ClientModInitializer {
	KeyMapping.Category ELDRITCH_ABILITIES = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "eldritch_abilities"));

	//public final KeyMapping keyDebugCrash = new KeyMapping("key.debug.crash", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);

	public final KeyMapping PetrificationAbility1 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch_ability_1", GLFW.GLFW_KEY_V, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility2 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch_ability_2", GLFW.GLFW_KEY_B, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility3 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch_ability_3", GLFW.GLFW_KEY_N, ELDRITCH_ABILITIES));

	@Override
	public void onInitializeClient() {

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}