package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.expr.IExpression;

public class ModelResolver implements IModelResolver {
   private ModelAdapter modelAdapter;
   private ModelBase model;
   private ModelRenderer thisModelRenderer;
   private CustomModelRenderer[] customModelRenderers;
   private ModelRenderer partModelRenderer;
   private IParameterResolver renderResolver;

   @Override
   public ModelVariableFloat getModelVariable(String name) {
      String[] stringArray = Config.tokenize(name, ".");
      if (stringArray.length != 2) {
         return null;
      }

      String text = stringArray[0];
      String text2 = stringArray[1];
      ModelRenderer modelRenderer = this.getModelRenderer(text);
      if (modelRenderer == null) {
         return null;
      }

      ModelVariableType modelVariableType = ModelVariableType.parse(text2);
      return modelVariableType == null ? null : new ModelVariableFloat(name, modelRenderer, modelVariableType);
   }

   public void setPartModelRenderer(ModelRenderer partModelRenderer) {
      this.partModelRenderer = partModelRenderer;
   }

   @Override
   public IExpression getExpression(String name) {
      IExpression expression = this.getModelVariable(name);
      if (expression != null) {
         return expression;
      }

      IExpression expression2 = this.renderResolver.getParameter(name);
      return expression2 != null ? expression2 : null;
   }

   public void setThisModelRenderer(ModelRenderer thisModelRenderer) {
      this.thisModelRenderer = thisModelRenderer;
   }

   public ModelResolver(ModelAdapter modelAdapter, ModelBase model, CustomModelRenderer[] customModelRenderers) {
      this.modelAdapter = modelAdapter;
      this.model = model;
      this.customModelRenderers = customModelRenderers;
      Class oclass = modelAdapter.getEntityClass();
      if (TileEntity.class.isAssignableFrom(oclass)) {
         this.renderResolver = new RenderResolverTileEntity();
      } else {
         this.renderResolver = new RenderResolverEntity();
      }
   }

   public ModelRenderer getModelRenderer(String name) {
      if (name == null) {
         return null;
      }

      if (name.indexOf(":") >= 0) {
         String[] stringArray = Config.tokenize(name, ":");
         ModelRenderer modelRenderer = this.getModelRenderer(stringArray[0]);

         for (int innerIndex = 1; innerIndex < stringArray.length; innerIndex++) {
            String text = stringArray[innerIndex];
            ModelRenderer modelRenderer2 = modelRenderer.getChildDeep(text);
            if (modelRenderer2 == null) {
               return null;
            }

            modelRenderer = modelRenderer2;
         }

         return modelRenderer;
      } else {
         if (this.thisModelRenderer != null && name.equals("this")) {
            return this.thisModelRenderer;
         }

         if (this.partModelRenderer != null && name.equals("part")) {
            return this.partModelRenderer;
         }

         ModelRenderer modelRenderer3 = this.modelAdapter.getModelRenderer(this.model, name);
         if (modelRenderer3 != null) {
            return modelRenderer3;
         }

         for (int index = 0; index < this.customModelRenderers.length; index++) {
            CustomModelRenderer innerIndex = this.customModelRenderers[index];
            ModelRenderer modelRenderer4 = innerIndex.getModelRenderer();
            if (name.equals(modelRenderer4.getId())) {
               return modelRenderer4;
            }

            ModelRenderer modelRenderer5 = modelRenderer4.getChildDeep(name);
            if (modelRenderer5 != null) {
               return modelRenderer5;
            }
         }

         return null;
      }
   }
}
