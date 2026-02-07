public class CousinRule extends Rule {

	public CousinRule(){
		setRuleText("Ith Cousin, J Times Removed");
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
		AlgebraSymbolVector ups = Algebra.getCurrent().getUpArrows();
		AlgebraSymbolVector dns = Algebra.getCurrent().getDownArrows();
//		System.out.println("In CousinRule.doesRuleApply ups = " + ups + " dns= "+dns);
		AlgebraSymbol x = null;
		AlgebraSymbolVector aq = a.getReducedProductPath();
		int i = 0;
		int j = 0;
		for (aq.reset();aq.isNext();) {
			x = aq.getNext();
			if ((i == 0) && (ups.indexOf(x) != -1)) {
				j++;
			}
			else if ((j > 0) && (dns.indexOf(x) != -1)) {
				i++;
			}
			else break;
		}

		return (i > 1 && j > 1 &&  j < i);
	}

	public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap = (AlgebraPath)a.clone();
		AlgebraSymbolVector e = Algebra.getCurrent().theKludge.reciprocal(a.getProductPath());
		AlgebraPath pr = new AlgebraPath(e,true);
		// suspect if trouble!
		a.path = pr.path;
		a.reducedPath = pr.reducedPath;
		//return true;
		return (!ap.equals(a));
	}

}