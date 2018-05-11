package compiler;

import java.io.*;

public class SourceFile {
	static final String EXT_CFLAT_SOURCE = ".cmm";
    static final String EXT_ASSEMBLY_SOURCE = ".s";
    static final String EXT_OBJECT_FILE = ".o";
    //static final String EXT_STATIC_LIBRARY = ".a";
   // static final String EXT_SHARED_LIBRARY = ".so";
    static final String EXT_EXECUTABLE_FILE = "";
    private String initName;//文件的无后缀名
    private String curName;//编译过程中生成的类型文件名.
	public SourceFile(String fileName){
		initName=fileName.split("\\.")[1];
		curName=fileName;
	}
	public String getName(){
		return curName;
	}
	public void setName(String target){
		this.curName=target;
	}
	public String initName(){
		return this.initName;
	}
	public static String asmFileNameOf(SourceFile file){
		return file.initName()+SourceFile.EXT_ASSEMBLY_SOURCE;
	}
	public static String cmmFileNameOf(SourceFile file){
		return file.initName()+SourceFile.EXT_CFLAT_SOURCE;
	}
	public static String objFileNameOf(SourceFile file){
		return file.initName()+SourceFile.EXT_OBJECT_FILE;
	}
}
