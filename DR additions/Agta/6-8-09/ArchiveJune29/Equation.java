
/* History
* 2/17 DR added procedure toText()
* 9/30 DR added procedure isIdEquation()
* 10/12 DR added procedure iszeroEquation(){
* 11/22 DR added procedure isRecursiveEquation()
* 11/10/02 DR added SIBDEF equation type
*/

public class Equation {
	AlgebraSymbolVector lhs = new AlgebraSymbolVector(2,1);
	AlgebraSymbolVector rhs = new AlgebraSymbolVector(2,1);

	int eqType = -1;

	public int getEqType(){
		return eqType;
	}

	public void setEqType(int i){
		if (eqType == NONINVERTIBLE) eqType = eqType +i;
		else eqType = i;
	}

	public Equation addRhs(AlgebraSymbol a) {
		rhs.addToEnd(a);
		a.addRhs(this);
		return this;
	}


	public Equation addLhs(AlgebraSymbol a) {
		lhs.addToEnd(a);
		a.addLhs(this);
		return this;
	}

	public Equation addRhs(String s) {
		AlgebraSymbol a = Algebra.getCurrent().getElement(s);
		rhs.addToEnd(a);
		a.addRhs(this);
		return this;
	}

	public Equation addLhs(String s) {
		AlgebraSymbol a = Algebra.getCurrent().getElement(s);
		lhs.addToEnd(a);
		a.addLhs(this);
		return this;
	}


	public Equation addLhs(String s,String s2) {
		AlgebraSymbol a = Algebra.getCurrent().getElement(s2);
		lhs.addToEnd(a);
		a.addLhs(this);
		a = Algebra.getCurrent().getElement(s);
		lhs.addToEnd(a);
		a.addLhs(this);
		return this;
	}

	public Equation addRhs(String s,String s2) {
		AlgebraSymbol a = Algebra.getCurrent().getElement(s2);
		rhs.addToEnd(a);
		a.addRhs(this);
		a = Algebra.getCurrent().getElement(s);
		rhs.addToEnd(a);
		a.addRhs(this);
		return this;
	}

	public AlgebraSymbolVector getLhs() {
		return lhs;
	}

	public AlgebraSymbolVector getRhs() {
		return rhs;
	}

	public void setLhs(AlgebraSymbolVector a) {
		lhs=a;
	}

	public void setRhs(AlgebraSymbolVector a) {
		rhs=a;
	}

	public Equation() {
	}

	public Equation(AlgebraSymbolVector a, AlgebraSymbolVector b) {
		setLhs(a);
		setRhs(b);
	}

	public Equation(AlgebraSymbol a, AlgebraSymbol b) {
		setLhs(new AlgebraSymbolVector(a));
		setRhs(new AlgebraSymbolVector(b));
	}

	public boolean isLhs(AlgebraSymbolVector cand) {
		if (cand.size() < lhs.size()) return false;
		Debug.prout("Comparing "+lhs.toString()+" "+cand.toString());
		for(int i = 0;i<lhs.size();i++) {
			if (!((AlgebraSymbol) cand.elementAt(i)).equals((AlgebraSymbol) lhs.elementAt(i))) return false;
		}
		Debug.prout("Is True "+lhs.toString());
		return true;
	}

	public boolean isRhs(AlgebraSymbolVector cand) {
		if (cand.size() < rhs.size()) return false;
		Debug.prout("Comparing "+rhs.toString()+" "+cand.toString());

		for(int i = 0;i<rhs.size() ;i++) {
			if (!((AlgebraSymbol) cand.elementAt(i)).equals((AlgebraSymbol) rhs.elementAt(i))) return false;
		}
		Debug.prout("Is True "+rhs.toString());
		return true;
	}

	public AlgebraSymbolVector substituteRhs(AlgebraSymbolVector expr) {
		if (isLhs(expr)) {
			int t = lhs.size();
			for(int i=0;i<t;i++) expr.removeEnd();
		/*	for (int i=0;i<rhs.size();i++) {
				expr.addToEnd(rhs.getSymbol(i));
			}*/

			Debug.prout("Substituting "+rhs.toString());
			return rhs;//.copy();

		} else return null;
	}

