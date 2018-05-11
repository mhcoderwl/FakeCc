package ast;
import type.*;
//存放类型的节点,分为存放type和存放typeRef声明
public class TypeNode extends Node{
	TypeRef typeRef;
	Type type;
	public TypeNode(TypeRef ref) {
        super();
        this.typeRef = ref;
    }

    public TypeNode(Type type) {
        super();
        this.type = type;
    }
    
    public Type type() {
        if (type == null) {
            throw new Error("TypeNode not resolved: " + typeRef);
        }
        return type;
    }
    public TypeRef typeRef() {
        return typeRef;
    }
    //是否被关联过
    public boolean isResolved() {
        return (type != null);
    }

    public void setType(Type t) {
        if (type != null) {
            throw new Error("Type already exist");
        }
        type = t;
    }
    //如果没有typeref返回空
    public Location location() {
        return typeRef == null ? null : typeRef.location();
    }
    public void printTree(Dumper d){
    	
    }
}
