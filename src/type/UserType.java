package type;

import ast.*;
//基本把接口全转成realType的接口调用
	public class UserType extends NamedType {
	    protected TypeNode real;

	    public UserType(String name, TypeNode real, Location loc) {
	        super(name, loc);
	        this.real = real;
	    }

	    public Type realType() {
	        return real.type();
	    }

	    public String toString() {
	        return name;
	    }

	    public long size() { return realType().size(); }
	    public long allocSize() { return realType().allocSize(); }
	    public long alignment() { return realType().alignment(); }

	    public boolean isVoid() { return realType().isVoid(); }
	    public boolean isInt() { return realType().isInt(); }
	    public boolean isInteger() { return realType().isInteger(); }
	    public boolean isSigned() { return realType().isSigned(); }
	    public boolean isPointer() { return realType().isPointer(); }
	    public boolean isArray() { return realType().isArray(); }
	    public boolean isAllocatedArray() { return realType().isAllocatedArray(); }
	    public boolean isCompositeType() { return realType().isCompositeType(); }
	    public boolean isStruct() { return realType().isStruct(); }
	    public boolean isUnion() { return realType().isUnion(); }
	    public boolean isUserType() { return true; }
	    public boolean isFunction() { return realType().isFunction(); }

	    public boolean isCallable() { return realType().isCallable(); }
	    public boolean isScalar() { return realType().isScalar(); }

	    public Type baseType() { return realType().baseType(); }

	    public boolean isSameType(Type other) {
	        return realType().isSameType(other);
	    }

	    public boolean isCompatible(Type other) {
	        return realType().isCompatible(other);
	    }

	    public boolean isCastableTo(Type other) {
	        return realType().isCastableTo(other);
	    }
}
