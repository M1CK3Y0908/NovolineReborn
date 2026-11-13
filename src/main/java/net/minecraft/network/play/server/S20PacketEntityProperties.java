package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private final List<S20PacketEntityProperties.Snapshot> snapshots = Lists.newArrayList();

   public S20PacketEntityProperties() {
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      int intValue = buf.readInt();

      for (int innerIndex = 0; innerIndex < intValue; innerIndex++) {
         String text = buf.readStringFromBuffer(64);
         double doubleValue = buf.readDouble();
         List<AttributeModifier> list = Lists.newArrayList();
         int varIntFromBuffer = buf.readVarIntFromBuffer();

         for (int outerIndex = 0; outerIndex < varIntFromBuffer; outerIndex++) {
            UUID uUID = buf.readUuid();
            list.add(new AttributeModifier(uUID, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
         }

         this.snapshots.add(new S20PacketEntityProperties.Snapshot(text, doubleValue, list));
      }
   }

   public S20PacketEntityProperties(int entityIdIn, Collection<IAttributeInstance> instances) {
      this.entityId = entityIdIn;

      for (IAttributeInstance iAttributeInstance : instances) {
         this.snapshots
            .add(new S20PacketEntityProperties.Snapshot(iAttributeInstance.getAttribute().getAttributeUnlocalizedName(), iAttributeInstance.getBaseValue(), iAttributeInstance.getModifiers()));
      }
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<S20PacketEntityProperties.Snapshot> getSnapshots() {
      return this.snapshots;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityProperties(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeInt(this.snapshots.size());

      for (S20PacketEntityProperties.Snapshot snapshot : this.snapshots) {
         buf.writeString(snapshot.getName());
         buf.writeDouble(snapshot.getBaseValue());
         buf.writeVarIntToBuffer(snapshot.getModifiers().size());

         for (AttributeModifier attributeModifier : snapshot.getModifiers()) {
            buf.writeUuid(attributeModifier.getID());
            buf.writeDouble(attributeModifier.getAmount());
            buf.writeByte(attributeModifier.getOperation());
         }
      }
   }

   public class Snapshot {
      private final String name;
      private final double baseValue;
      private final Collection<AttributeModifier> modifiers;

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }

      public Snapshot(String nameIn, double baseValueIn, Collection<AttributeModifier> modifiersIn) {
         this.name = nameIn;
         this.baseValue = baseValueIn;
         this.modifiers = modifiersIn;
      }

      public String getName() {
         return this.name;
      }

      public double getBaseValue() {
         return this.baseValue;
      }
   }
}
