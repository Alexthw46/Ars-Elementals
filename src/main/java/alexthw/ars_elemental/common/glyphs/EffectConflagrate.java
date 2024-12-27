package alexthw.ars_elemental.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectConflagrate extends ElementalAbstractEffect implements IDamageEffect {

    public static EffectConflagrate INSTANCE = new EffectConflagrate();

    public EffectConflagrate() {
        super("conflagrate", "Conflagrate");
    }

    @Override
    public String getBookDescription() {
        return "When it hits a target on fire, it causes a detonation that deals damage to all entities in range. That explosion might spread explosive powder on them, causing them to explode after a while.";
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level level, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity) || !(level instanceof ServerLevel world))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        double range = 3 + spellStats.getAoeMultiplier();
        DamageSource source = buildDamageSource(world, shooter);
        if (livingEntity.isOnFire() && attemptDamage(world, shooter, spellStats, spellContext, resolver, livingEntity, source, damage)) {
            world.sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5, vec.z, 50,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
            world.sendParticles(ParticleTypes.EXPLOSION, vec.x, vec.y + 0.5, vec.z, 50,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
            for (Entity e : world.getEntities(shooter, new AABB(
                    livingEntity.position().add(range, range, range), livingEntity.position().subtract(range, range, range)))) {
                if (e.equals(livingEntity) || !(e instanceof LivingEntity liv))
                    continue;
                if (attemptDamage(world, shooter, spellStats, spellContext, resolver, e, source, damage * 0.75F)) {
                    if (e.getRandom().nextDouble() > 0.9)
                        liv.addEffect(new MobEffectInstance(ModPotions.BLAST_EFFECT, 200, 0));
                    vec = e.position();
                    world.sendParticles(ParticleTypes.FLAME, vec.x, vec.y + 0.5, vec.z, 50,
                            ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);
                }
            }
        }
    }


    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.FLARE, shooter);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 9.0);
        addAmpConfig(builder, 3.0);
    }

    @Override
    public int getDefaultManaCost() {
        return 80;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the range of the conflagration.");
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
}