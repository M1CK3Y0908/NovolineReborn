package net.optifine.gui;

import com.mojang.authlib.exceptions.InvalidCredentialsException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;

public class GuiScreenCapeOF extends GuiScreenOF {
   private String linkUrl;
   private FontRenderer fontRenderer = Config.getMinecraft().fontRendererObj;
   private long messageHideTimeMs;
   private final GuiScreen parentScreen;
   private String title;
   private String message;
   private GuiButtonOF buttonCopyLink;

   public void setLinkUrl(String linkUrl) {
      this.linkUrl = linkUrl;
      this.buttonCopyLink.visible = linkUrl != null;
   }

   public GuiScreenCapeOF(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   private void showMessage(String msg, long timeMs) {
      this.message = msg;
      this.messageHideTimeMs = System.currentTimeMillis() + timeMs;
      this.setLinkUrl((String)null);
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
         }

         if (button.id == 210) {
            try {
               String name = this.mc.getSession().getProfile().getName();
               String text = this.mc.getSession().getProfile().getId().toString().replace("-", "");
               String token = this.mc.getSession().getToken();
               Random random = new Random();
               Random random2 = new Random(System.identityHashCode(new Object()));
               BigInteger bigInteger = new BigInteger(128, random);
               BigInteger bigInteger2 = new BigInteger(128, random2);
               String text2 = bigInteger.xor(bigInteger2).toString(16);
               this.mc.getSessionService().joinServer(this.mc.getSession().getProfile(), token, text2);
               String text3 = "https://optifine.net/capeChange?u=" + text + "&n=" + name + "&s=" + text2;
               if (Config.openWebLink(new URI(text3))) {
                  this.showMessage(Lang.get("of.message.capeOF.openEditor"), 10000L);
               } else {
                  this.showMessage(Lang.get("of.message.capeOF.openEditorError"), 10000L);
                  this.setLinkUrl(text3);
               }
            } catch (InvalidCredentialsException invalidCredentialsException) {
               Config.showGuiMessage(I18n.format("of.message.capeOF.error1"), I18n.format("of.message.capeOF.error2", invalidCredentialsException.getMessage()));
               Config.warn("Mojang authentication failed");
               Config.warn(invalidCredentialsException.getClass().getName() + ": " + invalidCredentialsException.getMessage());
            } catch (Exception exception) {
               Config.warn("Error opening OptiFine cape link");
               Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
            }
         }

         if (button.id == 220) {
            this.showMessage(Lang.get("of.message.capeOF.reloadCape"), 15000L);
            if (this.mc.thePlayer != null) {
               long l = System.currentTimeMillis() + 15000L;
               this.mc.thePlayer.setReloadCapeTimeMs(l);
            }
         }

         if (button.id == 230 && this.linkUrl != null) {
            setClipboardString(this.linkUrl);
         }
      }
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.capeOF.title");
      this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 155, this.height / 6 + 24, 150, 20, I18n.format("of.options.capeOF.openEditor")));
      this.buttonList.add(new GuiButtonOF(220, this.width / 2 - 155 + 160, this.height / 6 + 24, 150, 20, I18n.format("of.options.capeOF.reloadCape")));
      this.buttonCopyLink = new GuiButtonOF(230, this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("of.options.capeOF.copyEditorLink"));
      this.buttonCopyLink.visible = this.linkUrl != null;
      this.buttonList.add(this.buttonCopyLink);
      this.buttonList.add(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 144, I18n.format("gui.done")));
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      if (this.message != null) {
         this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height / 6 + 60, 16777215);
         if (System.currentTimeMillis() > this.messageHideTimeMs) {
            this.message = null;
            this.setLinkUrl((String)null);
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
