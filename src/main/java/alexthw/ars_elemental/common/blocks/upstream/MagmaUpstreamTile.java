package alexthw.ars_elemental.common.blocks.upstream;

import alexthw.ars_elemental.registry.ModTiles;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static alexthw.ars_elemental.ConfigHandler.Common.LAVA_ELEVATOR_COST;

public class MagmaUpstreamTile extends BlockEntity implements ITickable {
    public MagmaUpstreamTile(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.LAVA_UPSTREAM_TILE.get(), pPos, pBlockState);
    }

    @Override
    public void tick() {
        if (this.level instanceof ServerLevel serverLevel && serverLevel.getGameTime() % 2 == 0) {

            if (serverLevel.getBlockState(getBlockPos().above()) == this.getBlockState()) return;

            int power = 1;
            while (serverLevel.getBlockState(getBlockPos().below(power)) == this.getBlockState()) power++;
            List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().getCenter(), getBlockPos().above(46 * power).getCenter()).inflate(1.5), e -> !e.isSpectator() && e.isInLava() && !e.isCrouching());
            if (!entityList.isEmpty() && requiresSource()) {
                var source = SourceUtil.takeSourceMultiple(this.getBlockPos(), serverLevel, 10, LAVA_ELEVATOR_COST.get());
                if (source == null || source.isEmpty() || !source.stream().allMatch(ISpecialSourceProvider::isValid)) return;
            }
            for (LivingEntity e : entityList) {
                e.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100));
                Vec3 vec3 = e.getDeltaMovement();
                e.setDeltaMovement(vec3.x, 0.4, vec3.z);
                e.resetFallDistance();
                for (int i = 0; i < 3; i++) {
                    spawnBubbles(e, serverLevel);
                }
                e.hurtMarked = true;
            }
        }
    }

    private boolean requiresSource() {
        return LAVA_ELEVATOR_COST.get() > 0;
    }

    public void spawnBubbles(Entity e, ServerLevel level) {

        double d0 = e.getX();
        double d1 = e.getY();
        double d2 = e.getZ();

        level.sendParticles(ParticleTypes.LANDING_LAVA, d0 + ParticleUtil.inRange(-0.5D, 0.5), d1 + 1, d2 + ParticleUtil.inRange(-0.5D, 0.5), 2, 0.0D, 0.0D, 0.0D, 0.5f);

    }
}
