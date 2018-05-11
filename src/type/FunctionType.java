package type;
import java.util.*;
import entity.*;

//一个函数类型,有返回值类型,参数类型.
public class FunctionType extends Type{
	ParamTypes paramTypes;
	Type returnType;
	public FunctionType(Type returnType,ParamTypes paramTypes) {
		this.returnType=returnType;
		this.paramTypes=paramTypes;
		
		// TODO Auto-generated constructor stub
	}
	public boolean isFunction(){
		return true;
	}
	public boolean isCallable() { return true; }
	public Type returnType(){return this.returnType;}
	
	public boolean isSameType(Type obj){
		if(!obj.isFunction())return false;
		else{
			FunctionType t=(FunctionType)obj;
			return t.returnType().isSameType(returnType)
						&&t.paramTypes.isSameType(paramTypes);
		}
	}
	public boolean isCompatible(Type obj){
		if(!obj.isFunction())return false;
		else{
			FunctionType t=(FunctionType)obj;
			return t.returnType().isCompatible(returnType)
					&&t.paramTypes.isCompatible(paramTypes);
		}
	}
	//函数能否强制类型转换,只要是一个函数类型都能转换.
	public boolean isCastableTo(Type obj){
		return obj.isFunction();
		
	}
	/*
	 * paramtypes基类不是Type,返回一个参数类型的list.
	 */
	public List<Type> paramTypes(){return paramTypes.types();}
	
	//参数列表是否可变
    public boolean isVararg() {
        return paramTypes.isVararg();
    }
    //对于参数的个数是否符合要求,如果函数本身是可变参数列表,那么调用函数的实参个数比最小参数数量大.
    public boolean acceptsArgc(long numArgs) {
        if (paramTypes.isVararg()) {
            return (numArgs >= paramTypes.minArgc());
        }
        else {
            return (numArgs == paramTypes.argc());
        }
    }
    
    
    public long alignment() {
        throw new Error("FunctionType#alignment called");
    }

    public long size() {
        throw new Error("FunctionType#size called");
    }

    //按函数形式打印类型
    public String toString() {
        String sep = "";
        StringBuffer buf = new StringBuffer();
        buf.append(returnType.toString());
        buf.append("(");
        for (Type t : paramTypes.types()) {
            buf.append(sep);
            buf.append(t.toString());
            sep = ", ";
        }
        buf.append(")");
        return buf.toString();
    }
}
