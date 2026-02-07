
public class Token {
	public String sval=null;
	public int ttype=0;
	
	public static final int TT_EOF = -1;

    /** 
     * A constant indicating that the end of the line has been read. 
     */
    public static final int EOL = (System.getProperty("line.separator").equals("\r") ? '\r' : '\n');;
	public static final int EOF = -1;
    public static final int NUMBER = -2;
	public static final int WORD = -3;
	private static final int NOTHING = -4;

	
	public Token() {
	}
	
	public Token(String s, int t) {
		sval = s;
		ttype = t;
	}
}