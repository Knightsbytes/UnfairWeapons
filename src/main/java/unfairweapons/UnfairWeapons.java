package unfairweapons;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

			);



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		registerItems();
		registerItemGroups();
	}
}