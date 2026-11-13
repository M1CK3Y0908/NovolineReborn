package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.animation.Easing;
import wtf.novoline.utils.render.RenderUtils;

public class DamageParticles extends Module {
   private final ComboSetting damageParticles = settingManager.register(new ComboSetting(this, "Works on", 0, "All", "Target"));
   private final List<DamageParticles.Holder> particles = new ArrayList<>();
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      this.particles.removeIf(holder -> holder.expired);
      Iterator iterator2 = this.particles.iterator();

      while (iterator2.hasNext()) {
         ((DamageParticles.Holder)iterator2.next()).render();
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      int currentElement = this.damageParticles.getCurrentElement();

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (entityPlayer != minecraft.thePlayer) {
            if (currentElement == 1) {
               if (!ModuleManager.killaura.isEnabled()) {
                  break;
               }

               EntityLivingBase entityLivingBase2 = ModuleManager.killaura.entityLivingBase;
               if (entityLivingBase2 == null) {
                  break;
               }

               if (entityLivingBase2 != entityPlayer) {
                  continue;
               }
            }

            if (entityPlayer.previousHealth != -1.0F) {
               float f2 = entityPlayer.getHealth() - entityPlayer.previousHealth;
               if (f2 != 0.0F) {
                  this.particles.add(new DamageParticles.Holder(f2, entityPlayer.posX, entityPlayer.posY + entityPlayer.getEyeHeight() - 0.3F, entityPlayer.posZ));
               }
            }
         }
      }
   };

   public DamageParticles() {
      super(Category.VISUAL, "DamageParticles", "");
   }

   static Minecraft access$000() {
      return minecraft;
   }

   static Minecraft access$100() {
      return minecraft;
   }

   static Minecraft access$200() {
      return minecraft;
   }

   static Minecraft access$300() {
      return minecraft;
   }

   static final class Holder {
      private final Animation animation = new Animation();
      private final Animation animation2 = new Animation();
      public float offsetZ;
      public int unusedInt;
      private final Animation animation3 = new Animation();
      public float offsetY;
      private final Animation animation4 = new Animation();
      private final Animation animation5 = new Animation();
      public final float damage;
      public final double x;
      public double y;
      public final double z;
      public final long spawnTime;
      public float scale;
      public boolean expired;
      public float targetOffsetX;
      public float targetOffsetY;
      public float offsetX;
      public float targetOffsetZ;

      public void render() {
         if (!this.expired && !this.animation.hasFinished()) {
            this.scale = (float)this.animation2.getValue();
            this.offsetX = (float)this.animation3.getValue();
            this.offsetY = (float)this.animation4.getValue();
            this.offsetZ = (float)this.animation5.getValue();
            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
            GlStateManager.enableAlpha();
            GlStateManager.translate(
               this.x + this.offsetX - TileEntityRendererDispatcher.staticPlayerX,
               this.y + this.offsetY - TileEntityRendererDispatcher.staticPlayerY,
               this.z + this.offsetZ - TileEntityRendererDispatcher.staticPlayerZ
            );
            GlStateManager.rotate(-DamageParticles.access$200().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(
               DamageParticles.access$100().getRenderManager().playerViewX,
               DamageParticles.access$000().gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F,
               0.0F,
               0.0F
            );
            GlStateManager.scale(-this.scale, -this.scale, this.scale);
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            GlStateManager.alphaFunc(516, 0.001F);
            DamageParticles.access$300()
               .fontRendererObj
               .drawStringWithShadow(
                  String.format("%.1f", this.damage), 0.0F, 0.0F, RenderUtils.withAlphaFraction(this.damage < 0.0F ? -12469 : -12657535, 1.0F)
               );
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.popMatrix();
            this.animation.update();
            this.animation2.update();
            this.animation3.update();
            this.animation4.update();
            this.animation5.update();
         } else {
            this.expired = true;
         }
      }

      public Holder(float f2, double d, double d2, double d3) {
         this.damage = f2;
         this.x = d;
         this.y = d2;
         this.z = d3;
         this.spawnTime = System.currentTimeMillis();
         this.scale = 0.0F;
         this.expired = false;
         this.targetOffsetX = (float)ThreadLocalRandom.current().nextDouble(-0.6, 0.6);
         this.targetOffsetY = (float)ThreadLocalRandom.current().nextDouble(-0.6, 0.6);
         this.targetOffsetZ = (float)ThreadLocalRandom.current().nextDouble(-0.6, 0.6);
         this.animation.start(1.0, 0.02, 1600L, Easing.linear);
         this.animation2.start(0.0, ThreadLocalRandom.current().nextDouble(0.03, 0.06), 900L, Easing.backOut);
         this.animation3.start(0.0, this.targetOffsetX, 1200L, Easing.backOut);
         this.animation4.start(0.0, this.targetOffsetY, 1200L, Easing.backOut);
         this.animation5.start(0.0, this.targetOffsetZ, 1200L, Easing.backOut);
      }
   }
}
