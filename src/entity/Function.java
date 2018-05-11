package entity;

import type.*;
import ast.*;
import java.util.*;
public abstract class Function extends Entity{
	protected Params params;
	public Function(boolean isStatic, TypeNode t, String name,Params params) {
        super(isStatic,t,name);
        this.params=params;
    }
	
	public abstract  boolean isDefined();
	//public abstract  boolean isInitialized();
	
	public abstract  List<Parameter> parameters();
    
    /*
     * 返回类型就是函数的整体类型.
     */
    public Type returnType() {
        return ((FunctionType)type()).returnType();
    }
    public boolean isVoid() {
        return returnType().isVoid();
    }
}
