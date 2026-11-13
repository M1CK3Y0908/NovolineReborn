package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int horizontalIndex;
   private final int opposite;
   private final String name;
   public static final EnumFacing[] VALUES = new EnumFacing[6];
   private static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
   private final EnumFacing.Axis axis;
   private static final Map<String, EnumFacing> NAME_LOOKUP = Maps.newHashMap();
   private final EnumFacing.AxisDirection axisDirection;
   private final Vec3i directionVec;

   public static EnumFacing random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(float x, float y, float z) {
      EnumFacing enumFacing = NORTH;
      float f = Float.MIN_VALUE;

      for (EnumFacing enumFacing2 : values()) {
         float f2 = x * enumFacing2.directionVec.getX() + y * enumFacing2.directionVec.getY() + z * enumFacing2.directionVec.getZ();
         if (f2 > f) {
            f = f2;
            enumFacing = enumFacing2;
         }
      }

      return enumFacing;
   }

   EnumFacing(int i, int i2, int i3, String text, EnumFacing.AxisDirection axisDirection2, EnumFacing.Axis axis2, Vec3i vec3i) {
      this.index = i;
      this.horizontalIndex = i3;
      this.opposite = i2;
      this.name = text;
      this.axis = axis2;
      this.axisDirection = axisDirection2;
      this.directionVec = vec3i;
   }

   public int getFrontOffsetZ() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   private EnumFacing rotateZ() {
      switch (this) {
         case EAST:
            return DOWN;
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            return UP;
         case UP:
            return EAST;
         case DOWN:
            return WEST;
      }
   }

   public static EnumFacing byName(String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch (axis) {
         case X:
            if (this != WEST && this != EAST) {
               return this.rotateX();
            }

            return this;
         case Y:
            if (this != UP && this != DOWN) {
               return this.rotateY();
            }

            return this;
         case Z:
            if (this != NORTH && this != SOUTH) {
               return this.rotateZ();
            }

            return this;
         default:
            throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   public String getName2() {
      return this.name;
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing rotateY() {
      switch (this) {
         case NORTH:
            return EAST;
         case EAST:
            return SOUTH;
         case SOUTH:
            return WEST;
         case WEST:
            return NORTH;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   public EnumFacing getOpposite() {
      return VALUES[this.opposite];
   }

   public EnumFacing rotateYCCW() {
      switch (this) {
         case NORTH:
            return WEST;
         case EAST:
            return NORTH;
         case SOUTH:
            return EAST;
         case WEST:
            return SOUTH;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public static EnumFacing getFront(int index) {
      return VALUES[MathHelper.abs_int(index % VALUES.length)];
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static EnumFacing fromAngle(double angle) {
      return getHorizontal(MathHelper.floor_double(angle / 90.0 + 0.5) & 3);
   }

   public static EnumFacing getHorizontal(int horizontalIndex) {
      return HORIZONTALS[MathHelper.abs_int(horizontalIndex % HORIZONTALS.length)];
   }

   private EnumFacing rotateX() {
      switch (this) {
         case NORTH:
            return DOWN;
         case EAST:
         case WEST:
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
         case SOUTH:
            return UP;
         case UP:
            return NORTH;
         case DOWN:
            return SOUTH;
      }
   }

   public int getFrontOffsetX() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   public int getFrontOffsetY() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   static {
      for (EnumFacing enumFacing : values()) {
         VALUES[enumFacing.index] = enumFacing;
         if (enumFacing.getAxis().isHorizontal()) {
            HORIZONTALS[enumFacing.horizontalIndex] = enumFacing;
         }

         NAME_LOOKUP.put(enumFacing.getName2().toLowerCase(), enumFacing);
      }
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection axisDirection, EnumFacing.Axis axis) {
      for (EnumFacing enumFacing : values()) {
         if (enumFacing.getAxisDirection() == axisDirection && enumFacing.getAxis() == axis) {
            return enumFacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
   }

   public int getIndex() {
      return this.index;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("x", EnumFacing.Plane.HORIZONTAL),
      Y("y", EnumFacing.Plane.VERTICAL),
      Z("z", EnumFacing.Plane.HORIZONTAL);

      private final String name;
      private final EnumFacing.Plane plane;
      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Maps.newHashMap();

      public String getName2() {
         return this.name;
      }

      public boolean isHorizontal() {
         return this.plane == EnumFacing.Plane.HORIZONTAL;
      }

      public boolean apply(EnumFacing facing) {
         return facing != null && facing.getAxis() == this;
      }

      Axis(String text, EnumFacing.Plane plane2) {
         this.name = text;
         this.plane = plane2;
      }

      public static EnumFacing.Axis byName(String name) {
         return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
      }

      static {
         for (EnumFacing.Axis axis2 : values()) {
            NAME_LOOKUP.put(axis2.getName2().toLowerCase(), axis2);
         }
      }

      public EnumFacing.Plane getPlane() {
         return this.plane;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this.plane == EnumFacing.Plane.VERTICAL;
      }
   }

   public enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      @Override
      public String toString() {
         return this.description;
      }

      public int getOffset() {
         return this.offset;
      }

      AxisDirection(int i, String text) {
         this.offset = i;
         this.description = text;
      }
   }

   public enum Plane implements Predicate<EnumFacing>, Iterable<EnumFacing> {
      HORIZONTAL,
      VERTICAL;

      public boolean apply(EnumFacing facing) {
         return facing != null && facing.getAxis().getPlane() == this;
      }

      @Override
      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facings());
      }

      public EnumFacing random(Random rand) {
         EnumFacing[] aenumfacing = this.facings();
         return aenumfacing[rand.nextInt(aenumfacing.length)];
      }

      public EnumFacing[] facings() {
         switch (this) {
            case HORIZONTAL:
               return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
            case VERTICAL:
               return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
            default:
               throw new Error("Someone's been tampering with the universe!");
         }
      }
   }
}
