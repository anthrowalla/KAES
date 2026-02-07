import java.util.*; 

/* Revision History
* 31-10 MF Removed debugging exception generated in pushAlgebra.
* 31-10 MF Replaced all System.out.println with Debug(zerolevel,Message) construction
* 2/14 DR add null test "if (rcp != null)" to procedures "getUpOrDownArrows" and "isArrow"
* 2/22 DR moved procedure enterSpouseTerm() from AopsOps; added string argument to the procedure
* modified enterSpouseTerm() procedure in AopsOps to call algebra.enterSpouseTerm with automatically generated spouse symbols
* 7-3-01 MF added a static variable, theOnlyAlgebra and removed the algebras vector
*            problems that could stem from this emerge mainly in the new Algebra method, where the new
*			 algebra becomes the current algebra. However, this seems to be appropriate in most cases.
* 7-3-01 MF modified clone to preserve the current Algebra.
* 5/29 DR added procedures getAddReciprocalEquations
* 7/6 DR deleted getCurrent and pushCurrent in procedure clone
* 7/7 DR added substitute and isomorphicClone procedures, added
* getValues() procedure
* 7/12 DR included deepClone in isomorphicClone procedure
* 7/24 DR added procedure 	Equation substitute(Equation eq, AlgebraSymbolVector anew)
* added procedure constructIDEquations(); added procedure makeAlgebraJoin(Algebra a)
* added procedure addSexIdEquations
* 9/24 DR added algc.identityElement.setReciprocal(algc.identityElement);
* to isomorphicClone
* 9/28 DR corrected error in isArrow(); modified createReciprocalEquation(Equation e) to
* allow for equations having elements with different sexes
* 9/30 DR added procedure addCrossSexEquations() to construct equations needed when making
* join of two algebras, each with elements of a single, but opposite, sex
* 9/30 DR added procedure addAnalogousEquations() to construct equations of form
* aBCD=eF when ABCD=EF is already an equation and a,e have opposite sex and same
* direction as A, E
* added procedure addMixedSexSameArrowEquations()to include equations of form Pp = PP
* added procedure addSexIdEquations() for products of form IdX = 0 when Id is a pseudoidenity and
* sex of ID is not the same as sex of X; procedure not currently used
* added procedure constructIDEquations() to make explicit versions of implicit id equations
* 10/14 DR added procedure removeRedundancy()
* 10/29 DR added condition !as.isIdentityElement() to MALE and FEMALE case in isArrow()
* 11/7 DR added procedure hasSexMarkedGenerators(), modified makeAlgebraJoin to cover case
* where two sex marked algebras with identites are joined but the join has a single identity element
* 11/11 DR cleaned up code for algebraJoin; modified sexGenerator to accomodate adding sex
* generators when algebra has sex marked elements already; dddd procedure addLeftSexGeneratorProducts
* to add equations of form FX =Y where X is male marked and Y is female marked
* 11/22 DR added procedures getRecursiveEquations(), getZeroEquations()
* 8/23 DR modified getDisplayGenerators() to pass sex generators for displaying
* 8/26 DR added "N" as possible sex generator in getSexGenerators()
*/


public class Algebra implements ToXML {
	/** static list of all Algebras
	*/
//	public static AlgebraVector algebras=new AlgebraVector();
	public static Algebra theOnlyAlgebra = null;
   public long serialNumber = 1;
	public StringVector suppressEquations = new StringVector();

   public void setSerialNumber(long x) {
	  serialNumber = x;
   }

   public long getSerialNumber() {
	  return serialNumber;
   }
   public void setSuppressEquations(StringVector eqList) {
	  suppressEquations = eqList;
   }

   public StringVector getSuppressEquations() {
	  return suppressEquations;
   }

	/** static procedure for getting the current algebra in a developmental sequence
	* @return the current stage
	*/
	public static Algebra getCurrent() {
		return theOnlyAlgebra;
	//	if (algebras.size() > 0) return algebras.getLast();
	//	else return null;
	}

	public static Algebra popCurrent() {
		System.out.println("Call to Algebra.popCurrent... shouldn't call this routine");
		return theOnlyAlgebra;
	}
	public static Algebra pushCurrent(Algebra a) {
		theOnlyAlgebra = a;
	Debug.prout(0,"+++++++++++++++++Pushing a new algebra++++++++++++++++");
		return a;
	}

	public final static int RE_YES = 1;
	public final static int RE_NO = 0;
	public final static int RE_ASK = 2;

	public int addReciprocalEquations = RE_YES;

	public void setAddReciprocalEquations(int add) {
		addReciprocalEquations = add;
	}

	public int getAddReciprocalEquations() {
	    return addReciprocalEquations;
	 }

	/** the identity equation. The equation may contain unbound variables
	*/
	AlgebraSymbolVar identity = null;  // default settings, unbound

	/**
	*/
	public AlgebraKludge theKludge = new AlgebraKludge();

	/** reference name of this algebra
	*/
	String name="";

	/** replace ith symbol, amod, in Symbols by ith symbol in anew
	* @amod symbol to be replaced
	* @anew vector of replacement symbols
	* @return ith symbol in anew where amod is ith symbol in Symbols
	*/
	AlgebraSymbol substitute(AlgebraSymbol amod,AlgebraSymbolVector anew) {
		return substitute(amod,anew,theSymbols);
	    }

	/** replace ith symbol, amod, in amod by ith symbol in anew
	* @amod symbol to be replaced
	* @anew vector of replacement symbols
	* @aold vector of original symbols
	* @return ith symbol in anew where amod is ith symbol in aold
	*/

	AlgebraSymbol substitute(AlgebraSymbol amod,AlgebraSymbolVector anew,AlgebraSymbolVector aold) {
       /* if (amod.getClass().toString().equals("AlgebraSymbolVar")) {
         //   unbind();
            return amod;
        }*/
        if (amod.toString().equals("")) return amod;
        else {
			if (aold.indexOf(amod) == -1) return amod;
			else return (AlgebraSymbol) anew.elementAt(aold.indexOf(amod));
	    }
	}


	AlgebraSymbolVector substitute(AlgebraSymbolVector amod,AlgebraSymbolVector anew){
		AlgebraSymbolVector ret = new AlgebraSymbolVector(5,1);
		for (amod.reset();amod.isNext();) {
		    ret.addToEnd(substitute(amod.getNext(),anew));
		}
		return ret;
	}

	AlgebraSymbolVector substitute(AlgebraSymbolVector amod,AlgebraSymbolVector anew,
		    AlgebraSymbolVector aold){
		AlgebraSymbolVector ret = new AlgebraSymbolVector(5,1);
		for (amod.reset();amod.isNext();) {
		    ret.addToEnd(substitute(amod.getNext(),anew,aold));
		}
		return ret;
	}



	Equation substitute(Equation eq, AlgebraSymbolVector anew){
	    return (new Equation(substitute(eq.lhs,anew),substitute(eq.rhs,anew)));
	}

	Equation substitute(Equation eq, AlgebraSymbolVector anew, AlgebraSymbolVector aold){
	    return (new Equation(substitute(eq.lhs,anew,aold),substitute(eq.rhs,anew,aold)));
	}

	EquationVector substitute(EquationVector emod, AlgebraSymbolVector anew) {
		return substitute(emod,anew,theSymbols);
	}

	EquationVector substitute(EquationVector emod, AlgebraSymbolVector anew,
				AlgebraSymbolVector aold) {
	    EquationVector ret = new EquationVector(5,1);
	    boolean startFlag = true;
	    Equation eq = new Equation();
	    for (emod.reset();emod.isNext();) {
	        if (startFlag) {
	            eq = emod.getFirst();
	            startFlag = false;
	        }
	        else eq = emod.getNext();
	        Equation enew = substitute(eq,anew,aold);
	        //Equation enew = new Equation(substitute(eq.lhs,anew),substitute(eq.rhs,anew));
	        ret.addElement(enew);
	    }
	    return ret;
	}


	public Object isomorphicClone() {
	    unbind();
		Algebra algc = new Algebra();
		algc.nGenerators = nGenerators;
	//	algc.isoGenNum = isoGenNum;
		algc.rules = (RuleVector) rules.clone();
        algc.unbindSymbols = unbindSymbols;
      // System.out.println("UNBIND SYMBOLS "+algc.unbindSymbols);
		algc.theSymbols = (AlgebraSymbolVector) theSymbols.deepClone();
		algc.identityElement = substitute(identityElement,algc.theSymbols);
		algc.identityElement.setReciprocal(algc.identityElement);
      algc.focalElements = substitute(focalElements,algc.theSymbols);
        //System.out.println("start eqs "+structuralEquations);
		algc.structuralEquations = substitute(structuralEquations,algc.theSymbols);
        //System.out.println("end eqs" +algc.structuralEquations);
		algc.name = name;
		algc.theKludge = (AlgebraKludge) theKludge.clone();
		algc.identity = (AlgebraSymbolVar) identity.clone();
		algc.identity.setValue(algc.getIdentityElement());
		//algc.identity.setValue(getIdentityElement());
		algc.addReciprocalEquations = addReciprocalEquations;
		algc.suppressEquations = suppressEquations;
		return algc;
	}

        public String toXML() {
     unbind();
      StringBuffer out = new StringBuffer(XMLIndent.pp("<Algebra>"));
 /*     XMLIndent.increment();
      out.append(pp("<AlgebraName>"+name+"</AlgebraName>"));
      out.append(XMLIndent.pp("<Symbols>"));
      XMLIndent.increment();
      out.append(theSymbols.toXML());
      XMLIndent.decrement();
      out.append(XMLIndent.pp("</Symbols>"));
      out.append(theKludge.toXML());
      out.append(pp("<IdentityElement>"+identityElement.toXML()+"</IdentityElement>"));
      out.append(pp("<FocalElements>"+focalElements.toXML()+"</FocalElements>"));
      out.append(pp("<UnbindSymbols>"+unbindSymbols.toXML()+"</UnbindSymbols>"));
      out.append(rules.toXML())); // should be ok with lable since only rules
      out.append(structuralEquations.toXML()); // should be ok with lable since only equations

      out.append(XMLIndent.pp("<addReciprocalEquations>"+addReciprocalEquations+"</addReciprocalEquations>"));
      out.append(XMLIndent.pp("<nGenerators>"+nGenerators+"</nGenerators>"));

      XMLIndent.decrement();
*/
      out.append(XMLIndent.pp("</Algebra>"));
      return out.toString();
    }

	public Object clone() {
	    unbind();
		//Algebra a = getCurrent();
		Algebra algc = new Algebra();
		//pushCurrent(a);
		algc.nGenerators = nGenerators;
	//	algc.isoGenNum = isoGenNum;
		algc.rules = (RuleVector) rules.clone();
		algc.unbindSymbols = (AlgebraSymbolVector) unbindSymbols.clone();
		algc.identityElement = identityElement;
		algc.focalElements = (AlgebraSymbolVector) focalElements.clone();
		algc.name = name;
		algc.theKludge = (AlgebraKludge) theKludge.clone();
		algc.identity = identity;
		algc.addReciprocalEquations = addReciprocalEquations;
		algc.suppressEquations = suppressEquations;

		return algc;
	}

	/** initialise the algebra with a basic set of equations, xI = x and x0 = 0
	*/
	void defaultEquations() {
		// investigate moving to the addIdentityElement routine;

		AlgebraSymbolVar beta = new AlgebraSymbolVar(); // default settings, unbound
		addEquation(beta,Algebra.getCurrent().getElement("0")).addLhs("0"); // x0 = 0
		addEquation(Algebra.getCurrent().getElement("0"),Algebra.getCurrent().getElement("0")).addLhs(beta); // 0x = 0
		Algebra.getCurrent().getElement("0").theReciprocal = Algebra.getCurrent().getElement("0");
	}

