package net.minecraft.network;

import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
   public static int lastDimensionId = Integer.MIN_VALUE;

   public static void handlePacket(final Packet packet2, final INetHandler iNetHandler, IThreadListener iThreadListener) throws ThreadQuickExitException {
      if (!iThreadListener.isCallingFromMinecraftThread()) {
         iThreadListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               PacketThreadUtil.clientPreProcessPacket(packet2);
               packet2.processPacket(iNetHandler);
            }
         });
         throw ThreadQuickExitException.threadQuickExitException;
      }

      clientPreProcessPacket(packet2);
   }

   protected static void clientPreProcessPacket(Packet packet) {
      if (packet instanceof S08PacketPlayerPosLook) {
         Config.getRenderGlobal().onPlayerPositionSet();
      }

      if (packet instanceof S07PacketRespawn) {
         lastDimensionId = ((S07PacketRespawn)packet).getDimensionID();
      } else if (packet instanceof S01PacketJoinGame) {
         lastDimensionId = ((S01PacketJoinGame)packet).getDimension();
      } else {
         lastDimensionId = Integer.MIN_VALUE;
      }
   }

   private static ThreadQuickExitException createThreadQuickExitException(ThreadQuickExitException threadQuickExitException2) {
      return threadQuickExitException2;
   }
}
