package ir;
import asm.*;
import ast.*;
/*
 * 条件跳转,需要满足条件和不满足的对应跳转方向.
 */
public class CJump extends Stmt{
	//存有条件表达式,条件符合的跳转目标,其他情况的跳转目标
	protected Expr cond;
    protected Label thenLabel;
    protected Label elseLabel;

    public CJump(Location loc, Expr cond, Label thenLabel, Label elseLabel) {
        super(loc);
        this.cond = cond;
        this.thenLabel = thenLabel;
        this.elseLabel = elseLabel;
    }

    public Expr cond() {
        return cond;
    }

    public Label thenLabel() {
        return thenLabel;
    }

    public Label elseLabel() {
        return elseLabel;
    }

    public <S,E> S accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }

    public void printTree(Dumper d) {
        d.printMember("cond", cond);
        d.printMember("thenLabel", thenLabel);
        d.printMember("elseLabel", elseLabel);
    }
}
