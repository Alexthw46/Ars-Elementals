package alexthw.ars_elemental.common.entity.mages;

import alexthw.ars_elemental.common.glyphs.EffectConjureTerrain;
import alexthw.ars_elemental.common.glyphs.EffectEnvenom;
import alexthw.ars_elemental.common.glyphs.EffectSpike;
import alexthw.ars_elemental.common.glyphs.EffectSpores;
import alexthw.ars_elemental.registry.ModEntities;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBurst;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLeap;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSnare;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
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

public class EarthMage extends EntityMageBase {

    /**
     * ProjSpells
     * <p>
     * 1: Envenom
     * 2: Burst Spike
     * 3: Crush
     * 4: Snare Spike
     * 5: Shoot Terrain
     */
    public EarthMage(EntityType<? extends EntityMageBase> p_21368_, Level level) {
        super(p_21368_, level);
        this.school = SpellSchools.ELEMENTAL_EARTH;
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectEnvenom.INSTANCE, EffectSpores.INSTANCE, AugmentAmplify.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectBurst.INSTANCE, AugmentSensitive.INSTANCE, EffectSpike.INSTANCE, AugmentAmplify.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectCrush.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE));
        pSpells.add(new Spell(MethodProjectile.INSTANCE, EffectSnare.INSTANCE, AugmentExtendTime.INSTANCE, EffectSpike.INSTANCE));

        pSpells.add(new Spell(MethodPantomime.INSTANCE, EffectConjureTerrain.INSTANCE, EffectLeap.INSTANCE));
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        ItemStack book = this.getItemInHand(InteractionHand.MAIN_HAND);
        book.set(DataComponents.BASE_COLOR, DyeColor.GREEN);
    }

    public EarthMage(Level level) {
        this(ModEntities.EARTH_MAGE.get(), level);
    }
}
