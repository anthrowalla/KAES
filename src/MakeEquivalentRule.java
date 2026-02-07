import java.util.*;

public class MakeEquivalentRule extends Rule {

	public MakeEquivalentRule(){
		setRuleText("Make Elements Equivalent");
	}

	ListVector equiv = new ListVector();
	ListVector reducedEquiv = new ListVector();

	boolean activeRule = false;

	public void setActiveRule(boolean flag) {
	   activeRule = flag;
	}

	public boolean getActiveRule(){
	   return activeRule;
	}

	public void setEquivalentElements(AlgebraSymbolVector as1, AlgebraSymbolVector as2) {
		ListVector lv = new ListVector();
		lv.addElement(as1); lv.addElement(as2);
		//System.out.println("xxxxxxxxxxxxxxxxxx as1 "+as1+" as2 "+as2);
		if (equiv.indexOf(lv) == -1){
		    equiv.addElement(lv);//e.g. AI ~ EI

			AlgebraPath ap1 = new AlgebraPath(as1,true);
			AlgebraPath ap2 = new AlgebraPath(as2,true);
			AlgebraSymbolVector as11 = ap1.getReducedProductPath();
			AlgebraSymbolVector as22 = ap2.getReducedProductPath();
			ListVector lv1 = new ListVector();
			if ((!as11.equals(as1) && !as11.hasFocalElement())||
			(!as22.equals(as2) && !as22.hasFocalElement())){
				lv1.addElement(as11); lv1.addElement(as22);
					//System.out.println("xxxxxxxxxxxxxxxxxx as11 "+as11+" as22 "+as22);
					//if (reducedEquiv.indexOf(lv1) == -1)
				//} else {
				   // lv1.addElement(null);
					//lv1.addElement(null);
				//}
				reducedEquiv.addElement(lv1);//e.g. E~Ai
			} else reducedEquiv.addElement(lv);
		}
	}

	public ListVector getEquivalentElements() {
	    return equiv;
	}

	public ListVector getReducedEquivalentElements() {
	    return reducedEquiv;
	}