	public AlgebraSymbolVector substituteLhs(AlgebraSymbolVector expr) {
		if (isRhs(expr)) {
			int t = rhs.size();
			for(int i=0;i<t;i++) expr.removeEnd();
			//t = expr.size() - t - 1;
			/*for (int i=0;i<lhs.size();i++) {
				expr.addToEnd(lhs.getSymbol(i));
			}*/

			Debug.prout("Substituting "+lhs.toString());
			return lhs;//.copy();
		} else return null;
	}

	public boolean isLhsLR(AlgebraSymbolVector cand) {
		if (cand.size() < lhs.size()) return false;
		Debug.prout("Comparing "+lhs.toString()+" "+cand.toString());
		for(int i = 0;i<lhs.size();i++) {
			if (!((AlgebraSymbol) cand.getSymbol(i)).equals((AlgebraSymbol) lhs.getSymbol(i))) return false;
			Debug.prout("Compared "+lhs.toString()+" "+cand.toString());
		}
		Debug.prout("Is True "+lhs.toString());
		return true;
	}

	public boolean isRhsLR(AlgebraSymbolVector cand) {
		if (cand.size() < rhs.size()) return false;
		Debug.prout("Comparing "+rhs.toString()+" "+cand.toString());

		for(int i = 0;i<rhs.size();i++) {
			if (!((AlgebraSymbol) cand.getSymbol(i)).equals((AlgebraSymbol) rhs.getSymbol(i))) return false;
		}
		Debug.prout("Is True "+rhs.toString());
		return true;
	}

	public AlgebraSymbolVector substituteRhsLR(AlgebraSymbolVector expr) {
		if (isLhsLR(expr)) {
			int t = lhs.size();
			for(int i=0;i<t;i++) expr.removeBeginning();
		/*	for (int i=0;i<rhs.size();i++) {
				expr.addToEnd(rhs.getSymbol(i));
			}*/

			Debug.prout("Substituting "+rhs.toString());
			return rhs;//.copy();

		} else return null;
	}

	public AlgebraSymbolVector substituteLhsLR(AlgebraSymbolVector expr) {
		if (isRhsLR(expr)) {
			int t = rhs.size();
			for(int i=0;i<t;i++) expr.removeBeginning();
			//t = expr.size() - t - 1;
			/*for (int i=0;i<lhs.size();i++) {
				expr.addToEnd(lhs.getSymbol(i));
			}*/

			Debug.prout("Substituting "+lhs.toString());
			return lhs;//.copy();
		} else return null;
	}

	public String toString() {
		return lhs.toString() + ":"+ rhs.toString();
	}

	public boolean equals(Object e) {
		return toString().equals(e.toString());
	}

	public String toText() {
        String s = this.toString();
		if (s.indexOf("M") > 0 || s.indexOf("F")> 0) return "";
		if (s.endsWith("[]"))
		    s = s.replace(',','_').substring(0,s.indexOf("[]"))+"[_]";
		else if (s.startsWith("[,")) return "";
		s = s.replace(',',' ');
		String s1 = "";
		for (int i=0;i<s.length();i++) {
		    if (!s.substring(i,i+1).equals(" "))
		        s1 = s1+s.substring(i,i+1);
		}
		s1 = s1.replace('[',' ').replace(']',' ').replace(':','=').trim();
        return s1;
    }

    public boolean isIdEquation(){
        AlgebraSymbolVector lhs = getLhs();
        for (lhs.reset();lhs.isNext();){
            AlgebraSymbol as = lhs.getNext();
            if (as.isIdentityElement()) return true;
        }
        return false;
    }

    public boolean isZeroEquation(){
        AlgebraSymbolVector lhs = getLhs();
        for (lhs.reset();lhs.isNext();){
            AlgebraSymbol as = lhs.getNext();
            if (as.isZeroElement()) return true;
        }
        return false;
    }
    /** determines if the equation is of the form xx...x = x...x
    * @return boolean
    **/
    public boolean isRecursiveEquation(){
        AlgebraSymbolVector lhs = getLhs();
        AlgebraSymbolVector rhs = getRhs();
        return (lhs.sameElements() && rhs.sameElements() &&
                (lhs.getFirst().getValue().equals((rhs.getFirst().getValue()))));
     }

