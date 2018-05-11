package parser;

import java.util.*;

import ast.*;
import java.io.*;

import exception.*;
import utils.*;
public class LibraryLoader {
	protected List<String> loadPath;//加载的路径
    protected LinkedList<String> loadingLibraries;//用来判重,加载的头文件中不能递归import自己
    protected Map<String, Declarations> loadedLibraries;//已经加载的文件缓存
    //构造函数调用成员函数会报错,但是静态成员函数在构造函数之前就完成,可以调用
	public LibraryLoader(){
		this(defaultPath());
	}
	public LibraryLoader(List<String> Paths){
		loadPath=Paths;
		loadingLibraries=new LinkedList<String>();
		loadedLibraries=new HashMap<String,Declarations>();
	}
	//用线性表可以直接找到import自己的父文件,但是在判重处会导致线性时间.可以用HashSet
	public  Declarations loadLibrary(String filename,ErrorHandler errorHandler)throws CompileException{
		if(loadingLibraries.contains(filename)){
			throw new SemanticException("递归import自己"+loadingLibraries.getLast()+":"+filename);
		}
		if(loadedLibraries.containsKey(filename)){
			return loadedLibraries.get(filename);
		}
		loadingLibraries.addLast(filename);
		Declarations result=Parser.parseDeclFile(searchLib(filename), this, errorHandler);
		loadedLibraries.put(filename, result);
		loadingLibraries.removeLast();
		return result;
		
	}
	
	public static List<String> defaultPath(){
		return new ArrayList<String>(Arrays.asList("./test/import"));
	}
	public File searchLib(String filename) throws FileException {
        try {
            for (String path : loadPath) {
                File file = new File(path + "/" + libPath(filename) + ".hm");
                if (file.exists()) {
                    return file;
                }
            }
            throw new FileException("no such library header file: " + filename);
        }
        catch (SecurityException ex) {
            throw new FileException(ex.getMessage());
        }
    }
	public String libPath(String id) {
        return id.replace('.', '/');
    }
}
