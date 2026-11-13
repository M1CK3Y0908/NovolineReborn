package net.optifine.util;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class IntegratedServerUtils {
   public static TileEntity getTileEntity(BlockPos pos) {
      WorldServer worldServer = getWorldServer();
      if (worldServer == null) {
         return null;
      }

      Chunk chunk2 = worldServer.getChunkProvider().provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
      return chunk2 == null ? null : chunk2.getTileEntity(pos, net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK);
   }

   public static WorldServer getWorldServer() {
      Minecraft minecraft2 = Config.getMinecraft();
      WorldClient theWorld2 = minecraft2.theWorld;
      if (theWorld2 == null) {
         return null;
      }

      if (!minecraft2.isIntegratedServerRunning()) {
         return null;
      }

      IntegratedServer integratedServer = minecraft2.getIntegratedServer();
      if (integratedServer == null) {
         return null;
      }

      WorldProvider provider2 = theWorld2.provider;
      if (provider2 == null) {
         return null;
      }

      int dimensionId = provider2.getDimensionId();

      try {
         return integratedServer.worldServerForDimension(dimensionId);
      } catch (NullPointerException nullPointerException) {
         return null;
      }
   }

   public static Entity getEntity(UUID uUID) {
      WorldServer worldServer = getWorldServer();
      return worldServer == null ? null : worldServer.getEntityFromUuid(uUID);
   }
}
