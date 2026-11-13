package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.util.IChatComponent;

public interface ISpectatorMenuView {
   IChatComponent getPrompt();

   List<ISpectatorMenuObject> getItems();
}
