package decaf.driver.error;

import decaf.frontend.tree.Pos;

/**
 * example：incompatible operands: int + bool<br>
 * PA2
 */
public class IncompatRetTypeError extends DecafError {
    public IncompatRetTypeError(Pos pos) {
        super(pos);
    }

    @Override
    protected String getErrMsg() {
        return "incompatible return types in blocked expression";
    }
}
