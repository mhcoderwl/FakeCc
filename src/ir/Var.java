package ir;
import asm.*;
import ast.*;
import entity.*;
/*
 * 变量
 */
public class Var extends Expr{
	public Entity entity;
	
    public Var(Type type, Entity entity) {
        super(type);
        this.entity = entity;
    }

    public boolean isVar() { return true; }

    public Type type() {
        if (super.type() == null) {
            throw new Error("Var is too big to load by 1 insn");
        }
        return super.type();
    }

    public String name() { return entity.name(); }
    public Entity entity() { return entity; }
    //变量的asm地址
    public Operand address() {
        return entity.address();
    }
    //待定
    //public MemoryReference memref() {
   //     return entity.memref();
  //  }

    //返回变量的地址节点,用来寻址.
    public Addr addressNode(Type type) {
        return new Addr(type, entity);
    }
    
    //在asm生成中用来判断是否是函数指针
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
