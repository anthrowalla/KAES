import java.util.*;

/* History
* 3/10/01 DR added the filter fProdAndTermSexComplement to get the complement of a specified sex
* 5/06 DR added buildSet version that uses a focal term stringvector as an argument
* modified the definition of a focal term
* 5/29 DR changed findFocalTerm to use effective terms
* 7/18 DR added isEffectiveTerm(String term) to test if term is an effective term
* in the TransferKinInfoVector
* 7/27 DR Added procedures theGeneration, theGenerations, computeGenerations()
* 8/3 DR Added procedures checkCompletenessOfStructure(), checkProducts to see if
* kin terms products with generators have been defined
* 8/20 DR added check for affinal term in findFocalTerms since focal terms are not
* affinal terms (otherwise Spouse in AKT would be a focal term)
* 9/1 DR added getSex(String term) to get the sex of a kin term
* 10/2 DR added procedure fDecompose(String tsex) to decompose covering terms into
* their constituent terms and then filter using tsex
* 10/6 DR added procedure fDecompose(String tsex) to decompose merged terms into covered term
* of sex complement to tsex
* 1/24 DR added test if (!lookupTerm(lookupTerm(gen).getEffectiveTerm()).isAGenerator)
*			p = k.getProducts().getGenerator(gen);//covering term not a generator
* to procedure getEffectiveProducts
* Added code to isIdentityElement for case where A,B are gens but [A,B] is not a
* generator
*/

public class TransferKinInfoVector extends Vector {

    Hashtable kintab = null, inverseHash = null;
    long version=0, hashVersion=0, inverseVersion=0;
    String title="Generic";
    Preferences kinPreferences=null;



    TransferKinInfo lookupTerm(String s) {
	    if (kintab == null) buildTable();
	    return ((TransferKinInfo) kintab.get(s));
    }


    public StringVector toStringVector() {
	    StringVector ret = new StringVector();
	    for(reset();isNext();) ret.addElement(getNext().getTerm());
	    return ret;
    }


    public String toXML() {
	    StringBuffer sbuf = new StringBuffer(400);
	    sbuf.append(XMLIndent.space+"<KintermMap>"+XFile.Eol);
	    XMLIndent.increment();
	    sbuf.append(XMLIndent.space+"<Name>AKT</Name>"+XFile.Eol);
	    sbuf.append(XMLIndent.space+theMaster.toXML());
	    sbuf.append(XMLIndent.space+"<Kinterms>"+XFile.Eol);
	    XMLIndent.increment();
	    for(reset();isNext();) {
		    sbuf.append(getNext().toXML());
	    }
	    XMLIndent.decrement();
	    sbuf.append(XMLIndent.space+"</Kinterms>"+XFile.Eol);
	    if (kinPreferences != null)
		    sbuf.append(kinPreferences.toXML());
	    XMLIndent.decrement();

	    sbuf.append("</KintermMap>"+XFile.Eol);
	    return sbuf.toString();
    }

    public TransferKinInfo getSymbol(int index) {
	    return (TransferKinInfo) elementAt(index);
    }

    public void buildTable() {
	    kintab = new Hashtable(100);
	    for (reset();isNext();) {
		    TransferKinInfo k = getNext();
		    kintab.put(k.term,k);
	    }
	    hashVersion = version;
    }

    int index = -1;

    public void reset() {
	    index = -1;
    }

    public boolean isNext() {
	    return (index+1 < size());
    }

    public boolean isNextEffectiveTerm() {
	    int ndx = index;
	    while(isNext()) {
		    TransferKinInfo k = getNext();
		    if (k.coveringTerm == null & k.coveredTerms == null) {
			    index--;
			    return true;
		    }
		    if (k.coveredTerms != null & k.isCovered.isTrue()) {
			    index--;
			    return true;
		    }
		    if (k.coveringTerm != null & k.isCovered.isFalse()) {
			    index--;
			    return true;
		    }
	    }
	    return false;
    }

    public TransferKinInfo effectiveTerm(TransferKinInfo k) {
	  if (k.coveringTerm == null & k.coveredTerms == null) {
		 return k;
	  }
	  if (k.coveredTerms != null & k.isCovered.isTrue()) {
		 return k;
	  }
	  if (k.coveringTerm != null & k.isCovered.isFalse()) {
		 return k;
	  }
	  return null;
    }

    public TransferKinInfo effectiveGenerator(TransferKinInfo k) {
	   if (!k.isGenerator()) return null;
	   if (k.coveringTerm == null & k.coveredTerms == null) {
		  return k;
	   }
	   if (k.coveredTerms != null & k.isCovered.isTrue()) {
		  return k;
	   }
	   if (k.coveringTerm != null & k.isCovered.isFalse()) {
		  return k;
	   }
	   if (k.coveringTerm != null & k.isCovered.isTrue()) {
		  if (lookupTerm(k.coveringTerm).isGenerator()) return null;
		  else return k;
	   }
	   return null;
    }

    public TransferKinInfo getNext() {
	    if (isNext())
		    return (TransferKinInfo) elementAt(++index);
	    else return null;
    }

    public TransferKinInfo getNextEffectiveTerm() {
	    if (isNextEffectiveTerm()) {
		    return getNext();
	    } else return null;
    }

    public StringVector getEffectiveProducts(String term, String gen) {
	    if (term == null) {
		    System.out.println("getEffectiveProducts: Term is null :");
		    return new StringVector();
	    }
	    return getEffectiveProducts(lookupTerm(term),gen);
    }

    public StringVector getEffectiveProducts(TransferKinInfo k, String gen) {
//System.out.println("Effective products for :"+" k="+k);
      StringVector ret = new StringVector();
      TransferProduct p;
      try {
		if (!lookupTerm(lookupTerm(gen).getEffectiveTerm()).isAGenerator)
			p = k.getProducts().getGenerator(gen);//covering term not a generator
		else
            p = k.getProducts().getGenerator(lookupTerm(gen).getEffectiveTerm());
      } catch (NullPointerException e) {
        System.out.println("Exception a");
        p = null;
	  }
      if (p == null) {
       // System.out.println("p=null in getEffectiveProducts "+"gen "+gen+" lolol "+lookupTerm(gen));
        //System.out.println("effect gen= "+lookupTerm(gen).getEffectiveTerm());
		return  null;
      }
      String b;
		 //System.out.println("Effective products for :"+p+" k="+k+" gen "+gen+" genX="+lookupTerm(gen).getEffectiveTerm());
	    // if (p == null) return ret;
	  for(p.reset();p.isNext();) {
		 String m = p.getNext();

		 k = lookupTerm(m);
//System.out.println("k= "+k+"effective "+k.getEffectiveTerm());
		 if (k == null) System.out.println("k=null m="+m);

		 if ( ret.indexOf((b = k.getEffectiveTerm()))  == -1) ret.addElement(b);
	  }
      return ret;
    }

    public StringVector getProducts(TransferKinInfo k, String gen) {
//System.out.println("Effective products for :"+" k="+k+" gen="+gen);
      StringVector ret = new StringVector();
      TransferProduct p;
      try {
		if (!lookupTerm(lookupTerm(gen).getEffectiveTerm()).isAGenerator)
			p = k.getProducts().getGenerator(gen);//covering term not a generator
		else
            p = k.getProducts().getGenerator(lookupTerm(gen).getEffectiveTerm());
      } catch (NullPointerException e) {
        System.out.println("Exception a");
        p = null;
	  }
      if (p == null) {
        System.out.println("p=null in getEffectiveProducts");
        System.out.println("effect gen= "+lookupTerm(gen).getEffectiveTerm());
      }
      String b;
	  for(p.reset();p.isNext();) {
		 b = p.getNext();
		 if ( ret.indexOf(b)  == -1) ret.addElement(b);
	  }
      return ret;
    }


