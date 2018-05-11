package asm;
//整数字面量
public class IntegerLiteral implements Literal{
	protected long value;
	public IntegerLiteral(long n) {
        this.value = n;
    }
	 public long value() {
	        return this.value;
	    }
    public boolean equals(Object other) {
        return (other instanceof IntegerLiteral)
                && equals((IntegerLiteral)other);
    }

    public boolean equals(IntegerLiteral other) {
        return other.value == this.value;
    }
    public String toSource() {
        return new Long(value).toString();
    }

    public String toSource(SymbolTable table) {
        return toSource();
    }
}
