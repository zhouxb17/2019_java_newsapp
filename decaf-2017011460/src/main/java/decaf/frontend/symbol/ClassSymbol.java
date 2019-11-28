package decaf.frontend.symbol;

import decaf.frontend.scope.ClassScope;
import decaf.frontend.scope.GlobalScope;
import decaf.frontend.tree.Pos;
import decaf.frontend.type.ClassType;

import java.util.List;
import java.util.Optional;

/**
 * Class symbol, representing a class definition.
 */
public final class ClassSymbol extends Symbol {

    public final Optional<ClassSymbol> parentSymbol;

    public final ClassType type;

    /**
     * Associated class scope of this class.
     */
    public final ClassScope scope;

    public ClassSymbol(String name, ClassType type, ClassScope scope, Pos pos) {
        super(name, type, pos);
        this.parentSymbol = Optional.empty();
        this.scope = scope;
        this.type = type;
        scope.setOwner(this);
    }

    public ClassSymbol(String name, ClassSymbol parentSymbol, ClassType type, ClassScope scope, Pos pos) {
        super(name, type, pos);
        this.parentSymbol = Optional.of(parentSymbol);
        this.scope = scope;
        this.type = type;
        scope.setOwner(this);
    }

    @Override
    public GlobalScope domain() {
        return (GlobalScope) definedIn;
    }

    @Override
    public boolean isClassSymbol() {
        return true;
    }

    /**
     * Set as main class, by {@link decaf.frontend.typecheck.Namer}.
     */
    public void setMainClass() {
        main = true;
    }

    /**
     * Is it a main function?
     *
     * @return true/false
     */
    public boolean isMainClass() {
        return main;
    }

    //更改输出语句
    @Override
    protected String str() {
        String prefix = isAbstract ? "ABSTRACT " : "";
        return prefix + "class " + name + parentSymbol.map(classSymbol -> " : " + classSymbol.name).orElse("");
    }

    private boolean main;

    public boolean isAbstract;

    /*
       记录类中新定义的抽象方法和父类中没有被继承的抽象方法
       如果遍历后不为空，说明有新定义的抽象方法或父类中存在抽象方法没有被继承
       如果此时该类没有被声明为Abstract，则应该报错
     */
    public List<String> abstractMethodList;
}
