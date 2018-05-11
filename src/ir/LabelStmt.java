package ir;

import asm.*;
import ast.*;

/*
 * 标签
 */
public class LabelStmt extends Stmt{
	 private Label label;

	    public LabelStmt(Location loc, Label label) {
	        super(loc);
	        this.label = label;
	    }

	    public Label label() {
	        return label;
	    }

	    public <S,E> S accept(IRVisitor<S,E> visitor) {
	        return visitor.visit(this);
	    }

	    public void printTree(Dumper d) {
	        d.printMember("label", label);
	    }
}
