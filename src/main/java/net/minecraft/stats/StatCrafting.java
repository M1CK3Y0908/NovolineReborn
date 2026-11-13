package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
   private final Item item;

   public StatCrafting(String statIdPrefix, String itemName, IChatComponent statNameIn, Item itemIn) {
      super(statIdPrefix + itemName, statNameIn);
      this.item = itemIn;
      int idFromItem = Item.getIdFromItem(itemIn);
      if (idFromItem != 0) {
         IScoreObjectiveCriteria.INSTANCES.put(statIdPrefix + idFromItem, this.getCriteria());
      }
   }

   public Item getItem() {
      return this.item;
   }
}
