package compiler;
import java.util.*;
import entity.*;
import ast.*;
import type.*;
import utils.*;

public class TypeResolver extends Visitor
						implements DeclarationVisitor<Void>,EntityVisitor<Void>{
	TypeTable typeTable;
	ErrorHandler errorHandler;
	public TypeResolver(TypeTable typeTable,ErrorHandler errorHandler) {
		this.typeTable=typeTable;
		this.errorHandler=errorHandler;
	}
	/*
	 * 首先将定义的类型导入到表中,但不处理定义的类型中成员的typeRef.
	 * 如果出现成员中类型有定义的外部类型,则会陷入循环.
	 * 然后遍历定义的类型中成员的和定义的实体类中的typeRef.
	 */
	public void resolve(AST  ast){
		defineTypes(ast.types());
		for(TypeDefinition t:ast.types()){
			t.accept(this);
		}
		for(Entity ent:ast.entities()){
			ent.accept(this);
		}
	}
	public void defineTypes(List<TypeDefinition> defTypes){
		for(TypeDefinition defType:defTypes){
			if(typeTable.isDefined(defType.typeRef())){
				error(defType,"duplicated type definition: " + defType.typeRef());
			}
			typeTable.put(defType.typeRef(),defType.definingType());
		}
	}
	/*
	 * 给一个类型节点绑定上它的类型名称所对应的定义的类型.
	 */
	public void bindType(TypeNode node){
		//严谨
		if(node.isResolved())return;
		node.setType(typeTable.get(node.typeRef()));
	}
	
	/*
	 * 实现一系列visit的具体功能.
	 */
	/*
	 * 类型定义包含了两个typeNode
	 */
	public Void visit(TypedefNode typedef){
		bindType(typedef.typeNode());
        bindType(typedef.realTypeNode());
		return null;
	}
	 public Void visit(StructNode struct) {
	        resolveCompositeType(struct);
	        return null;
	    }

	    public Void visit(UnionNode union) {
	        resolveCompositeType(union);
	        return null;
	    }
	public Void resolveCompositeType(CompositeTypeDefinition node){
		for(Slot mem:node.members()){
			bindType(mem.typeNode());
		}
		return null;
	}
	/*
	 * 消解实体类
	 */
	public Void visit(DefinedVariable var) {
        bindType(var.typeNode());
        if (var.hasInitializer()) {
            visitExpr(var.initializer());
        }
        return null;
    }

    public Void visit(UndefinedVariable var) {
        bindType(var.typeNode());
        return null;
    }
   

    public Void visit(Constant c) {
        bindType(c.typeNode());
        visitExpr(c.value());
        return null;
    }

      public Void visit(DefinedFunction func) {
        resolveFunctionHeader(func);
        visitStmt(func.body());
        return null;
    }

    public Void visit(UndefinedFunction func) {
        resolveFunctionHeader(func);
        return null;
    }

   /*
    * 消解一个函数头,主要是绑定函数返回值的类型,和消解形参部分.
    */
    private void resolveFunctionHeader(Function func) {
        bindType(func.typeNode());
        for (Parameter param : func.parameters()) {
            // arrays must be converted to pointers in a function parameter.
            Type t = typeTable.getParamType(param.typeNode().typeRef());
            param.typeNode().setType(t);
        }
    }
	/*
	 * 表达式中含有的TypeRef要处理.语句中不直接含有类型定义,因此不会出现TypeRef,强制转换也出现在表达式中.
	 */
    public Void visit(BlockNode node) {
        for (DefinedVariable var : node.variables()) {
            var.accept(this);
        }
        visitStmts(node.stmts());
        return null;
    }

    public Void visit(CastNode node) {
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(SizeofExprNode node) {
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(SizeofTypeNode node) {
        bindType(node.operandTypeNode());
        bindType(node.typeNode());
        super.visit(node);
        return null;
    }

    public Void visit(IntegerLiteralNode node) {
        bindType(node.typeNode());
        return null;
    }

    public Void visit(StringLiteralNode node) {
        bindType(node.typeNode());
        return null;
    }
	
	/*
	 * 错误处理
	 */
	private void error(Node node, String msg) {
        errorHandler.error(node.location(), msg);
    }
	
}
