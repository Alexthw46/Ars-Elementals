package alexthw.ars_elemental.common.entity.familiars;

import alexthw.ars_elemental.common.entity.FirenandoEntity.Variants;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellModifierEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.entity.familiar.ISpellCastListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.*;

import java.util.ArrayList;
import java.util.List;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class FirenandoFamiliar extends FamiliarEntity implements ISpellCastListener {
    public FirenandoFamiliar(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public FirenandoFamiliar(Level world) {
        super(ModEntities.FIRENANDO_FAMILIAR.get(), world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        super.registerControllers(data);
        data.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate));
    }

    public static List<AbstractSpellPart> projectileGlyphs = new ArrayList<>();

    public void onModifier(SpellModifierEvent event) {
        // as long as the familiar is alive and the owner is the caster, the familiar will increase the damage of fire spells by 2 and reduce the mana cost of projectiles by 20%
        if (this.isAlive() && this.getOwner() != null && this.getOwner().equals(event.caster)) {
            if (SpellSchools.ELEMENTAL_FIRE.isPartOfSchool(event.spellPart)) {
                event.builder.addDamageModifier(2.0D);
            }
        }
    }

    @Override
    public void onCostCalc(SpellCostCalcEvent event) {
        if (this.isAlive())
            if (this.getOwner() != null && this.getOwner().equals(event.context.getUnwrappedCaster())) {
                if (projectileGlyphs.contains(event.context.getSpell().unsafeList().getFirst())) {
                    event.currentCost = (int) (event.currentCost - (event.context.getSpell().getCost() * 0.5));
                }
            }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide && player.equals(getOwner())) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == Items.MAGMA_CREAM) {
                stack.shrink(1);
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200));
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() == Blocks.MAGMA_BLOCK.asItem() && !getColor().equals(Variants.MAGMA.toString())) {
                setColor(Variants.MAGMA.toString());
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() == Blocks.SOUL_SAND.asItem() && !getColor().equals(Variants.SOUL.toString())) {
                setColor(Variants.SOUL.toString());
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    public PlayState idlePredicate(AnimationState<FirenandoFamiliar> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle.body"));
    }

    public ResourceLocation getTexture() {
        return prefix("textures/entity/firenando_" + (getColor().isEmpty() ? Variants.MAGMA.toString() : getColor()) + ".png");
    }

}
