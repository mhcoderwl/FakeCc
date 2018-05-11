package sys;


import asm.*;
import ir.*;
import entity.*;
import java.util.*;
import utils.*;
public class CodeGenerator implements IRVisitor<Void,Void>{
	//用来对一个定义的函数解析生成语句列表
	 private AssemblyCode as;
	 private Label epilogue;//函数尾声地址标签
	 private ErrorHandler errorHandler;
	 static private asm.Type naturalType=asm.Type.INT32;
	 final CodeGeneratorOptions options;
	 //汇编标签的字段
	 static final String LABEL_SYMBOL_BASE = ".L";
	    static final String CONST_SYMBOL_BASE = ".LC";
	 //主方法,首先确定所有字符串字面量和全局变量的地址,这些放在全局代码段
	 public  AssemblyCode generate(IR ir){
		 locateSymbols(ir);
	        return generateAssemblyCode(ir);
	 }
	 public CodeGenerator(CodeGeneratorOptions options,
	            Type naturalType, ErrorHandler errorHandler) {
	        this.options = options;
	        this.naturalType = naturalType;
	        this.errorHandler = errorHandler;
	    }
	 private void locateSymbols(IR ir) {
	        SymbolTable constSymbols = new SymbolTable(CONST_SYMBOL_BASE);
	        for (ConstantEntry ent : ir.constantTable().entries()) {
	            locateStringLiteral(ent, constSymbols);
	        }
	        for (Variable var : ir.allGlobalVariables()) {
	            locateGlobalVariable(var);
	        }
	        for (Function func : ir.allFunctions()) {
	            locateFunction(func);
	        }
	    }
	 private void locateFunction(Function func) {
	        func.setCallingSymbol(callingSymbol(func));
	        locateGlobalVariable(func);
	    }
	 private Symbol callingSymbol(Function func) {
	        if (func.isStatic()) {
	            return privateSymbol(func.symbolString());
	        }
	        else {
	            Symbol sym = globalSymbol(func.symbolString());
	            return shouldUsePLT(func) ? PLTSymbol(sym) : sym;
	        }
	    }
	 private void locateGlobalVariable(Entity ent) {
	        Symbol sym = symbol(ent.symbolString(), ent.isStatic());
	        if (options.isPositionIndependent()) {
	            if (ent.isStatic()) {
	                ent.setMemref(mem(localGOTSymbol(sym), GOTBaseReg()));
	            }
	            else {
	                ent.setAddress(mem(globalGOTSymbol(sym), GOTBaseReg()));
	            }
	        }
	        else {
	            ent.setMemref(mem(sym));
	            ent.setAddress(imm(sym));
	        }
	    }
	 private Symbol globalGOTSymbol(Symbol base) {
	        return new SuffixedSymbol(base, "@GOT");
	    }

	    private Symbol localGOTSymbol(Symbol base) {
	        return new SuffixedSymbol(base, "@GOTOFF");
	    }

