package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
   private final GuiMultiplayer owner;
   private static final Logger logger = LogManager.getLogger();
   private static final ThreadPoolExecutor serverPingerThreads = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );
   private String lastIconBase64;
   private final ServerData server;
   private long lastClickTime;
   private final Minecraft mc;
   private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
   private final ResourceLocation serverIcon;
   private DynamicTexture serverIconTexture;
   private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

   protected void drawTextureAt(int i, int i2, ResourceLocation resourceLocation) {
      this.mc.getTextureManager().bindTexture(resourceLocation);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(i, i2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int i2, int i3, int i4, int i5) {
      if (i4 <= 32) {
         if (i4 < 32 && i4 > 16 && this.canJoinServer()) {
            this.owner.selectServer(slotIndex);
            this.owner.connectToSelected();
            return true;
         }

         if (i4 < 16 && i5 < 16 && this.owner.canMoveServerUp(this, slotIndex)) {
            this.owner.moveServerUp(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }

         if (i4 < 16 && i5 > 16 && this.owner.canMoveServerDown(this, slotIndex)) {
            this.owner.moveServerDown(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }
      }

      this.owner.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
         this.owner.connectToSelected();
      }

      this.lastClickTime = Minecraft.getSystemTime();
      return false;
   }

   private boolean canJoinServer() {
      return true;
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      if (!this.server.pinged) {
         this.server.pinged = true;
         this.server.pingToServer = -2L;
         this.server.serverMOTD = "";
         this.server.populationInfo = "";
         serverPingerThreads.submit(new Runnable() {
            @Override
            public void run() {
               try {
                  ServerListEntryNormal.this.owner.getOldServerPinger().ping(ServerListEntryNormal.this.server);
               } catch (UnknownHostException unknownHostException) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
               } catch (Exception exception) {
                  ServerListEntryNormal.this.server.pingToServer = -1L;
                  ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
               }
            }
         });
      }

      boolean flag = this.server.version > 47;
      boolean flag2 = this.server.version < 47;
      boolean flag3 = flag || flag2;
      this.mc.fontRendererObj.getInt(this.server.serverName, x + 32 + 3, y + 1, 16777215);
      List list = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, listWidth - 32 - 2);

      for (int i = 0; i < Math.min(list.size(), 2); i++) {
         this.mc.fontRendererObj.getInt((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
      }

      String text = flag3 ? EnumChatFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
      int intValue = this.mc.fontRendererObj.getInt3(text);
      this.mc.fontRendererObj.getInt(text, x + listWidth - intValue - 15 - 2, y + 1, 8421504);
      byte b = 0;
      String text2 = null;
      int i2;
      String text3;
      if (flag3) {
         i2 = 5;
         text3 = flag ? "Client out of date!" : "Server out of date!";
         text2 = this.server.playerList;
      } else if (this.server.pinged && this.server.pingToServer != -2L) {
         if (this.server.pingToServer < 0L) {
            i2 = 5;
         } else if (this.server.pingToServer < 150L) {
            i2 = 0;
         } else if (this.server.pingToServer < 300L) {
            i2 = 1;
         } else if (this.server.pingToServer < 600L) {
            i2 = 2;
         } else if (this.server.pingToServer < 1000L) {
            i2 = 3;
         } else {
            i2 = 4;
         }

         if (this.server.pingToServer < 0L) {
            text3 = "(no connection)";
         } else {
            text3 = this.server.pingToServer + "ms";
            text2 = this.server.playerList;
         }
      } else {
         b = 1;
         i2 = (int)(Minecraft.getSystemTime() / 100L + slotIndex * 2 & 7L);
         if (i2 > 4) {
            i2 = 8 - i2;
         }

         text3 = "Pinging...";
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Gui.icons);
      Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, b * 10, 176 + i2 * 8, 10, 8, 256.0F, 256.0F);
      if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.lastIconBase64)) {
         this.lastIconBase64 = this.server.getBase64EncodedIconData();
         this.prepareServerIcon();
         this.owner.getServerList().saveServerList();
      }

      if (this.serverIconTexture != null) {
         this.drawTextureAt(x, y, this.serverIcon);
      } else {
         this.drawTextureAt(x, y, UNKNOWN_SERVER);
      }

      int i3 = mouseX - x;
      int i4 = mouseY - y;
      if (i3 >= listWidth - 15 && i3 <= listWidth - 5 && i4 >= 0 && i4 <= 8) {
         this.owner.setHoveringText(text3);
      } else if (i3 >= listWidth - intValue - 15 - 2 && i3 <= listWidth - 15 - 2 && i4 >= 0 && i4 <= 8) {
         this.owner.setHoveringText(text2);
      }

      if (this.mc.gameSettings.touchscreen || isSelected) {
         this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
         Gui.handleInt(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int i5 = mouseX - x;
         int i6 = mouseY - y;
         if (this.canJoinServer()) {
            if (i5 < 32 && i5 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canMoveServerUp(this, slotIndex)) {
            if (i5 < 16 && i6 < 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canMoveServerDown(this, slotIndex)) {
            if (i5 < 16 && i6 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   @Override
   public void noop(int i) {
   }

   protected ServerListEntryNormal(GuiMultiplayer guiMultiplayer, ServerData serverIn) {
      this.owner = guiMultiplayer;
      this.server = serverIn;
      this.mc = Minecraft.getMinecraft();
      this.serverIcon = new ResourceLocation("servers/" + serverIn.serverIP + "/icon");
      this.serverIconTexture = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
   }

   private void prepareServerIcon() {
      if (this.server.getBase64EncodedIconData() == null) {
         this.mc.getTextureManager().deleteTexture(this.serverIcon);
         this.serverIconTexture = null;
      } else {
         ByteBuf byteBuf = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
         ByteBuf byteBuf2 = Base64.decode(byteBuf);

         BufferedImage bufferedImage;
         label63: {
            try {
               bufferedImage = TextureUtil.readBufferedImage(new ByteBufInputStream(byteBuf2));
               Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
               break label63;
            } catch (Throwable throwable) {
               logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", throwable);
               this.server.setBase64EncodedIconData((String)null);
            } finally {
               byteBuf.release();
               byteBuf2.release();
            }

            return;
         }

         if (this.serverIconTexture == null) {
            this.serverIconTexture = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
            this.mc.getTextureManager().loadTexture(this.serverIcon, this.serverIconTexture);
         }

         bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.serverIconTexture.getTextureData(), 0, bufferedImage.getWidth());
         this.serverIconTexture.updateDynamicTexture();
      }
   }

   public ServerData getServerData() {
      return this.server;
   }

   @Override
   public void update() {
   }
}
