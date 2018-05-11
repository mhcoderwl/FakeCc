package compiler;
import java.util.*;
import entity.*;
import ast.*;
/*
 * 这个抽象类的作用是用来在语义分析的各个类中复用遍历同样节点的代码.
 * S,E分别是语句和表达式的visitor的返回类型.这些方法的返回值无需使用,用Void类型泛型.
 */

public abstract class Visitor implements ASTVisitor<Void, Void>{

	protected void visitStmt(StmtNode sn){
		sn.accept(this);
	}
	protected void visitStmts(List<? extends StmtNode> snList){
		for (StmtNode stmtNode : snList) {
			visitStmt(stmtNode);
		}
	}
	protected void visitExpr(ExprNode expr) {
        expr.accept(this);
    }

    protected void visitExprs(List<? extends ExprNode> exprs) {
        for (ExprNode e : exprs) {
            visitExpr(e);
        }
    }
    //遍历语句节点的调用
    public Void visit(BlockNode node) {
        for (DefinedVariable var : node.variables()) {
            if (var.hasInitializer()) {
                visitExpr(var.initializer());
            }
        }
        visitStmts(node.stmts());
        return null;
    }
  
    public Void visit(ExprStmtNode node){
    	visitExpr(node.expr());
    	return null;
    }
    public Void visit(IfNode node){
    	visitExpr(node.cond());
    	if(node.elseBody()!=null)
    		visitStmt(node.elseBody());
    	visitStmt(node.thenBody());
    	return null;
    }
    public Void visit(SwitchNode node){
    	visitExpr(node.cond());
    	visitStmts(node.cases());
    	return null;
    }
    public Void visit(CaseNode node){
    	visitExprs(node.values());
    	visitStmt(node.body());
    	return null; 
    }
    public Void visit(WhileNode node){
    	visitExpr(node.condition());
    	visitStmt(node.body());
    	return null;
    }
    public Void visit(DoWhileNode node){
    	visitExpr(node.cond());
    	visitStmt(node.body());
    	return null;
    }
    public Void visit(ForNode node){
    	visitExpr(node.cond());
    	visitStmt(node.body());
    	visitExpr(node.cond());
    	visitStmt(node.body());
    	return null;
    }
    public Void visit(BreakNode node){
    	return null;
    	
    }
    public Void visit(ContinueNode node){
    	return null;
    }
    public Void visit(GotoNode node){
    	
    	return null;
    }
    public Void visit(LabelNode node){
    	visitStmt(node.stmt());
    	return null;
    }
    public Void visit(ReturnNode node){
    	visitExpr(node.expr());
    	return null;
    }
    
    /*
     * 下面是表达式的接口
     */
    public Void visit(CondExprNode n) {
        visitExpr(n.cond());
        visitExpr(n.leftExpr());
        if (n.rightExpr() != null) {
            visitExpr(n.rightExpr());
        }
        return null;
    }

  

    public Void visit(AssignNode n) {
        visitExpr(n.lhs());
        visitExpr(n.rhs());
        return null;
    }

    public Void visit(OpAssignNode n) {
        visitExpr(n.lhs());
        visitExpr(n.rhs());
        return null;
    }

    public Void visit(BinaryOpNode n) {
        visitExpr(n.left());
        visitExpr(n.right());
        return null;
    }

    public Void visit(UnaryOpNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(PrefixOpNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(SuffixOpNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(FunCallNode node) {
        visitExpr(node.expr());
        visitExprs(node.args());
        return null;
    }

    public Void visit(ArefNode node) {
        visitExpr(node.expr());
        visitExpr(node.index());
        return null;
    }

    public Void visit(MemberNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(PtrMemberNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(DereferenceNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(AddressNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(CastNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(SizeofExprNode node) {
        visitExpr(node.expr());
        return null;
    }

    public Void visit(SizeofTypeNode node) {
        return null;
    }

    public Void visit(VariableNode node) {
        return null;
    }

    public Void visit(IntegerLiteralNode node) {
        return null;
    }

    public Void visit(StringLiteralNode node) {
        return null;
    }
    
}
