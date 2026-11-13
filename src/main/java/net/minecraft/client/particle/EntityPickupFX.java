package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.world.World;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;

public class EntityPickupFX extends EntityFX {
   private RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
   private Entity entityToPickUp;
   private Entity entityPickingUp;
   private int maxAge;
   private int age;
   private float yOffset;

   public EntityPickupFX(World worldIn, Entity entity2, Entity entity3, float f) {
      super(worldIn, entity2.posX, entity2.posY, entity2.posZ, entity2.motionX, entity2.motionY, entity2.motionZ);
      this.entityToPickUp = entity2;
      this.entityPickingUp = entity3;
      this.maxAge = 3;
      this.yOffset = f;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      Program program = null;
      if (Config.isShaders()) {
         program = Shaders.activeProgram;
         Shaders.nextEntity(this.entityToPickUp);
      }

      float f = (this.age + partialTicks) / this.maxAge;
      f *= f;
      double posX2 = this.entityToPickUp.posX;
      double posY2 = this.entityToPickUp.posY;
      double posZ2 = this.entityToPickUp.posZ;
      double d = this.entityPickingUp.lastTickPosX + (this.entityPickingUp.posX - this.entityPickingUp.lastTickPosX) * partialTicks;
      double d2 = this.entityPickingUp.lastTickPosY + (this.entityPickingUp.posY - this.entityPickingUp.lastTickPosY) * partialTicks + this.yOffset;
      double d3 = this.entityPickingUp.lastTickPosZ + (this.entityPickingUp.posZ - this.entityPickingUp.lastTickPosZ) * partialTicks;
      double d4 = posX2 + (d - posX2) * f;
      double d5 = posY2 + (d2 - posY2) * f;
      double d6 = posZ2 + (d3 - posZ2) * f;
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int i = brightnessForRender % 65536;
      int i2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, i2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      d4 -= interpPosX;
      d5 -= interpPosY;
      d6 -= interpPosZ;
      this.renderManager.renderEntityWithPosYaw(this.entityToPickUp, (float)d4, (float)d5, (float)d6, this.entityToPickUp.rotationYaw, partialTicks);
      if (Config.isShaders()) {
         Shaders.setEntityId((Entity)null);
         Shaders.useProgram(program);
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   @Override
   public void onUpdate() {
      this.age++;
      if (this.age == this.maxAge) {
         this.setDead();
      }
   }
}
