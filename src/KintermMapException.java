public class KintermMapException extends RuntimeException {
	
	public KintermMapException() {
		super();
	}
	
	public KintermMapException(String s) {
		super(s);
	}
	
	public KintermMapException(int message_number, String s) {
		super("!#" + message_number + ": " + (!s.startsWith("!#") ? s : s.substring(s.indexOf(":")+2)));
		setId(message_number);
	}
	
	public KintermMapException(int message) {
		this(message, messages.length > message ? messages[message] : "!# 0: General Kin Term Map Exception");
	}
	
	static String[] messages = {"!# 0: General Kinterm Map exception",
								"!# 1: Term mapping too complex: many to one."};


	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	protected int id=0;
}