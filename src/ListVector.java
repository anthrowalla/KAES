import java.util.*;
import java.lang.*;

public class ListVector extends Vector implements ToXML {
	int index=-1;
	Hashtable properties=null;
	public static String Eol = System.getProperty("line.separator");

	public void setTag(String t) {
		xmltag=t;
	}
	public String getTag() {
		return xmltag;
	}

	String xmltag=null;

	public ListVector() {
		super(10);
	}
	public ListVector(int i) {
		super(i);
	}
	public ListVector(int i, int j) {
		super(i,j);
	}

	public Object get() {
		if (index > -1 && index < size())
			return elementAt(index);
		else return null;
	}

	public Object prev() {
		if (index > 0 && index < size()+1) {
			index--;
			return get();
		}
		else return null;
	}


	public Object get(int i) {
		if (i > -1 && i < size()) {
			index = i;
			return get();
		}
		else return null;
	}

	public String toXML() {
		XMLBuffer sbuf = new XMLBuffer();
		String tag=null;

		if (getTag() != null) {
			tag = getTag();
		} else
			tag = getClass().getName();


		if (!tag.equals("")) sbuf.append("<"+tag);
		Enumeration ev;
		if (properties != null)
			if ((ev = properties.keys()).hasMoreElements()) {
				for(;ev.hasMoreElements();) {
					Object ek = ev.nextElement();
					sbuf.append(" "+ek+"=\""+properties.get(ek)+"\"");
				}
			}
				if (!tag.equals("")) sbuf.append(">"+Eol);
		sbuf.inc();
		for(reset();isNext();) {
			Object o = getNext();
			try {
				if (o.getClass().getMethod("toXML",null)!= null) {
					ToXML t = (ToXML) o;
					sbuf.append(t.toXML());
				} else {
					if (o.getClass().getMethod("getTag",null)!= null) {
						String q = (String) o.getClass().getMethod("getTag",null).invoke(o,null);
						sbuf.put("<"+q+">"+o.toString()+"</"+q+">"+Eol);
					} else  {
						String q = o.getClass().getName();
						sbuf.put("<"+q+">"+o.toString()+q+">"+Eol);
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				sbuf.put("<"+o.getClass().getName()+">"+o.toString()+
			 "</"+o.getClass().getName()+">"+Eol);
			}
		}
		sbuf.dec();
		if (!tag.equals("")) sbuf.put("</"+tag+">"+Eol);
		return sbuf.toString();
	}

	public String xxxtoXML() {
		StringBuffer sbuf = new StringBuffer(100);
		String tag = getClass().getName();
		sbuf.append(XMLIndent.getSpace()+"<"+tag+">"+XFile.Eol);
		XMLIndent.increment();
		for(reset();isNext();) {
			Object o = getNext();
			try {
				if (o.getClass().getMethod("toXML",null)!= null) {
					ToXML t = (ToXML) o;
					sbuf.append(t.toXML());
				} else {
					sbuf.append(XMLIndent.getSpace()+"<"+o.getClass().getName()+">"+o.toString()+
					"</"+o.getClass().getName()+">"+XFile.Eol);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
				sbuf.append(XMLIndent.getSpace()+"<"+o.getClass().getName()+">"+o.toString()+
					"</"+o.getClass().getName()+">"+XFile.Eol);
			}
		}
		XMLIndent.decrement();
		sbuf.append(XMLIndent.getSpace()+"</"+tag+">"+XFile.Eol);
		return sbuf.toString();
	}

	public void reset() {
		index = -1;
	}

	public boolean isNext() {
		return (index+1 < size());
	}

	public Object getNext() {
		if (isNext())
			return elementAt(++index);
		else return null;
	}

	public synchronized Object clone(boolean deep) {
/*		if (deep) {
			ListVector l = (ListVector) this.clone();
			for (l.reset();l.isNext();) {
				Object o = l.getNext();
				try {
					l.replace(o.clone(deep));
				} catch(Exception e) {
					l.replace(o.clone());
				}
			}
		}
		else */
		return this.clone();
	}

	public void replace(Object k) {
		setElementAt(k,index);
	}

	public void remove() {
		delete();
	}

	public void delete() {
		removeElementAt(index);
		index--;
	}

	public ListVector append(ListVector s) {
		for(s.reset();s.isNext();) {
			addElement(s.getNext());
		}
		return this;
	}

	public boolean addUnique(Object a) {
		if (indexOf(a) == -1) {
			addElement(a);
			return true;
		} else
			return false;
	}

	public ListVector appendUnique(ListVector s) {
		for(s.reset();s.isNext();) {
			addUnique(s.getNext());
		}
		return this;
	}

	public String toString2() {
	   StringBuffer sv = new StringBuffer();
	   for (reset();isNext();) {
		  sv.append(getNext().toString());
	   }
	   return sv.toString();
	}
	public String toStringSize() {
	   StringBuffer sv = new StringBuffer();
	   for (int i = 0;i < size();i++){
			sv.append(elementAt(i).toString());
	 //  for (reset();isNext();) {
		//  sv.append(getNext().toString());
	   }
	   return sv.toString();
	}

        public StringVector toStringVector() {
            StringVector sv = new StringVector(size());
            for (reset();isNext();) {
                sv.addElement(toString());
            }
            return sv;
        }
}

