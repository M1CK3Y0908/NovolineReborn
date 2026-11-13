package net.optifine.config;

import net.minecraft.src.Config;

public class VillagerProfession {
   private int profession;
   private int[] careers;

   public VillagerProfession(int profession, int[] careers) {
      this.profession = profession;
      this.careers = careers;
   }

   public boolean matches(int prof, int car) {
      return this.profession != prof ? false : this.careers == null || Config.equalsOne(car, this.careers);
   }

   @Override
   public String toString() {
      return this.careers == null ? this.profession + "" : this.profession + ":" + Config.arrayToString(this.careers);
   }

   public int[] getCareers() {
      return this.careers;
   }

   public int getProfession() {
      return this.profession;
   }
}
