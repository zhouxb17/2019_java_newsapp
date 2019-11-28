package decaf.frontend.typecheck;

import decaf.driver.Config;
import decaf.driver.Phase;
import decaf.driver.error.*;
import decaf.frontend.scope.LambdaScope;
import decaf.frontend.scope.Scope;
import decaf.frontend.scope.ScopeStack;
import decaf.frontend.symbol.ClassSymbol;
import decaf.frontend.symbol.LambdaSymbol;
import decaf.frontend.symbol.MethodSymbol;
import decaf.frontend.symbol.VarSymbol;
import decaf.frontend.tree.Pos;
import decaf.frontend.tree.Tree;
import decaf.frontend.type.*;
import decaf.lowlevel.log.IndentPrinter;
import decaf.printing.PrettyScope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * The typer phase: type check abstract syntax tree and annotate nodes with inferred (and checked) types.
 */
public class Typer extends Phase<Tree.TopLevel, Tree.TopLevel> implements TypeLitVisited {

    public Typer(Config config) {
        super("typer", config);
    }

    @Override
    public Tree.TopLevel transform(Tree.TopLevel tree) {
        var ctx = new ScopeStack(tree.globalScope);
        tree.accept(this, ctx);
        return tree;
    }

    @Override
    public void onSucceed(Tree.TopLevel tree) {
        if (config.target.equals(Config.Target.PA2)) {
            var printer = new PrettyScope(new IndentPrinter(config.output));
            printer.pretty(tree.globalScope);
            printer.flush();
        }
    }

    @Override
    public void visitTopLevel(Tree.TopLevel program, ScopeStack ctx) {
        for (var clazz : program.classes) {
            clazz.accept(this, ctx);
        }
    }

    @Override
    public void visitClassDef(Tree.ClassDef clazz, ScopeStack ctx) {
        ctx.open(clazz.symbol.scope);
        for (var field : clazz.fields) {
            field.accept(this, ctx);
        }
        ctx.close();
    }

    @Override
    public void visitMethodDef(Tree.MethodDef method, ScopeStack ctx) {
        ctx.open(method.symbol.scope);
        if (method.body != null) {
            method.body.accept(this, ctx);
        }
        if (method.body != null && !method.symbol.type.returnType.isVoidType() && !method.body.returns) {
            issue(new MissingReturnError(method.body.pos));
        }
        ctx.close();
    }

    /**
     * To determine if a break statement is legal or not, we need to know if we are inside a loop, i.e.
     * loopLevel {@literal >} 1?
     * <p>
     * Increase this counter when entering a loop, and decrease it when leaving a loop.
     */
    private int loopLevel = 0;

    @Override
    public void visitLambda(Tree.Lambda lambda, ScopeStack ctx) {
        // 对expr或block进行遍历
        // 根据typecheck后的expr或block确定函数返回的类型
        if (lambda.lambdaScope == null) {
            System.out.println("FUCK");
        }
        ctx.open(lambda.lambdaScope);
        if (lambda.expr != null) {
            assert lambda.localScope != null;
            ctx.open(lambda.localScope);
            lambda.expr.accept(this, ctx);
            ctx.close();
            lambda.type = new FunType(lambda.expr.type, lambda.argTypes);
        } else {
            // 创建returnTypes List, 用于记录block accept的过程中所有返回的returnType
            // 然后在 getLambdaBlockReturnType 中根据此 list 进行类型推导
            lambda.lambdaScope.returnTypes = new ArrayList<>();
            lambda.block.accept(this, ctx);
            // 如果没有返回语句，return BuildInType.Void, 否则，进行最小上界类型推导
            Type blockReturnType = (lambda.lambdaScope.returnTypes.isEmpty()) ?
                    BuiltInType.VOID : upBound(lambda.lambdaScope.returnTypes);
            if (!blockReturnType.isVoidType() && !lambda.block.returns) {
                issue(new MissingReturnError(lambda.block.pos));
            }
            if(blockReturnType.eq(BuiltInType.ERROR)) {	//返回类型存在问题
                issue(new IncompatRetTypeError(lambda.block.pos));
            }
            lambda.type = new FunType(blockReturnType, lambda.argTypes);
            System.out.println("my Type: " + (FunType)lambda.type);
        }
        ctx.close();

        // 确定好函数返回类型，建立lambda symbol
        lambda.symbol = new LambdaSymbol((FunType)lambda.type, lambda.lambdaScope, lambda.pos);
        ctx.declare(lambda.symbol);
    }

