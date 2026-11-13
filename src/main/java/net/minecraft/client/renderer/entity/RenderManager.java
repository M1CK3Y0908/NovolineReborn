package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.player.PlayerItemsLayer;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.visual.FreeLook;

public class RenderManager {
   public float playerViewX;
   private double renderPosY;
   public Entity livingPlayer;
   private Map<Class, Render> entityRenderMap = Maps.newHashMap();
   private double renderPosX;
   public double viewerPosZ;
   private FontRenderer textRenderer;
   private Map<String, RenderPlayer> skinMap = Maps.newHashMap();
   private boolean renderOutlines = false;
   private boolean renderShadow = true;
   private boolean debugBoundingBox = false;
   public World worldObj;
   private double renderPosZ;
   public double viewerPosY;
   public Render renderRender = null;
   public TextureManager renderEngine;
   public Entity pointedEntity;
   public GameSettings options;
   private RenderPlayer playerRenderer;
   public double viewerPosX;
   public float playerViewY;

   public RenderManager(TextureManager renderEngineIn, RenderItem itemRendererIn) {
      this.renderEngine = renderEngineIn;
      this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
      this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
      this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
      this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
      this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
      this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
      this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
      this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
      this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
      this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
      this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
      this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
      this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
      this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
      this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
      this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
      this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
      this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
      this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
      this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
      this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
      this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
      this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
      this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
      this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
      this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
      this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
      this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
      this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
      this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
      this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
      this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
      this.entityRenderMap.put(Entity.class, new RenderEntity(this));
      this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
      this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRendererIn));
      this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
      this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
      this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRendererIn));
      this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRendererIn));
      this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRendererIn));
      this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRendererIn));
      this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRendererIn));
      this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRendererIn));
      this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRendererIn));
      this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
      this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
      this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
      this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRendererIn));
      this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
      this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
      this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
      this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
      this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
      this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
      this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
      this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
      this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
      this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
      this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
      this.playerRenderer = new RenderPlayer(this);
      this.skinMap.put("default", this.playerRenderer);
      this.skinMap.put("slim", new RenderPlayer(this, true));
      PlayerItemsLayer.register(this.skinMap);
      if (Reflector.reflectorMethod106.isActive()) {
         Reflector.createObject2(Reflector.reflectorMethod106, this, this.entityRenderMap);
      }
   }

   public double getDistanceToCamera(double x, double y, double z) {
      double d = x - this.viewerPosX;
      double d2 = y - this.viewerPosY;
      double d3 = z - this.viewerPosZ;
      return d * d + d2 * d2 + d3 * d3;
   }

   public boolean checkEntity2(Entity entity2, double d, double d2, double d3, float f, float f2) {
      Render render2 = null;

      try {
         render2 = this.getEntityRenderObject(entity2);
         if (render2 != null && this.renderEngine != null) {
            try {
               if (render2 instanceof RendererLivingEntity) {
                  ((RendererLivingEntity)render2).setRenderOutlines(this.renderOutlines);
               }

               if (CustomEntityModels.isActive()) {
                  this.renderRender = render2;
               }

               render2.doRender2(entity2, d, d2, d3, f, f2);
            } catch (Throwable throwable) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity in world"));
            }

            try {
               if (!this.renderOutlines) {
                  render2.doRenderShadowAndFire(entity2, d, d2, d3, f, f2);
               }
            } catch (Throwable throwable2) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Post-rendering entity in world"));
            }

            if (this.debugBoundingBox && !entity2.isInvisible()) {
               try {
                  this.renderDebugBoundingBox(entity2, d, d2, d3, f, f2);
               } catch (Throwable throwable3) {
                  throw new ReportedException(CrashReport.makeCrashReport(throwable3, "Rendering entity hitbox in world"));
               }
            }
         } else if (this.renderEngine != null) {
            return false;
         }

         return true;
      } catch (Throwable throwable4) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable4, "Rendering entity in world");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being rendered");
         entity2.addEntityCrashInfo(crashReportCategory);
         CrashReportCategory crashReportCategory2 = crashReport.makeCategory("Renderer details");
         crashReportCategory2.addCrashSection("Assigned renderer", render2);
         crashReportCategory2.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(d, d2, d3));
         crashReportCategory2.addCrashSection("Rotation", f);
         crashReportCategory2.addCrashSection("Delta", f2);
         throw new ReportedException(crashReport);
      }
   }

   private void renderDebugBoundingBox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Shaders.isShadowPass) {
         GlStateManager.depthMask(false);
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         float f = entityIn.width / 2.0F;
         AxisAlignedBB entityBoundingBox = entityIn.getEntityBoundingBox();
         RenderGlobal.handleAxisAlignedBB(
            new AxisAlignedBB(
               entityBoundingBox.minX - entityIn.posX + x,
               entityBoundingBox.minY - entityIn.posY + y,
               entityBoundingBox.minZ - entityIn.posZ + z,
               entityBoundingBox.maxX - entityIn.posX + x,
               entityBoundingBox.maxY - entityIn.posY + y,
               entityBoundingBox.maxZ - entityIn.posZ + z
            ),
            255,
            255,
            255
         );
         if (entityIn instanceof EntityLivingBase) {
            RenderGlobal.handleAxisAlignedBB(
               new AxisAlignedBB(x - f, y + entityIn.getEyeHeight() - 0.01F, z - f, x + f, y + entityIn.getEyeHeight() + 0.01F, z + f),
               255,
               0,
               0
            );
         }

         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         Vec3 look = entityIn.getLook(partialTicks);
         worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer.pos(x, y + entityIn.getEyeHeight(), z).color(0, 0, 255, 255).endVertex();
         worldRenderer.pos(x + look.xCoord * 2.0, y + entityIn.getEyeHeight() + look.yCoord * 2.0, z + look.zCoord * 2.0).color(0, 0, 255, 255).endVertex();
         instance.draw();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public Map<Class, Render> getEntityRenderMap() {
      return this.entityRenderMap;
   }

   public boolean isDebugBoundingBox() {
      return this.debugBoundingBox;
   }

   public void setEntityRenderMap(Map arg1) {
      this.entityRenderMap = arg1;
   }

   public boolean shouldRender(Entity entityIn, ICamera camera, double camX, double camY, double camZ) {
      Render<Entity> render = this.getEntityRenderObject(entityIn);
      return render != null && render.shouldRender(entityIn, camera, camX, camY, camZ);
   }

   public void renderWitherSkull(Entity entityIn, float partialTicks) {
      double xCoordinate = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double yCoordinate = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double zCoordinate = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      Render<Entity> render = this.getEntityRenderObject(entityIn);
      if (render != null && this.renderEngine != null) {
         int brightnessForRender = entityIn.getBrightnessForRender(partialTicks);
         int i = brightnessForRender % 65536;
         int i2 = brightnessForRender / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, i2 / 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         render.renderName(entityIn, xCoordinate - this.renderPosX, yCoordinate - this.renderPosY, zCoordinate - this.renderPosZ);
      }
   }

   public <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn) {
      if (entityIn instanceof AbstractClientPlayer) {
         String text = ((AbstractClientPlayer)entityIn).getSkinType();
         RenderPlayer renderPlayer = this.skinMap.get(text);
         return (Render)(renderPlayer != null ? renderPlayer : this.playerRenderer);
      } else {
         return this.getEntityClassRenderObject((Class<? extends Entity>)entityIn.getClass());
      }
   }

   public void setPlayerViewY(float playerViewYIn) {
      this.playerViewY = playerViewYIn;
   }

   public Map<String, RenderPlayer> getSkinMap() {
      return Collections.unmodifiableMap(this.skinMap);
   }

   public void setRenderShadow(boolean renderShadowIn) {
      this.renderShadow = renderShadowIn;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   public void set(World worldIn) {
      this.worldObj = worldIn;
   }

   public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
      this.debugBoundingBox = debugBoundingBoxIn;
   }

   public boolean isRenderShadow() {
      return this.renderShadow;
   }

   public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
      this.renderPosX = renderPosXIn;
      this.renderPosY = renderPosYIn;
      this.renderPosZ = renderPosZIn;
   }

   public FontRenderer getFontRenderer() {
      return this.textRenderer;
   }

   public boolean renderEntitySimple(Entity entityIn, float partialTicks) {
      return this.checkEntity(entityIn, partialTicks);
   }

   public boolean checkEntity(Entity entity2, float f) {
      if (entity2.ticksExisted == 0) {
         entity2.lastTickPosX = entity2.posX;
         entity2.lastTickPosY = entity2.posY;
         entity2.lastTickPosZ = entity2.posZ;
      }

      double d = entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * f;
      double d2 = entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * f;
      double d3 = entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * f;
      float f2 = entity2.prevRotationYaw + (entity2.rotationYaw - entity2.prevRotationYaw) * f;
      int brightnessForRender = entity2.getBrightnessForRender(f);
      if (entity2.isBurning()) {
         brightnessForRender = 15728880;
      }

      int i = brightnessForRender % 65536;
      int i2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i / 1.0F, i2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return this.checkEntity2(entity2, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ, f2, f);
   }

   public boolean renderEntityWithPosYaw(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      return this.checkEntity2(entityIn, x, y, z, entityYaw, partialTicks);
   }

   public void cacheActiveRenderInfo(
      World worldIn, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn, GameSettings optionsIn, float partialTicks
   ) {
      this.worldObj = worldIn;
      this.options = optionsIn;
      this.livingPlayer = livingPlayerIn;
      this.pointedEntity = pointedEntityIn;
      this.textRenderer = textRendererIn;
      if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase)livingPlayerIn).isPlayerSleeping()) {
         IBlockState blockState = worldIn.getBlockState(new BlockPos(livingPlayerIn));
         Block block2 = blockState.getBlock();
         if (Reflector.checkObject(block2, Reflector.reflectorMethod30, blockState, worldIn, new BlockPos(livingPlayerIn), (EntityLivingBase)livingPlayerIn)) {
            EnumFacing enumFacing = (EnumFacing)Reflector.createObject4(block2, Reflector.reflectorMethod24, blockState, worldIn, new BlockPos(livingPlayerIn));
            int horizontalIndex = enumFacing.getHorizontalIndex();
            this.playerViewY = horizontalIndex * 90 + 180;
            this.playerViewX = 0.0F;
         } else if (block2 == Blocks.bed) {
            int horizontalIndex2 = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
            this.playerViewY = horizontalIndex2 * 90 + 180;
            this.playerViewX = 0.0F;
         }
      } else {
         this.playerViewY = livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks;
         this.playerViewX = livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks;
      }

      if (optionsIn.thirdPersonView == 2) {
         this.playerViewY += 180.0F;
      }

      FreeLook freeLook2 = ModuleManager.freeLook;
      boolean freeLookActive = freeLook2.isFreeLookActive();
      if (freeLookActive) {
         this.playerViewY = freeLook2.getLookYaw();
         this.playerViewX = freeLook2.getLookPitch();
      }

      this.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * partialTicks;
      this.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * partialTicks;
      this.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * partialTicks;
   }

   public <T extends Entity> Render<T> getEntityClassRenderObject(Class<? extends Entity> entityClass) {
      Render<? extends Entity> render = this.entityRenderMap.get(entityClass);
      if (render == null && entityClass != Entity.class) {
         render = this.getEntityClassRenderObject((Class<? extends Entity>)entityClass.getSuperclass());
         this.entityRenderMap.put(entityClass, render);
      }

      return (Render<T>)render;
   }
}
