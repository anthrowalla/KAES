import java.util.Vector;

public class KinTermVector extends Vector {
	
	int elem=0;
	
	public KinTermVector() {
		super(1,1);
	}

	public KinTermVector(int n) {
		super(n);
	}
	
	public KinTermVector(int n, int m) {
		super(n,m);
	}
	
	public KinTerm getKinTerm(int i) {
		elem = i;
		return (KinTerm) elementAt(elem);
	}

	public KinTerm getFirst() {
		elem = 0;
		return (KinTerm) elementAt(0);
	}

	public KinTerm getLast() {
		elem = size()-1;
		return (KinTerm) elementAt(elem);
	}

	public KinTerm getPrev() {
		if (--elem >= 0)
			return (KinTerm) elementAt(elem);
		else {
			elem=0;
			return null;
		}
	}
	
	public KinTerm getNext() {
		if (++elem < size())
			return (KinTerm) elementAt(elem);
		else {
			elem=size()-1;
			return null;
		}
	}
		
}