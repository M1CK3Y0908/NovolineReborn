package net.minecraft.client.resources;

import java.io.InputStream;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;

public interface IResource {
   ResourceLocation getResourceLocation();

   String getResourcePackName();

   <T extends IMetadataSection> T getMetadata(String text);

   InputStream getInputStream();

   boolean hasMetadata();
}
