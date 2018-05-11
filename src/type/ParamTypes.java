package type;

import entity.*;
import ast.*;
import java.util.*;
//参数的列表基类是参数槽,
public class ParamTypes extends ParamSlots<Type>{
	//是否是可变参数列表,所有的参数类型
	protected ParamTypes(Location loc, List<Type> paramDescs, boolean vararg) {
        super(loc, paramDescs, vararg);
    }
	public List<Type> types() {
        return paramDescriptors;
    }
	public boolean isSameType(ParamTypes obj){
		Iterator<Type> otherTypes = obj.types().iterator();
        for (Type t : paramDescriptors) {
            if (! t.isSameType(otherTypes.next())) {
                return false;
            }
        }
        return true;
	}
	public boolean isCompatible(ParamTypes obj){
		Iterator<Type> otherTypes = obj.types().iterator();
        for (Type t : paramDescriptors) {
            if (! t.isCompatible(otherTypes.next())) {
                return false;
            }
        }
        return true;
	}
}