    public TransferKinInfoVector buildSet(StringVector termV, int [] arrows) {
	    String term = "";
	    String fTerms = "";
	    Hashtable theSet = new Hashtable(100);
	    for(termV.reset();termV.isNext();) {
		    term = termV.getNext();
		TransferKinInfo k = lookupTerm(term);
		theSet.put(term,k.clone(true));
		_buildSet(k,arrows,theSet);
		fTerms = fTerms + term;
		if (termV.isNext()) fTerms = fTerms + " and ";
	    }
	    TransferKinInfoVector newKin = new TransferKinInfoVector();
	    newKin.setTitle(getTitle()+":"+"Connected to "+fTerms);
	    newKin.setTheMaster(getTheMaster());


	    for (Enumeration e = theSet.elements();e.hasMoreElements();) {
		    TransferKinInfo tk = (TransferKinInfo) e.nextElement();
		    newKin.addElement(tk);
	    }
	    newKin.buildTables();
	    newKin.fUpdateFromTerms();
	    newKin.buildTables();
	    return newKin;
    }
    public TransferKinInfoVector buildSet(String term, int [] arrows) {
	    Hashtable theSet = new Hashtable(100);

	    TransferKinInfo k = lookupTerm(term);
	    theSet.put(term,k.clone(true));
	    _buildSet(k,arrows,theSet);
	    TransferKinInfoVector newKin = new TransferKinInfoVector();
	    newKin.setTitle(getTitle()+":"+"Connected to "+term);
	    newKin.setTheMaster(getTheMaster());


	    for (Enumeration e = theSet.elements();e.hasMoreElements();) {
		    TransferKinInfo tk = (TransferKinInfo) e.nextElement();
		    newKin.addElement(tk);
	    }
	    newKin.buildTables();
	    newKin.fUpdateFromTerms();
	    newKin.buildTables();
	    return newKin;
    }


    public void _buildSet(TransferKinInfo kinterm, int [] arrows, Hashtable h) {
	    TransferProductsVector p = kinterm.getProducts();
	    if (p == null) {
						    Debug.prout(0,"_buildset ... Products p crashed!");
					    }
	    for (p.reset();p.isNext();) {
		    TransferProduct k = p.getNext();
			if (k == null) {
						    Debug.prout(0,"_buildset ... Products k crashed!");
					    }
		    TransferKinInfo m = lookupTerm(k.getGenerator());
		    TransferKinInfo n=null;
		    if (m == null) continue;
		    for(int i=0;i<arrows.length;i++) {
			    if (arrows[i] == m.orientation) {
				    for(k.reset();k.isNext();) {
					    String tx = k.getNext();
					    if (tx == null) {
						    Debug.prout(0,"_buildset ... Term tx1 crashed!");
					    }
					    if (h.get(tx) == null) {
						    n = lookupTerm(tx);
						    if (n == null) continue;
						    h.put(tx,n.clone(true));
					    } else{
						    n = null;
						    continue;
					    }
					    String eff;
					    eff = n.getEffectiveTerm();
					    if (eff == null) {
						    Debug.prout(0,"_buildset ... Term tx2 crashed!");
					    } else if (h.get(eff) == null) {
						    TransferKinInfo mx = lookupTerm(eff);
						    if (mx != null) {
							    h.put(eff,mx.clone(true));
						    }
					    }
					    if (n != null) _buildSet(n,arrows,h);
				    }
				    break;
			    }
		    }
	    }
    }

    public synchronized Object clone(boolean deep) {
	    TransferKinInfoVector t;
	    if (deep) {
		    t = new TransferKinInfoVector();
		    for(reset();isNext();) {
			    t.addElement(getNext().clone(deep));
		    }
		    t.setTitle(getTitle());
		    t.setTheMaster(getTheMaster());
		    t.buildTables();
		    t.title = title;
		    return t;
	    } else {
		    t = (TransferKinInfoVector) this.clone();
		    t.setTitle(getTitle());
		    t.setTheMaster(getTheMaster());
		    t.buildTables();
	    }
	    return t;
    }

    public TransferKinInfoVector deepClone() {
	    return (TransferKinInfoVector) clone(true);
    }

    public void setTitle(String s) {
	    title = s;
    }
    public String getTitle() {
	    return title;
    }

    /** get all generators
    @return list of generators
    */
    public TransferKinInfoVector getGenerators() {
	    TransferKinInfoVector ret = new TransferKinInfoVector();
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (k.isGenerator()) ret.addElement(k);
	    }
	    return ret;
    }

    /** get all generators of a given orientation
    @param orientation the constants that define the arrow orientation of the generator.
    @see ORIENTATION
    @return list of selected generators
    */
    public TransferKinInfoVector getGenerators(int orientation) {
	    TransferKinInfoVector ret = new TransferKinInfoVector();
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (k.getOrientation() == orientation)
			    ret.addElement(k);
	    }
	    return ret;
    }

    /** get all Effective generators
    Effective generators respect the status of covering and covered terms if the covered instance
    variable is set. Covering terms replace one or more other terms.
    @return list of effective generators
    */
    public TransferKinInfoVector getEffectiveGenerators() {
	    TransferKinInfoVector ret = new TransferKinInfoVector();
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		   if (effectiveGenerator(k) != null) {
			  ret.addElement(k);
		   }
	    }
	    return ret;
    }

    /** get all effective generators of a given orientation
    @param orientation the constants that define the arrow orientation of the generator.
    @see ORIENTATION
    @return list of selected generators
    */
    public TransferKinInfoVector getEffectiveGenerators(int orientation) {
	   TransferKinInfoVector ret = new TransferKinInfoVector();
	   for(reset();isNext();) {
		  TransferKinInfo k = getNext();
		  //System.out.println(" XXXXXZZZZZZZZZZZZZZZ k "+k +" orient "+k.getOrientation());
		  if (effectiveGenerator(k) != null && k.getOrientation() == orientation) {
			 ret.addElement(k);
		  }
	   }
	   return ret;
    }
    /** get all effective generators of a given orientation and sex
    @param orientation the constants that define the arrow orientation of the generator.
	@param sex the sex of the generators to be returned
    @see ORIENTATION
    @return list of selected generators
    */
    public TransferKinInfoVector getEffectiveGenerators(int orientation, String sex) {
	   TransferKinInfoVector ret = new TransferKinInfoVector();
	   for(reset();isNext();) {
		  TransferKinInfo k = getNext();
		  //System.out.println(" XXXXXZZZZZZZZZZZZZZZ k "+k +" orient "+k.getOrientation());
		  if (effectiveGenerator(k) != null && k.getOrientation() == orientation &&
		  k.getSex().equals(sex)) {
			 ret.addElement(k);
		  }
	   }
	   return ret;
    }

    /** delete the term currently pointed to by index
    */
    public void delete() {
	    TransferKinInfo t = getSymbol(index);
	    delete(t);
    }

    /** delete the term
    */
    public void delete(TransferKinInfo term) {
		if (term.coveredTerms != null) {
			for (int i=0;i<term.coveredTerms.size();i++) {
				String s = term.coveredTerms.getSymbol(i);
				TransferKinInfo k = lookupTerm(s);
				if (k != null) {
					k.coveringTerm = null;
					k.isCovered.setFalse();
				}
			}
		}
		if (term.coveringTerm != null) {
			TransferKinInfo t = lookupTerm(term.coveringTerm);
			if (t != null) delete(t);
		}
	    int t = indexOf(term);
	    term.getProducts().clearProducts();
	    removeElementAt(t);
	    if (t <= index) index--;
    }

    /** filter out all kinterms not of sex tsex
    @param tsex the sex to keep
    @return the list of terms of sex tsex
    */
    public TransferKinInfoVector fSex(String tsex) {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (tsex.indexOf(k.getSex()) == -1) {
			    delete();
		    }
	    }
	    return this;
    }

    /** filter out all kinterms that are not generators
    @return the list of terms that are generators
    */
    public TransferKinInfoVector fGen() {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (!k.isGenerator()) {
			    delete();
		    }
	    }
	    return this;
    }

    /** filter out all kinterms not of sex tsex and are not generators
    @param tsex the sex to keep
    @return the list of terms of sex tsex that are generators
    */
    public TransferKinInfoVector fGenSex(String tsex) {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    k.getProducts().fSex(tsex,this);
	    }
	    return this;
    }

    /** filter out all kinterm products that are not of sex tsex
    @param tsex the sex to keep
    @return the list of terms and their products that are of sex tsex
    */
    public TransferKinInfoVector fProdSex(String tsex) {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    k.getProducts().fProdSex(tsex,this);
	    }
	    return this;
    }

    /** filter out all kinterm products and terms that are of sex tsex
    @param tsex the sex to remove
    @return the list of terms and their products that are not of sex tsex
    */

    public TransferKinInfoVector fProdAndTermSexComplement(String tsex) {
	ListVector lv = new ListVector();
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (k.getSex().equals(tsex))
			lv.addElement(k.getTerm());
		    else
			k.getProducts().fProdSexComplement(tsex,this);
	    }
	    if (lv.size()!= 0)
		fRemove(lv);
	    return this;
    }

    /** replace covering term by the covered term for covered terms
     *  with sex the complement of tsex
    @param tsex the sex to remove
    @return the list of terms after covering terms have been replaced
    */

    public TransferKinInfoVector fDecompose(String tsex) {
	StringVector termV = new StringVector();
	StringVector cTermV = new StringVector();
	for (reset();isNext();) {
	    TransferKinInfo k = getNext();
	    if ((k.coveringTerm != null) && (!k.getSex().equals(tsex))) {
		termV.addElement(k.getTerm());
		cTermV.addElement(k.coveringTerm);
	    }
	}
	//System.out.println(" term v"+termV+" ctermv "+cTermV);
	for(reset();isNext();) {
	    TransferKinInfo k = getNext();
	    TransferProductsVector tp = k.getProducts();
	    for(tp.reset();tp.isNext();) {
		TransferProduct p = tp.getNext();
		String curGen = p.getGenerator();
		if (cTermV.indexOf(curGen) != -1){
		    p.set(0,(String)termV.elementAt(cTermV.indexOf(curGen)));
		}
	    }
	    if (termV.indexOf(k.getTerm())!= -1){
	        k.isCovered.setFalse();
	        k.coveringTerm = null;
	    }
	    if (cTermV.indexOf(k.getTerm()) != -1) {
		delete(k);
	    }
	}
	return this;
    }

