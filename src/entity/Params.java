package entity;

import java.util.* ;
import type.*;
import ast.*;

/*
 * 函数定义的形参列表
 */
public class Params extends ParamSlots<Parameter>
									implements ast.Dumpable{
	public Params(Location loc,List<Parameter> params) {
		super(loc, params,false);
	}
	public List<Parameter> parameters(){
		return this.paramDescriptors;
	}
	public ParamTypeRefs paramsTypeRef() {
        List<TypeRef> typerefs = new ArrayList<TypeRef>();
        for (Parameter param : paramDescriptors) {
            typerefs.add(param.typeNode().typeRef());
        }
        return new ParamTypeRefs(location, typerefs, vararg);
    }
	public void dump(Dumper d) {
        d.printNodeList("parameters", parameters());
    }
}
