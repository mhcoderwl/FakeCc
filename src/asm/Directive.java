package asm;

import utils.TextUtils;

//伪操作
public class Directive extends Assembly{
	private String content;
	public Directive(String content) {
        this.content = content;
    }
	 public String toSource(SymbolTable table) {
	        return this.content;
	    }

	    public String dump() {
	        return "(Directive " + TextUtils.dumpString(content.trim()) + ")";
	    }
}
