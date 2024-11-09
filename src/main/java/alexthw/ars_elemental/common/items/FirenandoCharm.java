package alexthw.ars_elemental.common.items;

import alexthw.ars_elemental.common.entity.FirenandoEntity;
import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FirenandoCharm extends AbstractSummonCharm {

    public FirenandoCharm(Properties properties) {
        super(properties.component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor(FirenandoEntity.Variants.MAGMA.toString())));
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        FirenandoEntity firenando = new FirenandoEntity(world);
        Vec3 vec = context.getClickLocation();
        firenando.fromCharmData(context.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
        firenando.setPos(vec.x, vec.y, vec.z);
        firenando.setHome(pos);
        firenando.setOwner(context.getPlayer());
        world.addFreshEntity(firenando);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        return useOnBlock(context, world, pos);
    }

}
