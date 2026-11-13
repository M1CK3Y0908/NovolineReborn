package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public interface INetHandlerPlayClient extends INetHandler {
   void handleTabComplete(S3APacketTabComplete s3APacketTabComplete);

   void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb s11PacketSpawnExperienceOrb);

   void handleSpawnPainting(S10PacketSpawnPainting s10PacketSpawnPainting);

   void handleDisplayScoreboard(S3DPacketDisplayScoreboard s3DPacketDisplayScoreboard);

   void handleEntityMetadata(S1CPacketEntityMetadata s1CPacketEntityMetadata);

   void handleCloseWindow(S2EPacketCloseWindow s2EPacketCloseWindow);

   void handleResourcePack(S48PacketResourcePackSend s48PacketResourcePackSend);

   void handleJoinGame(S01PacketJoinGame s01PacketJoinGame);

   void handleOpenWindow(S2DPacketOpenWindow s2DPacketOpenWindow);

   void handleDisconnect(S40PacketDisconnect s40PacketDisconnect);

   void handleEntityAttach(S1BPacketEntityAttach s1BPacketEntityAttach);

   void handleHeldItemChange(S09PacketHeldItemChange s09PacketHeldItemChange);

   void handleMultiBlockChange(S22PacketMultiBlockChange s22PacketMultiBlockChange);

   void handleEntityStatus(S19PacketEntityStatus s19PacketEntityStatus);

   void handleEntityTeleport(S18PacketEntityTeleport s18PacketEntityTeleport);

   void handleCollectItem(S0DPacketCollectItem s0DPacketCollectItem);

   void handleSpawnPosition(S05PacketSpawnPosition s05PacketSpawnPosition);

   void handleTeams(S3EPacketTeams s3EPacketTeams);

   void handleSpawnObject(S0EPacketSpawnObject s0EPacketSpawnObject);

   void handleDestroyEntities(S13PacketDestroyEntities s13PacketDestroyEntities);

   void handleEntityMovement(S14PacketEntity s14PacketEntity);

   void handleWindowProperty(S31PacketWindowProperty s31PacketWindowProperty);

   void handleEntityEquipment(S04PacketEntityEquipment s04PacketEntityEquipment);

   void handleKeepAlive(S00PacketKeepAlive s00PacketKeepAlive);

   void handleWindowItems(S30PacketWindowItems s30PacketWindowItems);

   void handleUpdateSign(S33PacketUpdateSign s33PacketUpdateSign);

   void handleEntityVelocity(S12PacketEntityVelocity s12PacketEntityVelocity);

   void handleCustomPayload(S3FPacketCustomPayload s3FPacketCustomPayload);

   void handleEntityHeadLook(S19PacketEntityHeadLook s19PacketEntityHeadLook);

   void handleEntityEffect(S1DPacketEntityEffect s1DPacketEntityEffect);

   void handleEntityNBT(S49PacketUpdateEntityNBT s49PacketUpdateEntityNBT);

   void handleUpdateScore(S3CPacketUpdateScore s3CPacketUpdateScore);

   void handleScoreboardObjective(S3BPacketScoreboardObjective s3BPacketScoreboardObjective);

   void handleBlockBreakAnim(S25PacketBlockBreakAnim s25PacketBlockBreakAnim);

   void handleUpdateHealth(S06PacketUpdateHealth s06PacketUpdateHealth);

   void handleSetSlot(S2FPacketSetSlot s2FPacketSetSlot);

   void handleAnimation(S0BPacketAnimation s0BPacketAnimation);

   void handleParticles(S2APacketParticles s2APacketParticles);

   void handlePlayerListItem(S38PacketPlayerListItem s38PacketPlayerListItem);

   void handleSpawnMob(S0FPacketSpawnMob s0FPacketSpawnMob);

   void handleExplosion(S27PacketExplosion s27PacketExplosion);

   void handleWorldBorder(S44PacketWorldBorder s44PacketWorldBorder);

   void handleTitle(S45PacketTitle s45PacketTitle);

   void handlePlayerPosLook(S08PacketPlayerPosLook s08PacketPlayerPosLook);

   void handleRespawn(S07PacketRespawn s07PacketRespawn);

   void handleConfirmTransaction(S32PacketConfirmTransaction s32PacketConfirmTransaction);

   void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter s47PacketPlayerListHeaderFooter);

   void handleCombatEvent(S42PacketCombatEvent s42PacketCombatEvent);

   void handleSoundEffect(S29PacketSoundEffect s29PacketSoundEffect);

   void handleChat(S02PacketChat s02PacketChat);

   void handleCamera(S43PacketCamera s43PacketCamera);

   void handleBlockAction(S24PacketBlockAction s24PacketBlockAction);

   void handleSignEditorOpen(S36PacketSignEditorOpen s36PacketSignEditorOpen);

   void handleBlockChange(S23PacketBlockChange s23PacketBlockChange);

   void handleChunkData(S21PacketChunkData s21PacketChunkData);

   void handleChangeGameState(S2BPacketChangeGameState s2BPacketChangeGameState);

   void handleSetCompressionLevel(S46PacketSetCompressionLevel s46PacketSetCompressionLevel);

   void handleStatistics(S37PacketStatistics s37PacketStatistics);

   void handleEffect(S28PacketEffect s28PacketEffect);

   void handleMapChunkBulk(S26PacketMapChunkBulk s26PacketMapChunkBulk);

   void handleSetExperience(S1FPacketSetExperience s1FPacketSetExperience);

   void handleTimeUpdate(S03PacketTimeUpdate s03PacketTimeUpdate);

   void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity s2CPacketSpawnGlobalEntity);

   void handleSpawnPlayer(S0CPacketSpawnPlayer s0CPacketSpawnPlayer);

   void handleUpdateTileEntity(S35PacketUpdateTileEntity s35PacketUpdateTileEntity);

   void handlePlayerAbilities(S39PacketPlayerAbilities s39PacketPlayerAbilities);

   void handleEntityProperties(S20PacketEntityProperties s20PacketEntityProperties);

   void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect s1EPacketRemoveEntityEffect);

   void handleMaps(S34PacketMaps s34PacketMaps);

   void handleServerDifficulty(S41PacketServerDifficulty s41PacketServerDifficulty);

   void handleUseBed(S0APacketUseBed s0APacketUseBed);
}
