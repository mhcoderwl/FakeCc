package asm;

public abstract class Assembly {
	abstract public String toSource(SymbolTable table);
    abstract public String dump();
    public void collectStatistics(Statistics stats) {
        // does nothing by default.
    }
    public boolean isLabel() {
        return false;
    }
}
