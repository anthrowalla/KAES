public class SexRule extends Rule {

	public SexRule(){
		setRuleText("American Kinship Terminology Sex Marking");
	}

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

	public boolean doesRuleApply(AlgebraPath a) {
		if (!activeRule) return false;
		if (a.getReducedProductPath().size() < 2) return false;
		AlgebraSymbol x = a.getReducedProductPath().getLast();
		boolean test=(Algebra.getCurrent().isArrow(x,Bops.MALEFEMALE));
//		System.out.println("In SexRule.doesRuleApply test ="+test+ " path= "+a);
		return test;
		//return (Algebra.getCurrent().isArrow(x,Bops.MALEFEMALE));
	}

	public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap = (AlgebraPath)a.clone();
		AlgebraSymbolVector sp = Algebra.getCurrent().getSpouseArrows();

		for (int i = 0;i< sp.size();i++) {
			AlgebraSymbol as = sp.getSymbol(i);
			AlgebraPath p = (AlgebraPath) a.clone();
			if (!p.product(as)) {
				AlgebraSymbolVector e = Algebra.getCurrent().theKludge.reciprocal(a.getReducedProductPath());
				AlgebraPath pr = new AlgebraPath(e,true);
				if (pr.product(as)) {
					return false;
				}
			} else {

				return false;
			}

		}
		//if slightest problem put in new path;
		if (a.getReducedProductPath().size() > 1) {
			a.getProductPath().removeEnd();
			a.getReducedProductPath().removeEnd();
		} else if(a.getProductPath().size() > 1){
		    a.getProductPath().removeEnd();
		}
		//return true;
		return (!ap.equals(a));
	}

}
