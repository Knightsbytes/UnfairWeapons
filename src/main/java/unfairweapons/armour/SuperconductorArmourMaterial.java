package unfairweapons.armour;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

import static unfairweapons.UnfairWeapons.MOD_ID;

public class SuperconductorArmourMaterial {
    public static final int BASE_DURABILITY = 50;

    public static final ResourceKey<EquipmentAsset> SUPERCONDUCTOR_ARMOR_MATERIAL_KEY = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MOD_ID, "superconductor"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 3,
                    ArmorType.CHESTPLATE, 8,
                    ArmorType.LEGGINGS, 6,
                    ArmorType.BOOTS, 3
            ),
            5,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.25F,
            ItemTags.REPAIRS_NETHERITE_ARMOR,
            SUPERCONDUCTOR_ARMOR_MATERIAL_KEY
    );
}