	    private Symbol PLTSymbol(Symbol base) {
	        return new SuffixedSymbol(base, "@PLT");
	    }
	 private void locateStringLiteral(ConstantEntry ent, SymbolTable syms) {
	        ent.setSymbol(syms.newSymbol());
	        if (options.isPositionIndependent()) {
	            Symbol offset = localGOTSymbol(ent.symbol());
	            ent.setMemref(mem(offset, GOTBaseReg()));
	        }
	        else {
	            ent.setMemref(mem(ent.symbol()));
	            ent.setAddress(imm(ent.symbol()));
	        }
	    }
	 //对IR生成asm的主方法
	 private AssemblyCode generateAssemblyCode(IR ir) {
	        AssemblyCode file = newAssemblyCode();
	        file._file(ir.fileName());//生成汇编伪操作
	        if (ir.hasGlobalVariableDefined()) {
	            generateDataSection(file, ir.definedGlobalVariables());//生成代码段.data
	        }
	        if (ir.hasStringLiteralDefined()) {
	            generateReadOnlyDataSection(file, ir.constantTable());//生成.rodata代码段
	        }
	        if (ir.hasFunctionDefined()) {
	            generateTextSection(file, ir.definedFunctions());//对函数进行汇编代码生成
	        }
	    
	        if (ir.hasCommonSymbolDefined()) {
	            generateCommonSymbols(file, ir.definedCommonSymbols());
	        }
	    //    if (options.isPositionIndependent()) {
	    //        PICThunk(file, GOTBaseReg());
	    //    }
	        return file;
	    }
	 private void generateDataSection(AssemblyCode file,
             List<DefinedVariable> gvars) {
		 	file._data();
		 	for (DefinedVariable var : gvars) {
		 		Symbol sym = globalSymbol(var.symbolString());
		 		if (!var.isStatic()) {
		 			file._globl(sym);
		 		}
		 		file._align(var.alignment());
		 		file._type(sym, "@object");
		 		file._size(sym, var.allocSize());
		 		file.label(sym);
		 		generateImmediate(file, var.type().allocSize(), var.ir());
		 	}
	 }
	 private void generateImmediate(AssemblyCode file, long size, Expr node) {
	        if (node instanceof Int) {
	            Int expr = (Int)node;
	            switch ((int)size) {
	            case 1: file._byte(expr.value());    break;
	            case 2: file._value(expr.value());   break;
	            case 4: file._long(expr.value());    break;
	            case 8: file._quad(expr.value());    break;
	            default:
	                throw new Error("entry size must be 1,2,4,8");
	            }
	        }
	        else if (node instanceof Str) {
	            Str expr = (Str)node;
	            switch ((int)size) {
	            case 4: file._long(expr.symbol());   break;
	            case 8: file._quad(expr.symbol());   break;
	            default:
	                throw new Error("pointer size must be 4,8");
	            }
	        }
	        else {
	            throw new Error("unknown literal node type" + node.getClass());
	        }
	    }
	 private void generateReadOnlyDataSection(AssemblyCode file,
             ConstantTable constants) {
		 file._section(".rodata");//切换到rodata片段
		 for (ConstantEntry ent : constants) {
			 file.label(ent.symbol());
			 file._string(ent.value());
		 }
	 }
	 private void generateTextSection(AssemblyCode file,
             List<DefinedFunction> functions) {
		 	file._text();
		 	for (DefinedFunction func : functions) {
		 		Symbol sym = globalSymbol(func.name());
		 		if (! func.isPrivate()) {
		 			file._globl(sym);
		 		}
		 		file._type(sym, "@function");
		 		file.label(sym);
		 		compileFunctionBody(file, func);
		 		file._size(sym, ".-" + sym.toSource());
		 	}
	 }
	 private void generateCommonSymbols(AssemblyCode file,
             List<DefinedVariable> variables) {
		 	for (DefinedVariable var : variables) {
		 		Symbol sym = globalSymbol(var.symbolString());
		 		if (var.isStatic()) {
		 			file._local(sym);
		 		}
		 		file._comm(sym, var.allocSize(), var.alignment());
		 	}
	 }
	    private AssemblyCode compileStmts(DefinedFunction func) {
	        as = newAssemblyCode();
	        epilogue = new Label();
	        for (Stmt s : func.ir()) {
	            compileStmt(s);
	        }
	        as.label(epilogue);
	        return as;
	    }
	    private void compileStmt(Stmt stmt) {
	        if (options.isVerboseAsm()) {
	            if (stmt.location() != null) {
	                as.comment(stmt.location().numberedLine());
	            }
	        }
	        stmt.accept(this);
	    }
	    private AssemblyCode newAssemblyCode(){
	    	return new AssemblyCode();
	    }
	    public Void visit(Var node) {
	        loadVariable(node, ax());
	        return null;
	    }
	    public Void visit(Int node) {
	        as.mov(imm(node.value()), ax());
	        return null;
	    }
	    public Void visit(Str node) {
	        loadConstant(node, ax());
	        return null;
	    }
	    public Void visit(Derefer node) {
	        compile(node.expr());
	        load(mem(ax()), ax(node.type()));
	        return null;
	    }
	
