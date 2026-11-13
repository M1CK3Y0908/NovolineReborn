package net.minecraft.realms;

import java.util.List;
import net.minecraft.client.gui.CommandSuggestions;
import net.minecraft.util.IChatComponent;
import wtf.novoline.utils.MinecraftUtils;

public class DisconnectedRealmsScreen extends CommandSuggestions {
   private final CommandSuggestions commandSuggestions;
   private String text;
   private int intValue13;
   private List<String> list;
   private IChatComponent iChatComponent;

   public DisconnectedRealmsScreen(CommandSuggestions commandSuggestions2, String text2, IChatComponent iChatComponent2) {
      this.commandSuggestions = commandSuggestions2;
      this.text = createString(text2);
      this.iChatComponent = iChatComponent2;
   }

   @Override
   public void noop9() {
      MinecraftUtils.refreshLanguage();
      this.update();
      this.list = this.getList(this.iChatComponent.getFormattedText(), this.getInt2() - 50);
      this.intValue13 = this.list.size() * this.visibleLines();
      this.handleRealmsButton(
         createRealmsButton(this.getInt2() / 2 - 100, this.getInt3() / 2 + this.intValue13 / 2 + this.visibleLines(), createString("gui.back"))
      );
   }

   @Override
   public void noop(RealmsButton realmsButton) {
      if (realmsButton.getInt2() == 0) {
         MinecraftUtils.displayScreen(this.commandSuggestions);
      }
   }

   @Override
   public void noop12(int i) {
      if (i == 1) {
         MinecraftUtils.displayScreen(this.commandSuggestions);
      }
   }

   @Override
   public void handleInt(int i, int i2, float f) {
      this.update2();
      this.handleString(this.text, this.getInt2() / 2, this.getInt3() / 2 - this.intValue13 / 2 - this.visibleLines() * 2, 11184810);
      int i3 = this.getInt3() / 2 - this.intValue13 / 2;
      if (this.list != null) {
         for (String text2 : this.list) {
            this.handleString(text2, this.getInt2() / 2, i3, 16777215);
            i3 += this.visibleLines();
         }
      }

      super.handleInt(i, i2, f);
   }
}
