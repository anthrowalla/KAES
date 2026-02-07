import java.util.*;
import java.awt.*;

/*History
* 10/11 DR added clr parameter to populateModel procedure
* 10/15 DR added symbolColor procedure
* 10/17 DR added symbolLineStyle procedure
* 10/19 DR added "generators x sex marked" products to protoCayley in generateProducts
* added restriction to populateModel3D to exclude sex generator in the connection procedure
* 10/29 DR added "-1" condition to populatteModel3D to permit excluding connecting points based on value of ord
* where ord is set to -1 in Model3D for a graph layer that will be excluded from graph
* 2/14 DR aded mapFlag to control symbols used in graph
* 2/24 DR corrected data partitioning in toData,toColumn,etc to allow for data of form "x [x, y] z"
* 2/27 DR added complexPaths test to calcCoordinates
* 3/3 DR added symbolSplitNode procedure to populateModel to mark if an element is bifurcated in the graph
* consolidate the two forms of populateModel
* 3/5 DR added the procedure useGenerators to activate the layer buttons in the graph window;
* added gen1Flag...gen3Flag, along with setGen1Flag, ..., setGen3Flag procedures
* 3/7 DR redid terminating conditions in generateProducts; added condition to make node white in populateModel to
* signify that products continue indefinitely beyond the graph boundary MAXLENGTH
* 7/14 DR added additional criteria based on tsex, candsex and curTsex to generateCayleyProducts_new; these
* need to be examined for their general validity!!!!
* 10/6 DR added procedures numberOfGenerators(), generatorNumber(AlgebraSymbol g),
* resetDimension(AlgebraSymbol g),useCalcMethod1(AlgebraPath thePath, String gen),
* useCalcMethod2(AlgebraPath thePath,int j,int dim, String gen) to calcCoordinates
* 11/7 DR added variant applyRules(RuleVector someRules, ListHashtable somePaths, AlgebraPathVector keys)
* to accomodate linealDescendantRule which is applied to the cayley products; modified populateModel
* to allow for sex generators when the algebra has sex marked generators (needed to accomodate
* Shipibo terminology where FaCh = M and MoCh = F DR 1/24/03??? is this correct??
* 11/12 DR added pathColor(AlgebraPath ap) to set sex color of path on a path
* criterion than just the first symbol in path symbolColor more flexible
* modified calcCoordinates and complexPath to use equivalent path form [X,&,Y]
* 11/22 DR added procedure computeMaxLength(), set MAXLENGTH = computeMaxLength() ini computeProducts
*11/24 DR rewrote main for loop in populateModel to be based on protoCayleyHash rather than protoCayley
* 5/27 DR added toData(Vector col, int labelcode) as a wrapper for two procedures toKinData
* and toAlgData, both used to construct entries for tables
* added toColumnNames(int labelcode) as a wraper to two procedures toAlgColumnNames
* and toKinColumnNames, both used to construct entries for tables
* 8/26 DR corrected error in generateCayleyProducts_new; get sexes from algebra
* 9/1 DR modified generateCayleyProducts_new to handle equivalent algebra products of
* form A&B, where A, B are alg elements that have been made equivalent
* 8/7/04 DWR changed theProducts.addElement to theProducts.addUnique in InitProducts
*/

public class CayleyTable extends Hashtable  implements CayleyEntity {
	AlgebraSymbolVector theSymbols = new AlgebraSymbolVector();
	AlgebraPathVector theProducts=new AlgebraPathVector();
	AlgebraSymbolVector generators = Algebra.getCurrent().getGenerators();
	ListVector fullPaths = new ListVector();

	boolean mapFlag = false;
	boolean gen1Flag = true;
	boolean gen2Flag = true;
	boolean gen3Flag = true;
	boolean gen4Flag = true;

	float [][] coordinates = null;
	
	Hashtable apRules = new Hashtable();//key = algpath.toString(), entry = result of rule reductions

	/** in future must be derived from KinTermMap
	* @see KinTermMap
	*/
	//public static int MAXLENGTH = 7;//maximum number of products
	
	public static int MAXLENGTH = MainFrame.prefs.getInt("Maximum_product_size",7);

   long serialNumber=0;
   
   public Hashtable getApRules(){
	   return apRules;
   }

   public void setCoordinates(float [][] n) {
	  coordinates = n;
   }

   public float[][] getCoordinates() {
	  return coordinates;
   }

   public CayleyTable() {

	}
   public void setSerialNumber(long n) {
	  serialNumber = n;
   }

   public long getSerialNumber() {
	  return serialNumber;
   }

	public CayleyTable(AlgebraSymbolVector sym) {
	//	theSymbols = sym;
	//	initProducts(sym);
	   setSymbols(sym);
	}

    public CayleyTable setMapFlag(boolean flag) {
          mapFlag = flag;
          return this;
    }

	public void setGen1Flag(boolean flag) {
          gen1Flag = flag;
    }

	public void setGen2Flag(boolean flag) {
          gen2Flag = flag;
    }

	public void setGen3Flag(boolean flag) {
          gen3Flag = flag;
    }

	public void setGen4Flag(boolean flag) {
          gen4Flag = flag;
    }


	public void setSymbols(AlgebraSymbolVector sym) {
		theSymbols = sym;
		initProducts(sym);
	}

	public void initProducts(AlgebraSymbolVector sym) {
		int i;
		System.out.println(" sym "+sym);
		for (i=0;i<sym.size();i++) {
			AlgebraSymbolVector x = new AlgebraSymbolVector();
			x.addElement(sym.getSymbol(i));
			//theProducts.addElement(new AlgebraPath(x,true));//dwr 8-7
			theProducts.addElement(new AlgebraPath(x,false));//dwr 8-7
			//theProducts.addUnique(new AlgebraPath(x,true));
			CayleyPathInfo cpi = new CayleyPathInfo(sym.getSymbol(i),new AlgebraPath(new AlgebraSymbol("I")), new AlgebraPath(sym.getSymbol(i)));
			fullPaths.add(cpi);
			cpi.sfullPaths.add(new AlgebraSymbolVector(sym.getSymbol(i)));
		//	cpi.sfullPaths.add(sym.getSymbol(i).toString());
		}
	}

	public ListVector getFullProducts() {
	   return fullPaths;
	}

	boolean fullProductFlag = false;

	public void fullProducts(AlgebraSymbol gen, AlgebraPath term, AlgebraPath prod){
	   int ndx;
	   if (!fullProductFlag) return;
	   ndx = findFullProd(term);
	   if (ndx == -1) {
		  System.out.println("In CayleyTable.fullProducts ... no prior term");
		  System.out.println("fullProducts: term="+term+" prod="+prod);
	   }
	   CayleyPathInfo cpi = (CayleyPathInfo) fullPaths.elementAt(ndx);

	  CayleyPathInfo cpex;
	  if ((ndx = findFullProd(prod)) != -1) cpex = (CayleyPathInfo) fullPaths.elementAt(ndx);
	   else {
		  cpex = new CayleyPathInfo(gen,term,prod);
		  fullPaths.add(cpex);
	   }
	   cpex.addProd(gen,cpi);
	}

	int findFullProd(AlgebraPath prod) {
	  // System.out.println("fullPaths "+fullPaths.toString());
	   for (int i = 0; i< fullPaths.size();i++) {
		//  System.out.println("CayleyPathInfo "+fullPaths.elementAt(i)+" prod="+prod);
		  if (((CayleyPathInfo)fullPaths.elementAt(i)).product.toString().equals(prod.toString())) return i;
	   }
	   return -1;
	}

	public AlgebraPathVector getProducts() {
		return theProducts;
	}

	ListVector protoCayley = new ListVector();
	ListHashtable protoCayleyHash = new ListHashtable();

