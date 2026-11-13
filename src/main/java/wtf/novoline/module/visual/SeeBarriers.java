package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.EnumParticleTypes;
import wtf.novoline.event.SpawnParticleEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;

public class SeeBarriers extends Module {
   @EventLink
   public final Listener<SpawnParticleEvent> onSpawnParticle = spawnParticleEvent -> {
      if (spawnParticleEvent.id == EnumParticleTypes.BARRIER.getParticleID()) {
         spawnParticleEvent.cancel();
      }
   };

   @Override
   protected void onEnable() {
      if (minecraft.effectRenderer != null) {
         for (EntityFX entityFX : minecraft.effectRenderer.fxLayers[1][1]) {
            if (entityFX instanceof Barrier) {
               entityFX.setDead();
            }
         }
      }
   }

   public SeeBarriers() {
      super(Category.VISUAL, "SeeBarriers", "");
   }

   @Override
   protected void onToggled() {
      if (minecraft.renderGlobal != null) {
         minecraft.renderGlobal.loadRenderers();
      }
   }
}
