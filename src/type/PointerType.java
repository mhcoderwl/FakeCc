package type;


public class PointerType extends Type{
	long size;//
	Type baseType;
	public PointerType(long pointerSize,Type type){
		baseType=type;
		size=pointerSize;
	}
	public Type baseType(){
		return baseType;
	}
	public boolean isPointer(){
		return true;
	}
	
    public boolean isScalar() { return true; }
    public boolean isSigned() { return false; }
    public boolean isCallable() { return baseType.isFunction(); }

    public long size() {
        return size;
    }
    public String toString() {
        return baseType.toString() + "*";
    }
    //相同类型必须比较他们的基本类型是否相同
    public boolean isSameType(Type obj){
    	if(!obj.isPointer())return false;
    	else 
    		return obj.baseType().isSameType(baseType());
    }
    //能否和目标类型转换.只能和指针类型还有int类型转换
    public boolean isCastableTo(Type other) {
        return other.isPointer() || other.isInteger();
    }
    //兼容性,void*和char*可以兼容.
    public boolean isCompatible(Type other) {
        if (!other.isPointer()) return false;
        if (baseType.isVoid()) {
            return true;
        }
        if (other.baseType().isVoid()) {
            return true;
        }
        return baseType.isCompatible(other.baseType());
    }
}
