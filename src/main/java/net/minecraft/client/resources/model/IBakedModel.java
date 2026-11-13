package net.minecraft.client.resources.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public interface IBakedModel {
   boolean isAmbientOcclusion();

   ItemCameraTransforms getItemCameraTransforms();

   List<BakedQuad> getFaceQuads(EnumFacing enumFacing);

   boolean isGui3d();

   boolean isBuiltInRenderer();

   List<BakedQuad> getGeneralQuads();

   TextureAtlasSprite getParticleTexture();
}
