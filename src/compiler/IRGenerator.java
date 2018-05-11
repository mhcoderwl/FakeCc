package compiler;
import java.util.*;
import ast.*;
import entity.*;
import exception.*;
import asm.*;
import asm.Type;
import ir.*;
import type.*;
import utils.*;
/*
 * 中间代码生成的启动类,遍历的是ast
 */
public class IRGenerator implements ASTVisitor<Void,Expr>{
	private  TypeTable typeTable;
    private  ErrorHandler errorHandler;
    public IRGenerator(TypeTable typeTable, ErrorHandler errorHandler) {
        this.typeTable = typeTable;
        this.errorHandler = errorHandler;
    }
    /*
     * 返回一个IR根节点,首先对全局变量的初始化表达式转成中间代码,然后再对定义的函数转换.
     */
    public IR generate(AST ast)throws SemanticException{
    	for(DefinedVariable var:ast.definedVariables()){
    		if(var.hasInitializer()){
    			var.setIR(transformExpr(var.initializer()));
    		}
    	}
    	for(DefinedFunction func:ast.definedFunctions()){
    		func.setIR(compileFunctionBody(func));
    	}
    	if(errorHandler.errorOccured()){
    		throw new SemanticException("IR generation failed");
    	}
    	return new IR(ast.location(),ast.definedVariables(),
    			ast.definedFunctions(),ast.funcdecls(),ast.scope(),ast.constTable());
    }
    List<Stmt> stmts=new ArrayList<Stmt>();//保存每条语句转换后的中间代码.
    LinkedList<LocalScope> scopeStack=new LinkedList<LocalScope>();//生成临时变量需要当前作用域
    LinkedList<Label>breakStack=new LinkedList<Label>();//用来存储break语句跳转的目的地
    LinkedList<Label>continueStack=new LinkedList<Label>();//continue语句的跳转目的地
    HashMap<String, LabelEntry>map=new HashMap<String,LabelEntry>();//跳转表,用来保存定义的跳转标签,goto语句用来查表.
    class LabelEntry{
    	private Label label;
    	private boolean isDefined;
    	public LabelEntry(Label label){
    		this.label=label;
    		isDefined=false;
    	}
    	public Label label(){
    		return this.label;
    	}
    	public boolean isDefined(){
    		return isDefined;
    	}
    	public void setDefined(){
    		this.isDefined=true;
    	}
    }
    public List<Stmt> compileFunctionBody(DefinedFunction f){
    	 transformStmt(f.body());
    	 checkJumpTable(map);
    	 return stmts;
    }
    public void transformStmt(StmtNode node){
    	node.accept(this);
    }
//    private final long exprLevel=0;//多层表达式递归时用这个变量来表达当前是否是一个表达式的子表达式.
//    private boolean isStatement(){
//    	return exprLevel==0;
//    }
    public Expr transformExpr(ExprNode node){	
    	Expr e=node.accept(this);
    	return e;
    }
    /*
     * 语句的visit转换,要用到用来保存中间状态和最终结果的容器.
     */
    //在switch中直接处理了他的body
    public Void visit(CaseNode node) {
        throw new Error("must not happen");
    }
    public Void visit(BlockNode node){
    	scopeStack.addFirst(node.scope());
    	//对变量定义部分转换,如果是有初始化的非静态变量,分成两个语句,定义一个变量,然后添加一个赋值语句初始化.
    	for(DefinedVariable var:node.variables()){
    		if(var.hasInitializer()){
    			if(var.isStatic())
    				var.setIR(transformExpr(var.initializer()));
    			else {
    				//加入语句列表
    				assign(var.location(),var(var),transformExpr(var.initializer()));
				}
    		}
    	}
    	for(StmtNode stmt:node.stmts()){
    		transformStmt(stmt);
    	}
    	scopeStack.removeFirst();
    	return null;
    }
    public Void visit(ExprStmtNode node) {
        Expr e = node.expr().accept(this);
        return null;
    }
    /*
     *if语句有三个跳转标签
     */
    public Void visit(IfNode node) {
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        Expr cond = transformExpr(node.cond());
        //分为有没有else的情况来处理
        if (node.elseBody() == null) {
            cjump(node.location(), cond, thenLabel, endLabel);//添加一个条件跳转中间节点,else为endLabel
            label(thenLabel);	//添加一个跳转标签对象
            transformStmt(node.thenBody());
            jump(endLabel);
            label(endLabel);	//添加结束跳转标签
        }
        else {
            cjump(node.location(), cond, thenLabel, elseLabel);
            label(thenLabel);
            transformStmt(node.thenBody());
            jump(endLabel);	//添加一个无条件跳转对象
            
            label(endLabel);
        }
        return null;
    }
    /*
     * 对于循环体我们要考虑break和continue的标签,
     */
    public Void visit(WhileNode node){
    	Label loopLabel=new Label();
    	Label thenLabel=new Label();
    	Label endLabel=new Label();
    	Expr cond=transformExpr(node.condition());
    	label(loopLabel);//循环的起点标签
    	cjump(node.location(),  cond,thenLabel, endLabel);
    	label(thenLabel);
    	pushContinue(loopLabel);
    	pushBreak(endLabel);
    	transformStmt(node.body());
    	popBreak();
    	popContinue();
    	jump(loopLabel);
    	label(endLabel);
    	return null;
    }
    public Void visit(ForNode node){
    	Label loopLabel=new Label();
    	Label thenLabel=new Label();
    	Label endLabel=new Label();
    	Label contLabel=new Label();
    	Expr cond=transformExpr(node.cond());
    	transformStmt(node.init());
    	label(loopLabel);//循环的起点标签
    	cjump(node.location(),  cond,thenLabel, endLabel);
    	label(thenLabel);
    	pushContinue(loopLabel);
    	pushBreak(endLabel);
    	transformStmt(node.body());
    	popBreak();
    	popContinue();
    	label(contLabel);//continue语句跳转标签
    	transformStmt(node.execute());
    	jump(loopLabel);
    	label(endLabel);
    	return null;
    	 
    }
    
