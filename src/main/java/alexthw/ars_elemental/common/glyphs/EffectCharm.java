package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.common.items.foci.NecroticFocus;
import alexthw.ars_elemental.mixin.FoxInvoker;
import alexthw.ars_elemental.registry.ModRegistry;
import alexthw.ars_elemental.util.EntityCarryMEI;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static alexthw.ars_elemental.registry.ModPotions.ENTHRALLED;

public class EffectCharm extends ElementalAbstractEffect implements IPotionEffect {

    public static EffectCharm INSTANCE = new EffectCharm();

    public EffectCharm() {
        super("charm", "Charm");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (shooter instanceof Player player && world instanceof ServerLevel level) {
            if (rayTraceResult.getEntity().getType().is(ModRegistry.CHARM_BLACKLIST)) return;
            if (rayTraceResult.getEntity() instanceof Mob mob) {
                if (mob.getMaxHealth() < GENERIC_INT.get() || player.getUUID().equals(ArsElemental.Dev)) {
                    // check if mob is hostile or neutral and not an animal that can fall in love
                    if (mob instanceof Enemy || (mob instanceof NeutralMob && (!(mob instanceof Animal a && a.canFallInLove())))) {
                        // calculate resistance and chance boost based on mob health and amplification
                        float resistance = 10 + 100 * (mob.getHealth() / mob.getMaxHealth());
                        double chanceBoost = 10 + spellStats.getAmpMultiplier() * 5;

                        // if the mob is undead and the shooter has the necrotic focus, increase the chance by 50%
                        if (mob.getType().is(EntityTypeTags.UNDEAD) && NecroticFocus.hasFocus(world, shooter)) {
                            chanceBoost += 50;
                        }

                        if (rollToSeduce((int) resistance, chanceBoost, level.getRandom())) {
                            applyPotion(mob, player, ENTHRALLED, spellStats);
                            playHeartParticles(mob, level);
                        }

                    } else if (rayTraceResult.getEntity() instanceof Animal animal) {
                        // check if the animal can be tamed, then tame it
                        if (animal instanceof TamableAnimal tamable && !tamable.isTame()) {
                            if (rollToSeduce(100, 25 * (1 + spellStats.getAmpMultiplier()), level.getRandom()))
                                tamable.tame(player);
                        } else if (animal instanceof AbstractHorse horse && !horse.isTamed()) {
                            if (rollToSeduce(100, 25 * (1 + spellStats.getAmpMultiplier()), level.getRandom()))
                                horse.setTamed(true);
                        } else if (animal instanceof Fox fox && !((FoxInvoker) fox).callTrusts(player.getUUID())) {
                            if (rollToSeduce(100, 25 * (1 + spellStats.getAmpMultiplier()), level.getRandom()))
                                ((FoxInvoker) fox).callAddTrustedUUID(player.getUUID());
                        } else if (animal.canFallInLove()) { // if the animal can fall in love, then make it fall in love
                            if (rollToSeduce(90, 25 * (1 + spellStats.getAmpMultiplier()), level.getRandom()))
                                animal.setInLove(player);
                        }

                    }
                }
            }
        }
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    // returns true if the roll is greater than the resistance
    private boolean rollToSeduce(int resistance, double chanceBoost, RandomSource rand) {
        return (rand.nextInt(0, resistance) + chanceBoost) >= resistance;
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
        addGenericInt(builder, 150, "Set the max hp limit for Charm, mobs with more max hp will be immune.", "charm_hp_limit");
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getPotionAugments();
    }

    public void applyPotion(LivingEntity entity, LivingEntity owner, Holder<MobEffect> potionEffect, SpellStats stats) {
        if (entity == null || owner == null) return;
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        int amp = (int) stats.getAmpMultiplier();
        entity.addEffect(new EntityCarryMEI(potionEffect, ticks, amp, false, true, owner, null));
    }

    private void playHeartParticles(LivingEntity entity, ServerLevel world) {
        for (int i = 0; i < 5; ++i) {
            double d0 = entity.getRandom().nextGaussian() * 0.02D;
            double d1 = entity.getRandom().nextGaussian() * 0.02D;
            double d2 = entity.getRandom().nextGaussian() * 0.02D;
            world.sendParticles(ParticleTypes.HEART, entity.getX() + (world.random.nextFloat() - 0.5) / 2, entity.getY() + (world.random.nextFloat() + 0.5), entity.getZ() + (world.random.nextFloat() - 0.5) / 2, 2, d0, d1, d2, 0.1f);
        }
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
    public String getBookDescription() {
        return "Try to dominate the mind of enemy, making them fight for you as a temporary ally, a tamable mob, to tame it, or a wild animal, to make it fall in love. Each Amplify and damage dealt raises the chance of successfully charming the target, as stronger mobs will have higher resistance to your control.";
    }
}
