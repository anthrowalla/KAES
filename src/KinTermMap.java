import java.util.*;
import java.awt.Rectangle;

/* HISTORY
* 2/12/01 DR replaced "if ((g=generators.indexOf(gen)) == -1) {throw new KintermMapException .....}"
* by "if ( ( g =  generators.indexOf(gen) ) == -1) return " ";"
* in procedure: public String generatorToMapgen(String gen)
* 2/13 DR added "if (femaleGen == null) continue;" and "if (maleGen == null) continue;"
* to the procedure: "public int matchTermsAndSexes"
* theGen added as an argument to the procedure "matchTermsAndSexes" to allow printing out the algebra generator in the message.
* 2/14 DR add the null test ( tki != null)to the procedure	"__addToAlgebraKin"
* 2/23 DR added procedure getMatchingFocalTerm (String fe); added fe to argument of _addToAlgebraKin;
* procedure used in addToAlgebraKin
* replaced 'currentTerm = "Self";' with currentTerm = getMatchingFocalTerm(fe);
* 2/24 DR removed messages for Male Gen and Female Gen in matchTermsAndSexes
* 3/11 DR modified the procedures associateAlgebraGeneratorsWithKinshipGenerators and matchTermsAndSexes to allow for algebra
* elements with the sex attribute set rather than using a sex generator to denote sex
* 3/12 DR modified __addToAlgebraKin so that it works with sex attributes and not just sex generators
* 7/01 DR replaced "I" by "isIdentityElement()" in procedure __addToAlgebraKin
* 11/11 DR added criterion (ret.indexOf(sexed) == -1) to getSexGenProducts
* 11/21 DR modified __addToAlgebraKin to work with equivalent elements of form abc&def
* 5/27 DR separated part of TestIsomorphism out as a new procedure, linkAlgebraWithKinTermMap(CayleyTable x),
* in order to reference the first part of without calling TestIsomorphism
* 8/1 DR rewrote testIsomorphism to return ListVector of non-matching terms and algebra
* elements
* 8/22 DR modified associateAlgebraGeneratorsWithKinshipGenerators to run only if the
* generating = null; corrected the logic of the procedure so that a set of kin term
* generators (UP, DOWN, SPOUSE) are compared against a set of algebra generators; added
* some error messages
* 8/28 corrected problem in _addToAlgebraKin with linking sex generator with generator
* when computing kin term corresponding to algebra path; added procedure putInHashTable
* to handle problem with multiple calls to put in _addToAlgebraKin due to some trial paths
* not corresponding to a kin term
* 9/1 DR modified __addToAlgebraKin to handle equivalent algebra elements A&B; if
* A&B links to K via generator G, then construct link from A or from B to K via G in
* accordance with sex of A, B and G
* 1/26 DR modified buildKinTermMap to handle case where generators are covered but
* covering term is not a generator
*/

public class KinTermMap extends Hashtable implements CayleyEntity  {
	/** the current kin term map
	*/
	static public KinTermMap theMap = null;
	static public Hashtable kinTermMaps = new Hashtable();
	/** number of kin term maps defined
	*/
	static int mapno=0;
	StringVector generatingTerms = new StringVector(3,1);
	String sexTerms[] = {"M","F","N"};
	StringVector generators = new StringVector(3,1);

	KinshipAlgebra theKinshipAlgebra = null;

        public StringVector getGenerators(){
          return generators;
        }

        public StringVector getGeneratingTerms(){
          return generatingTerms;
        }

	/** list of TransferKinInfo structures
	*/
	TransferKinInfoVector theKinTerms = new TransferKinInfoVector();

	/** creates a new kin term map with name Map + sequence number
	* @see #mapno
	*/
	public KinTermMap() {
		theMap = this;
		kinTermMaps.put("Map"+ ++mapno,this);

		init();
	}
	/** creates a new kin term map with name label
	* @param label the name of this kin term map
	*/
	public KinTermMap(String label) {
		theMap = this;
		kinTermMaps.put(label,this);
		mapno++;
		init();
	}
	/** creates a new kin term map with name label
	* @param label the name of this kin term map
	* @param Kin data to intialise to
	*/
	public KinTermMap(String label, TransferKinInfoVector m) {
		setTheKinTerms( m);
		theMap = this;
		kinTermMaps.put(label,this);
		mapno++;
		init();
	}
	/** creates a new kin term map with name label
	* @param label the name of this kin term map
	* @param Kin data to intialise to
	*/
	public KinTermMap(TransferKinInfoVector m) {
		setTheKinTerms( m);
		theMap = this;
		kinTermMaps.put("Map"+ ++mapno,this);
		mapno++;
		init();
	}

	/** override in subclasses to initialize the kinmap
	*/
	public void init() {
		if (theKinTerms.size()> 0) buildKinTermMap();
	}

	/** returns a kin term map based on key
	* @param key name of kin term map to retrieve
	*/
	static public KinTermMap getMap(String key) {
		return (KinTermMap) kinTermMaps.get(key);
	}

	/** makes an entry and definition for a kin term
	* @param mapTerm starting kin term
	* @param generatingTerm generating term relative to mapTerm
	* @param sex sex marker associated with mappedTerm
	* @param mappedTerm kin term associated with mapTerm and generatingTerm with sex marker sex
	*/
	public void mapTerm(String mapTerm, String generatingTerm, String sex, String mappedTerm) {
		put(mapTerm+" "+generatingTerm+" "+sex,mappedTerm);
	}

	/** makes an entry and definition for a kin term
	* @param mapKey starting kin term SPACE generating term SPACE sex marker
	* @param mappedTerm kin term associated with mapKey
	*/
	public void mapTerm(String mapkey, String mappedTerm) {
		put(mapkey,mappedTerm);
	}

	/** returns a kin term  based on keys
	* @param mapTerm starting kin term
	* @param generatingTerm generating term relative to mapTerm
	* @param sex sex marker associated with mappedTerm
	* @return kin term associated with mapTerm and generatingTerm with sex marker sex
	*/
	public String getTerm(String mapTerm, String generatingTerm, String sex) {
		return getTerm(mapTerm+" "+generatingTerm+" "+sex);
	}

	/** returns a kin term  based on key
	* @param mapTerm starting kin term
	* @param generatingTerm generating term relative to mapTerm
	* @return kin term associated with mapTerm and generatingTerm, assuming neuter
	*/
	public String getTerm(String mapTerm, String generatingTerm) {
		return getTerm(mapTerm+" "+generatingTerm+" "+"N");
	}

	/** returns a kin term  based on key
	* @param mapKey starting kin term SPACE generating term SPACE sex marker
	* @return kin term associated with mapKey
	*/
	public String getTerm(String mapTerm) {
		String s = (String) get(mapTerm);
		if (s == null) s = "<Undefined>";
		//System.out.println("-------------->>>>>GetTerm="+mapTerm);
		return s;
	}

	/** translates algebra generator name to kin term map generator name
	* @param gen algebra generator name
	* @return kin term map generator name associate with algebra generator name
	*/
	public String generatorToMapgen(String gen) {
		int g;

		if ( ( g =  generators.indexOf(gen) ) == -1) {
		    return "";
		}

//		if ( ( g =  generators.indexOf(gen) ) == -1) {
//			throw new KintermMapException(" ");
//			return "";
//		}
		else return (String) generatingTerms.elementAt(g);
	}

	/** translates algebra generator name to kin term map generator name
	* @param gen algebra generator name
	* @return kin term map generator name associated with algebra generator name
	*/
	public StringVector generatorToGenderedGenerators(String gen) {
		StringVector ret = new StringVector();
		for (int i = 0;i< generators.size();i++) {
			if (generators.getSymbol(i).endsWith(gen)) ret.addElement(generators.getSymbol(i));
		}
		return ret;
	}


	/** translates kin term map generator name to algebra generator name
	* @param gen  kin term map generator name
	* @return algebra generator name associate with kin term map generator name
	*/
	public String mapgenToGenerator(String gen) {
		int g;
		if ( ( g =  generatingTerms.indexOf(gen) ) == -1) return "";
		else return (String) generators.elementAt(g);
	}

	/** associate algebra generator name  with kin term map generator name
	* @param generatingTerm algebra generator name
	* @param generator  kin term map generator name
	*/
	public void associateGenerators(String generator, String generatingTerm) {
		generators.addElement(generator);
		generatingTerms.addElement(generatingTerm);
	}


	public String trace(AlgebraSymbolVector av) {
		String currentTerm;
		// set current term to focal term .. for now, rethink later when more than one focal term
		currentTerm = Algebra.getCurrent().getFocalElements().getSymbol(0).getValue();
		currentTerm = generatorToMapgen(currentTerm);
		for (int i=0;i<av.size();i++) {
			AlgebraSymbol a = av.getSymbol(i);
			String [] components = Algebra.getCurrent().theKludge.algebraSymbolToComponents(a);
			currentTerm = getTerm(currentTerm, generatorToMapgen(components[0]),components[1]);
			if (currentTerm == null) {
				currentTerm = "<Undefined>";
		System.out.println("-------------->>>>>currentTerm="+currentTerm);
				break;
			}
		}
		return currentTerm;
	}

