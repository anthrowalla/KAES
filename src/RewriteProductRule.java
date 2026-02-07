

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @Dwight Read
 * @version 1.0
 */

public class RewriteProductRule extends Rule{

    public RewriteProductRule() {
		setRuleText("Reset Products");
    }

	ListVector prods = new ListVector();
	//ListVector ruleData = new ListVector();

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

	public void setEquivalentProducts(AlgebraSymbolVector as1, AlgebraSymbolVector as2) {
		//System.out.println(" SSSSSSSSSSSSSSSSSSSS as1 "+as1+" as2 "+as2);
	   ListVector lv = new ListVector();
		lv.addElement(as1); lv.addElement(as2);
		if (prods.indexOf(lv) == -1)
		    prods.addElement(lv);
	}

	public ListVector getEquivalentProducts() {
	    return prods;
	}

	boolean inProducts(AlgebraSymbol gen,AlgebraSymbolVector asv0){
		if (asv0.size() == 0) return false;
		AlgebraSymbolVector asv = (AlgebraSymbolVector) asv0.clone();
		asv.addToEnd(gen);
		for (prods.reset();prods.isNext();){
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) ((ListVector) prods.getNext()).elementAt(0);
				//System.out.println("asv "+asv +" asv1 "+ asv1);
			if (asv.equals(asv1)) return true;
		}
		return false;
	}


    /* rule applies to products: oldermalesib I female, youngermalesib i female
    * olderfemalesib i male, youngerfemalesib i male
    * @a CayleyInfo the path to be tested for applicability of rule
    * return boolean
    */
    public boolean doesRuleApply(CayleyInfo a) {
		if (!activeRule) return false;
		if (ruleDataIndex(a) > -1) return true;
		AlgebraPath ap = a.term;
		//System.out.println("IN Does OlderYounger RULE cinfo "+ a);
		AlgebraSymbolVector asv0 = null;
		if (ap.isEquivalentPath() || ap.isReducedEquivalentPath()){
			//asv0 = ap.getReducedEquivalentPathRight().getProductPath();
			//asv0 = ap.getEquivalentPathRight().getProductPath();
			asv0 = ap.getReducedProductPath().equivalentRightProduct();
		//System.out.println("here is asv0 "+asv0+" right1 "+ap.getReducedEquivalentPathRight().getProductPath());
			if (!inProducts(a.generator,asv0)) return false;
			//asv0 = ap.getEquivalentPathLeft().getReducedProductPath();
			//asv0 = ap.getEquivalentPathLeft().getProductPath();
			asv0 = ap.getReducedProductPath().equivalentLeftProduct();
		//System.out.println("here is asv0 "+asv0+" left "+ap.getEquivalentPathLeft().getProductPath());
			return inProducts(a.generator,asv0);
		}else {
			asv0 = a.term.getReducedProductPath();
				//AlgebraSymbolVector asv = a.product.getProductPath();
				//System.out.println(" ALGEBRA PATH2 a "+a+" asv0 "+asv0+" red "+a.product.getReducedProductPath());
		    return inProducts(a.generator,asv0);
		}
    }

    /* rule applies if x equivalent to y and a = x or a = y
    * sex and generation
    * @a CayleyInfo the path to be tested for applicability of rule
    * return boolean
    */
    public boolean doesRuleApply(AlgebraPath a) {
		if (!activeRule) return false;
		//System.out.println("IN Does OlderYounger RULE path "+ a.getProductPath());
		AlgebraSymbolVector asv = a.getProductPath();
		if (asv.size() == 0) {return false;}
		//System.out.println(" ALGEBRA PATH a "+a +" asv "+asv+" asv red "+a.getReducedProductPath());
		for (prods.reset();prods.isNext();){
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) ((ListVector) prods.getNext()).elementAt(0);
				//System.out.println(" 2 asv "+asv +" asv1 "+ asv1);
			if (asv.equals(asv1)) return true;
		}
		String asvS = "";
		String asv1S = "";
		for (prods.reset();prods.isNext();){
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) ((ListVector) prods.getNext()).elementAt(0);
			asvS = asv.toString();asvS = asvS.substring(1,asvS.length()-1);
			asv1S = asv1.toString();asv1S = asv1S.substring(1,asv1S.length()-1);
			    //System.out.println(" AAAAAAAAAAAA asv "+asvS+" asv1 "+asv1S);
		    if (asvS.indexOf(asv1S) > -1){
			   // System.out.println(" AAAAAAAAAAAA asv "+asvS+" asv1 "+asv1S);
				return true;
		    }
		}
		return false;
    }

	public boolean applyRule(CayleyInfo a) {
		//System.out.println("IN OlderYounger RULE2 "+ a);
		String aString = a.toString();
		AlgebraPath ap0 = a.term;
		int k = ruleDataIndex(a);
		if (k > -1){
			a.product.reducedPath = getRuleDataReducedPath(k);
			return (!aString.equals(a.toString()));
		}
		ListVector data = new ListVector();
		data.addElement(a);
		AlgebraSymbolVector asv0 = null;
		if (ap0.isEquivalentPath() || ap0.isReducedEquivalentPath()){
			//asv0 = ap0.getEquivalentPathRight().getReducedProductPath();
			asv0 = ap0.getReducedProductPath().equivalentRightProduct();
			Debug.prout(0," asv0 "+asv0);
			//if (asv0.size() < ap0.getEquivalentPathLeft().getReducedProductPath().size())
				//asv0 = ap0.getEquivalentPathLeft().getReducedProductPath();
		}else asv0 = a.term.getReducedProductPath();
		AlgebraSymbolVector asv = (AlgebraSymbolVector) asv0.clone();
		asv.addToEnd(a.generator);


		//AlgebraSymbolVector asv = a.product.getProductPath();
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		for (prods.reset();prods.isNext();){
			ListVector lv =(ListVector) prods.getNext();
			//System.out.println(" list vector lv "+lv);
			AlgebraSymbolVector asv2 = (AlgebraSymbolVector) lv.elementAt(0);
			asv1 = (AlgebraSymbolVector) lv.elementAt(1);
			if (asv.equals(asv2)) break;
				//System.out.println("asv "+asv +" lhs "+ eq.getLhs());
		}
		//AlgebraPath ap = new AlgebraPath(asv1,true);
		AlgebraPath ap = new AlgebraPath(asv1,false);
		a.product.reducedPath = ap.getReducedProductPath();
				//System.out.println("xxxxxxxxxxxxxxxxxxxxxx8 ap is "+ap+" a "+a+" asv1 "+asv1+" red "+a.product.reducedPath);
		//return true;
		data.addElement(a.product.reducedPath);
		if (ruleData.indexOf(data) == -1) ruleData.addElement(data);
	    return (!aString.equals(a.toString()));
	}

	public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap1 = (AlgebraPath)a.clone();
		//System.out.println("IN OlderYounger RULE1 "+ a);
		AlgebraSymbolVector asv = a.getProductPath();
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		for (prods.reset();prods.isNext();){
			ListVector lv =(ListVector) prods.getNext();
			//System.out.println(" list vector lv "+lv);
			AlgebraSymbolVector asv0 = (AlgebraSymbolVector) lv.elementAt(0);
			asv1 = (AlgebraSymbolVector) lv.elementAt(1);
			if (asv.equals(asv0)) {
				AlgebraPath ap = new AlgebraPath(asv1,true);
				a.reducedPath = ap.getReducedProductPath();
				return (!ap1.equals(a));
			}
				//System.out.println("asv "+asv +" lhs "+ eq.getLhs());
		}
		int i = -1;
		AlgebraSymbolVector asv0 = null;
		String asvS = "";
		String asv0S = "";
		for (prods.reset();prods.isNext();){
			ListVector lv =(ListVector) prods.getNext();
			//System.out.println(" list vector lv "+lv);
			asv0 = (AlgebraSymbolVector) lv.elementAt(0);
			asv1 = (AlgebraSymbolVector) lv.elementAt(1);
			asvS = asv.toString();asvS = asvS.substring(1,asvS.length()-1);
			asv0S = asv0.toString();asv0S = asv0S.substring(1,asv0S.length()-1);
			i = asvS.indexOf(asv0S);i=i-i/2;
			if (i > -1) break;
				//System.out.println("asv "+asv +" lhs "+ eq.getLhs());
		}
		AlgebraSymbolVector asv2 = new AlgebraSymbolVector();
		for (int j=i+asv0.size()+1;j<asv.size()+1;j++) asv2.addToBeginning((AlgebraSymbol)asv.lastElement());
		for (asv1.reset();asv1.isNext();) asv2.addToEnd((AlgebraSymbol)asv1.getNext());
		for (int j=0;j<i;j++) asv2.addToBeginning((AlgebraSymbol)asv.lastElement());
		AlgebraPath ap = new AlgebraPath(asv2,true);
		a.reducedPath = ap.getReducedProductPath();
				//System.out.println("xxxxxxxxxxxxxxxxxxxxxx9 ap is "+ap+" a "+a+" red "+a.reducedPath+" asv2 "+asv2);
		//return true;
		return (!ap1.equals(a));
	}

/*	int prodDataIndex(CayleyInfo a){
		int i = -1;
	    for (ruleData.reset();ruleData.isNext();){
			i++;
		    ListVector lv = (ListVector) ruleData.getNext();
			if (((CayleyInfo)lv.elementAt(0)).equals(a)) return i;
	    }
		return -1;
	}*/

/*	AlgebraSymbolVector getProdDataReducedPath(int i){
		return (AlgebraSymbolVector) ((ListVector) ruleData.elementAt(i)).elementAt(1);
	}*/

}
