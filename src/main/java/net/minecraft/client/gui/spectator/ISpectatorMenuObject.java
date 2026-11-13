package net.minecraft.client.gui.spectator;

import net.minecraft.util.IChatComponent;

public interface ISpectatorMenuObject {
   void selectItem(SpectatorMenu spectatorMenu);

   IChatComponent getSpectatorName();

   boolean isEnabled();

   void renderIcon(float f, int i);
}
