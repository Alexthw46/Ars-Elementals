package alexthw.ars_elemental.common.items.caster_tools;

import alexthw.ars_elemental.client.caster_tools.SpellHornRenderer;
import alexthw.ars_elemental.util.GlyphEffectUtil;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SpellHorn extends Item implements GeoItem, ISpellModifierItem, ICasterTool {

    public SpellHorn(Properties properties) {
        super(properties.component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 20, animationState -> animationState.setAndContinue(spin)));
    }

    RawAnimation spin = RawAnimation.begin().thenLoop("wand_gem_spin");

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int remainingTicks) {

        //get how long the item has been used
        float j = getUseDuration(stack, pLivingEntity) - remainingTicks;

        //if the item has been used for at least the minimum duration, then cast the spell on every entity in the area of effect that matches the filter
        if (j >= getMinUseDuration() && pLivingEntity instanceof Player player) {
            float aoeMult = 0.5F + j / getMinUseDuration();
            if (pLevel instanceof ServerLevel) {
                AbstractCaster<? extends AbstractCaster<?>> caster = getSpellCaster(stack);
                if (caster == null) return;
                SpellResolver resolver = new SpellResolver(new SpellContext(pLevel, caster.getSpell(), pLivingEntity, new LivingCaster(pLivingEntity)));
                Predicate<Entity> filter = GlyphEffectUtil.getFilterPredicate(caster.getSpell(), (e -> e instanceof LivingEntity));
                for (Entity l : pLevel.getEntities((Entity) null, new AABB(player.blockPosition()).inflate(aoeMult), filter)) {
                    resolver.onResolveEffect(pLevel, new EntityHitResult(l));
                }
                resolver.expendMana();
            }
            if (j + 50 >= getMaxUseDuration()) {
                player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, getMaxUseDuration() * 4));
            }
            //play the sound effect and cooldown the item
            play(pLevel, player, aoeMult * 16);
            player.getCooldowns().addCooldown(this, 200);
        }

    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity player, @NotNull ItemStack stack, int count) {
        if ((getUseDuration(stack, player) - count) > getMaxUseDuration()) player.releaseUsingItem();
    }

    private static void play(Level level, Player player, float volume) {
        SoundEvent soundevent = SoundEvents.RAID_HORN.value();
        level.playSound(player, player, soundevent, SoundSource.RECORDS, volume, 1.0F);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity pEntity) {
        return 72000;
    }

    public int getMaxUseDuration() {
        return 200;
    }

    public int getMinUseDuration() {
        return 10;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public void scribeModifiedSpell(AbstractCaster<?> caster, Player player, InteractionHand hand, ItemStack stack, Spell.Mutable spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodSelf.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
    }


    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDurationModifier(1.0);
        return builder;
    }

    @Override
    public boolean isScribedSpellValid(AbstractCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.unsafeList().stream().noneMatch(s -> s instanceof AbstractCastMethod);
    }

    @Override
    public void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.wand.invalid"));
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {

            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }

            private final BlockEntityWithoutLevelRenderer renderer = new SpellHornRenderer();

        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (Screen.hasShiftDown() || !Config.GLYPH_TOOLTIPS.get())
            getInformation(stack, context, tooltip, flagIn);
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

}
