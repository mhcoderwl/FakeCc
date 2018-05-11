package ast;

import type.*;
public class AddressNode extends ExprNode{
	ExprNode expr;
	Type type;//表示整个节点是什么类型
	public AddressNode(ExprNode expr) {
		super();
		this.expr=expr;
	}
	public Location location(){
		return expr.location();
	}
	public Type type(){
		return this.type;
	}
	public void setType(Type t){
		this.type=t;
	}
	public ExprNode expr(){
		return this.expr;
	}
	public void printTree(Dumper d){
		d.printMember("expr: ",expr );
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
