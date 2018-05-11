package asm;
import sys.RegisterClass;
public class Register extends Operand{
	public Register(RegisterClass reg,asm.Type type){
		
	}
    public void collectStatistics(Statistics stats) {
        stats.registerUsed(this);
    }

     public String toSource(SymbolTable syms){
    	return null;
    }
     public String dump(){
    	return null;
    }
}
