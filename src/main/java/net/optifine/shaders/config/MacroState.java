package net.optifine.shaders.config;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.ParseException;

public class MacroState {
   private static final String IFNDEF = "ifndef";
   private static final String ENDIF = "endif";
   private static final String IF = "if";
   private static final String ELIF = "elif";
   private static final String DEFINE = "define";
   private boolean active = true;
   private static final String UNDEF = "undef";
   private Deque<Boolean> dequeState = new ArrayDeque<>();
   private static final String ELSE = "else";
   private static final String IFDEF = "ifdef";
   private static final Pattern PATTERN_DIRECTIVE = Pattern.compile("\\s*#\\s*(\\w+)\\s*(.*)");
   private static final Pattern PATTERN_DEFINED = Pattern.compile("defined\\s+(\\w+)");
   private static final Pattern PATTERN_DEFINED_FUNC = Pattern.compile("defined\\s*\\(\\s*(\\w+)\\s*\\)");
   private static final Pattern PATTERN_MACRO = Pattern.compile("(\\w+)");
   private Deque<Boolean> dequeResolved = new ArrayDeque<>();
   private static final List<String> MACRO_NAMES = Arrays.asList("define", "undef", "ifdef", "ifndef", "if", "else", "elif", "endif");
   private Map<String, String> mapMacroValues = new HashMap<>();

   private boolean eval(String str) {
      str = PATTERN_DEFINED.matcher(str).replaceAll("defined_$1");
      str = PATTERN_DEFINED_FUNC.matcher(str).replaceAll("defined_$1");
      int i = 0;

      boolean flag;
      do {
         flag = false;
         Matcher matcher2 = PATTERN_MACRO.matcher(str);

         while (matcher2.find()) {
            String text3 = matcher2.group();
            if (text3.length() > 0) {
               char c = text3.charAt(0);
               if ((Character.isLetter(c) || c == '_') && this.mapMacroValues.containsKey(text3)) {
                  String text4 = this.mapMacroValues.get(text3);
                  if (text4 == null) {
                     text4 = "1";
                  }

                  int i2 = matcher2.start();
                  int i3 = matcher2.end();
                  str = str.substring(0, i2) + " " + text4 + " " + str.substring(i3);
                  flag = true;
                  i++;
                  break;
               }
            }
         }
      } while (flag && i < 100);

      if (i >= 100) {
         Config.warn("Too many iterations: " + i + ", when resolving: " + str);
         return true;
      }

      try {
         MacroExpressionResolver macroExpressionResolver = new MacroExpressionResolver(this.mapMacroValues);
         ExpressionParser expressionParser = new ExpressionParser(macroExpressionResolver);
         IExpression iExpression = expressionParser.parse(str);
         if (iExpression.getExpressionType() == ExpressionType.BOOL) {
            IExpressionBool iExpressionBool = (IExpressionBool)iExpression;
            return iExpressionBool.eval();
         } else if (iExpression.getExpressionType() == ExpressionType.FLOAT) {
            IExpressionFloat iExpressionFloat = (IExpressionFloat)iExpression;
            float f = iExpressionFloat.eval();
            return f != 0.0F;
         } else {
            throw new ParseException("Not a boolean or float expression: " + iExpression.getExpressionType());
         }
      } catch (ParseException parseException) {
         Config.warn("Invalid macro expression: " + str);
         Config.warn("Error: " + parseException.getMessage());
         return false;
      }
   }

   public boolean processLine(String line) {
      Matcher matcher = PATTERN_DIRECTIVE.matcher(line);
      if (!matcher.matches()) {
         return this.active;
      }

      String text = matcher.group(1);
      String text2 = matcher.group(2);
      int i = text2.indexOf("//");
      if (i >= 0) {
         text2 = text2.substring(0, i);
      }

      boolean conditionMet = this.active;
      this.processMacro(text, text2);
      this.active = !this.dequeState.contains(Boolean.FALSE);
      return this.active || conditionMet;
   }

   private void processMacro(String name, String param) {
      StringTokenizer stringTokenizer = new StringTokenizer(param, " \t");
      String text3 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "";
      String text4 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken("").trim() : "";
      if (name.equals("define")) {
         this.mapMacroValues.put(text3, text4);
      } else if (name.equals("undef")) {
         this.mapMacroValues.remove(text3);
      } else if (name.equals("ifdef")) {
         boolean flag = this.mapMacroValues.containsKey(text3);
         this.dequeState.add(flag);
         this.dequeResolved.add(flag);
      } else if (name.equals("ifndef")) {
         boolean flag2 = !this.mapMacroValues.containsKey(text3);
         this.dequeState.add(flag2);
         this.dequeResolved.add(flag2);
      } else if (name.equals("if")) {
         boolean flag3 = this.eval(param);
         this.dequeState.add(flag3);
         this.dequeResolved.add(flag3);
      } else if (!this.dequeState.isEmpty()) {
         if (name.equals("elif")) {
            this.dequeState.removeLast();
            boolean flag4 = this.dequeResolved.removeLast();
            if (flag4) {
               this.dequeState.add(false);
               this.dequeResolved.add(flag4);
            } else {
               boolean flag5 = this.eval(param);
               this.dequeState.add(flag5);
               this.dequeResolved.add(flag5);
            }
         } else if (name.equals("else")) {
            this.dequeState.removeLast();
            boolean flag6 = this.dequeResolved.removeLast();
            boolean flag7 = !flag6;
            this.dequeState.add(flag7);
            this.dequeResolved.add(true);
         } else if (name.equals("endif")) {
            this.dequeState.removeLast();
            this.dequeResolved.removeLast();
         }
      }
   }

   public static boolean isMacroLine(String line) {
      Matcher matcher = PATTERN_DIRECTIVE.matcher(line);
      if (!matcher.matches()) {
         return false;
      }

      String text = matcher.group(1);
      return MACRO_NAMES.contains(text);
   }
}
