package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat.AnimatedLine;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.novoline.Novoline;
import wtf.novoline.client.discord.GatewayChatMessage;
import wtf.novoline.module.hud.HudManager;
import wtf.novoline.util.Room;

public class GuiChat extends GuiScreen {
   protected GuiTextField inputField;
   private final HudManager commandSuggestions = Novoline.getNovoline().getHudManager();
   private String historyBuffer = "";
   private int sentHistoryCursor = -1;
   private int autocompleteIndex;
   private static final String text = "§7[§r%s§7] §r%s%s §7(%d)§r%s§r: %s";
   private List<String> foundPlayerNames = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   private boolean playerNamesFound;
   private boolean waitingOnAutocomplete;
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
   private String defaultInputFieldText = "";

   public void onAutocompleteResponse(String[] matches) {
      if (this.waitingOnAutocomplete) {
         this.playerNamesFound = false;
         this.foundPlayerNames.clear();

         for (String text2 : matches) {
            if (text2.length() > 0) {
               this.foundPlayerNames.add(text2);
            }
         }

         String text3 = this.inputField.getText().substring(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false));
         String commonPrefix = StringUtils.getCommonPrefix(matches);
         if (commonPrefix.length() > 0 && !text3.equalsIgnoreCase(commonPrefix)) {
            this.inputField
               .deleteFromCursor(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            this.inputField.writeText(commonPrefix);
         } else if (this.foundPlayerNames.size() > 0) {
            this.playerNamesFound = true;
            this.autocompletePlayerNames();
         }
      }
   }

