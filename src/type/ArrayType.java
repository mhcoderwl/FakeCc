package type;


import ast.ASTVisitor;
/*
 * 多维数组的a[][] 的basetype 是 a[]的type
 */              
public class ArrayType extends Type{
	private Type baseType;
	private long length;
	private long pointerSize;
	static final protected long undefined = -1;
	public ArrayType(Type baseType, long pointerSize) {
        this(baseType, undefined, pointerSize);
    }

    public ArrayType(Type baseType, long length, long pointerSize) {
        this.baseType = baseType;
        this.length = length;
        this.pointerSize = pointerSize;
    }

    public boolean isArray() { return true; }

    public boolean isAllocatedArray() {
        return length != undefined &&
            (!baseType.isArray() || ((ArrayType)baseType).isAllocatedArray());
    }
    //是否是非定长数组
    public boolean isIncompleteArray() {
        if (! baseType.isArray()) return false;
        return !((ArrayType)baseType).isAllocatedArray();
    }

    public Type baseType() {
        return baseType;
    }

    public long length() {
        return length;
    }

    // 作为一个指针的大小.
    public long size() {
        return pointerSize;
    }

    
    public long allocSize() {
        if (length == undefined) {
            return size();
        }
        else {
            return baseType.allocSize() * length;
        }
    }

    public long alignment() {
        return baseType.alignment();
    }
    //数组比较两个基本类型是否相同,并且长度是否相同.
    public boolean equals(Object other) {
        if (! (other instanceof ArrayType)) return false;
        ArrayType type = (ArrayType)other;
        return (baseType.equals(type.baseType) && length == type.length);
    }

    public boolean isSameType(Type other) {
        // length is not important
        if (!other.isPointer() && !other.isArray()) return false;
        return baseType.isSameType(other.baseType());
    }

    public boolean isCompatible(Type target) {
        if (!target.isPointer() && !target.isArray()) return false;
        if (target.baseType().isVoid()) {
            return true;
        }
        return baseType.isCompatible(target.baseType())
                && baseType.size() == target.baseType().size();
    }

    public boolean isCastableTo(Type target) {
        return target.isPointer() || target.isArray();
    }

    public String toString() {
        if (length < 0) {
            return baseType.toString() + "[]";
        }
        else {
            return baseType.toString() + "[" + length + "]";
        }
    }
    
}
