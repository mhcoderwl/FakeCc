package ir;
import asm.*;
import ast.*;
/*
 * Op是操作不同类型的运算符
 */
public class UnaryOp extends Expr{
	 protected Op op;
	 protected Expr expr;
	 public UnaryOp(Type type, Op op, Expr expr) {
	        super(type);
	        this.op = op;
	        this.expr = expr;
	    }

	    public Op op() { return op; }
	    public Expr expr() { return expr; }

	    public <S,E> E accept(IRVisitor<S,E> visitor) {
	        return visitor.visit(this);
	    }

	    public void printTree(Dumper d) {
	        d.printMember("op", op.toString());
	        d.printMember("expr", expr);
	    }
}
