package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.common.entity.familiars.FirenandoHolder;
import alexthw.ars_elemental.common.entity.familiars.MermaidHolder;
import alexthw.ars_elemental.common.items.armor.ArmorSet;
import alexthw.ars_elemental.common.items.armor.ShockPerk;
import alexthw.ars_elemental.common.items.armor.SporePerk;
import alexthw.ars_elemental.common.rituals.*;
import alexthw.ars_elemental.common.rituals.forest.ArchwoodForestRitual;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.registry.ModRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.ArsNouveauRegistry.registeredSpells;
import static alexthw.ars_elemental.datagen.Datagen.provider;

public class AEPatchouliProvider extends PatchouliProvider {

    public AEPatchouliProvider(DataGenerator generatorIn) {
        super(generatorIn, provider);
    }

    @Override
    public void collectJsons(CachedOutput cache) {

        for (AbstractSpellPart spell : registeredSpells) {
            addGlyphPage(spell);
        }

        addPage(new PatchouliBuilder(GETTING_STARTED, "elemental_tweaks")
                        .withIcon(ModItems.DEBUG_ICON.get())
                        .withTextPage("ars_elemental.page.elemental_tweaks")
                        .withPage(ImbuementPage(ModItems.MARK_OF_MASTERY.get()))
                , getPath(GETTING_STARTED, "elemental_tweaks"));

        addPage(new PatchouliBuilder(RESOURCES, "flashing_archwood")
                        .withIcon(ModItems.FLASHING_ARCHWOOD.get())
                        .withTextPage("ars_elemental.page1.flashing_archwood")
                        .withPage(new SpotlightPage(ModItems.FLASHING_POD.get().asItem()).withText("ars_elemental.page2.flashing_archwood").linkRecipe(true))
                        .withPage(new EntityPage(prefix("flashing_weald_walker").toString()))
                , getPath(RESOURCES, "flashing_archwood"));

        addBasicItem(ModItems.WATER_URN.get(), MACHINES, new ApparatusPage(ModItems.WATER_URN.get()));
        addBasicItem(ModItems.WATER_UPSTREAM_BLOCK.get(), MACHINES, new ApparatusPage(ModItems.WATER_UPSTREAM_BLOCK.get()));
        addBasicItem(ModItems.LAVA_UPSTREAM_BLOCK.get(), MACHINES, new ApparatusPage(ModItems.LAVA_UPSTREAM_BLOCK.get()));
        addBasicItem(ModItems.AIR_UPSTREAM_BLOCK.get(), MACHINES, new ApparatusPage(ModItems.AIR_UPSTREAM_BLOCK.get()));
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.CURIO_BAG.get())
                        .withPage(new CraftingPage(ModItems.CURIO_BAG.get()))
                        .withPage(new ApparatusPage(ModItems.CASTER_BAG.get())),
                getPath(EQUIPMENT, "bags"));

        addBasicItem(ModItems.ENCHANTER_BANGLE.get(), EQUIPMENT, new ApparatusPage(ModItems.ENCHANTER_BANGLE.get()));

        addBasicItem(ModItems.SPELL_MIRROR.get(), AUTOMATION, new ApparatusPage(ModItems.SPELL_MIRROR.get()));

        addPage(new PatchouliBuilder(AUTOMATION, ModItems.ADVANCED_PRISM.get())
                        .withIcon(ModItems.ADVANCED_PRISM.get())
                        .withTextPage("ars_elemental.page1.advanced_prism")
                        .withPage(new CraftingPage(ModItems.ADVANCED_PRISM.get()))
                        .withTextPage("ars_elemental.page2.advanced_prism")
                        .withPage(ImbuementPage(ModItems.ARC_LENS.get()))
                        .withPage(ImbuementPage(ModItems.HOMING_LENS.get()))
                        .withPage(ImbuementPage(ModItems.ACC_LENS.get()))
                        .withPage(ImbuementPage(ModItems.DEC_LENS.get()))
                        .withPage(ImbuementPage(ModItems.PIERCE_LENS.get()))
                        .withPage(ImbuementPage(ModItems.RGB_LENS.get()))
                , getPath(AUTOMATION, "advanced_prism"));

        addPage(new PatchouliBuilder(AUTOMATION, "elemental_turrets")
                        .withIcon(ModItems.FIRE_TURRET.get())
                        .withTextPage("ars_elemental.page1.elemental_turrets")
                        .withPage(ImbuementPage(ModItems.FIRE_TURRET.get()))
                        .withPage(ImbuementPage(ModItems.WATER_TURRET.get()))
                        .withPage(ImbuementPage(ModItems.AIR_TURRET.get()))
                        .withPage(ImbuementPage(ModItems.EARTH_TURRET.get()))
                        .withPage(ImbuementPage(ModItems.SHAPING_TURRET.get()))
                , getPath(AUTOMATION, "elemental_turrets"));

        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.SPELL_HORN.get())
                        .withIcon(ModItems.SPELL_HORN.get())
                        .withTextPage("ars_elemental.page1.spell_horn")
                        .withPage(new ApparatusPage(ModItems.SPELL_HORN.get()))
                , getPath(EQUIPMENT, "spell_horn"));

        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.NECRO_FOCUS.get())
                        .withIcon(ModItems.NECRO_FOCUS.get())
                        .withTextPage("ars_elemental.page1.necrotic_focus")
                        .withPage(ImbuementPage(ModItems.ANIMA_ESSENCE.get()))
                        .withTextPage("ars_elemental.page2.necrotic_focus")
                        .withPage(new ApparatusPage(ModItems.NECRO_FOCUS.get()))
                        .withTextPage("ars_elemental.page3.necrotic_focus")
                , getPath(EQUIPMENT, "necrotic_focus")
        );

        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.FIRE_FOCUS.get())
                        .withIcon(ModItems.FIRE_FOCUS.get())
                        .withTextPage("ars_elemental.page1.fire_focus")
                        .withPage(ImbuementPage(ModItems.LESSER_FIRE_FOCUS.get()))
                        .withPage(new ApparatusPage(ModItems.FIRE_FOCUS.get()))
                        .withTextPage("ars_elemental.page2.fire_focus")
                , getPath(EQUIPMENT, "fire_focus")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.FIRE_BANGLE.get())
                        .withIcon(ModItems.FIRE_BANGLE.get())
                        .withTextPage("ars_elemental.page1.fire_bangle")
                        .withPage(new ApparatusPage(ModItems.FIRE_BANGLE.get()))
                , getPath(EQUIPMENT, "fire_bangle")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.WATER_FOCUS.get())
                        .withIcon(ModItems.WATER_FOCUS.get())
                        .withTextPage("ars_elemental.page1.water_focus")
                        .withPage(ImbuementPage(ModItems.LESSER_WATER_FOCUS.get()))
                        .withPage(new ApparatusPage(ModItems.WATER_FOCUS.get()))
                        .withTextPage("ars_elemental.page2.water_focus")
                , getPath(EQUIPMENT, "water_focus")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.WATER_BANGLE.get())
                        .withIcon(ModItems.WATER_BANGLE.get())
                        .withTextPage("ars_elemental.page1.water_bangle")
                        .withPage(new ApparatusPage(ModItems.WATER_BANGLE.get()))
                , getPath(EQUIPMENT, "water_bangle")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.AIR_FOCUS.get())
                        .withIcon(ModItems.AIR_FOCUS.get())
                        .withTextPage("ars_elemental.page1.air_focus")
                        .withPage(ImbuementPage(ModItems.LESSER_AIR_FOCUS.get()))
                        .withPage(new ApparatusPage(ModItems.AIR_FOCUS.get()))
                        .withTextPage("ars_elemental.page2.air_focus")
                , getPath(EQUIPMENT, "air_focus")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.AIR_BANGLE.get())
                        .withIcon(ModItems.AIR_BANGLE.get())
                        .withTextPage("ars_elemental.page1.air_bangle")
                        .withPage(new ApparatusPage(ModItems.AIR_BANGLE.get()))
                , getPath(EQUIPMENT, "air_bangle")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.EARTH_FOCUS.get())
                        .withIcon(ModItems.EARTH_FOCUS.get())
                        .withTextPage("ars_elemental.page1.earth_focus")
                        .withPage(ImbuementPage(ModItems.LESSER_EARTH_FOCUS.get()))
                        .withPage(new ApparatusPage(ModItems.EARTH_FOCUS.get()))
                        .withTextPage("ars_elemental.page2.earth_focus")
                , getPath(EQUIPMENT, "earth_focus")
        );
        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.EARTH_BANGLE.get())
                        .withIcon(ModItems.EARTH_BANGLE.get())
                        .withTextPage("ars_elemental.page1.earth_bangle")
                        .withPage(new ApparatusPage(ModItems.EARTH_BANGLE.get()))
                , getPath(EQUIPMENT, "earth_bangle")
        );

        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.SUMMON_BANGLE.get())
                        .withIcon(ModItems.SUMMON_BANGLE.get())
                        .withTextPage("ars_elemental.page1.summon_bangle")
                        .withPage(new ApparatusPage(ModItems.SUMMON_BANGLE.get()))
                , getPath(EQUIPMENT, "summon_bangle")
        );

        addPage(new PatchouliBuilder(EQUIPMENT, ModItems.ANIMA_BANGLE.get())
                        .withIcon(ModItems.ANIMA_BANGLE.get())
                        .withTextPage("ars_elemental.page1.anima_bangle")
                        .withPage(new ApparatusPage(ModItems.ANIMA_BANGLE.get()))
                , getPath(EQUIPMENT, "anima_bangle")
        );

        addPage(new PatchouliBuilder(AUTOMATION, ModItems.SIREN_CHARM.get())
                        .withIcon(ModItems.SIREN_CHARM.get())
                        .withTextPage("ars_elemental.page1.mermaid")
                        .withPage(new ApparatusPage(ModItems.SIREN_CHARM.get()))
                        .withPage(new EntityPage(prefix("siren_entity").toString()))
                        .withTextPage("ars_elemental.page2.mermaid")
                , getPath(AUTOMATION, "mermaid"));

        addPage(new PatchouliBuilder(AUTOMATION, ModItems.FIRENANDO_CHARM.get())
                        .withIcon(ModItems.FIRENANDO_CHARM.get())
                        .withTextPage("ars_elemental.page1.fire_golem")
                        .withPage(new EntityPage(prefix("firenando_entity").toString()))
                        .withPage(new ApparatusPage(ModItems.FIRENANDO_CHARM.get()))
                , getPath(AUTOMATION, "fire_golem"));

        addPerkPage(ShockPerk.INSTANCE);
        addPerkPage(SporePerk.INSTANCE);

        addArmorPage(ModItems.FIRE_ARMOR);
        addArmorPage(ModItems.WATER_ARMOR);
        addArmorPage(ModItems.AIR_ARMOR);
        addArmorPage(ModItems.EARTH_ARMOR);


        addFamiliarPage(new MermaidHolder());
        addFamiliarPage(new FirenandoHolder());

        addRitualPage(new SquirrelRitual());
        addRitualPage(new TeslaRitual());
        addRitualPage(new AttractionRitual());
        addRitualPage(new RepulsionRitual());
        addRitualPage(new DetectionRitual());
        addRitualPage(new ArchwoodForestRitual());

        addEnchantmentPage(ModRegistry.MIRROR);
        addEnchantmentPage(ModRegistry.SOULBOUND);
        addPage(new PatchouliBuilder(ENCHANTMENTS, ItemsRegistry.NOVICE_SPELLBOOK.get()).withTextPage("ars_elemental.page.book_protection").withName("tooltip.ars_nouveau.blessed").withPage(new ApparatusPage(prefix("invincible_book").toString())), getPath(ENCHANTMENTS, "invincible_book"));

        for (PatchouliPage patchouliPage : pages) {
            saveStable(cache, patchouliPage.build(), patchouliPage.path());
        }

    }

    private void addArmorPage(ArmorSet armorSet) {
        PatchouliBuilder builder = new PatchouliBuilder(ARMOR, armorSet.getTranslationKey())
                .withIcon(armorSet.getHat())
                .withPage(new TextPage("ars_elemental.page.armor_set.wip"))
                .withPage(new TextPage("ars_elemental.page.armor_set." + armorSet.getName()))
                .withPage(new AEPage(armorSet.getHat()))
                .withPage(new AEPage(armorSet.getChest()))
                .withPage(new AEPage(armorSet.getLegs()))
                .withPage(new AEPage(armorSet.getBoots()));

        this.pages.add(new PatchouliPage(builder, getPath(ARMOR, "armor_" + armorSet.getName())));
    }

    static class AEPage extends ApparatusPage {
        public AEPage(Item itemLike) {
            super(itemLike);
        }

        @Override
        public ResourceLocation getType() {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "elemental_armor_recipe");
        }
    }

    @Override
    public void addPerkPage(IPerk perk) {
        PerkItem perkItem = PerkRegistry.getPerkItemMap().get(perk.getRegistryName());
        PatchouliBuilder builder = new PatchouliBuilder(ARMOR, perkItem)
                .withIcon(perkItem)
                .withTextPage(perk.getDescriptionKey())
                .withPage(new ApparatusPage(perkItem)).withSortNum(99);
        this.pages.add(new PatchouliPage(builder, getPath(ARMOR, perk.getRegistryName().getPath() + ".json")));
    }

    @Override
    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("ars_elemental.page." + getRegistryName(item.asItem()).getPath()))
                .withPage(recipePage);
        var page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
        this.pages.add(page);
        return page;
    }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity.ars_elemental." + familiarHolder.getRegistryName().getPath())
                .withIcon("ars_elemental:" + familiarHolder.getRegistryName().getPath())
                .withTextPage("ars_elemental.familiar_desc." + familiarHolder.getRegistryName().getPath())
                .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
        this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
    }

    public void addRitualPage(AbstractRitual ritual) {
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item.ars_elemental." + ritual.getRegistryName().getPath())
                .withIcon(ritual.getRegistryName().toString())
                .withTextPage(ritual.getDescriptionKey())
                .withPage(new CraftingPage("ars_elemental:tablet_" + ritual.getRegistryName().getPath()));

        this.pages.add(new PatchouliPage(builder, getPath(RITUALS, ritual.getRegistryName().getPath())));
    }

