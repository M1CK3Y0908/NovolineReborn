package net.minecraft.client.gui.spectator;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SpectatorMenu {
   private static final ISpectatorMenuObject CLOSE_ITEM = new SpectatorMenu.EndSpectatorObject();
   private final List<SpectatorDetails> previousMenus = Lists.newArrayList();
   private static final ISpectatorMenuObject PREVIOUS_PAGE_ITEM = new SpectatorMenu.MoveMenuObject(-1, true);
   private static final ISpectatorMenuObject NEXT_PAGE_ITEM = new SpectatorMenu.MoveMenuObject(1, true);
   private static final ISpectatorMenuObject DISABLED_NEXT_PAGE_ITEM = new SpectatorMenu.MoveMenuObject(1, false);
   private int page;
   private ISpectatorMenuView currentGroup = new BaseSpectatorGroup();
   private int selectedSlot = -1;
   private final ISpectatorMenuRecipient recipient;
   public static final ISpectatorMenuObject EMPTY_SLOT = new ISpectatorMenuObject() {
      @Override
      public void selectItem(SpectatorMenu menu) {
      }

      @Override
      public void renderIcon(float brightness, int alpha) {
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("");
      }

      @Override
      public boolean isEnabled() {
         return false;
      }
   };

   public void selectSlot(int slot) {
      ISpectatorMenuObject spectatorMenuObject = this.getItem(slot);
      if (spectatorMenuObject != EMPTY_SLOT) {
         if (this.selectedSlot == slot && spectatorMenuObject.isEnabled()) {
            spectatorMenuObject.selectItem(this);
         } else {
            this.selectedSlot = slot;
         }
      }
   }

   public ISpectatorMenuObject getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public ISpectatorMenuObject getItem(int slot) {
      int i = slot + this.page * 6;
      return this.page > 0 && slot == 0
         ? PREVIOUS_PAGE_ITEM
         : (
            slot == 7
               ? (i < this.currentGroup.getItems().size() ? NEXT_PAGE_ITEM : DISABLED_NEXT_PAGE_ITEM)
               : (
                  slot == 8
                     ? CLOSE_ITEM
                     : (
                        i >= 0 && i < this.currentGroup.getItems().size()
                           ? (ISpectatorMenuObject)Objects.firstNonNull(this.currentGroup.getItems().get(i), EMPTY_SLOT)
                           : EMPTY_SLOT
                     )
               )
         );
   }

   public void exit() {
      this.recipient.onSpectatorMenuClosed(this);
   }

   public void selectCategory(ISpectatorMenuView group) {
      this.previousMenus.add(this.getCurrentDetails());
      this.currentGroup = group;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public List<ISpectatorMenuObject> getItems() {
      List<ISpectatorMenuObject> list = Lists.newArrayList();

      for (int index = 0; index <= 8; index++) {
         list.add(this.getItem(index));
      }

      return list;
   }

   public SpectatorDetails getCurrentDetails() {
      return new SpectatorDetails(this.currentGroup, this.getItems(), this.selectedSlot);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public ISpectatorMenuView getCurrentGroup() {
      return this.currentGroup;
   }

   public SpectatorMenu(ISpectatorMenuRecipient recipientIn) {
      this.recipient = recipientIn;
   }

   static class EndSpectatorObject implements ISpectatorMenuObject {
      @Override
      public void selectItem(SpectatorMenu menu) {
         menu.exit();
      }

      @Override
      public boolean isEnabled() {
         return true;
      }

      @Override
      public void renderIcon(float brightness, int alpha) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
         Gui.drawModalRectWithCustomSizedTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
      }

      private EndSpectatorObject() {
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("Close menu");
      }
   }

   static class MoveMenuObject implements ISpectatorMenuObject {
      private final int direction;
      private final boolean enabled;

      @Override
      public void selectItem(SpectatorMenu menu) {
         menu.page = this.direction;
      }

      public MoveMenuObject(int directionIn, boolean enabledIn) {
         this.direction = directionIn;
         this.enabled = enabledIn;
      }

      @Override
      public IChatComponent getSpectatorName() {
         return this.direction < 0 ? new ChatComponentText("Previous Page") : new ChatComponentText("Next Page");
      }

      @Override
      public boolean isEnabled() {
         return this.enabled;
      }

      @Override
      public void renderIcon(float brightness, int alpha) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
         if (this.direction < 0) {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         } else {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         }
      }
   }
}
