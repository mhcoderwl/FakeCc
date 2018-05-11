package ir;

import ast.*;

/*
 * 表达式语句
 */
public class ExprStmt extends Stmt{
	private Expr expr;
	public ExprStmt(Location loc,Expr expr){
		super(loc);
		this.expr=expr;
	}
	public Expr expr() {
        return expr;
    }
	@Override
	public <S,E> S accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }
	@Override
    public void printTree(Dumper d) {
        d.printMember("expr", expr);
	}
}
