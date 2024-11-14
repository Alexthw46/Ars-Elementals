package alexthw.ars_elemental.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.Predicate;

public class CompatUtils {
    static boolean botania = false;

    public static boolean isBotaniaLoaded() {
        return botania;
    }


    public static void checkCompats() {

        ModList modList = ModList.get();

        botania = modList.isLoaded("botania");

    }

    public static SlotResult getCurio(LivingEntity player, Predicate<ItemStack> predicate) {
        var lazy = CuriosApi.getCuriosInventory(player);
        SlotResult noResult = new SlotResult(null, ItemStack.EMPTY);
        if (lazy.isPresent()) {
            var curioInv = lazy.get();
                return curioInv.findFirstCurio(predicate).orElse(noResult);
        }
        return noResult;
    }

    public static List<SlotResult> getCurios(LivingEntity player, Predicate<ItemStack> predicate) {
        var lazy = CuriosApi.getCuriosInventory(player);
        List<SlotResult> noResult = NonNullList.withSize(2, new SlotResult(null, ItemStack.EMPTY));
        if (lazy.isPresent()) {
            var curioInv = lazy.get();
            return curioInv.findCurios(predicate);
        }
        return noResult;
    }


}
