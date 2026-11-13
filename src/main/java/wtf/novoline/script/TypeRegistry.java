package wtf.novoline.script;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import wtf.novoline.event.BlockCollisionEvent;
import wtf.novoline.event.DeviceButtonDownEvent;
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
import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.Module;
import wtf.novoline.script.adapter.AnimationTypeAdapter;
import wtf.novoline.script.adapter.AxisAlignedBBTypeAdapter;
import wtf.novoline.script.adapter.BlockCollisionEventTypeAdapter;
import wtf.novoline.script.adapter.BlockPosTypeAdapter;
import wtf.novoline.script.adapter.BlockTypeAdapter;
import wtf.novoline.script.adapter.CFontRendererTypeAdapter;
import wtf.novoline.script.adapter.EntityTypeAdapter;
import wtf.novoline.script.adapter.EnumFacingTypeAdapter;
import wtf.novoline.script.adapter.EventTypeAdapter10;
import wtf.novoline.script.adapter.EventTypeAdapter11;
import wtf.novoline.script.adapter.EventTypeAdapter2;
import wtf.novoline.script.adapter.EventTypeAdapter3;
import wtf.novoline.script.adapter.EventTypeAdapter4;
import wtf.novoline.script.adapter.EventTypeAdapter5;
import wtf.novoline.script.adapter.EventTypeAdapter7;
import wtf.novoline.script.adapter.EventTypeAdapter8;
import wtf.novoline.script.adapter.EventTypeAdapter9;
import wtf.novoline.script.adapter.FontRendererTypeAdapter;
import wtf.novoline.script.adapter.FramebufferTypeAdapter;
import wtf.novoline.script.adapter.GamemodeUpdateEventTypeAdapter;
import wtf.novoline.script.adapter.IBlockStateTypeAdapter;
import wtf.novoline.script.adapter.IChatComponentTypeAdapter;
import wtf.novoline.script.adapter.ItemStackTypeAdapter;
import wtf.novoline.script.adapter.MinecraftTypeAdapter;
import wtf.novoline.script.adapter.ModuleTypeAdapter;
import wtf.novoline.script.adapter.MovementInputTypeAdapter;
import wtf.novoline.script.adapter.NotificationTypeAdapter;
import wtf.novoline.script.adapter.PacketSendEventTypeAdapter;
import wtf.novoline.script.adapter.PlayerCapabilitiesTypeAdapter;
import wtf.novoline.script.adapter.PlayerMoveEventTypeAdapter;
import wtf.novoline.script.adapter.PolygonRendererTypeAdapter;
import wtf.novoline.script.adapter.RectRendererTypeAdapter;
import wtf.novoline.script.adapter.RenderWorldEventTypeAdapter;
import wtf.novoline.script.adapter.ScaledResolutionTypeAdapter;
import wtf.novoline.script.adapter.ServerDataTypeAdapter;
import wtf.novoline.script.adapter.ServerDisconnectEventTypeAdapter;
import wtf.novoline.script.adapter.SettingTypeAdapter;
import wtf.novoline.script.adapter.SlowdownEventTypeAdapter;
import wtf.novoline.script.adapter.SyncCurrentSlotEventTypeAdapter;
import wtf.novoline.script.adapter.TimerTypeAdapter;
import wtf.novoline.script.adapter.Vec3TypeAdapter;
import wtf.novoline.script.adapter.Vec3iTypeAdapter;
import wtf.novoline.script.adapter.WorldClientTypeAdapter;
import wtf.novoline.setting.Setting;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child2;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child3;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child4;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child5;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child6;
import wtf.novoline.util.EventTypeAdapters.EventTypeAdapter12Child7;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.PolygonRenderer;
import wtf.novoline.utils.render.shape.RectRenderer;

public class TypeRegistry extends UnifiedMap<Class<?>, AbstractTypeAdapter<?>> {
   private final TypeStore typeStore = TypeStore.getTypeStore();
   private final PacketAdapterRegistry packetAdapterRegistry = new PacketAdapterRegistry(this);

   public PacketAdapterRegistry getPacketAdapterRegistry() {
      return this.packetAdapterRegistry;
   }

   public static TypeRegistry getTypeRegistry() {
      return TypeRegistry.InstanceType.INSTANCE.typeRegistry;
   }

