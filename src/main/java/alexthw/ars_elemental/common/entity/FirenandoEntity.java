package alexthw.ars_elemental.common.entity;

import alexthw.ars_elemental.ConfigHandler.Common;
import alexthw.ars_elemental.api.item.ISchoolProvider;
import alexthw.ars_elemental.common.entity.ai.FireCannonGoal;
import alexthw.ars_elemental.common.glyphs.EffectConflagrate;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.EntityHomingProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.items.data.ICharmSerializable;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectIgnite;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class FirenandoEntity extends PathfinderMob implements ISchoolProvider, RangedAttackMob, GeoEntity, ITooltipProvider, IAnimationListener, IWandable, IDispellable, ICharmSerializable {

    private final RawAnimation idle = RawAnimation.begin().thenLoop("idle.body");
    private final RawAnimation inactive = RawAnimation.begin().thenPlayAndHold("inactive");
    private final RawAnimation mainIdle = RawAnimation.begin().thenLoop("idle");

    public FirenandoEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public FirenandoEntity(Level world) {
        super(ModEntities.FIRENANDO_ENTITY.get(), world);
    }

    private int castCooldown = 0;
    private final ParticleColor color = new ParticleColor(250, 50, 15);
    private final ParticleColor colorAlt = new ParticleColor(15, 100, 200);
    public final Spell spell = new Spell(MethodHomingProjectile.INSTANCE, EffectIgnite.INSTANCE, AugmentSensitive.INSTANCE, EffectConflagrate.INSTANCE, AugmentAmplify.INSTANCE, EffectKnockback.INSTANCE);
    public UUID owner;

    @Override
    public boolean isAlliedTo(@NotNull Entity pEntity) {
        return !(pEntity instanceof Enemy) || super.isAlliedTo(pEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if (castCooldown > 0) {
            this.castCooldown--;
        }
        if (!level().isClientSide() && level().getGameTime() % 20 == 0 && this.isActive() && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0f);
        }
    }


    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypeTags.IS_FIRE))
            return false;
        if (!isActive()) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isNoAi() {
        return super.isNoAi() || !isActive();
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (!level().isClientSide && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.entityData.set(ACTIVE, false);
            if (source.getEntity() != null) {
                if (Common.FIRENANDO_KILL.get() && source.getEntity().getUUID().equals(getOwner())) {
                    level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ModItems.FIRENANDO_CHARM.get())));
                    super.die(source);
                    return;
                }

                if (source.getEntity() instanceof Mob mob) {
                    mob.setTarget(null);
                }
            }
            this.setHealth(0.1F);
            return;
        }
        super.die(source);
    }

    @Override
    public void fromCharmData(PersistentFamiliarData data) {
        setColor(data.color());
        setCustomName(data.name());
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved() || level().isClientSide) return false;

        level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ModItems.FIRENANDO_CHARM.get())));
        ParticleUtil.spawnPoof((ServerLevel) level(), blockPosition());
        this.remove(RemovalReason.DISCARDED);

        return true;
    }


    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float pVelocity) {
        ParticleColor spellColor = getColor().equals(Variants.MAGMA.toString()) ? color : colorAlt;
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level(), spell, this, new LivingCaster(this)).withColors(spellColor));
        EntityHomingProjectileSpell projectileSpell = new EntityHomingProjectileSpell(level(), resolver);
        List<Predicate<LivingEntity>> ignore = MethodHomingProjectile.basicIgnores(this, false, resolver.spell);
        ignore.add(entity -> !(entity instanceof Enemy));
        ignore.add(entity -> entity instanceof FirenandoEntity firenando && getOwner().equals(firenando.getOwner()));
        projectileSpell.setColor(spellColor);
        projectileSpell.shoot(this, this.getXRot(), this.getYRot(), 0.0F, 0.8f, 0.8f);
        projectileSpell.setIgnored(ignore);
        level().addFreshEntity(projectileSpell);
        this.castCooldown = 20;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new GoBackHomeGoal(this, this::getHome, 12, () -> this.getTarget() == null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, false, (entity) -> entity instanceof Enemy));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new FireCannonGoal(this, 0.8d, 20, 55f, () -> castCooldown <= 0 && isActive(), Animations.SHOOT.ordinal(), 20));
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ARMOR, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    public static final EntityDataAccessor<Optional<BlockPos>> HOME = SynchedEntityData.defineId(FirenandoEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(FirenandoEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(FirenandoEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(FirenandoEntity.class, EntityDataSerializers.STRING);

    @Override
    public void onFinishedConnectionFirst(@Nullable GlobalPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            setHome(storedPos.pos());
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.home_set"));
        }
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public void setHome(BlockPos home) {
        this.entityData.set(HOME, Optional.of(home));
    }

    public @Nullable
    BlockPos getHome() {
        return this.entityData.get(HOME).orElse(null);
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOTING, false);
        builder.define(ACTIVE, true);
        builder.define(HOME, Optional.empty());
        builder.define(COLOR, Variants.MAGMA.toString());
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        NBTUtil.storeBlockPos(tag, "home", getHome());
        tag.putInt("cast", castCooldown);
        tag.putBoolean("active", this.entityData.get(ACTIVE));
        tag.putString("color", this.entityData.get(COLOR));
        if (owner != null) tag.putUUID("owner", owner);

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (NBTUtil.hasBlockPos(tag, "home")) {
            setHome(NBTUtil.getNullablePos(tag, "home"));
        }
        this.castCooldown = tag.getInt("cast");
        this.entityData.set(ACTIVE, tag.getBoolean("active"));
        this.entityData.set(COLOR, tag.getString("color"));
        this.owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
    }

    final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    AnimationController<FirenandoEntity> attackController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "idle_controller", 5, event -> isActive() ? event.setAndContinue(idle) : event.setAndContinue(inactive)));
        attackController = new AnimationController<>(this, "attack_controller", 1, this::attackPredicate);
        data.add(attackController);
    }

    PlayState attackPredicate(AnimationState<FirenandoEntity> event) {
        if (!isActive()) return PlayState.STOP;
        if (attackController.getCurrentAnimation() == null) {
           return event.setAndContinue(mainIdle);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        if (arg == Animations.SHOOT.ordinal()) {
            if (attackController.getCurrentAnimation() != null && (attackController.getCurrentAnimation().animation().name().equals("shoot"))) {
                return;
            }
            attackController.forceAnimationReset();
            attackController.setAnimation(RawAnimation.begin().thenPlay("shoot").thenLoop("idle"));
        }
    }

    public void setOwner(Player player) {
        this.owner = player.getUUID();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (getHome() != null) {
            String home = getHome().getX() + ", " + getHome().getY() + ", " + getHome().getZ();
            tooltip.add(Component.translatable("ars_nouveau.weald_walker.home", home));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.weald_walker.home", Component.translatable("ars_nouveau.nothing").getString()));
        }
    }

    @Override
    public SpellSchool getSchool() {
        return SpellSchools.ELEMENTAL_FIRE;
    }

    public enum Animations {
        SHOOT
    }

    public enum Variants {
        MAGMA,
        SOUL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide && player.getUUID().equals(owner)) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.getItem() == Blocks.MAGMA_BLOCK.asItem() && !getColor().equals(Variants.MAGMA.toString())) {
                this.setColor(Variants.MAGMA.toString());
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() == Blocks.SOUL_SAND.asItem() && !getColor().equals(Variants.SOUL.toString())) {
                this.setColor(Variants.SOUL.toString());
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        if (!player.level().isClientSide() && !isActive()) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.getItem() == Items.MAGMA_CREAM || stack.getItem() == Items.BLAZE_POWDER) {
                this.heal(100);
                this.entityData.set(ACTIVE, true);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    public ResourceLocation getTexture() {
        if (!isActive()) return prefix("textures/entity/firenando_inactive.png");
        return prefix("textures/entity/firenando_" + (getColor().isEmpty() ? Variants.MAGMA.toString() : getColor()) + ".png");
    }
}
