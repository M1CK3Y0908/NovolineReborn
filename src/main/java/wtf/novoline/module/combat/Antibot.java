package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.event.GamemodeUpdateEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class Antibot extends Module {
   private final MultiComboSetting antibot = settingManager.register(
      new MultiComboSetting(this, "Types").add("Hypixel", true).add("Tab list", false).add("Smart", false).add("Matrix", false).add("Hypixel staff", false)
   );
   private final List<Antibot.Helper> trackedPlayers = new CopyOnWriteArrayList<>();
   private final Stopwatch spawnDelayTimer = new Stopwatch();
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (this.antibot.isSelected(0)) {
            NetworkPlayerInfo playerInfo = ((AbstractClientPlayer)entityPlayer).getPlayerInfo();
            if (playerInfo == null) {
               entityPlayer.flag3 = true;
            } else {
               entityPlayer.flag3 = playerInfo.getResponseTime() == 0 && entityPlayer.getName().startsWith("§") || entityPlayer.hasCustomName();
            }
         }

         if (this.antibot.isSelected(4)) {
            for (Antibot.Helper helper2 : this.trackedPlayers) {
               if (helper2.entityId == entityPlayer.getEntityId()) {
                  double d = Math.hypot(entityPlayer.posX - entityPlayer.prevPosX, entityPlayer.posZ - entityPlayer.prevPosZ);
                  double d2 = Math.abs(helper2.lastSpeed - d);
                  if (helper2.stillTicks > 20 && minecraft.thePlayer.getDistanceToEntity(entityPlayer) <= 6.0F) {
                     entityPlayer.flag = true;
                  } else if (d2 == 0.0
                     && minecraft.thePlayer.motionY == 0.0
                     && !PlayerUtils.hasCollisionBelowEntity(2.0, entityPlayer)
                     && !entityPlayer.isInWater()
                     && !entityPlayer.isInLava()) {
                     helper2.stillTicks++;
                  }

                  helper2.lastSpeed = d;
               } else {
                  entityPlayer.flag = false;
               }
            }
         }
      }
   };
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.antibot.isSelected(4)) {
         if (packetReceiveEvent.packet instanceof S0CPacketSpawnPlayer s0CPacketSpawnPlayer) {
            if (!HypixelState.isGameType(HypixelGameType.LOBBY) && !HypixelState.isGameType(HypixelGameType.PRE_GAME) && this.spawnDelayTimer.hasElapsed(500L)) {
               this.trackedPlayers.add(new Antibot.Helper(s0CPacketSpawnPlayer.getPlayer(), s0CPacketSpawnPlayer.getEntityID()));
            }
         } else if (packetReceiveEvent.packet instanceof S13PacketDestroyEntities s13PacketDestroyEntities) {
            this.trackedPlayers.removeIf(helper -> Arrays.stream(s13PacketDestroyEntities.getEntityIDs()).boxed().anyMatch(integer -> helper.entityId == integer));
         }
      }
   };
   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> this.trackedPlayers.clear();
   @EventLink
   public final Listener<GamemodeUpdateEvent> onGamemodeUpdate = gamemodeUpdateEvent -> this.spawnDelayTimer.reset();

   private boolean hasOfflineUuid(EntityPlayer entityPlayer) {
      UUID id = entityPlayer.getGameProfile().getId();
      UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + entityPlayer.getName()).getBytes(StandardCharsets.UTF_8));
      return id.equals(uuid);
   }

   private boolean isInPlayerList(Collection collection, EntityPlayer entityPlayer) {
      return GuiPlayerTabOverlay.PLAYER_ORDERING.sortedCopy(collection).contains(minecraft.getNetHandler().getPlayerInfo(entityPlayer.getGameProfile().getId()));
   }

   public Antibot() {
      super(Category.COMBAT, "Antibot", "");
   }

   public boolean isPlayerBot(EntityPlayer entityPlayer) {
      if (this.antibot.isSelected(3) && !this.hasOfflineUuid(entityPlayer)) {
         return true;
      } else if (this.antibot.isSelected(2) && entityPlayer.badUuid) {
         return true;
      } else if (this.antibot.isSelected(0) && entityPlayer.flag3) {
         return true;
      } else if (this.antibot.isSelected(4) && entityPlayer.flag) {
         return true;
      } else {
         return this.antibot.isSelected(1) ? !this.isInPlayerList(minecraft.getNetHandler().getPlayerInfoMap(), entityPlayer) : false;
      }
   }

   static final class Helper {
      public int stillTicks;
      public final UUID uUID;
      public double lastSpeed;
      public final int entityId;

      public Helper(UUID uuid, int i) {
         this.uUID = uuid;
         this.entityId = i;
      }
   }
}