	public Vector traceMapPath(AlgebraSymbolVector av) {
		String currentTerm;
		Vector mapPath = new Vector(3,1);
		// set current term to focal term .. for now, rethink later when more than one focal term
		currentTerm = Algebra.getCurrent().getFocalElements().getSymbol(0).getValue();
		currentTerm = generatorToMapgen(currentTerm);
		for (int i=0;i<av.size();i++) {
			AlgebraSymbol a = av.getSymbol(i);
			String [] components = Algebra.getCurrent().theKludge.algebraSymbolToComponents(a);
			mapPath.addElement(currentTerm);
			currentTerm = getTerm(currentTerm, generatorToMapgen(components[0]),components[1]);
			if (currentTerm == null) {
				currentTerm = "<Undefined>";
		//System.out.println("-------------->>>>>GetTerm2="+currentTerm);
				break;
			}
		}
		mapPath.addElement(currentTerm);
		return mapPath;
	}

	Hashtable algebraKin = new Hashtable(100);
	AlgebraPathVector algebraKinKeys = new AlgebraPathVector();

/*	public boolean addToAlgebraKinx(AlgebraPath ap) {
		String currentTerm;
		boolean ret =true;
		boolean isEtc = false;
		AlgebraSymbol lastGenerator = null;
		//Debug.on(-6);
		Debug.prout(6,"addToAlgKin: this="+this);
Debug.prout(6,"addToAlgKin: ap="+ap);

		AlgebraSymbolVector av = ap.getReducedProductPath();
		// set current term to focal term .. for now, rethink later when more than one focal term
Debug.prout(6,"addToAlgKin: av="+av);
		AlgebraSymbolVector focalElements = Algebra.getCurrent().getFocalElements();
		if (focalElements.size() == 0) return false;
		currentTerm = focalElements.getSymbol(0).getValue(); // kludge...only looks at first +++++
		currentTerm = generatorToMapgen(currentTerm);

		//currentTerm = generatorToMapgen(av.getSymbol(0).getValue()); // cludge since no productions from self

		for (int i=0;i<av.size();i++) {
Debug.prout(6,"addToAlgKin: Probe a");
			AlgebraSymbol a = av.getSymbol(i);
Debug.prout(6,"addToAlgKin: a="+a);

			if (a.getValue().equals ("0")) {
				currentTerm = "<Undefined>";
Debug.prout(6,"addToAlgKin: <Undefined>="+a);
//System.out.println("addToAlgKin: <Undefined>="+a);
				ret = false;
				break;
			}
			if (a.getValue().equals ("I")) {
				currentTerm = "Self"; // derive from TransferKinInfo
Debug.prout(6,"addToAlgKin: Self="+a);
				ret = true;
				break;
			}
			String [] components = Algebra.getCurrent().theKludge.algebraSymbolToComponents(a);
Debug.prout(6,"currentterm in: " + currentTerm);
Debug.prout(6,"addToAlgKin: components="+components[0]+","+components[1]+" ");
Debug.prout(6,"addToAlgKin: generatorToMapgen="+generatorToMapgen(components[0]));

			currentTerm = getTerm(currentTerm, generatorToMapgen(components[0]),components[1]);
Debug.prout(6,"currentterm out: " + currentTerm);
			if (!currentTerm.equals ("<Undefined>")) {


				TransferKinInfo tki = getKintabTerm(currentTerm);
Debug.prout(6,"addToAlgKin: TransferKinInfo="+tki);

				if (tki.isEtc()) {
Debug.prout(6,"addToAlgKin: isEtc="+a);
					lastGenerator = a;
					isEtc = true;
				} else {
					isEtc = false;
				}
			}else{
				if (isEtc && a.equals(lastGenerator)) {
Debug.prout(6,"addToAlgKin: etc+="+a);
					currentTerm="etc+"; // possibly more later
					break;
				}

Debug.prout(6,"addToAlgKin: <Undefined>="+a);

				currentTerm = "<Undefined>";
				ret = false;
				break;
			}
		}
									Debug.prout(6,"at 7");

		if (!currentTerm.equals("etc+")) algebraKinKeys.addElement(ap);
									Debug.prout(6,"at 8");

		algebraKin.put(ap.toString(),currentTerm);
		return ret;
	}
*/
    String getMatchingFocalTerm (String fe) {
        return generatorToMapgen(fe);
    }

	void putInHashTable(String key,String currentTerm){
		String s = (String)algebraKin.get(key);
		if (s == null)
		   algebraKin.put(key,currentTerm);
		else {
			if (s.equals("<Undefined>") || s.equals("<Mismatch>")) {
				algebraKin.remove(key);
				algebraKin.put(key,currentTerm);
			} else{
				if (!currentTerm.equals("<Undefined>") && !currentTerm.equals("<Mismatch>")) {
					if (!s.equals(currentTerm)) {
						//System.out.println(" KEY KEY KEY "+key+" cterm "+currentTerm);
						// algebraKin.remove(key);
						//  algebraKin.put(key,"<Undefined>");//maps to two kin terms
					}
				}
			}
		}
	}

	boolean ret = true;

