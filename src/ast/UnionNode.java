package ast;
import type.*;
import java.util.*;
public class UnionNode extends CompositeTypeDefinition{
	public UnionNode(Location loc,String name, TypeRef ref,  List<Slot> membs) {
        super(loc, ref, name, membs);
    }

    public String kind() {
        return "struct";
    }

    public boolean isStruct() {
        return true;
    }

    // #@@range/definingType{
    public Type definingType() {
        return new UnionType(name(), members(), location());
    }
    // #@@}

    public <T> T accept(DeclarationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