	public void populateProtoCayleyHash() {
		for(int i=0; i < protoCayley.size();i++) {
			CayleyInfo ci = (CayleyInfo) protoCayley.elementAt(i);
			//if (ci.product.toString().equals("BB")||ci.product.toString().equals("CC"))
			//System.out.println("CI "+ci+ " " +ci.product.toString());
			protoCayleyHash.putInList(ci.product.toString(),ci);
		}
	}

    int computeMaxLength(){
        int i = 0;
        EquationVector eqr =  Algebra.getCurrent().getRecursiveEquations();
        for (eqr.reset();eqr.isNext();){
            Equation eq = eqr.getNext();
            if (eq.getLhs().size() > i) i = eq.getLhs().size();
        }
        return i;
    }

	public AlgebraPathVector generateProducts() {
		//Debug.on(-7);
		int i,j;
		AlgebraPath a=null;
		AlgebraPathVector tempProds=new AlgebraPathVector();
		int lastSize;
		Debug.prout(7,"Initial Products "+theProducts.toString());
		AlgebraSymbolVector noIdents, leftIdents, rightIdents;
		int iLen = computeMaxLength();
		//if (iLen > 0) MAXLENGTH = iLen;
		//System.out.println(" maxlen "+computeMaxLength());
		noIdents = new AlgebraSymbolVector();
		leftIdents = new AlgebraSymbolVector();
		rightIdents = new AlgebraSymbolVector();
		boolean oneFlag =  Algebra.getCurrent().getFocalElements().size() < 2;
		boolean removeZerosFlag = false;//used for rules that rewrite zero products
		//System.out.println("The Symbols in="+theSymbols);
		for (i = 0; i < theSymbols.size();i++) {
			AlgebraSymbol as = theSymbols.getSymbol(i);
	//		System.out.println("start as= "+as);
			if (!as.isGenerator()) continue;
			if (oneFlag && as.isIdentityElement())  continue;
			//if (as.toString().equals("F")) {rightIdents.addElement(as);continue;}
			//if (as.toString().equals("M")) {rightIdents.addElement(as);continue;};

			if (Algebra.getCurrent().isAlmostRightIdentity(as)) {rightIdents.addElement(as);continue;}
			if (Algebra.getCurrent().isAlmostLeftIdentity(as)) {leftIdents.addElement(as);continue;}
			noIdents.addElement(as);
		}

		//System.out.println("The Symbols out="+theSymbols+" rI "+rightIdents+" lI "+leftIdents);
		protoCayley = new ListVector();
		Debug.prout(7,"No Identities="+noIdents+" right Identities="+rightIdents+" left Identities="+leftIdents);
		int ii = 0;
		int skip = noIdents.indexOf("&");
		//if (skip != -1) System.out.println("SSSSSSSSSSSSSSSSSSSSS skip");
		AlgebraSymbolVector spouses = new AlgebraSymbolVector();
		if (Algebra.getCurrent().getGenerators(Bops.SPOUSE).size() == 2) {
			spouses = Algebra.getCurrent().getGenerators(Bops.SPOUSE);
		}
		for(lastSize = 0;theProducts.size() != lastSize;) {
			int index = lastSize;
			lastSize = theProducts.size();
			for (i=index;i<lastSize;i++) {
				for (j=0;j<noIdents.size();j++) {
					if (j == skip) continue;
					a = (AlgebraPath) theProducts.getSymbol(i).clone();
					boolean flag = false;//controls print statements only!
					if (spouses.indexOf(noIdents.getSymbol(j)) != -1){
						//System.out.println("in a "+a+" prod "+a.getReducedProductPath());
						if (a.getReducedProductPath().indexOf(spouses.elementAt(0)) != -1) continue;
						else if (a.getReducedProductPath().indexOf(spouses.elementAt(1)) != -1) continue;
					    String sex = a.getReducedProductPath().getLast().getSex();
						if (!sex.equals("N") && sex.equals(noIdents.getSymbol(j).getSex())) continue;
						//System.out.println("ou a "+a+" prod "+a.getReducedProductPath());
						//if (a.getReducedProductPath().size() == 3) flag = true;
					}
						//if (a.toString().equals("PP")||a.toString().equals("GG")) flag = true;
						//else flag = false;
					if (flag) System.out.println("ZZZZZZZZZZZZZZZZZZ a "+a+" noI "+noIdents.getSymbol(j));
					//if (a.product(noIdents.getSymbol(j))) {//excludes "0" from products
					//following two lines used because of rules that rewrite "0" products
				a.product(noIdents.getSymbol(j));//allows "0" product to be included
				removeZerosFlag = true; {//removes any zeros after rules are applied

					    //if (a.reducedPath.size() > MAXLENGTH) break;
						// Debug.prout(6,"ReducedXZX = "+a.reducedPath.toString());
				if (flag) System.out.println(" a "+a+" reduced "+a.reducedPath.toString());
						if (a.reducedPath.size() <= MAXLENGTH){
						       protoCayley.addElement(new CayleyInfo(noIdents.getSymbol(j),theProducts.getSymbol(i),a));

						    //protoCayley.addElement(new CayleyInfo(noIdents.getSymbol(j),theProducts.getSymbol(i),a));
						//if (a.path.size() >= MAXLENGTH) break;
						    //if (theProducts.indexOf(a) == -1) {
					//System.out.println(" add unique a "+a.toString());
							theProducts.addUnique(a);
						//	fullProducts(noIdents.getSymbol(j),theProducts.getSymbol(i),a);
							Debug.prout(8,"Adding a="+a.toString());
						    //}
						}
					}
				}
				if (a.reducedPath.size() >= MAXLENGTH+2) break;
			}
			//if (a.reducedPath.size() >= MAXLENGTH+1) break;
		}System.out.println(" got to here");
System.out.println("66666666666666666666666666666 wsay before proto "+theProducts);
		int psize = theProducts.size();
		boolean flag = false;
		for(rightIdents.reset();rightIdents.isNext();) {
		    AlgebraSymbol as = rightIdents.getNext();
			for(int k = 0; k < psize;k++) {
			//AlgebraPath prod = new AlgebraPath();
				AlgebraPath prod =  (AlgebraPath) theProducts.elementAt(k);
			   // if (!flag) {
				    prod = (AlgebraPath) prod.clone();
				//}
				prod.product(as);
				//AlgebraPath prod1 = (AlgebraPath) prod.clone();
				//AlgebraPath ap = (AlgebraPath) theProducts.getSymbol(k).clone();
				protoCayley.addElement(new CayleyInfo(as,theProducts.getSymbol(k),prod));
			   // if (!flag){
			    theProducts.addUnique(prod);
				fullProducts(as,theProducts.getSymbol(k),prod);
				//}
			}
			flag = true;
		}
System.out.println(" got to here1 ");
		psize = theProducts.size();
		for(leftIdents.reset();leftIdents.isNext();) {
			AlgebraSymbol as = leftIdents.getNext();
			for(int k = 0; k < psize;k++) {
				AlgebraPath prod = (AlgebraPath)((AlgebraPath) theProducts.getSymbol(k).clone());
				prod.getReducedProductPath().addToBeginning(as);
				prod.reducePath(prod.getReducedProductPath());
				protoCayley.addElement(new CayleyInfo(as,theProducts.getSymbol(k),prod));
				theProducts.addUnique(prod);
				fullProducts(as,theProducts.getSymbol(k),prod);
			}
		}
//System.out.println("66666666666666666666666666666 wsay before proto "+theProducts);
//System.out.println("protoCayley "+protoCayley.toString());
System.out.println(" got to here2 ");
		applyRules(Algebra.getCurrent().getRules(),theProducts);
			Debug.prout(7,"The final products="+theProducts);
		Debug.off();

/*		lastSize = theProducts.size();//add products of generators times sex marked symbols to protoCayley
		for (i=0;i<lastSize;i++) {
			for (j=0;j<noIdents.size();j++) {
				a = (AlgebraPath) theProducts.getSymbol(i).clone();
				AlgebraSymbol as = a.getReducedProductPath().getLast();
//System.out.println("PPPPPP a = "+a+ " as= "+as);
				if (rightIdents.indexOf(as) != -1) {
				    if (a.product(noIdents.getSymbol(j))) {
					    protoCayley.addElement(new CayleyInfo(noIdents.getSymbol(j),theProducts.getSymbol(i),a));
	//System.out.println("XXX gen "+noIdents.getSymbol(j)+" prod "+theProducts.getSymbol(i)+" res "+a);
				    }
				}
			}
		}

	*/
System.out.println("66666666666666666666666666666 before proto "+theProducts);
//System.out.println("protoCayley222 "+protoCayley.toString());
		populateProtoCayleyHash();
		applyRules(Algebra.getCurrent().getRules(),protoCayleyHash,theProducts);
//System.out.println("!!!!!!!!!!!!!!!!! proto "+protoCayleyHash.toString());
System.out.println("66666666666666666666666666666 proto "+theProducts);
//System.out.println("-------------!!------------");
//System.out.println("ProtoCayley bits "+theProducts);
//System.out.println("-------------------------");
//System.out.println("protoCayley "+protoCayley.toString());
//System.out.println("-------------!!------------");
		Algebra.getCurrent().setSerialNumber(Algebra.getCurrent().getSerialNumber()+1);
		setSerialNumber(Algebra.getCurrent().getSerialNumber());
		if (removeZerosFlag){
			removeZeros(protoCayley);
			removeZeros(theProducts);
			protoCayleyHash.clear();
			populateProtoCayleyHash();
		}

		return theProducts;
	}

