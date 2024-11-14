package alexthw.ars_elemental.common.items.foci;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.IUndeadSummon;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.common.glyphs.EffectPhantom;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.common.items.ElementalCurio;
import alexthw.ars_elemental.util.ParticleUtil;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHeal;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSummonUndead;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static alexthw.ars_elemental.ArsNouveauRegistry.NECROMANCY;
import static alexthw.ars_elemental.ConfigHandler.COMMON;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, modid = ArsElemental.MODID)
public class NecroticFocus extends ElementalCurio implements ISchoolFocus {

    public NecroticFocus(Item.Properties properties) {
        super(properties);
    }

    @Override
    public double getDiscount() {
        return COMMON.MajorFocusDiscount.get();
    }

    @Override
    public @NotNull SpellSchool getSchool() {
        return NECROMANCY;
    }

    public static void spawnDeathPoof(ServerLevel world, BlockPos pos){
        for(int i =0; i < 10; i++){
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY() + 1.2;
            double d2 = pos.getZ() + 0.5 ;
            world.sendParticles(ParticleTypes.ANGRY_VILLAGER, d0, d1, d2, 2,(world.random.nextFloat() - 0.5)/3, (world.random.nextFloat() - 0.5)/3, (world.random.nextFloat() - 0.5)/3, 0.1f);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, Level context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("tooltip.ars_elemental.focus_boost", NECROMANCY.getTextComponent()));
        pTooltipComponents.add(Component.translatable("tooltip.ars_elemental.focus_anima"));
    }

    public static boolean hasFocus(Level level, Entity entity) {
        if (!level.isClientSide && entity instanceof Player) {
            Optional<IItemHandlerModifiable> curios = CuriosUtil.getAllWornItems((LivingEntity)entity).resolve();
            if (curios.isPresent()) {
                IItemHandlerModifiable items = curios.get();
                return IntStream.range(0, items.getSlots()).anyMatch(i -> items.getStackInSlot(i).getItem() instanceof NecroticFocus);
            }
        }
        return false;
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        // if the spell is a necromancy spell, increase the duration of the spell.
        if (NECROMANCY.isPartOfSchool(spellPart)) {
            builder.addDurationModifier(2.0f);
            // if the spell is an: heal, phantom, or summon undead spell, increase the amplification of the spell.
            if (spellPart == EffectHeal.INSTANCE || spellPart == EffectPhantom.INSTANCE || spellPart == EffectSummonUndead.INSTANCE) {
                builder.addAmplification(2.0f);
            }
        }
        return builder;
    }

    @SubscribeEvent
    public static void lifeSteal(LivingDeathEvent event){
        // if the source of the damage is a summoned undead entity, heal the player who summoned it.
        if (event.getSource().getEntity() instanceof IUndeadSummon risen && risen.getOwnerUUID() != null && event.getEntity().level() instanceof ServerLevel level) {
            Player player = event.getEntity().level.getPlayerByUUID(risen.getOwnerUUID());
            if (player != null) {
                player.heal(2.0F);
                level.addFreshEntity(new EntityFollowProjectile(level, risen.getLivingEntity().blockPosition(), player.blockPosition(), ParticleUtil.soulColor.toWrapper()));
            }
        }
    }

    @SubscribeEvent
    public static void castSpell(SpellCastEvent event) {
        // if the player has a necrotic focus, and the spell is a homing projectile, make the summoned undead mobs look at the player's last target and recast the spell.
        if (event.getWorld() instanceof ServerLevel world && event.getEntity() instanceof Player player && hasFocus(world, player) && event.spell.getCastMethod() == MethodHomingProjectile.INSTANCE) {
            for (Mob i : world.getEntitiesOfClass(Mob.class, new AABB(event.getEntity().blockPosition()).inflate(30.0D), (l) -> l instanceof IUndeadSummon summon && player.getUUID().equals(summon.getOwnerUUID()))) {
                LivingEntity target = i.getTarget();
                if (target == null) target = player.getLastHurtMob();
                if (target != null && target.isAlive()) {
                    i.getLookControl().setLookAt(target);
                } else {
                    i.getLookControl().setLookAt(player.getViewVector(1));
                }
                EntitySpellResolver spellResolver = new EntitySpellResolver(event.context.clone().withWrappedCaster(new LivingCaster(i)));
                spellResolver.onCast(ItemStack.EMPTY, world);
            }
        }
    }

}
