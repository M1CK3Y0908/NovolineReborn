package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.utils.render.RenderUtils;

public class MobESP extends Module {
   private final CheckboxSetting mobESP = settingManager.register(new CheckboxSetting(this, "Mobs", true));
   private final CheckboxSetting animals = settingManager.register(new CheckboxSetting(this, "Animals", true));
   private final ColorSetting mobsColor = settingManager.register(new ColorSetting(this, "Mobs color", -138329) {
      @Override
      public boolean shouldShow() {
         return MobESP.this.mobESP.isEnabled();
      }
   });
   private final ColorSetting animalsColor = settingManager.register(new ColorSetting(this, "Animals color", -7405631) {
      @Override
      public boolean shouldShow() {
         return MobESP.this.animals.isEnabled();
      }
   });
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GL11.glLineWidth(1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GL11.glEnable(2848);
      ArrayList arrayList = new ArrayList(25);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      int i = RenderUtils.withAlpha(this.mobsColor.getColor(), 76);
      int i2 = RenderUtils.withAlpha(this.animalsColor.getColor(), 76);

      for (Entity entity2 : minecraft.theWorld.loadedEntityList) {
         double d = (entity2.posX - entity2.prevPosX) * (renderWorldEvent.partialTicks - 1.0F);
         double d2 = (entity2.posY - entity2.prevPosY) * (renderWorldEvent.partialTicks - 1.0F);
         double d3 = (entity2.posZ - entity2.prevPosZ) * (renderWorldEvent.partialTicks - 1.0F);
         if (this.mobESP.isEnabled() && (entity2 instanceof EntityMob || entity2 instanceof EntitySlime || entity2 instanceof EntityGhast)) {
            this.putBox(worldRenderer, entity2.getEntityBoundingBox().offset(d, d2, d3), i);
            arrayList.add(entity2);
         }

         if (this.animals.isEnabled()
            && (
               entity2 instanceof EntityAnimal
                  || entity2 instanceof EntityWaterMob
                  || entity2 instanceof EntityBat
                  || entity2 instanceof EntityVillager
                  || entity2 instanceof EntityGolem
            )) {
            this.putBox(worldRenderer, entity2.getEntityBoundingBox().offset(d, d2, d3), i2);
            arrayList.add(entity2);
         }
      }

      instance.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

      for (Entity entity3 : (Iterable<Entity>) arrayList) {
         double d4 = (entity3.posX - entity3.prevPosX) * (renderWorldEvent.partialTicks - 1.0F);
         double d5 = (entity3.posY - entity3.prevPosY) * (renderWorldEvent.partialTicks - 1.0F);
         double d6 = (entity3.posZ - entity3.prevPosZ) * (renderWorldEvent.partialTicks - 1.0F);
         if (entity3 instanceof EntityMob || entity3 instanceof EntitySlime || entity3 instanceof EntityGhast) {
            this.drawBox(worldRenderer, entity3.getEntityBoundingBox().offset(d4, d5, d6), this.mobsColor.getColor());
         } else if (entity3 instanceof EntityAnimal
            || entity3 instanceof EntityWaterMob
            || entity3 instanceof EntityBat
            || entity3 instanceof EntityVillager
            || entity3 instanceof EntityGolem) {
            this.drawBox(worldRenderer, entity3.getEntityBoundingBox().offset(d4, d5, d6), this.animalsColor.getColor());
         }
      }

      instance.draw();
      GL11.glDisable(2848);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   };

   public MobESP() {
      super(Category.VISUAL, "MobESP", "Подсвечивает мобов");
   }

   private void putBox(WorldRenderer worldRenderer, AxisAlignedBB axisAlignedBB, int i) {
      axisAlignedBB = axisAlignedBB.expand(0.002, 0.002, 0.002)
         .offset(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
      float f = (i >> 24 & 0xFF) / 255.0F;
      float f2 = (i >> 16 & 0xFF) / 255.0F;
      float f3 = (i >> 8 & 0xFF) / 255.0F;
      float f4 = (i & 0xFF) / 255.0F;
      this.putFilledBoxVertices(worldRenderer, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, f2, f3, f4, f);
   }

   private void drawBox(WorldRenderer worldRenderer, AxisAlignedBB axisAlignedBB, int i) {
      axisAlignedBB = axisAlignedBB.expand(0.002, 0.002, 0.002)
         .offset(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
      float f = (i >> 24 & 0xFF) / 255.0F;
      float f2 = (i >> 16 & 0xFF) / 255.0F;
      float f3 = (i >> 8 & 0xFF) / 255.0F;
      float f4 = (i & 0xFF) / 255.0F;
      this.putOutlineBoxVertices(worldRenderer, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, f2, f3, f4, f);
   }

   private void putFilledBoxVertices(
      WorldRenderer worldRenderer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
   }

   private void putOutlineBoxVertices(
      WorldRenderer worldRenderer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
   }
}
