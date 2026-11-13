package wtf.novoline.gui.overlay;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.RandomStringUtils;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.AccountType;
import wtf.novoline.gui.NovolineButton;
import wtf.novoline.gui.TextSelection;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.RenderUtils;

public abstract class AccountCredentialsOverlay extends Overlay {
   protected TextSelection textSelection;
   protected TextSelection textSelection2;
   protected final BlurRenderer blurRenderer2 = new BlurRenderer(Novoline.framebufferManager);
   protected final AccountManager accountManager = Novoline.getNovoline().getAccountManager();
   private final String text;
   private final String text2;

   @Override
   public void mouseClicked(int i, int i2, int i3) {
      this.textSelection.updateFocus(i, i2);
      this.textSelection2.updateFocus(i, i2);
      super.mouseClicked(i, i2, i3);
   }

   public AccountType getAccountType(String text3, String text4) {
      if (text4 == null || text4.isEmpty()) {
         return AccountType.CRACKED;
      } else {
         return text3.endsWith("@alt.com") ? AccountType.ALTENING : AccountType.MOJANG;
      }
   }

   public AccountCredentialsOverlay(GuiScreen guiScreen2, String text3, String text4) {
      super(guiScreen2);
      this.text = text3;
      this.text2 = text4;
   }

   @Override
   public void actionPerformed(GuiButton button) {
      switch (button.id) {
         case -3:
            int i = ThreadLocalRandom.current().nextInt(10, 16);
            this.textSelection.getSelection().collapse();
            this.textSelection.getTextBuffer().setText(RandomStringUtils.randomAlphanumeric(i));
            break;
         case -2:
            try {
               Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
               String text3 = (String)systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
               if (text3.contains(":")) {
                  String[] texts = text3.split(":");
                  this.textSelection.getSelection().collapse();
                  this.textSelection.getTextBuffer().setText(texts[0]);
                  this.textSelection2.getSelection().collapse();
                  this.textSelection2.getTextBuffer().setText(texts[1]);
               } else {
                  this.showNotification(
                     NotificationType.ERROR,
                     Language.translate("account_manager.login_handling.notification.title"),
                     Language.translate("account_manager.notification.login.import_error.body"),
                     5000
                  );
               }
            } catch (IOException | UnsupportedFlavorException exception) {
               this.showNotification(
                  NotificationType.ERROR,
                  Language.translate("account_manager.login_handling.notification.title"),
                  Language.translate("account_manager.notification.login.import_error.body"),
                  5000
               );
            }
            break;
         case -1:
            this.firstButtonClicked();
      }
   }

   public abstract void firstButtonClicked();

   @Override
   public void keyTyped(char c, int i) {
      if (i == 47 && GuiScreen.isCtrlKeyDown() && !this.textSelection.isFocused() && !this.textSelection2.isFocused()) {
         this.guiScreen.buttonList.stream().filter(guiButton -> guiButton.id == -2).findFirst().ifPresent(this::actionPerformed);
      } else {
         this.textSelection.keyTyped(c, i);
         this.textSelection2.keyTyped(c, i);
      }
   }

   @Override
   public void init() {
      this.guiScreen
         .buttonList
         .add(new NovolineButton(this.blurRenderer2, -1, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 12, 200, 20, this.text2));
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.blurRenderer2,
               -2,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 35,
               200,
               20,
               Language.translate("account_manager.gui.account_action.import")
            )
         );
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.blurRenderer2,
               -3,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 58,
               200,
               20,
               Language.translate("account_manager.gui.account_action.random_cracked")
            )
         );
      this.textSelection = new TextSelection(Fonts.rubikRegularSize16, this.blurRenderer2, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 - 50);
      this.textSelection2 = new TextSelection(Fonts.rubikRegularSize16, this.blurRenderer2, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 - 15)
         .withMask("*", (textSelection3) -> !((TextSelection) textSelection3).isFocused());
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
      this.blurRenderer2.blur("am-acc-overlay", this.minecraft.getFramebuffer(), scaledResolution, 10, 0.6F, 1);
      this.minecraft.getFramebuffer().bindFramebuffer(true);
      this.blurRenderer2.drawResult(scaledResolution);
      Fonts.rubikSemiboldSize24
         .drawStringWithShadow(
            this.text, this.guiScreen.width / 2.0F - Fonts.rubikSemiboldSize24.getStringWidth(this.text) / 2.0F, this.guiScreen.height / 2.0F - 85.0F, -1
         );
      int i = this.guiScreen.width / 2 - 100;
      Fonts.rubikRegularSize14.drawString(Language.translate("account_manager.gui.account_action.email"), i, this.textSelection.getY() - 7, -1);
      this.textSelection.draw(partialTicks);
      Fonts.rubikRegularSize14.drawString(Language.translate("account_manager.gui.account_action.password"), i, this.textSelection2.getY() - 7, -1);
      this.textSelection2.draw(partialTicks);
      RenderUtils.setColor(-1);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.blurRenderer2.releaseResult();
   }
}
