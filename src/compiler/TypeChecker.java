package compiler;
import java.util.*;
import ast.*;
import entity.*;
import exception.*;
import type.*;
import utils.*;
/*
 * 静态类型检查,比如说加号两边都必须是同样的类型而且是可加的,函数调用的类型要正确.
 * 除此之外,还要完成隐式类型转换,比如我们给一个int类型变量赋值double类型会将double先转成int.
 * 对单个节点处理,类型检查只需要对各节点的限制进行检查,当检查的结果需要隐式类型转换时添加CastNode节点.
 * 
 */
public class TypeChecker extends Visitor{
	 private final TypeTable table;
	 private final ErrorHandler errorHandler;
	 public TypeChecker(TypeTable typeTable,ErrorHandler errorHandler){
		 this.table=typeTable;
		 this.errorHandler=errorHandler;
	 }
	 /*
	  * 首先检查定义的变量类型是否错误.然后检查定义的函数.
	  */
	 public void check(AST ast)throws SemanticException{
		 checkVariables(ast.definedVariables());
		 checkFunctions(ast.definedFunctions());
		 if(errorHandler.errorOccured()) {
	            throw new SemanticException("compile failed.");
		 }
	 }
	private void check(StmtNode node) {
		    visitStmt(node);
	}
	private void check(ExprNode node) {
			visitExpr(node);
	}
	 public void checkVariables(List<DefinedVariable>vars){
		 for(DefinedVariable var:vars)
		 checkVariable(var);
	 }
	 //对于有定义的变量还需要检查是否是合格的左值,初始化的表达式也要检查
	 public void checkVariable(DefinedVariable var){
		 if(isInValidVariableType(var.type())){
			 error(var.location(), "invalid variable type: "+var.type());
		 }
		 if(var.hasInitializer()){
			 if(isInvalidLHSType(var.type())){
				 error(var.location(),"invalid LHS type: " + var.type());
				 return;
			 }
			 if(!var.initializer().type().isSameType(var.type()))
				 error(var.location(),"invalid initializer: "+var);
			 check(var.initializer());
		 }
	 }
	 public void checkFunctions(List<DefinedFunction>funcs){
		 for(DefinedFunction func:funcs)
			 checkFunction(func);
	 }
	 //函数检查遇到return时要看是不是和当前检查的函数声明返回值类型一致
	 DefinedFunction currentFunction=null;
	 public void checkFunction(DefinedFunction func){
		 currentFunction=func;
		 checkReturnType(func);
		 checkParamsType(func.parameters());
		 check(func.body());
	 }
	 public void checkParamsType(List<Parameter>params){
		 for (Parameter param : params) {
	            if (isInvalidParameterType(param.type())) {
	                error(param.location(),
	                        "invalid parameter type: " + param.type());
	            }
	        }
	 }
	 public void checkReturnType(DefinedFunction func){
		 if (isInvalidReturnType(func.returnType())) {
	            error(func.location(), "returns invalid type: " +func.returnType());
	        }
	 }
	 //参数的类型要求,不能是结构体,联合体,void,非定长多维数组
	 public boolean isInvalidParameterType(Type type){
		 return type.isCompositeType() || type.isVoid()
	                || type.isIncompleteArray();
	    }
	 //返回值类型要求
	 public boolean isInvalidReturnType(Type type){
		 return (type.isCompositeType()||type.isArray());
	 }
	 //能成为语句的表达式类型不能为结构体
	 private boolean isInvalidStatementType(Type t) {
	        return t.isStruct() || t.isUnion();
	    }
	 //变量的类型限制除了void类型外
	 public boolean isInValidVariableType(Type t){
		 return t.isVoid();
	 }
	 //左值的类型约束
	 private boolean isInvalidLHSType(Type t) {
	        // Array is OK if it is declared as a type of parameter.
	        return t.isStruct() || t.isUnion() || t.isVoid() || t.isArray();
	    }
	 private boolean isInvalidRHSType(Type t) {
	        return t.isStruct() || t.isUnion() || t.isVoid();
	    }
	 //检查是否是整型
	 private boolean mustBeInteger(ExprNode expr, String op) {
	        if (! expr.type().isInteger()) {
	        	error(expr, "wrong operand type for " + op + ": " + expr.type());
	            return false;
	        }
	        return true;
	    }
	 //检查是否是标量,只有字面量才是标量.
	 private boolean mustBeScalar(ExprNode expr, String op) {
	        if (! expr.type().isScalar()) {
	            error(expr, op);
	            return false;
	        }
	        return true;
	    }
	 
