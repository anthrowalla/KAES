import java.util.*;
import java.lang.reflect.Array;

/*history
* 10/10 DR changed return in searchGeneratorsForRecursiveEquations
* so that false is returned if no recursive equation is found;
* needed change for flagging dialogue text
* 3/11 DR added code to establishReciprocalsForGenerators to set recipocal of
* algebra element to same sex as algebra element
* 5/31 DR modified findRecursiveEquations so as to add more than one equation
* 11/23 DR changed searchGeneratorsForRecursiveEquations to return the types of equations
* changed findRecursiveEquations to return an EquationVector of equations found
* 11/10/02 DR added searchGeneratorsForParentSibEquations(), findParentSibEquations(AlgebraSymbol a)
*/

public class MakeAlgebra {

	Algebra theAlgebra = new Algebra();
	KinshipAlgebra theKinAlg = null;
	Hashtable kinToAlgNames = new Hashtable();
	Hashtable algToKinNames = new Hashtable();

	public MakeAlgebra(KinshipAlgebra k) {
		theKinAlg = k;
	}

	String letters = "PCABDEGHJKLNOQRSTUVWXYZMF";

	int curLetter = -1;

	public void addGenerators(StringVector gens) {
		for(gens.reset();gens.isNext();) {
		 addGenerator(gens.getNext());
		}
	}

	public void addGenerator(String gens) {
		theAlgebra.addGenerator(theAlgebra.getElement(assignName(gens)));
	}

	public boolean addReciprocal(String recip,  String gen, String target) {
		theAlgebra.addGenerator(theAlgebra.getElement(gen));
		theAlgebra.addGenerator(theAlgebra.getElement(recip));

		theAlgebra.getElement(recip).setArrowType(theAlgebra.getElement(gen).getOppositeArrow());
		if (theAlgebra.addEquation(recip+" "+gen,target) == null) return false;
		else return true;
//		theAlgebra.getElement(gen).setReciprocal(theAlgebra.getElement(recip));
//		theAlgebra.getElement(recip).setReciprocal(theAlgebra.getElement(gen));

//		System.out.println("addReciprocal: e="+recip+"*"+gen+"="+target);
	}

    public StringVector checkGeneratorsForReciprocals() {
		StringVector ret = new StringVector(1);
		AlgebraSymbolVector gv = theAlgebra.getGenerators();
	//	System.out.println("checkGeneratorsForReciprocals gv="+gv);
		for(int i=0;i<gv.size();i++) {
			String t;
			if (!hasReciprocal((t = gv.getSymbol(i).getValue())))
				ret.addElement(t);
		}
	//	System.out.println("checkGeneratorsForReciprocals ret="+ret);
		return ret;
	}


	public boolean hasReciprocal(String gen) {
		return ! (theAlgebra.getElement(gen).getReciprocal() == null);
	}


	public boolean establishReciprocalsForGenerators() {
		boolean flag = false;
		if (!hasFocalElements()) {
			//System.out.println("establishReciprocalsForGenerators: Message could not find a focal elements");

			return false;
		} else {
			boolean sibFlag = false;
		    EquationVector eq = theAlgebra.getEquations();
		    AlgebraSymbolVector asnew = new AlgebraSymbolVector();
		    AlgebraSymbolVector asold = new AlgebraSymbolVector();
			AlgebraSymbolVector asSib = new AlgebraSymbolVector();

			StringVector r = checkGeneratorsForReciprocals();
			if (r.size() > 0) {
				//System.out.println("establishReciprocalsForGenerators: r="+r);
				for(r.reset();r.isNext();) {
					String gen = r.getNext();
					ListVector x;
					if (getKinName(gen) == null) {
						Debug.prout(0,"establishReciprocalsForGenerators: kinterm not available: "+gen);
						x = new ListVector();
					} else x = theKinAlg.checkGeneratorForReciprocal(getKinName(gen));

					Debug.prout(0," ge "+ gen +" name "+getKinName(gen)+ " x "+x);
					if (x.size() == 0) {
						// ++++ Message could not find a reciprocal for this gen
						//System.out.println("establishReciprocalsForGenerators: Message could not find a reciprocal for "+gen);
						continue;
					}
					if (x.size() > 1) {
						x = _checkListVectors(x,gen);
						sibFlag = _checkForSibRecEquations(x);
					Debug.prout(0," sibflg "+sibFlag+" x "+x);
					}
					//sibFlag = true;
					replaceKinWithAlgebraNames(x);
					flag = true;
//System.out.println(" XXXXXX x "+x);
					for (x.reset();x.isNext();){
						StringVector xx =(StringVector) x.getNext();
						String sex = "";
						if (r.indexOf(xx.getSymbol(2)) != -1)
						    sex = theAlgebra.getElement(xx.getSymbol(2)).getSex();
						else sex = theAlgebra.getElement(xx.getSymbol(1)).getSex();
						theAlgebra.getElement(xx.getSymbol(1)).setSex(sex);
				Debug.prout(0," xx "+ xx +" symbol 1 "+xx.getSymbol(1)+" sex "+sex);
						if (r.indexOf(xx.getSymbol(2)) != -1){
						    asold.addElement(theAlgebra.getElement(xx.getSymbol(2)));
						    asnew.addElement(theAlgebra.getElement(xx.getSymbol(1)));
							if (sibFlag) {
								if (asSib.indexOf(theAlgebra.getElement(xx.getSymbol(2))) == -1)
							        asSib.addElement(theAlgebra.getElement(xx.getSymbol(2)));
								if (asSib.indexOf(theAlgebra.getElement(xx.getSymbol(1))) == -1)
						            asSib .addElement(theAlgebra.getElement(xx.getSymbol(1)));
							}
						}
						//System.out.println("asOld "+asold+" asnew "+asnew);
						if (!addReciprocal(xx.getSymbol(2),xx.getSymbol(1),xx.getSymbol(0))) return false;
					}
				}
			}
			//EquationVector eqNew = theAlgebra.substitute(eq,asnew,asold);
			EquationVector eqNew = theAlgebra.substitute(_modifyEquations(eq,r),asnew,asold);
//System.out.println("EQOld "+_modifyEquations(eq,r)+" eqnew "+eqNew);
		    for (eqNew.reset1();eqNew.isNext();)
				theAlgebra.addEquation(eqNew.getNext());
//System.out.println("alg eq "+eq);
//System.out.println("asSIB "+asSib);
		    if (sibFlag) {
				_makeSibEquations(asSib);
		    }
			return flag;
		}
	}
	void _makeSibEquations(AlgebraSymbolVector asSib){
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector(2);
		AlgebraSymbolVector asv2 = new AlgebraSymbolVector(1);
		asv1.addElement(asSib.elementAt(0));
		asv1.addElement(asSib.elementAt(1));
		asv2.addElement(theAlgebra.getIdentityElement());
		Equation eq1 = new Equation(asv1,asv2);
		EquationVector eqv = theAlgebra.getEquations();
		if (eqv.indexOf(eq1) != -1){
			AlgebraSymbolVector lhs = new AlgebraSymbolVector();
			AlgebraSymbolVector rhs = new AlgebraSymbolVector();
			AlgebraSymbol as = null;
			for (eqv.reset1();eqv.isNext();){
				Equation eqtn = eqv.getNext();
//System.out.println("EQUATION   "+eqtn);
				lhs = (AlgebraSymbolVector)eqtn.getLhs().clone(false);
				rhs = (AlgebraSymbolVector)eqtn.getRhs().clone(false);
//System.out.println("EQUATION   "+eqtn+" lhs "+lhs);

				AlgebraSymbol as1 = lhs.getLast();
				Equation eqNew = _MakeEquation(asSib,as1,null,lhs,rhs);
				if (eqNew != null) theAlgebra.addEquation(eqNew);
//	System.out.println("aaaa lhs " + lhs +" neweq "+eqNew);
				lhs = (AlgebraSymbolVector)eqtn.getLhs().clone(false);
				rhs = (AlgebraSymbolVector)eqtn.getRhs().clone(false);
				as1 = lhs.getFirst();
				eqNew = _MakeEquation(asSib,null,as1,lhs,rhs);
				if (eqNew != null) theAlgebra.addEquation(eqNew);
//	System.out.println("bbbb lhs " + lhs +" neweq "+eqNew);
			}
		}
	}

