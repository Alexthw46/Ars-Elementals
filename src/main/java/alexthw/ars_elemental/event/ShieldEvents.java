package alexthw.ars_elemental.event;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.registry.ModAdvTriggers;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.common.entity.EntityHomingProjectileSpell;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.items.EnchantersShield;
import com.hollingsworth.arsnouveau.common.items.data.ReactiveCasterData;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = ArsElemental.MODID)
public class ShieldEvents {

    @SubscribeEvent
    public static void onBlock(ProjectileImpactEvent event){
        //if the entity is blocking and the projectile is a spell projectile
        if (event.getProjectile() instanceof EntityProjectileSpell projectileSpell && event.getRayTraceResult() instanceof EntityHitResult result && result.getEntity() instanceof Player player && player.isBlocking()) {
            ItemStack stack = player.getOffhandItem();
            //if the shield is an enchanters shield then check if the shield has the mirror enchantment
            if (stack.getItem() instanceof EnchantersShield) {
                int level = stack.getEnchantmentLevel(player.level.holderOrThrow(ModRegistry.MIRROR));
                //bounce back the spell if the random number is less than the level of the enchantment
                if (level * .25 >= Math.random()) {
                    projectileSpell.setDeltaMovement(projectileSpell.getDeltaMovement().reverse().add(0, 0.2, 0));
                    if (projectileSpell instanceof EntityHomingProjectileSpell homing && level > 3) {
                        homing.getIgnored().add((e) -> e == player);
                    }
                    //trigger the advancement and remove mana from the player and set the cooldown of the shield to 1 second per level
                    if (player instanceof ServerPlayer && CapabilityRegistry.getMana(player) != null) {
                        ModAdvTriggers.MIRROR.get().trigger((ServerPlayer) player);
                        float pay = projectileSpell.spellResolver.getResolveCost() / (level * 2f);
                        CapabilityRegistry.getMana(player).removeMana(pay);
                        player.getCooldowns().addCooldown(stack.getItem(), 20 * level);
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockReactive(LivingShieldBlockEvent event){

        if (!(event.getEntity() instanceof Player player) || player.getCommandSenderWorld().isClientSide)
            return;
        ItemStack s = player.getOffhandItem();
        //if the shield is an enchanters shield then check if the shield has the reactive enchantment and if the roll is successful then cast the spell
        if (s.getItem() instanceof EnchantersShield && s.get(DataComponentRegistry.REACTIVE_CASTER) instanceof ReactiveCasterData reactiveCaster) {
            if (s.getEnchantmentLevel(player.level.holderOrThrow((EnchantmentRegistry.REACTIVE_ENCHANTMENT))) * .25 >= Math.random() && reactiveCaster.getSpell().isValid()) {
                reactiveCaster.castSpell(player.getCommandSenderWorld(), player, InteractionHand.OFF_HAND, null);
            }
        }

    }

    @SubscribeEvent
    public static void onSonicImpact(LivingIncomingDamageEvent event){
        if (!(event.getEntity() instanceof Player player && player.getCommandSenderWorld() instanceof ServerLevel level))
            return;
        ItemStack s = player.getOffhandItem();
        //if the shield is an enchanters shield then check if the shield has the reactive enchantment and if the roll is successful then cast the spell
        if (level.holder(ModRegistry.MIRROR).isEmpty())
            return;
        if (s.getItem() instanceof EnchantersShield && player.isBlocking() && s.getEnchantmentLevel(level.holderOrThrow(ModRegistry.MIRROR)) * .25 >= Math.random()) {
            if (event.getSource().getSourcePosition() != null && event.getSource().is(DamageTypes.SONIC_BOOM) && CapabilityRegistry.getMana(player) != null) {

                Vec3 vec3 = player.getViewVector(1.0F);
                Vec3 vec31 = event.getSource().getSourcePosition().vectorTo(player.position()).normalize();
                vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
                if (vec31.dot(vec3) < 0.0D)
                    reflectSonicBoom(event, player, s, level);
            }
        }
    }

    private static void reflectSonicBoom(LivingIncomingDamageEvent event, Player player, ItemStack stack, ServerLevel level) {
        event.setCanceled(true);

        if (!(event.getSource().getEntity() instanceof LivingEntity reflectTo)) {
            return;
        }
        Vec3 vec3 = player.getEyePosition();
        Vec3 vec31 = reflectTo.position().add(0.0D, 1.6F, 0.0D).subtract(vec3);
        Vec3 vec32 = vec31.normalize();

        for(int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
            Vec3 vec33 = vec3.add(vec32.scale(i));
            level.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        player.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        reflectTo.hurt(player.damageSources().sonicBoom(player), 10.0F);
        double d1 = 0.5D * (1.0D - reflectTo.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        double d0 = 2.5D * (1.0D - reflectTo.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        reflectTo.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);

        CapabilityRegistry.getMana(player).removeMana(150);
        player.getCooldowns().addCooldown(stack.getItem(), 20 * stack.getEnchantmentLevel(level.holderOrThrow(ModRegistry.MIRROR)));
        player.stopUsingItem();
    }

}
