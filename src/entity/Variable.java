package entity;



import ast.*;

public abstract class Variable extends Entity{
	public Variable(boolean isStatic, TypeNode type, String name){
		super(isStatic, type, name);
	}


}