    // 求List<Type> T的最小上界
    public Type upBound(List<Type> T) {
        Type type = null;
        // 选取非空t_k
        for (var t : T) {
            if (!t.eq(BuiltInType.NULL)) {
                type = t;
                break;
            }
        }

        if (type == null || type.eq(BuiltInType.NULL)) {
            // 所有return类型均为 BuiltInType.NULL
            return BuiltInType.NULL;
        } else if (type.isVoidType() || type.isBaseType() || type.isArrayType()) {
            // INT BOOL STRING VOID 四个基本类型或数组
            for (var t : T) {
                // 如果存在return类型不相同，报类型不兼容错误
                if (!type.eq(t)) {
                    return BuiltInType.ERROR;
                }
            }
            // 所有类型相同，返回该类型
            return type;
        } else if (type.isClassType()) {
            // ClassType
            // 判断是否所有返回类型都 <：t_k
            while (true) {
                if (judgeMiniAncestor(T, type)){
                    // type即为最小祖先
                    return type;
                } else {
                    if (((ClassType)type).superType.isPresent()){
                        // type不是最小祖先但还有父类
                        type = ((ClassType)type).superType.get();
                    } else {
                        // type不是最小祖先且没有父类
                        return BuiltInType.ERROR;
                    }
                }
            }
        } else {
            // FunType
            var funType =(FunType)type;
            var retList = new ArrayList<Type>();
            // 第i个成员为 所有 返回值的argList的第i个值 组成的list
            var argLists = new ArrayList<ArrayList<Type>>();
            var argNum  = funType.arity();

            // 初始化 argLists
            for (int i = 0;i < argNum;i ++){
                argLists.add(new ArrayList<Type>());
            }

            for (var t : T) {
                // 检查所有参数类型是否相同，argNum是否相同
                if (!t.isFuncType() || ((FunType)t).arity() != argNum) {
                    return BuiltInType.ERROR;
                }

                // 整合返回值列表和参数列表
                var tt = (FunType)t;
                retList.add(tt.returnType);
                for (int i = 0;i < argNum; i ++){
                    argLists.get(i).add(tt.argTypes.get(i));
                }
            }
            // 求返回值上界
            var returnType = upBound(retList);
            if (returnType.eq(BuiltInType.ERROR)) {
                return BuiltInType.ERROR;
            }
            var retArgList = new ArrayList<Type>();
            for (int i = 0;i < argNum; i ++) {
                // 求参数列表中每个位置的参数的下界
                var arg = downBound(argLists.get(i));
                if (arg.eq(BuiltInType.ERROR)) {
                    return  BuiltInType.ERROR;
                }
                retArgList.add(arg);
            }
            return new FunType(returnType, retArgList);
        }
        // 所有返回类型为BuiltInType.NULL，则返回类型为NULL
    }

    public boolean judgeMiniAncestor(List<Type> T, Type type) {
        for (var t : T){
            if (!t.subtypeOf(type)) {
                return false;
            }
        }
        return true;
    }

