package wtf.novoline.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClipCommand extends Command {
   @Override
   public void execute(String[] texts) {
      if (texts.length == 2) {
         try {
            double d = Double.parseDouble(texts[1]);
            EntityPlayerSP thePlayer2 = Minecraft.getMinecraft().thePlayer;
            if (texts[0].equalsIgnoreCase("vclip")) {
               thePlayer2.setPosition(thePlayer2.posX, thePlayer2.posY + d, thePlayer2.posZ);
               return;
            }

            if (texts[0].equalsIgnoreCase("hclip")) {
               double d2 = Math.cos(Math.toRadians(thePlayer2.rotationYaw + 90.0F)) * d;
               double d3 = Math.sin(Math.toRadians(thePlayer2.rotationYaw + 90.0F)) * d;
               thePlayer2.setPosition(thePlayer2.posX + d2, thePlayer2.posY, thePlayer2.posZ + d3);
               return;
            }
         } catch (Exception exception) {
            this.sendMessage("§cInvalid value!");
            return;
         }
      } else if (texts.length == 4 && texts[0].equalsIgnoreCase("clip")) {
         try {
            EntityPlayerSP thePlayer3 = Minecraft.getMinecraft().thePlayer;
            double d4 = texts[1].equals("~")
               ? thePlayer3.posX
               : (texts[1].startsWith("~") ? thePlayer3.posX + Double.parseDouble(texts[1].substring(1)) : Double.parseDouble(texts[1]));
            double d5 = texts[2].equals("~")
               ? thePlayer3.posY
               : (texts[2].startsWith("~") ? thePlayer3.posY + Double.parseDouble(texts[2].substring(1)) : Double.parseDouble(texts[2]));
            double d6 = texts[3].equals("~")
               ? thePlayer3.posZ
               : (texts[3].startsWith("~") ? thePlayer3.posZ + Double.parseDouble(texts[3].substring(1)) : Double.parseDouble(texts[3]));
            thePlayer3.setPosition(d4, d5, d6);
            return;
         } catch (Exception exception2) {
            this.sendMessage("§cInvalid value!");
            return;
         }
      }

      this.sendLocalized("command.wrong_arguments");
      this.sendMessage("§c.vclip <value>");
      this.sendMessage("§c.hclip <value>");
      this.sendMessage("§c.clip <x> <y> <z>");
      this.sendMessage("§c.clip <~addX> <~addY> <~addZ>");
   }

   public ClipCommand() {
      super("Teleports you to N number of blocks", "clip", "vclip", "hclip");
   }
}
