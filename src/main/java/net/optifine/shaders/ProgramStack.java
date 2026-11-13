package net.optifine.shaders;

import java.util.ArrayDeque;
import java.util.Deque;

public class ProgramStack {
   private Deque<Program> stack = new ArrayDeque<>();

   public Program pop() {
      if (this.stack.isEmpty()) {
         throw new RuntimeException("Program stack empty");
      } else {
         return this.stack.pollLast();
      }
   }

   public void push(Program program) {
      this.stack.addLast(program);
      if (this.stack.size() > 100) {
         throw new RuntimeException("Program stack overflow: " + this.stack.size());
      }
   }
}
