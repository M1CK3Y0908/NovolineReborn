package wtf.novoline.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;
import wtf.novoline.enums.RelationType;

public class FriendCommand extends Command {
   private final Minecraft minecraft = Minecraft.getMinecraft();

   @Override
   public void execute(String[] texts) {
      if (!texts[0].startsWith("rel") && texts.length > 1) {
         String text = texts[0].toLowerCase(Locale.ROOT);

         RelationType relationType = switch (text) {
            case "f", "friend" -> RelationType.FRIEND;
            case "tar", "target" -> RelationType.TARGET;
            default -> throw new IllegalArgumentException("Unexpected value: " + text);
         };
         String text2 = String.join(" ", Arrays.copyOfRange(texts, 1, texts.length));
         if ((texts.length != 3 || !texts[1].equals("add")) && !texts[1].equals("list") && !texts[1].equals("clear")) {
            this.minecraft.thePlayer.sendChatMessage(".rel " + text2);
         } else {
            this.minecraft.thePlayer.sendChatMessage(".rel " + text2 + " " + relationType.name());
         }
      } else if (texts.length == 4 && texts[1].equals("add")) {
         try {
            String text3 = texts[2];
            RelationType relationType2 = RelationType.valueOf(texts[3].toUpperCase(Locale.ROOT));
            this.novoline.getRelationManager().put(text3, relationType2);
            this.novoline.getRelationManager().save();
            this.sendLocalized("command.relations.added", texts[2], this.getTypeName(relationType2));
         } catch (IllegalArgumentException illegalArgumentException) {
            this.sendLocalized("command.relations.invalid_type");
         } catch (IOException ioException) {
            this.sendLocalized("command.relations.io_error");
         }
      } else if ((texts.length == 2 || texts.length == 3) && texts[1].equals("list")) {
         try {
            RelationType relationType3 = texts.length == 3 ? this.getRelationType(texts[2]) : null;
            java.util.Map.Entry[] relations2 = this.getRelations(relationType3);
            if (relationType3 != null) {
               this.sendLocalized("command.relations.list_filter", this.getTypeName(relationType3), relations2.length);
            } else {
               this.sendLocalized("command.relations.list", relations2.length);
            }

            for (int i = 0; i < relations2.length; i++) {
               java.util.Map.Entry entry2 = relations2[i];
               this.sendLocalized("command.relations.list_entry", i, entry2.getKey(), this.getTypeName((RelationType)entry2.getValue()));
            }
         } catch (IllegalArgumentException illegalArgumentException2) {
            this.sendLocalized("command.relations.invalid_type");
         }
      } else if (texts.length == 3 && texts[1].equals("remove")) {
         try {
            this.novoline.getRelationManager().remove(texts[2]);
            this.novoline.getRelationManager().save();
            this.sendLocalized("command.relations.removed", texts[2]);
         } catch (IOException ioException2) {
            this.sendLocalized("command.relations.io_error");
         }
      } else if ((texts.length == 2 || texts.length == 3) && texts[1].equals("clear")) {
         RelationType relationType4 = texts.length == 3 ? this.getRelationType(texts[2]) : null;
         java.util.Map.Entry[] relations3 = this.getRelations(relationType4);

         for (java.util.Map.Entry entry3 : relations3) {
            this.novoline.getRelationManager().remove(entry3.getKey());
         }

         if (relationType4 != null) {
            this.sendLocalized("command.relations.clear_filter", this.getTypeName(relationType4));
         } else {
            this.sendLocalized("command.relations.clear");
         }
      } else {
         this.sendLocalized("command.wrong_arguments");
         this.sendLocalized("command.relations.help_p1");
         this.sendLocalized("command.relations.help_p2");
         this.sendMessage("§c.rel/.t/.f list [type]");
         this.sendMessage("§c.rel/.t/.f clear [type]");
         this.sendMessage("§c.rel/.t/.f add <name> [type]");
         this.sendMessage("§c.rel/.t/.f remove <name> [type]");
      }
   }

   public FriendCommand() {
      super("manage relations", "relations", "rel", "friend", "f", "target", "tar");
   }

   private RelationType getRelationType(String text) {
      return RelationType.valueOf(text.toUpperCase(Locale.ROOT));
   }

   private java.util.Map.Entry<String, RelationType>[] getRelations(RelationType relationType) {
      return this.novoline.getRelationManager().entrySet().stream().filter(entry -> relationType == null || entry.getValue() == relationType).toArray(java.util.Map.Entry[]::new);
   }

   private String getTypeName(RelationType relationType) {
      return StringUtils.capitalize(relationType.name().toLowerCase(Locale.ROOT));
   }
}
