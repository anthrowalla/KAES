/* History
* 11/21 DR added NONE = KintermEditObject.NONE;
*/

public interface BuiltinOperations {
	public void reset();
	public boolean exec();
	public boolean exec(int start);
	public boolean exec(int [] opcodes);
	public boolean execOpcode(int code);
	public boolean resume();
	public void setupUndo();
	public void doUndo();

	final static int UP = KintermEditObject.UP;
	final static int DOWN = KintermEditObject.DOWN;
	final static int RIGHT = KintermEditObject.RIGHT;
	final static int LEFT = KintermEditObject.LEFT;
	final static int SPOUSE = KintermEditObject.SPOUSE;
	final static int SPOUSER = KintermEditObject.SPOUSER;
	final static int SEXGEN = KintermEditObject.SEXGEN;
	final static int IDENTITY = KintermEditObject.IDENTITY;
	final static int SIDE = KintermEditObject.SIDE;//this needs to be implemented!!! DR 8/26

	final static int NONE = KintermEditObject.NONE;
	final static int NEUTRAL = 8;
	final static int MALE = 16;
	final static int FEMALE = 32;
	final static int SEX = 8|16|32;
	final static int MALEFEMALE = 8|16;

	final static int[] ORIENTATIONS = KintermEditObject.ORIENTATIONS;
}