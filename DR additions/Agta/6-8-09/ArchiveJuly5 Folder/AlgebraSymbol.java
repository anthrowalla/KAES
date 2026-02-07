/* Revision History
* 31/10 MF Changed System.out.println to Debug.prout(1,M).
* 5/29 DR added test "if (this.theSymbol.equals("")) return this;" to procedure getReciprocal()
* 9/24 DR added else ft = null; to getSexMatchingFocalTerm(); added getReciprocal(AlgebraSymbol ft);
* 9/28 DR added added getReciprocal(String sex);
* added procedure getOppositeSexGenerator() to get generator with opposite sex to this
* 10/12 DR added procedure isZeroElement()
* 10/22 DR added procedure isSexGenerator()
*/

public class AlgebraSymbol extends Object implements Cloneable, ToXML {
	/** name/value of the algebra symbol
	*/
	String theSymbol="";
	/** name of the reciprocal algebra symbol if any
	*/
	AlgebraSymbol theReciprocal=null;
	/** name of equivalents to the algebra symbol if any
	*/
	AlgebraSymbolVector theEquated = null;
	/** for bifurcating symbols into covered and covering
	*/
	AlgebraSymbol [] splitAtom = null;
	/** equations where we're on the LHS
	*/
	EquationVector lhs = new EquationVector(10,5);
	/** equations where we're on the RHS
	*/
	EquationVector rhs = new EquationVector(10,5);

	/** sex associated with symbol ... possibly generalise in future
	*/
	String sex="N";

	/** designator for type of symbol
	*/
	String symbolType=""; // I = identity G generator S Spouse and U unified

	/** ????ordinal value for this if a generator
	*/
	int generatorOrd=0;

	/**
	   the arrow type === access on through getArrowType as is calculated when needed.
    */
	int arrowType = -1;

	/** is this a focal element?
	*/
	boolean focalElement=false;

	public String toXML() {
		return "<AlgebraSymbol symbol=\""+theSymbol+(isGenerator() ? "\" isGenerator=\"true\"/>":"/>");
	}
	/** define a new AlgebraSymbol
	* @param s the value to assign to the AlgebraSymbol
	* @return the new AlgebraSymbol
	*/
	public AlgebraSymbol(String s) {
		theSymbol = s;
	}
	/** define a new AlgebraSymbol
	* @return the new AlgebraSymbol
	*/
	public AlgebraSymbol() {
	}

	/** compares the value of two Algebra symbols based on value/name only
	* @param x algebra symbol to compare to
	* @return true or false
	*/
	public boolean equals(Object x) {
		x = ((AlgebraSymbol)x).getAlgebraSymbol();
		if (theSymbol.equals(((AlgebraSymbol)x).getValue(this))) {
			Debug.prout("Got one");
			return true;
		}
		return false;
	}

	/** set the sex of this algebra symbol
	*/
	public void setSex(String sex) {
		this.sex = sex;
	}


	/** get the sex of this algebra symbol
	* @return the sex
	*/
	public String getSex() {
		return sex;
	}

	public String getOppositeSex() {
	    String ret = "N";
	    if (getSex().equals("M")) ret =  "F";
	    else if (getSex().equals("F")) ret = "M";
	    return ret;
	}


	/** set the arrowType of this algebra symbol
	*/
	public void setArrowType(int aType) {
		this.arrowType = aType;
	}


	/** set the value of this algebra symbol to a name
	*/
	public void setValue(String s) {
		theSymbol = s;
	}

	/** get the value assigned to this algebra symbol
	* @return the value
	*/
	public String getValue() {
		return theSymbol;
	}

	/** get the value of an algebra symbol
	* @param s the algebra symbol to return value/name of
	* @return the value
	*/
	public String getValue(AlgebraSymbol s) {
		return theSymbol;
	}

	/** AlgebraSymbols are always bound ... default method does nothing
	* @see AlgebraSymbolVar
	*/
	public void unBind() {

	}
	/** returns value/name as string
	*/
	public String toString() {
		return getValue();
	}
	/** return this ... rather reflexive to accomodate unbound AlgebraSymbolVars
	* @see AlgebraSymbolvar
	* @return name/value
	*/
	public AlgebraSymbol getAlgebraSymbol() {
		return this;
	}
	/** tests if this is identity element
	* @return true if identity element
	*/
	public boolean isIdentityElement() {
		return getAlgebraSymbol().symbolType.equals("I");
	}

