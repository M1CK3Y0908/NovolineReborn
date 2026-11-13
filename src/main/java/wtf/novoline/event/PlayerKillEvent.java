package wtf.novoline.event;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerKillEvent extends Event {
   public EntityPlayer entityPlayer;

   public PlayerKillEvent(EntityPlayer entityPlayer2) {
      this.entityPlayer = entityPlayer2;
   }
}
