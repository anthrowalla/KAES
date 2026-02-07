import java.util.*;
/* History
* 2/18 DR added "if (generators.size() == 0)" to "current" procedure
* 3/11 DR added procedure  getPathSex()
* 9/30 DR added procedure isMixedSexSameArrowEquation(Equation e) to procedure reduce()
* to set flag when encountering equation of form Pp = PP, where P and p have the
* same direction but opposite sex; in this case, only want to substitute PP for Pp and
* not the reverse
* 11/11 DR modified count to take into account merging together alg elements in Shipibo
* this may be changed, depending on how equivalent terms are labelled
* 11/12 DR added procedures getEquivalentPathLeft,getEquivalentPathRight,
* equivalentPath to handle mapping structual equivalent pair X,Y to path [X,&,Y]
* 11/14 DR added procedure makeEquivalentPath, pathProduct
* 9/1 DR added procedure algPathToVector() to put equivalent algebra elements into
* vector form
* 2/9 DR added check in reduce for equation of form XY = XX; equation does not apply when
* reducing XYW and W and Y have different directions (e.g. SDP != SSP)
* 2/9 DR added Hashtable ht to buildtable for algebraSymbol and arrowType to speed up reduce() due to
* above check; otherwise, repeatedly compute arrowType for algebraSymbol
* 2/9 build Hashtable ht is in procedure nextPath, reducePath(AlgebraSymbolVector path)
* 6/8/09 DR added boolean flag for identifying when equation is skipped in a path reduction; uses NONINVERTIBLE
*/

/** needs generators set using addGenerator, Equations via addEquation
Generates a path segment relative to the focal element used to initialize.
*/
public class AlgebraPath {
    AlgebraSymbolVector path = new AlgebraSymbolVector(10,1);
    AlgebraSymbolVector reducedPath = new AlgebraSymbolVector(10,1);

	Hashtable ht = new Hashtable();
    int pp = 0; // path pointer ... path elements can be more than one character .. make into StringVector
    EquationVector equations = Algebra.getCurrent().getEquations();
    // EquationVector equations = null;
    AlgebraSymbolVector generators = Algebra.getCurrent().getGenerators();
    // AlgebraSymbolVector generators = null;

    int [] stack = new int[320];
    int sp = 0; // stack pointer
    Stack reduced = new Stack();

    AlgebraSymbol focalElement = null;
    AlgebraSymbol identityElement = null;

    public void setFocalElement(){
	    focalElement=Algebra.getCurrent().getFocalElements().getSymbol(0);
    }

    public void setIdentityElement(){
	    identityElement = Algebra.getCurrent().getIdentityElement();
    }

    public boolean equals(Object a) {
	    //System.out.println("         this="+this.toString()+"    a="+a.toString());
	    return toString().equals(((AlgebraPath) a).toString());
    }
    public boolean equalsFull(AlgebraPath a) {
	   //System.out.println("         this="+this.toString()+"    a="+a.toString());
	   AlgebraSymbolVector a1 = a.path;
	   AlgebraSymbolVector a2 = path;
	   return a1.toString().equals(a2.toString());
    }

    public String toString() {
	    StringBuffer ret=new StringBuffer(20);
	    for(int i=0;i<reducedPath.size();i++) ret.append(((AlgebraSymbol)reducedPath.elementAt(i)).getValue());
	    return ret.toString();
    }

    public String toString1() {
	    StringBuffer ret=new StringBuffer(20);
	    for(int i=0;i<path.size();i++) ret.append(((AlgebraSymbol)path.elementAt(i)).getValue());
	    return ret.toString();
    }


    public Object clone() {
	    AlgebraPath a = new AlgebraPath((AlgebraSymbolVector) path.clone(false),true);
	    a.focalElement = focalElement;
	    a.identityElement = identityElement;
	    a.generators = generators;
	    a.equations = equations;
	    return a;
    }
    public Object clone1() {
	    AlgebraPath a = new AlgebraPath((AlgebraSymbolVector) path.clone(false),(AlgebraSymbolVector)reducedPath.clone(false));
	    a.focalElement = focalElement;
	    a.identityElement = identityElement;
	    a.generators = generators;
	    a.equations = equations;
	    return a;
    }

    public int countSymbol(String s) {
	    int count = 0;
	    for (int i=0;i<reducedPath.size();i++) {
		    if (reducedPath.getSymbol(i).getValue().equals(s)) count++;
	    }
	    return count;

    }
    /** probably should reduce thePath before pushing on stacks
    */
    public AlgebraPath(AlgebraSymbolVector thePath,boolean reduce) {
	    path = thePath;
		if (reduce){
			stack[sp = 0] = 0;
			reduced.push(thePath.clone());
			reducePath(path);
		} else reducedPath = thePath;
    }

