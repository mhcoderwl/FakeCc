package entity;

import java.util.*;
import ast.*;

//参数列表的基类,可以成为参数类型列表,也可以成为形参列表,前者是类型,是抽象概念,后者是一个实体.
public abstract class ParamSlots<T> {
	protected Location location;
	protected List<T> paramDescriptors;
	protected boolean vararg=false;
	public ParamSlots(Location loc,List<T> params) {
		this(loc, params, false);
	}
	public ParamSlots(Location loc,List<T> params,boolean vararg) {
		paramDescriptors=params;
		this.vararg=vararg;
		this.location=loc;
	}
	 public int argc() {
	        if (vararg) {
	            throw new Error("must not happen: Param#argc for vararg");
	        }
	        return paramDescriptors.size();
	    }

	    public int minArgc() {
	        return paramDescriptors.size();
	    }

	    public void acceptVarargs() {
	        this.vararg = true;
	    }

	    public boolean isVararg() {
	        return vararg;
	    }

	    public Location location() {
	        return location;
	    }
}