	/** Initalise algebra by adding to the static list of algebra stages and setting up
	* basic unbound identity operator equations and null operator equations
	*/
	public Algebra() {
		//algebras.addElement(this);
		theOnlyAlgebra = this;
		defaultEquations();
		initRules();
	}

	/** Intialise with name in addition to adding to the static list of algebra stages\
	* and setting up basic unbound identity operator equations and null operator equations
	* @param name of algebra
	*/
	public Algebra(String n) {
		theOnlyAlgebra = this;
		//algebras.addElement(this);
		name = n;
		defaultEquations();
		initRules();
	}

	/** the symbols over which this algebra ranges
	*/
	AlgebraSymbolVector theSymbols = new AlgebraSymbolVector(5,1);

	/** Structural equations of algebra
	*/
	EquationVector structuralEquations  = new EquationVector(10,10);  // st_equatns

	/** focal elements of algebra if any
	*/
	AlgebraSymbolVector focalElements  = new AlgebraSymbolVector(1,1); // focTerm

	/** identity elements of algebra, if any
	*/
	// AlgebraSymbolVector identityElements  = new AlgebraSymbolVector(1,1); // ident elements
	AlgebraSymbol identityElement  = null; // ident elements

	/**
	*/
//	AlgebraSymbolVector affinalElements  = new AlgebraSymbolVector(3,1); // affTerms

	/** elements that are made equivalent
	*/
//	AlgebraSymbolVector equatedElements  = new AlgebraSymbolVector(5,1); // eqtId eqtFoc eqtElements

	/** things to unbind in unbind()
	*/
	AlgebraSymbolVector unbindSymbols  = new AlgebraSymbolVector(2,1);

	/** Rules of the algebra
	*/
	RuleVector rules = new RuleVector(6,1); // rules

	public RuleVector getRules() {
		return rules;
	}

	public void initRules() {
	}

	public void setRules(RuleVector r) {
		rules=r;
	}

	public void addRule(Rule r) {
		rules.addElement(r);
	}

	/**
	*/
	// int isoGenNum = 0;

	/** number of generators for this algebra
	*/
	int nGenerators=0;

	/** Set something to be unbound at end of evaluation cycle
	*/
	public void setUnBind(AlgebraSymbolVar a) {
		unbindSymbols.addToEnd(a);
	}

	/** unbinds bound variables after an evaluation cycle
	*/
	public void unbind() {
//	    System.out.println("UNBINDSYMBOLS "+unbindSymbols);
		for (int i = unbindSymbols.size()-1;i>=0;i--)
		    ((AlgebraSymbolVar) unbindSymbols.elementAt(i)).unBind();
	}

	/** return the Algebra Symbols this algebra ranges over
	*/
	public AlgebraSymbolVector getElements() {
		return theSymbols;
	}

	/** return the Algebra Values this algebra ranges over
	*/
	public StringVector getValues() {
	    AlgebraSymbolVector asv = getElements();
	    StringVector ret = new StringVector(5,1);
	    for (asv.reset();asv.isNext();) {
	        ret.addElement(asv.getNext().getValue());
	    }
		return ret;
	}

	/** return the focal elements of this Algebra, if any (empty vector means none)
	*/
	public AlgebraSymbolVector getFocalElements() {
		return focalElements;
	}

	/** return the sex specified focal elements of this Algebra, if any (empty vector means none)
	 *  @sex String sex of focal element
	*/
	public AlgebraSymbolVector getFocalElements(String sex) {
		AlgebraSymbolVector ret = new AlgebraSymbolVector(1,1);
		for (focalElements.reset();focalElements.isNext();){
			AlgebraSymbol as = focalElements.getNext();
			if (as.getSex().equals(sex)) ret.addElement(as);
		}
		return ret;
	}
	/** return the sex specified focal element of this Algebra, if any
	 *  @sex String sex of focal element
	*/
	public AlgebraSymbol getFocalElement(String sex) {
		for (focalElements.reset();focalElements.isNext();){
			AlgebraSymbol ret = focalElements.getNext();
			if (ret.getSex().equals(sex)) return ret;
		}
		return null;
	}

	/** return true if this Algebra has focal element(s)
	*/
	public boolean hasFocalElements() {
		return (focalElements.size() > 0);
	}

	/** return the identity elemnts of this Algebra, if any  (empty vector means none)
	*/
	public AlgebraSymbol getIdentityElement() {
		return identityElement;
	}

	/**
	*/
	public AlgebraSymbol findElement(AlgebraSymbol a) {
		Debug.prout("Finding!");
		int ndx = ((AlgebraSymbolVector)theSymbols).locateSymbol((AlgebraSymbol)a);
		if (ndx == -1) return null;
		Debug.prout("Found!");
		return (AlgebraSymbol) theSymbols.getSymbol(ndx);
	}

	/**
	*/
	public AlgebraSymbol findElement(String a) {
		return findElement(new AlgebraSymbol(a));
	}

	/** find a symbol of the algebra given its name, add it to the elements
	* if not present. Always succeeds.
	* @param a the element to find or add to the Algebra
	*/
	public AlgebraSymbol getElement(String a) {
		AlgebraSymbol n;
		if ((n = findElement(new AlgebraSymbol(a))) == null) {
			n = new AlgebraSymbol(a);
			theSymbols.addToEnd(n);
		}
		return n;
	}

	/** find a symbol of the algebra given its name, add it to the elements
	* if not present. Always succeeds.
	* @param a the AlgebraSymbol to find or add to the Algebra
	*/
	public AlgebraSymbol getElement(AlgebraSymbol a) {
		AlgebraSymbol n;
		if (findElement(a) == null) {
			theSymbols.addToEnd(a);
		}
		return a;
	}

	/** adds a generator to the list
	*/
	public void addGenerator(AlgebraSymbol a) {
		if (a.isGenerator()) return;
	   if (a.isSexGenerator() || a.getValue().equals("N"))
		  a.setArrowType(Bops.SEXGEN);
		a.setGenerator(++nGenerators);
	}

	/** returns the generator
	* @arrow arrow direction for generators
	* @sex sex for generators
	*/
	public AlgebraSymbol getGenerators(int arrow, String sex){
		AlgebraSymbolVector gens = getGenerators();
		for (gens.reset();gens.isNext();){
		    AlgebraSymbol as = gens.getNext();
			if (as.getArrowType() == arrow && as.getSex().equals(sex)) return as;
		}
		return null;
	}


	/** returns the generators in a new list
	* @arrow arrow direction for generators
	*/
	public AlgebraSymbolVector getGenerators(int arrow) {
		AlgebraSymbolVector a = getElements();
		int sz = a.size();
		AlgebraSymbolVector v = new AlgebraSymbolVector(sz);
		for(int i=0;i<sz;i++) {
			if (a.getSymbol(i).isGenerator() && a.getSymbol(i).getArrowType() == arrow)
			//isArrow(a.getSymbol(i),arrow))
			 v.addToEnd(a.getSymbol(i));
		}
		return v;
	}

	/** returns the generators in a new list
	* @sex sex of generators
	*/
	public AlgebraSymbolVector getGenerators(String sex) {
		AlgebraSymbolVector a = getElements();
		int sz = a.size();
		AlgebraSymbolVector v = new AlgebraSymbolVector(sz);
		for(int i=0;i<sz;i++) {
			if (a.getSymbol(i).isGenerator() && a.getSymbol(i).getSex().equals(sex))
			//isArrow(a.getSymbol(i),arrow))
			 v.addToEnd(a.getSymbol(i));
		}
		return v;
	}


	/** returns the generators in a new list
	*/
	public AlgebraSymbolVector getGenerators() {
		AlgebraSymbolVector a = getElements();
		int sz = a.size();
		AlgebraSymbolVector v = new AlgebraSymbolVector(sz);
		for(int i=0;i<sz;i++) {
			if (a.getSymbol(i).isGenerator()) v.addToEnd(a.getSymbol(i));
		}
		return v;
	}

	/** returns the generators in a new list
	*/
	public ListVector getDisplayGenerators() {
		AlgebraSymbolVector a = getElements();
		int sz = a.size();
		AlgebraSymbolVector v1 = new AlgebraSymbolVector(sz);
		AlgebraSymbolVector v2 = new AlgebraSymbolVector(2);
        ListVector ret = new ListVector(2);
		for(int i=0;i<sz;i++) {
			if (a.getSymbol(i).getValue().equals("&")) continue;
			if (a.getSymbol(i).isSexGenerator())
				v2.addToEnd(a.getSymbol(i));
			else if (!a.getSymbol(i).isZeroElement())
				v1.addToEnd(a.getSymbol(i));
		}
        ret.addElement(v1);
        if (v2.size() > 0) ret.addElement(v2);
		return ret;
	}

	/** returns the list of structural equations
	* @return the list of structural equations
	*/
	public EquationVector getEquations() {
		return structuralEquations;
	}

	/** test if equation is present in the algebra
	* @param e - the equation to look for
	* @return true is equation is in list of structural equations
	*/
	public boolean isEquation(Equation e) {
		if (structuralEquations.indexOf(e) != -1) return true;
		else return false;
	}

	public boolean hasIdentityElement() {
		if (identityElement != null) return true;
		else return false;
	}
	/** binds the identity element  and adds to list of identity elements
	* @param a new identity element
	* and updates a to be a focal element
	*/
	public void addIdentityElement(AlgebraSymbol a) {
		if (!a.isGenerator()) addGenerator(a); // keep in mind if we should do this or not ****
		if (a.isIdentityElement()) return;

		if (!hasIdentityElement()) {
			identity = new AlgebraSymbolVar(false);
			AlgebraSymbolVar alpha = new AlgebraSymbolVar(); // default settings, unbound
			addEquation(alpha,alpha).addLhs(identity); // xI = x

			addEquation(identity,alpha).addLhs(alpha); // Ix = x
		}

		identityElement = a;
		a.setIdentityElement();
		a.setReciprocal(a);
		// might need to do a little checking here is already exists a different identity element
		addFocalElement(a);
		identity.setValue(a);
	}

	/** add focal element to list and updates a to be a focal element
	* @param a new focal element
	*/
	public void addFocalElement(AlgebraSymbol a) {
		if (a.isFocalElement()) return;
		focalElements.addToEnd(a);
		a.setFocalElement(true);
	}

	/* Should have utility routine that reestablishes the reduced nature of all
	* Equations. Could be called after addition based on preference or
	* as a menu item ****
	*/

	/** adds placeholder equation for this algebra
	* @return the Equation
	*/
	public Equation addEquation() {
		Equation e = new Equation();
		return addEquation(e); // possible problem for future hash???
	}

