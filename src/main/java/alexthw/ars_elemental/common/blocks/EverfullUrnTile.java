package alexthw.ars_elemental.common.blocks;

import alexthw.ars_elemental.common.entity.spells.EntityLerpedProjectile;
import alexthw.ars_elemental.registry.ModTiles;
import alexthw.ars_elemental.util.BotaniaCompat;
import alexthw.ars_elemental.util.CompatUtils;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static alexthw.ars_elemental.ConfigHandler.Common.WATER_URN_COST;
import static net.minecraft.world.level.material.Fluids.WATER;

public class EverfullUrnTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    Set<BlockPos> toList = new HashSet<>();

    public EverfullUrnTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModTiles.URN_TILE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (level == null || level.getGameTime() % 60 != 0 || toList.isEmpty() || level.isClientSide)
            return;

        ArrayList<BlockPos> stale = new ArrayList<>();

        // Iterate over all the positions in the list and try to refill them if they are still valid
        for (BlockPos toPos : toList) {
            if (!level.isLoaded(toPos))
                continue;
            if (!isRefillable(toPos, level)) {
                // If the position is no longer valid, add it to the stale list, so it can be removed later
                stale.add(toPos);
                continue;
            }
            // If there is a source nearby, try to refill the position and remove the source
            if (SourceUtil.hasSourceNearby(this.worldPosition, level, 6, WATER_URN_COST.get()))
                if (tryRefill(level, toPos)) {
                    SourceUtil.takeSourceMultiple(getBlockPos(), level, 6, WATER_URN_COST.get());
                    createParticles(this.worldPosition.above(), toPos);
                }
        }
        for (BlockPos s : stale) {
            toList.remove(s);
        }
        updateBlock();
    }

    static final FluidStack waterStack = new FluidStack(WATER, 1000);

    private boolean tryRefill(Level world, BlockPos toPos) {

        // If the position is a cauldron, try to fill it with water and return true if successful
        if (world.getBlockState(toPos) == Blocks.CAULDRON.defaultBlockState()) {
            world.setBlockAndUpdate(toPos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
            return true;
        }

        // Otherwise, try to fill the position with water if it is a fluid handler
        IFluidHandler tank = world.getCapability(Capabilities.FluidHandler.BLOCK, toPos, Direction.UP);
        if (tank != null) {
            if (tank.fill(waterStack, IFluidHandler.FluidAction.SIMULATE) > 100) {
                tank.fill(waterStack, IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }

        // If the position is a botania apothecary, try to fill it with water and return true if successful
        return CompatUtils.isBotaniaLoaded() && BotaniaCompat.tryFillApothecary(toPos, world);
    }


    @Override
    public void onWanded(Player playerEntity) {
        this.clearPos();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
    }

    public void clearPos() {
        this.toList.clear();
        updateBlock();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable GlobalPos storedPosG, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPosG == null || !(level instanceof ServerLevel) || storedPosG.pos().equals(getBlockPos()))
            return;
        BlockPos storedPos = storedPosG.pos();
        if (this.isRefillable(storedPos, level)) {
            if (this.setSendTo(storedPos.immutable())) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.send", DominionWand.getPosString(storedPos)));
                ParticleUtil.beam(storedPos, worldPosition, level);
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
            }
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail.urn"));
        }
    }

    private boolean isRefillable(BlockPos storedPos, Level level) {
        if (storedPos == null) return false;

        if (level.getBlockState(storedPos).getBlock() instanceof AbstractCauldronBlock) {
            return true;
        } else if (level.getBlockEntity(storedPos) != null && level.getCapability(Capabilities.FluidHandler.BLOCK, storedPos, Direction.UP) != null) {
            return true;
        } else return CompatUtils.isBotaniaLoaded() && BotaniaCompat.isApothecary(storedPos, level);
    }

    public void createParticles(BlockPos from, BlockPos to) {
        if (level == null) return;
        EntityLerpedProjectile orb = new EntityLerpedProjectile(level,
                from, to,
                20, 50, 255);
        level.addFreshEntity(orb);
    }

    public boolean setSendTo(BlockPos pos) {
        if (closeEnough(pos)) {
            toList.add(pos);
            updateBlock();
            return true;
        }
        return false;
    }

    public boolean closeEnough(BlockPos pos) {
        return BlockUtil.distanceFrom(pos, this.worldPosition) <= getMaxDistance() && !pos.equals(getBlockPos());
    }

    private double getMaxDistance() {
        return 30D;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toList == null || toList.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.relay.no_to"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.relay.one_to", toList.size()));
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        toList = new HashSet<>();
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "to_" + counter)) {
            BlockPos pos = NBTUtil.getNullablePos(tag, "to_" + counter);
            if (pos != null) this.toList.add(pos);
            counter++;
        }

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        int counter = 0;
        for (BlockPos p : this.toList) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p);
            counter++;
        }
    }
}
