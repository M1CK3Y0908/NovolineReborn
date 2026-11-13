package wtf.novoline.script;

import java.util.HashMap;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import wtf.novoline.adapter.C0APacketAnimationTypeAdapter;
import wtf.novoline.script.adapter.C00PacketKeepAliveTypeAdapter;
import wtf.novoline.script.adapter.C01PacketChatMessageTypeAdapter;
import wtf.novoline.script.adapter.C02PacketUseEntityTypeAdapter;
import wtf.novoline.script.adapter.C03PacketPlayerTypeAdapter;
import wtf.novoline.script.adapter.C07PacketPlayerDiggingTypeAdapter;
import wtf.novoline.script.adapter.C08PacketPlayerBlockPlacementTypeAdapter;
import wtf.novoline.script.adapter.C09PacketHeldItemChangeTypeAdapter;
import wtf.novoline.script.adapter.C0BPacketEntityActionTypeAdapter;
import wtf.novoline.script.adapter.C0CPacketInputTypeAdapter;
import wtf.novoline.script.adapter.C0DPacketCloseWindowTypeAdapter;
import wtf.novoline.script.adapter.C0EPacketClickWindowTypeAdapter;
import wtf.novoline.script.adapter.C0FPacketConfirmTransactionTypeAdapter;
import wtf.novoline.script.adapter.C10PacketCreativeInventoryActionTypeAdapter;
import wtf.novoline.script.adapter.C11PacketEnchantItemTypeAdapter;
import wtf.novoline.script.adapter.C12PacketUpdateSignTypeAdapter;
import wtf.novoline.script.adapter.C13PacketPlayerAbilitiesTypeAdapter;
import wtf.novoline.script.adapter.C14PacketTabCompleteTypeAdapter;
import wtf.novoline.script.adapter.C15PacketClientSettingsTypeAdapter;
import wtf.novoline.script.adapter.C16PacketClientStatusTypeAdapter;
import wtf.novoline.script.adapter.C17PacketCustomPayloadTypeAdapter;
import wtf.novoline.script.adapter.C18PacketSpectateTypeAdapter;
import wtf.novoline.script.adapter.C19PacketResourcePackStatusTypeAdapter;
import wtf.novoline.script.adapter.S00PacketKeepAliveTypeAdapter;
import wtf.novoline.script.adapter.S02PacketChatTypeAdapter;
import wtf.novoline.script.adapter.S08PacketPlayerPosLookTypeAdapter;
import wtf.novoline.script.adapter.S32PacketConfirmTransactionTypeAdapter;
import wtf.novoline.script.adapter.C03PacketPlayerTypeAdapter.C04PacketPlayerPositionTypeAdapter;
import wtf.novoline.script.adapter.C03PacketPlayerTypeAdapter.C05PacketPlayerLookTypeAdapter;
import wtf.novoline.script.adapter.C03PacketPlayerTypeAdapter.C06PacketPlayerPosLookTypeAdapter;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class PacketAdapterRegistry extends HashMap<PacketAdapterRegistry.PacketKey, AbstractTypeAdapter<? extends Packet<?>>> {
   private void register(TypeRegistry typeRegistry, PacketAdapterRegistry.PacketKey packetKey, Class clazz, AbstractTypeAdapter abstractTypeAdapter) {
      this.put(packetKey, abstractTypeAdapter);
      typeRegistry.put(clazz, abstractTypeAdapter);
   }

   public PacketAdapterRegistry(TypeRegistry typeRegistry) {
      this.register(typeRegistry, createKey(0, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C00PacketKeepAlive.class, new C00PacketKeepAliveTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(1, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C01PacketChatMessage.class, new C01PacketChatMessageTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(2, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C02PacketUseEntity.class, new C02PacketUseEntityTypeAdapter(typeRegistry));
      this.register(typeRegistry, createKey(3, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C03PacketPlayer.class, new C03PacketPlayerTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(4, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C04PacketPlayerPosition.class, new C04PacketPlayerPositionTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(5, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C05PacketPlayerLook.class, new C05PacketPlayerLookTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(6, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C06PacketPlayerPosLook.class, new C06PacketPlayerPosLookTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(7, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C07PacketPlayerDigging.class, new C07PacketPlayerDiggingTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry,
         createKey(8, HandshakingType.PLAY, ClientboundType.SERVERBOUND),
         C08PacketPlayerBlockPlacement.class,
         new C08PacketPlayerBlockPlacementTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(9, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C09PacketHeldItemChange.class, new C09PacketHeldItemChangeTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(10, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C0APacketAnimation.class, new C0APacketAnimationTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(11, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C0BPacketEntityAction.class, new C0BPacketEntityActionTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(12, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C0CPacketInput.class, new C0CPacketInputTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(13, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C0DPacketCloseWindow.class, new C0DPacketCloseWindowTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(14, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C0EPacketClickWindow.class, new C0EPacketClickWindowTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry,
         createKey(15, HandshakingType.PLAY, ClientboundType.SERVERBOUND),
         C0FPacketConfirmTransaction.class,
         new C0FPacketConfirmTransactionTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry,
         createKey(16, HandshakingType.PLAY, ClientboundType.SERVERBOUND),
         C10PacketCreativeInventoryAction.class,
         new C10PacketCreativeInventoryActionTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(17, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C11PacketEnchantItem.class, new C11PacketEnchantItemTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(18, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C12PacketUpdateSign.class, new C12PacketUpdateSignTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(19, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C13PacketPlayerAbilities.class, new C13PacketPlayerAbilitiesTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(20, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C14PacketTabComplete.class, new C14PacketTabCompleteTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(21, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C15PacketClientSettings.class, new C15PacketClientSettingsTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(22, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C16PacketClientStatus.class, new C16PacketClientStatusTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry, createKey(23, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C17PacketCustomPayload.class, new C17PacketCustomPayloadTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(24, HandshakingType.PLAY, ClientboundType.SERVERBOUND), C18PacketSpectate.class, new C18PacketSpectateTypeAdapter(typeRegistry));
      this.register(
         typeRegistry,
         createKey(25, HandshakingType.PLAY, ClientboundType.SERVERBOUND),
         C19PacketResourcePackStatus.class,
         new C19PacketResourcePackStatusTypeAdapter(typeRegistry)
      );
      this.register(typeRegistry, createKey(0, HandshakingType.PLAY, ClientboundType.CLIENTBOUND), S00PacketKeepAlive.class, new S00PacketKeepAliveTypeAdapter(typeRegistry));
      this.register(typeRegistry, createKey(2, HandshakingType.PLAY, ClientboundType.CLIENTBOUND), S02PacketChat.class, new S02PacketChatTypeAdapter(typeRegistry));
      this.register(
         typeRegistry, createKey(8, HandshakingType.PLAY, ClientboundType.CLIENTBOUND), S08PacketPlayerPosLook.class, new S08PacketPlayerPosLookTypeAdapter(typeRegistry)
      );
      this.register(
         typeRegistry,
         createKey(50, HandshakingType.PLAY, ClientboundType.CLIENTBOUND),
         S32PacketConfirmTransaction.class,
         new S32PacketConfirmTransactionTypeAdapter(typeRegistry)
      );
   }

   private static PacketAdapterRegistry.PacketKey createKey(int i, HandshakingType handshakingType2, ClientboundType clientboundType2) {
      return new PacketAdapterRegistry.PacketKey(i, handshakingType2, clientboundType2);
   }

   public record PacketKey(int packetId, HandshakingType handshakingType, ClientboundType clientboundType) {
      public ClientboundType getClientboundType() {
         return this.clientboundType;
      }

      public int getPacketId() {
         return this.packetId;
      }

      public HandshakingType getHandshakingType() {
         return this.handshakingType;
      }
   }
}
