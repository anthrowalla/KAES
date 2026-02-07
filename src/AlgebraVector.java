import java.util.Vector;

public class AlgebraVector extends Vector {
	
	int elem=0;
	
	public AlgebraVector() {
		super(1,1);
	}

	public AlgebraVector(int n) {
		super(n);
	}
	
	public AlgebraVector(int n, int m) {
		super(n,m);
	}
	
	public Algebra getSymbol(int i) {
		elem = i;
		return (Algebra) elementAt(i);
	}

	public Algebra getFirst() {
		elem = 0;
		return (Algebra) elementAt(0);
	}

	public Algebra getLast() {
		elem = size()-1;
		return (Algebra) elementAt(elem);
	}

	public Algebra getPrev() {
		if (--elem >= 0)
			return (Algebra) elementAt(elem);
		else {
			elem=0;
			return null;
		}
	}
	
	public Algebra getNext() {
		if (++elem < size())
			return (Algebra) elementAt(elem);
		else {
			elem=size()-1;
			return null;
		}
	}
	
}