/* Appears to be redundant
 String getCoveredTerm(String cterm){
	for (reset();isNext();){
	    TransferKinInfo k = getNext();
	    if ((k.coveringTerm != null) && (k.coveringTerm.equals(cterm)))
		return k.getTerm();
	}
	return null;
    }
*/

    /** filter out all generators that are not of orientation arrow
    @param arrow the orientation to keep
    @return the list of generators of orientation arrow
    */
    public TransferKinInfoVector fGenArrow(int arrow) {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    k.getProducts().fArrow(arrow,this);
	    }
	    return this;
    }

    /** Remove kin terms from the kin term map that have no active generators. As currently written,
    * this may leave generator product references to the deleted term, so these should be pruned
    * if necessary! <br>
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fNoProductions() {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    if (k.getProducts().countActiveGenerators() == 0) {
			    delete();
		    }
	    }
	    return this;
    }

    /** Looks up a term in the inverse table to see what production the term appears in.
    */
    public StringVector getInverse(String term) {
	    if (inverseHash == null || inverseVersion != version) buildInverseTable();
	    return (StringVector) inverseHash.get(term);
    }

    /** Builds a hash table with the term as key and list of terms for which it is a generator productions.
    * The table needs to be rebuilt whenever a significant change is made to the kin term map.
    */
    public void buildInverseTable() {
	    inverseHash = new Hashtable(50);
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		  //  System.out.println(" HEREzz IS K "+k);
		    String curTerm = k.term;
		    if (inverseHash.get(curTerm) == null) inverseHash.put(curTerm,new StringVector());
		    TransferProductsVector tp = k.getProducts();
//System.out.println(" HERE IS TP"+tp);
		    for(tp.reset();tp.isNext();) {
			    TransferProduct p = tp.getNext();
			    String curGen = p.getGenerator();
			    for(p.reset();p.isNext();) {
				    String refTerm = p.getNext();

				    StringVector v = (StringVector) inverseHash.get(refTerm);
//System.out.println(" THE REF "+refTerm+" v "+v);
				    if (v == null) {
					    v = new StringVector();
					    inverseHash.put(refTerm,v);
				    }
				    v.addElement(curTerm + ";" + curGen);
				   // inverseHash.put(refTerm,v);
			    }
		    }
	    }
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		   // System.out.println(" HERE IS K "+k);
		    if (k.coveredTerms != null) {
			    StringVector kinv = (StringVector) inverseHash.get(k.term);
			    for(k.coveredTerms.reset();k.coveredTerms.isNext();) {
				    StringVector ninv = (StringVector) inverseHash.get(k.coveredTerms.getNext());
//System.out.println(" k IS "+k+" covered "+" ninv "+ninv);
				 if (ninv != null)  for(ninv.reset();ninv.isNext();) {
					    String vv = ninv.getNext();
					    if (kinv.indexOf(vv) == -1) kinv.addElement(vv);
				 } else {
					 new Message("Terms covered by "+k.toString()+" have no inverse relations", "", "", -10).addMessage();
				    }
			    }
		    }
	    }

	    inverseVersion = version;
    }

    /** finds the reciprocal (if any) and reciprocal equation
    * @param gen the generator in question
    * @param focal a focal term of the kin term map
    * @return a list of the equation(s), if any. Empty list implies none.
    */
    public ListVector findReciprocals(String gen, String focal) {
	    ListVector ret = new ListVector();
	    TransferKinInfo g = lookupTerm(gen);
    //	System.out.println("findReciprocals: term found for "+gen+"="+g.toString());
	    TransferKinInfoVector k = getEffectiveGenerators();
    //	System.out.println("findReciprocals: effectiveGenerators="+k.toString());
	    StringVector pk;
	    for (k.reset();k.isNext();) {
		    TransferKinInfo kp = k.getNext();
		    pk = getEffectiveProducts(g,kp.getTerm());
    //		System.out.println("findReciprocals: effectiveProducts pk="+pk.toString());
		    if (pk.indexOf(focal) != -1) {
			    StringVector m = new StringVector();
			    m.addElement(focal);
			    m.addElement(gen);
			    m.addElement(kp.getEffectiveTerm());
			    // System.out.println("findReciprocals: A: effectiveProducts m="+m);
			    ret.addElement(m);
			    continue;
		    }
	    }

	    StringVector fi = getInverse(focal);
    //	System.out.println("findReciprocals: inverse="+fi.toString());
	    for (fi.reset();fi.isNext();) {
		    String xx = fi.getNext();
		    String xy = xx.substring(0,xx.indexOf(";"));
		    if (!lookupTerm(xy).getEffectiveTerm().equals(xy)) continue;
		    xx = xx.substring(xx.indexOf(";")+1);
		    if (!xx.equals(gen)) continue;
		    StringVector m = new StringVector();
		    m.addElement(focal);
		    m.addElement(xy);
		    m.addElement(gen);
		    //System.out.println("findReciprocals: B: effectiveProducts m="+m);
		    ret.addElement(m);
		    continue;
	    }
	    return ret;
    }

    /** Adds to this kin term map any missing kin terms referenced in its generator productions.
    * Creates a kin term map that contains all kin terms referenced in its generation productions.
    * ???It appears that this will not work if the tables have been rebuilt -  @see buildtables().
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fUpdateFromProductions() {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    TransferProductsVector tp = k.getProducts();
		    for(tp.reset();tp.isNext();) {
			    TransferProduct p = tp.getNext();
			    for(p.reset();p.isNext();) {
				    String t =p.getNext();
				    TransferKinInfo kk = lookupTerm(t);
				    if (kk != null) {
					    if (indexOf(kk) == -1) addElement(kk);
				    } else {
					    Debug.prout(0,"Error in TransferKinInfoVector-fUpdateFromProductions - missing term = "+t);
				    }
			    }
		    }
	    }
	    return this;
    }


    /** Removes from this kin term map any kin terms not referenced in its generator productions,
	    or remove generator productions not in this kin term map.
    * Creates a kin term map that contains only generation productions which are terms of this map.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fUpdateFromTerms() {
	    for(reset();isNext();) {
		    TransferKinInfo k = getNext();
		    TransferProductsVector tp = k.getProducts();
		    for(tp.reset();tp.isNext();) {
			    TransferProduct p = tp.getNext();
			    for(p.reset();p.isNext();) {
				    String t =p.getNext();
				    TransferKinInfo kk = lookupTerm(t);
				    if (kk == null) {
					    p.delete();
				    }
			    }
		    }
	    }
	    return this;
    }

    /** (re-)builds the tables for the kin terms
    */

    public void buildTables() {
	    buildTable();
	    buildInverseTable();
    }

    /** Remove kin terms from this kin term map.
    * Creates a kin term map that does not contain the kin terms in <i>termsToRemove</i>.
    * If these terms are not in the kin term map then it is unchanged. References to these
    * terms are also removed from generator productions, as well as all productions associated with
    * a generator included in the list.
    * @param termsToRemove List of kin terms to be removed from this.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fRemove(Vector termsToRemove) {
	    for(int i = 0;i<termsToRemove.size();i++) {
		    TransferKinInfo t = lookupTerm((String) termsToRemove.elementAt(i));
		    if (t != null) {
			    int index = indexOf(t);
			    removeElementAt(index);
		    }
	    }
	    for(reset();isNext();) {
		    getNext().fRemove(termsToRemove);
	    }
	    return this;
    }

    /** Finds the union of this kin term map with another
    * Creates a kin term map that contains all kin terms in <i>this</i>
    * and the kin term map <i>union</i>. Note that the union only includes the set of productions
    * currently in the two kin term maps. Use <i>fMerge</i> to restore cross references between
    * the terms in the two maps that might have been lost in earlier transformations. <br>
    * Only this kin term map will be altered. The parameter kin term map will never be changed.
    * @param union The kin term map to be added to this.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fUnion(TransferKinInfoVector union) {
	    for(union.reset(); union.isNext();) {
		    TransferKinInfo u = union.getNext();
		    TransferKinInfo k = lookupTerm(u.term);
		    if (k == null) addElement(u);
		    else {
			    k.fUnion(u);
		    }
	    }
	    return this;
    }

    /** Finds the difference of this kin term map with another
    * Creates a kin term map that contains only kin terms in <i>this</i> that are not shared
    * with the kin term map <i>diff</i>.
    * This includes the difference of generator productions - only productions that are different
    * are retained. <br>
    * Only this kin term map will be altered. The parameter kin term map will never be changed.
    * @param diff The kin term map to be removed from this.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fDifference(TransferKinInfoVector diff) {
	    for(diff.reset(); diff.isNext();) {
		    TransferKinInfo u = diff.getNext();
		    TransferKinInfo k = lookupTerm(u.term);
		    if (k != null) k.fDifference(u);
	    }
	    return this;
    }

    /** Finds the intersection of this kin term map with another
    * Creates a kin term map that contains all kin terms in <i>this</i> that are shared with <i>intersect</i>.
    * This includes the intersection of generator productions - only productions that are in common
    * are reported. <br>
    * Only this kin term map will be altered. The parameter kin term map will never be changed.
    * @param intersect The kin term map to be intersected with this.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fIntersection(TransferKinInfoVector intersect) {
	    for(intersect.reset(); intersect.isNext();) {
		    TransferKinInfo u = intersect.getNext();
		    TransferKinInfo k = lookupTerm(u.term);
		    if (k != null) k.fIntersection(u);
	    }
	    for(reset(); isNext();) {
		    TransferKinInfo k = getNext();
		    if (intersect.indexOf(k) == -1) delete();
	    }
	    return this;
    }

    /** Merges this kin term map with another
    * This merges by examining the original kin term map that both maps were derived from,
    * and re-inserts generator productions for each map that include references to terms in the
    * other map. The object is to produce a complete kin term map with respect to the included
    * relationships. The results should be identical to a map that was created by removing
    * terms that are in neither of the present kin term maps.
    * Only this kin term map will be altered. The parameter map will never be changed.
    * @param merge The kin term map to be merged into this.
    * @param unity The original kin term map from which both this and <i>merge</i> were derived.
    * @return The current kin map. This is a convenience so that the filters can be applied inline.
    */
    public TransferKinInfoVector fMerge(TransferKinInfoVector merge, TransferKinInfoVector unity) {
	    TransferKinInfo k=null,l=null;
	    Vector names = new Vector(10);
	    //Vector namesk = new Vector(10);
	    for(merge.reset(); merge.isNext();) {
		    names.addElement(merge.getNext().term);
	    }
	    for(reset(); isNext();) {
		    names.addElement(getNext().term);
	    }
	    for (reset();isNext();) {
			    k = getNext();
			    // System.out.println("here " +k.term);
			    l = unity.lookupTerm(k.term);
			    if (l==null) Debug.prout(0,"tsk tsk!");
			    else
				    k.fMerge(l,names);
	    }
	    TransferKinInfoVector m1 = merge.deepClone();
	    for (m1.reset();m1.isNext();) {
			    k = m1.getNext();
			    l = unity.lookupTerm(k.term);
			    if (l==null) Debug.prout(0,"tsk tsk!2");
			    else
				    k.fMerge(l,names);
	    }
	    fUnion(m1);
	    return this;
    }

    /** Focal terms - non-gendered
    * our expectations is that there will be only one!
    */
    StringVector focalTerms=null;

    /** Returns the focal terms
    * @return the focal terms
    */
    public StringVector getFocalTerms() {
	    if (focalTerms == null) findFocalTerms();
	    return focalTerms;
    }

    public StringVector getEffectiveFocalTerms() {
	    StringVector k = getFocalTerms();
	Debug.prout(0," focal terms k "+k);
	    StringVector ret = new StringVector();
	    for(int i =0;i < k.size();i++) {
		    String r = lookupTerm(k.getSymbol(i)).getEffectiveTerm();
		    if (ret.indexOf(r) == -1) ret.addElement(r);
	    }
	    return ret;
    }

	public StringVector getEffectiveFocalTerms(String sex) {
	    StringVector k = getFocalTerms();
	System.out.println(" focal terms k "+k);
	    StringVector ret = new StringVector();
	    for(int i =0;i < k.size();i++) {
			if (!lookupTerm(k.getSymbol(i)).getSex().equals(sex)) continue;
		    String r = lookupTerm(k.getSymbol(i)).getEffectiveTerm();
		    if (ret.indexOf(r) == -1) ret.addElement(r);
	    }
	    return ret;
    }

    public ListVector findStructuralEquivalents() {
	    ListVector ret = new ListVector();
	    for (int i=0;i<size()-1;i++) {
		    TransferKinInfo a = getSymbol(i);
		    //if (a == null) System.out.println("a is null at top");
		    boolean leap = false;
		    for(ret.reset();ret.isNext();) {
			    StringVector eq = (StringVector) ret.getNext();
			    if (eq.indexOf(a.term) != -1) {
				    leap = true;
				    break;
			    }
		    }
		    if (leap) continue;
		    StringVector trefs = getInverse(a.term);
		    if (trefs == null) System.out.println("trefs is null at top");
		    boolean flag = false;

		    StringVector t = new StringVector();
		    if (t == null) System.out.println("t is null at middle");
		    t.addElement(a.term);

		    for (int j=i+1;j<size();j++) {
			    TransferKinInfo b = getSymbol(j);
			    if (a.coveredTerms != null) {
				    if (a.coveredTerms.indexOf(b.term) != -1) continue;
			    } else if (a.coveringTerm == b.term) continue;

			    if (b.getProducts().equivalentTo(a.getProducts())) {
				    // perhaps we should check to see if a appears in an earlier list
				    StringVector orefs = getInverse(b.term);
				    if (orefs == null) Debug.prout(0,"orefs is null at bottom");
				    if (orefs.size() != trefs.size()) continue;
				    if (orefs.size() == 0 && trefs.size() == 0) {t.addElement(b);continue;}
				    flag = true;
				    for(trefs.reset();trefs.isNext();) {
					    if (orefs.indexOf(trefs.getNext()) == -1) {
						    flag = false;
						    break;
					    }
				    }
				    if (flag) t.addElement(b.term);
			    }
		    }
		    if (t.size() > 1) ret.addElement(t);
	    }
	    return ret;
    }


