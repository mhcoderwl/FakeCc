package ir;

import asm.*;
import ast.*;

public class Jump extends Stmt{
	private Label label;
	public Jump(Location loc,Label label){
		super(loc);
		this.label=label;
	}
	public Label label() {
        return label;
    }
	@Override
	public void printTree(Dumper d) {
		d.printMember("label",label);
	}
@Override
	public <S, E> S accept(IRVisitor<S, E> visitor) {
		return visitor.visit(this);
	}
}