    public Void visit(DoWhileNode node){
    	Label loopLabel=new Label();
    	Label endLabel=new Label();
    	Label contLabel=new Label();
    	Expr cond=transformExpr(node.cond());
    	pushBreak(endLabel);
    	pushContinue(contLabel);
    	transformStmt(node.body());
    	popBreak();
    	popContinue();
    	label(contLabel);
    	cjump(node.location(),  cond,loopLabel, endLabel);
    	label(endLabel);
    	return null;
    }
    public Void visit(SwitchNode node) {
        List<Case> cases = new ArrayList<Case>();
        Label endLabel = new Label();
        Label defaultLabel = endLabel;

        Expr cond = transformExpr(node.cond());
        for (CaseNode c : node.cases()) {
            if (c.isDefault()) {
                defaultLabel = c.label();
            }
            else {
                for (ExprNode val : c.values()) {
                    Expr v = transformExpr(val);
                    cases.add(new Case(((Int)v).value(), c.label()));
                }
            }
        }
        //添加一个switch跳转,和条件跳转类似.
        stmts.add(new Switch(node.location(), cond, cases, defaultLabel, endLabel));
        //逐条添加case的语句.
        pushBreak(endLabel);
        for (CaseNode c : node.cases()) {
            label(c.label());
            transformStmt(c.body());
        }
        popBreak();
        label(endLabel);
        return null;
    }
    public Void visit(BreakNode node) {
        try {
            jump(node.location(), currentBreakTarget());
        }
        catch (JumpError err) {
            error(node, err.getMessage());
        }
        return null;
    }
    public Void visit(ContinueNode node) {
        try {
            jump(node.location(), currentContinueTarget());
        }
        catch (JumpError err) {
            error(node, err.getMessage());
        }
        return null;
    }
    private void pushContinue(Label lab){
    	continueStack.addFirst(lab);
    }
    private void pushBreak(Label lab){
    	breakStack.addFirst(lab);
    }
    private void popBreak(){
    	if(continueStack.isEmpty()){
    		throw new Error("break stack is empty");
    	}
    	 breakStack.pop();
    }
    private void popContinue(){
    	if(continueStack.isEmpty()){
    		throw new Error("continue stack is empty");
    	}
   	 continueStack.pop();
   }
    private Label currentContinueTarget()throws JumpError{
    	if (continueStack.isEmpty()) {
            throw new JumpError("continue from out of loop");
        }
    	return continueStack.peekFirst();
    }
    private Label currentBreakTarget()throws JumpError{
    	if (breakStack.isEmpty()) {
            throw new JumpError("break from out of loop");
        }
    	return breakStack.peekFirst();
    }
    public Void visit(LabelNode node) {
        try {
            stmts.add(new LabelStmt(node.location(),
                    defineLabel(node.name())));
            if (node.stmt() != null) {
                transformStmt(node.stmt());
            }
        }
        catch (SemanticException ex) {
            error(node, ex.getMessage());
        }
        return null;
    }
   public Label defineLabel(String name)throws SemanticException{
	   if(map.containsKey(name)){
		   LabelEntry tmp=map.get(name);
		   if(tmp.isDefined()){
			   throw new SemanticException(
		                "duplicated jump labels in " + name + "(): " + name);
		   }else{
			   tmp.setDefined();
			   return tmp.label();
		   }
	   }
	   LabelEntry result=putLabel(name);
	   result.setDefined();
	   return result.label();
   }
   public LabelEntry putLabel(String name){
	   LabelEntry result=new LabelEntry(new Label());
	   map.put(name, result);
	   return result;
   }
    public Void visit(GotoNode node){
    	if(!map.containsKey(node.target())){
    		putLabel(node.target());
    	}
    	jump(node.location(),map.get(node.target()).label());
        return null;
    }
    //检查跳转表是否有未定义的
    private void checkJumpTable(Map<String, LabelEntry> jumpMap) {
        for (Map.Entry<String, LabelEntry> ent : jumpMap.entrySet()) {
            String labelName = ent.getKey();
            LabelEntry jump = ent.getValue();
            if (!jump.isDefined()) {
                errorHandler.error(
                        "undefined label: " + labelName);
            }
        }
    }
    public Void visit(ReturnNode node) {
        stmts.add(new Return(node.location(),
                node.expr() == null ? null : transformExpr(node.expr())));
        return null;
    }
    /*
     * 带跳转的表达式转换,条件表达式,逻辑表达式,会出现跳转情况
     */
    //条件表达式,拆分为一个条件跳转
    public Expr visit(CondExprNode node) {
        Label thenLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        //添加一个临时变量用来保存节点返回的变量.
        DefinedVariable var = tmpVar(node.type());

        Expr cond = transformExpr(node.cond());
        cjump(node.location(), cond, thenLabel, elseLabel);
        label(thenLabel);
        assign(node.leftExpr().location(),
                var(var), transformExpr(node.leftExpr()));
        jump(endLabel);
        label(elseLabel);
        assign(node.rightExpr().location(),
        		var(var), transformExpr(node.rightExpr()));
        jump(endLabel);
        label(endLabel);
        return var(var);
    }

