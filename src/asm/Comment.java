package asm;

import utils.TextUtils;

public class Comment extends Assembly{
	private String string;
	private int indentLevel;
	public Comment(String string) {
        this(string, 0);
    }

    public Comment(String string, int indentLevel) {
        this.string = string;
        this.indentLevel = indentLevel;
    }
    public String toSource(SymbolTable table) {
        return "\t" + indent() + "# " + string;
    }
    //控制打印的格式
    protected String indent() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < indentLevel; i++) {
            buf.append("  ");
        }
        return buf.toString();
    }
    public String dump() {
        return "(Comment " + TextUtils.dumpString(string) + ")";
    }
}
