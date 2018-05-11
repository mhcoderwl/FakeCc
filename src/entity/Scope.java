package entity;

import java.util.*;

import exception.*;


public abstract class Scope {
	protected List<LocalScope> children;
	protected Scope(){
		children=new ArrayList<LocalScope>();
	}
	public abstract ToplevelScope toplevel();
	public abstract Entity get(String name)throws SemanticException;
	protected void addChild(LocalScope s) {
        children.add(s);
    }
}
