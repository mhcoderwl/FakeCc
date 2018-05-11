package ast;

public abstract class StmtNode extends Node{
	Location location;
	protected StmtNode(Location location){
		this.location=location;
	}
	public Location location() {
        return location;
    }
	abstract public <S,E> S accept(ASTVisitor<S,E> visitor);
}
