package alexthw.ars_elemental.client.mages;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static alexthw.ars_elemental.ArsElemental.prefix;

public class MageRenderer<M extends EntityMageBase> extends HumanoidMobRenderer<M, MageModel<M>> {
    private static final ResourceLocation fireTexture = prefix("textures/entity/fire_mage.png");
    private static final ResourceLocation waterTexture = prefix("textures/entity/water_mage.png");
    private static final ResourceLocation airTexture = prefix("textures/entity/air_mage.png");
    private static final ResourceLocation earthTexture = prefix("textures/entity/earth_mage.png");
    private static final ResourceLocation necroTexture = prefix("textures/entity/necro_mage.png");

    public MageRenderer(EntityRendererProvider.Context context) {
        super(context, new MageModel<>(context.getModelSet().bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new MageModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new MageModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull M pEntity) {
        return switch (pEntity.school.getId()){
            case "fire" ->  fireTexture;
            case "water" -> waterTexture;
            case "air" -> airTexture;
            case "earth" -> earthTexture;
            default -> necroTexture;
        };
    }
}
