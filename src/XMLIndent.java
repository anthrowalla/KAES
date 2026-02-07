public class XMLIndent {
	static int level=0;
	static int tabstop=4;
	static String space = "";
	static String spacer = "                                                                             ";
	static String Eol = System.getProperty("line.separator");
	
	static int getLevel() { return level;}
	static int getTab() { return tabstop;}
	static void setLevel(int i) { level=i;}

	static void setTab(int i) {
		tabstop=i;
	}
	
	static int getIndent() {
		int x = tabstop*level;
		if (x > spacer.length()) x = spacer.length();
		return x;
	}
	
	static String getSpace() {
		return space;
	}
	
	static void increment() {
		level++;
		space = spacer.substring(0,getIndent());
	}
	
	static void decrement() {
		level--;
		if (level < 0) level = 0;
		space = spacer.substring(0,getIndent());
	}
	
	static String pp(String s) {
		return space + s + Eol;
	}
}
