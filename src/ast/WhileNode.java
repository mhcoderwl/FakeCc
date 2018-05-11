package ast;

public class WhileNode extends StmtNode{
	StmtNode body;
	ExprNode cond;
	public WhileNode(Location location,ExprNode cond,StmtNode body){
		super(location);
		this.cond=cond;
		this.body=body;
	}
	public ExprNode condition(){
		return cond;
	}
	public StmtNode body(){
		return body;
	}
	
	public void printTree(Dumper d){
		d.printMember("cond: ", cond);
		d.printMember("body: ",body);
		
	}
	public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
