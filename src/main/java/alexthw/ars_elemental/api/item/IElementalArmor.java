package alexthw.ars_elemental.api.item;

import alexthw.ars_elemental.common.items.armor.AAMaterials;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.setup.registry.MaterialRegistry;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface IElementalArmor extends ISpellModifierItem {

    Map<SpellSchool, TagKey<DamageType>> damageResistances = new ConcurrentHashMap<>();

    static Holder<ArmorMaterial> schoolToMaterial(SpellSchool element) {
        return switch (element.getId()) {
            case "fire" -> AAMaterials.fire;
            case "air" -> AAMaterials.air;
            case "earth" -> AAMaterials.earth;
            case "water" -> AAMaterials.water;

            default -> MaterialRegistry.MEDIUM;
        };
    }

    @Override
    default SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        if (getSchool().isPartOfSchool(spellPart)) {
            builder.addAmplification(0.5);
        }
        return builder;
    }

    default double getDiscount(List<AbstractSpellPart> recipe) {
        // check if the recipe contains a glyph from the same school as this armor
        double sum = 0;
        for (AbstractSpellPart part : recipe) {
            if (getSchool().isPartOfSchool(part))
                sum += 0.2 * part.getCastingCost();
        }
        return sum;
    }

    SpellSchool getSchool();

    String getTier();

    default boolean doAbsorb(DamageSource damageSource) {
        // check if the damage source is in the list of damage sources that this armor can absorb
        return damageResistances.containsKey(getSchool()) && damageSource.is(damageResistances.get(getSchool()));
    }
    default boolean fillAbsorptions(DamageSource damageSource, HashMap<SpellSchool, Integer> bonusMap) {
        // check if the damage source is in the list of damage sources that this armor can absorb
        // then add the school to the bonus map
        if (doAbsorb(damageSource)) {
            bonusMap.put(getSchool(), bonusMap.getOrDefault(getSchool(), 0) + 1);
            return true;
        }else return false;
    }

}
