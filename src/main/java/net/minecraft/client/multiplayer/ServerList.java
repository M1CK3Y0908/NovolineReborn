package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   private final List<ServerData> servers = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;

   public ServerList(Minecraft mcIn) {
      this.mc = mcIn;
      this.loadServerList();
   }

   public void removeServerData(int index) {
      this.servers.remove(index);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void setServer(int index, ServerData server) {
      this.servers.set(index, server);
   }

   public ServerData getServerData(int index) {
      return this.servers.get(index);
   }

   public void saveServerList() {
      try {
         NBTTagList nbtTagList = new NBTTagList();

         for (ServerData serverData2 : this.servers) {
            nbtTagList.appendTag(serverData2.getNBTCompound());
         }

         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setTag("servers", nbtTagList);
         CompressedStreamTools.safeWrite(nbtTagCompound, new File(this.mc.mcDataDir, "servers.dat"));
      } catch (Exception exception) {
         logger.error("Couldn't save server list", exception);
      }
   }

   public void addServerData(ServerData server) {
      this.servers.add(server);
   }

   public static void saveSingleServer(ServerData server) {
      ServerList serverList = new ServerList(Minecraft.getMinecraft());
      serverList.loadServerList();

      for (int index = 0; index < serverList.countServers(); index++) {
         ServerData serverData = serverList.getServerData(index);
         if (serverData.serverName.equals(server.serverName) && serverData.serverIP.equals(server.serverIP)) {
            serverList.setServer(index, server);
            break;
         }
      }

      serverList.saveServerList();
   }

   public void swapServers(int i, int i2) {
      ServerData serverData = this.getServerData(i);
      this.servers.set(i, this.getServerData(i2));
      this.servers.set(i2, serverData);
      this.saveServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound nBTTagCompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
         if (nBTTagCompound == null) {
            return;
         }

         NBTTagList nBTTagList = nBTTagCompound.getTagList("servers", 10);

         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(nBTTagList.getCompoundTagAt(index)));
         }
      } catch (Exception exception) {
         logger.error("Couldn't load server list", exception);
      }
   }
}
