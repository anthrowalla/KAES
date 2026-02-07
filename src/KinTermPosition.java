import java.util.*;

public class KinTermPosition {
	static Hashtable allTerms = new Hashtable(10,2);
	
	Vector kinTerms = new Vector(2,1);
	Vector toLinks=new Vector(2,1);
	Vector fromLinks=new Vector(2,1);
	static Vector genList = new Vector(3,1);
	
	public KinTermPosition(String term, String sex) {
		KinTerm x = new KinTerm(term, sex);
		kinTerms.addElement(x);
		allTerms.put(term,this);
	}
		
	public void addTerm(String term, String sex) {
		KinTerm x = new KinTerm(term,sex);
		kinTerms.addElement(x);
		allTerms.put(term,this);
	}

	public KinTermPosition findTerm(String term) {
		return (KinTermPosition) allTerms.get(term);
	}
	
	public void linkTo(KinTermPosition term, String gen) {
		if (term == null) {
			Debug.prout("in KinTermPosition.linkTo ... null term");
			return;
		}
		int i = genList.indexOf(gen);
		if (i == -1)  {
			addGenerator(gen);
		}
		if (toLinks.size() < i+1) {
			toLinks.setSize(i+1);
			toLinks.setElementAt(new Vector(),i);
		}
		Vector c = (Vector) toLinks.elementAt(i);
		if (c.indexOf(term) == -1) {
			c.addElement(term);
			term.linkFrom(this,gen);
		}
	}

	public void linkTo(String term, String gen) {
		KinTermPosition a = (KinTermPosition) allTerms.get(term);
		if (a == null) {
			Debug.prout("Couldn't find a kintermpos for "+term);
			return;
		}
		linkTo(a,gen);
	}

	public void linkFrom(KinTermPosition term, String gen) {
		if (term == null) {
			Debug.prout("in KinTermPosition.linkTo ... null term");
			return;
		}
		int i = genList.indexOf(gen);
		if (i == -1)  {
			addGenerator(gen);
		}
		if (fromLinks.size() < i+1) {
			fromLinks.setSize(i+1);
			fromLinks.setElementAt(new Vector(),i);
		}
		Vector c = (Vector) fromLinks.elementAt(i);
		if (c.indexOf(term) == -1) {
			c.addElement(term);
			term.linkFrom(this,gen);
		}
	}

	public void linkFrom(String term, String gen) {
		KinTermPosition a = (KinTermPosition) allTerms.get(term);
		if (a == null) {
			Debug.prout("Couldn't find a kintermpos for "+term);
			return;
		}
		linkFrom(a,gen);
	}
	
	static public void addGenerator(String gen) {
		if (genList.indexOf(gen) == -1) {
			genList.addElement(gen);
		}
	}
	
	static public void addGenerators(String gens[]) {
		for (int i = 0; i < gens.length;i++) {
			if (gens[i] != null) addGenerator(gens[i]);
		}
	}
	
/*	static public void addKinTerms(String [][] kterms) {
		for (int i=0;i<kterms.length;i++) {
			String [] line = kterms[i];
			line[0]
		}
	}
*/	
	public Vector trace(String gen) {
		int i = genList.indexOf(gen);
		if (i != -1)  {
			Vector c = (Vector) toLinks.elementAt(i);
			if (c.size() == 0) return null;
			return c;
		}
		else return null;
	}
	
	public String tracePathToTerm(AlgebraSymbolVector path) {
		AlgebraSymbolVector xpath = path.copy();
		xpath.addToBeginning(new AlgebraSymbol());
		// vector corresponding to focal term ... this should be it
		// if path 1 long call yyy directly or more generally just prepend focal element to path
		return yyy(this,xpath);
	}
	
	String xxx(Vector v,  AlgebraSymbolVector path) {
		String term;
		for(int i=0;i<v.size();i++) {
			KinTermPosition a = (KinTermPosition) v.elementAt(i);
			if ((term = yyy(a,path.copy())) != null) return term;
		}
		return null;
	}
	
	String yyy(KinTermPosition a, AlgebraSymbolVector path) {
		AlgebraSymbol k = path.getFirst();
		path.removeBeginning();
		if (path.isEmpty()) {
			String sex = k.getSex();
			for(int i=0;i<kinTerms.size();i++) {
				if (((KinTerm) kinTerms.elementAt(i)).getSex().equals(sex)) 
					return ((KinTerm) kinTerms.elementAt(i)).getTerm();
			}
			return null;
		}
		Vector v = trace(path.getFirst().getValue());
		if (v == null) {
			AlgebraSymbol derivedGenNorm = Algebra.getCurrent().theKludge.getNorm(path.getFirst());
  			v = trace(derivedGenNorm.getValue());
  			if (v != null) return xxx(v,path);
			else return null;
		} else {
			return xxx(v,path);
		}
	}
}