	 public Void visit(CondExprNode node) {
	        super.visit(node);
	        checkCond(node.cond());
	        Type t = node.leftExpr().type();
	        Type e = node.rightExpr().type();
	        if (t.isSameType(e)) {
	            return null;
	        }
	        else {
	        	error(node,"two types not equal");
	        }
	        return null;
	    }
	 /*
	  * 函数调用检查
	  */
	 public Void visit(FunCallNode node) {
	        super.visit(node);
	        FunctionType type = node.functionType();
	        //检查参数个数是否正确
	        if (! type.acceptsArgc(node.numArgs())) {
	            error(node, "wrong number of argments: " + node.numArgs());
	            return null;
	        }
	        Iterator<ExprNode> args = node.args().iterator();
	        List<ExprNode> newArgs = new ArrayList<ExprNode>();
	        // 逐条比较函数的实参和形参的类型是否符合,得到一个新的实参列表,有些添加了强制转换.
	        for (Type param : type.paramTypes()) {
	            ExprNode arg = args.next();
	            newArgs.add(checkRHS(arg) ? implicitCast(param, arg) : arg);
	        }
	        // optional args
	        while (args.hasNext()) {
	            ExprNode arg = args.next();
	            newArgs.add(checkRHS(arg) ? castOptionalArg(arg) : arg);
	        }
	        node.replaceArgs(newArgs);
	        return null;
	    }
	 //对于可变参数列表中的实参类型,如果是整型,都转成对应的long型.
	    private ExprNode castOptionalArg(ExprNode arg) {
	        if (! arg.type().isInteger()) {
	            return arg;
	        }
	        Type t = arg.type().isSigned()
	            ? table.signedLong()
	            : table.unsignedLong();
	        return arg.type().size() < t.size() ? implicitCast(t, arg) : arg;
	    }
	    //隐式类型转换
	    private ExprNode implicitCast(Type targetType, ExprNode expr) {
	        if (expr.type().isSameType(targetType)) {
	            return expr;
	        }
	        else if (expr.type().isCastableTo(targetType)) {
	            return new CastNode(new TypeNode(targetType), expr);
	        }
	        else {
	            error(expr, "invalid cast from "+expr.type()+" to"+targetType);
	            return expr;
	        }
	    }
	    public Void visit(ArefNode node) {
	        super.visit(node);
	        mustBeInteger(node.index(), "[]");
	        return null;
	    }
	    //检查强制转换是否能发生
	    public Void visit(CastNode node) {
	        super.visit(node);
	        if (! node.expr().type().isCastableTo(node.type())) {
	            error(node, "invalid cast from "+node.expr().type()+" to "+node.type());
	        }
	        return null;
	    }
	 /*
	  * 检查赋值表达式的实现,首先检查左右类型是否是合格的类型,然后检查左右类型是否相同.
	  */
	 public Void visit(AssignNode node){
		 super.visit(node);
		 if(!checkLHS(node.lhs()))return null;
		 if(!checkRHS(node.rhs()))return null;
		 return null;
	 }
	 //参数类型一定可以当左值
	 public boolean checkLHS(ExprNode expr){
		 if(expr.isParameter())return true;
		 else if(isInvalidLHSType(expr.type())){
			 error(expr.location(),"invalid left type: "+expr.type());
			 return false;
		 }
		 return true;
	 }
	//右值检查
	public boolean checkRHS(ExprNode expr){
			 	if(isInvalidRHSType(expr.type())){
				 error(expr.location(),"invalid right type: "+expr.type());
				 return false;
			 }
			 return true;
		 }
	//复合赋值检查,复合赋值节点的符号直接用直接操作的符号而不是复合符号
	public Void visit(OpAssignNode node) {
        super.visit(node);
        if (! checkLHS(node.lhs())) return null;
        if (! checkRHS(node.rhs())) return null;
        if (node.operator().equals("+") || node.operator().equals("-")) {
        	//如果左边为指针,右边必须为整数
            if (node.lhs().type().isPointer()) {
            	if(!node.rhs().isInteger()){
    	    		error(node.rhs(), "Expression must be a Integer");
    	    	}
                node.setRHS(integralPromotedExpr(node.rhs()));
                return null;
            }
        }
        /*除了加减可以是指针外,其他情况必须是整数,首先将左右类型向上提升,
         * 按照优先级顺序强制转换.
         */
        if (! mustBeInteger(node.lhs(), node.operator())) return null;
        if (! mustBeInteger(node.rhs(), node.operator())) return null;
        Type l = integralPromotion(node.lhs().type());
        Type r = integralPromotion(node.rhs().type());
        //向上查找要转换成的共同类型
        Type opType = usualArithmeticConversion(l, r);
        if (! opType.isCompatible(l)) {
            warn(node, "incompatible implicit cast from "
                       + opType + " to " + l);
        }
        if (! r.isSameType(opType)) {
            // 将右表达式转换
            node.setRHS(new CastNode(new TypeNode(opType), node.rhs()));
        }
        return null;
    }
	//一元运算符节点检查
	public Void visit(UnaryOpNode node) {
        super.visit(node);
        if (node.operator().equals("!")) {
            mustBeScalar(node.expr(), node.operator());
        }
        else {
            mustBeInteger(node.expr(), node.operator());
        }
        return null;
    }
	// ++x, --x操作符,要给符号设定操作的整型类型.
    public Void visit(PrefixOpNode node) {
        super.visit(node);
        expectsScalarLHS(node);
        return null;
    }

