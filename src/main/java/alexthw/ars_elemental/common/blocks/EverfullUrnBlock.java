package alexthw.ars_elemental.common.blocks;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EverfullUrnBlock extends TickableModBlock implements BucketPickup {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public EverfullUrnBlock(Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    static final VoxelShape VSHAPE = Shapes.join(Block.box(3, 0, 3, 13, 9, 13), Block.box(5.5, 9, 5.5, 10.5, 13, 10.5), BooleanOp.OR);

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return VSHAPE;
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level level, BlockPos pos, @NotNull RandomSource random) {

        if (level.getBlockState(pos.above()).isAir()) {

            double i = pos.getX() + 0.5;
            double j = pos.getY() + 1.0;
            double k = pos.getZ() + 0.5;

            for (int c = 0; c < 5; c++) {
                double d0 = i + ParticleUtil.inRange(-0.25, 0.25);
                double d1 = j + ParticleUtil.inRange(0, 0.05);
                double d2 = k + ParticleUtil.inRange(-0.25, 0.25);
                level.addParticle(ParticleTypes.SPLASH, d0, d1, d2, 0, ParticleUtil.inRange(-0.01, 0.01), 0);
            }

        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new EverfullUrnTile(pPos, pState);
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, Mirror pMirror) {
        return this.rotate(pState, pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public @NotNull ItemStack pickupBlock(Player pPlayer, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ItemStack(Fluids.WATER.getBucket());
    }

    @Override
    public @NotNull Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

}
