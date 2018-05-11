package ir;
import asm.*;
import ast.*;
import entity.*;
/*
 * 取地址节点.取地址的对象一定是个实体.
 */
public class Addr extends Expr{
	 Entity entity;

	    public Addr(Type type, Entity entity) {
	        super(type);
	        this.entity = entity;
	    }

	    public boolean isAddr() { return true; }

	    public Entity entity() { return entity; }
	    //待定功能
	    public Operand address() {
	        return entity.address();
	    }
	    //待定功能
	  //  public MemoryReference memref() {
	 //       return entity.memref();
	//    }
	    
	    public Entity getEntityForce() {
	        return entity;
	    }

	    public <S,E> E accept(IRVisitor<S,E> visitor) {
	        return visitor.visit(this);
	    }

	    public void printTree(Dumper d) {
	        d.printMember("entity", entity.name());
	    }
}
