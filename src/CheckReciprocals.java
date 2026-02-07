public class CheckReciprocals extends AlgebraCheck {

	public CheckReciprocals(Algebra a) {
		super(a);
	}
	
	public boolean isTrue() {
		AlgebraSymbolVector as = theAlgebra.getElements();
		int sz = as.size();
		for(int i=0;i<sz;i++) {
			AlgebraSymbol a = as.getSymbol(i);
			if (a.getReciprocal() == null) {
				getReciprocal(a);
			}
		}
		return false;
	}
		
	public void getReciprocal(AlgebraSymbol a) {
		AlgebraSymbolVector v = theAlgebra.getGenerators();
		
	}
}
