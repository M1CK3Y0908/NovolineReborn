package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
   private static NBTBase createNBTBase(DataInput dataInput, NBTSizeTracker nbtSizeTracker) throws IOException {
      byte byteValue = dataInput.readByte();
      if (byteValue == 0) {
         return new NBTTagEnd();
      }

      dataInput.readUTF();
      NBTBase newByType = NBTBase.createNewByType(byteValue);

      try {
         newByType.read(dataInput, 0, nbtSizeTracker);
         return newByType;
      } catch (IOException ioException) {
         CrashReport crashReport = CrashReport.makeCrashReport(ioException, "Loading NBT data");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("NBT Tag");
         crashReportCategory.addCrashSection("Tag name", "[UNNAMED TAG]");
         crashReportCategory.addCrashSection("Tag type", byteValue);
         throw new ReportedException(crashReport);
      }
   }

   public static void writeCompressed(NBTTagCompound compound, OutputStream outputStream) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

      try {
         write(compound, dataOutputStream);
      } finally {
         dataOutputStream.close();
      }
   }

   public static NBTTagCompound read(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      NBTBase nBTBase = createNBTBase(input, sizeTracker);
      if (nBTBase instanceof NBTTagCompound) {
         return (NBTTagCompound)nBTBase;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(NBTTagCompound compound, File file) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

      try {
         write(compound, dataOutputStream);
      } finally {
         dataOutputStream.close();
      }
   }

   public static void write(NBTTagCompound compound, DataOutput output) throws IOException {
      writeTag(compound, output);
   }

   public static void safeWrite(NBTTagCompound compound, File file) throws IOException {
      File file2 = new File(file.getAbsolutePath() + "_tmp");
      if (file2.exists()) {
         file2.delete();
      }

      write(compound, file2);
      if (file.exists()) {
         file.delete();
      }

      if (file.exists()) {
         throw new IOException("Failed to delete " + file);
      }

      file2.renameTo(file);
   }

   public static NBTTagCompound read(DataInputStream inputStream) throws IOException {
      return read(inputStream, NBTSizeTracker.INFINITE);
   }

   public static NBTTagCompound readCompressed(InputStream is) throws IOException {
      DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));

      NBTTagCompound nBTTagCompound;
      try {
         nBTTagCompound = read(dataInputStream, NBTSizeTracker.INFINITE);
      } finally {
         dataInputStream.close();
      }

      return nBTTagCompound;
   }

   public static NBTTagCompound read(File file) throws IOException {
      if (!file.exists()) {
         return null;
      }

      DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

      NBTTagCompound nBTTagCompound;
      try {
         nBTTagCompound = read(dataInputStream, NBTSizeTracker.INFINITE);
      } finally {
         dataInputStream.close();
      }

      return nBTTagCompound;
   }

   private static void writeTag(NBTBase tag, DataOutput output) throws IOException {
      output.writeByte(tag.getId());
      if (tag.getId() != 0) {
         output.writeUTF("");
         tag.write(output);
      }
   }
}
