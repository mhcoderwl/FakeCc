package ast;

public interface DeclarationVisitor<T> {
	public T visit(TypedefNode typedef);
	public T visit(StructNode node);
	public T visit(UnionNode node);
}
