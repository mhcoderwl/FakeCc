package ast;

import entity.*;
import type.*;
/*
 * 变量节点也可能存储的是函数名,比如说func(a,b),那么func作为identifier被扫描器记录为一个variableNode.
 */
public class VariableNode extends LHSNode{
	private Entity entity;
	private String name;
	private Location location;
	public Location location(){
		return location;
	}
	public VariableNode(DefinedVariable var){
		this.entity=var;
	}
	public VariableNode(Location loc,String name){
		this.name=name;
		this.location=loc;
	}
	
	 public String name() {
	        return name;
	    }
	 public Entity entity() {
	        if (entity == null) {
	            throw new Error("VariableNode.entity == null");
	        }
	        return entity;
	    }

	    public boolean isResolved() {
	        return (entity != null);
	    }
	    public void setEntity(Entity ent) {
	    	entity = ent;
	    }
	    protected Type origType() {
	        return entity().type();
	    }
	    public TypeNode typeNode() {
	        return entity().typeNode();
	    }
	public void printTree(Dumper d){
		d.printMember("name: ", name);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