	boolean inEquivalents(AlgebraSymbol gen,AlgebraSymbolVector asv0){
		if (asv0.size() == 0) return false;
		AlgebraSymbolVector asv = (AlgebraSymbolVector) asv0.clone();
		asv.addToEnd(gen);
		for (equiv.reset();equiv.isNext();){
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) ((ListVector) equiv.getNext()).elementAt(0);
			if (asv.equals(asv1)) return true;
		}
		return false;
	}

    public boolean doesRuleApply(CayleyInfo a) {//if (1==1) return false;//may be needed for Shipibo??
		if (!activeRule) return false;
		if (ruleDataIndex(a) > -1) return true;
		//boolean flagg = false;//a.generator.getValue().equals("P");
		//if (!flagg) return false;
		AlgebraSymbolVector asv = a.product.getReducedProductPath();
		//if (flagg) System.out.println("In does apply a "+a+" asv "+asv);
		if (asv.size() == 0) {System.out.println(" ALGEBRA PATH a "+a +" asv "+asv);return false;}
		if (checkForEquivalence(asv) != null) return true;
		/*for (equiv.reset();equiv.isNext();){
			ListVector eqv = (ListVector) equiv.getNext();
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0))||
			asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) return true;
			//if (checkReducedEquivalence(asv,asv1,asv2)) return true;
		}*/
		if (checkForReducedEquivalence(asv) != null) return true;
		return false;
   }

    public boolean doesRuleApply(AlgebraPath a) {if (1==1) return false;
		if (!activeRule) return false;
		AlgebraSymbolVector asv = a.getReducedProductPath();
		if (asv.size() == 0) return false;
		if (checkForEquivalence(asv) != null) return true;
		AlgebraSymbolVector asv1 = a.getProductPath();
		if (checkForEquivalence(asv1) != null) return true;
	/*	for (equiv.reset();equiv.isNext();){
			ListVector eqv = (ListVector) equiv.getNext();
			//System.out.println(" a "+a+" asv "+asv + " asv1 "+asv1+" asv2 "+asv2);
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0))||
			asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) return true;
		    //if (!asv.toString().equals(a.path.toString())) {System.out.println(" asv "+asv.toString()+" path "+a.path.toString());};//return false;//skip check reduced based on
																		//togan case when e.g. AI and EI are not equivalent
			//if (checkReducedEquivalence(asv,asv1,asv2)) return true;
		}*/
		/*if (!asv.toString().equals(a.path.toString())) {
			System.out.println(" asv "+asv.toString()+" path "+a.path.toString());
			return false;
			};*///return false;//skip check reduced based on
																		//togan case when e.g. AI and EI are not equivalent
		if (checkForReducedEquivalence(asv) != null) return true;
		return false;
   }

	 /* rule modifies CayleyTable using equivalence between elements
	 * @a path to which the rule is to be applied
	 * return boolean
	 */
	 public boolean applyRule(CayleyInfo a) {
		//boolean flagg = a.generator.getValue().equals("I");
		//if (flagg) System.out.println("in MER cayley "+a);
		String aString = a.toString();
		int k = ruleDataIndex(a);
		if (k > -1){
			a.product.reducedPath = getRuleDataReducedPath(k);
			return (!aString.equals(a.toString()));
		}
		ListVector data = new ListVector();
		data.addElement(a);
		AlgebraPath ap0 = a.term;
		AlgebraSymbolVector asv0 = null;
		if (ap0.isReducedEquivalentPath()){
			asv0 = ap0.getReducedProductPath().equivalentLeftProduct();
		    AlgebraSymbolVector test = (AlgebraSymbolVector) asv0.clone();
			//if (flagg) System.out.println(" one "+(new AlgebraPath(test,true)).reducedPath.toString()+
			//" two "+asv0.toString());
			if (!(new AlgebraPath(test,true)).reducedPath.toString().equals(asv0.toString())){
				asv0 = ap0.getReducedProductPath().equivalentRightProduct();
				test = (AlgebraSymbolVector) asv0.clone();
			}
			test.addToEnd(a.generator);
			if ((new AlgebraPath(test,true)).toString().equals("0"))
				asv0 = ap0.getReducedProductPath().equivalentRightProduct();

		//if (flagg) System.out.println("VVVVVVVVVVVVVVVV asv0 "+asv0+" ap0 "+ap0+" asv00 "+ap0.getProductPath().equivalentRightProduct());
			//asv0 = ap0.getEquivalentPathLeft().getProductPath();//  getReducedProductPath();
		}else asv0 = a.term.getReducedProductPath();

		AlgebraSymbolVector asv1 = (AlgebraSymbolVector) asv0.clone();
		asv1.addToEnd(a.generator);
		//System.out.println("xxxxxxxxxx a "+a +" asv1 "+asv1 + " asvo "+asv0+" get Prod "+a.product.getProductPath());
		AlgebraPath app = new AlgebraPath(asv1,true);
		Rule rule = RuleFactory.getRule(RuleFactory.REWRITEPRODUCTRULE);
		if (rule.doesRuleApply(app)) rule.applyRule(app);

		AlgebraSymbolVector asv = app.getReducedProductPath();
		//if (flagg) System.out.println("yyyyyyy app "+app +" asv "+asv+" prod path "+app.getProductPath());
		AlgebraPath ap = null;
		boolean flag = false;
/*		if (tryFocalProductPath(asv)) {
			AlgebraSymbolVector theProduct = reduceProductPath(asv);
			ap = testEquivalence(theProduct);
			flag = true;
		}*/
		//if (ap == null) ap = testEquivalence(asv);
		if (ap == null) ap = makeEquivalence(checkForEquivalence(asv));
		//if (ap == null) ap = testEquivalence(a.product.getProductPath());
		if (ap == null) ap = makeEquivalence(checkForEquivalence(a.product.getProductPath()));
		if (ap == null &&
		(!asv.getFirst().isFocalElement() || asv.size() != asv0.size()) &&
		asv.size() != a.product.getProductPath().size()){//skip testR based on tongan with
			//e.g. Ai and Ei not equivalent; thus ADa-->Ai and Ai is not reduced
			if (asv.size() != 1 || asv.size() != asv0.size() ||
			a.generator.toString().equals(a.product.toString())){
			   // ap = testReducedEquivalence(asv);
			    ap = makeEquivalence(checkForReducedEquivalence(asv));
			}
		}
		if (ap == null) ap = checkChildElement(a,asv);
		if (ap != null){
			if (flag) {
			    //System.out.println("11111111111111111 a " + a+ " ap "+ap.getProductPath());
				a.product.reducedPath = ap.getProductPath();
			} else {
				//if (flagg) System.out.println("222222222222222222 a " + a+ " ap "+ap.getProductPath());
				a.product.reducedPath = ap.getReducedProductPath();
			}
		}
		data.addElement(a.product.reducedPath);
		if (ruleData.indexOf(data) == -1) ruleData.addElement(data);
	    return (!aString.equals(a.toString()));
    }

	 public boolean applyRule(AlgebraPath a) {
		AlgebraPath ap1 = (AlgebraPath)a.clone();
		System.out.println("IN make equivalent RULE " + a);
		AlgebraPath ap = null;
		if (tryFocalProductPath(a.getProductPath())) {
			AlgebraSymbolVector theProduct = reduceProductPath(a.getProductPath());
			//ap = testEquivalence(theProduct);
			ap = makeEquivalence(checkForEquivalence(theProduct));
		}
		ListVector lv = null;
		//if (ap == null) ap = testEquivalence(a.getReducedProductPath());
		if (ap == null) ap = makeEquivalence(checkForEquivalence(a.getReducedProductPath()));
		//if (ap == null) ap = testReducedEquivalence(a.getReducedProductPath());
		if (ap == null) ap = makeEquivalence(checkForReducedEquivalence(a.getReducedProductPath()));
		if (ap != null) a.reducedPath = ap.getReducedProductPath();
		System.out.println(" a "+a+" ap "+ap+" path "+a.path+" reduced "+a.reducedPath);
	    return (!ap1.toString().equals(a.toString()));
    }

	AlgebraPath checkChildElement(CayleyInfo a, AlgebraSymbolVector asv){
		if (Algebra.getCurrent().getFocalElements().size() < 2) return null;
	    if (asv.size() > 1) return null;
		AlgebraSymbol as = asv.getFirst();
		if (as.getArrowType() == Bops.DOWN) {
		    if (a.term.getReducedProductPath().size() > 1) return null;
			AlgebraSymbol as1 = a.term.getReducedProductPath().getFirst();
			if (as1.getArrowType() != Bops.DOWN) return null;
			String sex = as1.getSex();
			if (!a.generator.isFocalElement()) return null;
			if (as.getSex().equals(sex)) return null;
			AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
			asv1.addToBeginning(Algebra.getCurrent().getFocalElement(sex));
			asv1.addToEnd(as);
			return new AlgebraPath(asv1,true);
		}
		return null;
	}

	AlgebraSymbolVector reduceProductPath(AlgebraSymbolVector asv){
		if (asv.size() <= 3) return asv;//XIi or XiI does not reduce;XYIi reduce XY=Z, return ZIi or ZiI
		AlgebraSymbol as0 = asv.getFirst();
		asv.removeBeginning();
		AlgebraSymbol as1 = asv.getFirst();
		asv.removeBeginning();
		AlgebraPath ap = new AlgebraPath(asv,true);
		AlgebraSymbolVector asv1 = ap.getReducedProductPath();
		asv1.addToBeginning(as1);
		asv1.addToBeginning(as0);
		return asv1;
	}

	boolean tryFocalProductPath(AlgebraSymbolVector asv){
	    if (Algebra.getCurrent().getFocalElements().size() == 2 && asv.size() > 2){
			AlgebraSymbolVector ft = Algebra.getCurrent().getFocalElements();
		    return (ft.indexOf(asv.getSymbol(0)) > -1 && ft.indexOf(asv.getSymbol(1)) > -1);
	    }
		return false;
	}

	AlgebraPath makeEquivalentPath(AlgebraSymbolVector asv1,AlgebraSymbolVector asv2){
		AlgebraPath ap1 = new AlgebraPath(asv1,false);
		ap1.reducedPath = asv1;//this assumes that no reduction should be made of equivalent elements!!
		AlgebraPath ap2 = new AlgebraPath(asv2,false);
		ap2.reducedPath = asv2;
		return ap1.makeEquivalentPath(ap2);
	}