	public boolean addToAlgebraKin(AlgebraPath ap) {
	    String currentTerm;
	    boolean isEtc = false;
	    AlgebraSymbol lastGenerator = null;
		boolean ret1 = true;

	    AlgebraSymbolVector focalElements = Algebra.getCurrent().getFocalElements();
	    if (focalElements.size() == 0) return false;
	    for(int i=0;i<focalElements.size();i++) { // needs more work once we get to real multiple focal elements
		    String fe = focalElements.getSymbol(i).getValue(); // kludge...only looks at first +++++
		    currentTerm = getMatchingFocalTerm(fe);
			ret = true;
			//System.out.println(" at do _add ap " + ap);
			if (!__addToAlgebraKin(ap,0,fe,currentTerm,isEtc,lastGenerator)) ret1 = false;
			//System.out.println("xxxxxxxxxx currentTerm "+currentTerm+" ret "+ret);
	    }
		return ret1;//true;
	}
	public boolean __addToAlgebraKin(AlgebraPath ap, int ndx, String fe,String currentTerm, boolean isEtc, AlgebraSymbol lastGenerator) {
boolean fflag = (ap.toString()).equals("CCPP");fflag = false;
//if (fflag) System.out.println("888888888888888 ap"+ap);
	  //  AlgebraPath ap1 = new AlgebraPath();
//System.out.println(" begore ap "+ap);
	    AlgebraPathVector apv = ap.algPathToVector();
		//boolean ret = true;
//System.out.println(" after apv "+apv);

/*	    AlgebraPathVector apv = new AlgebraPathVector();
	    if (ap.equivalentPath()) {
		ap1=(new AlgebraPath(ap.reducedPath)).getEquivalentPathLeft();
		apv.addElement(ap1);
		ap1 = (new AlgebraPath(ap.reducedPath)).getEquivalentPathRight();
		ap1.reducePath(ap1.getReducedProductPath());
		if (ap1.getReducedProductPath().toString().equals(ap1.path.toString()))
		    apv.addElement(ap1);
	    }
	    else {
		ap1 = ap;
		apv.addElement(ap1);
	    }*/

//fflag =  (apv.equals("A, E")|| apv.toString().equals("[A, E]"));
//if (fflag)
if (fflag)System.out.println("APVXXXXXXXXXXXXXXXXXX"+apv+ " ap "+ap);
	    String ycurrentTerm=currentTerm;
	    boolean yisEtc=isEtc;
	    AlgebraSymbol ylastGenerator=lastGenerator;
	    int yndx = ndx;
		//System.out.println(" now do apv "+apv);
		for (apv.reset();apv.isNext();) {//cycle through terms in equivalent term
			AlgebraPath thep = (AlgebraPath)apv.getNext();//thep <> ap only if equivalent element
			currentTerm = ycurrentTerm;
			isEtc = yisEtc; lastGenerator=ylastGenerator;ndx=yndx;
			if (ndx >= thep.reducedPath.size()) {
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if (!currentTerm.equals("etc+")) {
					algebraKinKeys.addUnique(ap);
					algebraKinKeys.addUnique(thep);
					putInHashTable(ap.toString(),currentTerm);
if (fflag) System.out.println("aaaaaaaaaa thep "+thep+" ct "+currentTerm);
					putInHashTable(thep.toString(),currentTerm);
					if (ret) ret  = !currentTerm.equals("<Undefined>");
				   // algebraKin.put(ap.toString(),currentTerm);
				}
				return ret; // check for etc. eg algebra too small for ktm
			}
			String ggsex = "";
			AlgebraSymbol a=thep.getReducedProductPath().getSymbol(ndx);
	if (fflag)System.out.println("^&^&^&^&^&&^&^&&^^&^&^&&^  a "+a);
			if (ndx < thep.getReducedProductPath().size()-1) {
				ggsex = thep.getReducedProductPath().getSymbol(ndx+1).toString();
				if (!ggsex.equals("M") && !ggsex.equals("F")) ggsex = "";
			}

			if (a.getValue().equals("F")) {//test for sex generator
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if (( tki != null) && (tki.getSex().equals("F"))) {
					if (!currentTerm.equals("etc+")) {
						algebraKinKeys.addUnique(ap);
						algebraKinKeys.addUnique(thep);
					}
					putInHashTable(ap.toString(),currentTerm);
if (fflag) System.out.println("cccccccccccccccc thep "+thep+" ct "+currentTerm);
					putInHashTable(thep.toString(),currentTerm);
					if (ret) ret  = !currentTerm.equals("<Undefined>");
					//algebraKin.put(ap.toString(),currentTerm);
				}
				return ret;
			}
			if (a.getValue().equals("M")) {
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if ((tki != null) && (tki.getSex().equals("M"))) {
					if (!currentTerm.equals("etc+")) {
						algebraKinKeys.addUnique(ap);
						algebraKinKeys.addUnique(thep);
					}
					putInHashTable(ap.toString(),currentTerm);
if (fflag) System.out.println("ddddddddddddd thep "+thep+" ct "+currentTerm);
					putInHashTable(thep.toString(),currentTerm);
					if (ret) ret  = !currentTerm.equals("<Undefined>");
				   // algebraKin.put(ap.toString(),currentTerm);
				}
				return ret;
			}
			if (a.getValue().equals ("0")) {
				currentTerm = "<Undefined>";
		//System.out.println("-------------->>>>>0 currentTerm="+currentTerm+ " a " + a+" ap "+ap);
				algebraKin.put(ap.toString(),currentTerm);
				return false;
			} else if (a.isIdentityElement()) {
				currentTerm = getMatchingFocalTerm(fe);
				if (!currentTerm.equals("etc+")) {
				algebraKinKeys.addUnique(ap);
				algebraKinKeys.addUnique(thep);
				}
if (fflag) System.out.println("eeeeeeeeeeeeeee thep "+thep+" ct "+currentTerm);
				putInHashTable(thep.toString(),currentTerm);
				putInHashTable(ap.toString(),currentTerm);
				if (ret) ret  = !currentTerm.equals("<Undefined>");
				return ret;
			}
			StringVector a2k = generatorToGenderedGenerators(a.toString());
			String xcurrentTerm=currentTerm;
			boolean xisEtc=isEtc;
			AlgebraSymbol xlastGenerator=lastGenerator;
			for(int i=0;i<a2k.size();i++) {
				currentTerm = xcurrentTerm; isEtc = xisEtc; lastGenerator=xlastGenerator;
				String  gsex="N", gterm=a2k.getSymbol(i);
				if (gterm.length() > 1) gsex = a2k.getSymbol(i).substring(0,1);
				else gsex = Algebra.getCurrent().findElement(gterm).getSex();
				String oldCur = currentTerm;
				if ((a2k.size() > 1) || (ggsex.equals(""))) ggsex = gsex;//ggsex = "";
	if (fflag) System.out.println("XXXXX  ap "+ ap+" currentterm "+currentTerm+" gterm "+gterm+" ggsex "+ggsex);
				currentTerm = getTerm(currentTerm, generatorToMapgen(gterm),ggsex);
				if (oldCur.equals(currentTerm))  currentTerm = "<Mismatch>";//new code
				if (!currentTerm.equals("<Undefined>") && !currentTerm.equals("<Mismatch>") )
				    currentTerm = theKinTerms.lookupTerm(currentTerm).getEffectiveTerm();
	if (fflag) System.out.println("YYYYY ap "+ ap+" currentterm "+currentTerm+ " gen "+generatorToMapgen(gterm));
				//currentTerm = getTerm(currentTerm, generatorToMapgen(gterm),theSex);
			   //if (currentTerm.equals("<Undefined>") && !gsex.equals("N")) {
				if ((currentTerm.equals("<Undefined>") || currentTerm.equals("<Mismatch>")) && !ggsex.equals("N")) {
					String cTerm = getTerm(oldCur, generatorToMapgen(gterm),"N");
					if (!cTerm.equals("<Undefined>") && !cTerm.equals("<Mismatch>")) currentTerm = cTerm;//found a term
	if (fflag) System.out.println("ZZZZZ mismatch ap "+ ap+" currentterm "+currentTerm+" olldcur "+oldCur);
				}


				boolean definedFlag = false;
				if (a2k.size() > 1) {
				if ((i == 0) && (currentTerm.equals("<Undefined>") || currentTerm.equals("<Mismatch>"))) {
					String cTerm = getTerm(currentTerm,generatorToMapgen(a2k.getSymbol(1)),ggsex);
					if (cTerm != null) continue;
					else {
						algebraKinKeys.addUnique(ap);
						algebraKinKeys.addUnique(thep);
						putInHashTable(ap.toString(),currentTerm);
	if (fflag) System.out.println("ffffffffffff thep "+thep+" ct "+currentTerm);
						putInHashTable(thep.toString(),currentTerm);
						if (ret) ret  = !currentTerm.equals("<Undefined>");
						//algebraKin.put(ap.toString(),currentTerm);
						continue;
					}
				}
				else if (i == 0){
					definedFlag = true;
				}
				else if (currentTerm.equals("<Undefined>") || currentTerm.equals("<Mismatch>")){
					if (definedFlag) continue;
					else {
					   algebraKinKeys.addUnique(ap);
						algebraKinKeys.addUnique(thep);
						putInHashTable(ap.toString(),currentTerm);
if (fflag) System.out.println("ggggggggggggg thep "+thep+" ct "+currentTerm);
						putInHashTable(thep.toString(),currentTerm);
						if (ret) ret  = !currentTerm.equals("<Undefined>");
					//algebraKin.put(ap.toString(),currentTerm);
					}
				}
				}
				if (!currentTerm.equals("<Undefined>") && !currentTerm.equals("<Mismatch>")) {
					TransferKinInfo tki = getKintabTerm(currentTerm);
					if (tki.isEtc()) {
						lastGenerator = a;
						isEtc = true;
					} else {
						isEtc = false;
					}
					AlgebraPath ap2 = new AlgebraPath();
					ap2 = (AlgebraPath)thep.clone();//ap2 is modified
					if (ndx+1 < ap2.getReducedProductPath().size()) {
						if(!__addToAlgebraKin(ap,ndx+1,fe,currentTerm,isEtc,lastGenerator)) ret = false;
				} else {
					if (theKinTerms.lookupTerm(currentTerm).getSex().equals(ap.getPathSex())){
						if (!currentTerm.equals("etc+")) {
							algebraKinKeys.addUnique(ap);
							algebraKinKeys.addUnique(thep);
							if (!ap.equals(thep)) {
	if (fflag) System.out.println("hhhhhhhhhhhhhh thep "+thep+" ct "+_unpackTerm(thep,currentTerm));
							    putInHashTable(thep.toString(),_unpackTerm(thep,currentTerm));
							}
							putInHashTable(ap.toString(),currentTerm);
							if (ret) ret  = !currentTerm.equals("<Undefined>");
						   // algebraKin.put(ap.toString(),currentTerm);
						}
					}else {//sex mismatch,try matching with equivalent generators
					   AlgebraSymbolVector ap3 = new AlgebraSymbolVector();
					   if ((ap.isEquivalentPath() || ap.isReducedEquivalentPath())
						   && (ap3 = ap2.getReducedProductPath()).size()==1){//dwr 8/5
						//if (ap.isEquivalentPath()&& (ap3 = ap2.getReducedProductPath()).size()==1){//dwr 8/5
							if (generators.indexOf(ap3.elementAt(0).toString()) != -1){
							algebraKinKeys.addUnique(ap2);
	if (fflag) System.out.println("jjjjjjjjjjjjj ap2 "+ap2+" thep "+thep+" ct "+currentTerm);
								putInHashTable(ap2.toString(),currentTerm);
							algebraKinKeys.addUnique(ap);
							//if (fflag) putInHashTable(ap.toString(),"[bake2, bake1]");
								putInHashTable(ap.toString(),currentTerm);
							algebraKinKeys.addUnique(thep);
							putInHashTable(thep.toString(),currentTerm);
		if (fflag) System.out.println("909090909090 ap "+ap+ " ap2 "+ ap2 +" thep "+ thep+" current "+currentTerm);
							if (ret) ret  = !currentTerm.equals("<Undefined>");
							//algebraKin.put(ap2.toString(),currentTerm);
							}
						}
					}
				}
				}else{
					if (isEtc && a.equals(lastGenerator)) {
						currentTerm="etc+"; // possibly more later
					}
					algebraKinKeys.addUnique(thep);
					putInHashTable(ap.toString(),currentTerm);
	if (fflag) System.out.println("llllllllllll thep "+thep+" ct "+currentTerm);
					putInHashTable(thep.toString(),currentTerm);
					if (ret) ret  = !currentTerm.equals("<Undefined>");
					//algebraKin.put(ap.toString(),currentTerm);
				}
			}
		}
		return ret;
	}

