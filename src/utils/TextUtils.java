package utils;
//一些文本处理的方法工具.
import java.util.*;
import java.io.*;
public class TextUtils {
	static final private byte vtab = 013;
	
	static public String dumpString(String s){
		try{
			return dumpString(s,"UTF-8");
		}catch(UnsupportedEncodingException e){
			throw new Error("UTF-8 is not supported: "+ e.getMessage());
		}
	}
	
	//打印一个字符串字面量
	 static public String dumpString(String string, String encoding)
	            throws UnsupportedEncodingException {
	        byte[] src = string.getBytes(encoding);
	        StringBuffer buf = new StringBuffer();
	        buf.append("\"");
	        for (int n = 0; n < src.length; n++) {
	            int c = toUnsigned(src[n]);
	            if (c == '"') buf.append("\\\"");
	            else if (isPrintable(c)) buf.append((char)c);
	            else if (c == '\b') buf.append("\\b");
	            else if (c == '\t') buf.append("\\t");
	            else if (c == '\n') buf.append("\\n");
	            else if (c == vtab) buf.append("\\v");
	            else if (c == '\f') buf.append("\\f");
	            else if (c == '\r') buf.append("\\r");
	            else {
	                buf.append("\\" + Integer.toOctalString(c));
	            }
	        }
	        buf.append("\"");
	        return buf.toString();
	    }

	    static private int toUnsigned(byte b) {
	        return b >= 0 ? b : 256 + b;
	    }

	    static public boolean isPrintable(int c) {
	        return (' ' <= c) && (c <= '~');
	    }
	    public static void main(String[] args)throws Exception{
	    	System.out.println(dumpString("h", "UTF-8"));
	    	System.out.println("hel\\tlo\\n");
	    	String test="\"a\na\"";
	    	int c=test.indexOf("\\");
	    	//System.out.println(c);
	    }
	    static public <T> List<T> reverse(List<T> list) {
	        List<T> result = new ArrayList<T>(list.size());
	        ListIterator<T> it = list.listIterator(list.size());
	        while (it.hasPrevious()) {
	            result.add(it.previous());
	        }
	        return result;
	    }
}