	/** tests if this is zero element
	* @return true if zero element
	*/
	public boolean isZeroElement() {
		return getAlgebraSymbol().getValue().equals("0");

	}
	/** tests if this is sex generator
	* @return true if sex generator
	*/
	public boolean isSexGenerator() {
		return getAlgebraSymbol().getValue().equals("M")||
		getAlgebraSymbol().getValue().equals("F");
	}

	/** tests if this is sib generator
	* @return true if sib generator
	*/

	public boolean isSibGenerator() {
	   return (getArrowType() == Bops.LEFT || getArrowType() == Bops.RIGHT);
	}
	/** sets this to be identity element
	*/

	public void setIdentityElement() {
		getAlgebraSymbol().symbolType = "I";
	}

	/** tests if this is afocal element
	* @return true if a focal element
	*/

	public boolean isFocalElement() {
		return getAlgebraSymbol().focalElement;
	}

	public void setFocalElement(boolean a) {
		getAlgebraSymbol().focalElement = a;
	}
	/** tests if this is self-reciprocal
	* @return true if this is self-reciprocal
	*/

	public boolean isSelfReciprocal() {
		return getAlgebraSymbol().getReciprocal() == this;
	}

	/*public void setSelfReciprocal() {
		theReciprocal = this;
	}*/

	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!
	// Revise when we get to this ... !!!!!!!!!!!!!!!!!!!!!!!!**********!!!!!!!!!!!!!!

	/** equate two algebra symbols
	* @param a the algebra symbol to equate to
	*/
	public void setEquated(AlgebraSymbol a) { // revise
		if (theEquated == null) theEquated = new AlgebraSymbolVector();
		theEquated.addElement(a);
		//a.setEquated(this);
	}

	/** equate two algebra symbols
	* @param a the algebra symbol to equate to
	* @param e ???the algebra symbol to cover this and a as bifurcated terms
	*/
	public void setEquated(AlgebraSymbol a, AlgebraSymbol e) {
		if (theEquated == null) theEquated = new AlgebraSymbolVector();
		theEquated.addElement(a);
		//a.setEquated(this);
		e.setBifurcated(a,this);
	}
// ---------------------------------------------------------------------

	/** get the set of equated algebra symbols
	* @return the set of equated algebra symbols
	*/
	public AlgebraSymbolVector getEquated() {
		return getAlgebraSymbol().theEquated;
	}

	/** set two algebra symbols that this symbol will cover
	* @see getBifurcated()
	* @param a symbol at return array position 0
	* @param e symbol at return array position 1
	*/
	public void setBifurcated(AlgebraSymbol a, AlgebraSymbol e) {
		if (splitAtom == null) splitAtom = new AlgebraSymbol[2];
		splitAtom[0] = a;
		splitAtom[1] = e;
	}

	/** return an array of two Algebra symbols that are covered by this term, if any
	* @return AlgebraSymbol[0..1]
	* 0 = first symbol, 1 = second symbol
	* @see setBifurcated();
	*/
	public AlgebraSymbol[] getBifurcated() {
		return splitAtom;
	}

	/** add equation to list of equations we appear in the LHS of
	* @param e the equation to add
	*/
	public void addLhs(Equation e) { //
		if (getAlgebraSymbol().lhs.indexOf(e) == -1) getAlgebraSymbol().lhs.addElement(e);
	}

	/** add equation to list of equations we appear in the RHS of
	* @param e the equation to add
	*/
	public void addRhs(Equation e) { //
		if (getAlgebraSymbol().rhs.indexOf(e) == -1) getAlgebraSymbol().rhs.addElement(e);
	}

	/** is this a generator
	* @return true if generator
	*/
	public boolean isGenerator() {
		return getAlgebraSymbol().generatorOrd > 0;
	}

	/** get ordinal position of generator
	* @return ordinal value of generator, 0 if not a generator
	*/
	public int getGenerator() {
		return getAlgebraSymbol().generatorOrd;
	}

	/** get the type of this symbol
	* @return the type as String
	*/
	public String getSymbolType() {
		return getAlgebraSymbol().symbolType;
	}

	/** set the ordinal value of this as a generator
	* @param the ordinal value of generator
	*/
	public void setGenerator(int n) {
		getAlgebraSymbol().generatorOrd = n;
		getAlgebraSymbol().symbolType = "G";
	}

