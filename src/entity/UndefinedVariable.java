package entity;

import ast.*;
public class UndefinedVariable extends Variable{
	 public UndefinedVariable(TypeNode t, String name) {
	        super(false, t, name);
	    }

	    public boolean isDefined() { return false; }
	    public boolean isStatic() { return false; }
	    public boolean isInitialized() { return false; }

	    public void printTree(Dumper d) {
	        d.printMember("name", name);
	        d.printMember("isStatic", isStatic());
	        d.printMember("typeNode", typeNode);
	    }

	    public <T> T accept(EntityVisitor<T> visitor) {
	        return visitor.visit(this);
	    }
}
