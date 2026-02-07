import java.util.Vector;
/* history
* 9/30 DR added procedure reset1() since reset() skips first equation
*/

public class EquationVector extends Vector {
	
	int elem=0;
	
	public EquationVector() {
		super(1,1);
	}

	public EquationVector(int n) {
		super(n);
	}
	
	public EquationVector(int n, int m) {
		super(n,m);
	}
	
	public Equation getSymbol(int i) {
		elem = i;
		return (Equation) elementAt(elem);
	}

	public void reset() {
		elem = 0;
	}
	
	public void reset1() {
		elem = -1;
	}
	public boolean isNext() {
		if ((elem+1) < size()) return true;
		else return false;
	}
	
	public Equation getFirst() {
		elem = 0;
		return (Equation) elementAt(0);
	}

	public Equation getLast() {
		elem = size()-1;
		return (Equation) elementAt(elem);
	}

	public Equation getPrev() {
		if (--elem >= 0)
			return (Equation) elementAt(elem);
		else {
			elem=0;
			return null;
		}
	}
	
	public Equation getNext() {
		if (++elem < size())
			return (Equation) elementAt(elem);
		else {
			elem=size()-1;
			return null;
		}
	}
	
	public Object clone() {
		EquationVector v = (EquationVector) super.clone();
		v.elem = elem;
		return v;
	}
	/*
	public int indexOf(Equation e) {
		for(int i=0;i<this.size();i++) {
			if (getSymbol(i).equals(e)) return i;
		}
		return -1;
	}*/
		
}