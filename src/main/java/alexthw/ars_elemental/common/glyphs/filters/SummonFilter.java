package alexthw.ars_elemental.common.glyphs.filters;

import alexthw.ars_elemental.common.glyphs.ElementalAbstractFilter;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SummonFilter extends ElementalAbstractFilter {

    public static ElementalAbstractFilter INSTANCE = new SummonFilter("summon", "Summon");
    public static ElementalAbstractFilter NOT_INSTANCE = new SummonFilter("not_summon", "Not Summon").inverted();

    public SummonFilter(String name, String description) {
        super(name, description);
    }

    @Override
    public String getBookDescription() {
        return "Stops the spell from resolving " + (inverted ? "unless " : "if ") + "target a summoned creature";
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return false;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return target.getEntity() instanceof ISummon;
    }

}
