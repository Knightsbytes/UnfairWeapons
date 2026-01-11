package unfairweapons;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.UUID;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class UnfairWeaponsClient implements ClientModInitializer {
    private static final Identifier PETRIFICATION_COOLDOWNS_BACKGROUND = Identifier.fromNamespaceAndPath(
            MOD_ID,
            "textures/gui/petrification_image_background.png"
    );

	KeyMapping.Category ELDRITCH_ABILITIES = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "eldritch_abilities"));

	//public final KeyMapping keyDebugCrash = new KeyMapping("key.debug.crash", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);

	public final KeyMapping PetrificationAbility1 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_1", GLFW.GLFW_KEY_V, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility2 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_2", GLFW.GLFW_KEY_B, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility3 = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ability.eldritch_ability_3", GLFW.GLFW_KEY_N, ELDRITCH_ABILITIES));

    private static final HashMap<UUID, Long> cooldowns = new HashMap<>();

	@Override
	public void onInitializeClient() {

        final HashMap<String, Long> cooldowns = new HashMap<>();
        final int ABILITY_1_COOLDOWN = 100; // 5 seconds
        final int ABILITY_2_COOLDOWN = 200; // 10 seconds
        final int ABILITY_3_COOLDOWN = 300; // 15 seconds

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            long currentTick = client.level.getGameTime();
            UUID playerId = client.player.getUUID();

            // Ability 1
            while (PetrificationAbility1.consumeClick()) {
                MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

                if (effectInstance != null) {
                    if (effectInstance.getAmplifier() >= 1) {
                        // Check cooldown
                        String cooldownKey = playerId + "_ability1";
                        if (cooldowns.getOrDefault(cooldownKey, 0L) > currentTick) {
                            long remaining = cooldowns.get(cooldownKey) - currentTick;
                            client.player.displayClientMessage(
                                    Component.literal("Cooldown: " + String.format("%.1f", remaining / 20.0) + "s"),
                                    true
                            );
                            continue;
                        }

                        // Perform ability
                        client.player.displayClientMessage(Component.literal("Ability 1 used!"), false);

                        // Set cooldown
                        cooldowns.put(cooldownKey, currentTick + ABILITY_1_COOLDOWN);

                    } else {
                        client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
                    }
                }
            }

            // Ability 2
            while (PetrificationAbility2.consumeClick()) {
                MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

                if (effectInstance != null) {
                    if (effectInstance.getAmplifier() >= 2) {
                        // Check cooldown
                        String cooldownKey = playerId + "_ability2";
                        if (cooldowns.getOrDefault(cooldownKey, 0L) > currentTick) {
                            long remaining = cooldowns.get(cooldownKey) - currentTick;
                            client.player.displayClientMessage(
                                    Component.literal("Cooldown: " + String.format("%.1f", remaining / 20.0) + "s"),
                                    true
                            );
                            continue;
                        }

                        // Perform ability
                        client.player.displayClientMessage(Component.literal("Ability 2 used!"), false);

                        // Set cooldown
                        cooldowns.put(cooldownKey, currentTick + ABILITY_2_COOLDOWN);

                    } else {
                        client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
                    }
                }
            }

            // Ability 3
            while (PetrificationAbility3.consumeClick()) {
                MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

                if (effectInstance != null) {
                    if (effectInstance.getAmplifier() >= 2) {
                        // Check cooldown
                        String cooldownKey = playerId + "_ability3";
                        if (cooldowns.getOrDefault(cooldownKey, 0L) > currentTick) {
                            long remaining = cooldowns.get(cooldownKey) - currentTick;
                            client.player.displayClientMessage(
                                    Component.literal("Cooldown: " + String.format("%.1f", remaining / 20.0) + "s"),
                                    true
                            );
                            continue;
                        }

                        // Perform ability
                        client.player.displayClientMessage(Component.literal("Ability 3 used!"), false);

                        // Set cooldown
                        cooldowns.put(cooldownKey, currentTick + ABILITY_3_COOLDOWN);

                    } else {
                        client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
                    }
                }
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(MOD_ID, "before_chat"), UnfairWeaponsClient::render);

	}

    private static void render(GuiGraphics context, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Get screen dimensions
        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();

        // Position on screen
        int x = 0;
        int y = 0;

        // Image dimensions
        int width = 128;
        int height = 32;

        Identifier vanillaTest = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/icons.png");
        context.blit(vanillaTest, x, y, 0, 0, 128, 32, 256, 256);


        if (mc.player.hasEffect(PETRIFICATION_EFFECT)){
            //context.fill(x, y, x + width, y + height, 0xFFFF0000);
            MobEffectInstance effectInstance = mc.player.getEffect(PETRIFICATION_EFFECT);
            int effectInstanceAmplification = effectInstance.getAmplifier();

            if (effectInstanceAmplification >= 1){
                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_COOLDOWNS_BACKGROUND,
                        x, y, 0, 0,
                        width, height,
                        width, height
                );
            }

        }

    }
}