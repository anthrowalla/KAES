import java.awt.*;
import java.awt.event.*;

/* History
* 10/10 DR moved populateDialoguePanel procedures to Bops from AopsOps
* 31/10 MF Fixed minor problem in itemStateChanged to avoid generating incidental classCastExceptions while scanning Menu structure
* 01/21/01 DR added procedure getTheFrame
* 01/25/01 DR added populateDialoguePanel(String s, StringVector v,String t)
* 03/06 DR changed	InstructionSet instructions = null; to InstructionSet instructions = initInstructions();
* to resolve null pointer exception at load time
*/

public class Bops implements BuiltinOperations {

	/** Override in subclasses
	*/
	KintermFrame theFrame=null;
	TransferKinInfoVector tk = null;

	boolean inited = false;

	public void init() {
		inited=true;
		reset();
	}

	public void reset() {
		tk = null;
		getTk();
		setStage(-1);
		serialNumber++;
	}


	void getTk() {
		if (tk == null) {
Debug.prout(4,"getTk");

			tk = (TransferKinInfoVector) theFrame.kinshipTermsPanel1.getTransferKinInfo();
		}
	}
	
	public void setupUndo() {
		
	}

	public void doUndo() {
		
	}
	
	// -------------------------------
	long serialNumber=0;
	int stage=-1;
	//InstructionSet instructions = null;
	InstructionSet instructions = initInstructions();

	final static int ZERO = 0;

	// --------------------------------------

	static java.util.Hashtable operationMenus=new java.util.Hashtable(10);

	static InstructionSet getInstructionSet(String name) {
		return (InstructionSet) operationMenus.get(name);
	}

	Instruction getMenuItemInstruction(String menuName, int opcode) {
		InstructionSet ins = getInstructionSet(menuName);
		//Debug.prout(0," iset "+ins+" code "+opcode);
		if (ins == null) return null;
		for(ins.reset();ins.isNext();) {
			Instruction inq = (Instruction) ins.getNext();
		//Debug.prout(0," iset "+inq+" code "+inq.getOpcode());
			if (inq.getOpcode() == opcode) return inq;
		}
		return null;
	}

	void enableInstructionInSet(String menuName, int opcode, boolean
            enableState) {
		Instruction inq = getMenuItemInstruction(menuName,opcode);
		//Debug.prout(0," menuName "+menuName+" opcode "+opcode+" inq "+inq+" item "+inq.getTheMenuItem());
		if (inq == null) return;
		inq.getTheMenuItem().setEnabled(enableState);
	}

	/** Override in subclasses
	*/
	public InstructionSet initInstructions() {
		InstructionSet ins = new InstructionSet("Menu Title");
       	ins.create("Menu Item",ZERO);// .m() = multiple hits, .d(int dependson), .e(int exclusiveWith);
       	return ins;
	}

	public void resetInstructionMenuStates() {
	   if (getInstructions() != null)
		  getInstructions().enableInstructions();
	}

	public InstructionSet getInstructions() {
    	return instructions;
    }

	// -------------------------------

