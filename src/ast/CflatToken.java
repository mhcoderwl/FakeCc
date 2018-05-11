package ast;

import java.util.Iterator;
import java.util.*;
import org.omg.CORBA.PUBLIC_MEMBER;
import parser.ParserConstants;
import parser.Token;
import utils.*;
//这个类是对Token的包装
public class CflatToken implements Iterable<CflatToken>{
	protected Token token;
	protected boolean isSpecial;
	public CflatToken(Token token) {
        this(token, false);
    }
	public CflatToken(Token t,boolean isSpecial){
		this.token=t;
		this.isSpecial=isSpecial;
	}
	public String toString(){
		return this.token.image;
	}
	
	public Iterator<CflatToken> iterator(){
		return buildTokenList(token,false).iterator();
	}
	//第一段sptoken不要的版本
	protected List<CflatToken> tokensWithoutFirstSpecials() {
        return buildTokenList(token, true);
    }
	//包括去除头字符的版本,考虑其作用
	protected List<CflatToken> buildTokenList(Token first, boolean rejectFirstSpecials) {
        List<CflatToken> result = new ArrayList<CflatToken>();
        boolean rejectSpecials = rejectFirstSpecials;
        for (Token t = first; t != null; t = t.next) {
            if (t.specialToken != null && !rejectSpecials) {
                Token s = specialTokenHead(t.specialToken);
                for (; s != null; s = s.next) {
                    result.add(new CflatToken(s));
                }
            }
            result.add(new CflatToken(t));
            rejectSpecials = false;
        }
        return result;
    }
	//得到一段sptoken的第一个
    protected Token specialTokenHead(Token firstSpecial) {
        Token s = firstSpecial;
        while (s.specialToken != null) {
            s = s.specialToken;
        }
        return s;
    }
    //对token一些接口的调用

     public boolean isSpecial() {
         return this.isSpecial;
     }

     public int kindID() {
         return token.kind;
     }

     public String kindName() {
         return ParserConstants.tokenImage[token.kind];
     }

     public int lineno() {
         return token.beginLine;
     }

     public int column() {
         return token.beginColumn;
     }

     public String image() {
         return token.image;
     }
     //image的一份拷贝
     public String dumpedImage() {
         return TextUtils.dumpString(token.image);
     }

}
