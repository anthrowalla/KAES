import java.util.*;



/* Revision History

* 31/10 MF Changed System.out.println to Debug.prout(1,M)
* 7/02 DR modified clone by adding clone to getAlgebraSymbol
* 7/12 DR added deepClone
* 7/24 DR added "if (rec != null)" test to clone
* 9/24 DR corrected error in reciprocal() for mixed sex
* 9/28 DR added reciprocal(String sex); added sameSex(); added sameSex(String sex);
* 9/30 DR added sameElements();
* ?/10 DR added procedure consistentSex()
* 11/12 DR added procedures sameArrowPattern, equivalentProduct()
* 2/9 DR added procedure sameArrow(Hashtable ht)
*/

// see also TermTable for storing retrieving paths



public class AlgebraSymbolVector extends Vector {



	int elem=0;



	public AlgebraSymbolVector() {

		super(1,1);

	}



	public AlgebraSymbolVector(AlgebraSymbol a) {

		super(1,1);

		this.addToBeginning(a);

	}



	public AlgebraSymbolVector(int n) {

		super(n);

	}



	public AlgebraSymbolVector(int n, int m) {

		super(n,m);

	}



	public AlgebraSymbol getSymbol(int i) {

		elem = size()-1-i;
		if (elem < 0 || elem >= size()) throw new AlgebraSymbolException("AlgebraSymbolVector: Element out of Range");

		return (AlgebraSymbol) elementAt(elem);

	}



// would like to do following, but is declared as final method. Must use getSymbol(n),

// not elementAt with AlgebraSymbolVectors!!!!!!!!!!!



/*	public Object elementAt(int i) {

		elem = size()-1-i;

		return elementAt(elem);

	}

*/



	public AlgebraSymbol getLast() {

		if (size() == 0) return null;

		elem = 0;

		return (AlgebraSymbol) elementAt(0);

	}



	public int getIndex() {

		int k = super.indexOf(this);

		if (k >= 0) return size()-1-k;

		else return k;

	}



	public void setSymbol(AlgebraSymbol a,int n) {

		int k = size()-1-n;

		setElementAt(a,k);

	}



	public AlgebraSymbol getFirst() {

		elem = size()-1;
		if (elem < 0) throw new AlgebraSymbolVectorException("Empty AlgebraSymbolVector at getFirst()");
		return (AlgebraSymbol) elementAt(elem);

	}

	public void reset() {

		elem = size();

	}

	public boolean isNext() {

		return (elem > 0);

	}



	public AlgebraSymbol getNext() {

		if (--elem >= 0)

			return (AlgebraSymbol) elementAt(elem);

		else {

			elem=0;

			return null;

		}

	}



	public AlgebraSymbol getPrev() {

		if (++elem < size())

			return (AlgebraSymbol) elementAt(elem);

		else {

			elem=size()-1;

			return null;

		}

	}



	public void addToEnd(AlgebraSymbol a) { // must use instead of addElement

		super.insertElementAt(a,0);

	}

	public void addToBeginning(AlgebraSymbol a) { // must use instead of addElement
		super.addElement(a);
	}

	public void removeEnd() { // must use instead of removeElement
		super.removeElementAt(0);
	}

	public void removeBeginning() { // must use instead of removeElement
		super.removeElementAt(size()-1);
	}

	public int locateSymbol(AlgebraSymbol a) {
		for (int i=0;i<this.size();i++) {
			if (a.getValue().equals(((AlgebraSymbol)((AlgebraSymbolVector)this).getSymbol(i)).getValue()))
				return i;
		}
		return -1;
	}

    public AlgebraSymbolVector deepClone() {
        return (AlgebraSymbolVector) clone(true);
    }

	public Object clone(boolean deep) {
		AlgebraSymbolVector ac = new AlgebraSymbolVector(5,1);
 		AlgebraSymbolVector a = (AlgebraSymbolVector) super.clone();
		if (deep) {
            for (reset();isNext();){
			    ac.addToEnd(((AlgebraSymbol) getNext()).getAlgebraSymbol().makeClone());
			}
		    for (reset();isNext();) {
		        AlgebraSymbol as = (AlgebraSymbol) getNext();
				AlgebraSymbol rec = as.getReciprocal();
//Debug.prout(0," as "+ as+" rec "+rec);
				if (rec != null)
				   if (indexOf(rec) != -1)  ((AlgebraSymbol)ac.elementAt(indexOf(as))).setReciprocal((AlgebraSymbol)ac.elementAt(indexOf(rec)));
		    }
			for (int i = a.size()-1;i>= 0;i--) {
			    a.setElementAt((AlgebraSymbol) ac.elementAt(i),i);
			}
		}
		else {
		    for (int i = a.size()-1;i>= 0;i--) {
			    //a.setElementAt(((AlgebraSymbol) a.elementAt(i)).getAlgebraSymbol().makeClone(),i);
			    a.setElementAt(((AlgebraSymbol) a.elementAt(i)).getAlgebraSymbol(),i);
			}
		}
		return a;
	}

