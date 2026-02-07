public class SpouseSiblingRule extends Rule {

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

    /** rule applies if reduced path is spouse of sibling
    * @a path to which the rule is to be applied
    * return boolean
    */

	public boolean doesRuleApply(AlgebraPath a) {if (1==1) return false;
		if (!activeRule) return false;
		if (a.getReducedProductPath().equivalentProduct()) return false;
		if (a.getReducedProductPath().toString().equals("[0]")) return false;
		if (a.getReducedProductPath().getFirst().isFocalElement()) return false;
		AlgebraSymbolVector asv = a.getReducedProductPath();
		if (asv.size() > 1){
			if (((AlgebraSymbol)asv.getSymbol(1)).getArrowType() == Bops.SPOUSE
		    && (((AlgebraSymbol)asv.getFirst()).getArrowType() == Bops.LEFT ||
		    ((AlgebraSymbol)asv.getFirst()).getArrowType() == Bops.RIGHT)) {//WB=B;HZ=Z
				String sex = ((AlgebraSymbol)asv.getFirst()).getSex();
				if (!((AlgebraSymbol)asv.getSymbol(1)).getSex().equals(sex)) return true;
				else return false;
		    }
			else if (((AlgebraSymbol)asv.getFirst()).getArrowType() == Bops.SPOUSE
			 && (((AlgebraSymbol)asv.getSymbol(1)).getArrowType() == Bops.LEFT ||
			 ((AlgebraSymbol)asv.getSymbol(1)).getArrowType() == Bops.RIGHT)) {//BH=B;ZW=Z
				String sex = ((AlgebraSymbol)asv.getFirst()).getSex();
				if (((AlgebraSymbol)asv.getSymbol(1)).getSex().equals(sex)) return true;
				else return false;
			}
		/*	else if (((AlgebraSymbol)asv.getFirst()).getArrowType() == Bops.IDENTITY &&
			((AlgebraSymbol)asv.getSymbol(1)).getArrowType() == Bops.SPOUSE){
				String sex = ((AlgebraSymbol)asv.getFirst()).getSex();
				if (!((AlgebraSymbol)asv.getSymbol(1)).getSex().equals(sex)) return true;
				else return false;
			}*/
		}
		return false; 
    }

    /** rule modifies CayleyTable so that spouse of sibling is sibling
    * @a path to which the rule is to be applied
    * return boolean
    */
    public boolean applyRule(AlgebraPath a) {
	//	AlgebraPath ap = a.clone();
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		AlgebraSymbolVector asv = a.getReducedProductPath();
		for (asv.reset();asv.isNext();){
		    AlgebraSymbol as = asv.getNext();
			if (as.getArrowType() == Bops.SPOUSE) continue;
			asv1.addToEnd(as);
		}
		System.out.println("a "+a +" a reduced "+a.getReducedProductPath());
		a.setReducedProductPath(asv1.reduce());
		System.out.println("a after "+a +" a reduced "+a.getReducedProductPath());
       // return true;
	   return (!a.equals(a));
    }

}
