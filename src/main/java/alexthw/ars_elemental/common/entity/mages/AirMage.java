package alexthw.ars_elemental.common.entity.mages;

import alexthw.ars_elemental.common.glyphs.EffectDischarge;
import alexthw.ars_elemental.common.glyphs.EffectSpark;
import alexthw.ars_elemental.common.glyphs.MethodArcProjectile;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AirMage extends EntityMageBase {


    /**
     * SelfSpells
     * 0: Leap towards the enemy
     * 1: To use after leap, launch and shear enemies
     * <p>
     * ProjSpells
     * 1: Bouncing Lightning
     */
    public AirMage(EntityType<? extends EntityMageBase> p_21368_, Level level) {
        super(p_21368_, level);
        this.school = SpellSchools.ELEMENTAL_AIR;
        sSpells.add(new Spell(MethodSelf.INSTANCE, EffectLeap.INSTANCE, AugmentAmplify.INSTANCE, EffectBounce.INSTANCE));
        sSpells.add(new Spell(MethodSelf.INSTANCE, EffectOrbit.INSTANCE, EffectLaunch.INSTANCE, EffectDelay.INSTANCE, EffectWindshear.INSTANCE, EffectGravity.INSTANCE, AugmentExtendTime.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectLaunch.INSTANCE, AugmentDurationDown.INSTANCE, EffectDelay.INSTANCE, EffectWindshear.INSTANCE));
        pSpells.add(new Spell(MethodArcProjectile.INSTANCE, AugmentPierce.INSTANCE, AugmentPierce.INSTANCE, EffectLightning.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectSpark.INSTANCE, AugmentAmplify.INSTANCE, EffectDischarge.INSTANCE));
    }

    public AirMage(Level level) {
        this(ModEntities.AIR_MAGE.get(), level);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        ItemStack book = this.getItemInHand(InteractionHand.MAIN_HAND);
        book.set(DataComponents.BASE_COLOR, DyeColor.WHITE);
    }
}