	public AlgebraSymbolVector copy() {
		return (AlgebraSymbolVector) clone(false);
	}

	/** determine if all elements in the product are the same
	*/

	public boolean sameElements(){
	    AlgebraSymbol as = getFirst();
	    for (reset();isNext();){
	        if (!as.equals(getNext())) return false;
	    }
	    return true;
	}

	/** determine if all elements in product have the same sex
	* @theSex compare to theSex
	* @return true if all elements have same sex as theSex
	*/

    public boolean sameSex(String theSex) {
		for (int i = 0;i<size();i++) {
			if (!(getSymbol(i).getSex().equals(theSex)))
			    return false;
		}
       return true;
    }
	/** determine if all elements in product have the same sex
	* @return true if all elements have same sex
	*/

    public boolean sameSex() {
		return sameSex(getSymbol(0).getSex());
    }
	/** determine if all elements in product have the same sex or neutral sex
	* @return true if all elements have same sex
	*/

    public boolean consistentSex() {
        AlgebraSymbolVector asv = new AlgebraSymbolVector(2,1);
        for (reset();isNext();){
            AlgebraSymbol as = getNext();
            if (!as.getSex().equals ("N"))
                asv.addElement(as);
        }
        if (asv.size()!= 0)
		    return sameSex(asv.getSymbol(0).getSex());
		else return true;
    }

	/** compute reciprocal this product
	* @return reciprocal of the product
	*/
	//public AlgebraSymbolVector reciprocal() {
	public ListVector reciprocal() {
		AlgebraSymbolVector recip = new AlgebraSymbolVector(this.size());
		ListVector lv = new ListVector();
		if (size() == 1 && ((AlgebraSymbol)elementAt(0)).isZeroElement()){
		    lv.addElement(this);
			return lv;
		}
		for (int i=0;i<size()-1;i++){//H Fa --> null
		    AlgebraSymbol a = (AlgebraSymbol)elementAt(i);
			if (a.getSex().equals("N")) continue;
			if (!(a.getArrowType() == Bops.SPOUSE||
				a.getArrowType() == Bops.SPOUSER)) continue;
			if (a.getSex().equals(((AlgebraSymbol)elementAt(i+1)).getSex())) return null;
		}
		//if (sameSex()) {// same sex
		if (sameSex() && Algebra.getCurrent().getFocalElements().size() == 1){
			for(int i=0;i<size();i++) {
				AlgebraSymbol a = getSymbol(i).getReciprocal();
			    Debug.prout(1,"addReciprocal: rec="+a+"term="+getSymbol(i));
				if (a == null) return null;
				recip.addElement(a);
			}
			lv.addElement(recip);
		}
		else {//mixed sex
			if (getSymbol(0).isFocalElement()) {// form xyft
				for (int i=1;i<size();i++) {
					AlgebraSymbol a = getSymbol(i);
					AlgebraSymbol as = a.getReciprocal(getSymbol(i-1).getSexMatchingFocalTerm());
				//Debug.prout(0," ft "+getSymbol(i-1).getSexMatchingFocalTerm()+" a "+a+" as "+as);
					if (as == null) return null;
					recip.addElement(as);
				}
				if (getLast().getSexMatchingFocalTerm() == null) return null;
				if (getLast().getArrowType() == Bops.SPOUSE) {}//special case for terminal spouse element
				else recip.addElement(getLast().getSexMatchingFocalTerm());
				//AlgebraPath ap = new AlgebraPath(recip);
				//Debug.prout(0," BBBBBBBBBBBBBBBBBBB ap "+ap+" recip "+recip);
			//	recip = ap.getReducedProductPath();
				//Debug.prout(0," BBBBBBBBBBBBBBBBBBB recip " +recip);
				lv.addElement(recip);
				Debug.prout(1,"add mixed sex reciprocal: rec= "+recip+" term= "+this);
			} else {//XY --> XYi, XYI --> compute reciprocal
				AlgebraSymbolVector ft = Algebra.getCurrent().getFocalElements();
				if (ft.size() == 2) {
					for (int i = 0; i < ft.size(); i++){
						if (this.getFirst().getArrowType() == Bops.SPOUSE ||
							this.getFirst().getArrowType() == Bops.SPOUSER) {
							String sex = this.getFirst().getSex();
							if (!sex.equals("N") && sex.equals(((AlgebraSymbol)ft.elementAt(i)).getSex()))
								continue;
						}
						AlgebraSymbolVector asv = (AlgebraSymbolVector)this.clone();
						asv.addToBeginning((AlgebraSymbol)ft.elementAt(i));
						//Debug.prout(0," asv "+asv);
						ListVector lv1 = asv.reciprocal();
						//Debug.prout(0," asv "+asv +"lv1 "+lv1);
						for (lv1.reset();lv1.isNext();){
							lv.addElement((AlgebraSymbolVector)lv1.getNext());
						}
					}
					//Debug.prout(0,"NNNNNNNNNNNNNNNNNNNNNNNNNNN this "+ this +" asv "+asv+" rec "+rec+" i "+i+" ft "+ft);
				}else {}
			}
		}
		if (lv.size() == 0) return null;
		else return lv;
	}

