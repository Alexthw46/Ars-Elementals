package alexthw.ars_elemental.api.item;

import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.CompatUtils;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;

public interface ISchoolFocus extends ISpellModifierItem, ISchoolProvider {
    static @Nullable SpellSchool hasFocus(Entity entity) {
        if (entity instanceof ISchoolProvider mage) return mage.getSchool();
        else if (entity instanceof Player player) {
            var focus = getFocus(player);
            if (focus != null) return focus.getSchool();
        }
        return null;
    }

    static ISchoolFocus getFocus(@NotNull Player player) {
        //check the player's hands and curios for a focus and return the school if found
        for (InteractionHand curHand : InteractionHand.values()) {
            Item hand = player.getItemInHand(curHand).getItem();
            if (hand instanceof ISchoolFocus focus) {
                return focus;
            }
        }
        SlotResult curio = CompatUtils.getCurio(player, c -> (c.getItem() instanceof ISchoolFocus));
        if (!curio.stack().isEmpty() && curio.stack().getItem() instanceof ISchoolFocus focus) {
            return focus;
        }
        return null;
    }

    double getDiscount();

    static boolean fireCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.FIRE_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_FIRE_FOCUS.get());
    }

    static boolean waterCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.WATER_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_WATER_FOCUS.get());
    }

    static boolean earthCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.EARTH_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_EARTH_FOCUS.get());
    }

    static boolean airCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.AIR_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_AIR_FOCUS.get());
    }


}