	/** Calculate reciprocal equation
	* @param e equation to find reciprocal for
	* @return reciprocal equation for parameter equation
	*/
	public ListVector createReciprocalEquation(Equation e) {
		ListVector ret = new ListVector();
		//AlgebraSymbolVector el = null;
		//AlgebraSymbolVector er = null;
		ListVector el = null;
		ListVector er = new ListVector();
		//System.out.println("RECIPROCAL OF EQUATION e "+e);
		if (e.getLhs().getSymbol(0).getValue().equals("0") ||
		e.getLhs().size() == 0) return ret;//0=X
		if (!e.getRhs().getFirst().isZeroElement() &&
		!e.getRhs().getFirst().isFocalElement() &&
		getFocalElements().size() > 1 &&
		e.getLhs().getFirst().getArrowType() == Bops.SPOUSE &&
		!e.getLhs().getFirst().getSex().equals("N") &&
		!e.getRhs().getFirst().isFocalElement()) {//XSp=Y-->XSpft=Yft
			AlgebraSymbolVector lhs = (AlgebraSymbolVector)e.getLhs().clone();
			AlgebraSymbolVector rhs = (AlgebraSymbolVector)e.getRhs().clone();
			AlgebraSymbolVector fts = getFocalElements();
			AlgebraSymbol ft = null;
			for (fts.reset();fts.isNext();){
			    ft = fts.getNext();
				if (!ft.getSex().equals(e.getLhs().getFirst().getSex())) break;
			}
			lhs.addToBeginning(ft);
			rhs.addToBeginning(ft);
			el = lhs.reciprocal();
			er = rhs.reciprocal();
			//System.out.println("qqqqqqqqqqqqqqqq el "+el+" er "+er +" e "+e+" lhs "+lhs +" rhs "+rhs);
		} else if (e.getLhs().sameSex()){//AB = CD --> Br Ar=Dr Cr
			if (e.getRhs().getSymbol(0).getValue().equals("0")||
			(e.getRhs().sameSex() &&
			e.getLhs().getSymbol(0).getSex().equals(e.getRhs().getSymbol(0).getSex()))||
			e.getRhs().getSymbol(0).isIdentityElement()){
				el = e.getLhs().reciprocal();
				er = e.getRhs().reciprocal();
			} else if (!e.getRhs().sameSex()){//FF = FM --> DD = DS
				String sex = e.getLhs().getSymbol(0).getSex();
				el = e.getLhs().reciprocal(sex);
				er = e.getRhs().reciprocal(sex);
			}
		} else if (!e.getRhs().getSymbol(0).getValue().equals("0") &&
			    e.getRhs().sameSex()){//FM = FF --> DS = DD
			String sex = e.getLhs().getSymbol(0).getSex();
			//System.out.println(" eq "+e+" LHS "+e.getLhs());
			el = e.getLhs().reciprocal();
			if (getFocalElements().size() == 1)
			    er = e.getRhs().reciprocal(sex);//DR 5/28
			else {
				//er=e.getRhs().reciprocal(sex);
				er=e.getRhs().reciprocal();
			}
			//else er = e.getRhs().reciprocal();
		//System.out.println(" equation e "+e+" el "+el+" er "+er +" sex "+sex);
		} else if (!e.getRhs().getSymbol(0).getValue().equals("0")) {
			String sex = e.getRhs().getSymbol(0).getSex();
			el = e.getLhs().reciprocal();
			er = e.getRhs().reciprocal();
		//System.out.println(" equation e  "+e+" el "+el+" er "+er +" sex "+sex);
		} else if (e.getRhs().getSymbol(0).getValue().equals("0")){
			el = e.getLhs().reciprocal();
			er.addElement(e.getRhs());
		//System.out.println(" equation e  "+e+" el "+el+" er "+er);
		}
		if (el == null || er == null) return ret;
		//System.out.println(" el "+ el +" er "+ er);
		AlgebraSymbolVector recipl = new AlgebraSymbolVector();
		AlgebraSymbolVector recipr = new AlgebraSymbolVector();
		for (int i = 0; i < el.size();i++){
			recipl = (AlgebraSymbolVector) el.elementAt(i);
			if (er.size() < el.size()) {
			   recipr = (AlgebraSymbolVector) er.elementAt(0);
			}
			else {
			   recipr = (AlgebraSymbolVector) er.elementAt(i);
			}
			//Equation eq = new Equation(recip, (AlgebraSymbolVector) er.getNext());
			//if (Algebra.getCurrent().getEquations().indexOf(new Equation(recipl,recipr)) > -1) continue;
			recipl = recipl.reduce();
			//System.out.println("RRRRRRRRRRRRRRR recipl  "+recipl+" recipr "+recipr+" new "+new Equation(recipl,recipr));
			if (Algebra.getCurrent().getEquations().indexOf(new Equation(recipl,recipr)) > -1) continue;
			if (((AlgebraSymbol)recipl.elementAt(0)).isZeroElement()) continue;
			if (!((AlgebraSymbol)recipr.elementAt(0)).isZeroElement()){
			    recipr = recipr.reduce();
			} //else

			AlgebraPath p = new AlgebraPath(recipr,true);
			RuleVector rules = Algebra.getCurrent().getRules();
					//System.out.println(" in reduce "+p.toString());
			for (rules.reset();rules.isNext();){
				Rule rule = (Rule)rules.getNext();
			    if (rule.doesRuleApply(p)) {
				    rule.applyRule(p);
					break;
			    }
			}
			recipr = p.getReducedProductPath();
			int sizel = recipl.size(); int sizer = recipr.size();
			if (recipl.equivalentProduct()) sizel = recipl.equivalentLeftProduct().size();
			if (recipr.equivalentProduct()) sizer = recipr.equivalentLeftProduct().size();
			if (sizel < sizer) continue;
			if (recipl.toString().equals(recipr.toString())) continue;
			if (((AlgebraSymbol) recipl.elementAt(0)).getValue().equals("0")) continue;
			if (recipl.size() == 2 && recipr.size() == 2 &&
			recipl.sameArrow() && recipl.sameSex() &&
			recipl.getLast().equals(recipr.getLast()) &&
			recipr.sameArrow() && !recipr.sameSex()) continue;//XX =XY
			if (recipl.size() == recipr.size() && recipl.sameElements() &&
				recipl.getFirst().equals(recipr.lastElement())) ret.addElement(new Equation(recipr,recipl));
			else ret.addElement(new Equation(recipl, recipr));
		}
		return ret;
	}

	/** adds equation for this algebra -- checks global flag to either
	* accept, ask, or decline to enter the reciprocal equation as well
	* all other forms of equation call this one
	* @param e the equation to add to the Algebra
	* @return the Equation
	*/
	public Equation addEquation(Equation e) {
		int ndx;
		AlgebraSymbolVector left = e.getLhs();		
		if (left.size() > 1){
			left = left.reduce();
			if (left.equals(e.getRhs())) {
				return null;
			}			
		}
		if (_addEquation(e) == null) {System.out.println("EEEEEEEEEEEEE null e"+e);return null;}
		if (addReciprocalEquations == RE_YES){
			//System.out.println("EQUATION e "+e);
			ListVector eqv = createReciprocalEquation(e);
			for (eqv.reset();eqv.isNext();){
				Equation eq = (Equation) eqv.getNext();
				//System.out.println(" eq "+eq);
			  // _addEquation((Equation) eqv.getNext());
			   _addEquation(eq);
			}
			//System.out.println("EQUATION"+_addEquation(createReciprocalEquation(e)));
			}
		else if (addReciprocalEquations == RE_ASK) {
			// Message -- Dialog Box ask about adding reciprocal for this equation ++++
			ListVector eqv = createReciprocalEquation(e);
			for (eqv.reset();eqv.isNext();){
			   _addEquation((Equation) eqv.getNext());
			}
		}
		return e;
	}
	
	/** add an equation checking only for prior entry of the equation
	* @param e the equation to add to the Algebra
	* @return the Equation
	*/
	public Equation _addEquation(Equation e) {
		int ndx=0;
		if (e == null) return null;
		String s = e.toText();
		if (suppressEquations != null && suppressEquations.indexOf(s) != -1) {
			return null;
		}
		else {
			if ((ndx = structuralEquations.indexOf(e)) == -1) {
				structuralEquations.addElement(e); // possible problem for future hash???
			} else e = structuralEquations.getSymbol(ndx);
		}
		return e;
	}

	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as string of symbol names
	* @param right the RHS of equation as string of symbol names
	* @return the new equation
	*/
	public Equation addEquation(String left, String right) {
		return addEquation(makePath(left),makePath(right));
	}

	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as list of symbol names
	* @param right the RHS of equation as list of symbol names
	* @return the new equation
	*/
	public Equation addEquation(StringVector left, StringVector right) {
		return addEquation(makePath(left),makePath(right));
	}

	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as list of symbol names
	* @param right the RHS of equation as list of symbol names
	* @return the new equation
	*/
	public Equation addEquation(StringVector left, String right) {
		return addEquation(makePath(left),makePath(right));
	}

	/** adds equation to list of structural equations for this algebra
	* @param left algebra symbol to be appended to the LHS of equation
	* @param right algebra symbol to be appended to the RHS of equation
	* @return the new equation
	*/
	public Equation addEquation(AlgebraSymbol left, AlgebraSymbol right) {
		Equation e = new Equation();
		e.addLhs(left);
		e.addRhs(right);
		return addEquation(e);
	}

	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as list of algebra symbols to be LHS
	* @param right the RHS of equation as list of algebra symbols to be RHS
	* @return the new equation
	*/
	public Equation addEquation(AlgebraSymbolVector left, AlgebraSymbolVector right) {
		Equation e = new Equation();
		e.setLhs(left);
		e.setRhs(right);
		return addEquation(e);
	}

	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as list of symbol names
	* @param right the RHS of equation as list of symbol names
	* @return the new equation
	*/
	public Equation makeEquation(StringVector left, StringVector right) {
		Equation e = new Equation();
		e.setLhs(makePath(left));
		e.setRhs(makePath(right));
		return e;
	}

	public AlgebraSymbolVector makePath(String spath) {
		AlgebraSymbolVector path = new AlgebraSymbolVector(2,2);
		int sp = 0; int ndx = 0;
		spath = spath.trim();
		while ((ndx = spath.indexOf(" ",sp)) != -1) {
			String r = spath.substring(sp,ndx);
			sp = ndx+1;
			path.addToBeginning(getElement(r));
		}
		if (sp < spath.length()) path.addToBeginning(getElement(spath.substring(sp)));
		return path;
	}

	public AlgebraSymbolVector makePath(StringVector spath) {
		AlgebraSymbolVector path = new AlgebraSymbolVector(2,2);
		for (spath.reset();spath.isNext();) {
			path.addToBeginning(getElement( spath.getNext()));
		}
		return path;
	}
	public AlgebraSymbolVector getArrows(int arrow) {
		switch (arrow)	{
			case Bops.UP:
				return getUpArrows();
			case Bops.DOWN:
				return getDownArrows();
			case Bops.LEFT:
				return getSideArrows();
			case Bops.RIGHT:
				return getSideArrows();
			case Bops.SPOUSE:
				return getSpouseArrows();
			default:

			}
		return null;
	}


	public AlgebraSymbolVector getUpArrows(){
	    return getUpOrDownArrows(Bops.UP);
	}

	public AlgebraSymbolVector getDownArrows(){
	 return getUpOrDownArrows(Bops.DOWN);
	}

	// Review ...
	public AlgebraSymbolVector getUpOrDownArrows(int updown){

		AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
		AlgebraSymbolVector av = getGenerators();
		AlgebraSymbolVector fe = getFocalElements();

		for (av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
			if (as.getArrowType() == updown){
				ups.addToEnd(as);
			} else if (as.getArrowType() > -1) continue;
			AlgebraSymbol rcp = as.getReciprocal();
			if (rcp != null) {
			    for(fe.reset();fe.isNext();) {
				    AlgebraSymbol aft = fe.getNext();
				    if (isPCI_EquationVariant(new Equation(rcp,aft).addLhs(as))) {
					    if (!isPCI_EquationVariant(new Equation(as,aft).addLhs(rcp))) {
						    if (updown == Bops.UP) ups.addToEnd(as);
						    else if (updown == Bops.DOWN) ups.addToEnd(rcp);
						    else Debug.prout(0,"Algebra: getUpOrDownArrows - Unknown arrow type");
					    }
				    }
			    }
			}
		}
		return ups;
	}

	public AlgebraSymbolVector getSideArrows(){

		AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
		AlgebraSymbolVector av = getGenerators();

		for (av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
		   if (as.getArrowType() == Bops.RIGHT || as.getArrowType() == Bops.LEFT) ups.addToEnd(as);
		}
		return ups;
	}
	public AlgebraSymbolVector getLeftSideArrows(){

		AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
		AlgebraSymbolVector av = getGenerators();

		for (av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
		   if (as.getArrowType() == Bops.LEFT) ups.addToEnd(as);
		}
		return ups;
	}
	public AlgebraSymbolVector getRightSideArrows(){

		AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
		AlgebraSymbolVector av = getGenerators();

		for (av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
		   if (as.getArrowType() == Bops.RIGHT) ups.addToEnd(as);
		}
		return ups;
	}

