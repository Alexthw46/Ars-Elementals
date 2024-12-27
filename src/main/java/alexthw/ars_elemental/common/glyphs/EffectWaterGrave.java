package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.mixin.ZombieInvoker;
import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectWaterGrave extends ElementalAbstractEffect implements IDamageEffect, IPotionEffect {

    public static EffectWaterGrave INSTANCE = new EffectWaterGrave();

    private EffectWaterGrave() {
        super("watery_grave", "Watery Grave");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity living) {

            // If augmented by Extend Time, apply the Water Grave potion effect.
            if (spellStats.hasBuff(AugmentExtendTime.INSTANCE)) {
                this.applyConfigPotion(living, ModPotions.WATER_GRAVE, spellStats);
            } else {
                //Otherwise, make the entity sink.
                Vec3 delta = living.getDeltaMovement();
                double dy = Math.min(-1.0D, delta.y - 0.05D);
                living.setDeltaMovement(delta.x, dy, delta.z);
            }
            // If the entity is a zombie, convert it to a drowned.
            if (living instanceof Zombie zombie && !(living instanceof Drowned)) {
                ((ZombieInvoker) zombie).callStartUnderWaterConversion(20);
                return;
            }
            // If the entity's air supply is depleted, deal damage.
            int airSupply = living.getAirSupply();
            if (airSupply <= 0 || living.getType().is(EntityTypeTags.AQUATIC)) {
                double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
                attemptDamage(world, shooter, spellStats, spellContext, resolver, living, buildDamageSource(world, shooter), (float) damage);
            } else {
                // Otherwise, drain the entity's air supply.
                double newSupply = Math.max(-19, airSupply - 50 * (3 + spellStats.getAmpMultiplier()));
                living.setAirSupply((int) newSupply);
            }
            living.hurtMarked = true;
        }
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return shooter.damageSources().drown();
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
    }


    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    /**
     * Returns the set of augments that this spell part can be enhanced by.
     */
    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentRandomize.INSTANCE);
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return Set.of(SpellSchools.ELEMENTAL_WATER);
    }

    @Override
    public String getBookDescription() {
        return "Dooms a creature to drown. Reduces the target air supply and causes drowning damage when depleted. When augmented with Extend Time, they will be dragged down and unable to swim up for a short period.";
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }

}
