package net.optifine.expr;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.src.Config;

public class ExpressionParser {
   private IExpressionResolver expressionResolver;

   private void mergeOperators(List<IExpression> listExpr, List<FunctionType> listFuncs, int precedence) throws ParseException {
      for (int index = 0; index < listFuncs.size(); index++) {
         FunctionType functionType = listFuncs.get(index);
         if (functionType.getPrecedence() == precedence) {
            listFuncs.remove(index);
            IExpression expression = listExpr.remove(index);
            IExpression expression2 = listExpr.remove(index);
            IExpression expression3 = makeFunction(functionType, new IExpression[]{expression, expression2});
            listExpr.add(index, expression3);
            index--;
         }
      }
   }

   public IExpressionBool parseBool(String str) throws ParseException {
      IExpression expression = this.parse(str);
      if (!(expression instanceof IExpressionBool)) {
         throw new ParseException("Not a boolean expression: " + expression.getExpressionType());
      } else {
         return (IExpressionBool)expression;
      }
   }

   private static IExpression makeFunction(FunctionType type, IExpression[] args) throws ParseException {
      ExpressionType[] parameterTypes = type.getParameterTypes(args);
      if (args.length != parameterTypes.length) {
         throw new ParseException(
            "Invalid number of arguments, function: \"" + type.getName() + "\", count arguments: " + args.length + ", should be: " + parameterTypes.length
         );
      }

      for (int i = 0; i < args.length; i++) {
         ExpressionType expressionType = args[i].getExpressionType();
         ExpressionType expressionType2 = parameterTypes[i];
         if (expressionType != expressionType2) {
            throw new ParseException(
               "Invalid argument type, function: \"" + type.getName() + "\", index: " + i + ", type: " + expressionType + ", should be: " + expressionType2
            );
         }
      }

      if (type.getExpressionType() == ExpressionType.FLOAT) {
         return new FunctionFloat(type, args);
      } else if (type.getExpressionType() == ExpressionType.BOOL) {
         return new FunctionBool(type, args);
      } else if (type.getExpressionType() == ExpressionType.FLOAT_ARRAY) {
         return new FunctionFloatArray(type, args);
      } else {
         throw new ParseException("Unknown function type: " + type.getExpressionType() + ", function: " + type.getName());
      }
   }

   private static Deque<Token> getGroup(Deque<Token> deque, TokenType tokenTypeEnd, boolean tokenEndRequired) throws ParseException {
      ArrayDeque arrayDeque = new ArrayDeque();
      int i = 0;
      Iterator<Token> iterator = deque.iterator();

      while (iterator.hasNext()) {
         Token token = iterator.next();
         iterator.remove();
         if (i == 0 && token.getType() == tokenTypeEnd) {
            return arrayDeque;
         }

         arrayDeque.add(token);
         if (token.getType() == TokenType.BRACKET_OPEN) {
            i++;
         }

         if (token.getType() == TokenType.BRACKET_CLOSE) {
            i--;
         }
      }

      if (tokenEndRequired) {
         throw new ParseException("Missing end token: " + tokenTypeEnd);
      } else {
         return arrayDeque;
      }
   }

   private IExpression makeInfix(List<IExpression> listExpr, List<Token> listOper) throws ParseException {
      LinkedList linkedList = new LinkedList();

      for (Token token3 : listOper) {
         FunctionType functionType3 = FunctionType.parse(token3.getText());
         checkNull(functionType3, "Invalid operator: " + token3);
         linkedList.add(functionType3);
      }

      return this.makeInfixFunc(listExpr, linkedList);
   }

   private static ParseException createParseException(ParseException parseException) {
      return parseException;
   }

   private IExpression makeBracketed(Token token, Deque<Token> deque) throws ParseException {
      Deque<Token> deque3 = getGroup(deque, TokenType.BRACKET_CLOSE, true);
      return this.parseInfix(deque3);
   }

   private static IExpression makeConstantFloat(Token token) throws ParseException {
      float f = Config.parseFloat(token.getText(), Float.NaN);
      if (f == Float.NaN) {
         throw new ParseException("Invalid float value: " + token);
      } else {
         return new ConstantFloat(f);
      }
   }

   private IExpression makeVariable(Token token) throws ParseException {
      if (this.expressionResolver == null) {
         throw new ParseException("Model variable not found: " + token);
      } else {
         IExpression expression = this.expressionResolver.getExpression(token.getText());
         if (expression == null) {
            throw new ParseException("Model variable not found: " + token);
         } else {
            return expression;
         }
      }
   }

