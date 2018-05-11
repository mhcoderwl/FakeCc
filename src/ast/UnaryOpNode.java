package ast;
import type.*;
//一元操作符
public  class UnaryOpNode extends ExprNode{
	String operator;//操作符
	ExprNode expr;
	Type opType;//操作符的类型
	 public UnaryOpNode(String op, ExprNode expr) {
	        this.operator = op;
	        this.expr = expr;
	    }

	    public String operator() {
	        return operator;
	    }
	    public void setOpType(Type t) {
	        this.opType = t;
	    }
	    public Type opType() {
	        return opType;
	    }
	    public Type type() {
	        return expr.type();
	    }
	    public ExprNode expr() {
	        return expr;
	    }

	    public void setExpr(ExprNode expr) {
	        this.expr = expr;
	    }

	    public Location location() {
	        return expr.location();
	    }

	    protected void printTree(Dumper d) {
	        d.printMember("operator", operator);
	        d.printMember("expr", expr);
	    }
	    public <S,E> E accept(ASTVisitor<S,E> visitor) {
	        return visitor.visit(this);
	    }

}
