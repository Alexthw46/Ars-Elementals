package alexthw.ars_elemental.common.entity.spells;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.registry.DataSerializers;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class CarianPhalanx extends EntityProjectileSpell {
    public CarianPhalanx(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    //map owner to phalanxes it has
    public static Map<UUID, CarianPhalanx> phalanxMap;
    //reverse map phalanx to owner
    public static Map<UUID, UUID> reversePhalanxMap;

    public int ticksLeft;
    public static final EntityDataAccessor<Vec3> LAST_POS = SynchedEntityData.defineId(EntityOrbitProjectile.class, DataSerializers.VEC.get());
    public static final EntityDataAccessor<Integer> OFFSET = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(EntityOrbitProjectile.class, EntityDataSerializers.INT);
    public int extendTimes;

    LivingEntity target;

    public void setOffset(int offset) {
        entityData.set(OFFSET, offset);
    }

    public int getOffset() {
        int val = 15;
        return (entityData.get(OFFSET)) * val;
    }

    public void setTotal(int total) {
        entityData.set(TOTAL, total);
    }

    public int getTotal() {
        return entityData.get(TOTAL) > 0 ? entityData.get(TOTAL) : 1;
    }

    public void setAccelerates(int accelerates) {
        entityData.set(ACCELERATES, accelerates);
    }

    public int getAccelerates() {
        return entityData.get(ACCELERATES);
    }

    public void setAoe(float aoe) {
        entityData.set(AOE, aoe);
    }

    public float getAoe() {
        return entityData.get(AOE);
    }

    public double getRotateSpeed() {
        return 10.0 - getAccelerates();
    }

    public double getRadiusMultiplier() {
        return 1.5 + 0.5 * getAoe();
    }

    @Override
    public void tick() {
        super.tick();


    }

    @Override
    public Vec3 getNextHitPosition() {
        // while it has no target,
        // every sword of the phalanx should be in a fixed radius around the owner, based on the index of the phalanx.
        // the phalanx maximum size is 5 and it the center of the orbit is the eye level
        if (getOwner() == null) {
            return getAngledPosition(tickCount + 3);
        }
        if (target == null || target.isRemoved()) {

            Vec3 ownerPos = getOwner().getEyePosition();
            return new Vec3(
                    ownerPos.x() - getRadiusMultiplier() * Math.sin(tickCount / getRotateSpeed() + getOffset()),
                    ownerPos.y() + 1 - (getOwner().isShiftKeyDown() ? 0.25 : 0),
                    ownerPos.z() - getRadiusMultiplier() * Math.cos(tickCount / getRotateSpeed() + getOffset()));
        }else{
         // if we have a target, use homing's logic to get the next position
            return homeTo(target.blockPosition());
        }
    }

    private void homeTo(BlockPos dest) {

        double posX = getX();
        double posY = getY();
        double posZ = getZ();
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;

        if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0) {
            double targetX = dest.getX() + 0.5;
            double targetY = dest.getY() + 0.75;
            double targetZ = dest.getZ() + 0.5;
            Vec3 targetVector = new Vec3(targetX - posX, targetY - posY, targetZ - posZ);
            double length = targetVector.length();
            targetVector = targetVector.scale(0.3 / length);
            double weight = 0;
            if (length <= 3) {
                weight = (3.0 - length) * 0.3;
            }

            motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
            motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
            motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        this.setPos(posX, posY, posZ);

        this.setDeltaMovement(motionX, motionY, motionZ);
    }


    @Override
    public void tickNextPosition() {
        this.setPos(getAngledPosition(tickCount));
    }

    public Vec3 getAngledPosition(int nextTick) {
        double rotateSpeed = getRotateSpeed();
        double radiusMultiplier = getRadiusMultiplier();
        Entity owner = getOwner();
        if(owner == null || owner.isRemoved() || tracksGround){
            Vec3 lastVec = entityData.get(LAST_POS);
            return new Vec3(
                    lastVec.x() - radiusMultiplier * Math.sin(nextTick / rotateSpeed + getOffset()),
                    lastVec.y() + (tracksGround ? 0.5 : 0), // Offset if the owner died
                    lastVec.z() - radiusMultiplier * Math.cos(nextTick / rotateSpeed + getOffset()));
        }
        Vec3 lastVec = new Vec3(
                owner.getX() - radiusMultiplier * Math.sin(nextTick / rotateSpeed + getOffset()),
                owner.getY() + 1 - (owner.isShiftKeyDown() ? 0.25 : 0),
                owner.getZ() - radiusMultiplier * Math.cos(nextTick / rotateSpeed + getOffset()));
        entityData.set(LAST_POS, owner.position);

        return lastVec;
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.level.getEntity(this.entityData.get(OWNER_ID));
    }

    @Override
    public boolean canTraversePortals() {
        return false;
    }

    @Override
    public int getExpirationTime() {
        return 60 * 20 + 30 * 20 * extendTimes;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (level.isClientSide || result.getType() == HitResult.Type.MISS)
            return;

        if (result instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity().equals(this.getOwner()) && !tracksGround){
                return;
            }
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, result);
                Networking.sendToNearbyClient(level, BlockPos.containing(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        BlockPos.containing(result.getLocation()), getParticleColor()));
                attemptRemoval();
            }
        } else if (numSensitive > 0 && result instanceof BlockHitResult blockraytraceresult && !this.isRemoved()) {
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(this.level, blockraytraceresult);
            }
            Networking.sendToNearbyClient(level, ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                    BlockPos.containing(result.getLocation()).below(), getParticleColor()));
            attemptRemoval();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OFFSET, 0);
        pBuilder.define(ACCELERATES, 0);
        pBuilder.define(AOE, 0f);
        pBuilder.define(TOTAL, 0);
        pBuilder.define(LAST_POS, Vec3.ZERO);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        tag.putInt("offset", getOffset());
        tag.putFloat("aoe", getAoe());
        tag.putInt("accelerate", getAccelerates());
        tag.putInt("total", getTotal());
        tag.putDouble("lastX", entityData.get(LAST_POS).x);
        tag.putDouble("lastY", entityData.get(LAST_POS).y);
        tag.putDouble("lastZ", entityData.get(LAST_POS).z);
        //tag.putBoolean("canHitOwner", tracksGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        setOffset(tag.getInt("offset"));
        setAoe(tag.getFloat("aoe"));
        setAccelerates(tag.getInt("accelerate"));
        setTotal(tag.getInt("total"));
        entityData.set(LAST_POS, new Vec3(tag.getDouble("lastX"), tag.getDouble("lastY"), tag.getDouble("lastZ")));
        //tracksGround = tag.getBoolean("canHitOwner");
    }


}