     /** determines the structural form of an equation
     * @return the equation type
     **/
	public int equationType(){
		AlgebraSymbolVector lhs = getLhs();
		AlgebraSymbolVector rhs = getRhs();
		if (lhs.size()==2 && rhs.size()==1){//prototype:BB=B
		//Debug.prout(0,);
			int i = sibDefEquation();
			if (i > -1)
		      return eqType = i;
		}
		if (lhs.size()==2 && rhs.size()==1){//prototype:PB=P
			//Debug.prout(0,);
			int i = paSibDefEquation();
			if (i > -1)
				return eqType = i;
		}
		if (lhs.size()==2 && rhs.size()==1){//prototype:PB=0
			//Debug.prout(0,);
			int i = paSibZeroDefEquation();
			if (i > -1)
				return eqType = i;
		}
		
		if (getLhs().size() == getRhs().size() && //prototype FF = MM
		getLhs().sameArrow() && getLhs().sameSex() &&
		getRhs().sameArrow() && getRhs().sameSex() &&
		((AlgebraSymbol) getLhs().elementAt(0)).getArrowType() ==
		((AlgebraSymbol) getRhs().elementAt(0)).getArrowType()){
			return EQUIVALENCE;
		}

		if (getLhs().size() == getRhs().size() && getLhs().size() == 2 && //prototype DS = SS
		getLhs().sameArrow() && !getLhs().sameSex() &&
		getRhs().sameArrow() && getRhs().sameSex() &&
		((AlgebraSymbol) getLhs().elementAt(0)).getSex() !=
		((AlgebraSymbol) getRhs().elementAt(0)).getSex() &&
		((AlgebraSymbol) getLhs().elementAt(0)).getArrowType() ==
		((AlgebraSymbol) getRhs().elementAt(0)).getArrowType() &&
		((AlgebraSymbol) getLhs().elementAt(0)).getArrowType() == Bops.DOWN){
			return EQUIVALENCE_A;
		}

		if (getLhs().size() == 2 && getRhs().size() == 1){//prototype Si = D
		   AlgebraSymbolVector fts = Algebra.getCurrent().getFocalElements();
			if (fts.indexOf(getLhs().getFirst()) > -1 &&
			getLhs().getLast().getArrowType() == getRhs().getFirst().getArrowType() &&
			getLhs().getLast().getSex() != getRhs().getFirst().getSex()){
				//Debug.prout(0," equivalence "+this +" a typel "+getLhs().getLast().getArrowType()+" atype r "+getRhs().getFirst().getArrowType());
				return EQUIVALENCE_B;
			}
		}
	//	Debug.prout(0,"THE EQUATION eq "+eq+" as "+as);

	    if (lhs.sameElements()&& rhs.sameElements() &&
	        lhs.getFirst().getValue().equals(rhs.getFirst().getValue())) return eqType= GENERATIONLIMIT;

	    if (rhs.sameElements() && lhs.getFirst().getValue().equals(rhs.getFirst().getValue())&&
	        !lhs.getLast().getValue().equals(rhs.getLast().getValue())){
	        if (lhs.getLast().getArrowType() == Bops.DOWN &&
	            lhs.getFirst().getArrowType() == Bops.UP) {
	            AlgebraSymbolVector asv = new AlgebraSymbolVector();
	            for (int i = 0;i < lhs.size()-1;i++){
	                asv.addElement(lhs.getSymbol(i));
	            }
	            if (asv.sameElements()) return eqType = CLASSIFICATORY;
	        }
	    }
	    if (lhs.size()==4 && rhs.size()==2){//prototype:CCPP=CP
	        if (lhs.getLast().getArrowType() == Bops.DOWN &&
	        rhs.getLast().getArrowType() == Bops.DOWN &&
	        lhs.getFirst().getArrowType() == Bops.UP &&
	        rhs.getFirst().getArrowType() == Bops.UP) {
	        if (lhs.getSymbol(1).getValue().equals(lhs.getFirst().getValue()) &&
	            lhs.getSymbol(2).getValue().equals(lhs.getLast().getValue()))
	            return eqType = SIBLING;
	        }
	    }
	    return -1;
	}

