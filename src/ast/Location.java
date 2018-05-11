package ast;

import parser.Token;

public class Location {
	protected String sourceName;
	protected CflatToken token;
	public Location(String sourceName,CflatToken token){
		this.sourceName=sourceName;
		this.token=token;
	}
	public Location(String sourceName,Token token){
		this(sourceName,new CflatToken(token));
	}
	public String toString(){
		return sourceName+" : "+token.lineno();
	}
	 public String sourceName() {
        return sourceName;
    }

    public CflatToken token() {
        return token;
    }
	
	
}
