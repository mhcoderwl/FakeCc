package ast;

public class GotoNode extends StmtNode{
	protected String target;//跳转的字面量

    public GotoNode(Location loc, String target) {
        super(loc);
        this.target = target;
    }

    public String target() {
        return target;
    }

    protected void printTree(Dumper d) {
        d.printMember("target", target);
    }
    public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }

}
