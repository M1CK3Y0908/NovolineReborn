package wtf.novoline.client.discord;

public class Snowflake {
   private final long value;
   private static final int SEQUENCE_MASK = 7;
   private static final int NODE_MASK = 1023;
   private static final int TIMESTAMP_SHIFT = 21;
   public static final int ZERO = 0;
   private static final long TIMESTAMP_MASK = 4398046511103L;
   private static final int SEQUENCE_BITS = 3;

   public long getLongValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      long l = this.getLongValue();
      return 59 + (int)(l >>> 32 ^ l);
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Snowflake snowflake)) {
         return false;
      } else {
         return !snowflake.canEqual(this) ? false : this.getLongValue() == snowflake.getLongValue();
      }
   }

   public Snowflake(long l) {
      this.value = l;
   }

   public long getTimestamp() {
      return this.value >> 21;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Snowflake;
   }

   public int getSequence() {
      return (int)(this.value & 7L);
   }
}
