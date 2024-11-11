package alexthw.ars_elemental.common.entity.mages;

import alexthw.ars_elemental.ConfigHandler;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.api.item.ISchoolProvider;
import alexthw.ars_elemental.common.entity.ai.ProjCastingGoal;
import alexthw.ars_elemental.common.entity.ai.SelfCastGoal;
import alexthw.ars_elemental.common.items.armor.ArmorSet;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHarm;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHeal;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static alexthw.ars_elemental.util.ParticleUtil.schoolToColor;

public class EntityMageBase extends Monster implements RangedAttackMob, ISchoolProvider {

    public final List<Spell> pSpells = new ArrayList<>();
    public final List<Spell> sSpells = new ArrayList<>();

    public SpellSchool school;

    public int castCooldown = 0;

    /**
     * Default Proj -> simple harm
     * Default Self -> simple heal
     */
    protected EntityMageBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectHarm.INSTANCE, AugmentAmplify.INSTANCE));
        sSpells.add(new Spell(MethodSelf.INSTANCE, EffectHeal.INSTANCE, AugmentAmplify.INSTANCE));
    }

    @Override
    public void tick() {
        super.tick();
        if (castCooldown > 0) castCooldown--;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EntityMageBase.class, true, (e) -> e instanceof EntityMageBase mage && school != mage.school));
        if (ConfigHandler.Common.MAGES_AGGRO.get()) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, (e) -> (e instanceof Player player && ISchoolFocus.hasFocus(player) != school)));
        }
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Monster.class, true, (e) -> !(e instanceof EntityMageBase)));
        this.goalSelector.addGoal(3, new ProjCastingGoal<>(this, 1.0d, 30, 64f, () -> castCooldown <= 0, 0, 10));
        this.goalSelector.addGoal(6, new SelfCastGoal<>(this, 20, 0, () -> (castCooldown <= 0 && (getHealth() < getMaxHealth() / 4)), 0, 10));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));

        super.registerGoals();
    }



    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        if (school != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                setItemSlot(slot, getArmorForSlot(slot, this.school));
            }
        } else {
            setItemSlot(EquipmentSlot.HEAD, ItemsRegistry.BATTLEMAGE_HOOD.get().getDefaultInstance());
            setItemSlot(EquipmentSlot.CHEST, ItemsRegistry.BATTLEMAGE_ROBES.get().getDefaultInstance());
            setItemSlot(EquipmentSlot.LEGS, ItemsRegistry.BATTLEMAGE_LEGGINGS.get().getDefaultInstance());
            setItemSlot(EquipmentSlot.FEET, ItemsRegistry.BATTLEMAGE_BOOTS.get().getDefaultInstance());
        }
        setItemInHand(InteractionHand.MAIN_HAND, ItemsRegistry.APPRENTICE_SPELLBOOK.get().getDefaultInstance());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pDistanceFactor) {
        Spell spell = this.pSpells.get(random.nextInt(pSpells.size()));
        ParticleColor color = schoolToColor(this.school.getId());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level(), spell, this, new LivingCaster(this)).withColors(color));
        resolver.onCast(ItemStack.EMPTY, level());
        this.castCooldown = 40;
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("cast", castCooldown);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.castCooldown = tag.getInt("cast");
    }

    //Monster overrides

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity pEntity) {
        return super.isAlliedTo(pEntity) || school.equals(ISchoolFocus.hasFocus(pEntity));
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public int getBaseExperienceReward() {
        return 15;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel serverLevel, @NotNull DamageSource source, boolean wasRecentlyHit) {

    }

    public static ItemStack getArmorForSlot(EquipmentSlot slot, SpellSchool school) {
        Item item = switch (slot) {
            case HEAD -> getArmorSetFromElement(school).getHat();
            case CHEST -> getArmorSetFromElement(school).getChest();
            case LEGS -> getArmorSetFromElement(school).getLegs();
            case FEET -> getArmorSetFromElement(school).getBoots();
            default -> null;
        };

        if (item == null) return ItemStack.EMPTY;

        return item.getDefaultInstance();
    }

    private static ArmorSet getArmorSetFromElement(SpellSchool school) {
        return switch (school.getId()) {
            case "fire" -> ModItems.FIRE_ARMOR;
            case "water" -> ModItems.WATER_ARMOR;
            case "earth" -> ModItems.EARTH_ARMOR;
            case "air" -> ModItems.AIR_ARMOR;
            default -> new ArmorSet("necro", SpellSchools.NECROMANCY);
        };
    }

    @Override
    public SpellSchool getSchool() {
        return school;
    }
}