/*	public ListVector findStructuralEquivalentsRelaxed() {
	    TransferKinInfoVector tk = deepClone();
	    tk.fRemove(findFocalTerms());
	    return tk.findStructuralEquivalents();
    }
*/

    public TransferKinInfoVector mergeGenerators(ListVector candidateSets) {

		    for(candidateSets.reset();candidateSets.isNext();) {
			    Object o = candidateSets.getNext();
			    if (o instanceof StringVector) {
				    StringVector t = (StringVector) o;
				    mergeGenerators(t);
			    } else {
				    TransferKinInfoVector t = (TransferKinInfoVector) o;
				    mergeGenerators(t);
			    }
		    }
		    return this;
    }

    public TransferKinInfoVector mergeGenerators(TransferKinInfoVector candidates) {
	    StringVector cands = new StringVector();
	    for(candidates.reset();candidates.isNext();) {
		    cands.addElement(candidates.getNext().term);
	    }
	    return mergeGenerators(cands);
    }
    /** Needs to be checked that having one or more invalid generators in conjuntion with
    * a valid generator will not cause a problem
    */

    public TransferKinInfoVector mergeGenerators(StringVector candidates) { // throws Exception {
	    //if (candidates.size() < 2) throw new Exception("Can't do findMapsWithEquivalence without two or more candidates!");
	    TransferKinInfoVector tk = this;//.deepClone();
	    for(tk.reset();tk.isNext();) {
		    TransferProductsVector tpv = tk.getNext().getProducts();
		    tpv.reset();
		    candidates.reset();
		    TransferProduct unionGen = tpv.getGenerator(candidates.getNext());
		    if (unionGen == null) continue;
		    for(;candidates.isNext();) {
			    TransferProduct tp = tpv.getGenerator(candidates.getNext());
			    if (tp == null) continue;
			    //System.out.println("merge "+candidates.toString()+" "+tp.toString());
			    unionGen.fUnion(tp);
			    tpv.deleteGenerator(tp.getGenerator());
		    }
		    unionGen.setGenerator(candidates.toString());
	    }

	    tk.buildInverseTable();

	    return this;
	    //return tk.findStructuralEquivalents();
    }


    public TransferKinInfoVector mergeTerms(ListVector candidateSets) {

	    for(candidateSets.reset();candidateSets.isNext();) {
		    Object o = candidateSets.getNext();
		    if (o instanceof StringVector) {
			    StringVector t = (StringVector) o;
			    mergeTerms(t);
		    } else {
			    TransferKinInfoVector t = (TransferKinInfoVector) o;
			    mergeTerms(t);
		    }
	    }
	    return this;
    }



    public TransferKinInfoVector mergeTerms(ListVector candidateSets,boolean flag) {

	    for(candidateSets.reset();candidateSets.isNext();) {
		    Object o = candidateSets.getNext();
		    if (o instanceof StringVector) {
			    StringVector t = (StringVector) o;
			    mergeTerms(t,flag);
		    } else {
			    TransferKinInfoVector t = (TransferKinInfoVector) o;
			    mergeTerms(t);
		    }
	    }
	    return this;
    }





    public TransferKinInfoVector mergeTerms(TransferKinInfoVector candidates) {
	    StringVector cands = new StringVector();
	    for(candidates.reset();candidates.isNext();) {
		    cands.addElement(candidates.getNext().term);
	    }
	    return mergeTerms(cands);
    }

    public TransferKinInfoVector mergeTerms(StringVector candidates) {
	   return mergeTerms(candidates, false);
	}

    public TransferKinInfoVector mergeTerms(StringVector candidates, boolean dmerge) {
	    if (candidates.size() < 2) return this;
	    if (lookupTerm(candidates.toString()) != null) return this;
	    StringVector areCovered = new StringVector();
	    candidates.reset();
	    TransferKinInfo k = lookupTerm(candidates.getNext());
	    areCovered.addElement(k.term);
	    String sex = k.getSex();
		int orientation = -1; // No orientation by default, changed from ... k.orientation;
		boolean isAGenerator= false; // not set as generator in this routine ... was k.isGenerator();
	    boolean isAEtc = k.isEtc();

	    for(;candidates.isNext();) {
		    TransferKinInfo k2 = lookupTerm(candidates.getNext());
		    if (!k2.getSex().equals(sex)) {
			    sex = "N";
		    }
		//    if (k2.orientation != orientation) {
			   // orientation = -1;
		//    }
		    if (k2.isEtc() != isAEtc) { // mystery as to dissassociated behaviour when unequal - to be or not to be
			    isAEtc = true; // cop out for moment -- could have advanced setting of expert panel
			    // ++++ Message mentioning this anomoly FIO -- please mail home
		    }

		   // if (k2.isGenerator() != isAGenerator)
			  // isAGenerator = false;
		    areCovered.addElement(k2.term);
	    }
	    TransferKinInfo newTerm = new TransferKinInfo(candidates.toString(),sex,isAGenerator,orientation,k.origin);
	    //System.out.println("TransferKinInfoVector: getTheMaster()="+getTheMaster());
	    //System.out.println("TransferKinInfoVector: newVariableSet()="+getTheMaster().newVariableSet());
	    newTerm.setTheVariables(getTheMaster().newVariableSet());
	    newTerm.setSex(sex);
	    newTerm.setEtc(isAEtc);
	    TransferProductsVector newProds = (TransferProductsVector) k.getProducts().clone(true);
	    newTerm.products = newProds;

	    for(;candidates.isNext();) {
		    TransferKinInfo k2 = lookupTerm(candidates.getNext());
		    newTerm.fUnion(k2);
	    }
	    newTerm.coveredTerms = areCovered;

	    for(areCovered.reset();areCovered.isNext();) {
		    TransferKinInfo k2 = lookupTerm(areCovered.getNext());
		    k2.isCovered = newTerm.isCovered;
		    k2.coveringTerm = newTerm.term;
	    }
	    newTerm.isCovered.setTrue();
	//	newTerm.setDropMerge(dmerge); // added 13-1-2003 mf
	    this.addElement(newTerm);
	    buildTables();
	    return this;
    }

    /** Verifies if term is an identity element for this map,
    * our expectations is that there will be only one that is non-gendered,
    * and two in some cases where gendered (as in Tribriand terminology)
    * @term term to verified as identity term
    */

    public boolean isIdentityTerm(String term) {
	    return isIdentityTerm(lookupTerm(term),"N");
    }

    /** Verifies if term is an identity element for this map,
    * our expectations is that there will be only one that is non-gendered,
    * and two in some cases where gendered (as in Tribriand terminology)
    * @term term to verified as identity term
    */

    public boolean isIdentityTerm(TransferKinInfo term) {
	    return isIdentityTerm(term,"N");
    }

    /** Verifies if term is an identity element for this map,
    * with respect to generators of sex given by the parameter sex
    * @sex test agains generators of sex given by sex only for identity term
    * @term term to verified as identity term
    */
    public boolean isIdentityTerm(TransferKinInfo term, String sex) {
		TransferKinInfo k;
		//TransferProductsVector tp = term.getProducts();
		TransferKinInfoVector gens = getEffectiveGenerators();
		//System.out.println("GEN "+ gens);
		String g = "";
		for(gens.reset();gens.isNext();) {
			TransferKinInfo gen = gens.getNext();
			//System.out.println(" term "+term +" sex "+sex +" gen sex "+gen.sex);
			if ((!sex.equals("N")) && (!gen.getSex().equals(sex))) continue;
			g = gen.term;
	//System.out.println(" GENS "+gens+" term "+term+" g "+g);
			StringVector w = getEffectiveProducts(term,g);
	//System.out.println(" GENS "+gens+" term "+term+" g "+g+" w "+w+" size "+w.size());
		    if (w.size() == 0) return false;
			if (!lookupTerm(w.getSymbol(0)).isGenerator()) {
				TransferProductsVector tpv = term.getProducts();
				for (tpv.reset();tpv.isNext();){
					TransferProduct tp = tpv.getNext();
					if (tp.elementAt(0).equals(g))
						if (!tp.elementAt(1).equals(g))
							return false;
				}
			//System.out.println(" products "+tpv);
			}
			else if (!w.getSymbol(0).equals(g)) {
			    //System.out.println("isIdentityTerm: failed identity term "+term+" "+g+" "+w.toString());
			    return false;
			}
		}
		if (g.equals("")) return false;
		if (term.isGenerator()) {
			for(reset();isNextEffectiveTerm();) {
			k = getNextEffectiveTerm();
			//System.out.println(" term "+term +" k "+k+" getprod "+term.getProducts());
			StringVector gensv = getEffectiveProducts(k,term.getTerm());
			//StringVector gensv = getEffectiveProducts(k,term.toString());
			if (gensv.size() != 1) return false;
			if (!gensv.getSymbol(0).equals(k.getEffectiveTerm())) return false;
			}
		} else {
			Debug.prout(6,"isIdentityTerm: candidate identity term "+term);
			return true;
		}
		//	System.out.println("isIdentityTerm: general success "+term);
		return true;
    }


    /** Finds the focal terms for this map, if any
    * our expectations is that there will be only one that is non-gendered,
	* hence the same as an identity term,
    * and two in some cases where gendered (as in Tribriand terminology)
    * @return list of the <b>String</b> <i>names</i> of all focal terms.
    */
    public StringVector findFocalTerms() {
		buildTables();
		TransferKinInfo k = new TransferKinInfo();
		StringVector sret = new StringVector(1);
		for (int i = 0; i < this.size();i++) {
			k = (TransferKinInfo) this.elementAt(i);
			if (isIdentityTerm(k)){
				//System.out.println(" k.term "+k.term);
				sret.addElement(k.term);
				break;//only 1 identity term
			}
		}
		if (sret.size() == 0) {
			for (int i = 0; i < this.size();i++){
				k = (TransferKinInfo)this.elementAt(i);
				//System.out.println(" k "+k +" sex "+k.getSex());
				if (isIdentityTerm(k,k.getSex())){
					//System.out.println(" focal k.term "+k.term);
					sret.addElement(k.term);
				}
			}
		}
		focalTerms = sret;
		return sret;
    }

    public  boolean checkTermsForMultipleProducts() {
	    for (reset();isNextEffectiveTerm();) {
		    TransferKinInfo t = getNextEffectiveTerm();
		    TransferProductsVector pv = t.getProducts();
		    for(pv.reset();pv.isNext();) {
			    if (pv.getNext().getSize() > 1) {
				    if (getEffectiveProducts(t,pv.getSymbol(pv.index).getGenerator()).size() > 1) return true;
			    }
		    }
	    }
	    return false;
    }

    int up, down, right, left, spouse, spouser;

    public boolean checkSimplicityOfStructure() {
	System.out.println("effect gen "+getEffectiveGenerators());
	    up = getEffectiveGenerators(UP).size();
	    down = getEffectiveGenerators(DOWN).size();
	    left = getEffectiveGenerators(LEFT).size();
	    right = getEffectiveGenerators(RIGHT).size();
	    spouse = getEffectiveGenerators(SPOUSE).size();
	    spouser = getEffectiveGenerators(SPOUSER).size();
Debug.prout(1,"check simplicity: u " + up + " d " + down +" l "+left+" r"+right+" s "+spouse
+" sr "+spouser);
//System.out.println("check simplicity: u " + up + " d " + down +" l "+left+" r"+right+" s "+spouse
//+" sr "+spouser);
	    if (up > 1) return false;
	    if (down > 1) return false;
	   // if (up == 0) return false;
		if (up == 1 && down ==1) return false;
	    if (left > 1) return false;
	    if (right > 1) return false;
		if (left == 1 && right ==1) return false;
	    if (spouse != 0 || spouser != 0) return false;
	    if (checkTermsForMultipleProducts()) return false;

	    return true;
    }

    public boolean checkPrimaryStructure() {
		if (checkSimplicityOfStructure()) {
			up = getEffectiveGenerators(UP).size();
			down = getEffectiveGenerators(DOWN).size();
			left = getEffectiveGenerators(LEFT).size();
			right = getEffectiveGenerators(RIGHT).size();
			spouse = getEffectiveGenerators(SPOUSE).size();
			spouser = getEffectiveGenerators(SPOUSER).size();
			if ((up == 1) && (down == 1)) return false;
			if ((left == 1) && (right == 1)) return false;
			return true;
		}
	    else return false;
    }

    final static int UP = KintermEditObject.UP;
    final static int DOWN = KintermEditObject.DOWN;
    final static int RIGHT = KintermEditObject.RIGHT;
    final static int LEFT = KintermEditObject.LEFT;
    final static int SPOUSE = KintermEditObject.SPOUSE;
    final static int SPOUSER = KintermEditObject.SPOUSER;
    final static int SEXGEN = KintermEditObject.SEXGEN;

   final static int[] ORIENTATIONS = KintermEditObject.ORIENTATIONS;

    final static int NONE = KintermEditObject.NONE;;



    public void setTheMaster(VariablesFactory theMaster) {
	    this.theMaster = theMaster;
    }

    public VariablesFactory getTheMaster() {
	    return theMaster;
    }
    protected VariablesFactory theMaster=null;

    /** Checks if a kin term is an effective kin term.
    * @param term is the term to be checked
    * @return boolean flag
    */
    public boolean isEffectiveTerm(String term){
      for (this.reset();this.isNext();){
        if (this.getNext().getEffectiveTerm().equals(term))
          return true;
      }
    return false;
    }

   // Hashtable kinToGen = new Hashtable();

    Integer theGeneration(Hashtable kinToGen,TransferKinInfo gen,String term){
      Integer addI = (Integer) kinToGen.get(term);
      int i = addI.intValue();
      if ((gen.orientation == DOWN)||(gen.orientation == UP)){
        if (gen.orientation == DOWN) i--;
        else i++;
        addI = new Integer(i);
      }
      return addI;
    }

    void theGenerations(Hashtable kinToGen,String term){
      TransferKinInfoVector gens = getEffectiveGenerators();
      for (gens.reset();gens.isNext();){
        TransferKinInfo gen = gens.getNext();
        StringVector prods = getEffectiveProducts(term,gen.getTerm());
        if ((prods.size() > 1) || (prods.size() == 0)) continue;
        String prod = (String)prods.elementAt(0);
        if (kinToGen.get(prod) == null){
          kinToGen.put(prod,theGeneration(kinToGen,gen,term));
          //System.out.println(" product "+prod+" gen "+theGeneration(kinToGen,gen,term));
          theGenerations(kinToGen,prod);
        } else if ( Math.abs(((Integer)kinToGen.get(prod)).intValue()) >
                            Math.abs(theGeneration(kinToGen,gen,term).intValue())){
            //kinToGen.put(prod,new Integer(99));
           // System.out.println("the prod "+prod+" int "+((Integer)kinToGen.get(prod)).intValue()+
			//" gen "+theGeneration(kinToGen,gen,term).intValue());
			kinToGen.put(prod,theGeneration(kinToGen,gen,term));
          }
      }
    }

    /** Checks the generation status of each kin term.
    * @return hash table with terms as keys and generation status as entry in Integer form
    */
    public Hashtable computeGenerations(){
      Hashtable kinToGen = new Hashtable();
      StringVector ftv = getEffectiveFocalTerms();
      for (ftv.reset();ftv.isNext();){
        String term = ftv.getNext();
        kinToGen.put(term,new Integer(0));
        theGenerations(kinToGen,term);
      }
      return kinToGen;
    }

   /** @return List of generator, term pairs where generator of term is not defined in the map.
   */
   StringVector checkProducts(StringVector newProd,StringVector sv,String term){
      TransferKinInfoVector gens = getEffectiveGenerators();
      for (gens.reset();gens.isNext();){
        TransferKinInfo gen = gens.getNext();
        StringVector prods = getEffectiveProducts(term,gen.getTerm());
        if (prods.size() == 0) {
          sv.addElement(gen.getTerm());
          sv.addElement(term);
        } else for (prods.reset(); prods.isNext();){
            String newterm = prods.getNext();
            if (newProd.indexOf(newterm) == -1) {
              newProd.addElement(newterm);
              sv=checkProducts(newProd,sv,newterm);
              }
        }
      }
      return sv;
    }

    /** Checks map for completeness.
    * @return List of generator, term pairs where generator of term is not defined in the map.
    */
    public StringVector checkCompletenessOfStructure(){
      StringVector newProd = new StringVector(10,5);
      StringVector ret = new StringVector(10,1);
      StringVector ftv = getEffectiveFocalTerms();
      for (ftv.reset();ftv.isNext();){
        String term = ftv.getNext();
        ret = checkProducts(newProd,ret,term);
      }
      System.out.println(" missing terms "+ret);
      return ret;
    }

    /** get sex of a kin terms in string form
     *  @term the term
     *  @return sex of the term
     */
    public String getSex(String term) {
        for (reset();isNext();){
            TransferKinInfo tk = getNext();
            if (tk.getTerm().equals(term)){
                return tk.getSex();//target sex
            }
        }
	return "";
    }

   public boolean inArrowsSimilar(TransferKinInfo k1, TransferKinInfo k2) {
	  // test to see if the pattern if 'in' arrows is similar
	  StringVector s1 = getInverse(k1.getTerm());
	  StringVector s2 = getInverse(k2.getTerm());
	  String g0 = s2.toString(); // for referencing
      if (s1.size() != s2.size()) return false;//not similar
	  for (s1.reset();s1.isNext();) {
		 String g1 = s1.getNext();
		 int marker;
		 String g2 = g1.substring(0,(marker = g1.indexOf(';'))); // term
		 String g3 = g1.substring(marker+1); // generator
		 if (g0.indexOf(g2+";") == -1) { // is term present? if not bail out
			Debug.prout(0,"inArrowsSimilar: arrows in for "+k1.getTerm()+" not similar to "+k2.getTerm());
			return false;
		 }
		 boolean found=false;
		 int o1 = lookupTerm(g3).getOrientation();
		 for (s2.reset();s2.isNext();) {
			String h1 = s2.getNext();
			String h2 = h1.substring(0,(marker = h1.indexOf(';'))); // term
			if (h2.equals(g2)) {
			   String h3 = h1.substring(marker+1); // generator
			   if (o1 != lookupTerm(h3).getOrientation()) {
				  Debug.prout(0,"inArrowsSimilar: orientation of arrows in for "+k1.getTerm()+" not similar to "+k2.getTerm());
				  return false;
			   }
			   found = true;
			}
		 }
		 if (!found) return false; // must find each term
	  }
	  return true; // all in terms matched in name and arrow orientation of generator linking
   }

   public boolean areGeneratorsEquivalent(TransferKinInfo t1, TransferKinInfo t2) {
	  for (reset();isNext();) {
		 TransferKinInfo to = getNext();
		 TransferProduct tp1 = to.getProducts().getGenerator(t1.getTerm());
		 TransferProduct tp2 = to.getProducts().getGenerator(t2.getTerm());
		 if (tp2.size() != tp1.size()) return false; // not equivalent
		 tp2.reset();
		 for (tp1.reset();tp1.isNext();) { // automatically skips first position, which is the generator
			String s1 = lookupTerm(tp1.getNext()).getEffectiveTerm();
			if (!s1.equals(lookupTerm(tp2.getNext()).getEffectiveTerm())) return false;
		 }
	  }
	  return true; // made it all the way through
   }