	Equation _MakeEquation(AlgebraSymbolVector asSib,AlgebraSymbol as1,
		AlgebraSymbol as2,AlgebraSymbolVector lhs,AlgebraSymbolVector rhs){
//System.out.println(" AS 1 "+as1+" lhs "+lhs);
		boolean flag = (as1 != null);
		Equation newEq = null;
		AlgebraSymbol as = null;
		if (flag) as = as1;
		else as = as2;
		if (as.toString().equals("")) return null;
		if (lhs.sameElements()) return null;
		if (((AlgebraSymbol)rhs.elementAt(0)).isIdentityElement()) return null;
	//System.out.println(" AS 1 "+as1+" lhs "+lhs+" iiiii "+i);
		int i = asSib.indexOf(as);
		if (i != -1){
			if (i == 0) {
				if (flag){
					lhs.addToEnd((AlgebraSymbol)asSib.elementAt(1));
					rhs.addToEnd((AlgebraSymbol)asSib.elementAt(1));
				} else {
					lhs.addToBeginning((AlgebraSymbol)asSib.elementAt(1));
					rhs.addToBeginning((AlgebraSymbol)asSib.elementAt(1));
				}
			}
			else {
				if (flag){
					rhs.addToEnd((AlgebraSymbol)asSib.elementAt(0));
					lhs.addToEnd((AlgebraSymbol)asSib.elementAt(0));
				} else {
					rhs.addToBeginning((AlgebraSymbol)asSib.elementAt(0));
					lhs.addToBeginning((AlgebraSymbol)asSib.elementAt(0));
				}
			}
			AlgebraPath ap1 = new AlgebraPath();
			if (flag) ap1.reducePathLR(lhs);
			else ap1.reducePath(lhs);
	//System.out.println(" LHS "+lhs+" AP " +ap1+ "flag "+flag);
			AlgebraSymbolVector lhs1 = ap1.getReducedProductPath();
			AlgebraPath ap2 = new AlgebraPath();
			if (flag) ap2.reducePathLR(rhs);
			else ap2.reducePath(rhs);
	//System.out.println(" RHS "+rhs+" AP " +ap2+" flag "+flag);
			AlgebraSymbolVector rhs1 = ap2.getReducedProductPath();
			if (!rhs1.equals(lhs1))
			    newEq = new Equation(rhs1,lhs1);
		}
		return newEq;
	}


	ListVector _checkListVectors(ListVector x,String gen){
		int j = 0;
		if (theKinAlg.getKv().lookupTerm(getKinName(gen)).getOrientation() ==
							theKinAlg.getKv().UP) {
			j = 2;
		} else if (theKinAlg.getKv().lookupTerm(getKinName(gen)).getOrientation() ==
							theKinAlg.getKv().DOWN) {
			j = 1;
		}
		if (j > 0) {
			for (int i = 0;i < x.size();i++){
				StringVector sv = (StringVector)x.elementAt(i);
				if (sv.elementAt(j).equals(getKinName(gen))){
					x.removeAllElements();
					x.addElement(sv);
					break;
				}
			}
		}
		return x;
	}
	boolean _checkForSibRecEquations(ListVector x){
	    boolean theFlag = true;
		if (x.size() > 1){
			for (x.reset();x.isNext();){
				if (theFlag) {
					StringVector sv = (StringVector)x.getNext();
					String s0 = (String)sv.elementAt(0);
					StringVector ft = theKinAlg.getKv().getEffectiveFocalTerms();
					if (ft.size() > 0){
						theFlag = (s0.equals((String)ft.elementAt(0)));
						if (theFlag) {
			//System.out.println(" sv "+sv);
							int i = theKinAlg.getKv().lookupTerm((String)sv.elementAt(1)).getOrientation();
//System.out.println("1st i "+i);
//System.out.println(" right "+theKinAlg.getKv().RIGHT+" left "+theKinAlg.getKv().LEFT);
							theFlag = ((i == theKinAlg.getKv().RIGHT)||
													(i == theKinAlg.getKv().LEFT));
							if (theFlag) {
		//System.out.println("in here");
								i = theKinAlg.getKv().lookupTerm((String)sv.elementAt(2)).getOrientation();
//System.out.println("2nd i "+i);
								theFlag = ((i == theKinAlg.getKv().RIGHT)||
													(i == theKinAlg.getKv().LEFT));
							}
						}
					}
				}
			}
		}
		else theFlag = false;
		return theFlag;
	}

	EquationVector _modifyEquations(EquationVector eq,StringVector r){
		EquationVector eqOld = new EquationVector(5,1);
		for (eq.reset();eq.isNext();){
			Equation eqtn = eq.getNext();
			AlgebraSymbolVector lhs = eqtn.getLhs();
			boolean eqFlag = false;
			for (lhs.reset();lhs.isNext();){
				String s = ((AlgebraSymbol)lhs.getNext()).theSymbol;
				eqFlag = (r.indexOf(s) == -1);
				if (eqFlag) break;
			}
			if (!eqFlag) {
				AlgebraSymbolVector rhs = eqtn.getRhs();
				for (rhs.reset();rhs.isNext();){
					AlgebraSymbol as = (AlgebraSymbol)rhs.getNext();
					String s = as.theSymbol;
					eqFlag = ((r.indexOf(s) == -1) && (!as.isIdentityElement()));
				if (!eqFlag) break;
				}
			}
			if (!eqFlag) eqOld.addElement(eqtn);
		}
		return eqOld;
	}


	public boolean hasIdentityElement() {
		return theAlgebra.hasIdentityElement();
	}

	public boolean hasFocalElements() {
		return theAlgebra.hasFocalElements();
	}

	public boolean addIdentityElement(String id) {
			if (!isAlgName(id)) {
				// Defaulty: Assume all identity elements are called 'I'
				if (!theAlgebra.hasIdentityElement()) {
					theAlgebra.addIdentityElement(theAlgebra.getElement("I"));
				}
				kinToAlgNames.put(id,"I");
				algToKinNames.put("I",id);
				return true;
			} else if (getKinName("I") != null && getKinName("I").equals(id)) return true;
			else {
				// Message regarding the presense of identity  term which is not id ++++
				Debug.prout(0,"addIdentityElement: identity not 'I' "+id);
				return false;
			}
	}

	public String assignName(String inName) {
		String algName;
		// need provision to know what symbols have been indpendently added
		// using the equation panel, and to put up dialog allowing people
		// to link these to the terms coming into this routine
		// maybe also be able to do this in bulk on the equation panel as an
		// option, or to do putative assignments from a complete algebra to a
		// kin map
		if ((algName = (String) kinToAlgNames.get(inName)) == null) {
			String let = letters.substring(0,1);
			letters = letters.substring(1);
			kinToAlgNames.put(inName,let);
			algToKinNames.put(let,inName);
			return let;
		} else return algName;
	}

	public String assignName(String let, String inName) {
		String algName;
		// need provision to know what symbols have been indpendently added
		// using the equation panel, and to put up dialog allowing people
		// to link these to the terms coming into this routine
		// maybe also be able to do this in bulk on the equation panel as an
		// option, or to do putative assignments from a complete algebra to a
		// kin map
		if ((algName = (String) kinToAlgNames.get(inName)) == null) {
			int ndx;
			if ((ndx = letters.indexOf(let)) == -1) return null;
			else {
				letters = letters.substring(0,ndx) +
					((ndx < letters.length()-1) ? letters.substring(ndx+1) : "" );
			}
			kinToAlgNames.put(inName,let);
			algToKinNames.put(let,inName);
			return let;
		} else if (algName != let) return null;
		else return algName;
	}

	//public boolean searchGeneratorsForRecursiveEquations() {

	public Vector searchGeneratorsForReflexiveEquations() {
	    Vector eqType = new Vector();
	    EquationVector eqv = new EquationVector();
		AlgebraSymbolVector gens = theAlgebra.getGenerators();
		System.out.println("searchGeneratorsForReflexiveEquations gens="+gens);
		for(int i=0;i<gens.size();i++) {
			if (!gens.getSymbol(i).isIdentityElement()) {
				if (getKinName(gens.getSymbol(i).getValue()) == null) continue;
				eqv = findReflexiveEquations(gens.getSymbol(i));
				if (eqv == null) continue;
			//	System.out.println("QQQQQQQQQQQQQQQQQQQQQ eqv "+eqv);
				for (eqv.reset1();eqv.isNext();){
				    Equation eq = eqv.getNext();
				    theAlgebra.addEquation(eq);
				    if (eqType.indexOf(new Integer(eq.equationType())) == -1) {
				        eqType.addElement(new Integer(eq.equationType()));}
				}
			}
		}
		//return flag;
		return eqType;
	}