	public AlgebraSymbolVector getSpouseArrows(){

		AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
		AlgebraSymbolVector av = getGenerators();
		for (av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
		   if (as.getArrowType() == Bops.SPOUSE ||
			    as.getArrowType() == Bops.SPOUSER) ups.addToEnd(as);
		}
		return ups;
	}

    AlgebraSymbolVector getSexMarkedGenerators(String sex) {
        AlgebraSymbolVector ret = new AlgebraSymbolVector(2,1);
        AlgebraSymbolVector gens = getGenerators();
        for (gens.reset();gens.isNext();) {
            AlgebraSymbol gen = gens.getNext();
            if (gen.getSex().equals(sex) && !(gen.getValue().equals("F") ||
			gen.getValue().equals("M")) &&
            ret.indexOf(gen)== -1 ) ret.addElement(gen);
        }
        return ret;
    }

    AlgebraSymbolVector getSexGenerators() {
        AlgebraSymbolVector ret = new AlgebraSymbolVector(2,1);
        AlgebraSymbolVector gens = getGenerators();
        for (gens.reset();gens.isNext();) {
            AlgebraSymbol as = gens.getNext();
            if ((as.isSexGenerator() || as.toString().equals("N")) && ret.indexOf(as)== -1 )
				ret.addElement(as);
        }
        return ret;
    }

    boolean isPCI_EquationVariant (Equation eq) {
        if (isEquation(eq)) return true;
        AlgebraSymbolVector sexg = getSexGenerators();
        if (sexg != null) {
            AlgebraSymbolVector lhs = eq.getLhs();
            AlgebraSymbolVector rhs = eq.getRhs();
            for (sexg.reset();sexg.isNext();) {
                AlgebraSymbol as = sexg.getNext();
                if (isEquation(new Equation(lhs,new AlgebraSymbolVector(as)))) return true;
            }
        }
        return false;
    }



	public boolean isArrow(AlgebraSymbol as, int arrow){
		AlgebraSymbolVector fe = getFocalElements();
		//System.out.println("Algebra.isArrow about to check reciprocal status of generator "+as);
		AlgebraSymbol rcp = as.getReciprocal();
	//	System.out.println("Algebra.isArrow checked reciprocal status of generator "+as+" reciprocal "+rcp+" focal "+fe);
		for(fe.reset();fe.isNext();) {
			AlgebraSymbol aft = fe.getNext();
			switch (arrow)	{
				case Bops.UP:
				    if (rcp != null) {
				       // if (isEquation(new Equation(rcp,aft).addLhs(as))) {
				            //if (!isEquation(new Equation(as,aft).addLhs(rcp))) {
				        if (isPCI_EquationVariant(new Equation(rcp,aft).addLhs(as))) {
				            if (!isPCI_EquationVariant(new Equation(as,aft).addLhs(rcp))) {
						        return true;
					        }
				        }
				    }
				   // return false;
				   break;
				case Bops.DOWN:
				    if (rcp != null) {
				        if (isPCI_EquationVariant(new Equation(as,aft).addLhs(rcp))) {
				            if (!isPCI_EquationVariant(new Equation(rcp,aft).addLhs(as))) {
						        return true;
					        }
				        }
				    }
				  // return false;
				  break;
				case Bops.LEFT:
				    if ((isEquation(new Equation(as,as).addLhs(as))) &&
				    !(isArrow(as,Bops.UP)) && !(isArrow(as,Bops.DOWN))) {
				    return true;
				    }
				    return false;
				case Bops.RIGHT:
				    if ((isEquation(new Equation(as,as).addLhs(as))) &&
				    !(isArrow(as,Bops.UP)) && !(isArrow(as,Bops.DOWN))) {
				    return true;
				    }
				    return false;
				case Bops.SPOUSE:
					if (as.getSex().equals("N")) {
						if (isEquation(new Equation(as,aft).addLhs(as))) {
						    return true;
						}
					}
					else if ((isEquation(new Equation(as,getCurrent().getElement("0")).addLhs(as))) &&
				    !(isArrow(as,Bops.UP)) && !(isArrow(as,Bops.DOWN))) {
						return true;
					}
					//return false;
					break;
				case Bops.MALE :
					if (as.getSex().equals("M") && isAlmostRightIdentity(as)&&
					!as.isIdentityElement()) return true;
					else return false;
				case Bops.FEMALE:
					if (as.getSex().equals("F") && isAlmostRightIdentity(as)&&
					!as.isIdentityElement()) return true;
					else return false;
				case Bops.MALEFEMALE:
					if (isArrow(as,Bops.MALE) || isArrow(as,Bops.FEMALE)) return true;
					else return false;
				default:

				}
		}
		return false;
	}

	public boolean isAlmostRightIdentity(AlgebraSymbol a) {
		if (a.getArrowType() == Bops.LEFT || a.getArrowType() == Bops.RIGHT || a.getArrowType() == Bops.SIDE) return false;
		return _isAlmostRightIdentity(a,getGenerators());
	}

	public boolean isAlmostLeftIdentity(AlgebraSymbol a) {
		if (a.getArrowType() == Bops.LEFT || a.getArrowType() == Bops.RIGHT || a.getArrowType() == Bops.SIDE) return false;
		return _isAlmostLeftIdentity(a,getGenerators());
	}

	public boolean _isAlmostLeftIdentity(AlgebraSymbol a, AlgebraSymbolVector gens) { //
		for(int i=0;i<gens.size();i++) {
			AlgebraSymbol g;
			g = gens.getSymbol(i);
			if (g.isIdentityElement()) continue;
			AlgebraPath g1 = new AlgebraPath(g);
			g1.product(a);
			//if (isArrow(g,Bops.SPOUSE)) continue;
			if (g1.getReducedProductPath().size() == 1 && g1.getReducedProductPath().getFirst().equals(g)) continue;

			if (g1.getReducedProductPath().size() == 0 || g1.getReducedProductPath().getFirst().isIdentityElement()) {
				AlgebraSymbolVector ngens = gens.copy();
				ngens.removeElement(a);
	//			System.out.println("Calling _isAlmostLeftIdentity g="+g+" ngens="+ngens);
				if (!_isAlmostLeftIdentity(g,ngens)) return false;
//				System.out.println("Called _isAlmostLeftIdentity and survived");

			} else return false;
		}
		return true;
	}


	public boolean _isAlmostRightIdentity(AlgebraSymbol a, AlgebraSymbolVector gens) { //
		for(int i=0;i<gens.size();i++) {
			AlgebraSymbol g = gens.getSymbol(i);
			if (g.isIdentityElement()) continue;
			//if (isArrow(g,Bops.SPOUSE)) continue;
			AlgebraPath a1 = new AlgebraPath(a);
			a1.product(g);
			//System.out.println("a= "+a+" g= "+g+" a1= "+a1);
			if (a1.getReducedProductPath().size() == 1 && a1.getReducedProductPath().getFirst().equals(g)) continue;
			if (a1.getReducedProductPath().size() == 0 || a1.getReducedProductPath().getFirst().isIdentityElement()) {
				AlgebraSymbolVector ngens = gens.copy();
				ngens.removeElement(a);
				//System.out.println("Calling _isAlmostRightIdentity g="+g+" ngens="+ngens);
				if (!_isAlmostRightIdentity(g,ngens)) return false;
				//System.out.println("Called _isAlmostRightIdentity and survived");
			} else return false;
		}
		return true;
	}
    public boolean enterSpouseTerm() {
        return enterSpouseTerm("Sp");
    }

    public boolean enterSpouseTerm(String s) {
     	AlgebraSymbolVector av = getGenerators();
		boolean flag = true;
		for(av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
			if (as.isSexGenerator()) continue;
			if (!as.getSex().equals("N")) { // e.g. M/F
				flag = false;
			}
		}
        if (flag) return enterSpouseTerm(s,"");
        else {
            String t = s + "2";
            s = s + "1";
            return enterSpouseTerm(s,t);
        }
    }

	public boolean enterSpouseTerm(String s, String t) {
		AlgebraSymbolVector fe = getFocalElements();
		if (fe.size() == 1 && !fe.getFirst().getSex().equals("N")) {
			return false;
		}
		if (fe.size() == 0)
		    return false;

		AlgebraSymbolVector av = getGenerators();
		boolean flag = true;
		for(av.reset();av.isNext();) {
			AlgebraSymbol as = av.getNext();
			if (as.isSexGenerator()) continue;
			if (!as.getSex().equals("N")) { // e.g. M/F
				flag = false;
			}
		}

		//put symbols in algebra
		AlgebraSymbol n1=null,m1=null,f1=null;
		if (!flag) { // M/F
		    if (t == "") {
		        t = s + "2";
		        s = s + "1";
		    }
			m1 = getElement(s);
			m1.setSex("M");
			f1 = getElement(t);
			f1.setSex("F");
			addGenerator(m1);
			m1.setArrowType(Bops.SPOUSE);
			addGenerator(f1);
			f1.setArrowType(Bops.SPOUSE);
		} else { // N
		    if (s != "") n1 = getElement(s);
		    else n1 = getElement(t);
			n1.setSex("N");
			addGenerator(n1);
			n1.setArrowType(Bops.SPOUSE);
		}
		//Add structural definition equations
		setAddReciprocalEquations(RE_NO);
		if (n1 == null) { //M/F HH=0/WW=0,
			addEquation(m1.getValue()+" "+m1.getValue(),"0");
			addEquation(f1.getValue()+" "+f1.getValue(),"0");
		}else {//sp sp = ft
			addEquation(n1.getValue()+" "+n1.getValue(),fe.getFirst().getValue());
			n1.setReciprocal(n1);
			AlgebraSymbolVector asv = getSexGenerators();
			for (int i=0;i<getSexGenerators().size();i++)
				addEquation(n1.getValue()+" "+(AlgebraSymbol)getSexGenerators().elementAt(i),n1.getValue());
		}

		//add reciprocal equations
		if (n1 == null) { // M/F
			if (fe.size() == 1) { // sp of sp = ft
				addEquation(m1.getValue()+" "+f1.getValue(),fe.getFirst().getValue());
				addEquation(f1.getValue()+" "+m1.getValue(),fe.getFirst().getValue());
			} else if (fe.size() == 2) {
				if (fe.getFirst().getSex().equals(fe.getLast().getSex())) {
					setAddReciprocalEquations(RE_YES);
					return false;
				}
				if (fe.getFirst().getSex().equals(f1.getSex())) {//i
					addEquation(f1.getValue()+" "+m1.getValue(),fe.getFirst().getValue());//WH=i
					addEquation(m1.getValue()+" "+f1.getValue(),fe.getLast().getValue());
					//addEquation(m1.getValue()+" "+fe.getFirst().getValue(),m1.getValue());//Hi=H
					addEquation(m1.getValue()+" "+fe.getLast().getValue(),"0");//HI=0
					//addEquation(f1.getValue()+" "+fe.getLast().getValue(),f1.getValue());
					addEquation(f1.getValue()+" "+fe.getFirst().getValue(),"0");
					//addEquation(fe.getLast().getValue()+" "+m1.getValue(),m1.getValue());//IH=H
					//addEquation(fe.getFirst().getValue()+" "+f1.getValue(),f1.getValue());
					//addEquation(fe.getLast().getValue()+" "+f1.getValue(),"0");//IW=0
					//addEquation(fe.getFirst().getValue()+" "+m1.getValue(),"0");
				} else {//I
					addEquation(m1.getValue()+" "+f1.getValue(),fe.getFirst().getValue());
					addEquation(f1.getValue()+" "+m1.getValue(),fe.getLast().getValue());
					//addEquation(f1.getValue()+" "+fe.getFirst().getValue(),f1.getValue());//WI=W
					addEquation(f1.getValue()+" "+fe.getLast().getValue(),"0");//Wi=0
					//addEquation(m1.getValue()+" "+fe.getLast().getValue(),m1.getValue());
					addEquation(m1.getValue()+" "+fe.getFirst().getValue(),"0");
					//addEquation(fe.getLast().getValue()+" "+f1.getValue(),f1.getValue());//iW=W
					//addEquation(fe.getFirst().getValue()+" "+m1.getValue(),m1.getValue());
					//addEquation(fe.getLast().getValue()+" "+m1.getValue(),"0");//iH=0
					//addEquation(fe.getFirst().getValue()+" "+f1.getValue(),"0");
				}
			} else {
				setAddReciprocalEquations(RE_YES);
				return false;
			}
			m1.setReciprocal(f1);
			f1.setReciprocal(m1);
		}
		setAddReciprocalEquations(RE_YES);
		return true;
	}

