package ir;

import asm.*;
import java.util.*;
import ast.*;
public class Switch extends Stmt{
	private Expr cond;
	private List<Case> cases;
	private Label defaultLabel,endLabel;
	public Switch(Location loc, Expr cond,
            List<Case> cases, Label defaultLabel, Label endLabel) {
        super(loc);
        this.cond = cond;
        this.cases = cases;
        this.defaultLabel = defaultLabel;
        this.endLabel = endLabel;
    }

    public Expr cond() {
        return cond;
    }

    public List<Case> cases() {
        return cases;
    }

    public Label defaultLabel() {
        return defaultLabel;
    }

    public Label endLabel() {
        return endLabel;
    }

    public <S,E> S accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }

    public void printTree(Dumper d) {
        d.printMember("cond", cond);
        d.printMembers("cases", cases);
        d.printMember("defaultLabel", defaultLabel);
        d.printMember("endLabel", endLabel);
    }
}
