package alexthw.ars_elemental.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
        return state.is(BlockTags.DIRT) || state.is(Blocks.STONE);
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
        if (!isValidGround(level.getBlockState(origin.below()))) {
            return false; // Abort if no valid ground found
        }

        // Generate a base mound
        int baseRadius = random.nextInt(3) + 3; // Radius (3-5 blocks)
        int height = baseRadius / 2 + random.nextInt(3) + 2;     // Height (4-6 blocks)

        // Ensure no floating edges by checking and filling below the entire base layer with organic tapering
        BlockPos basePosCopy = new BlockPos(origin); // Keep a copy of the base position
        BlockPos basePos = origin.above(); // Start one block above the ground
        int currentRadius = baseRadius; // Start with the base radius

        while (currentRadius > 0) {
            boolean hasFloatingBlocks = false;

            // Check and fill the border with randomness for irregularity
            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    if (dx * dx + dz * dz <= currentRadius * currentRadius + random.nextInt(2)) { // Irregular edges

                        // Check and fill below if the block is floating
                        BlockPos fillPos = basePos.offset(dx, 0, dz);
                        while (level.isEmptyBlock(fillPos.below()) && fillPos.getY() > level.getMinBuildHeight()) {
                            level.setBlock(fillPos.below(), random.nextFloat() <= 0.1 ? GILDED_BLACKSTONE : BLACKSTONE, 3);
                            fillPos = fillPos.below();
                            hasFloatingBlocks = true; // Indicates we need to taper further
                        }
                    }
                }
            }

            // Stop tapering if no floating blocks were found in the current radius
            if (!hasFloatingBlocks) break;

            // Gradually adjust the radius for the next layer with some randomness
            basePos = basePos.below();
            currentRadius += random.nextBoolean() ? 1 : 0; // Occasionally expand radius for a natural look
        }


        // Restore the original base position and build the formation
        basePos = basePosCopy;
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
