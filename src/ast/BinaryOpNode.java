package ast;
import java.util.*;


import type.Type;
//二元运算符 例如(x+y),二元运算符的类型在解析期间待定,在语义分析的时候设定.
public class BinaryOpNode extends ExprNode{
	protected String operator;
	protected ExprNode left,right;
	protected Type type;//表达式整体的类型
	public BinaryOpNode(ExprNode left,String op,ExprNode right){
		super();
		this.operator=op;
		this.left=left;
		this.right=right;
	}
	public BinaryOpNode(Type type,ExprNode left,String op,ExprNode right){
		super();
		this.type=type;
		this.operator=op;
		this.left=left;
		this.right=right;
	}
	//一些接口
	public String operator(){
		return this.operator;
	}
	public ExprNode left(){
		return this.left;
	}
	public ExprNode right(){
		return this.right;
	}
	public Type type(){
		return this.type;
	}
	public void setLeft(ExprNode left){
		this.left=left;
	}
	public void setRight(ExprNode right){
		this.right=right;
	}
	public void setType(Type type){
		if(this.type!=null){
			throw new Error("BinaryOp#setType called twice");
		}
		this.type=type;
	}
	public Location location(){
		return left.location();
	}
	protected void printTree(Dumper d){
		d.printMember("operator",operator);
		d.printMember("left",left);
		d.printMember("right",right);
	}
	public boolean isBoolean(){
		return operator.equals("<")||operator.equals(">")
					||operator.equals(">=")||operator.equals("<=")
					||operator.equals("==")||operator.equals("!=");
	}
	public <S,E> E accept(ASTVisitor<S,E> visitor){
		return visitor.visit(this);
	}
}
