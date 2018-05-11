package compiler;
import type.*;
import utils.*;
import java.util.*;
import java.io.*;
import exception.*;
import parser.*;
import sys.*;
/*
 * 对于不需要构造时的参数的类,设计时可以用一个静态方法来生成类,设置构造函数私有,
 * 然后在静态方法里处理完传入参数后,返回生成的该类对象.有点像工厂模式.
 */
public class Options {
	static public Options parse(String[] args){
		Options opt=new Options();
		opt.parseArgs(args);
		return opt;
	}

	private Options(){
		
	}
	private List<SourceFile> sourceFiles;//保存解析出来的源文件名
	private CompilerMode mode;//解析出设置的模式
	private LibraryLoader loader=new LibraryLoader();//加载器
	private Platform platform = new X86Linux();//编译的平台
	private CodeGeneratorOptions genOptions = new CodeGeneratorOptions();
    //private AssemblerOptions asOptions = new AssemblerOptions();
	private String outputFileName;
	public boolean isAssembleRequired(){
		return mode.equals(CompilerMode.Assemble);
	}
	public List<SourceFile>sourceFiles(){
		return this.sourceFiles;
	}
	public CompilerMode mode(){
		return mode;
	}
	public LibraryLoader loader(){
		return this.loader;
	}
	public TypeTable typeTable(){
		return platform.typeTable();
	}
	private void parseArgs(String[] args){
		sourceFiles=new ArrayList<SourceFile>();
		
		for(String arg:args){
			if(arg.startsWith("-")){
				if(CompilerMode.isMode(arg)){
					if(mode!=null){
						parseError(mode.toOption()+" option and "+arg+" option is exculsive");
					}
					//设置编译模式
					mode=CompilerMode.getMode(arg);
				}else if(arg.startsWith("-")){
					if (arg.equals("--help")) {
						printHelp(System.out);
						System.exit(0);
					}else if (arg.startsWith("-o")) {
						outputFileName=arg.substring(2);
					}else if(arg.startsWith("-f")){
						sourceFiles.add(new SourceFile(arg.substring(2)));
					}
				}
                else {
                    parseError("unknown option: " + arg);
                }
			}
		}
	}
	CodeGenerator codeGenerator(ErrorHandler h) {
        return platform.codeGenerator(genOptions, h);
    }
	private void parseError(String msg) throws OptionParseError{
        throw new OptionParseError(msg);
    }
	 Assembler assembler(ErrorHandler h) {
	        return platform.assembler(h);
	    }
	static public void printHelp(PrintStream out) {
        out.println("Usage: cbc [options] file...");
        out.println("Options:");
        out.println("  --check-syntax   Checks syntax and quit.");
        out.println("  --print-tokens    Dumps tokens and quit.");
        out.println("  --print-ast       Dumps AST and quit.");
        out.println("  --print-semantic  Dumps AST after semantic checks and quit.");
        out.println("  --print-ir        Dumps IR and quit.");
 
    }
}
