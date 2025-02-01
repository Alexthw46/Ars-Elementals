package alexthw.ars_elemental.documentation;

import alexthw.ars_elemental.common.items.armor.ArmorSet;
import alexthw.ars_elemental.recipe.NetheriteUpgradeRecipe;
import alexthw.ars_elemental.registry.ModEntities;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.EntityEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.api.documentation.search.ConnectedSearch;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import static alexthw.ars_elemental.ArsElemental.prefix;
import static alexthw.ars_elemental.registry.ModRegistry.NETHERITE_UP;
import static com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry.*;
import static com.hollingsworth.arsnouveau.common.lib.LibBlockNames.BASIC_SPELL_TURRET;
import static com.hollingsworth.arsnouveau.common.lib.LibBlockNames.SPELL_PRISM;
import static com.hollingsworth.arsnouveau.setup.registry.Documentation.*;

public class AEDocumentation {

    static DocCategory EQUIPMENT = DocumentationRegistry.ITEMS;
    static DocCategory AUTOMATION = DocumentationRegistry.CRAFTING;


    private static DocEntry addPage(DocEntryBuilder builder) {
        return DocumentationRegistry.registerEntry(builder.category, builder.build());
    }

    public static void init(ReloadDocumentationEvent.AddEntries ignored) {
        addPage(new AEDocEntryBuilder(GETTING_STARTED, "spell_schools").withIcon(ModItems.DEBUG_ICON.get()).withTextPage("ars_elemental.page.schools").withPage(TextEntry.create(Component.translatable("ars_elemental.page.anima"), Component.translatable("ars_nouveau.school.necromancy"), ModItems.ANIMA_ESSENCE.get())));

        addBasicItem(ModItems.WATER_URN.get(), AUTOMATION);
        addBasicItem(ModItems.AIR_UPSTREAM_BLOCK.get(), CRAFTING);
        addBasicItem(ModItems.WATER_UPSTREAM_BLOCK.get(), CRAFTING);
        addBasicItem(ModItems.LAVA_UPSTREAM_BLOCK.get(), CRAFTING);

        var curioBag = addPage(new AEDocEntryBuilder(EQUIPMENT, ModItems.CURIO_BAG.get()).withIntroPage().withCraftingPages(ModItems.CURIO_BAG.get()).withCraftingPages(ModItems.CASTER_BAG.get()));

        addBasicItem(ModItems.SPELL_MIRROR.get(), AUTOMATION);

        var advancedPrism = addPage(new AEDocEntryBuilder(AUTOMATION, ModItems.ADVANCED_PRISM.get()).withTextPage("ars_elemental.page1.advanced_prism")
                .withCraftingPages(ModItems.ADVANCED_PRISM.get())
                .withCraftingPages(ModItems.ARC_LENS.get())
                .withCraftingPages(ModItems.HOMING_LENS.get())
                .withCraftingPages(ModItems.ACC_LENS.get())
                .withCraftingPages(ModItems.DEC_LENS.get())
                .withCraftingPages(ModItems.PIERCE_LENS.get())
                .withCraftingPages(ModItems.RGB_LENS.get()))
                .withRelation(getBaseEntry("block.ars_nouveau." + SPELL_PRISM));

        var elementalTurrets = addPage(new AEDocEntryBuilder(AUTOMATION, "elemental_turrets").withIcon(ModItems.FIRE_TURRET.get()).withTextPage("ars_elemental.page1.elemental_turrets")
                .withCraftingPages(ModItems.FIRE_TURRET.get())
                .withCraftingPages(ModItems.WATER_TURRET.get())
                .withCraftingPages(ModItems.AIR_TURRET.get())
                .withCraftingPages(ModItems.EARTH_TURRET.get())
                .withCraftingPages(ModItems.SHAPING_TURRET.get()))
                .withRelation(getBaseEntry(BASIC_SPELL_TURRET));

        addBasicItem(ModItems.SPELL_HORN.get(), SPELL_CASTING);

        addPage(new AEDocEntryBuilder(SPELL_CASTING, ModItems.NECRO_FOCUS.get())
                .withIntroPage()
                .withCraftingPages(ModItems.NECRO_FOCUS.get())
                .withTextPage("ars_elemental.page1.necro_focus")
        );

        addPage(new AEDocEntryBuilder(SPELL_CASTING, ModItems.FIRE_FOCUS.get())
                .withIntroPage()
                .withCraftingPages(ModItems.LESSER_FIRE_FOCUS.get())
                .withTextPage("ars_elemental.page2.fire_focus")
                .withCraftingPages(ModItems.FIRE_FOCUS.get())
        );

        addPage(new AEDocEntryBuilder(SPELL_CASTING, ModItems.WATER_FOCUS.get())
                .withIntroPage()
                .withCraftingPages(ModItems.LESSER_WATER_FOCUS.get())
                .withTextPage("ars_elemental.page2.water_focus")
                .withCraftingPages(ModItems.WATER_FOCUS.get())
        );

        addPage(new AEDocEntryBuilder(SPELL_CASTING, ModItems.AIR_FOCUS.get())
                .withIntroPage()
                .withCraftingPages(ModItems.LESSER_AIR_FOCUS.get())
                .withTextPage("ars_elemental.page2.air_focus")
                .withCraftingPages(ModItems.AIR_FOCUS.get())
        );

        addPage(new AEDocEntryBuilder(SPELL_CASTING, ModItems.EARTH_FOCUS.get())
                .withIntroPage()
                .withCraftingPages(ModItems.LESSER_EARTH_FOCUS.get())
                .withTextPage("ars_elemental.page2.earth_focus")
                .withCraftingPages(ModItems.EARTH_FOCUS.get())
        );

        var sirenCharm = addPage(new AEDocEntryBuilder(AUTOMATION, ModItems.SIREN_CHARM.get()).withIntroPage().withPage(EntityEntry.create(ModEntities.SIREN_ENTITY.get())).withTextPage("ars_elemental.page2.siren_charm"));

        var firenandoCharm = addPage(new AEDocEntryBuilder(AUTOMATION, ModItems.FIRENANDO_CHARM.get()).withIntroPage().withPage(EntityEntry.create(ModEntities.FIRENANDO_ENTITY.get())));

        addPage(new AEDocEntryBuilder(ARMOR, ModItems.MARK_OF_MASTERY.get()).withName("ars_elemental.title.elemental_upgrades").withIntroPage().withCraftingPages().withSortNum(5));
        addArmorSet(ModItems.FIRE_ARMOR);
        addArmorSet(ModItems.WATER_ARMOR);
        addArmorSet(ModItems.AIR_ARMOR);
        addArmorSet(ModItems.EARTH_ARMOR);

        addPage(new AEDocEntryBuilder(EQUIPMENT, ModItems.ENCHANTER_BANGLE.get())
                .withIntroPage()
                .withCraftingPages(ModItems.ENCHANTER_BANGLE.get())
                .withTextPage("ars_elemental.page.fire_bangle")
                .withCraftingPages(ModItems.FIRE_BANGLE.get())
                .withTextPage("ars_elemental.page.water_bangle")
                .withCraftingPages(ModItems.WATER_BANGLE.get())
                .withTextPage("ars_elemental.page.air_bangle")
                .withCraftingPages(ModItems.AIR_BANGLE.get())
                .withTextPage("ars_elemental.page.earth_bangle")
                .withCraftingPages(ModItems.EARTH_BANGLE.get())
                .withTextPage("ars_elemental.page.summon_bangle")
                .withCraftingPages(ModItems.SUMMON_BANGLE.get())
                .withTextPage("ars_elemental.page.anima_bangle")
                .withCraftingPages(ModItems.ANIMA_BANGLE.get())
        );
    }