	public Vector searchGeneratorsForParentSibEquations() {
	    Vector eqType = new Vector();
	   // boolean flag = false;
	    EquationVector eqv = new EquationVector();
		AlgebraSymbolVector gens = theAlgebra.getGenerators();
		//System.out.println("111 searchGeneratorsForReflexiveEquations gens="+gens+ " type "+eqType);
		for(int i=0;i<gens.size();i++) {
			if (!gens.getSymbol(i).isIdentityElement()) {
			    eqv = findParentSibEquations(gens.getSymbol(i));
				if (eqv.size() > 0) {
					for (eqv.reset1();eqv.isNext();){
						 Equation eq = eqv.getNext();
						//System.out.println("112 searchGeneratorsForReflexiveEquations gens="+gens+ " eq "+eq+" type "+eq.equationType());
						
						 theAlgebra.addEquation(eq);
					    if (eqType.indexOf(new Integer(eq.equationType())) == -1) {
							  eqType.addElement(new Integer(eq.equationType()));}
					}
					//if (!flag) flag = true;
				}
			}
		}


		//return flag;
		return eqType;
	}


	String getKinName(String algName) {
	//	System.out.println("********To KinName s="+algName+" r="+((String) algToKinNames.get(algName)));
		return (String) algToKinNames.get(algName);
	}

	String getAlgName(String kinName) {
		//System.out.println("********To AlgName kinName "+kinName);
		String x = (String) kinToAlgNames.get(kinName);
		if (x == null) x = assignName(kinName);
	//	System.out.println("********To AlgName s="+kinName+" r="+x);
		return x;
	}

	boolean isAlgName(String kinName) {
		return (!( kinToAlgNames.get(kinName) == null));
	}

	public StringVector replaceKinWithAlgebraNames(StringVector results) {
		if (results.size() == 0) {
			results.addElement("0");
		} else for(results.reset();results.isNext();) {
			String t = getAlgName(results.getNext());
			results.replace(t);
		}
		return results;
	}

	public void replaceKinWithAlgebraNames(ListVector results) {
		for(results.reset();results.isNext();) {
			StringVector x = (StringVector) results.getNext();
			replaceKinWithAlgebraNames(x);
		}
	}
	public StringVector replaceAlgebraWithKinNames(StringVector results) {
	/*	if (results.size() == 0) {
			results.addElement("0");  // nothing corresponding to not a kin term
		} else */
		for(results.reset();results.isNext();) {
			String t = getKinName(results.getNext());
			results.replace(t);
		}
		return results;
	}

	public void replaceAlgebraWithKinNames(ListVector results) {
		for(results.reset();results.isNext();) {
			StringVector x = (StringVector) results.getNext();
			replaceAlgebraWithKinNames(x);
		}

	}

	public EquationVector findReflexiveEquations(AlgebraSymbol a) {
        EquationVector ret = new EquationVector();
		String term = getKinName(a.getValue());
/*		if (term == null) {
			System.out.println("findReflexiveEquations not found a="+a.getValue());
			Trap k = new Trap();
			k.go();
			return;
		}*/
		ListVector results = theKinAlg.checkReflexivePath(term);
		if (results.size() > 0) {
			System.out.println("findReflexiveEquations Results="+results);
			replaceKinWithAlgebraNames(results);
			//System.out.println("findReflexiveEquations aResults="+results);
			for (results.reset();results.isNext();) {
			    StringVector left = (StringVector)results.getNext();
			    StringVector right = (StringVector)results.getNext();
			    //theAlgebra.addEquation(left,right);
			    Equation eq = new Equation(theAlgebra.makePath(left),theAlgebra.makePath(right));
			    if (ret.indexOf(eq)== -1) ret.addElement(eq);
			}
			//theAlgebra.addEquation((StringVector)results.elementAt(0),(StringVector)results.elementAt(1));
		    //return true;
		}
		//return false;
		return ret;
	}

	public EquationVector findParentSibEquations(AlgebraSymbol a) {
        EquationVector ret = new EquationVector();
		String term = getKinName(a.getValue());
		if (theKinAlg.getKv().lookupTerm(term).getOrientation() == theKinAlg.getKv().RIGHT ||
			theKinAlg.getKv().lookupTerm(term).getOrientation() == theKinAlg.getKv().LEFT) {//sib
			AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
			for (gens.reset();gens.isNext();){
				AlgebraSymbol gen = gens.getNext();
			    String g = getKinName(gen.getValue());
				if (theKinAlg.getKv().lookupTerm(g).getOrientation() == theKinAlg.getKv().UP) {//pa
					ListVector results = theKinAlg.checkParentSibPath(term,g);//(sib,pa = pa)
					if (results.size() > 0) {
						replaceKinWithAlgebraNames(results);
						for (results.reset();results.isNext();) {
							StringVector left = (StringVector)results.getNext();
							StringVector right = (StringVector)results.getNext();
							Equation eq = new Equation(theAlgebra.makePath(left),theAlgebra.makePath(right));
							if (ret.indexOf(eq)== -1) ret.addElement(eq);
						}
					}
				}
			}
		}
		return ret;
	}

   public boolean establishReciprocalEquations(){
		EquationVector eqtns = theAlgebra.getEquations();
		EquationVector neweqtns = new EquationVector();
		Equation eqtnr = null;
		for(eqtns.reset();eqtns.isNext();) {
			Equation eqtn = eqtns.getNext();
			ListVector eqv = theAlgebra.createReciprocalEquation(eqtn);
			for (eqv.reset();eqv.isNext();){
				eqtnr = (Equation) eqv.getNext();
				Debug.prout(0," EQUATION "+eqtn +" RECIPROCAL "+eqtnr);
				if (!eqtn.equals(eqtnr)) neweqtns.addElement(eqtnr);
			}
	   }
		int i = theAlgebra.getAddReciprocalEquations();
		theAlgebra.setAddReciprocalEquations(Algebra.RE_NO);
		//System.out.println("NEW EQ "+neweqtns+" size "+neweqtns.size());
		for (neweqtns.reset1();neweqtns.isNext();) {
			eqtnr = neweqtns.getNext();
			Debug.prout(0," ADD EQUATION "+eqtnr);
			theAlgebra.addEquation(eqtnr);
		}
		theAlgebra.setAddReciprocalEquations(i);
		return (neweqtns.size() != 0);
	}

    /** make an isomorphic copy of an algebra with new symbols
	* @return the isomorphic copy
	*/
	public Algebra makeIsomorphicAlgebra(KintermFrame fr) {
	    return makeIsomorphicAlgebra(null,true,fr);
	}

    /** make an isomorphic copy of an algebra with new symbols and same/opposite sex markings
	* @sameSex flag for same or opposite sex symbols
	* @return the isomorphic copy
	*/
	public Algebra makeIsomorphicAlgebra(boolean sameSex,KintermFrame fr) {
	    return makeIsomorphicAlgebra(null,sameSex,fr);
	}

