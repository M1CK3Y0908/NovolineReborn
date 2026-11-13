package net.optifine.config;

public class GlVersion {
   private int major;
   private int minor;
   private int release;
   private String suffix;

   public int getRelease() {
      return this.release;
   }

   public int getMinor() {
      return this.minor;
   }

   public int toInt() {
      return this.minor > 9
         ? this.major * 100 + this.minor
         : (this.release > 9 ? this.major * 100 + this.minor * 10 + 9 : this.major * 100 + this.minor * 10 + this.release);
   }

   public GlVersion(int major, int minor, int release, String suffix) {
      this.major = major;
      this.minor = minor;
      this.release = release;
      this.suffix = suffix;
   }

   public GlVersion(int major, int minor, int release) {
      this(major, minor, 0, (String)null);
   }

   public int getMajor() {
      return this.major;
   }

   public GlVersion(int major, int minor) {
      this(major, minor, 0);
   }

   @Override
   public String toString() {
      return this.suffix == null ? this.major + "." + this.minor + "." + this.release : this.major + "." + this.minor + "." + this.release + this.suffix;
   }
}