   public GuiChat(String defaultText) {
      this.defaultInputFieldText = defaultText;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.waitingOnAutocomplete = false;
      if (keyCode == 15) {
         this.autocompletePlayerNames();
      } else {
         this.playerNamesFound = false;
      }

      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (keyCode == 28 || keyCode == 156) {
         String text = this.inputField.getText().trim();
         if (text.length() > 0) {
            this.sendChatMessage(text);
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (keyCode == 200) {
         this.getSentHistory(-1);
      } else if (keyCode == 208) {
         this.getSentHistory(1);
      } else if (keyCode == 201) {
         this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
      } else if (keyCode == 209) {
         this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
      } else {
         this.inputField.textboxKeyTyped(typedChar, keyCode);
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   public void getSentHistory(int msgPos) {
      int i = this.sentHistoryCursor + msgPos;
      int size2 = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      i = MathHelper.clamp_int(i, 0, size2);
      if (i != this.sentHistoryCursor) {
         if (i == size2) {
            this.sentHistoryCursor = size2;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == size2) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.sentHistoryCursor = i;
         }
      }
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   @Override
   protected void setText(String newChatText, boolean shouldOverwrite) {
      if (shouldOverwrite) {
         this.inputField.setText(newChatText);
      } else {
         this.inputField.writeText(newChatText);
      }
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      this.commandSuggestions.getHudInputHandler().handleMouseRelease(i, i2);
      super.mouseReleased(i, i2, i3);
   }

   @Override
   public void updateScreen() {
      this.inputField.updateCursorCounter();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0) {
         if (eventDWheel > 1) {
            eventDWheel = 1;
         }

         if (eventDWheel < -1) {
            eventDWheel = -1;
         }

         if (!isShiftKeyDown()) {
            eventDWheel *= 7;
         }

         this.mc.ingameGUI.getChatGUI().scroll(eventDWheel);
      }
   }

   private void sendAutocompleteRequest(String message, String autocompletePrefix) {
      if (message.length() >= 1) {
         BlockPos blockPos = null;
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            blockPos = this.mc.objectMouseOver.getBlockPos();
         }

         this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(message, blockPos));
         this.waitingOnAutocomplete = true;
      }
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      if (i3 == 0) {
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         GuiNewChat chatGUI = Minecraft.getMinecraft().ingameGUI.getChatGUI();
         int scaleFactor = scaledResolution.getScaleFactor();
         float chatScale = chatGUI.getChatScale();
         int i4 = Mouse.getX() / scaleFactor - 3;
         int i5 = Mouse.getY() / scaleFactor - 40;
         i4 = MathHelper.floor_float(i4 / chatScale);
         i5 = MathHelper.floor_float(i5 / chatScale);
         i4 += 3;
         Collection collection = Novoline.getNovoline().getIrcConnection().getGatewayClient().getRooms().values();
         float f = 0.0F;
         f -= chatGUI.getChatHeight() + 4.0F;
         int i6 = MathHelper.ceiling_float_int(chatGUI.getChatWidth() / chatScale);
         int i7 = 1 + collection.size();
         float f2 = (i6 - 2.0F * (i7 - 1)) / i7;
         float f3 = 0.0F;
         float f4 = 12.0F;
         i5 = (int)(i5 + 1.0F);
         if (i5 > 0) {
            i5 = -i5;
         }

         if (i4 > f3 && i4 < f3 + f2 && i5 > f && i5 < f + f4 && GuiNewChat.selectedRoom != 0) {
            GuiNewChat.selectedRoom = 0;
            chatGUI.resetScroll();
         }

         f3 += f2 + 2.0F;
         int i8 = 1;

         for (Room room : (Iterable<Room>) collection) {
            if (i4 > f3 && i4 < f3 + f2 && i5 > f && i5 < f + f4 && GuiNewChat.selectedRoom != i8) {
               GuiNewChat.selectedRoom = i8;
               GuiNewChat.roomLines.clear();

               for (GatewayChatMessage gatewayChatMessage : room.getMessages().values()) {
                  GuiNewChat.roomLines.add(createAnimatedLine(gatewayChatMessage));
               }

               chatGUI.resetScroll();
            }

            f3 += f2 + 2.0F;
            i8++;
         }
      }

      this.commandSuggestions.getHudInputHandler().handleMouseClick(i, i2, i3);
      if (i3 == 0) {
         IChatComponent chatComponent2 = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
         if (this.handleComponentClick(chatComponent2)) {
            return;
         }
      }

      this.inputField.mouseClicked(i, i2, i3);
      super.mouseClicked(i, i2, i3);
   }

   public GuiChat() {
   }

   public static AnimatedLine createAnimatedLine(GatewayChatMessage gatewayChatMessage) {
      AnimatedLine animatedLine = new AnimatedLine();
      animatedLine.alpha = 255.0F;
      animatedLine.createdAt = System.currentTimeMillis();
      animatedLine.text = "§7[§r%s§7] §r%s%s §7(%d)§r%s§r: %s"
         .formatted(
            dateTimeFormatter.format(Instant.ofEpochMilli(gatewayChatMessage.getId().getTimestamp())),
            gatewayChatMessage.getNamePrefix(),
            gatewayChatMessage.getSenderUsername(),
            gatewayChatMessage.getSenderId(),
            gatewayChatMessage.getNameSuffix(),
            gatewayChatMessage.getContent()
         );
      return animatedLine;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      handleInt(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      this.inputField.drawTextBox();
      IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
      if (chatComponent != null && chatComponent.getChatStyle().getChatHoverEvent() != null) {
         this.handleComponentHover(chatComponent, mouseX, mouseY);
      }

      this.commandSuggestions.getHudRenderer().renderForEditor(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(256);
      this.inputField.update();
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
   }

   public void autocompletePlayerNames() {
      if (this.playerNamesFound) {
         this.inputField
            .deleteFromCursor(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
         if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
            this.autocompleteIndex = 0;
         }
      } else {
         int nthWordFromPos = this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false);
         this.foundPlayerNames.clear();
         this.autocompleteIndex = 0;
         String text2 = this.inputField.getText().substring(nthWordFromPos).toLowerCase();
         String text3 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
         this.sendAutocompleteRequest(text3, text2);
         if (this.foundPlayerNames.isEmpty()) {
            return;
         }

         this.playerNamesFound = true;
         this.inputField.deleteFromCursor(nthWordFromPos - this.inputField.getCursorPosition());
      }

      if (this.foundPlayerNames.size() > 1) {
         StringBuilder stringBuilder = new StringBuilder();

         for (String text4 : this.foundPlayerNames) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(", ");
            }

            stringBuilder.append(text4);
         }

         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringBuilder.toString()), 1);
      }

      this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
   }
}
