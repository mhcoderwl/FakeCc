package ast;
import type.*;
public class DoWhileNode extends StmtNode{
	private ExprNode cond;
	private StmtNode body;
	public DoWhileNode(Location location,ExprNode cond,StmtNode body){
		super(location);
		this.body=body;
		this.cond=cond;
	}
	public StmtNode body() {
        return body;
    }

    public ExprNode cond() {
        return cond;
    }
    protected void printTree(Dumper d) {
        d.printMember("body", body);
        d.printMember("cond", cond);
    }
    public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
