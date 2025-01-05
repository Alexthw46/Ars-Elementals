package alexthw.ars_elemental.api.item;

import alexthw.ars_elemental.common.items.foci.ElementalFocus;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.CompatUtils;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

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

    static Set<SpellSchool> getFociSchools(@NotNull Entity caster) {
        SpellSchool mainSchool = hasFocus(caster);
        if (mainSchool != null) {
            Set<SpellSchool> schools = new HashSet<>(Set.of(mainSchool));
            schools.addAll(mainSchool.getSubSchools());
            return schools;
        }
        return Set.of();
    }

    double getDiscount();

    static boolean fireCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.FIRE_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_FIRE_FOCUS.get()) || checkSchool(resolver, SpellSchools.ELEMENTAL_FIRE);
    }

    static boolean waterCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.WATER_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_WATER_FOCUS.get()) || checkSchool(resolver, SpellSchools.ELEMENTAL_WATER);
    }

    static boolean earthCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.EARTH_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_EARTH_FOCUS.get()) || checkSchool(resolver, SpellSchools.ELEMENTAL_EARTH);
    }

    static boolean airCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.AIR_FOCUS.get()) || resolver.hasFocus(ModItems.LESSER_AIR_FOCUS.get()) || checkSchool(resolver, SpellSchools.ELEMENTAL_AIR);
    }

    static boolean checkSchool(SpellResolver resolver, SpellSchool school) {
        LivingEntity entity = resolver.spellContext.getUnwrappedCaster();
        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(entity);
        if (items != null) {
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack stack = items.getStackInSlot(i);

                if (stack.getItem() instanceof ElementalFocus focus) {
                    String wantedId = school.getId();
                    String mainId = focus.getSchool().getId();
                    if (wantedId.equals(mainId)) {
                        return true;
                    }

                    for (SpellSchool sub : focus.getSchool().getSubSchools()) {
                        if (wantedId.equals(sub.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
