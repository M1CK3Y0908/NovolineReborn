package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
   private final IAttribute parentAttribute;
   private final String unlocalizedName;
   private boolean shouldWatch;
   private final double defaultValue;

   public BaseAttribute getBaseAttribute() {
      this.shouldWatch = true;
      return this;
   }

   @Override
   public String getAttributeUnlocalizedName() {
      return this.unlocalizedName;
   }

   protected BaseAttribute(IAttribute iAttribute, String unlocalizedNameIn, double defaultValueIn) {
      this.parentAttribute = iAttribute;
      this.unlocalizedName = unlocalizedNameIn;
      this.defaultValue = defaultValueIn;
      if (unlocalizedNameIn == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   @Override
   public boolean equals(Object arg1) {
      return arg1 instanceof IAttribute && this.unlocalizedName.equals(((IAttribute)arg1).getAttributeUnlocalizedName());
   }

   @Override
   public IAttribute getParentAttribute() {
      return this.parentAttribute;
   }

   @Override
   public int hashCode() {
      return this.unlocalizedName.hashCode();
   }

   @Override
   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   @Override
   public double getDefaultValue() {
      return this.defaultValue;
   }
}
