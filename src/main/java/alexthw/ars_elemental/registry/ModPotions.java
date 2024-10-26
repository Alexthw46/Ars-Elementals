package alexthw.ars_elemental.registry;

import alexthw.ars_elemental.common.mob_effects.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static alexthw.ars_elemental.ArsElemental.MODID;
import static com.hollingsworth.arsnouveau.common.lib.LibPotions.longPotion;
import static com.hollingsworth.arsnouveau.common.lib.LibPotions.potion;

public class ModPotions {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> MAGIC_FIRE;
    public static final DeferredHolder<MobEffect, MobEffect> FROZEN;

    public static final DeferredHolder<MobEffect, MobEffect> WATER_GRAVE;
    public static final DeferredHolder<MobEffect, MobEffect> MANA_BUBBLE;
    public static final DeferredHolder<MobEffect, MobEffect> ENTHRALLED;
    public static final DeferredHolder<MobEffect, MobEffect> HYMN_OF_ORDER;
    public static final DeferredHolder<MobEffect, LifeLinkEffect> LIFE_LINK;
    public static final DeferredHolder<MobEffect, EnderferenceEffect> ENDERFERENCE;
    public static final DeferredHolder<MobEffect, LightningLureEffect> LIGHTNING_LURE;
    public static final DeferredHolder<MobEffect, RepelEffect> REPEL;
    public static final DeferredHolder<MobEffect, VenomEffect> VENOM;


    public static final DeferredHolder<Potion, Potion> ENDERFERENCE_POTION;
    public static final DeferredHolder<Potion, Potion> LONG_ENDERFERENCE_POTION;

    public static final DeferredHolder<Potion, Potion> SHOCK_POTION;
    public static final DeferredHolder<Potion, Potion> LONG_SHOCK_POTION;


    @SubscribeEvent
    private static void addBrewingRecipes(final RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, Items.TWISTING_VINES.asItem(), ENDERFERENCE_POTION);
        builder.addMix(ENDERFERENCE_POTION, Items.GLOWSTONE_DUST, LONG_ENDERFERENCE_POTION);

        builder.addMix(Potions.AWKWARD, ModItems.FLASHING_POD.get().asItem(), SHOCK_POTION);
        builder.addMix(SHOCK_POTION, Items.GLOWSTONE_DUST, LONG_SHOCK_POTION);

    }

    static {
        MAGIC_FIRE = EFFECTS.register("hellfire", MagicFireEffect::new);
        FROZEN = EFFECTS.register("frozen", FrozenEffect::new);
        WATER_GRAVE = EFFECTS.register("watery_grave", WaterGraveEffect::new);
        MANA_BUBBLE = EFFECTS.register("mana_shield", BubbleShieldEffect::new);
        ENTHRALLED = EFFECTS.register("enthralled", EnthrallEffect::new);
        LIFE_LINK = EFFECTS.register("life_link", LifeLinkEffect::new);
        HYMN_OF_ORDER = EFFECTS.register("hymn_of_order", OrderEffect::new);
        ENDERFERENCE = EFFECTS.register("enderference", EnderferenceEffect::new);
        LIGHTNING_LURE = EFFECTS.register("static_charged", LightningLureEffect::new);
        REPEL = EFFECTS.register("repel", RepelEffect::new);
        VENOM = EFFECTS.register("venom", VenomEffect::new);

        ENDERFERENCE_POTION = POTIONS.register(potion("enderference"), () -> new Potion(new MobEffectInstance(ENDERFERENCE, 400)));
        LONG_ENDERFERENCE_POTION = POTIONS.register(longPotion("enderference"), () -> new Potion(new MobEffectInstance(ENDERFERENCE, 1200)));
        SHOCK_POTION = POTIONS.register(potion("shock"), () -> new Potion(new MobEffectInstance(LIGHTNING_LURE, 600)));
        LONG_SHOCK_POTION = POTIONS.register(longPotion("shock"), () -> new Potion(new MobEffectInstance(LIGHTNING_LURE, 400)));

    }

}
