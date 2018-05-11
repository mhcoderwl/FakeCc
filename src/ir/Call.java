package ir;
import asm.*;
import ast.*;
import java.util.*;
import entity.*;
/*
 * call函数调用,具体成员和FunCallNode一样
 */
public class Call extends Expr{
	private Expr expr;
    private List<Expr> args;

    public Call(Type type, Expr expr, List<Expr> args) {
        super(type);
        this.expr = expr;
        this.args = args;
    }

    public Expr expr() { return expr; }
    public List<Expr> args() { return args; }

    public long numArgs() {
        return args.size();
    }

   public boolean isFunctionCall(){
	   return expr.getEntity() instanceof Function;
   }
    public <S,E> E accept(IRVisitor<S,E> visitor) {
        return visitor.visit(this);
    }
    public Function function() {
        Entity ent = expr.getEntity();
        if (ent == null) {
            throw new Error("not a static funcall");
        }
        return (Function)ent;
    }
    public void printTree(Dumper d) {
        d.printMember("expr", expr);
        d.printMembers("args", args);
    }
}
