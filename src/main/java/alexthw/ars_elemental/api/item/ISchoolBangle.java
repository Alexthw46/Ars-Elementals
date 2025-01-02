package alexthw.ars_elemental.api.item;

import alexthw.ars_elemental.util.CompatUtils;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public interface ISchoolBangle extends ISpellModifierItem, ISchoolProvider {

    static boolean hasBangle(Level world, Entity entity, SpellSchool school) {
        if (!world.isClientSide && entity instanceof Player player) {
            for (SlotResult curio : CompatUtils.getCurios(player, c -> (c.getItem() instanceof ISchoolBangle))) {
                if (!curio.stack().isEmpty() && curio.stack().getItem() instanceof ISchoolBangle bangle && bangle.getSchool() == school) {
                    return true;
                }
            }
        }
        return false;
    }

    static Set<SpellSchool> getBangles(Level world, Entity entity) {
        Set<SpellSchool> schools = new HashSet<>();
        if (!world.isClientSide && entity instanceof Player player) {
            for (SlotResult curio : CompatUtils.getCurios(player, c -> (c.getItem() instanceof ISchoolBangle))) {
                if (!curio.stack().isEmpty() && curio.stack().getItem() instanceof ISchoolBangle bangle) {
                    schools.add(bangle.getSchool());
                    schools.addAll(bangle.getSchool().getSubSchools());
                }
            }
        }
        return schools;
    }


    default SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {

        if (getSchool().isPartOfSchool(spellPart)) {
            builder.addDamageModifier(2.0D);
        }

        return applyModifiers(builder, spellPart, rayTraceResult, world, shooter, spellContext);
    }

}
