package net.optifine.entity.model.anim;

public class FunctionFloat implements IExpressionFloat {
    private FunctionType type;
    private IExpression[] arguments;

    public FunctionFloat(FunctionType type, IExpression[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Override
    public float eval() {
        return this.type.evalFloat(this.arguments);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FLOAT;
    }

    @Override
    public String toString() {
        return "" + this.type + "()";
    }
}
