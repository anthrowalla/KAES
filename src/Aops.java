/* Change history
* 3/7 MF	Commentted out references to Algebra.popCurrent() which are no
*			longer required
* 10/6 DR added procedure resetTransferKinInfo() to update to current tk
*/
public class Aops extends Bops {
	ListVector theKinVectors = new ListVector();

	KinTermMap km = null;
	KinshipAlgebra ka = null;
	Algebra theAlgebra = null;

	public Aops() {
		theFrame = null;
	}

	public Aops(KintermFrame a) {
		theFrame=a;
	}

	public void resetAlgebra() {//not used
		tk = null;
		ka = null;
		// Algebra.popCurrent(); // pop and replace in reset
		getTk();
		getAlg();
		theFrame.setFrameAlgebra(Algebra.getCurrent());
		AKMStack.removeAllElements();
		setStage(-1);
		reset();
	}

	public void init() {// return to baseline Algebra, throw away saved algebra/contexts
		tk = null;
		ka = null;
		getTk();
		getAlg();
		// Algebra.popCurrent(); // pop and replace in reset
		AKMStack.removeAllElements();
		inited=true;
		reset();
	}

//	boolean inited = false;

	public void reset() {
		Debug.prout(4,"+++++++RESET");
		if (!inited) {
			inited = true;
			init();
			return;
		}
		if (instructions == null) initInstructions();
		setTheAlgebraFromFrame();
	//	Algebra.popCurrentx();
	//	theAlgebra = theFrame.getFrameAlgebra();
	//	Algebra.pushCurrent(theAlgebra);
		ka.makeAlg.theAlgebra = theAlgebra;
	//	tk = firstStageAlgebraOperations.tk;
	//	ka = firstStageAlgebraOperations.ka;
	//	km = firstStageAlgebraOperations.km;
	//	popAKM();
	//	pushAKM();
		//Debug.prout(4," setstage2");
		//setStage(-1);//Remove 11/9  problem with going to basegen w/o map op
		serialNumber++;
	}

        public void resetTransferKinInfo() {
	    tk = null;
	    getTk();
	    if (ka != null)
		ka.setKv(tk);
	  //  if (km != null)
	//	km.setTheKinTermsX(tk);
	    if (km != null){
	        //km = new KinTermMap(tk);
		km = new KinTermMap((TransferKinInfoVector) tk.clone(true));
	        Algebra.pushCurrent(theAlgebra);
	    }
        }


	public void setTheAlgebraFromFrame() {
	//	Algebra.popCurrent(); // pop and replace
		theAlgebra = theFrame.getFrameAlgebra();
		Algebra.pushCurrent(theAlgebra);
	}
//-------------- Utilities



	void getAlg() {
		if (ka == null) {
			getTk();
			km = new KinTermMap((TransferKinInfoVector) tk.clone(true));
			ka = km.theKinshipAlgebra;
			//theAlgebra = ka.makeAlg.theAlgebra; // maybe make Algebra.currentAlgebra()

		}
	}
/*
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
*/

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

	void makeNewPanel() {
		theFrame.makeNewPanel(tk);
	}

	class AlgebraKMData {
		Algebra algebra;
		KinTermMap kinterms;

		public AlgebraKMData(Algebra a, KinTermMap k) {
			algebra = a;
			kinterms = k;
		}
	}

	ListVector AKMStack = new ListVector();

	public void pushAKM() { // should we be setting frame algebra here??
		if (theAlgebra == null || km == null) return;
		AKMStack.addElement(new AlgebraKMData((Algebra) theAlgebra.clone(),
		(KinTermMap) km.clone()));
	}

	public AlgebraKMData popAKM() {
		AlgebraKMData ak;
		if (AKMStack.size() == 0) ak=null;
		else {
			ak = (AlgebraKMData) AKMStack.elementAt(AKMStack.size()-1);
		//Algebra.popCurrentx();
		//Algebra.pushCurrentx(ak.algebra);
		//theAlgebra = Algebra.getCurrentx();
			theFrame.setFrameAlgebra(ak.algebra);
			km = ak.kinterms;
			ka = km.theKinshipAlgebra;
			AKMStack.removeElementAt(AKMStack.size()-1);
			reset();
		}
		return ak;
	}
}
