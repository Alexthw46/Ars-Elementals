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

public class BlackstoneFormation extends Feature<NoneFeatureConfiguration> {
    public BlackstoneFormation(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private boolean isValidGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.STONE) || state.is(Blocks.BLACKSTONE);
    }

    private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
    private static final BlockState GILDED_BLACKSTONE = Blocks.GILDED_BLACKSTONE.defaultBlockState();
    private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Find valid ground
        BlockPos basePos = origin;
        while (basePos.getY() > level.getMinBuildHeight() + 3) {
            BlockState stateBelow = level.getBlockState(basePos.below());
            if (!level.isEmptyBlock(basePos.below()) && isValidGround(stateBelow)) {
                break;
            }
            basePos = basePos.below();
        }

        if (basePos.getY() <= level.getMinBuildHeight() + 3) {
            return false; // Abort if no valid ground found
        }

        // Generate a base mound
        int baseRadius = random.nextInt(3) + 3; // Radius (3-5 blocks)
        int height = baseRadius / 2 + random.nextInt(3) + 2;     // Height (4-6 blocks)

        // Make sure the formation is not floating, place blocks below the first layer to fill all air
        int fillBase = 0;
        BlockPos basePosCopy = new BlockPos(basePos);
        while (level.isEmptyBlock(basePos.below())) {
            level.setBlock(basePos.below(), BLACKSTONE, 3);
            basePos = basePos.below();
            fillBase++;
        }

        // Fill the foundations with blackstone
        for (int y = 0; y < fillBase; y++) {
            for (int dx = -baseRadius; dx <= baseRadius; dx++) {
                for (int dz = -baseRadius; dz <= baseRadius; dz++) {
                    BlockPos pos = basePos.offset(dx, y, dz);
                    if (dx * dx + dz * dz <= baseRadius * baseRadius + random.nextInt(2)) { // Irregular edges
                        BlockState block = BLACKSTONE;

                        // Add random variations
                        if (random.nextFloat() <= 0.1) block = GILDED_BLACKSTONE;

                        level.setBlock(pos, block, 3);
                    }
                }
            }
        }

        // Restore the original base position and build the formation
        for (int y = 0; y < height; y++) {
            int radius = baseRadius - y; // Gradual tapering
            if (radius <= 0) {
                height = y; // Prevents floating blocks
                break;
            }

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = basePos.offset(dx, y, dz);
                    if (dx * dx + dz * dz <= radius * radius + random.nextInt(2)) { // Irregular edges
                        BlockState block = BLACKSTONE;

                        // Add random variations
                        if (random.nextFloat() <= 0.1) block = GILDED_BLACKSTONE;

                        level.setBlock(pos, block, 3);
                    }
                }
            }
        }


        // Add lava pool or flow
        BlockPos lavaCenter = basePos.above(height - 1);

        // Clear space above the lava pool
        for (int dy = 0; dy < 2; dy++) {
            BlockPos clearPos = lavaCenter.above(dy);
            if (!level.getBlockState(clearPos).isAir()) {
                level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // Place the central lava pool
        level.setBlock(lavaCenter, LAVA, 3);

        // Lava flow down the side (randomized direction)
        if (random.nextBoolean()) {
            BlockPos flowStart = lavaCenter.mutable();
            for (int i = 0; i < 3; i++) {
                BlockPos flowPos = flowStart.offset(random.nextInt(3) - 1, -i, random.nextInt(3) - 1);
                if (level.isEmptyBlock(flowPos) || level.getBlockState(flowPos).canBeReplaced()) {
                    level.setBlock(flowPos, LAVA, 3);

                    // Clear space above the flow
                    BlockPos clearAbove = flowPos.above();
                    if (!level.getBlockState(clearAbove).isAir() && level.getBlockState(clearAbove).getFluidState().isEmpty()) {
                        level.setBlock(clearAbove, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        return true;
    }
}