  	/** create explicit id equation for each generator
	*/
    void constructIDEquations() {
	    AlgebraSymbolVector g = getElements();
	    AlgebraSymbol id = getIdentityElement();
		//System.out.println(" IDENTITY X "+ id+ " focal "+ getFocalElements());
	    AlgebraSymbolVector gens = getGenerators();
	    addGenerator(id);
	    identityElement = null;
        EquationVector eq = getEquations();
        EquationVector rem = new EquationVector(2,1);
	    Equation e = new Equation();
	    rem.addElement(e);
        for (eq.reset();eq.isNext();) {
            e = eq.getNext();
            if (e.getLhs().indexOf(id) > -1) rem.addElement(e);
        }
        for (rem.reset();rem.isNext();) {
            e = rem.getNext();
            eq.removeElement(e);
        }
	    unbind();
	    for (gens.reset();gens.isNext();) {
	        AlgebraSymbol gen = gens.getNext();
			e = new Equation(id,gen).addLhs(gen);
	        _addEquation(e);
	        e = new Equation(gen,gen).addLhs(id);
	        _addEquation(e);
	    }
    }



 	/** constucts smallest algebra containing two given algebras
	* @param a the algebra to be joined to this
	* @return the new algebra
	*/
    public Algebra makeAlgebraJoinOLD(Algebra a) {
       // EquationVector eqv = new EquationVector(10,5);
      //  EquationVector rmv = new EquationVector(2,1);
        AlgebraSymbolVector g1 = getElements();
	    AlgebraSymbolVector g2 = a.getElements().deepClone();
	    for (g2.reset();g2.isNext();){
	        AlgebraSymbol as = g2.getNext();
	        if (g1.indexOf(as) == -1) g1.addElement(as);
	    }
	    AlgebraSymbolVector fe1 =getFocalElements();
	//    System.out.println(" focals "+a.getFocalElements());
	    AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
	    for (fe2.reset();fe2.isNext();){
	        AlgebraSymbol as = fe2.getNext();
	        if (fe1.indexOf(as) == -1) fe1.addElement(as);
	    }
	    EquationVector e1= getEquations();
	    EquationVector e2 = a.getEquations();
	    for (e2.reset1();e2.isNext();) {
	        Equation eq = a.substitute(e2.getNext(),g2);
	        if (e1.indexOf(eq) == -1) e1.addElement(eq);
	    }
        AlgebraSymbol id1 = getIdentityElement();
        AlgebraSymbol id2 = a.getIdentityElement();
        if ((id1 != null) && (id2 != null)){
            if (!id1.equals(id2)) {
                constructIDEquations();
                a.constructIDEquations();
				e2 = a.getEquations();
				for (e2.reset1();e2.isNext();) {
					Equation eq = e2.getNext();
					if (e1.indexOf(eq) == -1) e1.addElement(eq);
				}
            }else if (!id1.getSex().equals(id2.getSex())) {
                String sex = id1.getSex();
                if (!sex.equals("N")){
                    if (sex.equals("F"))enterSexGenerator(Bops.FEMALE);
                    else enterSexGenerator(Bops.MALE);
                    id1.setSex("N");
                }
                sex = id2.getSex();
                if (!sex.equals("N")){
                    if (sex.equals("F")) enterSexGenerator(Bops.FEMALE);
                    else enterSexGenerator(Bops.MALE);
                    id2.setSex("N");
                }
            }
        }
	   // System.out.println(" THIS "+this.getEquations());
	    return this;
	}
 	/** constructs smallest algebra containing two given algebras
	* @param a the algebra to be joined to this
	* @return the new algebra
	*/
    public Algebra makeAlgebraJoinXX(Algebra a) {//Shipibo version
       // EquationVector eqv = new EquationVector(10,5);
       // EquationVector rmv = new EquationVector(2,1);
        AlgebraSymbolVector g1 = getElements();
	    AlgebraSymbolVector g2 = a.getElements().deepClone();
	    for (g2.reset();g2.isNext();){
	        AlgebraSymbol as = g2.getNext();
	        if (g1.indexOf(as) == -1) g1.addElement(as);
	    }
	    AlgebraSymbolVector fe1 =getFocalElements();
	//    System.out.println(" focals "+a.getFocalElements());
	    AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
	    for (fe2.reset();fe2.isNext();){
	        AlgebraSymbol as = fe2.getNext();
	        if (fe1.indexOf(as) == -1) fe1.addElement(as);
	    }
	    EquationVector e1= getEquations();
	    EquationVector e2 = a.getEquations();
	 /*   for (e2.reset1();e2.isNext();) {
	        Equation eq = a.substitute(e2.getNext(),g2);
			//System.out.println(" BBBBBBBBBBBBBBBBBB eq "+eq+" g2 "+g2);
	        if (e1.indexOf(eq) == -1) e1.addElement(eq);
	    }*/
        AlgebraSymbol id1 = getIdentityElement();
        AlgebraSymbol id2 = a.getIdentityElement();
        if (id1 != null && (id2 != null) && (!id1.equals(id2))){
		//if (!id1.equals(id2)) {
			constructIDEquations();
			a.constructIDEquations();
			e2 = a.getEquations();
			for (e2.reset1();e2.isNext();) {
				Equation eq = e2.getNext();
				if (e1.indexOf(eq) == -1) e1.addElement(eq);
			}
		}

	    for (e2.reset1();e2.isNext();) {
	        Equation eq = a.substitute(e2.getNext(),g2);
			//System.out.println(" BBBBBBBBBBBBBBBBBB eq "+eq+" g2 "+g2);
	        if (e1.indexOf(eq) == -1) e1.addElement(eq);
	    }


		if (!id1.getSex().equals(id2.getSex())) {
            String sex = id1.getSex();
            if (!sex.equals("N")){
                if (sex.equals("F"))enterSexGenerator(Bops.FEMALE);
                else enterSexGenerator(Bops.MALE);
                id1.setSex("N");
            }
            sex = id2.getSex();
            if (!sex.equals("N")){
                if (sex.equals("F")) enterSexGenerator(Bops.FEMALE);
                else enterSexGenerator(Bops.MALE);
                id2.setSex("N");
            }

        }
	   // System.out.println(" THIS "+this.getEquations());
	    return this;
	}

	public Algebra makeAlgebraJoinSameId(Algebra a) {//Shipibo version
	//	EquationVector eqv = new EquationVector(10,5);
		// EquationVector rmv = new EquationVector(2,1);
		AlgebraSymbolVector g1 = getElements();
		AlgebraSymbolVector g2 = a.getElements().deepClone();
		for (g2.reset();g2.isNext();){
			AlgebraSymbol as = g2.getNext();
			if (g1.indexOf(as) == -1) g1.addElement(as);
		}
		AlgebraSymbolVector fe1 =getFocalElements();
		//    System.out.println(" focals "+a.getFocalElements());
		AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
		for (fe2.reset();fe2.isNext();){
			AlgebraSymbol as = fe2.getNext();
			if (fe1.indexOf(as) == -1) fe1.addElement(as);
		}
		EquationVector e1= getEquations();
		EquationVector e2 = a.getEquations();
		/*   for (e2.reset1();e2.isNext();) {
		  Equation eq = a.substitute(e2.getNext(),g2);
		//System.out.println(" BBBBBBBBBBBBBBBBBB eq "+eq+" g2 "+g2);
		  if (e1.indexOf(eq) == -1) e1.addElement(eq);
		}*/
		AlgebraSymbol id1 = getIdentityElement();
		AlgebraSymbol id2 = a.getIdentityElement();

		for (e2.reset1();e2.isNext();) {
			Equation eq = a.substitute(e2.getNext(),g2);
			//System.out.println(" BBBBBBBBBBBBBBBBBB eq "+eq+" g2 "+g2);
			if (e1.indexOf(eq) == -1) e1.addElement(eq);
		}

		if (!id1.getSex().equals(id2.getSex())) {
			String sex = id1.getSex();
			if (!sex.equals("N")){
				 if (sex.equals("F"))enterSexGenerator(Bops.FEMALE);
				 else enterSexGenerator(Bops.MALE);
				 id1.setSex("N");
			}
			sex = id2.getSex();
			if (!sex.equals("N")){
				 if (sex.equals("F")) enterSexGenerator(Bops.FEMALE);
				 else enterSexGenerator(Bops.MALE);
				 id2.setSex("N");
			}
		}
	    //System.out.println(" THIS "+this.getEquations());
		return this;
	}

    public Algebra makeAlgebraJoinOLD2(Algebra a) {//trobriand version reconcile with old version
	    EquationVector e1= getEquations();
	    EquationVector e2 = a.getEquations();
		//same below
        AlgebraSymbol id1 = getIdentityElement();
        AlgebraSymbol id2 = a.getIdentityElement();
        if ((id1 != null) && (id2 != null)){
            if (!id1.equals(id2)) {
                constructIDEquations();
                a.constructIDEquations();

				e2 = a.getEquations();
				for (e2.reset1();e2.isNext();) {
					Equation eq = e2.getNext();
					if (e1.indexOf(eq) == -1) e1.addElement(eq);
				}
            }else if (!id1.getSex().equals(id2.getSex())) {
                String sex = id1.getSex();
                if (!sex.equals("N")){
                    if (sex.equals("F"))enterSexGenerator(Bops.FEMALE);
                    else enterSexGenerator(Bops.MALE);
                    id1.setSex("N");
                }
                sex = id2.getSex();
                if (!sex.equals("N")){
                    if (sex.equals("F")) enterSexGenerator(Bops.FEMALE);
                    else enterSexGenerator(Bops.MALE);
                    id2.setSex("N");
                }
            }
        }
       // EquationVector eqv = new EquationVector(10,5);
       // EquationVector rmv = new EquationVector(2,1);
        AlgebraSymbolVector g1 = getElements();
	    AlgebraSymbolVector g2 = a.getElements().deepClone();
	    for (g2.reset();g2.isNext();){
	        AlgebraSymbol as = g2.getNext();
	        if (g1.indexOf(as) == -1) g1.addElement(as);
	    }
	    AlgebraSymbolVector fe1 =getFocalElements();
	//    System.out.println(" focals "+a.getFocalElements());
	    AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
	    for (fe2.reset();fe2.isNext();){
	        AlgebraSymbol as = fe2.getNext();
	        if (fe1.indexOf(as) == -1) fe1.addElement(as);
	    }
	    for (e2.reset1();e2.isNext();) {
	        Equation eq = a.substitute(e2.getNext(),g2);
			//System.out.println(" BBBBBBBBBBBBBBBBBB eq "+eq+" g2 "+g2);
	        if (e1.indexOf(eq) == -1) e1.addElement(eq);
	    }
	   // System.out.println(" THIS "+this.getEquations());
	    return this;
	}