    // 求List<Type> T的最大下界
    public Type downBound(List<Type> T) {
        Type type = null;
        // 选取非空t_k
        for (var t : T) {
            if (!t.eq(BuiltInType.NULL)) {
                type = t;
                break;
            }
        }

        if (type == null || type.eq(BuiltInType.NULL)) {
            // 所有return类型均为 BuiltInType.NULL
            return BuiltInType.NULL;
        } else if (type.isVoidType() || type.isBaseType() || type.isArrayType()) {
            // INT BOOL STRING VOID 四个基本类型或数组
            for (var t : T) {
                // 如果存在return类型不相同，报类型不兼容错误
                if (!type.eq(t)) {
                    return BuiltInType.ERROR;
                }
            }
            // 所有类型相同，返回该类型
            return type;
        } else if (type.isClassType()) {
            // ClassType
            // 判断是否所有返回类型都 <：t_k
            for (var t:T) {
                if (t.subtypeOf(type)){
                    type = t;
                } else if (type.subtypeOf(t)) {
                    // do nothing
                } else {
                    // t 和 type 没有共同下界
                    return BuiltInType.ERROR;
                }
            }
            return type;
        } else {
            // FunType
            var funType =(FunType)type;
            var retList = new ArrayList<Type>();
            // 第i个成员为 所有 返回值的argList的第i个值 组成的list
            var argLists = new ArrayList<ArrayList<Type>>();
            var argNum  = funType.arity();

            // 初始化 argLists
            for (int i = 0;i < argNum;i ++){
                argLists.add(new ArrayList<Type>());
            }

            for (var t : T) {
                // 检查所有参数类型是否相同，argNum是否相同
                if (!t.isFuncType() || ((FunType)t).arity() != argNum) {
                    return BuiltInType.ERROR;
                }

                // 整合返回值列表和参数列表
                var tt = (FunType)t;
                retList.add(tt.returnType);
                for (int i = 0;i < argNum; i ++){
                    argLists.get(i).add(tt.argTypes.get(i));
                }
            }
            // 求返回值下界
            var returnType = downBound(retList);
            if (returnType.eq(BuiltInType.ERROR)) {
                return BuiltInType.ERROR;
            }
            var retArgList = new ArrayList<Type>();
            for (int i = 0;i < argNum; i ++) {
                // 求参数列表中每个位置的参数的上界
                var arg = upBound(argLists.get(i));
                if (arg.eq(BuiltInType.ERROR)) {
                    return  BuiltInType.ERROR;
                }
                retArgList.add(arg);
            }
            return new FunType(returnType, retArgList);
        }
    }

    @Override
    public void visitBlock(Tree.Block block, ScopeStack ctx) {
        ctx.open(block.scope);
        for (var stmt : block.stmts) {
            stmt.accept(this, ctx);
        }
        ctx.close();
        block.returns = !block.stmts.isEmpty() && block.stmts.get(block.stmts.size() - 1).returns;
    }

    @Override
    public void visitAssign(Tree.Assign stmt, ScopeStack ctx) {
        stmt.lhs.accept(this, ctx);
        stmt.rhs.accept(this, ctx);
        var lt = stmt.lhs.type;
        var rt = stmt.rhs.type;
        // 不能对成员方法赋值
        if(stmt.lhs instanceof Tree.VarSel && ((Tree.VarSel)stmt.lhs).isMemberFuncName) {
            issue(new AssignToMemberMethodError(stmt.pos, ((Tree.VarSel)stmt.lhs).name));
        }

        // 错误类型判断，类型不兼容
        if (lt.noError() && (!rt.subtypeOf(lt))) {
            issue(new IncompatBinOpError(stmt.pos, lt.toString(), "=", rt.toString()));
        }
        if(lt.noError()){
            var currFuncScope = ctx.nearestFormalOrLambdaScope();
            if(currFuncScope.isLambdaScope()&& stmt.lhs instanceof Tree.VarSel){
                //直接赋值的情况（没有引用）
                if(((Tree.VarSel) stmt.lhs).receiver.isEmpty()){
                    ListIterator<Scope> iter = ctx.scopeStack.listIterator(ctx.scopeStack.size());
                    while(iter.hasPrevious()){
                        var scope =iter.previous();
                        if(scope == currFuncScope){
                            break;
                        }
                    }
                    while (iter.hasPrevious()){
                        var scope = iter.previous();
                        if(!scope.isClassScope() && ((Tree.VarSel) stmt.lhs).symbol.domain()==scope){
                            issue(new AssignToCapturedVarError(stmt.pos));
                            break;
                        }
                    }
                }
            }
        }
    }
//    public void visitAssign(Tree.Assign stmt, ScopeStack ctx) {
//        stmt.lhs.accept(this, ctx);
//        stmt.rhs.accept(this, ctx);
//        var lt = stmt.lhs.type;
//        var rt = stmt.rhs.type;
//
//        if (lt.noError() && (lt.isFuncType() || !rt.subtypeOf(lt))) {
//            issue(new IncompatBinOpError(stmt.pos, lt.toString(), "=", rt.toString()));
//        }
//    }

