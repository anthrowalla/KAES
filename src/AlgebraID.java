public class AlgebraID extends Algebra{
	/** static list of all Algebras
	*/
	public static AlgebraVector algebras=new AlgebraVector();
	
	/** static procedure for getting the current algebra in a developmental sequence
	* @return the current stage
	*/
	public static Algebra getCurrent() {
		if (algebras.size() > 0) return algebras.getLast();
		else return null;
	}

	/** the identity equation. The equation may contain unbound variables
	*/
	AlgebraSymbolVar identity = null;  // default settings, unbound

	/** 
	*/
	public AlgebraKludge theKludge = new AlgebraKludge();

	/** reference name of this algebra
	*/
	String name="";
	
	/** initialise the algebra with a basic set of equations, xI = x and x0 = 0
	*/
	void defaultEquations() {
		identity = new AlgebraSymbolVar(false);	
		AlgebraSymbolVar alpha = new AlgebraSymbolVar(); // default settings, unbound
		addEquation(alpha,alpha).addLhs(identity); // xI = x

		addEquation(identity,alpha).addLhs(alpha); // Ix = x

		AlgebraSymbolVar beta = new AlgebraSymbolVar(); // default settings, unbound
		addEquation(beta,Algebra.getCurrent().getElement("0")).addLhs("0"); // x0 = 0
		addEquation(Algebra.getCurrent().getElement("0"),Algebra.getCurrent().getElement("0")).addLhs(beta); // 0x = 0
	}

	/** Initalise algebra by adding to the static list of algebra stages and setting up
	* basic unbound identity operator equations and null operator equations
	*/
	public AlgebraID() {
		algebras.addElement(this);
		defaultEquations();
	}
	
	/** Intialise with name in addition to adding to the static list of algebra stages\
	* and setting up basic unbound identity operator equations and null operator equations
	* @param name of algebra
	*/
	public AlgebraID(String n) {
		algebras.addElement(this);
		name = n;
		defaultEquations();
	}

	/** the symbols over which this algebra ranges
	*/
	AlgebraSymbolVector theSymbols = new AlgebraSymbolVector(5,1);

	/** Structural equations of algebra
	*/
	EquationVector structuralEquations  = new EquationVector(10,10);  // st_equatns
	
	/** focal elements of algebra if any
	*/
	AlgebraSymbolVector focalElements  = new AlgebraSymbolVector(1,1); // focTerm

	/** identity elements of algebra, if any
	*/
	AlgebraSymbolVector identityElements  = new AlgebraSymbolVector(1,1); // ident elements

	/** 
	*/
	AlgebraSymbolVector affinalElements  = new AlgebraSymbolVector(3,1); // affTerms
	
	/** elements that are made equivalent
	*/
	AlgebraSymbolVector equatedElements  = new AlgebraSymbolVector(5,1); // eqtId eqtFoc eqtElements

	/** things to unbind in unbind()
	*/
	AlgebraSymbolVector unbindSymbols  = new AlgebraSymbolVector(2,1);
	
	/** Rules of the algebra
	*/
	RuleVector rules = new RuleVector(6,1); // rules
	
	/** 
	*/
	int isoGenNum = 0;

	/** number of generators for this algebra
	*/
	int nGenerators=0;
	
	/** Set something to be unbound at end of evaluation cycle
	*/
	public void setUnBind(AlgebraSymbolVar a) {
		unbindSymbols.addToEnd(a);
	}
	
	/** unbinds bound variables after an evaluation cycle
	*/
	public void unbind() {
		for (int i = unbindSymbols.size()-1;i>=0;i--) ((AlgebraSymbolVar) unbindSymbols.elementAt(i)).unBind();
	}
	
	/** return the Algebra Symbols this algebra ranges over
	*/
	public AlgebraSymbolVector getElements() {
		return theSymbols;
	}
	
	/** return the focal elements of this Algebra, if any (empty vector means none)
	*/
	public AlgebraSymbolVector getFocalElements() {
		return focalElements;
	} 
	
	/** return the identy elemnts of this Algebra, if any  (empty vector means none)
	*/
	public AlgebraSymbolVector getIdentityElements() {
		return identityElements;
	}

	/** 
	*/
	public AlgebraSymbol findElement(AlgebraSymbol a) {
		Debug.prout("Finding!");
		int ndx = ((AlgebraSymbolVector)theSymbols).locateSymbol((AlgebraSymbol)a);
		if (ndx == -1) return null;
		Debug.prout("Found!");
		return (AlgebraSymbol) theSymbols.getSymbol(ndx);
	}

	/** 
	*/
	public AlgebraSymbol findElement(String a) {
		return findElement(new AlgebraSymbol(a));
	}

	/** find a symbol of the algebra given its name
	*/
	public AlgebraSymbol getElement(String a) {
		AlgebraSymbol n;
		if ((n = findElement(new AlgebraSymbol(a))) == null) {
			n = new AlgebraSymbol(a);
			theSymbols.addToEnd(n);
		}
		return n;
	}

	/** adds a generator to the list
	*/
	public void addGenerator(AlgebraSymbol a) {
		if (a.isGenerator()) return;
		a.setGenerator(++nGenerators);
	}

	/** returns the generators in a new list
	*/
	public AlgebraSymbolVector getGenerators() {
		AlgebraSymbolVector a = getElements();
		int sz = a.size();
		AlgebraSymbolVector v = new AlgebraSymbolVector(sz);
		for(int i=0;i<sz;i++) {
			if (a.getSymbol(i).isGenerator()) v.addToEnd(a.getSymbol(i));
		}
		return v;
	}

	/** returns the list of structural equations
	* @return the list of structural equations
	*/
	public EquationVector getEquations() {
		return structuralEquations;
	}

	/** binds the identity element  and adds to list of identity elements 
	* @param a new identity element
	* and updates a to be a focal element
	*/
	public void addIdentityElement(AlgebraSymbol a) {
		if (!a.isGenerator()) addGenerator(a);
		if (a.isIdentityElement()) return;
		identityElements.addToEnd(a);
		a.setIdentityElement();
//		a.setReciprocal(a);
		// might need to do a little checking here is already exists a different identity element
		addFocalElement(a);
		identity.setValue(a);
	}

	/** add focal element to list and updates a to be a focal element
	* @param a new focal element
	*/
	public void addFocalElement(AlgebraSymbol a) {
		if (a.isFocalElement()) return;
		focalElements.addToEnd(a);
		a.setFocalElement(true);
	}
	
	/** adds placeholder equation for this algebra
	*/
	public Equation addEquation() {
		Equation e = new Equation();
		structuralEquations.addElement(e); // possible problem for future hash???
		return e;
	}
	
	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as string of symbol names
	* @param right the RHS of equation as string of symbol names
	* @return the new equation
	*/
	public Equation addEquation(String left, String right) {
		return addEquation(makePath(left),makePath(right));
	}
	
	/** adds equation to list of structural equations for this algebra
	* @param left algebra symbol to be appended to the LHS of equation
	* @param right algebra symbol to be appended to the RHS of equation
	* @return the new equation
	*/
	public Equation addEquation(AlgebraSymbol left, AlgebraSymbol right) {
		Equation e = new Equation();
		e.addLhs(left);
		e.addRhs(right);
		structuralEquations.addElement(e);
		return e;
	}
	
	
	/** adds equation to list of structural equations for this algebra
	* @param left the LHS of equation as list of algebra symbols to be LHS
	* @param right the RHS of equation as list of algebra symbols to be RHS
	* @return the new equation
	*/
	public Equation addEquation(AlgebraSymbolVector left, AlgebraSymbolVector right) {
		Equation e = new Equation();
		e.setLhs(left);
		e.setRhs(right);
		structuralEquations.addElement(e);
		return e;
	}
	
	public AlgebraSymbolVector makePath(String spath) {
		AlgebraSymbolVector path = new AlgebraSymbolVector(2,2);
		int sp = 0; int ndx = 0;
		spath = spath.trim();
		while ((ndx = spath.indexOf(" ",sp)) != -1) {
			String r = spath.substring(sp,ndx);
			sp = ndx+1;
			path.addToBeginning(getElement(r));
		}
		if (sp < spath.length()) path.addToBeginning(getElement(spath.substring(sp)));
		return path;
	}

}

/*

		s_equiv,          {structurally equivalent pairs of terms}
		splitPtr				{pointer for list of terms to be split}
																: intPointer;
		nPtr,					{number of lists of terms}
		unifyB            {flags if compounds are unified}
	*/