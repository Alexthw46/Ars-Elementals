package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityHomingProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class MethodHomingProjectile extends ElementalAbstractForm {

    public static MethodHomingProjectile INSTANCE = new MethodHomingProjectile();

    public MethodHomingProjectile() {
        super("homing_projectile", "Homing Projectile");
    }

    @Override
    public String getBookDescription() {
        return "This projectile seek the nearest entity and follow it, will behave as a classic projectile while there are no valid targets. Players will only be targeted if augmented by Sensitive.";
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver, List<Predicate<LivingEntity>> ignore) {

        int numSplits = 1 + stats.getBuffCount(AugmentSplit.INSTANCE);

        List<EntityHomingProjectileSpell> projectiles = new ArrayList<>();
        for (int i = 0; i < numSplits; i++) {
            EntityHomingProjectileSpell spell = new EntityHomingProjectileSpell(world, resolver);
            projectiles.add(spell);
        }
        float velocity = getProjectileSpeed(stats);
        int opposite = -1;
        int counter = 0;
        for (EntityHomingProjectileSpell proj : projectiles) {
            proj.setIgnored(ignore);
            proj.shoot(shooter, shooter.getXRot(), shooter.getYRot() + Math.round(counter / 2.0) * 5 * opposite, 0.0F, velocity, 0.8f);
            opposite = opposite * -1;
            counter++;
            world.addFreshEntity(proj);
        }
    }

    public static float getProjectileSpeed(SpellStats stats) {
        return Math.max(0.2F, 0.5F + stats.getAccMultiplier() / 5.0F);
    }

    @Override
    public CastResolveType onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {

        List<Predicate<LivingEntity>> ignore = basicIgnores(shooter, spellStats.hasBuff(AugmentSensitive.INSTANCE), resolver.spell);

        if (shooter instanceof Player) {
            ignore.add(entity -> entity instanceof ISummon summon && shooter.getUUID().equals(summon.getOwnerUUID()));
            ignore.add(entity -> entity instanceof OwnableEntity pet && shooter.equals(pet.getOwner()));
        } else if (shooter instanceof ISummon summon && summon.getOwnerUUID() != null) {
            ignore.add(entity -> entity instanceof ISummon summon2 && summon.getOwnerUUID().equals(summon2.getOwnerUUID()));
            ignore.add(entity -> entity instanceof OwnableEntity pet && summon.getOwnerUUID().equals(pet.getOwnerUUID()));
        }

        summonProjectiles(world, shooter, spellStats, resolver, ignore);

        return CastResolveType.SUCCESS;
    }

    /**
     * Cast by players
     */
    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        onCast(null, shooter, world, spellStats, spellContext, resolver);
        return CastResolveType.SUCCESS;
    }

    /**
     * Cast by others.
     */
    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return CastResolveType.FAILURE;
    }

    @Override
    public CastResolveType onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        onCast(stack, caster, caster.level(), spellStats, spellContext, resolver);
        return CastResolveType.SUCCESS;
    }

    @Override
    public int getDefaultManaCost() {
        return 75;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentPierce.INSTANCE.getRegistryName(), 1);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentSensitive.INSTANCE);
    }

    public static List<Predicate<LivingEntity>> basicIgnores(LivingEntity shooter, Boolean targetPlayers, Spell spell) {
        List<Predicate<LivingEntity>> ignore = new ArrayList<>();

        ignore.add((entity -> !entity.isAlive()));
        ignore.add((entity -> entity == shooter));
        ignore.add(entity -> entity instanceof FamiliarEntity);
        ignore.add(entity -> entity.hasEffect(MobEffects.INVISIBILITY));
        ignore.add(shooter::isAlliedTo);
        if (!targetPlayers) {
            ignore.add(entity -> entity instanceof Player);
        }
        Set<IFilter> filters = GlyphEffectUtil.getFilters(spell.unsafeList(), 0);
        if (!filters.isEmpty()) {
            ignore.add(entity -> GlyphEffectUtil.checkIgnoreFilters(entity, filters));
        }
        return ignore;
    }

    public ModConfigSpec.IntValue PROJECTILE_TTL;

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentPierce.INSTANCE, "Projectiles will pierce through enemies and blocks an additional time.");
        map.put(AugmentSplit.INSTANCE, "Creates multiple projectiles.");
        map.put(AugmentAccelerate.INSTANCE, "Projectiles will move faster.");
        map.put(AugmentDecelerate.INSTANCE, "Projectiles will move slower.");
        map.put(AugmentSensitive.INSTANCE, "Projectiles will also target players.");
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PROJECTILE_TTL = builder.comment("Max lifespan of the projectile, in seconds.").defineInRange("max_lifespan", 30, 0, Integer.MAX_VALUE);
    }

}