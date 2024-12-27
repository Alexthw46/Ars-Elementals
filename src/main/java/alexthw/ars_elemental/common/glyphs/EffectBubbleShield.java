package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectBubbleShield extends ElementalAbstractEffect implements IPotionEffect {

    public static EffectBubbleShield INSTANCE = new EffectBubbleShield();

    public EffectBubbleShield() {
        super("bubble_shield", "Bubble Shield");
    }

    @Override
    public String getBookDescription() {
        return "Creates a protective coating that consumes mana to reduce incoming damage. This shielding bubble might also prevent debuff to affect the entity and will dissipate if the mana exhausts while defending. The effect will also shield from magic fire.";
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (rayTraceResult.getEntity() instanceof LivingEntity livingEntity) {
            if (CapabilityRegistry.getMana(livingEntity) != null) {
                this.applyConfigPotion(livingEntity, ModPotions.MANA_BUBBLE, spellStats);
            }
        }

    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 400;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
        addGenericInt(builder, 350, "Set how much mana is depleted every time a damage is mitigated by the Mana Bubble.", "absorption_cost");
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getSummonAugments();
    } //just time boosters


    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }

}
