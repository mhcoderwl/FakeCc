package ast;



import type.*;


//条件表达式(a>0?)a:b
public class CondExprNode extends ExprNode{
	ExprNode cond;
	ExprNode left;
	ExprNode right;
	public CondExprNode(ExprNode cond,ExprNode left,ExprNode right){
		this.cond=cond;
		this.left=left;
		this.right=right;
	}
	//用左值的表达式的类型返回
	public Type type(){
		return this.left.type();
	}
	public ExprNode leftExpr(){
		return this.left;
	}
	public ExprNode rightExpr(){
		return this.right;
	}
	public ExprNode cond(){
		return this.cond;
	}
	//用条件的位置返回
	public Location location(){
		return cond.location();
	}
	public void setLeftExpr(ExprNode expr) {
        this.left= expr;
    }
	public void setRightExpr(ExprNode expr) {
        this.right = expr;
    }
	public void printTree(Dumper d){
		d.printMember("cond", cond);
		d.printMember("leftExpr", left);
		d.printMember("rightExpr", right);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