	/** get the focal term with same sex as a generator
	* @return the focal term with the same sex if any (??? if no focal term)
	*/
	public AlgebraSymbol getSexMatchingFocalTerm() {
		AlgebraSymbol ft = null;
		AlgebraSymbolVector ftset = Algebra.getCurrent().getFocalElements();
//System.out.println("the focal elements are="+Algebra.getCurrent().getFocalElements());
		if (ftset.size() > 0) {
			for (ftset.reset();ftset.isNext();) {
				ft = ftset.getNext();
				//System.out.println("getsex= "+getSex()+" ft= "+ft+" ft.xes= "+ft.getSex());
				if (getSex().equals(ft.getSex()))
					return ft;
				else ft = null;
			}
		}
		return ft;
	}

	/** get the reciprocal of this symbol based on sex
	* @param the focal term
	* @return the reciprocal if any (??? if no reciprocal)
	*/

	public AlgebraSymbol getReciprocal(String sex) {
		int [] aset = {Bops.UP,Bops.DOWN,Bops.LEFT,Bops.RIGHT};
		if (isFocalElement()){
		    AlgebraSymbolVector fev = Algebra.getCurrent().getFocalElements();
		    for (fev.reset();fev.isNext();){
		        AlgebraSymbol fe = fev.getNext();
		        if (fe.getSex().equals(sex))
		            return fe;
		    }
		    return null;
		}
	    if (getSex().equals(sex)){
	    	return getReciprocal();
		} else if (getArrowType() == Bops.SPOUSE || getArrowType() == Bops.SPOUSER){
			return getReciprocal();
		}
	    else {
	    	for (int i=0;i<4;i++) {
	    		//if (Algebra.getCurrent().isArrow(this,aset[i])) {
	    		if (this.getArrowType() == aset[i]) {
	    			AlgebraSymbolVector arrows = Algebra.getCurrent().getArrows(this.getArrowType());
					for (arrows.reset();arrows.isNext();) {
						AlgebraSymbol arrow = arrows.getNext();
						if (arrow.getSex().equals(sex)){
							return arrow.getReciprocal();
						}
	    			}
	    		}
	    	}
	    }
	    return getAlgebraSymbol().theReciprocal;
	}

	/** get the reciprocal of this symbol based on sex of focal term
	* @param the focal term
	* @return the reciprocal if any (??? if no reciprocal)
	*/

	public AlgebraSymbol getReciprocal(AlgebraSymbol ft) {
	    return getReciprocal(ft.getSex());
	}

	/** get the reciprocal of this symbol
	* @return the reciprocal if any (??? if no reciprocal)
	*/

	public AlgebraSymbol getReciprocal() {
	   // System.out.println("XXXXXXXXXXXX this "+this+" rec "+getAlgebraSymbol().theReciprocal);
		if (this.theSymbol.equals("")) return this;
		else if (this.getValue().equals("&")) return getAlgebraSymbol().theReciprocal;
	    else if (getAlgebraSymbol().theReciprocal == null) {
		    if (!Algebra.getCurrent().hasFocalElements()) {
			    // Message A need to get some focal terms ++++
			    return getAlgebraSymbol().theReciprocal;
		    }
			String gen = getAlgebraSymbol().getValue();

		    AlgebraPath e1, e2, e3;
		    AlgebraSymbolVector gv = Algebra.getCurrent().getGenerators();
		    AlgebraSymbolVector fv = Algebra.getCurrent().getFocalElements();

		    for(int i=0;i<gv.size();i++) {
			    String tgen = gv.getSymbol(i).getValue();
			    e1 = new AlgebraPath(Algebra.getCurrent().makePath(gen+" "+tgen),true);
			    e2 = new AlgebraPath(Algebra.getCurrent().makePath(tgen+" "+gen),true);
			    for(int j=0;j<fv.size();j++) {
				    e3 = new AlgebraPath(fv.getSymbol(j));
				   // System.out.println("e3 : "+e3);
				    //System.out.println("getReciprocal A : a="+e1+" "+e1.getReducedProductPath()+ " b=" + e2+" "+ e2.getReducedProductPath());
				    if (e1.equals(e3)) {
				        theReciprocal = gv.getSymbol(i);
				        theReciprocal.setReciprocal(getAlgebraSymbol());
				        return getAlgebraSymbol().theReciprocal;
				    }
				    if (e2.equals(e3)) {
				        theReciprocal = gv.getSymbol(i);
				        theReciprocal.setReciprocal(getAlgebraSymbol());
				        return getAlgebraSymbol().theReciprocal;
				    }
				   // System.out.println("getTHEReciprocal B : a="+gen+" "+tgen);
			    }
		    }
		    // Message B  need to get some focal terms ++++

	    }
	    return getAlgebraSymbol().theReciprocal;
	}


