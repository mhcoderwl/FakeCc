package ast;

import java.util.*;
import entity.*;

public class BlockNode extends StmtNode{
	List<StmtNode> stmts;
	List<DefinedVariable> defvars;
	LocalScope scope;
	public BlockNode(Location location,List<DefinedVariable> vars,List<StmtNode> stmts) {
		super(location);
		this.stmts=stmts;
		this.defvars=vars;
	}
	public List<DefinedVariable> variables() {
        return defvars;
    }
	
    public List<StmtNode> stmts() {
        return stmts;
    }
	//查询最后一条语句
	public StmtNode tailStmt() {
        if (stmts.isEmpty()) return null;
        return stmts.get(stmts.size() - 1);
    }
	/*
	 * 设置作用域
	 */
	public Scope setScope(LocalScope scope){
		return this.scope=scope;
	}
	public LocalScope scope(){
		return this.scope;
	}
	public void printTree(Dumper d){
		d.printNodeList("variables", defvars);
		d.printNodeList("stmts", stmts);
	}
	public<S,E> S  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
