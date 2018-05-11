package ast;

import type.*;
public class TypedefNode extends TypeDefinition{
	TypeNode realType;//别名的真实类型
	public TypedefNode(Location location,TypeRef real,String name) {
		super(location, new UserTypeRef(name),name);
		this.realType=new TypeNode(real);// 保存定义的真实类型
	}
	 public boolean isUserType() {
	        return true;
	    }

	    public TypeNode realTypeNode() {
	        return realType;
	    }
	    public Type realType() {
	        return realType.type();
	    }

	    public TypeRef realTypeRef() {
	        return realType.typeRef();
	    }

	    // 返回我们定义的类型,是一个用户定义类型
	    public Type definingType() {
	        return new UserType(name(), realTypeNode(), location());
	    }


	    protected void printTree(Dumper d) {
	        d.printMember("name", name);
	        d.printMember("typeNode", typeNode);
	    }
	    public <T>T accept(DeclarationVisitor<T> visitor){
	    	return visitor.visit(this);
	    }

}
