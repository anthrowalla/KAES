import java.util.*;

/* History
* 3/10 DR added filter fProdSexComplement to get the complement of a given sex
*/

class TransferProductsVector extends Vector {

	public TransferProduct getSymbol(int index) {
		return (TransferProduct) elementAt(index);
	}
	
	int index = -1;
	
	public String toXML() {
		StringBuffer sbuf = new StringBuffer(100);
		sbuf.append("        <Products>"+XFile.Eol);
		for(reset();isNext();) {
			sbuf.append(getNext().toXML());
		}
		sbuf.append("        </Products>"+XFile.Eol);
		return sbuf.toString();
	}
	
	public void reset() {
		index = -1;
	}
	
	public boolean isNext() {
		return (index+1 < size());
	}
	
	public TransferProduct getNext() {
		if (isNext())
			return (TransferProduct) elementAt(++index);
		else return null;
	}	

	public synchronized Object clone(boolean deep) {
		if (deep) {
			TransferProductsVector t = new TransferProductsVector();
			for(reset();isNext();) {
				t.addElement(getNext().clone());
			}
			return t;
		} else return this.clone();
	}
	
	public void replace(TransferProduct k) {
		setElementAt(k,index);
	}
	
	public void clearProducts() {
		for(reset();isNext();) {
			TransferProduct k = getNext();
			String g = k.getGenerator();
			k.removeAllElements();
			k.setGenerator(g);
		}
	}
		
	public TransferProductsVector fSex(String tsex, TransferKinInfoVector top) {
		for(reset();isNext();) {
			TransferProduct k = getNext();
			TransferKinInfo m = top.lookupTerm(k.getGenerator());
			if (m == null) {
				String g = k.getGenerator();
				k.removeAllElements();
				k.setGenerator(g);
			} else {
				String gsex = m.sex;
				if (tsex.indexOf(gsex) == -1) {
					String g = k.getGenerator();
					k.removeAllElements();
					k.setGenerator(g);
				}
			}
		}
		return this;
	}
	
	public TransferProductsVector fProdSex(String tsex, TransferKinInfoVector top) {
		for(reset();isNext();) {
			TransferProduct k = getNext();
			for (k.reset();k.isNext();) {
				TransferKinInfo m = top.lookupTerm(k.getNext());
				if (m == null) {
					k.delete();
				} else {
					String ksex = m.sex;
					if (tsex.indexOf(ksex) == -1) {
						k.delete();
					}
				}
			}
		}
		return this;
	}

	public TransferProductsVector fProdSexComplement(String tsex, TransferKinInfoVector top) {
		for(reset();isNext();) {
			TransferProduct k = getNext();
			for (k.reset();k.isNext();) {
				TransferKinInfo m = top.lookupTerm(k.getNext());
				if (m == null) {
					k.delete();
				} else {
					String ksex = m.sex;
					if (tsex.indexOf(ksex) != -1) {
						k.delete();
					}
				}
			}
		}
		return this;
	}
	
	public TransferProductsVector fArrow(int arrow, TransferKinInfoVector top) {
		for(reset();isNext();) {
			TransferProduct k = getNext();
			TransferKinInfo m = top.lookupTerm(k.getGenerator());
			if (m == null){
					String g = k.getGenerator();
					k.removeAllElements();
					k.setGenerator(g);
			} else { 
				int garrow = m.orientation;
				if (!(garrow == arrow)) {
					String g = k.getGenerator();
					k.removeAllElements();
					k.setGenerator(g);
				}
			}
		}
		return this;
	}

	public int countActiveGenerators() {
		int n = 0;
		for(reset();isNext();) {
			if (getNext().size() > 1) n++;
		}
		return n;
	}


	public boolean logicallyEquivalentTo(TransferProductsVector to) {
	   for(reset(), to.reset();isNext() && to.isNext();) {
		  if (!to.getNext().logicallyEquivalentTo(getNext())) return false;
	   }
	   return true;
	}

	public boolean equivalentTo(TransferProductsVector to) {
		for(reset(), to.reset();isNext() && to.isNext();) {
			if (!to.getNext().equivalentTo(getNext())) return false;
		}
		return true;
	}

	public TransferProduct getGenerator(String gen) {
		TransferProduct ret = null;
		int i;
		for(reset();isNext();) {
			if (( ret = getNext()).getGenerator().equals(gen)) return ret;
		}
		ret = new TransferProduct();
		ret.setGenerator(gen);
		return ret;
	}

	public void deleteGenerator(String gen) {
		for(reset();isNext();) {
			//if (getNext().getGenerator().equals(gen)) delete();
			if (getNext().getGenerator().equals(gen)) delete();
		}
	}

	public void delete() {
		removeElementAt(index);
		index--;
	}

}