    /** make an isomorphic copy of an algebra with new symbols and opposite sex markings
    * @sv list of symbols that will not be replaced in isomorphic copy
	* @sameSex boolean switch for same sex or opposite sex isomorphic symbols
	* @return the isomorphic copy
	*/
	public Algebra makeIsomorphicAlgebra(StringVector sv, boolean sameSex,KintermFrame fr) {
	    Algebra a1 = theAlgebra;
	    Algebra a = (Algebra)a1.isomorphicClone();
	    a.unbind();
	    AlgebraSymbolVector g = a.getGenerators();
		int aType = -1;
	    for (g.reset();g.isNext();) {
	        AlgebraSymbol as = g.getNext();
//System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGG as "+ as+ " sex "+as.getSex());
			aType = as.getArrowType();
//System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGG as "+ as+ " sex "+as.getSex()+" tyep "+aType+ " identity "+as.isIdentityElement()+
//" focal "+as.isFocalElement() + " sv "+sv);
	        if (sv.indexOf(as.getValue()) == -1){
				if (sameSex) {
					if (aType == Bops.UP) as.setArrowType(Bops.DOWN);
					else if (aType == Bops.DOWN) as.setArrowType(Bops.UP);
					else if (aType == Bops.LEFT) as.setArrowType(Bops.RIGHT);
					else if (aType == Bops.RIGHT) as.setArrowType(Bops.LEFT);
					else if (aType == Bops.SIDE) as.setArrowType(Bops.SIDE);
				} else { as.setSex(as.getOppositeSex());}
	            if (as.isIdentityElement()) {
	                as.setValue("i");
	            }
	            else {
//System.out.println(" aas "+as+" atype "+as.getArrowType()+" sex "+as.getSex()+ " gens "+
//tk.getEffectiveGenerators(as.getArrowType()));
				    ListVector panels = fr.panels;
					TransferKinInfo gen = null;
					for (int i = panels.size()-1; i > -1;i--){
						KinshipTermsPanel panel = (KinshipTermsPanel)panels.elementAt(i);
						TransferKinInfoVector tk = (TransferKinInfoVector) panel.getTransferKinInfo();
//System.out.println(panel.getTransferKinInfo());
//System.out.println(tk.getEffectiveGenerators());
						TransferKinInfoVector gens = tk.getEffectiveGenerators(as.getArrowType());
//System.out.println(" the gens "+gens+" as "+as+ " tyhpe "+as.getArrowType());

						for (gens.reset();gens.isNext();){
							TransferKinInfo gg = gens.getNext();
							//System.out.println("BBBBBBBBBBBBBBBB gg "+gg+" ses "+gg.getSex()+" as "+as+" sex "+as.getSex());
							if (gg.getSex().equals(as.getSex())) {gen = gg;break;}
						}
						if (gen != null) break;
					}
					String let = "";
					if (gen != null){
				        String arrow = gen.getTerm();
				        let = assignName(arrow);
					} else {
//System.out.println(" arrow "+arrow+ " aas "+as +" let "+let);
	                    let = letters.substring(0,1);
		                letters = letters.substring(1);
					}
	                as.setValue(let);
	            }
	        }
//	        if (as.isIdentityElement()){
//	            as.setSex("N");
//	        }else
		    else if (!sameSex)
	            as.setSex(as.getOppositeSex());
	    }
/*	   System.out.println("identity "+a.identity);
	   System.out.println("identityElement "+a.identityElement);
	   System.out.println("focalElements "+a.focalElements);
	   System.out.println("thesymbols "+a.theSymbols);
	   System.out.println("structuralEquations "+a.structuralEquations);
	   System.out.println("unbindSymbols "+a.unbindSymbols);*/
	    return a;
	}

	public int getAlgebraClass(){//serves as classification of algebraic structures!!!!
	    int type = getAlgebraType();
		if ((type & CLASSIFICATORY) > 0) return CLASSIFICATORY;
		else if ((type & DESCRIPTIVE) > 0) return DESCRIPTIVE;
		return -1;
	}

	public int getAlgebraType(){//serves as classification of algebraic structures!!!!
								//System.out.println("sex marked female generators "+theAlgebra.getSexMarkedGenerators("F"));
								//System.out.println(" sib "+theAlgebra.hasSibGenerators()+" sex "+theAlgebra.getIdentityElement().getSex().equals("N"));
		if (theAlgebra.hasSibGenerators()){
			if (theAlgebra.getFocalElements().size() == 2 ||!theAlgebra.getIdentityElement().getSex().equals("N")) {
				if (singleChild()) return TROB;//trob
				else return TONGAN;
			} else return SIBGEN;	
		}
		else if (theAlgebra.getSexMarkedGenerators("F").size() > 0 ||
				 theAlgebra.getSexMarkedGenerators("M").size() > 0) return SHIPIBO;//shipibo
		else if (theAlgebra.getIdentityElement().getSex().equals("N")) return AKT;//AKT
		else return -1;
	}
	
	boolean singleChild(){//Si=S,SI=S,Di=D,DI=D
		AlgebraSymbolVector ft = theAlgebra.getFocalElements();
	    AlgebraSymbolVector asv = theAlgebra.getGenerators(Bops.DOWN);
		for (ft.reset();ft.isNext();){
			AlgebraSymbol as0 = ft.getNext();
		    for (asv.reset();asv.isNext();){
				AlgebraSymbol as1 = asv.getNext();
				AlgebraPath ap = new AlgebraPath(as1,as0);
				//System.out.println("as0 "+as0+" ap "+ap + " red "+ ap.getReducedProductPath()+" value "+as1.getValue());
				//if (!ap.getReducedProductPath().toString().equals(as1.getValue())) return false;
				if (!ap.toString().equals(as1.getValue())) return false;
		    }
		}
		return true;
	}

	public void addCrossSexEquations(){
	    //AlgebraSymbolVector id = theAlgebra.getFocalElements();
/*	    id.addElement(id1);
	    id.addElement(id2);*/
//	    System.out.println(" focal elements of the join algebra="+this.getFocalElements());
		//addSexIdEquations(id);//use with trobriand; id1 and id2 and sex marked
		if (getAlgebraClass() == CLASSIFICATORY){
			addSexIdEquations();//use with trobriand; id1 and id2 and sex marked
			addParentChildProductEquation();//logical: FSi=0,FDi=0,MDI=0,MSI=0
			addOlderYoungerSibProducts();//logical: BIfemale=Ifemale,bIfemale=Ifemale,etc.
		}
		// addPCEquations(id);
		addMixedSexSameArrowEquations();
		//if (id.size() == 2)
		   // addSibAnalogousEquations();//trobriand
		//addAnalogousEquations(id);
		addAnalogousEquations();
		//if (id.size() == 2)
		  // addCrowSkewingEquations();
		//makeElementsEquivalent();
		//if (id.size() > 1) {
		if (getAlgebraClass() == CLASSIFICATORY) {
			//addOlderYoungerSibProducts();//logical: BIfemale=Ifemale,bIfemale=Ifemale,etc.
		}

	}

	public void addClassificatoryStructure(boolean singleFlag, String sex1,String sex2){
		if (singleFlag) {
			addSingleChildEquations();// S,D,i,I --> Si = S, DI = D
			linkElementProductEquations(1,Bops.DOWN);//S, D--> S&D
		} else {
			//System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIn here sex2 "+sex2+" sex1 "+sex1);
			//addOlderYoungerSibProducts();//logical: BIfemale=Ifemale,bIfemale=Ifemale,etc.
			equateChildElements(sex1);//Ai&E, A&EI optional
			equateTheCrossCousins(sex2);//AiI&EiI,AIi&EIi optional
			addCrossCousinEquation();//EIG=i,AiP=I,optional
			//addParentChildProductEquation();//logical: FSi=0,FDi=0,MDI=0,MSI=0
			addSibChildProducts();//ZAi = Ai, etc.based on MER rule
			addParentChildProducts();//P(Si_&Di_)=0, etc., uses MER rule
			addChildSibParentProducts();//ABP = B (SB+F = B+), etc. optional
		}
		linkMaleFemaleElementEquations();//assumes grandparent, grandchild, etc e.g.tongan, trobriand
	}