/*
   public boolean mergeArrows(TransferKinInfo t1, TransferKinInfo t2) {
	  if (!areGeneratorsEquivalent(t1,t2)) return false;
	  if (t1.coveringTerm != null) {
		 if (t1.coveringTerm.equals(t2.coveringTerm)) {
			TransferKinInfo tc = lookupTerm(t1.coveringTerm);
			tc.orientation = t1.orientation; // acting on structure tests in progress
									// may need to isolate to prevent mess
			return true;
		 } else {
			System.out.println("mergeArrows: t1.coveringTerm("+t1.coveringTerm+") different from t2.coveringTerm("+t2.coveringTerm+")");
			return false;
		 }
	  } else {
		 if (t2.coveringTerm != null) {
			System.out.println("mergeArrows: t1.coveringTerm(null) different from t2.coveringTerm("+t2.coveringTerm+")");
			return false;
		 }
		 if (!t1.outArrowsIdentical(t2)) return false; // do not merge
		 if (!inArrowsSimilar(t1, t2)) return false; // do not merge

		 StringVector lv= new StringVector();
		 lv.add(t1.getTerm());
		 lv.add(t2.getTerm());
		 mergeTerms(lv,true);
		 TransferKinInfo tc = lookupTerm(t1.coveringTerm);
		 tc.orientation = t1.orientation;
		 buildTable();
		 buildInverseTable();
		 lv = findFocalTerms(); // --- need to calc focal terms
		 return true;
	  }
   }

   public boolean mergeArrows() {
	  boolean flag = false;
	  TransferKinInfoVector tv = getEffectiveGenerators(); // Effective generators??
	  TransferKinInfoVector tx = getEffectiveGenerators(); // Effective generators??
	  for(tv.reset();tv.isNext();) {
		 TransferKinInfo t1 = tv.getNext();
		 for(tx.reset();tx.isNext();) {
			TransferKinInfo t2 = tx.getNext();
			if (mergeArrows(t1,t2)) flag = true;
		 }
	  }
	  return flag;
   }
*/

