package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.ConfigHandler;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.blocks.ElementalSpellTurretTile;
import alexthw.ars_elemental.common.entity.spells.EntityMagnetSpell;
import alexthw.ars_elemental.registry.ModAdvTriggers;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.util.EntityCarryMEI;
import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.hollingsworth.arsnouveau.api.spell.SpellSchools.*;

@Mod.EventBusSubscriber(modid = ArsElemental.MODID)
public class GlyphEvents {

    @SubscribeEvent
    public static void empowerGlyphs(EffectResolveEvent.Pre event) {
        if (!ConfigHandler.COMMON.EnableGlyphEmpowering.get()) return;
        SpellSchool school = event.context.getCaster() instanceof TileCaster tc && tc.getTile() instanceof ElementalSpellTurretTile turret ? turret.getSchool() : ISchoolFocus.hasFocus(event.shooter);
        if (event.rayTraceResult instanceof BlockHitResult blockHitResult)
            empowerResolveOnBlocks(event, blockHitResult, school);
        else if (event.rayTraceResult instanceof EntityHitResult entityHitResult)
            empowerResolveOnEntities(event, entityHitResult, school);
    }

    public static void empowerResolveOnEntities(EffectResolveEvent.Pre event, EntityHitResult entityHitResult, SpellSchool school) {

        if (!(entityHitResult.getEntity() instanceof LivingEntity living && event.world instanceof ServerLevel))
            return;

        if (event.resolveEffect == EffectCut.INSTANCE) {
            if (living.hasEffect(ModPotions.LIFE_LINK.get())) {
                if (living.getEffect(ModPotions.LIFE_LINK.get()) instanceof EntityCarryMEI effect) {
                    if (effect.getOwner() != null) effect.getOwner().removeEffect(ModPotions.LIFE_LINK.get());
                    if (effect.getTarget() != null) effect.getTarget().removeEffect(ModPotions.LIFE_LINK.get());
                }
            }
        }

        if (event.resolveEffect == EffectIgnite.INSTANCE) {
            if (event.shooter != living && school == ELEMENTAL_FIRE)
                living.forceAddEffect(new MobEffectInstance(ModPotions.HELLFIRE.get(), 200, (int) event.spellStats.getAmpMultiplier() / 2), living);
        }
        if (event.resolveEffect == EffectLaunch.INSTANCE) {
            if (event.spellStats.getDurationMultiplier() != 0 && school == ELEMENTAL_AIR) {
                living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 50 * (1 + (int) event.spellStats.getDurationMultiplier()), (int) event.spellStats.getAmpMultiplier() / 2));
                if (event.shooter instanceof ServerPlayer serverPlayer && !(serverPlayer instanceof FakePlayer)) ModAdvTriggers.LEVITATE.trigger(serverPlayer);
            }
        }
        if (event.resolveEffect == EffectFreeze.INSTANCE) {
            if (event.shooter != living && school == ELEMENTAL_WATER) {
                if (living instanceof Skeleton skel && skel.getType() == EntityType.SKELETON) {
                    skel.setFreezeConverting(true);
                }
                living.setIsInPowderSnow(true);
                int newFrozenTicks = living.getTicksFrozen() + (int) (60 * event.spellStats.getAmpMultiplier());
                living.setTicksFrozen(newFrozenTicks);
                if (living.isFullyFrozen() && living.canFreeze() && !living.hasEffect(ModPotions.FROZEN.get()) && living.invulnerableTime > 10) {
                    living.invulnerableTime = 0;
                    living.forceAddEffect(new MobEffectInstance(ModPotions.FROZEN.get(), 10, 0, false, false, false), living);
                }
            }
        }
        if (event.resolveEffect == EffectGrow.INSTANCE) {
            if (living.getMobType() == MobType.UNDEAD && school == ELEMENTAL_EARTH && event.shooter instanceof Player) {
                ((IDamageEffect) event.resolveEffect).attemptDamage(event.world, event.shooter, event.spellStats, event.context, event.resolver, living, event.world.damageSources().magic(), (float) (3 + 2 * event.spellStats.getAmpMultiplier()));
                if (living.isDeadOrDying() && event.world.getRandom().nextInt(100) < 20) {
                    BlockPos feet = living.getOnPos();
                    BlockState underfoot = living.level().getBlockState(feet);
                    if ((underfoot.getBlock() == Blocks.MOSS_BLOCK || underfoot.is(BlockTags.DIRT) || underfoot.is(BlockTags.LEAVES)) && event.world.getBlockState(feet.above()).isAir()) {
                        living.level().setBlockAndUpdate(feet.above(), ModItems.GROUND_BLOSSOM.get().defaultBlockState());
                        if (event.shooter instanceof ServerPlayer serverPlayer && !(serverPlayer instanceof FakePlayer))
                            ModAdvTriggers.BLOSSOM.trigger(serverPlayer);
                    }
                }
            }
        }
        if (event.resolveEffect == EffectGravity.INSTANCE) {
            if (event.spellStats.hasBuff(AugmentSensitive.INSTANCE) && school == ELEMENTAL_EARTH) {
                EntityMagnetSpell.createMagnet(event.world, event.shooter, event.spellStats, event.context, event.rayTraceResult.getLocation());
                event.setCanceled(true);
            }
        }
    }

    public static void empowerResolveOnBlocks(EffectResolveEvent.Pre event, BlockHitResult blockHitResult, SpellSchool school) {

        if (event.resolveEffect == EffectConjureWater.INSTANCE) {
            if (school == ELEMENTAL_WATER) {
                if (GlyphEffectUtil.hasFollowingEffect(event.context, EffectFreeze.INSTANCE)) {
                    GlyphEffectUtil.placeBlocks(blockHitResult, event.world, event.shooter, event.spellStats, event.context, new SpellResolver(event.context), Blocks.ICE.defaultBlockState());
                    event.setCanceled(true);
                }
            }
        }

        if (event.resolveEffect == EffectGravity.INSTANCE) {
            if (event.spellStats.hasBuff(AugmentSensitive.INSTANCE) && school == ELEMENTAL_EARTH) {
                EntityMagnetSpell.createMagnet(event.world, event.shooter, event.spellStats, event.context, event.rayTraceResult.getLocation());
                event.setCanceled(true);
            }
        }

        if (event.resolveEffect == EffectIgnite.INSTANCE && event.shooter instanceof Player player) {
            //break or sublimate the ice
            int pierceBuff = event.spellStats.getBuffCount(AugmentPierce.INSTANCE);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(event.shooter, blockHitResult.getBlockPos(), blockHitResult, event.spellStats.getAoeMultiplier(), pierceBuff);
            BlockState state;

            boolean flag = school == ELEMENTAL_FIRE && GlyphEffectUtil.hasFollowingEffect(event.context, EffectEvaporate.INSTANCE);

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
