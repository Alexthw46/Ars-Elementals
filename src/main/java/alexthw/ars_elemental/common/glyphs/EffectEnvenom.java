package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectEnvenom extends ElementalAbstractEffect implements IPotionEffect, IDamageEffect {

    public static EffectEnvenom INSTANCE = new EffectEnvenom();

    public EffectEnvenom() {
        super("envenom", "Envenom");
    }

    @Override
    public String getBookDescription() {
        return "Poisons the target, dealing damage over time. If the target is already poisoned, the poison will become a more deadly venom. An envenomed target takes more damage from poison spores.";
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity target && canDamage(shooter, spellStats, spellContext, resolver, target)) {
            MobEffectInstance poison = target.getEffect(MobEffects.POISON);
            if (poison != null) {
                spellStats.setAmpMultiplier(poison.getAmplifier() / 2F + spellStats.getAmpMultiplier());
                this.applyConfigPotion(target, ModPotions.VENOM, spellStats);
                target.removeEffect(MobEffects.POISON);
            } else {
                boolean earth_switch = (target.getType().is(EntityTypeTags.IGNORES_POISON_AND_REGEN) || target instanceof Spider) && ISchoolFocus.earthCheck(resolver);
                this.applyConfigPotion(target, earth_switch ? ModPotions.VENOM : MobEffects.POISON, spellStats);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return getPotionAugments();
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 5);
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
