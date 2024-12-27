package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.api.IPropagator;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectReset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class PropagatorArc extends ElementalAbstractEffect implements IPropagator {

    public static PropagatorArc INSTANCE = new PropagatorArc();

    public PropagatorArc() {
        super("propagator_arc", "Propagate Arc");
        EffectReset.RESET_LIMITS.add(this);
    }

    @Override
    public String getBookDescription() {
        return "Convert the remainder of the spell into an Arc Projectile and shoot it from where it lands.";
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    @Override
    public void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        Vec3 pos = hitResult.getLocation();
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver).setGravity(true);
        projectileSpell.setPos(pos.add(0, 1, 0));
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        float sizeRatio = shooter.getEyeHeight() / Player.DEFAULT_EYE_HEIGHT;

        for (int i = 1; i < numSplits + 1; i++) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) offset = offset.getOpposite();
            // Alternate sides
            BlockPos projPos = BlockPos.containing(pos).relative(offset, i).offset(0, (int) (1.5 * sizeRatio), 0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver).setGravity(true);
            spell.setPos(projPos.getX(), projPos.getY(), projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = MethodArcProjectile.getProjectileSpeed(stats);
        Vec3 direction = IPropagator.getDirection(shooter, resolver, pos);
        for (EntityProjectileSpell proj : projectiles) {
            proj.setPos(proj.position().add(0, 0.25 * sizeRatio, 0));
            if (!(shooter instanceof FakePlayer)) {
                proj.shoot(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, 0.3f);
            } else {
                proj.shoot(direction.x, direction.y, direction.z, velocity, 0.8F);
            }
            world.addFreshEntity(proj);
        }
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public int getDefaultManaCost() {
        return 200;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return MethodArcProjectile.INSTANCE.getCompatibleAugments();
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
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
