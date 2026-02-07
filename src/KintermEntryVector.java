import java.util.*;

public class KintermEntryVector extends java.util.Vector {
	
	int elem=0;
	
	public KintermEntryVector() {
		super(1,1);
	}

	public KintermEntryVector(int n) {
		super(n);
	}
	//
	public KintermEntryVector(int n, int m) {
		super(n,m);
	}
	
	public KintermEntry getSymbol(int i) {
		elem = i;
		return (KintermEntry) elementAt(elem);
	}

}