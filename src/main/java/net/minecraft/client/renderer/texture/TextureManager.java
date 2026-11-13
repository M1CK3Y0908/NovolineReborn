package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomGuis;
import net.optifine.EmissiveTextures;
import net.optifine.RandomEntities;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ITickable, IResourceManagerReloadListener {
   private ResourceLocation boundTextureLocation;
   private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
   private final List<ITickable> listTickables = Lists.newArrayList();
   private ITextureObject boundTexture;
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private static final Logger logger = LogManager.getLogger();
   private IResourceManager theResourceManager;

   public ITextureObject getTexture(ResourceLocation textureLocation) {
      return this.mapTextureObjects.get(textureLocation);
   }

   public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
      if (name.equals("logo")) {
         texture = Config.getMojangLogoTexture(texture);
      }

      Integer integer = this.mapTextureCounters.get(name);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(name, integer);
      ResourceLocation resourceLocation2 = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
      this.loadTexture(resourceLocation2, texture);
      return resourceLocation2;
   }

   private static ReportedException createReportedException(ReportedException reportedException) {
      return reportedException;
   }

   public void reloadBannerTextures() {
      for (Entry<ResourceLocation, ITextureObject> entry : new HashSet<>(this.mapTextureObjects.entrySet())) {
         ResourceLocation resourceLocation = entry.getKey();
         ITextureObject textureObject = entry.getValue();
         if (textureObject instanceof LayeredColorMaskTexture) {
            this.loadTexture(resourceLocation, textureObject);
         }
      }
   }

   public ResourceLocation getBoundTextureLocation() {
      return this.boundTextureLocation;
   }

   public void deleteTexture(ResourceLocation textureLocation) {
      ITextureObject textureObject = this.getTexture(textureLocation);
      if (textureObject != null) {
         this.mapTextureObjects.remove(textureLocation);
         TextureUtil.deleteTexture(textureObject.getGlTextureId());
      }
   }

   public boolean loadTexture(ResourceLocation textureLocation, ITextureObject textureObj) {
      boolean conditionMet = true;

      try {
         textureObj.loadTexture(this.theResourceManager);
      } catch (IOException ioException) {
         logger.warn("Failed to load texture: " + textureLocation, ioException);
         textureObj = TextureUtil.missingTexture;
         this.mapTextureObjects.put(textureLocation, textureObj);
         conditionMet = false;
      } catch (Throwable throwable) {
         final ITextureObject textureObj2 = textureObj;
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Registering texture");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Resource location being registered");
         crashReportCategory.addCrashSection("Resource location", textureLocation);
         crashReportCategory.addCrashSectionCallable("Texture object class", new Callable<String>() {
            public String call() throws Exception {
               return textureObj2.getClass().getName();
            }
         });
         throw new ReportedException(crashReport);
      }

      this.mapTextureObjects.put(textureLocation, textureObj);
      return conditionMet;
   }

   public void bindTexture(ResourceLocation resource) {
      if (Config.isRandomEntities()) {
         resource = RandomEntities.getTextureLocation(resource);
      }

      if (Config.isCustomGuis()) {
         resource = CustomGuis.getTextureLocation(resource);
      }

      ITextureObject textureObject = this.mapTextureObjects.get(resource);
      if (EmissiveTextures.isActive()) {
         textureObject = EmissiveTextures.getEmissiveTexture(textureObject, this.mapTextureObjects);
      }

      if (textureObject == null) {
         textureObject = new SimpleTexture(resource);
         this.loadTexture(resource, textureObject);
      }

      if (Config.isShaders()) {
         ShadersTex.bindTexture(textureObject);
      } else {
         TextureUtil.bindTexture(textureObject.getGlTextureId());
      }

      this.boundTexture = textureObject;
      this.boundTextureLocation = resource;
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      Config.dbg("*** Reloading textures ***");
      Config.log("Resource packs: " + Config.getResourcePackNames());
      Iterator iterator2 = this.mapTextureObjects.keySet().iterator();

      while (iterator2.hasNext()) {
         ResourceLocation resourceLocation2 = (ResourceLocation)iterator2.next();
         String resourcePath = resourceLocation2.getResourcePath();
         if (resourcePath.startsWith("mcpatcher/") || resourcePath.startsWith("optifine/") || EmissiveTextures.isEmissive(resourceLocation2)) {
            ITextureObject iTextureObject = this.mapTextureObjects.get(resourceLocation2);
            if (iTextureObject instanceof AbstractTexture) {
               ((AbstractTexture)iTextureObject).deleteGlTexture();
            }

            iterator2.remove();
         }
      }

      EmissiveTextures.update();

      for (Entry entry2 : new HashSet<>(this.mapTextureObjects.entrySet())) {
         this.loadTexture((ResourceLocation)entry2.getKey(), (ITextureObject)entry2.getValue());
      }
   }

   public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj) {
      if (this.loadTexture(textureLocation, textureObj)) {
         this.listTickables.add(textureObj);
         return true;
      } else {
         return false;
      }
   }

   public ITextureObject getBoundTexture() {
      return this.boundTexture;
   }

   @Override
   public void tick() {
      Iterator iterator2 = this.listTickables.iterator();

      while (iterator2.hasNext()) {
         ((ITickable)iterator2.next()).tick();
      }
   }

   public TextureManager(IResourceManager resourceManager) {
      this.theResourceManager = resourceManager;
   }
}