	/** compute reciprocal this product, starting with theSex
	* theSex sex acted on by product
	* @return reciprocal of the product
	*/

	public ListVector reciprocal(String theSex) {
		ListVector lv = new ListVector();
		AlgebraSymbolVector recip = new AlgebraSymbolVector(this.size());
/*		boolean flag = false;
		for (int i = 0;i<size();i++) {
			flag =  (getSymbol(i).getSex().equals(theSex));
		}*/
		if (sameSex(theSex)) {// same sex
			for(int i=0;i<size();i++) {
				AlgebraSymbol a = getSymbol(i).getReciprocal();
			//Debug.prout(0,"i "+i+" this "+this+" a "+a);
				if (a == null) return null;
				recip.addElement(a);
			}
			lv.addElement(recip);
		}
		else {//mixed sex
			AlgebraSymbol a = null;
			AlgebraSymbol as = null;
			for (int i=0;i<size();i++) {
				a = getSymbol(i);
				if (i == 0) as = a.getReciprocal(theSex);
				else as = a.getReciprocal(getSymbol(i-1).getSex());
				//Debug.prout(0,"i "+i+" this "+this+" as "+as+ " theSex "+theSex);
				if (as == null) return null;
				recip.addElement(as);
			}
			if (Algebra.getCurrent().getFocalElements().size() == 2) {
				recip.addElement(getLast().getSexMatchingFocalTerm());
			}
			lv.addElement(recip);
		}
		//if (recip.size() == 0) return null;
		//else return recip;
		if (lv.size() == 0) return null;
		else return lv;
	}

	public StringVector toStringVector() {
		StringVector ret = new StringVector(this.size());
		for(int i = 0;i<size();i++) ret.addElement(((AlgebraSymbol)elementAt(i)).getValue());
		return ret;

	}

	public int getGeneration() {
	    int i = 0;
	    for (reset();isNext();){
	        AlgebraSymbol as = getNext();
	        if (as.getArrowType() == Bops.UP)
	            i++;
	        else if (as.getArrowType() == Bops.DOWN)
	            i--;
	    }
	    return i;
	}

	/** test if this and asv have the same arrow pattern
	* @asv test against this
	* @return boolean
	**/
    public boolean sameArrowPattern(AlgebraSymbolVector asv) {
        if (this.size() != asv.size()) return false;
        asv.reset();
        for (this.reset();this.isNext();){
            if (this.getNext().getArrowType() != asv.getNext().getArrowType())
                return false;
        }
        return true;
    }

	/** test if this has the same arrow for all algebra symbols
	* @return boolean
	**/
    public boolean sameArrow() {
		if (size() == 0) return false;
		//Debug.prout(0,"this "+this);
	    int as = ((AlgebraSymbol)elementAt(0)).getArrowType();
		for (int i = 0; i < size(); i++) {
	        if (as != ((AlgebraSymbol) elementAt(i)).getArrowType()) return false;
        }
        return true;
    }

