package sys;

import type.*;
import utils.ErrorHandler;
//后端代码类的接口,包括代码生成生成汇编代码,然后汇编器转成目标文件,链接器
public interface Platform {
	TypeTable typeTable();//额外包括了类型表,与平台有关.
    CodeGenerator codeGenerator(CodeGeneratorOptions opts, ErrorHandler h);
   // Assembler assembler(ErrorHandler h);
 //   Linker linker(ErrorHandler h);
}
