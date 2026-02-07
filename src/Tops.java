import java.awt.*;


public class Tops implements BuiltinOperations {

	/** Override in subclasses
	*/
	KintermFrame theFrame=null;
	TransferKinInfoVector tk1 = null;

	boolean inited = false;

	public void init() {
		inited=true;
		reset();
	}
	
	public void reset() {
		tk1 = null;
		getTk1();
		System.out.println(" setstage5");
		setStage(-1);
		serialNumber++;
	}
	
		
	void getTk1() {
		if (tk1 == null) {
			tk1 = (TransferKinInfoVector) theFrame.kinshipTermsPanel1.getTransferKinInfo();
		}
	}	

	// -------------------------------
	long serialNumber=0;
	int stage=-1;
	InstructionSet instructions = null;

	final static int ZERO = 0;

	// --------------------------------------
	
	/** Override in subclasses
	*/
	public InstructionSet initInstructions() {
		InstructionSet ins = new InstructionSet("Menu Title");
       	ins.create("Menu Item",ZERO);// .m() = multiple hits, .d(int dependson), .e(int exclusiveWith);
       	return ins;
	}

	public InstructionSet getInstructions() {
    	return instructions;
    }

	
	// -------------------------------
	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getStage() {
		return stage;
	}
	
	public long incSerialNumber() {
		return ++serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getSerialNumber() {
		return serialNumber;
	}
	
	public void goKaesWindow() {
		GlobalWindowManager.getMainWindow().toFront();
	}
	
	public void waitCursor() {
		theFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}
	
	public void arrowCursor() {
		theFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	// ------------------------------------
	/** Override in subclasses
	*/
	public boolean resume() {
		if (stage == -1) stage = 0;
		int opcodes[] = getCurrentOpcodes();
		if (stage >= opcodes.length) {
			stage = -1;
			serialNumber++;
			return false;
		}
		if (exec(opcodes[stage]) == false) {
			Debug.prout(4,"!!!!!!!!Bailing on "+stage+" = false");
			serialNumber++;
			return false;
		}
		Debug.prout(4,"Executed stage "+stage+"");
		stage++;
		return true;
	}
	
	int [] myOpcodes = {ZERO};
		
	public boolean exec() {
		return exec(myOpcodes);
	}
	
	public boolean exec(int opcode) {
		if (!inited) init();
		try {
			boolean flag = execOpcode(opcode);
			return (flag);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean exec(int [] opcodes) {
		boolean flag;
		//if (!inited) init();
		setCurrentOpcodes(opcodes);
		stage = 0;
		for(;;) {
			flag = resume();
			if (!flag || !Mode.is(Mode.AUTOMATIC)) break;
		}
		return flag;
	}
	
	/** Override in subclasses
	*/
	public boolean execOpcode(int opcode) {
		
		return false;
	}
	
	// -----------------------
	
	LocalVariables localVars = new LocalVariables();
	
	class LocalVariables extends java.util.Hashtable {
		public void putData(String name, Object data) {
			put(name,new SerializedData(name,data,serialNumber));
		}
		
		public SerializedData getDataRecord(String name) {
			return (SerializedData) get(name);
		}
		
		
		public Object getCurrentData(String name) {
			SerializedData s = getDataRecord(name);
			if (s == null) return null;
			if (s.isCurrent()) return s.data; // should delete s at this point??
			else return null;
		}
		
	}
	
	class SerializedData {
		long serial=-1;
		Object data;
		String name="Data";
		
		SerializedData(String name,Object data, long serial) {
			this.serial = serial;
			this.data = data;
			this.name = name;
		}
		
		public boolean isCurrent() {
			return (this.serial == serialNumber);
		}
		
		public boolean equals(Object n) {
			return (name.equals(n.toString()));
		}
		
		public String toString() {
			return name+":"+serial+":"+data.toString();
		} 
	}

	class SymActionAops3  implements java.awt.event.ItemListener 
	{

		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Debug.prout(4,"Action event performed");
		}
		
		boolean itemLock = false;
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			if (itemLock) return;
			theFrame.setCurrentBuiltins(Tops.this);
			CheckboxMenuItem em = (CheckboxMenuItem) event.getSource();
			String itemName = em.getLabel();
			InstructionSet aops2Ins = getInstructions();
			Instruction inst = aops2Ins.getInstruction(itemName);
			if (!inst.isMultipleHits()) {
				em.setEnabled(false);
			//	itemLock = true;
			//	if (!em.getState()) em.setState(true);
			//	itemLock = false;
			}
			
			/*int opcode = aops2Ins.getOpcode(itemName);//original code; see also Bops
			Debug.prout(4,"!+!+!+Executing "+itemName+" opcode="+opcode);
			Debug.prout(4,"++++++++++++++++++++++++++ready to do stuff in execOpcode opcode"+opcode);
			boolean st = execOpcode(opcode);
			Debug.prout(4,"!+!+!+Executed "+itemName+" opcode="+opcode+" state="+st);
			if (st == false) {
				for(int i=0;i<theMenu.getItemCount();i++) {
					CheckboxMenuItem m =(CheckboxMenuItem) theMenu.getItem(i);
					m.setState(false);
					m.setEnabled(true); // make adjustments for dependencies in future
					
				}
			} else {
				// aops2Ins.disableExclusiveWithInstructions(aops2Ins.getInstruction(itemName));
			}*/
			
			int opcode = aops2Ins.getOpcode(itemName);//original code; see also Bops
			Debug.prout(4,"++++++++++++++++++++++++++ready to do stuff in execOpcode opcode"+opcode);
			boolean st = execOpcode(opcode);
			Debug.prout(4,"!+!+!+Executed "+itemName+" opcode="+opcode+" state="+st);
			if (st == false) {
				em.setState(false);
				em.setEnabled(true); // make adjustments for dependencies in future
			} else {
				// aops2Ins.disableExclusiveWithInstructions(aops2Ins.getInstruction(itemName));
			}
			
			aops2Ins.enableInstructions();

			// what to do if true/false
		}
	}

	void insertInstructionMenu() {
		SymActionAops3 aopsListener = new SymActionAops3();
		CheckboxMenuItem xMenuItem;
		if (instructions == null) initInstructions();
		InstructionSet ins = instructions;
		theMenu = new java.awt.Menu(ins.getTitle());
		
		for(ins.reset();ins.isNext();) {
			Instruction inq = (Instruction) ins.getNext();
			inq.setTheMenuItem(xMenuItem = new java.awt.CheckboxMenuItem(ins.getCurrentName()));
			xMenuItem.setState(false); // shows instruction last executed
			theMenu.add(xMenuItem);
            theMenu.addSeparator();
			xMenuItem.addItemListener(aopsListener);
		}
		ins.enableInstructions();
	//	mainmenuBar.add(extraMenus[AOPS2MENU]);
	}



	public void setTheMenu(Menu theMenu) {
		this.theMenu = theMenu;
	}

	public Menu getTheMenu() {
		if (instructions == null) initInstructions();
		if (theMenu == null) insertInstructionMenu();
		return theMenu;
	}
	protected Menu theMenu=null;


	public void setCurrentOpcodes(int[] currentOpcodes) {
		this.currentOpcodes = currentOpcodes;

			
	}

	public int[] getCurrentOpcodes() {
		return currentOpcodes;
	}

	public void setCurrentOpcodes(int a, int b) {
		this.currentOpcodes[a] = b;
	}

	public int getCurrentOpcodes(int a) {
		return this.currentOpcodes[a];
	}
	protected int[] currentOpcodes;
	
	public void setupUndo(){
	}

	public void doUndo(){
	}

}
