package ast;
//自我作用运算符++和--
public abstract class UnaryArithmeticOpNode extends UnaryOpNode {
	protected long amount;
	public UnaryArithmeticOpNode(String op,ExprNode expr) {
		super(op, expr);// TODO Auto-generated constructor stub
	}
	public long amount() {
        return this.amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
