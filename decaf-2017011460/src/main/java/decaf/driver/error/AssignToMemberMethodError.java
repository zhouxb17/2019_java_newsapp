package decaf.driver.error;

import decaf.frontend.tree.Pos;

public class AssignToMemberMethodError extends DecafError {
    public String methodName;
    public AssignToMemberMethodError(Pos pos, String name) {
        super(pos);
        this.methodName = name;
    }

    @Override
    protected String getErrMsg() {
        return "cannot assign value to class member method '" + methodName + "'";
    }
}
