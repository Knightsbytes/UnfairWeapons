package unfairweapons;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import static net.minecraft.world.item.Items.ACACIA_BOAT;
import static unfairweapons.UnfairWeapons.MOD_ID;
import static net.minecraft.world.item.Items.registerItem;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
}
