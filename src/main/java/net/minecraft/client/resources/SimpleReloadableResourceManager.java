package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private final Map<String, FallbackResourceManager> domainResourceManagers = Maps.newHashMap();
   private static final Logger logger = LogManager.getLogger();
   private static final Joiner joinerResourcePacks = Joiner.on(", ");
   private final List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
   private final Set<String> setResourceDomains = Sets.newLinkedHashSet();
   private final IMetadataSerializer rmMetadataSerializer;

   @Override
   public void reloadResources(List<IResourcePack> resourcesPacksList) {
      this.clearResources();
      logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(resourcesPacksList, new Function<IResourcePack, String>() {
         public String apply(IResourcePack arg1) {
            return arg1.getPackName();
         }
      })));

      for (IResourcePack iResourcePack : resourcesPacksList) {
         this.reloadResourcePack(iResourcePack);
      }

      this.notifyReloadListeners();
   }

   @Override
   public Set<String> getResourceDomains() {
      return this.setResourceDomains;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourceManager resourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (resourceManager != null) {
         return resourceManager.getResource(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   private void clearResources() {
      this.domainResourceManagers.clear();
      this.setResourceDomains.clear();
   }

   public void reloadResourcePack(IResourcePack resourcePack) {
      for (String text : resourcePack.getResourceDomains()) {
         this.setResourceDomains.add(text);
         FallbackResourceManager fallbackResourceManager = this.domainResourceManagers.get(text);
         if (fallbackResourceManager == null) {
            fallbackResourceManager = new FallbackResourceManager(this.rmMetadataSerializer);
            this.domainResourceManagers.put(text, fallbackResourceManager);
         }

         fallbackResourceManager.addResourcePack(resourcePack);
      }
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      IResourceManager resourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (resourceManager != null) {
         return resourceManager.getAllResources(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   @Override
   public void registerReloadListener(IResourceManagerReloadListener reloadListener) {
      this.reloadListeners.add(reloadListener);
      reloadListener.onResourceManagerReload(this);
   }

   public SimpleReloadableResourceManager(IMetadataSerializer rmMetadataSerializerIn) {
      this.rmMetadataSerializer = rmMetadataSerializerIn;
   }

   private void notifyReloadListeners() {
      Iterator iterator2 = this.reloadListeners.iterator();

      while (iterator2.hasNext()) {
         ((IResourceManagerReloadListener)iterator2.next()).onResourceManagerReload(this);
      }
   }
}
