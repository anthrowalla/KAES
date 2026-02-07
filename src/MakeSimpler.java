public class MakeSimpler extends Operation {
	static MakeSimpler me = new MakeSimpler();
	
	public MakeSimpler() {
		name = "MakeSimpler";
	}
	
	public String run(KintermFrame f) {
		TransferKinInfoVector tk = (TransferKinInfoVector) f.kinshipTermsPanel1.getTransferKinInfo().clone(true);
		int len = tk.size();
		tk = noAffines(tk);
		f.makeNewPanel(tk);
		return "";
	}
	
	TransferKinInfoVector noAffines(TransferKinInfoVector tk) {
		int [] arrows = {tk.UP,tk.DOWN,tk.RIGHT,tk.LEFT};
		StringVector ftv = tk.getFocalTerms();
		return tk.buildSet(ftv,arrows);
	}

}