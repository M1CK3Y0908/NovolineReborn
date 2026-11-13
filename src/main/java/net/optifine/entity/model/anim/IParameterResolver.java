package net.optifine.entity.model.anim;

import net.optifine.expr.IExpression;

public interface IParameterResolver {
   IExpression getParameter(String text);
}
