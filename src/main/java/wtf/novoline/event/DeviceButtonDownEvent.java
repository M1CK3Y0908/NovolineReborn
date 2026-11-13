package wtf.novoline.event;

public class DeviceButtonDownEvent extends Event {
   public int keyId;
   public DeviceButtonDownEvent.MouseType mouseType;

   public DeviceButtonDownEvent(int i, DeviceButtonDownEvent.MouseType mouseType2) {
      super(23992540611656L, true);
      this.keyId = i;
      this.mouseType = mouseType2;
   }

   public enum MouseType {
      MOUSE,
      KEYBOARD;
   }
}
