

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @Dwight Read 6/29/03
 * @version 1.0
 */

public class RuleFactory {

    public RuleFactory() {
    }

	static ListVector ruleStatus = new ListVector();

	static public void setInitialRuleStatus(int rule, boolean status){
		ListVector rStatus = new ListVector();
		rStatus.addElement(new Integer(rule));
		rStatus.addElement(new Boolean(status));
		ruleStatus.addElement(rStatus);
    }

	public ListVector getInitialRuleStatus(){
	    return ruleStatus;
	}

	static boolean getTheInitialRuleStatus(int rule){
	    for (ruleStatus.reset();ruleStatus.isNext();){
		    ListVector lv = (ListVector) ruleStatus.getNext();
			Integer iRule = (Integer)lv.elementAt(0);
			if (iRule.intValue() == rule) return ((Boolean) lv.elementAt(1)).booleanValue();
	    }
		return true;
	}

	public static Rule setRule(int code) {
				Debug.prout(0," set rule "+code);
		//if (code != MAKEEQUIVALENTRULE && code != REWRITEPRODUCTRULE) return null;//Tongan: need a way to determine which rules will be activated in aopsprog?
		//if (code == MAKEEQUIVALENTRULE) return null;//Shipibo; need way to determine which rules are not allowed
		Algebra theAlgebra = Algebra.getCurrent();
		RuleVector rv = theAlgebra.getRules();
		switch (code){
			case CYLINDERRULE:
				if (getRule(CYLINDERRULE) == null) {
					CylinderRule r0 = new CylinderRule();
					r0.setActiveRule(getTheInitialRuleStatus(CYLINDERRULE));
					theAlgebra.addRule(r0);
					r0.setRuleText("Equate Top Element and Bottom Element");
					return r0;
				} else return getRule(CYLINDERRULE);
			case SPOUSEPRODUCTRULE:
				if (getRule(SPOUSEPRODUCTRULE) == null){
					SpouseProductRule r1 = new SpouseProductRule();
					r1.setActiveRule(getTheInitialRuleStatus(CYLINDERRULE));
					theAlgebra.addRule(r1);
					//r1.setRuleText("Make 'Spouse' of 'Sibling'");
					return r1;
				} else return getRule(SPOUSEPRODUCTRULE);
			case MBSELFRECIPROCALRULE:
				if (getRule(MBSELFRECIPROCALRULE) == null){
					MBSelfReciprocalRule r2 = new MBSelfReciprocalRule();
					r2.setActiveRule(getTheInitialRuleStatus(MBSELFRECIPROCALRULE));
					theAlgebra.addRule(r2);
				//r2.setRuleText("Make Elements Self-Reciprocal");
				    return r2;
				} else return getRule(MBSELFRECIPROCALRULE);
			case CROWSKEWINGRULE:
				if (getRule(CROWSKEWINGRULE) == null){
					CrowSkewingRule r3 = new CrowSkewingRule();
					r3.setActiveRule(getTheInitialRuleStatus(CROWSKEWINGRULE));
					theAlgebra.addRule(r3);
					//r3.setRuleText("Crow Skewing Rule");
					return r3;
				} else return getRule(CROWSKEWINGRULE);
			case MAKEEQUIVALENTRULE:
				if (getRule(MAKEEQUIVALENTRULE) == null){
					MakeEquivalentRule r4 = new MakeEquivalentRule();
					r4.setActiveRule(getTheInitialRuleStatus(MAKEEQUIVALENTRULE));
					theAlgebra.addRule(r4);
					//r4.setRuleText("Make Elements Equivalent");
					return r4;
				} else return getRule(MAKEEQUIVALENTRULE);
			case LINEALDESCENDANTRULE:
			//System.out.println("LINEALLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
				if (getRule(LINEALDESCENDANTRULE) == null){
					LinealDescendantRule r5 = new LinealDescendantRule();
					r5.setActiveRule(getTheInitialRuleStatus(LINEALDESCENDANTRULE));
					theAlgebra.addRule(r5);
					//r1.setRuleText("Male 'Son' and 'Daughter' Identical");
					return r5;
				} else return getRule(LINEALDESCENDANTRULE);
			case COUSINRULE:
				if (getRule(COUSINRULE) == null){
					CousinRule r6 = new CousinRule();
					r6.setActiveRule(getTheInitialRuleStatus(COUSINRULE));
					theAlgebra.addRule(r6);
					//r6.setRuleText("Ith Cousin, J Times Removed");
					return r6;
				} else return getRule(COUSINRULE);
			case SEXRULE:
				if (getRule(SEXRULE) == null){
					SexRule r7 = new SexRule();
					r7.setActiveRule(getTheInitialRuleStatus(SEXRULE));
					theAlgebra.addRule(r7);
					//r7.setRuleText("American Kinship Terminology Sex Marking");
					return r7;
				} else return getRule(SEXRULE);
			case REWRITEPRODUCTRULE:
				if (getRule(REWRITEPRODUCTRULE) == null){
					RewriteProductRule r8 = new RewriteProductRule();
					r8.setActiveRule(getTheInitialRuleStatus(REWRITEPRODUCTRULE));
					theAlgebra.addRule(r8);
					return r8;
				} else return getRule(REWRITEPRODUCTRULE);
		}
		return null;
	}