	public KintermFrame getTheFrame() {
	    return theFrame;
	}

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
	   waitCursor();
		if (!inited) init();
		try {
			boolean flag = execOpcode(opcode);
		   arrowCursor();
			return (flag);
		} catch (Exception e) {
		   arrowCursor();
			e.printStackTrace();
			return false;
		}
	}

	public boolean exec(int [] opcodes) {
		boolean flag;
		//if (!inited) init();
		setupUndo();
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

	void setMenuState(String itemName, boolean stateFlag, boolean enabledFlag){
		//InstructionSet ins = getInstructions();
		int opcode = getInstructions().getOpcode(itemName);
		//Instruction inst = ins.getInstruction(itemName);
		CheckboxMenuItem m = getInstructions().getInstruction(itemName).getTheMenuItem();
	//	System.out.println("in setMenuState opcode "+opcode);
		try {
			// test to see if m is actually checkbox rather than use exception
			//CheckboxMenuItem m =(CheckboxMenuItem) theMenu.getItem(i);
			//String iName = m.getLabel();
			//InstructionSet ins = getInstructions()
		//	System.out.println("in  for setMenuState opcode "+ins.getOpcode(m.getLabel())+" label "+m.getLabel());
				m.setState(stateFlag);
				m.setEnabled(enabledFlag); // make adjustments for dependencies in future
				//System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx opcode "+opcode);
		} catch (Exception e) {
			//	Debug.prout(4,"_+_+_+_+_+_ Bops.itemStateChanged() - Class = "+theMenu.getItem(i).getClass().toString());
		}
			
	}
	
	class SymAction  implements java.awt.event.ItemListener
	{

		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Debug.prout(4,"Action event performed");
		}

		
		boolean itemLock = false;
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			if (itemLock) return;
			theFrame.setCurrentBuiltins((BuiltinOperations) Bops.this);

			CheckboxMenuItem em = (CheckboxMenuItem) event.getSource();
			String itemName = em.getLabel();
			InstructionSet ins = getInstructions();
			Instruction inst = ins.getInstruction(itemName);
			if (!inst.isMultipleHits()) {
				em.setEnabled(false);
			//	itemLock = true;
			//	if (!em.getState()) em.setState(true);
			//	itemLock = false;
			}

			int opcode = ins.getOpcode(itemName);
			Debug.prout(4,"!+!+!+Executing "+itemName+" opcode="+opcode);
			Debug.prout(4,"++++++++++++++++++++++++++ready to do stuff in execOpcode opcode"+opcode);
			boolean st = execOpcode(opcode);
			Debug.prout(4,"!+!+!+Executed2 "+itemName+" opcode="+opcode+" state="+st);
			
			/*if (st == false) {//original code; resets all menu items; see same procedure in Tops
				for(int i=0;i<theMenu.getItemCount();i++) {
					try {
                    // test to see if m is actually checkbox rather than use exception
						CheckboxMenuItem m =(CheckboxMenuItem) theMenu.getItem(i);
						m.setState(false);
						m.setEnabled(true); // make adjustments for dependencies in future
					} catch (Exception e) {
					//	Debug.prout(4,"_+_+_+_+_+_ Bops.itemStateChanged() - Class = "+theMenu.getItem(i).getClass().toString());
					}

				}
			} else {
				// aops2Ins.disableExclusiveWithInstructions(aops2Ins.getInstruction(itemName));
			}*/
			
			if (st == false) {//modified code; only resets current menu item
				try {
					// test to see if m is actually checkbox rather than use exception
					//CheckboxMenuItem m =(CheckboxMenuItem) theMenu.getItem(i);
					em.setState(false);
					em.setEnabled(true); // make adjustments for dependencies in future
				} catch (Exception e) {
					//	Debug.prout(4,"_+_+_+_+_+_ Bops.itemStateChanged() - Class = "+theMenu.getItem(i).getClass().toString());
				}
					
			}				
			else {
				// aops2Ins.disableExclusiveWithInstructions(aops2Ins.getInstruction(itemName));
			}
			
			ins.enableInstructions();
			System.out.println("doing Instructions");
			// what to do if true/false
		}
	}


	void resetInstructionInSet(Instruction inq, boolean
							 enableState, boolean checkState) {
	   if (inq == null) return;
	   inq.getTheMenuItem().setEnabled(enableState);
	   inq.getTheMenuItem().setState(checkState);
	}

	void resetInstructionMenu() {
	   if (instructions == null) initInstructions();
	   InstructionSet ins = instructions;
	   String menuName = ins.getTitle();

	   for(ins.reset();ins.isNext();) {
		  Instruction inq = (Instruction) ins.getNext();
		  if (ins.getCurrentName() == "") {
			// theMenu.addSeparator();
		  }
		  else if (inq.getOpcode() == -1) {  //submenu
			inq = (Instruction) ins.getNext();
			 while (inq.getOpcode()!= -1) {
				resetInstructionInSet(inq,true,false);
				inq = (Instruction) ins.getNext();
			 }
		  }
		  else {
			 resetInstructionInSet(inq,true,false);
		  }

	   }
	   /*
		  Menu aMenu = new Menu ("Edit nonsense");
		for(ins.reset();ins.isNext();) {
		   Instruction inq = (Instruction) ins.getNext();
		   inq.setTheMenuItem(xMenuItem = new java.awt.CheckboxMenuItem(ins.getCurrentName()));
		   xMenuItem.setState(false); // shows instruction last executed
		   aMenu.add(xMenuItem);
		   xMenuItem.addItemListener(aopsListener);
		}

		theMenu.add(aMenu);
		*/
	   /*		ins.reset();
	   Menu aMenu = new Menu("Edit");
	   Instruction inq = (Instruction) ins.getNext();

			    inq.setTheMenuItem(item = new java.awt.CheckboxMenuItem(ins.getCurrentName()));
			    item.setState(false); // shows instruction last executed

	   item.addItemListener((ItemListener) aopsListener);
	   aMenu.add(item);
	   */


	   ins.enableInstructions();
	   //	mainmenuBar.add(extraMenus[AOPS2MENU]);
	}

	void insertInstructionMenu() {
		SymAction opsListener = new SymAction();
		CheckboxMenuItem xMenuItem,item;
		if (instructions == null) initInstructions();
		InstructionSet ins = instructions;
		operationMenus.put(ins.getTitle(),instructions);

		theMenu = new java.awt.Menu(ins.getTitle());

		for(ins.reset();ins.isNext();) {
			Instruction inq = (Instruction) ins.getNext();
			if (ins.getCurrentName() == "") {
			    theMenu.addSeparator();
			}
			else if (inq.getOpcode() == -1) {  //submenu
			    String theName = ins.getCurrentName();
			    Menu aMenu = new Menu(ins.getCurrentName());
			    inq = (Instruction) ins.getNext();
			    while (inq.getOpcode()!= -1) {
			        inq.setTheMenuItem(xMenuItem = new java.awt.CheckboxMenuItem(ins.getCurrentName()));
			        xMenuItem.setState(false); // shows instruction last executed
			        aMenu.add(xMenuItem);
			        xMenuItem.addItemListener(opsListener);
			        inq = (Instruction) ins.getNext();
			    }
			    theMenu.add(aMenu);
			}
			else {
			    inq.setTheMenuItem(xMenuItem = new java.awt.CheckboxMenuItem(ins.getCurrentName()));
			    xMenuItem.setState(false); // shows instruction last executed
			    theMenu.add(xMenuItem);
			    xMenuItem.addItemListener(opsListener);
			}
		}

		ins.enableInstructions();

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

	public void populateDialoguePanel() {
		populateDialoguePanel("");
	}

	public void populateDialoguePanel(String s) {
	    if (Mode.getMode() == Mode.AUTOMATIC)
		    theFrame.dialogueTextArea.append(XFile.Eol+s);
		else
		    theFrame.dialogueTextArea.setText(s);
	}

	public void populateDialoguePanel(String s, StringVector v) {
	    populateDialoguePanel(s);
        boolean flag = false;
		for(v.reset();v.isNext();)
		    if (flag)
			    theFrame.dialogueTextArea.append(", "+ v.getNext().replace('[', ' ').replace(']',' ').trim());
			else {
			    flag = true;
			    theFrame.dialogueTextArea.append(v.getNext().replace('[', ' ').replace(']',' ').trim());
			}
		theFrame.dialogueTextArea.append(".");
	}

	public void populateDialoguePanel(String s, StringVector v,String t) {
	    populateDialoguePanel(s,v);
	    int n = theFrame.dialogueTextArea.getSelectionEnd();
	    theFrame.dialogueTextArea.replaceRange("",n-1,n);
		if (t.substring(0,1).equals(".") || t.substring(0,1).equals(",") || t.substring(0,1).equals(":")) 
			theFrame.dialogueTextArea.append(t);
		else theFrame.dialogueTextArea.append(" "+t);
	}

	public void populateDialoguePanel(StringVector v) {
	    populateDialoguePanel("");
        boolean flag = false;
		for(v.reset();v.isNext();)
		    if (flag)
			    theFrame.dialogueTextArea.append(", "+ v.getNext().replace('[', ' ').replace(']',' ').trim());
			else {
			    flag = true;
			    theFrame.dialogueTextArea.append(v.getNext().replace('[', ' ').replace(']',' ').trim());
			}
		theFrame.dialogueTextArea.append(".");
	}
}

