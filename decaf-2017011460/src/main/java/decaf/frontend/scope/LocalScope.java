package decaf.frontend.scope;

import java.util.ArrayList;
import java.util.List;

/**
 * Local scope: stores locally-defined variables.
 */
public class LocalScope extends Scope {

    public LocalScope(Scope parent) {
        super(Kind.LOCAL);
        assert parent.isFormalOrLocalOrLambdaScope();
        if (parent.isFormalScope()) {
            ((FormalScope) parent).setNested(this);
        } else if(parent.isLambdaScope()) {
            ((LambdaScope) parent).setNested(this);
        } else {
            ((LocalScope) parent).nested.add(this);
        }
    }

    @Override
    public boolean isLocalScope() {
        return true;
    }

    /**
     * Collect all local scopes defined inside this scope.
     *
     * @return local scopes
     */
    public List<Scope> nestedLocalOrLambdaScopes() {
        return nested;
    }

    public List<Scope> nested = new ArrayList<>();
}
