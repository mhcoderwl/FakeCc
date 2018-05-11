package ast;

import type.*;
import java.util.*;
//switch和case是组合关系不是继承关系,他们都属于语句这一类.
public class SwitchNode extends StmtNode {
    protected ExprNode cond;
    protected List<CaseNode> cases;

    public SwitchNode(Location loc, ExprNode cond, List<CaseNode> cases) {
        super(loc);
        this.cond = cond;
        this.cases = cases;
    }

    public ExprNode cond() {
        return cond;
    }

    public List<CaseNode> cases() {
        return cases;
    }

    protected void printTree(Dumper d) {
        d.printMember("cond", cond);
        d.printNodeList("cases", cases);
    }
    public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
