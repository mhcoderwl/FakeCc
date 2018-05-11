package sys;
import type.*;
public class X86Linux implements Platform{
	public TypeTable typeTable() {
        return TypeTable.ilp32();
    }

//    public CodeGenerator codeGenerator(
//            CodeGeneratorOptions opts, ErrorHandler h) {
//        return new sys.CodeGenerator(
//                opts, naturalType(), h);
//    }
//
//    private Type naturalType() {
//        return Type.INT32;
//    }
//
//    public Assembler assembler(ErrorHandler h) {
//        return new GNUAssembler(h);
//    }
//
//    public Linker linker(ErrorHandler h) {
//        return new GNULinker(h);
//    }
}
