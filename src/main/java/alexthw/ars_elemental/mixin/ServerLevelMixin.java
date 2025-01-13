package alexthw.ars_elemental.mixin;

import alexthw.ars_elemental.ConfigHandler;
import alexthw.ars_elemental.common.entity.spells.FlashLightning;
import alexthw.ars_elemental.datagen.AETagsProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow
    protected abstract BlockPos findLightningTargetAround(BlockPos pos);

    @Inject(at = @At("HEAD"), method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V")
    public void tickChunk(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {
        if (!ConfigHandler.Common.LIGHTNINGS_BIOME.get()) return;
        var level = pChunk.getLevel();
        if (level.isClientSide) return;
        var chunkpos = pChunk.getPos();
        int x = chunkpos.getMinBlockX();
        int z = chunkpos.getMinBlockZ();
        if (level.random.nextInt(2000) == 0 && level.isRainingAt(new BlockPos(x, 120, z))) {
            var biome = pChunk.getLevel().getBiomeManager().getBiome(new BlockPos(x,120,z));
            if (biome.is(AETagsProvider.AEBiomeTagsProvider.FLASHING_BIOME)) {

                BlockPos blockpos = findLightningTargetAround(level.getBlockRandomPos(x, 0, z, 15));
                if (blockpos != null) {
                    FlashLightning lightning = new FlashLightning(level);
                    lightning.moveTo(Vec3.atBottomCenterOf(blockpos));
                    lightning.setVisualOnly(false);
                    level.addFreshEntity(lightning);
                }
            }
        }
    }
}
