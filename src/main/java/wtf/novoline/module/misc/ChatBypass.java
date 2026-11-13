package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.SettingManager;

public class ChatBypass extends Module {
   private final ComboSetting chatBypass = settingManager.register(new ComboSetting(this, "Mode", 0, "Unicode Font", "Invisible", "Period"));
   private final ComboSetting afterCharacter;
   private final ComboSetting font;
   private final CheckboxSetting onlyOddCharacters;
   private static final Map<Character, List<Character>> lookalikes = new ChatBypass.HashMapChild();
   @EventLink(0)
   private final Listener<PacketSendEvent> onPacketSend;

   public ChatBypass() {
      super(Category.MISC, "ChatBypass", "Bypasses chat filters");
      SettingManager settingManager2 = settingManager;
      String[] texts = new String[]{"After character", "Word end"};
      this.afterCharacter = settingManager2.register(new ComboSetting(this, "Insert type", 0, texts) {
         @Override
         public boolean shouldShow() {
            return ChatBypass.this.chatBypass.getCurrentElement() == 1;
         }
      });
      this.font = settingManager.register(new ComboSetting(this, "Font", 0, "Fullwidth Latin", "Type 1", "Type 2", "Type 3") {
         @Override
         public boolean shouldShow() {
            return ChatBypass.this.chatBypass.getCurrentElement() == 0;
         }
      });
      this.onlyOddCharacters = settingManager.register(new CheckboxSetting(this, "Only odd characters", true) {
         @Override
         public boolean shouldShow() {
            return ChatBypass.this.chatBypass.getCurrentElement() == 0;
         }
      });
      this.onPacketSend = packetSendEvent -> {
         Packet packet2 = packetSendEvent.packet;
         if (packet2 instanceof C01PacketChatMessage) {
            String message2 = ((C01PacketChatMessage)packet2).message;
            char[] cArray = message2.toCharArray();
            if (message2.startsWith("/")) {
               return;
            }

            int currentElement = this.chatBypass.getCurrentElement();

            String text = switch (currentElement) {
               case 0 -> {
                  int i2 = this.onlyOddCharacters.isEnabled() ? 2 : 1;
                  int currentElement2 = this.font.getCurrentElement();
                  if (currentElement2 == 0) {
                     for (int i3 = 0; i3 < cArray.length; i3 += i2) {
                        char c2 = cArray[i3];
                        if (c2 >= '!' && c2 < 128) {
                           cArray[i3] = (char)(c2 + 'ﻠ');
                        }
                     }
                  } else {
                     for (int i4 = 0; i4 < cArray.length; i4 += i2) {
                        List list = lookalikes.get(cArray[i4]);
                        if (list != null) {
                           char c3;
                           if (list.size() == 3) {
                              c3 = (Character)list.get(currentElement2 - 1);
                           } else {
                              int i5 = ThreadLocalRandom.current().nextInt(0, list.size());
                              c3 = (Character)list.get(i5);
                           }

                           cArray[i4] = c3;
                        }
                     }
                  }

                  yield String.valueOf(cArray);
               }
               case 1 -> {
                  StringBuilder stringBuilder = new StringBuilder();
                  if (this.afterCharacter.getCurrentElement() == 0) {
                     for (char c4 : cArray) {
                        stringBuilder.append(c4);
                        char c5 = (char)ThreadLocalRandom.current().nextInt(2048, 2080);
                        stringBuilder.append(c5);
                     }
                  } else {
                     for (String text2 : message2.split(" ")) {
                        if (text2.length() > 1) {
                           stringBuilder.append(text2, 0, text2.length() - 1);
                           stringBuilder.append('ﾟ');
                           stringBuilder.append(text2, text2.length() - 1, text2.length());
                        } else {
                           stringBuilder.append(text2);
                        }

                        stringBuilder.append(" ");
                     }
                  }

                  yield stringBuilder.toString();
               }
               case 2 -> {
                  StringBuilder stringBuilder2 = new StringBuilder();

                  for (char c6 : cArray) {
                     stringBuilder2.append(c6).append(".");
                  }

                  yield stringBuilder2.toString();
               }
               default -> throw new IllegalArgumentException("Unsupported mode: " + currentElement);
            };
            this.sendPacketUnregistered(new C01PacketChatMessage(text));
            packetSendEvent.cancel();
         }
      };
   }

