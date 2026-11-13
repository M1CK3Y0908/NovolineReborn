package net.optifine;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class DynamicLight {
   private Entity entity = null;
   private double offsetY = 0.0;
   private double lastPosX = -2.1474836E9F;
   private double lastPosY = -2.1474836E9F;
   private double lastPosZ = -2.1474836E9F;
   private int lastLightLevel = 0;
   private boolean underwater = false;
   private long timeCheckMs = 0L;
   private Set<BlockPos> setLitChunkPos = new HashSet<>();
   private net.minecraft.util.BlockPos.MutableBlockPos blockPosMutable = new net.minecraft.util.BlockPos.MutableBlockPos();

   public void updateLitChunks(RenderGlobal renderGlobal) {
      for (BlockPos blockPos9 : this.setLitChunkPos) {
         RenderChunk renderChunk9 = renderGlobal.getRenderChunk(blockPos9);
         this.updateChunkLight(renderChunk9, (Set<BlockPos>)null, (Set<BlockPos>)null);
      }
   }

   public void update(RenderGlobal renderGlobal) {
      if (Config.isDynamicLightsFast()) {
         long l = System.currentTimeMillis();
         if (l < this.timeCheckMs + 500L) {
            return;
         }

         this.timeCheckMs = l;
      }

      double xCoordinate = this.entity.posX - 0.5;
      double yCoordinate = this.entity.posY - 0.5 + this.offsetY;
      double zCoordinate = this.entity.posZ - 0.5;
      int lightLevel = DynamicLights.getLightLevel(this.entity);
      double d = xCoordinate - this.lastPosX;
      double d2 = yCoordinate - this.lastPosY;
      double d3 = zCoordinate - this.lastPosZ;
      double d4 = 0.1;
      if (Math.abs(d) > d4
         || Math.abs(d2) > d4
         || Math.abs(d3) > d4
         || this.lastLightLevel != lightLevel) {
         this.lastPosX = xCoordinate;
         this.lastPosY = yCoordinate;
         this.lastPosZ = zCoordinate;
         this.lastLightLevel = lightLevel;
         this.underwater = false;
         World world = renderGlobal.getWorld();
         if (world != null) {
            this.blockPosMutable.set(MathHelper.floor_double(xCoordinate), MathHelper.floor_double(yCoordinate), MathHelper.floor_double(zCoordinate));
            IBlockState blockState = world.getBlockState(this.blockPosMutable);
            Block block = blockState.getBlock();
            this.underwater = block == Blocks.water;
         }

         HashSet hashSet = new HashSet();
         if (lightLevel > 0) {
            EnumFacing facing = (MathHelper.floor_double(xCoordinate) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
            EnumFacing facing2 = (MathHelper.floor_double(yCoordinate) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
            EnumFacing facing3 = (MathHelper.floor_double(zCoordinate) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            BlockPos blockPos9 = new BlockPos(xCoordinate, yCoordinate, zCoordinate);
            RenderChunk renderChunk = renderGlobal.getRenderChunk(blockPos9);
            BlockPos blockPos2 = this.getChunkPos(renderChunk, blockPos9, facing);
            RenderChunk renderChunk2 = renderGlobal.getRenderChunk(blockPos2);
            BlockPos blockPos3 = this.getChunkPos(renderChunk, blockPos9, facing3);
            RenderChunk renderChunk3 = renderGlobal.getRenderChunk(blockPos3);
            BlockPos blockPos4 = this.getChunkPos(renderChunk2, blockPos2, facing3);
            RenderChunk renderChunk4 = renderGlobal.getRenderChunk(blockPos4);
            BlockPos blockPos5 = this.getChunkPos(renderChunk, blockPos9, facing2);
            RenderChunk renderChunk5 = renderGlobal.getRenderChunk(blockPos5);
            BlockPos blockPos6 = this.getChunkPos(renderChunk5, blockPos5, facing);
            RenderChunk renderChunk6 = renderGlobal.getRenderChunk(blockPos6);
            BlockPos blockPos7 = this.getChunkPos(renderChunk5, blockPos5, facing3);
            RenderChunk renderChunk7 = renderGlobal.getRenderChunk(blockPos7);
            BlockPos blockPos8 = this.getChunkPos(renderChunk6, blockPos6, facing3);
            RenderChunk renderChunk8 = renderGlobal.getRenderChunk(blockPos8);
            this.updateChunkLight(renderChunk, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk2, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk3, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk4, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk5, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk6, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk7, this.setLitChunkPos, hashSet);
            this.updateChunkLight(renderChunk8, this.setLitChunkPos, hashSet);
         }

         this.updateLitChunks(renderGlobal);
         this.setLitChunkPos = hashSet;
      }
   }

   public double getOffsetY() {
      return this.offsetY;
   }

   private BlockPos getChunkPos(RenderChunk renderChunk, BlockPos pos, EnumFacing facing) {
      return renderChunk != null ? renderChunk.getBlockPosOffset16(facing) : pos.offset(facing, 16);
   }

   public double getLastPosZ() {
      return this.lastPosZ;
   }

   public int getLastLightLevel() {
      return this.lastLightLevel;
   }

   private void updateChunkLight(RenderChunk renderChunk, Set<BlockPos> setPrevPos, Set<BlockPos> setNewPos) {
      if (renderChunk != null) {
         CompiledChunk compiledChunk = renderChunk.getCompiledChunk();
         if (compiledChunk != null && !compiledChunk.isEmpty()) {
            renderChunk.setNeedsUpdate(true);
         }

         BlockPos blockPos = renderChunk.getPosition();
         if (setPrevPos != null) {
            setPrevPos.remove(blockPos);
         }

         if (setNewPos != null) {
            setNewPos.add(blockPos);
         }
      }
   }

   public DynamicLight(Entity entity) {
      this.entity = entity;
      this.offsetY = entity.getEyeHeight();
   }

   public boolean isUnderwater() {
      return this.underwater;
   }

   public double getLastPosY() {
      return this.lastPosY;
   }

   public Entity getEntity() {
      return this.entity;
   }

   @Override
   public String toString() {
      double offsetY2 = this.offsetY;
      return "Entity: " + this.entity + ", offsetY: " + offsetY2;
   }

   public double getLastPosX() {
      return this.lastPosX;
   }
}