    public static Rule getRule(int code) {
		//System.out.println(" get rule "+code);
		Algebra theAlgebra = Algebra.getCurrent();
		RuleVector rv = theAlgebra.getRules();
		switch (code){
			case CYLINDERRULE:
				CylinderRule r0 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof CylinderRule) {
						r0 = (CylinderRule) r;
						return r0;
					}
				}
				break;
			case SPOUSEPRODUCTRULE:
				SpouseProductRule r1 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof SpouseProductRule) {
						r1 = (SpouseProductRule) r;
						return r1;
					}
				}
				break;
			case MBSELFRECIPROCALRULE:
				MBSelfReciprocalRule r2 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof MBSelfReciprocalRule) {
						r2 = (MBSelfReciprocalRule) r;
						return r2;
					}
				}
				break;
			case CROWSKEWINGRULE:
				CrowSkewingRule r3 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof CrowSkewingRule) {
						Debug.prout(0,"MMMMMMMMMMMMMMMMMMMMMM in crwo");
						r3 = (CrowSkewingRule) r;
						return r3;
					}
				}
				break;
			case MAKEEQUIVALENTRULE:
				MakeEquivalentRule r4 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof MakeEquivalentRule) {
						r4 = (MakeEquivalentRule) r;
						return r4;
					}
				}
				break;
			case LINEALDESCENDANTRULE:
				//RuleVector rv = theAlgebra.getRules();
				LinealDescendantRule r5 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof LinealDescendantRule) {
						r5 = (LinealDescendantRule) r;
						return r5;
					}
				}
				break;
			case COUSINRULE:
				CousinRule r6 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof CousinRule) {
						r6 = (CousinRule) r;
						return r6;
					}
				}
				break;
			case SEXRULE:
				SexRule r7 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof SexRule) {
						r7 = (SexRule) r;
						return r7;
					}
				}
				break;
			case REWRITEPRODUCTRULE:
				RewriteProductRule r8 = null;
				for (int i=0;i<rv.size();i++){
					Rule r = (Rule) rv.elementAt(i);
					if (r instanceof RewriteProductRule) {
						r8 = (RewriteProductRule) r;
						return r8;
					}
				}
				break;
		}
		return null;
	}

	final static int CYLINDERRULE = 0;
	final static int SPOUSEPRODUCTRULE = 1;
	final static int MBSELFRECIPROCALRULE = 2;
	final static int CROWSKEWINGRULE = 3;
	final static int MAKEEQUIVALENTRULE = 4;
	final static int LINEALDESCENDANTRULE = 5;//s&d --> c for 0 generation and below
	final static int COUSINRULE = 6;
	final static int SEXRULE = 7;//akt sex rule
	final static int REWRITEPRODUCTRULE = 8;
}