 	/** adds equation to list of structural equations for this algebra
 	* of form id1 id2=id1 and id2 id1= id2 and id1 Z = B, id2 B = Z, id1 sex B and id2 sex F
	* @param id the sex-marked (pseudo) identity elements
	*/
    void addSexIdEquations() {//use with trobriand
		AlgebraSymbolVector id = theAlgebra.getFocalElements();
		AlgebraSymbol id1 = (AlgebraSymbol)id.elementAt(0);
		AlgebraSymbol id2 = (AlgebraSymbol)id.elementAt(1);
		if (id1.getSex().toString().equals(id2.getSex().toString())) return;

	// System.out.println("gen sex "+ gen+" "+sex+" id sex "+as + " "+sex1);
		//Equation eq = new Equation(id1,id2).addLhs(id2);//br si = br
		Equation eq = new Equation(id1,id1).addLhs(id2).addLhs(id1);//br si a separate element?
		theAlgebra._addEquation(eq);
		//eq = new Equation(id2,id1).addLhs(id1);
		eq = new Equation(id2,id2).addLhs(id1).addLhs(id2);
		theAlgebra._addEquation(eq);
		AlgebraSymbolVector gens = theAlgebra.getGenerators();
		for (gens.reset();gens.isNext();) {
			AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType() == Bops.UP) {
				eq = new Equation(id1,gen).addLhs(gen);
				theAlgebra._addEquation(eq);
				eq = new Equation(id2,gen).addLhs(gen);
				theAlgebra._addEquation(eq);
				if (!gen.getSex().equals(id1.getSex())) {
					eq = new Equation(gen,gen).addLhs(id1).addLhs(id2).addRhs(id2);
					theAlgebra.addEquation(eq);
				} else if (!gen.getSex().equals(id2.getSex())) {
					eq = new Equation(gen,gen).addLhs(id2).addLhs(id1).addRhs(id1);
					theAlgebra.addEquation(eq);
				}
			} else if (gen.getArrowType() == Bops.DOWN) {
				AlgebraSymbol g1 = gen.getOppositeSexGenerator();
				if (!id1.getSex().equals(gen.getSex())) {
					eq = new Equation(gen,g1).addLhs(id1);
				  theAlgebra._addEquation(eq);
				} else if (!id2.getSex().equals(gen.getSex())) {
					eq = new Equation(gen,g1).addLhs(id2);
				   theAlgebra._addEquation(eq);
				}
				//eq = new Equation(id1,gen).addLhs(gen);//exclude DI=D
				//_addEquation(eq);
				//eq = new Equation(id2,gen).addLhs(gen);
				//_addEquation(eq);
			} else if (gen.getArrowType() == Bops.RIGHT || gen.getArrowType() == Bops.LEFT){
				if (!gen.getSex().equals(id1.getSex())) {
					eq = new Equation(id1,Algebra.getCurrent().getElement("0")).addLhs(gen);
				   theAlgebra._addEquation(eq);
					eq = new Equation(gen,id1).addLhs(id1);
				   theAlgebra._addEquation(eq);

				} else if (!gen.getSex().equals(id2.getSex())) {
					eq = new Equation(id2,Algebra.getCurrent().getElement("0")).addLhs(gen);
				   theAlgebra._addEquation(eq);
					eq = new Equation(gen,id2).addLhs(id2);
				   theAlgebra._addEquation(eq);
				}
			}
		}
	}

	/** adds equations of form Pp=PP where P and p are in the same
	* direction but differ in sex marking
	*/
	void addMixedSexSameArrowEquations() {
	    AlgebraSymbolVector gens = theAlgebra.getGenerators();
	    AlgebraSymbolVector usedG = new AlgebraSymbolVector(3,1);
	    for (gens.reset();gens.isNext();){
	        AlgebraSymbol g = gens.getNext();
	        if (usedG.indexOf(g) == -1) {
	            AlgebraSymbol g1 = g.getOppositeSexGenerator();
	            usedG.addElement(g);
	            usedG.addElement(g1);
	            if (g.getArrowType() == Bops.UP ||g.getArrowType() == Bops.DOWN) {
	                Equation eq = new Equation(g1,g).addLhs(g).addRhs(g);
	                theAlgebra.addEquation(eq);
	                eq =  new Equation(g,g1).addLhs(g1).addRhs(g1);
	                theAlgebra.addEquation(eq);
	            }
	        //System.out.println("g = "+g+" g1= "+g1);
            }
	    }
	}

	/** if xy=zw is an equation, X(Z) is opposite sex generator for x(z) then
	* make Xy=Zw an equation
	*/
	void addAnalogousEquations() {//new procedure used with Tongan; 9/17 works with Shipibo
		AlgebraSymbolVector id = theAlgebra.getFocalElements();
		boolean oneIdFlag = id.size() < 2;
	    EquationVector eqs = theAlgebra.getEquations();
	    EquationVector newEqs = new EquationVector();
	    Equation e = new Equation();
	    for (eqs.reset1();eqs.isNext();){
	        Equation eq = eqs.getNext();
	        AlgebraSymbolVector lhs = (AlgebraSymbolVector) eq.getLhs().clone();
	        AlgebraSymbolVector rhs = (AlgebraSymbolVector)eq.getRhs().clone();
	        AlgebraSymbol as = lhs.getLast();
	        if (!eq.isIdEquation() && !eq.isZeroEquation()
	        && as != null && lhs.size() != rhs.size()){
				//System.out.println("THE EQUATION eq "+eq);
	            AlgebraSymbol as1 = as.getOppositeSexGenerator();
               if (as1 != null) {
						as = rhs.getLast();
						AlgebraSymbol as2 = as.getOppositeSexGenerator();
         //   System.out.println(" right as "+as+" as1 "+as1);

						if (as2 != null) {
						if (oneIdFlag)
						    rhs.setElementAt(as2,0);
						else {
							if (lhs.getLast().getArrowType() == Bops.UP || lhs.getLast().getArrowType() == Bops.DOWN){
								rhs.setElementAt(as2,0);
								AlgebraSymbol as0 = rhs.getFirst();
								if (!as0.isFocalElement()) {
									String sex = as0.getSex();
									AlgebraSymbol asf = null;
									if (sex.equals("M"))
									    asf = theAlgebra.getFocalElement("F");
									else
									    asf = theAlgebra.getFocalElement("M");
								    rhs.addToBeginning(asf);
								}
							}
							else if (lhs.getLast().getArrowType() == Bops.LEFT || lhs.getLast().getArrowType() == Bops.RIGHT){
									//System.out.println(" lhs "+lhs);
								//if (lhs.sameSex() && lhs.getFirst().getArrowType() == Bops.UP) {
									//System.out.println(" lhs "+lhs);
								//	continue;
								//}
								rhs.setElementAt(Algebra.getCurrent().getElement("0"),0);
							}
						}
						AlgebraSymbolVector rhs1 = rhs.reduce(); //.getReducedProductPath();
						lhs.setElementAt(as1,0);
						AlgebraPath ap = new AlgebraPath();
						ap.reducePath(lhs);
						AlgebraSymbolVector lhs1 = null;
						lhs1 = ap.getReducedProductPath();
						if (lhs1.size() < lhs.size()) continue;
/*						if (lhs1.size() == 1) continue;
                    } else if (!ap.getReducedProductPath().toString().equals(lhs.toString())) continue;*/
						//ap = new AlgebraPath();
					//	ap.reducePath(rhs);
						if (oneIdFlag)
						    e = new Equation(lhs1,rhs1);
						else
						    e = new Equation(lhs,rhs1);
						newEqs.addElement(e);
						// addEquation(lhs,rhs1);
	   // System.out.println("END eq "+ e);
	                }
	            }
	        }
	    }
		for (int i=0;i<newEqs.size();i++){
		    Equation e1 = (Equation) newEqs.elementAt(i);
			if (e1.getLhs().size() != e1.getRhs().size()) continue;
			String rhs = e1.getRhs().toString();
			for (int j =i+1;j<newEqs.size();j++){
			    Equation e2 = (Equation) newEqs.elementAt(j);
				if (rhs.equals(e2.getRhs().toString())){
				    if (e1.getEqType() == -1) e1.setEqType(Equation.NONINVERTIBLE);
					if (e2.getEqType() == -1) e2.setEqType(Equation.NONINVERTIBLE);
				}
			}
		}

	    for (newEqs.reset1();newEqs.isNext();){
	        e = newEqs.getNext();

//System.out.println(" Analogous NEWEQ "+e);
	        theAlgebra._addEquation(e);
	    }
	}

	/** equations of form S FemaleSelf = S, D MaleSelf = D
	*
	*/
	void addSingleChildEquations(){
	    AlgebraSymbolVector id = theAlgebra.getFocalElements();
		AlgebraSymbolVector gens = theAlgebra.getGenerators();
		AlgebraSymbol so = null; AlgebraSymbol da = null;
		for (gens.reset();gens.isNext();){
			AlgebraSymbol as = gens.getNext();
			if (as.getArrowType() == Bops.DOWN) {
			    if (as.getSex().equals("M"))	so = as;
				else if (as.getSex().equals("F")) da = as;
			}
		}
		for (id.reset();id.isNext();){
		    AlgebraSymbol as = id.getNext();
			Equation eq = null;
			if (as.getSex().equals("M"))	{
			    AlgebraPath p = new AlgebraPath(da,as);
//System.out.println(" p "+p+" reduced "+p.getReducedProductPath());
				if (p.getReducedProductPath().size() == 2) {
				    eq = new Equation(as,da).addLhs(da);
System.out.println(" Equation eq "+eq);
				}
			} else if (as.getSex().equals("F"))	{
			    AlgebraPath p = new AlgebraPath(so,as);
				if (p.getReducedProductPath().size() == 2) {
				    eq = new Equation(as,so).addLhs(so);
				}
			}
			if (eq != null && theAlgebra.getEquations().indexOf(eq) == -1) {theAlgebra._addEquation(eq);
		    System.out.println(" Equation eq "+eq);}
		}
	}

	/** make equations of form xx...x=yy...y up to max size of product of
	* form xx...x = x...x, where x and y are of opposite sex and either up or down
	*/
	void linkMaleFemaleElementEquations(){

		EquationVector eqv = theAlgebra.getEquations();
		int nD = 0; int nU = 0;
		for (eqv.reset();eqv.isNext();){
			Equation eq = eqv.getNext();
			AlgebraSymbolVector lhs = eq.getLhs();
			AlgebraSymbolVector rhs = eq.getRhs();
			if (lhs.getFirst().getArrowType() != Bops.UP &&
				lhs.getFirst().getArrowType() != Bops.DOWN) continue;
			if (!lhs.sameArrow() || !rhs.sameArrow()) continue;
			if (!lhs.sameSex() || !rhs.sameSex()) continue;
			if (!lhs.getFirst().equals(rhs.getFirst())) continue;
			if (lhs.size() -1 == rhs.size()){//eq of form e.g. ffff=fff
				if (lhs.getFirst().getArrowType() == Bops.UP)
				    nU = rhs.size();
				else if (lhs.getFirst().getArrowType() == Bops.DOWN)
					nD = rhs.size();
			}
			if (nU > 0 && nD > 0) break;
		}
		if (nU+nD==0) return;

		for (int j=2;j<nD+1;j++)//skip S,D
			linkElementProductEquations(j,Bops.DOWN);
		for (int j=2;j<nU+1;j++)//skip M,F
			linkElementProductEquations(j,Bops.UP);
	}

	/** make equations of form xx...x=yy...y and equate xx...x&yy...y
	*
	* @n int size of product
	* @type int type of product
	*/
	void linkElementProductEquations(int n, int type){
		AlgebraSymbolVector asvF = new AlgebraSymbolVector();
		AlgebraSymbolVector asvM = new AlgebraSymbolVector();
		for (int i=0;i<n;i++){
		    asvF.addElement(theAlgebra.getGenerators(type,"F"));
		    asvM.addElement(theAlgebra.getGenerators(type,"M"));
		}
		theAlgebra.makeEquivalentElementEquation(asvF,asvM,true);
	}

	void addOlderYoungerSibProducts(){
		addOlderYoungerSibProducts("M");
		addOlderYoungerSibProducts("F");
	}


	void addOlderYoungerSibProductsX(String sex){//BIfemale=Ifemale,bIfemale=Ifemale,etc.
		//AlgebraSymbol pa = getGenerators(Bops.UP,sex);
		AlgebraSymbolVector gens = theAlgebra.getGenerators(sex);
		AlgebraSymbol id1 = null;
		AlgebraSymbol sibO= null; AlgebraSymbol sibY = null;
		if (sex.equals("M")) id1 = (AlgebraSymbol) theAlgebra.getFocalElements("F").elementAt(0);
		else id1 = (AlgebraSymbol) theAlgebra.getFocalElements("M").elementAt(0);
		if (sex.equals("M")){
			sibO = theAlgebra.getGenerators(Bops.LEFT,"F");
		    sibY = theAlgebra.getGenerators(Bops.RIGHT,"F");
		}else{
		    sibO = theAlgebra.getGenerators(Bops.LEFT,"M");
		    sibY = theAlgebra.getGenerators(Bops.RIGHT,"M");
		}
		AlgebraSymbolVector lv1 = new AlgebraSymbolVector();
		AlgebraSymbolVector lv2 = new AlgebraSymbolVector();
		for (gens.reset();gens.isNext();){
			AlgebraSymbol gen = gens.getNext();
			AlgebraPath ap = new AlgebraPath(id1,gen);
			if (!ap.getProductPath().equals(ap.getReducedProductPath())) continue;
			lv1.clear();lv2.clear();
			lv1.addElement(sibO);
			lv1.addElement(id1);
			lv1.addElement(gen);
			lv2.addElement(id1);
			lv2.addElement(gen);
			Equation eq = new Equation(lv1,lv2);
			theAlgebra._addEquation(eq);
System.out.println("EEEEEEEEEEEEEEEEEEE eq "+eq);
			//theAlgebra.putInRewriteProductRule(lv1,lv2);
			//System.out.println(" lv stufff lv1 "+lv1+" lv2 "+lv2);
			lv1.removeElementAt(0);
			lv1.insertElementAt(sibY,0);
			Equation eq1 = new Equation(lv1,lv2);
			theAlgebra._addEquation(eq1);
System.out.println("EEEEEEEEEEEEEEEEEEE eq1 "+eq1);
		//	System.out.println(" lv stufff2 lv1 "+lv1+" lv2 "+lv2);
			//theAlgebra.putInRewriteProductRule(lv1,lv2);
		}
	}

		void addOlderYoungerSibProducts(String sex){//BIfemale=Ifemale,bIfemale=Ifemale,etc.
		//AlgebraSymbol pa = getGenerators(Bops.UP,sex);
		AlgebraSymbolVector gens = theAlgebra.getGenerators(sex);
		AlgebraSymbol id1 = null;
		AlgebraSymbol sibO= null; AlgebraSymbol sibY = null;
		if (sex.equals("M")) id1 = (AlgebraSymbol) theAlgebra.getFocalElements("F").elementAt(0);
		else id1 = (AlgebraSymbol) theAlgebra.getFocalElements("M").elementAt(0);
		if (sex.equals("M")){
			sibO = theAlgebra.getGenerators(Bops.LEFT,"F");
		    sibY = theAlgebra.getGenerators(Bops.RIGHT,"F");
		}else{
		    sibO = theAlgebra.getGenerators(Bops.LEFT,"M");
		    sibY = theAlgebra.getGenerators(Bops.RIGHT,"M");
		}
		ListVector lv1 = new ListVector();
		ListVector lv2 = new ListVector();
		for (gens.reset();gens.isNext();){
			AlgebraSymbol gen = gens.getNext();
			AlgebraPath ap = new AlgebraPath(id1,gen);
			if (!ap.getProductPath().equals(ap.getReducedProductPath())) continue;
			lv1.clear();lv2.clear();
			lv1.addElement(sibO);
			lv1.addElement(id1);
			lv1.addElement(gen);
			lv2.addElement(id1);
			lv2.addElement(gen);
			theAlgebra.putInRewriteProductRule(lv1,lv2);
			//System.out.println(" lv stufff lv1 "+lv1+" lv2 "+lv2);
			lv1.removeElementAt(0);
			lv1.insertElementAt(sibY,0);
		//	System.out.println(" lv stufff2 lv1 "+lv1+" lv2 "+lv2);
			theAlgebra.putInRewriteProductRule(lv1,lv2);
		}
	}

	void equateChildElements(String sex){//Ai&E, A&EI
		AlgebraSymbolVector ft = theAlgebra.getFocalElements();
		for (ft.reset();ft.isNext();){
		    AlgebraSymbol id = ft.getNext();
			if (sex.equals("")) continue;//do not equate
			if (!sex.equals("N")){
			    if (!id.getSex().equals(sex)) continue;
			}
			AlgebraSymbolVector asvF = new AlgebraSymbolVector();
			AlgebraSymbolVector asvM = new AlgebraSymbolVector();
			asvF.addElement(theAlgebra.getGenerators(Bops.DOWN,"F"));
			asvM.addElement(theAlgebra.getGenerators(Bops.DOWN,"M"));
			asvF.addElement(id);
			asvM.addElement(id);
			if (id.getSex().equals("F"))
			    theAlgebra.makeEquivalentElementEquation(asvM,asvF,false);
			else
			    theAlgebra.makeEquivalentElementEquation(asvF,asvM,false);
		}
	}

	void equateTheCrossCousins(String sex){
		if (sex.equals("N")) {
			equateCrossCousins("M");
			equateCrossCousins("F");
		}else if (sex.equals("M")) {
			equateCrossCousins("M");
		}else if(sex.equals("F")) {
			equateCrossCousins("F");
		}
	}

	void equateCrossCousins(String sex){//AiI&EiI,AIi&EIi
		AlgebraSymbolVector asvF = new AlgebraSymbolVector();
		AlgebraSymbolVector asvM = new AlgebraSymbolVector();
		asvF.addElement(theAlgebra.getGenerators(Bops.DOWN,"F"));
		asvM.addElement(theAlgebra.getGenerators(Bops.DOWN,"M"));
		AlgebraSymbol id1 = (AlgebraSymbol) theAlgebra.getFocalElements(sex).elementAt(0);
		AlgebraSymbol id2 = null;
		if (sex.equals("M")) id2 = (AlgebraSymbol)theAlgebra.getFocalElements("F").elementAt(0);
		else id2 = (AlgebraSymbol)theAlgebra.getFocalElements("M").elementAt(0);
		asvF.addElement(id1);
		asvM.addElement(id1);
		asvF.addElement(id2);
		asvM.addElement(id2);
		if (sex.equals("F")) theAlgebra.makeEquivalentElementEquation(asvM,asvF,false);
		else theAlgebra.makeEquivalentElementEquation(asvF,asvM,false);
	}

	void addCrossCousinEquation(){
		addCrossCousinEquation("M");
		addCrossCousinEquation("F");
	}

	void addCrossCousinEquation(String sex){//EIG=i,AiP=I,
		AlgebraSymbol ch = theAlgebra.getGenerators(Bops.DOWN,sex);
		AlgebraSymbol pa = theAlgebra.getGenerators(Bops.UP,sex);
		AlgebraSymbol id1 = null;AlgebraSymbol id2 = null;
		id2 = (AlgebraSymbol)theAlgebra.getFocalElements(sex).elementAt(0);
		if (sex.equals("M")) id1 = (AlgebraSymbol) theAlgebra.getFocalElements("F").elementAt(0);
		else id1 = (AlgebraSymbol) theAlgebra.getFocalElements("M").elementAt(0);
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		AlgebraSymbolVector asv2 = new AlgebraSymbolVector();
		asv1.addElement(ch);
		asv1.addElement(id1);
		asv1.addElement(pa);
		asv2.addElement(id2);
		Equation eq = new Equation(asv1,asv2);
		theAlgebra._addEquation(eq);

	}

	void addParentChildProductEquation(){
		addParentChildProductEquation("M");
		addParentChildProductEquation("F");
	}

	void addParentChildProductEquation(String sex){//FSi=0,FDi=0,MDI=0,MSI=0
		AlgebraSymbol ch1 = theAlgebra.getGenerators(Bops.DOWN,sex);
		AlgebraSymbol ch2 = ch1.getOppositeSexGenerator();
		AlgebraSymbol pa = theAlgebra.getGenerators(Bops.UP,sex);
		AlgebraSymbol id = null;
		if (sex.equals("M")) id = (AlgebraSymbol) theAlgebra.getFocalElements("F").elementAt(0);
		else id = (AlgebraSymbol) theAlgebra.getFocalElements("M").elementAt(0);
		AlgebraSymbol ch = null;
		AlgebraSymbolVector asv2 = new AlgebraSymbolVector();
		AlgebraSymbolVector sp = theAlgebra.getGenerators(Bops.SPOUSE);
		if (sp.size() == 0) asv2.addElement(theAlgebra.getElement("0"));
		else {
			for (sp.reset();sp.isNext();){
				AlgebraSymbol as = sp.getNext();
			    if (as.getSex().equals("N") || as.getSex().equals(sex)) {
					asv2.addElement(as);
					break;
			    }
			}
		}
		//asv2.addElement(getElement("0"));
		for (int j=0;j<2;j++){
			AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
			if (j == 0) ch = ch1;
			else ch = ch2;
			asv1.addElement(pa);
			asv1.addElement(ch);
			AlgebraSymbolVector test1 = new AlgebraSymbolVector();
			test1.addElement(ch);
			asv1.addElement(id);
			test1.addElement(id);
			AlgebraSymbolVector test = (AlgebraSymbolVector) asv1.clone();
			AlgebraPath ap = new AlgebraPath(test,true);
			AlgebraSymbolVector asv3 = ap.getReducedProductPath();
			Debug.prout(0," test "+test.toString()+ " asv3 "+asv3.toString());
			if (test.toString().equals(asv3.toString())){//non reducible product
				ap = new AlgebraPath(asv1,true);
				asv3 = ap.getReducedProductPath();
				Equation eq = new Equation(asv3,asv2);
				//System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx new equation "+eq);
				theAlgebra._addEquation(eq);
			}else {//reducible product so need rewrite rule
				ap = new AlgebraPath(test1,false);
			    asv3 = ap.getReducedProductPath();
				asv3.addToEnd(pa);
				ListVector lv1 = new ListVector();
				ListVector lv2 = new ListVector();
				for (int i=0;i<asv3.size();i++) lv1.addElement((asv3.elementAt(i)));
				for (int i=0;i<asv2.size();i++) lv2.addElement((asv2.elementAt(i)));
				theAlgebra.putInRewriteProductRule(lv1,lv2);
			}
		}
	}

	void addSibChildProducts(){
		addSibChildProducts("M");
		addSibChildProducts("F");
	}

	void addSibChildProducts(String sex){//ZAi = Ai, etc.based on MER rule
		MakeEquivalentRule merRule = (MakeEquivalentRule)RuleFactory.getRule(RuleFactory.MAKEEQUIVALENTRULE);
		if (merRule == null) return;
		ListVector equiv = merRule.getEquivalentElements();
		AlgebraSymbol sibL = null; AlgebraSymbol sibR = null;AlgebraSymbol ft = null;
		if (sex.equals("M")) {
			sibL = theAlgebra.getGenerators(Bops.LEFT,"F");
			sibR = theAlgebra.getGenerators(Bops.RIGHT,"F");
			ft = theAlgebra.getFocalElement("F");
		} else {
		    sibL = theAlgebra.getGenerators(Bops.LEFT,"M");
		    sibR = theAlgebra.getGenerators(Bops.RIGHT,"M");
			ft = theAlgebra.getFocalElement("M");
		}
		ListVector lv1 = new ListVector();
		ListVector lv2 = new ListVector();
		for (equiv.reset();equiv.isNext();){
			ListVector lv = (ListVector)equiv.getNext();
			Debug.prout(0," now at lv "+lv);
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) lv.elementAt(0);//  si ch i
			if (asv1.getLast().getArrowType() != Bops.DOWN ||
				//!asv1.getLast().getSex().equals(sex) ||
				asv1.getSymbol(asv1.size()-2).getArrowType() != Bops.IDENTITY ||
				asv1.getSymbol(asv1.size()-2).getSex().equals(sex)) continue;
			AlgebraSymbolVector asv2 = (AlgebraSymbolVector)lv.elementAt(1);
			if (asv2.getLast().getArrowType() != Bops.DOWN ||
				//asv2.getLast().getSex().equals(sex) ||
				asv2.getSymbol(asv2.size()-2).getArrowType() != Bops.IDENTITY ||
				asv2.getSymbol(asv2.size()-2).getSex().equals(sex)) continue;
			AlgebraSymbolVector asv = null;
			for (int k=0;k<2;k++){
				if (k==0) asv = asv1;
				else asv = asv2;
				for (int i=0;i<2;i++){
					lv1.clear();lv2.clear();
					if (i==0) lv1.addElement(sibL);
					else lv1.addElement(sibR);
					for (int j=0;j<asv.size();j++) lv1.addElement((asv.elementAt(j)));
					for (int j=0;j<asv.size();j++) lv2.addElement((asv.elementAt(j)));
					theAlgebra.putInRewriteProductRule(lv1,lv2);
					//System.out.println("LLLLLLLLLLLLLLLLL lv1 "+lv1+" lv2 "+lv2);
				}
				lv1.set(0,ft);//assumes i,I are sibling terms for opposite sex ego
				theAlgebra.putInRewriteProductRule(lv1,lv2);
				//System.out.println("LLLLLLLLLLLLLLLLL lv1 "+lv1+" lv2 "+lv2+" last "+asv1.getLast());
			}

		}
	}

	void addParentChildProducts(){
	    addParentChildProducts("M");
		addParentChildProducts("F");
	}

	void addParentChildProducts(String sex){//P(Si_&Di_)=0, etc., uses MER rule
		MakeEquivalentRule merRule = (MakeEquivalentRule)RuleFactory.getRule(RuleFactory.MAKEEQUIVALENTRULE);
		if (merRule == null) return;
		ListVector equiv = merRule.getEquivalentElements();
		AlgebraSymbol pa = theAlgebra.getGenerators(Bops.UP,sex);
		ListVector lv1 = new ListVector();
		ListVector lv2 = new ListVector();
		AlgebraSymbolVector sp = theAlgebra.getGenerators(Bops.SPOUSE);
		if (sp.size() == 0) lv2.addElement(theAlgebra.getElement("0"));
		else {
			for (sp.reset();sp.isNext();){
				AlgebraSymbol as = sp.getNext();
			    if (as.getSex().equals("N") || as.getSex().equals(sex)) {
					lv2.addElement(as);
					break;
			    }
			}
		}
		for (equiv.reset();equiv.isNext();){
			ListVector lv = (ListVector)equiv.getNext();
			//System.out.println(" now at lv "+lv);
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) lv.elementAt(0);
			if (asv1.getLast().getArrowType() != Bops.DOWN ||
				asv1.getSymbol(asv1.size()-2).getArrowType() != Bops.IDENTITY ||
				asv1.getSymbol(asv1.size()-2).getSex().equals(sex)) continue;
			AlgebraSymbolVector asv2 = (AlgebraSymbolVector)lv.elementAt(1);
			if (asv1.size() != asv2.size()) continue;
			if (asv2.getLast().getArrowType() != Bops.DOWN ||
				asv2.getSymbol(asv1.size()-2).getArrowType() != Bops.IDENTITY ||
				asv2.getSymbol(asv1.size()-2).getSex().equals(sex)) continue;
			AlgebraSymbolVector asv = null;
			for (int j=0;j<2;j++){
				if (j==0) asv = asv1;
				else asv = asv2;
				lv1.clear();
				lv1.addElement(pa);
				for (int i=0;i<asv.size();i++) lv1.addElement((asv.elementAt(i)));
				theAlgebra.putInRewriteProductRule(lv1,lv2);
				//System.out.println("in parent child=0 "+" lv1 "+lv1+" lv2 "+lv2);
			}
		}
	}

	void addChildSibParentProducts(){
	    addChildSibParentProducts("M");
	    addChildSibParentProducts("F");
	}

	void addChildSibParentProducts(String sex){//ABP = B (SB+F=B+) etc
		//AlgebraSymbol pa = getGenerators(Bops.UP,sex);
		AlgebraSymbol ch = theAlgebra.getGenerators(Bops.DOWN,sex);
		AlgebraSymbol sibO = theAlgebra.getGenerators(Bops.LEFT,sex);
		AlgebraSymbol sibO1 = sibO.getOppositeSexGenerator();
		AlgebraSymbol sibY = theAlgebra.getGenerators(Bops.RIGHT,sex);
		AlgebraSymbol sibY1 = sibY.getOppositeSexGenerator();
		AlgebraSymbol pa = theAlgebra.getGenerators(Bops.UP,sex);
		AlgebraSymbol pa1 = pa.getOppositeSexGenerator();
		ListVector lv1 = new ListVector();ListVector lv11 = new ListVector();
		ListVector lv2 = new ListVector();ListVector lv21 = new ListVector();
		lv1.addElement(ch);
		lv1.addElement(sibO);
		lv1.addElement(pa);
		lv2.addElement(sibO);
		theAlgebra.putInRewriteProductRule(lv1,lv2);
		lv11.addElement(ch);
		lv11.addElement(sibO1);
		lv11.addElement(pa1);
		lv21.addElement(sibO);
		theAlgebra.putInRewriteProductRule(lv11,lv21);
			//System.out.println(" lv stufff lv1 "+lv1+" lv2 "+lv2);
		lv1.removeElementAt(1);
		lv1.insertElementAt(sibY,1);
		lv2.removeElementAt(0);
		lv2.insertElementAt(sibY,0);
		//	System.out.println(" lv stufff2 lv1 "+lv1+" lv2 "+lv2);
		theAlgebra.putInRewriteProductRule(lv1,lv2);
		lv11.removeElementAt(1);
		lv11.insertElementAt(sibY1,1);
		lv21.removeElementAt(0);
		lv21.insertElementAt(sibY,0);
		theAlgebra.putInRewriteProductRule(lv11,lv21);
	}





	public static final int AKT = 1;
	public static final int SHIPIBO = 2;
	public static final int TROB = 4;
	public static final int TONGAN = 8;
	public static final int SIBGEN = 16;

	public static final int CLASSIFICATORY = (TROB|TONGAN|SIBGEN);
	public static final int DESCRIPTIVE = (AKT|SHIPIBO);

}

