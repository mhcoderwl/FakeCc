package entity;

import ast.*;
import ir.*;
import type.*;
import java.util.*;
/*
 * 一个函数的定义有参数列表,函数体,函数名,返回类型,返回类型作为这个函数的整体类型
 */
public class DefinedFunction extends Function{
	    private BlockNode body;
	    private LocalScope scope;
	    private List<Stmt> irList;
	    public DefinedFunction(boolean isStatic, TypeNode type,
	            String name, Params params, BlockNode body) {
	        super(isStatic, type, name,params);
	        this.body = body;
	    }

	    public boolean isDefined() {
	        return true;
	    }
	    public void setIR(List<Stmt> irList){
	    	this.irList=irList;
	    }
	    public List<Stmt> ir(){
	    	return this.irList;
	    }
	    public List<Parameter> parameters() {
	        return params.parameters();
	    }
	    public List<DefinedVariable> localVariables() {
	        return scope.allLocalVariables();
	    }
	    public BlockNode body() {
	        return body;
	    }
	    /*
		 * 设置作用域
		 */
		public Scope setScope(LocalScope scope){
			return this.scope=scope;
		}
	    public void printTree(ast.Dumper d){
	    	d.printMember("name", name);
	        d.printMember("isStatic", isStatic);
	        d.printMember("params", params);
	        d.printMember("body", body);
	    }
	    public boolean isPrivate(){
	    	return isStatic;
	    }
	    public <T> T accept(EntityVisitor<T> visitor) {
	        return visitor.visit(this);
	    }
}
