package wtf.novoline.api;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public interface IBakedQuadBuilder {
   void update();

   VertexFormat getVertexFormat();

   void setQuadOrientation(EnumFacing enumFacing);
}
