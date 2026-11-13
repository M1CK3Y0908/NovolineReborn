package net.minecraft.client.gui;

import com.thealtening.api.TheAltening;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomStringUtils;
import wtf.novoline.Novoline;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.gui.AccountListPanel;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.ProxyManagerScreen;
import wtf.novoline.lang.AuthException;
import wtf.novoline.temporal.DurationTemporal;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.account.AlteningAccount;
import wtf.novoline.utils.account.CrackedAccount;
import wtf.novoline.utils.account.LoginHandler;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.proxy.ProxyEntry;
import wtf.novoline.utils.proxy.ProxyList;

public class GuiDisconnected extends GuiScreen {
   private List<String> multilineMessage;
   private final Random random = new Random();
   private final GuiScreen parentScreen;
   private String reason;
   private int textHeight;
   private IChatComponent message;
   private final String text;
   private final ServerData serverData;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
      this.text = this.getString();
      this.serverData = Novoline.getNovoline().getServerData();
      Novoline.getNovoline().getBus().post(new ServerDisconnectEvent(this.message));
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      Novoline novoline2 = Novoline.getNovoline();
      AccountManager accountManager = novoline2.getAccountManager();
      switch (guiButton.id) {
         case 0:
            this.mc.displayGuiScreen(this.parentScreen);
            break;
         case 1:
            this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, this.serverData));
            break;
         case 2:
            if (accountManager.getAccount() != null) {
               accountManager.remove(accountManager.getAccount());
               accountManager.setAccount(null);
               novoline2.getNotificationManager().show(new Notification(NotificationType.SUCCESS, "Account Manager", "Account removed!", 3000L));
            } else {
               novoline2.getNotificationManager().show(new Notification(NotificationType.ERROR, "Account Manager", "You're not logged in to an account!", 3000L));
            }
            break;
         case 3:
            AccountListPanel accountListPanel = novoline2.getAltManagerScreen().getAccountListPanel();
            if (accountListPanel.isEmpty()) {
               novoline2.getNotificationManager().show(new Notification(NotificationType.ERROR, "Account Manager", "Please add an account!", 3000L));
            } else {
               int i = this.random.nextInt(accountListPanel.size());
               accountListPanel.get(i).onDoubleClick();
            }
            break;
         case 4:
            if (accountManager.getAlteningToken() == null) {
               novoline2.getNotificationManager().show(new Notification(NotificationType.ERROR, "Account Manager", "No TheAltening token is present", 5000L));
               return;
            }

            new Thread(
                  () -> TheAltening.newAsyncRetriever(accountManager.getAlteningToken())
                     .getAccountDataAsync()
                     .exceptionally(
                        throwable -> {
                           novoline2.getNotificationManager()
                              .show(new Notification(NotificationType.ERROR, "Account Manager", "Couldn't generate the account. Invalid token?", 5000L));
                           return null;
                        }
                     )
                     .thenAccept(account2 -> {
                        AlteningAccount alteningAccount = new AlteningAccount(account2.getToken());

                        try {
                           LoginHandler.onLoggedIn(alteningAccount.login());
                        } catch (AuthException authException) {
                           LoginHandler.reportLoginFailure(authException);
                        }
                     }),
                  "Altening-Generate-Thread"
               )
               .start();
            break;
         case 5:
            String text2 = RandomStringUtils.randomAlphanumeric(ThreadLocalRandom.current().nextInt(10, 16));

            try {
               Session session2 = new CrackedAccount(text2).login();
               LoginHandler.onLoggedIn(session2);
            } catch (AuthException authException2) {
            }
            break;
         case 6:
            this.mc.displayGuiScreen(new AltManagerScreen(this));
            break;
         case 7:
            this.mc.displayGuiScreen(new ProxyManagerScreen(this));
      }
   }

   private String getString() {
      if (HypixelState.getSessionStart() != null) {
         Duration duration = Duration.between(HypixelState.getSessionStart(), Instant.now());
         return new DurationTemporal(duration).toString();
      } else {
         return null;
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(
         this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2 - 30, 11184810
      );
      int i = this.height / 2 - this.textHeight / 2 - 30;
      if (this.multilineMessage != null) {
         for (String text2 : this.multilineMessage) {
            this.drawCenteredString(this.fontRendererObj, text2, this.width / 2, i, 16777215);
            i += this.fontRendererObj.FONT_HEIGHT;
         }
      }

      i += 105;
      if (this.serverData != null) {
         this.drawCenteredString(
            this.fontRendererObj,
            EnumChatFormatting.GRAY
               + "Username: "
               + EnumChatFormatting.RESET
               + this.mc.session.getUsername()
               + EnumChatFormatting.DARK_GRAY
               + " | "
               + EnumChatFormatting.GRAY
               + "Playing on "
               + EnumChatFormatting.RESET
               + this.serverData.serverIP,
            this.width / 2,
            i,
            16777215
         );
         i += 15;
      }

      if (this.text != null && !this.text.isEmpty()) {
         this.drawCenteredString(
            this.fontRendererObj, EnumChatFormatting.GRAY + "Play Time: " + EnumChatFormatting.RESET + this.text, this.width / 2, i, 16777215
         );
         i += 15;
      }

      ProxyList proxyList = Novoline.getNovoline().getProxyList();
      if (proxyList.isEnabled() && proxyList.getCurrent() != null) {
         ProxyEntry current2 = proxyList.getCurrent();
         this.drawCenteredString(
            this.fontRendererObj,
            EnumChatFormatting.GRAY + "Proxy: " + EnumChatFormatting.RESET + current2.getAddress() + ":" + current2.getPort(),
            this.width / 2,
            i,
            16777215
         );
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
      int i = this.height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT - 30;
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i, I18n.format("gui.toMenu")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, i + 22, 99, 20, "Reconnect"));
      this.buttonList.add(new GuiButton(2, this.width / 2 - 100, i + 44, 99, 20, "Remove Alt"));
      this.buttonList.add(new GuiButton(5, this.width / 2 + 1, i + 22, 99, 20, "Random Cracked"));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 1, i + 44, 99, 20, "Random Alt"));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 66, 200, 20, "Generate and Reconnect"));
      this.buttonList.add(new GuiButton(6, 5, 5, 120, 20, "Account Manager"));
      this.buttonList.add(new GuiButton(7, 5, 27, 120, 20, "Proxy Manager"));
   }
}
