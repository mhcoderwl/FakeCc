package asm;

//直接地址引用
public class DirectMemoryReference extends MemoryReference{
	 private Literal value;//引用的可能是一个立即数,也可能是一个symbol
	    public DirectMemoryReference(Literal val) {
	        this.value = val;
	    }
	    public Literal value() {
	        return this.value;
	    }
	    public String toString() {
	        return toSource(SymbolTable.dummy());
	    }

	    public String toSource(SymbolTable table) {
	        return this.value.toSource(table);
	    }
	    public String dump() {
	        return "(DirectMemoryReference " + value.dump() + ")";
	    }
}