   private IExpression makeInfixFunc(List<IExpression> listExpr, List<FunctionType> listFunc) throws ParseException {
      if (listExpr.size() != listFunc.size() + 1) {
         throw new ParseException("Invalid infix expression, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
      }

      if (listExpr.size() == 1) {
         return listExpr.get(0);
      }

      int i = Integer.MAX_VALUE;
      int i2 = Integer.MIN_VALUE;

      for (FunctionType functionType3 : listFunc) {
         i = Math.min(functionType3.getPrecedence(), i);
         i2 = Math.max(functionType3.getPrecedence(), i2);
      }

      if (i2 >= i && i2 - i <= 10) {
         for (int i3 = i2; i3 >= i; i3--) {
            this.mergeOperators(listExpr, listFunc, i3);
         }

         if (listExpr.size() == 1 && listFunc.size() == 0) {
            return listExpr.get(0);
         } else {
            throw new ParseException("Error merging operators, expressions: " + listExpr.size() + ", operators: " + listFunc.size());
         }
      } else {
         throw new ParseException("Invalid infix precedence, min: " + i + ", max: " + i2);
      }
   }

   public IExpressionFloat parseFloat(String str) throws ParseException {
      IExpression expression = this.parse(str);
      if (!(expression instanceof IExpressionFloat)) {
         throw new ParseException("Not a float expression: " + expression.getExpressionType());
      } else {
         return (IExpressionFloat)expression;
      }
   }

   private IExpression makeFunction(FunctionType type, Deque<Token> deque) throws ParseException {
      if (type.getParameterCount(new IExpression[0]) == 0) {
         Token token3 = deque.peek();
         if (token3 == null || token3.getType() != TokenType.BRACKET_OPEN) {
            return makeFunction(type, new IExpression[0]);
         }
      }

      Token token4 = deque.poll();
      Deque group = getGroup(deque, TokenType.BRACKET_CLOSE, true);
      IExpression[] iExpressions = this.parseExpressions(group);
      return makeFunction(type, iExpressions);
   }

   private FunctionType getFunctionType(Token token, Deque<Token> deque) throws ParseException {
      Token token2 = deque.peek();
      if (token2 != null && token2.getType() == TokenType.BRACKET_OPEN) {
         FunctionType enumfunctiontype1 = FunctionType.parse(token.getText());
         checkNull(enumfunctiontype1, "Unknown function: " + token);
         return enumfunctiontype1;
      } else {
         FunctionType functionType = FunctionType.parse(token.getText());
         if (functionType == null) {
            return null;
         } else if (functionType.getParameterCount(new IExpression[0]) > 0) {
            throw new ParseException("Missing arguments: " + functionType);
         } else {
            return functionType;
         }
      }
   }

   public IExpression parse(String str) throws ParseException {
      try {
         Token[] atoken = TokenParser.parse(str);
         if (atoken == null) {
            return null;
         }

         ArrayDeque arrayDeque = new ArrayDeque<>(Arrays.asList(atoken));
         return this.parseInfix(arrayDeque);
      } catch (IOException ioException) {
         throw new ParseException(ioException.getMessage(), ioException);
      }
   }

   private static void checkNull(Object obj, String message) throws ParseException {
      if (obj == null) {
         throw new ParseException(message);
      }
   }

   private IExpression parseExpression(Deque<Token> deque) throws ParseException {
      Token token = deque.poll();
      checkNull(token, "Missing expression");
      switch (token.getType()) {
         case NUMBER:
            return makeConstantFloat(token);
         case IDENTIFIER:
            FunctionType functionType = this.getFunctionType(token, deque);
            if (functionType != null) {
               return this.makeFunction(functionType, deque);
            }

            return this.makeVariable(token);
         case BRACKET_OPEN:
            return this.makeBracketed(token, deque);
         case OPERATOR:
            FunctionType functionType2 = FunctionType.parse(token.getText());
            checkNull(functionType2, "Invalid operator: " + token);
            if (functionType2 == FunctionType.PLUS) {
               return this.parseExpression(deque);
            } else if (functionType2 == FunctionType.MINUS) {
               IExpression expression = this.parseExpression(deque);
               return makeFunction(FunctionType.NEG, new IExpression[]{expression});
            } else if (functionType2 == FunctionType.NOT) {
               IExpression expression2 = this.parseExpression(deque);
               return makeFunction(FunctionType.NOT, new IExpression[]{expression2});
            }
         default:
            throw new ParseException("Invalid expression: " + token);
      }
   }

   private IExpression parseInfix(Deque<Token> deque) throws ParseException {
      if (deque.isEmpty()) {
         return null;
      }

      LinkedList linkedList = new LinkedList();
      LinkedList linkedList2 = new LinkedList();
      IExpression expression = this.parseExpression(deque);
      checkNull(expression, "Missing expression");
      linkedList.add(expression);

      while (true) {
         Token token = deque.poll();
         if (token == null) {
            return this.makeInfix(linkedList, linkedList2);
         }

         if (token.getType() != TokenType.OPERATOR) {
            throw new ParseException("Invalid operator: " + token);
         }

         IExpression expression2 = this.parseExpression(deque);
         checkNull(expression2, "Missing expression");
         linkedList2.add(token);
         linkedList.add(expression2);
      }
   }

   private IExpression[] parseExpressions(Deque<Token> deque) throws ParseException {
      ArrayList arrayList = new ArrayList();

      while (true) {
         Deque group = getGroup(deque, TokenType.COMMA, false);
         IExpression iExpression = this.parseInfix(group);
         if (iExpression == null) {
            return (IExpression[]) arrayList.toArray(new IExpression[arrayList.size()]);
         }

         arrayList.add(iExpression);
      }
   }

   public ExpressionParser(IExpressionResolver expressionResolver) {
      this.expressionResolver = expressionResolver;
   }
}
