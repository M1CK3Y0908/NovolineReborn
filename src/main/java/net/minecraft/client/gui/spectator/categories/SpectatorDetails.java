package net.minecraft.client.gui.spectator.categories;

import com.google.common.base.Objects;
import java.util.List;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;

public class SpectatorDetails {
   private final ISpectatorMenuView group;
   private final List<ISpectatorMenuObject> items;
   private final int selectedSlot;

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public ISpectatorMenuObject getObject(int slot) {
      return slot >= 0 && slot < this.items.size()
         ? (ISpectatorMenuObject)Objects.firstNonNull(this.items.get(slot), SpectatorMenu.EMPTY_SLOT)
         : SpectatorMenu.EMPTY_SLOT;
   }

   public SpectatorDetails(ISpectatorMenuView groupIn, List<ISpectatorMenuObject> itemsIn, int selectedSlotIn) {
      this.group = groupIn;
      this.items = itemsIn;
      this.selectedSlot = selectedSlotIn;
   }
}
