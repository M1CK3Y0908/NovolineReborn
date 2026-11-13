package wtf.novoline.utils.hypixel;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S38PacketPlayerListItem.Action;
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import wtf.novoline.Novoline;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.GameStartEvent;
import wtf.novoline.event.GamemodeUpdateEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.utils.NameUtils;
import wtf.novoline.utils.time.Stopwatch;

public class GameTracker {
   private final Minecraft minecraft = Minecraft.getMinecraft();
   private final Stopwatch leaveTimer = new Stopwatch();
   private final List<EntityPlayer> suspectedVictims = new CopyOnWriteArrayList<>();
   private boolean trackingKills = false;
   private HypixelGameType hypixelGameType;
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (HypixelState.isServerType(ServerType.HYPIXEL)) {
         if (packetReceiveEvent.packet instanceof S38PacketPlayerListItem s38PacketPlayerListItem) {
            if (s38PacketPlayerListItem.getAction() != Action.REMOVE_PLAYER || this.minecraft.theWorld == null) {
               return;
            }

            for (AddPlayerData addPlayerData : s38PacketPlayerListItem.getEntries()) {
               EntityPlayer playerEntityByUUID = this.minecraft.theWorld.getPlayerEntityByUUID(addPlayerData.getProfile().getId());
               if (playerEntityByUUID != null && playerEntityByUUID != this.minecraft.thePlayer && playerEntityByUUID.getHealth() < playerEntityByUUID.getMaxHealth()) {
                  this.trackingKills = true;
                  this.leaveTimer.reset();
                  this.suspectedVictims.add(playerEntityByUUID);
               }
            }
         } else if (packetReceiveEvent.packet instanceof S02PacketChat s02PacketChat) {
            if (HypixelState.isGameType(HypixelGameType.THE_PIT)) {
               IChatComponent chatComponent = s02PacketChat.getChatComponent();
               ClickEvent chatClickEvent = chatComponent.getChatStyle().getChatClickEvent();
               if (chatClickEvent != null && chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND && chatClickEvent.getValue().startsWith("/killrecap")) {
                  String unformattedText = chatComponent.getUnformattedText();
                  String[] texts = unformattedText.split(" ");
                  if (!unformattedText.contains("%") && !chatComponent.getFormattedText().startsWith(EnumChatFormatting.RED.toString())) {
                     String text = NameUtils.stripFormatting(texts[texts.length - 3]);
                     EntityPlayer playerEntityByName = this.minecraft.theWorld.getPlayerEntityByName(text);
                     Novoline.getNovoline().sendChatMessage("killed " + text);
                     if (playerEntityByName != null) {
                        Novoline.getNovoline().getBus().post(new PlayerKillEvent(playerEntityByName));
                     }
                  }
               }
            } else {
               String unformattedText2 = s02PacketChat.getChatComponent().getUnformattedText();

               for (EntityPlayer entityPlayer : this.suspectedVictims) {
                  if (this.trackingKills
                     && this.minecraft.thePlayer != null
                     && !this.leaveTimer.hasElapsed(10000L)
                     && unformattedText2.contains(this.minecraft.thePlayer.getName())
                     && unformattedText2.contains(entityPlayer.getName())) {
                     this.suspectedVictims.remove(entityPlayer);
                     Novoline.getNovoline().getBus().post(new PlayerKillEvent(entityPlayer));
                  }

                  if (this.suspectedVictims.isEmpty()) {
                     this.trackingKills = false;
                  }
               }
            }
         }
      }
   };
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive2 = packetReceiveEvent -> {
      if (HypixelState.isServerType(ServerType.HYPIXEL)) {
         Packet packet2 = packetReceiveEvent.packet;
         if (packet2 instanceof S02PacketChat) {
            IChatComponent chatComponent = ((S02PacketChat)packet2).getChatComponent();
            Iterator iterator2 = chatComponent.getSiblings().iterator();

            while (iterator2.hasNext()) {
               ClickEvent chatClickEvent = ((IChatComponent)iterator2.next()).getChatStyle().getChatClickEvent();
               if (chatClickEvent != null && chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND && chatClickEvent.getValue().startsWith("/play")) {
                  String text = chatClickEvent.getValue().substring("/play ".length());
                  boolean flag = chatComponent.getFormattedText().startsWith(EnumChatFormatting.GREEN.toString());
                  Novoline.getNovoline().getBus().post(new GameEndEvent(text, flag));
               }
            }
         }
      }
   };
   @EventLink
   public final Listener<GamemodeUpdateEvent> onGamemodeUpdate = gamemodeUpdateEvent -> {
      if (HypixelState.isServerType(ServerType.HYPIXEL)) {
         if (this.hypixelGameType == HypixelGameType.PRE_GAME
            && gamemodeUpdateEvent.hypixelGameType != HypixelGameType.UNKNOWN
            && gamemodeUpdateEvent.hypixelGameType != HypixelGameType.LOBBY) {
            Novoline.getNovoline().getBus().post(new GameStartEvent(gamemodeUpdateEvent.hypixelGameType));
         }

         this.hypixelGameType = gamemodeUpdateEvent.hypixelGameType;
      }
   };
}
