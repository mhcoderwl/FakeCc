package ast;

public class SuffixOpNode extends UnaryArithmeticOpNode{
	public SuffixOpNode(String op,ExprNode expr) {
		super(op, expr);// TODO Auto-generated constructor stub
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
