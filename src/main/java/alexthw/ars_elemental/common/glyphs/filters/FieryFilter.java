package alexthw.ars_elemental.common.glyphs.filters;

import alexthw.ars_elemental.common.glyphs.ElementalAbstractFilter;
import alexthw.ars_elemental.registry.ModRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FieryFilter extends ElementalAbstractFilter {

    public static ElementalAbstractFilter INSTANCE = new FieryFilter("fiery", "Fiery");
    public static ElementalAbstractFilter NOT_INSTANCE = new FieryFilter("not_fiery", "Not Fiery").inverted();

    FieryFilter(String name, String description) {
        super(name, description);
    }

    @Override
    public String getBookDescription() {
        return "Stops the spell from resolving " + (inverted ? "unless " : "if ") + "target a fire immune or fiery creature";
    }
    /**
     * Whether the filter should allow the block hit
     *
     * @param target BlockHitResult
     */
    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return false;
    }

    /**
     * Whether the filter should allow the entity hit
     *
     * @param target EntityHitResult
     */
    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return target.getEntity() instanceof LivingEntity living && (living.getType().is(ModRegistry.FIERY) || living.fireImmune());
    }

}
