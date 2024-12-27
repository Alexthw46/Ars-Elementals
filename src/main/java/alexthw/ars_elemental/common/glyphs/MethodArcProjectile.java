package alexthw.ars_elemental.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MethodArcProjectile extends ElementalAbstractForm {

    public static MethodArcProjectile INSTANCE = new MethodArcProjectile();

    public static float getProjectileSpeed(SpellStats stats) {
        return Math.max(0.2f, 1.0f + stats.getAccMultiplier() / 2.0f);
    }

    public MethodArcProjectile() {
        super("arc_projectile", "Arc Projectile");
    }

    @Override
    public String getBookDescription() {
        return "This projectile is affected by gravity. Every Pierce applied will make it bounce one time when it hit the ground.";
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);
        float sizeRatio = shooter.getEyeHeight() / Player.DEFAULT_EYE_HEIGHT;

        for (int i = 1; i < 1 + numSplits + 1; i++) {
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver).setGravity(true);
            projectiles.add(spell);
        }

        float velocity = getProjectileSpeed(stats);
        int opposite = -1;
        int counter = 0;
        for (EntityProjectileSpell proj : projectiles) {
            proj.setPos(proj.position().add(0, 0.25 * sizeRatio, 0));
            proj.shoot(shooter, shooter.getXRot(), shooter.getYRot() + Math.round(counter / 2.0) * 5 * opposite, 0.0F, velocity, 0.5f);
            opposite = opposite * -1;
            counter++;
            world.addFreshEntity(proj);
        }
    }

    @Override
    public CastResolveType onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        summonProjectiles(world, shooter, spellStats, resolver);
        return CastResolveType.SUCCESS;
    }

    /**
     * Cast by players
     */
    @Override
    public CastResolveType onCastOnBlock(@NotNull UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        summonProjectiles(world, shooter, spellStats, resolver);
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
        return 10;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentSensitive.INSTANCE);//, AugmentExtract.INSTANCE);
    }

    public ModConfigSpec.IntValue PROJECTILE_TTL;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PROJECTILE_TTL = builder.comment("Max lifespan of the projectile, in seconds.").defineInRange("max_lifespan", 60, 0, Integer.MAX_VALUE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentPierce.INSTANCE, "Projectiles will bounce on blocks or hit through enemies an additional time.");
        map.put(AugmentSplit.INSTANCE, "Creates multiple projectiles.");
        map.put(AugmentAccelerate.INSTANCE, "Projectiles will move faster.");
        map.put(AugmentDecelerate.INSTANCE, "Projectiles will move slower.");
        map.put(AugmentSensitive.INSTANCE, "Projectiles will hit plants and other materials that do not block motion.");
    }
}