	void removeZeros(ListVector c){
		ListVector temp = new ListVector();
		for (c.reset();c.isNext();){
			CayleyInfo cInfo = (CayleyInfo) c.getNext();
			if (cInfo.product.getReducedProductPath().getFirst().getValue().equals("0"))
				temp.addElement(cInfo);
		}
		for (temp.reset();temp.isNext();)
			c.remove((CayleyInfo)temp.getNext());
	}

	void removeZeros(AlgebraPathVector apv){
		ListVector temp = new ListVector();
		for (apv.reset();apv.isNext();){
		    AlgebraPath ap = (AlgebraPath)apv.getNext();
			if (ap.getReducedProductPath().getFirst().getValue().equals("0"))
				temp.addElement(ap);
		}
		for (temp.reset();temp.isNext();){
		    apv.remove((AlgebraPath) temp.getNext());
		}
	}


   // ListHashTable

	/* apply rules to the algebra elements
	* @someRules the rules to be applied
	* @somePaths the elements to which the rules are to be applied
	*/
	public void applyRules(RuleVector someRules, AlgebraPathVector somePaths) {
		AlgebraPathVector productions = new AlgebraPathVector();
		for(someRules.reset();someRules.isNext();) {
			Rule rule = (Rule) someRules.getNext();
			if (!rule.getActiveRule()) continue;
			ListVector lv = new ListVector();
			//System.out.println(" SOMEPATHA "+somePaths+" text "+rule.getRuleText());
			for (somePaths.reset();somePaths.isNext();) {
				AlgebraPath aPath = (AlgebraPath) somePaths.getNext();
				//System.out.println(" aPath "+aPath+" text "+rule.getRuleText());
				if (rule.doesRuleApply(aPath)) {
					//AlgebraPath ap = (AlgebraPath) aPath.clone();
					boolean equivFlag = aPath.isReducedEquivalentPath();
					AlgebraSymbolVector asv = aPath.getReducedProductPath();
					AlgebraPath ap = null;
					if (rule.applyRule(aPath)) {
						//if (!ap.equals(aPath)){
						if (!equivFlag && aPath.isReducedEquivalentPath()) {
							AlgebraPath asvL = aPath.getReducedEquivalentPathLeft();
							AlgebraPath asvR = aPath.getReducedEquivalentPathRight();
							if (asvL.getReducedProductPath().equals(asv)) ap = asvR;
							else ap = asvL;
							AlgebraPath ap1 = (AlgebraPath) ap.clone1();
							//lv.addElement(ap1);
							if (rule.applyRule(ap)){
							    lv.addElement(ap1);
								//System.out.println(" remove list ap1 "+ap1+" ap "+ap);
							    if (productions.indexOf(ap) == -1) productions.addElement(ap);
							}
						}
						//System.out.println("BBBBBBBBBBBBBB somePaths "+somePaths.toStringSize());

						somePaths.remove();
						//System.out.println("BBBBBBBBBBBBBBaftger somePaths "+somePaths.toStringSize());

						if (somePaths.indexOf(aPath) == -1 && productions.indexOf(aPath) == -1)
							productions.addElement(aPath);
						//System.out.println(" MMMMMMMMMMMMM added aPath "+aPath);
					}
				}
			}
			for (lv.reset();lv.isNext();){
			    AlgebraPath ap = (AlgebraPath)lv.getNext();
				//System.out.println("to remove ap "+ap);
				somePaths.remove(ap);
			}
			if (productions.size() != 0) somePaths.append(productions);
			productions.clear();
		}
		if (productions.size() != 0) somePaths.append(productions);
	}


