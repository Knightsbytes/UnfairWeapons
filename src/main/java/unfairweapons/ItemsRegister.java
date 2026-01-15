package unfairweapons;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import static net.minecraft.world.effect.MobEffects.INSTANT_HEALTH;
import static net.minecraft.world.item.Items.registerBlock;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static net.minecraft.world.item.Items.registerItem;
import static unfairweapons.UnfairWeapons.PETRIFICATION_EFFECT;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import unfairweapons.armour.SuperconductorArmourMaterial;
import unfairweapons.items.ClaymoreItem;
import unfairweapons.items.GunItem;
import unfairweapons.items.CutlassItem;
import unfairweapons.items.PetrificationNeedleItem;
import unfairweapons.items.block.TentacleBlock;

import java.util.List;
import java.util.function.Function;

public class ItemsRegister {
    public static void registerItems() {
    }

    private static ResourceKey<Item> modItemId(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static final DeathProtection TOTEM_OF_PETRIFICATION_TOTEM = new DeathProtection(
            List.of(
                    new ApplyStatusEffectsConsumeEffect(
                            List.of(
                                    new MobEffectInstance(PETRIFICATION_EFFECT, 90000, 0),
                                    new MobEffectInstance(INSTANT_HEALTH, 1, 100)
                            )
                    )
            )
    );

    //Create Armour resource key
    private final static ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(MOD_ID,"equipment_asset"));
    public static ResourceKey<EquipmentAsset> createId(String string) {
        return ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(MOD_ID, string));
    }

    public static final ToolMaterial CLAYMORE_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 8.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
    public static final ToolMaterial CYBER_CUTLASS_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 6.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);


    public static final Item CLAYMORE = registerItem(
            modItemId("claymore"),
            props -> new ClaymoreItem(props, 2.0F, true, true),
            new Item.Properties()
                    .sword(CLAYMORE_MATERIAL, 1.0f, -3F)
                    .fireResistant()
                    .stacksTo(1)
    );



    public static final Block ELDRITCH_TENTACLE_BLOCK = register("eldritch_tentacle_block", TentacleBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
    public static final Item ELDRITCH_TENTACLE_BLOCK_ITEM = registerBlock(ELDRITCH_TENTACLE_BLOCK);

    public static final Item GUN = registerItem(modItemId("gun"), GunItem::new, new Item.Properties().stacksTo(1));
    public static final Item BULLET = registerItem(modItemId("bullet"), Item::new, new Item.Properties());
    public static final Item CUTLASS = registerItem(modItemId("cutlass"), CutlassItem::new, new Item.Properties().stacksTo(1).sword(CYBER_CUTLASS_MATERIAL, 1.0F, -2.4F));

    public static final Item SUPERCONDUCTOR_HELMET = registerItem(
            modItemId("superconductor_helmet"),
            Item::new,
            new Item.Properties().humanoidArmor(SuperconductorArmourMaterial.INSTANCE, ArmorType.HELMET)
                    .durability(ArmorType.HELMET.getDurability(SuperconductorArmourMaterial.BASE_DURABILITY))
    );
    public static final Item SUPERCONDUCTOR_CHESTPLATE = registerItem(
            modItemId("superconductor_chestplate"),
            Item::new,
            new Item.Properties().humanoidArmor(SuperconductorArmourMaterial.INSTANCE, ArmorType.CHESTPLATE)
                    .durability(ArmorType.CHESTPLATE.getDurability(SuperconductorArmourMaterial.BASE_DURABILITY))
    );

    public static final Item SUPERCONDUCTOR_LEGGINGS = registerItem(
            modItemId("superconductor_leggings"),
            Item::new,
            new Item.Properties().humanoidArmor(SuperconductorArmourMaterial.INSTANCE, ArmorType.LEGGINGS)
                    .durability(ArmorType.LEGGINGS.getDurability(SuperconductorArmourMaterial.BASE_DURABILITY))
    );

    public static final Item SUPERCONDUCTOR_BOOTS = registerItem(
            modItemId("superconductor_boots"),
            Item::new,
            new Item.Properties().humanoidArmor(SuperconductorArmourMaterial.INSTANCE, ArmorType.BOOTS)
                    .durability(ArmorType.BOOTS.getDurability(SuperconductorArmourMaterial.BASE_DURABILITY))
    );

    public static final Item TOTEM_OF_PETRIFICATION = registerItem(
            modItemId("totem_of_petrification"),
            Item::new,
            new Item.Properties().component(
                    DataComponents.DEATH_PROTECTION, TOTEM_OF_PETRIFICATION_TOTEM
            )
                    .stacksTo(1)
    );


    public static final Item PETRIFICATION_VIAL = registerItem(
            modItemId("petrified_vial"),
            PetrificationNeedleItem::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static Block register(ResourceKey<Block> resourceKey, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        Block block = (Block)function.apply(properties.setId(resourceKey));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    private static Block register(String string, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return register(blockId(string), function, properties);
    }

    private static Block register(String string, BlockBehaviour.Properties properties) {
        return register(string, Block::new, properties);
    }

    private static ResourceKey<Block> blockId(String string) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, string));
    }
}
