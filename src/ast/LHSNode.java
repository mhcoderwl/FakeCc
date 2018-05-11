package ast;
import type.*;
public abstract class LHSNode extends ExprNode{
	protected Type type, origType;//这里两个类型变量,一个存放当前类型,一个存放组合类型的原始类型.
	 public Type type() {
	        return type != null ? type : origType();
	    }

	    public void setType(Type t) {
	        this.type = t;
	    }


	    public long allocSize() { return origType().allocSize(); }
	    abstract protected Type origType();
	    public boolean isLvalue() { return true; }
	    public boolean isAssignable() { return isLoadable(); }

	    public boolean isLoadable() {
	        Type t = origType();
	        return !t.isArray() && !t.isFunction();
	    }
	
	
	
	
}
