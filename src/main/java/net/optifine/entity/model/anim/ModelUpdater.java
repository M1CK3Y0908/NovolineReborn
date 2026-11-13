package net.optifine.entity.model.anim;

public class ModelUpdater {
   private ModelVariableUpdater[] modelVariableUpdaters;

   public void update() {
      for (int i = 0; i < this.modelVariableUpdaters.length; i++) {
         this.modelVariableUpdaters[i].update();
      }
   }

   public ModelUpdater(ModelVariableUpdater[] modelVariableUpdaters) {
      this.modelVariableUpdaters = modelVariableUpdaters;
   }

   public boolean initialize(IModelResolver mr) {
      for (int i = 0; i < this.modelVariableUpdaters.length; i++) {
         if (!this.modelVariableUpdaters[i].initialize(mr)) {
            return false;
         }
      }

      return true;
   }
}
