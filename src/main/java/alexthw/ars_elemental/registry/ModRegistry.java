package alexthw.ars_elemental.registry;

import alexthw.ars_elemental.common.CasterHolderContainer;
import alexthw.ars_elemental.common.CurioHolderContainer;
import alexthw.ars_elemental.common.components.ElementProtectionFlag;
import alexthw.ars_elemental.common.components.SchoolCasterTomeData;
import alexthw.ars_elemental.common.items.CasterHolder;
import alexthw.ars_elemental.common.items.CurioHolder;
import alexthw.ars_elemental.recipe.ConfigCondition;
import alexthw.ars_elemental.recipe.ElementalArmorRecipe;
import alexthw.ars_elemental.recipe.HeadCutRecipe;
import alexthw.ars_elemental.recipe.NetheriteUpgradeRecipe;
import alexthw.ars_elemental.util.CompatUtils;
import alexthw.ars_elemental.util.SupplierBlockStateProviderAE;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static alexthw.ars_elemental.ArsElemental.MODID;
import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.common.items.armor.AAMaterials.A_MATERIALS;
import static alexthw.ars_elemental.registry.ModEntities.ENTITIES;
import static alexthw.ars_elemental.registry.ModItems.BLOCKS;
import static alexthw.ars_elemental.registry.ModItems.ITEMS;
import static alexthw.ars_elemental.registry.ModParticles.PARTICLES;
import static alexthw.ars_elemental.registry.ModPotions.EFFECTS;
import static alexthw.ars_elemental.registry.ModPotions.POTIONS;
import static alexthw.ars_elemental.registry.ModTiles.TILES;
import static alexthw.ars_elemental.world.ModWorldgen.FEATURES;

