
public class GlobalEquationList {
	static EquationVector equations = null;
	
	static void setEquations(EquationVector e) {
		equations = e;
	}

	static EquationVector getEquations() {
		return equations;
	}
	
}