	int sibDefEquation(){
		if (lhs.getLast().getArrowType()==Bops.RIGHT &&
		rhs.getFirst().getArrowType() == Bops.RIGHT &&
		lhs.getFirst().getArrowType() == Bops.RIGHT) {
			return SIBDEF;
		} else if (lhs.getLast().getArrowType()==Bops.LEFT &&
		rhs.getFirst().getArrowType() == Bops.LEFT &&
		lhs.getFirst().getArrowType() == Bops.LEFT) {
			return SIBDEF;
		}
		return -1;
	}
	int paSibDefEquation(){
		if (lhs.getLast().getArrowType()==Bops.UP &&
			rhs.getFirst().getArrowType() == Bops.UP &&
			lhs.getFirst().getArrowType() == Bops.RIGHT) {
			return PASIBDEF;
		} else if (lhs.getLast().getArrowType()==Bops.UP &&
				   rhs.getFirst().getArrowType() == Bops.UP &&
				   lhs.getFirst().getArrowType() == Bops.LEFT) {
			return PASIBDEF;
		}
		return -1;
	}
	int paSibZeroDefEquation(){
		if (lhs.getLast().getArrowType()==Bops.UP &&
			rhs.getSymbol(0).getValue() == "0" &&
			lhs.getFirst().getArrowType() == Bops.RIGHT) {
			return PASIBZERODEF;
		} else if (lhs.getLast().getArrowType()==Bops.UP &&
				   rhs.getSymbol(0).getValue() == "0" &&
				   lhs.getFirst().getArrowType() == Bops.LEFT) {
			return PASIBZERODEF;
		}
		return -1;
	}
	
/*	Integer equationTypeOLD(){
	    AlgebraSymbolVector lhs = getLhs();
	    AlgebraSymbolVector rhs = getRhs();
	    if (lhs.size()==2 && rhs.size()==1){//prototype:BB=B
			Debug.prout(0,);
	        if (Algebra.getCurrent().isArrow(lhs.getLast(),Bops.RIGHT)&&
	        Algebra.getCurrent().isArrow(rhs.getFirst(),Bops.RIGHT)&&
	        Algebra.getCurrent().isArrow(lhs.getFirst(),Bops.RIGHT)) {
	            return new Integer(SIBDEF);
	        }
	    }
	    if (lhs.sameElements()&& rhs.sameElements() &&
	        lhs.getFirst().getValue().equals(rhs.getFirst().getValue())) return new Integer(GENERATIONLIMIT);
	    if (rhs.sameElements() && lhs.getFirst().getValue().equals(rhs.getFirst().getValue())&&
	        !lhs.getLast().getValue().equals(rhs.getLast().getValue())){
	        if (Algebra.getCurrent().isArrow(lhs.getLast(), Bops.DOWN) &&
	            Algebra.getCurrent().isArrow(lhs.getFirst(),Bops.UP)) {
	            AlgebraSymbolVector asv = new AlgebraSymbolVector();
	            for (int i = 0;i < lhs.size()-1;i++){
	                asv.addElement(lhs.getSymbol(i));
	            }
	            if (asv.sameElements()) return new Integer(CLASSIFICATORY);
	        }
	    }
	    if (lhs.size()==4 && rhs.size()==2){//prototype:CCPP=CP
	        if (Algebra.getCurrent().isArrow(lhs.getLast(),Bops.DOWN)&&
	        Algebra.getCurrent().isArrow(rhs.getLast(),Bops.DOWN)&&
	        Algebra.getCurrent().isArrow(lhs.getFirst(),Bops.UP) &&
	        Algebra.getCurrent().isArrow(rhs.getFirst(),Bops.UP)) {
	        if (lhs.getSymbol(1).getValue().equals(lhs.getFirst().getValue()) &&
	            lhs.getSymbol(2).getValue().equals(lhs.getLast().getValue()))
	            return new Integer(SIBLING);
	        }
	    }
	    return null;
	}
*/
	public static final int NONINVERTIBLE = 100;
	public static final int CLASSIFICATORY = 0;
	public static final int GENERATIONLIMIT = 1;
	public static final int SIBLING = 2;
	public static final int SIBDEF = 3;
	public static final int EQUIVALENCE = 4;
	public static final int EQUIVALENCE_A = 5;
	public static final int EQUIVALENCE_B = 6;
	public static final int PASIBDEF = 7;
	public static final int PASIBZERODEF = 8;
}
