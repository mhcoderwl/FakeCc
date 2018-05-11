package type;
import exception.*;
//类型抽象出的最底层的基类
public abstract class Type {
	static final public long sizeUnknown = -1;
	public abstract long size();
	//分配的字节大小,数组和结构体有用
	public long allocSize() { return size(); }
	public long alignment() { return allocSize(); }
	public abstract boolean isSameType(Type other);
	public boolean isVoid() { return false; }
    public boolean isInt() { return false; }
    public boolean isInteger() { return false; }
    public boolean isScalar(){return false;}
    //是否有符号
    public boolean isSigned(){ throw new Error("#isSigned for non-integer type"); }
    public boolean isPointer() { return false; }
    public boolean isArray() { return false; }
    public boolean isCompositeType() { return false; }
    public boolean isStruct() { return false; }
    public boolean isUnion() { return false; }
    public boolean isUserType() { return false; }
    public boolean isFunction() { return false; }
    public boolean isIncompleteArray(){return false;}
    public boolean isAllocatedArray(){return false;}
    //方法接口
    //是否可调用
    public boolean isCallable(){
    	return false;
    }
    //是否兼容,是否能类型转换
    abstract public boolean isCompatible(Type other);
    abstract public boolean isCastableTo(Type target);
    //指针和数组类型返回的是其基础类型,而不是整体的类型.
    public Type baseType(){
    	throw new SemanticError("no basetype");
    }
    
}