	String _unpackTerm(AlgebraPath ap, String term){
		StringVector sv = theKinTerms.lookupTerm(term).getCoveredTerms();
		if (sv != null) {
		    String sex = ap.getPathSex();
			for (sv.reset();sv.isNext();){
				String s = sv.getNext();
			    if (theKinTerms.lookupTerm(s).getSex().equals(sex))
					return s;
			}
		}
		return term;
	}




/*
	public void __addToAlgebraKinX(AlgebraPath ap, int ndx, String fe,String currentTerm, boolean isEtc, AlgebraSymbol lastGenerator) {

		if (ndx >= ap.getReducedProductPath().size()) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
System.out.println("tki="+tki+" =currentTerm "+currentTerm+"getsex="+tki.getSex());
			//if (tki.getSex().equals("N")) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			//}
			return; // check for etc. eg algebra too small for ktm
		}

		AlgebraSymbol a = ap.getReducedProductPath().getSymbol(ndx);
//	System.out.println("AP.REDUCED+"+a);
		//if (a.getSex().equals("F")) {
		if (a.getValue().equals("F")) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if (( tki != null) && (tki.getSex().equals("F"))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
	//	if (a.getSex().equals("M")) {
		if (a.getValue().equals("M")) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if ((tki != null) && ((tki.getSex().equals("M")) || (tki.getSex().equals("N")))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
		if (a.getValue().equals ("0")) {
			currentTerm = "<Undefined>";
			algebraKin.put(ap.toString(),currentTerm);
			return;
		} else if (a.getValue().equals ("I")) {
	        currentTerm = getMatchingFocalTerm(fe);
			//currentTerm = "Self"; // derive from TransferKinInfo
			if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
			algebraKin.put(ap.toString(),currentTerm);
			return;
		}
		StringVector a2k = generatorToGenderedGenerators(a.toString());
//System.out.println("XXXXX a2k="+a2k);
//System.out.println("XXX Generators="+generators);
		String xcurrentTerm=currentTerm;
		boolean xisEtc=isEtc;
		AlgebraSymbol xlastGenerator=lastGenerator;
		for(int i=0;i<a2k.size();i++) {
			currentTerm = xcurrentTerm; isEtc = xisEtc; lastGenerator=xlastGenerator;
			String gkin, gsex="N", gterm=a2k.getSymbol(i);
			if (gterm.length() > 1) {
				gsex = a2k.getSymbol(i).substring(0,1);
				gkin = a2k.getSymbol(i).substring(1);
			}
			gkin = gterm;//gsex= "M";
			String oldCur = currentTerm;
			currentTerm = getTerm(currentTerm, generatorToMapgen(gterm),gsex);
//System.out.println("XYXYX currentTerm="+currentTerm);
			if (currentTerm.equals("<Undefined>") && !gsex.equals("N"))
				currentTerm = getTerm(oldCur, generatorToMapgen(gterm),"N");
			//if (currentTerm.length() < 3)
				//System.out.println("=======>currentterm"+oldCur+" result="+currentTerm+" gkin "+gkin+" gsex= "+gsex+" term="+generatorToMapgen(gterm));
			if (!currentTerm.equals("<Undefined>")) {
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if (tki.isEtc()) {
					lastGenerator = a;
					isEtc = true;
				} else {
					isEtc = false;
				}
				if (ndx+1 < ap.getReducedProductPath().size()) {
					__addToAlgebraKin(ap,ndx+1,fe,currentTerm,isEtc,lastGenerator);
				} else {
					//if (currentTerm.equals("etc+")) algebraKin.put(ap.toString(),currentTerm); else
					// if (tki.getSex().equals("N")) {
					 //if (tki.getSex().equals("M")) {
						if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
						algebraKin.put(ap.toString(),currentTerm);
					//}
System.out.println("tki="+tki+"=currentTerm "+currentTerm+"getsex="+tki.getSex());
				}
			}else{
				if (isEtc && a.equals(lastGenerator)) {
					currentTerm="etc+"; // possibly more later
				}

				algebraKin.put(ap.toString(),currentTerm);
			}
		}
	}
	*/

	public StringVector compareCayleyTables(CayleyEntity aMap) {
		// Match key lengths?
//System.out.println(" Algebrakin "+algebraKin.toString()+"\n----------------------");
//System.out.println(" aMap "+aMap);
		StringVector ret = new StringVector();
		for(Enumeration e = aMap.keys();e.hasMoreElements();) {
			String q = (String) e.nextElement();
		    if (q.startsWith("etc+")) continue;
			String a = aMap.getTerm(q);
			String b = getTerm(q);
		//System.out.println("KinCayley key="+q+ " a "+a+" b "+b);
			if (!a.equals(b)) {
				// Message -- no Match ++++
				if (b.equals("<Undefined>") || b.equals("<Mismatch>")) {
					// Message -- ++++
					if (b.equals("<Undefined>"))
					   ret.addElement(new String("<K-UNDEF> Path="+q+": Cayley term="+a+": KinMap term="+b));
					else
					   ret.addElement(new String("<K-MISMA> Path="+q+": Cayley term="+a+": KinMap term="+b));
				} else if (b.equals("<NotKey>")) {
					// Message -- ++++
					ret.addElement(new String("<K-NOTKEY> Path="+q+": Cayley term="+a+": KinMap term="+b));
				} else if (b.equals("0")) {
					// Message -- ++++
					ret.addElement(new String("<K-B-ZERO> Path="+q+": Cayley term="+a+": KinMap term="+b));
				} else if (a.equals("0")) {
					// Message -- ++++
					ret.addElement(new String("<K-A-ZERO> Path="+q+": Cayley term="+a+": KinMap term="+b));
				} else {
					// Message -- not the same why ++++
					ret.addElement(new String("<K-OTHER> Path="+q+": Cayley term="+a+": KinMap term="+b));
				}
			}
		}
		return ret;
	}

	public String toString() {
		StringBuffer p = new StringBuffer();
		for(Enumeration e = keys();e.hasMoreElements();) {
			String q = (String) e.nextElement();
			String r = (String) get(q);
			p.append(q+"="+r+"\n");
		}
		return p.toString();
	}

/*	public boolean testIsomorphismX() {
		CayleyTable x = new CayleyTable(Algebra.getCurrent().getElements());
		x.generateProducts();
		return testIsomorphismX(x);
	}*/

	public CayleyTable linkAlgebraWithKinTermMap(CayleyTable x){
		StringVector misfits = new StringVector();
		return linkAlgebraWithKinTermMap(x,misfits);
	}
	
	public CayleyTable linkAlgebraWithKinTermMap(CayleyTable x,StringVector misfits){
        //  StringVector misfits;
          if (x == null) {
            x = new CayleyTable(Algebra.getCurrent().getElements());
            x.generateProducts();
          }
	//System.out.println(" HERE IS X "+x);
          if (!associateAlgebraGeneratorsWithKinshipGenerators(x.theProducts)) {
            System.out.println("+_+_+_+_+_+_+_+_+_+_+ Couldn't associate generators in associateAlgebraGeneratorsWithKinshipGenerators");
            return null;
          }
		StringVector mfits = new StringVector();
          if ((mfits = mapAlgebraProductsToKinshipTerms(x.getProducts())).size() != 0) {
            System.out.println("testIsomorphism: misfits="+misfits);
			  for (mfits.reset();mfits.isNext();){
				  misfits.addElement(mfits.getNext());
			  }
          }
//System.out.println("99999999999999999999999999"+x.getProducts()+"\n x"+x);
          x.generateCayleyProducts_new(this, algebraKin, algebraKinKeys);
	//System.out.println("CCXCXCXCXC algkkinkys "+algebraKinKeys.toString()+"\n----");
          return x;
        }

/*	public boolean testIsomorphismX(CayleyTable x) {
                x = linkAlgebraWithKinTermMap(x);
                if (x == null) return false;
		/*StringVector misfits;
		if (x == null) {
		    System.out.println("IT WAS NULLLLLLLLLLLLLLLL");
			x = new CayleyTable(Algebra.getCurrent().getElements());
			x.generateProducts();
		}
		//System.out.println("Cayley KintermMap=\n"+toString()+"\n----------------");
		if (!associateAlgebraGeneratorsWithKinshipGenerators(x.theProducts)) {
			// Message -- Couldn't associate generators ++++
			System.out.println("+_+_+_+_+_+_+_+_+_+_+ Couldn't associate generators in associateAlgebraGeneratorsWithKinshipGenerators");
			return false;
		}

		if ((misfits = mapAlgebraProductsToKinshipTerms(x.getProducts())).size() != 0) {
			// Message -- Some basic Cayley row headers are not defined in the kin temp map ++++
			System.out.println("testIsomorphism: misfits="+misfits);
		}
	//	System.out.println("algebraKin="+algebraKin);
	//	System.out.println("algebraKinKeys="+algebraKinKeys);
//System.out.println("Algebra kin");
		x.generateCayleyProducts_new(this, algebraKin, algebraKinKeys);

//System.out.println(" this "+this);
//System.out.println("testIsomorphism: +_+_+_+_+_+_+_+_+_+_+ Generated Cayley products");

*/
/*
		StringVector testAgainstMe = x.compareCayleyTables(this);
//System.out.println("testIsomorphism: +_+_+_+_+_+_+_+_+_+_+ Compared this against Cayley products");
		StringVector testAgainstIt = compareCayleyTables(x);
//System.out.println("testIsomorphism: +_+_+_+_+_+_+_+_+_+_+ Compared  Cayley products against this ");



		if ( testAgainstMe.size() == 0 && testAgainstIt.size() == 0) {
			//System.out.println("Isomorphic! testAgainstMe="+testAgainstMe+" testAgainstIt="+testAgainstIt);
			System.out.println("Isomorphic! ");
			return true;
		} else if (testAgainstMe.size() == 0 ) {
			System.out.println("IHalf-isomorphic Me! ");
			//System.out.println("Half-isomorphic Me! testAgainstMe="+testAgainstMe+" testAgainstIt="+testAgainstIt);
			return false;
		} else if (testAgainstIt.size() == 0 ){
			System.out.println("Half-isomorphic It! testAgainstMe="+testAgainstMe+" testAgainstIt="+testAgainstIt);
			//System.out.println("IHalf-isomorphic It! ");
			return false;
		} else {
		//	System.out.println("Not isomorphic Me! ");
		//	System.out.println("Not Isomorphic! testAgainstMe="+testAgainstMe);
			System.out.println("Not Isomorphic! testAgainstMe="+testAgainstMe+" testAgainstIt="+testAgainstIt);
			return false;
		}
//undo all ssytmeoou

	}*/

