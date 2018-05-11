package ast;
//节点的基类
import java.io.PrintStream;
//格式化输出
abstract public class Node implements Dumpable {
	public Node(){
		
	}
	//返回某节点对应的语法在代码中的位置,也能返回出错的语句的位置.
	public abstract Location location();
	public void dump(){
		dump(System.out);
	}
	public void dump(PrintStream out){
		dump(new Dumper(out));
	}
	//以文本形式打印语法树
	public void dump(Dumper d){
		d.printClass(this,location());
		printTree(d);
	}
	//打印树的内容
	abstract protected void printTree(Dumper d);
}
