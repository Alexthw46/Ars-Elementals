package alexthw.ars_elemental.common.rituals.forest;

import alexthw.ars_elemental.registry.ModItems;
import alexthw.ars_elemental.world.ModWorldgen;
import com.hollingsworth.arsnouveau.api.ritual.ConjureBiomeRitual;
import com.hollingsworth.arsnouveau.common.block.ArchfruitPod;
import com.hollingsworth.arsnouveau.setup.registry.BiomeRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class ArchwoodForestRitual extends ConjureBiomeRitual {

    public static String ID = "ritual_archwood_forest";

    public ArchwoodForestRitual() {
        super(BiomeRegistry.ARCHWOOD_FOREST);
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        // get the pod, if consumed
        Optional<Block> pod = getConsumedItems().stream().map(ItemStack::getItem).filter(item -> item instanceof BlockItem bi && bi.getBlock() instanceof ArchfruitPod).map(item -> ((BlockItem) item).getBlock()).findFirst();
        if (pod.isPresent()) {
            if (pod.get() == BlockRegistry.BOMBEGRANTE_POD.get()) {
                biome = ModWorldgen.Biomes.BLAZING_FOREST_KEY;
            } else if (pod.get() == BlockRegistry.FROSTAYA_POD.get()) {
                biome = ModWorldgen.Biomes.CASCADING_FOREST_KEY;
            } else if (pod.get() == BlockRegistry.MENDOSTEEN_POD.get()) {
                biome = ModWorldgen.Biomes.FLOURISHING_FOREST_KEY;
            } else if (pod.get() == ModItems.FLASHING_POD.get()) {
                biome = ModWorldgen.Biomes.FLASHING_FOREST_KEY;
            }
        }
    }

    @Override
    public BlockState stateForPos(BlockPos nextPos) {
        return nextPos.getY() == getPos().getY() - 1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        boolean pod = getConsumedItems().stream().anyMatch(i -> stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ArchfruitPod);
        return super.canConsumeItem(stack) || (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ArchfruitPod && !pod);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return prefix(ID);
    }

    @Override
    public String getLangName() {
        return "Conjure Island: Archwood Forest";
    }

    @Override
    public String getLangDescription() {
        return "Creates an island of grass and dirt in a circle around the ritual, converting the area to an Archwood Forest. Augmenting with an Elemental Archfruit with convert to a thematic forest. The island will generate with a radius of 7 blocks. Augmenting the ritual with Source Gems will increase the radius by 1 for each gem. Source must be provided nearby as blocks are generated.";
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        if (biome != BiomeRegistry.ARCHWOOD_FOREST)
            tag.putString("biome", biome.location().toString());
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        if (tag.contains("biome"))
            biome = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(tag.getString("biome")));
    }
}