	public ListVector testIsomorphism(StringVector misfits) {
		CayleyTable x = new CayleyTable(Algebra.getCurrent().getElements());
		x.generateProducts();
		return testIsomorphism(x,misfits);
	}


	public ListVector testIsomorphism(CayleyTable x,StringVector misfits) {
          ListVector ret = new ListVector();
		//StringVector misfits = new StringVector();
         // System.out.println(" AT LINKKKKKKK x= "+x);
//System.out.println("78787878787878787878 START x= "+x);

          x = linkAlgebraWithKinTermMap(x,misfits);
//System.out.println("ONE AN DNO x= "+x+" misfits "+misfits);
          if (x == null) return null;
		//StringVector testAgainstMe = x.compareCayleyTables(this);
		//StringVector testAgainstIt = compareCayleyTables(x);
          ret.addElement(x.compareCayleyTables(this));
          ret.addElement(compareCayleyTables(x));
	//System.out.println("RET RET "+ret);
          return ret;
	}

/*	public StringVector mapAlgebraProductsToKinshipTermsXX(AlgebraPathVector a) {
		algebraKin.clear();
		StringVector ret = new StringVector();
		for(int i=0;i<a.size();i++) {
		    AlgebraPath ap = new AlgebraPath();
		    ap = (AlgebraPath)a.getSymbol(i).clone();
		   // System.out.println(" the oath "+ a+ " symbol "+a.getSymbol(i)+" i "+i);
			if (!addToAlgebraKin(ap.getEquivalentPathLeft())) {
				ret.addElement(ap.getEquivalentPathLeft().toString());
			//if (!addToAlgebraKin(a.getSymbol(i).getEquivalentPathLeft())) {
			//	ret.addElement(a.getSymbol(i).getEquivalentPathLeft().toString());
			}
		}
		return ret;
	}*/

	public StringVector mapAlgebraProductsToKinshipTerms(AlgebraPathVector a) {
		algebraKin.clear();
		StringVector ret = new StringVector();
		for(int i=0;i<a.size();i++) {
		   // System.out.println(" the oath "+ a+ " symbol "+a.getSymbol(i)+" i "+i);
			if (!addToAlgebraKin(a.getSymbol(i))) {
				Debug.prout(4," at ret ");
				ret.addElement(a.getSymbol(i).toString());
			}
		}
		return ret;
	}

	Hashtable kintab = new Hashtable(100);

	TransferKinInfo getKintabTerm(String s) {
		return ((TransferKinInfo) kintab.get(s));
	}

	/** (re-)builds based on information in incoming TransferKinInfoVector structure
	*/
	public void buildKinTermMap() { // note addition of etc
		if (theKinTerms != null) buildKinTermMap(theKinTerms);
	}
	/** (re-)builds based on information in incoming TransferKinInfoVector structure
	* @param tk the kinship term info
	*/

	public void buildKinTermMap(TransferKinInfoVector tk) { // note addition of etc
		theKinTerms = tk;
		TransferKinInfo k,m;
		this.clear();
		kintab.clear();
		for (int i=0;i<tk.size();i++) {
			k = (TransferKinInfo) tk.elementAt(i);
			kintab.put(k.term,k);
		}
		StringVector tgen = tk.getEffectiveGenerators().toStringVector();
	//System.out.println("ZZZZZZZZZZZZZZZZeffective "+tgen);
	//System.out.println("QQQQQQQQQQQQQQQQQQ gens "+tk.getGenerators());
	//System.out.println(" the whole thing "+tk);
		for (tk.reset();tk.isNextEffectiveTerm();) {
			k = tk.getNextEffectiveTerm();
			for(tgen.reset();tgen.isNext();) {
				String agen = tgen.getNext();
				//if (agen.equals("[bake2, bake1]")) continue;
				StringVector r = tk.getEffectiveProducts(k,agen);
		//if (k.getTerm().equals("baba"))
		//System.out.println(" PRODUCTS "+ tk.getProducts(k,agen)+ " r "+r);
				//if (agen.equals("Spouse")){
					//{
				//System.out.println("agen "+agen+" k "+ k +" here's r "+r);
				//System.out.println(" agen again "+ k.getProducts());
				//}
				for(r.reset();r.isNext();) {
					String s = r.getNext();
					if (coveredGenerators(s,tk)) {
						StringVector rr = tk.getProducts(k,agen);
						for (rr.reset();rr.isNext();) {
						    String ss = rr.getNext();
				//System.out.println("AAA term "+ k.term+" agen "+  agen+" trns "+ ((TransferKinInfo) kintab.get(ss)).sex+" sex "+ss);
					        mapTerm( k.term,  agen, ((TransferKinInfo) kintab.get(ss)).sex,ss);
						}
					}
					mapTerm( k.term,  agen, ((TransferKinInfo) kintab.get(s)).sex,s);
				}
			}
		}
		tk.getEffectiveFocalTerms();
	}

	boolean coveredGenerators(String term, TransferKinInfoVector tk){
	    TransferKinInfo tki = tk.lookupTerm(term);
		if (tki.isGenerator()) return false;
		if (tki.isCovered.isTrue()) {
			StringVector sv = tki.getCoveredTerms();
			if (sv.size() < 2) return false;
			for (sv.reset();sv.isNext();){
			    if (!tk.lookupTerm(sv.getNext()).isGenerator())
					return false;
			}
			return true;
		}
		return false;
		//return false;
	}

	/* return sex marked versions of generator
	* @gen generator
	* @sexes sex markers
	* @prods algebra elements
	* @ret sex marked versions of generator based on sexes
	*/
	public AlgebraPathVector getGenSexProducts(AlgebraSymbol gen, AlgebraSymbolVector sexes, AlgebraPathVector prods) {
		AlgebraPathVector ret = new AlgebraPathVector();
		AlgebraPath sexed = new AlgebraPath(gen);
		if (prods.indexOf(sexed) != -1) ret.addElement(sexed);
		else if (prods.splitEquivalentPaths().indexOf(sexed) != -1)ret.addElement(sexed);
		if (gen.getSex().equals("M")||gen.getSex().equals("F")) return ret;
		for (sexes.reset();sexes.isNext();) {
			AlgebraSymbol sex = sexes.getNext();
			sexed = new AlgebraPath(sex,gen);
			if ((prods.indexOf(sexed) != -1) && (ret.indexOf(sexed) == -1)){
				ret.addElement(sexed);
			}
		}
		return ret;
	}
		static final int m = 1;
		static final int f = 2;
		static final int n = 4;