   static class HashMapChild extends HashMap<Character, List<Character>> {
      HashMapChild() {
         this.put('a', List.of('ą', 'ᴀ', 'ᵃ'));
         this.put('b', List.of('ც', 'ʙ', 'ᵇ'));
         this.put('c', List.of('ƈ', 'ᴄ', 'ᶜ'));
         this.put('d', List.of('ᑺ', 'ᴅ', 'ᵈ'));
         this.put('e', List.of('є', 'ᴇ', 'ᵉ'));
         this.put('f', List.of('ϝ', 'ꜰ', 'ᶠ'));
         this.put('g', List.of('ɠ', 'ɢ', 'ᵍ'));
         this.put('h', List.of('ԋ', 'ʜ', 'ʰ'));
         this.put('i', List.of('є', 'ɪ', 'ⁱ'));
         this.put('j', List.of('ʝ', 'ᴊ', 'ʲ'));
         this.put('k', List.of('ƙ', 'ᴋ', 'ᵏ'));
         this.put('l', List.of('ʅ', 'ʟ', 'ˡ'));
         this.put('m', List.of('ɱ', 'ᴍ', 'ᵐ'));
         this.put('n', List.of('ภ', 'ɴ', 'ⁿ'));
         this.put('o', List.of('๏', 'ᴏ', 'ᵒ'));
         this.put('p', List.of('ק', 'ᴘ', 'ᵖ'));
         this.put('q', List.of('զ', 'զ', 'Ɋ'));
         this.put('r', List.of('Ɽ', 'ʀ', 'ʳ'));
         this.put('s', List.of('ѕ', 'ꜱ', 'ˢ'));
         this.put('t', List.of('է', 'ᴛ', 'ᵗ'));
         this.put('u', List.of('մ', 'ᴜ', 'ᵘ'));
         this.put('v', List.of('ѵ', 'ᴠ', 'ᵛ'));
         this.put('w', List.of('ա', 'ᴡ', 'ʷ'));
         this.put('x', List.of('×', 'x', 'ˣ'));
         this.put('y', List.of('ყ', 'ʏ', 'ʸ'));
         this.put('z', List.of('ȥ', 'ᴢ', 'ᶻ'));
         this.put('A', List.of('Ä', 'ᴀ', 'A'));
         this.put('B', List.of('ß', 'ʙ', 'ᴮ'));
         this.put('C', List.of('Ç', 'ᴄ', 'ᶜ'));
         this.put('D', List.of('Ð', 'ᴅ', 'ᴰ'));
         this.put('E', List.of('ᗴ', 'ᴇ', 'ᴱ'));
         this.put('F', List.of('Ƒ', 'ꜰ', 'ᶠ'));
         this.put('G', List.of('Ɠ', 'ɢ', 'ᴳ'));
         this.put('H', List.of('Ⱨ', 'ʜ', 'ᴴ'));
         this.put('I', List.of('Ì', 'ɪ', 'ᴵ'));
         this.put('J', List.of('ᒘ', 'ᴊ', 'ᴶ'));
         this.put('K', List.of('₭', 'ᴋ', 'ᴷ'));
         this.put('L', List.of('Ꝉ', 'ʟ', 'ᴸ'));
         this.put('M', List.of('Ɱ', 'ᴍ', 'ᴹ'));
         this.put('N', List.of('₦', 'ɴ', 'ᴺ'));
         this.put('O', List.of('Ö', 'ᴏ', 'ᴼ'));
         this.put('P', List.of('₱', 'ᴘ', 'ᴾ'));
         this.put('Q', List.of('ợ', 'զ', 'Ɋ'));
         this.put('R', List.of('Ɽ', 'ʀ', 'ᴿ'));
         this.put('S', List.of('Ŝ', 'ꜱ', 'ˢ'));
         this.put('T', List.of('₮', 'ᴛ', 'ᵀ'));
         this.put('U', List.of('Ú', 'ᴜ', 'ᵁ'));
         this.put('V', List.of('ᐺ', 'ᴠ', 'ⱽ'));
         this.put('W', List.of('₩', 'ᴡ', 'ᵂ'));
         this.put('X', List.of('Ӿ', 'x', 'ˣ'));
         this.put('Y', List.of('¥', 'ʏ', 'ʸ'));
         this.put('Z', List.of('Ⱬ', 'ᴢ', 'ᶻ'));
         this.put('/', List.of('᜵'));
         this.put('?', List.of('ʔ'));
         this.put('1', List.of('１', '₁', '¹'));
         this.put('2', List.of('ᒿ', '₂', '²'));
         this.put('3', List.of('З', '₃', '³'));
         this.put('4', List.of('Ꮞ', '₄', '⁴'));
         this.put('5', List.of('Ƽ', '₅', '⁵'));
         this.put('6', List.of('Ꮾ', '₆', '⁶'));
         this.put('7', List.of('７', '₇', '⁷'));
         this.put('8', List.of('৪', '₈', '⁸'));
         this.put('9', List.of('Ꝯ', '₉', '⁹'));
         this.put('0', List.of('੦', '₀', '⁰'));
      }
   }
}
