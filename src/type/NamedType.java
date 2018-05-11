package type;

import ast.*;

//自命名的类型需要有location这个成员记录位置信息.
public abstract class NamedType extends Type{
	protected String name;
	protected Location location;
	public NamedType(String name,Location loc){
		this.name=name;
		this.location=loc;
	}
	 public String name() {
	        return name;
	    }

	    public Location location() {
	        return location;
	    }
}
