package alexthw.ars_elemental.common.entity.summon;

import alexthw.ars_elemental.api.IUndeadSummon;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.EntityAllyVex;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

public class AllyVhexEntity extends EntityAllyVex implements IUndeadSummon {
    public AllyVhexEntity(EntityType<? extends Vex> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
    }

    public AllyVhexEntity(Level p_i50190_2_, LivingEntity owner) {
        super(p_i50190_2_, owner);
    }

    public AllyVhexEntity(Level world, EntityAllyVex oldVex, Player player) {
        this(world,player);

        moveTo(oldVex.blockPosition(), 0.0F, 0.0F);
        finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(oldVex.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        setOwner(player);
        setBoundOrigin(oldVex.getBoundOrigin());
        setLimitedLife(50+oldVex.getTicksLeft());
        //setLimitedLife(50+oldVex.serializeNBT().getInt("LifeTicks"));

        oldVex.getActiveEffects().stream().filter(e -> e.getEffect().value().isBeneficial()).forEach(this::addEffect);
    }

    public EntityType<?> getType() {
        return ModEntities.VHEX_SUMMON.get();
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.6F);
    }

    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

}