	public AlgebraPath(AlgebraSymbolVector thePath,AlgebraSymbolVector theReducedPath){
		path = thePath;
		reducedPath=theReducedPath;
	}
    public AlgebraPath(AlgebraSymbol focalE) {
	    //setFocalElement(focalE);
	    if (focalE == null) throw new AlgebraException("AlgebraPath: null AlgebraSymbol");
	    path.removeAllElements(); // seems a bit paranoid
	    path.addToEnd(focalE);
	    stack[sp = 0] = 0;
	    AlgebraSymbolVector k = new AlgebraSymbolVector(2,2);
	    k.addToEnd(focalE);
	    reduced.push(k);
	    reducedPath.addElement(focalE);
    }

    public AlgebraPath(AlgebraSymbol a, AlgebraSymbol b) {
	    this(b);
	    product(a);
    }

    public AlgebraPath(AlgebraSymbol a, AlgebraSymbol b, AlgebraSymbol c) {
	    this(c);
	    product(b);
	    product(a);
    }

    public AlgebraPath() {
	    //AlgebraSymbol focalE = Algebra.getCurrent().getFocalElements().getSymbol(0);
	    //setFocalElement(focalE);
	    path.removeAllElements(); // paranoid again??
	    //path.addToEnd(focalE);
	    stack[sp = 0] = 0;
	    AlgebraSymbolVector k = new AlgebraSymbolVector(2,2);
	    //k.addToEnd(focalE);
	    reduced.push(k);
    //	reducePath(path);
    }


    void fail() {
	    if (path.size() > 0) {
			    path.removeEnd();
	    }
	    ++stack[sp];
	    Debug.prout("NGen "+generators.size());
	    while (sp >= 0 && stack[sp] >= generators.size()) {
		    sp--;
		    if (!reduced.empty()) reduced.pop();
		    if (path.size() > 0) {
			    path.removeEnd();
		    }
	    }
	    nextPath();
    }

    void succeed() {
	    stack[sp]++;
	    stack[++sp] = 0;
	    reduced.push(reducedPath.clone());
	    nextPath();

    }

/*	public void applyRules(RuleVector someRules, AlgebraPath somePath) {
		for(someRules.reset();someRules.isNext();) {
			Rule rule = (Rule) someRules.getNext();
			if (!rule.getActiveRule()) continue;
System.out.println("RULE "+rule+" soomepatbh "+somePath);
			if (rule.doesRuleApply(somePath)) {
				rule.applyRule(somePath);
				System.out.println("did somepath "+somePath);
			}
		}
	}*/

//boolean flag = true;//used to control print statements only
	
	boolean pathFlag = false;//flag used to signal equation to be skipped in reduction
    public boolean product(AlgebraSymbol a) { //  main routine to call in AlgebraPath ... generates next path segment
    //	if (a == null) throw new Error("AlgebraPath: null AlgebraSymbol");
		

		
		for(int i=0;i< equations.size();i++) {
			Equation e = (Equation) equations.elementAt(i);

			if ((e.getEqType() == Equation.NONINVERTIBLE) &&(e.getLhs().equals(path))) {
				pathFlag = true;System.out.println("555555555555555555555555555 lhs "+e.getLhs()+ " path "+path);
			}
		}
		
		
	boolean flag = false;
	//flag = (path.size() == 2 && a.isFocalElement());
	    if (a == null) return false;
	if (flag) System.out.println("XXXXXXXXXXBEFORE a "+a+" path "+path+" this "+this);
	    a.getProduct(path);
		if (flag) System.out.println("XXXXXXXXXX a "+a+" path "+path+" this "+this);
    //	Debug.prout(2,"product_ path= "+path);
	//if (path.toString().equals("[A, &, E]")) System.out.println(" In product a "+a+" path "+path);
	    reducePath(path);//in here somewhere
	    Debug.prout("Path: "+path.toString()+" Reduced "+ reducedPath.toString());
	//if (path.toString().equals("[A, &, E]")) System.out.println(" path "+path.toString()+" reduced "+ reducedPath.toString());
//applyRules(Algebra.getCurrent().getRules(),this);
	    //return (!(reducedPath.size() == 0)); // empty path indicates non-kin
		//if ( equals(new AlgebraPath(Algebra.getCurrent().getElement("0")))) System.out.println(" a "+a+" this.path "+this.path+" red "+this.getProductPath()+" this.red "+this.getReducedProductPath());
	    return (!(reducedPath.size() == 0 || equals(new AlgebraPath(Algebra.getCurrent().getElement("0"))))); // empty path indicates non-kin
    }

