package entity;
import ast.*;
/*
 * const常量必须在定义时初始化,所以有expr
 */
public class Constant extends Entity{
	private ExprNode expr;
	public Constant( TypeNode type, String name,ExprNode expr){
		super(true, type, name);
		this.expr=expr;
	}
	public boolean isAssignable() { return false; }
    public boolean isDefined() { return true; }
    public boolean isInitialized() { return true; }
    public boolean isConstant() { return true; }
    public ExprNode value(){return expr;}
	public void printTree(Dumper d){
		d.printMember("expr: ", expr);
		
	}
	public <T> T accept(EntityVisitor<T> visitor){
		return visitor.visit(this);
	}
}
