package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.util.EntityCarryMEI;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
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
                forceApplyPotion(livingEntity, player, LIFE_LINK, spellStats);
            else applyPotion(livingEntity, player, LIFE_LINK, spellStats);

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
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDefaultPotionConfig(builder);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getSummonAugments();
    } //just time boosters

    public void applyPotion(LivingEntity entity, LivingEntity owner, Holder<MobEffect> potionEffect, SpellStats stats) {
        if (entity == null || owner == null) return;
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        entity.addEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity));
        owner.addEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity));
    }

    public void forceApplyPotion(LivingEntity entity, LivingEntity owner, Holder<MobEffect> potionEffect, SpellStats stats) {
        if (entity == null || owner == null) return;
        int ticks = getBaseDuration() * 20 + getExtendTimeDuration() * stats.getDurationInTicks();
        entity.forceAddEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity), entity);
        owner.forceAddEffect(new EntityCarryMEI(potionEffect, ticks, 0, false, true, owner, entity), owner);
    }

    @Override
    public String getBookDescription() {
        return "You create a link between your life force and the target's. Any damage dealt to you will be shared with the target and any healing of the target will be shared with you. Cut can sever the life link, ending the effect on both sides.";
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
