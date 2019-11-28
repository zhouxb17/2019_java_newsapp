package decaf.driver.error;

import decaf.frontend.tree.Pos;

public class AbstractClassNotSatisfied extends DecafError{
    private String name;
    public AbstractClassNotSatisfied(Pos pos, String name) {
        super(pos);
        this.name = name;
    }

    @Override
    protected String getErrMsg() {
        return "'" + name + "' is not abstract and does not override all abstract methods";
    }
}