/*	AlgebraPath testEquivalence(AlgebraSymbolVector asv) {
		for (equiv.reset();equiv.isNext();){
			ListVector eqv = (ListVector) equiv.getNext();
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0)) || asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) {
				return makeEquivalentPath(asv1,asv2);
			}
		}
		return null;
	}*/

	AlgebraPath makeEquivalence(ListVector eqv) {
		if (eqv == null) return null;
		return makeEquivalentPath((AlgebraSymbolVector) eqv.elementAt(0),(AlgebraSymbolVector) eqv.elementAt(1));
	}

	ListVector checkForEquivalence(AlgebraSymbolVector asv){
		for (equiv.reset();equiv.isNext();){
		    ListVector eqv = (ListVector) equiv.getNext();
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0)) ||
			asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) return eqv;
		}
	    return null;
	}
	ListVector checkForReducedEquivalence(AlgebraSymbolVector asv){
		//for (lv.reset();lv.isNext();){
		for (int i=0;i<reducedEquiv.size();i++){
		    ListVector eqv = (ListVector) reducedEquiv.elementAt(i);
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0)) ||
			asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) return (ListVector) equiv.elementAt(i);
		}
	    return null;
	}


}


	/*AlgebraSymbolVector recomputeProduct(CayleyInfo ci) {
		AlgebraSymbolVector asvL = (AlgebraSymbolVector)ci.term.getReducedProductPath().equivalentLeftProduct().clone();
		asvL.addToEnd(ci.generator);
		AlgebraPath ap = new AlgebraPath(asvL,true);
		//Rule rule = RuleFactory.getRule(RuleFactory.REWRITEPRODUCTRULE);
		//if (rule.doesRuleApply(ap)) rule.applyRule(ap);
		if (ap.reducedPath.isZeroVector()){
		    AlgebraSymbolVector asvR = (AlgebraSymbolVector)ci.term.getReducedProductPath().equivalentRightProduct().clone();
		    asvR.addToEnd(ci.generator);
			ap = new AlgebraPath(asvR,true);
		   // if (rule.doesRuleApply(ap)) rule.applyRule(ap);
			return ap.reducedPath;
		}
		return ap.reducedPath;
	}*/



