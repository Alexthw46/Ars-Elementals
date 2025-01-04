package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.entity.ai.FollowOwnerGoal;
import alexthw.ars_elemental.registry.ModPotions;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = ArsElemental.MODID)
public class Events {

    public static final String TAG_SOULBOUND_DROP_COUNT = "tagSoulboundDC";
    public static final String TAG_SOULBOUND_PREFIX = "tagSoulboundPrefix";
    public static final String TAG_SOULBOUND = "tagSoulbound";

    @SubscribeEvent
    public static void customAI(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity && !event.getLevel().isClientSide) {
            if (event.getEntity() instanceof PathfinderMob mob && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof FlyingPathNavigation)) {
                try {
                    mob.goalSelector.addGoal(2, new FollowOwnerGoal(mob, 1.5F, 3.0F, 1.2F));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    @SubscribeEvent
    public static void focusDiscount(SpellCostCalcEvent event) {
        var caster = event.context.getUnwrappedCaster();
        if (!caster.level().isClientSide() && caster instanceof Player player) {
            //if the player is holding a focus, and the spell match the focus's school, apply the focus discount.
            ISchoolFocus focus = ISchoolFocus.getFocus(player);
            if (focus != null && event.context.getSpell().unsafeList().stream().anyMatch(focus.getSchool()::isPartOfSchool))
                event.currentCost = (int) (event.currentCost - focus.getDiscount() * event.context.getSpell().getCost());
        }
    }

    @SubscribeEvent
    public static void DeathEvent(LivingDeathEvent event) {

        //when the player dies, if they have the hymn of order effect, save the effect duration to the player's persistent data.
        if (event.getEntity() instanceof Player player && player.hasEffect(ModPotions.HYMN_OF_ORDER)) {
            MobEffectInstance effect = player.getEffect(ModPotions.HYMN_OF_ORDER);
            if (effect == null) return;
            CompoundTag data = player.getPersistentData();
            if (!data.contains(Player.PERSISTED_NBT_TAG)) {
                data.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
            }
            CompoundTag persist = data.getCompound(Player.PERSISTED_NBT_TAG);

            persist.putBoolean("magic_locked", true);
            persist.putInt("magic_lock_duration", effect.getDuration());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrop(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.level.holder(ModRegistry.MIRROR).isEmpty())
                return;
            if (player instanceof FakePlayer || player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                return;
            }
            //if the player has soulbound enchantment, save the item to the player's persistent data.
            Collection<ItemEntity> drops = event.getDrops();
            List<ItemEntity> keeps = new ArrayList<>();
            for (ItemEntity item : drops) {
                ItemStack stack = item.getItem();
                if (!stack.isEmpty() && stack.getEnchantmentLevel(player.level().holderOrThrow(ModRegistry.SOULBOUND)) > 0) {
                    keeps.add(item);
                }
            }

            if (!keeps.isEmpty()) {
                drops.removeAll(keeps);

                CompoundTag cmp = new CompoundTag();
                cmp.putInt(TAG_SOULBOUND_DROP_COUNT, keeps.size());

                int i = 0;
                for (ItemEntity keep : keeps) {
                    ItemStack stack = keep.getItem();
                    var cmp1 = stack.saveOptional(event.getEntity().registryAccess());
                    cmp.put(TAG_SOULBOUND_PREFIX + i, cmp1);
                    i++;
                }

                CompoundTag data = player.getPersistentData();
                if (!data.contains(Player.PERSISTED_NBT_TAG)) {
                    data.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
                }

                CompoundTag persist = data.getCompound(Player.PERSISTED_NBT_TAG);
                persist.put(TAG_SOULBOUND, cmp);
            }
        }

    }


    @SubscribeEvent
    public static void onPlayerRespawnHW(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || event.getEntity().level().holder(ModRegistry.SOULBOUND).isEmpty()) return;
        CompoundTag data = event.getEntity().getPersistentData();
        //when the player respawns, if they have the hymn of order effect, apply the effect to the player.
        if (data.contains(Player.PERSISTED_NBT_TAG)) {
            CompoundTag persist = data.getCompound(Player.PERSISTED_NBT_TAG);
            if (persist.contains("magic_locked") && persist.contains("magic_lock_duration")) {
                event.getEntity().addEffect(new MobEffectInstance(ModPotions.HYMN_OF_ORDER, persist.getInt("magic_lock_duration")));
                persist.remove("magic_locked");
                persist.remove("magic_lock_duration");
            }


            //when the player respawns, if they have soulbound items, give them back to the player.
            CompoundTag soulTag = persist.getCompound(TAG_SOULBOUND);

            int count = soulTag.getInt(TAG_SOULBOUND_DROP_COUNT);
            List<ItemStack> recovered = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                CompoundTag toRecover = soulTag.getCompound(TAG_SOULBOUND_PREFIX + i);
                ItemStack stack = ItemStack.parseOptional(event.getEntity().registryAccess(), toRecover);
                if (!stack.isEmpty()) {
                    recovered.add(stack.copy());
                }
            }

            //try to put the items in the same slots they were in before.
            for (ItemStack stack : recovered) {
                if (stack.getItem() instanceof ArmorItem armor && event.getEntity().getItemBySlot(armor.getType().getSlot()).isEmpty()) {
                    event.getEntity().setItemSlot(armor.getType().getSlot(), stack);
                } else if (stack.getItem() instanceof ShieldItem && event.getEntity().getOffhandItem().isEmpty()) {
                    event.getEntity().setItemInHand(InteractionHand.OFF_HAND, stack);
                } else {
                    ItemHandlerHelper.giveItemToPlayer(event.getEntity(), stack);
                }
            }

            persist.remove(TAG_SOULBOUND);

        }
    }

    @SubscribeEvent
    public static void soulboundCurio(DropRulesEvent event) {
        event.addOverride(i -> {
            Level level = event.getEntity().level;
            return level.holder(ModRegistry.MIRROR).isPresent() &&
                   i.getEnchantmentLevel(level.holderOrThrow(ModRegistry.SOULBOUND)) > 0;
        }, ICurio.DropRule.ALWAYS_KEEP);
    }


    @SubscribeEvent
    public static void despawnProtection(ItemExpireEvent event) {
        var stackTag = event.getEntity().getItem().get(ModRegistry.P4E);
        if (stackTag != null && stackTag.flag()) {
            event.getEntity().setUnlimitedLifetime();
            event.setExtraLife(0);
        }
    }

}