	public Algebra makeAlgebraJoinDifferentId(Algebra a) {//trobriand version reconcile with old version
		EquationVector e1= getEquations();
		EquationVector e2 = a.getEquations();
		//same below
		AlgebraSymbol id1 = getIdentityElement();
		AlgebraSymbol id2 = a.getIdentityElement();
		if (id1 != null && id2 != null) {
			constructIDEquations();
			a.constructIDEquations();
			e2 = a.getEquations();
			for (e2.reset1();e2.isNext();) {
				Equation eq = e2.getNext();
				if (e1.indexOf(eq) == -1) e1.addElement(eq);
			}
		}
		//EquationVector eqv = new EquationVector(10,5);
		// EquationVector rmv = new EquationVector(2,1);
		AlgebraSymbolVector g1 = getElements();
		AlgebraSymbolVector g2 = a.getElements().deepClone();
		for (g2.reset();g2.isNext();){
			AlgebraSymbol as = g2.getNext();
			if (g1.indexOf(as) == -1) g1.addElement(as);
		}
		AlgebraSymbolVector fe1 =getFocalElements();
		//    System.out.println(" focals "+a.getFocalElements());
		AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
		for (fe2.reset();fe2.isNext();){
			AlgebraSymbol as = fe2.getNext();
			if (fe1.indexOf(as) == -1) fe1.addElement(as);
		}
		for (e2.reset1();e2.isNext();) {
			Equation eq = a.substitute(e2.getNext(),g2);
		   if (e1.indexOf(eq) == -1) e1.addElement(eq);
		}
		return this;
	}


// 	/** adds equation to list of structural equations for this algebra
// 	* adds cross sex equations of form PC=id
//	* @param id the sex-marked (pseudo) identity elements
//	*/
/*    void addPCEquations(AlgebraSymbolVector id) {
       // AlgebraSymbolVector gens = getGenerators();
        EquationVector eqs = getEquations();
        EquationVector pc = new EquationVector(2,1);
        for (eqs.reset();eqs.isNext();) {
            Equation eq = eqs.getNext();
            //System.out.println("eq "+eq);

            if ((id.indexOf(eq.getRhs().getFirst()) != -1)&&
            (eq.getLhs().size() == 2)&&
            (id.indexOf(eq.getLhs().getFirst()) == -1)) {
                pc.addElement(eq);
            }
        }
        System.out.println("PC IS "+pc);
        if (pc.size() == 2) {
            AlgebraSymbolVector lhs1 = ((Equation)pc.elementAt(0)).getLhs();
            AlgebraSymbolVector lhs2 = ((Equation)pc.elementAt(1)).getLhs();
            AlgebraSymbolVector rhs1 = ((Equation)pc.elementAt(0)).getRhs();
            AlgebraSymbolVector rhs2 = ((Equation)pc.elementAt(1)).getRhs();
            Equation eq = new Equation((AlgebraSymbol)lhs1.elementAt(1),rhs2.getFirst()).addLhs(lhs2.elementAt(0).toString());
            addEquation(eq);
            eq = new Equation((AlgebraSymbol)lhs2.elementAt(1),rhs1.getFirst()).addLhs(lhs1.elementAt(0).toString());
            addEquation(eq);
        }
    }*/



	/** adds equations of form Pp=PP where P and p are in the same
	* direction but differ in sex marking
	*/




	boolean isSexGeneratorEquation(Equation eq) {
	    AlgebraSymbolVector asv = getSexGenerators();
	    AlgebraSymbolVector lhs = eq.getLhs();
	    AlgebraSymbolVector rhs = eq.getRhs();
	    for (asv.reset();asv.isNext();){
	        AlgebraSymbol as = asv.getNext();
	        if (lhs.indexOf(as) != -1 ) return true;
	    }
	    return false;
	}



	/** if xy=w is an equation, y and Y are sibs then
	* make xY= w an equation
	*/
	void addSibAnalogousEquations() {
	    EquationVector eqs = getEquations();
		//System.out.println("4444444444444444444 eqs "+eqs);
	    EquationVector newEqs = new EquationVector();
	    Equation e = new Equation();
	    for (eqs.reset1();eqs.isNext();){
	        Equation eq = eqs.getNext();
	     /*   if (isSexGeneratorEquation(eq)) {
System.out.println("SEX EQ "+eq);continue;}*/
			if (eq.getRhs().size() > 1) continue;
			if (eq.getLhs().size() != 2) continue;
		    if (eq.isIdEquation()) continue;
		    if (eq.isZeroEquation()) continue;
			AlgebraSymbolVector lhs = (AlgebraSymbolVector) eq.getLhs().clone();
	        AlgebraSymbolVector rhs = (AlgebraSymbolVector)eq.getRhs().clone();
	        AlgebraSymbol as = lhs.getLast();
			AlgebraSymbol as1 = lhs.getFirst();
    System.out.println("START eq "+eq+" left as "+as+" as type "+as.getArrowType()+ " as1 " +as1+"as1 type "+as1.getArrowType());
			if ((as1.getArrowType() != Bops.RIGHT && as1.getArrowType() != Bops.LEFT)
			&& (as.getArrowType() == Bops.RIGHT || as.getArrowType() == Bops.LEFT)) {
				AlgebraSymbolVector gens = getGenerators();
				for (gens.reset();gens.isNext();){
					AlgebraSymbol gen = gens.getNext();
					if ((gen.getArrowType() == Bops.RIGHT || gen.getArrowType() == Bops.LEFT) &&
					gen.getArrowType() != as.getArrowType()){
	                lhs.setElementAt(gen,0);
	                e = new Equation(lhs,rhs);
	                newEqs.addElement(e);
	                // addEquation(lhs,rhs1);
   // System.out.println("START lhs "+lhs+" rhs "+rhs);
	                }
	            }
	        }
	    }
	    for (newEqs.reset1();newEqs.isNext();){
	        e = newEqs.getNext();
System.out.println(" NEWEQ "+e);
	        addEquation(e);
	    }
	}


 	/** add equations based on generators with opposite sex
	*/

/*	public void addCrossSexEquations(boolean singleFlag, String sex1,String sex2){
	    AlgebraSymbolVector id = getFocalElements();
/*	    id.addElement(id1);
	    id.addElement(id2);*/
//	    System.out.println(" focal elements of the join algebra="+this.getFocalElements());
		//addSexIdEquations(id);//use with trobriand; id1 and id2 and sex marked

/*		 if (id.size() > 1) addSexIdEquations();//use with trobriand; id1 and id2 and sex marked
		// addPCEquations(id);
		addMixedSexSameArrowEquations();
		//if (id.size() == 2)
		   // addSibAnalogousEquations();//trobriand
		//addAnalogousEquations(id);
		addAnalogousEquations();
		//if (id.size() == 2)
		  // addCrowSkewingEquations();
		//makeElementsEquivalent();
		if (id.size() > 1) {
			if (singleFlag) {
				addSingleChildEquations();// S,D,i,I --> Si = S, DI = D
				linkElementProductEquations(1,Bops.DOWN);//S, D--> S&D
			} else {
				addOlderYoungerSibProducts();//BIfemale=Ifemale,bIfemale=Ifemale,etc.
				equateChildElements(sex1);//Ai&E, A&EI
				equateTheCrossCousins(sex2);//AiI&EiI,AIi&EIi
				addCrossCousinEquation();//EIG=i,AiP=I,
				addParentChildProductEquation();//logical: FSi=0,FDi=0,MDI=0,MSI=0
				addSibChildProducts();//ZAi = Ai, etc.based on MER rule
				addParentChildProducts();//P(Si_&Di_)=0, etc., uses MER rule
				addChildSibParentProducts();//ABP = B (SB+F = B+), etc.
			}
			linkMaleFemaleElementEquations();//assumes grandparent, grandchild, etc e.g.tongan, trobriand
		}
/*		MakeEquivalentRule rule = (MakeEquivalentRule) RuleFactory.getRule(RuleFactory.MAKEEQUIVALENTRULE);
		ListVector lv = rule.getEquivalentElements();
		for (lv.reset();lv.isNext();){
		    ListVector lv1 = (ListVector)lv.getNext();
			System.out.println(" eqv1 "+lv1.elementAt(0)+" eqv 2 "+lv1.elementAt(1));
		}
		RewriteProductRule rule1 = (RewriteProductRule) RuleFactory.getRule(RuleFactory.REWRITEPRODUCTRULE);;
		lv = rule1.getEquivalentProducts();
		for (lv.reset();lv.isNext();){
		    ListVector lv1 = (ListVector)lv.getNext();
			System.out.println(" eqvA "+lv1.elementAt(0)+" eqv B "+lv1.elementAt(1));
		}*/
	//}

	void makeEquivalentElementEquation(AlgebraSymbolVector asv1,AlgebraSymbolVector asv2,boolean flag){
		MakeEquivalentRule mer = (MakeEquivalentRule) RuleFactory.setRule(RuleFactory.MAKEEQUIVALENTRULE);

		//MakeEquivalentRule mer = new MakeEquivalentRule();
		//addRule(mer);
		//mer.setActiveRule(true);
		AlgebraSymbolVector a1 = asv1; AlgebraSymbolVector a2 = asv2;
		if (((AlgebraSymbol)asv1.getLast()).getSex().equals("M")) {
		    a1 = asv2;
			a2 = asv1;
		}
		mer.setEquivalentElements(a1,a2);//S,D --> S&D
		if (flag){
			Equation eq = new Equation(a1,a2);
			_addEquation(eq);}
		}


	void equateFocalElementEquation3X(String sex){//so i fa & da i fa
		AlgebraSymbolVector ft = getFocalElements();
		AlgebraSymbolVector asvF = new AlgebraSymbolVector();
		AlgebraSymbolVector asvM = new AlgebraSymbolVector();
		asvF.addElement(getGenerators(Bops.DOWN,"F"));
		asvM.addElement(getGenerators(Bops.DOWN,"M"));
		for (ft.reset();ft.isNext();){
		    AlgebraSymbol id = ft.getNext();
			if (id.getSex().equals(sex)) continue;
		    asvF.addElement(id);
		    asvM.addElement(id);
		    asvF.addElement(getGenerators(Bops.UP,sex));
		    asvM.addElement(getGenerators(Bops.UP,sex));
			if (sex.equals("F"))
		        makeEquivalentElementEquation(asvF,asvM,true);
			else
		        makeEquivalentElementEquation(asvM,asvF,true);
			break;
		}
	}







	void putInRewriteProductRule(ListVector lSide, ListVector rSide){
		RewriteProductRule oyRule = (RewriteProductRule)RuleFactory.getRule(RuleFactory.REWRITEPRODUCTRULE);
		if (oyRule == null)
		    oyRule = (RewriteProductRule) RuleFactory.setRule(RuleFactory.REWRITEPRODUCTRULE);
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		AlgebraSymbolVector asv2 =  new AlgebraSymbolVector();
		for (lSide.reset();lSide.isNext();){
		    asv1.addElement(lSide.getNext());
		}
		for (rSide.reset();rSide.isNext();){
		    asv2.addElement(rSide.getNext());
		}
		//System.out.println(" product  asv1 "+asv1+" asv2 "+asv2);
		oyRule.setEquivalentProducts(asv1,asv2);//BIG=IG
	}


/*
	void markEquivalentEquationsXX() {
		addRule(new CrowSkewingRule());
		MakeEquivalentRule mer = new MakeEquivalentRule();
		addRule(mer);
		mer.setActiveRule(true);
	   EquationVector eqv = getEquations();
		AlgebraSymbolVector asvA = null;
		AlgebraSymbolVector asvB = null;
		for (eqv.reset1();eqv.isNext();){
		   Equation eq = eqv.getNext();
		   if (eq.equationType() == Equation.EQUIVALENCE) {
			   mer.setEquivalentElements(eq.getLhs(),eq.getRhs());
				//eq.setEqType(Equation.EQUIVALENCE);
		   }else if (eq.equationType() == Equation.EQUIVALENCE_A) {
				if (asvA == null) {
					asvA = eq.getRhs();
					eq.setEqType(Equation.EQUIVALENCE);
				}
				else {
					mer.setEquivalentElements(eq.getRhs(),asvA);
					Equation eq1 = new Equation(eq.getRhs(),asvA);
					_addEquation(eq1);
					eq.setEqType(Equation.EQUIVALENCE);
				}
		   } else if (eq.equationType() == Equation.EQUIVALENCE_B) {
				if (asvB == null) {
					//eq.setEqType(Equation.EQUIVALENCE);
					asvB = eq.getRhs();
				}
				else {
					mer.setEquivalentElements(eq.getRhs(),asvB);
					//eq.setEqType(Equation.EQUIVALENCE);
				}
		   }
		}
	}
*/

/*	void makeElementsEquivalent(){
	   EquationVector eqv = getEquations();
		AlgebraSymbolVector ft = getFocalElements();
		for (eqv.reset();eqv.isNext();){
		   Equation eq = eqv.getNext();
		   if (eq.getRhs().size() > 1) {
				if (eq.getLhs().size() != eq.getRhs().size()) continue;
				if (!eq.getLhs().sameSex() || !eq.getRhs().sameSex()) continue;
				AlgebraSymbol asl = eq.getLhs().getFirst();
				AlgebraSymbol asr = eq.getRhs().getFirst();
				if (asl.getArrowType() == Bops.UP && asr.getArrowType() != Bops.DOWN) continue;
				if (asl.getArrowType() == Bops.DOWN && asr.getArrowType() != Bops.UP) continue;
				if (asl.getArrowType() == Bops.LEFT && asr.getArrowType() != Bops.RIGHT) continue;
				if (asl.getArrowType() == Bops.RIGHT && asr.getArrowType() != Bops.LEFT) continue;
				AlgebraPath pl = new AlgebraPath(eq.getLhs());
				AlgebraPath pr = new AlgebraPath(eq.getRhs());
				pl.makeEquivalentPath(pr);
		   }
		}
	}*/

