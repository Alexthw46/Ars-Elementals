package alexthw.ars_elemental.common.items;

import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;

public interface ISchoolBangle extends ISpellModifierItem {

    static @Nullable SpellSchool hasBangle(Level world, Entity entity) {
        if (!world.isClientSide && entity instanceof Player player) {
            SlotResult curio = CuriosApi.getCuriosHelper().findFirstCurio(player, c -> (c.getItem() instanceof ISchoolBangle)).orElse(null);
            if (curio != null && curio.stack().getItem() instanceof ISchoolBangle bangle) {
                return bangle.getSchool();
            }
            for (InteractionHand curHand : InteractionHand.values()) {
                Item hand = player.getItemInHand(curHand).getItem();
                if (hand instanceof ISchoolBangle bangle) {
                    return bangle.getSchool();
                }
            }
        }
        return null;
    }

    SpellSchool getSchool();

    default SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {

        if (getSchool().isPartOfSchool(spellPart)) {
            builder.addDamageModifier(1.0D);
        }

        return applyModifiers(builder, spellPart, rayTraceResult, world, shooter, spellContext);
    }

}
