package wtf.novoline.utils.render;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class CustomVertexFormats {
   public static final VertexFormatWrapper BLOCK = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper ITEM = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper OLDMODEL_POSITION_TEX_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper PARTICLE_POSITION_TEX_COLOR_LMAP = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_TEX = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_TEX_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_TEX_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_TEX_LMAP_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static final VertexFormatWrapper POSITION_TEX_COLOR_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static final VertexElementWrapper POSITION_3F = new VertexElementWrapper(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
   public static final VertexElementWrapper COLOR_4UB = new VertexElementWrapper(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR, 4));
   public static final VertexElementWrapper TEX_2F = new VertexElementWrapper(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2));
   public static final VertexElementWrapper TEX_2S = new VertexElementWrapper(new VertexFormatElement(1, EnumType.SHORT, EnumUsage.UV, 2));
   public static final VertexElementWrapper NORMAL_3B = new VertexElementWrapper(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3));
   public static final VertexElementWrapper PADDING_1B = new VertexElementWrapper(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));

   static {
      BLOCK.addElement(POSITION_3F);
      BLOCK.addElement(COLOR_4UB);
      BLOCK.addElement(TEX_2F);
      BLOCK.addElement(TEX_2S);
      ITEM.addElement(POSITION_3F);
      ITEM.addElement(COLOR_4UB);
      ITEM.addElement(TEX_2F);
      ITEM.addElement(NORMAL_3B);
      ITEM.addElement(PADDING_1B);
      OLDMODEL_POSITION_TEX_NORMAL.addElement(POSITION_3F);
      OLDMODEL_POSITION_TEX_NORMAL.addElement(TEX_2F);
      OLDMODEL_POSITION_TEX_NORMAL.addElement(NORMAL_3B);
      OLDMODEL_POSITION_TEX_NORMAL.addElement(PADDING_1B);
      PARTICLE_POSITION_TEX_COLOR_LMAP.addElement(POSITION_3F);
      PARTICLE_POSITION_TEX_COLOR_LMAP.addElement(TEX_2F);
      PARTICLE_POSITION_TEX_COLOR_LMAP.addElement(COLOR_4UB);
      PARTICLE_POSITION_TEX_COLOR_LMAP.addElement(TEX_2S);
      POSITION.addElement(POSITION_3F);
      POSITION_COLOR.addElement(POSITION_3F);
      POSITION_COLOR.addElement(COLOR_4UB);
      POSITION_TEX.addElement(POSITION_3F);
      POSITION_TEX.addElement(TEX_2F);
      POSITION_NORMAL.addElement(POSITION_3F);
      POSITION_NORMAL.addElement(NORMAL_3B);
      POSITION_NORMAL.addElement(PADDING_1B);
      POSITION_TEX_COLOR.addElement(POSITION_3F);
      POSITION_TEX_COLOR.addElement(TEX_2F);
      POSITION_TEX_COLOR.addElement(COLOR_4UB);
      POSITION_TEX_NORMAL.addElement(POSITION_3F);
      POSITION_TEX_NORMAL.addElement(TEX_2F);
      POSITION_TEX_NORMAL.addElement(NORMAL_3B);
      POSITION_TEX_NORMAL.addElement(PADDING_1B);
      POSITION_TEX_LMAP_COLOR.addElement(POSITION_3F);
      POSITION_TEX_LMAP_COLOR.addElement(TEX_2F);
      POSITION_TEX_LMAP_COLOR.addElement(TEX_2S);
      POSITION_TEX_LMAP_COLOR.addElement(COLOR_4UB);
      POSITION_TEX_COLOR_NORMAL.addElement(POSITION_3F);
      POSITION_TEX_COLOR_NORMAL.addElement(TEX_2F);
      POSITION_TEX_COLOR_NORMAL.addElement(COLOR_4UB);
      POSITION_TEX_COLOR_NORMAL.addElement(NORMAL_3B);
      POSITION_TEX_COLOR_NORMAL.addElement(PADDING_1B);
   }
}