	public Algebra makeAlgebraJoin(Algebra a){
		AlgebraSymbol id1 = getIdentityElement();
		AlgebraSymbol id2 = a.getIdentityElement();
		if ((id1 == null) || (id2 == null)) return null;
		if (id1.toString().equals(id2.toString()))
			return makeAlgebraJoinSameId(a);
		else return makeAlgebraJoinDifferentId(a);
	}

	public boolean addReciprocalEquations(boolean sibGen){
		boolean addFlag = false;
	    AlgebraSymbol id = getIdentityElement();
		AlgebraSymbolVector gens = getGenerators();
		EquationVector eqv = getEquations();
		boolean pcFlag = false;
		for (int i=0;i<eqv.size();i++){
			if (((Equation) eqv.elementAt(i)).equationType() == Equation.PASIBZERODEF)
				pcFlag = true;
		}
		int up = -1; int down = -1; int left = -1; int right = -1; int spouse = -1;
		int i = -1;
		for (gens.reset();gens.isNext();){
			i++;
		    AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType() == Bops.UP) up = i;
			if (gen.getArrowType() == Bops.DOWN) down = i;
			if (gen.getArrowType() == Bops.LEFT) left = i;
			if (gen.getArrowType() == Bops.RIGHT) right = i;
			if (gen.getArrowType() == Bops.SPOUSE) spouse = i;
		}
		if (up > -1 && down > -1 && left == -1 && right == -1 && !sibGen) {//PC=I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(down),id).addLhs((AlgebraSymbol)gens.getSymbol(up));
			addEquation(eq);
			addFlag = true;
		} else if (up == -1 && down == -1 && left > -1 && right > -1) {//Bb=I, bB= I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(left),id).addLhs((AlgebraSymbol)gens.getSymbol(right));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(right),id).addLhs((AlgebraSymbol)gens.getSymbol(left));
			addEquation(eq);
			addFlag = true;
		} else if (up == -1 && down == -1 && (left > -1 || right > -1) && (left == -1 || right == -1)) {//BB=I
			if (left > -1) {
				Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(left),id).addLhs((AlgebraSymbol)gens.getSymbol(left));
				addEquation(eq);
			    addFlag = true;
			} else {
				Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(right),id).addLhs((AlgebraSymbol)gens.getSymbol(right));
				addEquation(eq);
			    addFlag = true;
			}
		} else if (up > -1 && down > -1 && left > -1 && right > -1 && pcFlag){//PC=I, Bb=I, bB=I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(down),id).addLhs((AlgebraSymbol)gens.getSymbol(up));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(left),id).addLhs((AlgebraSymbol)gens.getSymbol(right));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(right),id).addLhs((AlgebraSymbol)gens.getSymbol(left));
			addEquation(eq);
			addFlag = true;
		} else if (up > -1 && down > -1 && left > -1 && right > -1){//PC=I, CP=I, Bb=I, bB=I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(down),id).addLhs((AlgebraSymbol)gens.getSymbol(up));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(up),id).addLhs((AlgebraSymbol)gens.getSymbol(down));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(left),id).addLhs((AlgebraSymbol)gens.getSymbol(right));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(right),id).addLhs((AlgebraSymbol)gens.getSymbol(left));
			addEquation(eq);
			addFlag = true;
		} else if ((up > -1 && down > -1 && sibGen)){//PC=I, CP=I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(down),id).addLhs((AlgebraSymbol)gens.getSymbol(up));
			addEquation(eq);
			eq =  new Equation((AlgebraSymbol)gens.getSymbol(up),id).addLhs((AlgebraSymbol)gens.getSymbol(down));
			addEquation(eq);
			addFlag = true;
		}
		if (spouse > -1){//SpSp = I
			Equation eq =  new Equation((AlgebraSymbol)gens.getSymbol(spouse),id).addLhs((AlgebraSymbol)gens.getSymbol(spouse));
			addEquation(eq);
		}
		return addFlag;
	}

	public boolean isMixedPattern(){
		AlgebraSymbolVector gens = getGenerators();
		int up = -1; int down = -1; int left = -1; int right = -1; int spouse = -1;
		int i = -1;
		for (gens.reset();gens.isNext();){
			i++;
		    AlgebraSymbol gen = gens.getNext();
		//	System.out.println(" ge "+gen +" type "+gen.getArrowType());
			if (gen.getArrowType() == Bops.UP) up = i;
			else if (gen.getArrowType() == Bops.DOWN) down = i;
			else if (gen.getArrowType() == Bops.LEFT) left = i;
			else if (gen.getArrowType() == Bops.RIGHT) right = i;
			else if (gen.getArrowType() == Bops.SPOUSE) spouse = i;
		}
		//System.out.println("up "+up+" down "+down+" left "+left +" right "+right);
		//System.out.println((up > -1 || down > -1) && (left > -1 ||right > -1));
		return (up > -1 || down > -1) && (left > -1 ||right > -1);
	}


	/** change sex marking to "N" for specified element
	* @as set sex marking of as to "N"
	*/
	public void removeSexMarking(AlgebraSymbol as) {
	    if (as.getSex().equals("N")) {}
	    else {
	        AlgebraSymbol as1 = as.getOppositeSexGenerator();
	        as.setSex("N");
            if (as1 == null) {}
	        else {
	            AlgebraSymbolVector s = new AlgebraSymbolVector(5,1);
	            for (theSymbols.reset();theSymbols.isNext();) {
	                AlgebraSymbol as2 = theSymbols.getNext();
	                if (!as2.equals(as1)) s.addToEnd(as2);
	                else s.addToEnd(as);
	            }
	        // System.out.println("theSymbols "+theSymbols+" s = "+s);
                substitute(focalElements,s);
            // substitute(identityElements,s);
                structuralEquations = substitute(structuralEquations,s);
                theSymbols = substitute(theSymbols,s);
	        }
	    }
	}

	/** remove redundant elements and equations
	*/
	public void removeRedundancy(){
        AlgebraSymbolVector gen = new AlgebraSymbolVector(5,1);
	    for (theSymbols.reset();theSymbols.isNext();) {
	        AlgebraSymbol as = theSymbols.getNext();
	        if (gen.indexOf(as) == -1) gen.addToEnd(as);
	    }
        theSymbols = gen;
        gen = new AlgebraSymbolVector(2,1);
	    for (focalElements.reset();focalElements.isNext();) {
	        AlgebraSymbol as = focalElements.getNext();
	        if (gen.indexOf(as) == -1) gen.addToEnd(as);
	    }
        focalElements = gen;
        EquationVector eqv = new EquationVector(5,2);
	    for (structuralEquations.reset();structuralEquations.isNext();) {
	        Equation eq = structuralEquations.getNext();
	        if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
	    }
        structuralEquations = eqv;
	}

    void addLeftSexGeneratorProducts(String sex) {
        EquationVector eqv = new EquationVector(5,2);
        EquationVector rmv = new EquationVector(5,2);
        AlgebraSymbolVector gens = getSexMarkedGenerators(sex);
//System.out.println(" GENS "+gens+" sex "+sex);
        for (gens.reset();gens.isNext();){
            AlgebraSymbol gen = gens.getNext();
           // if (gen.getSex().equals("N")) continue;
            if (gen.isIdentityElement()) continue;
			if (gen.isSexGenerator()) continue;
          //  String sex = gen.getSex();
            AlgebraSymbol as = getElement(sex);
            Equation eq = new Equation(gen,gen).addLhs(as);
            AlgebraSymbol gen1 = gen.getOppositeSexGenerator(this);
            if (gen1 != null) {
                eq = new Equation(gen1,gen).addLhs(as);
                if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
            }
           // System.out.println(" NEW EQ "+eq +" gen "+gen);
            if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
            if (isArrow(gen,Bops.UP)) {
                AlgebraSymbol genr = gen.getReciprocal();
                if (genr.getSex().equals("N")) continue;
	            eq = new Equation(genr,as).addLhs(gen);
                if (eqv.indexOf(eq) == -1) {
                    eqv.addElement(eq);
	                AlgebraSymbol id = getIdentityElement();
	                if (id != null) {
                        eq = new Equation(genr,id).addLhs(gen);
                        if (rmv.indexOf(eq)== -1) rmv.addElement(eq);
                    }
                }
            }
        }
	    for (eqv.reset1();eqv.isNext();) {
	        Equation eq = eqv.getNext();
//	 System.out.println(" THE EQ "+eq);
	        _addEquation(eq);
	    }
	    EquationVector eqs = getEquations();
	    for (rmv.reset1();rmv.isNext();) {
	        Equation eq = rmv.getNext();
            if (eqs.indexOf(eq)!= -1) eqs.removeElementAt(eqs.indexOf(eq));
	    }
    }

	public boolean enterSexGenerator(int sex) {
		AlgebraSymbolVector gens = getGenerators();
		AlgebraSymbolVector sexv = new AlgebraSymbolVector(1);
		String sextype = "";
		AlgebraSymbol gen = null;
		boolean flag = false;
//System.out.println("THE GENS "+gens);
		for (gens.reset();gens.isNext();) {
			gen = gens.getNext();
			if (isArrow(gen,Bops.MALEFEMALE)) {
			    sexv.addElement(gen);
			// System.out.println("ADD gen "+gen);
			}
		}
		if (sexv.size() < 2){
			if (sex == Bops.MALE)
				sextype = "M";
			else
				sextype = "F";
			if (!(sexv.size() == 1 && sextype == sexv.getFirst().getSex())) {
				AlgebraSymbol sexsym = getElement(new AlgebraSymbol(sextype));
				sexsym.setSex(sextype);
				addGenerator(getElement(sextype));
				flag = true;

		//		ka.makeAlg.theAlgebra.setAddReciprocalEquations(.RE_NO);
				Equation eq = null;;
				for (gens.reset();gens.isNext();) {
					gen = gens.getNext();
					if (gen.isIdentityElement()) continue;

			// if (theAlgebra.isArrow(gen,Aops.SPOUSE)) continue;

					eq = new Equation(sexsym,gen).addLhs(gen);
					_addEquation(eq); // xF = x

		//			eq = new Equation(male,gen).addLhs(gen);
		//			theAlgebra._addEquation(eq); // xM = x
				}
				eq = new Equation(sexsym,sexsym).addLhs(sexsym);
				_addEquation(eq);

				if (sexv.size() == 1) {//crossproduct equations with other sex generator
					if (hasIdentityElement()) {
						eq = new Equation(sexsym,getIdentityElement()).addLhs(sexv.getFirst().getSex());
						_addEquation(eq);
						eq = new Equation(sexv.getFirst(),getIdentityElement()).addLhs(sexsym);
						_addEquation(eq);
						sexv.getFirst().setReciprocal(sexsym);
						sexsym.setReciprocal(sexv.getFirst());
					}
					else {//doesn't work with almostRightIdentity; needs fixing
						//eq = new Equation(sexsym,getElement("0")).addLhs(sexv.getFirst());
						eq = new Equation(sexsym,getElement("0")).addLhs(sexv.getFirst());

						_addEquation(eq);
						eq = new Equation(sexv.getFirst(),getElement("0")).addLhs(sexsym);
						_addEquation(eq);
						sexv.getFirst().setReciprocal(sexsym);//need to rethink reciprocals for this case
						sexsym.setReciprocal(sexv.getFirst());
					}
				}
			}
            addLeftSexGeneratorProducts(sextype);
		}

		//may be needed for sp m = f sp definition of sex generators
/*		ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_NO);
		for (gens.reset();gens.isNext();) {
			gen = gens.getNext();
			if (theAlgebra.isArrow(gen,Aops.SPOUSE)) {
				AlgebraSymbol sexF = theAlgebra.getElement("F");
				AlgebraSymbol sexM = theAlgebra.getElement("M");
			    //Equation eq = new Equation(sexF,gen).addLhs(gen).addRhs(sexM);//spF=Msp
			    Equation eq = new Equation(sexF,theAlgebra.getElement("0")).addLhs(gen).addLhs(sexF);//spF=M
				theAlgebra._addEquation(eq);
				//eq = new Equation(sexM,gen).addLhs(gen).addRhs(sexF);//spM=Fsp
				eq = new Equation(sexM,theAlgebra.getElement("0")).addLhs(gen).addLhs(sexM);//spM=F
				theAlgebra._addEquation(eq);
			}
		}
		ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
	*/
	    return flag;
	}

	/* determine if there are both male marked and female marked generators
	* @return boolean
	*/
	public boolean hasSexMarkedGenerators(){
	    AlgebraSymbolVector gens = getGenerators();
	    boolean maleFlag = false; boolean femaleFlag = false;
	    for (gens.reset();gens.isNext();) {
	        AlgebraSymbol gen = gens.getNext();
			if (gen.isSexGenerator()) continue;
	        if (gen.getSex().equals("M")) maleFlag = true;
	        else if (gen.getSex().equals("F")) femaleFlag = true;
	    }
	    return (maleFlag && femaleFlag);
	}

		/* determine if there are sib generators
	* @return boolean
	*/
	public boolean hasSibGenerators(){
	    AlgebraSymbolVector gens = getGenerators();
	    for (gens.reset();gens.isNext();) {
	        AlgebraSymbol gen = gens.getNext();
	        if (gen.getArrowType() == Bops.LEFT || gen.getArrowType() == Bops.RIGHT) return true;
	    }
	    return false;
	}

	EquationVector getRecursiveEquations(){
	    EquationVector ret = new EquationVector();
	    EquationVector eqs = getEquations();
	    for (eqs.reset();eqs.isNext();){
	        Equation eq = eqs.getNext();
	        if (eq.isRecursiveEquation() && ret.indexOf(eq) == -1)
	            ret.addElement(eq);
	    }
	    return ret;
	}

	EquationVector getZeroEquations(){
	    EquationVector ret = new EquationVector();
	    EquationVector eqs = getEquations();
	    for (eqs.reset();eqs.isNext();){
	        Equation eq = eqs.getNext();
	        if (eq.isZeroEquation() && ret.indexOf(eq) == -1)
	            ret.addElement(eq);
	    }
	    return ret;
	}


}

