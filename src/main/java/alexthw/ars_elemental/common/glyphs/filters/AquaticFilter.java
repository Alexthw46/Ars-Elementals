package alexthw.ars_elemental.common.glyphs.filters;

import alexthw.ars_elemental.common.glyphs.ElementalAbstractFilter;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class AquaticFilter extends ElementalAbstractFilter {

    public static ElementalAbstractFilter INSTANCE = new AquaticFilter("aquatic", "Aquatic");
    public static ElementalAbstractFilter NOT_INSTANCE = new AquaticFilter("not_aquatic", "Not Aquatic").inverted();

    AquaticFilter(String name, String description) {
        super(name, description);
    }

    @Override
    public String getBookDescription() {
        return "Stops the spell from resolving " + (inverted ? "unless " : "if ") + "target an aquatic creature";
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
        if (target.getEntity() instanceof LivingEntity living) {
            return living.getType().is(EntityTypeTags.AQUATIC);
        }
        return false;
    }

}