	/* apply rules to the Cayley Products
	* @someRules the rules to be applied
	* @someProducts the Cayley Products to which the rules are to be applied
	* @somePaths the elements to be tested for addition or deletion
	*/
	public void applyRules(RuleVector someRules, ListHashtable someProducts, AlgebraPathVector somePaths) {
		apRules.clear();
	    ListVector productions = new ListVector();
	    AlgebraPathVector prods = new AlgebraPathVector();
	    AlgebraPathVector newProds = new AlgebraPathVector();
	    AlgebraPath prod = new AlgebraPath();
	    for(someRules.reset();someRules.isNext();) {
			Rule rule = (Rule) someRules.getNext();
			if (!rule.getActiveRule()) continue;
			//System.out.println(" rule "+rule.toString());
			for (Enumeration e=someProducts.keys();e.hasMoreElements();){
				Object keyx = e.nextElement();
				ListVector clv = someProducts.getList(keyx.toString());
				if (clv.size()==0) continue; // possible error
				for (clv.reset();clv.isNext();){
					CayleyInfo cinfo = (CayleyInfo)clv.getNext();
					AlgebraPath curCayley = cinfo.product;
					//if (curCayley.toString().length() == 0) continue; // our bugger was M and F
					if (rule.doesRuleApply(cinfo)) {
						//prod = cinfo.product;

							//System.out.println(" removebefore clv "+cinfo);
						protoCayley.remove(cinfo);//added 8/12 tongan dwr make sure it works on all terminologies
						if (rule.applyRule(cinfo)){
							//System.out.println(" removeafter clv "+cinfo);
							clv.remove();//8-12 this was blanked out; not clear why
							if (someProducts.get(cinfo) == null){
								//System.out.println("in null cinfo "+cinfo);
								if (cinfo.product.isReducedEquivalentPath()){
									prod = cinfo.product.getReducedEquivalentPathLeft();
									if (prods.indexOf(prod) == -1) {
										prods.addElement(prod);
										newProds.addElement(cinfo.product);
										if (!prod.equals(cinfo.product.getReducedEquivalentPathRight())){
											prod = cinfo.product.getReducedEquivalentPathRight();
											if (prods.indexOf(prod) == -1) {
												prods.addElement(prod);
												newProds.addElement(cinfo.product);
											}
										}
									}
								}
								prod = cinfo.term;
								//if (cinfo.term.toString().equals("M") ||cinfo.term.toString().equals("F"))
							   // System.out.println(" prod term "+cinfo.term);
								if(prod.isReducedEquivalentPath()){
								    prod = cinfo.term.getReducedEquivalentPathLeft();
									if (prods.indexOf(prod) == -1){
										prods.addElement(prod);
										/*if (cinfo.term.isEquivalentPath())
										    newProds.addElement(cinfo.term);
										else
										    newProds.addElement(new AlgebraPath(cinfo.term.reducedPath));*/
										//newProds.addElement(cinfo.term);
										newProds.addElement(new AlgebraPath(cinfo.term.reducedPath,false));

		//System.out.println("  in apply cinfo "+cinfo+" newProds "+newProds+" term "+cinfo.term+" path "+cinfo.term.path);
										prod = cinfo.term.getReducedEquivalentPathRight();
										if (prods.indexOf(prod) == -1){
											prods.addElement(prod);
											/*if (cinfo.term.isEquivalentPath())
										    newProds.addElement(cinfo.term);
											else
										    newProds.addElement(new AlgebraPath(cinfo.term.reducedPath));*/
											//newProds.addElement(cinfo.term);
											newProds.addElement(new AlgebraPath(cinfo.term.reducedPath,false));											
										}
		//System.out.println("  in apply cinfo2 "+cinfo.term.reducedPath+" newProds "+newProds+ " term "+cinfo.term+" path "+cinfo.term.path);
									}
								}
								//System.out.println(" add cinfo "+cinfo);
								productions.addUnique(cinfo);
							}
						}else {
							protoCayley.addElement(cinfo);//replace if cinfo not modified
						}
					}
				}
			}
	   // }
	  // System.out.println(" productions "+productions);
	  //  System.out.println(" prods "+prods);
	  //  System.out.println(" newprods "+newProds);
		    for (productions.reset();productions.isNext();) {
				CayleyInfo ci = (CayleyInfo)productions.getNext();
				  //  System.out.println(" put in list "+ci);
				someProducts.putInList(ci.product.toString(),ci);
				protoCayley.addElement(ci);
			 }
			 int j = -1;

			 for (prods.reset();prods.isNext();){
				  j++;
				prod = (AlgebraPath)prods.getNext();
				int i = 0;
				if (( i = somePaths.indexOf(prod))!= -1) somePaths.removeElementAt(i);
				AlgebraPath newProd = (AlgebraPath) newProds.elementAt(j);
				if (somePaths.indexOf(newProd) == -1) somePaths.addElement(newProd);
				System.out.println("!!!!!!!!! prod "+prod +" newpord "+newProd+" i "+i);
				apRules.put(prod.toString(),newProd);
			 }
			 AlgebraPathVector  temp = new AlgebraPathVector();
			 for (somePaths.reset();somePaths.isNext();){
				 temp.addUnique(somePaths.getNext());
			 }
			 somePaths.clear();
			 for (temp.reset();temp.isNext();){
				 somePaths.addElement(temp.getNext());
			 }
			 for (Enumeration e=someProducts.keys();e.hasMoreElements();) {
				Object keyx = e.nextElement();
				ListVector clv = someProducts.getList(keyx.toString());
				if (clv == null) throw new KintermMapException("clv is null");
				for (clv.reset();clv.isNext();){
					 CayleyInfo cinfo = (CayleyInfo)clv.getNext();
					 if (!cinfo.generator.isSexGenerator()){
					 //if (!cinfo.generator.toString().equals("M") && !cinfo.generator.toString().equals("F")){
						int n = prods.indexOf(cinfo.term);
						if (n != -1) cinfo.term = (AlgebraPath) newProds.elementAt(n);
						n = prods.indexOf(cinfo.product);
						if (n != -1) cinfo.product = (AlgebraPath)newProds.elementAt(n);
					 }
				}
			 }
	    }
		for (Enumeration e=someProducts.keys();e.hasMoreElements();) {
			Object keyx = e.nextElement();
			ListVector clv = someProducts.getList(keyx.toString());
			StringVector slv = new StringVector();
			ListVector clv1 = new ListVector();
			for (clv.reset();clv.isNext();){
				CayleyInfo ci = (CayleyInfo)clv.getNext();
				if (slv.indexOf(ci.toString())== -1){
					clv1.addElement(ci);
					slv.addElement(ci.toString());
				 }
			}
			clv.removeAllElements();
			for (clv1.reset();clv1.isNext();){
				 CayleyInfo ci = (CayleyInfo)clv1.getNext();
				 clv.addElement(ci);
				//System.out.println("NEW CI "+ci);
			}
		}
	}


	public void applyRules(RuleVector someRules, AlgebraPath somePath) {
		for(someRules.reset();someRules.isNext();) {
			Rule rule = (Rule) someRules.getNext();
			if (!rule.getActiveRule()) continue;
			if (rule.doesRuleApply(somePath)) {
				rule.applyRule(somePath);
			}
		}
	}

	public AlgebraPath generateCayleyProduct(int row, int col) {
		AlgebraPath ap = (AlgebraPath) theProducts.getSymbol(row).clone();
		if (!ap.product(theSymbols.getSymbol(col))) {
			ap.reducedPath.addElement(Algebra.getCurrent().getElement("0"));
		}
		return ap;
	}

	public AlgebraPath generateCayleyProduct(int row, AlgebraSymbol as) {
		AlgebraPath ap = (AlgebraPath) theProducts.getSymbol(row).clone();
		if (!ap.product(as)) {
			ap.reducedPath.addElement(Algebra.getCurrent().getElement("0"));
		}
		return ap;
	}

	public AlgebraPath generateCayleyProduct(AlgebraPath ap, AlgebraSymbol as) {
		ap = (AlgebraPath) ap.clone();
		if (!ap.product(as)) {
			ap.reducedPath.addElement(Algebra.getCurrent().getElement("0"));
		}
		return ap;
	}

	/** generate Cayley table entries from the products in this CayleyTable.
	* This form includes the identity mapping if any.
	*/
	public void generateCayleyProducts() {
		for(int i = 0; i < theProducts.size();i++) {
			for (int j=0;j< theSymbols.size();j++) {
				put(theProducts.getSymbol(i).toString()+" "+theSymbols.getSymbol(j).getValue()+" "+
					theSymbols.getSymbol(j).getSex(),generateCayleyProduct(i,j).toString());
			}
		}
	}
	/** generate Cayley table entries from the products in this CayleyTable translating symbol names into
	* corresponding terms (usually kin terms). This form includes the identity mapping if any.
	* @see KinTermMap.mapAlgebraProductsToKinshipTerms(AlgebraPathVector a)
	* @param kinNames hash table with product to kin term mapping
	*/
/*
	public void generateCaleyProducts(Hashtable kinNames) {
		generateCayleyProducts(kinNames,theProducts,true);
	}
*/
	/** generate Cayley table entries from the products from another CayleyEntity translating symbol names into
	* corresponding terms (usually kin terms). This form used to limit the products to the products of an
	* external map. However products are generated by the Algebra associated with this CayleyTable.
	* This form does not include the identity mapping, if any, in order to accommodate CayleyEntities that do
	* not equate focal terms or identity terms to generators.
	* @see KinTermMap
	* @param kinNames hash table with product to kin term mapping
	* @param keys list of paths (e.g. table rows) to generate products from.
	*/

	public void generateCayleyProducts(Hashtable kinNames, AlgebraPathVector keys) {
		generateCayleyProducts(kinNames,keys,false);
	}

	/** generate Cayley table entries from the products from another CayleyEntity translating symbol names into
	* corresponding terms (usually other algebra elements). This form used to limit the products to the products of an
	* external algebra map. However products are generated by the Algebra associated with this CayleyTable.
	* This form optionally include the identity mapping based on doIdentity, in order to accommodate algebras that do
	* not have identity terms, e.g. partial isomorphism is all that is claimed, at best.
	* @see KinTermMap.mapAlgebraProductsToKinshipTerms(AlgebraPathVector a)
	* @param kinNames hash table with product to kin term mapping
	* @param keys list of paths (e.g. table rows) to generate products from.
	* @param doIdentity if true, generate products using identity element as generator.
	*/


