package ast;

public interface ASTVisitor<S,E> {
	//语句的接口
	public S visit(BlockNode node);
    public S visit(ExprStmtNode node);
    public S visit(IfNode node);
    public S visit(SwitchNode node);
    public S visit(CaseNode node);
    public S visit(WhileNode node);
    public S visit(DoWhileNode node);
    public S visit(ForNode node);
    public S visit(BreakNode node);
    public S visit(ContinueNode node);
    public S visit(GotoNode node);
    public S visit(LabelNode node);
    public S visit(ReturnNode node);

    // 表达式的visit接口
    public E visit(AssignNode node);
    public E visit(OpAssignNode node);
    public E visit(CondExprNode node);
    public E visit(BinaryOpNode node);
    public E visit(UnaryOpNode node);
    public E visit(PrefixOpNode node);
    public E visit(SuffixOpNode node);
    public E visit(ArefNode node);
    public E visit(MemberNode node);
    public E visit(PtrMemberNode node);
    public E visit(FunCallNode node);
    public E visit(DereferenceNode node);
    public E visit(AddressNode node);
    public E visit(CastNode node);
    public E visit(SizeofExprNode node);
    public E visit(SizeofTypeNode node);
    public E visit(VariableNode node);
    public E visit(IntegerLiteralNode node);
    public E visit(StringLiteralNode node);
}
