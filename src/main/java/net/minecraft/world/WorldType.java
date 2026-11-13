package net.minecraft.world;

public class WorldType {
   public static final WorldType[] worldTypes = new WorldType[16];
   private final String worldType;
   private boolean hasNotificationData;
   private final int generatorVersion;
   public static final WorldType DEFAULT = new WorldType(0, "default", 1).setVersioned();
   public static final WorldType FLAT = new WorldType(1, "flat");
   private boolean canBeCreated;
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType AMPLIFIED = new WorldType(3, "amplified").setNotificationData();
   public static final WorldType CUSTOMIZED = new WorldType(4, "customized");
   public static final WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");
   private final int worldTypeId;
   public static final WorldType DEFAULT_1_1 = new WorldType(8, "default_1_1", 0).getWorldType();
   private boolean isWorldTypeVersioned;

   public String getTranslateName() {
      return "generator." + this.worldType;
   }

   private WorldType getWorldType() {
      this.canBeCreated = false;
      return this;
   }

   public String getTranslatedInfo() {
      return this.getTranslateName() + ".info";
   }

   public boolean showWorldInfoNotice() {
      return this.hasNotificationData;
   }

   public int getGeneratorVersion() {
      return this.generatorVersion;
   }

   public WorldType getWorldTypeForGeneratorVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   public boolean getCanBeCreated() {
      return this.canBeCreated;
   }

   private WorldType setVersioned() {
      this.isWorldTypeVersioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.isWorldTypeVersioned;
   }

   public String getWorldTypeName() {
      return this.worldType;
   }

   private WorldType setNotificationData() {
      this.hasNotificationData = true;
      return this;
   }

   public static WorldType parseWorldType(String type) {
      for (int index = 0; index < worldTypes.length; index++) {
         if (worldTypes[index] != null && worldTypes[index].worldType.equalsIgnoreCase(type)) {
            return worldTypes[index];
         }
      }

      return null;
   }

   private WorldType(int id, String name, int version) {
      this.worldType = name;
      this.generatorVersion = version;
      this.canBeCreated = true;
      this.worldTypeId = id;
      worldTypes[id] = this;
   }

   private WorldType(int id, String name) {
      this(id, name, 0);
   }

   public int getWorldTypeID() {
      return this.worldTypeId;
   }
}
