package net.minecraft.util;

public class Vec4b {
   private byte iconBit;
   private byte x;
   private byte y;
   private byte rotation;

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Vec4b vec4b)) {
         return false;
      } else {
         return this.iconBit != vec4b.iconBit ? false : (this.rotation != vec4b.rotation ? false : (this.x != vec4b.x ? false : this.y == vec4b.y));
      }
   }

   public byte getRotation() {
      return this.rotation;
   }

   public Vec4b(Vec4b other) {
      this.iconBit = other.iconBit;
      this.x = other.x;
      this.y = other.y;
      this.rotation = other.rotation;
   }

   public byte getIconBit() {
      return this.iconBit;
   }

   public byte getY() {
      return this.y;
   }

   public byte getX() {
      return this.x;
   }

   public Vec4b(byte iconBitIn, byte xIn, byte yIn, byte rotationIn) {
      this.iconBit = iconBitIn;
      this.x = xIn;
      this.y = yIn;
      this.rotation = rotationIn;
   }

   @Override
   public int hashCode() {
      int iconBit2 = this.iconBit;
      iconBit2 = 31 * iconBit2 + this.x;
      iconBit2 = 31 * iconBit2 + this.y;
      return 31 * iconBit2 + this.rotation;
   }
}
