package entity;

import asm.*;
public class ConstantEntry {
	String value;
	protected Symbol symbol;
	private ImmediateValue address;//常量的汇编地址是一个立即数
	public ConstantEntry(String value){
		this.value=value;
	}                                                                                                                                                                                                                                                                           
	public void setAddress(ImmediateValue imm) {
        this.address = imm;
    }

    public ImmediateValue address() {
        return this.address;
    }
    public Symbol symbol() {
        if (symbol == null) {
            throw new Error("must not happen: symbol == null");
        }
        return symbol;
    }
}
