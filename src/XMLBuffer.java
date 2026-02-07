public class XMLBuffer {
	StringBuffer b=new StringBuffer();
	
	public static String EOL = System.getProperty("line.separator");
	
	public XMLBuffer put(String s) {
		b.append(XMLIndent.getSpace()+s+EOL);
		leol = true;
		return this;
	}

	public XMLBuffer append(String s) {
		sp();
		b.append(s);
		leol = false;
		return this;
	}

	public String toString() {
		return (b.toString());
	}
	
	public XMLBuffer inc() {
		XMLIndent.increment();
		return this;
	}

	public XMLBuffer dec() {
		XMLIndent.decrement();
		return this;
	}

	public XMLBuffer eol() {
		b.append(EOL);
		leol = true;
		return this;
	}

	public XMLBuffer space() {
		if (leol) b.append(XMLIndent.getSpace());
		leol = false;
		return this;
	}

	public XMLBuffer sp() {
		return space();
	}
	
	boolean leol=true;
	java.util.Stack lastTag=new java.util.Stack();
	
	public XMLBuffer tag(String t) {
		append("<"+t+">");
		lastTag.push(t);
		return this;
	}

	public XMLBuffer finish() {
		while (!lastTag.empty()) {
			itag();
		}
		return this;
	}
	
	public XMLBuffer itag() {
		if (lastTag.empty()) return this;
		return itag((String) lastTag.pop());
	}

	public XMLBuffer itag(String t) {
		return append("</"+t+">");
	}
	
	public XMLBuffer format(String s, int len) {
		int p,sp=-1, st=0;
		String t;
		for (;;) {
			if (s.length() < len) {
				put(s);
				break;
			}
			t = s.substring(0,len);
			if ((p = t.lastIndexOf('\n')) >0) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('\r')) >0) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('\t')) >0) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf(' ')) >0) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('>')) >0 && p+1 < t.length()) {
				t = t.substring(0,p+1);
			} else if ((p = t.lastIndexOf('<')) >0) {
				t = t.substring(0,p);
			}
			put(t);
			s = s.substring(t.length());
			//System.out.println("Format "+t);
		}
		//System.out.println("Exit format");
		return this;
	}
}