	public void generateCayleyProducts(Hashtable kinNames, AlgebraPathVector keys, boolean doIdentity) {
	//System.out.println("kin names= "+kinNames.toString());
	//System.out.println("===========================");
	//System.out.println("keys " +keys.toString());
		for(int i = 0; i < keys.size();i++) {
			for (int j=0;j< theSymbols.size();j++) {

				String keyPath = keys.getSymbol(i).toString();
Debug.prout(6,"generateCayleyProducts: keyPath="+keyPath);
				if (keyPath.length() >= MAXLENGTH-1) continue;

				AlgebraSymbol curSymbol = theSymbols.getSymbol(j).getAlgebraSymbol();
Debug.prout(6,"generateCayleyProducts: curSymbol="+curSymbol);

				if (curSymbol.isIdentityElement() && !doIdentity ) continue;

				String curTerm = (String) kinNames.get(keyPath);
Debug.prout(6,"generateCayleyProducts: curTerm="+curTerm);
	//			if (curTerm.equals("etc+")) continue;

				String curKinterm = (String) kinNames.get(curSymbol.getValue());
Debug.prout(6,"generateCayleyProducts: curKinterm="+curKinterm);
				String curSex = curSymbol.getSex();
Debug.prout(6,"generateCayleyProducts: theSymbols="+theSymbols);
				AlgebraPath curCayley = generateCayleyProduct(keys.getSymbol(i),curSymbol);
Debug.prout(6,"generateCayleyProducts: curCayley="+curCayley);
				String curTarget = (String) kinNames.get(curCayley.toString());
				if (curTarget == null) { // need to apply rules
					String oldCayley = curCayley.toString();
					applyRules(Algebra.getCurrent().getRules(),curCayley);
					//System.out.println(" IS apply needed?");
					curTarget = (String) kinNames.get(curCayley.toString());
					if (curTarget == null) { // diagnostic point - an algebra product exists that has no analogue in the kin term map
						Message.create(Mode.ALL,"Kinterm map is incomplete.", "Kinterm map is incomplete. Algebra term="+
														oldCayley+". Assigning 'No Kin Term' to this path.",null,1);
						curTarget = "No Kin Term";
					}
				}
				if (curTarget.equals("etc+")) continue;
			//	if (curTarget.startsWith("Etc")||curTarget.startsWith("etc")) continue; // tighten up the specification of etc as a string!
//System.out.println("**************term +" +curTerm+" kterm "+curKinterm+" sex "+ curSex+" target "+curTarget);
				put(curTerm+" "+curKinterm+" "+ curSex, curTarget);
			}
		}

	}

	public void generateCayleyProducts(Hashtable kinNames) {

		for(int i = 0; i < protoCayley.size();i++) {
			CayleyInfo cinfo = (CayleyInfo) protoCayley.getNext();
				String keyPath = cinfo.term.toString();// keys.getSymbol(i).toString();
				AlgebraSymbol curSymbol = cinfo.generator;// theSymbols.getSymbol(j).getAlgebraSymbol();


				String curTerm = (String) kinNames.get(keyPath);
				String curKinterm = (String) kinNames.get(curSymbol.getValue());
				String curSex = curSymbol.getSex();
				AlgebraPath curCayley = cinfo.product; // generateCayleyProduct(keys.getSymbol(i),curSymbol);
				String curTarget = (String) kinNames.get(curCayley.toString());
				if (curTarget == null) { // diagnostic point - an algebra product exists that has no analogue in the kin term map
					Message.create(Mode.ALL,"Kinterm map is incomplete.", "Kinterm map is incomplete. Algebra term="+
													cinfo.product+". Assigning 'No Kin Term' to this path.",null,1);
					curTarget = "No Kin Term";
				}
				if (curTarget.equals("etc+")) continue;
//System.out.println("**************termA +" +curTerm+" kterm "+curKinterm+" sex "+ curSex+" target "+curTarget);
				put(curTerm+" "+curKinterm+" "+ curSex, curTarget);
		}
	}


// when able to make bake1 and bake2 have a cover term, bake, verify that B&C maps to
// bake in algebraKin; right now B&C maps to bake2
//versioin of this procedure with a lot of print statements is at the end of the file
//

