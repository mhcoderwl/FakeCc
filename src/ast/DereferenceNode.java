package ast;

import type.*;
//解指针节点*ptr
public class DereferenceNode extends LHSNode{
	ExprNode expr;
	public DereferenceNode(ExprNode expr){
		super();
		this.expr=expr;
	}
	public Location location(){
		return expr.location();
	}

    public ExprNode expr() {
        return expr;
    }
    //解引用的表达式的基本类型
    protected Type origType() {
        return expr.type().baseType();
    }
    public void setExpr(ExprNode expr) {
        this.expr = expr;
    }

	public void printTree(Dumper d){
		 if (type != null) {
	            d.printMember("type", type);
	        }
	        d.printMember("expr", expr);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
