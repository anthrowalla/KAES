import java.util.Vector;
import java.lang.*;

public class StringVector extends Vector {
	int index=-1;
	
	public StringVector() {
		super(10);
	}
	public StringVector(int i) {
		super(i);
	}
	public StringVector(int i, int j) {
		super(i,j);
	}
	
	public String getSymbol(int i){
		return (String) elementAt(i);
	}
	public void put(int i, String s) {
		if (size() < i+1) setSize(i+10);
		setElementAt(s,i);
	}
	public void add(String s) {
		addElement(s);
	}
	public String toXML() {
		StringBuffer sbuf = new StringBuffer(100);
		sbuf.append("<Strings>"+XFile.Eol);
		for(reset();isNext();) {
			sbuf.append("    <String>"+getNext().toString()+"</String>"+XFile.Eol);
		}
		sbuf.append("</Strings>"+XFile.Eol);
		return sbuf.toString();
	}
	
	public void reset() {
		index = -1;
	}
	
	public boolean isNext() {
		return (index+1 < size());
	}
	
	public String getNext() {
		if (isNext())
			return (String) elementAt(++index);
		else return null;
	}	

	public synchronized Object clone(boolean deep) {
		return this.clone();
	}
	
	public void replace(String k) {
		setElementAt(k,index);
	}
	
	public void delete() {
		removeElementAt(index);
		index--;
	}

	public StringVector append(StringVector s) {
		for(s.reset();s.isNext();) {
			addElement(s.getNext());
		}
		return this;
	}
}