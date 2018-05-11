package ast;
import type.*;
//数组节点,数组由一个表达式加上一个[]和一个index表达式组成.
public class ArefNode extends LHSNode{
	private ExprNode expr;
	private ExprNode index;
	public ArefNode(ExprNode expr,ExprNode index){
		this.expr=expr;
		this.index=index;
	}
	 public ExprNode expr() { return expr; }
	    public ExprNode index() { return index; }
	// a[x][y][z] is a.返回最底层的那个类型
	public ExprNode baseExpr(){
		if(isMultiDimension())return ((ArefNode)expr).baseExpr();
		else return expr;
	}
	public boolean isMultiDimension(){
		return expr instanceof ArefNode;
	}
	
	protected Type origType() {
        return expr.origType().baseType();
    }
	//所含基本元素类型的大小
	public long elementSize() {
        return origType().allocSize();
    }
    public long length() {
        return ((ArrayType)expr.origType()).length();
    }
	 protected void printTree(Dumper d) {
	        if (type != null) {
	            d.printMember("type", type);
	        }
	        d.printMember("expr", expr);
	        d.printMember("index", index);
	    }
	 public Location location(){
		 return expr.location();
	 }
	 public<S,E> E  accept(ASTVisitor<S, E> visitor){
	    	return visitor.visit(this);
	    }

}
