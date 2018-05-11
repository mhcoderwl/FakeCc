package type;

import java.util.*;
import ast.*;
import entity.*;
public class ParamTypeRefs extends ParamSlots<TypeRef>{
	 public ParamTypeRefs(List<TypeRef> paramDescs) {
	        super(null,paramDescs);
	    }

	    public ParamTypeRefs(Location loc, List<TypeRef> paramDescs, boolean vararg) {
	        super(loc, paramDescs, vararg);
	    }

	    public List<TypeRef> typerefs() {
	        return paramDescriptors;
	    }

	    public ParamTypes internTypes(TypeTable table) {
	        List<Type> types = new ArrayList<Type>();
	        for (TypeRef ref : paramDescriptors) {
	            types.add(table.getParamType(ref));
	        }
	        return new ParamTypes(location, types, vararg);
	    }

	    public boolean equals(Object other) {
	        return (other instanceof ParamTypeRefs)
	                && equals((ParamTypeRefs)other);
	    }

	    public boolean equals(ParamTypeRefs other) {
	        return vararg == other.vararg
	                && paramDescriptors.equals(other.paramDescriptors);
	    }
}
