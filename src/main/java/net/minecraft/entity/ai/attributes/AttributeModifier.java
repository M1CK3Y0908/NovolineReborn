package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.UUID;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   private boolean isSaved = true;
   private final UUID id;
   private final String name;
   private final double amount;
   private final int operation;

   @Override
   public String toString() {
      boolean isSaved2 = this.isSaved;
      UUID id2 = this.id;
      String name2 = this.name;
      int operation2 = this.operation;
      double amount2 = this.amount;
      return "AttributeModifier{amount=" + amount2 + ", operation=" + operation2 + ", name='" + name2 + "', id=" + id2 + ", serialize=" + isSaved2 + "}";
   }

   public AttributeModifier(String nameIn, double amountIn, int operationIn) {
      this(MathHelper.getRandomUuid(ThreadLocalRandom.current()), nameIn, amountIn, operationIn);
   }

   public String getName() {
      return this.name;
   }

   public UUID getID() {
      return this.id;
   }

   @Override
   public boolean equals(Object arg1) {
      if (this == arg1) {
         return true;
      }

      if (arg1 != null && this.getClass() == arg1.getClass()) {
         AttributeModifier attributeModifier = (AttributeModifier)arg1;
         if (this.id != null) {
            if (!this.id.equals(attributeModifier.id)) {
               return false;
            }
         } else if (attributeModifier.id != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public double getAmount() {
      return this.amount;
   }

   public AttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
      this.id = idIn;
      this.name = nameIn;
      this.amount = amountIn;
      this.operation = operationIn;
      Validate.notEmpty(nameIn, "Modifier name cannot be empty");
      Validate.inclusiveBetween(0L, 2L, operationIn, "Invalid operation");
   }

   public boolean isSaved() {
      return this.isSaved;
   }

   public int getOperation() {
      return this.operation;
   }

   @Override
   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public AttributeModifier getAttributeModifier() {
      this.isSaved = false;
      return this;
   }
}
