package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.ConfigHandler;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.entity.spells.EntityMagnetSpell;
import alexthw.ars_elemental.registry.ModAdvTriggers;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.util.EntityCarryMEI;
import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;

@EventBusSubscriber(modid = ArsElemental.MODID)
public class GlyphEvents {

    @SubscribeEvent
    public static void empowerGlyphs(EffectResolveEvent.Pre event) {
        if (!ConfigHandler.COMMON.EnableGlyphEmpowering.get()) return;
        if (event.rayTraceResult instanceof BlockHitResult blockHitResult)
            empowerResolveOnBlocks(event, blockHitResult, event.resolver);
        else if (event.rayTraceResult instanceof EntityHitResult entityHitResult)
            empowerResolveOnEntities(event, entityHitResult, event.resolver);
    }

    public GlyphEvents() {
    }

    public static void empowerResolveOnEntities(EffectResolveEvent.Pre event, EntityHitResult entityHitResult, SpellResolver resolver) {

        if (!(entityHitResult.getEntity() instanceof LivingEntity living && event.world instanceof ServerLevel))
            return;

        boolean hasFire = ISchoolFocus.fireCheck(resolver);
        boolean hasWater = ISchoolFocus.waterCheck(resolver);
        boolean hasEarth = ISchoolFocus.earthCheck(resolver);
        boolean hasAir = ISchoolFocus.airCheck(resolver);
        //boolean hasAnima = resolver.hasFocus(ModItems.NECRO_FOCUS.get().getDefaultInstance());

        if (event.resolveEffect == EffectCut.INSTANCE) {
            if (living.hasEffect(ModPotions.LIFE_LINK)) {
                if (living.getEffect(ModPotions.LIFE_LINK) instanceof EntityCarryMEI effect) {
                    if (effect.getOwner() != null) effect.getOwner().removeEffect(ModPotions.LIFE_LINK);
                    if (effect.getTarget() != null) effect.getTarget().removeEffect(ModPotions.LIFE_LINK);
                }
            }
        }


        if (event.resolveEffect == EffectIgnite.INSTANCE && hasFire)
            living.addEffect(new MobEffectInstance(ModPotions.MAGIC_FIRE, 200), living);

        if (event.resolveEffect == EffectLaunch.INSTANCE) {
            if (event.spellStats.getDurationMultiplier() != 0 && hasAir) {
                living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 50 * (1 + (int) event.spellStats.getDurationMultiplier()), (int) event.spellStats.getAmpMultiplier() / 2));
                if (event.shooter instanceof ServerPlayer serverPlayer && !(serverPlayer instanceof FakePlayer))
                    ModAdvTriggers.LEVITATE.get().trigger(serverPlayer);
            }
        }
        if (event.resolveEffect == EffectFreeze.INSTANCE) {
            if (event.shooter != living && hasWater) {
                if (living instanceof Skeleton skeleton && skeleton.getType() == EntityType.SKELETON) {
                    skeleton.setFreezeConverting(true);
                }
                living.setIsInPowderSnow(true);
                int newFrozenTicks = living.getTicksFrozen() + (int) (60 * event.spellStats.getAmpMultiplier());
                living.setTicksFrozen(newFrozenTicks);
                if (living.isFullyFrozen() && living.canFreeze()) {
                    living.forceAddEffect(new MobEffectInstance(ModPotions.FROZEN, 60, 0, false, false, false), living);
                }
            }
        }
        if (event.resolveEffect == EffectGrow.INSTANCE) {
            if (living.getType().is(EntityTypeTags.UNDEAD) && hasEarth && event.shooter instanceof Player) {
                ((IDamageEffect) event.resolveEffect).attemptDamage(event.world, event.shooter, event.spellStats, event.context, event.resolver, living, event.world.damageSources().magic(), (float) (3 + 2 * event.spellStats.getAmpMultiplier()));
                if (living.isDeadOrDying() && event.world.getRandom().nextInt(100) < 20) {
                    BlockPos feet = living.getOnPos();
                    BlockState underfoot = living.level().getBlockState(feet);
                    if ((underfoot.getBlock() == Blocks.MOSS_BLOCK || underfoot.is(BlockTags.DIRT) || underfoot.is(BlockTags.LEAVES)) && event.world.getBlockState(feet.above()).isAir()) {
                        living.level().setBlockAndUpdate(feet.above(), ModItems.GROUND_BLOSSOM.get().defaultBlockState());
                        if (event.shooter instanceof ServerPlayer serverPlayer && !(serverPlayer instanceof FakePlayer))
                            ModAdvTriggers.BLOSSOM.get().trigger(serverPlayer);
                    }
                }
            }
        }
        if (event.resolveEffect == EffectGravity.INSTANCE) {
            if (event.spellStats.hasBuff(AugmentSensitive.INSTANCE) && hasEarth) {
                var magnet = EntityMagnetSpell.createMagnet(event.world, event.shooter, event.spellStats, event.context, event.rayTraceResult.getLocation());
                magnet.setTracked(entityHitResult.getEntity());
                event.world.addFreshEntity(magnet);
                event.setCanceled(true);
            }
        }
    }

    public static void empowerResolveOnBlocks(EffectResolveEvent.Pre event, BlockHitResult blockHitResult, SpellResolver resolver) {

        boolean hasFire = ISchoolFocus.fireCheck(resolver);
        boolean hasWater = ISchoolFocus.waterCheck(resolver);
        boolean hasEarth = ISchoolFocus.earthCheck(resolver);
        //boolean hasAir = ISchoolFocus.airCheck(resolver);

        if (event.resolveEffect == EffectConjureWater.INSTANCE) {
            if (hasWater) {
                if (GlyphEffectUtil.hasFollowingEffect(event.context, EffectFreeze.INSTANCE)) {
                    GlyphEffectUtil.placeBlocks(blockHitResult, event.world, event.shooter, event.spellStats, event.context, new SpellResolver(event.context), Blocks.ICE.defaultBlockState());
                    event.setCanceled(true);
                }
            }
        }

        if (event.resolveEffect == EffectGravity.INSTANCE) {
            if (event.spellStats.hasBuff(AugmentSensitive.INSTANCE) && hasEarth) {
                var magnet = EntityMagnetSpell.createMagnet(event.world, event.shooter, event.spellStats, event.context, event.rayTraceResult.getLocation());
                event.world.addFreshEntity(magnet);
                event.setCanceled(true);
            }
        }

        if (event.resolveEffect == EffectIgnite.INSTANCE && event.shooter instanceof Player player) {
            //break or sublimate the ice
            int pierceBuff = event.spellStats.getBuffCount(AugmentPierce.INSTANCE);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(event.shooter, blockHitResult.getBlockPos(), blockHitResult, event.spellStats.getAoeMultiplier(), pierceBuff);
            BlockState state;

            boolean flag = hasFire && GlyphEffectUtil.hasFollowingEffect(event.context, EffectEvaporate.INSTANCE);

            for (BlockPos pos1 : posList) {
                state = event.world.getBlockState(pos1);
                if (state.getBlock() instanceof IceBlock ice) {
                    if (flag) {
                        event.world.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        ice.playerDestroy(event.world, player, pos1, state, null, ItemStack.EMPTY);
                    }
                    event.setCanceled(true);
                }
            }

        }

    }

}
