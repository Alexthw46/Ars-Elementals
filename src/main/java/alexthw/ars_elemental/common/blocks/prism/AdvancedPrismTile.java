package alexthw.ars_elemental.common.blocks.prism;

import alexthw.ars_elemental.registry.ModTiles;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AdvancedPrismTile extends ModdedTile implements IWandable, GeoBlockEntity {
    private static final String TAG_LENTS = "prismLent";
    private static final String TAG_ROTATION_X = "rotationX";
    private static final String TAG_ROTATION_Y = "rotationY";
    public float rotationX, rotationY;

    private ItemStack prismLens;

    public AdvancedPrismTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public AdvancedPrismTile(BlockPos pos, BlockState state) {
        super(ModTiles.ADVANCED_PRISM.get(), pos, state);
    }

    public void aim(@Nullable BlockPos blockPos, Player playerEntity) {
        if (blockPos == null) return;

        // This is the code that makes the prism aim at the block

        Vec3 thisVec = Vec3.atCenterOf(getBlockPos());
        Vec3 blockVec = Vec3.atCenterOf(blockPos);

        Vec3 diffVec = blockVec.subtract(thisVec);
        Vec3 diffVec2D = new Vec3(diffVec.x, diffVec.z, 0);
        Vec3 rotVec = new Vec3(0, 1, 0);
        double angle = angleBetween(rotVec, diffVec2D) / Math.PI * 180.0;

        if (blockVec.x < thisVec.x) {
            angle = -angle;
        }

        setRotX((float) angle + 90);

        rotVec = new Vec3(diffVec.x, 0, diffVec.z);
        angle = angleBetween(diffVec, rotVec) * 180F / Math.PI;
        if (blockVec.y < thisVec.y) {
            angle = -angle;
        }
        setRotY((float) angle);

        updateBlock();
        ParticleUtil.beam(blockPos, getBlockPos(), level);
        PortUtil.sendMessageNoSpam(playerEntity, Component.literal("Prism now aims to " + blockPos.toShortString()));
    }

    public static double angleBetween(Vec3 a, Vec3 b) {
        double projection = a.normalize().dot(b.normalize());
        return Math.acos(Mth.clamp(projection, -1, 1));
    }

    @Override
    public void onWanded(Player playerEntity) {
        // remove prism lens and drop it
        if (prismLens != null) {
            playerEntity.level().addFreshEntity(new ItemEntity(playerEntity.level(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), prismLens));
            prismLens = ItemStack.EMPTY;
        }
        updateBlock();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) this.aim(storedPos, playerEntity);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putFloat(TAG_ROTATION_Y, rotationY);
        tag.putFloat(TAG_ROTATION_X, rotationX);
        if (prismLens != null) {
            tag.put(TAG_LENTS, prismLens.save(pRegistries));
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        rotationX = tag.getFloat(TAG_ROTATION_X);
        rotationY = tag.getFloat(TAG_ROTATION_Y);
        if (tag.contains(TAG_LENTS)) {
            prismLens = ItemStack.parse(pRegistries, tag.getCompound(TAG_LENTS)).orElse(ItemStack.EMPTY);
        }
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotX(float rot) {
        rotationX = rot;
    }

    public void setRotY(float rot) {
        rotationY = rot;
    }


    /**
     * @return Vector for projectile shooting. Don't ask me why it works, it was pure luck.
     */
    public Vec3 getShootAngle() {
        // This is the code that makes the prism shoot in the direction it is aiming at
        float f = getRotationY() * ((float) Math.PI / 180F);
        float f1 = (90 + getRotationX()) * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4).reverse();
    }

    final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public ItemStack getLens() {
        return prismLens == null ? ItemStack.EMPTY : prismLens;
    }

    void setLens(ItemStack lent, Player pPlayer) {
        onWanded(pPlayer);
        this.prismLens = lent;
        updateBlock();
    }
}