    public Expr visitAndNode(BinaryOpNode node) {
        Label rightLabel = new Label();
        Label endLabel = new Label();
        DefinedVariable var = tmpVar(node.type());

        assign(node.left().location(),
        		var(var), transformExpr(node.left()));
        //与表达式如果左边为假跳转最后
        cjump(node.location(), var(var), rightLabel, endLabel);
        label(rightLabel);
        assign(node.right().location(),
        		var(var), transformExpr(node.right()));
        label(endLabel);
        return var(var);
    }

    public Expr visitOrNode(BinaryOpNode node) {
        Label rightLabel = new Label();
        Label endLabel = new Label();
        DefinedVariable var = tmpVar(node.type());

        assign(node.left().location(),
        		var(var), transformExpr(node.left()));
        //或表达式如果左边成立直接跳转最后
        cjump(node.location(), var(var), endLabel, rightLabel);
        label(rightLabel);
        assign(node.right().location(),
        		var(var), transformExpr(node.right()));
        label(endLabel);
        return var(var);
    }
    /*
     * 有副作用的表达式转换,通过生成一个中间变量来防止复合表达式多次执行.先解析右边,然后赋值
     * 给一个中间变量,然后中间变量在赋值给左边,然后返回中间变量的值.如果返回左边表达式会导致重复执行左边表达式.
     */
    public Expr visit(AssignNode node){
    	Expr lExpr=transformExpr(node.lhs());
    	Expr rExpr=transformExpr(node.rhs());
    	Location lloc=node.lhs().location();
    	Location rloc=node.rhs().location();
    	DefinedVariable tmp = tmpVar(node.rhs().type());
        assign(rloc, var(tmp), rExpr);
        assign(lloc, lExpr, var(tmp));
        return var(tmp);
    }
    //前缀自增减,直接转换成一个复合自我赋值语句,
    public Expr visit(PrefixOpNode node) {
        // ++expr -> expr += 1
        type.Type t = node.expr().type();
        return transformOpAssign(node.location(),
                binOp(node.operator()), t,
                transformExpr(node.expr()), imm(t, 1));
    }

