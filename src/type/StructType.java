package type;

import java.util.*;
import utils.*;
import ast.*;

public class StructType extends CompositeType{
	public StructType(String name, List<Slot> membs, Location loc) {
		super(name, membs, loc);// TODO Auto-generated constructor stub
	}
	public boolean isUnion() { return true; }
	//计算当前每个成员的字节偏移量,要考虑字节对齐.
	protected void computeOffsets() {
        long offset = 0;
        long maxAlign = 1;
        for (Slot s : members()) {
            offset = AsmUtils.align(offset, s.allocSize());//计算偏移量
            s.setOffset(offset);
            offset += s.allocSize();//下一个成员的起始偏移量
            maxAlign = Math.max(maxAlign, s.alignment());//最后预留字节
        }
        cachedSize = AsmUtils.align(offset, maxAlign);//预留字节
        cachedAlign = maxAlign;//最大成员的字节大小
    }
    public boolean isSameType(Type other) {
        if (! other.isStruct()) return false;
        return equals((StructType)other);
    }
}
