package entity;

import java.util.*;
import asm.*;
import ast.*;
import type.*;
/*
 * 实体的抽象基类
 */
public abstract class Entity implements ast.Dumpable{
	protected TypeNode typeNode;
	protected String name;
	protected long nRefered;//实体被引用的次数
	protected boolean isStatic;
	protected MemoryReference memref;//每个实体类都有相应的内存引用
    protected Operand address;
	public Entity(boolean isStatic, TypeNode type, String name) {
        this.name = name;
        this.isStatic = isStatic;
        this.typeNode = type;
    }
	public boolean isStatic() {
        return isStatic;
    }
	public boolean isConstant(){
		return false;
	}
	public abstract boolean isDefined();
	public long refered(){
		return ++this.nRefered;
	}
	public void dump(Dumper d){
		d.printClass(this,location());
		printTree(d);
	}
	/*
	 * 用类型的位置代表当前位置
	 */
	public Location location() {
        return typeNode.location();
    }
	public String name() {
        return name;
    }
	public type.Type type() {
        return typeNode.type();
    }
	public ExprNode value(){
		throw new Error("no const entity ");
	}
	public void setMemref(MemoryReference mem) {
        this.memref = mem;
    }

    public MemoryReference memref() {
        checkAddress();
        return memref;
    }
    public void checkAddress() {
        if (memref == null && address == null) {
            throw new Error("address did not resolved: " + name);
        }
    }
	public TypeNode typeNode(){
		return this.typeNode;
	}
	public Operand address(){
		return address;
	}
	public void setAddress(Operand addr){
		address=addr;
	}
	/*
	 * 调用的是EntityVisitor接口.用来遍历实体类的接口
	 */
	abstract public <T> T accept(EntityVisitor<T> visitor);
	public abstract void printTree(Dumper d);
}
