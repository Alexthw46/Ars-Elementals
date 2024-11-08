package alexthw.ars_elemental.common.blocks.prism;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDecelerate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DecelerationPrismLens extends AbstractPrismLens {

    public DecelerationPrismLens(Properties properties) {
        super(properties, "deceleration");
    }

    @Override
    public void shoot(ServerLevel world, BlockPos pos, EntityProjectileSpell spell, Vec3 angle) {
        spell.spellResolver.spell = spell.spellResolver.spell.mutable().add(1, AugmentDecelerate.INSTANCE).immutable();
        super.shoot(world, pos, spell, angle);
    }

    @Override
    public boolean canConvert(EntityProjectileSpell spell, Level level, BlockPos pos) {
        return true;
    }

}