//    public void addEnchantmentPage(ResourceKey<Enchantment> enchantmentKey) {
//        Enchantment enchantment = ArsNouveau.proxy.getClientWorld().registryAccess().registryOrThrow(Registries.ENCHANTMENT).get(enchantmentKey);
//        if (enchantment == null) return;
//        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, String.valueOf(enchantment.description()))
//                .withIcon(getRegistryName(Items.ENCHANTED_BOOK).toString())
//                .withTextPage("ars_elemental.enchantment_desc." + enchantmentKey.location().getPath());
//
//        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
//            builder.withPage(new EnchantingPage("ars_nouveau:" + enchantmentKey.location().getPath() + "_" + i));
//        }
//        this.pages.add(new PatchouliPage(builder, getPath(ENCHANTMENTS, enchantmentKey.location().getPath())));
//    }

    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName("ars_elemental.glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage("ars_elemental.glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
    }


    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public @NotNull String getName() {
        return "Ars Elemental Patchouli Datagen";
    }

    ImbuementPage ImbuementPage(ItemLike item) {
        return new ImbuementPage("ars_elemental:imbuement_" + getRegistryName(item.asItem()).getPath());
    }

    private ResourceLocation getRegistryName(Item asItem) {
        return BuiltInRegistries.ITEM.getKey(asItem);
    }


}
