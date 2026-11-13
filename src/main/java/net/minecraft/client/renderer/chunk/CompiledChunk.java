package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledChunk {
   public static final CompiledChunk DUMMY = new CompiledChunk() {
      @Override
      public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
         return false;
      }

      @Override
      public void setAnimatedSprites(EnumWorldBlockLayer arg1, BitSet arg2) {
         throw new UnsupportedOperationException();
      }

      @Override
      protected void setLayerUsed(EnumWorldBlockLayer enumWorldBlockLayer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setLayerStarted(EnumWorldBlockLayer enumWorldBlockLayer2) {
         throw new UnsupportedOperationException();
      }
   };
   private final boolean[] layersUsed = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
   private net.minecraft.client.renderer.WorldRenderer.State state;
   private final boolean[] layersStarted = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
   private boolean empty = true;
   private final List<TileEntity> tileEntities = Lists.newArrayList();
   private SetVisibility setVisibility = new SetVisibility();
   private BitSet[] animatedSprites = new BitSet[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];

   public BitSet getAnimatedSprites(EnumWorldBlockLayer arg1) {
      return this.animatedSprites[arg1.ordinal()];
   }

   public void setState(net.minecraft.client.renderer.WorldRenderer.State stateIn) {
      this.state = stateIn;
   }

   public void setVisibility(SetVisibility visibility) {
      this.setVisibility = visibility;
   }

   public boolean isLayerEmpty(EnumWorldBlockLayer layer) {
      return !this.layersUsed[layer.ordinal()];
   }

   public boolean isLayerStarted(EnumWorldBlockLayer layer) {
      return this.layersStarted[layer.ordinal()];
   }

   public boolean isEmpty() {
      return this.empty;
   }

   public void setAnimatedSprites(EnumWorldBlockLayer arg1, BitSet arg2) {
      this.animatedSprites[arg1.ordinal()] = arg2;
   }

   public void setLayerStarted(EnumWorldBlockLayer layer) {
      this.layersStarted[layer.ordinal()] = true;
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.setVisibility.isVisible(facing, facing2);
   }

   public List<TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public net.minecraft.client.renderer.WorldRenderer.State getState() {
      return this.state;
   }

   protected void setLayerUsed(EnumWorldBlockLayer layer) {
      this.empty = false;
      this.layersUsed[layer.ordinal()] = true;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.tileEntities.add(tileEntityIn);
   }
}
