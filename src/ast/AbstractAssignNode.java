package ast;
import type.*;
public abstract class AbstractAssignNode extends ExprNode{
	ExprNode left;
	ExprNode right;
	public AbstractAssignNode(ExprNode lhs, ExprNode rhs) {
        super();
        this.left = lhs;
        this.right = rhs;
    }

    public Type type() {
        return left.type();
    }

    public ExprNode lhs() {
        return left;
    }

    public ExprNode rhs() {
        return right;
    }

    public void setRHS(ExprNode expr) {
        this.right = expr;
    }

    public Location location() {
        return left.location();
    }

    protected void printTree(Dumper d) {
        d.printMember("lhs", left);
        d.printMember("rhs", right);
    }
    
}
