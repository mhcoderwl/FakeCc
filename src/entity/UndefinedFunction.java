package entity;

import ast.*;
import java.util.*;
/*
 * 声明的函数不能为静态的.
 */
public class UndefinedFunction extends Function{
	
	public UndefinedFunction(TypeNode t, String name,Params params) {
        super(false,t,name,params);
    }
	public boolean isDefined(){
		return false;
	}
	public void printTree(Dumper d){
		 d.printMember("name", name);
	        d.printMember("isStatic", isStatic());
	        d.printMember("typeNode", typeNode);
	        d.printMember("params", params);
	}
	public List<Parameter> parameters() {
        return params.parameters();
    }
	public <T> T accept(EntityVisitor<T> entityVisitor){
		return entityVisitor.visit(this);
	}
}