/*	AlgebraPath testReducedEquivalence(AlgebraSymbolVector asv) {
		for (reducedEquiv.reset();reducedEquiv.isNext();){
			ListVector eqv = (ListVector) reducedEquiv.getNext();
			if (asv.equals((AlgebraSymbolVector) eqv.elementAt(0)) || asv.equals((AlgebraSymbolVector) eqv.elementAt(1))) {
				return makeEquivalentPath(asv1,asv2);
			}
		}
		return null;
	}*/

/*	AlgebraPath testReducedEquivalence(AlgebraSymbolVector asv) {
		for (reducedEquiv.reset();reducedEquiv.isNext();){
			ListVector eqv = (ListVector) reducedEquiv.getNext();
			AlgebraSymbolVector asv1 = (AlgebraSymbolVector) eqv.elementAt(0);
			AlgebraSymbolVector asv2 = (AlgebraSymbolVector) eqv.elementAt(1);
			if (checkReducedEquivalence(asv,asv1,asv2)){
				return makeEquivalentPath(asv1,asv2);
			}
		}
		return null;
	}*/

/*
	String sex = "";
	 public boolean applyRule(AlgebraPath a) {
		AlgebraSymbolVector asv = a.getReducedProductPath();
		Equation eq = new Equation();
		if (asv.size()==1) eq = findEquivalentEquation((AlgebraSymbol) asv.elementAt(0));
		else eq = findEquivalentEquation(asv);
		AlgebraSymbol as = asv.getFirst().getOppositeSexGenerator();
		//AlgebraPath ap1 = new AlgebraPath(as,as);
		AlgebraPath ap = new AlgebraPath();
		if (asv.size()==1) {
			//System.out.println(" asv "+asv+" eq "+eq );
		   AlgebraPath ap1 = new AlgebraPath(as);
			if (sex.equals("")) {
				if (eq.getLhs().getLast().equals((AlgebraSymbol) asv.elementAt(0))){
					 ap = ap1.makeEquivalentPath(a);
					 sex = ap1.getPathFirstSex();
				} else {
					ap = a.makeEquivalentPath(ap1);
					sex = ap1.getPathFirstSex();
				}
			} else {
			   if (ap1.getPathFirstSex().equals(sex))
					ap = ap1.makeEquivalentPath(a);
				else	ap = a.makeEquivalentPath(ap1);
			}
		} else {
		   AlgebraPath ap1 = new AlgebraPath(as,as);
			if (eq.getLhs().equals(asv)) ap = ap1.makeEquivalentPath(a);
		   else ap = a.makeEquivalentPath(ap1);
		}
		//AlgebraPath ap = ap1.makeEquivalentPath(a.product);
		//System.out.println(" BEFORE 2a " +a+" ap "+ap);
		a.reducedPath = ap.getReducedProductPath();
		//a.path = ap.getProductPath();
		//a.product.reducedPath = ap.getReducedProductPath();
		//System.out.println(" AFTGER2 a " +a+" ap "+ap);
		return true;
    }
*/
/*
	 public boolean applyRulexx(CayleyInfo a) {
		AlgebraSymbolVector asv = a.product.getReducedProductPath();
		Equation eq = new Equation();
		if (asv.size()==1) eq = findEquivalentEquation((AlgebraSymbol) asv.elementAt(0));
		else eq = findEquivalentEquation(asv);
		AlgebraSymbol as = asv.getFirst().getOppositeSexGenerator();
		//AlgebraPath ap1 = new AlgebraPath();
		AlgebraPath ap = new AlgebraPath();
		if (asv.size()==1) {
		   AlgebraPath ap1 = new AlgebraPath(as);
			if (sex.equals("")) {
				if (eq.getLhs().getLast().equals((AlgebraSymbol) asv.elementAt(0))){
					 ap = ap1.makeEquivalentPath(a.product);
					 sex = ap1.getPathFirstSex();
				} else {
					ap = a.product.makeEquivalentPath(ap1);
					sex = ap1.getPathFirstSex();
				}
			} else {
			   if (ap1.getPathFirstSex().equals(sex))
					ap = ap1.makeEquivalentPath(a.product);
				else	ap = a.product.makeEquivalentPath(ap1);
			}
		} else {
		   AlgebraPath ap1 = new AlgebraPath(as,as);
			if (eq.getLhs().equals(asv)) ap = ap1.makeEquivalentPath(a.product);
		   else ap = a.product.makeEquivalentPath(ap1);
		}
		a.product.reducedPath = ap.getReducedProductPath();
     return true;
    }
*/

