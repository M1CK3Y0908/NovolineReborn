package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;
import wtf.novoline.particle.EntityFXChild1.IParticleFactoryImpl2;
import wtf.novoline.particle.EntityFXChild3.IParticleFactoryImpl;
import wtf.novoline.util.FireworkEffects.EntityFXChild3;

public class EffectRenderer {
   public List<EntityFX>[][] fxLayers = new List[4][];
   private List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
   private Random rand = new Random();
   private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();
   protected World worldObj;
   private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
   private TextureManager renderer;

   private void moveToLayer(EntityFX effect, int layerFrom, int layerTo) {
      for (int index = 0; index < 4; index++) {
         if (this.fxLayers[index][layerFrom].contains(effect)) {
            this.fxLayers[index][layerFrom].remove(effect);
            this.fxLayers[index][layerTo].add(effect);
         }
      }
   }

   public EffectRenderer(World worldIn, TextureManager rendererIn) {
      this.worldObj = worldIn;
      this.renderer = rendererIn;

      for (int index = 0; index < 4; index++) {
         this.fxLayers[index] = new List[2];

         for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
            this.fxLayers[index][innerIndex] = Lists.newArrayList();
         }
      }

      this.registerVanillaParticles();
   }

   public void renderParticles(Entity entityIn, float partialTicks) {
      float rotationX = ActiveRenderInfo.getRotationX();
      float rotationZ = ActiveRenderInfo.getRotationZ();
      float rotationYZ = ActiveRenderInfo.getRotationYZ();
      float rotationXY = ActiveRenderInfo.getRotationXY();
      float rotationXZ = ActiveRenderInfo.getRotationXZ();
      EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.alphaFunc(516, 0.003921569F);
      boolean flag = ActiveRenderInfo.getBlockAtEntityViewpoint(this.worldObj, entityIn, partialTicks).getMaterial() == Material.water;

      for (int i = 0; i < 3; i++) {
         for (int i2 = 0; i2 < 2; i2++) {
            final int i3 = i;
            if (!this.fxLayers[i][i2].isEmpty()) {
               switch (i2) {
                  case 0:
                     GlStateManager.depthMask(false);
                     break;
                  case 1:
                     GlStateManager.depthMask(true);
               }

               switch (i) {
                  case 0:
                  default:
                     this.renderer.bindTexture(particleTextures);
                     break;
                  case 1:
                     this.renderer.bindTexture(TextureMap.locationBlocksTexture);
               }

               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

               for (int i4 = 0; i4 < this.fxLayers[i][i2].size(); i4++) {
                  final EntityFX entityFX2 = this.fxLayers[i][i2].get(i4);

                  try {
                     if (flag || !(entityFX2 instanceof EntitySuspendFX)) {
                        entityFX2.renderParticle(worldRenderer, entityIn, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                     }
                  } catch (Throwable throwable) {
                     CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                     CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being rendered");
                     crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
                        public String call() throws Exception {
                           return entityFX2.toString();
                        }
                     });
                     crashReportCategory.addCrashSectionCallable(
                        "Particle Type",
                        new Callable<String>() {
                           public String call() throws Exception {
                              return i3 == 0
                                 ? "MISC_TEXTURE"
                                 : (i3 == 1 ? "TERRAIN_TEXTURE" : (i3 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i3));
                           }
                        }
                     );
                     throw new ReportedException(crashReport);
                  }
               }

               instance.draw();
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
   }

   public void renderLitParticles(Entity entityIn, float partialTick) {
      float f = MathHelper.cos(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float f2 = MathHelper.sin(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float f4 = f * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float f5 = MathHelper.cos(entityIn.rotationPitch * (float) (Math.PI / 180.0));

      for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
         List list = this.fxLayers[3][innerIndex];
         if (!list.isEmpty()) {
            WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

            for (int i = 0; i < list.size(); i++) {
               ((EntityFX)list.get(i)).renderParticle(worldRenderer, entityIn, partialTick, f, f5, f2, f3, f4);
            }
         }
      }
   }

   public EntityFX spawnEffectParticle(
      int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters
   ) {
      IParticleFactory particleFactory = this.particleTypes.get(particleId);
      if (particleFactory != null) {
         EntityFX entityFX = particleFactory.getEntityFX(this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
         if (entityFX != null) {
            this.addEffect(entityFX);
            return entityFX;
         }
      }

      return null;
   }

   public void addEffect(EntityFX effect) {
      if (effect != null && (!(effect instanceof EntityFXChild3) || Config.isFireworkParticles())) {
         int fxLayer = effect.getFXLayer();
         int i = effect.getAlpha() != 1.0F ? 0 : 1;
         if (this.fxLayers[fxLayer][i].size() >= 4000) {
            this.fxLayers[fxLayer][i].remove(0);
         }

         this.fxLayers[fxLayer][i].add(effect);
      }
   }

   private void updateEffectLayer(int layer) {
      for (int index = 0; index < 2; index++) {
         this.updateEffectAlphaLayer(this.fxLayers[layer][index]);
      }
   }

   public void clearEffects(World worldIn) {
      this.worldObj = worldIn;

      for (int index = 0; index < 4; index++) {
         for (int innerIndex = 0; innerIndex < 2; innerIndex++) {
            this.fxLayers[index][innerIndex].clear();
         }
      }

      this.particleEmitters.clear();
   }

   public void moveToNoAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 0, 1);
   }

   public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
      boolean flag;
      if (Reflector.reflectorMethod19.isActive() && Reflector.reflectorMethod29.isActive()) {
         Block block2 = state.getBlock();
         flag = !Reflector.checkObject(block2, Reflector.reflectorMethod29, this.worldObj, pos)
            && !Reflector.checkObject(block2, Reflector.reflectorMethod19, this.worldObj, pos, this);
      } else {
         flag = state.getBlock().getMaterial() != Material.air;
      }

      if (flag) {
         state = state.getBlock().getActualState(state, this.worldObj, pos);

         for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 4; i2++) {
               for (int i3 = 0; i3 < 4; i3++) {
                  double d = pos.getX() + (i + 0.5) / 4;
                  double d2 = pos.getY() + (i2 + 0.5) / 4;
                  double d3 = pos.getZ() + (i3 + 0.5) / 4;
                  this.addEffect(
                     new EntityDiggingFX(
                           this.worldObj, d, d2, d3, d - pos.getX() - 0.5, d2 - pos.getY() - 0.5, d3 - pos.getZ() - 0.5, state
                        )
                        .setBlockPos(pos)
                  );
               }
            }
         }
      }
   }

   public void updateEffects() {
      for (int i = 0; i < 4; i++) {
         this.updateEffectLayer(i);
      }

      ArrayList arrayList = Lists.newArrayList();

      for (EntityParticleEmitter entityParticleEmitter : this.particleEmitters) {
         entityParticleEmitter.onUpdate();
         if (entityParticleEmitter.isDead) {
            arrayList.add(entityParticleEmitter);
         }
      }

      this.particleEmitters.removeAll(arrayList);
   }

   public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
      IBlockState blockState = this.worldObj.getBlockState(pos);
      Block block = blockState.getBlock();
      if (block.getRenderType() != -1) {
         int xCoordinate = pos.getX();
         int yCoordinate = pos.getY();
         int zCoordinate = pos.getZ();
         float f = 0.1F;
         double d = xCoordinate
            + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F)
            + f
            + block.getBlockBoundsMinX();
         double d2 = yCoordinate
            + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F)
            + f
            + block.getBlockBoundsMinY();
         double d3 = zCoordinate
            + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F)
            + f
            + block.getBlockBoundsMinZ();
         if (side == EnumFacing.DOWN) {
            d2 = yCoordinate + block.getBlockBoundsMinY() - f;
         }

         if (side == EnumFacing.UP) {
            d2 = yCoordinate + block.getBlockBoundsMaxY() + f;
         }

         if (side == EnumFacing.NORTH) {
            d3 = zCoordinate + block.getBlockBoundsMinZ() - f;
         }

         if (side == EnumFacing.SOUTH) {
            d3 = zCoordinate + block.getBlockBoundsMaxZ() + f;
         }

         if (side == EnumFacing.WEST) {
            d = xCoordinate + block.getBlockBoundsMinX() - f;
         }

         if (side == EnumFacing.EAST) {
            d = xCoordinate + block.getBlockBoundsMaxX() + f;
         }

         this.addEffect(
            new EntityDiggingFX(this.worldObj, d, d2, d3, 0.0, 0.0, 0.0, blockState)
               .setBlockPos(pos)
               .multiplyVelocity(0.2F)
               .multipleParticleScaleBy(0.6F)
         );
      }
   }

   private void tickParticle(final EntityFX particle) {
      try {
         particle.onUpdate();
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being ticked");
         final int fxLayer = particle.getFXLayer();
         crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
            public String call() throws Exception {
               return particle.toString();
            }
         });
         crashReportCategory.addCrashSectionCallable("Particle Type", new Callable<String>() {
            public String call() throws Exception {
               return fxLayer == 0 ? "MISC_TEXTURE" : (fxLayer == 1 ? "TERRAIN_TEXTURE" : (fxLayer == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + fxLayer));
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   public String getStatistics() {
      int i = 0;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < 2; nestedIndex++) {
            i += this.fxLayers[innerIndex][nestedIndex].size();
         }
      }

      return i + "";
   }

   private void registerVanillaParticles() {
      this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new wtf.novoline.particle.EntityFXChild8.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new wtf.novoline.particle.EntityFXChild7.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new wtf.novoline.particle.EntityFXChild6.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new wtf.novoline.particle.EntityFXChild1.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new IParticleFactoryImpl2());
      this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new wtf.novoline.particle.EntityFXChild9.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new net.minecraft.client.particle.EntityAuraFX.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
      this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
      this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
      this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
      this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new wtf.novoline.particle.EntityFXChild5.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new wtf.novoline.particle.EntityFXChild6.IParticleFactoryImpl2());
      this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new wtf.novoline.particle.EntityFXChild6.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new wtf.novoline.particle.EntityFXChild2.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
      this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
      this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
      this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
      this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
      this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
      this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
      this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
      this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new wtf.novoline.particle.EntityFXChild4.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
      this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new wtf.novoline.particle.EntityFXChild5.IParticleFactoryImpl2());
      this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
      this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new wtf.novoline.util.FireworkEffects.IParticleFactoryImpl());
      this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
   }

   public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
      this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
   }

   public void registerParticle(int id, IParticleFactory particleFactory) {
      this.particleTypes.put(id, particleFactory);
   }

   public void moveToAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 1, 0);
   }

   private void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
      ArrayList arrayList = Lists.newArrayList();
      long l = System.currentTimeMillis();
      int size2 = entitiesFX.size();

      for (int i = 0; i < entitiesFX.size(); i++) {
         EntityFX entityFX2 = entitiesFX.get(i);
         this.tickParticle(entityFX2);
         if (entityFX2.isDead) {
            arrayList.add(entityFX2);
         }

         size2--;
         if (System.currentTimeMillis() > l + 20L) {
            break;
         }
      }

      if (size2 > 0) {
         int i2 = size2;

         for (Iterator iterator2 = entitiesFX.iterator(); iterator2.hasNext() && i2 > 0; i2--) {
            ((EntityFX)iterator2.next()).setDead();
            iterator2.remove();
         }
      }

      entitiesFX.removeAll(arrayList);
   }
}