    private static void addArmorSet(ArmorSet armorSet) {
        registerEntry(ARMOR, new AEDocEntryBuilder(ARMOR, armorSet.getTranslationKey())
                .withPage(TextEntry.create("ars_elemental.page.armor_set." + armorSet.getName(), armorSet.getTranslationKey()))
                .withSortNum(10)
                .withCraftingPages(armorSet.getHat())
                .withCraftingPages(armorSet.getChest())
                .withCraftingPages(armorSet.getLegs())
                .withCraftingPages(armorSet.getBoots())
                .build()
        );

    }


    public static void postInit(ReloadDocumentationEvent.Post ignored) {
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        getBaseEntry("archwood").addPage(TextEntry.create(Component.translatable("ars_elemental.page2.flashing_archwood"), ModItems.FLASHING_POD.get().getName(), ModItems.FLASHING_POD.get()));
        Search.addConnectedSearch(new ConnectedSearch(ArsNouveau.prefix("archwood"), new ItemStack(ModItems.FLASHING_POD.get()).getHoverName(), new ItemStack(ModItems.FLASHING_POD.get())));
        getBaseEntry("weald_walker").addPage(EntityEntry.create(ModEntities.FLASHING_WEALD_WALKER.get(), getLangPath("weald_walker", 6)));

        ItemStack animaEssence = ModItems.ANIMA_ESSENCE.get().getDefaultInstance();
        getBaseEntry("imbuement_chamber").addPages(getRecipePages(animaEssence, prefix("imbuement_anima_essence")));
        Search.addConnectedSearch(new ConnectedSearch(ArsNouveau.prefix("imbuement_chamber"), animaEssence.getHoverName(), animaEssence));
        RecipeHolder<NetheriteUpgradeRecipe> protectionBook = manager.byKeyTyped(NETHERITE_UP.get(), prefix("invincible_book"));

        getBaseEntry("spell_books").addPage(TextEntry.create("ars_elemental.page.book_protection", "tooltip.ars_nouveau.blessed")).addPage(P4EEntry.create(protectionBook));

        getBaseEntryItem(ItemsRegistry.ENCHANTERS_SHIELD).addPage(TextEntry.create("ars_elemental.page.enchanters_shield"))
                .withRelation(getEntry(prefix("mirror_shield")));

        getBaseEntryGlyph(EffectIgnite.INSTANCE).addPage(TextEntry.create("ars_elemental.page.ignite"));
        getBaseEntryGlyph(EffectGravity.INSTANCE).addPage(TextEntry.create("ars_elemental.page.gravity"));
        getBaseEntryGlyph(EffectFreeze.INSTANCE).addPage(TextEntry.create("ars_elemental.page.freeze"));
        getBaseEntryGlyph(EffectLaunch.INSTANCE).addPage(TextEntry.create("ars_elemental.page.launch"));
        getBaseEntryGlyph(EffectGrow.INSTANCE).addPage(TextEntry.create("ars_elemental.page.grow"));
        getBaseEntryGlyph(EffectCut.INSTANCE).addPage(TextEntry.create("ars_elemental.page.cut"));

    }

    private static DocEntry getBaseEntryGlyph(AbstractSpellPart instance) {
        return getEntry(instance.getRegistryName());
    }

    private static DocEntry getBaseEntryItem(ItemRegistryWrapper<?> delegate) {
        return getBaseEntry(delegate.get().getDescriptionId());
    }

    static DocEntry getBaseEntry(String entry) {
        return getEntry(ArsNouveau.prefix(entry));
    }

    static class AEDocEntryBuilder extends DocEntryBuilder {

        public AEDocEntryBuilder(DocCategory category, String name) {
            this(category, name, prefix(name));
        }

        public AEDocEntryBuilder(DocCategory category, String name, ResourceLocation entryId) {
            super(category, name.contains(".") ? name : "ars_elemental.page." + name, entryId);
        }

        public AEDocEntryBuilder(DocCategory category, ItemLike itemLike) {
            super(category, itemLike);
        }

        public DocEntryBuilder withIntroPage(String id) {
            textCounter++;
            pages.add(TextEntry.create(Component.translatable("ars_elemental.page" + textCounter + "." + id), Component.translatable(titleKey), displayItem));
            return this;
        }
    }
}