    boolean nextPath() { //  main routine to call in AlgebraPath ... generates next path segment
		if (ht == null){
		for (generators.reset();generators.isNext();){
			AlgebraSymbol as = generators.getNext();
			ht.put(as.toString(),new Integer(as.getArrowType()));
		}
		}
	    AlgebraSymbol a = current();
    //	if (a == null) throw new Error("AlgebraPath: null AlgebraSymbol");
	    if (a == null) return false;
	    a.getProduct(path);
	    reducedPath = (AlgebraSymbolVector) ((AlgebraSymbolVector) reduced.peek()).clone(false);
	    a.getProduct(reducedPath);
	    Debug.prout("Clone "+reducedPath.toString());
	    if (reduce(ht)) {
	    //	fail();
	    //	return(nextPath());
		    /*if (!reduce()) {
			    fail();
			    return(nextPath());
		    }*/
	    //} else {
	    //	succeed();
	    }
	    Debug.prout("Path: "+path.toString()+" Reduced "+ reducedPath.toString());
	    return reducedPath.size() != 0; // empty path indicates non-kin
    }

    public void addGenerators(AlgebraSymbolVector x) {
	    generators = x;
    }

    public void addEquations(EquationVector e) {
	    equations = e;
    }

    AlgebraSymbol current() {
	    if (sp < 0) return null;
    if (generators.size() == 0) return null;
	    return (AlgebraSymbol)  generators.getSymbol(stack[sp]);
    }

    void setFocalElement(AlgebraSymbol i) {
	    focalElement = i;
	    path.removeAllElements();
	    path.addToEnd(i);
	    while (!reduced.empty()) reduced.pop();
	    AlgebraSymbolVector k = new AlgebraSymbolVector(2,2);
	    k.addToEnd(i);
	    reduced.push(k);
    }

    void setIdentityElement(AlgebraSymbol i) {
	    identityElement = i;
    }

    public boolean reducePath(AlgebraSymbolVector path) { //  . generates reduction of path segment
		for(int i=0;i< equations.size();i++) {//ensures eq ab=cd only reduces product ab to cd and not cd to ab
			Equation e = (Equation) equations.elementAt(i);
			if ((e.getEqType() == Equation.NONINVERTIBLE) &&(e.getLhs().equals(path))) {
				pathFlag = true; //flag for skipping equation in reduction when path matches left side of the equation to be skipped
			}
			//if (e.getLhs().size() == e.getRhs().size() && e.getLhs().size() == path.size()) continue;
			if (e.getLhs().size() != e.getRhs().size() || e.getLhs().size() != path.size()) continue;//skip reduction equations	
				//if (e.getLhs().size() != path.size()) continue;
			
			if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) continue;//skip equivalence equations
		

			if (e.getEqType() == Equation.EQUIVALENCE ||
				e.getEqType() == (Equation.EQUIVALENCE+Equation.NONINVERTIBLE)) {
			System.out.println("equation skipo "+e);continue;}//skip equivalence equations
					//System.out.println(" Left e.getLhs() "+e.getLhs()+" right "+e.getRhs()+" path "+path);
				if (e.getLhs().equals(path)) {
					AlgebraSymbolVector sv = (AlgebraSymbolVector)e.getRhs().clone();
					reducePath(sv);
					reducedPath = sv; 
					//System.out.println(" made change e.getLhs() "+e.getLhs()+" right "+reducedPath);
					return true;					
				}
		}
					
		if (ht == null){
			for (generators.reset();generators.isNext();){
				AlgebraSymbol as = generators.getNext();
				ht.put(as.toString(),new Integer(as.getArrowType()));
			}
		}
		this.path = path;
		if (!path.equivalentProduct()) {
			reducedPath = new AlgebraSymbolVector(2,2);
			return _reducePath(path,reducedPath);
		}
		AlgebraSymbolVector pathL = path.equivalentLeftProduct();
		AlgebraSymbolVector pathR = path.equivalentRightProduct();
		//System.out.println(" pathL "+pathL+" pathR "+pathR);
		AlgebraSymbolVector reducedPathL = new AlgebraSymbolVector(2,2);
		AlgebraSymbolVector reducedPathR = new AlgebraSymbolVector(2,2);
		boolean ret = true;
		if ((ret = _reducePath(pathL,reducedPathL))) {
			if ((ret = _reducePath(pathR,reducedPathR))) {
				reducedPath = new AlgebraSymbolVector(2,2);
				for (reducedPathR.reset();reducedPathR.isNext();){
					reducedPath.addToEnd(reducedPathR.getNext());
				}
				reducedPath.addToEnd(Algebra.getCurrent().getElement("&"));
				for (reducedPathL.reset();reducedPathL.isNext();){
					reducedPath.addToEnd(reducedPathL.getNext());
				}
				Debug.prout(0," path "+path+" reduced path "+reducedPath);
			}
		}
	    return ret;
    }

	boolean _reducePath(AlgebraSymbolVector aPath,AlgebraSymbolVector aReducedPath){
	    boolean ret = true;
	    for (int i = 0;i< aPath.size();i++) {
		    AlgebraSymbol a = aPath.getSymbol(i);
		    a.getProduct(aReducedPath);
		    reducedPath = aReducedPath;
		    if (reduce(ht)) {
			    if (!reduce(ht)) {
			    }
		    }
			aReducedPath = reducedPath;
		    if (aReducedPath.size() == 0) {
			    // mindful changing to accommodate "0" in path
			    // reducedPath.addElement(Algebra.getCurrent().getElement("0"));
			     ret = false;
			     break;
		    }
	    }
		return ret;
	}

