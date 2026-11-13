package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface INetHandlerPlayServer extends INetHandler {
   void processConfirmTransaction(C0FPacketConfirmTransaction c0FPacketConfirmTransaction);

   void processVanilla250Packet(C17PacketCustomPayload c17PacketCustomPayload);

   void processChatMessage(C01PacketChatMessage c01PacketChatMessage);

   void processKeepAlive(C00PacketKeepAlive c00PacketKeepAlive);

   void processUpdateSign(C12PacketUpdateSign c12PacketUpdateSign);

   void processCreativeInventoryAction(C10PacketCreativeInventoryAction c10PacketCreativeInventoryAction);

   void processHeldItemChange(C09PacketHeldItemChange c09PacketHeldItemChange);

   void handleSpectate(C18PacketSpectate c18PacketSpectate);

   void processCloseWindow(C0DPacketCloseWindow c0DPacketCloseWindow);

   void processPlayer(C03PacketPlayer c03PacketPlayer);

   void processClientStatus(C16PacketClientStatus c16PacketClientStatus);

   void processEntityAction(C0BPacketEntityAction c0BPacketEntityAction);

   void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement);

   void processEnchantItem(C11PacketEnchantItem c11PacketEnchantItem);

   void processUseEntity(C02PacketUseEntity c02PacketUseEntity);

   void processInput(C0CPacketInput c0CPacketInput);

   void processClientSettings(C15PacketClientSettings c15PacketClientSettings);

   void handleResourcePackStatus(C19PacketResourcePackStatus c19PacketResourcePackStatus);

   void processPlayerDigging(C07PacketPlayerDigging c07PacketPlayerDigging);

   void processTabComplete(C14PacketTabComplete c14PacketTabComplete);

   void processClickWindow(C0EPacketClickWindow c0EPacketClickWindow);

   void processPlayerAbilities(C13PacketPlayerAbilities c13PacketPlayerAbilities);

   void handleAnimation(C0APacketAnimation c0APacketAnimation);
}
