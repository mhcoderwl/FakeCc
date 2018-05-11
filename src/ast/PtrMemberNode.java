package ast;
import type.*;
//指针调用的成员
public class PtrMemberNode extends LHSNode{
	private ExprNode expr;
	private String member;
	public PtrMemberNode(ExprNode expr,String member){
		this.expr=expr;
		this.member=member;
	}
	public ExprNode expr(){
		return this.expr;
	}
	public String member(){
		return member;
	}
	protected Type origType() {
        return dereferedCompositeType().memberType(member);
    }
	public  long offset(){
		return dereferedCompositeType().memberOffset(member);
	}
	public Location location(){
		return expr.location();
	}
	//返回指针的指向类型.
	public Type dereferedType(){
		PointerType pt=(PointerType)expr.type();
		return pt.baseType();
	}
	public CompositeType dereferedCompositeType(){
		PointerType pt=(PointerType)expr.type();
		return (CompositeType)pt.baseType();
	}
	public void printTree(Dumper d){
		d.printMember("expr: ", expr);
		d.printMember("member", member);
	}
	public<S,E> E  accept(ASTVisitor<S, E> visitor){
    	return visitor.visit(this);
    }
}
