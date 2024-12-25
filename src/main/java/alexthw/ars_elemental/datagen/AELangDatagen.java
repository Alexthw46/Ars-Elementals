package alexthw.ars_elemental.datagen;


import alexthw.ars_elemental.ArsElemental;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public class AELangDatagen extends LanguageProvider {

    private final Map<String, String> data = new TreeMap<>();

    public AELangDatagen(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        ArsNouveauAPI arsNouveauAPI = ArsNouveauAPI.getInstance();
        for (Supplier<Glyph> supplier : GlyphRegistry.getGlyphItemMap().values()) {
            Glyph glyph = supplier.get();
            AbstractSpellPart spellPart = glyph.spellPart;
            ResourceLocation registryName = glyph.spellPart.getRegistryName();
            if (registryName.getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.glyph_desc." + registryName.getPath(), spellPart.getBookDescription());
                add("ars_elemental.glyph_name." + registryName.getPath(), spellPart.getName());

                Map<AbstractAugment, String> augmentDescriptions = new HashMap<>();
                spellPart.addAugmentDescriptions(augmentDescriptions);

                for (AbstractAugment augment : augmentDescriptions.keySet()) {
                    add("ars_nouveau.augment_desc." + registryName.getPath() + "_" + augment.getRegistryName().getPath(), augmentDescriptions.get(augment));
                }
            }
        }
        for (FamiliarScript i : FamiliarRegistry.getFamiliarScriptMap().values()) {
            if (i.familiar.getRegistryName().getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.familiar_desc." + i.familiar.getRegistryName().getPath(), i.familiar.getBookDescription());
                add("ars_elemental.familiar_name." + i.familiar.getRegistryName().getPath(), i.familiar.getBookName());
                add("item.ars_elemental." + i.familiar.getRegistryName().getPath(), i.familiar.getBookName());
            }
        }
        for (RitualTablet i : RitualRegistry.getRitualItemMap().values()) {
            if (i.ritual.getRegistryName().getNamespace().equals(ArsElemental.MODID)) {
                add("ars_elemental.ritual_desc." + i.ritual.getRegistryName().getPath(), i.ritual.getLangDescription());
                add("item.ars_elemental." + i.ritual.getRegistryName().getPath(), i.ritual.getLangName());
            }
        }

        for (PerkItem i : PerkRegistry.getPerkItemMap().values()) {
            if (i.perk.getRegistryName().getNamespace().equals(ArsElemental.MODID) && !i.perk.getRegistryName().getPath().equals("blank_thread")) {
                add("ars_elemental.perk_desc." + i.perk.getRegistryName().getPath(), i.perk.getLangDescription());
                add("item.ars_elemental." + i.perk.getRegistryName().getPath(), i.perk.getLangName());
            }
        }
    }
}