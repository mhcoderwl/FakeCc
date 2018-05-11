package ast;

public class ContinueNode extends StmtNode{
	 public ContinueNode(Location loc) {
	        super(loc);
	    }

	    protected void printTree(Dumper d) {
	    	
	    }
	    public<S,E> S  accept(ASTVisitor<S, E> visitor){
	    	return visitor.visit(this);
	    }
}
