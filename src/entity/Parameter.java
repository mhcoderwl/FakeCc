package entity;

import type.Type;
import asm.MemoryReference;
import ast.*;

/*
 * 一个形参实体类,形参不能有默认值
 */
public class Parameter extends DefinedVariable{
	public Parameter(TypeNode type, String name){
		super(false, type, name, null);
	}
	
	
}
