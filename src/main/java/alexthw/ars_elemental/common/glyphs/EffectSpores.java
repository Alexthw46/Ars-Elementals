package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.registry.ModAdvTriggers;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.ELEMENTAL_EARTH;

public class EffectSpores extends ElementalAbstractEffect implements IDamageEffect, IPotionEffect {

    public static EffectSpores INSTANCE = new EffectSpores();

    public EffectSpores() {
        super("poison_spores", "Spores");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level level, @Nonnull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity && level instanceof ServerLevel world))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);

        //calculate damage and potion effect duration
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        double range = 3 + spellStats.getAoeMultiplier();
        int effectSec = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        MobEffectInstance venom = livingEntity.getEffect(ModPotions.VENOM);
        if (venom != null) {
            damage += 2 + 3 * venom.getAmplifier();
        }

        if (!canDamage(shooter, spellStats, spellContext, resolver, livingEntity)) return;

        damage(vec, world, shooter, spellStats, damage, effectSec, livingEntity, spellContext, resolver);

        //Damage all nearby entities
        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.position().add(range, range, range), livingEntity.position().subtract(range, range, range)))) {
            if (e.equals(livingEntity) || e.equals(shooter))
                continue;
            //if the entity is not dead and is affected by poison or hunger, damage it, otherwise apply poison
            if (canDamage(e, spellStats, spellContext, resolver, e)) {
                vec = e.position();
                damage(vec, world, shooter, spellStats, damage, effectSec, e, spellContext, resolver);
            } else {
                e.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * effectSec, (int) spellStats.getAmpMultiplier()));
            }
        }
    }

    @Override
    public boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, @NotNull Entity entity) {
        return entity instanceof LivingEntity living && !(living.getHealth() <= 0) && (living.hasEffect(MobEffects.POISON) || living.hasEffect(MobEffects.HUNGER) || living.hasEffect(ModPotions.VENOM));
    }

    public void damage(Vec3 vec, ServerLevel world, @Nonnull LivingEntity shooter, SpellStats stats, float damage, int snareTime, LivingEntity livingEntity, SpellContext spellContext, SpellResolver resolver) {
        if (livingEntity.getType().is(EntityTypeTags.UNDEAD))
            damage *= 1.25F;
        attemptDamage(world, shooter, stats, spellContext, resolver, livingEntity, buildDamageSource(world, shooter), damage);
        world.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, vec.x, vec.y + 0.5, vec.z, 50,
                ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.5);
        //if the entity is dead, spawn a ground blossom on the ground below it
        if (livingEntity.isDeadOrDying() && world.getRandom().nextInt(100) < 5 && ISchoolFocus.earthCheck(resolver)) {
            BlockPos feet = livingEntity.getOnPos();
            BlockState underfoot = world.getBlockState(feet);
            if ((underfoot.getBlock() == Blocks.MOSS_BLOCK || underfoot.is(BlockTags.DIRT) || underfoot.is(BlockTags.LEAVES)) && world.getBlockState(feet.above()).isAir()) {
                world.setBlockAndUpdate(feet.above(), ModItems.GROUND_BLOSSOM.get().defaultBlockState());
                if (shooter instanceof ServerPlayer serverPlayer && !(serverPlayer instanceof FakePlayer))
                    ModAdvTriggers.BLOSSOM.get().trigger(serverPlayer);
            }

        } else
            livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * snareTime)); //otherwise apply hunger

    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        shooter = !(shooter instanceof Player) ? ANFakePlayer.getPlayer((ServerLevel) world) : shooter;
        return DamageUtil.source(world, ModRegistry.POISON, shooter);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 2.5);
        addPotionConfig(builder, 10);
        addExtendTimeConfig(builder, 3);

    }


    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE, AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(ELEMENTAL_EARTH);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the range of the poison spores released.");
    }
}
