package alexthw.ars_elemental.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectNullify extends ElementalAbstractEffect {

    public static final EffectNullify INSTANCE = new EffectNullify();

    public EffectNullify() {
        super("nullify_defense", "Nullify Defense");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity entity) {
            entity.invulnerableTime = 0;
        }
    }

    @Override
    protected int getDefaultManaCost() {
        return 1000;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return Set.of(SpellSchools.NECROMANCY);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        ENABLED = builder.comment("Is Enabled?").define("enabled", false);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
}