// ---------- Commented out code to end to delete!!!!!!!!!!!!!!

/*

		s_equiv,          {structurally equivalent pairs of terms}
		splitPtr				{pointer for list of terms to be split}
																: intPointer;
		nPtr,					{number of lists of terms}
		unifyB            {flags if compounds are unified}
	*/

 /*   public Algebra makeAlgebraJoinX(Algebra a) {
        AlgebraSymbol id1 = getIdentityElement();
        AlgebraSymbol id2 = a.getIdentityElement();
        EquationVector eqv = new EquationVector(10,5);
        EquationVector rmv = new EquationVector(2,1);
        if (!id1.equals(id2)) {
            if (id1 != null) constructIDEquations();
            if (id2 != null) a.constructIDEquations();
        }else if (!id1.getSex().equals(id2.getSex())) {
            enterSexGenerator(Bops.FEMALE);
            a.enterSexGenerator(Bops.FEMALE);
            enterSexGenerator(Bops.MALE);
            a.enterSexGenerator(Bops.MALE);
            AlgebraSymbolVector gens = getCurrent().getGenerators();
            for (gens.reset();gens.isNext();){
                AlgebraSymbol gen = gens.getNext();
                if (gen.isIdentityElement()) continue;
                if (gen.toString().equals("M") || gen.toString().equals("F")) continue;
                String sex = gen.getSex();
                AlgebraSymbol as = getCurrent().getElement(sex);
                Equation eq = new Equation(gen,gen).addLhs(as);
                if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
                if (getCurrent().isArrow(gen,Bops.UP)) {
                    AlgebraSymbol genr = gen.getReciprocal();
	                eq = new Equation(genr,as).addLhs(gen);
                    if (eqv.indexOf(eq) == -1) {
                        eqv.addElement(eq);
                        eq = new Equation(genr,id1).addLhs(gen);
                        if (rmv.indexOf(eq)== -1) rmv.addElement(eq);
                    }
                 }
            }
            gens = getGenerators();
            for (gens.reset();gens.isNext();){
                AlgebraSymbol gen = gens.getNext();
                if (gen.isIdentityElement()) continue;
                if (gen.toString().equals("M") || gen.toString().equals("F")) continue;
                String sex = gen.getSex();
                AlgebraSymbol as = getElement(sex);
                Equation eq = new Equation(gen,gen).addLhs(as);
                if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
                AlgebraSymbol gen1 = gen.getOppositeSexGenerator(a,this);
               eq = new Equation(gen1,gen).addLhs(as);
               if (eqv.indexOf(eq) == -1) eqv.addElement(eq);
                if (isArrow(gen,Bops.UP)) {
                    AlgebraSymbol genr = gen.getReciprocal();
                    //AlgebraSymbol gen1r = gen1.getReciprocal();
                    eq = new Equation(genr,as).addLhs(gen);
                    if (eqv.indexOf(eq) == -1) {
                        eqv.addElement(eq);
                        eq = new Equation(genr,id1).addLhs(gen);
                        if (rmv.indexOf(eq)== -1) rmv.addElement(eq);
                    }
                  //  eq = new Equation(gen1r,as).addLhs(gen);
                    //if (eqv.indexOf(eq) == -1) {
                      //  eqv.addElement(eq);
                        //eq = new Equation(gen1r,id1).addLhs(gen);
                        //if (rmv.indexOf(eq)== -1) rmv.addElement(eq);
                    //}
                }
            }
            id1.setSex("N");
            id2.setSex("N");
        }
        AlgebraSymbolVector g1 = getElements();
	    AlgebraSymbolVector g2 = a.getElements().deepClone();
	    for (g2.reset();g2.isNext();){
	        AlgebraSymbol as = g2.getNext();
	        if (g1.indexOf(as) == -1) g1.addElement(as);
	    }
	    AlgebraSymbolVector fe1 =getFocalElements();
	//    System.out.println(" focals "+a.getFocalElements());
	    AlgebraSymbolVector fe2 = a.getFocalElements().deepClone();
	    for (fe2.reset();fe2.isNext();){
	        AlgebraSymbol as = fe2.getNext();
	        if (fe1.indexOf(as) == -1) fe1.addElement(as);
	    }
	    EquationVector e1= getEquations();
	    EquationVector e2 = a.getEquations();
	    for (e2.reset();e2.isNext();) {
	        Equation eq = a.substitute(e2.getNext(),g2);
	        if (e1.indexOf(eq) == -1) e1.addElement(eq);
	    }
	    for (eqv.reset();eqv.isNext();) {
	        Equation eq = eqv.getNext();
	        _addEquation(eq);
	    }
	    EquationVector e3 = getCurrent().getEquations();
	    for (rmv.reset1();rmv.isNext();) {
	         Equation eq = rmv.getNext();
	   System.out.println("REMOVE "+eq);
             if (e1.indexOf(eq)!= -1) e1.removeElementAt(e1.indexOf(eq));
             if (e2.indexOf(eq)!= -1) e2.removeElementAt(e2.indexOf(eq));
            if (e3.indexOf(eq)!= -1) e3.removeElementAt(e3.indexOf(eq));

	    }
	    System.out.println(" THIS "+this.getEquations());
	    return this;

		    void addSexIdEquations(AlgebraSymbolVector id) {
        AlgebraSymbolVector gens = getGenerators();
        for (gens.reset();gens.isNext();) {
            AlgebraSymbol gen = gens.getNext();
            String sex = gen.getSex();
            if (!sex.equals("N")) {
                for (id.reset();id.isNext();) {
                    AlgebraSymbol as = id.getNext();
                    String sex1 = as.getSex();
            // System.out.println("gen sex "+ gen+" "+sex+" id sex "+as + " "+sex1);
                   if ((!sex1.equals("N"))&& (!sex.equals(sex1))) {
                        Equation eq = new Equation(gen,Algebra.getCurrent().getElement("0")).addLhs(as);
                        addEquation(eq);
                        eq = new Equation(as,Algebra.getCurrent().getElement("0")).addLhs(gen);
                        addEquation(eq);
						System.out.println("NNNNNNNNNNNNNNNNNNNN eq "+eq);
                   }
                }
            }
        }
    }

	}*/

/*	public void addCrowSkewingEquationsX() {
	   AlgebraSymbolVector gens = getGenerators();
		AlgebraSymbol fa = null;
		AlgebraSymbol mo = null;
		AlgebraSymbolVector si = new AlgebraSymbolVector();
		EquationVector newEqs = new EquationVector();
		Equation e = new Equation();
		for (gens.reset();gens.isNext();){
		   AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType() == Bops.UP && gen.getSex().equals("M")) {
				fa = gen;
			} else if (gen.getArrowType() == Bops.UP && gen.getSex().equals("F")) {
				mo = gen;
			} else if ((gen.getArrowType() == Bops.LEFT || gen.getArrowType() == Bops.RIGHT) &&
			gen.getSex().equals("F")) {
				si.addElement(gen);
				//System.out.println(" gen "+gen);
			}else if (gen.getArrowType() == Bops.IDENTITY && gen.getSex().equals("F")){
				si.addElement(gen);
			}
		}
		for (si.reset();si.isNext();){
		   AlgebraSymbol as = si.getNext();
			e = new Equation(fa,fa).addLhs(as).addRhs(mo);
	//System.out.println(" e "+e);
			newEqs.addElement(e);
		}
		for (newEqs.reset1();newEqs.isNext();){
			e = newEqs.getNext();
	//System.out.println(" e  new"+e);
			e = addEquation(e);
	//System.out.println(" e  after "+e);
		}
	}*/

/*	boolean reducibleSide(AlgebraPath side){
		AlgebraSymbolVector side1 = side.getReducedProductPath();
		if (side1.size() == 1) return true;
		else {System.out.println("path side "+ side+ " reduced "+side1);return true;}
	}

	boolean reducibleSide(AlgebraSymbolVector side){
		AlgebraPath ap = new AlgebraPath(side);
		//if (ap.getReducedProductPath().equals(side)) return false;

		if (ap.getReducedProductPath().size() != 1) return false;

		else {System.out.println(" side "+ side +" reduced "+ap.getReducedProductPath());return true;}
	}*/


