package ir;
import asm.*;
import ast.*;
/*
 * 解引用*ptr
 */
public class Derefer extends Expr{
	 private Expr expr;

	    public Derefer(Type type, Expr expr) {
	        super(type);
	        this.expr = expr;
	    }

	    public Expr expr() { return expr; }
	    
	    //返回所解的地址
	    public Expr addressNode(Type type) {
	        return expr;
	    }

	    public <S,E> E accept(IRVisitor<S,E> visitor) {
	        return visitor.visit(this);
	    }

	    public void printTree(Dumper d) {
	        d.printMember("expr", expr);
	    }
}
