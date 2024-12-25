package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.ArsElemental;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ArsElemental.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {

    public static CompletableFuture<HolderLookup.Provider> provider;
    public static PackOutput output;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        provider = event.getLookupProvider();
        output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new AEBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(event.includeClient(), new AEItemModelProvider(gen, existingFileHelper));
        BlockTagsProvider BTP = new AETagsProvider.AEBlockTagsProvider(gen, provider, existingFileHelper);
        gen.addProvider(event.includeServer(), new AEEnchantmentProvider(output, provider));
        gen.addProvider(event.includeServer(), new AEDamageTypesProvider(output, provider));
        gen.addProvider(event.includeServer(), BTP);
        gen.addProvider(event.includeServer(), new AETagsProvider.AEMobEffectTagProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEItemTagsProvider(gen, provider, BTP, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEEntityTagProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEDamageTypeProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(gen, provider));
        gen.addProvider(event.includeServer(), new AELootTables(gen, provider));
        gen.addProvider(event.includeServer(), new CompostablesProvider(output, provider));

        gen.addProvider(event.includeServer(), new AEImbuementProvider(gen));
        gen.addProvider(event.includeServer(), new AEGlyphProvider(gen));
        gen.addProvider(event.includeServer(), new AEApparatusProvider(gen));
        gen.addProvider(event.includeServer(), new AECrushProvider(gen));

        gen.addProvider(event.includeServer(), new AEPatchouliProvider(gen));
        gen.addProvider(event.includeServer(), new AECurioProvider(output, existingFileHelper, provider));

        gen.addProvider(event.includeServer(), new AEAdvancementsProvider(output, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AECasterTomeProvider(gen));
        //gen.addProvider(event.includeClient(), new AELangDatagen(output, ArsElemental.MODID, "en_us"));

        gen.addProvider(event.includeServer(), new AEWorldgenProvider(output, provider));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEFeatureTagsProvider(gen, provider, existingFileHelper));
        gen.addProvider(event.includeServer(), new AETagsProvider.AEBiomeTagsProvider(gen, provider, existingFileHelper));

    }

    public static <T> Collection<T> takeAll(Collection<T> src, Predicate<T> predicate) {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext()) {
            T item = iter.next();
            if (predicate.test(item)) {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }

}
