

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CrowSkewingRule extends Rule {

   public CrowSkewingRule() {
		setRuleText("Crow Skewing Rule");
   }

	AlgebraSymbol mo = null;
	AlgebraSymbol fa = null;
	AlgebraSymbol si = null;
	AlgebraSymbol siO = null;
	AlgebraSymbol siY = null;
	AlgebraSymbol br = null;
	AlgebraSymbol brO = null;
	AlgebraSymbol brY = null;
	AlgebraSymbol so = null;
	AlgebraSymbol da = null;

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

    /* rule applies if product is of form ...ZF...
    * @a AlgebraPath the path to be tested for applicability of rule
    * return boolean
    */
	public boolean doesRuleApply(AlgebraPath a) {
		//System.out.println(" CROW SKEWING RULE");
		if (!activeRule) return false;
		//String as = a.getProductPath().toString();
		AlgebraSymbolVector av = a.getProductPath();
		if (ruleApplies(av)) return true;
		if (reciprocalRuleApplies(av)) return true;
		av = a.getReducedProductPath();
		   //as = a.getReducedProductPath().toString();
		if (ruleApplies(av)) return true;
		if (reciprocalRuleApplies(av)) return true;
		return false;
	}

	boolean ruleApplies(AlgebraSymbolVector av){
		String test = "";
		String as = av.toString();
		if (fa == null ) getParentSymbols();
		if (si == null || siY == null || siO == null) getSisterSymbols();
		if (fa != null) {
		   if (si != null) {
				test = si.getValue()+", "+fa.getValue();
				if (as.indexOf(test) != -1) return true;
		   }
			if (siO != null) {
				test = siO.getValue()+", "+fa.getValue();
				if (as.indexOf(test) != -1) return true;
		   }
			if (siY != null) {
				test = siY.getValue()+", "+fa.getValue();
				if (as.indexOf(test) != -1) return true;
		   }
		}
		return false;
	}

	boolean reciprocalRuleApplies(AlgebraSymbolVector av){
		String as = av.toString();
		String test = "";
		if (da == null) getChildSymbols();
		if (br == null || brY == null || brO == null) getBrotherSymbols();
		if (da != null) {
		   if (br != null) {
				test = da.getValue()+", "+br.getValue();
				int indx = as.indexOf(test);//as of form [x, y, z]
				if (indx == -1) {
				   test = so.getValue()+", "+br.getValue();
					indx = as.indexOf(test);
				}
			//System.out.println("AAAAAAAAAA as "+as+" test "+test + " indx "+indx);
				if (indx > -1) {
					if (av.size() == 2) return false;//cb
					indx = (indx -1)/3;
					if (av.size() > indx+2) {
				      AlgebraSymbol a = (AlgebraSymbol) av.elementAt(indx + 2);
				      if (a != null && a.getSex().equals("F")) return true;
					}
				}
		   }
			if (brO != null) {
				test = da.getValue() + ", "+ brO.getValue();
				int indx = as.indexOf(test);
				if (indx > -1) {
					indx = (indx -1)/3;
					if (av.size() > indx+2) {
						AlgebraSymbol a = (AlgebraSymbol) av.elementAt(indx + 2);
						if (a != null && a.getSex().equals("F")) return true;
					}
				}
		   }
			if (brY != null) {
				test = da.getValue() + ", "+ brY.getValue();
				int indx = as.indexOf(test);
				if (indx > -1) {
					indx = (indx -1)/3;
					if (av.size() > indx+2) {
				      AlgebraSymbol a = (AlgebraSymbol) av.elementAt(indx + 2);
				      if (a != null && a.getSex().equals("F")) return true;
					}
				}
			}
		}
		return false;
	}

    /* rule modifies CayleyTable so that Si of Fa is replaced by Mo of Fa
    * or Child of Br of female is replaced by Child of Si of female
    * @asv AlgebraSymbolVector to which the rule is to be applied
    * return AlgebraSymbolVector
    */
	AlgebraSymbolVector crowSkewingRule(AlgebraSymbolVector asv) {
		AlgebraSymbolVector ret = new AlgebraSymbolVector();
		//boolean flag = (asv.equals("[E, E, D, i, P, i]"));
		for (int i = 0; i < asv.size();i++){
			AlgebraSymbol as = (AlgebraSymbol) asv.elementAt(i);
			if ((as.isSibGenerator() || as.isFocalElement()) && as.getSex().equals("F") && i<asv.size()-1){//Z
				int j = i+1;//i++;
				AlgebraSymbol as1 = (AlgebraSymbol) asv.elementAt(j);
				if (as1.getArrowType() == Bops.UP && as1.getSex().equals("M")) {//ZF
					if (ret.size() > 0){
						AlgebraSymbol a = ret.getFirst();
						ret.remove(ret.size()-1);
						AlgebraPath p = new AlgebraPath(a,mo);//Z-->M
						AlgebraSymbolVector asv1 = p.getReducedProductPath();
						for (int ii = 0; ii < asv1.size();ii++){
							ret.addToBeginning((AlgebraSymbol) asv1.elementAt(ii));
						//ret.addToBeginning(mo);
						//ret = ret.reduce();
						}
					   ret.addToBeginning(fa);
						i = j;
					} else {//ZF
						ret.addToBeginning(mo);//Z-->M
					   ret.addToBeginning(fa);
						i = j;
					}
				} else {//ZX
					ret.addToBeginning(as);
					//ret.addToBeginning(as1);
				}
			} else if (as.getArrowType() == Bops.DOWN && i<asv.size()-1) {//C
			   int j = i+1;
			//System.out.println(" asv "+asv.toStringVector()+" j "+j);
				AlgebraSymbol as1 = (AlgebraSymbol) asv.elementAt(j);

				if ((as1.isSibGenerator() || as1.isFocalElement()) && as1.getSex().equals("M")) {//CB
					if (i<asv.size()-2) {
						int k = i+2;
						AlgebraSymbol as2 = 	(AlgebraSymbol) asv.elementAt(k);
						if (as2.getSex().equals("F")) {//CBfemale
							boolean flag =  (ret.size() == 0);
				//if (flag) System.out.println("SSSSSSSSSSS as "+as +" as1 "+as1 +" as2 "+as2+ " ret "+ret+" i "+i);

							/*AlgebraPath p = new AlgebraPath(as,so);//Da So --> Da Da
							AlgebraSymbolVector asv1 = p.getReducedProductPath();

							for (int ii = 0; ii < asv1.size();ii++){
								ret.addToBeginning((AlgebraSymbol) asv1.elementAt(ii));
							}
							ret.addToBeginning(as2);*/


							ret.addToBeginning(as);//Bfemale
							AlgebraPath p = new AlgebraPath(so,as2);//Sofemale --> X
							AlgebraSymbolVector asv1 = p.getReducedProductPath();

							for (int ii = 0; ii < asv1.size();ii++){
								ret.addToBeginning((AlgebraSymbol) asv1.elementAt(ii));
							}
							//if (flag)
				//System.out.println("SSSSSSSSSSS  again as "+as +" as1 "+as1 +" as2 "+as2+ " ret "+ret+" i "+i);
							i = k;
						}else {//CBX
							ret.addToBeginning(as);
						}
					} else if (as1.isFocalElement()) {//CmaleSelf(female)
						ret.addToBeginning(as);//C-->C
						/*if (so == null) getChildSymbols();
						ret.addToBeginning(so);//B-->So
						System.out.println(" product asv "+asv+" ret "+ret+" i "+ i+" j "+j);
						i = j;*/
					}
		      } else {//C
			      ret.addToBeginning(as);
		      }
		   } else {//X
				ret.addToBeginning(as);
		   }
		}
		return ret;
	}

	boolean getParentSymbols() {
		AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		for (gens.reset();gens.isNext();){
		   AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType()==Bops.UP) {
				if (gen.getSex().equals("F"))
				   mo = gen;
		      else if (gen.getSex().equals("M"))
					fa = gen;
			}
		}
		return (mo != null && fa != null);
	}

	boolean getBrotherSymbols() {
		AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		for (gens.reset();gens.isNext();){
		   AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType()==Bops.LEFT) {
				if (gen.getSex().equals("M"))
					brY = gen;
			} else if (gen.getArrowType()==Bops.RIGHT) {
				if (gen.getSex().equals("M"))
					brO = gen;
			} else if (gen.getArrowType()==Bops.IDENTITY) {
				if (gen.getSex().equals("M"))
					br = gen;
			}
		}
		return (brY != null && brO != null && br != null);
	}

		boolean getSisterSymbols() {
		AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		for (gens.reset();gens.isNext();){
		   AlgebraSymbol gen = gens.getNext();
			if (gen.getArrowType()==Bops.LEFT) {
				if (gen.getSex().equals("F"))
					siY = gen;
			} else if (gen.getArrowType()==Bops.RIGHT) {
				if (gen.getSex().equals("F"))
					siO = gen;
			} else if (gen.getArrowType()==Bops.IDENTITY) {
				if (gen.getSex().equals("F"))
					si = gen;
			}
		}
		return (siY != null && siO != null && si != null);
	}

	boolean getChildSymbols() {
		AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		for (gens.reset();gens.isNext();){
		   AlgebraSymbol gen = gens.getNext();
		   if (gen.getArrowType()==Bops.DOWN) {
				if (gen.getSex().equals("F"))
					da = gen;
				else if (gen.getSex().equals("M"))
					so = gen;
			}
		} 
		return (so != null && da != null);
	}

	public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap1 = (AlgebraPath)a.clone();
		//System.out.println("APPLY RULE a one "+a+" a.path "+a.path.toStringVector());
		if ((mo != null && fa != null) || getParentSymbols()){
			// AlgebraSymbolVector asv = (AlgebraSymbolVector)a.getReducedProductPath();
			AlgebraSymbolVector asv = (AlgebraSymbolVector)a.getProductPath().clone();
			for (;;){
				for (;;){
					String s = asv.toString();
					asv = crowSkewingRule(asv);
					if (s.equals(asv.toString())) break;
				}
				String s = asv.toString();
				asv = asv.reduce();
				if (s.equals(asv.toString())) break;
			}
			//System.out.println(" asv four a "+a+" asv "+asv);
					//a.setProductPath(asv);
			AlgebraPath ap = new AlgebraPath(asv,false);
			//ap.reducePath(asv);
			if (!doesRuleApply(ap)) {//apply rules to reduced symbol asv
				RuleVector rules = Algebra.getCurrent().getRules();
				//for (rules.reset();rules.isNext();){
					//Rule rule = (Rule) rules.getNext();
				for (int i=0;i<rules.size();i++){
					Rule rule = (Rule) rules.elementAt(i);
					if (rule.equals(this)) continue;
					if (rule.doesRuleApply(ap)) rule.applyRule(ap);
				}
			}
			a.setReducedProductPath(ap.getReducedProductPath());
			Debug.prout(0, "asv five a "+a+" ap "+ap.getReducedProductPath());
		//	a.setReducedProductPath(asv);
			//return true;
			return (!ap1.equals(a));
		}
		return false;
	}
}
