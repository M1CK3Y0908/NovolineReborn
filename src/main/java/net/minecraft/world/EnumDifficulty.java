package net.minecraft.world;

public enum EnumDifficulty {
   PEACEFUL(0, "options.difficulty.peaceful"),
   EASY(1, "options.difficulty.easy"),
   NORMAL(2, "options.difficulty.normal"),
   HARD(3, "options.difficulty.hard");

   private final int difficultyId;
   private final String difficultyResourceKey;
   private static final EnumDifficulty[] difficultyEnums = new EnumDifficulty[values().length];

   public int getDifficultyId() {
      return this.difficultyId;
   }

   EnumDifficulty(int i, String text) {
      this.difficultyId = i;
      this.difficultyResourceKey = text;
   }

   static {
      for (EnumDifficulty enumDifficulty : values()) {
         difficultyEnums[enumDifficulty.difficultyId] = enumDifficulty;
      }
   }

   public String getDifficultyResourceKey() {
      return this.difficultyResourceKey;
   }

   public static EnumDifficulty getDifficultyEnum(int i) {
      return difficultyEnums[i % difficultyEnums.length];
   }
}
