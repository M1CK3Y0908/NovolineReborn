package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;
import wtf.novoline.Novoline;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.ProxyManagerScreen;
import wtf.novoline.utils.math.SmoothingUtils;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {
   protected Minecraft mc;
   public int width;
   public List<GuiButton> buttonList = Lists.newArrayList();
   private int touchValue;
   private GuiButton selectedButton;
   private static final Logger LOGGER = LogManager.getLogger();
   public boolean allowUserInput;
   private int eventButton;
   public int height;
   protected RenderItem itemRender;
   private static final Set<String> PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   protected FontRenderer fontRendererObj;
   private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
   public List<GuiLabel> labelList = Lists.newArrayList();
   private URI clickedLinkURI;
   private long lastMouseEvent;

   public void initGui() {
   }

   public static boolean isCtrlKeyDown() {
      return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.selectedButton != null && state == 0) {
         this.selectedButton.update();
         this.selectedButton = null;
      }
   }

   public void onResize(Minecraft mcIn, int w, int h) {
      this.setWorldAndResolution(mcIn, w, h);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      for (int i = 0; i < this.buttonList.size(); i++) {
         GuiButton guiButton = this.buttonList.get(i);
         if (guiButton.id >= 0) {
            guiButton.drawButton(this.mc, mouseX, mouseY);
         }
      }

      for (int i2 = 0; i2 < this.labelList.size(); i2++) {
         this.labelList.get(i2).handleMinecraft2(this.mc);
      }

      if (this.mc.theWorld == null && this.mc.currentScreen != null && !(this instanceof AltManagerScreen) && !(this instanceof ProxyManagerScreen)) {
         SmoothingUtils.updateDelta();
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         GlStateManager.pushMatrix();
         float scaleFactor = scaledResolution.getScaleFactor();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         double d = scaledResolution.getScaledWidth_double();
         double d2 = scaledResolution.getScaledHeight_double();
         scaledResolution.handleDouble2(d / 2.0 * scaleFactor);
         scaledResolution.handleDouble(d2 / 2.0 * scaleFactor);
         Novoline.getNovoline().getNotificationManager().renderAll(scaledResolution);
         GlStateManager.popMatrix();
      }
   }

   protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
      this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
   }

   public void handleInt5(int i, int i2) throws IOException {
      this.mouseClicked(i, i2, 0);
   }

   public static boolean isAltKeyDown() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }

   public static String getClipboardString() {
      try {
         Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)transferable.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception exception) {
      }

      return "";
   }

   protected boolean handleComponentClick(IChatComponent component) {
      if (component == null) {
         return false;
      }

      ClickEvent chatClickEvent = component.getChatStyle().getChatClickEvent();
      if (isShiftKeyDown()) {
         if (component.getChatStyle().getInsertion() != null) {
            this.setText(component.getChatStyle().getInsertion(), false);
         }
      } else if (chatClickEvent != null) {
         if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.OPEN_URL) {
            if (!this.mc.gameSettings.chatLinks) {
               return false;
            }

            try {
               URI uri = new URI(chatClickEvent.getValue());
               String scheme = uri.getScheme();
               if (scheme == null) {
                  throw new URISyntaxException(chatClickEvent.getValue(), "Missing protocol");
               }

               if (!PROTOCOLS.contains(scheme.toLowerCase())) {
                  throw new URISyntaxException(chatClickEvent.getValue(), "Unsupported protocol: " + scheme.toLowerCase());
               }

               if (this.mc.gameSettings.chatLinksPrompt) {
                  this.clickedLinkURI = uri;
                  this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, chatClickEvent.getValue()));
               } else {
                  this.openWebLink(uri);
               }
            } catch (URISyntaxException uriSyntaxException) {
               LOGGER.error("Can't open url for " + chatClickEvent, uriSyntaxException);
            }
         } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.OPEN_FILE) {
            URI uri2 = new File(chatClickEvent.getValue()).toURI();
            this.openWebLink(uri2);
         } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.SUGGEST_COMMAND) {
            this.setText(chatClickEvent.getValue(), true);
         } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND) {
            this.sendChatMessage(chatClickEvent.getValue(), false);
         } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.TWITCH_USER_INFO) {
            ChatUserInfo chatUserInfo = this.mc.getIStream().getChatUserInfo(chatClickEvent.getValue());
            if (chatUserInfo != null) {
               this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getIStream(), chatUserInfo));
            } else {
               LOGGER.error("Tried to handle twitch user but couldn't find them!");
            }
         } else {
            LOGGER.error("Don't know how to handle " + chatClickEvent);
         }

         return true;
      }

      return false;
   }

   public void updateScreen() {
   }

   public void handleKeyboardInput() throws IOException {
      if (Keyboard.getEventKeyState()) {
         this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
      }

      this.mc.dispatchKeypresses();
   }

   public void startClosing() {
   }

   protected void drawHoveringText(List<String> textLines, int x, int y) {
      if (!textLines.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         int i = 0;

         for (String text : textLines) {
            int intValue = this.fontRendererObj.getInt3(text);
            if (intValue > i) {
               i = intValue;
            }
         }

         int i2 = x + 12;
         int i3 = y - 12;
         int i4 = 8;
         if (textLines.size() > 1) {
            i4 = 8 + 2 + (textLines.size() - 1) * 10;
         }

         if (i2 + i > this.width) {
            i2 -= 28 + i;
         }

         if (i3 + i4 + 6 > this.height) {
            i3 = this.height - i4 - 6;
         }

         this.zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         this.drawGradientRect(i2 - 3, i3 - 4, i2 + i + 3, i3 - 3, -267386864, -267386864);
         this.drawGradientRect(i2 - 3, i3 + i4 + 3, i2 + i + 3, i3 + i4 + 4, -267386864, -267386864);
         this.drawGradientRect(i2 - 3, i3 - 3, i2 + i + 3, i3 + i4 + 3, -267386864, -267386864);
         this.drawGradientRect(i2 - 4, i3 - 3, i2 - 3, i3 + i4 + 3, -267386864, -267386864);
         this.drawGradientRect(i2 + i + 3, i3 - 3, i2 + i + 4, i3 + i4 + 3, -267386864, -267386864);
         this.drawGradientRect(i2 - 3, i3 - 3 + 1, i2 - 3 + 1, i3 + i4 + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(i2 + i + 2, i3 - 3 + 1, i2 + i + 3, i3 + i4 + 3 - 1, 1347420415, 1344798847);
         this.drawGradientRect(i2 - 3, i3 - 3, i2 + i + 3, i3 - 3 + 1, 1347420415, 1347420415);
         this.drawGradientRect(i2 - 3, i3 + i4 + 2, i2 + i + 3, i3 + i4 + 3, 1344798847, 1344798847);

         for (int i5 = 0; i5 < textLines.size(); i5++) {
            String text2 = textLines.get(i5);
            this.fontRendererObj.drawStringWithShadow(text2, i2, i3, -1);
            if (i5 == 0) {
               i3 += 2;
            }

            i3 += 10;
         }

         this.zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   protected void setText(String newChatText, boolean shouldOverwrite) {
   }

   public static boolean isKeyComboCtrlA(int keyID) {
      return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (id == 31102009) {
         if (result) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }
   }

   public void handleInt2(int i, int i2) throws IOException {
      this.mouseReleased(i, i2, 0);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
   }

   private void openWebLink(URI url) {
      try {
         Class<?> oclass = Class.forName("java.awt.Desktop");
         Object desktopInstance = oclass.getMethod("getDesktop").invoke(null);
         oclass.getMethod("browse", URI.class).invoke(desktopInstance, url);
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't open link", throwable);
      }
   }

   public void update5() {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, this.height, 0.0).tex(0.0, this.height / 32.0F + 0).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(this.width, this.height, 0.0).tex(this.width / 32.0F, this.height / 32.0F + 0).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(this.width, 0.0, 0.0).tex(this.width / 32.0F, 0).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0).color(64, 64, 64, 255).endVertex();
      instance.draw();
   }

   protected void handleComponentHover(IChatComponent component, int x, int y) {
      if (component != null && component.getChatStyle().getChatHoverEvent() != null) {
         HoverEvent chatHoverEvent = component.getChatStyle().getChatHoverEvent();
         if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = null;

            try {
               NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
               if (tagFromJson instanceof NBTTagCompound) {
                  itemStack = ItemStack.loadItemStackFromNBT(tagFromJson);
               }
            } catch (NBTException nbtException) {
            }

            if (itemStack != null) {
               this.renderToolTip(itemStack, x, y);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
            }
         } else if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound tagFromJson2 = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
                  if (tagFromJson2 instanceof NBTTagCompound) {
                     ArrayList arrayList = Lists.newArrayList();
                     NBTTagCompound nbtTagCompound = tagFromJson2;
                     arrayList.add(nbtTagCompound.getString("name"));
                     if (nbtTagCompound.hasKey("type", 8)) {
                        String string = nbtTagCompound.getString("type");
                        arrayList.add("Type: " + string + " (" + EntityList.getIDFromString(string) + ")");
                     }

                     arrayList.add(nbtTagCompound.getString("id"));
                     this.drawHoveringText(arrayList, x, y);
                  } else {
                     this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                  }
               } catch (NBTException nbtException2) {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
               }
            }
         } else if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_TEXT) {
            this.drawHoveringText(NEWLINE_SPLITTER.splitToList(chatHoverEvent.getValue().getFormattedText()), x, y);
         } else if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_ACHIEVEMENT) {
            StatBase oneShotStat = StatList.getOneShotStat(chatHoverEvent.getValue().getUnformattedText());
            if (oneShotStat != null) {
               IChatComponent statName = oneShotStat.getStatName();
               ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (oneShotStat.isAchievement() ? "achievement" : "statistic"));
               chatComponentTranslation.getChatStyle().setItalic(true);
               String text = oneShotStat instanceof Achievement ? ((Achievement)oneShotStat).getDescription() : null;
               ArrayList arrayList2 = Lists.newArrayList(new String[]{statName.getFormattedText(), chatComponentTranslation.getFormattedText()});
               if (text != null) {
                  arrayList2.addAll(this.fontRendererObj.listFormattedStringToWidth(text, 150));
               }

               this.drawHoveringText(arrayList2, x, y);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
            }
         }

         GlStateManager.disableLighting();
      }
   }

   public static boolean isShiftKeyDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public void drawDefaultBackground() {
      this.update3();
   }

   public void sendChatMessage(String msg, boolean addToChat) {
      if (addToChat) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
      }

      this.mc.thePlayer.sendChatMessage(msg);
   }

   public void sendChatMessage(String msg) {
      this.sendChatMessage(msg, true);
   }

   public static boolean isKeyComboCtrlX(int keyID) {
      return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   protected void renderToolTip(ItemStack stack, int x, int y) {
      List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

      for (int index = 0; index < list.size(); index++) {
         if (index == 0) {
            list.set(index, stack.getRarity().rarityColor + list.get(index));
         } else {
            list.set(index, EnumChatFormatting.GRAY + list.get(index));
         }
      }

      this.drawHoveringText(list, x, y);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         int size2 = this.buttonList.size();

         for (int i = 0; i < Math.min(size2, this.buttonList.size()); i++) {
            GuiButton guiButton = this.buttonList.get(i);
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               this.selectedButton = guiButton;
               guiButton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guiButton);
            }
         }
      }
   }

   protected void handleInt7(int i, int i2, int i3) {
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.mc = mc;
      this.itemRender = mc.getRenderItem();
      this.fontRendererObj = mc.fontRendererObj;
      this.width = width;
      this.height = height;
      this.buttonList.clear();
      this.initGui();
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   public static boolean isKeyComboCtrlV(int keyID) {
      return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public void handleInput() throws IOException {
      if (Mouse.isCreated()) {
         while (Mouse.next()) {
            this.handleMouseInput();
         }
      }

      if (Keyboard.isCreated()) {
         while (Keyboard.next()) {
            this.handleKeyboardInput();
         }
      }
   }

   public void update3() {
      if (this.mc.theWorld != null) {
         this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.update5();
      }
   }

   public static boolean isKeyComboCtrlC(int keyID) {
      return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static void setClipboardString(String copyText) {
      if (!StringUtils.isEmpty(copyText)) {
         try {
            StringSelection stringSelection = new StringSelection(copyText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, (ClipboardOwner)null);
         } catch (Exception exception) {
         }
      }
   }

   public void handleMouseInput() throws IOException {
      int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int i2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int eventButton2 = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
            return;
         }

         this.eventButton = eventButton2;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(i, i2, this.eventButton);
      } else if (eventButton2 != -1) {
         if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseReleased(i, i2, eventButton2);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long l = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.handleInt7(i, i2, this.eventButton);
      }
   }
}
