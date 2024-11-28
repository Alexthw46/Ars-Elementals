package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.network.DischargeEffectPacket;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import static alexthw.ars_elemental.registry.ModPotions.LIGHTNING_LURE;

public class EffectDischarge extends ElementalAbstractEffect implements IDamageEffect, IPotionEffect {

    public static EffectDischarge INSTANCE = new EffectDischarge();

    public EffectDischarge() {
        super("discharge", "Discharge");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity) || !(world instanceof ServerLevel))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        double range = 3 + spellStats.getAoeMultiplier();
        DamageSource source = buildDamageSource(world, shooter);

        // If the target is shocked, damage all nearby entities and apply the shock effect to them
        if (livingEntity.hasEffect(ModPotions.SHOCKED_EFFECT) || livingEntity.hasEffect(LIGHTNING_LURE)) {
            // If the target is static charged, damage is increased by 30% and the effect is removed
            if (livingEntity.hasEffect(LIGHTNING_LURE)) {
                damage *= 1.3F;
                livingEntity.removeEffect(LIGHTNING_LURE);
            }
            // If the target wear energy armors, damage is increased by 10% for each armor piece and energy is drained
            for (ItemStack i : livingEntity.getArmorSlots()) {
                IEnergyStorage energyStorage = i.getCapability(Capabilities.EnergyStorage.ITEM);
                if (energyStorage != null) {
                    energyStorage.extractEnergy((int) (energyStorage.getEnergyStored() * 0.25), false);
                    damage *= 1.1F;
                }
            }
            // Damage all nearby entities and apply the shock effect to them
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.blockPosition()).inflate(range), (e) -> !e.equals(shooter))) {
                attemptDamage(world, shooter, spellStats, spellContext, resolver, entity, source, damage);
                this.applyConfigPotion(entity, ModPotions.SHOCKED_EFFECT, spellStats);
                DischargeEffectPacket.send(world, new ParticleColor(225, 200, 50), livingEntity.position(), entity.position());
            }
        }
    }

    @Override
    public void applyPotion(LivingEntity entity, Holder<MobEffect> potionEffect, SpellStats stats, int baseDurationSeconds, int durationBuffSeconds, boolean showParticles) {
        if (entity == null) return;
        int ticks = baseDurationSeconds * 20 + durationBuffSeconds * stats.getDurationInTicks();
        int amp = (int) Math.min(stats.getAmpMultiplier(), 5);
        entity.addEffect(new MobEffectInstance(potionEffect, ticks, amp, false, showParticles, true));
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        shooter = !(shooter instanceof Player) ? ANFakePlayer.getPlayer((ServerLevel) world) : shooter;
        return DamageUtil.source(world, ModRegistry.SPARK, shooter);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 7.0);
        addAmpConfig(builder, 3.0);
        addPotionConfig(builder, 15);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public int getDefaultManaCost() {
        return 40;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE, AugmentRandomize.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @NotNull
    @Override
    protected Set<SpellSchool> getSchools() {
        return Set.of(SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 15 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 5 : EXTEND_TIME.get();
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the range of the discharge shock.");
    }
}
