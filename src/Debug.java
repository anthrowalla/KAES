/* Revision History

* 31-10 MF Changed Debug so that if level == zerolevel or the calling level in prout is zerolevel the message is printed.

			this was to permit all System.out.println messages to be converted to Debug messages

			for easy suppression later by changing zerolevel to an impossible value.

*/

import java.util.Stack;

public class Debug {

	static boolean debugging=false;
	static boolean dumpflag=false;
	static int level=9;
	public static int zerolevel = 0;
	static Stack dlevel = new Stack();

	static public void prout(String x) {
		prout(level,x);
	}

	static public void prout(int l, String x, boolean dmp) {
		boolean olddump = dumpflag;
		dumpflag = dmp;
		prout(l,x);
		dumpflag = olddump;
	}
	
	static public void prout(int l, String x) {
		if (debugging && level < 0) {
			if (-level == l) {
				System.out.println(x+" : Debug "+l);
				if (dumpflag) {
					Throwable t = new Throwable();
					t.printStackTrace();
				}
			}
		} else if ((l == zerolevel || level == zerolevel) || (debugging && l <= level)) {
			System.out.println(x+" : Debug "+l);
			if (dumpflag) {
				Throwable t = new Throwable();
				t.printStackTrace();
			}
		}
	}

	public static void  on() {
		debugging = true;
	}

	public static void  on(int l) {
		debugging = true;
		level=l;
	}

	public static  void  off() {
		debugging = false;
		level = 9;
	}

	public static  void  dodump() {
		dumpflag = true;
	}

	public static  void  dump() {
		Throwable t = new Throwable();
		t.printStackTrace();
	}
	
	public static  void  nodump() {
		dumpflag = false;
	}
	
	public void push() {
		dlevel.push(new Integer(level));
	}
	
	public void pop() {
		if (!dlevel.empty()) level = ((Integer) dlevel.pop()).intValue();
	}
}