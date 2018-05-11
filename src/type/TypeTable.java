package type;

import java.util.*;

import utils.ErrorHandler;

import ast.CompositeTypeDefinition;
import ast.Slot;

public class TypeTable {
	private int intSize;
    private int longSize;
    private int pointerSize;//规定的指针所占字节
	Map<TypeRef,Type>table;
	//根据平台的不同类型所占的字节数不同,创建类型表
	static public TypeTable ilp32() { return newTable(1, 2, 4, 4, 4); }
    static public TypeTable ilp64() { return newTable(1, 2, 8, 8, 8); }
    static public TypeTable lp64()  { return newTable(1, 2, 4, 8, 8); }
    static public TypeTable llp64() { return newTable(1, 2, 4, 4, 8); }
	static private TypeTable newTable(int charsize, int shortsize,
            int intsize, int longsize, int ptrsize) {
		TypeTable table = new TypeTable(intsize, longsize, ptrsize);
		table.put(new VoidTypeRef(), new VoidType());
		table.put(IntegerTypeRef.charRef(),
				new IntegerType(charsize,  true, "char"));
		table.put(IntegerTypeRef.shortRef(),
				new IntegerType(shortsize, true, "short"));
		table.put(IntegerTypeRef.intRef(),
				new IntegerType(intsize, true, "int"));
		table.put(IntegerTypeRef.longRef(),
				new IntegerType(longsize, true, "long"));
		table.put(IntegerTypeRef.ucharRef(),
				new IntegerType(charsize, false, "unsigned char"));
		table.put(IntegerTypeRef.ushortRef(),
				new IntegerType(shortsize, false, "unsigned short"));
		table.put(IntegerTypeRef.uintRef(),
				new IntegerType(intsize, false, "unsigned int"));
		table.put(IntegerTypeRef.ulongRef(),
				new IntegerType(longsize, false, "unsigned long"));
		return table;
}
	//构造函数私有只能通过静态方法来创建表.
	private TypeTable(int intSize,int longSize,int pointerSize){
		this.intSize = intSize;
	    this.longSize = longSize;
	    this.pointerSize = pointerSize;
		table=new HashMap<TypeRef,Type>();
	}
	public Collection<Type> types() {
        return table.values();
    }
	/*
	 * 对表中类型进行语义检查的启动方法.
	 */
	 	public void semanticCheck(ErrorHandler h) {
        for (Type t : types()) {
          
            if (t instanceof CompositeType) {
                checkVoidMember((CompositeType)t, h);
                checkDuplicatedMember((CompositeType)t, h);
            }
            else if (t instanceof ArrayType) {
            	checkVoidArray((ArrayType)t, h);
            }
            checkRecursiveDefinition(t, h);
        }
    }
	 
	/*
	 * 插入一对元素
	 */
	public void put(TypeRef ref,Type type){
		if(table.containsKey(ref)){
			throw new Error("duplicated type definition: " + ref);
		}
		table.put(ref, type);
	}
	public boolean isDefined(TypeRef ref){
		return table.containsKey(ref);
	}
	public Type get(TypeRef ref) {
        Type type = table.get(ref);
        if (type == null) {
            if (ref instanceof UserTypeRef) {
               
                UserTypeRef uref = (UserTypeRef)ref;
                throw new Error("undefined type: " + uref.name());
            }
            else if (ref instanceof PointerTypeRef) {
                PointerTypeRef pref = (PointerTypeRef)ref;
                Type t = new PointerType(pointerSize, get(pref.baseType()));
                table.put(pref, t);
                return t;
            }
            //数组类型要递归get数组的baseType的ref,返回的type当做当前的数组的baseType.
            else if (ref instanceof ArrayTypeRef) {
                ArrayTypeRef aref = (ArrayTypeRef)ref;
                Type t = new ArrayType(get(aref.baseType()),
                                       aref.length(),
                                       pointerSize);
                table.put(aref, t);
                return t;
            }
            else if (ref instanceof FunctionTypeRef) {
                FunctionTypeRef fref = (FunctionTypeRef)ref;
                Type t = new FunctionType(get(fref.returnType()),
                                          fref.params().internTypes(this));
                table.put(fref, t);
                return t;
            }
            throw new Error("unregistered type: " + ref.toString());
        }
        return type;
    }
	/*
	 * 对于参数的类型,如果是查出是一个数组类型的话,返回的是指针类型,不论是几维数组.
	 */
	public Type getParamType(TypeRef ref) {
        Type t = get(ref);
        return t.isArray() ? pointerTo(t.baseType()) : t;
    }
	
