import java.awt.Rectangle;

/** An information structure used by KinTermMap
*/
class KinTermInfo {
	/** kin term associated with this information structure
	*/
	KinTerm term = null;
	/** is this a generator
	*/
	boolean isGenerator=false;
	/** orientation if it is a generator
	*/
	int orientation=-1;
	/** screen bounds for this kin term instance in a kin term map
	*/
	Rectangle bounds = new Rectangle(0,0,10,10);

	/** list of  generators in parallel with  kin terms
	*/
	KinTermVector generator = new KinTermVector();
	/** list of kin terms that have a mapping from this kin term
	*/
	KinTermVector toTerm = new KinTermVector();

	/** list of unique generators for this kin term
	*/
	KinTermVector generators = new KinTermVector();
	/** number of unique generators for this kin term
	*/
	int ngen = 0;

	/** create information structure for a kin term in a specific kin term map
	* @param term the kin term associate with this KTInfo structure
	*/
	public KinTermInfo(KinTerm term) {
		this.term = term;
	}
	/** create information structure for a kin term in a specific kin term map
	* @param term the kin term associate with this KTInfo structure
	*/
	public KinTermInfo(String term, String sex) {
		this.term = new KinTerm(term,sex);
		
	}
	
	/** get kin terms associated with generator
	* @param gen generator
	* @return KinTermVector of assoicated kin terms
	*/
	public KinTermVector mapGenToTerms(KinTerm gen) {
		int i=0;
		KinTermVector kv = new KinTermVector();
		while ((i = generator.indexOf(gen,i) ) != -1) {
			kv.addElement(toTerm.elementAt(i));
			if (i < kv.size() - 1) i++;
		}
		return kv;
	}
	
	/** get generator associated with kin term
	* @param term kin term
	* @return generator associated with kin term
	*/
	public KinTerm mapTermToGen(KinTerm term) {
		int i;
		if ((i = toTerm.indexOf(term) ) != -1) return  generator.getKinTerm(i);
		else return null;
	}
	
	/** add or modify generator-term mapping
	* if neither exists add the pair
	* if generator exists and kin term exists throw exception unless they are already assoicated
	* if generator exists and kin term does not change the kin term for generator
	* if kin term exists and generator does not change the generator for kin term
	* @param gen generating term
	* @param newterm term mapping for generator relative to this kin term
	*/
	public void addGenTerm(KinTerm gen, KinTerm newterm) {
		int i = generator.indexOf(gen);
		int j = toTerm.indexOf(newterm);
		
		if (i != -1) {
			if (j != -1) {
				if (i != j) {
					generator.addElement(gen);
					toTerm.addElement(newterm);
				} 
			} else toTerm.setElementAt(newterm,i);
		} else {
			if (j != -1) {
				if (mapGenToTerms(generator.getKinTerm(j)).size() == 1) {
					generators.removeElement(generator.elementAt(j));
					ngen--;
				}
				generator.setElementAt(gen,j);
				ngen++;
				generators.addElement(gen);
			} else {
				generator.addElement(gen);
				toTerm.addElement(newterm);
				generators.addElement(gen);
				ngen++;
			}
		}
	}
	/** remove generator-term mapping 
	* removes from list of unique generators if only instance of generator associated with kin term
	* @param kinterm kin term
	*/
	public void removeKinTerm(KinTerm kinterm) throws Exception {
		int i = toTerm.indexOf(kinterm);
		
		if (i != -1) {
			if (mapGenToTerms(generator.getKinTerm(i)).size() == 1) {
				ngen--;
				generators.removeElement(generator.elementAt(i));
			}
			generator.removeElement(generator.elementAt(i));
			toTerm.removeElement(kinterm);
		}
	}
	
	
}