	public void generateCayleyProducts_new(KinTermMap theMap,Hashtable kinNames, AlgebraPathVector keys) {
	    //String [] sexes = {"","M","F","N"}; // acquire sexes from the algebra, not like this
	    AlgebraSymbolVector asv = Algebra.getCurrent().getSexGenerators();
	    String [] sexes = new String[1+asv.size()];
	    sexes[0] = "";//sexes[1]="M";sexes[2]="F";
	    for (int i = 0;i < asv.size();i++){
		sexes[i+1]= ((AlgebraSymbol)asv.elementAt(i)).getValue();
	    }
	    String [] cand_terms = new String[sexes.length];
	    String [] cand_gens = new String[sexes.length];
            TransferKinInfoVector tkv = theMap.getTheKinTerms();
			//System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNN proto "+protoCayleyHash.toString());
	    for(int i = 0; i < keys.size();i++) {
			ListVector clv = protoCayleyHash.getList(keys.getSymbol(i).toString());
//System.out.println("CCCCCCCCCCCCCC clv "+clv+" keys "+keys.getSymbol(i));
			if (clv == null) {Debug.prout(0,"KEYS "+keys+" symbol "+keys.getSymbol(i).toString()+" CLV "+clv+" i "+i);continue;}
			if (clv.size()==0) continue; // possible error


			CayleyInfo cinfo = (CayleyInfo)clv.elementAt(0);
			AlgebraPath curCayley = cinfo.product; // generateCayleyProduct(keys.getSymbol(i),curSymbol);
			if (curCayley.toString().length() == 0) continue; // our bugger was M and F
			String curTarget = (String) kinNames.get(cinfo.product.toString());//kin term target
			String curTsex = tkv.getSex(curTarget);
			if (curTarget == null) { // diagnostic point - an algebra product exists that has no analogue in the kin term map
				Message.create(Mode.ALL,"Kinterm map is incomplete.", "Kinterm map is incomplete. Algebra term="+
					cinfo.product+". Assigning 'No Kin Term' to this path.",null,1);
				curTarget = "No Kin Term";
			}
			if (curTarget.equals("etc+")) continue;
			String gender = curCayley.getPathSex();//sex of algebra element target
			if (gender.equals("F") || gender.equals("M")) {
			} else gender = "N";
			String gen = cinfo.generator.toString();
			if ((gen.equals("M") || gen.equals("F")) && (clv.size() == 1)){
				clv = protoCayleyHash.getList(cinfo.term.toString());
				// not clear why needed; may be needed for AKT, but not needed for shipibo
				//clv.size()==1 prevents it from being activated for Shipibo
				//print out with fflag to see if the condition is satisfied and
				//clv has more than one term; if so, the remaining terms of clv will not
				//be processed; this was the problem with Shipibo terminology
				if (clv == null) throw new KintermMapException("clv is null");
			} else if (gen.equals("N")) {
				continue;
			} else {}
			int m = -1;
			for(clv.reset();clv.isNext();) {
				m++;
				cinfo = (CayleyInfo)clv.getNext();
				//AlgebraPathVector apv1 = cinfo.term.algPathToVectorx();
	boolean fflag = cinfo.term.toString().equals("B&C");fflag = false;
	if (fflag) System.out.println("BBBBBB " +cinfo+ " apv1 "+cinfo.term);
				//for (apv1.reset();apv1.isNext();){
				//String keyPath = apv1.getNext().toString();
				String keyPath = cinfo.term.toString();
				AlgebraSymbol curSymbol = cinfo.generator;// theSymbols.getSymbol(j).getAlgebraSymbol();
				if (curSymbol.toString().equals("M") || curSymbol.toString().equals("F")) {
					continue; //throw new KintermMapException("found m/f sex anyway");
				} else if (curSymbol.toString().equals("N")) continue; // throw new KintermMapException("found neutral sex anyway");;
				for (int j=0;j<sexes.length;j++) {
				//for (int j=0;j<sexes.length;j++) {
					cand_terms[j] = (String) kinNames.get(sexes[j]+keyPath);
if (fflag)	System.out.println("AAAAAAAAAA sex keypath "+sexes[j]+" "+keyPath+ " cand "+cand_terms[j]);
					cand_gens[j] = (String) kinNames.get(sexes[j]+curSymbol.getValue());
				}
					// ideally we will examine the undefined terms for evidence they are in the algebra
				String curASex = gender;//sex of alg target
				for (int j=0;j<sexes.length;j++) {
					String curTerm = cand_terms[j];//in kin term form
					if (curTerm == null) continue;
					if (curTerm.equals("<Undefined>") || curTerm.equals("<Mismatch>")) continue;
					String curTermSex = tkv.getSex(curTerm);
					for (int k=0;k<sexes.length;k++) {
						if (cand_gens[k] == null) continue;
						if (cand_gens[k].equals("<Undefined>") || cand_gens[k].equals("<Mismatch>")) continue;
						String gensex = tkv.getSex(cand_gens[k]);
					//next two lines implement sex marked spouse rule.  May need to embed into algebra
						if (Algebra.getCurrent().isArrow(curSymbol,Aops.SPOUSE)) {
								//if (sexes[j].equals(curSex)) continue;
							if (!curTermSex.equals("N") && curTermSex.equals(curTsex)) continue;
						}
						AlgebraPathVector apv = curCayley.algPathToVector();
						boolean notDefinedFlag = false;
						for (int ii = 0; ii < apv.size(); ii++){
							AlgebraPath ap = (AlgebraPath)apv.elementAt(ii);
							String newTarget = (String) kinNames.get(ap.toString());//kin term target
							String newTsex = tkv.getSex(newTarget);
							String newASex = ap.getPathSex();//sex of algebra element target
							if (!newASex.equals(newTsex)) {
								continue;
							}// diagnostic point - an algebra product exists that has no analogue in the kin term map

							if (!gensex.equals("N")&& !gensex.equals(newTsex)&& !newTsex.equals("N")) {
								continue;}
							if (sexes[k].equals(newASex) || sexes[k].equals("") || newASex.equals("N")) {
if (fflag)		System.out.println("**************Dterm +" +curTerm+" kterm "+cand_gens[k]+" sex "+ newASex+" target "+curTarget);
								put(curTerm+" "+cand_gens[k]+" "+ newASex, newTarget);
							}
						}
						String oldCurTarget = curTarget;
						if (!curASex.equals(curTsex)) { // diagnostic point - an algebra product exists that has no analogue in the kin term map
						   if (!curTarget.equals("<Mismatch>")){
							Message.create(Mode.ALL,"Kinterm map is incomplete.", "Kinterm map is incomplete. Algebra term="+
								cinfo.product+". Assigning "+curTarget+" to this path.",null,1);
							   curTarget = "No Kin Term";
						   }
							else Message.create(Mode.ALL,"Kinterm map mismatch with algebra.", "Kinterm map mismatch with algebra. Algebra term="+
								cinfo.product+". Assigning "+curTarget+" to this path.",null,1);
						}
						if (!gensex.equals("N")&& !gensex.equals(curTsex)&& !curTsex.equals("N") &&!curTarget.equals("<Mismatch>")) {
							curTarget = oldCurTarget;
							continue;
						}
						if (sexes[k].equals(curASex) || sexes[k].equals("") || curASex.equals("N") || curTarget.equals("<Mismatch>")) {
if (fflag)
System.out.println("**************Eterm +" +curTerm+" kterm "+cand_gens[k]+" sex "+ curASex+" target "+curTarget);
							put(curTerm+" "+cand_gens[k]+" "+ curASex, curTarget);
						}
						curTarget = oldCurTarget;
					}
				}
				//}
			}


	    }
	}

	/** Compares a Cayley table against an external Cayley table using the
	* external table's keys;
	* @param an object with some Cayley capabilities
	* @return a list of anomolies -- empty indicates half-isomorphism e.g. do both ways to determine isomorphism
	*/
	public StringVector compareCayleyTables(CayleyEntity aMap) {
		// Match key lengths?
		StringVector ret = new StringVector();
		//System.out.println("=========>protoCayleyHash contents="+protoCayleyHash.toString());
		//System.out.println("=========>CayleyTable contents="+toString());
	//	System.out.println("=========>protoCayleyHash contents="+protoCayleyHash.toString()+"\n----------------------");
	//	System.out.println("=========>CayleyTable contents="+toString()+"\n----------------------");
		for(Enumeration e = aMap.keys();e.hasMoreElements();) {
			String q = (String) e.nextElement();

			String a = aMap.getTerm(q);
			String b = getTerm(q);
	//System.out.println("XXXXXYYYY q="+q +" a= "+a +" b="+b+" amap "+aMap);
			if (!a.equals(b)) {
				// Message -- no Match ++++
				if (b.equals("<Undefined>") || b.equals("<Mismatch>")) {
					// Message -- ++++
	//System.out.println("XXXXXYYYY q="+q +" a= "+a +" b="+b);
				   if (b.equals("<Undefined>"))
					   ret.addElement(new String("<C-UNDEF> Path="+q+": KinMap term="+a+": Cayley term="+b));
					else
					   ret.addElement(new String("<C-MISMATCH> Path="+q+": KinMap term="+a+": Cayley term="+b));
				} else if (b.equals("<NotKey>")) {
					// Message -- ++++
	//System.out.println("XXXXXYYYY11111 q="+q +" a= "+a +" b="+b);
					ret.addElement(new String("<C-NOTKEY> Path="+q+": KinMap term="+a+": Cayley term="+b));
				} else if (b.equals("0")) {
					// Message -- ++++
					ret.addElement(new String("<C-B-ZERO> Path="+q+": KinMap term="+a+": Cayley term="+b));
				} else if (a.equals("0")) {
					// Message -- ++++
					ret.addElement(new String("<C-A-ZERO> Path="+q+": KinMap term="+a+": Cayley term="+b));
				} else {
					// Message -- not the same why ++++
					ret.addElement(new String("<C-OTHER> Path="+q+": KinMap term="+a+": Cayley term="+b));
				}

			}
		}
		return ret;
	}

	/** returns a product term  based on product key
	* @param mapTerm the key to look up
	* @return term associated with mapTerm
	*/
	public String getTerm(String mapTerm) {
		String s = (String) get(mapTerm);
		if (s == null) {
			s = "<NotKey>";
			//System.out.println("MMMMMMMMMMMMMMMMMMM mapTerm = "+mapTerm);
		}
		return s;
	}

	public String toString() {
		StringBuffer p = new StringBuffer();
		for(Enumeration e = keys();e.hasMoreElements();) {
			String q = (String) e.nextElement();
			String r = (String) get(q);
			p.append(q+" = "+r+"\n");
		}
		return p.toString();
	}

	public String toTableString() {
	    int i = 0;
		StringBuffer p = new StringBuffer();
//System.out.println("ZZZZZ at enumeration");
		for(Enumeration e = keys();e.hasMoreElements();) {
			String q = (String) e.nextElement();
			String r = (String) get(q);
//System.out.println("XXXX q ="+q+" r="+r);
			q = q.substring(0,q.lastIndexOf(" "));//eliminate terminal sex generator

// System.out.println("XXXX againq ="+q);
           if ((i = q.lastIndexOf(" [")) == -1) i = q.lastIndexOf(" ");
			String t = q.substring(0,i);
			String g = q.substring(i+1);
			p.append(g+" of " + t + " is " + r +"."+"\n");
		}
		return p.toString();
	}