	/** set the reciprocal of this symbol
	* @param the reciprocal symbol
	*/
	public void setReciprocal(AlgebraSymbol r) {
		getAlgebraSymbol().theReciprocal = r;
		if (r.getReciprocal() != this) r.setReciprocal(this);
	}

	/** get the product of this symbol with a list of symbols
	* @param a the list of symbols
	* @return the product list
	*/
	public AlgebraSymbolVector getProductOLd(AlgebraSymbolVector a) {
	    a.addToEnd(this);
		return a;
	}

	public AlgebraSymbolVector getProduct(AlgebraSymbolVector a) {
		int i = a.indexOf(Algebra.getCurrent().getElement("&"));
		int j = a.size();
	    if (a.equivalentProduct() && ((j/2)*2 !=j) && i == ((a.size()-1)/2)) {// is middle element
			System.out.println(" a start "+a);
			AlgebraSymbolVector aL = a.equivalentLeftProduct();
			AlgebraSymbolVector ret = a.equivalentRightProduct();
			//a1 = a;
	        aL.addToEnd(this);
	        ret.addToEnd(this);
			ret.addToEnd((AlgebraSymbol)a.elementAt(i));
			for (aL.reset();aL.isNext();) ret.addToEnd(aL.getNext());
			//System.out.println("this "+ this +" a "+ a +" aL "+aL+" ret "+ret);
			return ret;
	    }
			//if (a.toString().indexOf("&") > -1)
			//System.out.println(" a start "+a);
	    a.addToEnd(this);
		return a;
	}

	/** get the product of this symbol with a list of symbols by adding to front
	* @param a the list of symbols
	* @return the product list
	*/
	public AlgebraSymbolVector getProductLR(AlgebraSymbolVector a) {
		int i = a.indexOf(Algebra.getCurrent().getElement("&"));
		int j = a.size();
	    if (a.equivalentProduct() && ((j/2)*2 !=j) && i == ((a.size()-1)/2)) {
			//System.out.println(" a start1 "+a);
			AlgebraSymbolVector aL = a.equivalentLeftProduct();
			AlgebraSymbolVector ret = a.equivalentRightProduct();
			//a1 = a;
	        aL.addToBeginning(this);
	        ret.addToBeginning(this);
			ret.addToEnd((AlgebraSymbol)a.elementAt(i));
			for (aL.reset();aL.isNext();) ret.addToEnd(aL.getNext());
			//System.out.println("this "+ this +" a "+ a +" aL "+aL+" ret "+ret);
			return ret;
	    }
		a.addToBeginning(this);
		return a;
	}

	/** clone this symbol
	* @return a clone of this symbol
	*/
	public AlgebraSymbol makeClone() {
		AlgebraSymbol k;
		try {
			k =  (AlgebraSymbol) this.clone();
		} catch (Exception e) {
			Debug.prout("Problem with makeClone: "+this.toString());
			k = null;

		}
		return k;
	}

	int getArrowType(Algebra a){
		if (getValue().equals("0")) return -1;
	   if (arrowType != -1) return arrowType;
	   if (isIdentityElement()) return arrowType;
		int [] aset = {AopsOps.UP,AopsOps.DOWN,AopsOps.RIGHT,AopsOps.LEFT,AopsOps.SPOUSE,AopsOps.SPOUSER,AopsOps.SEXGEN};
	    for (int i=0;i<aset.length;i++) {
			//System.out.println(" 333 i "+i +" aset "+aset[i]);
		   if (a.isArrow(this,aset[i])) {
			   arrowType = aset[i];
	            return aset[i];
		   }
	    }
	    return AopsOps.NONE;
	}

	int getArrowType(){
	    return getArrowType(Algebra.getCurrent());
	}

	int getArrowTypex(){
		int [] aset = {AopsOps.UP,AopsOps.DOWN,AopsOps.RIGHT,AopsOps.LEFT,AopsOps.SPOUSE};
	    for (int i=0;i<aset.length;i++) {
	        if (Algebra.getCurrent().isArrow(this,aset[i]))
	            return aset[i];
	    }
	    return -1;
	}

