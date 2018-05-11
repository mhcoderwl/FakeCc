package ir;

import entity.Entity;
import asm.*;
import ast.*;
/*
 * 相比于语法树的表达式,多了一个asm.type,来表示返回的INT类型.
 */
public abstract class Expr implements Dumpable{
	protected Type type;
	public Expr(Type type){
		this.type=type;
	}
	public Type type(){
		return type;
	}
	public boolean isVar(){
		return this instanceof Var;
	}
	public ImmediateValue asmValue() {
        throw new Error("Expr#asmValue called");
    }
	  public Operand address() {
	        throw new Error("Expr#address called");
	    }

	    public MemoryReference memref() {
	        throw new Error("Expr#memref called");
	    }
	   
	    public Entity getEntity(){
	    	return null;
	    }
	public void dump(Dumper d){
		d.printClass(this);
        d.printMember("type", type);
        printTree(d);
	}
	public Expr addressNode(asm.Type type){
		throw new Error("error");
	}
	public boolean isAddr() { return false; }
	public abstract void printTree(Dumper d);
	//用来遍历中间代码树的visitor模式
	public abstract <S,E>E accept(IRVisitor<S, E>visitor);
}
