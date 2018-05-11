package asm;

public class Instruction extends Assembly{
	private final String operation;
	private final String suffix;//指令后置字符
	private final Operand[] ops;//操作数集合
	boolean needRelocation;
	public Instruction(String operation) {
	        this(operation, "", new Operand[0], false);
	    }

	public Instruction(String operation, String suffix, Operand a1) {
	        this(operation, suffix, new Operand[] { a1 }, false);
	    }

	    public Instruction(String operation, String suffix,
	                       Operand a1, Operand a2) {
	        this(operation, suffix, new Operand[] { a1, a2 }, false);
	    }

	    public Instruction(String operation, String suffix,
	                       Operand a1, Operand a2, boolean reloc) {
	        this(operation, suffix, new Operand[] { a1, a2 }, reloc);
	    }

	    public Instruction(String operation, String suffix, Operand[] operands, boolean reloc) {
	        this.operation = operation;
	        this.suffix = suffix;
	        this.ops = operands;
	        this.needRelocation = reloc;
	    }
	    public String operation() {
	        return this.operation;
	    }

	    public boolean isJumpInstruction() {
	        return operation.equals("jmp")
	                || operation.equals("jz")
	                || operation.equals("jne")
	                || operation.equals("je")
	                || operation.equals("jne");
	    }
	@Override
	public String toSource(SymbolTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

}
