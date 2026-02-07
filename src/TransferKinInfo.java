import java.util.*;
import java.awt.Point;

/** The basic common datatype describing kinship terms
*/
public class TransferKinInfo {
	String term;
	String sex;
	Point origin;
	int orientation;
	boolean isAGenerator=false;
	TransferProductsVector products;

	BooleanSwitch isCovered=new BooleanSwitch(false);
	String coveringTerm = null;
	StringVector coveredTerms = null;

	// boolean dropMerge=false;

	public TransferKinInfo() {
		term = "FOO";
		setSex("N");

		isAGenerator = false;
		orientation = -1;
		origin = new Point(0,0);
	}

	TransferKinInfo(String t,  String s, boolean isgen, int orient, Point org) {
		term = t;
		setSex(s);
		isAGenerator = isgen;
		orientation = orient;
		origin = org;
	}

	public void setSex(String sex) {
		this.sex = sex;
		if (theVariables != null) theVariables.setVarValue("Sex",sex);
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public void setGenerator(boolean generator) {
		this.isAGenerator = generator;
	}

	public boolean isGenerator() {
		return isAGenerator;
	}

	public String getSex() {
		// theVariables.getVarValue("Sex");
		return sex;
	}

	public String getTerm() {
		return term;
	}

	public String getEffectiveTerm() {
		if (isCovered.isTrue()) {
			if (coveringTerm != null) return coveringTerm;
			else return getTerm();
		} else return getTerm();
	}

	public int getOrientation() {
		return orientation;
	}

	public String toXML() {
		StringBuffer buf = new StringBuffer();

		buf.append(XMLIndent.space+"<Kinterm>"+XFile.Eol);
		XMLIndent.increment();
		buf.append(XMLIndent.space+"<Term>"+term+"</Term>"+XFile.Eol+
				XMLIndent.space+"<Sex>"+sex+"</Sex>"+XFile.Eol+
				XMLIndent.space+"<IsGen>"+isAGenerator+"</IsGen>"+XFile.Eol+
				XMLIndent.space+"<Orientation>"+orientation+"</Orientation>"+XFile.Eol+
				XMLIndent.space+"<Origin><x>"+origin.x+"</x><y>"+origin.y+"</y></Origin>"+XFile.Eol+
				XMLIndent.space+"<Covered>"+isCovered+"</Covered>"+XFile.Eol+
				XMLIndent.space+"<Etc>"+isEtc()+"</Etc>"+XFile.Eol);
	//	if (dropMerge)
	//	   buf.append(XMLIndent.space+"<DropMerge>"+dropMerge+"</DropMerge>"+XFile.Eol);
		if (coveringTerm == null)
			buf.append(XMLIndent.space+"<CoveringTerm>null</CoveringTerm>"+XFile.Eol);
		else
			buf.append(XMLIndent.space+"<CoveringTerm>"+coveringTerm+"</CoveringTerm>"+XFile.Eol);
		if (coveredTerms == null)
			buf.append(XMLIndent.space+"<CoveredTerms>null</CoveredTerms>"+XFile.Eol);
		else {
			buf.append(XMLIndent.space+"<CoveredTerms>"+XFile.Eol);
		   XMLIndent.increment();
			for(coveredTerms.reset();coveredTerms.isNext();) {
				buf.append(XMLIndent.space+"<CoveredTerm>"+coveredTerms.getNext()+"</CoveredTerm>"+XFile.Eol);
			}
			XMLIndent.decrement();
			buf.append(XMLIndent.space+"</CoveredTerms>"+XFile.Eol);
		}
		buf.append(theVariables.toXML());
		buf.append(products.toXML());
		XMLIndent.decrement();
		buf.append(XMLIndent.space+"</Kinterm>"+XFile.Eol);
		return buf.toString();
	}

	void addProduct(Vector p) {
		products.addElement(p);
	}

	public synchronized Object clone() {
		TransferKinInfo tf = new TransferKinInfo(term,sex,isAGenerator,orientation,origin);
		tf.products = products;
		tf.setEtc(isEtc());
		tf.setTheVariables((Variables) theVariables.clone());
	//	tf.dropMerge = dropMerge; // would we want to keep this bit of history???
		return tf;
	}

	public synchronized Object clone(boolean deep) {
		if (deep) {
			TransferKinInfo t=null;
			try {
				t = (TransferKinInfo) this.clone();
			} catch (Exception e) {
				Debug.prout(4,"Couldn't clone in TransferKinInfo");
				return null;
			}
			t.origin = new Point(origin);
			t.products = (TransferProductsVector) products.clone(deep);
			if (coveredTerms != null) t.coveredTerms = (StringVector) coveredTerms.clone();
			t.isCovered = (BooleanSwitch) isCovered.replace();//.clone();
			t.coveringTerm = coveringTerm;
			t.setEtc(isEtc());
		//	t.dropMerge = dropMerge; // would we want to keep this bit of history???

			return t;
		} else 	try {
				return this.clone();
			} catch (Exception e) {
				Debug.prout(4,"Couldn't clone in TransferKinInfo part 2");
				return null;
			}

	}

	public boolean equals(Object o) {
		return o.toString().equals(this.toString());
	}

	public String toString() {
		return term + " " + sex + " " + isAGenerator+" "+isCovered.toString();
	}

	public TransferProductsVector getProducts() {
		return products;
	}

	public TransferKinInfo fUnion(TransferKinInfo union) {
		TransferProductsVector u = union.getProducts();
		TransferProductsVector k = this.getProducts();
		for(u.reset(),k.reset(); u.isNext() && k.isNext();) {
			TransferProduct up = u.getNext();
			TransferProduct kp = k.getNext();
			kp.fUnion(up);
		}
		return this;
	}

	public TransferKinInfo fDifference(TransferKinInfo union) {
		TransferProductsVector u = union.getProducts();
		TransferProductsVector k = this.getProducts();
		for(u.reset(),k.reset(); u.isNext() && k.isNext();) {
			TransferProduct up = u.getNext();
			TransferProduct kp = k.getNext();
			kp.fDifference(up);
		}
		return this;
	}

	public TransferKinInfo fIntersection(TransferKinInfo intersect) {
		TransferProductsVector u = intersect.getProducts();
		TransferProductsVector k = this.getProducts();
		for(u.reset(),k.reset(); u.isNext() && k.isNext();) {
			TransferProduct up = u.getNext();
			TransferProduct kp = k.getNext();
			kp.fIntersection(up);
		}
		return this;
	}

	public TransferKinInfo fMerge(TransferKinInfo unity,Vector names) {
		TransferProductsVector u = unity.getProducts();
		TransferProductsVector k = this.getProducts();
		for(u.reset(),k.reset(); u.isNext() && k.isNext();) {
			TransferProduct up = u.getNext();
			TransferProduct kp = k.getNext();
			kp.fMerge(up, names);
		}
		return this;
	}
	public TransferKinInfo fRemove(Vector names) {
		TransferProductsVector k = this.getProducts();
		for(k.reset(); k.isNext();) {
			TransferProduct kp = k.getNext();
			kp.fRemove(names);
		}
		return this;
	}


	public void setEtc(boolean etc) {
		this.etc = etc;


	}

	public boolean isEtc() {
		return etc;
	}
	protected boolean etc;

	public void setTheVariables(Variables theVariables) {
		this.theVariables = theVariables;
	}

	public Variables getTheVariables() {
		return theVariables;
	}

	public StringVector getCoveredTerms() {
	    return coveredTerms;
	}

	protected Variables theVariables;

	public boolean outArrowsIdentical(TransferKinInfo tk1) {
	   // test to see if the pattern of 'out' arrows match
	   if (! getProducts().equivalentTo(tk1.getProducts())) {
		  //System.out.println("outArrowsIdentical: arrows out for "+getTerm()+" not equivalent to "+tk1.getTerm());

		 // if (getTerm().equals("Mother"))System.out.println(" motehr  "+getProducts()+" tk1 "+tk1.getProducts());
		  return false;
	   }
	   return true;
	}

	final static int UP = KintermEditObject.UP;
	final static int DOWN = KintermEditObject.DOWN;
	final static int RIGHT = KintermEditObject.RIGHT;
	final static int LEFT = KintermEditObject.LEFT;
	final static int SPOUSE = KintermEditObject.SPOUSE;
	final static int SPOUSER = KintermEditObject.SPOUSER;
	final static int SEXGEN = KintermEditObject.SEXGEN;

	
}


