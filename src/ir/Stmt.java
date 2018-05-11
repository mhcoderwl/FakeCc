package ir;

import asm.*;
import ast.*;

/*
 * 语句的基类
 */
public abstract class Stmt implements Dumpable{
	protected Location location;
	public Stmt(Location location){
		this.location=location;
	}
	public void dump(Dumper d){
		d.printClass(this);
        printTree(d);
	}
	public abstract void printTree(Dumper d);
	//用来遍历中间代码树的visitor模式,生成asm代码
	public abstract <S,E>S accept(IRVisitor<S, E>visitor);
}
