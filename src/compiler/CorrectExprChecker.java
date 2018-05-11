package compiler;

import java.util.*;
import ast.*;
import entity.*;
import exception.*;
import type.*;
import utils.*;
/*
 * 检查表达式是否正确的类,那么只要是会出现exprNode的地方都要检查
 */
public class CorrectExprChecker extends Visitor{
	private TypeTable table;
	private ErrorHandler errorHandler;
	public CorrectExprChecker(TypeTable table,ErrorHandler errorHandler){
		this.table=table;
		this.errorHandler=errorHandler;
	}
	public void check(AST ast)throws SemanticException{
		checkGlobalVariables(ast.definedVariables());
		checkfunctions(ast.definedFunctions());
		if (errorHandler.errorOccured()) {
            throw new SemanticException("compile failed.");
        }
	}
	public void checkGlobalVariables(List<DefinedVariable>vars){
		for(DefinedVariable var: vars){
			checkGlobalVariable(var);
		}
	}
	public void checkfunctions(List<DefinedFunction>funcs){
		for(DefinedFunction f: funcs){
			checkStmt(f.body());
		}
	}
	//全局变量如果有初始化,那么初始化的表达式一定是一个常量,不能是变量.
	public void checkGlobalVariable(DefinedVariable var){
		checkVariable(var);
		if(var.hasInitializer()){
			checkConstant(var.initializer());
		}
	}
	public void checkConstant(ExprNode node){
		if(!node.isConstant()){
			error(node, "not a constant");
		}
	}
	/*
	 * 变量定义中含有表达式
	 */
	public void checkVariable(DefinedVariable var){
		if(var.hasInitializer()){
			checkExpr(var.initializer());
		}
	}
	
	public void checkExpr(ExprNode expr){
		expr.accept(this);
	}
	public void checkStmt(StmtNode expr){
		expr.accept(this);
	}
	
	//***********表达式的处理**************
	 public Void visit(AssignNode node) {
	        super.visit(node);
	        checkAssignment(node);
	        return null;
	    }

	    public Void visit(OpAssignNode node) {
	        super.visit(node);
	        checkAssignment(node);
	        return null;
	    }

	    private void checkAssignment(AbstractAssignNode node) {
	        if (! node.lhs().isAssignable()) {
	            error(node.location(), "invalid lhs expression");
	        }
	    }
	 public Void visit(PrefixOpNode node) {
	        super.visit(node);
	        if (! node.expr().isAssignable()) {
	            error(node.expr().location(),
	                    "cannot increment/decrement");
	        }
	        return null;
	    }

	    public Void visit(SuffixOpNode node) {
	        super.visit(node);
	        if (! node.expr().isAssignable()) {
	        	error(node.expr().location(),
	                    "cannot increment/decrement");
	        }
	        return null;
	    }

	    public Void visit(FunCallNode node) {
	        super.visit(node);
	        if (! node.expr().isCallable()) {
	        	error(node.location(),
	                    "calling object is not a function");
	        }
	        return null;
	    }
	public Void visit(BlockNode block){
		for(StmtNode stmt:block.stmts())
		visitStmt(stmt);
		return null;
	}
	//如果是一个常量要检查
	public Void visit(VariableNode node){
		super.visit(node);
		if(node.entity().isConstant()){
			checkConstant(node);
		}
		return null;
	}
	public Void visit(CastNode node) {
        super.visit(node);
        if (node.type().isArray()) {
            error(node.location(), "cast specifies array type");
        }
        return null;
    }
	//取值节点首先表达式一定是一个左值,对于函数或者数组类型直接赋于其类型,不用转成指针类型再设置.
	public Void visit(AddressNode node) {
        super.visit(node);
        if (! node.expr().isLvalue()) {
            error(node.location(), "invalid expression for &");
        }
        Type base = node.expr().type();
        if (! node.expr().isLoadable()) {
            
            node.setType(base);
        }
        else {
            node.setType(table.pointerTo(base));
        }
        return null;
    }
	/*
	 * 首先对取值的表达式检查是否为指针类型.然后对数组类型和函数类型进行处理.
	 */
	public Void visit(DereferenceNode node){
		super.visit(node);
		if(!node.expr().isPointer()){
			undereferableError(node.location());
		}
		
		return null;
	}
	public Void  visit(MemberNode node){
		super.visit(node);
		
		checkHasMember(node.expr().type(),node.location(),node.member());
		return null;
	}
	public Void  visit(PtrMemberNode node){
		super.visit(node);
		if(!node.expr().isPointer()){
			undereferableError(node.location());
		}
		checkHasMember(node.dereferedType(),node.location(),node.member());
		return null;
	}
	public void checkHasMember(Type type,Location loc,String member){
		if(!type.isCompositeType()){
			error(loc, "non-compositeType accessing member: "+member);
		}
		CompositeType tmp=(CompositeType)type;
		if(!tmp.hasMember(member)){
			error(loc,tmp+" do not have member : "+member);
		}
	}
	public void undereferableError(Location loc){
		error(loc,"undereferable error");
	}
	/*
	 * 错误处理
	 */
	private void error(Node node, String msg) {
        errorHandler.error(node.location(), msg);
    }
	private void error(Location loc, String msg) {
        errorHandler.error(loc, msg);
    }
	
	
}
