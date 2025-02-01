package alexthw.ars_elemental.common.entity.summon;

import alexthw.ars_elemental.api.IUndeadSummon;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonHorse;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE;


public class SummonSkeleHorse extends SummonHorse implements IUndeadSummon {
    public SummonSkeleHorse(EntityType<? extends Horse> type, Level worldIn) {
        super(type, worldIn);
    }

    public SummonSkeleHorse(Level level) {
        this(ModEntities.SKELEHORSE_SUMMON.get(), level);
    }

    public SummonSkeleHorse(SummonHorse oldHorse, Player summoner){
        this(summoner.level());
        BlockPos position = oldHorse.blockPosition();
        setPos(position.getX(), position.getY(), position.getZ());
        ticksLeft = oldHorse.getTicksLeft();
        tameWithName(summoner);
        getHorseInventory().setItem(0, new ItemStack(Items.SADDLE));
        setOwnerID(summoner.getUUID());
        setDropChance(EquipmentSlot.CHEST, 0.0F);
        oldHorse.getActiveEffects().stream().filter(e -> e.getEffect().value().isBeneficial()).forEach(this::addEffect);
    }

    protected @NotNull SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return this.isEyeInFluidType(WATER_TYPE.value()) ? SoundEvents.SKELETON_HORSE_AMBIENT_WATER : SoundEvents.SKELETON_HORSE_AMBIENT;
    }

    protected @NotNull SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.SKELETON_HORSE_DEATH;
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource p_30916_) {
        super.getHurtSound(p_30916_);
        return SoundEvents.SKELETON_HORSE_HURT;
    }

    protected @NotNull SoundEvent getSwimSound() {
        if (this.onGround()) {
            if (!this.isVehicle()) {
                return SoundEvents.SKELETON_HORSE_STEP_WATER;
            }

            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                return SoundEvents.SKELETON_HORSE_GALLOP_WATER;
            }

            if (this.gallopSoundCounter <= 5) {
                return SoundEvents.SKELETON_HORSE_STEP_WATER;
            }
        }

        return SoundEvents.SKELETON_HORSE_SWIM;
    }

    protected void playSwimSound(float p_30911_) {
        if (this.onGround()) {
            super.playSwimSound(0.3F);
        } else {
            super.playSwimSound(Math.min(0.1F, p_30911_ * 25.0F));
        }

    }

    protected void playJumpSound() {
        if (this.isInWater()) {
            this.playSound(SoundEvents.SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
        } else {
            super.playJumpSound();
        }

    }

    @Override
    public boolean dismountsUnderwater() {
        return false;
    }

    protected float getWaterSlowDown() {
        return 0.96F;
    }

}
