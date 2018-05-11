package ast;

import java.util.*;
import entity.*;
import compiler.*;
import type.*;
import utils.*;
import exception.*;
import java.io.*;
public class AST extends Node{
	Declarations declarations;
	Location location;
	ToplevelScope scope;//保存作用域
	ConstantTable table;//保存常量表
	
	public AST(Location loc,Declarations dec){
		declarations=dec;
		location=loc;
	}
	//返回能调用的实体
	public List<Entity> entities(){
		List<Entity> enList=new ArrayList<Entity>();
		enList.addAll(declarations.funcdecls());
		enList.addAll(declarations.defuns());
		enList.addAll(declarations.vardecls());
		enList.addAll(declarations.defvars());
		enList.addAll(declarations.constants());
		return enList;
	}
	//对语法树进行语义检查,包括四项
	public void check(TypeTable table,ErrorHandler errorHandler)throws SemanticException{
		new LocalResolver(errorHandler).resolve(this);
		new TypeResolver(table, errorHandler).resolve(this);
		table.semanticCheck(errorHandler);
		new CorrectExprChecker(table, errorHandler).check(this);
        new TypeChecker(table, errorHandler).check(this);
	}
	public ToplevelScope scope(){
		return this.scope;
	}
	public ConstantTable constTable(){
		return this.table;
	}
	//返回只是声明的内容.
	 public List<Entity> declarations() {
	        List<Entity> result = new ArrayList<Entity>();
	        result.addAll(declarations.funcdecls());
	        result.addAll(declarations.vardecls());
	        return result;
	    }
	 public List<UndefinedFunction> funcdecls(){
		 return declarations.funcdecls();
	 }
	 //返回有定义的实体
	 public List<Entity> definitions() {
	        List<Entity> result = new ArrayList<Entity>();
	        result.addAll(declarations.defvars());
	        result.addAll(declarations.defuns());
	        result.addAll(declarations.constants());
	        return result;
	    }
	 /*
	  * 返回所有用户定义的类型
	  */
	 public List<TypeDefinition> types() {
	        List<TypeDefinition> result = new ArrayList<TypeDefinition>();
	        result.addAll(declarations.defstructs());
	        result.addAll(declarations.defunions());
	        result.addAll(declarations.typedefs());
	        return result;
	    }
	 
	 public List<Constant> constants() {
	        return declarations.constants();
	    }

	    public List<DefinedVariable> definedVariables() {
	        return declarations.defvars();
	    }

	    public List<DefinedFunction> definedFunctions() {
	        return declarations.defuns();
	    }
	    
	    public void setScope(ToplevelScope scope){
	    	this.scope=scope;
	    }
	    public void setConstantTable(ConstantTable table){
	    	this.table=table;
	    }
	    public void printTokens(PrintStream s) {
	        for (CflatToken t : location.token()) {
	            printPair(t.kindName(), t.dumpedImage(), s);
	        }
	    }

	    static final private int NUM_LEFT_COLUMNS = 24;

	    private void printPair(String key, String value, PrintStream s) {
	        s.print(key);
	        for (int n = NUM_LEFT_COLUMNS - key.length(); n > 0; n--) {
	            s.print(" ");
	        }
	        s.println(value);
	    }
	public Location location(){
		return this.location;
	}
	protected void printTree(Dumper d){
		d.printNodeList("variables", definedVariables());
        d.printNodeList("functions", definedFunctions());
	}
}
