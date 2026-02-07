import java.util.Vector;

public class RuleVector extends ListVector {

	public RuleVector() {
		super(1,1);
	}

	public RuleVector(int n) {
		super(n);
	}

	public RuleVector(int n, int m) {
		super(n,m);
	}

	public Rule getSymbol(int i) {
		index = i;
		return (Rule) elementAt(index);
	}

	public Rule getSymbol() {
		return (Rule) get();
	}

	public Rule getFirst() {
		index = 0;
		return (Rule) elementAt(0);
	}

	public Rule getLast() {
		index = size()-1;
		return (Rule) get();
	}

	public Rule getPrev() {
		if (--index >= 0)
			return (Rule) get();
		else {
			index=-1;
			return null;
		}
	}

	public Rule getNextSymbol() {
		return (Rule) getNext();
	}

}