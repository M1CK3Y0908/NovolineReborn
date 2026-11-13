package net.minecraft.client.resources.data;

import java.util.Collections;
import java.util.List;

public class TextureMetadataSection implements IMetadataSection {
   private final boolean textureBlur;
   private final boolean textureClamp;
   private final List<Integer> listMipmaps;

   public boolean getTextureBlur() {
      return this.textureBlur;
   }

   public TextureMetadataSection(boolean flag, boolean flag2, List<Integer> list) {
      this.textureBlur = flag;
      this.textureClamp = flag2;
      this.listMipmaps = list;
   }

   public boolean getTextureClamp() {
      return this.textureClamp;
   }

   public List<Integer> getListMipmaps() {
      return Collections.unmodifiableList(this.listMipmaps);
   }
}
