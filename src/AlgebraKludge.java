    /**
     * Description
     * @param param description
     * @return
     * @see x#y
     */

    /**
     * 
     * @param
     * @return
     * @see
     */

import java.util.Vector;

/**
 * A structure shared by all related AlgebraSymbols
 * Provides some global glue
 *
 * @version 	1.0 12/10/99
 * @author 	Michael D. Fischer & Dwight W. Read
 */

public class AlgebraKludge {
	/** private variable */
	InstantiationEquivVector equivs = new InstantiationEquivVector();
	
    /**
    * Kludge for AKT !!!!!!! genearlise -
    *    stores last Component in global structure to do gender test
    */
	public AlgebraSymbol lastComponent=null;

    /**
     * preprocess a path in a string to an unnormalised AlgebraSymbolVector.
     * @param spath a string of algebraic path elements
     * @return unnormalised list of AlgebraSymbols
     * @see Algebra
     */
	public AlgebraSymbolVector preprocess(String spath) {
		AlgebraSymbolVector av=new AlgebraSymbolVector();
		InstantiationEquivalence a;
		int ndx = 0, sp=0;
		boolean found=false;
		spath = spath.trim()+" ";
		Debug.prout("entering preprocess");
		while ((ndx = spath.indexOf(" ",sp)) != -1) {
			String r = spath.substring(sp,ndx);
			sp = ndx+1;
			equivs.reset();
			found = false;
			while ((a = equivs.nextEquivalance()) != null) {
				if (a.getKinType().equals(r)) {
					av.addToEnd(a.getNormKinTerm()); 
					found = true;
					Debug.prout("found "+av.toString());
					break;
				}
				
			}
			if (!found) {
				Debug.prout("in AlgebraKludge.preprocess - Couldn't find equivalence for"+r);
				return null;
			}
			lastComponent = a.getKinTerm(); // Kludge for AKT .. store last component for gender test
		}
		Debug.prout("leaving preprocess");
		return av;
	}

    /**
     * Produce a normalised AlgebraSymbolVecto from an unnormalised one.
     * @param av a list of algebraic path elements
     * @return normalised list of AlgebraSymbols
     */
	public AlgebraSymbolVector process(AlgebraSymbolVector av) {
		AlgebraPath ap = new AlgebraPath();
	
		ap.reducePathLR(av);
		return ap.getReducedProductPath();
	}
	
	/**
	* Ideosyncratic for AKT ... generalise later
	* adds prep to end of av - handles sex marked terms
	* @param av 
	* @param prep 
	* @return result suitable for translating into lexical terms
	*/
	public Vector postprocess(AlgebraSymbolVector av, AlgebraSymbol prep) { 
		java.util.Vector result = new Vector(2,1);
		AlgebraSymbolVector ay = av.copy();
		ay.addToEnd(prep);
		AlgebraPath ap = new AlgebraPath();
	
		if (ap.reducePathLR(ay)) {
			result = nfurcate(av);
		} else {
			ay = av.copy();
			ay = reciprocal(ay);
			Debug.prout("ay="+ay.toString());
			ay.addToEnd(prep);
			Debug.prout("ay="+ay.toString());
			if (ap.reducePathLR(ay)) {
				result = nfurcate(av);
			} else {
				result.addElement(av);
			}
		}
		return result;
	}

	/**
	* Ideosyncratic for AKT ... generalise later
	* bifurcates based on sex marked terms
	* uses the fearsome lastComponent
	* @param av list of algebra terms
	* @return result suitable for translating AlgebraSymbols into lexical terms
	* @see #lastComponent
	*/
	
	Vector nfurcate(AlgebraSymbolVector av) {
		java.util.Vector result = new Vector(2,1);
		AlgebraSymbolVector k;
		
		equivs.reset();
		InstantiationEquivalence a;
		while ((a = equivs.nextEquivalance()) != null) {
			if (a.getNormKinTerm().equals(av.getLast())) {
				
				k = av.copy();
				k.removeEnd();
				k.addToEnd(a.getKinTerm());
				result.addElement(k);
				
				if (a.getKinTerm().getSex().equals(lastComponent.getSex())) {
					break;
				}
			}
		}
		return result;
	}
	