    @Override
    public void visitExprEval(Tree.ExprEval stmt, ScopeStack ctx) {
        stmt.expr.accept(this, ctx);
    }


    @Override
    public void visitIf(Tree.If stmt, ScopeStack ctx) {
        checkTestExpr(stmt.cond, ctx);
        stmt.trueBranch.accept(this, ctx);
        stmt.falseBranch.ifPresent(b -> b.accept(this, ctx));
        // if-stmt returns a value iff both branches return
        stmt.returns = stmt.trueBranch.returns && stmt.falseBranch.isPresent() && stmt.falseBranch.get().returns;
    }

    @Override
    public void visitWhile(Tree.While loop, ScopeStack ctx) {
        checkTestExpr(loop.cond, ctx);
        loopLevel++;
        loop.body.accept(this, ctx);
        loopLevel--;
    }

    @Override
    public void visitFor(Tree.For loop, ScopeStack ctx) {
        ctx.open(loop.scope);
        loop.init.accept(this, ctx);
        checkTestExpr(loop.cond, ctx);
        loop.update.accept(this, ctx);
        loopLevel++;
        for (var stmt : loop.body.stmts) {
            stmt.accept(this, ctx);
        }
        loopLevel--;
        ctx.close();
    }

    @Override
    public void visitBreak(Tree.Break stmt, ScopeStack ctx) {
        if (loopLevel == 0) {
            issue(new BreakOutOfLoopError(stmt.pos));
        }
    }

    @Override
    public void visitReturn(Tree.Return stmt, ScopeStack ctx) {
        // 更改visitReturn，使得
        // 对正常的FormalScope中，因为已经确定了函数类型，直接返回
        // 对新加的LambdaScope中，将返回值添加到该Scope的returnList中，然后进行统一判断
        var scope = ctx.nearestFormalOrLambdaScope();
        assert scope != null;
        // 如果为 FunType，继续递归求得返回值
        stmt.expr.ifPresent(e -> e.accept(this, ctx));
        var actual = stmt.expr.map(e -> e.type).orElse(BuiltInType.VOID);
        if (scope.isFormalScope()) {
            // 正常类型
            var expected = ctx.currentMethod().type.returnType;
            if (actual.noError() && !actual.subtypeOf(expected)) {
                issue(new BadReturnTypeError(stmt.pos, expected.toString(), actual.toString()));
            }
        } else {
            ((LambdaScope)scope).returnTypes.add(actual);
        }
        stmt.returns = stmt.expr.isPresent();
    }

    @Override
    public void visitPrint(Tree.Print stmt, ScopeStack ctx) {
        int i = 0;
        for (var expr : stmt.exprs) {
            expr.accept(this, ctx);
            i++;
            if (expr.type != null && expr.type.noError() && !expr.type.isBaseType()) {
                issue(new BadPrintArgError(expr.pos, Integer.toString(i), expr.type.toString()));
            }
        }
    }

    private void checkTestExpr(Tree.Expr expr, ScopeStack ctx) {
        expr.accept(this, ctx);
        if (expr.type.noError() && !expr.type.eq(BuiltInType.BOOL)) {
            issue(new BadTestExpr(expr.pos));
        }
    }

    // Expressions

    @Override
    public void visitIntLit(Tree.IntLit that, ScopeStack ctx) {
        that.type = BuiltInType.INT;
    }

    @Override
    public void visitBoolLit(Tree.BoolLit that, ScopeStack ctx) {
        that.type = BuiltInType.BOOL;
    }