    public boolean sameArrow(Hashtable ht) {
		AlgebraSymbol as = (AlgebraSymbol)elementAt(0);
		if ((Integer)ht.get(as.toString()) == null)
			ht.put(as.toString(),new Integer(as.getArrowType()));
//Debug.prout(0," as "+as + " hash "+((Integer)ht.get(as.toString())).intValue());
		int j = ((Integer)ht.get(as.toString())).intValue();
		for (int i = 0; i < size(); i++) {
			as = (AlgebraSymbol)elementAt(i);
			if (as.toString().equals("&")) continue;
		    if ((Integer)ht.get(as.toString()) == null)
			    ht.put(as.toString(),new Integer(as.getArrowType()));
	        if (j != ((Integer)ht.get(as.toString())).intValue()) return false;
        }
        return true;
    }

    /**
    * test to see if this has equivalent element form
    * @return boolean
    */
    public boolean equivalentProduct(){
		for(int i=0;i<size();i++){
			if (((AlgebraSymbol)elementAt(i)).getValue().equals("&")) return true;
		}
        return false;
    }


    /**
    * get right product of an equivalent product
    * @return right product
    */
    public AlgebraSymbolVector equivalentRightProduct(){
		AlgebraSymbolVector ret = new AlgebraSymbolVector();
		for (int i=size()-1;i>-1;i--){
		    AlgebraSymbol as = (AlgebraSymbol)elementAt(i);
			if (as.getValue().equals("&")) break;
			ret.addToEnd(as);
		}
        return ret;
    }

/*    public AlgebraSymbolVector equivalentRightProduct(){//incorrect procedure; keep for
	reference since it was being used previously
		AlgebraSymbolVector ret = new AlgebraSymbolVector();
		for (int i=0;i<size();i++){
		    AlgebraSymbol as = (AlgebraSymbol)elementAt(i);
			if (as.getValue().equals("&")) break;
			ret.addToEnd(as);
		}
        return ret;
    }*/

    /**
    * get left product of an equivalent product
    * @return left product
    */
    public AlgebraSymbolVector equivalentLeftProduct(){
		AlgebraSymbolVector ret = new AlgebraSymbolVector();
		for (int i = 0; i<size();i++){
		    AlgebraSymbol as = (AlgebraSymbol) elementAt(i);
			if (as.getValue().equals("&")) break;
			ret.addToBeginning(as);
		}
        return ret;
    }

	/**
	*  reduce algebra symbol vector
	*  @return reduced algebra symbol vector
	*/

	public AlgebraSymbolVector reduce() {
	   AlgebraSymbolVector ret = new AlgebraSymbolVector();
		AlgebraPath ap = new AlgebraPath();
		ap.reducePath(this);
		return ap.getReducedProductPath();
	}
//    /**
//    * test to see if two AlgebraSymbolVectors are equal
//	* @prod test against this
//    * @return boolean
//    */
/*    public boolean equals(AlgebraSymbolVector prod){
		if (prod.size() != size()) return false;
	    for (int i = 0; i < prod.size(); i++){
		    AlgebraSymbol as = (AlgebraSymbol) prod.elementAt(i);
			if (!as.equals(elementAt(i))) return false;
	    }
        return true;
    }*/

