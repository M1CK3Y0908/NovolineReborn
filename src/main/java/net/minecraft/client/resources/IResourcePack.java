package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack {
   boolean resourceExists(ResourceLocation resourceLocation);

   Set<String> getResourceDomains();

   InputStream getInputStream(ResourceLocation resourceLocation) throws IOException;

   String getPackName();

   BufferedImage getPackImage() throws IOException;

   <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer iMetadataSerializer, String text) throws IOException;
}
