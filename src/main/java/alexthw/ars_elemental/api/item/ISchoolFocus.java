package alexthw.ars_elemental.api.item;

import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.CompatUtils;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.HashSet;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.*;

public interface ISchoolFocus extends ISpellModifierItem, ISchoolProvider {

    /**
     * Get the first focus in the player's hands or curios
     *
     * @param player the player to check
     * @return the first focus found, or null if none
     */
    static ISchoolFocus getFirstFocus(@NotNull Player player) {
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

    /**
     * Get the schools of the foci in the player's hands and curios
     *
     * @param caster the entity to check
     * @return the schools of all the foci of the entity
     */
    static Set<SpellSchool> getFociSchools(@Nullable LivingEntity caster) {
        if (caster == null) return Set.of();
        if (caster instanceof ISchoolProvider provider) {
            return provider.getSchools();
        }
        Set<SpellSchool> schools = new HashSet<>();
        for (InteractionHand curHand : InteractionHand.values()) {
            Item hand = caster.getItemInHand(curHand).getItem();
            if (hand instanceof ISchoolFocus focus) schools.addAll(focus.getSchools());
        }
        SlotResult curio = CompatUtils.getCurio(caster, c -> (c.getItem() instanceof ISchoolFocus));
        if (!curio.stack().isEmpty() && curio.stack().getItem() instanceof ISchoolFocus focus) {
            schools.addAll(focus.getSchools());
        }
        return schools;
    }

    double getDiscount();

    static boolean fireCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.LESSER_FIRE_FOCUS.get()) || getFociSchools(resolver.spellContext.getUnwrappedCaster()).contains(ELEMENTAL_FIRE);
    }

    static boolean waterCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.LESSER_WATER_FOCUS.get()) || getFociSchools(resolver.spellContext.getUnwrappedCaster()).contains(ELEMENTAL_WATER);
    }

    static boolean earthCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.LESSER_EARTH_FOCUS.get()) || getFociSchools(resolver.spellContext.getUnwrappedCaster()).contains(ELEMENTAL_EARTH);
    }

    static boolean airCheck(SpellResolver resolver) {
        return resolver.hasFocus(ModItems.LESSER_AIR_FOCUS.get()) || getFociSchools(resolver.spellContext.getUnwrappedCaster()).contains(ELEMENTAL_AIR);
    }

}
