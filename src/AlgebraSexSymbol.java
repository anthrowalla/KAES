public class AlgebraSexSymbol extends AlgebraSymbol {
	// override clone if add any state info
	// specialised subclassing of AlgebraSymbol
	/** define a new AlgebraSymbol
	* @param s the value to assign to the AlgebraSymbol
	* @return the new AlgebraSymbol
	*/
	public AlgebraSexSymbol(String s) {
		theSymbol = s;
	}
	/** define a new AlgebraSymbol
	* @return the new AlgebraSymbol
	*/
	public AlgebraSexSymbol() {
	}

	public AlgebraSymbolVector getProduct(AlgebraSymbolVector a) {
	//	AlgebraSymbolVector sp = Algebra.getCurrent().getSpouseArrows();
	//	return super.getProduct(a);
		// System.out.println("I'm a sex symbol!");
		AlgebraSymbolVector sp = Algebra.getCurrent().getSpouseArrows();
	//	System.out.println("AlgebraSexSymbol.getProduct: sp= "+sp);
		for (int i = 0;i< sp.size();i++) {
			AlgebraSymbol as = sp.getSymbol(i);
			AlgebraPath p = new AlgebraPath((AlgebraSymbolVector) a.clone(false),true);
			if (!p.product(as)) {
				AlgebraSymbolVector e = Algebra.getCurrent().theKludge.reciprocal(a);
				AlgebraPath pr = new AlgebraPath(e,true);
	//		System.out.println("AlgebraSexSymbol.getProduct: a= "+a+" pr= "+pr);
				if (pr.product(as)) {
					a.addToEnd(this);
					return a;
				}
			} else {
	//			System.out.println("AlgebraSexSymbol.getProduct (true) addingot: a= "+a+" symbol="+this);
				a.addToEnd(this);
				return a;
			}
		}
		//a.addToEnd(Algebra.getCurrent().getElement("I"));

		return a;
	}
}