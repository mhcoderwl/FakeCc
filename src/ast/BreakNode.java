package ast;

public class BreakNode extends StmtNode{
	public BreakNode(Location location) {
		super(location);// TODO Auto-generated constructor stub
	}
	public void printTree(Dumper d){
		
	}
	public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
