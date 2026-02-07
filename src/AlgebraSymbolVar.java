/* Revisions History
* 7/5 DR added theSet = Algebra.getCurrent().getElements(); to setValue
* 7/24 DR added clone() procedure
*/

import java.lang.*;

public class AlgebraSymbolVar extends AlgebraSymbol {
	AlgebraSymbol theVar = null;
	boolean bind = false;
	
	AlgebraSymbolVector theSet = null; // range over
	

	public AlgebraSymbolVar(boolean s) {
		theSet = Algebra.getCurrent().getElements();
		if (s) Algebra.getCurrent().setUnBind(this);
	}
		
	public AlgebraSymbolVar() {
		theSet = Algebra.getCurrent().getElements();
		Algebra.getCurrent().setUnBind(this);
	}

		
	public AlgebraSymbolVar(AlgebraSymbol x) {
		theSet = Algebra.getCurrent().getElements();
		theVar = x;
		bind = true;
	}
		
	public AlgebraSymbolVar(String s) {
		AlgebraSymbol x = Algebra.getCurrent().getElement(s);
		theSet = Algebra.getCurrent().getElements();
		theVar = x;
		bind = true;
	}

    public Object clone() {
        AlgebraSymbolVar ret = new AlgebraSymbolVar();
        ret.theVar = theVar.makeClone();
        ret.theSet = (AlgebraSymbolVector) theSet.clone();
        ret.bind = bind;
        return (AlgebraSymbolVar) ret;
    }
    
	public void setSet(AlgebraSymbolVector s) {
		theSet = s;
	}
	// determine where to unBind()!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public void unBind() {
		if (bind) theVar = null;
		bind = false;
	}
	
	public void setValue(AlgebraSymbol s) { // throws Exception {
		theSet = Algebra.getCurrent().getElements();
		if (theSet.indexOf(s) != -1) {
			theVar = s;
			bind = true;
		} else {
			// throw new Exception();
		}
	}
	
	public String getValue() {
		if (bind) return theVar.getValue();
		else return ""; // need to make this more obvious ... unbound string perhaps
	}
	
	public String getValue(AlgebraSymbol s) {
		if (bind) return theVar.getValue();
		else setValue(s);
		return getValue();
	}
			
	public AlgebraSymbol getAlgebraSymbol() {
		if (bind) return theVar;
		else return this;
	}

	public boolean equals(Object x) {
		if (bind) {
			if (theVar.equals(x)) {
				return true;
			}
		} else if (theSet.indexOf(((AlgebraSymbol)x).getAlgebraSymbol()) != -1) {
			theVar = ((AlgebraSymbol)x).getAlgebraSymbol();
			return true;
		}
		return false;	
	}

}