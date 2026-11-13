package wtf.novoline.script.adapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Timer;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class MinecraftTypeAdapter extends AbstractTypeAdapter<Minecraft> {
   public ScriptValue put(ScriptValue scriptValue, Minecraft minecraft2) {
      scriptValue.putRunnable("update", () -> this.put(scriptValue, minecraft2));
      scriptValue.putRunnable("commit", () -> this.apply(scriptValue, minecraft2));
      scriptValue.handleObject(minecraft2);
      scriptValue.putObject("thePlayer", minecraft2.thePlayer, EntityPlayerSP.class);
      scriptValue.putObject("theWorld", minecraft2.theWorld, WorldClient.class);
      scriptValue.putObject("fontRendererObj", minecraft2.fontRendererObj, FontRenderer.class);
      scriptValue.putObject("timer", minecraft2.timer, Timer.class);
      scriptValue.putSupplier("getDebugFPS", Minecraft::getDebugFPS);
      scriptValue.putSupplier("getFramebuffer", MappingUtils.mappingSupplier(Framebuffer.class, minecraft2::getFramebuffer));
      scriptValue.putSupplier("isSingleplayer", minecraft2::isSingleplayer);
      scriptValue.putSupplier("getCurrentServerData", minecraft2::getCurrentServerData);
      scriptValue.put("debug", minecraft2.debug);
      scriptValue.put("displayWidth", minecraft2.displayWidth);
      scriptValue.put("displayHeight", minecraft2.displayHeight);
      scriptValue.put("leftClickCounter", minecraft2.leftClickCounter);
      scriptValue.put("rightClickDelayTimer", minecraft2.rightClickDelayTimer);
      return scriptValue;
   }

   public MinecraftTypeAdapter(TypeRegistry typeRegistry2) {
      super("Minecraft", typeRegistry2);
   }

   public Minecraft apply(ScriptValue scriptValue, Minecraft minecraft2) {
      this.typeRegistry.getTypeAdapter(EntityPlayerSP.class).applyWithSubTypes(scriptValue.getScriptValue("thePlayer"), minecraft2.thePlayer);
      this.typeRegistry.getTypeAdapter(Timer.class).apply(scriptValue.getScriptValue("timer"), minecraft2.timer);
      minecraft2.debug = scriptValue.getString("debug");
      minecraft2.displayWidth = scriptValue.getInt("displayWidth");
      minecraft2.displayHeight = scriptValue.getInt("displayHeight");
      minecraft2.leftClickCounter = scriptValue.getInt("leftClickCounter");
      minecraft2.rightClickDelayTimer = scriptValue.getInt("rightClickDelayTimer");
      return minecraft2;
   }
}
