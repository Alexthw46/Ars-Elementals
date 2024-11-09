package alexthw.ars_elemental.mixin;

import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.ritual.RitualAwakening;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(RitualAwakening.class)
public abstract class RitualAwakeningMixin {

    @Shadow(remap = false)
    EntityType<WealdWalker> entity;
    @Shadow(remap = false)
    BlockPos foundPos;

    @Shadow(remap = false)
    public abstract void destroyTree(ServerLevel world, Set<BlockPos> set);

    @WrapWithCondition(method = "tick", remap = false, at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/ritual/RitualAwakening;findTargets(Lnet/minecraft/server/level/ServerLevel;)V"))
    public boolean findFlashing(RitualAwakening instance, ServerLevel level) {
        if (instance.getPos() == null) return false;
        for (BlockPos p : BlockPos.withinManhattan(instance.getPos(), 3, 1, 3)) {
            Set<BlockPos> flashing = SpellUtil.DFSBlockstates(level, p, 350, (b) -> b.getBlock() == ModItems.FLASHING_ARCHWOOD_LOG.get() || b.getBlock() == ModItems.FLASHING_LEAVES.get());
            if (flashing.size() >= 50) {
                entity = ModEntities.FLASHING_WEALD_WALKER.get();
                foundPos = p;
                destroyTree(level, flashing);
                return false;
            }
        }
        return (entity == null);
    }

}
