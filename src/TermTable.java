import java.util.*;

public class TermTable {
	Hashtable terms2paths = new Hashtable(30,5);
	Hashtable paths2terms = new Hashtable(50,5);
	Vector terms = new Vector(30,5);
	
	String theTerm;
	
	public TermTable() {
		
	}
	
	public void defTermPath(String term, AlgebraSymbolVector path) {
		Vector v;
		
		if (terms.indexOf(term) < 0) {
			terms.addElement(term);
			terms2paths.put(term, (v = new Vector(5,2))); // Vector of AlgebraSymbolVectors
		} else  v = (Vector) terms2paths.get(term);
		
		if (v == null) {
			Debug.prout("in TermTable.defTerm: null path returned for term "+term);
			
		} else {
			v.addElement(path);
		//	terms2paths.put(term,v);
		}
		paths2terms.put(path.toString(),term);
	}
	
	public String getTerm(String path) {
		return (String) paths2terms.get(path);
	}
	
	public String getTerm(AlgebraSymbolVector path) {
		return getTerm(path.toString());
	}

	public Vector getPaths(String term) {
		return (Vector) terms2paths.get(term);
	}
}