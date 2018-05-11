package entity;

import java.util.*;

import exception.*;

/*
 * 每个作用域只有一个父作用域,用有序哈希map可以保证按照代码的输出顺序输出信息.
 */
public class LocalScope extends Scope{
	private Scope parent;
	private Map<String, DefinedVariable>variables;
	public LocalScope(Scope parent){
		this.parent=parent;
		parent.addChild(this);//父作用域添加此作用域,构造未完成就添加,在多线程里是不允许的
		variables=new LinkedHashMap<String,DefinedVariable>();
	}
	public boolean isToplevel() {
        return false;
    }

    public ToplevelScope toplevel() {
        return parent.toplevel();
    } 
    public Scope parent() {
        return this.parent;
    }

    public List<LocalScope> children() {
        return children;
    }
    /*
     * 查看这个作用域内有没有定义此名称的变量
     */
    public boolean hasDefinedVariable(String name){
    	return variables.containsKey(name);
    }
    /*
     * 查询一个变量或者函数,如果没有则向上查看
     */
    public Entity get(String name) throws SemanticException {
        DefinedVariable var = variables.get(name);
        if (var != null) {
            return var;
        }
        else {
            return parent.get(name);
        }
    }
    /*
     * 给这个作用域定义一个变量
     */
    public void defineVariable(DefinedVariable var) {
        if (variables.containsKey(var.name())) {
            throw new Error("duplicated variable: " + var.name());
        }
        variables.put(var.name(), var);
    }
    /*
     * 返回此作用域的所有局部变量,不包括子作用域
     */
    public List<DefinedVariable>localVariables(){
    	List<DefinedVariable>result=new ArrayList<DefinedVariable>();
    	for(DefinedVariable var:variables.values()){
    		if(!var.isStatic()){
    			result.add(var);
    		}
    	}
    	return result;
    }
    /*
     * 返回此作用域的所有静态变量,包括子作用域的静态变量
     */
    public List<DefinedVariable> staticLocalVariables() {
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for (LocalScope s : allLocalScopes()) {
            for (DefinedVariable var : s.variables.values()) {
                if (var.isStatic()) {
                    result.add(var);
                }
            }
        }
        return result;
    }
    /*
     * 返回这个作用域和子作用域的所有变量
     */
    public List<DefinedVariable> allLocalVariables() {
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for (LocalScope s : allLocalScopes()) {
            result.addAll(s.localVariables());
        }
        return result;
    }
    public List<LocalScope> allLocalScopes() {
        List<LocalScope> result = new ArrayList<LocalScope>();
        result.add(this);
        for(LocalScope child:children())
        result.addAll(child.allLocalScopes());
        return result;
    }
    //在当前的作用域里添加一个临时变量
    public DefinedVariable allocateTmp(type.Type t) {
        DefinedVariable var = DefinedVariable.tmp(t);
        defineVariable(var);
        return var;
    }
}
