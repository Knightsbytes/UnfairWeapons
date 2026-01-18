package unfairweapons;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unfairweapons.entity.PetrifyingEye;
import unfairweapons.networking.ApplyPetrification3Packet;
import unfairweapons.networking.PetrifiedAbility2Packet;
import unfairweapons.networking.SummonPetrifyingEyePacket;

import java.util.List;

import static unfairweapons.CreativeTabRegister.registerItemGroups;
import static unfairweapons.ItemsRegister.registerItems;

public class UnfairWeapons implements ModInitializer {
	public static final String MOD_ID = "unfair-weapons";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Holder<MobEffect> PETRIFICATION_EFFECT =
			Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, "petrification"), new PetrificationEffect()
					.addAttributeModifier(Attributes.MAX_HEALTH, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.health"), 10.0, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.ARMOR, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.armor"), 20.0, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.safe_fall_distance"), 2000.0, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.strength"), 6.0, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.fromNamespaceAndPath(MOD_ID,"effect.petrification.speed"), 0.4F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
					.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.entity_interaction_range"), 1F, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, Identifier.fromNamespaceAndPath(MOD_ID, "effect.petrification.block_interaction_range"), 1F, AttributeModifier.Operation.ADD_VALUE)
					.addAttributeModifier(Attributes.ATTACK_SPEED, Identifier.fromNamespaceAndPath(MOD_ID,"effect.petrification.haste"), 0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
			);

	public static final Holder<MobEffect> IMMORTALITY_EFFECT =
			Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, "immortality"), new ImmortalityEffect()
					.addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.fromNamespaceAndPath(MOD_ID, "effect.immortality.strength"), 99999999999.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
			);

	public static final EntityType<PetrifyingEye> PETRIFYING_EYE_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			Identifier.fromNamespaceAndPath(MOD_ID, "petrifying_eye"),
			EntityType.Builder.of(PetrifyingEye::new, MobCategory.MISC)
					.sized(1.0f, 2.0f)
					.clientTrackingRange(10)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "petrifying_eye")))
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		PayloadTypeRegistry.playC2S().register(ApplyPetrification3Packet.TYPE, ApplyPetrification3Packet.CODEC);

		// Handle packet on server
		ServerPlayNetworking.registerGlobalReceiver(ApplyPetrification3Packet.TYPE, (packet, context) -> {
			context.server().execute(() -> {
				context.player().addEffect(new MobEffectInstance(
						PETRIFICATION_EFFECT,
						90000,
						2
				));
			});
		});

		PayloadTypeRegistry.playC2S().register(SummonPetrifyingEyePacket.TYPE, SummonPetrifyingEyePacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SummonPetrifyingEyePacket.TYPE, (packet, context) -> {
			ServerPlayer player = context.player();
			ServerLevel level = player.level();
			PetrifyingEye eye = new PetrifyingEye(PETRIFYING_EYE_ENTITY, level);
			eye.setPos(player.position().x, player.position().y, player.position().z);
			level.addFreshEntity(eye);
		});

		PayloadTypeRegistry.playC2S().register(PetrifiedAbility2Packet.TYPE, PetrifiedAbility2Packet.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PetrifiedAbility2Packet.TYPE, (packet, context) -> {
			ServerPlayer player = context.player();
			ServerLevel level = player.level();

			player.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 100, false, false, true));
			player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 6000, 5, false, false, true));

			AABB affectedEntities = new AABB(player.position(), player.position()).inflate(5);
			for (Player victim : level.getEntitiesOfClass(Player.class, affectedEntities)){
				if (victim != player) {
					victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, false, false, true));
					victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, true));
					victim.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 5, false, false, true));
					victim.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 40, 1, false, false, true));
					victim.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 1, false, false, true));
				}
			}
		});

		registerItems();
		registerItemGroups();
	}
}