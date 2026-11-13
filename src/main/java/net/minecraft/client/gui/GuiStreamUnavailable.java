package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Util;
import net.minecraft.util.Session.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import tv.twitch.ErrorCode;

public class GuiStreamUnavailable extends GuiScreen {
   private final IChatComponent iChatComponent = new ChatComponentTranslation("stream.unavailable.title");
   private final List<String> list = Lists.newArrayList();
   private final GuiScreen guiScreen;
   private final GuiStreamUnavailable.NoFboType noFboType;
   private final List<ChatComponentTranslation> list2;
   private static final Logger logger = LogManager.getLogger();

   @Override
   public void startClosing() {
   }

   private void handleString(String text) {
      try {
         Class clazz = Class.forName("java.awt.Desktop");
         Object object = clazz.getMethod("getDesktop").invoke(null);
         clazz.getMethod("browse", URI.class).invoke(object, new URI(text));
      } catch (Throwable throwable) {
         logger.error("Couldn't open link", throwable);
      }
   }

   public GuiStreamUnavailable(GuiScreen guiScreen2, GuiStreamUnavailable.NoFboType noFboType2, List list3) {
      this.guiScreen = guiScreen2;
      this.noFboType = noFboType2;
      this.list2 = list3;
   }

   @Override
   public void initGui() {
      if (this.list.isEmpty()) {
         this.list.addAll(this.fontRendererObj.listFormattedStringToWidth(this.noFboType.getIChatComponent().getFormattedText(), (int)(this.width * 0.75F)));
         if (this.list2 != null) {
            this.list.add("");

            for (ChatComponentTranslation chatComponentTranslation : this.list2) {
               this.list.add(chatComponentTranslation.getUnformattedTextForChat());
            }
         }
      }

      if (this.noFboType.getIChatComponent2() != null) {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 50, 150, 20, I18n.format("gui.cancel")));
         this.buttonList
            .add(new GuiButton(1, this.width / 2 - 155 + 160, this.height - 50, 150, 20, I18n.format(this.noFboType.getIChatComponent2().getFormattedText())));
      } else {
         this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height - 50, 150, 20, I18n.format("gui.cancel")));
      }
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      if (guiButton.enabled) {
         if (guiButton.id == 1) {
            switch (this.noFboType) {
               case ACCOUNT_NOT_BOUND:
               case FAILED_TWITCH_AUTH:
                  this.handleString("https://account.mojang.com/me/settings");
                  break;
               case ACCOUNT_NOT_MIGRATED:
                  this.handleString("https://account.mojang.com/migrate");
                  break;
               case UNSUPPORTED_OS_MAC:
                  this.handleString("http://www.apple.com/osx/");
                  break;
               case UNKNOWN:
               case LIBRARY_FAILURE:
               case INITIALIZATION_FAILURE:
                  this.handleString("http://bugs.mojang.com/browse/MC");
            }
         }

         this.mc.displayGuiScreen(this.guiScreen);
      }
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.drawDefaultBackground();
      int i3 = Math.max((int)(this.height * 0.85 / 2.0 - this.list.size() * this.fontRendererObj.FONT_HEIGHT / 2.0F), 50);
      this.drawCenteredString(
         this.fontRendererObj, this.iChatComponent.getFormattedText(), this.width / 2, i3 - this.fontRendererObj.FONT_HEIGHT * 2, 16777215
      );

      for (String text : this.list) {
         this.drawCenteredString(this.fontRendererObj, text, this.width / 2, i3, 10526880);
         i3 += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(i, i2, f);
   }

   public GuiStreamUnavailable(GuiScreen guiScreen2, GuiStreamUnavailable.NoFboType noFboType2) {
      this(guiScreen2, noFboType2, (List)null);
   }

   public static void handleGuiScreen(GuiScreen guiScreen2) {
      Minecraft minecraft2 = Minecraft.getMinecraft();
      IStream iStream = minecraft2.getIStream();
      if (!OpenGlHelper.framebufferSupported) {
         ArrayList arrayList = Lists.newArrayList();
         arrayList.add(new ChatComponentTranslation("stream.unavailable.no_fbo.version", GL11.glGetString(7938)));
         arrayList.add(new ChatComponentTranslation("stream.unavailable.no_fbo.blend", GLContext.getCapabilities().GL_EXT_blend_func_separate));
         arrayList.add(new ChatComponentTranslation("stream.unavailable.no_fbo.arb", GLContext.getCapabilities().GL_ARB_framebuffer_object));
         arrayList.add(new ChatComponentTranslation("stream.unavailable.no_fbo.ext", GLContext.getCapabilities().GL_EXT_framebuffer_object));
         minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.NO_FBO, arrayList));
      } else if (iStream instanceof NullStream) {
         if (((NullStream)iStream).getThrowable().getMessage().contains("Can't load AMD 64-bit .dll on a IA 32-bit platform")) {
            minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.LIBRARY_ARCH_MISMATCH));
         } else {
            minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.LIBRARY_FAILURE));
         }
      } else if (!iStream.isActive6() && iStream.getErrorCode() == ErrorCode.TTV_EC_OS_TOO_OLD) {
         switch (Util.getOSType()) {
            case WINDOWS:
               minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.UNSUPPORTED_OS_WINDOWS));
               break;
            case OSX:
               minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.UNSUPPORTED_OS_MAC));
               break;
            default:
               minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.UNSUPPORTED_OS_OTHER));
         }
      } else if (!minecraft2.getPropertyMap().containsKey("twitch_access_token")) {
         if (minecraft2.getSession().getSessionType() == Type.LEGACY) {
            minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.ACCOUNT_NOT_MIGRATED));
         } else {
            minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.ACCOUNT_NOT_BOUND));
         }
      } else if (!iStream.isActive3()) {
         switch (iStream.getErrorType()) {
            case INVALID_TOKEN:
               minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.FAILED_TWITCH_AUTH));
               break;
            case ERROR:
            default:
               minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.FAILED_TWITCH_AUTH_ERROR));
         }
      } else if (iStream.getErrorCode() != null) {
         List list3 = Arrays.asList(new ChatComponentTranslation("stream.unavailable.initialization_failure.extra", ErrorCode.getString(iStream.getErrorCode())));
         minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.INITIALIZATION_FAILURE, list3));
      } else {
         minecraft2.displayGuiScreen(new GuiStreamUnavailable(guiScreen2, GuiStreamUnavailable.NoFboType.UNKNOWN));
      }
   }

   public enum NoFboType {
      NO_FBO(new ChatComponentTranslation("stream.unavailable.no_fbo")),
      LIBRARY_ARCH_MISMATCH(new ChatComponentTranslation("stream.unavailable.library_arch_mismatch")),
      LIBRARY_FAILURE(new ChatComponentTranslation("stream.unavailable.library_failure"), new ChatComponentTranslation("stream.unavailable.report_to_mojang")),
      UNSUPPORTED_OS_WINDOWS(new ChatComponentTranslation("stream.unavailable.not_supported.windows")),
      UNSUPPORTED_OS_MAC(
         new ChatComponentTranslation("stream.unavailable.not_supported.mac"), new ChatComponentTranslation("stream.unavailable.not_supported.mac.okay")
      ),
      UNSUPPORTED_OS_OTHER(new ChatComponentTranslation("stream.unavailable.not_supported.other")),
      ACCOUNT_NOT_MIGRATED(
         new ChatComponentTranslation("stream.unavailable.account_not_migrated"), new ChatComponentTranslation("stream.unavailable.account_not_migrated.okay")
      ),
      ACCOUNT_NOT_BOUND(
         new ChatComponentTranslation("stream.unavailable.account_not_bound"), new ChatComponentTranslation("stream.unavailable.account_not_bound.okay")
      ),
      FAILED_TWITCH_AUTH(new ChatComponentTranslation("stream.unavailable.failed_auth"), new ChatComponentTranslation("stream.unavailable.failed_auth.okay")),
      FAILED_TWITCH_AUTH_ERROR(new ChatComponentTranslation("stream.unavailable.failed_auth_error")),
      INITIALIZATION_FAILURE(
         new ChatComponentTranslation("stream.unavailable.initialization_failure"), new ChatComponentTranslation("stream.unavailable.report_to_mojang")
      ),
      UNKNOWN(new ChatComponentTranslation("stream.unavailable.unknown"), new ChatComponentTranslation("stream.unavailable.report_to_mojang"));

      private final IChatComponent iChatComponent;
      private final IChatComponent iChatComponent2;

      public IChatComponent getIChatComponent() {
         return this.iChatComponent;
      }

      NoFboType(IChatComponent iChatComponent3) {
         this(iChatComponent3, (IChatComponent)null);
      }

      NoFboType(IChatComponent iChatComponent3, IChatComponent iChatComponent4) {
         this.iChatComponent = iChatComponent3;
         this.iChatComponent2 = iChatComponent4;
      }

      public IChatComponent getIChatComponent2() {
         return this.iChatComponent2;
      }
   }
}