   public TypeRegistry() {
      this.put(Module.class, new ModuleTypeAdapter(this));
      this.put(Setting.class, new SettingTypeAdapter(this));
      this.put(CFontRenderer.class, new CFontRendererTypeAdapter(this));
      this.put(Notification.class, new NotificationTypeAdapter(this));
      this.put(PolygonRenderer.class, new PolygonRendererTypeAdapter(this));
      this.put(RectRenderer.class, new RectRendererTypeAdapter(this));
      this.put(Animation.class, new AnimationTypeAdapter(this));
      this.put(BlockCollisionEvent.class, new BlockCollisionEventTypeAdapter(this));
      this.put(DeviceButtonDownEvent.class, new EventTypeAdapter8(this));
      this.put(GameEndEvent.class, new EventTypeAdapter11(this));
      this.put(GamemodeUpdateEvent.class, new GamemodeUpdateEventTypeAdapter(this));
      this.put(GameStartEvent.class, new EventTypeAdapter2(this));
      this.put(LoadWorldEvent.class, new EventTypeAdapter12Child(this));
      this.put(JumpEvent.class, new EventTypeAdapter12Child2(this));
      this.put(OnUpdateWalkingPlayerEvent.class, new EventTypeAdapter12Child3(this));
      this.put(UpdateEvent.class, new EventTypeAdapter12Child4(this));
      this.put(RunTickEvent.class, new EventTypeAdapter12Child5(this));
      this.put(MoveEntityEvent.class, new EventTypeAdapter12Child6(this));
      this.put(MoveEntityEvent2.class, new EventTypeAdapter12Child7(this));
      this.put(PacketSendEvent.class, new PacketSendEventTypeAdapter(this));
      this.put(PacketReceiveEvent.class, new EventTypeAdapter5(this));
      this.put(PlayerKillEvent.class, new EventTypeAdapter9(this));
      this.put(PlayerMoveEvent.class, new PlayerMoveEventTypeAdapter(this));
      this.put(PreUpdateEvent.class, new EventTypeAdapter7(this));
      this.put(RenderBlockEvent.class, new EventTypeAdapter3(this));
      this.put(RenderScreenEvent.class, new EventTypeAdapter10(this));
      this.put(RenderWorldEvent.class, new RenderWorldEventTypeAdapter(this));
      this.put(ServerConnectEvent.class, new EventTypeAdapter4(this));
      this.put(ServerDisconnectEvent.class, new ServerDisconnectEventTypeAdapter(this));
      this.put(SlowdownEvent.class, new SlowdownEventTypeAdapter(this));
      this.put(SyncCurrentSlotEvent.class, new SyncCurrentSlotEventTypeAdapter(this));
      this.put(Vec3.class, new Vec3TypeAdapter(this));
      this.put(Vec3i.class, new Vec3iTypeAdapter(this));
      this.put(BlockPos.class, new BlockPosTypeAdapter(this));
      this.put(EnumFacing.class, new EnumFacingTypeAdapter(this));
      this.put(ItemStack.class, new ItemStackTypeAdapter(this));
      this.put(IChatComponent.class, new IChatComponentTypeAdapter(this));
      this.put(IBlockState.class, new IBlockStateTypeAdapter(this));
      this.put(Block.class, new BlockTypeAdapter(this));
      this.put(Entity.class, new EntityTypeAdapter(this));
      this.put(AxisAlignedBB.class, new AxisAlignedBBTypeAdapter(this));
      this.put(PlayerCapabilities.class, new PlayerCapabilitiesTypeAdapter(this));
      this.put(Minecraft.class, new MinecraftTypeAdapter(this));
      this.put(WorldClient.class, new WorldClientTypeAdapter(this));
      this.put(FontRenderer.class, new FontRendererTypeAdapter(this));
      this.put(Timer.class, new TimerTypeAdapter(this));
      this.put(ServerData.class, new ServerDataTypeAdapter(this));
      this.put(ScaledResolution.class, new ScaledResolutionTypeAdapter(this));
      this.put(MovementInput.class, new MovementInputTypeAdapter(this));
      this.put(Framebuffer.class, new FramebufferTypeAdapter(this));
   }

   public AbstractTypeAdapter put(Class clazz, AbstractTypeAdapter abstractTypeAdapter) {
      ScriptType scriptType = ScriptType.createScriptType(abstractTypeAdapter.getTypeName(), clazz);
      if (!this.typeStore.isRegistered(scriptType.getName())) {
         this.typeStore.add(scriptType);
      }

      for (ScriptType scriptType2 : (Iterable<ScriptType>) abstractTypeAdapter.getSubTypes()) {
         scriptType2.setParentName(scriptType.getName());
         this.typeStore.add(scriptType2);
      }

      super.put(clazz, abstractTypeAdapter);
      abstractTypeAdapter.registerSubAdapters();
      return abstractTypeAdapter;
   }

   public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) {
      return (TypeAdapter<T>)this.get(clazz);
   }

   public TypeStore getTypeStore() {
      return this.typeStore;
   }

   enum InstanceType {
      INSTANCE;

      private final TypeRegistry typeRegistry = new TypeRegistry();
   }
}
