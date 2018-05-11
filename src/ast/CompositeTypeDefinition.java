package ast;

import java.util.*;
import type.*;
//组合类型的抽象基类,含有定义的成员变量列表
public abstract class CompositeTypeDefinition extends TypeDefinition{
	protected List<Slot> members;

    public CompositeTypeDefinition(Location loc, TypeRef ref,
                                   String name, List<Slot> membs) {
        super(loc, ref, name);
        members = membs;
    }

    public boolean isCompositeType() {
        return true;
    }

    abstract public String kind();

    public List<Slot> members() {
        return members;
    }
    
    protected void printTree(Dumper d) {
        d.printMember("name", name);
        d.printNodeList("members", members);
    }
}
