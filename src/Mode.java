public class Mode {
	static int mode=0;
	final static int AUTOMATIC=1;
	final static int MANUAL=2;
	final static int TRACE=4;
	final static int MESSAGE=8;
	final static int STEP=16;
	final static int ALL=-1;
	
	public static void setMode(int m) {
		mode = m;
	}

	public static int getMode() {
		return mode;
	}

	public static boolean is(int m) {
		return (mode & m) != 0;
	}
}