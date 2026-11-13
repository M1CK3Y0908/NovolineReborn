package net.optifine.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

public class TokenParser {
   private static Token readToken(char chFirst, TokenType type, PushbackReader pr) throws IOException {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(chFirst);

      while (true) {
         int i = pr.read();
         if (i < 0) {
            break;
         }

         char c0 = (char)i;
         if (!type.hasCharNext(c0)) {
            pr.unread(c0);
            break;
         }

         stringBuffer.append(c0);
      }

      return new Token(type, stringBuffer.toString());
   }

   public static Token[] parse(String str) throws IOException, ParseException {
      StringReader stringReader = new StringReader(str);
      PushbackReader pushbackReader = new PushbackReader(stringReader);
      ArrayList arrayList = new ArrayList();

      while (true) {
         int i = pushbackReader.read();
         if (i < 0) {
            return (Token[]) arrayList.toArray(new Token[arrayList.size()]);
         }

         char c0 = (char)i;
         if (!Character.isWhitespace(c0)) {
            TokenType tokenType = TokenType.getTypeByFirstChar(c0);
            if (tokenType == null) {
               throw new ParseException("Invalid character: '" + c0 + "', in: " + str);
            }

            Token token = readToken(c0, tokenType, pushbackReader);
            arrayList.add(token);
         }
      }
   }
}
