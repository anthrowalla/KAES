import java.util.Hashtable;
import java.util.Stack;
//import java.util.Vector;
import java.awt.Color;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class KinTypeMapper extends Hashtable {

  //Stack pathelems = new Stack();
 // int gennumstack [] = new int [300];
 // int sp = -1;

  //AlgebraSymbol ident = Algebra.getCurrent().getIdentityElement();
	AlgebraSymbolVector generators = Algebra.getCurrent().getGenerators();
	//AlgebraSymbolVector focalElements = Algebra.getCurrent().getFocalElements();

	AlgebraPathVector inPath=null;
	AlgebraPathVector thePaths = new AlgebraPathVector();
	KinTermMap ktm;

	long serialNumber=0;

	public void setSerialNumber(long x) {
		serialNumber = x;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public boolean hasPath() {
		return inPath != null;
	}

	public KinTypeMapper() {
	//
	}

	public KinTypeMapper(CayleyTable p,KinTermMap km) {
		ktm = km;
		cayleyT = p;
		//    setGeneratorKinTypeEquivalences();
	}

	String removeString(String theString,String remove) {
		while (theString.indexOf(remove) > -1) {
			int i = theString.indexOf(remove);
			theString = theString.substring(0,i)+
					  theString.substring(i+remove.length(),theString.length());
		}
		return theString;
	}

	String insertString(String theString,String after,String insert) {
		String tmp = "";
		while (theString.indexOf(after) > -1) {
			int i = theString.indexOf(after);
			tmp = tmp+theString.substring(0,i+after.length())+insert;
			theString = theString.substring(i+after.length(),theString.length());
		}
		theString = tmp + theString;
		return theString;
	}

	String replaceString(String theString,String replace,String by) {
		while (theString.indexOf(replace) > -1) {
			theString = replaceTheString(theString,replace,by);
			/* int i = theString.indexOf(replace);
			theString = theString.substring(0,i)+by+
					  theString.substring(i+replace.length(),theString.length());*/
		}
		return theString;
	}

	String replaceTheString(String theString,String replace,String by) {
		int i = theString.indexOf(replace);
		  // System.out.println(" theString "+theString+" replace "+replace+" by "+by +" i "+i);
		if (i > -1) {
			theString = theString.substring(0,i)+by+
					  theString.substring(i+replace.length(),theString.length());
		}
		return theString;
	}

	public Vector toData(Vector col) {
		int cap = col.capacity();
		// buildAllPaths();//deleted 2/15
		ListVector data = new ListVector(10,5);
		boolean undefinedFlag = MainFrame.prefs.getBoolean("Print_undefined_kintype_products_in_Kintype_Products_Table",true);
		//System.out.println(" IN PATH "+inPath);
		//System.out.println(" THIS IS "+this);
		boolean egoFlag = MainFrame.prefs.getBoolean("Print_ego_in_Kintype_Products_Table",false);
		AlgebraPathVector aPath = (AlgebraPathVector) inPath.clone();
		for(aPath.reset();aPath.isNext();) {
			String s = aPath.getNext().toString();
			if (!undefinedFlag && s.equals("0")) continue;//don't print paths that are not a kin term
			Debug.prout(4," THE PATH s "+s);
			// if (s.indexOf("x")== 0) continue;
			AlgebraKinType qq = (AlgebraKinType) get(s);
			// System.out.println(" BEFORE qq.kinTypes "+qq.kinTypes);
		/*	if (!s.equals("0")){
			    qq.removeRedundancy();//don't change undefined products
				qq.replaceBySibType();//These are also in genealogicalGrid
			}*/
			qq.orderByLength(); //ordered by length in modifyKinTypes
			// System.out.println(" AFTER qq.kinTypes "+qq.kinTypes);
			// AlgebraKinType qq = (AlgebraKinType) get(aPath.getNext().toString());
			Vector row = new Vector(cap);
			ColorTerm term = new ColorTerm(Color.black);
			String sex = qq.algPath.getPathSex();
			//System.out.println(" line 2 "+sex);
			if (sex.equals("M")) term.setTheColor(Color.blue);
			else if (sex.equals("F")) term.setTheColor(Color.red);
			//System.out.println(" line 3 ");
			if (alg2KinTerm(qq.path) == null || alg2KinTerm(qq.path).equals("")) term.setTheTerm(qq.path);
			else term.setTheTerm(alg2KinTerm(qq.path));
			//System.out.println(" line 4 ");
			row.addElement(term);
			//if (alg2KinTerm(qq.path) == null || alg2KinTerm(qq.path).equals("")) row.addElement(qq.path);
			//else row.addElement(alg2KinTerm(qq.path));
			//System.out.println(" line 5 ");
			String out = qq.kinTypes.toString();
			//System.out.println(" line 6 "+out);
			//String txt = "";
			int j = out.indexOf(",");
			/*if (2 < j && out.indexOf("ego") != -1 && out.indexOf("ego") < j) {
				if (out.indexOf("]") < j)
					txt = out.substring(out.indexOf("ego"),out.indexOf(",")-1);
				else
					txt = out.substring(out.indexOf("ego"),out.indexOf(","));
			}*/
		//System.out.println(" out "+out.toString());
			out = replaceTheString(out,"ego],","eg*],");
			out = replaceTheString(out,"ego-M],","eg*-M],");
			out = replaceTheString(out,"ego-F],","eg*-F],");
			out = removeString(out,",");
			out = removeString(out," ");
			out = replaceString(out,"][",", ");
			//if (!txt.equals(""))
			//	out = insertString(out,txt," ");
			out = replaceString(out,"ego-F","eg*-F");
			out = replaceString(out,"ego-M","eg*-M");
			if (!egoFlag) out = replaceString(out,"ego","");
			else out = insertString(out,"ego","'s ");
			out = replaceTheString(out,"eg*,","ego,");
			out = replaceTheString(out,"eg*-M,","male ego,");
			out = replaceTheString(out,"eg*-F,","female ego,");
			out = replaceString(out,"eg*-M","male ego's ");
			out = replaceString(out,"eg*-F","female ego's ");
			//out = insertString(out,"ego-F"," ");
			//out = insertString(out,"ego-M"," ");
			//out = replaceTheString(out,"ego,","ego ,");
			out = replaceTheString(out,"[[","{");
			out = replaceTheString(out,"]]","}");
			Debug.prout(4," OUT "+head(out.toString()));
			row.addElement(out);
			data.addElement(row);
		}
		System.out.println(" DATA "+head(data.toString()));
		return (Vector)data;
	}

	public String head(String r) {
		int n = 300;
		if (r.length() < n) return r;
		return r.substring(1,300);
	}

	public Vector toColumnNames() {
		Vector column = new Vector();
		column.addElement("Algebra Element");
		column.addElement("Kin Type Products");
		return column;
	}

	CayleyTable cayleyT=null;

	/* procedure for construction kin type product paths and converting
	* paths to algebra elements
	*/
	public boolean buildAllPaths(CayleyTable cly) {
	    return buildAllPaths(cly,"N");
	}
	//Hashtable aTmp = new Hashtable();

	public boolean buildAllPaths(CayleyTable cly,String sex) {
		if (getSerialNumber() == Algebra.getCurrent().getSerialNumber()) return true;
		if (true) {cayleyT = cly; //dwr 8-8
			ListVector akts = new ListVector();
			//PREFERENCES
			
			int upG = MainFrame.prefs.getInt("Maximum_ascending_links",2);
			int downG = MainFrame.prefs.getInt("Maximum_descending_links",2);
			int coll = MainFrame.prefs.getInt("Maximum_collateral_links",2);
			int aff = MainFrame.prefs.getInt("Maximum_affinal_links",1);
			buildInterestingPaths(upG,downG,coll,aff,0,sex);//2 = lo gen, 2 = higen, 1 = collateral, 0 = affinal , 0 = wander
			//buildInterestingPaths(2,2,1,1,0,sex);//2 = lo gen, 2 = higen, 1 = collateral, 0 = affinal , 0 = wander
			buildArrowsex2Path();
			buildSym2Gen(syms);
			Hashtable tmp = new Hashtable();
			//Hashtable aTmp = new Hashtable();
			//System.out.println("sym2Gen="+sym2Gen.toString());
			LinealDescendantRule ldr = (LinealDescendantRule) RuleFactory.getRule(RuleFactory.LINEALDESCENDANTRULE);
			for(interestingPaths.reset();interestingPaths.isNext();) {
				String a;
				a = (String) interestingPaths.getNext();
				
				//System.out.println("first interesting path a = "+toSymString(a));
				StringVector sv = new StringVector();
				sv.addElement(a);
				
				//AlgebraPathVector av = kts2Alg(sv);
				ListVector lv = kts2Alg(sv);
				sv.reset();
				for (lv.reset();lv.isNext();){
					AlgebraPathVector av = (AlgebraPathVector)lv.getNext();
					//a = (String)sv.elementAt(0);
					a = (String)sv.getNext();
					System.out.println("interesting path a = "+toSymString(a)+" av "+av);
					if (av.size() == 0) continue;
					// if (toSymString(a).equals("[ego-Mzd]"))
					//Debug.prout(0,"THE AV "+av);
					for (int i=0;i<av.size();i++){
						AlgebraPath ap = (AlgebraPath)av.elementAt(i);
						if ((AlgebraPath)cly.getApRules().get(ap.toString()) != null){
							//av.removeElementAt(i);
							System.out.println(" get aprules "+ap.toString()+" result "+((AlgebraPath)cly.getApRules().get(ap.toString())).toString());
							av.setElementAt((AlgebraPath)cly.getApRules().get(ap.toString()),i);
						}
					}
					//String avs = av.toString();
					/*if (aTmp.get(avs) == null) {
						Debug.prout(0," THE AV START av "+av + " prod "+((AlgebraPath)av.elementAt(0)).getProductPath().toStringVector()+" a "+toSymString(a));
					cly.applyRules(Algebra.getCurrent().getRules(),av);
					Debug.prout(0,"THE AV again "+av);
					//cly.populateProtoCayleyHash();
					cly.applyRules(Algebra.getCurrent().getRules(),cly.protoCayleyHash,av);//XXXX temporary
						aTmp.put(avs,av);						
					} else av = (AlgebraPathVector)aTmp.get(avs);*/
					//System.out.println("THE AV again again "+av);
					//LinealDescendantRule ldr = (LinealDescendantRule) RuleFactory.getRule(RuleFactory.LINEALDESCENDANTRULE);
					//may need to do this for all rules
					for(av.reset();av.isNext();) {
						AlgebraPath ap = av.getNextSymbol();
						//LinealDescendantRule ldr = (LinealDescendantRule) RuleFactory.getRule(RuleFactory.LINEALDESCENDANTRULE);
						if (ldr != null && ldr.activeRule){
							for (;;){
								if (ldr.rewriteProduct(ap.toString()) != null)
									ap = ldr.rewriteProduct(ap.toString());
								else break;
							}
						}
						AlgebraKinType at = (AlgebraKinType) tmp.get(ap.toString());
						if (at == null) {
							at = new AlgebraKinType(ap);
							tmp.put(ap.toString(),at);
							if (inPath == null) inPath = new AlgebraPathVector();
							inPath.addElement(ap);
							akts.addElement(at);
						}
						//System.out.println("ap prodpath = "+ ap.getProductPath()+"ap = "+ap.toString1()+ " a="+toSymString(a));
						//at.kinTypes.addElement(toListVector(a));
						at.kinTypes.addElement(toStringVector(a));
					}
					
				}
				
				
			}
			clear();
			/*		if (inPath != null && cly != null) {
			System.out.println(" START RULES in path"+inPath);
			if (Algebra.getCurrent().getRules() != null ) {
			System.out.println(" APPLY START RULES");
			cly.applyRules(Algebra.getCurrent().getRules(),inPath);
			System.out.println("INPATH Between"+inPath);
			cly.applyRules(Algebra.getCurrent().getRules(),cly.protoCayleyHash,inPath);
			System.out.println("INPATH "+inPath);
			}
			}*/
			Debug.prout(0,"INPATH "+inPath);
			if (inPath == null) return false;
			inPath.clear();
			AlgebraSymbolVector afq = Algebra.getCurrent().getFocalElements();
			for (afq.reset();afq.isNext();) {
				AlgebraSymbol xa=afq.getNext();
				AlgebraPath xp = new AlgebraPath(xa);
				AlgebraKinType xk = new AlgebraKinType(xp);
				// ListVector xv = new ListVector();
				/*StringVector xv = new StringVector();
				if (xa.getSex().equals("F")) xv.addElement("ego-F");
				else if (xa.getSex().equals("M")) xv.addElement("ego-M");
				else xv.addElement("ego");
				xk.kinTypes.addElement(xv);*/ //now handled in build interesting paths
				put(xk.path,xk);
				inPath.addElement(xp);
			}
			boolean modifyFlag = MainFrame.prefs.getBoolean("Do_not-simplify_kintype_products_in_Kintype_Products_Table",false);
			//System.out.println(" modify flag "+modifyFlag);
			if (!modifyFlag) 
				modifyKinTypeProducts(akts);
			for(akts.reset();akts.isNext();){
				AlgebraKinType akt;
				akt = (AlgebraKinType) akts.getNext();
				akt.path = akt.algPath.toString();
				System.out.println(" algPath.path "+akt.algPath.path+" red "+akt.algPath.reducedPath+" akt.path "+akt.path);
				AlgebraKinType older;
				older = (AlgebraKinType) get(akt.path);
				if (older == null) {
		//System.out.println("New: "+akt.path+" kintypes="+akt.kinTypes);
					put(akt.path,akt);
					inPath.addElement(akt.algPath);
				} else {
					// System.out.println("Old- "+akt.path);
				//System.out.println("Old: "+akt.path+" kintypes="+akt.kinTypes+"older akt "+older.kinTypes);
				if (older == akt) continue;
					older.kinTypes.append(akt.kinTypes);
				}
			}
			//	System.out.println("KintermMapper = "+ toString());
		//	modifyKinTypeProducts(akts);
			
			
			
			setSerialNumber(Algebra.getCurrent().getSerialNumber());
		} else {
			 System.out.println("Cayley.fullPaths=\n"+cly.getFullProducts().toString());
			cayleyT = cly;
			ListVector cpaths = cly.getFullProducts();
			for(cpaths.reset();cpaths.isNext();) {
				CayleyPathInfo cpi = (CayleyPathInfo) cpaths.getNext();
				AlgebraKinType ak = alg2Kin(cpi);
				put(ak.path,ak);
				if (inPath == null) inPath=new AlgebraPathVector();
				inPath.addElement(cpi.product);
				//System.out.println("IN PATH ELEMENT "+cpi.product.toString());
			}
		}
		return true;
	}

	public AlgebraKinType alg2Kin(CayleyPathInfo cpi) {
		AlgebraKinType ak = new AlgebraKinType(cpi.product);
		ListVector akp = cpi.sfullPaths;

		for(akp.reset();akp.isNext();) {
			AlgebraSymbolVector ap = (AlgebraSymbolVector) akp.getNext();
			String blah = algPath2Kin(ap);
			ak.addKinType(blah);
		}
		return ak;
	}

	public String algPath2Kin(AlgebraSymbolVector ap) {
		StringBuffer sb = new StringBuffer();
		ap.reset();
		if (ap.isNext()) {
			AlgebraSymbol stk = ap.getNext();
			for (;ap.isNext();) {
				AlgebraSymbol as = ap.getNext();
				if (as.toString().equals("M")) {
					if (stk == null) Debug.prout(0,"KinTypeMapper.algPath2Kin: sex generator without symbol");
					else sb.append(mapGeneratorToKinType(stk,"M"));
					stk=null;
				} else if (as.toString().equals("F")) {
					if (stk == null) Debug.prout(0,"KinTypeMapper.algPath2Kin: sex generator without symbol");
					else sb.append(mapGeneratorToKinType(stk,"F"));
					stk=null;
				} else {
					if (stk != null) {
						sb.append(mapGeneratorToKinType(stk,stk.getSex()));
						stk = as;
					} else {
						stk = as;
					}
				}
			}
			if (stk != null) {
				if (stk.toString().equals("M")) {
					Debug.prout(0,"KinTypeMapper.algPath2Kin: sex generator without symbol at end of path");
				} else if (stk.toString().equals("F")) {
				    Debug.prout(0,"KinTypeMapper.algPath2Kin: sex generator without symbol at end of path");
				} else {
				    sb.append(mapGeneratorToKinType(stk,stk.getSex()));
				}
			}
		}
		return sb.toString();
	}

	public String mapGeneratorToKinType(AlgebraSymbol g, String sex) {
		String sm = "";
		String sf = "";
		String sn = "";

		if (g.isFocalElement()) {
			// if (g.isIdentityElement()) return "ego";
			if (sex.equals("M")) return "ego-M";
			else if (sex.equals("F"))  return "ego-F";
			else return "ego";
		} else {
			int i = g.getArrowType();
			// if (g.getSex().equals(g.theSymbol)) i = 99;
			// System.out.println("g ="+g +" i "+i);
			switch (i){
				case Bops.UP:
				   sf = "m"; sm = "f"; sn="p";
				   break;
				case Bops.DOWN:
				   sf = "d";sm = "s"; sn="c";
				   break;
				case Bops.LEFT:
				   sf = "z+"; sm = "b+";  sn="s+";
				   break;
				case Bops.RIGHT:
				   sf = "z-"; sm = "b-"; sn="s-";
				   break;
				case Bops.SIDE:
				   sf = "z"; sm = "b"; sn="sb";
				   break;
				case Bops.SPOUSE:
				   sf = "w"; sm = "h";  sn="g";
				   break;
				default:
				   sf = "?"; sm = "??"; sn="???";
				   break;
			}
			if (sex.equals("M")) return(sm);
			else if (sex.equals("F")) return(sf);
			else {
				return sn;
				//	((AlgebraKinType)get(as+"gen")).addKinType(sm);
				//	((AlgebraKinType)get(as+"gen")).addKinType(sf);
			}
		}
	}

	ListVector interestingPaths = new ListVector();
	StringBuffer tstack = new StringBuffer();

	public String toSym(char sym) {
		return syms[sym];
	}

	public ListVector toListVector(StringBuffer sb) {
		ListVector ret = new ListVector();
		for (int i=0;i<sb.length();i++) {
			int cx = (int) sb.charAt(i);
			//  System.out.println("cx="+cx);
			ret.addElement(syms[cx]);
		}
		return ret;
	}

	public ListVector toListVector(String sb) {
		ListVector ret = new ListVector();
		for (int i=0;i<sb.length();i++) {
			int cx = (int) sb.charAt(i);
			//  System.out.println("cx="+cx);
			ret.addElement(syms[cx]);
		}
		return ret;
	}

	public StringVector toStringVector(String sb) {
		StringVector ret = new StringVector();
		for (int i=0;i<sb.length();i++) {
			int cx = (int) sb.charAt(i);
			//  System.out.println("cx="+cx);
			ret.addElement(syms[cx]); 
		}
		return ret;
	}


	public String toSymString(StringBuffer sb) {
		StringBuffer ret = new StringBuffer();
		for (int i=0;i<sb.length();i++) {
			int cx = (int) sb.charAt(i);
			//  System.out.println("cx="+cx);
			ret.append(syms[cx]);
		}
		return "["+ret.toString()+"]";
	}

	public String toSymString(String sb) {
		StringBuffer ret = new StringBuffer();
		for (int i=0;i<sb.length();i++) {
			int cx = (int) sb.charAt(i);
			ret.append(syms[cx]);
		}
		return "["+ret.toString()+"]";
	}

	public static int MALE=1;
	public static int FEMALE=2;
	public static int UP=4;
	public static int DOWN=8;
	public static int RCOLL=16;
	public static int LCOLL=32;
	public static int COLL=64;
	public static int OLDER=128;
	public static int YOUNGER=256;
	public static int AFFINAL=512;
	public static int AFFINAL_PATH=1024;
	public static int EGO=2048;

	public  int OVER = (LCOLL|RCOLL|COLL);
	public  int MALE_FEMALE = (MALE|FEMALE);
	public  int PASS = AFFINAL_PATH;



//below is the modification I made to include 2 ego symbols; I tried including 3 symbols -- the third
//would be just "ego" with neutral sex, but my recollection is that it had problems.  The number of ego
//symbols also relates to the menu choices under Operations, and the option for a kin type table
//below is the way I set it up to use one set of arrays with descriptive terminologies and a different
//set of arrays for classificatory terminologies -- it works, but is definitely not elegant!

	int symt[] = new int[128];

	String symsC[] = {  "ego",				  "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",       "l",				"l+",				"l-",			"b",        "b-",       "b+",       "z",        "z-",       "z+",   "g",								"h",                        "w"};
	int propertiesC[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE_FEMALE,LCOLL|MALE_FEMALE,RCOLL|MALE_FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
	int masksC[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE_FEMALE,LCOLL|MALE_FEMALE,RCOLL|MALE_FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
	int conditionsC[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		UP|AFFINAL,			DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL ,OVER|DOWN,		OVER|DOWN,		OVER|DOWN,			OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,							AFFINAL,                  AFFINAL};

	String symsD[] = {  "ego",				  "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",   "g",								"h",                        "w"};
	int propertiesD[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
	int masksD[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
	int conditionsD[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		AFFINAL,			DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,							AFFINAL,                  AFFINAL};

	String symsD1[] = {  "ego",				  "ego-M",             "ego-F",					"f",      "m",      "s",		"d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",   "g",								"h",                        "w"};
	int propertiesD1[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
	int masksD1[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
	int conditionsD1[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,			DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,							AFFINAL,                  AFFINAL};
	
	int nEgo = 3; int isEgo = 1; //number of ego symbols above; currently 3 
	
	String syms[] = null;
	int iSyms = 21;//maximum number of symbols in symsC or symsD
	int properties[] = null;
	int masks[] = null;
	int conditions[] = null;
 
	int symIndex[] = new int[iSyms]; int symIndex_index=-1;
	boolean cFlag = false; boolean dFlag = false;
	
	void setArrays(){
		if (Algebra.getCurrent().hasSibGenerators()){
			syms = symsC;
			properties = propertiesC;  
			masks = masksC;
			conditions = conditionsC;
		}else if (Algebra.getCurrent().getSexGenerators().size() != 0) {
			syms = symsD1;
			properties = propertiesD1;
			masks = masksD1;
			conditions = conditionsD1;				
		}else{
			syms = symsD;
			properties = propertiesD;
			masks = masksD;
			conditions = conditionsD;				
		}
		cFlag = false; dFlag=false;
		if (Algebra.getCurrent().hasSibGenerators()) cFlag = true;
		else dFlag = true;
		symIndex_index=-1;
		for(int i=nEgo;i < syms.length;i++) {
			System.out.println(" generator "+syms[i]);
			if (isGen((char) i)) {
				symIndex[++symIndex_index] = i;
				System.out.println("Entering generator "+syms[i]);
			}			
		}
	}
		
 
	public void buildInterestingPaths(int logen, int higen, int ncoll, int naffinal, int wander, String sex) {
		interestingPaths.clear();
		setArrays();
	//System.out.println("nEgo "+nEgo+" sex "+sex);
		if (sex.equals("N")) {
			tstack.append((char) 0);
			interestingPaths.addElement(tstack.toString());
		} else if (sex.equals("M")) {
			tstack.append((char) 1);
			interestingPaths.addElement(tstack.toString());
		} else if (sex.equals("F")) {
			tstack.append((char) 2);
			interestingPaths.addElement(tstack.toString());
		}
		//buildInterestingPaths(nEgo,0,logen, higen, ncoll, naffinal, wander);
		buildInterestingPaths(0,0,logen, higen, ncoll, naffinal, wander);
	}
	
/* public void buildInterestingPathsY(int logen, int higen, int ncoll, int naffinal, int wander, String sex) {
		interestingPaths.clear();
		buildInterestingPaths(0,0,logen, higen, ncoll, naffinal, wander);
	}*/


	public void buildInterestingPaths(int symbol, int status, int logen, int higen, int ncoll, int naffinal, int wander) {
		if ((status & AFFINAL_PATH) != 0 && wander == 0) return;
		int xlogen=logen, xhigen=higen, xcoll=ncoll, xaffinal=naffinal, xwander=wander;
		boolean failed=true;
		int nstatus=status;
		int props = properties[symIndex[symbol]];
		int conds = conditions[symIndex[symbol]] & status;
		if (conds == 0) {
			if ((props & UP) != 0 && higen > 0) {
				failed = false;xhigen--;xlogen++;
			}  else if ((props & DOWN) != 0 && logen > 0)  {
				failed = false;xhigen++; xlogen--;
			} else if ((props & OVER) != 0 &&  ncoll > 0) {
			    failed = false;xcoll--;
			} else if ((props & AFFINAL) != 0 && (status & MALE_FEMALE & props) == 0 && naffinal > 0) {
			    failed = false;xaffinal--;
			}
			if ((status & AFFINAL_PATH) != 0 && wander > 0) xwander--;
		}
		if (!failed) {
			nstatus |= props; // a keeper - saves sex and direction
			nstatus &= (masks[symIndex[symbol]]|PASS); // clear bits
			tstack.append((char) symIndex[symbol]);
			if (tstack.length() > isEgo) {
				//System.out.println("S="+symbol+" D="+status+" P="+nstatus+" lo="+xlogen+" hi="+xhigen+" C="+xcoll+" A="+xaffinal+" W="+wander+" T="+toSymString(tstack));
				interestingPaths.addElement(tstack.toString());
			}
			//buildInterestingPaths(nEgo, nstatus, xlogen, xhigen,xcoll, xaffinal, xwander);
			buildInterestingPaths(0, nstatus, xlogen, xhigen,xcoll, xaffinal, xwander);
			tstack.setLength(tstack.length()-1);
		}
		symbol++;
		//System.out.println("symbol "+symbol+" logen "+logen+" higen "+higen+" ncoll "+ncoll);
		if (symbol >= symIndex.length) return;
		buildInterestingPaths(symbol, status, logen, higen, ncoll, naffinal, wander);
		return;
	}

/*	public int lookupSymXX(String s) {
		//for (int i=0;i<syms.length;i++) dwr 7-17
		for (int i=nEgo;i<syms.length;i++)
		if (syms[i].equals(s)) return i;
		return -1;
	}*/

	public boolean isGen(char sy) {
		 int arrow=-1; String sex="N";
		 int props = properties[sy];

		 if ((props & UP) != 0) arrow=Bops.UP;
		 else if ((props & DOWN) != 0) arrow=Bops.DOWN;
		 else if ((props & LCOLL) != 0) arrow=Bops.LEFT;
		 else if ((props & RCOLL) != 0) arrow=Bops.RIGHT;
		 else if ((props & AFFINAL) != 0) arrow=Bops.SPOUSE;
		 else if ((props & COLL) != 0) arrow=100; //assumes collateral w/o side is left side
		 else if ((props & EGO) != 0) arrow=Bops.IDENTITY;

		 if (((props & MALE) != 0) && ((props & FEMALE)!= 0)) sex="N";
		 
		 else if ((props & MALE) != 0) sex="M";
		 else if ((props & FEMALE)!= 0)  sex="F";//add neutral case?? dwr 7-17
		//else if ((props & MALE_FEMALE)!= 0)  sex="N";//add neutral case?? dwr 7-17
		// int nsex = props & MALE_FEMALE;
		 if (arrow == 100) {
			 if (Algebra.getCurrent().getGenerators(Bops.LEFT,sex) != null && Algebra.getCurrent().getGenerators(Bops.RIGHT,sex) != null) return false;
			 arrow = Bops.LEFT;
		 } else if (arrow == Bops.LEFT) {//CHECK THIS
			 if (Algebra.getCurrent().getGenerators(Bops.RIGHT,sex) == null) return false;
		 }
		 AlgebraSymbol a = Algebra.getCurrent().getGenerators(arrow,sex);
		System.out.println(" arrow "+arrow +" sex "+sex+" a "+a);
		 //System.out.println(" a "+a);
		 if (a != null) return true;
		// return a != null;
		 
		 AlgebraSymbol sx = null; boolean sexM = false; boolean sexF = false;
		 sexM = ((sx = Algebra.getCurrent().findElement("M")) != null && sx.isSexGenerator());		 
		 sexF = ((sx = Algebra.getCurrent().findElement("F")) != null && sx.isSexGenerator());
		 if (arrow != Bops.IDENTITY && (sexF || sexM)){
			 if (Algebra.getCurrent().getGenerators(arrow,"N") != null) {
				 if ((sex.equals("M") && sexM) || (sex.equals("F") && sexF)) return true;
			 }
			 return false;
		 } 
		 return false;
		// AlgebraSymbol a = Algebra.getCurrent().getGenerators(arrow,sex);
		// System.out.println(" arrow "+arrow +" sex "+sex+" a "+a);
		 //System.out.println(" a "+a);
		 //return a != null;
	}
 
 
 
	public ListVector kintype2Gen(char sy) {
		int arrow=-1; String sex="N";
		int props = properties[sy];
		ListVector ret = new ListVector();
		AlgebraSymbolVector asv = new AlgebraSymbolVector();
		if ((props & UP) != 0) arrow=Bops.UP;
		else if ((props & DOWN) != 0) arrow=Bops.DOWN;
		else if ((props & LCOLL) != 0) arrow=Bops.LEFT;
		else if ((props & RCOLL) != 0) arrow=Bops.RIGHT;
		else if ((props & AFFINAL) != 0) arrow=Bops.SPOUSE;
		else if ((props & COLL) != 0) {
			//if (ktm.theKinshipAlgebra.makeAlg.getAlgebraType() == MakeAlgebra.TONGAN ||
			  //  ktm.theKinshipAlgebra.makeAlg.getAlgebraType() == MakeAlgebra.TROB)
			//switch (ktm.theKinshipAlgebra.makeAlg.getAlgebraClass()) {
				//case MakeAlgebra.CLASSIFICATORY:
			//if ((ktm.theKinshipAlgebra.makeAlg.getAlgebraType() & MakeAlgebra.CLASSIFICATORY) != 0)
			arrow=Bops.LEFT;//assumes collateral w/o side is left side
					//break;
				//case MakeAlgebra.DESCRIPTIVE:
				   // arrow=Bops.SIDE;//not implemented -- DR 8/26
					//break;
			//}
		}
		else if ((props & EGO) != 0) arrow=Bops.IDENTITY;
		if (((props & MALE) != 0) && ((props & FEMALE)!= 0)) sex="N";
		else if ((props & MALE) != 0) sex="M";
		else if ((props & FEMALE)!= 0)  sex="F";//add neutral case?? dwr 7-17
		int nsex = props & MALE_FEMALE;if (nsex == 3) nsex = 0;//CHECK THIS 7/23 dwr
		//find all candidates
		//System.out.println(" sy "+sy+" arrow "+arrow +" nsex "+nsex);
		AlgebraSymbol a = arrowsex2Path[arrow][nsex][0];
// System.out.println(" a = "+a+" male "+ arrowsex2Path[arrow][MALE][0]);
		int sexndx;
		if (a == null) {
			switch (nsex) {
				case 0: a = arrowsex2Path[arrow][MALE][0];
					if (a == null) return ret;
					asv.addToEnd(a);
					//ret.addElement(asv);
					a = arrowsex2Path[arrow][FEMALE][0];
					if (a == null) {
						ret.clear();
						return ret;
					}
					break;
				case 2:
				case 1: a = arrowsex2Path[arrow][0][0];
					if (a == null) return ret;
					AlgebraSymbol sx;
					if ((sx = Algebra.getCurrent().findElement(sex)) != null && sx.isSexGenerator()) {
						asv.addToEnd(a);
						a = sx;
					}
					break;
				default: return ret;
			}
		}
		//System.out.println("fell thru "+" a$"+a+"$");
		asv.addToEnd(a);
		ret.addElement(asv);
		return ret;
	}

	ListVector [] sym2Gen = new ListVector[128];
	//AlgebraSymbol [][][] arrowsex2Path = new AlgebraSymbol [8][3][3];
	AlgebraSymbol [][][] arrowsex2Path = new AlgebraSymbol [9][3][3];

	public void buildArrowsex2Path() {
		for(int i=0;i<generators.size();i++) {
			int xarrow = generators.getSymbol(i).getArrowType();
			String xsex = generators.getSymbol(i).getSex();
			if (xarrow == -1) continue; // not defined
			Debug.prout(0,"arrow="+xarrow+" sex="+xsex+" symbol="+generators.getSymbol(i));
			arrowsex2Path[xarrow][xsex.equals("N")?0:xsex.equals("M")?1:2][0] = generators.getSymbol(i);
		}
	}
	
	void addFocalElementSym2Gen(String sex){
		AlgebraSymbol a = Algebra.getCurrent().getFocalElement(sex);//I
		AlgebraSymbolVector asv = new AlgebraSymbolVector();
		if (a != null) {
			asv.addElement(a);
			ListVector lv = new ListVector();
			lv. addElement(asv);
			if (sex.equals("M")) sym2Gen[1] = lv;
			else if (sex.equals("F")) sym2Gen[2] = lv;
			else if  (sex.equals("N")) sym2Gen[0] = lv;
		}
	}
 
 
	public void buildSym2Gen(String[] kts) {
		//for (int i=0;i<kts.length;i++) {
		addFocalElementSym2Gen("N");
		addFocalElementSym2Gen("M");
		addFocalElementSym2Gen("F");

		
		for (int i=nEgo;i<kts.length;i++) {
			Debug.prout(0,"xxx i="+i+" kts "+kts[i]);
			sym2Gen[i] = kintype2Gen((char) i);
			Debug.prout(0,"yyy i="+i+" alg="+ sym2Gen[i]);
		}
		//for (int i=0;i<kts.length;i++) { 
		for (int i=nEgo;i<kts.length;i++) {
			char sy = (char) i;
			int props = properties[sy];
			int sex = props&MALE_FEMALE;if (sex == 3) sex =0;
			//System.out.println(" sy "+sy+" i  "+i+" sym2Gen[sy] "+sym2Gen[sy]+" sym "+sym2Gen[i]);
			if (!cFlag && sym2Gen[sy].size() == 0){
				ListVector ret;
				ret = new ListVector();
				if ((props & OVER) != 0) {//over is up and down
					AlgebraSymbol up = arrowsex2Path[Bops.UP][0][0];
					if (up == null) {
						if (arrowsex2Path[Bops.UP][1][0] == null &&
						arrowsex2Path[Bops.UP][2][0] == null) {
							// error condition or no appropriate arrow;
						} else {
							if ((up = arrowsex2Path[Bops.UP][1][0]) != null)
							ret.addElement(new AlgebraSymbolVector(up));
							if ((up = arrowsex2Path[Bops.UP][2][0]) != null)
							ret.addElement(new AlgebraSymbolVector(arrowsex2Path[Bops.UP][2][0]));
						}
					} else ret.addElement(new AlgebraSymbolVector(up));
					AlgebraSymbol down = arrowsex2Path[Bops.DOWN][0][0];
					//System.out.println(" ret is "+ret +" up "+up+ " down "+down+" sex "+sex);
					if (down == null) {
						if (sex == 0) {
							down = arrowsex2Path[Bops.DOWN][1][0];
							AlgebraSymbol down2 = arrowsex2Path[Bops.DOWN][2][0];
							if (down != null && down2 != null){
								if (ret.size() == 1) {
									AlgebraSymbolVector ap = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
									ap.addToEnd(down);
									AlgebraSymbolVector ap2 = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
									ap2.addToEnd(down2);
									ret.clear();
									ret.addElement(ap);
									ret.addElement(ap2);
								} else {
									AlgebraSymbolVector ap = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
									ap.addToEnd(down);
									AlgebraSymbolVector ap2 = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
									ap2.addToEnd(down2);
									AlgebraSymbolVector ap3 = ((AlgebraSymbolVector) ret.elementAt(1)).copy();
									ap3.addToEnd(down);
									AlgebraSymbolVector ap4 = ((AlgebraSymbolVector) ret.elementAt(1)).copy();
									ap4.addToEnd(down2);
									ret.clear();
									ret.addElement(ap);
									ret.addElement(ap2);
									ret.addElement(ap3);
									ret.addElement(ap4);
								}
							} else continue;//error no appropriate products
						} else {//sex != 0
							down = arrowsex2Path[Bops.DOWN][sex][0];
							if (down == null) {
								continue;
								// can't proceed
							}
							addToRet(ret, down);
						}
					} else {
					if (sex == 0) {
						addToRet(ret, down);
						} else {
							String ssex = (sex == 1 ? "M" : "F");
							AlgebraSymbol mf = Algebra.getCurrent().findElement(ssex);
							if (mf == null) {
								Debug.prout("IN DOWN2 "+down);
								addToRet(ret, down);
							} else {
								if (mf.isSexGenerator()) {
									addToRet(ret, down);
									addToRet(ret, mf);
								} else {
									addToRet(ret, down);
								}
							}
						}
					}
					sym2Gen[sy] = ret;
				}
			}//else System.out.println("SKIPPED OVER");
		}
	}

	ListVector addToRet (ListVector ret, AlgebraSymbol down) {
		//System.out.println(" ret is "+ret +" down "+down);
		if (ret.size() == 1) {
			AlgebraSymbolVector ap = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
			ap.addToEnd(down);
			ret.clear();
			ret.addElement(ap);
		} else {
			AlgebraSymbolVector ap = ((AlgebraSymbolVector) ret.elementAt(0)).copy();
			ap.addToEnd(down);
			AlgebraSymbolVector ap3 = ((AlgebraSymbolVector) ret.elementAt(1)).copy();
			ap3.addToEnd(down);
			ret.clear();
			ret.addElement(ap);
			ret.addElement(ap3);
		}
		return ret;
	}
	ListVector lret = new ListVector();
 
	void _makeProd(AlgebraSymbol a){
		AlgebraPathVector ret = new AlgebraPathVector();
		AlgebraPath ap = new AlgebraPath();
		ap.product(a);
		ret.addElement(ap);
		lret.addElement(ret);
	}
 
	void _makeProd(AlgebraSymbol a, StringVector ktsv,boolean clear){
		String kts = (String)ktsv.elementAt(0);
		if (clear) ktsv.clear();
		AlgebraPathVector ret = new AlgebraPathVector();
		AlgebraPath ap = new AlgebraPath();
		ap.product(a);
		String s ="";
		if (a.getSex().equals("M")) s = (char)1+kts.substring(1,kts.length());
		else s = (char)2+kts.substring(1,kts.length());
		ktsv.addElement(s);
		ret.addElement(ap);
		lret.addElement(ret);
	}
 
	String mobOst = "";String mobYst = "";String fazOst = "";String fazYst = "";
	String palOst = "";String palYst = "";
	String mobst = "";String fazst = "";String palst = "";

	void modifyZeroPaSibProducts(StringVector ktsv){
		if (!mobOst.equals("") || !mobYst.equals("") || !fazOst.equals("") || !fazYst.equals("")
			|| !palOst.equals("") || !palYst.equals("")) return;
		AlgebraPath bOmop = new AlgebraPath();
		AlgebraPath bYmop = new AlgebraPath();
		AlgebraPath zOfap = new AlgebraPath();
		AlgebraPath zYfap = new AlgebraPath();
		AlgebraPath lOpap = new AlgebraPath();
		AlgebraPath lYpap = new AlgebraPath();
		int ibO = -1;int ibY = -1; int izO = -1; int izY = -1;int ilO = -1;int ilY = -1;
		int imo = -1;int ifa = -1;int ipa = -1;int ib = -1; int iz = -1;int il = -1;
		AlgebraSymbol bOs=null;AlgebraSymbol bYs = null;AlgebraSymbol zOs = null;
		AlgebraSymbol zYs = null;AlgebraSymbol lOs=null;AlgebraSymbol lYs = null;
		AlgebraSymbol mos= null;AlgebraSymbol fas= null;AlgebraSymbol pas= null;
		for (int i = 0; i < iSyms; i++){
			if (syms[symIndex[i]].equals("b+")) {
				bOs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ibO = symIndex[i];
			}else if (syms[symIndex[i]].equals("b-")){
				 bYs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ibY = symIndex[i];
			}else if (syms[symIndex[i]].equals("z+")){
				 zOs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				izO = symIndex[i];
			}else if (syms[symIndex[i]].equals("z-")){
				 zYs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				izY = symIndex[i];
			}else if (syms[symIndex[i]].equals("l+")){
				lOs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ilO = symIndex[i];
			}else if (syms[symIndex[i]].equals("l-")){
				lYs = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ilY = symIndex[i];
			}else if (syms[symIndex[i]].equals("f")){
				 fas = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ifa = symIndex[i];
			}else if (syms[symIndex[i]].equals("m")){
				 mos = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				imo = symIndex[i];
			}else if (syms[symIndex[i]].equals("n")){
				pas = (AlgebraSymbol)((AlgebraSymbolVector) sym2Gen[symIndex[i]].elementAt(0)).elementAt(0);
				ipa = symIndex[i];
			}
		//System.out.println(" i is "+i+" index is "+symIndex_index);
			if (syms[i].equals("b")) {
				symIndex[++symIndex_index] = i;
				//sym2Gen[i] = sym2Gen[1];//b --> I//needs to be redone; not clear what is the goal
				ib = i;
			}
			else if(syms[i].equals("z")) {
				symIndex[++symIndex_index] = i;
				//sym2Gen[i] = sym2Gen[2];//z --> i
				iz = i;
			}
			else if(syms[i].equals("l")) {
				symIndex[++symIndex_index] = i;
				//sym2Gen[i] = sym2Gen[0];//l --> I
				il = i;
			}
		}
		boolean flag1 = false;boolean flag2 = false;boolean flag3 = false;boolean flag4 = false;
		boolean flag5 = false;boolean flag6 = false;
		if (bOmop.product(mos)) bOmop.product(bOs);
		flag1 = bOmop.toString().equals("0");
		if (bYmop.product(mos)) bYmop.product(bYs);
		flag2 = bYmop.toString().equals("0");
		if (zOfap.product(fas)) zOfap.product(zOs);
		flag3 = zOfap.toString().equals("0");
		if (zYfap.product(fas)) zYfap.product(zYs);
		flag4 = zYfap.toString().equals("0");
		if (lOpap.product(fas)) lOpap.product(lOs);
		flag5 = lOpap.toString().equals("0");
		if (lYpap.product(pas)) lYpap.product(lYs);
		flag6 = lYpap.toString().equals("0");
		//String bOmost = "";String bYmost = "";String zOfast = "";String zYfast = "";
		if (flag1) {mobOst = mobOst+(char)imo;
			mobOst = mobOst+ (char)ibO;
		}
		if (flag2) {mobYst = mobYst + (char)imo;
			mobYst = mobYst+ (char)ibY;
		}
		if (flag3) {fazOst = fazOst + (char)ifa;
			fazOst = fazOst+ (char)izO;
		}
		if (flag4) {fazYst = fazYst + (char)ifa;
			fazYst = fazYst+ (char)izY;
		}
		if (flag5) {palOst = palOst + (char)ipa;
			palOst = palOst+ (char)ilO;
		}
		if (flag6) {palYst = palYst + (char)ipa;
			palYst = palYst+ (char)ilY;
		}
		mobst = mobst + (char)imo;
		mobst = mobst + (char)ib;
		fazst = fazst + (char)ifa;
		fazst = fazst + (char)iz;
		palst = palst + (char)ipa;
		palst = palst + (char)il;
		//System.out.println("mobOst = "+toSymString(mobOst));
		//System.out.println("mobYst = "+toSymString(mobYst));
		//System.out.println("fazOst = "+toSymString(fazOst));
		//System.out.println("fazYst = "+toSymString(fazYst));
		//System.out.println("mobst = "+toSymString(mobst));
		//System.out.println("fazst = "+toSymString(fazst));
	}
	
	 void initAlgProds(StringVector ktsv){
		 String kts = (String)ktsv.elementAt(0);
		// AlgebraPathVector ret = new AlgebraPathVector();
		// ListVector lret = new ListVector();
		 AlgebraPath ap; AlgebraPath bp;AlgebraPath abp; AlgebraPath bap;
		 ap = new AlgebraPath(); bp = new AlgebraPath();abp = new AlgebraPath();bap = new AlgebraPath();
		 AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");
		 AlgebraSymbol b = Algebra.getCurrent().getFocalElement("F");

		 boolean skipFlag = false;
		 boolean skipABFlag = false;
		 boolean skipBAFlag = false;
		 if (a != null && b != null && kts.length() > 1){
			ap.product(a); bp.product(b);
			abp.product(b); abp.product(a);
			bap.product(a); bap.product(b);
			//ListVector vk = sym2Gen[(int) kts.charAt(1)];
			AlgebraSymbolVector av = (AlgebraSymbolVector) sym2Gen[(int) kts.charAt(1)].elementAt(0);
			ap.product((AlgebraSymbol)av.elementAt(0));
			bp.product((AlgebraSymbol)av.elementAt(0));
			abp.product((AlgebraSymbol)av.elementAt(0));
			bap.product((AlgebraSymbol)av.elementAt(0));			
			String s = ap.toString();
			skipFlag = s.equals(bp.toString());
			if (skipFlag){
				skipABFlag = s.equals(abp.toString());
				skipBAFlag = s.equals(bap.toString());
			} else {
				skipABFlag = abp.toString().equals("0");
				skipBAFlag = bap.toString().equals("0");
			}		
			//ap = new AlgebraPath(); 
			//bp = new AlgebraPath();
			abp = new AlgebraPath();
			bap = new AlgebraPath();
		 }
		 
		 //System.out.println(" fe M "+a+" fe F "+b+" size "+ktsv.size());
		 AlgebraPathVector apv = cayleyT.getProducts();
		 if (kts.charAt(0) == (char) 0) { // neutral
			 AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
			 if (aa != null) {//ego and N
				 _makeProd(aa);
			 } else if (skipFlag){
				 _makeProd(a);
			 } else {//ego and M and/or F
				 if (a != null) {//ego and M
					// ktsv.clear();//replace ego by ego-M
					 _makeProd(a,ktsv,true);
					 if (b != null) {//ego and M and F
						 _makeProd(b,ktsv,false);
					 }
				 } else {
					 if (b != null) {//ego and F
						// ktsv.clear();//replace ego by ego-F
						 _makeProd(b,ktsv,true);
					 }
					 else {}//return ret;
				 }
				 if (a != null && b != null){
					 bap.product(a);
					 bap.product(b);
					 abp.product(b);
					 abp.product(a);					
				 }
			 }
		 } else if (kts.charAt(0) == (char) 1) { // male ego-M
			 AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
			 if (aa != null){//ego-M and N
				 _makeProd(aa);
			 } else {
				 if (a != null) {//ego-M and M
					 _makeProd(a);
				 } else{}//ego-M and F
			 }
			 if (a != null && b != null){
				 bap.product(a);
				 bap.product(b);
			 }
		 } else if (kts.charAt(0) == (char) 2) { //female
			 AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
			 if (aa != null) {//ego-F and N
				 _makeProd(aa);
			 } else {
				 if (b != null){//ego-F and F
					 _makeProd(b);
				 } 
				 else{}
			 }
			 if (a != null && b != null){
				 abp.product(b);
				 abp.product(a);					
			 }
		 }
		 
		 if (!skipABFlag && abp.getProductPath().size() != 0 && apv.indexOf(abp) != -1) {//ab of form Ii
			 AlgebraPathVector ret1 = new AlgebraPathVector();
			 bp = new AlgebraPath();
			 bp.product(b);
			 ret1.addElement(bp);
			 lret.addElement(ret1);
			 StringBuffer sb = new StringBuffer();
			 sb.append((char)2);
			 sb.append((char)1);
			 sb.append(kts.substring(1,kts.length()));
			 String s = sb.toString();
			 //System.out.println("in four");
			// String s = (char)2+kts.substring(1,kts.length());
			 ktsv.addElement(s);
			 //kts(0)= (char)2;
		 }
		 if (!skipBAFlag && bap.getProductPath().size() != 0 && apv.indexOf(bap) != -1) {//ba of form iI
			 AlgebraPathVector ret1 = new AlgebraPathVector();
			 ap = new AlgebraPath();
			 ap.product(a);
			 ret1.addElement(ap);
			 lret.addElement(ret1);
			 StringBuffer sb = new StringBuffer();
			 sb.append((char)1);
			 sb.append((char)2);
			 sb.append(kts.substring(1,kts.length()));
			 String s = sb.toString();
			 //	System.out.println("in seven");
			// String s = (char)1+kts.substring(1,kts.length());
			 ktsv.addElement(s);
			 //kts[0] = (char)1;
		 }	
	 }

 
	 void _addProd(AlgebraSymbol as,int sym, StringVector ktsv){
		 String kts = (String)ktsv.elementAt(0);
		 String kType = "";
		 if (kts.length() > 1) kType = toSym(kts.charAt(1));
		 AlgebraSymbol gen =(AlgebraSymbol) ((AlgebraSymbolVector)kintype2Gen((char)sym).elementAt(0)).elementAt(0);
		 AlgebraPath xap = new AlgebraPath();
		 AlgebraPathVector ret = new AlgebraPathVector();
		 xap.product(gen);
		 xap.product(as);
		 if ((xap.toString()).equals("0")) return;
		 if (!(xap.toString()).equals(as.toString()+gen.toString())) return;
		 // kts = (char)1+((char)sym+kts);
		 if (!kType.equals("") && !kType.equals("s") && !kType.equals("d") && !kType.equals("c")
			 && !kType.equals("h") && !kType.equals("w") && !kType.equals("g")) return;
		 AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");
		 AlgebraSymbol b = Algebra.getCurrent().getFocalElement("F");
		 AlgebraPath xapa = new AlgebraPath();
		 AlgebraPath xapb = new AlgebraPath();
		 AlgebraPath ap = new AlgebraPath();
		 AlgebraPath bp = new AlgebraPath();
		 xapa.product(a);
		 xapa.product(gen);
		 xapb.product(b);
		 xapb.product(gen);
		 StringBuffer sb = new StringBuffer();
		 StringBuffer sb1 = new StringBuffer();
		 if (((String)xapa.toString()).equals((String)xapb.toString())){
			 if(((String)xapa.toString()).equals("0")) return; 
			 else {
				 ap.product(a);
				 sb.append((char)0);//self
			 }
		 }else if (((String)xapa.toString()).equals("0")){
			 ap.product(b);
			 sb.append((char)2);//ego-F
		 }else if  (((String)xapb.toString()).equals("0")){
			 ap.product(a);
			 sb.append((char)1);//ego-M
		 }else{
			 ap.product(a);
			 bp.product(b);
			 sb.append((char)1);
			 sb1.append((char)2);
		 }
		// sb.append((char)1);
		 sb.append((char)sym);
		 if (as.getSex().equals("M")) sb.append((char)1);
		 else if (as.getSex().equals("F")) sb.append((char)2);
		 else if (as.getSex().equals("N")) sb.append((char)0);
		 else return;
		 sb.append(kts.substring(1,kts.length()));
		 String s = sb.toString();
		 String s1 = "";
		 if (bp.getReducedProductPath().size() != 0){
			 sb1.append((char)sym);
			 if (as.getSex().equals("M")) sb1.append((char)1);
			 else if (as.getSex().equals("F")) sb1.append((char)2);
			 else if (as.getSex().equals("N")) sb1.append((char)0);
			 else return;
			 sb1.append(kts.substring(1,kts.length()));
			 s1 = sb1.toString();
		 }
		 // String s = (char)1+kts.substring(1,kts.length());
		 ktsv.addElement(s);
		 // ktsv.addElement(kts);
		 ret.addElement(ap);
		 if (!s1.equals("")) {
			 ktsv.addElement(s1);
			 ret.addElement(bp);
		 } 		 
		 lret.addElement(ret);
		// System.out.println(" lret "+lret+" ktxv "+ktsv);
	 }
 
	 void initExtraAlgProds(StringVector ktsv){
		 AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");//I
		 AlgebraSymbol b = Algebra.getCurrent().getFocalElement("F");//i																 // kts = kts.substring(1,kts.length());//delete initial I char
		 for (int i=0;i<symIndex_index;i++){
			 int sym = symIndex[i];
			// AlgebraSymbol gen =(AlgebraSymbol) ((AlgebraSymbolVector)kintype2Gen((char)sym).elementAt(0)).elementAt(0);
			 if (a != null){
				 _addProd(a,sym,ktsv); 
			 }
			 if (b != null){
				 _addProd(b,sym,ktsv); 
			 }
			 
			// System.out.println(" adpr "+abp.toString()+" gen "+ gen.toString()+" symbol "+sym+ " abp "+abp+" ktsv "+ktsv+ " lret "+lret);
		 }
	 }
	public ListVector kts2Alg(StringVector ktsv) {
		lret.clear(); 
		initAlgProds(ktsv);//lret is built in this routine, so it must be always be called
		if (cFlag){//classificatory terminologies only
			//initAlgProds(ktsv);
			initExtraAlgProds(ktsv);
			modifyZeroPaSibProducts(ktsv);
		}
		//ktsv.clear();
		//ktsv.addElement(kts);
		//System.out.println(" ret.size "+lret.size()+ " ktsv.size "+ktsv.size());
		AlgebraPath ap = null;
		StringVector nktsv = new StringVector();
		lret.reset();//lret and ktsv are of same length and are traversed in parallel when lret <> null
		//for (ktsv.reset();ktsv.isNext();){
		for (int ii = 0;ii<ktsv.size();ii++){
			String kts1 = (String)ktsv.elementAt(ii);
			//String kts1 = (String) ktsv.getNext();
			cFlag = false;//something wrong with following, not clear why needed; kts1 is not text string
			if (cFlag) {
				kts1 = replaceTheString(kts1,fazOst,fazst);//replace fz+ by fz
				kts1 = replaceTheString(kts1,fazYst,fazst);
				kts1 = replaceTheString(kts1,mobOst,mobst);
				kts1 = replaceTheString(kts1,mobYst,mobst);
				ktsv.setElementAt(kts1,ii);			
			}
			AlgebraPathVector aret = (AlgebraPathVector)lret.getNext();
		//System.out.println(" aret "+aret+" string "+ toSymString(kts1));
			for(int i=1;i<kts1.length();i++) {//dwr 7-17
											  //for(int i=nEgo;i<kts.length();i++) {
				int cx = (int) kts1.charAt(i);
				//if (i==1 && (cx==0||cx==1||cx==2)) continue;//skip over iI or Ii
				ListVector vk;
				vk = sym2Gen[cx];
				if (vk != null) System.out.println("cx="+cx+" vk="+vk);
				else System.out.println("vk is null"+" cx = "+cx);
				if (vk == null) continue;
				int r = vk.size();
				if (r == 0) {
					// undefined path relative to algebra ... per haps message
					aret.clear();
					System.out.println("Bailing from kts2Alg r=0, cx = "+cx);
					//return ret;
					break;//check this
				}
				if (r > 1) {
					Debug.prout(0,"kts2Alg r>1");
					AlgebraPathVector tmp = (AlgebraPathVector) aret.clone();
					aret.clear();
					for(tmp.reset();tmp.isNext();) {
						ap = (AlgebraPath) tmp.getNext();
						AlgebraPath ax = (AlgebraPath) ap.clone();
						for(vk.reset();vk.isNext();) {
							//AlgebraPath ax = (AlgebraPath) ap.clone();
							//AlgebraSymbolVector av;
							AlgebraSymbolVector av = (AlgebraSymbolVector) vk.getNext();
							for(av.reset();av.isNext();) {
								ax.product(av.getNext());
								//System.out.println(" the next ap "+ap.toString()+" done "+done);
							}
							aret.addElement(ax);//check this out
						}
					}
				} else {
					for(aret.reset();aret.isNext();) {
						ap = aret.getNextSymbol();
						for(vk.reset();vk.isNext();) {
							AlgebraSymbolVector av;
							av = (AlgebraSymbolVector) vk.getNext();
							for(av.reset();av.isNext();) {
								ap.product(av.getNext());
							}
						}
					}
				}
				if (aret.size() > 1) {
					Debug.prout(0,"Culling aret");
					
					int n = aret.size();
					for(int j=0;j<aret.size()-1;j++) {
						for(int k=aret.size()-1;k > j; k--) {
							if (aret.getSymbol(k).equals(aret.getSymbol(j))) {
								aret.removeElementAt(k);
							}
						}
					}
				}
			}
		}
			//System.out.println(" lret size "+lret.size()+" ktsv size "+ktsv.size());
		return lret;
	}


	boolean flag = false;

	String alg2KinTerm(String s){
		String ret = s;
		if ((ktm != null) && (!flag)){
			if (!ktm.associateAlgebraGeneratorsWithKinshipGenerators(inPath))
			{};
			if (ktm.mapAlgebraProductsToKinshipTerms(inPath).size() == 0)
			{};
			flag = true;
		}
		if (ktm != null && ktm.algebraKin != null) ret = (String) ktm.algebraKin.get(s);
		return ret;
	}
 
	void modifyKinTypeProducts(ListVector lv){
		for(lv.reset();lv.isNext();){
			AlgebraKinType akt = (AlgebraKinType)lv.getNext();
			if (akt.path.toString().equals("0")) continue;
			//ListVector theTypes = new ListVector();
			//AlgebraKinType akt = ap;
			akt.expandKinTypes();
			akt.removeRedundancy();
			//akt.removeRedundancy();
			if (!Algebra.getCurrent().hasSibGenerators())
				akt.replaceBySibType();
			akt. replaceEgoBySibType();
			akt.replaceByEgo_SibType();
			akt.removeEgoRedundancy();
			akt.orderByLength();
			//System.out.println(" three kinTypes "+akt.kinTypes);
		}
		/*for(lv.reset();lv.isNext();){
			AlgebraKinType akt = (AlgebraKinType)lv.getNext();
			if (akt.path.toString().equals("0")) continue;
			akt.removeEgoRedundancy();
		}*/
	}
 
 }


/*public ListVector kts2AlgXXX(StringVector ktsv) {
ListVector lret = initAlgProds(ktsv);

//ktsv.clear();
//ktsv.addElement(kts);
//System.out.println(" ret.size "+lret.size()+ " ktsv.size "+ktsv.size());
String firstp = "";
int[] iremove = {0,0,0,0};
int j = -1;
lret.reset();
AlgebraPath ap = null;
for (ktsv.reset();ktsv.isNext();){
	j++;
	boolean done = false; boolean firstFlag = true;
	String kts1 = (String) ktsv.getNext();
	AlgebraPathVector aret = (AlgebraPathVector)lret.getNext();
	for(int i=1;i<kts1.length();i++) {//dwr 7-17
									  //for(int i=nEgo;i<kts.length();i++) {
		int cx = (int) kts1.charAt(i);
		if (i==1 && (cx==0||cx==1||cx==2)) continue;
		ListVector vk;
		vk = sym2Gen[cx];
		// if (vk != null) System.out.println("cx="+cx+" vk="+vk);
		//else System.out.println("vk is null"+" cx = "+cx);
		//if (vk == null) continue;
		int r = vk.size();
		if (r == 0) {
			// undefined path relative to algebra ... per haps message
			aret.clear();
			// System.out.println("Bailing from kts2Alg r=0, cx = "+cx);
			//return ret;
			break;//check this
		}
		if (r > 1) {
			Debug.prout(4,"kts2Alg r>1");
			AlgebraPathVector tmp = (AlgebraPathVector) aret.clone();
			aret.clear();
			for(tmp.reset();tmp.isNext();) {
				ap = (AlgebraPath) tmp.getNext();
				for(vk.reset();vk.isNext();) {
					AlgebraPath ax = (AlgebraPath) ap.clone();
					AlgebraSymbolVector av;
					av = (AlgebraSymbolVector) vk.getNext();
					for(av.reset();av.isNext();) {
						ax.product(av.getNext());
						if (firstp.equals("")){
							firstp = ap.toString();
							//System.out.println("22222222222222222222222222 ap is "+ap+" firstp "+firstp);
						} else if (firstFlag && firstp.equals(ap.toString())) {
							done = true;
							iremove[j] = 1;
						}
						//System.out.println(" the next ap "+ap.toString()+" done "+done);
						if (done) break;
					}
					if (done) break;
					aret.addElement(ax);//check this out
				}
				if (done) break;
			}
		} else {
			for(aret.reset();aret.isNext();) {
				ap = aret.getNextSymbol();
				for(vk.reset();vk.isNext();) {
					AlgebraSymbolVector av;
					av = (AlgebraSymbolVector) vk.getNext();
					for(av.reset();av.isNext();) {
						ap.product(av.getNext());
						if (firstp.equals("")){
							firstp = ap.toString();
							//System.out.println("22222222222222222222222222 ap is "+ap+" firstp "+firstp);
						} else if (firstFlag && firstp.equals(ap.toString())){
							done = true;
							iremove[j] = 1;
							//System.out.println(" the next ap "+ap.toString()+" done "+done+" j "+j);
						} 
						if (done) break;
					}
					if (done) break;
				}
				if (done) break;
			}
		}
		if (aret.size() > 1 && !done) {
			Debug.prout(4,"Culling aret");
			int n = aret.size();
			for(j=0;j<aret.size()-1;j++) {
				for(int k=aret.size()-1;k > j; k--) {
					if (aret.getSymbol(k).equals(aret.getSymbol(j))) {
						aret.removeElementAt(k);
					}
				}
			}
		}
		if (done) break;
		firstFlag = false;
	}
	//if (done) break;
	}
//System.out.println("RRRRRRRRRRRRRR ret "+ret);
int k = 0;
for (int i=0;i<4;i++){
	if (iremove[i] == 1) {
		lret.removeElementAt(i-k);
		ktsv.removeElementAt(i-k);
		k++;
	}
}
return lret;
}*/
 /*
  
  ListVector initAlgProdsXX(StringVector ktsv){
	  String kts = (String)ktsv.elementAt(0);
	  AlgebraPathVector ret = new AlgebraPathVector();
	  ListVector lret = new ListVector();
	  AlgebraPath ap; AlgebraPath bp;AlgebraPath abp; AlgebraPath bap;
	  ap = new AlgebraPath(); bp = new AlgebraPath();abp = new AlgebraPath();bap = new AlgebraPath();
	  AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");
	  AlgebraSymbol b = Algebra.getCurrent().getFocalElement("F");
	  
	  boolean skipFlag = false;
	  boolean skipABFlag = false;
	  boolean skipBAFlag = false;
	  if (a != null && b != null && kts.length() > 1){
		  ap.product(a); bp.product(b);
		  abp.product(b); abp.product(a);
		  bap.product(a); bap.product(b);
		  //ListVector vk = sym2Gen[(int) kts.charAt(1)];
		  AlgebraSymbolVector av = (AlgebraSymbolVector) sym2Gen[(int) kts.charAt(1)].elementAt(0);
		  ap.product((AlgebraSymbol)av.elementAt(0));
		  bp.product((AlgebraSymbol)av.elementAt(0));
		  abp.product((AlgebraSymbol)av.elementAt(0));
		  bap.product((AlgebraSymbol)av.elementAt(0));			
		  String s = ap.toString();
		  skipFlag = s.equals(bp.toString());
		  if (skipFlag){
			  skipABFlag = s.equals(abp.toString());
			  skipBAFlag = s.equals(bap.toString());
		  } else {
			  skipABFlag = abp.toString().equals("0");
			  skipBAFlag = bap.toString().equals("0");
		  }		
		  ap = new AlgebraPath(); 
		  bp = new AlgebraPath();
		  abp = new AlgebraPath();
		  bap = new AlgebraPath();
	  }
	  
	  //System.out.println(" fe M "+a+" fe F "+b+" size "+ktsv.size());
	  AlgebraPathVector apv = cayleyT.getProducts();
	  if (kts.charAt(0) == (char) 0) { // neutral
		  AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
		  if (aa != null) {//ego and N
						   //System.out.println("in start");
			  ap.product(aa);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  } else if (skipFlag){
			  ap.product(a);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  } else {//ego and M and/or F
				  //a = Algebra.getCurrent().getFocalElement("M");	
			  if (a != null) {//ego and M
							  //System.out.println("in zero ");
				  ktsv.clear();//replace ego by ego-M
				  ap.product(a);
				  String s = (char)1+kts.substring(1,kts.length());
				  ktsv.addElement(s);
				  ret.addElement(ap);
				  lret.addElement(ret);
				  
				  //a = Algebra.getCurrent().getFocalElement("F");
				  if (b != null) {//ego and M and F
								  //System.out.println("in one");
					  AlgebraPathVector ret1 = new AlgebraPathVector();
					  bp.product(b);
					  String s1 = (char)2+kts.substring(1,kts.length());
					  ktsv.addElement(s1);//add ego-F
						  ret1.addElement(bp);
						  lret.addElement(ret1);
				  }
			  } else {
				  //a = Algebra.getCurrent().getFocalElement("F");	
				  if (b != null) {//ego and F
					  ktsv.clear();//replace ego by ego-F
					  ap.product(b);
					  //System.out.println("in two");
					  String s = (char)2+kts.substring(1,kts.length());
					  ktsv.addElement(s);
					  ret.addElement(ap);
					  lret.addElement(ret);
				  }
				  else {}//return ret;
			  }
			  if (a != null && b != null){
				  bap.product(a);
				  bap.product(b);
				  abp.product(b);
				  abp.product(a);					
			  }
		  }
	  } else if (kts.charAt(0) == (char) 1) { // male ego-M
											  //System.out.println("in six");
		  /*		 if (Algebra.getCurrent().hasSibGenerators()){
		  //AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");
		  //System.out.println("in five");
		  if (a != null) {//ego-M and M
			  ap.product(a);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  }
		  else {}//return ret;	//ego-M and F	
		  } else {*/
		/*  AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
		  if (aa != null){//ego-M and N
			  ap.product(aa);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  } else {
			  //a = Algebra.getCurrent().getFocalElement("M");	
			  if (a != null) {//ego-M and M
				  ap.product(a);
				  ret.addElement(ap);
				  lret.addElement(ret);
			  } else{}//return ret;//ego-M and F
		  }
		  // }
		  if (a != null && b != null){
			  bap.product(a);
			  bap.product(b);
		  }
	  } else if (kts.charAt(0) == (char) 2) { //female
											  //System.out.println("in three");
		  /* if (Algebra.getCurrent().hasSibGenerators()){
		  //AlgebraSymbol a = Algebra.getCurrent().getFocalElement("F");
		  if (b != null) {
			  ap.product(b);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  }
		  else {}//return ret;
		  } else {*/
		/*  AlgebraSymbol aa = Algebra.getCurrent().getFocalElement("N");
		  if (aa != null) {//ego-F and N
			  ap.product(aa);
			  ret.addElement(ap);
			  lret.addElement(ret);
		  } else {
			  //a = Algebra.getCurrent().getFocalElement("F");	
			  if (b != null){//ego-F and F
				  ap.product(b);
				  ret.addElement(ap);
				  lret.addElement(ret);
			  } 
			  else{}//return ret;
		  }
		  // }
		  if (a != null && b != null){
			  abp.product(b);
			  abp.product(a);					
		  }
	  }
	  
	  //ret.addElement(ap);
	  //lret.addElement(ret);
	  /*if (bp.getProductPath().size() != 0){
		  AlgebraPathVector ret0 = new AlgebraPathVector();
	  ret0.addElement(bp);
	  lret.addElement(ret0);
	  String s = (char)
	  } */
	  //System.out.println(" added abp "+abp+" adp.getProductPath() "+abp.getProductPath());
	/*  if (!skipABFlag && abp.getProductPath().size() != 0 && apv.indexOf(abp) != -1) {//ab of form Ii
		  AlgebraPathVector ret1 = new AlgebraPathVector();
		  ret1.addElement(abp);
		  lret.addElement(ret1);
		  StringBuffer sb = new StringBuffer();
		  sb.append((char)2);
		  sb.append((char)1);
		  sb.append(kts.substring(1,kts.length()));
		  String s = sb.toString();
		  //System.out.println("in four");
		  // String s = (char)2+kts.substring(1,kts.length());
		  ktsv.addElement(s);
		  //kts(0)= (char)2;
	  }
	  if (!skipBAFlag && bap.getProductPath().size() != 0 && apv.indexOf(bap) != -1) {//ba of form iI
		  AlgebraPathVector ret1 = new AlgebraPathVector();
		  ret1.addElement(bap);
		  lret.addElement(ret1);
		  StringBuffer sb = new StringBuffer();
		  sb.append((char)1);
		  sb.append((char)2);
		  sb.append(kts.substring(1,kts.length()));
		  String s = sb.toString();
		  //	System.out.println("in seven");
		  // String s = (char)1+kts.substring(1,kts.length());
		  ktsv.addElement(s);
		  //kts[0] = (char)1;
	  }	
	  return lret;
  }
 */ 
/*
 void _addFinalAlgProducts(AlgebraPathVector aret,String kts1,StringVector ktsv){
	 AlgebraSymbol a = Algebra.getCurrent().getFocalElement("M");
	 AlgebraSymbol b = Algebra.getCurrent().getFocalElement("F");
	 //System.out.println(" aret "+aret+" n "+ aret.size());
	 if (a != null && b != null){
		 ListVector tret = new ListVector();
		 StringVector uret = new StringVector();
		 AlgebraPathVector mret = new AlgebraPathVector();
		 AlgebraPathVector fret = new AlgebraPathVector();
		 String mkts1 = "";
		 String fkts1 = "";
		 AlgebraPath ap = null;
		 for(aret.reset();aret.isNext();) {
			 ap = (AlgebraPath) aret.getNext();
			 if (((String)ap.toString()).equals("0")) continue;
			 if (ap.getReducedProductPath().firstElement().equals(a) ||
				 ap.getReducedProductPath().firstElement().equals(b)) continue;
			 //int n = ap.reducedPath.size();
			 String sex = ap.getPathSex();
			 AlgebraPath ap1 = (AlgebraPath) ap.clone1();
			 if (sex.equals("M")){
				 ap1.product(b);
				 //System.out.println(" ap "+ap+" ap1 "+ap1+" string "+ toSymString(kts1));
				 if (ap1.getReducedProductPath().firstElement().equals(b)) 
					 fret.addElement(ap1);
			 }
			 else{
				 ap1.product(a);
				 ap1 = (AlgebraPath)cayleyT.getApRules().get(ap1.toString());
				 if (ap1.getReducedProductPath().firstElement().equals(a)) 
					 mret.addElement(ap1);
			 }
		 }
		 if (mret.size() != 0){
			 //System.out.println(" the next ap "+ap.toString());
			 tret.addElement(mret);	
			 mkts1 = kts1+(char)1;
			 uret.addElement(mkts1);
		 } 
		 if (fret.size() != 0){
			 tret.addElement(fret);
			 fkts1 = kts1+(char)2;
			 uret.addElement(fkts1);
		 } 	
		 //System.out.println("RRRRRRRRRRRRRR ret "+ret);
		 uret.reset();
		 for (tret.reset();tret.isNext();){
			 lret.addElement((AlgebraPathVector) tret.getNext());
			 ktsv.addElement(uret.getNext());
		 }
	 }
 }
 
*/ 
		  /*	
		  String symsC[] = {  "f",      "m",      "s",    "d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",       "h",                        "w"};
		   //String syms[] = {"f","m","s","d","b","b-","b+","z","z-","z+","h","w"};
		   int propertiesC[] = {UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
		   int masksC[] =      {UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
		   int conditionsC[] = {DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL, OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,                  AFFINAL};
		   
		   String symsD[] = {  "f",      "m",      "s",    "d",        "b",             "z",              "h",                        "w"};
		   int propertiesD[] = {UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
		   int masksD[] =      {UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
		   int conditionsD[] = {DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL, OVER|DOWN, OVER|DOWN,     AFFINAL,                  AFFINAL};
		   
		   int nEgo = 0; int isEgo = 0;//number of ego symbols above; currently 0*/
		   
		   /*	
		   String symsC[] = {  "ego-M",             "ego-F",                "f",      "m",      "s",    "d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",       "h",                        "w"};
			//String syms[] = {"f","m","s","d","b","b-","b+","z","z-","z+","h","w"};
			int propertiesC[] = {EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksC[] =      {EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsC[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL, OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,                  AFFINAL};
			
			String symsD[] = {  "ego-M",             "ego-F",                "f",      "m",      "s",    "d",        "b",             "z",              "h",                        "w"};
			int propertiesD[] = {EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksD[] =      {EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsD[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL, OVER|DOWN, OVER|DOWN,     AFFINAL,                  AFFINAL};
			
			int nEgo = 2; int isEgo = 1;//number of ego symbols above; currently 2*/
			/*
			 String symsC[] = {  "ego",				  "ego-M",             "ego-F",                "f",      "m",      "s",    "d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",       "h",                        "w"};
			 //String syms[] = {"f","m","s","d","b","b-","b+","z","z-","z+","h","w"};
			 int propertiesC[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			 int masksC[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
			 int conditionsC[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,                  AFFINAL};
			 
			 String symsD[] = {  "ego",                "ego-M",             "ego-F",                "f",      "m",      "s",    "d",        "b",             "z",              "h",                        "w"};
			 int propertiesD[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			 int masksD[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,          UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
			 int conditionsD[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN, OVER|DOWN,     AFFINAL,                  AFFINAL};
			 
			 */	
			//String symsC[] = {  "ego",				  "ego-M",             "ego-F",                "f",      "m",      "s",    "d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",       "h",                        "w"};
			/*	String symsC[] = {  "ego",				  "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",   "u",								"h",                        "w"};
			int propertiesC[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksC[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsC[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		AFFINAL,			DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,							AFFINAL,                  AFFINAL};
			
			String symsD[] = {  "ego",                "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",        "b",             "z",              "h",                        "w"};
			int propertiesD[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksD[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,COLL|FEMALE,AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsD[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		AFFINAL,			DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,OVER|DOWN, OVER|DOWN,     AFFINAL,                  AFFINAL};
			*/
			/*String symsD[] = {  "ego",				  "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",           "g",								"h",                        "w"};
			int propertiesD[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksD[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsD[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		AFFINAL,			DOWN|OVER,DOWN|OVER,AFFINAL  , AFFINAL    ,   AFFINAL,							AFFINAL,                  AFFINAL};
			*/
			/*	String symsC[] = {  "ego",				  "ego-M",             "ego-F",				"p",			"c",				"f",      "m",      "s",		"d",        "b",        "b-",       "b+",       "z",        "z-",       "z+",   "g",								"h",                        "w"};
			int propertiesC[] = {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE|AFFINAL_PATH,  AFFINAL|MALE|AFFINAL_PATH,AFFINAL|FEMALE|AFFINAL_PATH};
			int masksC[] =      {EGO|MALE_FEMALE,     EGO|MALE,            EGO|FEMALE,			UP|MALE_FEMALE, DOWN|MALE_FEMALE,   UP|MALE,  UP|FEMALE,DOWN|MALE, DOWN|FEMALE,COLL|MALE,LCOLL|MALE,RCOLL|MALE,COLL|FEMALE,LCOLL|FEMALE,RCOLL|FEMALE,AFFINAL|MALE_FEMALE,				AFFINAL|MALE,             AFFINAL|FEMALE};
			int conditionsC[] = {DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|UP|OVER|AFFINAL,DOWN|OVER,		UP|AFFINAL,			DOWN|OVER,DOWN|OVER,UP|AFFINAL,UP|AFFINAL ,OVER|DOWN,OVER|DOWN, OVER|DOWN, OVER|DOWN,  OVER|DOWN,   OVER|DOWN,   AFFINAL,							AFFINAL,                  AFFINAL};
			*/	
			
