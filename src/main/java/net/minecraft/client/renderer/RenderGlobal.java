package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.optifine.CustomColors;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.SmartAnimations;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.render.ChunkVisibility;
import net.optifine.render.CloudRenderer;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadowUtils;
import net.optifine.shaders.gui.GuiShaderOptions;
import net.optifine.util.ChunkUtils;
import net.optifine.util.RenderChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.novoline.utils.render.RenderUtils;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
   private static final Logger logger = LogManager.getLogger();
   private WorldClient theWorld;
   private double doubleValue;
   private VertexBuffer starVBO;
   private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
   private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> setTileEntities = Sets.newHashSet();
   private int starGLCallList = -1;
   private int glSkyList = -1;
   private int glSkyList2 = -1;
   public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
   private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
   private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
   private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
   private double frustumUpdatePosX = Double.MIN_VALUE;
   private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
   private double frustumUpdatePosY = Double.MIN_VALUE;
   private double frustumUpdatePosZ = Double.MIN_VALUE;
   private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
   private int countEntitiesTotal;
   private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
   private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
   private double lastViewEntityX = Double.MIN_VALUE;
   private ShaderGroup entityOutlineShader;
   private double lastViewEntityY = Double.MIN_VALUE;
   private double lastViewEntityZ = Double.MIN_VALUE;
   private double lastViewEntityPitch = Double.MIN_VALUE;
   private VertexBuffer sky2VBO;
   private double lastViewEntityYaw = Double.MIN_VALUE;
   private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher(50101515795373L);
   private int renderDistanceChunks = -1;
   private int countEntitiesHidden;
   private VertexBuffer skyVBO;
   private int renderEntitiesStartupCounter = 2;
   private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
   private boolean debugFixTerrainFrustum = false;
   private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
   private ClippingHelper debugFixedClippingHelper;
   private final Vector3d debugTerrainFrustumPosition = new Vector3d();
   private boolean vboEnabled = false;
   public boolean displayListEntitiesDirty = true;
   private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");
   public Set chunksToResortTransparency = new LinkedHashSet();
   public Set chunksToUpdateForced = new LinkedHashSet();
   private int cloudTickCounter;
   private Deque visibilityDeque = new ArrayDeque();
   private double doubleValue2;
   private Framebuffer entityOutlineFramebuffer;
   private List<RenderGlobal.ContainerLocalRenderInformation> renderInfosEntities = new ArrayList<>(1024);
   private List<RenderGlobal.ContainerLocalRenderInformation> renderInfosTileEntities = new ArrayList<>(1024);
   private List renderInfosNormal = new ArrayList(1024);
   private List renderInfosEntitiesNormal = new ArrayList(1024);
   private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EnumFacing.VALUES)));
   private double doubleValue3;
   private List renderInfosTileEntitiesNormal = new ArrayList(1024);
   private List renderInfosShadow = new ArrayList(1024);
   private int countEntitiesRendered;
   private List renderInfosEntitiesShadow = new ArrayList(1024);
   private List renderInfosTileEntitiesShadow = new ArrayList(1024);
   private int renderDistance = 0;
   private int renderDistanceSq = 0;
   public Entity renderedEntity;
   private IChunkProvider worldChunkProvider = null;
   private static int renderEntitiesCounter = 0;
   private LongHashMap worldChunkProviderMap = null;
   private int countLoadedChunksPrev = 0;
   private int countTileEntitiesRendered;
   private RenderEnv renderEnv = new RenderEnv(Blocks.air.getDefaultState(), new BlockPos(0, 0, 0));
   public boolean renderOverlayDamaged = false;
   public boolean renderOverlayEyes = false;
   private boolean firstWorldLoad = false;
   private CloudRenderer cloudRenderer;
   public final Minecraft mc;
   private ViewFrustum viewFrustum;
   private final RenderManager renderManager;
   private final TextureManager renderEngine;
   private ChunkRenderContainer renderContainer;
   IRenderChunkFactory renderChunkFactory;
   private VertexFormat vertexBufferFormat;

   private void generateSky2() {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.sky2VBO != null) {
         this.sky2VBO.deleteGlBuffers();
      }

      if (this.glSkyList2 >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList2);
         this.glSkyList2 = -1;
      }

      if (this.vboEnabled) {
         this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, -16.0F, true);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.sky2VBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList2 = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList2, 4864);
         this.renderSky(worldRenderer, -16.0F, true);
         tessellator.draw();
         GL11.glEndList();
      }
   }

   @Override
   public void onEntityAdded(Entity entity2) {
      RandomEntities.entityLoaded(entity2, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.entityAdded(entity2, this);
      }
   }

   @Override
   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      if (progress >= 0 && progress < 10) {
         DestroyBlockProgress destroyBlockProgress = this.damagedBlocks.get(breakerId);
         if (destroyBlockProgress == null
            || destroyBlockProgress.getPosition().getX() != pos.getX()
            || destroyBlockProgress.getPosition().getY() != pos.getY()
            || destroyBlockProgress.getPosition().getZ() != pos.getZ()) {
            destroyBlockProgress = new DestroyBlockProgress(breakerId, pos);
            this.damagedBlocks.put(breakerId, destroyBlockProgress);
         }

         destroyBlockProgress.setPartialBlockDamage(progress);
         destroyBlockProgress.setCloudUpdateTick(this.cloudTickCounter);
      } else {
         this.damagedBlocks.remove(breakerId);
      }
   }

   public int getInt(EnumWorldBlockLayer enumWorldBlockLayer, double d2, Entity entity2) {
      RenderHelper.disableStandardItemLighting();
      if (enumWorldBlockLayer == EnumWorldBlockLayer.TRANSLUCENT && !Shaders.isShadowPass) {
         this.mc.mcProfiler.startSection("translucent_sort");
         double d3 = entity2.posX - this.doubleValue;
         double d4 = entity2.posY - this.doubleValue2;
         double d5 = entity2.posZ - this.doubleValue3;
         if (d3 * d3 + d4 * d4 + d5 * d5 > 1.0) {
            this.doubleValue = entity2.posX;
            this.doubleValue2 = entity2.posY;
            this.doubleValue3 = entity2.posZ;
            int i = 0;
            this.chunksToResortTransparency.clear();

            for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfos) {
               if (containerLocalRenderInformation.renderChunk.compiledChunk.isLayerStarted(enumWorldBlockLayer) && i++ < 15) {
                  this.chunksToResortTransparency.add(containerLocalRenderInformation.renderChunk);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.startSection("filterempty");
      int i2 = 0;
      boolean flag = enumWorldBlockLayer == EnumWorldBlockLayer.TRANSLUCENT;
      int i3 = flag ? this.renderInfos.size() - 1 : 0;
      int i4 = flag ? -1 : this.renderInfos.size();
      int i5 = flag ? -1 : 1;

      for (int i6 = i3; i6 != i4; i6 += i5) {
         RenderChunk renderChunk2 = this.renderInfos.get(i6).renderChunk;
         if (!renderChunk2.getCompiledChunk().isLayerEmpty(enumWorldBlockLayer)) {
            i2++;
            this.renderContainer.addRenderChunk(renderChunk2, enumWorldBlockLayer);
         }
      }

      if (i2 == 0) {
         this.mc.mcProfiler.endSection();
         return i2;
      }

      if (Config.isFogOff() && this.mc.entityRenderer.fovModifierForced) {
         GlStateManager.disableFog();
      }

      this.mc.mcProfiler.endStartSection("render_" + enumWorldBlockLayer);
      this.renderBlockLayer(enumWorldBlockLayer);
      this.mc.mcProfiler.endSection();
      return i2;
   }

   @Override
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
   }

   public static void handleAxisAlignedBB(AxisAlignedBB axisAlignedBB, int i, int i2, int i3) {
      Tessellator instance2 = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance2.getWorldRenderer();
      worldRenderer2.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      instance2.draw();
      worldRenderer2.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      instance2.draw();
      worldRenderer2.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      worldRenderer2.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(i, i2, i3, 255).endVertex();
      instance2.draw();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.updateDestroyBlockIcons();
   }

   protected boolean isRenderEntityOutlines() {
      return !Config.isShaders()
         && !Config.isAntialiasing()
         && this.entityOutlineFramebuffer != null
         && this.entityOutlineShader != null
         && this.mc.thePlayer != null
         && this.mc.thePlayer.isSpectator()
         && this.mc.gameSettings.keyBinding2.isKeyDown();
   }

   private void generateStars() {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.starVBO != null) {
         this.starVBO.deleteGlBuffers();
      }

      if (this.starGLCallList >= 0) {
         GLAllocation.deleteDisplayLists(this.starGLCallList);
         this.starGLCallList = -1;
      }

      if (this.vboEnabled) {
         this.starVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderStars(worldRenderer);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.starVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.starGLCallList = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GL11.glNewList(this.starGLCallList, 4864);
         this.renderStars(worldRenderer);
         tessellator.draw();
         GL11.glEndList();
         GlStateManager.popMatrix();
      }
   }

   @Override
   public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
   }

   private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
   }

   public int getCountEntitiesRendered() {
      return this.countEntitiesRendered;
   }

   public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
      if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
         this.loadRenderers();
      }

      this.theWorld.theProfiler.startSection("camera");
      double d2 = viewEntity.posX - this.frustumUpdatePosX;
      double d3 = viewEntity.posY - this.frustumUpdatePosY;
      double d4 = viewEntity.posZ - this.frustumUpdatePosZ;
      if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX
         || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY
         || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ
         || d2 * d2 + d3 * d3 + d4 * d4 > 16.0) {
         this.frustumUpdatePosX = viewEntity.posX;
         this.frustumUpdatePosY = viewEntity.posY;
         this.frustumUpdatePosZ = viewEntity.posZ;
         this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
         this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
         this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
         this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
      }

      if (Config.isDynamicLights()) {
         DynamicLights.update(this);
      }

      this.theWorld.theProfiler.endStartSection("renderlistcamera");
      double d5 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
      double d6 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
      double d7 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
      this.renderContainer.initialize(d5, d6, d7);
      this.theWorld.theProfiler.endStartSection("cull");
      if (this.debugFixedClippingHelper != null) {
         Frustum frustum = new Frustum(this.debugFixedClippingHelper);
         frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
         camera = frustum;
      }

      this.mc.mcProfiler.endStartSection("culling");
      BlockPos blockPos = new BlockPos(d5, d6 + viewEntity.getEyeHeight(), d7);
      RenderChunk renderChunk2 = this.viewFrustum.getRenderChunk(blockPos);
      new BlockPos(MathHelper.floor_double(d5 / 16.0) * 16, MathHelper.floor_double(d6 / 16.0) * 16, MathHelper.floor_double(d7 / 16.0) * 16);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty
         || !this.chunksToUpdate.isEmpty()
         || viewEntity.posX != this.lastViewEntityX
         || viewEntity.posY != this.lastViewEntityY
         || viewEntity.posZ != this.lastViewEntityZ
         || viewEntity.rotationPitch != this.lastViewEntityPitch
         || viewEntity.rotationYaw != this.lastViewEntityYaw;
      this.lastViewEntityX = viewEntity.posX;
      this.lastViewEntityY = viewEntity.posY;
      this.lastViewEntityZ = viewEntity.posZ;
      this.lastViewEntityPitch = viewEntity.rotationPitch;
      this.lastViewEntityYaw = viewEntity.rotationYaw;
      boolean flag = this.debugFixedClippingHelper != null;
      this.mc.mcProfiler.endStartSection("update");
      Lagometer.timerVisibility.start();
      int countLoadedChunks = this.getCountLoadedChunks();
      if (countLoadedChunks != this.countLoadedChunksPrev) {
         this.countLoadedChunksPrev = countLoadedChunks;
         this.displayListEntitiesDirty = true;
      }

      int i = 256;
      if (!ChunkVisibility.isFinished()) {
         this.displayListEntitiesDirty = true;
      }

      if (!flag && this.displayListEntitiesDirty && Config.isIntegratedServerRunning()) {
         i = ChunkVisibility.getMaxChunkY(this.theWorld, viewEntity, this.renderDistanceChunks);
      }

      RenderChunk renderChunk3 = this.viewFrustum.getRenderChunk(new BlockPos(viewEntity.posX, viewEntity.posY, viewEntity.posZ));
      if (Shaders.isShadowPass) {
         this.renderInfos = this.renderInfosShadow;
         this.renderInfosEntities = this.renderInfosEntitiesShadow;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
         if (!flag && this.displayListEntitiesDirty) {
            this.clearRenderInfos();
            if (renderChunk3 != null && renderChunk3.getPosition().getY() > i) {
               this.renderInfosEntities.add(renderChunk3.getRenderInfo());
            }

            Iterator iterator2 = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);

            while (iterator2.hasNext()) {
               RenderChunk renderChunk4 = (RenderChunk)iterator2.next();
               if (renderChunk4 != null && renderChunk4.getPosition().getY() <= i) {
                  RenderGlobal.ContainerLocalRenderInformation renderInfo = renderChunk4.getRenderInfo();
                  if (!renderChunk4.compiledChunk.isEmpty() || renderChunk4.isNeedsUpdate()) {
                     this.renderInfos.add(renderInfo);
                  }

                  if (ChunkUtils.hasEntities(renderChunk4.getChunk())) {
                     this.renderInfosEntities.add(renderInfo);
                  }

                  if (renderChunk4.getCompiledChunk().getTileEntities().size() > 0) {
                     this.renderInfosTileEntities.add(renderInfo);
                  }
               }
            }
         }
      } else {
         this.renderInfos = this.renderInfosNormal;
         this.renderInfosEntities = this.renderInfosEntitiesNormal;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
      }

      if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
         this.displayListEntitiesDirty = false;
         this.clearRenderInfos();
         this.visibilityDeque.clear();
         Deque visibilityDeque2 = this.visibilityDeque;
         boolean renderChunksMany2 = this.mc.renderChunksMany;
         if (renderChunk2 != null && renderChunk2.getPosition().getY() <= i) {
            boolean flag2 = false;
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = new RenderGlobal.ContainerLocalRenderInformation(renderChunk2, (EnumFacing)null);
            Set set2 = SET_ALL_FACINGS;
            if (set2.size() == 1) {
               Vector3f viewVector = this.getViewVector(viewEntity, partialTicks);
               EnumFacing opposite = EnumFacing.getFacingFromVector(viewVector.x, viewVector.y, viewVector.z).getOpposite();
               set2.remove(opposite);
            }

            if (set2.isEmpty()) {
               flag2 = true;
            }

            if (flag2 && !playerSpectator) {
               this.renderInfos.add(containerLocalRenderInformation);
            } else {
               if (playerSpectator && this.theWorld.getBlockState(blockPos).getBlock().isOpaqueCube()) {
                  renderChunksMany2 = false;
               }

               renderChunk2.setFrameIndex(frameCount);
               visibilityDeque2.add(containerLocalRenderInformation);
            }
         } else {
            int i2 = blockPos.getY() > 0 ? Math.min(i, 248) : 8;
            if (renderChunk3 != null) {
               this.renderInfosEntities.add(renderChunk3.getRenderInfo());
            }

            for (int i3 = -this.renderDistanceChunks; i3 <= this.renderDistanceChunks; i3++) {
               for (int i4 = -this.renderDistanceChunks; i4 <= this.renderDistanceChunks; i4++) {
                  RenderChunk renderChunk5 = this.viewFrustum.getRenderChunk(new BlockPos((i3 << 4) + 8, i2, (i4 << 4) + 8));
                  if (renderChunk5 != null && renderChunk5.isBoundingBoxInFrustum(camera, frameCount)) {
                     renderChunk5.setFrameIndex(frameCount);
                     RenderGlobal.ContainerLocalRenderInformation renderInfo2 = renderChunk5.getRenderInfo();
                     renderInfo2.initialize((EnumFacing)null, 0);
                     visibilityDeque2.add(renderInfo2);
                  }
               }
            }
         }

         this.mc.mcProfiler.startSection("iteration");
         boolean fogOn = Config.isFogOn();

         while (!visibilityDeque2.isEmpty()) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 = (RenderGlobal.ContainerLocalRenderInformation)visibilityDeque2.poll();
            RenderChunk renderChunk6 = containerLocalRenderInformation2.renderChunk;
            EnumFacing facing2 = containerLocalRenderInformation2.facing;
            CompiledChunk compiledChunk2 = renderChunk6.compiledChunk;
            if (!compiledChunk2.isEmpty() || renderChunk6.isNeedsUpdate()) {
               this.renderInfos.add(containerLocalRenderInformation2);
            }

            if (ChunkUtils.hasEntities(renderChunk6.getChunk())) {
               this.renderInfosEntities.add(containerLocalRenderInformation2);
            }

            if (compiledChunk2.getTileEntities().size() > 0) {
               this.renderInfosTileEntities.add(containerLocalRenderInformation2);
            }

            for (EnumFacing enumFacing : renderChunksMany2 ? ChunkVisibility.getFacingsNotOpposite(containerLocalRenderInformation2.setFacing) : EnumFacing.VALUES) {
               if (!renderChunksMany2 || facing2 == null || compiledChunk2.isVisible(facing2.getOpposite(), enumFacing)) {
                  RenderChunk renderChunkOffset = this.getRenderChunkOffset(blockPos, renderChunk6, enumFacing, fogOn, i);
                  if (renderChunkOffset != null && renderChunkOffset.setFrameIndex(frameCount) && renderChunkOffset.isBoundingBoxInFrustum(camera, frameCount)) {
                     int i5 = containerLocalRenderInformation2.setFacing | 1 << enumFacing.ordinal();
                     RenderGlobal.ContainerLocalRenderInformation renderInfo3 = renderChunkOffset.getRenderInfo();
                     renderInfo3.initialize(enumFacing, i5);
                     visibilityDeque2.add(renderInfo3);
                  }
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.endStartSection("captureFrustum");
      if (this.debugFixTerrainFrustum) {
         this.fixTerrainFrustum(d5, d6, d7);
         this.debugFixTerrainFrustum = false;
      }

      Lagometer.timerVisibility.end();
      if (Shaders.isShadowPass) {
         Shaders.mcProfilerEndSection();
      } else {
         this.mc.mcProfiler.endStartSection("rebuildNear");
         this.renderDispatcher.clearChunkUpdates();
         Set chunksToUpdate2 = this.chunksToUpdate;
         this.chunksToUpdate = Sets.newLinkedHashSet();
         Lagometer.timerChunkUpdate.start();

         for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation3 : this.renderInfos) {
            RenderChunk renderChunk7 = containerLocalRenderInformation3.renderChunk;
            if (renderChunk7.isNeedsUpdate() || chunksToUpdate2.contains(renderChunk7)) {
               this.displayListEntitiesDirty = true;
               BlockPos position = renderChunk7.getPosition();
               boolean flag3 = blockPos.distanceSq(position.getX() + 8, position.getY() + 8, position.getZ() + 8) < 768.0;
               if (!flag3) {
                  this.chunksToUpdate.add(renderChunk7);
               } else if (!renderChunk7.isPlayerUpdate()) {
                  this.chunksToUpdateForced.add(renderChunk7);
               } else {
                  this.mc.mcProfiler.startSection("build near");
                  this.renderDispatcher.updateChunkNow(renderChunk7);
                  renderChunk7.setNeedsUpdate(false);
                  this.mc.mcProfiler.endSection();
               }
            }
         }

         Lagometer.timerChunkUpdate.end();
         this.chunksToUpdate.addAll(chunksToUpdate2);
         this.mc.mcProfiler.endSection();
      }
   }

   @Override
   public void markBlockForUpdate(BlockPos blockPos) {
      int x3 = blockPos.getX();
      int y3 = blockPos.getY();
      int z3 = blockPos.getZ();
      this.markBlocksForUpdate(x3 - 1, y3 - 1, z3 - 1, x3 + 1, y3 + 1, z3 + 1);
   }

   public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
      int i = 0;
      if (Reflector.reflectorMethod100.isActive()) {
         i = Reflector.createInt2(Reflector.reflectorMethod100);
      }

      if (this.renderEntitiesStartupCounter > 0) {
         if (i > 0) {
            return;
         }

         this.renderEntitiesStartupCounter--;
      } else {
         double d2 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
         double d3 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks;
         double d4 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
         this.theWorld.theProfiler.startSection("prepare");
         TileEntityRendererDispatcher.instance
            .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
         this.renderManager
            .cacheActiveRenderInfo(
               this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks
            );
         renderEntitiesCounter++;
         if (i == 0) {
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            this.countTileEntitiesRendered = 0;
         }

         Entity renderViewEntity2 = this.mc.getRenderViewEntity();
         double d5 = renderViewEntity2.lastTickPosX + (renderViewEntity2.posX - renderViewEntity2.lastTickPosX) * partialTicks;
         double d6 = renderViewEntity2.lastTickPosY + (renderViewEntity2.posY - renderViewEntity2.lastTickPosY) * partialTicks;
         double d7 = renderViewEntity2.lastTickPosZ + (renderViewEntity2.posZ - renderViewEntity2.lastTickPosZ) * partialTicks;
         TileEntityRendererDispatcher.staticPlayerX = d5;
         TileEntityRendererDispatcher.staticPlayerY = d6;
         TileEntityRendererDispatcher.staticPlayerZ = d7;
         this.renderManager.setRenderPosition(d5, d6, d7);
         this.mc.entityRenderer.enableLightmap();
         this.theWorld.theProfiler.endStartSection("global");
         List loadedEntityList = this.theWorld.getLoadedEntityList();
         if (i == 0) {
            this.countEntitiesTotal = loadedEntityList.size();
         }

         if (Config.isFogOff() && this.mc.entityRenderer.fovModifierForced) {
            GlStateManager.disableFog();
         }

         boolean active = Reflector.reflectorMethod35.isActive();
         boolean active2 = Reflector.reflectorMethod84.isActive();

         for (int i2 = 0; i2 < this.theWorld.weatherEffects.size(); i2++) {
            Entity entity2 = this.theWorld.weatherEffects.get(i2);
            if (!active || Reflector.checkObject(entity2, Reflector.reflectorMethod35, i)) {
               this.countEntitiesRendered++;
               if (entity2.isInRangeToRender3d(d2, d3, d4)) {
                  this.renderManager.renderEntitySimple(entity2, partialTicks);
               }
            }
         }

         if (this.isRenderEntityOutlines()) {
            GlStateManager.depthFunc(519);
            GlStateManager.disableFog();
            this.entityOutlineFramebuffer.framebufferClear();
            this.entityOutlineFramebuffer.bindFramebuffer(false);
            this.theWorld.theProfiler.endStartSection("entityOutlines");
            RenderHelper.disableStandardItemLighting();
            this.renderManager.setRenderOutlines(true);

            for (int i3 = 0; i3 < loadedEntityList.size(); i3++) {
               Entity entity3 = (Entity)loadedEntityList.get(i3);
               boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                  && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
               boolean flag2 = entity3.isInRangeToRender3d(d2, d3, d4)
                  && (entity3.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity3.getEntityBoundingBox()) || entity3.riddenByEntity == this.mc.thePlayer)
                  && entity3 instanceof EntityPlayer;
               if ((entity3 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag) && flag2) {
                  this.renderManager.renderEntitySimple(entity3, partialTicks);
               }
            }

            this.renderManager.setRenderOutlines(false);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.entityOutlineShader.loadShaderGroup(partialTicks);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            this.mc.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableFog();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.depthFunc(515);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
         }

         this.theWorld.theProfiler.endStartSection("entities");
         boolean shaders2 = Config.isShaders();
         if (shaders2) {
            Shaders.beginEntities();
         }

         RenderItemFrame.updateItemRenderDistance();
         boolean fancyGraphics2 = this.mc.gameSettings.fancyGraphics;
         this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
         boolean flag3 = Shaders.isShadowPass && !this.mc.thePlayer.isSpectator();

         for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfosEntities) {
            Chunk chunk2 = containerLocalRenderInformation.renderChunk.getChunk();
            ClassInheritanceMultiMap classInheritanceMultiMap = chunk2.getEntityLists()[containerLocalRenderInformation.renderChunk.getPosition().getY() / 16];
            if (!classInheritanceMultiMap.isEmpty()) {
               for (Entity entity4 : (Iterable<Entity>) classInheritanceMultiMap) {
                  if (!active || Reflector.checkObject(entity4, Reflector.reflectorMethod35, i)) {
                     boolean flag4 = this.renderManager.shouldRender(entity4, camera, d2, d3, d4) || entity4.riddenByEntity == this.mc.thePlayer;
                     if (flag4) {
                        boolean flag5 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                           ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping()
                           : false;
                        if (entity4 == this.mc.getRenderViewEntity() && !flag3 && this.mc.gameSettings.thirdPersonView == 0 && !flag5
                           || !(entity4.posY < 0.0) && !(entity4.posY >= 256.0) && !this.theWorld.isBlockLoaded(new BlockPos(entity4))) {
                           continue;
                        }

                        this.countEntitiesRendered++;
                        this.renderedEntity = entity4;
                        if (shaders2) {
                           Shaders.nextEntity(entity4);
                        }

                        this.renderManager.renderEntitySimple(entity4, partialTicks);
                        this.renderedEntity = null;
                     }

                     if (!flag4 && entity4 instanceof EntityWitherSkull && (!active || Reflector.checkObject(entity4, Reflector.reflectorMethod35, i))) {
                        this.renderedEntity = entity4;
                        if (shaders2) {
                           Shaders.nextEntity(entity4);
                        }

                        this.mc.getRenderManager().renderWitherSkull(entity4, partialTicks);
                        this.renderedEntity = null;
                     }
                  }
               }
            }
         }

         this.mc.gameSettings.fancyGraphics = fancyGraphics2;
         if (shaders2) {
            Shaders.endEntities();
            Shaders.beginBlockEntities();
         }

         this.theWorld.theProfiler.endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();
         if (Reflector.reflectorMethod83.isActive()) {
            TileEntityRendererDispatcher.instance.preDrawBatch();
         }

         TileEntitySignRenderer.updateTextRenderDistance();

         for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 : this.renderInfosTileEntities) {
            List tileEntities = containerLocalRenderInformation2.renderChunk.getCompiledChunk().getTileEntities();
            if (!tileEntities.isEmpty()) {
               for (TileEntity tileEntity : (Iterable<TileEntity>) tileEntities) {
                  if (active2) {
                     if (!Reflector.checkObject(tileEntity, Reflector.reflectorMethod84, i)) {
                        continue;
                     }

                     AxisAlignedBB axisAlignedBB = (AxisAlignedBB)Reflector.createObject4(tileEntity, Reflector.reflectorMethod82);
                     if (axisAlignedBB != null && !camera.isBoundingBoxInFrustum(axisAlignedBB)) {
                        continue;
                     }
                  }

                  if (shaders2) {
                     Shaders.nextBlockEntity(tileEntity);
                  }

                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
                  this.countTileEntitiesRendered++;
               }
            }
         }

         synchronized (this.setTileEntities) {
            for (TileEntity tileEntity2 : this.setTileEntities) {
               if (!active2 || Reflector.checkObject(tileEntity2, Reflector.reflectorMethod84, i)) {
                  if (shaders2) {
                     Shaders.nextBlockEntity(tileEntity2);
                  }

                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity2, partialTicks, -1);
               }
            }
         }

         if (Reflector.reflectorMethod83.isActive()) {
            TileEntityRendererDispatcher.instance.drawBatch(i);
         }

         this.renderOverlayDamaged = true;
         this.preRenderDamagedBlocks();

         for (DestroyBlockProgress destroyBlockProgress2 : this.damagedBlocks.values()) {
            BlockPos position = destroyBlockProgress2.getPosition();
            TileEntity tileEntity3 = this.theWorld.getTileEntity(position);
            if (tileEntity3 instanceof TileEntityChest tileEntityChest) {
               if (tileEntityChest.adjacentChestXNeg != null) {
                  position = position.offset(EnumFacing.WEST);
                  tileEntity3 = this.theWorld.getTileEntity(position);
               } else if (tileEntityChest.adjacentChestZNeg != null) {
                  position = position.offset(EnumFacing.NORTH);
                  tileEntity3 = this.theWorld.getTileEntity(position);
               }
            }

            Block block2 = this.theWorld.getBlockState(position).getBlock();
            boolean flag6;
            if (active2) {
               flag6 = false;
               if (tileEntity3 != null
                  && Reflector.checkObject(tileEntity3, Reflector.reflectorMethod84, i)
                  && Reflector.checkObject(tileEntity3, Reflector.reflectorMethod81)) {
                  AxisAlignedBB axisAlignedBB2 = (AxisAlignedBB)Reflector.createObject4(tileEntity3, Reflector.reflectorMethod82);
                  if (axisAlignedBB2 != null) {
                     flag6 = camera.isBoundingBoxInFrustum(axisAlignedBB2);
                  }
               }
            } else {
               flag6 = tileEntity3 != null
                  && (block2 instanceof BlockChest || block2 instanceof BlockEnderChest || block2 instanceof BlockSign || block2 instanceof BlockSkull);
            }

            if (flag6) {
               if (shaders2) {
                  Shaders.nextBlockEntity(tileEntity3);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity3, partialTicks, destroyBlockProgress2.getPartialBlockDamage());
            }
         }

         this.postRenderDamagedBlocks();
         this.renderOverlayDamaged = false;
         if (shaders2) {
            Shaders.endBlockEntities();
         }

         renderEntitiesCounter--;
         this.mc.entityRenderer.disableLightmap();
         this.mc.mcProfiler.endSection();
      }
   }

   private EntityFX spawnEntityFX(
      int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
         int particleSetting2 = this.mc.gameSettings.particleSetting;
         if (particleSetting2 == 1 && this.theWorld.rand.nextInt(3) == 0) {
            particleSetting2 = 2;
         }

         double xCoordinate = this.mc.getRenderViewEntity().posX - xCoord;
         double yCoordinate = this.mc.getRenderViewEntity().posY - yCoord;
         double zCoordinate = this.mc.getRenderViewEntity().posZ - zCoord;
         if (particleID == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID() && !Config.isPortalParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
            return null;
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
            return null;
         }

         if (!ignoreRange) {
            double d2 = 256.0;
            if (particleID == EnumParticleTypes.CRIT.getParticleID()) {
               d2 = 38416.0;
            }

            if (xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate > d2) {
               return null;
            }

            if (particleSetting2 > 1) {
               return null;
            }
         }

         EntityFX entityFX = this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
         if (particleID == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.WATER_DROP.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.TOWN_AURA.getParticleID()) {
            CustomColors.updateMyceliumFX(entityFX);
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID()) {
            CustomColors.updatePortalFX(entityFX);
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID()) {
            CustomColors.updateReddustFX(entityFX, this.theWorld, xCoord, yCoord, zCoord);
         }

         return entityFX;
      } else {
         return null;
      }
   }

   public RenderGlobal(Minecraft mcIn) {
      this.cloudRenderer = new CloudRenderer(mcIn);
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.renderEngine = mcIn.getTextureManager();
      this.renderEngine.bindTexture(locationForcefieldPng);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.updateDestroyBlockIcons();
      this.vboEnabled = OpenGlHelper.useVbo();
      if (this.vboEnabled) {
         this.renderContainer = new VboRenderList();
         this.renderChunkFactory = new VboChunkFactory();
      } else {
         this.renderContainer = new RenderList();
         this.renderChunkFactory = new ListChunkFactory();
      }

      this.vertexBufferFormat = new VertexFormat();
      this.vertexBufferFormat.addElement(new VertexFormatElement(0, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType.FLOAT, net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage.POSITION, 3));
      this.generateStars();
      this.generateSky();
      this.generateSky2();
   }

   private void renderStars(WorldRenderer worldRendererIn) {
      Random random2 = new Random(10842L);
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

      for (int i = 0; i < 1500; i++) {
         double d2 = random2.nextFloat() * 2.0F - 1.0F;
         double d3 = random2.nextFloat() * 2.0F - 1.0F;
         double d4 = random2.nextFloat() * 2.0F - 1.0F;
         double d5 = 0.15F + random2.nextFloat() * 0.1F;
         double d6 = d2 * d2 + d3 * d3 + d4 * d4;
         if (d6 < 1.0 && d6 > 0.01) {
            d6 = 1.0 / Math.sqrt(d6);
            d2 *= d6;
            d3 *= d6;
            d4 *= d6;
            double d7 = d2 * 100.0;
            double d8 = d3 * 100.0;
            double d9 = d4 * 100.0;
            double d10 = Math.atan2(d2, d4);
            double d11 = Math.sin(d10);
            double d12 = Math.cos(d10);
            double d13 = Math.atan2(Math.sqrt(d2 * d2 + d4 * d4), d3);
            double d14 = Math.sin(d13);
            double d15 = Math.cos(d13);
            double d16 = random2.nextDouble() * Math.PI * 2.0;
            double d17 = Math.sin(d16);
            double d18 = Math.cos(d16);

            for (int i2 = 0; i2 < 4; i2++) {
               double d19 = ((i2 & 2) - 1) * d5;
               double d20 = ((i2 + 1 & 2) - 1) * d5;
               double d21 = d19 * d18 - d20 * d17;
               double d22 = d20 * d18 + d19 * d17;
               double d23 = d21 * d14 + 0.0 * d15;
               double d24 = 0.0 * d14 - d21 * d15;
               double d25 = d24 * d11 - d22 * d12;
               double d26 = d22 * d11 + d24 * d12;
               worldRendererIn.pos(d7 + d25, d8 + d23, d9 + d26).endVertex();
            }
         }
      }
   }

   private void renderSky(WorldRenderer worldRendererIn, float posY, boolean reverseX) {
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
      int i = (this.renderDistance / 64 + 1) * 64 + 64;

      for (int i2 = -i; i2 <= i; i2 += 64) {
         for (int i3 = -i; i3 <= i; i3 += 64) {
            float f = i2;
            float f2 = i2 + 64;
            if (reverseX) {
               f2 = i2;
               f = i2 + 64;
            }

            worldRendererIn.pos(f, posY, i3).endVertex();
            worldRendererIn.pos(f2, posY, i3).endVertex();
            worldRendererIn.pos(f2, posY, i3 + 64).endVertex();
            worldRendererIn.pos(f, posY, i3 + 64).endVertex();
         }
      }
   }

   public void deleteAllDisplayLists() {
   }

   public void loadRenderers() {
      if (this.theWorld != null) {
         this.displayListEntitiesDirty = true;
         Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
         Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
         BlockModelRenderer.updateAoLightValue();
         if (Config.isDynamicLights()) {
            DynamicLights.clear();
         }

         SmartAnimations.update();
         this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
         this.renderDistance = this.renderDistanceChunks * 16;
         this.renderDistanceSq = this.renderDistance * this.renderDistance;
         boolean vboEnabled2 = this.vboEnabled;
         this.vboEnabled = OpenGlHelper.useVbo();
         if (vboEnabled2 && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
         } else if (!vboEnabled2 && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
         }

         this.generateStars();
         this.generateSky();
         this.generateSky2();
         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.stopChunkUpdates();
         synchronized (this.setTileEntities) {
            this.setTileEntities.clear();
         }

         this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         if (this.theWorld != null) {
            Entity renderViewEntity2 = this.mc.getRenderViewEntity();
            if (renderViewEntity2 != null) {
               this.viewFrustum.updateChunkPositions(renderViewEntity2.posX, renderViewEntity2.posZ);
            }
         }

         this.renderEntitiesStartupCounter = 2;
      }

      if (this.mc.thePlayer == null) {
         this.firstWorldLoad = true;
      }
   }

   public void createBindEntityOutlineFbs(int width, int height) {
      if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
         this.entityOutlineShader.createBindFramebuffers(width, height);
      }
   }

   public void renderClouds(float partialTicks, int pass) {
      if (!Config.isCloudsOff()) {
         if (Reflector.reflectorMethod89.isActive()) {
            WorldProvider provider2 = this.mc.theWorld.provider;
            Object object = Reflector.createObject4(provider2, Reflector.reflectorMethod89);
            if (object != null) {
               Reflector.handleObject(object, Reflector.reflectorMethod95, partialTicks, this.theWorld, this.mc);
               return;
            }
         }

         if (this.mc.theWorld.provider.isSurfaceWorld()) {
            if (Config.isShaders()) {
               Shaders.beginClouds();
            }

            if (Config.isCloudsFancy()) {
               this.renderCloudsFancy(partialTicks, pass);
            } else {
               float partialTicks2 = partialTicks;
               partialTicks = 0.0F;
               GlStateManager.disableCull();
               float f = (float)(
                  this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
               );
               Tessellator instance2 = Tessellator.getInstance();
               WorldRenderer worldRenderer2 = instance2.getWorldRenderer();
               this.renderEngine.bindTexture(locationCloudsPng);
               GlStateManager.enableBlend();
               RenderUtils.applyBlendFunc();
               Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
               float f2 = (float)cloudColour.xCoord;
               float f3 = (float)cloudColour.yCoord;
               float f4 = (float)cloudColour.zCoord;
               this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, partialTicks2, cloudColour);
               if (this.cloudRenderer.shouldUpdateGlList()) {
                  this.cloudRenderer.startUpdateGlList();
                  if (pass != 2) {
                     float f5 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
                     float f6 = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
                     float f7 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;
                     f2 = f5;
                     f3 = f6;
                     f4 = f7;
                  }

                  double d2 = this.cloudTickCounter + partialTicks;
                  double d3 = this.mc.getRenderViewEntity().prevPosX
                     + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
                     + d2 * 0.03F;
                  double d4 = this.mc.getRenderViewEntity().prevPosZ
                     + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks;
                  int i = MathHelper.floor_double(d3 / 2048.0);
                  int i2 = MathHelper.floor_double(d4 / 2048.0);
                  d3 -= i * 2048;
                  d4 -= i2 * 2048;
                  float f8 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
                  f8 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                  float f9 = (float)(d3 * 4.8828125E-4);
                  float f10 = (float)(d4 * 4.8828125E-4);
                  worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                  for (int i3 = -256; i3 < 256; i3 += 32) {
                     for (int i4 = -256; i4 < 256; i4 += 32) {
                        worldRenderer2.pos(i3 + 0, f8, i4 + 32)
                           .tex((i3 + 0) * 4.8828125E-4F + f9, (i4 + 32) * 4.8828125E-4F + f10)
                           .color(f2, f3, f4, 0.8F)
                           .endVertex();
                        worldRenderer2.pos(i3 + 32, f8, i4 + 32)
                           .tex((i3 + 32) * 4.8828125E-4F + f9, (i4 + 32) * 4.8828125E-4F + f10)
                           .color(f2, f3, f4, 0.8F)
                           .endVertex();
                        worldRenderer2.pos(i3 + 32, f8, i4 + 0)
                           .tex((i3 + 32) * 4.8828125E-4F + f9, (i4 + 0) * 4.8828125E-4F + f10)
                           .color(f2, f3, f4, 0.8F)
                           .endVertex();
                        worldRenderer2.pos(i3 + 0, f8, i4 + 0)
                           .tex((i3 + 0) * 4.8828125E-4F + f9, (i4 + 0) * 4.8828125E-4F + f10)
                           .color(f2, f3, f4, 0.8F)
                           .endVertex();
                     }
                  }

                  instance2.draw();
                  this.cloudRenderer.endUpdateGlList();
               }

               this.cloudRenderer.renderGlList();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableBlend();
               GlStateManager.enableCull();
            }

            if (Config.isShaders()) {
               Shaders.endClouds();
            }
         }
      }
   }

   public void renderSky(float partialTicks, int pass) {
      if (Reflector.reflectorMethod90.isActive()) {
         WorldProvider provider2 = this.mc.theWorld.provider;
         Object object = Reflector.createObject4(provider2, Reflector.reflectorMethod90);
         if (object != null) {
            Reflector.handleObject(object, Reflector.reflectorMethod95, partialTicks, this.theWorld, this.mc);
            return;
         }
      }

      if (this.mc.theWorld.provider.getDimensionId() == 1) {
         this.renderSkyEnd();
      } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
         GlStateManager.disableTexture2D();
         boolean shaders2 = Config.isShaders();
         if (shaders2) {
            Shaders.disableTexture2D();
         }

         Vec3 skyColor = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
         skyColor = CustomColors.getSkyColor(
            skyColor, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (shaders2) {
            Shaders.setSkyColor(skyColor);
         }

         float f = (float)skyColor.xCoord;
         float f2 = (float)skyColor.yCoord;
         float f3 = (float)skyColor.zCoord;
         if (pass != 2) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
         }

         GlStateManager.color(f, f2, f3);
         Tessellator instance2 = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance2.getWorldRenderer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         if (shaders2) {
            Shaders.enableFog();
         }

         GlStateManager.color(f, f2, f3);
         if (shaders2) {
            Shaders.preSkyList();
         }

         if (Config.isSkyEnabled()) {
            if (this.vboEnabled) {
               this.skyVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.skyVBO.drawArrays(7);
               this.skyVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList);
            }
         }

         GlStateManager.disableFog();
         if (shaders2) {
            Shaders.disableFog();
         }

         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         RenderHelper.disableStandardItemLighting();
         float[] sunriseSunsetColors = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
         if (sunriseSunsetColors != null && Config.isSunMoonEnabled()) {
            GlStateManager.disableTexture2D();
            if (shaders2) {
               Shaders.disableTexture2D();
            }

            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f7 = sunriseSunsetColors[0];
            float f8 = sunriseSunsetColors[1];
            float f9 = sunriseSunsetColors[2];
            if (pass != 2) {
               float f10 = (f7 * 30.0F + f8 * 59.0F + f9 * 11.0F) / 100.0F;
               float f11 = (f7 * 30.0F + f8 * 70.0F) / 100.0F;
               float f12 = (f7 * 30.0F + f9 * 70.0F) / 100.0F;
               f7 = f10;
               f8 = f11;
               f9 = f12;
            }

            worldRenderer2.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer2.pos(0.0, 100.0, 0.0).color(f7, f8, f9, sunriseSunsetColors[3]).endVertex();

            for (int i = 0; i <= 16; i++) {
               float f13 = i * (float) Math.PI * 2.0F / 16.0F;
               float f14 = MathHelper.sin(f13);
               float f15 = MathHelper.cos(f13);
               worldRenderer2.pos(f14 * 120.0F, f15 * 120.0F, -f15 * 40.0F * sunriseSunsetColors[3]).color(sunriseSunsetColors[0], sunriseSunsetColors[1], sunriseSunsetColors[2], 0.0F).endVertex();
            }

            instance2.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture2D();
         if (shaders2) {
            Shaders.enableTexture2D();
         }

         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         GlStateManager.pushMatrix();
         float f16 = 1.0F - this.theWorld.getRainStrength(partialTicks);
         GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);
         if (shaders2) {
            Shaders.preCelestialRotate();
         }

         GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
         if (shaders2) {
            Shaders.postCelestialRotate();
         }

         float f17 = 30.0F;
         if (Config.isSunTexture()) {
            this.renderEngine.bindTexture(locationSunPng);
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer2.pos(-f17, 100.0, -f17).tex(0.0, 0.0).endVertex();
            worldRenderer2.pos(f17, 100.0, -f17).tex(1.0, 0.0).endVertex();
            worldRenderer2.pos(f17, 100.0, f17).tex(1.0, 1.0).endVertex();
            worldRenderer2.pos(-f17, 100.0, f17).tex(0.0, 1.0).endVertex();
            instance2.draw();
         }

         f17 = 20.0F;
         if (Config.isMoonTexture()) {
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int moonPhase = this.theWorld.getMoonPhase();
            int i2 = moonPhase % 4;
            int i3 = moonPhase / 4 % 2;
            float f18 = (i2 + 0) / 4.0F;
            float f19 = (i3 + 0) / 2.0F;
            float f20 = (i2 + 1) / 4.0F;
            float f21 = (i3 + 1) / 2.0F;
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer2.pos(-f17, -100.0, f17).tex(f20, f21).endVertex();
            worldRenderer2.pos(f17, -100.0, f17).tex(f18, f21).endVertex();
            worldRenderer2.pos(f17, -100.0, -f17).tex(f18, f19).endVertex();
            worldRenderer2.pos(-f17, -100.0, -f17).tex(f20, f19).endVertex();
            instance2.draw();
         }

         GlStateManager.disableTexture2D();
         if (shaders2) {
            Shaders.disableTexture2D();
         }

         float f22 = this.theWorld.getStarBrightness(partialTicks) * f16;
         if (f22 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
            GlStateManager.color(f22, f22, f22, f22);
            if (this.vboEnabled) {
               this.starVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.starVBO.drawArrays(7);
               this.starVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.starGLCallList);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.enableFog();
         if (shaders2) {
            Shaders.enableFog();
         }

         GlStateManager.popMatrix();
         GlStateManager.disableTexture2D();
         if (shaders2) {
            Shaders.disableTexture2D();
         }

         GlStateManager.color(0.0F, 0.0F, 0.0F);
         double d2 = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();
         if (d2 < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            float f23 = -((float)(d2 + 65.0));
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer2.pos(-1.0, f23, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, f23, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, f23, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, f23, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, f23, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, f23, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, f23, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, f23, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer2.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            instance2.draw();
         }

         if (this.theWorld.provider.isSkyColored()) {
            GlStateManager.color(f * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
         } else {
            GlStateManager.color(f, f2, f3);
         }

         if (this.mc.gameSettings.renderDistanceChunks <= 4) {
            GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -((float)(d2 - 16.0)), 0.0F);
         if (Config.isSkyEnabled()) {
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GlStateManager.run();
               GlStateManager.run2();
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               GlStateManager.run3();
            } else {
               GlStateManager.callList(this.glSkyList2);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         if (shaders2) {
            Shaders.enableTexture2D();
         }

         GlStateManager.depthMask(true);
      }
   }

   public void updateChunks(long finishTimeNano) {
      finishTimeNano = (long)(finishTimeNano + 1.0E8);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty | this.renderDispatcher.runChunkUploads(finishTimeNano);
      if (this.chunksToUpdateForced.size() > 0) {
         Iterator iterator2 = this.chunksToUpdateForced.iterator();

         while (iterator2.hasNext()) {
            RenderChunk renderChunk2 = (RenderChunk)iterator2.next();
            if (!this.renderDispatcher.updateChunkLater(renderChunk2)) {
               break;
            }

            renderChunk2.setNeedsUpdate(false);
            iterator2.remove();
            this.chunksToUpdate.remove(renderChunk2);
            this.chunksToResortTransparency.remove(renderChunk2);
         }
      }

      if (this.chunksToResortTransparency.size() > 0) {
         Iterator iterator3 = this.chunksToResortTransparency.iterator();
         if (iterator3.hasNext()) {
            RenderChunk renderChunk3 = (RenderChunk)iterator3.next();
            if (this.renderDispatcher.updateTransparencyLater(renderChunk3)) {
               iterator3.remove();
            }
         }
      }

      double d2 = 0.0;
      int updatesPerFrame = Config.getUpdatesPerFrame();
      if (!this.chunksToUpdate.isEmpty()) {
         Iterator iterator4 = this.chunksToUpdate.iterator();

         while (iterator4.hasNext()) {
            RenderChunk renderChunk4 = (RenderChunk)iterator4.next();
            boolean chunkRegionEmpty = renderChunk4.isChunkRegionEmpty();
            boolean flag;
            if (chunkRegionEmpty) {
               flag = this.renderDispatcher.updateChunkNow(renderChunk4);
            } else {
               flag = this.renderDispatcher.updateChunkLater(renderChunk4);
            }

            if (!flag) {
               break;
            }

            renderChunk4.setNeedsUpdate(false);
            iterator4.remove();
            if (!chunkRegionEmpty) {
               double d3 = 2.0 * RenderChunkUtils.getRelativeBufferSize(renderChunk4);
               d2 += d3;
               if (d2 > updatesPerFrame) {
                  break;
               }
            }
         }
      }
   }

   private void preRenderDamagedBlocks() {
      GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.doPolygonOffset(-1.0F, -10.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.pushMatrix();
      if (Config.isShaders()) {
         ShadersRender.beginBlockDamage();
      }
   }

   private void updateDestroyBlockIcons() {
      TextureMap textureMap = this.mc.getTextureMapBlocks();

      for (int index = 0; index < this.destroyBlockIcons.length; index++) {
         this.destroyBlockIcons[index] = textureMap.getAtlasSprite("minecraft:blocks/destroy_stage_" + index);
      }
   }

   public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
      if (this.theWorld != null) {
         this.theWorld.removeWorldAccess(this);
      }

      this.frustumUpdatePosX = Double.MIN_VALUE;
      this.frustumUpdatePosY = Double.MIN_VALUE;
      this.frustumUpdatePosZ = Double.MIN_VALUE;
      this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
      this.renderManager.set(worldClientIn);
      this.theWorld = worldClientIn;
      if (Config.isDynamicLights()) {
         DynamicLights.clear();
      }

      ChunkVisibility.reset();
      this.worldChunkProvider = null;
      this.worldChunkProviderMap = null;
      this.renderEnv.reset((IBlockState)null, (BlockPos)null);
      Shaders.checkWorldChanged(this.theWorld);
      if (worldClientIn != null) {
         worldClientIn.addWorldAccess(this);
         this.loadRenderers();
      } else {
         this.chunksToUpdate.clear();
         this.clearRenderInfos();
         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.viewFrustum = null;
      }
   }

   public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
      synchronized (this.setTileEntities) {
         this.setTileEntities.removeAll(tileEntitiesToRemove);
         this.setTileEntities.addAll(tileEntitiesToAdd);
      }
   }

   public void handleEntityPlayer(EntityPlayer entityPlayer, MovingObjectPosition movingObjectPosition, float f) {
      if (movingObjectPosition.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.disableTexture2D();
         if (Config.isShaders()) {
            Shaders.disableTexture2D();
         }

         GlStateManager.depthMask(false);
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         Block block2 = this.theWorld.getBlockState(blockPos).getBlock();
         if (block2.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockPos)) {
            block2.setBlockBoundsBasedOnState(this.theWorld, blockPos);
            double d2 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * f;
            double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * f;
            double d4 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * f;
            AxisAlignedBB selectedBoundingBox = block2.getSelectedBoundingBox(this.theWorld, blockPos);
            net.minecraft.block.Block.EnumOffsetType offsetType = block2.getOffsetType();
            if (offsetType != net.minecraft.block.Block.EnumOffsetType.NONE) {
               selectedBoundingBox = BlockModelUtils.getOffsetBoundingBox(selectedBoundingBox, offsetType, blockPos);
            }

            drawSelectionBoundingBox(selectedBoundingBox.expand(0.002F, 0.002F, 0.002F).offset(-d2, -d3, -d4));
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         if (Config.isShaders()) {
            Shaders.enableTexture2D();
         }

         GlStateManager.disableBlend();
      }
   }

   private void renderSkyEnd() {
      if (Config.isSkyEnabled()) {
         GlStateManager.disableFog();
         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.depthMask(false);
         this.renderEngine.bindTexture(locationEndSkyPng);
         Tessellator instance2 = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance2.getWorldRenderer();

         for (int i = 0; i < 6; i++) {
            GlStateManager.pushMatrix();
            if (i == 1) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3) {
               GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5) {
               GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int i2 = 40;
            int i3 = 40;
            int i4 = 40;
            if (Config.isCustomColors()) {
               Vec3 vec3 = new Vec3(40 / 255.0, 40 / 255.0, 40 / 255.0);
               vec3 = CustomColors.getWorldSkyColor(vec3, this.theWorld, this.mc.getRenderViewEntity(), 0.0F);
               i2 = (int)(vec3.xCoord * 255.0);
               i3 = (int)(vec3.yCoord * 255.0);
               i4 = (int)(vec3.zCoord * 255.0);
            }

            worldRenderer2.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(i2, i3, i4, 255).endVertex();
            worldRenderer2.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(i2, i3, i4, 255).endVertex();
            worldRenderer2.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(i2, i3, i4, 255).endVertex();
            worldRenderer2.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(i2, i3, i4, 255).endVertex();
            instance2.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   @Override
   public void onEntityRemoved(Entity entity2) {
      RandomEntities.entityUnloaded(entity2, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.entityRemoved(entity2, this);
      }
   }

   private RenderChunk getRenderChunkOffset(BlockPos arg1, RenderChunk arg2, EnumFacing arg3, boolean arg4, int arg5) {
      RenderChunk renderChunkNeighbour = arg2.getRenderChunkNeighbour(arg3);
      if (renderChunkNeighbour == null) {
         return null;
      }

      if (renderChunkNeighbour.getPosition().getY() > arg5) {
         return null;
      }

      if (arg4) {
         BlockPos position = renderChunkNeighbour.getPosition();
         int i = arg1.getX() - position.getX();
         int i2 = arg1.getZ() - position.getZ();
         if (i * i + i2 * i2 > this.renderDistanceSq) {
            return null;
         }
      }

      return renderChunkNeighbour;
   }

   @Override
   public void notifyLightSet(BlockPos blockPos) {
      int x3 = blockPos.getX();
      int y3 = blockPos.getY();
      int z3 = blockPos.getZ();
      this.markBlocksForUpdate(x3 - 1, y3 - 1, z3 - 1, x3 + 1, y3 + 1, z3 + 1);
   }

   private void fixTerrainFrustum(double x, double y, double z) {
      this.debugFixedClippingHelper = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
      Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
      matrix4f.transpose();
      Matrix4f matrix4f2 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
      matrix4f2.transpose();
      Matrix4f matrix4f3 = new Matrix4f();
      Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
      matrix4f3.invert();
      this.debugTerrainFrustumPosition.x = x;
      this.debugTerrainFrustumPosition.y = y;
      this.debugTerrainFrustumPosition.z = z;
      this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for (int index = 0; index < 8; index++) {
         Matrix4f.transform(matrix4f3, this.debugTerrainMatrix[index], this.debugTerrainMatrix[index]);
         this.debugTerrainMatrix[index].x = this.debugTerrainMatrix[index].x / this.debugTerrainMatrix[index].w;
         this.debugTerrainMatrix[index].y = this.debugTerrainMatrix[index].y / this.debugTerrainMatrix[index].w;
         this.debugTerrainMatrix[index].z = this.debugTerrainMatrix[index].z / this.debugTerrainMatrix[index].w;
         this.debugTerrainMatrix[index].w = 1.0F;
      }
   }

   public int getCountTileEntitiesRendered() {
      return this.countTileEntitiesRendered;
   }

   public int getCountActiveRenderers() {
      return this.renderInfos.size();
   }

   public int getCountChunksToUpdate() {
      return this.chunksToUpdate.size();
   }

   public String getDebugInfoEntities() {
      return "E: "
         + this.countEntitiesRendered
         + "/"
         + this.countEntitiesTotal
         + ", B: "
         + this.countEntitiesHidden
         + ", I: "
         + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered)
         + ", "
         + Config.getVersionDebug();
   }

   public void makeEntityOutlineShader() {
      if (OpenGlHelper.shadersSupported) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         ResourceLocation resourceLocation = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourceLocation);
            this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
         } catch (IOException ioException) {
            logger.warn("Failed to load shader: " + resourceLocation, ioException);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         } catch (JsonSyntaxException jsonSyntaxException) {
            logger.warn("Failed to load shader: " + resourceLocation, jsonSyntaxException);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         }
      } else {
         this.entityOutlineShader = null;
         this.entityOutlineFramebuffer = null;
      }
   }

   public void renderWorldBorder(Entity entityIn, float partialTicks) {
      Tessellator instance2 = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance2.getWorldRenderer();
      WorldBorder worldBorder = this.theWorld.getWorldBorder();
      double d2 = this.mc.gameSettings.renderDistanceChunks * 16;
      if (entityIn.posX >= worldBorder.maxX() - d2
         || entityIn.posX <= worldBorder.minX() + d2
         || entityIn.posZ >= worldBorder.maxZ() - d2
         || entityIn.posZ <= worldBorder.minZ() + d2) {
         if (Config.isShaders()) {
            Shaders.pushProgram();
            Shaders.useProgram(Shaders.ProgramTexturedLit);
         }

         double d3 = 1.0 - worldBorder.getClosestDistance(entityIn) / d2;
         d3 = Math.pow(d3, 4.0);
         double d4 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double d5 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double d6 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.renderEngine.bindTexture(locationForcefieldPng);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int id = worldBorder.getStatus().getID();
         float f = (id >> 16 & 0xFF) / 255.0F;
         float f2 = (id >> 8 & 0xFF) / 255.0F;
         float f3 = (id & 0xFF) / 255.0F;
         GlStateManager.color(f, f2, f3, (float)d3);
         GlStateManager.doPolygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlpha();
         GlStateManager.disableCull();
         float f4 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer2.setTranslation(-d4, -d5, -d6);
         double d7 = Math.max(MathHelper.floor_double(d6 - d2), worldBorder.minZ());
         double d8 = Math.min(MathHelper.ceiling_double_int(d6 + d2), worldBorder.maxZ());
         if (d4 > worldBorder.maxX() - d2) {
            float f5 = 0.0F;

            for (double d9 = d7; d9 < d8; f5 += 0.5F) {
               double d10 = Math.min(1.0, d8 - d9);
               float f6 = (float)d10 * 0.5F;
               worldRenderer2.pos(worldBorder.maxX(), 256.0, d9).tex(f4 + f5, f4 + 0.0F).endVertex();
               worldRenderer2.pos(worldBorder.maxX(), 256.0, d9 + d10).tex(f4 + f6 + f5, f4 + 0.0F).endVertex();
               worldRenderer2.pos(worldBorder.maxX(), 0.0, d9 + d10).tex(f4 + f6 + f5, f4 + 128.0F).endVertex();
               worldRenderer2.pos(worldBorder.maxX(), 0.0, d9).tex(f4 + f5, f4 + 128.0F).endVertex();
               d9++;
            }
         }

         if (d4 < worldBorder.minX() + d2) {
            float f7 = 0.0F;

            for (double d11 = d7; d11 < d8; f7 += 0.5F) {
               double d12 = Math.min(1.0, d8 - d11);
               float f8 = (float)d12 * 0.5F;
               worldRenderer2.pos(worldBorder.minX(), 256.0, d11).tex(f4 + f7, f4 + 0.0F).endVertex();
               worldRenderer2.pos(worldBorder.minX(), 256.0, d11 + d12).tex(f4 + f8 + f7, f4 + 0.0F).endVertex();
               worldRenderer2.pos(worldBorder.minX(), 0.0, d11 + d12).tex(f4 + f8 + f7, f4 + 128.0F).endVertex();
               worldRenderer2.pos(worldBorder.minX(), 0.0, d11).tex(f4 + f7, f4 + 128.0F).endVertex();
               d11++;
            }
         }

         d7 = Math.max(MathHelper.floor_double(d4 - d2), worldBorder.minX());
         d8 = Math.min(MathHelper.ceiling_double_int(d4 + d2), worldBorder.maxX());
         if (d6 > worldBorder.maxZ() - d2) {
            float f9 = 0.0F;

            for (double d13 = d7; d13 < d8; f9 += 0.5F) {
               double d14 = Math.min(1.0, d8 - d13);
               float f10 = (float)d14 * 0.5F;
               worldRenderer2.pos(d13, 256.0, worldBorder.maxZ()).tex(f4 + f9, f4 + 0.0F).endVertex();
               worldRenderer2.pos(d13 + d14, 256.0, worldBorder.maxZ()).tex(f4 + f10 + f9, f4 + 0.0F).endVertex();
               worldRenderer2.pos(d13 + d14, 0.0, worldBorder.maxZ()).tex(f4 + f10 + f9, f4 + 128.0F).endVertex();
               worldRenderer2.pos(d13, 0.0, worldBorder.maxZ()).tex(f4 + f9, f4 + 128.0F).endVertex();
               d13++;
            }
         }

         if (d6 < worldBorder.minZ() + d2) {
            float f11 = 0.0F;

            for (double d15 = d7; d15 < d8; f11 += 0.5F) {
               double d16 = Math.min(1.0, d8 - d15);
               float f12 = (float)d16 * 0.5F;
               worldRenderer2.pos(d15, 256.0, worldBorder.minZ()).tex(f4 + f11, f4 + 0.0F).endVertex();
               worldRenderer2.pos(d15 + d16, 256.0, worldBorder.minZ()).tex(f4 + f12 + f11, f4 + 0.0F).endVertex();
               worldRenderer2.pos(d15 + d16, 0.0, worldBorder.minZ()).tex(f4 + f12 + f11, f4 + 128.0F).endVertex();
               worldRenderer2.pos(d15, 0.0, worldBorder.minZ()).tex(f4 + f11, f4 + 128.0F).endVertex();
               d15++;
            }
         }

         instance2.draw();
         worldRenderer2.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableAlpha();
         GlStateManager.doPolygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlpha();
         RenderUtils.applyBlendFunc();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         if (Config.isShaders()) {
            Shaders.popProgram();
         }
      }
   }

   public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
      double d2 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double d3 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double d4 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      if (!this.damagedBlocks.isEmpty()) {
         this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         this.preRenderDamagedBlocks();
         worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
         worldRendererIn.setTranslation(-d2, -d3, -d4);
         worldRendererIn.noColor();
         Iterator iterator2 = this.damagedBlocks.values().iterator();

         while (iterator2.hasNext()) {
            DestroyBlockProgress destroyBlockProgress2 = (DestroyBlockProgress)iterator2.next();
            BlockPos position = destroyBlockProgress2.getPosition();
            double d5 = position.getX() - d2;
            double d6 = position.getY() - d3;
            double d7 = position.getZ() - d4;
            Block block2 = this.theWorld.getBlockState(position).getBlock();
            boolean flag;
            if (Reflector.reflectorMethod81.isActive()) {
               boolean flag2 = block2 instanceof BlockChest || block2 instanceof BlockEnderChest || block2 instanceof BlockSign || block2 instanceof BlockSkull;
               if (!flag2) {
                  TileEntity tileEntity = this.theWorld.getTileEntity(position);
                  if (tileEntity != null) {
                     flag2 = Reflector.checkObject(tileEntity, Reflector.reflectorMethod81);
                  }
               }

               flag = !flag2;
            } else {
               flag = !(block2 instanceof BlockChest) && !(block2 instanceof BlockEnderChest) && !(block2 instanceof BlockSign) && !(block2 instanceof BlockSkull);
            }

            if (flag) {
               if (d5 * d5 + d6 * d6 + d7 * d7 > 1024.0) {
                  iterator2.remove();
               } else {
                  IBlockState blockState = this.theWorld.getBlockState(position);
                  if (blockState.getBlock().getMaterial() != Material.air) {
                     int partialBlockDamage = destroyBlockProgress2.getPartialBlockDamage();
                     TextureAtlasSprite textureAtlasSprite = this.destroyBlockIcons[partialBlockDamage];
                     this.mc.getBlockRendererDispatcher().renderBlockDamage(blockState, position, textureAtlasSprite, this.theWorld);
                  }
               }
            }
         }

         tessellatorIn.draw();
         worldRendererIn.setTranslation(0.0, 0.0, 0.0);
         this.postRenderDamagedBlocks();
      }
   }

   private void postRenderDamagedBlocks() {
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
      if (Config.isShaders()) {
         ShadersRender.endBlockDamage();
      }
   }

   public WorldClient getWorld() {
      return this.theWorld;
   }

   public void onPlayerPositionSet() {
      if (this.firstWorldLoad) {
         this.loadRenderers();
         this.firstWorldLoad = false;
      }
   }

   @Override
   public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
      Random random = this.theWorld.rand;
      switch (sfxType) {
         case 1000:
            this.theWorld.handleBlockPos(blockPosIn, "random.click", 1.0F, 1.0F);
            break;
         case 1001:
            this.theWorld.handleBlockPos(blockPosIn, "random.click", 1.0F, 1.2F);
            break;
         case 1002:
            this.theWorld.handleBlockPos(blockPosIn, "random.bow", 1.0F, 1.2F);
            break;
         case 1003:
            this.theWorld.handleBlockPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 1004:
            this.theWorld.handleBlockPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
            break;
         case 1005:
            if (Item.getItemById(data) instanceof ItemRecord) {
               this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(data)).recordName);
            } else {
               this.theWorld.playRecord(blockPosIn, (String)null);
            }
            break;
         case 1006:
            this.theWorld.handleBlockPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 1007:
            this.theWorld.handleBlockPos(blockPosIn, "mob.ghast.charge", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1008:
            this.theWorld.handleBlockPos(blockPosIn, "mob.ghast.fireball", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1009:
            this.theWorld.handleBlockPos(blockPosIn, "mob.ghast.fireball", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1010:
            this.theWorld.handleBlockPos(blockPosIn, "mob.zombie.wood", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1011:
            this.theWorld.handleBlockPos(blockPosIn, "mob.zombie.metal", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1012:
            this.theWorld.handleBlockPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1014:
            this.theWorld.handleBlockPos(blockPosIn, "mob.wither.shoot", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1015:
            this.theWorld.handleBlockPos(blockPosIn, "mob.bat.takeoff", 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1016:
            this.theWorld.handleBlockPos(blockPosIn, "mob.zombie.infect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1017:
            this.theWorld.handleBlockPos(blockPosIn, "mob.zombie.unfect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            break;
         case 1020:
            this.theWorld.handleBlockPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 1021:
            this.theWorld.handleBlockPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 1022:
            this.theWorld.handleBlockPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 2000:
            int i = data % 3 - 1;
            int i2 = data / 3 % 3 - 1;
            double xCoordinate = blockPosIn.getX() + i * 0.6 + 0.5;
            double yCoordinate = blockPosIn.getY() + 0.5;
            double zCoordinate = blockPosIn.getZ() + i2 * 0.6 + 0.5;

            for (int index = 0; index < 10; index++) {
               double d2 = random.nextDouble() * 0.2 + 0.01;
               double d3 = xCoordinate + i * 0.01 + (random.nextDouble() - 0.5) * i2 * 0.5;
               double d4 = yCoordinate + (random.nextDouble() - 0.5) * 0.5;
               double d5 = zCoordinate + i2 * 0.01 + (random.nextDouble() - 0.5) * i * 0.5;
               double d6 = i * d2 + random.nextGaussian() * 0.01;
               double d7 = -0.03 + random.nextGaussian() * 0.01;
               double d8 = i2 * d2 + random.nextGaussian() * 0.01;
               this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, d6, d7, d8);
            }

            return;
         case 2001:
            Block block = Block.getBlockById(data & 4095);
            if (block.getMaterial() != Material.air) {
               this.mc
                  .getSoundHandler()
                  .playSound(
                     new PositionedSoundRecord(
                        new ResourceLocation(block.stepSound.getBreakSound()),
                        (block.stepSound.getVolume() + 1.0F) / 2.0F,
                        block.stepSound.getFrequency() * 0.8F,
                        blockPosIn.getX() + 0.5F,
                        blockPosIn.getY() + 0.5F,
                        blockPosIn.getZ() + 0.5F
                     )
                  );
            }

            this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 0xFF));
            break;
         case 2002:
            double xCoordinate2 = blockPosIn.getX();
            double yCoordinate2 = blockPosIn.getY();
            double zCoordinate2 = blockPosIn.getZ();

            for (int nestedIndex = 0; nestedIndex < 8; nestedIndex++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  xCoordinate2,
                  yCoordinate2,
                  zCoordinate2,
                  random.nextGaussian() * 0.15,
                  random.nextDouble() * 0.2,
                  random.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.potionitem),
                  data
               );
            }

            int colorFromDamage = Items.potionitem.getColorFromDamage(data);
            float f = (colorFromDamage >> 16 & 0xFF) / 255.0F;
            float f2 = (colorFromDamage >> 8 & 0xFF) / 255.0F;
            float f3 = (colorFromDamage >> 0 & 0xFF) / 255.0F;
            EnumParticleTypes particleTypes = EnumParticleTypes.SPELL;
            if (Items.potionitem.isEffectInstant(data)) {
               particleTypes = EnumParticleTypes.SPELL_INSTANT;
            }

            for (int nestedIndex2 = 0; nestedIndex2 < 100; nestedIndex2++) {
               double d9 = random.nextDouble() * 4.0;
               double d10 = random.nextDouble() * Math.PI * 2.0;
               double d11 = Math.cos(d10) * d9;
               double d12 = 0.01 + random.nextDouble() * 0.5;
               double d13 = Math.sin(d10) * d9;
               EntityFX entityFX = this.spawnEntityFX(
                  particleTypes.getParticleID(),
                  particleTypes.getShouldIgnoreRange(),
                  xCoordinate2 + d11 * 0.1,
                  yCoordinate2 + 0.3,
                  zCoordinate2 + d13 * 0.1,
                  d11,
                  d12,
                  d13
               );
               if (entityFX != null) {
                  float f4 = 0.75F + random.nextFloat() * 0.25F;
                  entityFX.setRBGColorF(f * f4, f2 * f4, f3 * f4);
                  entityFX.multiplyVelocity((float)d9);
               }
            }

            this.theWorld.handleBlockPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F);
            break;
         case 2003:
            double xCoordinate3 = blockPosIn.getX() + 0.5;
            double yCoordinate3 = blockPosIn.getY();
            double zCoordinate3 = blockPosIn.getZ() + 0.5;

            for (int outerIndex = 0; outerIndex < 8; outerIndex++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  xCoordinate3,
                  yCoordinate3,
                  zCoordinate3,
                  random.nextGaussian() * 0.15,
                  random.nextDouble() * 0.2,
                  random.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.ender_eye)
               );
            }

            for (double d14 = 0.0; d14 < Math.PI * 2; d14 += Math.PI / 20) {
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  xCoordinate3 + Math.cos(d14) * 5.0,
                  yCoordinate3 - 0.4,
                  zCoordinate3 + Math.sin(d14) * 5.0,
                  Math.cos(d14) * -5.0,
                  0.0,
                  Math.sin(d14) * -5.0
               );
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  xCoordinate3 + Math.cos(d14) * 5.0,
                  yCoordinate3 - 0.4,
                  zCoordinate3 + Math.sin(d14) * 5.0,
                  Math.cos(d14) * -7.0,
                  0.0,
                  Math.sin(d14) * -7.0
               );
            }

            return;
         case 2004:
            for (int outerIndex2 = 0; outerIndex2 < 20; outerIndex2++) {
               double xCoordinate4 = blockPosIn.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double yCoordinate4 = blockPosIn.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double zCoordinate4 = blockPosIn.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xCoordinate4, yCoordinate4, zCoordinate4, 0.0, 0.0, 0.0);
               this.theWorld.spawnParticle(EnumParticleTypes.FLAME, xCoordinate4, yCoordinate4, zCoordinate4, 0.0, 0.0, 0.0);
            }

            return;
         case 2005:
            ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
      }
   }

   public void resetClouds() {
      this.cloudRenderer.reset();
   }

   public boolean hasNoChunkUpdates() {
      return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
   }

   public int getCountRenderers() {
      return this.viewFrustum.renderChunks.length;
   }

   protected void stopChunkUpdates() {
      this.chunksToUpdate.clear();
      this.renderDispatcher.stopChunkUpdates();
   }

   private void generateSky() {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.skyVBO != null) {
         this.skyVBO.deleteGlBuffers();
      }

      if (this.glSkyList >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList);
         this.glSkyList = -1;
      }

      if (this.vboEnabled) {
         this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, 16.0F, false);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.skyVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList, 4864);
         this.renderSky(worldRenderer, 16.0F, false);
         tessellator.draw();
         GL11.glEndList();
      }
   }

   @Override
   public void broadcastSound(int soundID, BlockPos pos, int data) {
      switch (soundID) {
         case 1013:
         case 1018:
            if (this.mc.getRenderViewEntity() != null) {
               double xCoordinate = pos.getX() - this.mc.getRenderViewEntity().posX;
               double yCoordinate = pos.getY() - this.mc.getRenderViewEntity().posY;
               double zCoordinate = pos.getZ() - this.mc.getRenderViewEntity().posZ;
               double d2 = Math.sqrt(xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate);
               double xCoordinate2 = this.mc.getRenderViewEntity().posX;
               double yCoordinate2 = this.mc.getRenderViewEntity().posY;
               double zCoordinate2 = this.mc.getRenderViewEntity().posZ;
               if (d2 > 0.0) {
                  xCoordinate2 += xCoordinate / d2 * 2.0;
                  yCoordinate2 += yCoordinate / d2 * 2.0;
                  zCoordinate2 += zCoordinate / d2 * 2.0;
               }

               if (soundID == 1013) {
                  this.theWorld.playSound(xCoordinate2, yCoordinate2, zCoordinate2, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else {
                  this.theWorld.playSound(xCoordinate2, yCoordinate2, zCoordinate2, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   private void renderCloudsFancy(float partialTicks, int pass) {
      partialTicks = 0.0F;
      GlStateManager.disableCull();
      float f = (float)(
         this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
      );
      Tessellator instance2 = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance2.getWorldRenderer();
      double d2 = this.cloudTickCounter + partialTicks;
      double d3 = (
            this.mc.getRenderViewEntity().prevPosX
               + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
               + d2 * 0.03F
         )
         / 12.0;
      double d4 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks)
            / 12.0
         + 0.33F;
      float f2 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
      f2 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
      int i = MathHelper.floor_double(d3 / 2048.0);
      int i2 = MathHelper.floor_double(d4 / 2048.0);
      d3 -= i * 2048;
      d4 -= i2 * 2048;
      this.renderEngine.bindTexture(locationCloudsPng);
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
      float f3 = (float)cloudColour.xCoord;
      float f4 = (float)cloudColour.yCoord;
      float f5 = (float)cloudColour.zCoord;
      this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, cloudColour);
      if (pass != 2) {
         float f6 = (f3 * 30.0F + f4 * 59.0F + f5 * 11.0F) / 100.0F;
         float f7 = (f3 * 30.0F + f4 * 70.0F) / 100.0F;
         float f8 = (f3 * 30.0F + f5 * 70.0F) / 100.0F;
         f3 = f6;
         f4 = f7;
         f5 = f8;
      }

      float f9 = f3 * 0.9F;
      float f10 = f4 * 0.9F;
      float f11 = f5 * 0.9F;
      float f12 = f3 * 0.7F;
      float f13 = f4 * 0.7F;
      float f14 = f5 * 0.7F;
      float f15 = f3 * 0.8F;
      float f16 = f4 * 0.8F;
      float f17 = f5 * 0.8F;
      float f18 = MathHelper.floor_double(d3) * 0.00390625F;
      float f19 = MathHelper.floor_double(d4) * 0.00390625F;
      float f20 = (float)(d3 - MathHelper.floor_double(d3));
      float f21 = (float)(d4 - MathHelper.floor_double(d4));
      GlStateManager.scale(12.0F, 1.0F, 12.0F);

      for (int i3 = 0; i3 < 2; i3++) {
         if (i3 == 0) {
            GlStateManager.colorMask(false, false, false, false);
         } else {
            switch (pass) {
               case 0:
                  GlStateManager.colorMask(false, true, true, true);
                  break;
               case 1:
                  GlStateManager.colorMask(true, false, false, true);
                  break;
               case 2:
                  GlStateManager.colorMask(true, true, true, true);
            }
         }

         this.cloudRenderer.renderGlList();
      }

      if (this.cloudRenderer.shouldUpdateGlList()) {
         this.cloudRenderer.startUpdateGlList();

         for (int i4 = -3; i4 <= 4; i4++) {
            for (int i5 = -3; i5 <= 4; i5++) {
               worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               float f22 = i4 * 8;
               float f23 = i5 * 8;
               float f24 = f22 - f20;
               float f25 = f23 - f21;
               if (f2 > -5.0F) {
                  worldRenderer2.pos(f24 + 0.0F, f2 + 0.0F, f25 + 8.0F)
                     .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                     .color(f12, f13, f14, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 8.0F, f2 + 0.0F, f25 + 8.0F)
                     .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                     .color(f12, f13, f14, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 8.0F, f2 + 0.0F, f25 + 0.0F)
                     .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                     .color(f12, f13, f14, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 0.0F, f2 + 0.0F, f25 + 0.0F)
                     .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                     .color(f12, f13, f14, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
               }

               if (f2 <= 5.0F) {
                  worldRenderer2.pos(f24 + 0.0F, f2 + 4.0F - 9.765625E-4F, f25 + 8.0F)
                     .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                     .color(f3, f4, f5, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 8.0F, f2 + 4.0F - 9.765625E-4F, f25 + 8.0F)
                     .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                     .color(f3, f4, f5, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 8.0F, f2 + 4.0F - 9.765625E-4F, f25 + 0.0F)
                     .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                     .color(f3, f4, f5, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer2.pos(f24 + 0.0F, f2 + 4.0F - 9.765625E-4F, f25 + 0.0F)
                     .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                     .color(f3, f4, f5, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
               }

               if (i4 > -1) {
                  for (int i6 = 0; i6 < 8; i6++) {
                     worldRenderer2.pos(f24 + i6 + 0.0F, f2 + 0.0F, f25 + 8.0F)
                        .tex((f22 + i6 + 0.5F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i6 + 0.0F, f2 + 4.0F, f25 + 8.0F)
                        .tex((f22 + i6 + 0.5F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i6 + 0.0F, f2 + 4.0F, f25 + 0.0F)
                        .tex((f22 + i6 + 0.5F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i6 + 0.0F, f2 + 0.0F, f25 + 0.0F)
                        .tex((f22 + i6 + 0.5F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (i4 <= 1) {
                  for (int i7 = 0; i7 < 8; i7++) {
                     worldRenderer2.pos(f24 + i7 + 1.0F - 9.765625E-4F, f2 + 0.0F, f25 + 8.0F)
                        .tex((f22 + i7 + 0.5F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i7 + 1.0F - 9.765625E-4F, f2 + 4.0F, f25 + 8.0F)
                        .tex((f22 + i7 + 0.5F) * 0.00390625F + f18, (f23 + 8.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i7 + 1.0F - 9.765625E-4F, f2 + 4.0F, f25 + 0.0F)
                        .tex((f22 + i7 + 0.5F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + i7 + 1.0F - 9.765625E-4F, f2 + 0.0F, f25 + 0.0F)
                        .tex((f22 + i7 + 0.5F) * 0.00390625F + f18, (f23 + 0.0F) * 0.00390625F + f19)
                        .color(f9, f10, f11, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (i5 > -1) {
                  for (int i8 = 0; i8 < 8; i8++) {
                     worldRenderer2.pos(f24 + 0.0F, f2 + 4.0F, f25 + i8 + 0.0F)
                        .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + i8 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 8.0F, f2 + 4.0F, f25 + i8 + 0.0F)
                        .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + i8 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 8.0F, f2 + 0.0F, f25 + i8 + 0.0F)
                        .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + i8 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 0.0F, f2 + 0.0F, f25 + i8 + 0.0F)
                        .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + i8 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                  }
               }

               if (i5 <= 1) {
                  for (int i9 = 0; i9 < 8; i9++) {
                     worldRenderer2.pos(f24 + 0.0F, f2 + 4.0F, f25 + i9 + 1.0F - 9.765625E-4F)
                        .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + i9 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 8.0F, f2 + 4.0F, f25 + i9 + 1.0F - 9.765625E-4F)
                        .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + i9 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 8.0F, f2 + 0.0F, f25 + i9 + 1.0F - 9.765625E-4F)
                        .tex((f22 + 8.0F) * 0.00390625F + f18, (f23 + i9 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer2.pos(f24 + 0.0F, f2 + 0.0F, f25 + i9 + 1.0F - 9.765625E-4F)
                        .tex((f22 + 0.0F) * 0.00390625F + f18, (f23 + i9 + 0.5F) * 0.00390625F + f19)
                        .color(f15, f16, f17, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                  }
               }

               instance2.draw();
            }
         }

         this.cloudRenderer.endUpdateGlList();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
   }

   public int getCountLoadedChunks() {
      if (this.theWorld == null) {
         return 0;
      }

      IChunkProvider chunkProvider = this.theWorld.getChunkProvider();
      if (chunkProvider == null) {
         return 0;
      }

      if (chunkProvider != this.worldChunkProvider) {
         this.worldChunkProvider = chunkProvider;
         this.worldChunkProviderMap = (LongHashMap)Reflector.createObject6(chunkProvider, Reflector.reflectorField22);
      }

      return this.worldChunkProviderMap == null ? 0 : this.worldChunkProviderMap.getNumHashElements();
   }

   public void setDisplayListEntitiesDirty() {
      this.displayListEntitiesDirty = true;
   }

   public void renderEntityOutlineFramebuffer() {
      if (this.isRenderEntityOutlines()) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
         GlStateManager.disableBlend();
      }
   }

   public void resumeChunkUpdates() {
      if (this.renderDispatcher != null) {
         this.renderDispatcher.resumeChunkUpdates();
      }
   }

   private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
      this.mc.entityRenderer.enableLightmap();
      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32884);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32886);
      }

      if (Config.isShaders()) {
         ShadersRender.preRenderChunkLayer(blockLayerIn);
      }

      this.renderContainer.renderChunkLayer(blockLayerIn);
      if (Config.isShaders()) {
         ShadersRender.postRenderChunkLayer(blockLayerIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (VertexFormatElement vertexFormatElement : DefaultVertexFormats.BLOCK.getElements()) {
            net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage usage = vertexFormatElement.getUsage();
            int index2 = vertexFormatElement.getIndex();
            switch (usage) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index2);
                  GL11.glDisableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                  break;
               case COLOR:
                  GL11.glDisableClientState(32886);
                  GlStateManager.resetColor();
            }
         }
      }

      this.mc.entityRenderer.disableLightmap();
   }

   private void spawnParticle(
      EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
   }

   public void pauseChunkUpdates() {
      if (this.renderDispatcher != null) {
         this.renderDispatcher.pauseChunkUpdates();
      }
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      tessellator.draw();
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
   }

   public void updateClouds() {
      if (Config.isShaders()) {
         if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(24)) {
            GuiShaderOptions guiShaderOptions = new GuiShaderOptions((GuiScreen)null, Config.getGameSettings());
            Config.getMinecraft().displayGuiScreen(guiShaderOptions);
         }

         if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
         }
      }

      this.cloudTickCounter++;
      if (this.cloudTickCounter % 20 == 0) {
         this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
      }
   }

   private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
      while (iteratorIn.hasNext()) {
         int creationCloudUpdateTick = iteratorIn.next().getCreationCloudUpdateTick();
         if (this.cloudTickCounter - creationCloudUpdateTick > 400) {
            iteratorIn.remove();
         }
      }
   }

   @Override
   public void spawnParticle(
      int particleID,
      boolean ignoreRange,
      final double xCoord,
      final double yCoord,
      final double zCoord,
      double xOffset,
      double yOffset,
      double zOffset,
      int... parameters
   ) {
      try {
         this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being added");
         crashReportCategory.addCrashSection("ID", particleID);
         if (parameters != null) {
            crashReportCategory.addCrashSection("Parameters", parameters);
         }

         crashReportCategory.addCrashSectionCallable("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   private void clearRenderInfos() {
      if (renderEntitiesCounter > 0) {
         this.renderInfos = new ArrayList<>(this.renderInfos.size() + 16);
         this.renderInfosEntities = new ArrayList<>(this.renderInfosEntities.size() + 16);
         this.renderInfosTileEntities = new ArrayList<>(this.renderInfosTileEntities.size() + 16);
      } else {
         this.renderInfos.clear();
         this.renderInfosEntities.clear();
         this.renderInfosTileEntities.clear();
      }
   }

   public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
      return false;
   }

   public RenderChunk getRenderChunk(BlockPos arg1) {
      return this.viewFrustum.getRenderChunk(arg1);
   }

   public String getDebugInfoRenders() {
      int count = this.viewFrustum.renderChunks.length;
      int i = 0;
      Iterator iterator2 = this.renderInfos.iterator();

      while (iterator2.hasNext()) {
         CompiledChunk compiledChunk = ((RenderGlobal.ContainerLocalRenderInformation)iterator2.next()).renderChunk.compiledChunk;
         if (compiledChunk != CompiledChunk.DUMMY && !compiledChunk.isEmpty()) {
            i++;
         }
      }

      return String.format(
         "C: %d/%d %sD: %d, %s", i, count, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo()
      );
   }

   protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
      float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
      float f2 = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
      if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
         f += 180.0F;
      }

      float f3 = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f5 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
      float f6 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
      return new Vector3f(f4 * f5, f6, f3 * f5);
   }

   @Override
   public void playRecord(String recordName, BlockPos blockPosIn) {
      ISound sound = this.mapSoundPositions.get(blockPosIn);
      if (sound != null) {
         this.mc.getSoundHandler().stopSound(sound);
         this.mapSoundPositions.remove(blockPosIn);
      }

      if (recordName != null) {
         ItemRecord itemRecord = ItemRecord.getRecord(recordName);
         if (itemRecord != null) {
            this.mc.ingameGUI.setRecordPlayingMessage(itemRecord.getRecordNameLocal());
         }

         PositionedSoundRecord positionedSoundRecord = PositionedSoundRecord.create(
            new ResourceLocation(recordName), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ()
         );
         this.mapSoundPositions.put(blockPosIn, positionedSoundRecord);
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   public static class ContainerLocalRenderInformation {
      final RenderChunk renderChunk;
      EnumFacing facing;
      int setFacing;

      private void initialize(EnumFacing arg1, int arg2) {
         this.facing = arg1;
         this.setFacing = arg2;
      }

      public ContainerLocalRenderInformation(RenderChunk renderChunk2, EnumFacing enumFacing) {
         this.renderChunk = renderChunk2;
         this.facing = enumFacing;
         this.setFacing = 0;
      }
   }
}
