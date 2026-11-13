package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   protected Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

   @Override
   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
      UnmodifiableIterator unmodifiableIterator = blockIn.getBlockState().getValidStates().iterator();

      while (unmodifiableIterator.hasNext()) {
         IBlockState iBlockState = (IBlockState)unmodifiableIterator.next();
         this.mapStateModelLocations.put(iBlockState, this.getModelResourceLocation(iBlockState));
      }

      return this.mapStateModelLocations;
   }

   protected abstract ModelResourceLocation getModelResourceLocation(IBlockState iBlockState);

   public String getPropertyString(Map<IProperty, Comparable> map) {
      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : map.entrySet()) {
         if (stringBuilder.length() != 0) {
            stringBuilder.append(",");
         }

         IProperty iProperty = (IProperty)entry.getKey();
         Comparable comparable = (Comparable)entry.getValue();
         stringBuilder.append(iProperty.getName());
         stringBuilder.append("=");
         stringBuilder.append(iProperty.getName(comparable));
      }

      if (stringBuilder.length() == 0) {
         stringBuilder.append("normal");
      }

      return stringBuilder.toString();
   }
}
