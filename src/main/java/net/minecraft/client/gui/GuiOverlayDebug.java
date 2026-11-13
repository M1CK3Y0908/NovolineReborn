package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui {
   private String debugOF = null;
   private List<String> debugInfoLeft = null;
   private List<String> debugInfoRight = null;
   private long updateInfoLeftTimeMs = 0L;
   private long updateInfoRightTimeMs = 0L;
   private final Minecraft mc;
   private final FontRenderer fontRenderer;

   private static long bytesToMb(long bytes) {
      return bytes / 1024L / 1024L;
   }

   protected List<String> getDebugInfoRight() {
      long l = Runtime.getRuntime().maxMemory();
      long l2 = Runtime.getRuntime().totalMemory();
      long l3 = Runtime.getRuntime().freeMemory();
      long l4 = l2 - l3;
      ArrayList arrayList = Lists.newArrayList(
         new String[]{
            String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
            String.format("Mem: % 2d%% %03d/%03dMB", l4 * 100L / l, bytesToMb(l4), bytesToMb(l)),
            String.format("Allocated: % 2d%% %03dMB", l2 * 100L / l, bytesToMb(l2)),
            "",
            String.format("CPU: %s", OpenGlHelper.getCpu()),
            "",
            String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)),
            GL11.glGetString(7937),
            GL11.glGetString(7938)
         }
      );
      long bufferAllocated = NativeMemory.getBufferAllocated();
      long bufferMaximum = NativeMemory.getBufferMaximum();
      long l5 = bytesToMb(bufferAllocated);
      long l6 = bytesToMb(bufferMaximum);
      long l7 = l5;
      String text2 = "Native: " + l7 + "/" + l6 + "MB";
      arrayList.add(4, text2);
      arrayList.set(5, "GC: " + MemoryMonitor.getAllocationRateMb() + "MB/s");
      if (Reflector.reflectorMethod14.isActive()) {
         Object object = Reflector.createObject2(Reflector.reflectorMethod17);
         arrayList.add("");
         arrayList.addAll((Collection)Reflector.createObject4(object, Reflector.reflectorMethod14, false));
      }

      if (this.isReducedDebug()) {
         return arrayList;
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
         IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
         if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
            blockState = blockState.getBlock().getActualState(blockState, this.mc.theWorld, blockPos);
         }

         arrayList.add("");
         arrayList.add(String.valueOf(Block.blockRegistry.getNameForObject(blockState.getBlock())));
         UnmodifiableIterator unmodifiableIterator = blockState.getProperties().entrySet().iterator();

         while (unmodifiableIterator.hasNext()) {
            Entry entry = (Entry)unmodifiableIterator.next();
            String text3 = ((Comparable)entry.getValue()).toString();
            if (entry.getValue() == Boolean.TRUE) {
               text3 = EnumChatFormatting.GREEN + text3;
            } else if (entry.getValue() == Boolean.FALSE) {
               text3 = EnumChatFormatting.RED + text3;
            }

            arrayList.add(((IProperty)entry.getKey()).getName() + ": " + text3);
         }
      }

      return arrayList;
   }

   public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
      this.mc.mcProfiler.startSection("debug");
      GlStateManager.pushMatrix();
      this.renderDebugInfoLeft();
      this.renderDebugInfoRight(scaledResolutionIn);
      GlStateManager.popMatrix();
      if (this.mc.gameSettings.showLagometer) {
         this.renderLagometer();
      }

      this.mc.mcProfiler.endSection();
   }

   protected void renderDebugInfoLeft() {
      List debugInfoLeft2 = this.debugInfoLeft;
      if (debugInfoLeft2 == null || System.currentTimeMillis() > this.updateInfoLeftTimeMs) {
         debugInfoLeft2 = this.call();
         this.debugInfoLeft = debugInfoLeft2;
         this.updateInfoLeftTimeMs = System.currentTimeMillis() + 100L;
      }

      for (int i = 0; i < debugInfoLeft2.size(); i++) {
         String text2 = (String)debugInfoLeft2.get(i);
         if (!Strings.isNullOrEmpty(text2)) {
            int i2 = this.fontRenderer.FONT_HEIGHT;
            int intValue = this.fontRenderer.getInt3(text2);
            int i3 = 2 + i2 * i;
            handleInt(1, i3 - 1, 2 + intValue + 1, i3 + i2 - 1, -1873784752);
            this.fontRenderer.getInt(text2, 2, i3, 14737632);
         }
      }
   }

   protected void renderDebugInfoRight(ScaledResolution scaledRes) {
      List<String> list = this.debugInfoRight;
      if (list == null || System.currentTimeMillis() > this.updateInfoRightTimeMs) {
         list = this.getDebugInfoRight();
         this.debugInfoRight = list;
         this.updateInfoRightTimeMs = System.currentTimeMillis() + 100L;
      }

      for (int index = 0; index < list.size(); index++) {
         String text = list.get(index);
         if (!Strings.isNullOrEmpty(text)) {
            int i = this.fontRenderer.FONT_HEIGHT;
            int intValue = this.fontRenderer.getInt3(text);
            int i2 = scaledRes.getScaledWidth() - 2 - intValue;
            int i3 = 2 + i * index;
            handleInt(i2 - 1, i3 - 1, i2 + intValue + 1, i3 + i - 1, -1873784752);
            this.fontRenderer.getInt(text, i2, i3, 14737632);
         }
      }
   }

   protected List<String> call() {
      BlockPos blockPos = new BlockPos(
         this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ
      );
      if (this.mc.debug != this.debugOF) {
         StringBuffer stringBuffer = new StringBuffer(this.mc.debug);
         int fpsMin = Config.getFpsMin();
         int i = this.mc.debug.indexOf(" fps ");
         if (i >= 0) {
            stringBuffer.insert(i, "/" + fpsMin);
         }

         if (Config.isSmoothFps()) {
            stringBuffer.append(" sf");
         }

         if (Config.isAnisotropicFiltering()) {
            stringBuffer.append(" af");
         }

         if (Config.isAntialiasing()) {
            stringBuffer.append(" aa");
         }

         if (Config.isRenderRegions()) {
            stringBuffer.append(" reg");
         }

         if (Config.isShaders()) {
            stringBuffer.append(" sh");
         }

         this.mc.debug = stringBuffer.toString();
         this.debugOF = this.mc.debug;
      }

      StringBuilder stringBuilder = new StringBuilder();
      TextureMap textureMap = Config.getTextureMap();
      stringBuilder.append(", A: ");
      if (SmartAnimations.isActive()) {
         stringBuilder.append(textureMap.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
         stringBuilder.append("/");
      }

      stringBuilder.append(textureMap.getCountAnimations() + TextureAnimations.getCountAnimations());
      String text = stringBuilder.toString();
      if (this.isReducedDebug()) {
         return Lists.newArrayList(
            new String[]{
               "Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
               this.mc.debug,
               this.mc.renderGlobal.getDebugInfoRenders(),
               this.mc.renderGlobal.getDebugInfoEntities(),
               "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + text,
               this.mc.theWorld.getProviderName(),
               "",
               String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
            }
         );
      }

      Entity entity = this.mc.getRenderViewEntity();
      EnumFacing facing = entity.getHorizontalFacing();
      String message = "Invalid";
      switch (facing) {
         case NORTH:
            message = "Towards negative Z";
            break;
         case SOUTH:
            message = "Towards positive Z";
            break;
         case WEST:
            message = "Towards negative X";
            break;
         case EAST:
            message = "Towards positive X";
      }

      List<String> list = Lists.newArrayList(
         new String[]{
            "Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
            this.mc.debug,
            this.mc.renderGlobal.getDebugInfoRenders(),
            this.mc.renderGlobal.getDebugInfoEntities(),
            "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + text,
            this.mc.theWorld.getProviderName(),
            "",
            String.format(
               "XYZ: %.3f / %.5f / %.3f",
               this.mc.getRenderViewEntity().posX,
               this.mc.getRenderViewEntity().getEntityBoundingBox().minY,
               this.mc.getRenderViewEntity().posZ
            ),
            String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()),
            String.format(
               "Chunk: %d %d %d in %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4
            ),
            String.format(
               "Facing: %s (%s) (%.1f / %.1f)",
               facing,
               message,
               MathHelper.wrapAngleTo180_float(entity.rotationYaw),
               MathHelper.wrapAngleTo180_float(entity.rotationPitch)
            )
         }
      );
      if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockPos)) {
         Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockPos);
         list.add("Biome: " + chunk.getBiome(blockPos, this.mc.theWorld.getWorldChunkManager()).biomeName);
         list.add(
            "Light: "
               + chunk.getLightSubtracted(blockPos, 0)
               + " ("
               + chunk.getLightFor(EnumSkyBlock.SKY, blockPos)
               + " sky, "
               + chunk.getLightFor(EnumSkyBlock.BLOCK, blockPos)
               + " block)"
         );
         DifficultyInstance difficultyInstance = this.mc.theWorld.getDifficultyForLocation(blockPos);
         if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
            EntityPlayerMP entityPlayerMP = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());
            if (entityPlayerMP != null) {
               DifficultyInstance difficultyInstance2 = this.mc.getIntegratedServer().getDifficultyAsync(entityPlayerMP.worldObj, new BlockPos(entityPlayerMP));
               if (difficultyInstance2 != null) {
                  difficultyInstance = difficultyInstance2;
               }
            }
         }

         list.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyInstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
      }

      if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
         list.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos blockPos2 = this.mc.objectMouseOver.getBlockPos();
         list.add(String.format("Looking at: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
      }

      return list;
   }

   private boolean isReducedDebug() {
      return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
   }

   private void renderLagometer() {
   }

   public GuiOverlayDebug(Minecraft mc) {
      this.mc = mc;
      this.fontRenderer = mc.fontRendererObj;
   }
}
