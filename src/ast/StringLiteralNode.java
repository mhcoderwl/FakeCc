package ast;
import entity.*;
import type.*;
public class StringLiteralNode extends LiteralNode{
	 private String value;
	private ConstantEntry entry;
	 public StringLiteralNode(Location loc, TypeRef ref, String value) {
	        super(loc, ref);
	        this.value = value;
	  }

	 public String value() {
	        return value;
	    }
	 public void setEntry(ConstantEntry entry){
		 this.entry=entry;
	 }
	 public ConstantEntry entry(){
		 return entry;
	 }
	 public void printTree(Dumper d){
		 d.printMember("value: ", value);
	 }
	 public<S,E> E  accept(ASTVisitor<S, E> visitor){
	    	return visitor.visit(this);
	    }
}
