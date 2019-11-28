package decaf.frontend.typecheck;

import decaf.driver.ErrorIssuer;
import decaf.driver.error.VoidArgError;
import decaf.driver.error.BadArrElementError;
import decaf.driver.error.ClassNotFoundError;
import decaf.frontend.scope.ScopeStack;
import decaf.frontend.tree.Tree;
import decaf.frontend.tree.Visitor;
import decaf.frontend.type.BuiltInType;
import decaf.frontend.type.FunType;
import decaf.frontend.type.Type;

import java.util.ArrayList;

/**
 * Infer the types of type literals in the abstract syntax tree.
 * <p>
 * These visitor methods are shared by {@link Namer} and {@link Typer}.
 */
public interface TypeLitVisited extends Visitor<ScopeStack>, ErrorIssuer {

    // visiting types
    @Override
    default void visitTInt(Tree.TInt that, ScopeStack ctx) {
        that.type = BuiltInType.INT;
    }

    @Override
    default void visitTBool(Tree.TBool that, ScopeStack ctx) {
        that.type = BuiltInType.BOOL;
    }

    @Override
    default void visitTString(Tree.TString that, ScopeStack ctx) {
        that.type = BuiltInType.STRING;
    }

    @Override
    default void visitTVoid(Tree.TVoid that, ScopeStack ctx) {
        that.type = BuiltInType.VOID;
    }

    @Override
    default void visitTClass(Tree.TClass typeClass, ScopeStack ctx) {
        var c = ctx.lookupClass(typeClass.id.name);
        if (c.isEmpty()) {
            issue(new ClassNotFoundError(typeClass.pos, typeClass.id.name));
            typeClass.type = BuiltInType.ERROR;
        } else {
            typeClass.type = c.get().type;
        }
    }

    @Override
    default void visitTArray(Tree.TArray typeArray, ScopeStack ctx) {
        typeArray.elemType.accept(this, ctx);
        if (typeArray.elemType.type.eq(BuiltInType.ERROR)) {
            typeArray.type = BuiltInType.ERROR;
        } else if (typeArray.elemType.type.eq(BuiltInType.VOID)) {
            issue(new BadArrElementError(typeArray.pos));
            typeArray.type = BuiltInType.ERROR;
        } else {
            typeArray.type = new decaf.frontend.type.ArrayType(typeArray.elemType.type);
        }
    }

    @Override
    default void visitTLambda(Tree.TLambda typeLambda, ScopeStack ctx) {
        // 解析返回值
        typeLambda.returnType.accept(this, ctx);
        if (typeLambda.returnType.type.eq(BuiltInType.ERROR)) {
            typeLambda.type = BuiltInType.ERROR;
        }
        // 解析参数
        var hasError = false;
        var typeList = new ArrayList<Type>();
        for (var param : typeLambda.typeList) {
            param.accept(this, ctx);
            if(param.type.eq(BuiltInType.ERROR)) {
                typeLambda.type = BuiltInType.ERROR;
                hasError = true;
            } else if (param.type.eq(BuiltInType.VOID)){//如果参数返回类型是void，则需要报错
                typeLambda.type = BuiltInType.ERROR;
                hasError = true;
                issue(new VoidArgError(param.pos));
            } else {
                typeList.add(param.type);
            }
        }

        if (!hasError) {
            typeLambda.type = new FunType(typeLambda.returnType.type, typeList);
        }
    }
}
