package unfairweapons;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import unfairweapons.entity.DeathLaser;
import unfairweapons.entity.PetrifyingEye;
import unfairweapons.networking.ApplyPetrification3Packet;
import unfairweapons.networking.PetrifiedAbility2Packet;
import unfairweapons.networking.SpawnPetrifiedSludgePacket;
import unfairweapons.networking.SummonPetrifyingEyePacket;

import java.util.HashMap;
import java.util.UUID;

import static unfairweapons.UnfairWeapons.MOD_ID;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

public class UnfairWeaponsClient implements ClientModInitializer {

    private static final Identifier PETRIFICATION_COOLDOWNS_BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_image_background.png");
    private static final Identifier PETRIFICATION_GUI_NEEDLE_FULL = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_gui_needle_full.png");
    private static final Identifier PETRIFICATION_GUI_NEEDLE_EMPTY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_gui_needle_empty.png");

    private static final Identifier PETRIFICATION_GUI_LEVEL_2_BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/advanced_petrification_gui_background.png");
    private static final Identifier PETRIFICATION_GUI_SELECTION_UP = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_selection_up.png");
    private static final Identifier PETRIFICATION_GUI_SELECTION_DOWN = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_selection_down.png");
    private static final Identifier PETRIFICATION_GUI_SELECTION_LEFT = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_selection_left.png");
    private static final Identifier PETRIFICATION_GUI_SELECTION_RIGHT = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_selection_right.png");
    private static final Identifier PETRIFICATION_GUI_EYE_ABILITY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_0.png");
    private static final Identifier PETRIFICATION_GUI_HEART_ABILITY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_1.png");
    private static final Identifier PETRIFICATION_GUI_TENTACLE_ABILITY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_2.png");
    private static final Identifier PETRIFICATION_GUI_LASER_ABILITY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_3.png");
    private static final Identifier PETRIFICATION_GUI_BROKEN_ABILITY = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/petrification_abilities/ability_4.png");


