package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class StateMap extends StateMapperBase {
   private final IProperty<?> name;
   private final String suffix;
   private final List<IProperty<?>> ignored;

   private StateMap(IProperty<?> name, String suffix, List<IProperty<?>> ignored) {
      this.name = name;
      this.suffix = suffix;
      this.ignored = ignored;
   }

   @Override
   protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
      Map<IProperty, Comparable> map = Maps.newLinkedHashMap(state.getProperties());
      String text;
      if (this.name == null) {
         text = Block.blockRegistry.getNameForObject(state.getBlock()).toString();
      } else {
         text = ((IProperty)this.name).getName(map.remove(this.name));
      }

      if (this.suffix != null) {
         text = text + this.suffix;
      }

      for (IProperty<?> property : this.ignored) {
         map.remove(property);
      }

      return new ModelResourceLocation(text, this.getPropertyString(map));
   }

   public static class Builder {
      private String suffix;
      private IProperty<?> name;
      private final List<IProperty<?>> ignored = Lists.newArrayList();

      public StateMap build() {
         return new StateMap(this.name, this.suffix, this.ignored);
      }

      public StateMap.Builder ignore(IProperty<?>... iPropertys) {
         Collections.addAll(this.ignored, iPropertys);
         return this;
      }

      public StateMap.Builder withSuffix(String builderSuffixIn) {
         this.suffix = builderSuffixIn;
         return this;
      }

      public StateMap.Builder withName(IProperty<?> builderPropertyIn) {
         this.name = builderPropertyIn;
         return this;
      }
   }
}
