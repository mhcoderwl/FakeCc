package ast;

public class OpAssignNode extends AbstractAssignNode{
	String operator;
	public OpAssignNode(ExprNode lhs,ExprNode rhs,String operator){
		super(lhs, rhs);
		this.operator=operator;
	}
	public String operator(){
		return operator;
	}
	public void printTree(Dumper d){
		d.printMember("operator: ", operator);
		super.printTree(d);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
