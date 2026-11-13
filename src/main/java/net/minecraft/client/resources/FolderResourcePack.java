package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack extends AbstractResourcePack {
   @Override
   public Set<String> getResourceDomains() {
      HashSet hashSet = Sets.newHashSet();
      File file = new File(this.resourcePackFile, "assets/");
      if (file.isDirectory()) {
         for (File file2 : file.listFiles((java.io.FileFilter)DirectoryFileFilter.DIRECTORY)) {
            String relativeName = getRelativeName(file, file2);
            if (!relativeName.equals(relativeName.toLowerCase())) {
               this.logNameNotLowercase(relativeName);
            } else {
               hashSet.add(relativeName.substring(0, relativeName.length() - 1));
            }
         }
      }

      return hashSet;
   }

   @Override
   protected InputStream getInputStreamByName(String name) throws IOException {
      return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, name)));
   }

   public FolderResourcePack(File resourcePackFileIn) {
      super(resourcePackFileIn);
   }

   @Override
   protected boolean hasResourceName(String name) {
      return new File(this.resourcePackFile, name).isFile();
   }
}