public class ModRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<DataComponentType<?>> D_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, MODID);
    public static final DeferredRegister<BlockStateProviderType<?>> BS_PROVIDERS = DeferredRegister.create(Registries.BLOCK_STATE_PROVIDER_TYPE, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigCondition>> CONFIG_CONDITION = CONDITION_CODECS.register("config", () -> ConfigCondition.CODEC);

    public static final TagKey<Item> BLACKLIST_BAGGABLE = ItemTags.create(prefix("blacklist_bag_item"));

    public static final TagKey<Item> SOULBOUND_ABLE = ItemTags.create(prefix("soulbound_extra"));

    public static final TagKey<EntityType<?>> ATTRACT_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, prefix("attraction_ritual_blacklist"));

    public static final TagKey<EntityType<?>> FIERY = TagKey.create(Registries.ENTITY_TYPE, prefix("fiery"));
    public static final TagKey<EntityType<?>> AERIAL = TagKey.create(Registries.ENTITY_TYPE, prefix("aerial"));
    public static final TagKey<EntityType<?>> INSECT = TagKey.create(Registries.ENTITY_TYPE, prefix("insect"));

    public static final ResourceKey<DamageType> CUT = key(Registries.DAMAGE_TYPE, "beheading");
    public static final ResourceKey<DamageType> POISON = key(Registries.DAMAGE_TYPE, "poison");
    public static final ResourceKey<DamageType> MAGIC_FIRE = key(Registries.DAMAGE_TYPE, "hellfire");
    public static final ResourceKey<DamageType> SPARK = key(Registries.DAMAGE_TYPE, "spark");

    public static TagKey<DamageType> FIRE_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("fire_damage"));
    public static TagKey<DamageType> WATER_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("water_damage"));
    public static TagKey<DamageType> EARTH_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("earth_damage"));
    public static TagKey<DamageType> AIR_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, prefix("air_damage"));


    public static void registerRegistries(IEventBus bus) {
        A_MATERIALS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        PARTICLES.register(bus);
        EFFECTS.register(bus);
        POTIONS.register(bus);
        ENCHANTMENTS.register(bus);
        ATTRIBUTES.register(bus);
        RECIPES.register(bus);
        SERIALIZERS.register(bus);
        FEATURES.register(bus);
        BS_PROVIDERS.register(bus);
        TABS.register(bus);
        D_COMPONENTS.register(bus);
        CONDITION_CODECS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ELEMENTAL_TAB;

    public static final DeferredHolder<MenuType<?>, MenuType<CurioHolderContainer>> CURIO_HOLDER;
    public static final DeferredHolder<MenuType<?>, MenuType<CasterHolderContainer>> CASTER_HOLDER;


    public static final DeferredHolder<BlockStateProviderType<?>, BlockStateProviderType<?>> AE_BLOCKSTATE_PROVIDER;

    public static final ResourceKey<Enchantment> MIRROR = key(Registries.ENCHANTMENT,"mirror_shield");
    public static final ResourceKey<Enchantment> SOULBOUND = key(Registries.ENCHANTMENT,"soulbound");

    public static final DeferredHolder<Attribute, Attribute> SUMMON_POWER = PerkAttributes.registerAttribute("ars_elemental.perk.summon_power", (id) -> new RangedAttribute(id,0,0,10000.0D), "ee3a4090-c5f5-4a26-a9c2-6044e9e609de");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ElementProtectionFlag>> P4E = D_COMPONENTS.register("p4e", () -> DataComponentType.<ElementProtectionFlag>builder().persistent(ElementProtectionFlag.CODEC).networkSynchronized(ElementProtectionFlag.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SchoolCasterTomeData>> E_TOME_CASTER = D_COMPONENTS.register("elemental_tome_caster", () -> DataComponentType.<SchoolCasterTomeData>builder().persistent(SchoolCasterTomeData.CODEC.codec()).networkSynchronized(SchoolCasterTomeData.STREAM_CODEC).build());

    public static final DeferredHolder<RecipeType<?>, RecipeType<NetheriteUpgradeRecipe>> NETHERITE_UP;
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<NetheriteUpgradeRecipe>> NETHERITE_UP_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP;
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElementalArmorRecipe>> ELEMENTAL_ARMOR_UP_SERIALIZER;
    public static final DeferredHolder<RecipeType<?>, RecipeType<HeadCutRecipe>> HEAD_CUT;
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HeadCutRecipe>> HEAD_CUT_SERIALIZER;


    static {

        CURIO_HOLDER = CONTAINERS.register("curio_holder", () -> IMenuTypeExtension.create((id, inv, extraData) -> {
            int slot = extraData.readInt();
            ItemStack stack = slot < 0 ? CompatUtils.getCurio(inv.player, i -> i.getItem() instanceof CurioHolder).stack() : inv.getItem(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof CurioHolder))
                stack = ModItems.CURIO_BAG.get().getDefaultInstance();
            return new CurioHolderContainer(id, inv, stack);
        }));
        CASTER_HOLDER = CONTAINERS.register("caster_holder", () -> IMenuTypeExtension.create((id, inv, extraData) -> {
            int slot = extraData.readInt();
            ItemStack stack = slot < 0 ? CompatUtils.getCurio(inv.player, i -> i.getItem() instanceof CasterHolder).stack() : inv.getItem(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof CasterHolder))
                stack = ModItems.CASTER_BAG.get().getDefaultInstance();
            return new CasterHolderContainer(id, inv, stack);
        }));

        AE_BLOCKSTATE_PROVIDER = BS_PROVIDERS.register("ae_stateprovider", () -> new BlockStateProviderType<>(SupplierBlockStateProviderAE.CODEC));

        HEAD_CUT = RECIPES.register("head_cut", () -> RecipeType.simple(prefix("head_cut")));
        HEAD_CUT_SERIALIZER = SERIALIZERS.register("head_cut", HeadCutRecipe.Serializer::new);
        NETHERITE_UP = RECIPES.register("netherite_upgrade", () -> RecipeType.simple(prefix("netherite_upgrade")));
        NETHERITE_UP_SERIALIZER = SERIALIZERS.register("netherite_upgrade", NetheriteUpgradeRecipe.Serializer::new);
        ELEMENTAL_ARMOR_UP = RECIPES.register("armor_upgrade", () -> RecipeType.simple(prefix("armor_upgrade")));
        ELEMENTAL_ARMOR_UP_SERIALIZER = SERIALIZERS.register("armor_upgrade", ElementalArmorRecipe.Serializer::new);

        ELEMENTAL_TAB = TABS.register("general", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ars_elemental"))
                .icon(() -> ModItems.DEBUG_ICON.get().getDefaultInstance())
                .displayItems((params, output) -> {
                    for (var entry : ITEMS.getEntries()) {
                        output.accept(entry.get().getDefaultInstance());
                    }
                }).withTabsBefore(CreativeTabRegistry.BLOCKS.getId())
                .build());
    }

    static <T> ResourceKey<T> key(ResourceKey<Registry<T>> registryResourceKey, String name) {
        return ResourceKey.create(registryResourceKey, prefix(name));
    }
}
