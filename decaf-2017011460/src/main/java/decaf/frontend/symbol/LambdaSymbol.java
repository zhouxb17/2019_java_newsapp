package decaf.frontend.symbol;

import decaf.frontend.scope.LambdaScope;
import decaf.frontend.tree.Pos;
import decaf.frontend.type.FunType;

/**
 * Method symbol, representing a method definition.
 */
public final class LambdaSymbol extends Symbol {

    public final FunType type;

    /**
     * Associated formal scope of the method parameters.
     */
    public final LambdaScope scope;

    public LambdaSymbol(FunType type, LambdaScope scope, Pos pos) {
        super("lambda@" + pos, type, pos);
        this.type = type;
        this.scope = scope;
        scope.setOwner(this);
    }

    @Override
    public boolean isLambdaSymbol() {
        return true;
    }

    @Override
    protected String str() {
        return String.format("function %s : %s", name, type);
    }
}
