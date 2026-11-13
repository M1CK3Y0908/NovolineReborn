package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestions;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends CommandSuggestions {
   private static final Logger logger = LogManager.getLogger();
   private GuiScreen guiScreen;

   public void handleGuiScreen(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;

      try {
         Class clazz = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor declaredConstructor = clazz.getDeclaredConstructor(CommandSuggestions.class);
         declaredConstructor.setAccessible(true);
         Object object = declaredConstructor.newInstance(this);
         Minecraft.getMinecraft().displayGuiScreen(((CommandSuggestions)object).getGuiScreenRealmsProxy());
      } catch (Exception exception) {
         logger.error("Realms module missing", exception);
      }
   }

   @Override
   public void noop9() {
      Minecraft.getMinecraft().displayGuiScreen(this.guiScreen);
   }
}