/*
	 Equation findEquivalentEquation(AlgebraSymbolVector asv){
	   EquationVector eqv = Algebra.getCurrent().getEquations();
		AlgebraSymbol as = asv.getFirst().getOppositeSexGenerator();
		for (eqv.reset1();eqv.isNext();){
		   Equation eq = eqv.getNext();
			if (!eq.getLhs().equals(asv) && !eq.getRhs().equals(asv)) continue;
			if (!eq.getLhs().sameElements() || !eq.getRhs().sameElements()) continue;
			if (eq.getLhs().equals(asv) && !eq.getRhs().getFirst().equals(as)) continue;
			if (eq.getRhs().equals(asv) && !eq.getLhs().getFirst().equals(as)) continue;
			return eq;
		}
		return null;
	 }
*/

/*	 Equation findEquivalentEquation(AlgebraSymbol as){//S i = D
		AlgebraSymbol ftS = new AlgebraSymbol(); AlgebraSymbol ftO = new AlgebraSymbol();
		AlgebraSymbolVector fts = Algebra.getCurrent().getFocalElements();
		if (((AlgebraSymbol) fts.elementAt(0)).getSex().equals(as.getSex())) {
		   ftS = (AlgebraSymbol) fts.elementAt(0);
			ftO = (AlgebraSymbol) fts.elementAt(1);
		} else {
		   ftO = (AlgebraSymbol) fts.elementAt(0);
			ftS = (AlgebraSymbol) fts.elementAt(1);
		}
		AlgebraSymbol as1 = as.getOppositeSexGenerator(); //D
		AlgebraSymbolVector asv = new AlgebraSymbolVector();
		asv.addToBeginning(ftO);asv.addToEnd(as);
		AlgebraSymbolVector asv1 = new AlgebraSymbolVector();
		asv1.addToBeginning(ftS);asv1.addToEnd(as1);
		Equation eqA = null;
		Equation eqB = null;
	   EquationVector eqv = Algebra.getCurrent().getEquations();
		for (eqv.reset1();eqv.isNext();){
			Equation eq1 = eqv.getNext();
			if (eq1.getLhs().equals(asv) && eq1.getRhs().size() == 1 &&
				((AlgebraSymbol) eq1.getRhs().elementAt(0)).equals(as1)) eqA = eq1;//Si=D
			else if (eq1.getLhs().equals(asv1) && eq1.getRhs().size() == 1 &&
				((AlgebraSymbol) eq1.getRhs().elementAt(0)).equals(as)) eqB = eq1;//DI=S
			if (eqA != null && eqB != null) break;
		}
		if (eqB == null) eqA = null;
		else {
			eqA.setEqType(Equation.EQUIVALENCE);
			eqB.setEqType(Equation.EQUIVALENCE);
		}
//	System.out.println("THE EQUATION eq "+eq+" as "+as);
		return eqA;
	 }
*/
/*	boolean parentChildProductPathX(AlgebraSymbolVector asv){
		if (asv.size() < 3) return false;
		System.out.println(" in pcp asv "+asv);
		AlgebraSymbol as1 = asv.getSymbol(asv.size()-1);
		if (as1.getArrowType() == Bops.UP) {
			String sex = as1.getSex();
		    AlgebraSymbol as2 = asv.getSymbol(asv.size()-2);
			if (as2.getArrowType() == Bops.DOWN && as2.getSex().equals(sex)) {
		        AlgebraSymbol as3 = asv.getSymbol(asv.size()-3);
				if (as3.getArrowType() == Bops.IDENTITY) return !as3.getSex().equals(sex);
			}
		}
		return false;
	}
*/

