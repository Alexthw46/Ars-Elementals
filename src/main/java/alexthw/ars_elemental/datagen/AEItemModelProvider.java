package alexthw.ars_elemental.datagen;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.api.item.SpellPrismLens;
import alexthw.ars_elemental.common.items.CasterHolder;
import alexthw.ars_elemental.common.items.caster_tools.SpellHorn;
import alexthw.ars_elemental.registry.ModItems;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.common.block.StrippableLog;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static alexthw.ars_elemental.datagen.Datagen.takeAll;

@SuppressWarnings("ALL")
public class AEItemModelProvider extends ItemModelProvider {
    public AEItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), ArsElemental.MODID, existingFileHelper);
    }

    private static final ResourceLocation GENERATED = ResourceLocation.withDefaultNamespace("item/generated");

    private static final ResourceLocation HANDHELD = ResourceLocation.withDefaultNamespace("item/handheld");
    private static final ResourceLocation SPAWN_EGG = ResourceLocation.withDefaultNamespace("item/template_spawn_egg");

    @Override
    protected void registerModels() {
        Set<DeferredHolder<Item,? extends Item>> items = new HashSet<>(ModItems.ITEMS.getEntries());

        takeAll(items, i -> i.get() instanceof AnimBlockItem).forEach(this::blockItem);
        takeAll(items, i -> i.get() instanceof SpellHorn);
        takeAll(items, i -> i.get() instanceof SpellPrismLens);
        takeAll(items, i -> i.get() instanceof CasterHolder);
        takeAll(items, i -> i.get() instanceof BlockItem bi && bi.getBlock() instanceof ArchfruitPod).forEach(this::generatedItem);
        takeAll(items, i -> i.get() instanceof BlockItem bi && bi.getBlock() instanceof FenceBlock).forEach(this::fenceBlockItem);
        takeAll(items, i -> i.get() instanceof BlockItem bi && (bi.getBlock() instanceof SaplingBlock || bi.getBlock() instanceof FlowerBlock)).forEach(this::blockGeneratedItem);
        takeAll(items, i -> i.get() instanceof BlockItem).forEach(this::blockItem);
        takeAll(items, i -> i.get() instanceof DiggerItem).forEach(this::handheldItem);
        takeAll(items, i -> i.get() instanceof SpawnEggItem).forEach(this::spawnEgg);
        items.forEach(this::generatedItem);

    }

    private void spawnEgg(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, SPAWN_EGG);
    }

    private void handheldItem(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, HANDHELD).texture("layer0", ArsElemental.prefix("item/" + name));
    }

    private void generatedItem(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, GENERATED).texture("layer0", ArsElemental.prefix("item/" + name));
    }

    private void focusModel(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        withExistingParent("item/focus/" + name, GENERATED).texture("layer0", ArsElemental.prefix("item/" + name));
    }

    private void blockGeneratedItem(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, GENERATED).texture("layer0", ArsElemental.prefix("block/" + name));
    }

    private void blockItem(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        String root = "block/";
        if (i.get() instanceof BlockItem bi && (bi.getBlock() instanceof RotatedPillarBlock || bi.getBlock() instanceof LeavesBlock || bi.getBlock() instanceof StrippableLog))
            root = "block/archwood/";
        getBuilder(name).parent(new ModelFile.UncheckedModelFile(ArsElemental.prefix(root + name)));
    }

    private void fenceBlockItem(DeferredHolder<Item, ? extends Item> i) {
        String name = BuiltInRegistries.ITEM.getKey(i.get()).getPath();
        String baseName = name.substring(0, name.length() - 6);
        fenceInventory(name, ArsElemental.prefix("block/" + baseName));
    }

    @NotNull
    @Override
    public String getName() {
        return "Ars Elemental Item Models";
    }
}
