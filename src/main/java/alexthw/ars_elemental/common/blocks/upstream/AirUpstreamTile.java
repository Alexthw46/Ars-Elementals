package alexthw.ars_elemental.common.blocks.upstream;

import alexthw.ars_elemental.registry.ModTiles;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static alexthw.ars_elemental.ConfigHandler.Common.AIR_ELEVATOR_COST;

public class AirUpstreamTile extends BlockEntity implements ITickable {

    public AirUpstreamTile(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.AIR_UPSTREAM_TILE.get(), pPos, pBlockState);
    }

    @Override
    public void tick() {
        if (this.level instanceof ServerLevel serverLevel && serverLevel.getGameTime() % 20 == 0) {
            if (serverLevel.getBlockState(getBlockPos().above()) == this.getBlockState()) return;

            int power = 1;
            while (serverLevel.getBlockState(getBlockPos().below(power)) == this.getBlockState()) power++;
            List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().getCenter(), getBlockPos().above(46 * power).getCenter()).inflate(1.1), e -> !e.isSpectator() && !e.isInWater() && !e.isInLava());

            if (!entityList.isEmpty() && requiresSource()) {
                var source = SourceUtil.takeSourceMultiple(this.getBlockPos(), serverLevel, 10, power * AIR_ELEVATOR_COST.get());
                if (source == null || source.isEmpty() || !source.stream().allMatch(ISpecialSourceProvider::isValid)) return;
            }
            for (LivingEntity e : entityList) {
                e.resetFallDistance();
                e.addEffect(new MobEffectInstance((e.isCrouching() ? MobEffects.SLOW_FALLING : MobEffects.LEVITATION), 25, 1, false, false, false));
                e.hurtMarked = true;
            }
        }
    }

    private boolean requiresSource() {
        return AIR_ELEVATOR_COST.get() > 0;
    }

}