/*    boolean _reducePath(AlgebraSymbolVector path) { //  . generates reduction of path segment
	    boolean ret = true;
	    reducedPath = new AlgebraSymbolVector(2,2);
	    for (int i = 0;i< path.size();i++) {
		    AlgebraSymbol a = path.getSymbol(i);
		    a.getProduct(reducedPath);
		    if (reduce(ht)) {
			    if (!reduce(ht)) {
			    }
		    }
		    if (reducedPath.size() == 0) {
			    // mindful changing to accommodate "0" in path
			    // reducedPath.addElement(Algebra.getCurrent().getElement("0"));
			     ret = false;
			     break;
		    }
	    }
	    return ret;
    }*/

    public boolean reducePath(String spath) { //  . generates reduction of path segment
    //	AlgebraSystemVector x = reducedPath;//procedure does not appear to be used

	    path = new AlgebraSymbolVector(2,2);
	    int sp = 0; int ndx = 0;

	    while ((ndx = spath.indexOf(" ",sp)) != -1) {
		    String r = spath.substring(sp,ndx);
		    sp = ndx+1;
		    path.addToBeginning(Algebra.getCurrent().getElement(r));
	    }
	    if (sp < spath.length()) path.addToBeginning(Algebra.getCurrent().getElement(spath.substring(sp)));

	    return reducePath(path);
    }
//reducePathLR does not use the check added to reducePath for equations of form XY = XX;
//not clear if the check is needed here or not; has to do with non-associativity

    public boolean reducePathLR(AlgebraSymbolVector path) { //  . generates reduction of path segment
    //	AlgebraSystemVector x = reducedPath;

	    reducedPath = new AlgebraSymbolVector(2,2);
	    for (int i = path.size()-1;i >= 0;i--) {
		    AlgebraSymbol a = path.getSymbol(i);
		    a.getProductLR(reducedPath);
		    if (reduceLR()) {
			    if (!reduceLR()) {
			    }
		    }
		    if (reducedPath.size() == 0) break;
	    }
	    return reducedPath.size() != 0; // empty path indicates non-kin
    }

    public boolean reducePathLR(String spath) { //  . generates reduction of path segment
    //	AlgebraSystemVector x = reducedPath;

	    path = new AlgebraSymbolVector(2,2);
	    int sp = 0; int ndx = 0;
	    spath = spath.trim();
	    while ((ndx = spath.indexOf(" ",sp)) != -1) {
		    String r = spath.substring(sp,ndx);
		    sp = ndx+1;
		    path.addToEnd(Algebra.getCurrent().getElement(r));
	    }
	    if (sp < spath.length()) path.addToEnd(Algebra.getCurrent().getElement(spath.substring(sp)));

	    return reducePathLR(path);
    }

    AlgebraSymbolVector getProductPath() {
	    return path;
    }

	  void setProductPath(AlgebraSymbolVector asv){
		path = asv;
	 }

    AlgebraSymbolVector getReducedProductPath() {
	    return reducedPath;
    }

	 void setReducedProductPath(AlgebraSymbolVector asv){
		reducedPath = asv;
	 }

	boolean isMixedSexSameArrowEquation(Equation e){
		//System.out.println("equatoin "+e);
		if (e.getLhs().size() == 2){
			return (!e.getLhs().sameSex() && e.getRhs().sameElements());
		}
		return false;
	}

	boolean reduce(Hashtable ht) {
		return reduce(new AlgebraSymbolStack(),ht);
	}


    boolean reduce(AlgebraSymbolStack stk,Hashtable ht) {
	    int i;
	    boolean apply=false;
	    boolean doneEqual = false, doneEqual2 = false;
	    for(;;) {
		    int plen = reducedPath.size();
		    apply = false;

		    Algebra.getCurrent().unbind(); // unbind variables

		    for(i=0;i< equations.size();i++) {
			    Equation e = (Equation) equations.elementAt(i);
				if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) {continue;}//skip equation

				 if (e.getEqType() == Equation.EQUIVALENCE ||
				 e.getEqType() == (Equation.EQUIVALENCE+Equation.NONINVERTIBLE)) continue;//skip equivalence equations
			    if ((e.getLhs().size() != e.getRhs().size())) {
				    if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
					    if (e.getRhs().getFirst().equals(Algebra.getCurrent().getElement("0")))  {
						    reducedPath.removeAllElements();
						    apply = false;
						    break;
					    }
					    apply = true;
					    doneEqual = false;
					    stk.popSymbol().getProduct(reducedPath);
				    }
			    }
		    }

		    boolean lenchanged = !(plen == reducedPath.size());
		    if (lenchanged) continue;
