package alexthw.ars_elemental.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(BasicSpellTurretTile.class)
public interface TurretAccessor {

    //accessor for uuid
    @Accessor
    UUID getUuid();
}