/*
   public boolean doesRuleApplyX(CayleyInfo a) {
		if (!activeRule) return false;
		AlgebraPath ap = a.term;
		AlgebraSymbolVector asv0 = null;
		if (ap.isEquivalentPath()){
			asv0 = ap.getEquivalentPathRight().getReducedProductPath();
			    System.out.println("in equiv here is asv0 "+asv0+" term "+a.term+" gen "+a.generator+" prod "+a.product.getReducedProductPath());
			if (!inEquivalents(a.generator,asv0)) return false;
			asv0 = ap.getEquivalentPathLeft().getReducedProductPath();
			    System.out.println("in equiv asv0 "+asv0+" rigt "+ap.getEquivalentPathLeft().getReducedProductPath());
			return inEquivalents(a.generator,asv0);
		}else {
			asv0 = a.term.getReducedProductPath();
				//AlgebraSymbolVector asv = a.product.getProductPath();
				//System.out.println(" ALGEBRA PATH2 a "+a+" asv "+asv+" red "+a.product.getReducedProductPath());
		    return inEquivalents(a.generator,asv0);
		}
   }
*/

/*	boolean checkReducedEquivalence(AlgebraSymbolVector asv,AlgebraSymbolVector asv1,AlgebraSymbolVector asv2){
		AlgebraPath ap1 = new AlgebraPath(asv1,true);
		AlgebraPath ap2 = new AlgebraPath(asv2,true);
		AlgebraSymbolVector asv11 = ap1.getReducedProductPath();
		AlgebraSymbolVector asv22 = ap2.getReducedProductPath();
		if (asv.equals(asv11)|| asv.equals(asv22)) return true;
		return false;
	}*/
