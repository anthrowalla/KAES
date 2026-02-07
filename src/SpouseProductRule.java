public class SpouseProductRule extends Rule {

	public SpouseProductRule(){
		setRuleText("Make 'Spouse' of 'Sibling'");
	}

	ListVector cForm = new ListVector();
	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

    /** rule applies if reduced path contains spouse element
    * @a path to which the rule is to be applied
    * return boolean
    */

	public boolean doesRuleApply(AlgebraPath a) {
		if (!activeRule) return false;
		AlgebraSymbolVector asv = a.getReducedProductPath();
		if (asv.equivalentProduct()) return false;
		if (asv.toString().equals("[0]")) return false;
		//if (asv.getFirst().isFocalElement()) return false;
		if (asv.size() == 1) return false;
		for (asv.reset();asv.isNext();){
			if (asv.getNext().getArrowType() == Bops.SPOUSE) return true;
		}
		return false;
    }

    /** rule modifies CayleyTable according to marriage structure
    * @a path to which the rule is to be applied
    * return boolean
    */
    public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap = (AlgebraPath)a.clone();
		String test = a.getReducedProductPath().toString();
		AlgebraSymbolVector asv = a.getReducedProductPath();
		int index = determineSpouseProductPattern(asv);
		if (index == -1) {System.out.println("In SpouseProductRule: asv "+ asv); return false;}

		AlgebraSymbolVector asv1 = (AlgebraSymbolVector)((AlgebraSymbolVector)cForm.elementAt(index)).clone();
		for (int j=3;j<asv.size();j++){//add remaining symbols
			asv1.addToEnd(asv.getSymbol(j));
		}
		asv1 = asv1.reduce();
		a.setReducedProductPath(asv1);
		if (!test.equals(a.getReducedProductPath().toString()) && doesRuleApply(a)) applyRule(a);
		//a.setReducedProductPath((AlgebraSymbolVector)cForm.elementAt(index));
		if (!doesRuleApply(a)) {
			RuleVector rules = Algebra.getCurrent().getRules();
			for (rules.reset();rules.isNext();){
				Rule rule = (Rule) rules.getNext();
				if (rule.equals(this)) continue;
				if (rule.doesRuleApply(a)) rule.applyRule(a);
			}
        }

		//System.out.println(" in Apply: reduce a "+a+" asv  "+asv+" reduced "+a.getReducedProductPath());
        //return true;
		return (!ap.equals(a));
    }

	int determineSpouseProductPattern(AlgebraSymbolVector asv){
		int ret = -1; int nIndex = 0;
		int upIndex = -1; int downIndex = -1; int spouseIndex = -1; int sibOIndex = -1;
		int idIndex = -1; int sibYIndex = -1; int sibIndex = -1;int id1Index = -1;
		int i = -1;
		String sex = "";
		for (asv.reset();asv.isNext();){
			i++;
		    AlgebraSymbol as = asv.getNext();
			if (as.getArrowType() == Bops.UP && upIndex == -1) {
				upIndex = i;
				nIndex++;
			}
			else if (as.getArrowType() == Bops.DOWN && downIndex == -1) {
				downIndex = i;
				nIndex++;
			}
			else if (as.getArrowType() == Bops.SPOUSE && spouseIndex == -1) {
				spouseIndex = i;
				sex = as.getSex();
				nIndex++;
			}
			else if (as.getArrowType() == Bops.LEFT && sibOIndex == -1) {
				sibOIndex = i;
				nIndex++;
			}
			else if (as.getArrowType() == Bops.RIGHT && sibYIndex == -1) {
				sibYIndex = i;
				nIndex++;
			}
			else if (as.getArrowType() == Bops.IDENTITY) {
				if (idIndex == -1) idIndex = i;
				else id1Index = i;
				nIndex++;
			}
			if (nIndex > 2) break;
		}
		sibIndex = sibOIndex + sibYIndex +1;
		System.out.println("Start asv "+asv+" up "+upIndex+" do "+downIndex+" sp "+spouseIndex+" sib "+sibIndex+" id "+idIndex);
		if (upIndex == spouseIndex+1 && sibIndex == -1 && downIndex == -1 && idIndex < 1) {//FH
			ret = 0;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		} else if (downIndex != -1 && spouseIndex == downIndex+1 && sibIndex == -1 && upIndex == -1 && idIndex < 1) {//WS
			ret = 0;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sibOIndex == spouseIndex+1 && upIndex == -1 && downIndex == -1) {//B+H=B+I=B+
			ret = 1;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				((AlgebraSymbolVector) cForm.elementAt(ret)).addToBeginning(asv.getLast());
				System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sibOIndex != -1 && spouseIndex == sibOIndex+1 && upIndex == -1 && downIndex == -1) {//WB+,
			ret = 1;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				//System.out.println(" cForm "+cForm.toStringVector());
				((AlgebraSymbolVector) cForm.elementAt(ret)).addToBeginning(asv.getFirst());
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
			   // cForm.insertElementAt(asv,ret);
		}else if (sibYIndex == spouseIndex+1 && upIndex == -1 && downIndex == -1) {//B-H=B-I=B-
			ret = 2;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				((AlgebraSymbolVector) cForm.elementAt(ret)).addToBeginning(asv.getLast());
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sibYIndex != -1 && spouseIndex == sibYIndex+1 && upIndex == -1 && downIndex == -1) {//WB-
			ret = 2;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
			//	System.out.println(" cForm "+cForm.toStringVector());
				((AlgebraSymbolVector) cForm.elementAt(ret)).addToBeginning(asv.getFirst());
			//	System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sex.equals("F") && ((idIndex == spouseIndex+1 && upIndex == -1 && downIndex == -1) ||
		(id1Index == spouseIndex+1 && idIndex == 0 && upIndex == -1 && downIndex == -1))){//IW,IWI
			ret = 3;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sex.equals("M") && ((spouseIndex == idIndex+1 && upIndex == -1 && downIndex == -1) ||
		(spouseIndex == id1Index+1 && idIndex == 0 && upIndex == -1 && downIndex == -1))){//Hi,HiI
			ret = 3;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sex.equals("M") && ((idIndex == spouseIndex+1 && upIndex == -1 && downIndex == -1) ||
		(id1Index == spouseIndex+1 && idIndex == 0 && upIndex == -1 && downIndex == -1))){//iH,iHi
			ret = 4;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (sex.equals("F") && ((spouseIndex == idIndex+1 && upIndex == -1 && downIndex == -1) ||
		(spouseIndex == id1Index+1 && idIndex == 0 && upIndex == -1 && downIndex == -1))){//WI,WIi
			ret = 4;
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				for (asv.reset();asv.isNext();){
				    ((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv.getNext());
				}
				//System.out.println("ret "+ret+" asv1 "+(AlgebraSymbolVector) cForm.elementAt(ret)+" asv "+asv);
			}
		}else if (upIndex == 0 && spouseIndex == idIndex+1 && idIndex == upIndex+1 && sibIndex == -1) {//WBM
			String sex1 = "";
			System.out.println(" WBM or HZF "+ asv);
			if (((AlgebraSymbol)asv.getSymbol(2)).getSex().equals("F")){
			//if (asv.getFirst().getSex().equals("F")){
			    ret = 5;
				sex1 = "F";
			} else {
				ret = 6;
				sex1 = "M";
			}
			if (cForm.size() < ret+1) {
				for (int j=cForm.size();j<ret+1;j++)
					cForm.insertElementAt(new AlgebraSymbolVector(),j);
			}
				AlgebraSymbolVector asv2 = new AlgebraSymbolVector();
			if (((AlgebraSymbolVector) cForm.elementAt(ret)).size() == 0){
				//AlgebraSymbolVector asv2 = new AlgebraSymbolVector();
				//((AlgebraSymbolVector) cForm.elementAt(ret)).addToBeginning(asv.getFirst());
				asv2.addToBeginning(asv.getFirst());
				AlgebraSymbolVector fts = Algebra.getCurrent().getFocalElements();
				AlgebraSymbol as = null;
				for (fts.reset();fts.isNext();){
					as = fts.getNext();
					if (as.getSex().equals(sex1)) break;
				}
				//((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(as);
				asv2.addToEnd(as);
			/*	for (int j=3;j<asv.size();j++){//add remaining symbols
					asv2.addToEnd(asv.getSymbol(j));
				}*/
				asv2 = asv2.reduce();
				for (asv2.reset();asv2.isNext();){
					((AlgebraSymbolVector) cForm.elementAt(ret)).addToEnd(asv2.getNext());
				}
				System.out.println("asv2 "+asv2);
				System.out.println("ret "+ret+" cForm "+(AlgebraSymbolVector) cForm.elementAt(ret));
			}
		}
		if (ret != -1)
		System.out.println("End ret "+ret+" asv "+asv+" cForm "+(AlgebraSymbolVector)cForm.elementAt(ret));
		else System.out.println("End ret "+ret+" asv "+asv);
		return ret;
	}

}