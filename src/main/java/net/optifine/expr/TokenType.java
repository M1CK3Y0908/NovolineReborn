package net.optifine.expr;

public enum TokenType {
   IDENTIFIER("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_:."),
   NUMBER("0123456789", "0123456789."),
   OPERATOR("+-*/%!&|<>=", "&|="),
   COMMA(","),
   BRACKET_OPEN("("),
   BRACKET_CLOSE(")");

   private String charsFirst;
   private String charsNext;
   public static final TokenType[] VALUES = values();

   TokenType(String text2) {
      this(text2, "");
   }

   public String getCharsNext() {
      return this.charsNext;
   }

   public boolean hasCharNext(char ch) {
      return this.charsNext.indexOf(ch) >= 0;
   }

   public String getCharsFirst() {
      return this.charsFirst;
   }

   public static TokenType getTypeByFirstChar(char ch) {
      for (int index = 0; index < VALUES.length; index++) {
         TokenType tokenType = VALUES[index];
         if (tokenType.getCharsFirst().indexOf(ch) >= 0) {
            return tokenType;
         }
      }

      return null;
   }

   TokenType(String text2, String text3) {
      this.charsFirst = text2;
      this.charsNext = text3;
   }

   private static class Const {
      static final String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      static final String VALUE_0123456789 = "0123456789";
   }
}