    /*(expr)++的节点转换,先创建两个中间变量一个保存表达式的地址,一个保存表达式值,然后执行表达式+1,然后返回
     * 表达式原始的值,实现等同于c++中重载后置操作符
     */
    
    public Expr visit(SuffixOpNode node) {
        // #@@range/SuffixOp_init{
        Expr expr = transformExpr(node.expr());
        type.Type t = node.expr().type();
        Op op = binOp(node.operator());
        Location loc = node.location();
            // cont(expr++) -> a = &expr; v = *a; *a = *a + 1; cont(v)        
            DefinedVariable a = tmpVar(pointerTo(t));
            DefinedVariable v = tmpVar(t);
            assign(loc, var(a), addressOf(expr));
            assign(loc, var(v), derefer(a));
            //imm是增加和减少的字节长度,类型是Int
            assign(loc, derefer(a), bin(op, t, derefer(a), imm(t, 1)));
            return var(v);
    }
    public Expr visit(FunCallNode node) {
        List<Expr> args = new ArrayList<Expr>();
        for (ExprNode arg : node.args()) {
            args.add(0, transformExpr(arg));
        }
        Expr call = new Call(asmType(node.type()),
                transformExpr(node.expr()), args);

            DefinedVariable tmp = tmpVar(node.type());
            assign(node.location(), var(tmp), call);
            return var(tmp);
    }
    /*
     * 无副作用的表达式的转换
     */
    public Expr visit(UnaryOpNode node) {
        if (node.operator().equals("+")) {
            //表示符号正
            return transformExpr(node.expr());
        }
        else {
            return new UnaryOp(asmType(node.type()),//生成asm.Type类型
                    Op.internUnary(node.operator()),//返回汇编中的操作符类型
                    transformExpr(node.expr()));
        }
    }
    //自我赋值,先处理左右两个节点,返回各自的表达式中间节点.
    public Expr visit(OpAssignNode node) {
        // Evaluate RHS before LHS.
        Expr rhs = transformExpr(node.rhs());
        Expr lhs = transformExpr(node.lhs());
        type.Type t = node.lhs().type();
        Op op = Op.internBinary(node.operator(), t.isSigned());
        return transformOpAssign(node.location(), op, t, lhs, rhs);
    }
    private Expr transformOpAssign(Location loc,
            Op op, type.Type lhsType, Expr lhs, Expr rhs) {
        
        	// 转化规则:cont(lhs += rhs) -> a = &lhs; *a = *a + rhs; cont(*a)
    	//先增加一个用来存地址的临时变量.
            DefinedVariable a = tmpVar(pointerTo(lhsType));
            assign(loc, var(a), addressOf(lhs));
            assign(loc, derefer(a), bin(op, lhsType, derefer(a), rhs));
            return derefer(a);
    }
    //用来方便生成一个BinaryOp节点,还要判断一下是否是指针类型运算
    private BinaryOp bin(Op op, type.Type leftType, Expr left, Expr right) {
        if (isPtrBinary(op, leftType)) {
            return new BinaryOp(left.type(), op, left,
                    new BinaryOp(right.type(), Op.MUL,
                            right, ptrBaseSize(leftType)));
        }
        else {
            return new BinaryOp(left.type(), op, left, right);
        }
    }
    /*
     * 二元运算符转换,首先转换左右表达式和操作符,先处理右侧表达式,
     * 因为有可能表达式会改变某些值,比如右侧有i++这种,左侧也有i这个值,那么处理左侧时i的值就会被+1.
     */
    public Expr visit(BinaryOpNode node) {
    	//如果是与或表达式,则归到处理跳转表达式一类
    	if(node.operator().equals("||")){
    		return visitAndNode(node);
    	}
    	if(node.operator().equals("&&")){
    		return visitOrNode(node);
    	}
        Expr right = transformExpr(node.right());
        Expr left = transformExpr(node.left());
        Op op = Op.internBinary(node.operator(), node.type().isSigned());
        type.Type t = node.type();
        type.Type r = node.right().type();
        type.Type l = node.left().type();
        //指针类型间的运算
        if (isPointerMinus(l,op, r)) {
            //指针相减的情况,添加一个除法,除去基本类型的大小.
            Expr tmp = new BinaryOp(asmType(t), op, left, right);
            return new BinaryOp(asmType(t), Op.S_DIV, tmp, ptrBaseSize(l));
        }
        else if (isPointerAddNum(l,op,r)) {
            // 指针加上一个整数的情况,要乘上基本类型的大小.
            return new BinaryOp(asmType(t), op,
                    left,new BinaryOp(asmType(r), Op.MUL, right, ptrBaseSize(l)));
        }
        else if (isNumAddPointer(l,op,r)) {
            //同上
            return new BinaryOp(asmType(t), op,
                    new BinaryOp(asmType(l), Op.MUL, left, ptrBaseSize(r)),
                    right);
        }
        else {
            return new BinaryOp(asmType(t), op, left, right);
        }
    }
    