//DR added this procedure
    public ListVector findStructuralSimilar() {
	    ListVector ret = new ListVector();
	    for (int i=0;i<size()-1;i++) {
		    TransferKinInfo a = getSymbol(i);
		    //if (a == null) System.out.println("a is null at top");
		    boolean leap = false;
		    for(ret.reset();ret.isNext();) {
			    StringVector eq = (StringVector) ret.getNext();
			    if (eq.indexOf(a.term) != -1) {
				    leap = true;
				    break;
			    }
		    }
		    if (leap) continue;
		    StringVector t = new StringVector();
		    t.addElement(a.term);
		    for (int j=i+1;j<size();j++) {
			    TransferKinInfo b = getSymbol(j);
			    if (a.coveredTerms != null) {
				    if (a.coveredTerms.indexOf(b.term) != -1) continue;
			    } else if (a.coveringTerm == b.term) continue;
				if (!a.outArrowsIdentical(b)) continue; // do not merge
				if (!inArrowsSimilar(a, b)) continue; // do not merge
				t.addElement(b.term);
		    }
		    if (t.size() > 1) ret.addElement(t);
	    }
	    return ret;
    }

    /** Checks the map to see if equations of the form spouse of parent = parent
    * are valid in the kin term map.
    * @return boolean flag
    */

    boolean testMapForSpouseOfParentEquations() {
     // if (testMap == false) return true;
     // TransferKinInfoVector tk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
      //getTk();
      TransferKinInfoVector pa = getEffectiveGenerators(UP);
      TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
	  if (sp.size() == 0) return false;
	  String sex1 = "";
	  String sex2 = "";
      for (pa.reset();pa.isNext();){
        TransferKinInfo parent = pa.getNext();
		  sex1 = parent.getSex();
        for (sp.reset();sp.isNext();){
			TransferKinInfo spouse = sp.getNext();
			sex2 = spouse.getSex();
			if (sex1.equals("N") || sex2.equals("N") || !sex1.equals(sex2)){
				StringVector sv = getEffectiveProducts(parent,spouse.getTerm());
				if (!pa.isEffectiveTerm((String)sv.elementAt(0))) return false;				
			}
        }
      }
      return true;
    }

    /** Checks the map to see if equations of the form son = daughter for descendant terms
    * are valid in the kin term map. Only applies when there is neutral identity element.
    * @return boolean flag
    */
    boolean testMapForLinealDescendantRule(){//so or da of <= 0 generation element is the same
                                             //if (testMap == false) return true;
                                             // getTk()
        StringVector ft = getFocalTerms();
        if (ft.size() != 1) return false;
       //if (getSex((String)ft.elementAt(0)) != "N") return false;
        
        if (!getSex((String)ft.elementAt(0)).equals("N")) return false;
        TransferKinInfoVector ch = getEffectiveGenerators(DOWN);
        Hashtable kinToGen = computeGenerations();
        StringVector termV = new StringVector(3,1);
        for(Enumeration e = kinToGen.keys();e.hasMoreElements();) {
            String key = (String) e.nextElement();
            int i = ((Integer) kinToGen.get(key)).intValue();
            //if (i <=0){termV.addElement(key);}
            //System.out.println("XXXXXXXXXXXXXXXXXXXXXXX) key "+key+" i "+i);
            if ((i <=0) && (ch.size() == 2)){//this was 99
                StringVector prod1 = getEffectiveProducts(key,((TransferKinInfo)ch.elementAt(0)).getTerm());
                StringVector prod2 = getEffectiveProducts(key,((TransferKinInfo)ch.elementAt(1)).getTerm());
                //return prod1.equals(prod2);
                if (!prod1.equals(prod2)) return false;
            }
        }
        return true;
    }

    /** Checks the map to see if equations of the form sibling of spouse = spouse of sibling
    * are valid in the kin term map.
    * @return boolean flag
    */

    boolean testMapForSiblingInLawEquations() {
      //if (testMap == false) return true;
     // getTk();
      TransferKinInfoVector pa = getEffectiveGenerators(UP);
      TransferKinInfoVector ch = getEffectiveGenerators(DOWN);
      TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
	  if (sp.size() == 0) return false;
      StringVector sibinlaw1 = termProduct(sp,ch,pa);
     // Debug.prout(4," sib stuff "+sibinlaw1);    
      if (sibinlaw1.size() == 0) return false;
      StringVector sibinlaw2 = termProduct(ch,pa,sp);
    //  Debug.prout(4," sib stuff 2 "+sibinlaw2);
      if (sibinlaw2.size() == 0) return false;
      if (sibinlaw1.size() != sibinlaw2.size()) return false;
      for (sibinlaw1.reset();sibinlaw1.isNext();) {
        if (sibinlaw2.indexOf(sibinlaw1.getNext()) == -1) return false;
      }
     // Debug.prout(4," sib stuff "+sibinlaw1+" other "+sibinlaw2);
      return true;
    }
    /** Checks the map to see if equations of the form parent of child-in-law = 0
    * are valid in the kin term map.
    * @return boolean flag
    */

    boolean testMapForParentOfChildInLawEquations(){
    //  if (testMap == false) return true;
     // getTk();
      TransferKinInfoVector pa = getEffectiveGenerators(UP);
      TransferKinInfoVector ch = getEffectiveGenerators(DOWN);
      TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
	  if (sp.size() == 0) return false;
      Debug.prout(4,"Child in law "+termProduct(pa,sp,ch)+" pa "+pa+" sp "+sp +" ch "+ch);
      return (termProduct(pa,sp,ch).size() == 0);
    }
    /** Checks the map to see if equations of the form son of FemaleSelf = son
	*  or daughter of MaleSelf = Daughter, where son of MaleSelf = son and
	*  daughter of FemaleSelf = daughter,
    * are valid in the kin term map.
    * @return boolean flag
    */
    boolean testMapForSingleChildProperty(){//ch of sib distinct from own ch
      TransferKinInfoVector side = getEffectiveGenerators(LEFT);
      TransferKinInfoVector right = getEffectiveGenerators(RIGHT);
	  for (right.reset();right.isNext();) side.addElement(right.getNext());
	  if (side.size() == 0) return false;
	  TransferKinInfoVector down = getEffectiveGenerators(DOWN);
	  if (down.size() == 0) return false;
	  StringVector prod = termProduct(down,side);
	  if (prod.size() == 0) return false;
	  StringVector sv = down.toStringVector();
	  boolean flag = false;
	  for (prod.reset();prod.isNext();){
		String s = prod.getNext();
		if (flag = (sv.indexOf(s) == -1)) break;
	  }
	  return !flag;
   }

    /** Tests the map for atomic sib generators
    * @return boolean flag
    */
    boolean testMapForAtomicSib(){//child of parent = focal term
		StringVector focal = getEffectiveFocalTerms();
		if (focal.size() != 1) return false;
		String sex = getSex((String)focal.elementAt(0));
        TransferKinInfoVector down = getEffectiveGenerators(DOWN,sex);
		if (down.size() != 1) return false;
		TransferKinInfoVector up = getEffectiveGenerators(UP,sex);
		if (up.size() != 1) return false;
	    StringVector prod = termProduct(down,up);
		if (prod.size() != 1) return false;
		return ((String)prod.elementAt(0)).equals((String)focal.elementAt(0));
	}

    /** Checks the map to see if Son of Focal Term = Daughter of Focal Term
    * is valid in the kin term map.
	* @param sex sex of focal term
    * @return boolean flag
    */
    boolean testMapForChildEquivalence(String sex){//so male person = daughter of male person
		StringVector focal = getEffectiveFocalTerms(sex);
		if (focal.size() != 1) return false;
        TransferKinInfoVector down0 = getEffectiveGenerators(DOWN,sex);
		TransferKinInfoVector down1 = null;
		if (sex.equals("M")) down1 = getEffectiveGenerators(DOWN,"F");
		else down1 = getEffectiveGenerators(DOWN,"M");
	    if (down0.size() != 1 || down1.size() != 1) return false;
		//System.out.println("Down0 "+ down0+" down1 "+down1);
	    StringVector prod0 = getEffectiveProducts((TransferKinInfo)down0.elementAt(0),(String)focal.elementAt(0));
	    StringVector prod1 = getEffectiveProducts((TransferKinInfo)down1.elementAt(0),(String)focal.elementAt(0));
	    if (prod0.size() != 1 || prod1.size() != 1) return false;
		return ((String)prod0.elementAt(0)).equals((String)prod1.elementAt(0));
   }

    /** Checks the map to see if Son of Sibling of Focal Term = Daughter of Sibling of Focal Term
    * is valid in the kin term map where Sibling and Focal Term have opposite sex
	* @param sex sex of focal term
    * @return boolean flag
    */
    boolean testMapForChildSibEquivalence(String sex){//so sister male person = daughter sister of male person
		StringVector focal0 = getEffectiveFocalTerms(sex);
        TransferKinInfoVector down0 = getEffectiveGenerators(DOWN,sex);
		StringVector focal1 = null;
		TransferKinInfoVector down1 = null;
		if (sex.equals("M")) {
			down1 = getEffectiveGenerators(DOWN,"F");
			focal1 = getEffectiveFocalTerms("F");
		}
		else {
			down1 = getEffectiveGenerators(DOWN,"M");
			focal1 = getEffectiveFocalTerms("M");
		}
	    if (down0.size() != 1 || down1.size() != 1) return false;
	    if (focal0.size() != 1 || focal1.size() != 1) return false;
	    StringVector prod0 = getEffectiveProducts((TransferKinInfo)focal1.elementAt(0),(String)focal0.elementAt(0));
	    StringVector prod1 = getEffectiveProducts((TransferKinInfo)focal1.elementAt(0),(String)focal0.elementAt(0));
	    if (prod0.size() != 1 || prod1.size() != 1) return false;
	    prod0 = getEffectiveProducts((TransferKinInfo)down0.elementAt(0),(String)prod0.elementAt(0));
	    prod1 = getEffectiveProducts((TransferKinInfo)down1.elementAt(0),(String)prod1.elementAt(0));
	    if (prod0.size() != 1 || prod1.size() != 1) return false;
		return ((String)prod0.elementAt(0)).equals((String)prod1.elementAt(0));
   }

    StringVector termProduct(TransferKinInfoVector t1,TransferKinInfoVector t2, TransferKinInfoVector t3){
      StringVector ret = new StringVector(1,1);
      for (t3.reset();t3.isNext();){
        TransferKinInfo term3 = t3.getNext();
        for (t2.reset();t2.isNext();){
          StringVector trm = getEffectiveProducts(term3,t2.getNext().getTerm());
          //Debug.prout(4," sib " +trm);
          for (trm.reset();trm.isNext();){
            String term = trm.getNext();
            for (t1.reset();t1.isNext();){
              StringVector aTerm = getEffectiveProducts(term,t1.getNext().getTerm());
              for (aTerm.reset();aTerm.isNext();){
                String s = aTerm.getNext();
                if (ret.indexOf(s) == -1) ret.addElement(s);
              }
            }
          }
        }
      }
      return ret;
    }

    StringVector termProduct(TransferKinInfoVector t1,TransferKinInfoVector t2){
      StringVector ret = new StringVector(1,1);
      for (t2.reset();t2.isNext();){
        TransferKinInfo term = t2.getNext();
        for (t1.reset();t1.isNext();){
          StringVector aTerm = getEffectiveProducts(term,t1.getNext().getTerm());
          for (aTerm.reset();aTerm.isNext();){
            String s = aTerm.getNext();
            if (ret.indexOf(s) == -1) ret.addElement(s);
          }
        }
      }
      return ret;
    }


}

