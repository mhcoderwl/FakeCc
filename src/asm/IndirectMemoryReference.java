package asm;

public class IndirectMemoryReference extends MemoryReference{
	Literal offset;
	Register base;
	boolean fixed;//是否是固定地址
	public IndirectMemoryReference(long offset, Register base) {
        this(new IntegerLiteral(offset), base, true);
    }

    public IndirectMemoryReference(Symbol offset, Register base) {
        this(offset, base, true);
    }
    private IndirectMemoryReference(
            Literal offset, Register base, boolean fixed) {
        this.offset = offset;
        this.base = base;
        this.fixed = fixed;
    }
    //设置一个偏移量
    public void fixOffset(long diff) {
        if (fixed) {
            throw new Error("must not happen: fixed = true");
        }
        long curr = ((IntegerLiteral)offset).value;
        this.offset = new IntegerLiteral(curr + diff);
        this.fixed = true;
    }
    public 
    public String dump() {
        return "(IndirectMemoryReference "
                + (fixed ? "" : "*")
                + offset.dump() + " " + base.dump() + ")";
    }
}
