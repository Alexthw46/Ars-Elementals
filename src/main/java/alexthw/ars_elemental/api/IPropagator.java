package alexthw.ars_elemental.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IPropagator{

    AbstractAugment DUMMY = AugmentDampen.INSTANCE;

    static Vec3 getDirection(LivingEntity shooter, SpellResolver resolver, Vec3 pos) {
        Vec3 direction = pos.subtract(shooter.position());
        if (resolver.spellContext.getCaster() instanceof TileCaster tc) {
            if (tc.getTile() instanceof RotatingTurretTile rotatingTurretTile) {
                direction = rotatingTurretTile.getShootAngle();
            } else if (tc.getTile() instanceof BasicSpellTurretTile) {
                direction = new Vec3(tc.getTile().getBlockState().getValue(BasicSpellTurret.FACING).step());
            } else if (tc.getTile() instanceof RuneTile) {
                direction = new Vec3(tc.getTile().getBlockState().getValue(RuneBlock.FACING).step());
            }
        }
        return direction;
    }

    default void copyResolver(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        SpellContext newContext = spellContext.makeChildContext();
        var mutable_spell = newContext.getSpell().mutable();
        mutable_spell.recipe.addFirst(DUMMY);
        newContext.withSpell(mutable_spell.immutable());
        spellContext.setCanceled(true);
        SpellResolver newResolver = resolver.getNewResolver(newContext);
        propagate(world, rayTraceResult, shooter, stats, newResolver);
    }

    void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver);

}