    @Override
    public void visitStringLit(Tree.StringLit that, ScopeStack ctx) {
        that.type = BuiltInType.STRING;
    }

    @Override
    public void visitNullLit(Tree.NullLit that, ScopeStack ctx) {
        that.type = BuiltInType.NULL;
    }

    @Override
    public void visitReadInt(Tree.ReadInt readInt, ScopeStack ctx) {
        readInt.type = BuiltInType.INT;
    }

    @Override
    public void visitReadLine(Tree.ReadLine readStringExpr, ScopeStack ctx) {
        readStringExpr.type = BuiltInType.STRING;
    }

    @Override
    public void visitUnary(Tree.Unary expr, ScopeStack ctx) {
        expr.operand.accept(this, ctx);
        var t = expr.operand.type;
        if (t.noError() && !compatible(expr.op, t)) {
            // Only report this error when the operand has no error, to avoid nested errors flushing.
            issue(new IncompatUnOpError(expr.pos, Tree.opStr(expr.op), t.toString()));
        }

        // Even when it doesn't type check, we could make a fair guess based on the operator kind.
        // Let's say the operator is `-`, then one possibly wants an integer as the operand.
        // Once he/she fixes the operand, according to our type inference rule, the whole unary expression
        // must have type int! Thus, we simply _assume_ it has type int, rather than `NoType`.
        expr.type = resultTypeOf(expr.op);
    }

    public boolean compatible(Tree.UnaryOp op, Type operand) {
        return switch (op) {
            case NEG -> operand.eq(BuiltInType.INT); // if e : int, then -e : int
            case NOT -> operand.eq(BuiltInType.BOOL); // if e : bool, then !e : bool
        };
    }

    public Type resultTypeOf(Tree.UnaryOp op) {
        return switch (op) {
            case NEG -> BuiltInType.INT;
            case NOT -> BuiltInType.BOOL;
        };
    }

    @Override
    public void visitBinary(Tree.Binary expr, ScopeStack ctx) {
        expr.lhs.accept(this, ctx);
        expr.rhs.accept(this, ctx);
        var t1 = expr.lhs.type;
        var t2 = expr.rhs.type;
        if (t1.noError() && t2.noError() && !compatible(expr.op, t1, t2)) {
            issue(new IncompatBinOpError(expr.pos, t1.toString(), Tree.opStr(expr.op), t2.toString()));
        }
        expr.type = resultTypeOf(expr.op);
    }

    public boolean compatible(Tree.BinaryOp op, Type lhs, Type rhs) {
        if (op.compareTo(Tree.BinaryOp.ADD) >= 0 && op.compareTo(Tree.BinaryOp.MOD) <= 0) { // arith
            // if e1, e2 : int, then e1 + e2 : int
            return lhs.eq(BuiltInType.INT) && rhs.eq(BuiltInType.INT);
        }

        if (op.equals(Tree.BinaryOp.AND) || op.equals(Tree.BinaryOp.OR)) { // logic
            // if e1, e2 : bool, then e1 && e2 : bool
            return lhs.eq(BuiltInType.BOOL) && rhs.eq(BuiltInType.BOOL);
        }

        if (op.equals(Tree.BinaryOp.EQ) || op.equals(Tree.BinaryOp.NE)) { // eq
            // if e1 : T1, e2 : T2, T1 <: T2 or T2 <: T1, then e1 == e2 : bool
            return lhs.subtypeOf(rhs) || rhs.subtypeOf(lhs);
        }

        // compare
        // if e1, e2 : int, then e1 > e2 : bool
        return lhs.eq(BuiltInType.INT) && rhs.eq(BuiltInType.INT);
    }

    public Type resultTypeOf(Tree.BinaryOp op) {
        if (op.compareTo(Tree.BinaryOp.ADD) >= 0 && op.compareTo(Tree.BinaryOp.MOD) <= 0) { // arith
            return BuiltInType.INT;
        }
        return BuiltInType.BOOL;
    }

