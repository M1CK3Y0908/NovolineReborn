package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;

public class ResourcePackListEntryFound extends ResourcePackListEntry {
   private final ResourcePackRepository.Entry resourcePackEntry;

   public ResourcePackRepository.Entry getResourcePackEntry() {
      return this.resourcePackEntry;
   }

   @Override
   protected int getCompatibility() {
      return this.resourcePackEntry.getCompatibility();
   }

   @Override
   protected String getName() {
      return this.resourcePackEntry.getResourcePackName();
   }

   @Override
   protected String getDescription() {
      return this.resourcePackEntry.getTexturePackDescription();
   }

   public ResourcePackListEntryFound(GuiScreenResourcePacks resourcePacksGUIIn, ResourcePackRepository.Entry entry) {
      super(resourcePacksGUIIn);
      this.resourcePackEntry = entry;
   }

   @Override
   protected void bindTexture() {
      this.resourcePackEntry.bindTexturePackIcon(this.mc.getTextureManager());
   }
}
