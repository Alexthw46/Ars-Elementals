package alexthw.ars_elemental.common.glyphs;

import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectConjureWater;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class EffectConjureTerrain extends ElementalAbstractEffect {

    public static EffectConjureTerrain INSTANCE = new EffectConjureTerrain();

    private EffectConjureTerrain() {
        super("conjure_terrain", "Conjure Terrain");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        // Get the number of Amplify Augments in the spell and use that to determine the block to place
        int amps = spellStats.getBuffCount(AugmentAmplify.INSTANCE);
        BlockState toPlace = switch (amps){
            case 1 -> Blocks.COBBLESTONE.defaultBlockState();
            case 2 -> Blocks.COBBLED_DEEPSLATE.defaultBlockState();
            default -> Blocks.DIRT.defaultBlockState();
        };
        // If the spell contains a Conjure Water effect, place mud instead, and if it contains a Crush effect, place sand instead
        if (spellContext.hasNextPart()) {
            while (spellContext.hasNextPart()) {
                AbstractSpellPart next = spellContext.nextPart();
                if (next instanceof AbstractEffect) {
                    if (next == EffectConjureWater.INSTANCE) {
                        toPlace = Blocks.MUD.defaultBlockState();
                    } else if (next == EffectCrush.INSTANCE) {
                        // If the spell contains a Crush effect with an Amplify augment, place sandstone instead
                        toPlace = amps > 0 ? Blocks.SANDSTONE.defaultBlockState() : Blocks.SAND.defaultBlockState();
                    } else if (next == EffectSmelt.INSTANCE && amps > 0) {
                        // If the spell contains a Smelt effect with an Amplify augment, place deepslate instead
                        toPlace = amps > 1 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
                    } else {
                        spellContext.setCurrentIndex(spellContext.getCurrentIndex() - 1);
                    }
                    break;
                }
            }
        }
        GlyphEffectUtil.placeBlocks(rayTraceResult, world, shooter, spellStats, spellContext, resolver, toPlace);
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentAmplify.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION, SpellSchools.ELEMENTAL_EARTH);
    }

    @Override
    public String getBookDescription() {
        return "Places terrain block at a location. Can place more blocks if augmented with AoE or Pierce";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Cobblestone will be used instead of Dirt. Deepslate will be used if added again.");
    }
}
