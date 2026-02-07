/* History
* /11/11 DR added applyRule(CayleyInfo a)
*/

public class Rule {
	String ruleText="A Rule";

	public Rule(String textal_description) {
		ruleText = textal_description;
	}

	public Rule() {
	}

	ListVector ruleData = new ListVector();

	public void setRuleText(String textal_description) {
		ruleText = textal_description;
	}

	public String getRuleText() {
		return ruleText;
	}

	public boolean doesRuleApply(AlgebraPath a) {
		return false;
	}

	public boolean doesRuleApply(CayleyInfo a) {
		return false;
	}

	public boolean applyRule(AlgebraPath a) {
		return false;
	}

	public boolean doesRuleApply(AlgebraPath a, CayleyTable cly){
		return false;
	}
	public boolean applyRule(CayleyInfo a) {
		return false;
	}

	public boolean getActiveRule(){
	    return false;
	}

	AlgebraSymbolVector getRuleDataReducedPath(int i){
		return (AlgebraSymbolVector) ((ListVector) ruleData.elementAt(i)).elementAt(1);
	}

	public int ruleDataIndex(CayleyInfo a){
		int i = -1;
	    for (ruleData.reset();ruleData.isNext();){
			i++;
		    ListVector lv = (ListVector) ruleData.getNext();
			if (((CayleyInfo)lv.elementAt(0)).equals(a)) return i;
	    }
		return -1;
	}
}