/*			if (!stk.empty()) {
			    stk.popSymbol().getProduct(reducedPath);
			    continue;
		    }*/
		    apply = false; // prelim

		    Algebra.getCurrent().unbind(); // unbind variables
		    if (doneEqual == false && reducedPath.size() > 1) {
			    //if (!doneEqual)
			    for(i=0;i< equations.size();i++) {
				    Equation e = (Equation) equations.elementAt(i);
					if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) {continue;}//skip equation
					
					if (e.getEqType() == Equation.EQUIVALENCE ||
					e.getEqType() == Equation.EQUIVALENCE+Equation.NONINVERTIBLE) continue;//skip equivalence equations
				    if (e.getLhs().size() > 1 && e.getLhs().size() == e.getRhs().size()) {
						//boolean flag = isMixedSexSameArrowEquation(e);flag = true;
					//if (flag) System.out.println("AAAAAAAAAAAAAAAAAAAAAA lhs "+e.getLhs()+" rhs "+e.getRhs());
						if (isMixedSexSameArrowEquation(e)){//needed for shipibo? Not clear when it is  used
						//if ((e.getRhs().size()+1 != reducedPath.size()) || (reducedPath.sameArrow())){
							if ((e.getRhs().size()+1 != reducedPath.size()) || (reducedPath.sameArrow(ht))){
								if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
									stk.popSymbol().getProduct(reducedPath);
									apply = true;
								}
							} else {
							 if (e.getEqType() < Equation.NONINVERTIBLE && stk.pushSymbols(e.substituteLhs(reducedPath))) {
									stk.popSymbol().getProduct(reducedPath);
									apply = true;
								} else if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
									stk.popSymbol().getProduct(reducedPath);
									apply = true;
								}
							 }
						} else {
						 if (e.getEqType() < Equation.NONINVERTIBLE && stk.pushSymbols(e.substituteLhs(reducedPath))) {
								stk.popSymbol().getProduct(reducedPath);
								apply = true;
							} else if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
								stk.popSymbol().getProduct(reducedPath);
								apply = true;
							}
						 }
						 doneEqual = true;
				    }
			    }
		    }
			//if (flag) System.out.println(" BBBBBBBBBBBBBBBBBBBBBBBB redu "+reducedPath);
		    if (!apply && doneEqual2 == false && reducedPath.size() > 2) {
			    stk.pushSymbol(reducedPath.getLast());
			    reducedPath.removeEnd();
			    for(i=0;i< equations.size();i++) {
				    Equation e = (Equation) equations.elementAt(i);
					if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) {
					continue;}//skip equivalence equations

					 if (e.getEqType() == Equation.EQUIVALENCE ||
					 e.getEqType() == Equation.EQUIVALENCE+Equation.NONINVERTIBLE) continue;//skip equivalence equations
				    if (e.getLhs().size() > 1 && e.getLhs().size() == e.getRhs().size()) {
					 //boolean flag = isMixedSexSameArrowEquation(e);
						if (isMixedSexSameArrowEquation(e)){
							if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
								stk.popSymbol().getProduct(reducedPath);
								apply = true;
							}
						} else {
							if (e.getEqType() < Equation.NONINVERTIBLE && stk.pushSymbols(e.substituteLhs(reducedPath))) {
								stk.popSymbol().getProduct(reducedPath);
								apply = true;
							} else if (stk.pushSymbols(e.substituteRhs(reducedPath))) {
								stk.popSymbol().getProduct(reducedPath);
								apply = true;
							}
						}
						doneEqual2 = true;
				    }
			    }

			    if (!apply) stk.popSymbol().getProduct(reducedPath);

		    }
		    if (reducedPath.size() == 0 && stk.empty()) break; // DO: empty symbolstack probably here

		    if (!apply)
			    if (stk.empty()) break;
			    else stk.popSymbol().getProduct(reducedPath);
	    }
	    if (!stk.empty()) System.out.println("!!!!!!Stack is not empty! path="+toString()+
			    "stack top="+stk.popSymbol());
	    return reducedPath.size() != 0;
    }

    boolean reduceLR() {
	    return reduceLR(new AlgebraSymbolStack());
    }

    boolean reduceLR(AlgebraSymbolStack stk) {
	    int i;
	    boolean apply=false;
	    boolean doneEqual = false;
	    for(;;)
	    {
		    int plen = reducedPath.size();
		    apply = false;

		    Algebra.getCurrent().unbind(); // unbind variables

		    for(i=0;i< equations.size();i++) {
			    Equation e = (Equation) equations.elementAt(i);
				if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)){continue;}//skip equation

				 if (e.getEqType() == Equation.EQUIVALENCE ||
				 e.getEqType() == Equation.EQUIVALENCE+Equation.NONINVERTIBLE) continue;//skip equivalence equations
			    if (stk.pushSymbolsRev(e.substituteRhsLR(reducedPath))) {
				    if (e.getRhs().getFirst().equals(Algebra.getCurrent().getElement("0"))) { // change to top of stk
					    reducedPath.removeAllElements();
				    //	reducedPath.addToEnd(getElement("0");
					    apply = false;
					    break;
				    }
				    apply = true;
				    doneEqual = false;
				    stk.popSymbol().getProductLR(reducedPath);
			    }
		    Algebra.getCurrent().unbind(); // unbind variables
		    }

		    boolean lenchanged = !(plen == reducedPath.size());

		    if (lenchanged) continue;

		    apply = false; // prelim

		    Algebra.getCurrent().unbind(); // unbind variables

		    if (!doneEqual)
		    for(i=0;i< equations.size();i++) {
			    Equation e = (Equation) equations.elementAt(i);
				if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) {continue;}//skip equation

				 if (e.getEqType() == Equation.EQUIVALENCE ||
				 e.getEqType() == Equation.EQUIVALENCE+Equation.NONINVERTIBLE) continue;//skip equivalence equations
			    if (e.getLhs().size() > 1 && e.getLhs().size() == e.getRhs().size()) {
				    if (e.getEqType() < Equation.NONINVERTIBLE && stk.pushSymbolsRev(e.substituteLhsLR(reducedPath))) {
					    stk.popSymbol().getProductLR(reducedPath);
				    }
				    doneEqual = true;
			    }
		    Algebra.getCurrent().unbind(); // unbind variables
		    }

		    Algebra.getCurrent().unbind(); // unbind variables

		    for(i=0;i< equations.size();i++) {
			    Equation e = (Equation) equations.elementAt(i);
				if ((e.getEqType() == Equation.NONINVERTIBLE) && (!pathFlag)) {continue;}//skip equation
				if (e.getEqType() == Equation.EQUIVALENCE ||
				e.getEqType() == Equation.EQUIVALENCE+Equation.NONINVERTIBLE) continue;//skip equivalence equations
			    if ((e.getLhs().size() != e.getRhs().size())) {
				    if (stk.pushSymbolsRev(e.substituteRhsLR(reducedPath))) {
					    if (e.getRhs().getFirst().equals(Algebra.getCurrent().getElement("0")))  {
						    reducedPath.removeAllElements();
					    //	reducedPath.addToEnd(Algebra.getCurrent().getElement("0"));
						    apply = false;
						    doneEqual = false;
						    break;
					    }
					    apply = true;
					    stk.popSymbol().getProductLR(reducedPath);
				    }
			    }
		    Algebra.getCurrent().unbind(); // unbind variables
		    }
		    if (reducedPath.size() == 0) break; // DO: empty symbolstack probably here

		    if (!apply) {
			    if (stk.empty()) break;
			    else stk.popSymbol().getProductLR(reducedPath);
		    }
	    }
	    return reducedPath.size() != 0;
    }

    public String getPathSex(){
		//System.out.println(" this "+this +" path "+this.path +" reduced "+this.reducedPath);
		//if (!isEquivalentPath())//dwr 8/5
		if (!isReducedEquivalentPath())//dwr 8/5
			return getReducedProductPath().getLast().getSex();
		else {
			//AlgebraPath apr = getEquivalentPathRight();//dwr 8/5
			//AlgebraPath apl = getEquivalentPathLeft();//dwr  8/5
			AlgebraPath apr = getReducedEquivalentPathRight();//dwr 8/5
			AlgebraPath apl = getReducedEquivalentPathLeft();//dwr 8/5
			//System.out.println(" the right "+apr+" left "+apl);
			if (apr.getPathSex().equals(apl.getPathSex())) return apr.getPathSex();
			else return "N";
			//return reducedPath.getLast().getSex();
		}
    }

    public String getPathFirstSex(){
		//if (!isEquivalentPath())//dwr 8/5
		if (!isReducedEquivalentPath())//dwr 8/5
			return getReducedProductPath().getFirst().getSex();
		else {
			AlgebraSymbolVector asr = getEquivalentPathRight().getReducedProductPath();
			AlgebraSymbolVector asl = getEquivalentPathLeft().getReducedProductPath();
			//if (path.getFirst().getSex().equals(((AlgebraSymbol)path.elementAt(path.indexOf("&")-1)).getSex()))
			//	return path.getFirst().getSex();
			//else return "N";
			if (asr.getFirst().getSex().equals(asl.getFirst().getSex()))   return asr.getFirst().getSex();
			else return "N";
			//return reducedPath.getLast().getSex();
		}
    }

    public boolean isEquivalentPath(){
		//return (path.equivalentProduct());//Shipibo
		//return (path.equivalentProduct() || reducedPath.equivalentProduct());//Trobriand/Tongan -- works with Shipibo
       return (path.equivalentProduct());//dwr 8/5 changed calls to iep() to match this definition
	//return (this.toString().indexOf("&") != -1);
    }

    public boolean isReducedEquivalentPath(){
		return (reducedPath.equivalentProduct());//used in Tongan
       //return (reducedPath.equivalentProduct());
	//return (this.toString().indexOf("&") != -1);
    }

    public AlgebraPath getEquivalentPathRightX(){
		AlgebraPath p = new AlgebraPath();
		for (path.reset();path.isNext();){
			AlgebraSymbol as = path.getNext();
			if (as.getValue().equals("&")) break;
			//path.product(as);
			p.path.addToEnd(as);
			p.reducedPath.addToEnd(as);
		}
		//System.out.println("THE PATH IS "+path);
		return p;
    }

    public AlgebraPath getReducedEquivalentPathRight(){
		AlgebraPath p = new AlgebraPath();
		for (reducedPath.reset();reducedPath.isNext();){
			AlgebraSymbol as = reducedPath.getNext();
			if (as.getValue().equals("&")) break;
			//path.product(as);
			p.path.addToEnd(as);
			p.reducedPath.addToEnd(as);
		}
		//System.out.println("THE PATH IS "+path);
		return p;
    }

	    public AlgebraPath getEquivalentPathRight(){
		AlgebraPath p = new AlgebraPath();
		for (path.reset();path.isNext();){
			AlgebraSymbol as = path.getNext();
			if (as.getValue().equals("&")) break;
			//path.product(as);
			//p.product(as);
			p.path.addToEnd(as);
			p.reducedPath.addToEnd(as);
		}
		//System.out.println("THE PATH IS "+path+" this "+this+ " p "+p);
		//AlgebraPath rp = new AlgebraPath(p.path);//see if Shipibo needs these two lines
		//p.reducedPath = rp.getReducedProductPath();
		return p;
    }


    public AlgebraPath getReducedEquivalentPathLeft(){
		AlgebraPath p = new AlgebraPath();
		boolean startFlag = false;
		//for (path.reset();path.isNext();){
		for (reducedPath.reset();reducedPath.isNext();){
			AlgebraSymbol as = reducedPath.getNext();
			if (startFlag) {
			//path.product(as);
				p.path.addToEnd(as);
				p.reducedPath.addToEnd(as);
			}
			if (!startFlag) startFlag = (as.getValue().equals("&"));
		}
		if (!startFlag) return this;
		//System.out.println("THE PATH IS "+path);
		else return p;
    }

    public AlgebraPath getEquivalentPathLeftXX(){
		AlgebraPath p = new AlgebraPath();
		boolean startFlag = false;
		//System.out.println(" this "+this+"  path "+path);
		for (path.reset();path.isNext();){
			AlgebraSymbol as = path.getNext();
			if (startFlag) {
			//path.product(as);
				p.path.addToEnd(as);
				p.reducedPath.addToEnd(as);
			}
			if (!startFlag) startFlag = (as.getValue().equals("&"));
		}
		if (!startFlag) return this;
		else {
			//AlgebraPath rp = new AlgebraPath(p.path);
			//p.reducedPath = rp.getReducedProductPath();
		//System.out.println("THE PATH IS "+p+ " path "+p.path+" red "+p.reducedPath);
			return p;
		}
    }
	
	
    public AlgebraPath getEquivalentPathLeft(){
		AlgebraPath p = new AlgebraPath();
		boolean startFlag = false;
		//System.out.println(" this "+this+"  path "+path);
		//for (reducedPath.reset();reducedPath.isNext();){//dwr 8/5
		for (path.reset();path.isNext();){//dwr 8/5
			//AlgebraSymbol as = reducedPath.getNext();//dwr 8/5
			AlgebraSymbol as = path.getNext();//dwr 8/5
			if (startFlag) {
			//path.product(as);
				//p.product(as);
				p.path.addToEnd(as);
				p.reducedPath.addToEnd(as);
			}
			if (!startFlag) startFlag = (as.getValue().equals("&"));
		}
		if (!startFlag) return this;
		else {
			//AlgebraPath rp = new AlgebraPath(p.path);
			//p.reducedPath = rp.getReducedProductPath();//Check Shipibo to see if this statement is needed; otherwise don't use it
		//System.out.println("THE PATH IS "+p+ " path "+p.path+" red "+p.reducedPath+" get red "+rp.getReducedProductPath());
			return p;
		}
    }

 /*   public AlgebraPath getEquivalentPathLeftX(){
		AlgebraPath p = new AlgebraPath();
		boolean startFlag = false;
		for (reducedPath.reset();reducedPath.isNext();){
			AlgebraSymbol as = reducedPath.getNext();
			if (startFlag) {
			//path.product(as);
			p.path.addToEnd(as);
			p.reducedPath.addToEnd(as);
			}
			if (!startFlag) startFlag = (as.getValue().equals("&"));
		}
		if (!startFlag) return this;
		//System.out.println("THE PATH IS "+path);
		else return p;
    }*/



    public boolean isReducedPath(){
		AlgebraPath path = ((AlgebraPath)this.clone());//clone reduces the path
		return (reducedPath.toString().equals(path.getReducedProductPath().toString()));
    }

    /** construct product without reducing the path
    * @thePath path with which product is taken
    * @product product of the path
    */
    public void pathProduct(AlgebraPath product){
		for (product.reducedPath.reset();product.reducedPath.isNext();){
			AlgebraSymbol as1 = product.reducedPath.getNext();
			this.path.addToEnd(as1);
			this.reducedPath.addToEnd(as1);
		}
    }

	/**
	* combine two paths into equivalent path form; does not test for equivalence
	* @prod one path
	* @this other path
	* @return equivalent path
	*/

    public AlgebraPath makeEquivalentPath(AlgebraPath prod) {
		AlgebraSymbol as = Algebra.getCurrent().getElement("&");
		if (as.getReciprocal(as) == null) as.setReciprocal(as);
		//AlgebraSymbol as = new AlgebraSymbol("&");
		AlgebraPath newp = new AlgebraPath();
		newp.pathProduct(this);
		newp.path.addToEnd(as);
		newp.reducedPath.addToEnd(as);
		newp.pathProduct(prod);
		return newp;
    }

    /** converts equivalent path A&B into vector form [A, B]
    * @return algebraPathVector
    */
    public AlgebraPathVector algPathToVector(){
		AlgebraPathVector apv = new AlgebraPathVector();
		if (this.isEquivalentPath()) {//dwr 8/5 no change
			//AlgebraPath ap1=(new AlgebraPath(reducedPath)).getEquivalentPathLeft();
			AlgebraPath ap1=getEquivalentPathLeft();//dwr 8/5 no change
			apv.addElement(ap1);
			//ap1 = (new AlgebraPath(reducedPath)).getEquivalentPathRight();
			ap1 = getEquivalentPathRight();
			//ap1.reducePath(ap1.getReducedProductPath());
			//if (ap1.getReducedProductPath().toString().equals(ap1.path.toString()))
			   apv.addElement(ap1);
		}
		else {
			apv.addElement(this);
		}
		return apv;
    }

    public AlgebraPathVector algPathToVectorx(){
		AlgebraPathVector apv = new AlgebraPathVector();
			apv.addElement(this);

		return apv;
    }

}

