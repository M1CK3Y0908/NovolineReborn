package wtf.novoline.script.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C03PacketPlayerTypeAdapter extends PacketTypeAdapter<C03PacketPlayer> {
   public C03PacketPlayerTypeAdapter(TypeRegistry typeRegistry2) {
      super(3, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C03PacketPlayer", typeRegistry2);
      this.addSubTypes(
         ScriptType.createScriptType("C04PacketPlayerPosition", C04PacketPlayerPosition.class),
         ScriptType.createScriptType("C05PacketPlayerLook", C05PacketPlayerLook.class),
         ScriptType.createScriptType("C06PacketPlayerPosLook", C06PacketPlayerPosLook.class)
      );
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(C04PacketPlayerPosition.class, new C03PacketPlayerTypeAdapter.C04PacketPlayerPositionTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(C05PacketPlayerLook.class, new C03PacketPlayerTypeAdapter.C05PacketPlayerLookTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(C06PacketPlayerPosLook.class, new C03PacketPlayerTypeAdapter.C06PacketPlayerPosLookTypeAdapter(this.typeRegistry));
   }

   public C03PacketPlayer unpack(Map map) {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      return scriptValue.containsKey("onGround") ? new C03PacketPlayer(scriptValue.checkString("onGround")) : new C03PacketPlayer();
   }

   protected void putPacketFields(ScriptValue scriptValue, C03PacketPlayer c03PacketPlayer) {
      scriptValue.put("x", c03PacketPlayer.x);
      scriptValue.put("y", c03PacketPlayer.y);
      scriptValue.put("z", c03PacketPlayer.z);
      scriptValue.put("yaw", c03PacketPlayer.yaw);
      scriptValue.put("pitch", c03PacketPlayer.pitch);
      scriptValue.put("onGround", c03PacketPlayer.onGround);
      scriptValue.put("moving", c03PacketPlayer.moving);
      scriptValue.put("rotating", c03PacketPlayer.rotating);
   }

   public C03PacketPlayer apply(ScriptValue scriptValue, C03PacketPlayer c03PacketPlayer) {
      c03PacketPlayer.x = scriptValue.getFloat("x");
      c03PacketPlayer.y = scriptValue.getFloat("y");
      c03PacketPlayer.z = scriptValue.getFloat("z");
      c03PacketPlayer.yaw = scriptValue.getFloat("yaw");
      c03PacketPlayer.pitch = scriptValue.getFloat("pitch");
      c03PacketPlayer.onGround = scriptValue.checkString("onGround");
      c03PacketPlayer.moving = scriptValue.checkString("moving");
      c03PacketPlayer.rotating = scriptValue.checkString("rotating");
      return c03PacketPlayer;
   }

   public static final class C04PacketPlayerPositionTypeAdapter extends PacketTypeAdapter<C04PacketPlayerPosition> {
      public C04PacketPlayerPosition unpack(Map map) {
         ScriptValue scriptValue = ScriptValue.createScriptValue(map);
         return scriptValue.containsKey("x")
            ? new C04PacketPlayerPosition(scriptValue.getFloat("x"), scriptValue.getFloat("y"), scriptValue.getFloat("z"), scriptValue.checkString("onGround"))
            : new C04PacketPlayerPosition();
      }

      public C04PacketPlayerPositionTypeAdapter(TypeRegistry typeRegistry2) {
         super(4, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C04PacketPlayerPosition", typeRegistry2);
      }
   }

   public static final class C05PacketPlayerLookTypeAdapter extends PacketTypeAdapter<C05PacketPlayerLook> {
      public C05PacketPlayerLook unpack(Map map) {
         ScriptValue scriptValue = ScriptValue.createScriptValue(map);
         return scriptValue.containsKey("yaw")
            ? new C05PacketPlayerLook(scriptValue.getFloat("yaw"), scriptValue.getFloat("pitch"), scriptValue.checkString("onGround"))
            : new C05PacketPlayerLook();
      }

      public C05PacketPlayerLookTypeAdapter(TypeRegistry typeRegistry2) {
         super(5, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C05PacketPlayerLook", typeRegistry2);
      }
   }

   public static final class C06PacketPlayerPosLookTypeAdapter extends PacketTypeAdapter<C06PacketPlayerPosLook> {
      public C06PacketPlayerPosLook unpack(Map map) {
         ScriptValue scriptValue = ScriptValue.createScriptValue(map);
         return scriptValue.containsKey("x")
            ? new C06PacketPlayerPosLook(
               scriptValue.getFloat("x"), scriptValue.getFloat("y"), scriptValue.getFloat("z"), scriptValue.getFloat("yaw"), scriptValue.getFloat("pitch"), scriptValue.checkString("onGround")
            )
            : new C06PacketPlayerPosLook();
      }

      public C06PacketPlayerPosLookTypeAdapter(TypeRegistry typeRegistry2) {
         super(6, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C06PacketPlayerPosLook", typeRegistry2);
      }
   }
}