	/*
	 * 检查一个结构体或者联合体类型中是否有重复定义的成员
	 */
	public void checkDuplicatedMember(CompositeType type,ErrorHandler errorHandler){
		HashSet<String> members=new HashSet<String>();
		for(Slot member:type.members()){
			if(members.contains(member.name())){
				errorHandler.error(type.location(),type.toString()+" has duplicated member: "+ member.name());
			}else{
				members.add(member.name());
			}
		}
	}
	/*
	 * 检查一个结构体或联合体中的类型是否有void成员.
	 */
	public void checkVoidMember(CompositeType type,ErrorHandler errorHandler){
		for(Slot member:type.members()){
			if(member.type().isVoid()){
				errorHandler.error(type.location(),type.toString()+" has Void member: "+ member.name());
			}
		}
	}
	/*
	 * 检查一个数组类型是否为void.
	 */
	public void checkVoidArray(ArrayType type,ErrorHandler errorHandler){
		if(type.baseType().isVoid()){
			errorHandler.error("Void Array");
		}
	}
	/*
	 * 检查是否有循环定义:
	 * 			结构体a中含有结构体b,b中含c,c中含a
	 * 转换成一个有向图中检查是否有环的问题.
	 * 可以用DFS,也可以用拓扑排序,后者需要得到图的所有节点,前者可以查看以某一个节点为起点来检查
	 */
	//**************DFS实现***************
	
	public void checkRecursiveDefinition(Type t,ErrorHandler errorHandler){
		HashSet<Type>visited=new HashSet<Type>();
		_checkRecursiveDefinition( t, errorHandler,visited);
	}
	public void _checkRecursiveDefinition(Type t,ErrorHandler errorHandler,HashSet<Type> visited){
		if(visited.contains(t)){
			errorHandler.error(((NamedType)t).location(),"recursive Type Definition: "+t);
		}else{
			visited.add(t);
			//复合类型检查所有成员
			if(t instanceof CompositeType){
				CompositeType tmp=(CompositeType)t;
				for(Type memberType:tmp.memberTypes()){
					_checkRecursiveDefinition( memberType, errorHandler,visited);
				}
			}
			//类型别名指向的类型
			if(t instanceof UserType){
				_checkRecursiveDefinition( ((UserType)t).realType(), errorHandler,visited);
			}
			//数组的基本类型也在图中
			if(t instanceof ArrayType){
				ArrayType tmp=(ArrayType)t;
				_checkRecursiveDefinition( tmp.baseType(), errorHandler,visited);
			}
			visited.remove(t);
		}
	}
	
	/*
	 * 根据平台不同规定的int和pointer类型所占的字节大小,返回对应的类型.
	 */
	public VoidType voidType() {
        return (VoidType)table.get(new VoidTypeRef());
    }

    public IntegerType signedChar() {
        return (IntegerType)table.get(IntegerTypeRef.charRef());
    }

    public IntegerType signedShort() {
        return (IntegerType)table.get(IntegerTypeRef.shortRef());
    }

    public IntegerType signedInt() {
        return (IntegerType)table.get(IntegerTypeRef.intRef());
    }

    public IntegerType signedLong() {
        return (IntegerType)table.get(IntegerTypeRef.longRef());
    }

    public IntegerType unsignedChar() {
        return (IntegerType)table.get(IntegerTypeRef.ucharRef());
    }

    public IntegerType unsignedShort() {
        return (IntegerType)table.get(IntegerTypeRef.ushortRef());
    }

    public IntegerType unsignedInt() {
        return (IntegerType)table.get(IntegerTypeRef.uintRef());
    }

    public IntegerType unsignedLong() {
        return (IntegerType)table.get(IntegerTypeRef.ulongRef());
    }
    //创建一个指向指定类型的指针类型
    public PointerType pointerTo(Type baseType) {
        return new PointerType(pointerSize, baseType);
    }
    public int intSize() {
        return this.intSize;
    }

    public int longSize() {
        return this.longSize;
    }

    public int pointerSize() {
        return this.pointerSize;
    }
	
	
	
}
