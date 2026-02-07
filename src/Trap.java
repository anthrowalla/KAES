public class Trap {
	public void go() {
		int xxx=1;
		try {
			xxx--;
			int a = 1/xxx;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}