	    public Void visit(Addr node) {
	        loadAddress(node.entity(), ax());
	        return null;
	    }
	    public Void visit(Assign node) {
	        if (node.lhs().isAddr() && node.lhs().memref() != null) {
	            compile(node.rhs());
	            store(ax(node.lhs().type()), node.lhs().memref());
	        }
	        else if (node.rhs().isConstant()) {
	            compile(node.lhs());
	            as.mov(ax(), cx());
	            loadConstant(node.rhs(), ax());
	            store(ax(node.lhs().type()), mem(cx()));
	        }
	        else {
	            compile(node.rhs());
	            as.virtualPush(ax());
	            compile(node.lhs());
	            as.mov(ax(), cx());
	            as.virtualPop(ax());
	            //将寄存器的值写入cx指向的内存
	            store(ax(node.lhs().type()), mem(cx()));
	        }
	        return null;
	    }
	    
	    //一元运算符的转换,
	    public Void visit(UnaryOp node){
	    	Type src = node.expr().type();
	        Type dest = node.type();
	        compile(node.expr());
	        switch (node.op()) {
	        case UMINUS:
	            as.neg(ax(src));
	            break;
	        case BIT_NOT:
	            as.not(ax(src));
	            break;
	        case NOT:
	            as.test(ax(src), ax(src));
	            as.sete(al());
	            as.movzx(al(), ax(dest));
	            break;
	        case S_CAST:
	            as.movsx(ax(src), ax(dest));
	            break;
	        case U_CAST:
	            as.movzx(ax(src), ax(dest));
	            break;
	        default:
	            throw new Error("unknown unary operator: " + node.op());
	        }
	    	return null;
	    }
	    //二元运算符转换
	    public Void visit(BinaryOp node) {
	        Op op = node.op();
	        Type t = node.type();
	            compile(node.right());
	            as.virtualPush(ax());
	            compile(node.left());
	            as.virtualPop(cx());
	            compileBinaryOp(op, ax(t), cx(t));
	        return null;
	    }
	    private void compileBinaryOp(Op op, Register left, Operand right) {
	        switch (op) {
	        case ADD:
	            as.add(right, left);
	            break;
	        case SUB:
	            as.sub(right, left);
	            break;
	        case MUL:
	            as.imul(right, left);
	            break;
	        case S_DIV:
	        case S_MOD:
	            as.cltd();
	            as.idiv(cx(left.type));
	            if (op == Op.S_MOD) {
	                as.mov(dx(), left);
	            }
	
	            break;
	        case U_DIV:
	        case U_MOD:
	            as.mov(imm(0), dx());
	            as.div(cx(left.type));
	            if (op == Op.U_MOD) {
	                as.mov(dx(), left);
	            }
	            break;
	        case BIT_AND:
	            as.and(right, left);
	            break;
	        case BIT_OR:
	            as.or(right, left);
	            break;
	        case BIT_XOR:
	            as.xor(right, left);
	            break;
	        case BIT_LSHIFT:
	            as.sal(cl(), left);
	            break;
	        case BIT_RSHIFT:
	            as.shr(cl(), left);
	            break;
	        case ARITH_RSHIFT:
	            as.sar(cl(), left);
	            break;
	        default:
	            
	            as.cmp(right, ax(left.type));
	            //取出标志位语句
	            switch (op) {
	            case EQ:        as.sete (al()); break;
	            case NEQ:       as.setne(al()); break;
	            case S_GT:      as.setg (al()); break;
	            case S_GTEQ:    as.setge(al()); break;
	            case S_LT:      as.setl (al()); break;
	            case S_LTEQ:    as.setle(al()); break;
	            case U_GT:      as.seta (al()); break;
	            case U_GTEQ:    as.setae(al()); break;
	            case U_LT:      as.setb (al()); break;
	            case U_LTEQ:    as.setbe(al()); break;
	            default:
	                throw new Error("unknown binary operator: " + op);
	            }
	            as.movzx(al(), left);
	        }
	     
	    }
	    private boolean doesRequireRegisterOperand(Op op) {
	        switch (op) {
	        case S_DIV:
	        case U_DIV:
	        case S_MOD:
	        case U_MOD:
	        case BIT_LSHIFT:
	        case BIT_RSHIFT:
	        case ARITH_RSHIFT:
	            return true;
	        default:
	            return false;
	        }
	    }
	    public Void visit(ExprStmt stmt) {
	        compile(stmt.expr());
	        return null;
	    }
	    public Void visit(LabelStmt node) {
	        as.label(node.label());
	        return null;
	    }

