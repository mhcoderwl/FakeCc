package type;

import java.util.List;

import ast.Location;
import ast.Slot;
import utils.*;
public class UnionType extends CompositeType{
	public UnionType(String name, List<Slot> membs, Location loc) {
		super(name, membs, loc);// TODO Auto-generated constructor stub
	}
	public boolean isUnion() { return true; }
	 protected void computeOffsets() {
	        long maxSize = 0;
	        long maxAlign = 1;
	        for (Slot s : members) {
	            s.setOffset(0);
	            maxSize = Math.max(maxSize, s.allocSize());
	            maxAlign = Math.max(maxAlign, s.alignment());
	        }
	        cachedSize = AsmUtils.align(maxSize, maxAlign);
	        cachedAlign = maxAlign;
	    }
    public boolean isSameType(Type other) {
        if (! other.isUnion()) return false;
        return equals((UnionType)other);
    }
}
