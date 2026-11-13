package net.minecraft.client.stream;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.util.Map;

public class Metadata {
   private final String text;
   private Map<String, String> map;
   private String text2;
   private static final Gson gson = new Gson();

   public String getText() {
      return this.text;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", this.text).add("description", this.text2).add("data", this.getString2()).toString();
   }

   public String getString2() {
      return this.map != null && !this.map.isEmpty() ? gson.toJson(this.map) : null;
   }

   public Metadata(String text3) {
      this(text3, (String)null);
   }

   public String getString() {
      return this.text2 == null ? this.text : this.text2;
   }

   public Metadata(String text3, String text4) {
      this.text = text3;
      this.text2 = text4;
   }

   public void setText2(String text3) {
      this.text2 = text3;
   }

   public void handleString(String text3, String text4) {
      if (this.map == null) {
         this.map = Maps.newHashMap();
      }

      if (this.map.size() > 50) {
         throw new IllegalArgumentException("Metadata payload is full, cannot add more to it!");
      }

      if (text3 == null) {
         throw new IllegalArgumentException("Metadata payload key cannot be null!");
      }

      if (text3.length() > 255) {
         throw new IllegalArgumentException("Metadata payload key is too long!");
      }

      if (text4 == null) {
         throw new IllegalArgumentException("Metadata payload value cannot be null!");
      }

      if (text4.length() > 255) {
         throw new IllegalArgumentException("Metadata payload value is too long!");
      }

      this.map.put(text3, text4);
   }
}