	    public Void visit(Jump node) {
	        as.jmp(node.label());
	        return null;
	    }
	    //条件跳转
	    public Void visit(CJump node) {
	        compile(node.cond());
	        Type t = node.cond().type();
	        as.test(ax(t), ax(t));//与自己与
	        as.jnz(node.thenLabel());//ZF位为1跳转
	        as.jmp(node.elseLabel());//否则跳转elseLabel
	        return null;
	    }
	    public Void visit(Switch node) {
	        compile(node.cond());
	        Type t = node.cond().type();
	        for (Case c : node.cases()) {
	            as.mov(imm(c.value), cx());
	            as.cmp(cx(t), ax(t));
	            //条件跳转,ZF为1时执行
	            as.je(c.label);
	        }
	        as.jmp(node.defaultLabel());
	        return null;
	    }
	    /*函数调用首先转换参数的表达式,然后把结果全压入栈,判断是指针调用还是函数名调用,
	    *指针调用要生成用绝对地址的call,函数名调用生成普通call指令
	    */
	    public Void visit(Call node) {
	        for (Expr arg : TextUtils.reverse(node.args())) {
	            compile(arg);
	            as.push(ax());
	        }
	        if (node.isFunctionCall()) {
	            as.call(node.function().callingSymbol());
	        }
	        else {
	            compile(node.expr());
	            as.callAbsolute(ax());
	        }
	       // 回滚栈状态
	        rollBackStack(as, stackSizeFromWordNum(node.numArgs()));
	        return null;
	    }
	    public Void visit(Return node) {
	        if (node.expr() != null) {
	            compile(node.expr());
	        }
	        as.jmp(epilogue);
	        return null;
	    }
	    //栈帧分配时用来记录寄存器和变量的信息.
	    class StackFrameInfo {
	        List<Register> saveRegs;
	        long lvarSize;
	        long tempSize;

