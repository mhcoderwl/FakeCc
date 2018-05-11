package compiler;
import sys.AssemblyCode;
import utils.*;
import java.io.*;
import java.util.*;
import ir.*;
import ast.AST;
import parser.*;
import exception.*;
public class Compiler {
	static public final String compilerName="CmmC";
	private final ErrorHandler errorHandler;
	static public void main(String[] args){
		new Compiler().start(args);
	}
	public Compiler(){
		this.errorHandler=new ErrorHandler(compilerName);
	}
	public void start(String[] args){
		//对命令参数解析,封装成一个option类
		Options opts = parseOptions(args);
		//只是检查文件语法.
        if (opts.mode() == CompilerMode.CheckSyntax) {
            checkSyntax(opts);
        }
       try{
            List<SourceFile> srcs = opts.sourceFiles();
            build(srcs, opts);
        }
        catch (CompileException ex) {
            errorHandler.error(ex.getMessage());
            System.exit(1);
        }
	}
	public Options parseOptions(String[] args){
		Options opts=Options.parse(args);
		return opts;
	}
	public void checkSyntax(Options opts){
		for(SourceFile file:opts.sourceFiles()){
			if (isValidSyntax(file.getName(), opts)) {
                System.out.println(file.getName() + ": Syntax OK");
            }
            else {
                System.out.println(file.getName() + ": Syntax Error");
            }
		}
	}
	//捕获到语法错误和文件错误返回false
	private boolean isValidSyntax(String path, Options opts) {
        try {
            parseFile(path, opts);
            return true;
        }
        catch (SyntaxException ex) {
            return false;
        }
        catch (FileException ex) {
            errorHandler.error(ex.getMessage());
            return false;
        }
    }
	/*Build 过程包括 编译生成汇编码,汇编器生成目标文件,链接器连接生成可执行文件.
	 */
	public void build(List<SourceFile> srcs,Options opts)throws CompileException{
		
		for(SourceFile file:srcs){
			String destName=SourceFile.asmFileNameOf(file);
			compile(file.getName(),destName,opts);
			file.setName(destName);
		//如果不需要继续转换汇编指令,直接返回
			if (! opts.isAssembleRequired()) continue;
			destName=SourceFile.objFileNameOf(file);
			//assmble(file.getName(),destName,opts);
			file.setName(destName);
		}
	}
	/*编译的过程包括:生成语法树,语义检查,生成中间代码,生成汇编码
	 * * 每执行一步要查看选项是否已完成
	 */
	private void compile(String srcName,String destName,Options opts)throws CompileException{
		AST ast=parseFile(srcName, opts);
		if(opts.mode()==CompilerMode.PrintTokens){
			ast.printTokens(System.out);
			return;
		}
		if(opts.mode()==CompilerMode.PrintAST){
			ast.dump();
			return;
		}
		ast.check(opts.typeTable(),errorHandler);
		if(opts.mode()==CompilerMode.PrintSemantic){
			ast.dump();
			return;
		}
		IR ir=new IRGenerator(opts.typeTable(), errorHandler).generate(ast);
		if(opts.mode()==CompilerMode.PrintIR){
			ir.dump();
			return;
		}
		AssemblyCode asm = generateAssembly(ir, opts);
        if (printAsm(asm, opts.mode())) return;
        writeFile(destName, asm.toSource());
	}
	
	private void writeFile(String path, String str) throws FileException {
        if (path.equals("-")) {
            System.out.print(str);
            return;
        }
        try {
            BufferedWriter f = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path)));
            try {
                f.write(str);
            }
            finally {
                f.close();
            }
        }
        catch (FileNotFoundException ex) {
            errorHandler.error("file not found: " + path);
            throw new FileException("file error");
        }
        catch (IOException ex) {
            errorHandler.error("IO error" + ex.getMessage());
            throw new FileException("file error");
        }
    }

 
	    private boolean printAsm(AssemblyCode asm, CompilerMode mode) {
	        if (mode == CompilerMode.PrintAsm) {
	            System.out.print(asm.toSource());
	            return true;
	        }
	        else {
	            return false;
	        }
	    }

	public AssemblyCode generateAssembly(IR ir, Options opts) {
        return opts.codeGenerator(errorHandler).generate(ir);
    }
	private AST parseFile(String filename,Options opts)throws SyntaxException,FileException{
		return Parser.parseFile(new File(filename),opts.loader(),errorHandler);
	}
	
	
}
