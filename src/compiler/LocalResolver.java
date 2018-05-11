package compiler;

import java.util.*;
import ast.*;
import entity.*;
import exception.*;
import utils.*;
public class LocalResolver extends Visitor{
	private LinkedList<Scope>scopeStack;//用来处理的栈
	private ConstantTable constantTable;//用来管理字符串常量的表
	private ErrorHandler errorHandler;
	public LocalResolver(ErrorHandler errorHandler){
		this.errorHandler=errorHandler;
		scopeStack=new LinkedList<Scope>();
	}
	/*
	 * 消解变量引用的方法,从语法树的根节点开始,首先push全局作用域,然后在逐步消解各个部分
	 * 最后把消解的结果存到AST节点中,是为了下一步中间代码生成使用.
	 */
	public void resolve(AST ast)throws SemanticException{
		ToplevelScope scope=new ToplevelScope();
		scopeStack.push(scope);
		//首先添加全局声明
		for(Entity entity:ast.declarations() ){
			scope.declareEntity(entity);
		}
		//添加全局定义
		for(Entity entity :ast.definitions()){
			scope.defineEntity(entity);
		}
		resolveConstantValues(ast.constants());
		resolveFunctions(ast.definedFunctions());
		resolveDefinedGvars(ast.definedVariables());
		//scope.checkReference(errorHandler);
		//保存信息
		ast.setScope(scope);
		ast.setConstantTable(constantTable);
	}
	public void resolveConstantValues(List<Constant>constants){
		for(Constant constant:constants){
			resolve(constant);
		}
	}
	public void resolveDefinedGvars(List<DefinedVariable> vars){
		for(DefinedVariable var:vars){
			if(var.hasInitializer())
			resolve(var.initializer());
		}
	}
	public void resolveFunctions(List<DefinedFunction> funcs){
		for(DefinedFunction func:funcs){
			resolve(func);
		}
	}
	/*
	 * 消解一个函数,函数参数列表也形成一个作用域.
	 */
	public void resolve(DefinedFunction func){
		pushScope(func.parameters());
		resolve(func.body());
		func.setScope((LocalScope)popScope());
	}
	/*
	 * 消解一个常量,因为常量的定义中有表达式,可能有变量.
	 */
	public void resolve(Constant cons){
		resolve(cons.value());
	}
	
	public void resolve(ExprNode node){
		node.accept(this);
	}
	public void resolve(StmtNode node){
		node.accept(this);
	}
	public Scope currentScope(){
		return scopeStack.peek();
	}
	
	/*
	 * 入栈,方法压如的都是LocalScope类型.
	 */
	public void pushScope(List<? extends DefinedVariable> vars){
		LocalScope scope = new LocalScope(currentScope());
        for (DefinedVariable var : vars) {
            if (scope.hasDefinedVariable(var.name())) {
                error(var.location(),
                    "duplicated variable in scope: " + var.name());
            }
            else {
                scope.defineVariable(var);
            }
        }
        scopeStack.addFirst(scope);
	}
	public Scope popScope(){
		return scopeStack.pop();
	}
	/*
	 * 根据此类的功能实现的visit方法
	 */
	/*
	 *对于函数块,我们要push一个作用域,然后在visit,最后弹出作用域
	 */
	public Void visit(BlockNode node){
		pushScope(node.variables());//添加新的局部变量
		super.visit(node);
		node.setScope((LocalScope)popScope());
		return null;
	}
	public Void visit(VariableNode node){
		try{
		String varName=node.name();
		Entity ent=currentScope().get(varName);//向上查找定义
		ent.refered();//此定义被引用次数增加
		node.setEntity(ent);//可能是一个函数名,因此联系的是entity类而不是variable类
		}catch(SemanticException e){
			error(node.location(), e.getMessage());
		}
		return null;
	}
	//一个字符串字面量
	public Void visit(StringLiteralNode node){
		String name=node.value();
		node.setEntry(constantTable.insert(name));
		return null;
	}
	/*
	 * 错误处理
	 */
	private void error(Node node, String message) {
        errorHandler.error(node.location(), message);
    }

    private void error(Location loc, String message) {
        errorHandler.error(loc, message);
    }
}
