package alexthw.ars_elemental.common.entity.ai;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.api.item.ISchoolProvider;
import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static alexthw.ars_elemental.util.ParticleUtil.schoolToColor;

public class SelfCastGoal<T extends EntityMageBase> extends CastGoal<T> {

    int index;
    Spell spell;

    public SelfCastGoal(T entity, int cooldown, int index, Supplier<Boolean> canUse, int animId, int delayTicks) {
        super(entity, 1.0, cooldown, 100, canUse, animId, delayTicks);
        this.index = index;
    }

    @Override
    public void tick() {
        super.tick();
        if (spell == null) spell = mob.sSpells.get(index);

        ParticleColor color = schoolToColor(mob.school.getId());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(mob.level, this.spell, this.mob, new LivingCaster(this.mob)).withColors(color)) {
            @Override
            public boolean hasFocus(Item stack) {
                return stack instanceof ISchoolFocus focus && mob instanceof ISchoolProvider provider && focus.getSchool() == provider.getSchool();
            }
        };
        resolver.onCast(ItemStack.EMPTY, mob.level);
        mob.castCooldown = 40;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse.get();
    }

}
