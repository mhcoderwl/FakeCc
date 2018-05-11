package ast;

import type.*;

public class IntegerLiteralNode extends LiteralNode{
	private long value;
	public IntegerLiteralNode(Location loc,TypeRef ref,long value){
		super(loc, ref);
		this.value=value;
	}
	public long value(){
		return value;
	}
	public void printTree(Dumper d){
		d.printMember("value: ", value);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
