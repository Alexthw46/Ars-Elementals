package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.IUndeadSummon;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.entity.summon.*;
import alexthw.ars_elemental.common.items.armor.SummonPerk;
import alexthw.ars_elemental.common.items.foci.NecroticFocus;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.entity.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.Set;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = ArsElemental.MODID)
public class SummonEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void summonedEvent(SummonEvent event) {

        Set<SpellSchool> foci = ISchoolFocus.getFociSchools(event.shooter);

        if (!event.world.isClientSide && !foci.isEmpty()) {

            // boost summoned entities if necromancy focus is equipped
            if (foci.contains(SpellSchools.NECROMANCY)) {
                if (event.summon.getLivingEntity() != null) {
                    event.summon.getLivingEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 1));
                    event.summon.getLivingEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 1));
                }
            }

            // change summoned entities if water/fire/necromancy focus is equipped
            // first focus in the list takes priority
            if (event.summon instanceof SummonHorse oldHorse && event.shooter instanceof ServerPlayer summoner) {
                for (SpellSchool focus : foci) {
                    switch (focus.getId()) {
                        case "water" -> {
                            SummonDolphin newHorse = new SummonDolphin(oldHorse, summoner);
                            if (newHorse.getOwnerUUID() != null) {
                                oldHorse.remove(Entity.RemovalReason.DISCARDED);
                                event.summon = newHorse;
                                event.world.addFreshEntity(newHorse);
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(summoner, newHorse);
                            }
                        }
                        case "fire" -> {
                            SummonStrider newHorse = new SummonStrider(oldHorse, summoner);
                            if (newHorse.getOwnerUUID() != null) {
                                oldHorse.remove(Entity.RemovalReason.DISCARDED);
                                event.summon = newHorse;
                                event.world.addFreshEntity(newHorse);
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(summoner, newHorse);
                            }
                        }
                        case "earth" -> {
                            SummonCamel newHorse = new SummonCamel(oldHorse, summoner);
                            if (newHorse.getOwnerUUID() != null) {
                                oldHorse.remove(Entity.RemovalReason.DISCARDED);
                                event.summon = newHorse;
                                event.world.addFreshEntity(newHorse);
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(summoner, newHorse);
                            }
                        }
                        case "necromancy" -> {
                            SummonSkeleHorse newHorse = new SummonSkeleHorse(oldHorse, summoner);
                            if (newHorse.getOwnerUUID() != null) {
                                oldHorse.remove(Entity.RemovalReason.DISCARDED);
                                event.summon = newHorse;
                                event.world.addFreshEntity(newHorse);
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(summoner, newHorse);
                            }
                        }
                        default -> {
                            continue;
                        }
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void reRaiseSummon(SummonEvent.Death event) {
        if (!event.world.isClientSide) {
            ServerLevel world = (ServerLevel) event.world;
            var owner = event.summon instanceof IFollowingSummon summon ? summon.getSummoner() : event.summon.getOwner();
            if (owner instanceof Player player && !(event.summon instanceof IUndeadSummon)) {
                // re-raise summoned entities if necrotic focus is equipped
                if (NecroticFocus.hasFocus(event.world, player)) {
                    LivingEntity toRaise = null;
                    if (event.summon instanceof SummonWolf wolf) {
                        toRaise = new SummonDirewolf(world, player, wolf);
                    } else if (event.summon instanceof EntityAllyVex vex) {
                        toRaise = new AllyVhexEntity(world, vex, player);
                    } else if (event.summon instanceof SummonSkeleton skelly) {
                        toRaise = new SummonUndead(world, skelly, player);
                    }
                    if (toRaise instanceof IUndeadSummon undead) {
                        undead.inherit(event.summon);
                        event.world.addFreshEntity(toRaise);
                        NecroticFocus.spawnDeathPoof(world, toRaise.blockPosition());
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void summonSickReduction(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance.getEffect() == ModPotions.SUMMONING_SICKNESS_EFFECT) {
            effectInstance.duration = effectInstance.getDuration() * (1 - PerkUtil.countForPerk(SummonPerk.INSTANCE, event.getEntity()) / 10);
        }
    }

    @SubscribeEvent
    public static void summonPowerup(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ISummon summon && event.getEntity().level() instanceof ServerLevel) {
            if (summon.getOwner() instanceof Player player) {

                event.setNewDamage((float) (event.getNewDamage() + player.getAttributeValue(ModRegistry.SUMMON_POWER)));

                if (summon instanceof SummonWolf) {
                    Set<SpellSchool> schools = ISchoolFocus.getFociSchools(player);
                    for (SpellSchool school : schools)
                        switch (school.getId()) {
                            case "fire" -> event.getEntity().setRemainingFireTicks(5 * 20);
                            case "water" ->
                                    event.getEntity().addEffect(new MobEffectInstance(ModPotions.FREEZING_EFFECT, 100, 1));
                            case "air" ->
                                    event.getEntity().addEffect(new MobEffectInstance(ModPotions.SHOCKED_EFFECT, 100, 1));
                            case "earth" -> event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 100));
                        }
                }
            }
        }
    }
}