	        long saveRegsSize() { return saveRegs.size() * STACK_WORD_SIZE; }
	        long lvarOffset() { return saveRegsSize(); }
	        long tempOffset() { return saveRegsSize() + lvarSize; }
	        long frameSize() { return saveRegsSize() + lvarSize + tempSize; }
	    }
	    //编译函数体,首先分配函数参数的内存引用,函数实参在调用函数上已经入栈,除了原ebp,返回地址就到了实参位置.
	    private void compileFunctionBody(AssemblyCode file,DefinedFunction func){
	    	StackFrameInfo frame = new StackFrameInfo();
	        locateParameters(func.parameters());
	        frame.lvarSize = locateLocalVariables(func.lvarScope());
	        AssemblyCode body = optimize(compileStmts(func));//这里编译了函数后的集合能得到使用了那些callee-save寄存器
	        frame.saveRegs = usedCalleeSaveRegisters(body);
	        frame.tempSize = body.virtualStack.maxSize();
	        fixLocalVariableOffsets(func.lvarScope(), frame.lvarOffset());
	        fixTempVariableOffsets(body, frame.tempOffset());
	        if (options.isVerboseAsm()) {
	            printStackFrameLayout(file, frame, func.localVariables());
	        }
	        generateFunctionBody(file, body, frame);
	    }
	    //查看当前已经使用的CalleeSave寄存器
	    private List<Register> usedCalleeSaveRegisters(AssemblyCode body) {
	        List<Register> result = new ArrayList<Register>();
	        for (Register reg : calleeSaveRegisters) {
	            if (body.doesUses(reg)) {
	                result.add(reg);
	            }
	        }
	        result.remove(bp());
	        return result;
	    }
	    //生成序言和尾声
	    private void generateFunctionBody(AssemblyCode file,
	            AssemblyCode body, StackFrameInfo frame) {
	        file.virtualStack.reset();
	        prologue(file, frame.saveRegs, frame.frameSize());
	        if (options.isPositionIndependent() && body.doesUses(GOTBaseReg())) {
	            loadGOTBaseAddress(file, GOTBaseReg());
	        }
	        file.addAll(body.assemblies());
	        epilogue(file, frame.saveRegs);
	        file.virtualStack.fixOffset(0);
	    }
	    static private List<Register> calleeSaveRegisters = new ArrayList<Register>();//寄存器结果
	    static final RegisterClass[] CALLEE_SAVE_REGISTERS = {
	        RegisterClass.BX, RegisterClass.BP,
	        RegisterClass.SI, RegisterClass.DI
	    };
	    static{
	            for (RegisterClass c : CALLEE_SAVE_REGISTERS) {
	            	calleeSaveRegisters.add(new Register(c, naturalType));
	            }	      
	        };
	    private AssemblyCode optimize(AssemblyCode body) {
	        if (options.optimizeLevel() < 1) {
	            return body;
	        }
	        body.apply(PeepholeOptimizer.defaultSet());
	        body.reduceLabels();
	        return body;
	    }
	    static final private long PARAM_START_WORD = 2;//函数参数对应的内存引用在2个4字节后
	    private void locateParameters(List<Parameter> params) {
	    	long numWords = PARAM_START_WORD;
	    	for(Parameter var : params) {
	    		var.setMemref(mem(stackSizeFromWordNum(numWords), bp()));
	    		numWords++;
	    	}
	    }
	    private long locateLocalVariables(LocalScope scope) {
	        return locateLocalVariables(scope, 0);
	    }

	    private long locateLocalVariables(LocalScope scope, long parentStackLen) {
	        long len = parentStackLen;//取得父作用域已经分配的内存引用偏移量
	        for (DefinedVariable var : scope.localVariables()) {
	            len = alignStack(len + var.allocSize());//对于下一个局部变量要内存对齐
	            var.setMemref(relocatableMem(-len, bp()));
	        }
	        long maxLen = len;
	        for (LocalScope s : scope.children()) {
	        	//所有的子作用域的len都相同,意味着同层作用域间的局部变量会覆盖
	            long childLen = locateLocalVariables(s, len);
	            maxLen = Math.max(maxLen, childLen);
	        }
	        return maxLen;
	    }
	   
	    static final private long STACK_WORD_SIZE = 4;
	    //栈上字节分配需要对齐
	    private long alignStack(long size) {
	        return AsmUtils.align(size, STACK_WORD_SIZE);
	    }


