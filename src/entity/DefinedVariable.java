package entity;

import ir.*;
import asm.Symbol;
import ast.*;

//定义的变量实体
public class DefinedVariable extends Variable{
	private ExprNode initializer;//用来初始化变量的表达式
	private long sequence=-1;//用来处理静态变量名称冲突的问题
	private Expr ir;//初始化的中间代码
	private Symbol symbol;
	public DefinedVariable(boolean isStatic, TypeNode type, String name, ExprNode init){
		super(isStatic, type, name);
		this.initializer=init;
	}
	//静态方法用来添加临时局部变量,用于一些表达式的中间代码转换.
	static private long tmpSeq = 0;//区分名字
	static public DefinedVariable tmp(type.Type t) {
        return new DefinedVariable(false,
                new TypeNode(t), "@tmp" + tmpSeq++, null);
    }
	public Expr ir(){
		return this.ir;
	}
	public ExprNode initializer(){
		return this.initializer;
	}
	public boolean hasInitializer(){
		return initializer!=null;
	}
	public void setInitializer(ExprNode expr) {
        this.initializer = expr;
    }
	public boolean isDefined(){
		return true;
	}
	public void setIR(Expr ir){
		this.ir=ir;
	}
	public void setSequence(int seq){
		this.sequence=seq;
	}
	public void printTree(ast.Dumper d) {
        d.printMember("name", name);
        d.printMember("isStatic", isStatic);
        d.printMember("typeNode", typeNode);
        d.printMember("initializer", initializer);
    }
	public String symbolString() {
        return (sequence < 0) ? name : (name + "." + sequence);
    }
	public <T> T accept(EntityVisitor<T> entityVisitor){
		return entityVisitor.visit(this);
	}
}
