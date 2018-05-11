package asm;

public class ImmediateValue extends Operand{
	Literal expr;//立即数是一个$加上一个字面量
	public ImmediateValue(long n) {
        this(new IntegerLiteral(n));
    }

    public ImmediateValue(Literal expr) {
        this.expr = expr;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ImmediateValue)) return false;
        ImmediateValue imm = (ImmediateValue)other;
        return expr.equals(imm.expr);
    }

    public Literal expr() {
        return this.expr;
    }

    public void collectStatistics(Statistics stats) {
        // does nothing
    }

    public String toSource(SymbolTable table) {
        return "$" + expr.toSource(table);
    }
    //用来打印汇编代码结构的时候的输出
    public String dump() {
        return "(ImmediateValue " + expr.dump() + ")";
    }
}
