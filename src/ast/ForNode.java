package ast;

public class ForNode extends StmtNode{
	ExprStmtNode init;
	ExprNode cond;
	ExprStmtNode execute;
	StmtNode body;
	//参数为表达式类型的初始化和执行语句,要转成语句,因为这是能独立成语句的表达式.
	public ForNode(Location location,ExprNode init,ExprNode cond,ExprNode execute,StmtNode body){
		super(location);
		this.init=new ExprStmtNode(location,init);
		this.cond=cond;
		this.execute=new ExprStmtNode(location,execute);
		this.body=body;
	}
	 public StmtNode init() {
	        return init;
	    }

	    public ExprNode cond() {
	        return cond;
	    }

	    public StmtNode execute() {
	        return execute;
	    }

	    public StmtNode body() {
	        return body;
	    }
	public void printTree(Dumper d){
		d.printMember("init: ", init);
		d.printMember("cond: ", cond);
		d.printMember("execute: ", execute);
		d.printMember("body :", body);
	}
	public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
