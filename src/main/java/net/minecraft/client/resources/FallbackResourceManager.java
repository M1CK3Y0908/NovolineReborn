package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger logger = LogManager.getLogger();
   protected final List<IResourcePack> resourcePacks = Lists.newArrayList();
   private final IMetadataSerializer frmMetadataSerializer;

   public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
      this.frmMetadataSerializer = frmMetadataSerializerIn;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourcePack iResourcePack = null;
      ResourceLocation resourceLocation = getLocationMcmeta(location);

      for (int index = this.resourcePacks.size() - 1; index >= 0; index--) {
         IResourcePack resourcePack2 = this.resourcePacks.get(index);
         if (iResourcePack == null && resourcePack2.resourceExists(resourceLocation)) {
            iResourcePack = resourcePack2;
         }

         if (resourcePack2.resourceExists(location)) {
            InputStream inputStream = null;
            if (iResourcePack != null) {
               inputStream = this.getInputStream(resourceLocation, iResourcePack);
            }

            return new SimpleResource(
               resourcePack2.getPackName(), location, this.getInputStream(location, resourcePack2), inputStream, this.frmMetadataSerializer
            );
         }
      }

      throw new FileNotFoundException(location.toString());
   }

   static ResourceLocation getLocationMcmeta(ResourceLocation location) {
      return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
   }

   public void addResourcePack(IResourcePack resourcePack) {
      this.resourcePacks.add(resourcePack);
   }

   @Override
   public Set<String> getResourceDomains() {
      return null;
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      ArrayList arrayList = Lists.newArrayList();
      ResourceLocation locationMcmeta = getLocationMcmeta(location);

      for (IResourcePack iResourcePack : this.resourcePacks) {
         if (iResourcePack.resourceExists(location)) {
            InputStream inputStream2 = iResourcePack.resourceExists(locationMcmeta) ? this.getInputStream(locationMcmeta, iResourcePack) : null;
            arrayList.add(new SimpleResource(iResourcePack.getPackName(), location, this.getInputStream(location, iResourcePack), inputStream2, this.frmMetadataSerializer));
         }
      }

      if (arrayList.isEmpty()) {
         throw new FileNotFoundException(location.toString());
      } else {
         return arrayList;
      }
   }

   protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
      InputStream inputStream = resourcePack.getInputStream(location);
      return logger.isDebugEnabled()
         ? new FallbackResourceManager.InputStreamLeakedResourceLogger(inputStream, location, resourcePack.getPackName())
         : inputStream;
   }

   static class InputStreamLeakedResourceLogger extends InputStream {
      private boolean isClosed = false;
      private final InputStream inputStream;
      private final String message;

      @Override
      public int read() throws IOException {
         return this.inputStream.read();
      }

      @Override
      protected void finalize() throws Throwable {
         if (!this.isClosed) {
            FallbackResourceManager.logger.warn(this.message);
         }

         super.finalize();
      }

      public InputStreamLeakedResourceLogger(InputStream inputStream2, ResourceLocation location, String resourcePack) {
         this.inputStream = inputStream2;
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
         this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + byteArrayOutputStream.toString();
      }

      @Override
      public void close() throws IOException {
         this.inputStream.close();
         this.isClosed = true;
      }
   }
}