	public int matchTermsAndSexes(AlgebraSymbolVector asv, AlgebraSymbolVector sexes,
	    TransferKinInfoVector tkv, AlgebraPathVector algproducts,
	    AlgebraSymbol maleGen, AlgebraSymbol femaleGen,
	    String arrow) {
            String ckk_term="";
            int count = 0;
            for (asv.reset();asv.isNext();) {
                AlgebraSymbol as = asv.getNext();
				boolean maleFlag = (as.getSex().equals("M"));
				boolean femaleFlag = (as.getSex().equals("F"));
				AlgebraPathVector gprods = getGenSexProducts(as,sexes,algproducts);
				int mask = 0;
			//System.out.println(" AS "+as+" XXXXgprods="+gprods+" algprods "+algproducts+" sexes "+sexes);
			//System.out.println("algsymbol as="+as+" malegen="+maleGen+" femalegen="+femaleGen+" mf "+maleFlag+" ff "+femaleFlag);
				for(tkv.reset();tkv.isNext();) {
					TransferKinInfo ckk = tkv.getNext();
					ckk_term = ckk.getTerm();
					//System.out.println(" chkk_term "+ckk_term+" sex "+ckk.getSex());
					if (ckk.getSex().equals("M")) {
						if (((maleFlag)&& (gprods.indexOf(new AlgebraPath(as)) != -1)) ||
						 (gprods.indexOf(new AlgebraPath(maleGen,as)) != -1)) {
							if ((mask & m) == m) {
								Message.create(Mode.ALL,"Already entered a male "+arrow+" generator. "
												+ckk_term+" cannot be matched.", "",null,2);
								// Message .. already defined male downarrrow
								continue;
							}
							if ((maleGen == null) && !maleFlag) continue;
							if (maleFlag) associateGenerators( as.getValue(),ckk_term) ;
							else
								associateGenerators( maleGen.getValue()+as.getValue(),ckk_term) ;
						mask |= m;
						count++;
						}
					} else if (ckk.getSex().equals("F")) {
						if (((femaleFlag)&& (gprods.indexOf(new AlgebraPath(as)) != -1)) ||
								gprods.indexOf(new AlgebraPath(femaleGen,as)) != -1) {
							if ((mask & f) == f) {
								Message.create(Mode.ALL,"Already entered a female "+arrow+" generator. "
												+ckk_term+" can not be matched.", "",null,2);
								// Message .. already defined male downarrrow
								continue;
							}
							if ((femaleGen == null)&& !femaleFlag) continue;
							if (femaleFlag) associateGenerators( as.getValue(),ckk_term);
							else
								associateGenerators( femaleGen.getValue()+as.getValue(),  ckk_term) ;
							mask |= f;
							count++;
						}
					} else  if (ckk.getSex().equals("N")) {
						if (gprods.indexOf(new AlgebraPath(as)) != -1) {
							if ((mask & n) == n) {
								//System.out.println(" mask "+ mask +" n "+n);
								Message.create(Mode.ALL,"Already entered a neutral "+arrow+" generator. "
												+ckk_term+" can not be matched.", "",null,2);
								// Message .. already defined male downarrrow
								continue;
							}
							mask |= n;
							associateGenerators(as.getValue(),  ckk_term) ;
								Debug.prout(" what is as "+as.getValue()+" ckkxk "+ckk_term);
							count++;
						}
					}
			    }
              }

	//	if (maleGen != null)
	//	Message.create(Mode.ALL,"male gen is"+maleGen.getValue()+" can not be matched.", "",null,2);

	//	if (femaleGen != null)
	//	Message.create(Mode.ALL,"female gen is"+femaleGen.getValue()+" can not be matched.", "",null,2);


		if (count == 0) {
                        String theGens = "";
                        for (asv.reset();asv.isNext();){
                          theGens = theGens + asv.getNext().getValue()+" ";
                        }
			Message.create(Mode.ALL,"No matches were found for the "+arrow+" generator "+theGens+". "
							+"The term "+ckk_term+" cannot be matched with "+theGens+".", "",null,2);
		}
		if (count < tkv.size()) {
                System.out.println(" TKV IS "+tkv+" COUNT "+count);
                System.out.println(" generators "+generators+" generatingTerms "+generatingTerms);
			Message.create(Mode.ALL,"Not all "+arrow+" generators were matched. "
							+" Sex should be introduced in the algebra..", "",null,7);
		}
		return tkv.size()-count;
               // return count;
	}

//something wrong with return true ??
	public boolean associateAlgebraGeneratorsWithKinshipGenerators(AlgebraPathVector algproducts) {
          if (generators.size() > 0){
	    if (generators.size() != theKinTerms.getEffectiveGenerators().size()) {
              generators.clear();
              generatingTerms.clear();
              return associateTheAlgebraGeneratorsWithKinshipGenerators(algproducts);
            }
	  else
              return true;
	    }
          else return associateTheAlgebraGeneratorsWithKinshipGenerators(algproducts);
        }

	public boolean associateTheAlgebraGeneratorsWithKinshipGenerators(AlgebraPathVector algproducts) {
//System.out.println(" EFFECTIVE "+theKinTerms.getEffectiveGenerators());
		theKinshipAlgebra.makeAlg.theAlgebra = Algebra.getCurrent();
		AlgebraSymbolVector k = theKinshipAlgebra.getAlgebraSymbols();
//System.out.println(" symbols k "+k+" algproducts "+algproducts);
		StringVector ksv = theKinTerms.getFocalTerms();
		AlgebraSymbolVector asv = theKinshipAlgebra.makeAlg.theAlgebra.getFocalElements();

		int nSexes = 0;//purpose of nSexes is not clear
		int mask=0;
		//boolean maleFlag = false; boolean femaleFlag = false;
		boolean returnFlag = true;

		if (ksv.size() != asv.size()) {
			Message.create(Mode.ALL,"Mismatch in number of focal terms between Algebra and Kinterm Map.",
				"Mismatch in numbers of focal terms between Algebra and Kinterm Map. Kinterm focal(s)="
				+ksv+" Algebra focals="+asv+"\nAlgebraSymbols="+theKinshipAlgebra.makeAlg.theAlgebra.getElements()+
				"\nEquations="+theKinshipAlgebra.makeAlg.theAlgebra.getEquations(),null,1);
			return false;
		}

		AlgebraSymbol maleGen = null;
		AlgebraSymbol femaleGen = null;
		AlgebraSymbolVector sexes = new AlgebraSymbolVector(3);
		// may need some work since we need to reconcile if our M and F are really
		// male and female or vice versa. May need marked NEUTRAL added (M&F)
		for (int i = 0;i<k.size();i++) {
			AlgebraSymbol as = k.getSymbol(i);
		//System.out.println("XXXXXXXX AS "+as+" sex "+as.getSex()+" thype "+as.getArrowType());
			if (as.toString().equals("0")) continue;
			if (as.toString().equals("&")) continue;
			if (theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.MALEFEMALE)) {
		//System.out.println("IN HERE HRE HERE ");
				if (as.getSex(). equals("M")) sexes.addElement(maleGen = as);
				else if (as.getSex(). equals("F")) sexes.addElement(femaleGen = as);
				nSexes++;
			}

			else if (!(as.toString().equals("M")) && as.getSex().equals("M")) {
			  // maleFlag = true;
			   nSexes++;
			}
			else if (!(as.toString().equals("F")) && as.getSex().equals("F")) {
			   //sexes.addElement(femaleGen = as);
			  // femaleFlag = true;
			   nSexes++;
			}

		}
		if (ksv.size() == 1) { // will not necessarily work if sex is not N
			AlgebraSymbol xa = asv.getSymbol(0);
			String xk = ksv.getSymbol(0);
	//System.out.println("nsexes "+nSexes+" kin "+xk+" sex "+theKinTerms.lookupTerm(xk).getSex()+" alg "+xa + " sex "+xa.getSex());
			if (nSexes > 1 && !theKinTerms.lookupTerm(xk).getSex().equals(xa.getSex())) return false;
			associateGenerators(xa.getValue(),xk);
                        //System.out.println(" what is xza "+xa+" xk "+k);
			//
		} else { // big hairy multi-focal element figuring routine ****
		}
            AlgebraSymbolVector down = new AlgebraSymbolVector(2,1);
            AlgebraSymbolVector up = new AlgebraSymbolVector(2,1);
            AlgebraSymbolVector spouse = new AlgebraSymbolVector(2,1);
            AlgebraSymbolVector left = new AlgebraSymbolVector(2,1);
            AlgebraSymbolVector right = new AlgebraSymbolVector(2,1);
			for (asv.reset();asv.isNext();) {

			for (int i = 0;i<k.size();i++) {
				AlgebraSymbol as = k.getSymbol(i);
				if (as.isFocalElement() || as.getValue().equals("0") || as.toString().equals("&")) continue;
				//Debug.on(-2);
				Debug.prout(2,"assoicate_1: "+as);
				//two following procedures do not distinguish right from left
				//if (theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.LEFT)) continue;
				//if (theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.RIGHT)) continue;
		//System.out.println(" LEFTRIGHT as "+as.getArrowType(theKinshipAlgebra.makeAlg.theAlgebra));
				AlgebraPath xx= new AlgebraPath(as,as);
				if (xx.getReducedProductPath().size() == 1) {
					if (xx.getReducedProductPath().getSymbol(0).equals(as)) {
						// needs filling in... how to distinguish left and right arrows
						//continue;//temporary solution
					}
				}
				// Need to deal with other stuff.... right left spouse

				Debug.prout(2,"assoicate_2: "+as);

				/*AlgebraSymbol rec = as.getReciprocal();
				if (rec == null) continue;

				AlgebraPath aa = new AlgebraPath(rec,as);
				Debug.prout(2,"assoicate_3: "+as);
				AlgebraPath ab = new AlgebraPath(as,rec);

				boolean ba = asv.indexOf(aa.getReducedProductPath().getSymbol(0)) != -1;
				boolean bb = asv.indexOf(ab.getReducedProductPath().getSymbol(0)) != -1;*/
	//System.out.println("AS AS ASD AS "+as +" type "+as.getArrowType());
				boolean ba = as.getArrowType() == Bops.DOWN;// theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.DOWN);
				boolean bb = as.getArrowType() == Bops.UP;//theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.UP);
				boolean bc = as.getArrowType() == Bops.SPOUSE;//theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.SPOUSE);
				boolean bd = false; // = theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.SPOUSER);
				boolean be = theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.MALE);
				boolean bf = theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.FEMALE);
				boolean bg = as.getArrowType() == Bops.LEFT;
				boolean bh = as.getArrowType() == Bops.RIGHT;
				/*boolean be = (as.getArrowType() == Bops.SEXGEN && as.getSex().equals("M"));//
				//.makeAlg.theAlgebra.isArrow(as,Bops.MALE);
				boolean bf = (as.getArrowType() == Bops.SEXGEN && as.getSex().equals("F"));//theKinshipAlgebra.makeAlg.theAlgebra.isArrow(as,Bops.FEMALE);*/
				if (ba & bb) {
					// Message - a problem -- can't distinguish up or down arrow ++++
					Message.create(Mode.ALL,"Can't determine if "+as+ " is an up or down arrow.",
					"Verify whether or not "+as+" should be an ascendant or descendant "+
									"generator in the kin term map.",null,1);
					//System.out.println("mapAlgebraElementsToKinshipTerms: "+
							//"can't distinguish up or down arrow as="+as);
					returnFlag = false;
					continue;
				}
				if (ba) {
					// downarrow
					TransferKinInfoVector tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.DOWN);
					if (tkv.size() == 0) {
						// Message - no match for down arrow ++++
						  Message.create(Mode.ALL,"Can't match down arrow "+ as+".",
						  "The algebra element "+as+" is descendant but no kin term corresponds to a down arrow."
										  ,null,1);
						//System.out.println("mapAlgebraElementsToKinshipTerms: "+
						//	"can't match down arrow as="+as);
						returnFlag = false;
						continue;
					}
					down.addElement(as);
					//int count=0;
					//count = matchTermsAndSexes(as, sexes, tkv, algproducts, maleGen, femaleGen,
					//                            as,"Down");
					continue;
				} else if (bb) {
					// uparrow
					TransferKinInfoVector tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.UP);
					if (tkv.size() == 0) {
						// Message - no match for uparrow ++++
						  Message.create(Mode.ALL,"Can't match up arrow "+ as+".",
						  "The algebra element "+as+" is ascendant but no kin term corresponds to an up arrow."
										  ,null,1);
						//System.out.println("mapAlgebraElementsToKinshipTerms: "+
						//	"can't match up arrow as="+as);
						returnFlag = false;
						continue;
					}
					up.addElement(as);
					//int count=0;
					//count = matchTermsAndSexes(as, sexes, tkv, algproducts, maleGen, femaleGen,
					//                            as,"Up");
					continue;
				}  else if (bc) {
					// spousearrow
					TransferKinInfoVector tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.SPOUSE);
					if (tkv.size() == 0) {
						// Message - no match for spouse arrow ++++
								  Message.create(Mode.ALL,"Can't match spouse arrow "+ as+".",
						  "The algebra element "+as+" is a spouse but the kin term map does not have a spouse element."
										  ,null,1);
							//	System.out.println("mapAlgebraElementsToKinshipTerms: "+
							//"can't match spouse arrow as="+as);
						returnFlag = false;
						continue;
					}
					spouse.addElement(as);
					//int count=0;
					//count = matchTermsAndSexes(as, sexes, tkv, algproducts, maleGen, femaleGen,
					//                            as, "Spouse");
					continue;
				} else if (bg) {
					// leftarrow
					TransferKinInfoVector tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.LEFT);
					if (tkv.size() == 0) {
						// Message - no match for left arrow ++++
								  Message.create(Mode.ALL,"Can't match left arrow "+ as+".",
						  "The algebra element "+as+" is a left term but the kin term map does not have a left term."
										  ,null,1);
						returnFlag = false;
						continue;
					}
					left.addElement(as);
					continue;
				}else if (bh) {
					// rightarrow
					TransferKinInfoVector tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.RIGHT);
					if (tkv.size() == 0) {
						// Message - no match for right arrow ++++
								  Message.create(Mode.ALL,"Can't match right arrow "+ as+".",
						  "The algebra element "+as+" is a right arrow but the kin term map does not have a right term."
										  ,null,1);
							//	System.out.println("mapAlgebraElementsToKinshipTerms: "+
							//"can't match right  as="+as);
						returnFlag = false;
						continue;
					}
					right.addElement(as);
					continue;
				}
				else if (be) continue;
				else if (bf) continue;
				// Message -- couldn't determine arrow type ++++
					Message.create(Mode.ALL,"Can't determine arrow type of "+as+ " .",
					"Verify whether or not "+as+" should correspond to an ascendant or descendant "+
									"generator in the kin term map.",null,1);
					returnFlag = false;
					//System.out.println("mapAlgebraElementsToKinshipTerms: "+
							//"can't determine  arrow type as="+as);
			}
					int count = 0;
					TransferKinInfoVector tkv = null;
					if (down.size() != 0) {
					  tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.DOWN);
			  //System.out.println(" up "+up+" down "+down+" spoue "+spouse+" algprods "+algproducts);
					  count = matchTermsAndSexes(down, sexes, tkv, algproducts, maleGen, femaleGen,
											   "Down");
					}
					if (up.size() != 0) {
					  tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.UP);
					  count = matchTermsAndSexes(up, sexes, tkv, algproducts, maleGen, femaleGen,
											   "Up");
					  if (count != 0) returnFlag = false;
					}
					if (spouse.size() != 0) {
					  tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.SPOUSE);
					  count = matchTermsAndSexes(spouse, sexes, tkv, algproducts, maleGen, femaleGen,
											   "Spouse");
					  if (count != 0) returnFlag = false;
					}
					if (left.size() != 0) {
						//System.out.println("in LEFT");
					  tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.LEFT);
					  count = matchTermsAndSexes(left, sexes, tkv, algproducts, maleGen, femaleGen,
											   "Left");
					  if (count != 0) returnFlag = false;
					}
					if (right.size() != 0) {
						//System.out.println("in RIGHT");
					  tkv = theKinTerms.getEffectiveGenerators(TransferKinInfoVector.RIGHT);
					  count = matchTermsAndSexes(right, sexes, tkv, algproducts, maleGen, femaleGen,
											   "Right");
					  if (count != 0) returnFlag = false;
					}

			Debug.prout(0,"--------------------------Generators="+generators);
			Debug.prout(0,"--------------------------GeneratingTerms="+generatingTerms);
				  }
          return returnFlag;

	}

	public void setTheKinTerms(TransferKinInfoVector theKinTerms) {
		this.theKinTerms = theKinTerms;
		theKinshipAlgebra = new KinshipAlgebra(theKinTerms);
	}

	public TransferKinInfoVector getTheKinTerms() {
		return theKinTerms;
	}
}

