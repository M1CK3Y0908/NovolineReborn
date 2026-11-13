package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.utils.NameUtils;

public class NameProtect extends Module {
   private final MultiComboSetting nameProtect = settingManager.register(
      new MultiComboSetting(this, "Hide options").add("Player name", true).add("Players in lobby", false).add("Server ID", true)
   );
   private final MultiComboSetting hideIn = settingManager.register(
      new MultiComboSetting(this, "Hide in").add("Tab menu", true).add("TargetHUD", true).add("NameTags", true).add("Scoreboard", false)
   );
   private final ComboSetting hideMode = settingManager.register(new ComboSetting(this, "Hide mode", 0, "Replace", "Obfuscate"));
   @EventLink(0)
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (packetReceiveEvent.packet instanceof S02PacketChat s02PacketChat) {
         IChatComponent iChatComponent = this.replaceNames(s02PacketChat.getChatComponent());
         s02PacketChat.setChatComponent(iChatComponent);
      } else if (packetReceiveEvent.packet instanceof S3EPacketTeams s3EPacketTeams) {
         String prefix = s3EPacketTeams.getPrefix();
         String text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM"));
         if (this.nameProtect.isSelected(2) && prefix != null && prefix.startsWith("§7" + text)) {
            String[] texts = prefix.split(" {2}");
            if (texts.length > 1) {
               prefix = this.replaceNameKeepingColors(prefix, NameUtils.stripFormatting(texts[1]));
               s3EPacketTeams.setPrefix(prefix);
               if (s3EPacketTeams.getSuffix() != null) {
                  String suffix = s3EPacketTeams.getSuffix();
                  s3EPacketTeams.setSuffix(this.replaceNameKeepingColors(suffix, NameUtils.stripFormatting(suffix)));
               }
            }
         }
      }
   };

   private IChatComponent replaceNames(IChatComponent iChatComponent) {
      return this.replaceNames(iChatComponent, null);
   }

   public String protectText(String text) {
      if (this.nameProtect.isSelected(0)) {
         String username = minecraft.session.getUsername();
         if (this.hideMode.getCurrentElement() == 0) {
            text = text.replace(username, "Me");
         } else {
            text = this.replaceNameKeepingColors(text, username);
         }
      }

      if (this.nameProtect.isSelected(1) && minecraft.getNetHandler() != null && minecraft.thePlayer != null && minecraft.thePlayer.ticksExisted > 10) {
         Iterator iterator2 = minecraft.getNetHandler().getPlayerInfoMap().iterator();

         while (iterator2.hasNext()) {
            String name2 = ((NetworkPlayerInfo)iterator2.next()).getGameProfile().getName();
            if (this.hideMode.getCurrentElement() == 0) {
               text = text.replace(name2, "Player");
            } else {
               text = this.replaceNameKeepingColors(text, name2);
            }
         }
      }

      return text;
   }

   public NameProtect() {
      super(Category.MISC, "NameProtect", "Hides player names");
   }

   private IChatComponent replaceNames(IChatComponent iChatComponent, IChatComponent iChatComponent2) {
      if (iChatComponent.getSiblings().isEmpty()) {
         IChatComponent iChatComponent3 = this.protectComponent(iChatComponent);
         if (iChatComponent2 != null) {
            iChatComponent3.getChatStyle().setParentStyle(iChatComponent2.getChatStyle());
         }

         return iChatComponent3;
      } else {
         IChatComponent copy = iChatComponent.createCopy();
         copy.getSiblings().clear();
         ChatComponentText chatComponentText = new ChatComponentText(copy.getUnformattedText());
         chatComponentText.setChatStyle(copy.getChatStyle());
         IChatComponent iChatComponent4 = this.protectComponent(chatComponentText);

         for (IChatComponent iChatComponent5 : iChatComponent.getSiblings()) {
            iChatComponent4.appendSibling(this.replaceNames(iChatComponent5, iChatComponent4));
         }

         return iChatComponent4;
      }
   }

   public IChatComponent protectComponent(IChatComponent iChatComponent) {
      return new ChatComponentText(this.protectText(iChatComponent.getUnformattedText())).setChatStyle(iChatComponent.getChatStyle());
   }

   public MultiComboSetting getHideIn() {
      return this.hideIn;
   }

   public String replaceNameKeepingColors(String text, String text2) {
      int i = text.indexOf(text2);
      if (i != -1) {
         char[] cArray = text.substring(0, i + text2.length()).toCharArray();
         String text3 = "";
         StringBuilder stringBuilder = new StringBuilder();

         for (int i2 = 0; i2 < cArray.length - 1; i2++) {
            if (cArray[i2] == 167) {
               char c = cArray[i2 + 1];
               if (c >= '0' && c <= 'f') {
                  text3 = "§" + c;
                  stringBuilder.setLength(0);
               } else {
                  stringBuilder.append("§").append(c);
               }
            }
         }

         text = text.replace(text2, "§k" + text2 + "§r" + text3 + stringBuilder);
      }

      return text;
   }
}
