package alexthw.ars_elemental.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.AbstractFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import static alexthw.ars_elemental.ArsElemental.prefix;

public abstract class ElementalAbstractFilter extends AbstractFilter {

    protected boolean inverted = false;

    public ElementalAbstractFilter(String name, String description) {
        super(prefix("glyph_" + name + "_filter"), "Filter:" + description);
    }

    @Override
    public Integer getTypeIndex() {
        return 15;
    }

    public ElementalAbstractFilter inverted() {
        this.inverted = !inverted;
        return this;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!shouldAffect(rayTraceResult, world)) spellContext.setCanceled(true);
    }

    @Override
    public boolean shouldAffect(HitResult rayTraceResult, Level level) {
        return inverted != super.shouldAffect(rayTraceResult, level);
    }
}