    //附带的一些方法
    private boolean isPointerMinus( type.Type l,Op op, type.Type r){
    	return l.isPointer()&&r.isPointer()&&op.equals(Op.SUB);
    }
    private boolean isPointerAddNum(type.Type l,Op op,type.Type r){
    	return isPtrBinary(op,l)&&r.isInt();
    }
    private boolean isNumAddPointer(type.Type l,Op op, type.Type r){
    	return isPtrBinary(op,r)&&l.isInt();
    }
    private boolean isPtrBinary(Op op, type.Type operandType) {
            return op==Op.ADD&&op==Op.SUB&&operandType.isPointer();
    }
    //返回指针的中间类型
    private Expr ptrBaseSize(type.Type t) {
        return new Int(ptrdiff_t(), t.baseType().size());
    }
    /*
     * 数组转换,先转换数组的表达式部分,
     */
    public Expr visit(ArefNode node) {
        Expr expr = transformExpr(node.baseExpr());//基础部分的表达式
        Expr offset = new BinaryOp(ptrdiff_t(), Op.MUL,
                size(node.elementSize()), transformIndex(node));
        BinaryOp addr = new BinaryOp(ptr_t(), Op.ADD, expr, offset);
        return derefer(addr, node.type());//转成Mem节点,即可以成为右值也可以成为左值.
    }
    
    
    // 如果多维数组定义: t[e][d][c][b][a] ary;
    // 那么对于取某个元素:&ary[a0][b0][c0][d0][e0](第一个参数对应定义时的最外围的那一维,以此类推)
    //     = &ary + edcb*a0 + edc*b0 + ed*c0 + e*d0 + e0
    //     = &ary + (((((a0)*b + b0)*c + c0)*d + d0)*e + e0) * sizeof(t)
    
    private Expr transformIndex(ArefNode node) {
        if (node.isMultiDimension()) {
            return new BinaryOp(int_t(), Op.ADD,
                    transformExpr(node.index()),
                    new BinaryOp(int_t(), Op.MUL,
                            new Int(int_t(), node.length()),
                            transformIndex((ArefNode)node.expr())));
        }
        else {
            return transformExpr(node.index());
        }
    }

    /* 结构体变量调用成员变量的转换,首先转换类型是结构体的表达式,然后返回其地址,
     * addressOf用来将一个右值转换成左值,即对一个右值表达式取地址.转换成Addr节点.此节点整体可转成结构体
     * 的地址加上成员变量的偏移地址,最后再形成一个Derefer节点就转成右值了.
     * 这样上层调用如果需要左值再取出Derefer的表达式即可..如果成员变量是数组,那么直接返回一个地址.
     */
    public Expr visit(MemberNode node) {
        Expr expr = addressOf(transformExpr(node.expr()));//结构体的起始地址,
        Expr offset = ptrdiff(node.offset());
        Expr addr = new BinaryOp(ptr_t(), Op.ADD, expr, offset);
        return node.isLoadable() ? derefer(addr, node.type()) : addr;
    }

    public Expr visit(PtrMemberNode node) {
        Expr expr = transformExpr(node.expr());
        Expr offset = ptrdiff(node.offset());
        Expr addr = new BinaryOp(ptr_t(), Op.ADD, expr, offset);
        return node.isLoadable() ? derefer(addr, node.type()) : addr;
    }
    