        public Vector toColumnNames(int labelcode){
          Vector column = new Vector();
          switch (labelcode) {
            case ALGDATA:
              column = _toAlgColumnNames();
              break;
            case KINDATA:
              column = _toKinColumnNames();
              break;
          }
          return column;
        }

	Vector _toKinColumnNames() {
          Vector column = new Vector();
          int i = 0;
          column.addElement("OF");
          for(Enumeration e = keys();e.hasMoreElements();) {
            String q = (String) e.nextElement();
            q = q.substring(0,q.lastIndexOf(" "));//eliminate terminal sex generator
            if ((i = q.lastIndexOf(" [")) == -1) i = q.lastIndexOf(" ");
            String g = q.substring(i+1);
            if (column.indexOf(g) == -1) column.addElement(g);
          }
 //        System.out.println(" RETURN "+ column);
          return column;
	}

	Vector _toAlgColumnNames() {
		boolean sexFlag = Algebra.getCurrent().hasSexMarkedGenerators();
		Vector column = new Vector();
		column.addElement("o");
		protoCayley.reset();
		for(int i = 0; i < protoCayley.size();i++) {
			CayleyInfo cinfo = (CayleyInfo) protoCayley.getNext();
			if (sexFlag && cinfo.generator.isSexGenerator()) continue;
			String g = cinfo.generator.toString();
			if (column.indexOf(g) == -1) column.addElement(g);
		}
		protoCayley.reset();
		return column;
	}

    public Vector toData(Vector col, int labelcode) {
      Vector data = new Vector();
      switch(labelcode){
        case ALGDATA:
          data = _toAlgData(col);
          break;
        case KINDATA:
          data = _toKinData(col);
          break;
      }
      return (Vector) data;
    }

	Vector _toAlgData(Vector col) {
		boolean sexFlag = Algebra.getCurrent().hasSexMarkedGenerators();
		ListVector data = new ListVector(10,5);
		int cap = col.capacity();
		protoCayley.reset();
		for(int i = 0; i < protoCayley.size();i++) {
            CayleyInfo cinfo = (CayleyInfo) protoCayley.getNext();

       // System.out.println("XXXXXXXXXXXXX info "+cinfo);
            String q = cinfo.term.toString();
			if (q.equals("0")) continue;
			if (sexFlag && cinfo.generator.isSexGenerator()) continue;
            String g = cinfo.generator.toString();
            String r = cinfo.product.toString();
//System.out.println("1 q = "+q+" g "+g+" r "+r);
            int index = col.indexOf(g);
		//	System.out.println("g= "+g+" index= "+index);
            if (index < 0) index = 0;
            boolean flag = false;
            for (data.reset();data.isNext();) {
              Vector rw =  (Vector) data.getNext();
              if (rw.firstElement().equals(q)) {
                rw.setElementAt(r,index);
                flag = true;
                break;
              }
            }
            if (!flag) {
              Vector row = new Vector(cap);
              row.addElement(q);
              for (int j = 0; j < cap-1; j++)
                row.addElement("");
              row.setElementAt(r,index);
        //  System.out.println(" row "+row);
              data.addElement(row);
            }
		}
        return (Vector)data;
	}

	Vector _toKinData(Vector col) {
	    ListVector data = new ListVector(10,5);
	    int cap = col.capacity();
	    int iq = 0;
            for(Enumeration e = keys();e.hasMoreElements();) {
              String q = (String) e.nextElement();
              String r = (String) get(q);
              q = q.substring(0,q.lastIndexOf(" "));//eliminate terminal sex generator
              if ((iq = q.lastIndexOf(" [")) == -1) iq = q.lastIndexOf(" ");
              String t = q.substring(0,iq);
              String g = q.substring(iq+1);
              int index = col.indexOf(g);
//			System.out.println("g= "+g+" index= "+index);
              if (index < 0) index = 0;
              boolean flag = false;
              for (data.reset();data.isNext();) {
                Vector rw =  (Vector) data.getNext();
                if (rw.firstElement().equals(t)) {
			        //row.ensureCapacity(idex+1);
			        //System.out.println("rw = "+rw+" t= "+t);
                  rw.setElementAt(r,index);
			       // System.out.println("rw = "+rw+" r= "+r+" index "+index);
                  flag = true;
                  break;
                }
              }
              if (!flag) {
                Vector row = new Vector(cap);
                row.addElement(t);
                for (int i = 0; i < cap-1; i++)
                   row.addElement("");
                //System.out.println("XXXX idex= "+idex+" row "+row);

                row.setElementAt(r,index);
 			//p.append(g+" of " + t + " = " + r +"\n");
                data.addElement(row);
              }
            }
          return (Vector)data;
	}

	boolean complexPaths () {
		for(int i=0;i<theProducts.size();i++) {
			AlgebraPath thePath = theProducts.getSymbol(i);
			//System.out.println("the path "+thePath);
			if (!thePath.toString().equals("")){
			    if (complexPath(thePath)) return true;
			}
		}
	    return false;
	}

	boolean complexPath(AlgebraPath ap) {//complex path of form ...XA...XX; XABXX not complex to allow PACP to be drawn in Agta terminology
		//if (ap.isEquivalentPath() && ap.isReducedEquivalentPath())return complexPath(ap.getEquivalentPathLeft());//dwr 8/5
		//else if (ap.isReducedEquivalentPath()) return complexPath(ap.getReducedEquivalentPathLeft());//dwr 8/5
		if (ap.isReducedEquivalentPath()) return complexPath(ap.getReducedEquivalentPathLeft());//dwr 8/5
		//if (ap.getReducedProductPath().equivalentProduct()) return complexPath(ap.getReducedEquivalentPathLeft());
	   // System.out.println(" apath "+ap+" reduecd "+ap.reducedPath);
		AlgebraSymbol as = ap.reducedPath.getSymbol(0);
		int j = 0;
		for(int i=0;i<ap.reducedPath.size();i++) {
			if (ap.reducedPath.getSymbol(i).getValue() == "F" || ap.reducedPath.getSymbol(i).getValue() == "M") continue;
			if (as.equals(ap.reducedPath.getSymbol(i))) {
				 j++;
				 continue;
			}
			else {
				if ((j != ap.countSymbol(as.getValue())) && (j+1 != ap.countSymbol(as.getValue()))) {
					return true;
				}
				 else {
					  as = ap.reducedPath.getSymbol(i);
					  j = 1;
				 }
			}
	  }
	  return false;
	}

	int numberOfGenerators(){
	    int i = 0;
	    for (generators.reset();generators.isNext();){
	        AlgebraSymbol gen = generators.getNext();
	        if (!(gen.isIdentityElement())&& gen.getArrowType()!=Bops.SEXGEN){//!Algebra.getCurrent().isArrow(gen,Bops.MALEFEMALE)){
	            i++;
	        }
	    }
	    return i;
	}


	Hashtable prodHash = new Hashtable();
	KintermEntry ke = new KintermEntry();

    boolean useGenerator(String as){
        int i = 0;
        for (generators.reset();generators.isNext();) {
            String gen = generators.getNext().toString();
            if ((gen != "I") && (gen != "0")){
                i++;
                if (gen.equals(as)) {
                    if (((i == 1) && (!gen1Flag)) || ((i == 2) && (!gen2Flag)) ||
		    ((i == 3) && (!gen3Flag)) || ((i == 4) && (!gen4Flag)))
                        return false;
                    else return true;
                }
            }
        }
		return true;
    }

