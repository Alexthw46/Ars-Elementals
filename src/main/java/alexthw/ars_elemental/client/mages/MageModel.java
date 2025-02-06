package alexthw.ars_elemental.client.mages;

import alexthw.ars_elemental.common.entity.mages.EntityMageBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MageModel<M extends EntityMageBase> extends HumanoidModel<M> {

    public MageModel(ModelPart pRoot) {
        super(pRoot, RenderType::entityTranslucent);
    }

    @Override
    public void setupAnim(@NotNull M entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.currentAnim == 2) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightArm.zRot = (float) (Math.PI * 3.0 / 4.0);
            this.leftArm.zRot = (float) (-Math.PI * 3.0 / 4.0);
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (entity.currentAnim == 1) {
            float f3 = ageInTicks / 60.0F;
            this.head.x = Mth.sin(f3 * 10.0F);
            this.head.y = Mth.sin(f3 * 40.0F) + 0.4F;
            this.rightArm.zRot = (float) (Math.PI / 180.0) * (70.0F + Mth.cos(f3 * 40.0F) * 10.0F);
            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
            this.rightArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.leftArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.body.y = Mth.sin(f3 * 40.0F) * 0.35F;
        }
    }
}
