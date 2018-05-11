package ast;
import type.*;
//一个字面量表达式,整数或者字符串,都包含一个位置信息和一个类型节点,类型比如int,long,uint,这些.
public abstract class LiteralNode extends ExprNode{
	protected Location location;
	protected TypeNode typeNode;
	 public LiteralNode(Location loc, TypeRef ref) {
	        super();
	        this.location = loc;
	        this.typeNode = new TypeNode(ref);
	    }

	    public Location location() {
	        return location;
	    }

	    public Type type() {
	        return typeNode.type();
	    }

	    public TypeNode typeNode() {
	        return typeNode;
	    }

	    public boolean isConstant() {
	        return true;
	    }
	    
}
