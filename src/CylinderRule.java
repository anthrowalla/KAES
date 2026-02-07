public class CylinderRule extends Rule {

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

    /** rule links ChildChild...Child term with equation CC...C = C...C
	 *  to ParentParent...Parent term
    * @a AlgebraPath the path to be tested for applicability of rule
    * return boolean
    */

	AlgebraPath thePath = null;
    public boolean doesRuleApply(AlgebraPath a) {
		//System.out.println("DOES CYLINDEDR RULE APPLY "+a);
		if (!activeRule) return false;
		AlgebraSymbolVector asv = a.getReducedProductPath();
		int aType = 0;
		if (thePath != null) aType = thePath.getReducedProductPath().getFirst().getArrowType();
		//if (a.isEquivalentPath()) asv = a.getEquivalentPathLeft().getProductPath();//dwr 8/5 no change
		if (a.isReducedEquivalentPath()) asv = a.getReducedEquivalentPathLeft().getProductPath();//dwr 8/6
		//System.out.println(" a "+a+" asv "+asv);
		if (!asv.sameArrow() || !asv.sameSex()) return false;
		if (thePath != null && asv.getLast().getArrowType() == aType) return false;
		//if (asv.reciprocal().size() != 1) return false;
//System.out.println("BBBBBBBBBBBBBB a "+a +" asv "+asv+" rec "+asv.reciprocal()+" thepath "+thePath);
		EquationVector eqtns = Algebra.getCurrent().getEquations();
		AlgebraSymbolVector asL = null;
		for (eqtns.reset1();eqtns.isNext();){
		    Equation eq = eqtns.getNext();
			asL = eq.getLhs();
			//System.out.println(" asL "+asL+ " rhs "+eq.getRhs());
			if (!(asL.size() > 1) || !asL.sameArrow() || !asL.sameSex() ||
			!eq.getRhs().sameArrow() || !eq.getRhs().sameSex() ||
			asL.size() != (eq.getRhs().size()+1) ||
			asL.getFirst().getArrowType() != eq.getRhs().getFirst().getArrowType() ||
			(asL.getFirst().getArrowType() != Bops.DOWN && asL.getFirst().getArrowType() != Bops.UP) ||
			eq.getRhs().size() != asv.size()) {
				asL = null;
				continue;
			}
			if (thePath != null && asL.getFirst().getArrowType() == aType) {
				asL = null;
				continue;
			}
			break;
		}
		if (asL != null){
			//asv = a.getReducedProductPath();
			if (asv.getFirst().equals(asL.getFirst()) ||
			asv.getLast().equals(asL.getFirst())) {
				if (thePath != null) return true;
				thePath = a;
				//System.out.println(" thePath "+thePath);
			}
		}
       return false;
    }

    /** rule modifies CayleyTable so that ChildChild---Child is replaced by
    * ParentParent...Parent
    * @a path to which the rule is to be applied
    * return boolean
    */
    public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap = (AlgebraPath)a.clone1();
		//System.out.println("in cylinder rule a "+a+" apth "+a.path+" red "+a.reducedPath+" ap "+ap);
		//AlgebraSymbolVector asv = a.getEquivalentPathLeft().getProductPath();//dwr 8/5
		AlgebraSymbolVector asv = a.getReducedEquivalentPathLeft().getProductPath();//dwr 8/5
		if (thePath.getReducedProductPath().getFirst().getArrowType() == Bops.DOWN){
		    thePath.setReducedProductPath(a.getReducedProductPath());
		} else a.setReducedProductPath(thePath.getReducedProductPath());
		//System.out.println(" a "+a+" red "+a.getReducedProductPath()+" ap "+ap+" EQUALS "+ap.equals(a));

		//AlgebraSymbolVector asv = a.getReducedProductPath().equivalentLeftProduct();
		//a.setReducedProductPath((AlgebraSymbolVector)asv.reciprocal().elementAt(0));
       // return true;
	   return (!ap.equals(a));
    }
}