	public void populateModel(ThreeD md) {
	    populateModel(md,null);
	}

	boolean reverseCoords(){
		AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		int i = 0; int iU = 0; int iD = 0;
		for (gens.reset();gens.isNext();){
			i++;
		    AlgebraSymbol gen = gens.getNext();
			if (iD == 0 && gen.getArrowType() == Bops.DOWN) iD = i;
			else if (iU == 0 && gen.getArrowType() == Bops.UP) iU = i;
		}
		if (iU > 0 && iU < iD) return true;
		else return false;
	}

	public void populateModel(ThreeD md,Hashtable ak) {
          int ord=0;int del = 0;int radius = 0;
          Color clr1 = Color.gray;// neutral color
          Color clr = Color.black;
          Boolean split = new Boolean(false);
          boolean colorFlag = false;
          Integer line = Model3D.SOLID;
          Debug.prout(6,"Generators="+generators.toString());
          for(int i = 0;i <theProducts.size();i++) {
            String as = theProducts.getSymbol(i).toString();
			if (as.equals("M")|| as.equals("F")) continue;
            String trm = null;
            if (ak != null) trm = (String) ak.get(as);
          //  if ((as.equals("M") || as.equals("F")) && !Algebra.getCurrent().hasSexMarkedGenerators())
            //  continue;
            del = 0;
            if (theProducts.getSymbol(i).reducedPath.toString().equals("[]")){
              continue;}
            if ((theProducts.getSymbol(i).reducedPath.elementAt(0).toString().equals("M"))
            ||(theProducts.getSymbol(i).reducedPath.elementAt(0).toString().equals("F")))
               del = 1;
            clr = pathColor((AlgebraPath)theProducts.getSymbol(i));
            if (!colorFlag) colorFlag = (!clr.equals(clr1));
           // if (theProducts.getSymbol(i).getEquivalentPathLeft().reducedPath.size() >= MAXLENGTH+del) {//dwr 8/5
			if (theProducts.getSymbol(i).getReducedEquivalentPathLeft().reducedPath.size() >= MAXLENGTH+del) {//dwr 8/5
              clr = Color.white;//indicates that graph continues idefinitely
              radius = 0;
            }else {radius = 5;}
  		System.out.println("XX33 clr="+clr+" symbol as ="+as+" trm "+trm+" stuff "+theProducts.getSymbol(i));
            String kt = as;
            if (mapFlag) {
              kt = "No Term";
              if (trm != null)
                kt = trm;
            }
			if (reverseCoords()) {
				if (!as.equals("0")) ord = md.setPoint(coordinates[i][1],
				  coordinates[i][0],coordinates[i][2],kt,clr,radius);
				else ord = md.setPoint(coordinates[i][1],coordinates[i][0],coordinates[i][2],Color.black);
			} else {
				if (!as.equals("0")) ord = md.setPoint(coordinates[i][0],
				  coordinates[i][1],coordinates[i][2],kt,clr,radius);
				else ord = md.setPoint(coordinates[i][0],coordinates[i][1],coordinates[i][2],Color.black);
			}

            Debug.prout(6,as+": Coordinates="+coordinates[i][0]+", "+coordinates[i][1]+", "+coordinates[i][2]+" Ord="+ord);
            prodHash.put(as,new Integer(ord));
          }

          for (Enumeration e=protoCayleyHash.keys();e.hasMoreElements();) {
            Object keyx = e.nextElement();
            ListVector clv = protoCayleyHash.getList(keyx.toString());
            for (clv.reset();clv.isNext();){
              CayleyInfo cinfo = (CayleyInfo) clv.getNext();
              AlgebraSymbol as = (AlgebraSymbol) cinfo.generator;
              if (!useGenerator(as.toString())) continue;
              if (as.toString().equals("M") || as.toString().equals("F")) continue;
              Integer index1 = (Integer) prodHash.get(cinfo.product.toString());
              if (index1 == null || index1.intValue() == -1) continue;
              Integer index0 = (Integer) prodHash.get(cinfo.term.toString());
              if (index0 == null || index0.intValue() == -1) continue;
              clr = symbolColor(as);
              line = symbolLineStyle(as);
             //  split = symbolSplitNode(as,colorFlag,index0.intValue(),index1.intValue()); -- change colorflag to true for now...
              split = symbolSplitNode(as,true,index0.intValue(),index1.intValue());
              md.connectPoint(index0.intValue(),index1.intValue(),clr,line,split);
            }
          }

		  md.setSize();
	}

    public Boolean symbolSplitNode(AlgebraSymbol as,boolean cFlag,int i, int j) {
        AlgebraSymbolVector sp = Algebra.getCurrent().getSpouseArrows();
        return new Boolean(((i == j) && (cFlag ) && (sp.indexOf(as) != -1)));
    }

    Color pathColor(AlgebraPath ap){
        if (ap.isReducedEquivalentPath()) {
            Color clr1 = pathColor(ap.getReducedEquivalentPathLeft());
            Color clr2 = pathColor(ap.getReducedEquivalentPathRight());
            if (clr1 == clr2) return clr1;
            else return ke.getSexColour(ke.NEUTRAL);
        }
        else return symbolColor((AlgebraSymbol) ap.reducedPath.elementAt(0));
    }

	public Color symbolColor(AlgebraSymbol as) {
		Color clr = Color.black;//undefined color

		if (as.getSex().equals("N"))
		    clr = ke.getSexColour(ke.NEUTRAL);
		else if (as.getSex().equals("F"))
		    clr = ke.getSexColour(ke.FEMALE);
		else if (as.getSex().equals("M"))
		    clr = ke.getSexColour(ke.MALE);
		return clr;
	}

	public Integer symbolLineStyle(AlgebraSymbol as){
	    Integer i = Model3D.SOLID;
//Algebra.getCurrent().getUpArrows().indexOf(as);
	    if (as.getArrowType() == Bops.UP)
	        return new Integer(-i.intValue());
	    else if (as.getArrowType() == Bops.DOWN) {
			i = Model3D.DASH1;
	       // if (Algebra.getCurrent().getDownArrows().indexOf(as) > -1)
			return new Integer(-i.intValue());
		}
	    else if (as.getArrowType() == Bops.LEFT || as.getArrowType() == Bops.RIGHT) {
			i = Model3D.DASH2;
	       // if (Algebra.getCurrent().getDownArrows().indexOf(as) > -1)
			return new Integer(-i.intValue());
		}
	    else if (generators.indexOf(as) > -1)
	        return new Integer(-i.intValue());
	    return i;
	}



	public void populateModelGenerator(AlgebraSymbol g, ThreeD md) {
		for(int i=0; i < theProducts.size();i++) {
			populateModelProduct(theProducts.getSymbol(i),g,md);
		}
	}


	public void populateModelProduct(AlgebraPath a, AlgebraSymbol g, ThreeD md) {

		AlgebraPath b = (AlgebraPath) a.clone();
		b.product(g);
		//b.reducePath(b.path);
		Integer index1 = (Integer) prodHash.get(b.toString());
		if (index1 == null) {
		/*	float [] coord = new float[generators.size()];
			for(int i = 0;i<generators.size();i++) {
				coord[i] = b.countSymbol(generators.getSymbol(i).getValue());
			}*/
			Debug.prout(6,"A Product not found!!!"+b.toString()+" Generator g="+g.toString()+
								"  path="+a.toString());
			// winging we will ignore these for the second
		} else {
			Integer index0 = (Integer) prodHash.get(a.toString());
			if (index0 != null) {
				md.connectPoint(index0.intValue(),index1.intValue());
			} else Debug.prout(0,"B Product not found!!!"+a.toString());
		}
	}

	public String toXML(){
		return "";
	}

      final static int ALGDATA = 0;
      final static int KINDATA = 1;
}

