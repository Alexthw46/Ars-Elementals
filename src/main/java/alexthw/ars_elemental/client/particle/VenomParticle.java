package alexthw.ars_elemental.client.particle;

import alexthw.ars_elemental.registry.ModParticles;
import alexthw.ars_elemental.registry.ModPotions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.EffectParticleModificationEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT)
public class VenomParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public VenomParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, SpriteSet spriteSet) {
        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 20;
        friction = 0.5f;
        this.spriteSet = spriteSet;
        pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        if ((age % 5) == 0) {
            setSpriteFromAge(spriteSet);
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @NotNull
    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteSet) {
        return (data, level, x, y, z, dx, dy, dz) -> new VenomParticle(level, x, y, z, dx, dy, dz, spriteSet);
    }

    @SubscribeEvent
    static public void swapParticle(EffectParticleModificationEvent event) {
        if (event.getEffect().getEffect() == ModPotions.VENOM) {
            event.setParticleOptions(ModParticles.VENOM.get());
        }
    }

}