    public Expr visit(DereferenceNode node) {
        Expr addr = transformExpr(node.expr());
        return node.isLoadable() ? derefer(addr, node.type()) : addr;
    }
    //将一个实体转换成解引用的形式节点
    private Expr derefer(Entity ent){
    	return new Derefer(asmType(ent.type()), var(ent));
    }
    private Expr derefer(Expr expr,type.Type t){
    	return new Derefer(asmType(t), expr);
    }
    public Expr visit(AddressNode node) {
        Expr e = transformExpr(node.expr());
        return node.expr().isLoadable() ? addressOf(e) : e;
    }

    public Expr visit(CastNode node) {
        if (node.isEffectiveCast()) {
            return new UnaryOp(asmType(node.type()),
                    node.expr().type().isSigned() ? Op.S_CAST : Op.U_CAST,
                    transformExpr(node.expr()));
        }
            transformExpr(node.expr());
            return null;
    }

    public Expr visit(SizeofExprNode node) {
        return new Int(size_t(), node.expr().allocSize());
    }
    //返回一个Int中间节点
    public Expr visit(SizeofTypeNode node) {
        return new Int(size_t(), node.operand().allocSize());
    }

    public Expr visit(VariableNode node) {
        if (node.entity().isConstant()) {
            return transformExpr(node.entity().value());
        }
        Var var = var(node.entity());
        return node.isLoadable() ? var : addressOf(var);
    }

    public Expr visit(IntegerLiteralNode node) {
        return new Int(asmType(node.type()), node.value());
    }

    public Expr visit(StringLiteralNode node) {
        return new Str(asmType(node.type()), node.entry());
    }
    //将变量或者解引用节点转成地址节点.
    private Expr addressOf(Expr expr) {
        return expr.addressNode(ptr_t());
    }
    //将实体转成Var对象
	private Var var(Entity var){
		return new Var(varType(var.type()), var);
	}
	//地址偏移的中间节点
	private Int ptrdiff(long n) {
        return new Int(ptrdiff_t(), n);
    }
	//变量类型转成汇编的INT类型
	private asm.Type varType(type.Type t){
		return asmType(t);//enum类型中的一种
	}
	
	private asm.Type asmType(type.Type t) {
        if (t.isVoid()) return int_t();
        return asm.Type.get(t.size());
    }
	//根据每种类型的大小转成在汇编中对应INT类型
	private asm.Type int_t() {
        return asm.Type.get(typeTable.intSize());
	}
	//对一个size类型转换成Int类型
	private Int size(long n) {
        return new Int(size_t(), n);
    }
    private asm.Type size_t() {
        return asm.Type.get(typeTable.longSize());
    }
    //指针类型锁对应的中间代码类型
    private asm.Type ptr_t() {
        return asm.Type.get(typeTable.pointerSize());
    }
    //地址偏移量的类型所对应中间代码类型,地址偏移是一个long型整数
    private asm.Type ptrdiff_t() {
        return asm.Type.get(typeTable.longSize());
    }
    //创建一个指针类型
    private PointerType pointerTo(type.Type t){
    	return typeTable.pointerTo(t);
    }
	//添加一个跳转标签
	private void label(Label label) {
        stmts.add(new LabelStmt(null, label));
    }
	//添加一个跳转节点
    private void jump(Location loc, Label target){
        stmts.add(new Jump(loc, target));
    }
    //在当前作用域中添加一个临时变量,
    private DefinedVariable tmpVar(type.Type t) {
        return scopeStack.getLast().allocateTmp(t);
    }
    
    private void jump(Label target) {
        jump(null, target);
    }
    //添加一个条件跳转标签
    private void cjump(Location loc, Expr cond, Label thenLabel, Label elseLabel) {
        stmts.add(new CJump(loc, cond, thenLabel, elseLabel));
    }
    //添加一个赋值语句
    private void assign(Location loc, Expr lhs, Expr rhs) {
        stmts.add(new Assign(loc, addressOf(lhs), rhs));
    }
    //一元操作符转二元操作符的中间表示
    private Op binOp(String uniOp) {
        return uniOp.equals("++") ? Op.ADD : Op.SUB;
    }
    //生成一个值为n的Int
    private Int imm(type.Type operandType, long n) {
        if (operandType.isPointer()) {
            return new Int(ptrdiff_t(), n);
        }
        else {
            return new Int(int_t(), n);
        }
    }
    private void error(Node n, String msg) {
        errorHandler.error(n.location(), msg);
    }
}
