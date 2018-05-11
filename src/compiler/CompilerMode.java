package compiler;

import java.util.HashMap;

public enum CompilerMode {
	CheckSyntax ("--check-syntax"),
	PrintTokens ("--print-tokens"),
	PrintAST ("--print-ast"),
	PrintSemantic ("--print-semantic"),
	PrintIR ("--print-ir"),
	PrintAsm ("--print-asm"),
    Compile ("-S"),
    Assemble ("-c");
	static private HashMap<String, CompilerMode>map;//用一个map来存储编译模式和对应字符串的映射.
	static{
		map = new HashMap<String, CompilerMode>();
		map.put("--check-syntax", CheckSyntax);
		map.put("--print-tokens", PrintTokens);
		map.put("--print-ast", PrintAST);
		map.put("--print-semantic", PrintSemantic);
        map.put("--print-ir", PrintIR);
        map.put("--print-asm", PrintAsm);
        map.put("-S", Compile);
        map.put("-c", Assemble);
	}
	 static public CompilerMode getMode(String arg){
	    	return map.get(arg);
	    }
	static public boolean isMode(String arg){
		return map.containsKey(arg);
	}
	private final String option;
    CompilerMode(String option) {
        this.option = option;
    }
    public String toOption() {
        return option;
    }
}
