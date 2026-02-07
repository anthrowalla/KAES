/* History
* 11/21 DR added procedure splitEquivalentPaths()
*/

public class AlgebraPathVector extends ListVector {

	public AlgebraPath getNextSymbol() {
		return (AlgebraPath) getNext();
	}

	public AlgebraPath getSymbol(int i) {
		return (AlgebraPath) elementAt(i);
	}
	
	public boolean addUnique(AlgebraPath p) {
		if (indexOf(p) == -1) {addElement(p);
		return true;}
		else return false;
	}

   public boolean addUniqueFullpath(AlgebraPath p) {
	  for (int i=0;i< size();i++) {
		 if (((AlgebraPath) elementAt(i)).equalsFull(p)) {
			return false;
		 }
	  }
	  addElement(p);
	  return true;
   }
   
	public AlgebraPathVector splitEquivalentPaths() {
	    AlgebraPathVector ret = new AlgebraPathVector();
	    for (reset();isNext();){
	        AlgebraPath ap = (AlgebraPath) getNext();
	        if (ap.reducedPath.equivalentProduct()) {
	           // AlgebraPath ap1 = ap.getEquivalentPathLeft();//dwr 8/5
	            AlgebraPath ap1 = ap.getReducedEquivalentPathLeft();//dwr 8/5
	            if (ret.indexOf(ap1) == -1) ret.addElement(ap1);
	            //ap1 = ap.getEquivalentPathRight();//dwr 8/5
	            ap1 = ap.getReducedEquivalentPathRight();//dwr 8/5
	            if (ret.indexOf(ap1) == -1) ret.addElement(ap1);
	        } else ret.addElement(ap);
	    }
	    return ret;
	} 

}