		/**
	 * change algebra elements to transliterated kin term form
	 * return transliteration
	 */
	public String makeTransliteration(){
		String txt = "";
		String sex = getLast().getSex();
		if (equivalentProduct() && !getFirst().getSex().equals(sex)) sex = "N";
		int[] pattern = getArrowPattern();//sp,pa,ch,sib,ft,sex,O/Y generator
		if (pattern[5] == 1) {
			if (sex.equals("M")) txt = "male sex generator";
			else if (sex.equals("F")) txt = "female sex generator";
			return txt;
		}
			//Debug.prout(0,"VVVVVVVVVVVV this "+this+" pattern "+pattern[0]+pattern[1]+pattern[2]+pattern[3]);
		if (pattern[0] == 1 && pattern[1] == 0 && pattern[2] ==0 && pattern[3] == 0 && pattern[4] == 0){//hu/wi
		    if (sex.equals("F")) txt = "'Wife'";
			else if (sex.equals("M")) txt = "'Husband'";
		    else txt = "'Spouse'";
			return txt;
		} else if (pattern[0] == 0 && ((pattern[1] != 0 && pattern[2] == 0) ||
		(pattern[1] == 0 && pattern[2] != 0)) && pattern[3] == 0 && pattern[4] == 0){//pa/ch
			int j = 0;
			if (pattern[1] != 0) j = 1;
			else j = 2;
		    switch (pattern[j]) {
				case 1:
					txt = "";
					break;
				case 2:
					txt = "Grand";
					break;
				case 3:
					txt = "Greatgrand";
					break;
				default:
					txt = "Great...grand";
					break;
		   }
		   int k = j;
		   if (pattern[0] == 1) k = (j % 2) + 1;
		   switch (k){
				case 1:
					if (sex.equals("F")) txt = txt+"Mother";
					else if (sex.equals("M")) txt = txt+"Father";
					else txt = txt+"Parent";
					if (pattern[0] == 1) txt = txt+ "-in-law";
					txt = "'"+txt+"'";
					return txt;
				case 2:
					if (sex.equals("F")) txt = txt+"Daughter";
					else if (sex.equals("M")) txt = txt+"Son";
					else txt = txt+"Child";
					if (pattern[0] == 1 && j == 1) txt = txt+ "-in-law";
					txt = "'"+txt+"'";
					return txt;
		   }
		} else if (pattern[0] == 0 && pattern[1] >= 2 && pattern[2] >= 2 && pattern[3] == 0) {//cousin
			txt = "'Cousin'";
			return txt;
		} else if ((pattern[1] == 1 && pattern[2] == 1 && pattern[3] ==0) ||
		(pattern[1] == 0 && pattern[2] == 0 && pattern[3] == 1)) {//sib
			if (sex.equals("F") )txt = "Sister";
			else if (sex.equals("M") )txt = "Brother";
			else txt = "Sibling";
			if (pattern[0] == 1) txt = txt+ "-in-law";
			//System.out.println(" pattern "+pattern[3]+" pattern "+pattern[1]);
			if (pattern[3] == 1 && pattern[6] == 0) txt = "C-"+txt;
			else if (pattern[3] == 1 && pattern[6] == 1) txt = "Older "+txt;
			else if (pattern[3] == 1 && pattern[6] == -1) txt = "Younger "+txt;
			txt = "'"+txt+"'";
			return txt;
		} else if ((pattern[1] >= 2 && pattern[2] == 1 && pattern[3] == 0)||
		(pattern[1] == 1 && pattern[2] >= 2 && pattern[3] == 0) ||
		(pattern[1] != 0 && pattern[2] == 0 && pattern[3] == 1) ||
		(pattern[1] != 0 && pattern[2] == 0 && pattern[3] == 0 && pattern[4] == 1) ||
		(pattern[1] == 0 && pattern[2] != 0 && pattern[3] == 1)){//uncle/nephew
			int j = 0;
			int k = 0;
			if (pattern[4] == 1) {
				if (pattern[1] > 0) j = 1;
				else j = 2;
				k = pattern[j]+1;
			}
			else if (pattern[3] == 0) {
				if (pattern[1] > 1) j = 1;
				else j = 2;
				k = pattern[j];
			} else {
				if (pattern[1] > 0) j = 1;
				else j = 2;
			    k = pattern[j] +1;
			}
			switch (k) {
				case 2:
					txt = "";
					break;
				case 3:
					txt = "Great";
					break;
				case 4:
					txt = "Greatgreat";
					break;
				default:
					txt = "Great...great";
					break;
			}
			switch (j){
				case 1:
					if (sex.equals("F")) txt = txt+"Aunt";
					else if (sex.equals("M")) txt = txt+"Uncle";
					else txt = txt+"Nuncle";
					if (pattern[3] == 1) txt = "C-"+txt;
					txt = "'"+txt+"'";
					return txt;
				case 2:
					if (pattern[0] == 0) {
						if (sex.equals("F")) txt = txt+"Niece";
						else if (sex.equals("M")) txt = txt+"Nephew";
						else txt = txt+"Nibling";
						if (pattern[3] == 1) txt = "C-"+txt;
					    txt = "'"+txt+"'";
					    return txt;
					}
			}
		}
		txt = "";
		//for (reset();isNext();){
			//AlgebraSymbol as = (AlgebraSymbol)getNext();
		for (int i = 0; i < size();i++){
			AlgebraSymbol as = (AlgebraSymbol) elementAt(i);
			if (as.toString().equals("&")) {
				txt = txt +" or ";
				continue;
			}
			sex = as.getSex();
			if (!txt.equals("") && !txt.endsWith(" or ")) txt = txt +" of ";
			switch (as.getArrowType()){
				case Bops.UP:
					if (sex.equals("M")) txt = txt + "'Father'";
					else if (sex.equals("F")) txt = txt + "'Mother'";
					else txt = txt + "'Parent'";
					break;
				case Bops.DOWN:
					if (sex.equals("M")) txt = txt + "'Son'";
					else if (sex.equals("F")) txt = txt + "'Daughter'";
					else txt = txt + "'Child'";
					break;
				case Bops.RIGHT:
					if (sex.equals("M")) txt = txt + "'Older Brother'";
					else if (sex.equals("F")) txt = txt + "'Older Sister'";
					break;
				case Bops.LEFT:
					if (sex.equals("M")) txt = txt + "'Younger Brother'";
					else if (sex.equals("F")) txt = txt + "'Younger Sister'";
					break;
				case Bops.SPOUSE:
					if (sex.equals("M")) txt = txt + "'Husband'";
					else if (sex.equals("F")) txt = txt + "'Wife'";
					else txt = txt + "'Spouse'";
					break;
				case Bops.SPOUSER:
					if (sex.equals("M")) txt = txt + "'Husband'";
					else if (sex.equals("F")) txt = txt + "'Wife'";
					else txt = txt + "'Spouse'";
					break;
				case Bops.IDENTITY:
					//if (size() ==1) {

						if (sex.equals("M")){
							txt = txt + "'Male Self'";
						}
						else if (sex.equals("F")) {

							txt = txt + "'Female Self'";
						}
						else txt = txt + "'Self'";
					//}
					break;
			}
		}
		if (txt == "'Sister' of 'Husband'") txt = txt + " = 'Wife' of 'Brother'";
		else if (txt == "'Wife' of 'Brother'") txt = txt + " = 'Sister' of 'Husband'";
		return txt;
	}

