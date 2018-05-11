package type;

import ast.Location;

//ref类型的存在是在解析的时候先生成ref类型然后在语义分析中转成相应的type.
public class TypeRef {
	protected Location location;
	 public TypeRef(Location loc) {
	        this.location = loc;
	    }

	    public Location location() {
	        return location;
	    }
	    //在查类型表时需要用
	    public int hashCode() {
	        return toString().hashCode();
	    }
}