	KeyMapping.Category ELDRITCH_ABILITIES = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MOD_ID, "eldritch_abilities"));

	//public final KeyMapping keyDebugCrash = new KeyMapping("key.debug.crash", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);

	public final KeyMapping PetrificationAbility1 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch ability 1", GLFW.GLFW_KEY_UP, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility2 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch ability 2", GLFW.GLFW_KEY_RIGHT, ELDRITCH_ABILITIES));
	public final KeyMapping PetrificationAbility3 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch ability 3", GLFW.GLFW_KEY_DOWN, ELDRITCH_ABILITIES));
    public final KeyMapping PetrificationAbility4 = KeyBindingHelper.registerKeyBinding(new KeyMapping("eldritch ability 4", GLFW.GLFW_KEY_LEFT, ELDRITCH_ABILITIES));

    private static final HashMap<UUID, Long> cooldowns = new HashMap<>();

    private static long PetrificationCooldown1;
    private static long PetrificationCooldown2;
    private static long PetrificationCooldown3;
    private static long PetrificationCooldown4;
    private static long PetrificationCooldown5;

    private static long PetrificationAbility3Duration;

	@Override
	public void onInitializeClient() {

        EntityRendererRegistry.register(
                UnfairWeapons.PETRIFYING_EYE_ENTITY,
                PetrifyingEyeRenderer::new
        );

        EntityRendererRegistry.register(
                UnfairWeapons.DEATH_LASER,
                DeathLaserRenderer::new
        );

        final HashMap<String, Long> cooldowns = new HashMap<>();
        final int ABILITY_1_COOLDOWN = 200;
        final int ABILITY_2_COOLDOWN = 200;
        final int ABILITY_3_COOLDOWN = 300;

        final int ABILITY_4_COOLDOWN = 300;
        final int ABILITY_5_COOLDOWN = 300;

        final HashMap<String, Long> eldritchKeyCombo = new HashMap<>();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;

            long currentTick = client.level.getGameTime();
            UUID playerId = client.player.getUUID();

            String cooldownKey1 = playerId + "_ability1";
            String cooldownKey2 = playerId + "_ability2";
            String cooldownKey3 = playerId + "_ability3";
            String cooldownKey4 = playerId + "_ability4";
            String cooldownKey5 = playerId + "_ability5";

            if (cooldowns.getOrDefault(cooldownKey1, 0L) > currentTick) {
                PetrificationCooldown1 = cooldowns.get(cooldownKey1) - currentTick;
            }else {
                PetrificationCooldown1 = 0;
            }

            if (cooldowns.getOrDefault(cooldownKey2, 0L) > currentTick) {
                PetrificationCooldown2 = cooldowns.get(cooldownKey2) - currentTick;
            }else {
                PetrificationCooldown2 = 0;
            }

            if (cooldowns.getOrDefault(cooldownKey3, 0L) > currentTick) {
                PetrificationCooldown3 = cooldowns.get(cooldownKey3) - currentTick;
            }else {
                PetrificationCooldown3 = 0;
            }

            if (cooldowns.getOrDefault(cooldownKey4, 0L) > currentTick) {
                PetrificationCooldown4 = cooldowns.get(cooldownKey4) - currentTick;
            }else {
                PetrificationCooldown4 = 0;
            }

            if (cooldowns.getOrDefault(cooldownKey5, 0L) > currentTick) {
                PetrificationCooldown5 = cooldowns.get(cooldownKey5) - currentTick;
            }else {
                PetrificationCooldown5 = 0;
            }

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

                        ClientPlayNetworking.send(new SummonPetrifyingEyePacket());
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

                        ClientPlayNetworking.send(new PetrifiedAbility2Packet());

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

                        if (cooldowns.getOrDefault(playerId + "_ability3_triggered", 0L) == 0) {
                            cooldowns.put(playerId + "_ability3_triggered", 1L);
                        }

                        else {
                            cooldowns.put(playerId + "_ability3_triggered", 0L);
                        }

                    } else {
                        client.player.displayClientMessage(Component.literal("You don't have the needed effect to use this!"), false);
                    }
                }
            }

            while (PetrificationAbility4.consumeClick()) {
                MobEffectInstance effectInstance = client.player.getEffect(PETRIFICATION_EFFECT);

                if (effectInstance != null) {
                    if (effectInstance.getAmplifier() == 1) {
                        ClientPlayNetworking.send(new ApplyPetrification3Packet());
                    }
                }
            }
            String durationKey = playerId + "_ability3_triggered";
            if (cooldowns.getOrDefault(durationKey, 0L) == 1) {
                ClientPlayNetworking.send(new SpawnPetrifiedSludgePacket(client.player.getOnPos()));
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

        int width2 = 96;
        int height2 = 192;

        Font font = mc.font;

        if (mc.player.hasEffect(PETRIFICATION_EFFECT)){
            //context.fill(x, y, x + width, y + height, 0xFFFF0000);
            MobEffectInstance effectInstance = mc.player.getEffect(PETRIFICATION_EFFECT);
            int effectInstanceAmplification = effectInstance.getAmplifier();

            if (effectInstanceAmplification >= 1 && effectInstanceAmplification < 3) {


                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_COOLDOWNS_BACKGROUND,
                        x, y, 0, 0,
                        width, height,
                        width, height
                );

                if (effectInstanceAmplification == 1) {

                    context.blit(
                            RenderPipelines.GUI_TEXTURED,
                            PETRIFICATION_GUI_NEEDLE_FULL,
                            x + 105, y + 8, 0, 0,
                            13, 16,
                            13, 16
                    );
                } else {
                    context.blit(
                            RenderPipelines.GUI_TEXTURED,
                            PETRIFICATION_GUI_NEEDLE_EMPTY,
                            x + 105, y + 8, 0, 0,
                            13, 16,
                            13, 16
                    );
                }

                context.drawString(font, String.format("%.1f", PetrificationCooldown1 / 20.0), x + 20, y + 14, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown2 / 20.0), x + 54, y + 14, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown3 / 20.0), x + 87, y + 14, 0xFFFFFFFF, true);
            }
            else if (effectInstanceAmplification >= 3){

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_LEVEL_2_BACKGROUND,
                        x, y, 0, 0,
                        width2, height2,
                        width2, height2
                );

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_EYE_ABILITY,
                        x + 4, y + 43, 0, 0,
                        25, 25,
                        25, 25
                );

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_HEART_ABILITY,
                        x + 4, y + 71, 0, 0,
                        25, 25,
                        25, 25
                );

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_TENTACLE_ABILITY,
                        x + 4, y + 99, 0, 0,
                        25, 25,
                        25, 25
                );

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_LASER_ABILITY,
                        x + 4, y + 127, 0, 0,
                        25, 25,
                        25, 25
                );

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFICATION_GUI_BROKEN_ABILITY,
                        x + 4, y + 155, 0, 0,
                        25, 25,
                        25, 25
                );

                context.drawString(font, String.format("%.1f", PetrificationCooldown1 / 20.0), x + 30, y + 47, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown2 / 20.0), x + 30, y + 75, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown3 / 20.0), x + 30, y + 103, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown4 / 20.0), x + 30, y + 131, 0xFFFFFFFF, true);
                context.drawString(font, String.format("%.1f", PetrificationCooldown5 / 20.0), x + 30, y + 159, 0xFFFFFFFF, true);
            }
        }

    }
}

class PetrifyingEyeRenderer
        extends EntityRenderer<PetrifyingEye, EntityRenderState> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/entity/pig/pig.png");

    public PetrifyingEyeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void extractRenderState(PetrifyingEye entity,
                                   EntityRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }


}

class DeathLaserRenderer extends EntityRenderer<DeathLaser, EntityRenderState> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/entity/pig/pig.png");

    public DeathLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void extractRenderState(DeathLaser entity,
                                   EntityRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }

    public Identifier getTextureLocation(EntityRenderState state) {
        return TEXTURE;
    }


}