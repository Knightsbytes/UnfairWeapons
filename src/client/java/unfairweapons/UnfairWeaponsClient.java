package unfairweapons;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.lwjgl.glfw.GLFW;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class UnfairWeaponsClient implements ClientModInitializer {
	KeyMapping.Category ELDRITCH_ABILITIES = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "eldritch_abilities"));

	//public final KeyMapping keyDebugCrash = new KeyMapping("key.debug.crash", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);

	public final KeyMapping PetrificationAbility1 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_1", GLFW.GLFW_KEY_V, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility2 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_2", GLFW.GLFW_KEY_B, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility3 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_3", GLFW.GLFW_KEY_N, ELDRITCH_ABILITIES));

	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (PetrificationAbility1.consumeClick()) {
				MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

				if (effectInstance != null) {
					if (client.player != null) {
						if (effectInstance.getAmplifier() >= 0) {
							client.player.displayClientMessage(Component.literal("Key pressed!"), false);

						} else if (effectInstance.getAmplifier() < 0) {
							client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
						}
					}
				}

			}

			while (PetrificationAbility2.consumeClick()) {
				MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

				if (effectInstance != null) {
					if (client.player != null) {
						if (effectInstance.getAmplifier() >= 1) {
							client.player.displayClientMessage(Component.literal("Key pressed!"), false);

						} else if (effectInstance.getAmplifier() < 1) {
							client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
						}
					}
				}

			}

			while (PetrificationAbility3.consumeClick()) {
				MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

				if (effectInstance != null) {
					if (client.player != null) {
						if (effectInstance.getAmplifier() >= 1) {
							client.player.displayClientMessage(Component.literal("Key pressed!"), false);

						} else if (effectInstance.getAmplifier() < 1) {
							client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
						}
					}
				}

			}
		});
	}
}