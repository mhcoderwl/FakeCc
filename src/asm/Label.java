package asm;

//一条标签有一个symbol
public class Label extends Assembly{
	Symbol symbol;
	public Label() {
        this(new UnnamedSymbol());
    }

    public Label(Symbol sym) {
        this.symbol = sym;
    }
    
    public Symbol symbol() {
        return symbol;
    }
    public void setSymbol(Symbol s){
    	symbol=s;
    }
    public boolean isLabel() {
        return true;
    }

    public String toSource(SymbolTable table) {
        return symbol.toSource(table) + ":";
    }

    public String dump() {
        return "(Label " + symbol.dump() + ")";
    }

}
