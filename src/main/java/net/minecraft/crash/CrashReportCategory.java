package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class CrashReportCategory {
   private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];
   private final CrashReport crashReport;
   private final String name;

   public int getInt() {
      StackTraceElement[] stackTrace2 = Thread.currentThread().getStackTrace();
      if (stackTrace2.length <= 0) {
         return 0;
      }

      this.stackTrace = new StackTraceElement[stackTrace2.length - 3 - 1];
      System.arraycopy(stackTrace2, 4, this.stackTrace, 0, this.stackTrace.length);
      return this.stackTrace.length;
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
      final int idFromBlock = Block.getIdFromBlock(blockIn);
      category.addCrashSectionCallable("Block type", new Callable<String>() {
         public String call() throws Exception {
            try {
               return String.format("ID #%d (%s // %s)", idFromBlock, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
            } catch (Throwable throwable2) {
               return "ID #" + idFromBlock;
            }
         }
      });
      category.addCrashSectionCallable("Block data value", new Callable<String>() {
         public String call() throws Exception {
            if (blockData < 0) {
               return "Unknown? (Got " + blockData + ")";
            }

            String text = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
            return String.format("%1$d / 0x%1$X / 0b%2$s", blockData, text);
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   public void addCrashSectionCallable(String sectionName, Callable<String> callable) {
      try {
         this.addCrashSection(sectionName, callable.call());
      } catch (Throwable throwable2) {
         this.addCrashSectionThrowable(sectionName, throwable2);
      }
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final IBlockState state) {
      category.addCrashSectionCallable("Block", new Callable<String>() {
         public String call() throws Exception {
            return state.toString();
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   public CrashReportCategory(CrashReport report, String name) {
      this.crashReport = report;
      this.name = name;
   }

   public static String getCoordinateInfo(double x, double y, double z) {
      return String.format("%.2f,%.2f,%.2f - %s", x, y, z, getCoordinateInfo(new BlockPos(x, y, z)));
   }

   public void addCrashSection(String sectionName, Object value) {
      this.children.add(new CrashReportCategory.Entry(sectionName, value));
   }

   public static String getCoordinateInfo(BlockPos pos) {
      int xCoordinate = pos.getX();
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ();
      StringBuilder stringBuilder = new StringBuilder();

      try {
         stringBuilder.append(String.format("World: (%d,%d,%d)", xCoordinate, yCoordinate, zCoordinate));
      } catch (Throwable throwable2) {
         stringBuilder.append("(Error finding world loc)");
      }

      stringBuilder.append(", ");

      try {
         int i = xCoordinate >> 4;
         int i2 = zCoordinate >> 4;
         int i3 = xCoordinate & 15;
         int i4 = yCoordinate >> 4;
         int i5 = zCoordinate & 15;
         int i6 = i << 4;
         int i7 = i2 << 4;
         int i8 = (i + 1 << 4) - 1;
         int i9 = (i2 + 1 << 4) - 1;
         stringBuilder.append(
            String.format(
               "Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)",
               i3,
               i4,
               i5,
               i,
               i2,
               i6,
               i7,
               i8,
               i9
            )
         );
      } catch (Throwable throwable3) {
         stringBuilder.append("(Error finding chunk loc)");
      }

      stringBuilder.append(", ");

      try {
         int i10 = xCoordinate >> 9;
         int i11 = zCoordinate >> 9;
         int i12 = i10 << 5;
         int i13 = i11 << 5;
         int i14 = (i10 + 1 << 5) - 1;
         int i15 = (i11 + 1 << 5) - 1;
         int i16 = i10 << 9;
         int i17 = i11 << 9;
         int i18 = (i10 + 1 << 9) - 1;
         int i19 = (i11 + 1 << 9) - 1;
         stringBuilder.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               i10,
               i11,
               i12,
               i13,
               i14,
               i15,
               i16,
               i17,
               i18,
               i19
            )
         );
      } catch (Throwable throwable4) {
         stringBuilder.append("(Error finding world loc)");
      }

      return stringBuilder.toString();
   }

   public void appendToStringBuilder(StringBuilder builder) {
      builder.append("-- ").append(this.name).append(" --\n");
      builder.append("Details:");

      for (CrashReportCategory.Entry entry : this.children) {
         builder.append("\n\t");
         builder.append(entry.getKey());
         builder.append(": ");
         builder.append(entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         builder.append("\nStacktrace:");

         for (StackTraceElement stackTraceElement3 : this.stackTrace) {
            builder.append("\n\tat ");
            builder.append(stackTraceElement3.toString());
         }
      }
   }

   public void trimStackTraceEntriesFromBottom(int amount) {
      StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - amount];
      System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
      this.stackTrace = astacktraceelement;
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public void addCrashSectionThrowable(String sectionName, Throwable throwable) {
      this.addCrashSection(sectionName, throwable);
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
      if (this.stackTrace.length != 0 && stackTraceElement != null) {
         StackTraceElement stackTraceElement3 = this.stackTrace[0];
         if (stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod()
            && stackTraceElement3.getClassName().equals(stackTraceElement.getClassName())
            && stackTraceElement3.getFileName().equals(stackTraceElement.getFileName())
            && stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName())) {
            if (stackTraceElement2 != null != this.stackTrace.length > 1) {
               return false;
            }

            if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
               return false;
            }

            this.stackTrace[0] = stackTraceElement;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static class Entry {
      private final String key;
      private final String value;

      public String getValue() {
         return this.value;
      }

      public Entry(String key, Object value) {
         this.key = key;
         if (value == null) {
            this.value = "~~NULL~~";
         } else if (value instanceof Throwable throwable) {
            this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
         } else {
            this.value = value.toString();
         }
      }

      public String getKey() {
         return this.key;
      }
   }
}