    // x++, x--
    public Void visit(SuffixOpNode node) {
        super.visit(node);
        expectsScalarLHS(node);
        return null;
    }
    /*
     * 如果一元运算符作用的表达式类型是整型,则操作的长度为1,
     * 如果为指针类型,则操作的长度为指针指向类型的字节数.
     */
    private void expectsScalarLHS(UnaryArithmeticOpNode node) {
        if (!node.expr().isParameter()) {
            mustBeScalar(node.expr(), node.operator());
        }
        if (node.expr().type().isInteger()) {
            Type opType = integralPromotion(node.expr().type());
            if (! node.expr().type().isSameType(opType)) {
                node.setOpType(opType);
            }
            node.setAmount(1);
        }
        else if (node.expr().type().isPointer()) {
            node.setAmount(node.expr().type().baseType().size());
        }
        else {
            throw new Error("must not happen");
        }
    }
	/*
	 * 二元运算的节点检查,如果只能运算类符号,只能两侧都是整型,比较符号两侧都是标量.
	 */
	public Void visit(BinaryOpNode node) {
        super.visit(node);
        if (node.operator().equals("*")
                || node.operator().equals("/")
                || node.operator().equals("%")
                || node.operator().equals("&")
                || node.operator().equals("|")
                || node.operator().equals("^")
                || node.operator().equals("<<")
                || node.operator().equals(">>")
                || node.operator().equals("+")
                || node.operator().equals("-")) {
            expectsSameInteger(node);
        }
        else if (node.operator().equals("==")
                || node.operator().equals("!=")
                || node.operator().equals("<")
                || node.operator().equals("<=")
                || node.operator().equals(">")
                || node.operator().equals(">=")) {
            expectsComparableScalars(node);
        }
        else {
            throw new Error("unknown binary operator: " + node.operator());
        }
        return null;
    }
	private void expectsSameInteger(BinaryOpNode node){
		//出错立马返回,不会出现继续出错
		if(!mustBeInteger(node.left(), node.operator()))return ;
		if(!mustBeInteger(node.right(), node.operator()))return;
		arithmeticImplicitCast(node);
	}
	/*
	 * 对一个二元运算符左右两侧进行向上提升
	 */
	 private void arithmeticImplicitCast(BinaryOpNode node) {
	        Type r = integralPromotion(node.right().type());
	        Type l = integralPromotion(node.left().type());
	        Type target = usualArithmeticConversion(l, r);
	        if (! l.isSameType(target)) {
	           //左边和最终类型不符合需要提升
	            node.setLeft(new CastNode(new TypeNode(target), node.left()));
	        }
	        if (! r.isSameType(target)) {
	            
	            node.setRight(new CastNode(new TypeNode(target), node.right()));
	        }
	        //将二元运算节点的类型设置成提升后的类型
	        node.setType(target);
	    }
	 //比较类型的运算符类型检查
	 private void expectsComparableScalars(BinaryOpNode node) {
	        if (! mustBeScalar(node.left(), node.operator())) return;
	        if (! mustBeScalar(node.right(), node.operator())) return;
	        arithmeticImplicitCast(node);
	    }
	//向上提升,如果成功则添加一个强制转换的类型.
	private ExprNode integralPromotedExpr(ExprNode expr) {
        Type t = integralPromotion(expr.type());
        if (t.isSameType(expr.type())) {
            return expr;
        }
        else {
            return new CastNode(new TypeNode(t), expr);
        }
    }
	//向上转型,比int字节数小的整形都转成signed int.否则不转
	public Type integralPromotion(Type type){
		if(!type.isInteger()){
			throw new Error("integralPromotion for " + type);
		}
		Type tmp=table.signedInt();
		if(tmp.size()<type.size()){
			return type;
		}
		return tmp;
	}
	/*
	 * 转换规则按照c语言的规则:有long都转成long,signed优先级大于signed,然后到int.
	 */
	 private Type usualArithmeticConversion(Type l, Type r) {
	        Type s_int = table.signedInt();
	        Type u_int = table.unsignedInt();
	        Type s_long = table.signedLong();
	        Type u_long = table.unsignedLong();
	        if (    (l.isSameType(u_int) && r.isSameType(s_long))
	             || (r.isSameType(u_int) && l.isSameType(s_long))) {
	            return u_long;
	        }
	        else if (l.isSameType(u_long) || r.isSameType(u_long)) {
	            return u_long;
	        }
	        else if (l.isSameType(s_long) || r.isSameType(s_long)) {
	            return s_long;
	        }
	        else if (l.isSameType(u_int)  || r.isSameType(u_int)) {
	            return u_int;
	        }
	        else {
	            return s_int;
	        }
	    }
	 /*
	  * 检查语句的实现
	  */
	 //检查返回值语句的时候要和当前检查的函数声明的返回值进行对比.
	 public Void visit(ReturnNode node) {
	        super.visit(node);
	        if (currentFunction.isVoid()) {
	            if (node.expr() != null) {
	                error(node, "returning value from void function");
	            }
	        }
	        else {  
	            if (node.expr() == null) {
	                error(node, "missing return value");
	                return null;
	            }
	            if (node.expr().type().isVoid()) {
	                error(node, "returning void");
	                return null;
	            }
	            if(!node.expr().type().equals(currentFunction.returnType())){
	            	error(node,"Type is not equal to function returnType");
	            }
	          
	        }
	        return null;
	    }
	 public Void Visit(BlockNode node){
		 for (DefinedVariable var : node.variables()) {
	            checkVariable(var);
	        }
	        for (StmtNode n : node.stmts()) {
	            check(n);
	        }
	        return null;
		 
	 }
	 public Void visit(SwitchNode node) {
	        super.visit(node);
	        checkSwitchCond(node.cond());
	        return null;
	    }
	 public Void visit(IfNode node) {
	        super.visit(node);
	        checkCond(node.cond());
	        return null;
	    }

	    public Void visit(WhileNode node) {
	        super.visit(node);
	        checkCond(node.condition());
	        return null;
	    }

	    public Void visit(ForNode node) {
	        super.visit(node);
	        checkCond(node.cond());
	        return null;
	    }
	    public Void visit(ExprStmtNode node) {
	        check(node.expr());
	        if (isInvalidStatementType(node.expr().type())) {
	            error(node, "invalid statement type: " + node.expr().type());
	            return null;
	        }
	        return null;
	    }
	    //条件必须是一个标量
	    private void checkCond(ExprNode cond) {
	        if(!cond.type().isScalar())
	        	error(cond, "Expression must be a Scalar condtion");
	    }
	    //switch的条件必须是整型,字符类型被解析成整型
	    private void checkSwitchCond(ExprNode cond){
	    	if(!cond.isInteger()){
	    		error(cond, "Expression must be a Integer");
	    	}
	    }
	 /*
		 * 错误和警告处理
		 */
		private void error(Node node, String msg) {
	        errorHandler.error(node.location(), msg);
	    }
		private void error(Location loc, String msg) {
	        errorHandler.error(loc, msg);
	    }
		private void warn(Node n, String msg) {
	        errorHandler.warn(n.location(), msg);
	    }
}
