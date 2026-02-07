

public class AlgebraGenerator implements ToXML {
	AlgebraSymbol symbol;
		
//	Hashtable generators = new Hashtable(32);
//	Hashtable elementGenerators = new Hashtable(32);
	

	public AlgebraGenerator(AlgebraSymbol a) {
		symbol = a;
	}

	public String toXML() {
		return "<AlgebraGenerator symbol=\""+symbol+"\"/>";
	}
	/*	
	public AlgebraGenerator newGenerator(String element, int rel) {
		AlgebraGenerator a = new AlgebraGenerator(element,rel);
		a.generators = generators;
		a.elementGenerators = elementGenerators;
		elementGenerators.put(element,a);
		return a;
	}

	public AlgebraGenerator getGenerator(String element) {
		return (AlgebraGenerator) elementGenerators.get(element);
	}

	public int getArrow(String element) {
		return ((AlgebraGenerator) elementGenerators.get(element)).getArrow();
	}
	
	public int getArrow() {
		return arrow;
	}
	
	public void addGeneratorProduct(AlgebraGenerator a,String product) {
		generators.put(a,product);
	}
	
	public String getProduct(AlgebraGenerator a) {
		return (String) generators.get(a);
	}
*/
	public AlgebraSymbolVector getProduct(AlgebraSymbolVector a) {
		a.addToEnd(symbol);
		return a;
	}
}
