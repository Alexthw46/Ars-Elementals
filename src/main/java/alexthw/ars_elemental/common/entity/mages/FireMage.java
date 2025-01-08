package alexthw.ars_elemental.common.entity.mages;

import alexthw.ars_elemental.common.glyphs.EffectConflagrate;
import alexthw.ars_elemental.common.glyphs.MethodArcProjectile;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectExplosion;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFirework;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFlare;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectIgnite;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FireMage extends EntityMageBase {

    /**
     * ProjSpells
     * 1: Flare bolt
     * 2: Bouncing Firework
     * 3: Homing Explosion
     */
    public FireMage(EntityType<? extends EntityMageBase> type, Level level) {
        super(type, level);
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectIgnite.INSTANCE, EffectFlare.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectIgnite.INSTANCE, EffectConflagrate.INSTANCE));
        pSpells.add(new Spell(MethodArcProjectile.INSTANCE, AugmentSplit.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentPierce.INSTANCE, EffectFirework.INSTANCE, AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE));
        pSpells.add(new Spell(MethodHomingProjectile.INSTANCE, AugmentSensitive.INSTANCE, EffectExplosion.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE));
        this.school = SpellSchools.ELEMENTAL_FIRE;
    }

    public FireMage(Level level) {
        this(ModEntities.FIRE_MAGE.get(), level);
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        ItemStack book = this.getItemInHand(InteractionHand.MAIN_HAND);
        book.set(DataComponents.BASE_COLOR, DyeColor.RED);
    }

}