class AlgebraSymbolStack extends java.util.Stack {
    public AlgebraSymbolStack() {

    }

    public AlgebraSymbolStack(AlgebraSymbolVector v) {
	    for (int i=0;i<v.size();i++) {
		    this.pushSymbol(v.getSymbol(i));
	    }
    }

    public AlgebraSymbolStack(AlgebraSymbol v) {
	    this.pushSymbol(v);
    }

    AlgebraSymbol pushSymbol(AlgebraSymbol a) {
	    return (AlgebraSymbol) push(a.getAlgebraSymbol());
    }

    AlgebraSymbol popSymbol() {
	    return (AlgebraSymbol) pop();
    }

    AlgebraSymbol peekSymbol() {
	    return (AlgebraSymbol) peek();
    }

    boolean pushSymbols(AlgebraSymbolVector v) {
	    //if (v == null) throw new Error("AlgebraPath: null AlgebraSymbol");
	    if (v == null) return false;
	    if (v.size() == 0) return false;
		if (v.equivalentProduct()){
			int j = v.indexOf(Algebra.getCurrent().getElement("&"));
			//AlgebraSymbolVector asv = new AlgebraSymbolVector();
			//while (!this.empty()) asv.addElement(this.popSymbol());
			//for (asv.reset();asv.isNext();) this.pushSymbol(asv.getNext());
			for (int i=v.size()-1;i>j;i--) this.pushSymbol((AlgebraSymbol)v.getSymbol(i));
			//for (asv.reset();asv.isNext();) this.pushSymbol(asv.getNext());(int i=j-1;i>=0;i--)
			//for (int i=v.size()-1;i>j;i--)  {System.out.println(" v "+v+" last i "+i+" as "+(AlgebraSymbol)v.getSymbol(i));this.pushSymbol((AlgebraSymbol)v.getSymbol(i));}
			return true;
		} else {
			for (int i=v.size()-1;i>=0;i--) {
				this.pushSymbol(v.getSymbol(i));
			}
		}
	    return true;
    }

    boolean pushSymbolsRev(AlgebraSymbolVector v) {
	    //if (v == null) throw new Error("AlgebraPath: null AlgebraSymbol");
	    if (v == null) return false;
	    if (v.size() == 0) return false;
	    for (int i=v.size()-1;i>=0;i--) {
		    this.pushSymbol((AlgebraSymbol) v.elementAt(i));
	    }
	    return true;
    }
}