	/** Decomposes an AlgebraSymbol into its feature components
	*	@param a The AlgebraSymbol to decompose
	*	@return array[2] of components 0=name 1=sex
	*	We must extend this routine in future to accommode more variables
	*/
	
	public String [] algebraSymbolToComponents(AlgebraSymbol a) {
		String [] ret = new String[2];
		ret[1] = a.getSex();
//		Debug.prout(4,"algebraSYmtolToComponents a = "+a);
		
		if (a.isGenerator()) {
			ret[0] = a.getValue();
		} else if (a.getValue().equals ("0")) {
			ret[0] = a.getValue();
		} else {
			ret[0] = getNorm(a).getValue();
		}
		return ret;
	}
	
	AlgebraSymbolVector reciprocal(AlgebraSymbolVector p) {
		AlgebraSymbolVector recip = new AlgebraSymbolVector();
		int sz = p.size();
		for (int i=0;i<sz;i++) {
			recip.addToBeginning(p.getSymbol(i).getReciprocal());
		}
		return recip;
	}
	
	public InstantiationEquivalence addEquivalence(String kinType, AlgebraSymbol kinTerm, AlgebraSymbol normKinTerm) {
		InstantiationEquivalence a = new InstantiationEquivalence(kinType,kinTerm,normKinTerm);
		equivs.addElement(a);
		return a;
	}
	
	public AlgebraSymbol getNorm(AlgebraSymbol kint) {
		return equivs.getNorm(kint);
	}
	
	public Object clone() {
		AlgebraKludge k = new AlgebraKludge();
		k.equivs = (InstantiationEquivVector) equivs.clone();
		k.lastComponent = lastComponent;
		
		return k;
	}
}

class InstantiationEquivalence {
	String kinType;
	AlgebraSymbol kinTerm;
	AlgebraSymbol normKinTerm;
	
	public InstantiationEquivalence(String kinType,
		AlgebraSymbol kinTerm, AlgebraSymbol normKinTerm) {
		this.kinType = kinType;
		this.kinTerm = kinTerm;
		this.normKinTerm = normKinTerm;
	}
	public String getKinType() {
		return kinType;
	}
	public AlgebraSymbol getKinTerm() {
		return kinTerm;
	}
	
	public void setKinType(String f) {
		kinType=f;
	}
	public void setKinTerm(AlgebraSymbol t) {
		kinTerm=t;
	}
	public AlgebraSymbol getNormKinTerm() {
		return normKinTerm;
	}
	
	public void setNormKinTerm(AlgebraSymbol f) {
		normKinTerm=f;
	}
}

/**
 * An extended Vector that simplifies comparing equivalences
 *
 * @version 	1.0 12/10/99
 * @author 	Michael D. Fischer & Dwight W. Read
 */

class InstantiationEquivVector extends java.util.Vector{
	int ndx=0;
	
	/** reset index to beginning of Vector
	*/
	public void reset() {
		ndx = 0;
	}

	/** @return next equivalence
	*/
	
	public InstantiationEquivalence nextEquivalance() {
		return getEquivalance(ndx++);
	}
	
	/** @return current equivalence
	*/
	public InstantiationEquivalence currentEquivalance() {
		return getEquivalance(ndx-1);
	}

	/** Sets current position to this index.
	* @return indexed equivalence
	* @param i index of equivalenceto get.
	*/
	public InstantiationEquivalence getEquivalance(int i) {
		if (i < size()) {
			ndx = i;
			return (InstantiationEquivalence) elementAt(ndx++);
		} else return null;
	}

	/** Normalise an AlgebraSymbol
	* @return normalised AlgebraSymbol
	* @param kint the symbol to normalise
	*/
	
	public AlgebraSymbol getNorm(AlgebraSymbol kint) {
		reset();
		InstantiationEquivalence nkt;
		for(nkt = nextEquivalance(); nkt != null;nkt = nextEquivalance()) {
			if (nkt.getKinTerm() == kint) return nkt.getNormKinTerm();
		}
		return null;
	}
}
