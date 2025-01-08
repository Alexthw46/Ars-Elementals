package alexthw.ars_elemental.common.entity;

import alexthw.ars_elemental.common.blocks.mermaid_block.MermaidTile;
import alexthw.ars_elemental.common.entity.ai.*;
import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SummonUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.items.data.ICharmSerializable;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class MermaidEntity extends PathfinderMob implements GeoEntity, IAnimationListener, IDispellable, ICharmSerializable {

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public static final EntityDataAccessor<Boolean> CHANNELING = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> CHANNELING_ENTITY = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> JUMPING = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(MermaidEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private final RawAnimation swim = RawAnimation.begin().thenLoop("swim");
    private final RawAnimation idle = RawAnimation.begin().thenLoop("idle");
    public int channelCooldown;

    public MermaidEntity(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.1F, 0.7F, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public MermaidEntity(Level level, boolean tamed) {
        this(ModEntities.SIREN_ENTITY.get(), level);
        setTamed(tamed);
    }

    @Override
    public float maxUpStep() {
        return super.maxUpStep() + 0.5F;
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> p_149138_) {
        return MermaidAi.makeBrain((Brain<MermaidEntity>) this.brainProvider().makeBrain(p_149138_));
    }

    @Override
    public @NotNull Brain<MermaidEntity> getBrain() {
        return (Brain<MermaidEntity>) super.getBrain();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new HybridStrollGoal(this, 1.0f, 40));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6F));
        goalSelector.addGoal(4, new DolphinJumpGoal(this, 10));
        goalSelector.addGoal(5, new GoBackHomeGoal(this, this::getHome, 20, () -> this.getHome() != null));
        goalSelector.addGoal(8, new FollowBoatGoalM(this, () -> this.getHome() == null));
        goalSelector.addGoal(5, new MermaidChannelGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        // reduce cooldown by 1 every tick if it's greater than 0 (and not on client)
        if (!level().isClientSide && channelCooldown > 0)
            channelCooldown--;
        SummonUtil.healOverTime(this);

        // if the entity is named Jeb_ , set the color to a random variant every 10 ticks
        if (!level().isClientSide && level().getGameTime() % 10 == 0 && this.getName().getString().toLowerCase(Locale.ROOT).equals("jeb_")) {
            this.entityData.set(COLOR, MermaidEntity.Variants.random().toString());
        }

        // if the entity is channeling, spawn particles around the entity
        if (level().isClientSide && isChanneling() && getChannelEntity() != -1) {
            Entity entity = level().getEntity(getChannelEntity());
            if (entity == null || entity.isRemoved())
                return;
            Vec3 vec = entity.position();
            level().addParticle(GlowParticleData.createData(MermaidTile.shrineParticle),
                    (float) vec.x - Math.sin(ClientInfo.ticksInGame / 8D),
                    (float) vec.y + Math.sin(ClientInfo.ticksInGame / 5d) / 8D + 0.5,
                    (float) vec.z - Math.cos(ClientInfo.ticksInGame / 8D),
                    0, 0, 0);
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        // drop charm on death if tamed
        if (!level().isClientSide && isTamed()) {
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), ModItems.SIREN_CHARM.get().getDefaultInstance()));
        }
        super.die(source);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float p_70097_2_) {
        return SummonUtil.canSummonTakeDamage(source) && super.hurt(source, p_70097_2_);
    }

    //Dolphin inheritance
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    public int getMaxAirSupply() {
        return 6000;
    }

    public boolean removeWhenFarAway(double dist) {
        return !isTamed();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

//    @Override
//    public @NotNull MobType getMobType() {
//        return MobType.WATER;
//    }

    @Override
    public int getBaseExperienceReward() {
        return 0;
    }

    //IDispellable
    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level().isClientSide && isTamed()) {
            ItemStack stack = new ItemStack(ModItems.SIREN_CHARM.get());
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level(), blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return this.isTamed();
    }

    //gecko stuff
    AnimationController<MermaidEntity> actions;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "idle", 0, this::idle));
        actions = new AnimationController<>(this, "actions", 10, e -> e.setAndContinue(getDeltaMovement().length() > 0 || level().isClientSide && PatchouliHandler.isPatchouliWorld() ? swim : idle));
        data.add(actions);
    }

    private PlayState idle(AnimationState<MermaidEntity> event) {
        PlayState result = PlayState.CONTINUE;
        if (level().isClientSide && PatchouliHandler.isPatchouliWorld()) {
            event.setAndContinue(RawAnimation.begin().thenLoop("ground"));
        } else if (isJumping()) {
            event.setAndContinue(RawAnimation.begin().thenLoop("jump"));
        } else if (getDeltaMovement().y > 0.3) {
            setJump(true);
            event.setAndContinue(RawAnimation.begin().thenLoop("jump"));
        } else if (onGround() && !isInWater()) {
            event.setAndContinue(RawAnimation.begin().thenLoop("ground"));
        } else {
            event.setAndContinue(RawAnimation.begin().thenLoop("floating"));
        }
        return result;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }


    //data stuff

    public static AttributeSupplier createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.FOLLOW_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .build();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOME, Optional.empty());
        builder.define(TAMED, false);
        builder.define(JUMPING, false);
        builder.define(COLOR, Variants.random().toString());
        builder.define(CHANNELING, false);
        builder.define(CHANNELING_ENTITY, -1);
    }

    public boolean isJumping() {
        return this.entityData.get(JUMPING);
    }

    public void setJump(boolean is) {
        this.entityData.set(JUMPING, is);
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
    }

    public void setHome(BlockPos home) {
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable
    BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    public @Nullable MermaidTile getShrine() {
        BlockPos homePos = getHome();
        if (homePos == null || !(level().getBlockEntity(homePos) instanceof MermaidTile shrine))
            return null;
        return shrine;
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putString("color", this.entityData.get(COLOR));
        tag.putBoolean("channeling", this.entityData.get(CHANNELING));
        tag.putInt("cooldown", channelCooldown);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (NBTUtil.hasBlockPos(tag, "home"))
            this.entityData.set(HOME, Optional.of(NBTUtil.getBlockPos(tag, "home")));
        setTamed(tag.getBoolean("tamed"));
        this.entityData.set(COLOR, tag.getString("color"));
        channelCooldown = tag.getInt("cooldown");
    }

    public int getMaxSpawnClusterSize() {
        return 3;
    }

    public static boolean checkSurfaceWaterAnimalSpawnRules(LevelAccessor levelAccessor, BlockPos pos) {
        int i = levelAccessor.getSeaLevel() + 15;
        int j = i - 30;
        boolean f1 = pos.getY() >= j && pos.getY() <= i;
        boolean beach = (levelAccessor.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) || levelAccessor.getBlockState(pos.below()).is(Blocks.SAND) || levelAccessor.getBlockState(pos.below()).is(Blocks.SANDSTONE)) && levelAccessor.getRawBrightness(pos, 0) > 8;
        boolean sea = levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER) && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER);
        return f1 && (beach || sea);
    }

    //Pathfinder
    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new MermaidPathNavigation<>(this, pLevel);
    }

    @Override
    public void startAnimation(int arg) {

    }

    public boolean isChanneling() {
        return this.entityData.get(CHANNELING);
    }

    public void setChanneling(boolean channeling) {
        this.entityData.set(CHANNELING, channeling);
    }

    public int getChannelEntity() {
        return this.entityData.get(CHANNELING_ENTITY);
    }

    public void setChannelingEntity(int entityID) {
        this.entityData.set(CHANNELING_ENTITY, entityID);
    }


    public static class MermaidPathNavigation<E extends PathfinderMob> extends WaterBoundPathNavigation {
        public MermaidPathNavigation(E p_149218_, Level p_149219_) {
            super(p_149218_, p_149219_);
        }

        /**
         * If on ground or swimming and can swim
         */
        protected boolean canUpdatePath() {
            return true;
        }

        protected @NotNull PathFinder createPathFinder(int p_149222_) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new PathFinder(this.nodeEvaluator, p_149222_);
        }

        public boolean isStableDestination(BlockPos p_149224_) {
            return !this.level.getBlockState(p_149224_.below()).isAir();
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }

    int animTicks;
    boolean taming;

    @Override
    protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel) this.level(), this);
        MermaidAi.updateActivity(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isJumping() && animTicks <= 30) {
            animTicks++;
        }
        // check if the animation is done
        if (animTicks > 30) {
            setJump(false);
            animTicks = 0;
            // if it's being tamed, spawn shards and award achievement
            if (!isTamed() && taming) {
                taming = false;
                ItemStack stack = new ItemStack(ModItems.SIREN_SHARDS.get(), 1 + level().random.nextInt(2));
                level().addFreshEntity(new ItemEntity(level(), getX(), getY() + 0.5, getZ(), stack));
                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.POOF_MOB.get(), (ServerLevel) this.level(), this.getOnPos(), 10);
                this.remove(RemovalReason.DISCARDED);
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1f, 1f);
            }
        }
    }

    //Variants

    public @NotNull InteractionResult interactAt(@NotNull Player pPlayer, @NotNull Vec3 pVec, @NotNull InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || pPlayer.getCommandSenderWorld().isClientSide)
            return InteractionResult.PASS;

        ItemStack stack = pPlayer.getItemInHand(hand);

        // if the player is holding a dye, change the mermaid's color
        if (isTamed()) {
            String color = Variants.getColorFromStack(stack);
            if (color != null && !getColor().equals(color)) {
                this.setColor(color);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.getItem() == Items.SEA_PICKLE && !taming) {
            this.setDeltaMovement(getDeltaMovement().add(0, 0.8, 0));
            this.setJump(true);
            this.markHurt();
            taming = true;
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(pPlayer, pVec, hand);
    }

    static final RandomSource mermaidRandom = RandomSource.createNewThreadLocalInstance();

    public enum Variants {
        KELP,
        BUBBLE,
        FIRE,
        TUBE,
        HORN,
        BRAIN;

        public static String getColorFromStack(ItemStack stack) {

            if (stack.getItem() == Items.KELP) return KELP.toString();
            if (stack.getItem() == Items.BUBBLE_CORAL) return BUBBLE.toString();
            if (stack.getItem() == Items.HORN_CORAL) return HORN.toString();
            if (stack.getItem() == Items.TUBE_CORAL) return TUBE.toString();
            if (stack.getItem() == Items.FIRE_CORAL) return FIRE.toString();
            if (stack.getItem() == Items.BRAIN_CORAL) return BRAIN.toString();

            return null;
        }

        public static Variants random() {
            // create a map of the enum values and their ordinal values to get a random one
            Map<Integer, Variants> ordinalMap = Arrays.stream(Variants.values()).collect(Collectors.toMap(Enum::ordinal, var -> var, (a, b) -> b));

            return ordinalMap.get(mermaidRandom.nextInt(ordinalMap.size()));
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        setColor(data.color());
        setCustomName(data.name());
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
    }

    public ResourceLocation getTexture() {
        return prefix("textures/entity/mermaid_" + (getColor().isEmpty() ? Variants.KELP.toString() : getColor()) + ".png");
    }

}
