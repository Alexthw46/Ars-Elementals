package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.EntityCarryMEI;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static alexthw.ars_elemental.registry.ModPotions.LIFE_LINK;

public class EffectLifeLink extends ElementalAbstractEffect implements IPotionEffect {

    public static EffectLifeLink INSTANCE = new EffectLifeLink();

    public EffectLifeLink() {
        super("life_link", "Life Link");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        //if the shooter has the necromancy focus, the effect will be forcefully applied to the target and the shooter
        if (rayTraceResult.getEntity() instanceof LivingEntity livingEntity && shooter instanceof Player player && player != livingEntity) {
            if (resolver.hasFocus(ModItems.NECRO_FOCUS.get()))
                forceApplyPotion(livingEntity, player, LIFE_LINK.get(), spellStats);
            else applyPotion(livingEntity, player, LIFE_LINK.get(), spellStats);
        }
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getSummonAugments();
    } //just time boosters

    public void applyPotion(LivingEntity entity, LivingEntity owner, MobEffect potionEffect, SpellStats stats) {
        if (entity == null || owner == null) return;
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        entity.addEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity));
        owner.addEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity));
    }

    public void forceApplyPotion(LivingEntity entity, LivingEntity owner, MobEffect potionEffect, SpellStats stats) {
        if (entity == null || owner == null) return;
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        entity.forceAddEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity), entity);
        owner.forceAddEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity), owner);
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
