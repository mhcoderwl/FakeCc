package ast;

import java.util.*;
import type.*;
public class FunCallNode extends ExprNode{
	List<ExprNode>  args;//实参列表
	ExprNode expr;//调用的表达式
	public FunCallNode(ExprNode expr,List<ExprNode> args){
		super();
		this.args=args;
		this.expr=expr;
	}
	public ExprNode expr() {
        return expr;
    }
	public Type type(){
		return expr.type();
	}
	public FunctionType functionType(){
		return (FunctionType)expr.type();
	}
	public Location location(){
		return expr.location();
	}
	public List<ExprNode> args() {
        return args;
    }
	public long numArgs(){
		return args.size();
	}
	public void replaceArgs(List<ExprNode>args){
		this.args=args;
	}
	public void printTree(Dumper d){
		d.printMember("expr: ", expr);
		d.printNodeList("params: ", args);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
