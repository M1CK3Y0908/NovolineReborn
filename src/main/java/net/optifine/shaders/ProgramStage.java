package net.optifine.shaders;

public enum ProgramStage {
   NONE(""),
   SHADOW("shadow"),
   GBUFFERS("gbuffers"),
   DEFERRED("deferred"),
   COMPOSITE("composite");

   private String name;

   ProgramStage(String text) {
      this.name = text;
   }

   public String getName() {
      return this.name;
   }
}
