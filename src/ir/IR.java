package ir;
import java.util.*;
import ast.*;
import type.*;
import asm.*;
import entity.*;
import java.io.*;
/*
 * 中间代码的根节点,需要有一个常量表.
 */
public class IR {
	Location source;
	//对Declaration的解包
    List<DefinedVariable> defvars;
    List<DefinedFunction> defuns;
    List<UndefinedFunction> funcdecls;
    ToplevelScope scope;	//作用域
    ConstantTable constantTable;
    List<DefinedVariable> gvars;   //缓存定义变量
    List<DefinedVariable> comms;  //缓存未定义变量
    public IR(Location source,
            List<DefinedVariable> defvars,
            List<DefinedFunction> defuns,
            List<UndefinedFunction> funcdecls,
            ToplevelScope scope,
            ConstantTable constantTable) {
        super();
        this.source = source;
        this.defvars = defvars;
        this.defuns = defuns;
        this.funcdecls = funcdecls;
        this.scope = scope;
        this.constantTable = constantTable;
    }
    public String fileName() {
        return source.sourceName();
    }

    public Location location() {
        return source;
    }

    public List<DefinedVariable> definedVariables() {
        return defvars;
    }

    public boolean isFunctionDefined() {
        return !defuns.isEmpty();
    }

    public List<DefinedFunction> definedFunctions() {
        return defuns;
    }
    public boolean hasFunctionDefined(){
    	return !defuns.isEmpty();
    }
    public ToplevelScope scope() {
        return scope;
    }

    public List<Function> allFunctions() {
        List<Function> result = new ArrayList<Function>();
        result.addAll(defuns);
        result.addAll(funcdecls);
        return result;
    }
    //所有的全局变量
    public List<Variable> allGlobalVariables() {
        return scope.allGlobalVariables();
    }

    public boolean hasGlobalVariableDefined() {
        return ! definedGlobalVariables().isEmpty();
    }

    //返回被初始化过的全局变量
    public List<DefinedVariable> definedGlobalVariables() {
        if (gvars == null) {
            initVariables();
        }
        return gvars;
    }

    public boolean hasCommonSymbolDefined() {
        return ! definedCommonSymbols().isEmpty();
    }
    public boolean hasStringLiteralDefined(){
    	return !constantTable.isEmpty();
    }
    //返回未初始化的全局变量
    public List<DefinedVariable> definedCommonSymbols() {
        if (comms == null) {
            initVariables();
        }
        return comms;
    }
    public ConstantTable constantTable() {
        return constantTable;
    }
    //将变量分成定义和未定义缓存起来
    private void initVariables() {

        gvars = new ArrayList<DefinedVariable>();
        comms = new ArrayList<DefinedVariable>();
        for (DefinedVariable var : scope.allDefinedGlobalVariables()) {
            (var.hasInitializer() ? gvars : comms).add(var);
        }
    }
    //默认打印到标准输出
    public void dump() {
        dump(System.out);
    }
    public void dump(PrintStream s) {
        Dumper d = new Dumper(s);
        d.printClass(this, source);
        d.printVars("variables", defvars);
        d.printFuncs("functions", defuns);
    }
}
