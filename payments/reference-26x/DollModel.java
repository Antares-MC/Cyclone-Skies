package com.paymentalerts.skin;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

final class DollModel extends Model<Pose> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    static final record Pose(boolean crouching, float age) {


        

        

        


    }

    DollModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityTranslucent);
        this.head = modelPart.getChild("head");
        this.body = modelPart.getChild("body");
        this.rightArm = modelPart.getChild("right_arm");
        this.leftArm = modelPart.getChild("left_arm");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
    }

    public void setupAnim(Pose pose) {
        super.setupAnim(pose);
        float cos = (((float) Math.cos(pose.age() * 0.09f)) * 0.05f) + 0.05f;
        float sin = ((float) Math.sin(pose.age() * 0.067f)) * 0.05f;
        this.rightArm.zRot += cos;
        this.leftArm.zRot -= cos;
        this.rightArm.xRot += sin;
        this.leftArm.xRot -= sin;
        if (pose.crouching()) {
            this.body.xRot = 0.5f;
            this.rightArm.xRot += 0.4f;
            this.leftArm.xRot += 0.4f;
            this.rightLeg.z += 4.0f;
            this.leftLeg.z += 4.0f;
            this.head.y += 4.2f;
            this.body.y += 3.2f;
            this.leftArm.y += 3.2f;
            this.rightArm.y += 3.2f;
        }
    }
}
