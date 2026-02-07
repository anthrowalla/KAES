public class VariablesFactory implements ToXML { 
	 StringVector varnames = new StringVector();
	 ListVector variablePrototypes = new ListVector();
	
	 public void defineVariable(String name) {
		if (varnames.indexOf(name) != -1) return;
		varnames.addElement(name);
		Variable q = new Variable(name);
		q.values = new ListVector();
		variablePrototypes.addElement(q);
	}
	
	 public Variable newVariableInstance(String name) {
	 	return newVariableInstance(name,null);
	 }

	 public Variable newVariableInstance(String name, Object value) {
		int ndx;
		if ((ndx = varnames.indexOf(name)) != -1) {
			Variable a = (Variable)((Variable)variablePrototypes.elementAt(ndx)).makeInstance(name);
			if (value != null) a.addElement(value);
			return a;
		} else {
			System.out.println("Variables.newVariableInstance: undefined variable name="+name);
			return null;
		}
	} 

	public Variables newVariableSet() {
		Variables a = new Variables();
		a.masterRecord = this;
		a.removeAllElements();
		return a;
	} 
	
	public String toXML() {
		StringBuffer xml = new StringBuffer(100);
		xml.append( XMLIndent.space+"<VariablesFactory>"+XFile.Eol);
		XMLIndent.increment(); 
		xml.append(XMLIndent.pp("<Variables>"));
		XMLIndent.increment();
		for (variablePrototypes.reset();variablePrototypes.isNext();) {
			xml.append(((Variable)variablePrototypes.getNext()).toXML(true));
		}
		XMLIndent.decrement();
		xml.append(XMLIndent.pp("</Variables>"));
		XMLIndent.decrement();
		xml.append(XMLIndent.pp("</VariablesFactory>"));
		return xml.toString();
	}
}
