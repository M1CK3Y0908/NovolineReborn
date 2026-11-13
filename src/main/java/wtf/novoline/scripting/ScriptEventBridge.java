package wtf.novoline.scripting;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.novoline.event.BlockCollisionEvent;
import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.event.Event;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.GameStartEvent;
import wtf.novoline.event.GamemodeUpdateEvent;
import wtf.novoline.event.JumpEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.event.MoveEntityEvent2;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderBlockEvent;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.event.ServerConnectEvent;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.event.SlowdownEvent;
import wtf.novoline.event.SpawnParticleEvent;
import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.scripting.ScriptEventHandler.HelperChild;

public class ScriptEventBridge {
   @EventLink
   private final Listener<BlockCollisionEvent> onBlockCollision = this::dispatch;
   @EventLink
   private final Listener<DeviceButtonDownEvent> onDeviceButtonDown = this::dispatch;
   @EventLink
   private final Listener<GameEndEvent> onGameEnd = this::dispatch;
   @EventLink
   private final Listener<GamemodeUpdateEvent> onGamemodeUpdate = this::dispatch;
   @EventLink
   private final Listener<GameStartEvent> onGameStart = this::dispatch;
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = this::dispatch;
   @EventLink
   private final Listener<JumpEvent> onJump = this::dispatch;
   @EventLink
   private final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = this::dispatch;
   @EventLink
   private final Listener<MoveEntityEvent> onMoveEntity = this::dispatch;
   @EventLink
   private final Listener<MoveEntityEvent2> onMoveEntityEvent2 = this::dispatch;
   @EventLink
   private final Listener<RunTickEvent> onRunTick = this::dispatch;
   @EventLink
   private final Listener<UpdateEvent> onUpdate = this::dispatch;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = this::dispatch;
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = this::dispatch;
   @EventLink
   private final Listener<SpawnParticleEvent> onSpawnParticle = this::dispatch;
   @EventLink
   private final Listener<PlayerKillEvent> onPlayerKill = this::dispatch;
   @EventLink
   private final Listener<PlayerMoveEvent> onPlayerMove = this::dispatch;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = this::dispatch;
   @EventLink
   private final Listener<RenderBlockEvent> onRenderBlock = this::dispatch;
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen = this::dispatch;
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = this::dispatch;
   @EventLink
   private final Listener<ServerConnectEvent> onServerConnect = this::dispatch;
   @EventLink
   private final Listener<ServerDisconnectEvent> onServerDisconnect = this::dispatch;
   @EventLink
   private final Listener<SlowdownEvent> onSlowdown = this::dispatch;
   @EventLink
   private final Listener<SyncCurrentSlotEvent> onSyncCurrentSlot = this::dispatch;
   private final ScriptEventDispatcher scriptEventDispatcher;
   private final ScriptEventHandlerList scriptEventHandlerList;

   public ScriptEventBridge(ScriptEventDispatcher scriptEventDispatcher2, ScriptEventHandlerList scriptEventHandlerList2) {
      this.scriptEventDispatcher = scriptEventDispatcher2;
      this.scriptEventHandlerList = scriptEventHandlerList2;
   }

   private void dispatch(Event event2) {
      for (ScriptEventHandler scriptEventHandler : this.scriptEventHandlerList
         .sortByPriority(this.scriptEventHandlerList.findAllEnabledByEvent(HelperChild.forClass(event2.getClass())))) {
         this.scriptEventDispatcher.dispatchEvent(scriptEventHandler, event2);
         if (event2.isCancelled()) {
            break;
         }
      }
   }
}
