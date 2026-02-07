public class Mops extends Bops {

	ListVector theKinVectors = new ListVector();
	// KintermFrame theFrame=null;
	// TransferKinInfoVector tk = null;

    public Mops() {
        theFrame = null;
    }

    public Mops(KintermFrame a) {
        theFrame=a;
    }

	public void reset() {
		tk = null;
		getTk();
		setStage(-1);
	}

	TransferKinInfoVector pop() {
		if (theKinVectors.size() != 0) {
			tk = (TransferKinInfoVector) theKinVectors.lastElement();
			theKinVectors.removeElementAt(theKinVectors.size()-1);
			return tk;
		} else {
			tk = null;
			getTk();
			return null;
		}
	}

	void push() {
		theKinVectors.addElement(tk);
		tk = (TransferKinInfoVector) tk.clone(true);
	}


	void makeNewPanel() {
		theFrame.makeNewPanel(tk);
	}

//-------------- Utilities

	public void setMyOpcodes(int[] myOpcodes) {
		this.myOpcodes = myOpcodes;

	}

	public int[] getMyOpcodes() {
		return myOpcodes;
	}

	public void setMyOpcodes(int a, int b) {
		this.myOpcodes[a] = b;
	}

	public int getMyOpcodes(int a) {
		return this.myOpcodes[a];
	}

}
