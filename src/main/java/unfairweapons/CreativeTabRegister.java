package unfairweapons;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static unfairweapons.ItemsRegister.*;
import static unfairweapons.UnfairWeapons.LOGGER;

public class CreativeTabRegister {
    public static final ResourceKey<CreativeModeTab> UNFAIR_ITEMS = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath("bliss", "bliss_items")
    );

    public static void registerItemGroups() {
        // Register the tab
        LOGGER.info("Implementing the unfair item tab");
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                UNFAIR_ITEMS,
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.unfairitems.unfair_items"))
                        .icon(() -> new ItemStack(CLAYMORE))
                        .displayItems((parameters, output) -> {
                            output.accept(CLAYMORE);
                            output.accept(GUN);
                            output.accept(CUTLASS);
                            output.accept(SUPERCONDUCTOR_HELMET);
                            output.accept(SUPERCONDUCTOR_CHESTPLATE);
                            output.accept(SUPERCONDUCTOR_LEGGINGS);
                            output.accept(SUPERCONDUCTOR_BOOTS);
                        })
                        .build()

        );
    }
}
