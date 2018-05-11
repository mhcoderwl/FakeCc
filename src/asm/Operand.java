package asm;

public abstract class Operand {
	 abstract public String toSource(SymbolTable table);
	    abstract public String dump();
}