    @Override
    public void visitNewArray(Tree.NewArray expr, ScopeStack ctx) {
        expr.elemType.accept(this, ctx);
        expr.length.accept(this, ctx);
        var et = expr.elemType.type;
        var lt = expr.length.type;

        if (et.isVoidType()) {
            issue(new BadArrElementError(expr.elemType.pos));
            expr.type = BuiltInType.ERROR;
        } else {
            expr.type = new ArrayType(et);
        }

        if (lt.noError() && !lt.eq(BuiltInType.INT)) {
            issue(new BadNewArrayLength(expr.length.pos));
        }
    }

    @Override
    public void visitNewClass(Tree.NewClass expr, ScopeStack ctx) {
        var clazz = ctx.lookupClass(expr.clazz.name);
        if (clazz.isPresent()) {
            //如果新建的这个类不是抽象的，则正常声明
            if(!clazz.get().isAbstract) {
                expr.symbol = clazz.get();
                expr.type = expr.symbol.type;
            } else {//否则需要报错
                issue(new NewAbstractMainError(expr.pos, expr.clazz.name));
                expr.type = BuiltInType.ERROR;
            }
        } else {
            issue(new ClassNotFoundError(expr.pos, expr.clazz.name));
            expr.type = BuiltInType.ERROR;
        }
    }

    @Override
    public void visitThis(Tree.This expr, ScopeStack ctx) {
        if (ctx.currentMethod().isStatic()) {
            issue(new ThisInStaticFuncError(expr.pos));
        }
        expr.type = ctx.currentClass().type;
    }

    private boolean allowClassNameVar = false;

    @Override
    public void visitVarSel(Tree.VarSel expr, ScopeStack ctx) {
        // has not receiver
        if (expr.receiver.isEmpty()) {
            // 当前varsel 已经到达了最顶头，没有receiver
            // Variable, which should be complicated since a legal variable could refer to a local var,
            // a visible member var, and a class name.
            var symbol = ctx.lookupBefore(expr.name, localVarDefPos.orElse(expr.pos));
            if (symbol.isPresent() && !definingVariable.contains(symbol.get().name)) {
                // 对普通变量引用
                if (symbol.get().isVarSymbol()) {
                    var var = (VarSymbol) symbol.get();
                    expr.symbol = var;
                    expr.type = var.type;
                    if (var.isMemberVar()) {
                        // var 确实是成员变量
                        if (ctx.currentMethod().isStatic()) {
                            // static方法中调用非静态变量
                            issue(new RefNonStaticError(expr.pos, ctx.currentMethod().name, expr.name));
                        } else {
                            expr.setThis();
                        }
                    }
                    return;
                }
                // 对类名引用
                if (symbol.get().isClassSymbol() && allowClassNameVar) { // special case: a class name
                    var clazz = (ClassSymbol) symbol.get();
                    expr.type = clazz.type;
                    expr.isClassName = true;
                    return;
                }
                // 对函数/方法名引用
                if (symbol.get().isMethodSymbol()) {
                    var func=(MethodSymbol)symbol.get();
                    expr.symbol=func;
                    expr.type=func.type;
                    if (func.isMemberFunc()) {
                        expr.isMemberFuncName = true;
                        if (ctx.currentMethod().isStatic() && !func.isStatic()) {
                            // static方法中调用非静态方法
                            issue(new RefNonStaticError(expr.pos, ctx.currentMethod().name, expr.name));
                        } else {
                            expr.setThis();
                        }
                    }
                    return;
                }
            }

            expr.type = BuiltInType.ERROR;
            issue(new UndeclVarError(expr.pos, expr.name));
            return;
        }

        // has receiver
        var receiver = expr.receiver.get();
        allowClassNameVar = true;
        receiver.accept(this, ctx);
        allowClassNameVar = false;
        var rt = receiver.type;
        expr.type = BuiltInType.ERROR;

        // receiver type 存在问题
        if (!rt.noError()) {
            return;
        }

        // 注意下面两个判断条件的顺序问题，需要先进行array.length特判, 再判断是否reciever为class
        // 对array.length的处理：
        if (rt.isArrayType() && expr.name.equals("length")) { // Special case: array.length()
            expr.type = new FunType(BuiltInType.INT,new ArrayList<Type>());
            expr.isArrayLength = true;
            return;
        }

        // receiver不是Class，报错
        if (!rt.isClassType()) {
            issue(new NotClassFieldError(expr.pos, expr.name, rt.toString()));
            return;
        }

        // 向下类型转换
        var ct = (ClassType) rt;
        // 得到当前varsel绑定的scope
        var field = ctx.getClass(ct.name).scope.lookup(expr.name);

        //控制权限 如果是成员变量或非静态成员函数 不允许通过类名访问
        if (receiver instanceof Tree.VarSel) {
            var v1 = (Tree.VarSel) receiver;
            if (v1.isClassName) {
                // 针对测例 fun-var-ok-1
                if(field.isPresent() && (field.get().isVarSymbol()||(field.get().isMethodSymbol() && !((MethodSymbol)field.get()).isStatic()))) {
                    // special case like MyClass.foo: report error cannot access field 'foo' from 'class : MyClass'
                    issue(new NotClassFieldError(expr.pos, expr.name, ctx.getClass(v1.name).type.toString()));
                    return;
                }
            }
        }

        // 语法规范：不允许通过类名访问其他类成员变量或非静态成员函数
        if (field.isPresent() && field.get().isVarSymbol()) {
            var var = (VarSymbol) field.get();
            if (var.isMemberVar()) {
                expr.symbol = var;
                expr.type = var.type;
                if (!ctx.currentClass().type.subtypeOf(var.getOwner().type)) {
                    // 不能接触其他类中成员变量
                    issue(new FieldNotAccessError(expr.pos, expr.name, ct.toString()));
                }
            }
        } else if (field.isPresent() && field.get().isMethodSymbol()) {
            var func = (MethodSymbol) field.get();
            if (func.isMemberFunc()) {
                expr.symbol = func;
                expr.type = func.type;
                expr.isMemberFuncName = true;
            }
        } else if (field.isEmpty()) {
            issue(new FieldNotFoundError(expr.pos, expr.name, ct.toString()));
        } else {
            issue(new NotClassFieldError(expr.pos, expr.name, ct.toString()));
        }
    }

