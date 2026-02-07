public abstract class IdentityEquation extends Equation {
	boolean complete=false;
	
	public IdentityEquation() {
		// in subclasses initialise rhs and lsh with known symbols
	}
	
	public void addIdentityElement(AlgebraSymbol i) {
		// insert into lhs and rhs as needed;
		complete=true;
	}
	
}