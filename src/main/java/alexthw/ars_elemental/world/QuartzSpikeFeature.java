package alexthw.ars_elemental.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class QuartzSpikeFeature extends Feature<NoneFeatureConfiguration> {
    public QuartzSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private boolean isValidGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.STONE) || state.is(Blocks.SAND) || state.is(Blocks.GRAVEL);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Search for valid ground
        BlockPos blockpos = origin;
        while (blockpos.getY() > level.getMinBuildHeight() + 3) {
            BlockState stateBelow = level.getBlockState(blockpos.below());
            if (!level.isEmptyBlock(blockpos.below()) && isValidGround(stateBelow)) {
                break;
            }
            blockpos = blockpos.below();
        }

        // Abort if no valid ground found
        if (blockpos.getY() <= level.getMinBuildHeight() + 3) {
            return false;
        }

        // drop down so we don't make it floating on a border
        blockpos = blockpos.below();

        // Decide spike properties
        int height = random.nextInt(6) + 4; // Height between 4 and 10
        int baseRadius = random.nextInt(2) + 1; // Base radius of 1 or 2 blocks
        int quartzStart = height * 2 / 3; // Top 1/3 of the spike is quartz

        // Generate the spike
        for (int y = 0; y < height; y++) {
            int radius = baseRadius - (y / (height / baseRadius));

            if (radius < 0 || y >= height - 2) radius = 0; // Ensure radius 0 is minimum (only the center block)

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius) { // Inside circle
                        BlockPos pos = blockpos.offset(dx, y, dz);

                        // Choose material based on height
                        if (y >= quartzStart) {
                            // Top 1/3: Quartz
                            level.setBlock(pos, Blocks.QUARTZ_BLOCK.defaultBlockState(), 2);
                        } else {
                            // Bottom 2/3: Calcite or Diorite
                            BlockState whiteRock = random.nextBoolean() ? Blocks.CALCITE.defaultBlockState() : Blocks.DIORITE.defaultBlockState();
                            level.setBlock(pos, whiteRock, 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}