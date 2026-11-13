package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;

public class ModelAdapterChicken extends ModelAdapter {
   @Override
   public ModelBase makeModel() {
      return new ModelChicken();
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "right_leg", "left_leg", "right_wing", "left_wing", "bill", "chin"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderChicken(renderManager, modelBase, shadowSize);
   }

   public ModelAdapterChicken() {
      super(EntityChicken.class, "chicken", 0.3F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
      if (!(model instanceof ModelChicken modelChicken)) {
         return null;
      } else {
         return modelPart.equals("head")
            ? modelChicken.head
            : (
               modelPart.equals("body")
                  ? modelChicken.body
                  : (
                     modelPart.equals("right_leg")
                        ? modelChicken.rightLeg
                        : (
                           modelPart.equals("left_leg")
                              ? modelChicken.leftLeg
                              : (
                                 modelPart.equals("right_wing")
                                    ? modelChicken.rightWing
                                    : (
                                       modelPart.equals("left_wing")
                                          ? modelChicken.leftWing
                                          : (modelPart.equals("bill") ? modelChicken.bill : (modelPart.equals("chin") ? modelChicken.chin : null))
                                    )
                              )
                        )
                  )
            );
      }
   }
}
