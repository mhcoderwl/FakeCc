package entity;
/*
 * 实体的遍历接口
 */
public interface EntityVisitor<T> {
	public T visit(DefinedVariable var);
    public T visit(UndefinedVariable var);
    public T visit(DefinedFunction func);
    public T visit(UndefinedFunction func);
    public T visit(Constant c);
	
}
