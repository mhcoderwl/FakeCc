package ir;

import entity.*;
import asm.*;
/*
 * 字符串常量,成员是字符串常量的包装ConstantEntry
 */
public class Str extends Expr{
	ConstantEntry entry;
	Symbol symbol;
	public Str(Type type,ConstantEntry entry){
		super(type);
        this.entry = entry;
	}
	public void printTree(Dumper d){
		d.printMember("entry: ", entry.toString());
	}
	//返回字符串常量在汇编中的值,是一个地址,用一个标签表示.
	public ImmediateValue asmValue() {
        return entry.address();
    }
	public <S,E> E accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }
	public Symbol symbol(){
		return this.symbol;
	}
}
