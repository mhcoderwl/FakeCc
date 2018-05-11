package entity;
import java.util.*;

import exception.*;
/*
 * 全局范围存有函数和变量,基类为实体类
 */
public class ToplevelScope extends Scope{
	Map<String, Entity>entities;
	public ToplevelScope() {
        super();
        entities = new LinkedHashMap<String, Entity>();
        //staticVariables = null;
    }
	public boolean isToplevel() {
        return true;
    }
	public Scope parent() {
        return null;
    }
	
	public ToplevelScope toplevel(){
		return this;
	}
	/*
	 * 声明一个全局变量或者一个函数,声明可以重复声明.
	 */
    public void declareEntity(Entity entity) throws SemanticException {
        Entity e = entities.get(entity.name());
        if (e != null) {
            throw new SemanticException("duplicated declaration: " +
                    entity.name() + ": " +
                    e.location() + " and " + entity.location());
        }
        entities.put(entity.name(), entity);
    }


    /*
     * 定义一个全局变量或着一个函数.定义不能重复定义.
     */
    public void defineEntity(Entity entity) throws SemanticException {
        Entity e = entities.get(entity.name());
        if (e != null && e.isDefined()) {
            throw new SemanticException("duplicated definition: " +
                    entity.name() + ": " +
                    e.location() + " and " + entity.location());
        }
        entities.put(entity.name(), entity);
    }
	/*
	 *获得指定名称的实体.
	 */
	public  Entity get(String name)throws SemanticException{
		Entity entity = entities.get(name);
        if (entity == null) {
            throw new SemanticException("unresolved reference: " + name);
        }
        return entity;
	}
	/*
	 * 所有的全局变量,包括所有静态变量,有定义的,未定义的.
	 */
	public List<Variable> allGlobalVariables() {
        List<Variable> result = new ArrayList<Variable>();
        for (Entity ent : entities.values()) {
            if (ent instanceof Variable) {
                result.add((Variable)ent);
            }
        }
        result.addAll(staticLocalVariables());
        return result;
    }
	/*
	 *所有的有定义的全局变量
	 */
	public List<DefinedVariable> allDefinedGlobalVariables() {
        List<DefinedVariable> result = new ArrayList<DefinedVariable>();
        for (Entity ent : entities.values()) {
            if (ent instanceof DefinedVariable) {
                result.add((DefinedVariable)ent);
            }
        }
        result.addAll(staticLocalVariables());
        return result;
    }
	/*
	 * 所有的静态变量,包括局部静态变量,静态变量必须有定义.
	 */
	public List<DefinedVariable> staticLocalVariables() {
		List<DefinedVariable> staticLocalVariables=null;
        if (staticLocalVariables == null) {
        	staticLocalVariables = new ArrayList<DefinedVariable>();
            for (LocalScope s : children) {
            	staticLocalVariables.addAll(s.staticLocalVariables());
            }
            //多个静态变量重名的处理
            Map<String, Integer> seqTable = new HashMap<String, Integer>();
            for (DefinedVariable var : staticLocalVariables) {
                Integer seq = seqTable.get(var.name());
                if (seq == null) {
                    var.setSequence(0);
                    seqTable.put(var.name(), 1);
                }
                else {
                    var.setSequence(seq);
                    seqTable.put(var.name(), seq + 1);
                }
            }
        }
        return staticLocalVariables;
    }

}
