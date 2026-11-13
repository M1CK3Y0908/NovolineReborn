package net.optifine.entity.model;

import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapter {
   private Class entityClass;
   private String name;
   private float shadowSize;
   private String[] aliases;

   public String getName() {
      return this.name;
   }

   public abstract IEntityRenderer makeEntityRender(ModelBase modelBase, float f);

   public abstract ModelRenderer getModelRenderer(ModelBase modelBase, String text);

   public ModelAdapter(Class entityClass, String name, float shadowSize, String[] aliases) {
      this.entityClass = entityClass;
      this.name = name;
      this.shadowSize = shadowSize;
      this.aliases = aliases;
   }

   public float getShadowSize() {
      return this.shadowSize;
   }

   public abstract ModelBase makeModel();

   public ModelAdapter(Class entityClass, String name, float shadowSize) {
      this.entityClass = entityClass;
      this.name = name;
      this.shadowSize = shadowSize;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public ModelRenderer[] getModelRenderers(ModelBase model) {
      String[] stringArray = this.getModelRendererNames();
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < stringArray.length; index++) {
         String text = stringArray[index];
         ModelRenderer modelRenderer = this.getModelRenderer(model, text);
         if (modelRenderer != null) {
            arrayList.add(modelRenderer);
         }
      }

      return (ModelRenderer[]) arrayList.toArray(new ModelRenderer[arrayList.size()]);
   }

   public Class getEntityClass() {
      return this.entityClass;
   }

   public abstract String[] getModelRendererNames();
}
