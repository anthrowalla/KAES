

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class KinTermLabel {

    public KinTermLabel() {
    }
	String label = "";

	public void setKinTermLabel(String text){
	    label = text;
	}

	public String getKinTermLabel(){
	    return label;
	}

	/**
	 * change algebra elements to transliterated kin term form
	 * return transliteration
	 */
	public String makeTransliteration(AlgebraSymbolVector asv){
		System.out.println(" asv "+asv);
		String txt = "";
		String sex = asv.getLast().getSex();
		if (asv.equivalentProduct() && !asv.getFirst().getSex().equals(sex)) sex = "N";
		int[] pattern = getArrowPattern(asv);//sp,pa,ch,sib,ft
		//System.out.println("VVVVVVVVVVVV this "+this+" pattern "+pattern[0]+pattern[1]+pattern[2]+pattern[3]);
		if (pattern[0] == 1 && pattern[1] == 0 && pattern[2] ==0 && pattern[3] == 0 && pattern[4] == 0){//hu/wi
		    if (sex.equals("F")) txt = "'Wife'";
			else if (sex.equals("M")) txt = "'Husband'";
		    else txt = "'Spouse'";
			return txt;
		} else if (pattern[0] == 0 && ((pattern[1] != 0 && pattern[2] == 0) ||
		(pattern[1] == 0 && pattern[2] != 0)) && pattern[3] == 0){//pa/ch
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
			txt = "'"+txt+"'";
			return txt;
		} else if ((pattern[1] >= 2 && pattern[2] == 1 && pattern[3] == 0)||
		(pattern[1] == 1 && pattern[2] >= 2 && pattern[3] == 0) ||
		(pattern[1] != 0 && pattern[2] == 0 && pattern[3] == 1) ||
		(pattern[1] != 0 && pattern[2] == 0 && pattern[3] == 0 && pattern[4] == 1) ||
		(pattern[1] == 0 && pattern[2] != 0 && pattern[3] == 1)){//uncle/nephew
			int j = 0;
			int k = 0;
			if (pattern[3] == 0) {
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
					txt = "'"+txt+"'";
					return txt;
				case 2:
					if (pattern[0] == 0) {
						if (sex.equals("F")) txt = txt+"Niece";
						else if (sex.equals("M")) txt = txt+"Nephew";
						else txt = txt+"Nibling";
					    txt = "'"+txt+"'";
					    return txt;
					}
			}
		}
		txt = "";
		//for (reset();isNext();){
			//AlgebraSymbol as = (AlgebraSymbol)getNext();
		for (int i = 0; i < asv.size();i++){
			AlgebraSymbol as = (AlgebraSymbol) asv.elementAt(i);
			if (as.toString().equals("&")) {
				txt = txt +" or ";
				continue;
			}
			sex = as.getSex();
			Debug.prout(0," TTTTTTTTTTTTT txt #"+txt+"#");
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
				case Bops.LEFT:
					if (sex.equals("M")) txt = txt + "'Older Brother'";
					else if (sex.equals("F")) txt = txt + "'Older Sister'";
					break;
				case Bops.RIGHT:
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
						if (sex.equals("M")) txt = txt + "'Brother' of female speaker";
						else if (sex.equals("F")) txt = txt + "'Sister' of male speaker";
						else txt = txt + "'Self'";
					//}
					break;
			}
		}
		if (txt == "'Sister' of 'Husband'") txt = txt + " = 'Wife' of 'Brother'";
		else if (txt == "'Wife' of 'Brother'") txt = txt + " = 'Sister' of 'Husband'";
		return txt;
	}

	int[] getArrowPattern(AlgebraSymbolVector asv){
		int [] ret = {0,0,0,0,0};//sp,pa,ch,sib,ft
		boolean upFlag = false;
		boolean downFlag = false;
		boolean eqvFlag = false;
		int j = 0;
		//for (reset();isNext();){
		for (int i=0;i<asv.size();i++){
			j++;
		    AlgebraSymbol as = (AlgebraSymbol) asv.elementAt(i);
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
				ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0;ret[4] = 0;
				return ret;
			} else if (as.getArrowType() == Bops.SPOUSE ||as.getArrowType() == Bops.SPOUSER) {
				if (j == 1 || j == asv.size()) ret[0]++;
				else {
					ret[0] = 0;ret[1] = 0; ret[2] = 0; ret[3] = 0;ret[4] = 0;
					return ret;
				}
			}
		}
		return ret;
	}

	public String getTransliteration(AlgebraPath ap){
		String text = ""; 
		String sex = "";
		AlgebraSymbolVector asv = null; //ap.getReducedProductPath();
		//if (ap.isEquivalentPath()) { dwr 8/5
		if (ap.isReducedEquivalentPath()) {//dwr 8/5
			//AlgebraSymbolVector asL = ap.getEquivalentPathLeft().getReducedProductPath();
			//AlgebraSymbolVector asR = ap.getEquivalentPathRight().getReducedProductPath();
			AlgebraSymbolVector asL = ap.getReducedEquivalentPathLeft().getReducedProductPath();
			AlgebraSymbolVector asR = ap.getReducedEquivalentPathRight().getReducedProductPath();
			//System.out.println(" equilvalent path "+ap+" L "+asL+" R "+asR+" "+asL.sameArrow()+" "+asR.sameArrow());
			if (asL.sameArrow() && asR.sameArrow() && asL.getFirst().getArrowType() ==
			asR.getFirst().getArrowType() && asL.size() == asR.size())
				asv = ap.getReducedProductPath();//asL;
		} else if (ap.getReducedProductPath().sameArrow())
			asv = ap.getReducedProductPath();
			//if (true) return " ";
		//if (asv != null) return asv.makeTransliteration();
		//else return ap.getReducedProductPath().makeTransliteration();
		//if (asv != null) text = ((AlgebraSymbolVector)asv.clone(true)).makeTransliteration();
		//else text = ((AlgebraSymbolVector)ap.getReducedProductPath().clone(true)).makeTransliteration();
		if (asv != null) text = makeTransliteration((AlgebraSymbolVector)asv.clone(true));
		else text = makeTransliteration((AlgebraSymbolVector)ap.getReducedProductPath().clone(true));
		return text;
	}

}