/*
	public void __addToAlgebraKinOLDOONE(AlgebraPath ap, int ndx, String fe,String aCurrentTerm, boolean isEtc, AlgebraSymbol lastGenerator) {
	    AlgebraPath ap1 = new AlgebraPath();
	    AlgebraPathVector apv = new AlgebraPathVector();
		if (ap.equivalentPath()) {
		    ap1=(new AlgebraPath(ap.reducedPath)).getEquivalentPathLeft();
		    apv.addElement(ap1);
		    ap1 = (new AlgebraPath(ap.reducedPath)).getEquivalentPathRight();
	        ap1.reducePath(ap1.getReducedProductPath());
		    if (ap1.getReducedProductPath().toString().equals(ap1.path.toString()))
		        apv.addElement(ap1);
		}
		else {
		    ap1 = ap;
		    apv.addElement(ap1);
		}
		String ycurrentTerm=aCurrentTerm;
		boolean yisEtc=isEtc;
		AlgebraSymbol ylastGenerator=lastGenerator;
		int yndx = ndx;
		int ii = -1;
		for (apv.reset();apv.isNext();) {
		    ii++;
		    String currentTerm = new String();
		    currentTerm = ycurrentTerm;
			isEtc = yisEtc; lastGenerator=ylastGenerator;ndx=yndx;
		    AlgebraPath thep = (AlgebraPath)apv.getNext();
//System.out.println(" thep "+thep+" ii "+ii);
		if (ndx >= thep.reducedPath.size()) {

		//wasif (ndx >= ap1.reducedPath.size()) {
		//if (ndx >= ap.getReducedProductPath().size()) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
		//System.out.println(" AP Again "+ap);
			if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
			algebraKin.put(ap.toString(),currentTerm);
			return; // check for etc. eg algebra too small for ktm
		}
//	 AlgebraSymbol a=(new AlgebraPath(ap.reducedPath)).getEquivalentPathLeft().getReducedProductPath().getSymbol(ndx);
	AlgebraSymbol a=thep.getReducedProductPath().getSymbol(ndx);
	 //new was   AlgebraSymbol a=ap.getEquivalentPathLeft().getReducedProductPath().getSymbol(ndx);
	   // was AlgebraSymbol a=ap.getReducedProductPath().getSymbol(ndx);


		if (a.getValue().equals("F")) {//test for sex generator
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if (( tki != null) && (tki.getSex().equals("F"))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
		if (a.getValue().equals("M")) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if ((tki != null) && (tki.getSex().equals("M"))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
		if (a.getValue().equals ("0")) {
			currentTerm = "<Undefined>";
			algebraKin.put(ap.toString(),currentTerm);
			return;
		//} else if (a.getValue().equals ("I")) {
		} else if (a.isIdentityElement()) {
	        currentTerm = getMatchingFocalTerm(fe);
			if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
			algebraKin.put(ap.toString(),currentTerm);
			return;
		}
		StringVector a2k = generatorToGenderedGenerators(a.toString());
//System.out.println(" a " + a + " a2k "+a2k);
		String xcurrentTerm=currentTerm;
		boolean xisEtc=isEtc;
		AlgebraSymbol xlastGenerator=lastGenerator;
		for(int i=0;i<a2k.size();i++) {
			currentTerm = xcurrentTerm; isEtc = xisEtc; lastGenerator=xlastGenerator;
			String  gsex="N", gterm=a2k.getSymbol(i);
			if (gterm.length() > 1) gsex = a2k.getSymbol(i).substring(0,1);
			else gsex = Algebra.getCurrent().findElement(gterm).getSex();
			String oldCur = currentTerm;
//		System.out.println(" lastgen "+lastGenerator+" cur term "+currentTerm+" gsex "+gsex);
			currentTerm = getTerm(currentTerm, generatorToMapgen(gterm),gsex);
		//	System.out.println(" get curterm "+currentTerm+" gterm "+gterm+" gentomap "+generatorToMapgen(gterm));
			if (currentTerm.equals("<Undefined>") && !gsex.equals("N")) {
				currentTerm = getTerm(oldCur, generatorToMapgen(gterm),"N");
			//System.out.println(" new curterm "+currentTerm+" gterm ");

			}
			if (!currentTerm.equals("<Undefined>")) {
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if (tki.isEtc()) {
					lastGenerator = a;
					isEtc = true;
				} else {
					isEtc = false;
				}
				AlgebraPath ap2 = new AlgebraPath();
				//wasap2 = ap.getEquivalentPathLeft();//modified
				ap2 = (AlgebraPath)thep.clone();//modified
				if (ndx+1 < ap2.getReducedProductPath().size()) {
					__addToAlgebraKin(ap,ndx+1,fe,currentTerm,isEtc,lastGenerator);
				} else {

					AlgebraSymbolVector ap3 = new AlgebraSymbolVector();
				    boolean flag = false;
                    if (ap.equivalentPath()&& (ap3 = ap2.getReducedProductPath()).size()==1){
					    if (generators.indexOf(ap3.elementAt(0).toString()) != -1){
					        flag = true;
					    }
					}

				    flag = false;

				    if (!flag){
					if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);}
					//AlgebraSymbolVector ap3 = new AlgebraSymbolVector();
					//System.out.println(" ap " + ap.toString()+ " cur term "+currentTerm+" gen "+generators);
                    if (ap.equivalentPath()&& (ap3 = ap2.getReducedProductPath()).size()==1){
					    if (generators.indexOf(ap3.elementAt(0).toString()) != -1){
					        System.out.println("STUFF ap2 "+ap2.toString()+" cur term "+currentTerm+" IN ADD SUFFT");
					        //System.out.println("STUFF apRight "+ap.getEquivalentPathRight().toString()+" cur term "+"bake1"+" IN ADD SUFFT");
			                algebraKinKeys.addUnique(ap2);
				               // algebraKinKeys.addUnique(ap.getEquivalentPathRight());
			               //if (ii==0)  algebraKin.put(ap2.toString(),"bake2");
			              // else
					        algebraKin.put(ap2.toString(),currentTerm);
					        //algebraKin.put(ap.getEquivalentPathRight().toString(),currentTerm);
					    // algebraKin.put(ap2.toString(),"bake1");
					       // algebraKin.put(ap.getEquivalentPathRight().toString(),"bake1");
					    }
					}if (!flag){
					 algebraKin.put(ap.toString(),currentTerm);}
					//else algebraKin.put(ap.toString(),currentTerm);
				}
			}else{
				if (isEtc && a.equals(lastGenerator)) {
					currentTerm="etc+"; // possibly more later
				}
System.out.println(" ap "+ap+" term "+currentTerm);
				algebraKin.put(ap.toString(),currentTerm);
			}
		}
		}
	}


	public void __addToAlgebraKinOLD(AlgebraPath ap, int ndx, String fe,String currentTerm, boolean isEtc, AlgebraSymbol lastGenerator) {
		//System.out.println(" AP "+ap);
		//if (ndx >= ((AlgebraPath)ap.clone()).getReducedProductPath().size()) {
	    AlgebraPath ap1 = new AlgebraPath();
	//was	if (ap.equivalentPath()) ap1=ap.getEquivalentPathLeft();
		if (ap.equivalentPath()) ap1=(new AlgebraPath(ap.reducedPath)).getEquivalentPathLeft();
		else ap1 = ap;
		if (ndx >= ap1.reducedPath.size()) {

		//if (ndx >= ap.getReducedProductPath().size()) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
		//System.out.println(" AP Again "+ap);

		/*	boolean flag = (generators.indexOf(ap1.getReducedProductPath()) != -1);
		    if (!currentTerm.equals("etc+")) {
			    if {flag)
			        algebraKinKeys.addUnique(ap1);
	            else
			        algebraKinKeys.addUnique(ap);
			    }
			if (flag)
			    algebraKin.put(ap1.toString(),currentTerm);
			else
			    algebraKin.put(ap.toString(),currentTerm);
*/
/*
			if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
			algebraKin.put(ap.toString(),currentTerm);
			return; // check for etc. eg algebra too small for ktm
		}
	 AlgebraSymbol a=(new AlgebraPath(ap.reducedPath)).getEquivalentPathLeft().getReducedProductPath().getSymbol(ndx);

	 //new was   AlgebraSymbol a=ap.getEquivalentPathLeft().getReducedProductPath().getSymbol(ndx);
	   // was AlgebraSymbol a=ap.getReducedProductPath().getSymbol(ndx);


		if (a.getValue().equals("F")) {//test for sex generator
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if (( tki != null) && (tki.getSex().equals("F"))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
		if (a.getValue().equals("M")) {
			TransferKinInfo tki = getKintabTerm(currentTerm);
			if ((tki != null) && (tki.getSex().equals("M"))) {
				if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
				algebraKin.put(ap.toString(),currentTerm);
			}
			return;
		}
		if (a.getValue().equals ("0")) {
			currentTerm = "<Undefined>";
			algebraKin.put(ap.toString(),currentTerm);
			return;
		//} else if (a.getValue().equals ("I")) {
		} else if (a.isIdentityElement()) {
	        currentTerm = getMatchingFocalTerm(fe);
			if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
			algebraKin.put(ap.toString(),currentTerm);
			return;
		}
		StringVector a2k = generatorToGenderedGenerators(a.toString());
//System.out.println(" a " + a + " a2k "+a2k);
		String xcurrentTerm=currentTerm;
		boolean xisEtc=isEtc;
		AlgebraSymbol xlastGenerator=lastGenerator;
		for(int i=0;i<a2k.size();i++) {
			currentTerm = xcurrentTerm; isEtc = xisEtc; lastGenerator=xlastGenerator;
			String  gsex="N", gterm=a2k.getSymbol(i);
			if (gterm.length() > 1) gsex = a2k.getSymbol(i).substring(0,1);
			else gsex = Algebra.getCurrent().findElement(gterm).getSex();
			String oldCur = currentTerm;
//		System.out.println(" lastgen "+lastGenerator+" cur term "+currentTerm+" gsex "+gsex);
			currentTerm = getTerm(currentTerm, generatorToMapgen(gterm),gsex);
		//	System.out.println(" get curterm "+currentTerm+" gterm "+gterm+" gentomap "+generatorToMapgen(gterm));
			if (currentTerm.equals("<Undefined>") && !gsex.equals("N")) {
				currentTerm = getTerm(oldCur, generatorToMapgen(gterm),"N");
			//System.out.println(" new curterm "+currentTerm+" gterm ");

			}
			if (!currentTerm.equals("<Undefined>")) {
				TransferKinInfo tki = getKintabTerm(currentTerm);
				if (tki.isEtc()) {
					lastGenerator = a;
					isEtc = true;
				} else {
					isEtc = false;
				}
				AlgebraPath ap2 = new AlgebraPath();
				ap2 = ap.getEquivalentPathLeft();//modified
				if (ndx+1 < ap2.getReducedProductPath().size()) {
					__addToAlgebraKin(ap,ndx+1,fe,currentTerm,isEtc,lastGenerator);
				} else {
					if (!currentTerm.equals("etc+")) algebraKinKeys.addUnique(ap);
					AlgebraSymbolVector ap3 = new AlgebraSymbolVector();
					//System.out.println(" ap " + ap.toString()+ " cur term "+currentTerm+" gen "+generators);
                    if (ap.equivalentPath()&& (ap3 = ap2.getReducedProductPath()).size()==1){
					    if (generators.indexOf(ap3.elementAt(0).toString()) != -1){
					        System.out.println("STUFF ap2 "+ap2.toString()+" cur term "+currentTerm+" IN ADD SUFFT");
					        System.out.println("STUFF apRight "+ap.getEquivalentPathRight().toString()+" cur term "+"bake1"+" IN ADD SUFFT");
			                algebraKinKeys.addUnique(ap2);
			                algebraKinKeys.addUnique(ap.getEquivalentPathRight());
					        algebraKin.put(ap2.toString(),currentTerm);
					        //algebraKin.put(ap.getEquivalentPathRight().toString(),currentTerm);
					    // algebraKin.put(ap2.toString(),"bake1");
					        algebraKin.put(ap.getEquivalentPathRight().toString(),"bake1");
					    }
					}
					 algebraKin.put(ap.toString(),currentTerm);
					//else algebraKin.put(ap.toString(),currentTerm);
				}
			}else{
				if (isEtc && a.equals(lastGenerator)) {
					currentTerm="etc+"; // possibly more later
				}
System.out.println(" ap "+ap+" term "+currentTerm);
				algebraKin.put(ap.toString(),currentTerm);
			}
		}
	}


*/