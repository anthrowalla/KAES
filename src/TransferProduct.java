import java.util.*;

class TransferProduct extends Vector {

	int index = 0;
	public String getGenerator() {
		return (String) elementAt(0);
	}

	public void setGenerator(String gen) {
		if (size() == 0) {
			addElement(gen);
		} else {
			setElementAt(gen,0);
		}
	}
	
	public String toXML() {
		StringBuffer sbuf = new StringBuffer(100);
		sbuf.append("            <Generator Name="+'"'+getGenerator()+'"'+">"+XFile.Eol);
		for(reset();isNext();) {
			sbuf.append("                <Product>"+getNext()+"</Product>"+XFile.Eol);
		}
		sbuf.append("            </Generator>"+XFile.Eol);
		return sbuf.toString();
	}
	
	public void reset() {
		index = 0;
	}
	
	public boolean isNext() {
		return (index+1 < size());
	}

	public String getNext() {
		if (isNext())
			return (String) elementAt(++index);
		else return null;
	}
	
	public String getSymbol(int i) {
		index = i+1;
		return (String) elementAt(i+1);
	}
	
	public int getSize() {
		return size()-1;
	}
	
	public void delete() {
		removeElementAt(index);
		index--;
	}

	public TransferProduct fUnion(TransferProduct union) {
		String ugen = union.getGenerator();
		String kgen = this.getGenerator();
		/*if (!kgen.equals(ugen)) Debug.prout(4,"in TransferKinInfo:fUnion ... generator mismatch");
		else*/ for(union.reset(); union.isNext();) {
			String up = union.getNext();
			if (indexOf(up,1) == -1) addElement(up);
		}
		return this;
	}

	public TransferProduct fDifference(TransferProduct union) {
		int index;
		String ugen = union.getGenerator();
		String kgen = this.getGenerator();
		if (!kgen.equals(ugen)) Debug.prout(4,"in TransferKinInfo:fDifference ... generator mismatch"); // *** diag
		else for(union.reset(); union.isNext();) {
			String up = union.getNext();
			if ((index = indexOf(up,1)) != -1) delete();
		}
		return this;
	}

	public TransferProduct fIntersection(TransferProduct union) {
		String ugen = union.getGenerator();
		String kgen = this.getGenerator();
		if (!kgen.equals(ugen)) Debug.prout(4,"in TransferKinInfo:fIntersection ... generator mismatch");
		else {
			for(reset(); isNext();) {
				String up = getNext();
				if ((union.indexOf(up,1)) == -1) delete();
			}
		}
		return this;
	}
	
	public TransferProduct fMerge(TransferProduct unity, Vector names) {
		String ugen = unity.getGenerator();
		String kgen = this.getGenerator();
		if (!kgen.equals(ugen)) Debug.prout(4,"in TransferKinInfo:fMerge... generator mismatch");
		else {
			for(int i = 0;i < names.size();i++) {
				String n = (String) names.elementAt(i);
				// Debug.prout(4,"fMerge: generator="+kgen+" name="+n);
				if (unity.indexOf(n,1) != -1) {
					if (indexOf(n,1) == -1) {
						addElement(n);
						// Debug.prout(4,"fMerge: generator="+kgen+": Added="+n);
					}
					//else Debug.prout(4,"         fMerge: generator="+kgen+": in original!!!="+n);
				} // else Debug.prout(4,"                  fMerge: wasn't in unity="+n);
			}
		}
		return this;
	}

	public TransferProduct fRemove(Vector names) {
		String kgen = this.getGenerator();
		if (names.indexOf(kgen) != -1) {
			removeAllElements();
			addElement(kgen);
			return this;
		}
		for(reset();isNext();) {
			if (names.indexOf(getNext()) != -1) delete();
		}
		return this;
	}
	
	public TransferProduct fRemove(TransferProduct names) {
		String kgen = this.getGenerator();
		for(reset();isNext();) {
			if (names.indexOf(getNext()) != -1) delete();
		}
		return this;
	}

	/** is a product equivalent with respect to governing generator
    **/
	public boolean equivalentTo(TransferProduct to) {
		if (!getGenerator().equals(to.getGenerator()))  return false;
		if (size() != to.size()) return false;
		if (size() == 0 && to.size() == 0) return true;
		for(reset();isNext();) {
			if (to.indexOf(getNext()) == -1) return false;
		}
		return true;
	}
	
	/** is a product logically equivalent with respect to governing generator
	   e.g. is product list identical
	   **/
	public boolean logicallyEquivalentTo(TransferProduct to) {
	 //  if (!getGenerator().equals(to.getGenerator()))  return false;
	   // possibly check orientation ... though if product list is same, who cares? Deal with null case though
	   if (size() != to.size()) return false;
	   if (size() == 1 && to.size() == 1) return false; // Only generators in string ... null products
		 /* if (to.getGenerator().orientation == getGenerator().orientation) return true; else return false;*/
		  reset();
	   for(getNext();isNext();) {
		  if (to.indexOf(getNext()) == -1) return false;
	   }
	   return true;
	}	
}
