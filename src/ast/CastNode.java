package ast;

import type.*;

public class CastNode extends ExprNode{
	//一个强制类型转换表达式有转换的类型和要转换的表达式组成
	TypeNode typeNode;
	ExprNode expr;
	public CastNode(TypeNode type ,ExprNode expr){
		this.typeNode=type;
		this.expr=expr;
	}
	//一些接口
	public Type type(){
		return this.typeNode.type();
	}
	public TypeNode typeNode(){
		return this.typeNode;
	}
	public ExprNode expr(){
		return expr;
	}
	public boolean isLvalue(){
		return this.expr.isLvalue();
	}
	//考虑强制类型转换是否正确的初步判别方法,两个类型的大小关系.
	public boolean isEffectiveCast() {
        return type().size() > expr.type().size();
    }
	public Location location(){
		return expr.location();
	}
	public void printTree(Dumper d){
		d.printMember("expr: ", expr);
		d.printMember("castType: ",typeNode );
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
