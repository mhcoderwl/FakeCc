package ast;

import type.*;
import exception.*;
/*
 * .调用成员
 */
public class MemberNode extends LHSNode{
	private ExprNode expr;//调用的表达式,一定是一个结构体或者集合.
	private String member;//调用的成员
	 public MemberNode(ExprNode expr, String member) {
	        this.expr = expr;
	        this.member = member;
	    }
	 public CompositeType baseType() {
	        try {
	            return (CompositeType)expr.type();
	        }
	        catch (ClassCastException err) {
	            throw new SemanticError(err.getMessage());
	        }
	    }
	 //查看当前调用的成员在结构体中的偏移量
	 public long offset() {
	        return baseType().memberOffset(member);
	    }
	 public String member(){
		 return member;
	 }
	 public Location location(){
		 return expr.location();
	 }
	 public ExprNode expr(){
		 return this.expr;
	 }
	 public Type origType() {
	        return baseType().memberType(member);
	    }
	 public void printTree(Dumper d){
		 d.printMember("expr: ", expr);
		d.printMember("member", member);
	 }
	 public<S,E> E  accept(ASTVisitor<S, E> visitor){
	    	return visitor.visit(this);
	    }
}
