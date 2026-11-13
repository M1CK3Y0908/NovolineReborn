package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import wtf.novoline.event.RenderGameOverlayEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.render.BlurFramebuffer;
import wtf.novoline.utils.render.FramebufferManager;
import wtf.novoline.utils.render.MaskShader;
import wtf.novoline.utils.render.RenderUtils;

public class Chams extends Module {
   public static boolean renderingChams;
   public ComboSetting chams = settingManager.register(new ComboSetting(this, "Mode", 0, "Simple", "Colored", "Sky"));
   public CheckboxSetting arm = settingManager.register(new CheckboxSetting(this, "Arm", false) {
      @Override
      public boolean shouldShow() {
         return Chams.this.chams.getCurrentElement() == 1;
      }
   });
   public CheckboxSetting wireframe = settingManager.register(new CheckboxSetting(this, "Wireframe", false) {
      @Override
      public boolean shouldShow() {
         return Chams.this.chams.getCurrentElement() == 1;
      }
   });
   private boolean renderedTargets;
   public ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -12986881) {
      @Override
      public boolean shouldShow() {
         return Chams.this.chams.getCurrentElement() == 1;
      }
   });
   private BlurFramebuffer blurFramebuffer;
   private BlurFramebuffer maskFramebuffer;
   private final FramebufferManager framebufferManager = new FramebufferManager();
   @EventLink
   private final Listener<RenderGameOverlayEvent> onRenderGameOverlay = renderGameOverlayEvent -> {
      if (this.chams.getCurrentElement() == 2 && this.renderedTargets) {
         GlStateManager.pushMatrix();
         GlStateManager.enableTexture2D();
         MaskShader.maskShader.use();
         ARBShaderObjects.glUniform1iARB(MaskShader.maskShader.getUniformLocation("u_texture"), this.blurFramebuffer.bindTexture());
         ARBShaderObjects.glUniform2fARB(MaskShader.maskShader.getUniformLocation("u_texture_ratio"), 1.0F, 1.0F);
         ARBShaderObjects.glUniform2fARB(MaskShader.maskShader.getUniformLocation("u_texture_offset"), 0.0F, 0.0F);
         ARBShaderObjects.glUniform1iARB(MaskShader.maskShader.getUniformLocation("u_mask_texture"), this.maskFramebuffer.bindTextureUnit(8));
         ARBShaderObjects.glUniform1fARB(MaskShader.maskShader.getUniformLocation("u_alpha"), 1.0F);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         RenderUtils.setColor(-1);
         GlStateManager.disableLighting();
         RenderUtils.drawFullscreenQuad(renderGameOverlayEvent.scaledResolution);
         ARBShaderObjects.glUseProgramObjectARB(0);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.enableBlend();
      }
   };
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (this.chams.getCurrentElement() == 2) {
         this.framebufferManager.resizeFreeFramebuffers();
         this.maskFramebuffer = this.framebufferManager.acquire("chams-mask");
         this.maskFramebuffer.bindFramebuffer();
         GlStateManager.enableColorMaterial();
         RenderUtils.setColor(-1);
         renderingChams = true;
         this.renderedTargets = this.renderPlayers(renderWorldEvent.partialTicks);
         renderingChams = false;
         GlStateManager.disableColorMaterial();
         if (!this.renderedTargets) {
            minecraft.getFramebuffer().bindFramebuffer(true);
         } else {
            this.blurFramebuffer = this.framebufferManager.acquire("chams-skybox", -16777216);
            this.blurFramebuffer.bindFramebuffer();
            double d = 100.0;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, minecraft.thePlayer.getEyeHeight(), 0.0F);
            GlStateManager.rotate((float)(System.currentTimeMillis() % 144000L) / 400.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)InterpolationUtils.oscillate(0.0, 5.0, 8000L), 1.0F, 0.0F, 0.0F);
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            GlStateManager.enableTexture2D();
            RenderUtils.setColor(-1);
            GlStateManager.disableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14lf.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(d, d, -d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(d, -d, -d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(d, -d, d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(d, d, d).tex(1.0, 0.0).endVertex();
            instance.draw();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14rt.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-d, d, d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(-d, -d, d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(-d, -d, -d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(-d, d, -d).tex(1.0, 0.0).endVertex();
            instance.draw();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14bk.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(d, d, d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(d, -d, d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(-d, -d, d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(-d, d, d).tex(1.0, 0.0).endVertex();
            instance.draw();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14ft.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-d, d, -d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(-d, -d, -d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(d, -d, -d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(d, d, -d).tex(1.0, 0.0).endVertex();
            instance.draw();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14up.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(d, d, d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(-d, d, d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(-d, d, -d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(d, d, -d).tex(1.0, 0.0).endVertex();
            instance.draw();
            minecraft.getTextureManager().bindTexture(new ResourceLocation("novoline", "skybox/space1/space_14dn.png"));
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-d, -d, d).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(d, -d, d).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(d, -d, -d).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(-d, -d, -d).tex(1.0, 0.0).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableLighting();
            minecraft.getFramebuffer().bindFramebuffer(true);
            this.blurFramebuffer.release();
         }

         this.maskFramebuffer.release();
      }
   };

   public Chams() {
      super(Category.VISUAL, "Chams", "");
   }

   private boolean renderPlayers(float f) {
      boolean flag = false;

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0) {
            double d = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * f;
            double d2 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * f;
            double d3 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * f;
            float f2 = entityPlayer.prevRotationYaw + (entityPlayer.rotationYaw - entityPlayer.prevRotationYaw) * f;
            Render entityRenderObject = minecraft.getRenderManager().getEntityRenderObject(entityPlayer);
            if (entityRenderObject != null) {
               entityRenderObject.doRender2(
                  entityPlayer,
                  d - TileEntityRendererDispatcher.staticPlayerX,
                  d2 - TileEntityRendererDispatcher.staticPlayerY,
                  d3 - TileEntityRendererDispatcher.staticPlayerZ,
                  f2,
                  f
               );
            }

            flag = true;
         }
      }

      return flag;
   }

   @Override
   protected void onToggled() {
      renderingChams = false;
   }
}
