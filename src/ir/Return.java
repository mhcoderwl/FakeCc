package ir;

import ast.*;

public class Return extends Stmt{
	Expr expr;
	public Return(Location loc,Expr expr){
		super(loc);
		this.expr=expr;
	}
	@Override
	public void printTree(Dumper d){
		d.printMember("expr",expr);
	}
	public Expr expr(){
		return expr;
	}
	@Override
	public <S,E>S  accept(IRVisitor<S,E>visitor){
		return visitor.visit(this);
	}
}