	/** get the generator in same direction but of opposite sex
	* @return the opposite sex generator
	*/
	public AlgebraSymbol getOppositeSexGenerator(){
        return getOppositeSexGenerator(Algebra.getCurrent());
    }


/*	public AlgebraSymbol getOppositeSexGenerator(){
	    if (this.getValue().equals("0")) return this;
	    if (this.getSex().equals("N")) return this;
	    String sex = getOppositeSex();
	    int arrow = getArrowType();
//System.out.println("THIS "+this+" sex"+ sex+" arrow "+arrow);
	    if (arrow != -1){
	        AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators(arrow);
	        for (gens.reset();gens.isNext();) {
	            AlgebraSymbol g = gens.getNext();
//System.out.println(" THE g "+g+" thesex "+g.getSex());
	            if (g.getSex().equals(sex))
	                return g;
	        }
	    }
	    return null;
	}*/

	/** get the generator in same direction but of opposite sex
	* @a algebra with the opposite sex generator
	* @return the opposite sex generator
	*/

	public AlgebraSymbol getOppositeSexGenerator(Algebra a){
  //  System.out.println(" XXXXYYYYYY "+this.getValue()+" sex "+this.getSex());
	    if (this.getValue().equals("0")) return this;
	    if (this.getSex().equals("N")) return this;
	    String sex = getOppositeSex();
	    int arrow = getArrowType(a);
		//System.out.println(" this "+this+ " type "+getArrowType(a));
	    {//if (arrow != -1){
	        AlgebraSymbolVector gens = a.getGenerators(arrow);
	        for (gens.reset();gens.isNext();) {
	            AlgebraSymbol g = gens.getNext();
//System.out.println(" THE g "+g+" thesex "+g.getSex()+" gens "+a.getGenerators());
	            if (g.getSex().equals(sex))
	                return g;
	        }
	    }
	    return null;
	}

	public AlgebraSymbol getOppositeSexGenerator(Algebra a, Algebra a1){
	    if (this.getValue().equals("0")) return this;
	    if (this.getSex().equals("N")) return this;
	    String sex = getOppositeSex();
	    int arrow = getArrowType(a1);
//System.out.println("THIS "+this+" sex"+ sex+" arrow "+arrow);
	    if (arrow != -1){
	        AlgebraSymbolVector gens = a.getGenerators(arrow);
	        for (gens.reset();gens.isNext();) {
	            AlgebraSymbol g = gens.getNext();
//System.out.println(" THE g "+g+" thesex "+g.getSex());
	            if (g.getSex().equals(sex))
	                return g;
	        }
	    }
	    return null;
	}

	/** get the arrow in opposite direction
	* @return the opposite arrow
	*/

	public int getOppositeArrow(){
    //System.out.println(" XXXXYYYYYY "+this.getValue()+" sex "+this.getSex()+" ty;e "+getArrowType());
	    if (this.getValue().equals("0")) return Bops.NONE;
	    int arrow = getArrowType();
		int aType = Bops.NONE;
		switch (arrow){
		    case Bops.UP:
			    aType = Bops.DOWN;
			    break;
			case Bops.DOWN:
			    aType = Bops.UP;
			    break;
		    case Bops.LEFT:
			    aType = Bops.RIGHT;
			    break;
			case Bops.RIGHT:
			    aType = Bops.LEFT;
			    break;
		    case Bops.SPOUSE:
			    aType = Bops.SPOUSER;
			    break;
			case Bops.SPOUSER:
			    aType = Bops.SPOUSE;
			    break;
			default:
				aType = Bops.NONE;
		}
	    return aType;
	}


	/** get the generator in opposite direction but of same sex
	* @return the opposite arrow generator
	*/

	public AlgebraSymbol getOppositeArrowSameSexGenerator(){
    //System.out.println(" XXXXYYYYYY "+this.getValue()+" sex "+this.getSex()+" ty;e "+getArrowType());
	    if (this.getValue().equals("0")) return this;

	    int aType = getOppositeArrow();
	    if (aType != Bops.NONE) {
	        AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators(aType);
			//System.out.println("bbbbbbbbbbb gens "+gens+" atype "+aType);
	        for (gens.reset();gens.isNext();) {
	            AlgebraSymbol g = gens.getNext();
//System.out.println(" THE g "+g+" thesex "+g.getSex()+" gens "+a.getGenerators());
	            if (g.getSex().equals(getSex()))
	                return g;
	        }
	    }
	    return null;
	}


}

