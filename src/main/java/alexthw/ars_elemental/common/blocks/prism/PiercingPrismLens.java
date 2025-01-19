package alexthw.ars_elemental.common.blocks.prism;

import alexthw.ars_elemental.ConfigHandler;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PiercingPrismLens extends AbstractPrismLens {
    public PiercingPrismLens(Properties properties) {
        super(properties, "pierce");
    }

    @Override
    public void shoot(ServerLevel world, BlockPos pos, EntityProjectileSpell spell, Vec3 angle) {
        super.shoot(world, pos, spell, angle);
        spell.pierceLeft++;
        SourceUtil.takeSourceMultiple(pos, world, 6, AugmentPierce.INSTANCE.getCastingCost());
    }

    @Override
    public boolean canConvert(EntityProjectileSpell spell, Level level, BlockPos pos) {
        return spell.pierceLeft < ConfigHandler.Common.PIERCE_LENS_LIMIT.get() && SourceUtil.hasSourceNearby(pos, level, 6, AugmentPierce.INSTANCE.getCastingCost());
    }
}
