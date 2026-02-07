import java.awt.CheckboxMenuItem;

	public class Instruction {
		String name;
		int opcode;
		ListVector dependsOn=new ListVector();
		ListVector exclusiveWith = new ListVector();

		Instruction(String n, int o) {
			name=n;
			opcode=o;
		}
		
		Instruction(String n, int o, int [] d, int [] e) {
			name=n;
			opcode=o;
			for (int i=0;i<d.length;i++) {
				dependsOn.addElement(new Integer(d[i]));
			}
				for (int i=0;i<d.length;i++) {
				exclusiveWith.addElement(new Integer(e[i]));
			}
		}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setOpcode(int opCode) {
		this.opcode = opCode;
	}

	public int getOpcode() {
		return opcode;
	}

	public Instruction e(int x) {
		addExclusive(x);
		return this;
	}

	public Instruction m() {
		setMultipleHits(true);
		return this;
	}
	
	public void addExclusive(int x) {
		exclusiveWith.addElement(new Integer(x));
	}

	public Instruction d(int x) {
		addDepend(x);
		return this;
	}
	
	public void addDepend(int x) {
		dependsOn.addElement(new Integer(x));
	}
	
	public void setDependsOn(ListVector dependsOn) {
		this.dependsOn = dependsOn;
	}

	public ListVector getDependsOn() {
		return dependsOn;
	}

	public int getDependsOn(int a) {
		return ((Integer) this.dependsOn.elementAt(a)).intValue();
	}

	public void setExclusiveWith(ListVector exclusiveWith) {
		this.exclusiveWith = exclusiveWith;
	}

	public ListVector getExclusiveWith() {
		return exclusiveWith;
	}

	public int getExclusiveWith(int a) {
		return ((Integer) this.exclusiveWith.elementAt(a)).intValue();
	}

	public void setTheMenuItem(CheckboxMenuItem theMenuItem) {
		this.theMenuItem = theMenuItem;
	}

	public CheckboxMenuItem getTheMenuItem() {
		return theMenuItem;
	}

	protected CheckboxMenuItem theMenuItem;

	public void setMultipleHits(boolean multipleHits) {
		this.multipleHits = multipleHits;
	}

	public boolean isMultipleHits() {
		return multipleHits;
	}
	protected boolean multipleHits=false;
}
