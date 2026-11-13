package wtf.novoline.module;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.DeviceButtonDownEvent.MouseType;
import wtf.novoline.module.Module.PressType;

public class KeybindHandler {
   @EventLink(4)
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      for (Module module2 : ModuleManager.modules) {
         int keyBind = module2.getKeyBind();
         if (module2.getPressType() == PressType.HOLD && keyBind != 0) {
            module2.setEnabled(keyBind < 0 ? Mouse.isButtonDown(Math.abs(keyBind) - 1) : Keyboard.isKeyDown(keyBind));
         }
      }
   };
   @EventLink
   private final Listener<DeviceButtonDownEvent> onDeviceButtonDown = deviceButtonDownEvent -> {
      for (Module module2 : ModuleManager.modules) {
         if (module2.getPressType() == PressType.PRESS) {
            int keyBind = module2.getKeyBind();
            if (keyBind != 0) {
               int i = Math.abs(keyBind) - 1;
               if (deviceButtonDownEvent.mouseType == MouseType.MOUSE && keyBind < 0 && i == deviceButtonDownEvent.keyId && Mouse.isButtonDown(i)
                  || deviceButtonDownEvent.mouseType == MouseType.KEYBOARD && deviceButtonDownEvent.keyId == keyBind) {
                  module2.toggle();
               }
            }
         }
      }
   };
}
