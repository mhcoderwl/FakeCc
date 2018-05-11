package type;

import java.util.*;
import ast.*;
import java.lang.reflect.*;
import exception.*;
public abstract class CompositeType extends NamedType{
	protected List<Slot> members;
	//待定
    protected long cachedSize;
    protected long cachedAlign;
    protected boolean isRecursiveChecked;//用来记录是否检查过循环引用的问题.

    public CompositeType(String name, List<Slot> membs, Location loc) {
        super(name, loc);
        this.members = membs;
        this.cachedSize = Type.sizeUnknown;
        this.cachedAlign = Type.sizeUnknown;
        this.isRecursiveChecked = false;
    }

    public boolean isCompositeType() {
        return true;
    }

    public boolean isSameType(Type other) {
        return compareMemberTypes(other, "isSameType");
    }

    public boolean isCompatible(Type target) {
        return compareMemberTypes(target, "isCompatible");
    }

    public boolean isCastableTo(Type target) {
        return compareMemberTypes(target, "isCastableTo");
    }
    //逐个比较每个成员,把三种比较的共同代码抽出.
    protected boolean compareMemberTypes(Type other, String cmpMethod) {
        if (isStruct() && !other.isStruct()) return false;
        if (isUnion() && !other.isUnion()) return false;
        CompositeType otherType = (CompositeType)other;
        //成员个数不同直接返回false
        if (members.size() != other.size()) return false;
        Iterator<Type> otherTypes = otherType.memberTypes().iterator();
        //迭代比较
        for (Type t : memberTypes()) {
            if (! compareTypesBy(cmpMethod, t, otherTypes.next())) {
                return false;
            }
        }
        return true;
    }

    protected boolean compareTypesBy(String cmpMethod, Type t, Type tt) {
        try {
        	//反射机制,得到任意类的任意方法,方法名和方法的调用参数类型
            Method cmp = Type.class.getMethod(cmpMethod,
                                              Type.class );
            Boolean b = (Boolean)cmp.invoke(t,  tt );
            return b.booleanValue();
        }
        catch (NoSuchMethodException ex) {
            throw new Error(ex.getMessage());
        }
        catch (IllegalAccessException ex) {
            throw new Error(ex.getMessage());
        }
        catch (InvocationTargetException ex) {
            throw new Error(ex.getMessage());
        }
    }

    public long size() {
        if (cachedSize == Type.sizeUnknown) {
            computeOffsets();
        }
        return cachedSize;
    }

    public long alignmemt() {
        if (cachedAlign == Type.sizeUnknown) {
            computeOffsets();
        }
        return cachedAlign;
    }

    public List<Slot> members() {
        return members;
    }

    public List<Type> memberTypes() {
        List<Type> result = new ArrayList<Type>();
        for (Slot s : members) {
            result.add(s.type());
        }
        return result;
    }

    public boolean hasMember(String name) {
        return (get(name) != null);
    }
    /*
     * 查看一个成员变量的类型
     */
    public Type memberType(String name) {
        return fetch(name).type();
    }

    public long memberOffset(String name) {
        Slot s = fetch(name);
        if (s.offset() == Type.sizeUnknown) {
            computeOffsets();
        }
        return s.offset();
    }
    //字节对齐
    abstract protected void computeOffsets();

    protected Slot fetch(String name) {
        Slot s = get(name);
        if (s == null) {
            throw new SemanticError("no such member in "
                                    + toString() + ": " + name);
        }
        return s;
    }
    //取出一个成员
    public Slot get(String name) {
        for (Slot s : members) {
            if (s.name().equals(name)) {
                return s;
            }
        }
        return null;
    }
}