    @Override
    public void visitIndexSel(Tree.IndexSel expr, ScopeStack ctx) {
        expr.array.accept(this, ctx);
        expr.index.accept(this, ctx);
        var at = expr.array.type;
        var it = expr.index.type;

        // []前ArrayType
        if (!at.noError()) {
            expr.type = BuiltInType.ERROR;
            return;
        }

        if (!at.isArrayType()) {
            issue(new NotArrayError(expr.array.pos));
            expr.type = BuiltInType.ERROR;
            return;
        }

        expr.type = ((ArrayType) at).elementType;
        if (!it.eq(BuiltInType.INT)) {
            issue(new SubNotIntError(expr.pos));
        }
    }

    @Override
    public void visitCall(Tree.Call expr, ScopeStack ctx) {
        // accept receiver
        expr.func.accept(this, ctx);
        // 是否函数返回类型推导出错
        if (!expr.func.type.noError()) {
            expr.type = BuiltInType.ERROR;
            return;
        }
        // 是否确实为函数类型, 不是则不可调用
        if(!expr.func.type.isFuncType()) {
            issue(new NotCallableError(expr.pos, expr.func.type.toString()));
            expr.type=BuiltInType.ERROR;
            return;
        }
        // 是否为特例函数 array.length
        if(expr.func instanceof Tree.VarSel && ((Tree.VarSel)expr.func).isArrayLength) {
            if(!expr.args.isEmpty()) {
                issue(new BadLengthArgError(expr.pos, expr.args.size()));
            }
            expr.type=BuiltInType.INT;
            expr.isArrayLength = true;
            return;
        }

        // 正常函数，进行返回类型和参数类型检查
        typeCall(expr, ctx);
    }

