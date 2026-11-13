package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class PacketBuffer extends ByteBuf {
   private static String text;
   private final ByteBuf buf;

   @Override
   public int getMediumLE(int i) {
      return this.buf.getMediumLE(i);
   }

   @Override
   public boolean isReadable() {
      return this.buf.isReadable();
   }

   @Override
   public ByteBuf capacity(int i) {
      return this.buf.capacity(i);
   }

   public int getInt(ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByte(byteBufProcessor);
   }

   @Override
   public ByteBuf writeBoolean(boolean b) {
      return this.buf.writeBoolean(b);
   }

   @Override
   public String toString(Charset charset) {
      return this.buf.toString(charset);
   }

   @Override
   public ByteBuf writeShortLE(int i) {
      return this.buf.writeShortLE(i);
   }

   public String readStringFromBuffer(int maxLength) {
      int varIntFromBuffer = this.readVarIntFromBuffer();
      if (varIntFromBuffer > maxLength * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + varIntFromBuffer + " > " + maxLength * 4 + ")");
      } else if (varIntFromBuffer < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String text2 = this.toString(this.readerIndex(), varIntFromBuffer, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + varIntFromBuffer);
         if (text2.length() > maxLength) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + varIntFromBuffer + " > " + maxLength + ")");
         } else {
            return text2;
         }
      }
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf, int i, int i2) {
      return this.buf.writeBytes(byteBuf, i, i2);
   }

   @Override
   public ByteBuf slice() {
      return this.buf.slice();
   }

   @Override
   public ByteBuffer internalNioBuffer(int i, int i2) {
      return this.buf.internalNioBuffer(i, i2);
   }

   @Override
   public int capacity() {
      return this.buf.capacity();
   }

   @Override
   public ByteBuf readBytes(byte[] bytes, int i, int i2) {
      return this.buf.readBytes(bytes, i, i2);
   }

   @Override
   public ByteBuf writeIntLE(int i) {
      return this.buf.writeLongLE(i);
   }

   @Override
   public ByteBuf setByte(int i, int i2) {
      return this.buf.setByte(i, i2);
   }

   @Override
   public ByteBuf clear() {
      return this.buf.clear();
   }

   @Override
   public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
      return this.buf.setCharSequence(i, charSequence, charset);
   }

   @Override
   public byte readByte() {
      return this.buf.readByte();
   }

   @Override
   public ByteBuf setLongLE(int i, long l) {
      return this.buf.setLongLE(i, l);
   }

   @Override
   public int refCnt() {
      return this.buf.refCnt();
   }

   @Override
   public int hashCode() {
      return this.buf.hashCode();
   }

   @Override
   public ByteBuf writeMedium(int i) {
      return this.buf.writeMedium(i);
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this.buf.retainedDuplicate();
   }

   @Override
   public long readUnsignedInt() {
      return this.buf.readUnsignedInt();
   }

   @Override
   public int getInt(int i) {
      return this.buf.getInt(i);
   }

   public long readVarLong() {
      long l = 0L;
      int i = 0;

      byte b0;
      do {
         b0 = this.readByte();
         l |= (long)(b0 & 127) << i++ * 7;
         if (i > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while ((b0 & 128) == 128);

      return l;
   }

   public int readVarIntFromBuffer() {
      int i = 0;
      int i2 = 0;

      byte b0;
      do {
         b0 = this.readByte();
         i |= (b0 & 127) << i2++ * 7;
         if (i2 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while ((b0 & 128) == 128);

      return i;
   }

   @Override
   public ByteBuf resetWriterIndex() {
      return this.buf.resetWriterIndex();
   }

   @Override
   public ByteBuf slice(int i, int i2) {
      return this.buf.slice(i, i2);
   }

   public void writeUuid(UUID uUID) {
      this.writeLong(uUID.getMostSignificantBits());
      this.writeLong(uUID.getLeastSignificantBits());
   }

   @Override
   public ByteBuf readBytes(int i) {
      return this.buf.readBytes(i);
   }

   @Override
   public ByteBuffer[] nioBuffers(int i, int i2) {
      return this.buf.nioBuffers(i, i2);
   }

   @Override
   public int writeCharSequence(CharSequence charSequence, Charset charset) {
      return this.buf.writeCharSequence(charSequence, charset);
   }

   @Override
   public int maxWritableBytes() {
      return this.buf.maxWritableBytes();
   }

   @Override
   public ByteBuf retainedSlice() {
      return this.buf.retainedSlice();
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf, int i) {
      return this.buf.readBytes(byteBuf, i);
   }

   @Override
   public ByteBuf retain(int i) {
      return this.buf.retain(i);
   }

   @Override
   public ByteBuf ensureWritable(int i) {
      return this.buf.ensureWritable(i);
   }

   @Override
   public ByteBuf writeDouble(double v) {
      return this.buf.writeDouble(v);
   }

   @Override
   public int getUnsignedMediumLE(int i) {
      return this.buf.getUnsignedMediumLE(i);
   }

   @Override
   public int bytesBefore(int i, int i2, byte b) {
      return this.buf.bytesBefore(i, i2, b);
   }

   @Override
   public ByteBuf writeShort(int i) {
      return this.buf.writeShort(i);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf, int i2) {
      return this.buf.getBytes(i, byteBuf, i2);
   }

   @Override
   public int arrayOffset() {
      return this.buf.arrayOffset();
   }

   @Override
   public ByteBuf unwrap() {
      return this.buf.unwrap();
   }

   public ItemStack readItemStackFromBuffer() throws IOException {
      ItemStack itemStack = null;
      int shortValue = this.readShort();
      if (shortValue >= 0) {
         int byteValue = this.readByte();
         int shortValue2 = this.readShort();
         itemStack = new ItemStack(Item.getItemById(shortValue), byteValue, shortValue2);
         itemStack.setTagCompound(this.readNBTTagCompoundFromBuffer());
      }

      return itemStack;
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf, int i2) {
      return this.buf.setBytes(i, byteBuf, i2);
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      return this.buf.discardSomeReadBytes();
   }

   @Override
   public ByteBuf getBytes(int i, byte[] bytes) {
      return this.buf.getBytes(i, bytes);
   }

   @Override
   public int forEachByte(ByteProcessor byteProcessor) {
      return this.buf.forEachByte(byteProcessor);
   }

   public void writeEnumValue(Enum<?> value) {
      this.writeVarIntToBuffer(value.ordinal());
   }

   @Override
   public boolean getBoolean(int i) {
      return this.buf.getBoolean(i);
   }

   @Override
   public ByteBuf skipBytes(int i) {
      return this.buf.skipBytes(i);
   }

   @Override
   public ByteBuf duplicate() {
      return this.buf.duplicate();
   }

   @Override
   public int writableBytes() {
      return this.buf.writableBytes();
   }

   @Override
   public ByteBuf touch(Object hint) {
      return this.buf.touch(hint);
   }

   public void writeItemStackToBuffer(ItemStack stack) {
      if (stack == null) {
         this.writeShort(-1);
      } else {
         this.writeShort(Item.getIdFromItem(stack.getItem()));
         this.writeByte(stack.stackSize);
         this.writeShort(stack.getMetadata());
         NBTTagCompound nBTTagCompound = null;
         if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
            nBTTagCompound = stack.getTagCompound();
         }

         this.writeNBTTagCompoundToBuffer(nBTTagCompound);
      }
   }

   @Override
   public ByteBuffer nioBuffer(int i, int i2) {
      return this.buf.nioBuffer(i, i2);
   }

   @Override
   public boolean isDirect() {
      return this.buf.isDirect();
   }

   @Override
   public ByteBuf setShortLE(int i, int i2) {
      return this.buf.setShortLE(i, i2);
   }

   public void writeChatComponent(IChatComponent component) throws IOException {
      this.writeString(net.minecraft.util.IChatComponent.Serializer.componentToJson(component));
   }

   @Override
   public int forEachByteDesc(ByteProcessor byteProcessor) {
      return this.buf.forEachByteDesc(byteProcessor);
   }

   public static int getVarIntSize(int input) {
      for (int index = 1; index < 5; index++) {
         if ((input & -1 << index * 7) == 0) {
            return index;
         }
      }

      return 5;
   }

   @Override
   public int ensureWritable(int i, boolean b) {
      return this.buf.ensureWritable(i, b);
   }

   @Override
   public ByteBuf discardReadBytes() {
      return this.buf.discardReadBytes();
   }

   @Override
   public long getUnsignedIntLE(int i) {
      return this.buf.getUnsignedIntLE(i);
   }

   public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
      return enumClass.getEnumConstants()[this.readVarIntFromBuffer()];
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf) {
      return this.buf.writeBytes(byteBuf);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
      return this.buf.getBytes(i, byteBuffer);
   }

   public UUID readUuid() {
      return new UUID(this.readLong(), this.readLong());
   }

   @Override
   public long getLongLE(int i) {
      return this.buf.getLongLE(i);
   }

   public IChatComponent readChatComponent() throws IOException {
      return net.minecraft.util.IChatComponent.Serializer.jsonToComponent(this.readStringFromBuffer(32767));
   }

   @Override
   public ByteBuf order(ByteOrder byteOrder) {
      return this.buf.order(byteOrder);
   }

   @Override
   public ByteBuf writeBytes(byte[] bytes, int i, int i2) {
      return this.buf.writeBytes(bytes, i, i2);
   }

   @Override
   public int forEachByteDesc(int i, int i2, ByteProcessor byteProcessor) {
      return this.buf.forEachByteDesc(byteProcessor);
   }

   @Override
   public int readUnsignedShortLE() {
      return this.buf.readUnsignedShortLE();
   }

   public void writeVarLong(long value) {
      while ((value & -128L) != 0L) {
         this.writeByte((int)(value & 127L) | 128);
         value >>>= 7;
      }

      this.writeByte((int)value);
   }

   @Override
   public ByteBuf writeLongLE(long l) {
      return this.buf.writeLongLE(l);
   }

   @Override
   public ByteBuf copy(int i, int i2) {
      return this.buf.copy(i, i2);
   }

   public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
      int i = this.readerIndex();
      if (this.readByte() == 0) {
         return null;
      }

      this.readerIndex(i);

      try {
         return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
      } catch (IOException ioException) {
         throw new EncoderException(ioException);
      }
   }

   public BlockPos readBlockPos() {
      return BlockPos.fromLong(this.readLong());
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.buf.nioBuffers();
   }

   @Override
   public int getUnsignedShortLE(int i) {
      return this.buf.getUnsignedShortLE(i);
   }

   @Override
   public ByteBuf markReaderIndex() {
      return this.buf.markReaderIndex();
   }

   @Override
   public ByteBuf writeFloat(float v) {
      return this.buf.writeFloat(v);
   }

   @Override
   public int readUnsignedMedium() {
      return this.buf.readUnsignedMedium();
   }

   @Override
   public int getBytes(int i, FileChannel fileChannel, long l, int i2) throws IOException {
      return this.buf.getBytes(i, fileChannel, l, i2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
      return this.buf.writeBytes(scatteringByteChannel, i);
   }

   @Override
   public boolean release() {
      return this.buf.release();
   }

   @Override
   public double getDouble(int i) {
      return this.buf.getDouble(i);
   }

   static {
      handleString(null);
   }

   @Override
   public int setBytes(int i, FileChannel fileChannel, long l, int i2) throws IOException {
      return this.buf.setBytes(i, fileChannel, l, i2);
   }

   @Override
   public ByteBuf setZero(int i, int i2) {
      return this.buf.setZero(i, i2);
   }

   @Override
   public boolean isReadOnly() {
      return this.buf.isReadOnly();
   }

   @Override
   public CharSequence getCharSequence(int i, int i2, Charset charset) {
      return this.buf.getCharSequence(i, i2, charset);
   }

   @Override
   public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
      return this.buf.writeBytes(fileChannel, l, i);
   }

   @Override
   public ByteBuf getBytes(int i, OutputStream outputStream, int i2) throws IOException {
      return this.buf.getBytes(i, outputStream, i2);
   }

   @Override
   public ByteBuf retainedSlice(int i, int i2) {
      return this.buf.retainedSlice(i, i2);
   }

   @Override
   public int getMedium(int i) {
      return this.buf.getMedium(i);
   }

   @Override
   public ByteBuf setMedium(int i, int i2) {
      return this.buf.setMedium(i, i2);
   }

   @Override
   public boolean hasArray() {
      return this.buf.hasArray();
   }

   @Override
   public int getIntLE(int i) {
      return this.buf.getIntLE(i);
   }

   @Override
   public ByteBuf setBytes(int i, byte[] bytes) {
      return this.buf.setBytes(i, bytes);
   }

   public int getInt2(ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByteDesc(byteBufProcessor);
   }

   @Override
   public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
      return this.buf.readBytes(fileChannel, l, i);
   }

   public void writeBlockPos(BlockPos pos) {
      this.writeLong(pos.toLong());
   }

   @Override
   public int readMediumLE() {
      return this.buf.readMediumLE();
   }

   @Override
   public short readUnsignedByte() {
      return this.buf.readUnsignedByte();
   }

   public static String getText() {
      return text;
   }

   @Override
   public short readShortLE() {
      return this.buf.readShortLE();
   }

   @Override
   public long getLong(int i) {
      return this.buf.getLong(i);
   }

   @Override
   public int nioBufferCount() {
      return this.buf.nioBufferCount();
   }

   @Override
   public ByteBuf setBoolean(int i, boolean b) {
      return this.buf.setBoolean(i, b);
   }

   @Override
   public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int i2) throws IOException {
      return this.buf.setBytes(i, scatteringByteChannel, i2);
   }

   public void writeByteArray(byte[] array) {
      this.writeVarIntToBuffer(array.length);
      this.writeBytes(array);
   }

   @Override
   public double readDouble() {
      return this.buf.readDouble();
   }

   @Override
   public int forEachByte(int i, int i2, ByteProcessor byteProcessor) {
      return this.buf.forEachByte(i, i2, byteProcessor);
   }

   @Override
   public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
      return this.buf.readBytes(outputStream, i);
   }

   @Override
   public int indexOf(int i, int i2, byte b) {
      return this.buf.indexOf(i, i2, b);
   }

   @Override
   public ByteBuf setBytes(int i, byte[] bytes, int i2, int i3) {
      return this.buf.setBytes(i, bytes, i2, i3);
   }

   @Override
   public String toString(int i, int i2, Charset charset) {
      return this.buf.toString(i, i2, charset);
   }

   @Override
   public ByteBuf writeInt(int i) {
      return this.buf.writeInt(i);
   }

   @Override
   public boolean isWritable(int i) {
      return this.buf.isWritable(i);
   }

   @Override
   public char getChar(int i) {
      return this.buf.getChar(i);
   }

   @Override
   public float readFloat() {
      return this.buf.readFloat();
   }

   @Override
   public String toString() {
      return this.buf.toString();
   }

   @Override
   public CharSequence readCharSequence(int i, Charset charset) {
      return this.buf.readCharSequence(i, charset);
   }

   @Override
   public ByteBuf setIndex(int i, int i2) {
      return this.buf.setIndex(i, i2);
   }

   @Override
   public ByteBuf readRetainedSlice(int i) {
      return this.buf.readRetainedSlice(i);
   }

   @Override
   public int readInt() {
      return this.buf.readInt();
   }

   @Override
   public int maxCapacity() {
      return this.buf.maxCapacity();
   }

   @Override
   public long readUnsignedIntLE() {
      return this.buf.readUnsignedIntLE();
   }

   @Override
   public long readLongLE() {
      return this.buf.readLongLE();
   }

   @Override
   public ByteBuf copy() {
      return this.buf.copy();
   }

   @Override
   public int readerIndex() {
      return this.buf.readerIndex();
   }

   @Override
   public ByteBuf readerIndex(int i) {
      return this.buf.readerIndex(i);
   }

   @Override
   public int readIntLE() {
      return this.buf.readIntLE();
   }

   @Override
   public ByteBuf readSlice(int i) {
      return this.buf.readSlice(i);
   }

   @Override
   public boolean isReadable(int i) {
      return this.buf.isReadable(i);
   }

   @Override
   public int getUnsignedMedium(int i) {
      return this.buf.getUnsignedMedium(i);
   }

   @Override
   public int readUnsignedShort() {
      return this.buf.readUnsignedShort();
   }

   @Override
   public short getShortLE(int i) {
      return this.buf.getShortLE(i);
   }

   @Override
   public ByteBuf markWriterIndex() {
      return this.buf.markWriterIndex();
   }

   @Override
   public int writerIndex() {
      return this.buf.writerIndex();
   }

   @Override
   public ByteBuf setFloat(int i, float v) {
      return this.buf.setFloat(i, v);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
      return this.buf.writeBytes(byteBuf, i);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
      return this.buf.setBytes(i, byteBuffer);
   }

   @Override
   public byte[] array() {
      return this.buf.array();
   }

   @Override
   public ByteBuf setChar(int i, int i2) {
      return this.buf.setChar(i, i2);
   }

   @Override
   public long getUnsignedInt(int i) {
      return this.buf.getUnsignedInt(i);
   }

   @Override
   public ByteBuf retain() {
      return this.buf.retain();
   }

   @Override
   public ByteBuf readBytes(ByteBuffer byteBuffer) {
      return this.buf.readBytes(byteBuffer);
   }

   @Override
   public int getUnsignedShort(int i) {
      return this.buf.getUnsignedShort(i);
   }

   @Override
   public ByteBuf setLong(int i, long l) {
      return this.buf.setLong(i, l);
   }

   @Override
   public boolean isWritable() {
      return this.buf.isWritable();
   }

   @Override
   public char readChar() {
      return this.buf.readChar();
   }

   @Override
   public ByteBuf setDouble(int i, double v) {
      return this.buf.setDouble(i, v);
   }

   @Override
   public ByteBuf writeZero(int i) {
      return this.buf.writeZero(i);
   }

   @Override
   public ByteBuf writeBytes(byte[] bytes) {
      return this.buf.writeBytes(bytes);
   }

   @Override
   public ByteBuf resetReaderIndex() {
      return this.buf.resetReaderIndex();
   }

   @Override
   public ByteBuf touch() {
      return this.buf.touch();
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf) {
      return this.buf.getBytes(i, byteBuf);
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.buf.alloc();
   }

   public static void handleString(String text2) {
      text = text2;
   }

   @Override
   public short readShort() {
      return this.buf.readShort();
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf, int i2, int i3) {
      return this.buf.setBytes(i, byteBuf, i2, i3);
   }

   @Override
   public ByteBuf setIntLE(int i, int i2) {
      return this.buf.setIntLE(i, i2);
   }

   @Override
   public boolean equals(Object other) {
      return this.buf.equals(other);
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf, int i, int i2) {
      return this.buf.readBytes(byteBuf, i, i2);
   }

   @Override
   public short getShort(int i) {
      return this.buf.getShort(i);
   }

   @Override
   public ByteBuf writeLong(long l) {
      return this.buf.writeLong(l);
   }

   @Override
   public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int i2) throws IOException {
      return this.buf.getBytes(i, gatheringByteChannel, i2);
   }

   @Override
   public ByteBuf asReadOnly() {
      return this.buf.asReadOnly();
   }

   @Override
   public boolean readBoolean() {
      return this.buf.readBoolean();
   }

   public PacketBuffer writeString(String text) {
      byte[] byteArray = text.getBytes(Charsets.UTF_8);
      if (byteArray.length > 32767) {
         throw new EncoderException("String too big (was " + byteArray.length + " bytes encoded, max 32767)");
      }

      this.writeVarIntToBuffer(byteArray.length);
      this.writeBytes(byteArray);
      return this;
   }

   @Override
   public long readLong() {
      return this.buf.readLong();
   }

   @Override
   public ByteBuf setMediumLE(int i, int i2) {
      return this.buf.setMediumLE(i, i2);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer byteBuffer) {
      return this.buf.writeBytes(byteBuffer);
   }

   @Override
   public boolean hasMemoryAddress() {
      return this.buf.hasMemoryAddress();
   }

   public void writeVarIntToBuffer(int input) {
      while ((input & -128) != 0) {
         this.writeByte(input & 127 | 128);
         input >>>= 7;
      }

      this.writeByte(input);
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf) {
      return this.buf.readBytes(byteBuf);
   }

   @Override
   public ByteBuf writeChar(int i) {
      return this.buf.writeChar(i);
   }

   @Override
   public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
      return this.buf.readBytes(gatheringByteChannel, i);
   }

   @Override
   public short getUnsignedByte(int i) {
      return this.buf.getUnsignedByte(i);
   }

   @Override
   public long memoryAddress() {
      return this.buf.memoryAddress();
   }

   @Override
   public float getFloat(int i) {
      return this.buf.getFloat(i);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf) {
      return this.buf.setBytes(i, byteBuf);
   }

   @Override
   public ByteBuf setInt(int i, int i2) {
      return this.buf.setInt(i, i2);
   }

   @Override
   public int compareTo(ByteBuf byteBuf) {
      return this.buf.compareTo(byteBuf);
   }

   public PacketBuffer(ByteBuf wrapped) {
      this.buf = wrapped;
   }

   @Override
   public int writeBytes(InputStream inputStream, int i) throws IOException {
      return this.buf.writeBytes(inputStream, i);
   }

   @Override
   public int setBytes(int i, InputStream inputStream, int i2) throws IOException {
      return this.buf.setBytes(i, inputStream, i2);
   }

   @Override
   public ByteOrder order() {
      return this.buf.order();
   }

   public void writeNBTTagCompoundToBuffer(NBTTagCompound nbt) {
      if (nbt == null) {
         this.writeByte(0);
      } else {
         try {
            CompressedStreamTools.write(nbt, new ByteBufOutputStream(this));
         } catch (IOException ioException) {
            throw new EncoderException(ioException);
         }
      }
   }

   @Override
   public ByteBuf readBytes(byte[] bytes) {
      return this.buf.readBytes(bytes);
   }

   @Override
   public ByteBuffer nioBuffer() {
      return this.buf.nioBuffer();
   }

   @Override
   public int readUnsignedMediumLE() {
      return this.buf.readUnsignedMediumLE();
   }

   @Override
   public byte getByte(int i) {
      return this.buf.getByte(i);
   }

   @Override
   public ByteBuf writeMediumLE(int i) {
      return this.buf.writeMediumLE(i);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf, int i2, int i3) {
      return this.buf.getBytes(i, byteBuf, i2, i3);
   }

   @Override
   public int readMedium() {
      return this.buf.readMedium();
   }

   @Override
   public int readableBytes() {
      return this.buf.readableBytes();
   }

   @Override
   public ByteBuf writerIndex(int i) {
      return this.buf.writerIndex(i);
   }

   @Override
   public boolean release(int i) {
      return this.buf.release(i);
   }

   @Override
   public int bytesBefore(byte b) {
      return this.buf.bytesBefore(b);
   }

   @Override
   public ByteBuf getBytes(int i, byte[] bytes, int i2, int i3) {
      return this.buf.getBytes(i, bytes, i2, i3);
   }

   public byte[] readByteArray() {
      byte[] byteArray = new byte[this.readVarIntFromBuffer()];
      this.readBytes(byteArray);
      return byteArray;
   }

   @Override
   public int bytesBefore(int i, byte b) {
      return this.buf.bytesBefore(i, b);
   }

   @Override
   public ByteBuf writeByte(int i) {
      return this.buf.writeByte(i);
   }

   @Override
   public ByteBuf setShort(int i, int i2) {
      return this.buf.setShort(i, i2);
   }
}
