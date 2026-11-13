package wtf.novoline.utils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class ViaPipelineUtils {
   public static ChannelPipeline addBefore(ChannelPipeline channelPipeline, String text, String text2, ChannelHandler channelHandler) {
      switch (text) {
         case "decoder":
            if (channelPipeline.get("via-decoder") != null) {
               text = "via-decoder";
            }
            break;
         case "encoder":
            if (channelPipeline.get("via-encoder") != null) {
               text = "via-encoder";
            }
      }

      return channelPipeline.addBefore(text, text2, channelHandler);
   }
}
