package alexthw.ars_elemental.common.items;

import alexthw.ars_elemental.common.blocks.mermaid_block.MermaidTile;
import alexthw.ars_elemental.common.entity.MermaidEntity;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class SirenCharm extends AbstractSummonCharm {

    public SirenCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.PRISMARINE) {
            world.setBlockAndUpdate(pos, ModItems.MERMAID_ROCK.get().defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof MermaidTile) {
            MermaidEntity mermaid = new MermaidEntity(world, true);
            Vec3 vec = context.getClickLocation();
            mermaid.fromCharmData(context.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
            mermaid.setPos(vec.x, vec.y, vec.z);
            mermaid.setHome(pos);
            world.addFreshEntity(mermaid);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
