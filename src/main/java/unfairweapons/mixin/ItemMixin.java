package unfairweapons.mixin;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Mixin(Items.class)
public class ItemMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyElytra(CallbackInfo ci) {
        DataComponentMap newComponents = DataComponentMap.builder()
                .addAll(Items.ELYTRA.components())
                .set(DataComponents.GLIDER, null)
                .build();

        ((ItemComponentAccessor) Items.ELYTRA).setComponents(newComponents);
    }
}

