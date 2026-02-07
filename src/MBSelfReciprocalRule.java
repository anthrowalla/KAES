public class MBSelfReciprocalRule extends Rule {

	public  MBSelfReciprocalRule(){
		setRuleText("Make Elements Self-Reciprocal");
	}

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

    /** rule applies if reduced path is "Child" and reciprocal of path is
    * not "Parent" and is not a focal element and is not "0"
    * @a path to which the rule is to be applied
    * return boolean
    */

	public boolean doesRuleApply(AlgebraPath a) {
		if (!activeRule) return false;
		if (a.getProductPath().equivalentProduct()) return false;
		if (a.getReducedProductPath().toString().equals("[0]")) return false;
		if (!a.getProductPath().getFirst().isFocalElement()) return false;
		AlgebraSymbolVector asv = a.getReducedProductPath();
		//if (a.isEquivalentPath()) asv = a.getReducedProductPath().equivalentLeftProduct(); dwr 8/5
		if (a.isReducedEquivalentPath()) asv = a.getReducedProductPath().equivalentLeftProduct();//dwr 8/5
		if (asv.size() == 1 && ((AlgebraSymbol)asv.elementAt(0)).getArrowType() == Bops.DOWN) {
			ListVector lv1 = ((AlgebraSymbolVector) a.getProductPath().clone()).reciprocal();
			if (lv1 == null) return false;
			if (lv1.size() != 1) return false;
			AlgebraSymbolVector av = (AlgebraSymbolVector)lv1.elementAt(0);
		    CrowSkewingRule r = getCrowSkewingRule();
		    AlgebraSymbolVector asv0 = null;
			AlgebraPath p = new AlgebraPath(av,true);
		    if (r != null && r.doesRuleApply(p)){
			    if (r.applyRule(p)){
				    asv0 = p.getReducedProductPath();
					if (asv0.sameArrow()) return false;
//System.out.println("crow rule asv0 "+asv0);
			    }
				else asv0 = av.reduce();
		    } else asv0 = av.reduce();

			if (asv0.toString().equals("[0]")) return false;
		//System.out.println(" XXXXX a "+p.path.toStringVector()+" asv0 "+asv0);
			if (asv0.size() != 1) return true;
			if (((AlgebraSymbol) asv0.elementAt(0)).isFocalElement()) return false;
		//System.out.println(" XXXXX1 a "+a.path.toStringVector()+" asv0 "+asv0);
			if (((AlgebraSymbol)asv0.elementAt(0)).getArrowType() != Bops.UP) return true;
			else return false;
		}
		return false;
    }

    /** rule modifies CayleyTable so that element is now self-reciprocal
    * @a path to which the rule is to be applied
    * return boolean
    */
    public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap = (AlgebraPath)a.clone();
		System.out.println("In MBSelfReciprocalRule");
		ListVector lv = ((AlgebraSymbolVector) a.getProductPath().clone()).reciprocal();
		AlgebraSymbolVector av = (AlgebraSymbolVector)lv.elementAt(0);
		CrowSkewingRule r = getCrowSkewingRule();
		AlgebraSymbolVector asv = null;
		AlgebraPath p = new AlgebraPath(av,true);
		if (r != null && r.doesRuleApply(p)){
			if (r.applyRule(p)){
				asv = p.getReducedProductPath();
//System.out.println("crow rule asv0 "+asv0);
			}
			else asv = av.reduce();
		} else asv = av.reduce();
		//AlgebraSymbolVector asv = ((AlgebraSymbolVector)lv.elementAt(0)).reduce();
		System.out.println(" a "+a+" asv "+asv);
		a.setReducedProductPath(asv);
       // return true;
	   return (!ap.equals(a));
    }

	CrowSkewingRule getCrowSkewingRule(){
		RuleVector rv = Algebra.getCurrent().getRules();
		CrowSkewingRule r1 = null;
		for (int i=0;i<rv.size();i++){
			Rule r = (Rule) rv.elementAt(i);
			if (r instanceof CrowSkewingRule) {
				r1 = (CrowSkewingRule) r;
				break;
			}
		}
	return r1;
	}
}