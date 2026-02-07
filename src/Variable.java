
/** Class Variable can have many values. The internal routine getValue will
return a single value, or "N" when it has multiple values. If you need all the values, they are simply the elements of Variable retrieved using the ordinary mechanisms for a ListVector
**/
public class Variable extends ListVector implements ToXML {
	String name="";
	ListVector values=null;
	
	public Variable(String name) {
		this.name = name;
	}

	public Variable makeInstance (String name) {
		Variable a = (Variable) clone();
		a.name = name;
		a.values = this.values;
		a.removeAllElements();
		return a;
	}

	public void setValue(Object xvalue) {
		removeAllElements();
		addValue(xvalue);
	}
	
	public void addValue(Object xvalue) {
		if (xvalue.equals("N")) {
			removeAllElements();
			addElement("N");
			return;
		}
		if (size() == 1 && elementAt(0).equals("N")) {
			if (values.indexOf(xvalue) == -1) values.addElement(xvalue);
			return;
		}
		if (indexOf(xvalue) == -1) addElement(xvalue);
		if (values.indexOf(xvalue) == -1) values.addElement(xvalue);
	}
	
	public boolean equals(Object v) {
		Variable p = (Variable) v;
		if (getValue().equals(p.getValue())) return true;
		if (getValue() == "*" || p.getValue() == "*") return true;
		if (getValue() == "N" || p.getValue() == "N") return true;
		
		return false;
	}
	
	public String getValue() {
		if (size() > 1) return "N";
		else if (size() == 0) return "*";
		else return elementAt(0).toString();
	}

	public String toXML() {
		return toXML(false);
	}
	public String toXML(boolean t) {
		StringBuffer xml = new StringBuffer(100);
		xml.append(XMLIndent.pp("<Variable Name="+'"'+name+'"'+">"));
		XMLIndent.increment();
		if (t) {
			xml.append(XMLIndent.pp("<VariableValueProtos>"));
			XMLIndent.increment();
			for(values.reset();values.isNext();) {
				xml.append(XMLIndent.pp("<VariableValueProto Value="+'"'+values.getNext().toString()+'"'+"/>"));
			}
			XMLIndent.decrement();
			xml.append(XMLIndent.pp("</VariableValueProtos>"));
		}
		xml.append(XMLIndent.pp("<VariableValues>"));
		XMLIndent.increment();
		if (size() == 0) xml.append(XMLIndent.pp("<VariableValue Value="+'"'+"*"+'"'+"/>"));
		else for(reset();isNext();) {
			xml.append(XMLIndent.pp("<VariableValue Value="+'"'+getNext().toString()+'"'+"/>"));
		}
		XMLIndent.decrement();
		xml.append(XMLIndent.pp("</VariableValues>"));
		XMLIndent.decrement();
		xml.append(XMLIndent.space+"</Variable>"+XFile.Eol);
		return xml.toString();
	}
}