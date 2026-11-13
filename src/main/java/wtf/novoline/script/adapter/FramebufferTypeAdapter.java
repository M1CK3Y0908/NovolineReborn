package wtf.novoline.script.adapter;

import net.minecraft.client.shader.Framebuffer;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public class FramebufferTypeAdapter extends AbstractTypeAdapter<Framebuffer> {
   public ScriptValue put(ScriptValue scriptValue, Framebuffer framebuffer) {
      scriptValue.put("framebufferTextureWidth", framebuffer.framebufferTextureHeight);
      scriptValue.put("framebufferTextureHeight", framebuffer.framebufferTextureHeight);
      scriptValue.put("framebufferWidth", framebuffer.framebufferWidth);
      scriptValue.put("framebufferHeight", framebuffer.framebufferHeight);
      scriptValue.put("useDepth", framebuffer.useDepth);
      scriptValue.put("framebufferObject", framebuffer.framebufferObject);
      scriptValue.put("framebufferTexture", framebuffer.framebufferTexture);
      scriptValue.put("depthBuffer", framebuffer.depthBuffer);
      scriptValue.put("framebufferColor", framebuffer.framebufferColor);
      scriptValue.put("framebufferFilter", framebuffer.framebufferFilter);
      scriptValue.putBiConsumer("createBindFramebuffer", framebuffer::createBindFramebuffer);
      scriptValue.putRunnable("deleteFramebuffer", framebuffer::deleteFramebuffer);
      scriptValue.putBiConsumer("createFramebuffer", framebuffer::createFramebuffer);
      scriptValue.putConsumer("setFramebufferFilter", framebuffer::setFramebufferFilter);
      scriptValue.putRunnable("checkFramebufferComplete", framebuffer::checkFramebufferComplete);
      scriptValue.putRunnable("bindFramebufferTexture", framebuffer::bindFramebufferTexture);
      scriptValue.putRunnable("unbindFramebufferTexture", framebuffer::unbindFramebufferTexture);
      scriptValue.putConsumer("bindFramebuffer", framebuffer::bindFramebuffer);
      scriptValue.putRunnable("unbindFramebuffer", framebuffer::unbindFramebuffer);
      scriptValue.putConsumer4("setFramebufferColor", framebuffer::setFramebufferColor);
      scriptValue.putBiConsumer("framebufferRender", framebuffer::framebufferRender);
      scriptValue.putConsumer3("framebufferRenderExt", framebuffer::framebufferRenderExt);
      scriptValue.putRunnable("framebufferClear", framebuffer::framebufferClear);
      return scriptValue;
   }

   public FramebufferTypeAdapter(TypeRegistry typeRegistry) {
      super("Framebuffer", typeRegistry);
   }
}