	    private long stackSizeFromWordNum(long numWords) {
	        return numWords * STACK_WORD_SIZE;
	    }
	    //加载字符串常量到寄存器中,常量的asmvalue为定义的标签
	    private void loadConstant(Expr node, Register reg) {
	        if (node.asmValue() != null) {
	            as.mov(node.asmValue(), reg);
	        }
	        else if (node.memref() != null) {
	            as.lea(node.memref(), reg);
	        }
	        else {
	            throw new Error("must not happen: constant has no asm value");
	        }
	    }
	    private void loadVariable(Var var, Register dest) {
	        if (var.memref() == null) {
	            Register a = dest.forType(naturalType);
	            as.mov(var.address(), a);
	            load(mem(a), dest.forType(var.type()));
	        }
	        else {
	            load(var.memref(), dest.forType(var.type()));
	        }
	    }
	    private IndirectMemoryReference relocatableMem(long offset, Register base) {
	        return IndirectMemoryReference.relocatable(offset, base);
	    }
	    //设定局部变量的偏移量
	    private void fixLocalVariableOffsets(LocalScope scope, long len) {
	        for (DefinedVariable var : scope.allLocalVariables()) {
	            var.memref().fixOffset(-len);
	        }
	    }

	    private void fixTempVariableOffsets(AssemblyCode asm, long len) {
	        asm.virtualStack.fixOffset(-len);
	    }
	    
	    private void extendStack(AssemblyCode file, long len) {
	        if (len > 0) {
	            file.sub(imm(len), sp());
	        }
	    }
	   
	    private void rewindStack(AssemblyCode file, long len) {
	        if (len > 0) {
	            file.add(imm(len), sp());
	        }
	    }
	    private void loadAddress(Entity var, Register dest) {
	        if (var.address() != null) {
	            as.mov(var.address(), dest);
	        }
	        else {
	            as.lea(var.memref(), dest);
	        }
	    }
	    private void compile(Stmt stmt){
	    	stmt.accept(this);
	    }
	    //转换一个表达式,
	    private void compile(Expr n) {
	        if (options.isVerboseAsm()) {
	            as.comment(n.getClass().getSimpleName() + " {");
	            as.indentComment();
	        }
	        n.accept(this);
	        if (options.isVerboseAsm()) {
	            as.unindentComment();
	            as.comment("}");
	        }
	    }
	    private Register GOTBaseReg() {
	        return bx();
	    }
	    private Register ax() { return ax(naturalType); }
	    private Register al() { return ax(Type.INT8); }
	    private Register bx() { return bx(naturalType); }
	    private Register cx() { return cx(naturalType); }
	    private Register cl() { return cx(Type.INT8); }
	    private Register dx() { return dx(naturalType); }
	    private Register ax(Type t) {
	        return new Register(RegisterClass.AX, t);
	    }

	    private Register bx(Type t) {
	        return new Register(RegisterClass.BX, t);
	    }
	  

	    private Register cx(Type t) {
	        return new Register(RegisterClass.CX, t);
	    }

	    private Register dx(Type t) {
	        return new Register(RegisterClass.DX, t);
	    }

	    private Register si() {
	        return new Register(RegisterClass.SI, naturalType);
	    }

	    private Register di() {
	        return new Register(RegisterClass.DI, naturalType);
	    }

	    private Register bp() {
	        return new Register(RegisterClass.BP, naturalType);
	    }

	    private Register sp() {
	        return new Register(RegisterClass.SP, naturalType);
	    }
	    private void load(MemoryReference mem, Register reg) {
	        as.mov(mem, reg);
	    }
	    //生成内存引用的方法,只有直接引用symbol为直接内存引用
	    private DirectMemoryReference mem(Symbol sym) {
	        return new DirectMemoryReference(sym);
	    }

	    private IndirectMemoryReference mem(Register reg) {
	        return new IndirectMemoryReference(0, reg);
	    }

	    private IndirectMemoryReference mem(long offset, Register reg) {
	        return new IndirectMemoryReference(offset, reg);
	    }

	    private IndirectMemoryReference mem(Symbol offset, Register reg) {
	        return new IndirectMemoryReference(offset, reg);
	    }
	    //生成立即数
	    private ImmediateValue imm(long n) {
	        return new ImmediateValue(n);
	    }

	    private ImmediateValue imm(Symbol sym) {
	        return new ImmediateValue(sym);
	    }

	    private ImmediateValue imm(Literal lit) {
	        return new ImmediateValue(lit);
	    }
}
