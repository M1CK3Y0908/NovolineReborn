package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;

public class Programs {
   private List<Program> programs = new ArrayList<>();
   private Program programNone = this.getProgram("", ProgramStage.NONE);

   private Program getProgram(String text, ProgramStage programStage2) {
      int size2 = this.programs.size();
      Program program = new Program(size2, text, programStage2);
      this.programs.add(program);
      return program;
   }

   public int getCount() {
      return this.programs.size();
   }

   public Program make(String name, ProgramStage programStage, Program backupProgram) {
      int count = this.programs.size();
      Program program = new Program(count, name, programStage, backupProgram);
      this.programs.add(program);
      return program;
   }

   public Program makeComposite(String name) {
      return this.make(name, ProgramStage.COMPOSITE, this.programNone);
   }

   public Program getProgramNone() {
      return this.programNone;
   }

   public Program[] getArray2(String text) {
      Program[] programs2 = new Program[16];

      for (int i = 0; i < 16; i++) {
         String text2 = i == 0 ? text : text + i;
         programs2[i] = this.makeDeferred(text2);
      }

      return programs2;
   }

   public Program makeDeferred(String name) {
      return this.make(name, ProgramStage.DEFERRED, this.programNone);
   }

   public Program makeGbuffers(String name, Program backupProgram) {
      return this.make(name, ProgramStage.GBUFFERS, backupProgram);
   }

   @Override
   public String toString() {
      return this.programs.toString();
   }

   public Program makeVirtual(String name) {
      return this.getProgram(name, ProgramStage.NONE);
   }

   public String[] getProgramNames() {
      String[] stringArray = new String[this.programs.size()];

      for (int index = 0; index < stringArray.length; index++) {
         stringArray[index] = this.programs.get(index).getName();
      }

      return stringArray;
   }

   public Program[] getArray(String text) {
      Program[] programs2 = new Program[16];

      for (int i = 0; i < 16; i++) {
         String text2 = i == 0 ? text : text + i;
         programs2[i] = this.makeComposite(text2);
      }

      return programs2;
   }

   public Program[] getPrograms() {
      return this.programs.toArray(new Program[this.programs.size()]);
   }

   public Program makeShadow(String name, Program backupProgram) {
      return this.make(name, ProgramStage.SHADOW, backupProgram);
   }

   public Program getProgram(String name) {
      if (name == null) {
         return null;
      }

      for (int i = 0; i < this.programs.size(); i++) {
         Program program = this.programs.get(i);
         if (program.getName().equals(name)) {
            return program;
         }
      }

      return null;
   }
}
