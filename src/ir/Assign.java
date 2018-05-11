package ir;

import ast.*;
/*
 * 赋值在这里转成了语句
 */
public class Assign extends Stmt{
	protected Expr lhs, rhs;

    public Assign(Location loc, Expr lhs, Expr rhs) {
        super(loc);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expr lhs() {
        return lhs;
    }

    public Expr rhs() {
        return rhs;
    }

    public <S,E> S accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }

    public void printTree(Dumper d) {
        d.printMember("lhs", lhs);
        d.printMember("rhs", rhs);
    }
	
}
