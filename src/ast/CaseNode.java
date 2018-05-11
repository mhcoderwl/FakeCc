package ast;

import java.util.*;
import asm.*;
public class CaseNode extends StmtNode {
	private Label label;//汇编跳转的标签
    private List<ExprNode> values;
    private BlockNode body;

    public CaseNode(Location loc, List<ExprNode> values, BlockNode body) {
        super(loc);
        this.values = values;
        this.body = body;
        this.label = new Label();
    }

    public List<ExprNode> values() {
        return values;
    }

    public boolean isDefault() {
        return values.isEmpty();
    }

    public BlockNode body() {
        return body;
    }

    public Label label() {
        return label;
    }

    public void printTree(Dumper d) {
        d.printNodeList("values", values);
        d.printMember("body", body);
    }
    public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
