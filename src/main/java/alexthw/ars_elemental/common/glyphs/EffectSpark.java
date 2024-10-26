package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static alexthw.ars_elemental.registry.ModPotions.LIGHTNING_LURE;

public class EffectSpark extends ElementalAbstractEffect implements IPotionEffect, IDamageEffect {

    public static EffectSpark INSTANCE = new EffectSpark();

    public EffectSpark() {
        super("spark", "Spark");
    }

    @Override
    protected int getDefaultManaCost() {
        return 15;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity target && canDamage(shooter, spellStats, spellContext, resolver, target)) {
            double damage = this.DAMAGE.get() + this.AMP_VALUE.get() * spellStats.getAmpMultiplier() + (target.isInWaterRainOrBubble() ? 2 : 0);
            attemptDamage(world, shooter, spellStats, spellContext, resolver, target, buildDamageSource(world, shooter), (float) damage);
            this.applyConfigPotion(target, ISchoolFocus.airCheck(resolver) ? LIGHTNING_LURE : ModPotions.SHOCKED_EFFECT, spellStats);
        }
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        shooter = !(shooter instanceof Player) ? ANFakePlayer.getPlayer((ServerLevel) world) : shooter;
        return DamageUtil.source(world, ModRegistry.SPARK, shooter);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 3);
        addAmpConfig(builder, 1.5);
        addPotionConfig(builder, 15);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentRandomize.INSTANCE);
    }

    public @NotNull Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_AIR);
    }

    public int getBaseDuration() {
        return this.POTION_TIME == null ? 15 : this.POTION_TIME.get();
    }

    public int getExtendTimeDuration() {
        return this.EXTEND_TIME == null ? 5 : this.EXTEND_TIME.get();
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }
}