/*	void addMixedSexSameArrowEquationsXXXXXXXXXXXX() {
	    AlgebraSymbolVector gens = getGenerators();
	    AlgebraSymbolVector usedG = new AlgebraSymbolVector(3,1);
	//	System.out.println(" GENS "+getGenerators());
		AlgebraSymbolVector upDownGens = new AlgebraSymbolVector(3,1);
	    for (gens.reset();gens.isNext();){
	        AlgebraSymbol g = gens.getNext();
			if (g.getArrowType() == Bops.UP) upDownGens.addToEnd(g);
			else if (g.getArrowType() == Bops.DOWN) upDownGens.addToBeginning(g);
	    }
		for (upDownGens.reset();upDownGens.isNext();){
	        AlgebraSymbol g = upDownGens.getNext();
			int aType = g.getArrowType();
			//System.out.println(" g is "+g);
	        if (usedG.indexOf(g) == -1) {
	            AlgebraSymbol g1 = g.getOppositeSexGenerator();
		  //  System.out.println("THE THETHETH "+g+" g1 "+g1);
	            usedG.addElement(g);
	            usedG.addElement(g1);
	            //if (g.getArrowType() == Bops.UP ||g.getArrowType() == Bops.DOWN) {
	            if (g.getArrowType() == aType) {
	                Equation eq = new Equation(g1,g).addLhs(g).addRhs(g);
						//System.out.println(" eq added "+eq);
	                addEquation(eq);
	                eq =  new Equation(g,g1).addLhs(g1).addRhs(g1);
						//System.out.println(" eq added "+eq);
	                addEquation(eq);
	            }
	        //System.out.println("g = "+g+" g1= "+g1);
            }
	    }
	}

*/
/*
	/** if xy=zw is an equation, X(Z) is opposite sex generator for x(z) then
	* make Xy=Zw an equation
	*/
	/*
	void addAnalogousEquationsYYYYYYYYYYYYY() {//original procedure use with shipibo
	    EquationVector eqs = getEquations();
	    EquationVector newEqs = new EquationVector();
	    Equation e = new Equation();
	    for (eqs.reset1();eqs.isNext();){
	        Equation eq = eqs.getNext();
	     //   if (isSexGeneratorEquation(eq)) {
//System.out.println("SEX EQ "+eq);continue;
	        AlgebraSymbolVector lhs = (AlgebraSymbolVector) eq.getLhs().clone();
	        AlgebraSymbolVector rhs = (AlgebraSymbolVector)eq.getRhs().clone();
	        AlgebraSymbol as = lhs.getLast();
	        if (!(eq.isIdEquation()) && !(eq.isZeroEquation())
	        && (as != null)&& (lhs.size() != rhs.size())){
	            AlgebraSymbol as1 = as.getOppositeSexGenerator();
   // System.out.println("START eq "+eq+" left as "+as+" as1 "+as1);
				if (as1 != null) {
					lhs.setElementAt(as1,0);
					AlgebraPath ap = new AlgebraPath();
					ap.reducePath(lhs);
                    AlgebraSymbolVector lhs1 = ap.getReducedProductPath();
					if (lhs1.size() == 1) continue;
					as = rhs.getLast();
					as1 = as.getOppositeSexGenerator();
  //   System.out.println(" right as "+as+" as1 "+as1);

					if (as1 != null) {
						rhs.setElementAt(as1,0);
						ap = new AlgebraPath();
						ap.reducePath(rhs);
						AlgebraSymbolVector rhs1 = ap.getReducedProductPath();
						e = new Equation(lhs1,rhs1);
						newEqs.addElement(e);
						// addEquation(lhs,rhs1);
						// System.out.println("START lhs "+lhs+"END lhs1 "+lhs1+" rhs "+rhs+" rhs1 "+rhs1);
					}
				}
	        }
	    }
	    for (newEqs.reset1();newEqs.isNext();){
	        e = newEqs.getNext();
//System.out.println(" NEWEQ "+e);
	       // _addEquation(e);
			setAddReciprocalEquations(RE_NO);
			addEquation(e);
			setAddReciprocalEquations(RE_YES);
	    }
	}

*/