    // 对正常函数，进行返回类型和参数类型检查
    private void typeCall(Tree.Call call, ScopeStack ctx) {
        // 根据varsel类型确定call的返回类型
        call.type=((FunType)call.func.type).returnType;

        // TODO 返回类型应为函数类型，包括两种，正常函数和Lambda函数
        // 此处添加判断，用于判断是否是lambda类型
        boolean isLambda = false;
        // typing args
        var args = call.args;
        for (var arg : args) {
            arg.accept(this, ctx);
        }

        // check signature compatibility
        var type = (FunType)call.func.type;
        // 检查参数个数是否正确
        if (type.arity() != args.size()) {
            var _lambda = !(call.func instanceof Tree.VarSel);
            var _name = _lambda ? "": ((Tree.VarSel)call.func).name;
            issue(new BadArgCountError(call.pos, _name, type.arity(), args.size(), _lambda));
        }
        // 检查所需参数和传进参数是否匹配
        // FunType 维护的参数列表 List<Type>
        var iter1 = type.argTypes.iterator();
        // call 解析产生的 '(' arg1 ',' arg2 ',' ... ')' List<Expr>
        var iter2 = call.args.iterator();
        for (int i = 1; iter1.hasNext() && iter2.hasNext(); i++) {
            Type t1 = iter1.next();
            Tree.Expr expr = iter2.next();
            Type t2 = expr.type;
            if (t2.noError() && !t2.subtypeOf(t1)) {
                issue(new BadArgTypeError(expr.pos, i, t2.toString(), t1.toString()));
            }
        }
    }

    @Override
    public void visitClassTest(Tree.ClassTest expr, ScopeStack ctx) {
        expr.obj.accept(this, ctx);
        expr.type = BuiltInType.BOOL;

        if (!expr.obj.type.isClassType()) {
            issue(new NotClassError(expr.obj.type.toString(), expr.pos));
        }
        var clazz = ctx.lookupClass(expr.is.name);
        if (clazz.isEmpty()) {
            issue(new ClassNotFoundError(expr.pos, expr.is.name));
        } else {
            expr.symbol = clazz.get();
        }
    }

    @Override
    public void visitClassCast(Tree.ClassCast expr, ScopeStack ctx) {
        expr.obj.accept(this, ctx);

        if (!expr.obj.type.isClassType()) {
            issue(new NotClassError(expr.obj.type.toString(), expr.pos));
        }

        var clazz = ctx.lookupClass(expr.to.name);
        if (clazz.isEmpty()) {
            issue(new ClassNotFoundError(expr.pos, expr.to.name));
            expr.type = BuiltInType.ERROR;
        } else {
            expr.symbol = clazz.get();
            expr.type = expr.symbol.type;
        }
    }

    @Override
    public void visitLocalVarDef(Tree.LocalVarDef stmt, ScopeStack ctx) {
        if (stmt.initVal.isEmpty()) return;
        // 将正在定义的变量堆入模拟栈中
        definingVariable.add(stmt.name);

        var initVal = stmt.initVal.get();
        localVarDefPos = Optional.ofNullable(stmt.id.pos);
        initVal.accept(this, ctx);
        localVarDefPos = Optional.empty();

        // 将正在定义的变量从栈中pop出
        definingVariable.remove(stmt.name);

        var lt = stmt.symbol.type;
        var rt = initVal.type;
        if (lt == null) {//左侧部分没有定义类型
            if (rt.isVoidType()) {//右侧类型被推断为void
                issue(new BadVarTypeError(stmt.id.pos, stmt.id.name));
                stmt.symbol.type = BuiltInType.ERROR;
                return;
            } else {//右侧类型是正常类型
                stmt.symbol.type = rt;
            }
        } else {//左侧部分已声明类型
            if (lt.noError() && (!rt.subtypeOf(lt))) {//如果右边类型不能和左侧兼容，报错
                issue(new IncompatBinOpError(stmt.assignPos, lt.toString(), "=", rt.toString()));
            }
        }
    }

    // Only usage: check if an initializer cyclically refers to the declared variable, e.g. var x = x + 1
    private Optional<Pos> localVarDefPos = Optional.empty();
    private List<String> definingVariable = new ArrayList<>();
}
