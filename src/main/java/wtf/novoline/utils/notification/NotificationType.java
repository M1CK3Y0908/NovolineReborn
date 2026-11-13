package wtf.novoline.utils.notification;

import net.minecraft.util.ResourceLocation;

public enum NotificationType {
   INFO(new ResourceLocation("novoline", "images/info.png"), 537929584, -1728030816),
   WARNING(new ResourceLocation("novoline", "images/warning.png"), 547898384, -1713867776),
   ERROR(new ResourceLocation("novoline", "images/error.png"), 545718272, -1715000292),
   SUCCESS(new ResourceLocation("novoline", "images/success.png"), 536894465, -1728017359);

   private final ResourceLocation icon;
   private final int accentColor;
   private final int backgroundColor;

   public int getBackgroundColor() {
      return this.backgroundColor;
   }

   public ResourceLocation getResourceLocation() {
      return this.icon;
   }

   public int getAccentColor() {
      return this.accentColor;
   }

   NotificationType(ResourceLocation resourceLocation, int i, int i2) {
      this.icon = resourceLocation;
      this.accentColor = i;
      this.backgroundColor = i2;
   }
}
