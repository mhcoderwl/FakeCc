package ast;
import type.*;
import exception.*;


abstract public class ExprNode extends Node{
	
	public ExprNode(){
		super();
	}
	//返回表达式的类型
	abstract public Type type();
	//表达式节点共同拥有的接口.
	protected Type origType() { return type(); }//有复合类型的情况会返回原始类型
	 public long allocSize() { return type().allocSize(); }

	    public boolean isConstant() { return false; }
	    public boolean isParameter() { return false; }
	   
	    public boolean isLvalue() { return false; }
	    public boolean isAssignable() { return false; }
	    public boolean isLoadable() { return false; }
	    public boolean isInteger(){return false;}
	    public boolean isCallable() {
	        try {
	            return type().isCallable();
	        }
	        catch (SemanticError err) {
	            return false;
	        }
	    }

	    // #@@range/isPointer{
	    public boolean isPointer() {
	        try {
	            return type().isPointer();
	        }
	        catch (SemanticError err) {
	            return false;
	        }
	    }
	    abstract public <S,E> E accept(ASTVisitor<S,E> visitor);
}
