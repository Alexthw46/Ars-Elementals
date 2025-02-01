package alexthw.ars_elemental.common.blocks.prism;

import alexthw.ars_elemental.api.item.SpellPrismLens;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.block.SpellPrismBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedPrism extends SpellPrismBlock implements EntityBlock {

    public AdvancedPrism(Properties blockProps) {
        super(blockProps.noOcclusion());
    }

    static final SpellPrismLens defaultLent = (spell, level, pos) -> true;

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!player.level().isClientSide) {
            if (stack.getItem() instanceof SpellPrismLens && level.getBlockEntity(pos) instanceof AdvancedPrismTile tile) {
                tile.setLens(stack.split(1), player);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onHit(ServerLevel world, BlockPos pos, EntityProjectileSpell spell) {
        if (!(world.getBlockEntity(pos) instanceof AdvancedPrismTile tile)) return;
        Position iposition = getDispensePosition(pos, tile.getShootAngle());
        spell.setPos(iposition.x(), iposition.y(), iposition.z());
        spell.prismRedirect++;
        if (spell.prismRedirect >= 3) {
            ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.PRISMATIC.get(), world, pos, 10);
        }
        if (spell.spellResolver == null) {
            spell.remove(Entity.RemovalReason.DISCARDED);
            return;
        }
        Vec3 vec3d = tile.getShootAngle().normalize();
        // get the lens from the tile and check if it can convert the spell, if it can, shoot it, if not use the default lens
        if (tile.getLens().getItem() instanceof SpellPrismLens lens && lens.canConvert(spell, world, pos)) {
            lens.shoot(world, pos, spell, vec3d);
        } else {
            defaultLent.shoot(world, pos, spell, vec3d);
        }
        BlockUtil.updateObservers(world, pos);
    }

    public Position getDispensePosition(BlockPos blockPos, Vec3 direction) {
        Vec3 coords = Vec3.atCenterOf(blockPos);
        double d0 = coords.x() + 0.5D * direction.x();
        double d1 = coords.y() + 0.5D * direction.y();
        double d2 = coords.z() + 0.5D * direction.z();
        return new Vec3(d0, d1, d2);
    }

    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        Direction orientation = placer == null ? Direction.WEST : Direction.orderedByNearest(placer)[0].getOpposite();

        if (!(world.getBlockEntity(pos) instanceof AdvancedPrismTile prismTile)) return;
        switch (orientation) {
            case DOWN:
                prismTile.rotationY = -90F;
                break;
            case UP:
                prismTile.rotationY = 90F;
                break;
            case NORTH:
                prismTile.rotationX = 270F;
                break;
            case SOUTH:
                prismTile.rotationX = 90F;
                break;
            case WEST:
                break;
            case EAST:
                prismTile.rotationX = 180F;
                break;
        }
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, LevelAccessor level, @NotNull BlockPos pos, @NotNull Rotation rot) {
        if (level.getBlockEntity(pos) instanceof AdvancedPrismTile prismTile) {
            prismTile.setRotX(prismTile.getRotationX() + switch (rot) {
                        case NONE -> 0;
                        case CLOCKWISE_90 -> 90;
                        case CLOCKWISE_180 -> 180;
                        case COUNTERCLOCKWISE_90 -> -90;
                    }
            );
            prismTile.updateBlock();
        }
        return super.rotate(state, level, pos, rot);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new AdvancedPrismTile(pPos, pState);
    }
}
