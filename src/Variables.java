
public class Variables extends ListVector implements ToXML {
	VariablesFactory masterRecord=null;
	
	public String getVarValue(String varname) {
		int ndx;
		normaliseSize();
		if ((ndx = masterRecord.varnames.indexOf(varname)) != -1) {
			String s = ((Variable) elementAt(ndx)).getValue();
			if (s == "*") return "N";
			else return s;
		} else {
			System.out.println("Variables.setVarValue: unknown variable name="+varname);
			return "N";
		}
		
	}
	
	public Object clone(boolean deep) {
		if (deep) {
			Variables v = (Variables) super.clone(deep);
			v.masterRecord = this.masterRecord;
			return v;
		} else return clone();
	}
	
	public Object clone() {
		Variables v = (Variables) super.clone();
		v.masterRecord = this.masterRecord;
		return v;
	}
	
	public String getRareVarValue(String varname) {
		int ndx;
		normaliseSize();
		if ((ndx = masterRecord.varnames.indexOf(varname)) != -1) {
			return ((Variable) elementAt(ndx)).getValue();
			
		} else {
			System.out.println("Variables.setVarValue: unknown variable name="+varname);
			return "N";
		}
		
	}

	public ListVector getLiteralVarValue(String varname) {
		int ndx;
		normaliseSize();
		if ((ndx = masterRecord.varnames.indexOf(varname)) != -1) {
			Variable v = ((Variable) elementAt(ndx));
			if (v.size() != 0 && v.elementAt(0).equals("N")) {
				v.removeAllElements();
				for(v.values.reset();v.values.isNext();) {
					v.addElement(v.values.getNext());
				}
			}
			return v;
		} else {
			System.out.println("Variables.setVarValue: unknown variable name="+varname);
			return null;
		}
		
	}

	public void addVarValue(String varname, Object value) {
		int ndx;
		normaliseSize();
		if ((ndx = masterRecord.varnames.indexOf(varname)) != -1) {
			((Variable) elementAt(ndx)).addValue(value);
		} else {
			System.out.println("Variables: request for undefined variable="+varname);
		}
	}


	public void setVarValue(String varname, Object value) {
		int ndx;
		normaliseSize();
		if ((ndx = masterRecord.varnames.indexOf(varname)) != -1) {
			((Variable) elementAt(ndx)).setValue(value);
		} else {
			System.out.println("Variables: request for undefined variable="+varname);
		}
	}
	
	public void normaliseSize() {
		if (masterRecord.varnames.size() > size()) {
			for(int i=size();i<masterRecord.varnames.size();i++) {
				addElement(masterRecord.newVariableInstance(masterRecord.varnames.getSymbol(i)));
			}
		}
	}
	
	public boolean equals(Variables x) {
		int ndx;
		normaliseSize();
		x.normaliseSize();
		for(int i = 0; i< masterRecord.varnames.size();i++) {
			if (!((Variable) elementAt(i)).equals(((Variable) x.elementAt(i)))) return false;
		}
		return true;
	}	
	
	public String toXML() {
		StringBuffer xml = new StringBuffer(100);
		xml.append(XMLIndent.pp("<Variables>"));
		XMLIndent.increment();
		for(reset();isNext();) {
			xml.append(XMLIndent.pp(((Variable)getNext()).toXML()));
		}
		XMLIndent.decrement();
		xml.append(XMLIndent.pp("</Variables>"));
		return xml.toString();
	}
}


