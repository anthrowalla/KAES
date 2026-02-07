import java.util.*;

public class ListHashtable extends Hashtable {
		
	public ListVector putInList(Object key, Object a) {
		ListVector r = (ListVector) get(key);
		if (r == null) {
			r = new ListVector();
			super.put(key,r);
		}
		r.addElement(a);
		return r;
	}
	
	public ListVector getList(Object key) {
		return (ListVector) get(key);
	}
	
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("[");
		for (Enumeration e=keys();e.hasMoreElements();) {
			Object keyx = e.nextElement();
			ret.append("Key="+keyx.toString()+" contents="+get(keyx).toString()+"\n");
		}
		ret.append("]");
		return ret.toString();
	}
}