	int[] getArrowPattern(){
		int [] ret = {0,0,0,0,0,0,0};//sp,pa,ch,sib,ft,sex,O/Y gen
		boolean upFlag = false;
		boolean downFlag = false;
		boolean eqvFlag = false;
		int j = 0;
		//for (reset();isNext();){
		for (int i=0;i<size();i++){
			j++;
		    AlgebraSymbol as = (AlgebraSymbol) elementAt(i);
		    //AlgebraSymbol as = getNext();
			if (as.getValue().equals("&")) {
				eqvFlag = true;
				continue;
			}
			if (as.getArrowType() == Bops.UP) {
				if (upFlag) {
					ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0; ret[4] = 0;
					return ret;
				} else {
					if (eqvFlag && ret[1] == 0) {
						ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0; ret[4] = 0;
						return ret;
					} else if (!eqvFlag)
				        ret[1]++;
				    if (ret[2] > 0) downFlag = true;
				}
			} else if (as.getArrowType() == Bops.DOWN) {
				if (downFlag) {
					ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0; ret[4] = 0;
					return ret;
				} else {
					if (eqvFlag && ret[2] == 0) {
						ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0; ret[4] = 0;
						return ret;
					}else if (!eqvFlag)
				        ret[2]++;
				    if (ret[1] > 0) downFlag = true;
				}
			} else if (!as.getSex().equals("N") && as.getArrowType() == Bops.IDENTITY){
				if (ret[4] > 0) {
					ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0;ret[4] = 0;
					return ret;
				} else ret[4]++;
			} else if (as.getArrowType() == Bops.LEFT ||as.getArrowType() == Bops.RIGHT) {
				ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 1;ret[4] = 0;
				if (Algebra.getCurrent().getGenerators(Bops.RIGHT).size() > 0) {
					if (as.getArrowType() == Bops.LEFT) ret[6] = 1;
					else ret[6] = -1;
				}
				//return ret;
			} else if (as.getArrowType() == Bops.SPOUSE ||as.getArrowType() == Bops.SPOUSER) {
				if (j == 1 || j == size()) ret[0]++;
				else {
					ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0;ret[4] = 0;
					return ret;
				}
			} else if (size() == 1 && as.isSexGenerator()) ret[5] = 1;
		}
		return ret;
	}
	public AlgebraSymbolVector makeEquivalent(AlgebraSymbolVector asv){
		AlgebraSymbol as = Algebra.getCurrent().getElement("&");
	    this.addToBeginning(as);
		for (int i=0;i<asv.size();i++){
		    this.addToBeginning((AlgebraSymbol) asv.elementAt(i));
		}
		return this;
	}

	public boolean hasFocalElement(){
		AlgebraSymbolVector ft = Algebra.getCurrent().getFocalElements();
		for (ft.reset();ft.isNext();){
			if (locateSymbol(ft.getNext()) > -1) return true;
		}
		return false;
	}

	public boolean isZeroVector() {
	    return  (size() == 1 && ((AlgebraSymbol)elementAt(0)).getValue().equals("0"));
	}
}

