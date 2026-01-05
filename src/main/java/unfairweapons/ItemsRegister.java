package unfairweapons;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import static net.minecraft.world.item.Items.ACACIA_BOAT;
import static net.minecraft.world.item.equipment.ArmorMaterials.makeDefense;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static net.minecraft.world.item.Items.registerItem;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import unfairweapons.items.ClaymoreItem;
import unfairweapons.items.ExplosiveItem;
import unfairweapons.items.GunItem;
import unfairweapons.items.CutlassItem;

import java.util.List;

public class ItemsRegister {
    public static void registerItems() {
    }

    private static ResourceKey<Item> modItemId(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    public static final ToolMaterial CLAYMORE_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 8.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
    public static final ToolMaterial CYBER_CUTLASS_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 6.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);

    public static ArmorMaterial SUPERCONDUCTOR = new ArmorMaterial(
            37, makeDefense(3, 6, 8, 3, 19), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssets.NETHERITE
    );

    public static final Item CLAYMORE = registerItem(
            modItemId("claymore"),
            props -> new ClaymoreItem(props, 2.0F, true, true),
            new Item.Properties()
                    .sword(CLAYMORE_MATERIAL, 1.0f, -3F)
                    .fireResistant()
                    .stacksTo(1)
    );

    public static final Item GUN = registerItem(modItemId("gun"), GunItem::new, new Item.Properties().stacksTo(1));
    public static final Item BULLET = registerItem(modItemId("bullet"), Item::new, new Item.Properties());
    public static final Item CUTLASS = registerItem(modItemId("cutlass"), CutlassItem::new, new Item.Properties().stacksTo(1).sword(CYBER_CUTLASS_MATERIAL, 1.0F, -2.4F));

    public static final Item SUPERCONDUCTOR_HELMET = registerItem(
            modItemId("superconductor_helmet"), Item::new, new Item.Properties().humanoidArmor(SUPERCONDUCTOR, ArmorType.HELMET).fireResistant()
    );
    public static final Item SUPERCONDUCTOR_CHESTPLATE = registerItem(
            modItemId("superconductor_chestplate"), Item::new, new Item.Properties().humanoidArmor(SUPERCONDUCTOR, ArmorType.CHESTPLATE).fireResistant()
    );
    public static final Item SUPERCONDUCTOR_LEGGINGS = registerItem(
            modItemId("superconductor_leggings"), Item::new, new Item.Properties().humanoidArmor(SUPERCONDUCTOR, ArmorType.LEGGINGS).fireResistant()
    );
    public static final Item SUPERCONDUCTOR_BOOTS = registerItem(
            modItemId("superconductor_boots"), Item::new, new Item.Properties().humanoidArmor(SUPERCONDUCTOR, ArmorType.BOOTS).fireResistant()
    );
}
