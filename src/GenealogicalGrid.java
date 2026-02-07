import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
import java.awt.FontMetrics;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GenealogicalGrid extends GenealogicalModel3D{
	
	public GenealogicalGrid() {
	}
	String egoSex = "N";
	
    String ego = "ego",egoM = "ego-M", egoF = "ego-F", txtC = ", ";
    int downEnd = 3; int upEnd = 4;
    ThreeD md = null;
    float cht = (float)0.25;float pht = (float) 1.5;
    float eqwid = (float)0.16;//0.08;//width of equal sign 0.08
	float eqw = (float)0.7*eqwid;//smaller equal sign but keep spacing based on eqwid
	float eqspace = (float) 0.05;//distance to equal sign 0.05
	float eqht = (float) 0.05;//spacing between 2 lines of equal sign
	float upWidth = 8;//7;//6;
	float scalar = (float) 1.5;//1.5;1.6;//1.8;//2.3;
	float factor = (float) 2.0;
	float downWidth1 = (float)2.0;//widths for 3 sets of descendants
	float downWidth2 = (float)1.2;//0.9;
	float downWidth3 = (float)0.6;//0.5;//0.7;//0.5;//0.3;
	float theWidth = (float) 0.0;//widths for descending genealogy
	int sib = 0,parent = 1,child = 2,spouse = 3;
	KinTypeMapper ktm = new KinTypeMapper();
	ListVector labelV = new ListVector();
	StringVector sexV = new StringVector();
	int iLet = 0;
	String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	Hashtable kinTermsToSymbols = null;//new Hashtable();
	ListHashtable kinT2AlgE = null;//new ListHashtable();
	boolean termFlag = true;
	boolean leftSide = false;
	boolean transFlag = false;
	
	public KinTypeMapper getKinTypeMapper() {
		return ktm;
	}
	
	public void setTermFlag(boolean flag) {
		termFlag = flag;
	}
	public void setTransFlag(boolean flag) {
		transFlag = flag;
	}
	
	public void setEgoSex(String sex){
		egoSex = sex;
	}
	
	public String getEgoSex(){
		return egoSex;
	}
	
	float offset() {
		if (leftSide) return 4*eqwid;
		else return -4*eqwid;
	}
	
	void setKinTypeMapper(KinTypeMapper k){
		ktm = k;
	}
	void setThreeD(ThreeD amd) {
		this.md = amd;
	}
	
	void drawEgo(float x, float y, float z,String kType){
		if (kType.equals(ego)) {
			String label = kinTermLabel(kType);
			if (label.indexOf(",") != -1){
				drawTriangle(x+offset()/2,y,1,egoM,0);
				drawCircle(x,y,z,egoF,-radius);
			}else{
				drawTriangle(x+offset()/2,y,1,ego,0);
				drawCircle(x,y,z,ego,-radius);
			}
		} else if (kType.equals(egoM)) drawTriangle(x,y,1,egoM,0);
		else drawCircle(x,y,z,egoF,-radius);
	}
	
	String drawDownTriangle(float x, float y, float z, int iType, String kType){
		String ret = "";
		if (iType == spouse)  ret = kType+"h";
		else ret = kType+"s";
		drawTriangle(x,y,z,ret,0);
		return ret;
	}
	
	String drawUpTriangle(float x, float y, float z, int iType, String kType){
		String ret = "";
		if (kType.equals(ego)||kType.equals(egoM)||(kType.equals(egoF))){
			drawEgo(x,y,z,kType);
			ret = kType + txtC;
		}
		else {
			if (iType == sib) ret = kType+"b";
			else if (iType == parent) ret = kType+"f";
			else ret = kType + "h";
			drawTriangle(x,y,z,ret,0);
		}
		return ret;
	}
	
	String drawDownCircle(float x, float y, float z, int iType, String kType){
		String ret = "";
		if (iType == spouse) ret = kType+"w";
		else ret = kType+"d";
		drawCircle(x,y,z,ret,radius);
		return ret;
	}
	
	String drawUpCircle(float x, float y, float z, int iType, String kType){
		String ret = kType;
		if (kType.equals(ego)||kType.equals(egoM)||(kType.equals(egoF))){
			drawEgo(x,y,z,kType);
			ret = kType + txtC;
		}
		else {
			if (iType == sib) ret = kType+"z";
			else if (iType == parent) ret = kType+"m";
			else ret = kType + "w";
			drawCircle(x,y,z,ret,radius);
		}
		return ret;
	}
	
	void connectPairPoints(float x1,float y1, float z1, float x2,
						   float y2,float z2){
		md.connectPoint(md.setPoint(x1,y1,z1),md.setPoint(x2,y2,z2,Color.red));
	}
	
	void connectPairPoints(float x1,float y1, float z1, float x2,
						   float y2,float z2, Color c){
		md.connectPoint(md.setPoint(x1,y1,z1),md.setPoint(x2,y2,z2),c);
	}
	
	boolean leftAdjust = false;
	String graphUpSegment(float x, float y, float z,float width, boolean flag,int end,
						  String kType){
		float ht = 0;
		String ret = kType;
		if (flag) {
			if (end == 1) {
				int end3 = 0;
				if (kType.equals(egoF)){
					ret = drawUpCircle(x,y+cht,z,sib,kType);//center female,
					drawEgoSpouse(x,y+cht,z,spouse,ret);
				}
				else {
					ret = drawUpTriangle(x,y+cht,z,sib,kType);//center male,
					drawEgoSpouse(x,y+cht,z,spouse,ret);
				}
				doDownGraphSegment(x,y,z,theWidth,end3,true,ret);
				leftAdjust = !leftSide;
				String kTypeS = drawUpTriangle(x-width,y+cht,z,sib,ret);//left male
				leftAdjust = leftSide;
				drawUpCircle(x-width-offset(),y+cht,z,spouse,kTypeS);//female spouse
				int end2 = 0;
				doDownGraphSegment(x-width,y,z,theWidth,end2,false,kTypeS);
				leftAdjust = !leftSide;
				kTypeS = drawUpCircle(x+width,y+cht,z,sib,ret);//right female
				leftAdjust = leftSide;
				drawUpTriangle(x+width-offset(),y+cht,z,spouse,kTypeS);//male spouse
				int end1 = 0;
				doDownGraphSegment(x+width,y,z,theWidth,end1,false,kTypeS);
			}
			else {
				leftAdjust = true;
				ret = drawUpTriangle(x+width,y+cht,z,parent,kType);//right male
				leftAdjust = !leftSide;
				String kTypeS = drawUpTriangle(x-width,y+cht,z,sib,ret);//left male
				leftAdjust = leftSide;
				drawUpCircle(x-width-offset(),y+cht,z,spouse,kTypeS);//female spouse
				int end1 = 0;
				doDownGraphSegment(x-width,y,z,theWidth,end1,false,kTypeS);
				leftAdjust = !leftSide;
				kTypeS = drawUpCircle(x,y+cht,z,sib,ret);//center female
				leftAdjust = leftSide;
				drawUpTriangle(x-offset(),y+cht,z,spouse,kTypeS);//male spouse
				int end2 = 0;
				doDownGraphSegment(x,y,z,theWidth,end2,true,kTypeS);
			}
		}
		else {
			ret = drawUpCircle(x-width,y+cht,z,parent,kType);//left female
			leftAdjust = !leftSide;
			String kTypeS = drawUpTriangle(x,y+cht,z,sib,ret);//center male
			leftAdjust = leftSide;
			drawUpCircle(x-offset(),y+cht,z,spouse,kTypeS);//female spouse
			int end2 = 0;
			doDownGraphSegment(x,y,z,theWidth,end2,true,kTypeS);
			leftAdjust = !leftSide;
			kTypeS = drawUpCircle(x+width,y+cht,z,sib,ret);//right female
			leftAdjust = leftSide;
			drawUpTriangle(x+width-offset(),y+cht,z,spouse,kTypeS);//male spouse
			int end1 = 0;
			doDownGraphSegment(x+width,y,z,theWidth,end1,false,kTypeS);//true
		}
		connectPairPoints(x-width,y+cht,z,x-width,y,z);//child
		connectPairPoints(x-width,y,z,x+width,y,z);//siblilng
		connectPairPoints(x,y,z,x,y+cht,z);//child
		connectPairPoints(x+width,y,z,x+width,y+cht,z);//child
		if (end + 1 == upEnd) ht = 3*cht/2;
		else ht = pht;
		connectPairPoints(x,y,z,x,y-ht,z);//parent
		connectPairPoints(x-eqw,y-ht-eqspace,z,x+eqw,y-ht-eqspace,z);//equal sign
		connectPairPoints(x-eqw,y-ht-eqspace-eqht,z,x+eqw,y-ht-eqspace-eqht,z);
		return ret;
	}
	
	void drawEgoSpouse(float x, float y, float z,int spouse,String kType){
		if (kType.equals(ego+txtC)) {
			String s = drawDownTriangle(x-3*offset()/2,y,z,spouse,kType);
			drawDownCircle(x-offset(),y,z,spouse,kType);
		} else if (kType.equals(egoM+txtC)) drawDownCircle(x-offset(),y,z,spouse,kType);
		else drawDownTriangle(x-offset(),y,z,spouse,kType);
	}
	
	void graphDownSegment(float x, float y, float z,float width,int end, boolean flag,
						  String kType){
		float eq = (float) 1.0;//factor for scaling equal sign width
		y = y + (float)2.5*cht;
		if (end == 1) leftAdjust = !leftSide;
		else leftAdjust = true;
		String ret = drawDownTriangle(x-width,y+cht,z,child,kType);
		if (end == 1) leftAdjust = !leftSide;
		String ret1 = drawDownCircle(x+width,y+cht,z,child,kType);
		if (end == 1) { //draw spouses
			leftAdjust = leftSide;
			drawDownTriangle(x+width-offset(),y+cht,z,spouse,ret1);//kType);
			leftAdjust = leftSide;
			drawDownCircle(x-width-offset(),y+cht,z,spouse,ret);//kType);
		}
		connectPairPoints(x-width,y+cht,z,x-width,y,z);//child - male
		connectPairPoints(x-width,y,z,x+width,y,z);//siblilng
		connectPairPoints(x+width,y,z,x+width,y+cht,z);//child - female
		connectPairPoints(x,y,z,x,y-cht,z);//parent
		if ((theWidth == downWidth3) && (end == 2)) eq = (float)0.5;//small equal sign
			float ht = 0;
			if (end == 1) ht = eqspace+eqht;
			connectPairPoints(x-eq*eqw,y-cht-eqspace-ht,z,x+eq*eqw,y-cht-eqspace-ht,z);//equal sign
			connectPairPoints(x-eq*eqw,y-cht-eqspace-eqht,z,x+eq*eqw,y-cht-eqspace-eqht,z);
		}
		
	Color getLabelColor(String label){
		StringVector lv = new StringVector();
		int n = label.indexOf(",");
		while (n != -1){
			String s = label.substring(0,n-1);
			lv.addElement(s);
			label = label.substring(n+1,label.length());
			n = label.indexOf(",");
		}
		lv.addElement(label);
		boolean flag = true;
		String sex = "";
		for (lv.reset();lv.isNext();){
			String s = lv.getNext();
			for (int i=0;i<labelV.size();i++){
				if (((String)labelV.elementAt(i)).substring(0,1).equals(s)){
					if (sex.equals("")) sex = (String)sexV.elementAt(i);
					else flag = (sex.equals((String)sexV.elementAt(i)));
					if (!flag) break;
				}
			}
			if (!flag) break;
		}
		Color c = Color.gray;
		if (flag) {
			if (sex.equals("M")) c = Color.blue;
			else if (sex.equals("F")) c = Color.red;
			else c = Color.black;
		}
		return c;
	}
	
	void drawTriangle(float x, float y, float z,String kType,int radius){
		Color c = Color.gray;
		String label = kinTermLabel(kType);
		x = x+upWidth/400;
		float x1 = x-upWidth/80; float x2 = x+upWidth/80; float y1 = y+upWidth/80;//x--120; y--60
			if (!label.equals(" ")) c = Color.black;
			connectPairPoints(x,y,z,x1,y1,z,c);
			connectPairPoints(x2,y1,z,x,y,z,c);
			connectPairPoints(x1,y1,z,x2,y1,z,c);
			if (leftAdjust && label.indexOf("%") != -1){
				String temp = ktm.removeString(label,"%M");
				temp = ktm.removeString(temp,"%F");
				temp = ktm.removeString(label,"%N");
				md.setPoint(x-(float)md.getGraphics().getFontMetrics().stringWidth(temp)/100,y,z,label,getLabelColor(label),0);
			}
			else md.setPoint(x,y,z,label,getLabelColor(label),0);
			leftAdjust = false;
	}
	
	void drawCircle(float x, float y, float z,String kType,int radius){
		String txt = "";
		if (radius < 0) {
			radius = - radius;//negative to flag
			txt = "#";//flag for filling oval in draw procedure
		}
		String label = kinTermLabel(kType);
		if (leftAdjust && label.indexOf("%") != -1){
			String temp = ktm.removeString(label,"%M");
			temp = ktm.removeString(temp,"%F");
			temp = ktm.removeString(label,"%N");
			String tmp = "";
			for (int i=0;i<temp.length();i++) tmp = tmp+" ";
			md.setPoint(x,y,z,txt+" ",getLabelColor(label),radius);
			md.setPoint(x-(float)md.getGraphics().getFontMetrics().stringWidth(temp)/100,y,z,txt+label,getLabelColor(label),0);
		}
		else md.setPoint(x,y,z,txt+label,getLabelColor(label),radius);
		leftAdjust = false;
	}
	
	void doUpGraphSegment(float x,float y,float z, float width, int end,boolean flag,
						  String kType){
		end++;
		if (end == upEnd) {
			if (flag) {
				x = x+width;
				y = y + pht+cht-3*cht/2;
				drawUpTriangle(x,y,z,parent,kType);
				x = x + 2*factor*eqwid;
				drawUpCircle(x,y,z,parent,kType);
			}
			return;
		}
		if (end == 1) theWidth = downWidth1;
		else if (end == 2) theWidth = downWidth2;
		else theWidth = downWidth3;
		kType = graphUpSegment(x,y,z,width,flag,end,kType);
		String kType1 = kType;
		String kType2 = kType;
		String kType3 = kType;
		y = y - pht-eqspace-eqht-cht;
		if (end == 2) width = (float) 0.65*width;//make + generaton bar less wide
		width = width/scalar;
		if (end == 1) leftSide = true;//initialize value
		doUpGraphSegment(x-factor*eqwid-width,y,z,width,end,true,kType2);
		if (end == 1) leftSide = false;
		doUpGraphSegment(x+factor*eqwid+width,y,z,width,end,false,kType3);
	}
	
	void doDownGraphSegment(float x,float y,float z, float width, int end, boolean flag,
							String kType){
		end++;
		if (end == downEnd) return;
		x = x - offset()/2;
		if (end == 2) width = width*(float)0.7;//size of gen -3 sibling bar
		graphDownSegment(x,y,z,width,end,flag,kType);
		y = y + cht+eqspace+eqht+cht;
		float width1 = width;//use old value to reset x
		width = width/scalar;
		String kType1 = kType+"s";
		doDownGraphSegment(x-width1,y,z,width,end,false,kType1);//male
		String kType2 = kType+"d";
		doDownGraphSegment(x+width1,y,z,width,end,true,kType2);//female
	}
	
	boolean usedSymbol(String kinTerm) {
		return (!( kinTermsToSymbols.get(kinTerm) == null));
	}
	
	String formatTerm(String kType){
		String ret = "";
		int n = kType.indexOf(" ");
		if (n == -1) ret = "["+kType+"]";
		else {
			n++;
			ret = "["+kType.substring(0,n);
			for(int i = n;i <= kType.length()-1;i++){
				if (i < kType.length()-1) ret = ret+kType.substring(i,i+1)+", ";
				else ret = ret+kType.substring(i)+"]";
			}
		}
		return ret;
	}
	
	String makeLabel(ListVector lblV, String kType) {//make label for printing box of labels below grid
		String ret = "", let = "";
		if (lblV.size() == 0) return " ";
		int code = ktm.ktm.theKinshipAlgebra.makeAlg.getAlgebraClass();
		for (int i=0;i<lblV.size();i++){
			String label = "";
			AlgebraPath ap = (AlgebraPath)lblV.elementAt(i);
			String sex = ap.getPathSex();
			String st = ap.toString();
			if (st.equals("0")) {
				if (ret.endsWith(",")) {
					ret = ret.substring(0,ret.indexOf(","));
				}
				continue;
			}
			if (termFlag || transFlag) {
				if (termFlag) {
					label = ktm.alg2KinTerm(st);
					if (label == null || label.equals("")) label = getTransliteration(ap);
				} else if (transFlag) label = getTransliteration(ap);
			} else label = st;//use terms or alg elements
				switch (code) {
					case MakeAlgebra.DESCRIPTIVE: break;
					case MakeAlgebra.CLASSIFICATORY:
						if (label.endsWith(" of 'Sister'") ||label.endsWith(" of 'Brother'")) 
							label = _sisterBrotherLabel(label,kType);
						label = ktm.replaceTheString(label,"'Female Self' of 'Male Self'","'Sister' of male ego");
						label = ktm.replaceTheString(label,"'Male Self' of 'Female Self'","'Brother' of female ego");
						label = ktm.replaceTheString(label,"'Son' of 'Female Self'","'Nephew'");
						label = ktm.replaceTheString(label,"'Daughter' of 'Male Self'","'Niece'");
						if (label.equals("'Male Self'") || label.equals("'Female Self'")){
							if (kType.indexOf(",") != -1) {
								label = ktm.replaceTheString(label,"'Male Self'","'Brother'");
								label = ktm.replaceTheString(label,"'Female Self'","'Sister'");	
							}else{
								label = ktm.replaceTheString(label,"'Male Self'","male ego");
								label = ktm.replaceTheString(label,"'Female Self'","female ego");	
							}
						}
							label = ktm.replaceTheString(label,"'Daughter' of 'Sister'","'Niece'");
						label = ktm.replaceTheString(label,"'Daughter' of 'Brother'","'Niece'");
						label = ktm.replaceTheString(label,"'Son' of 'Brother'","'Nephew'");
						label = ktm.replaceTheString(label,"'Son' of 'Sister'","'Nephew'");
						break;
				}
				boolean flag = false;
				for (int j=0;j<labelV.size();j++){
					String s = (String) labelV.elementAt(j);
					if (!flag) flag = s.endsWith(" = "+label);
					if (flag) break;
				}
				if (!flag) {
					let = letters.substring(iLet,++iLet);
					if (labelV.addUnique(let+" = "+label))
						sexV.addElement(sex);
					if (!usedSymbol(label))
						kinTermsToSymbols.put(label,let);
				}
				let = (String)kinTermsToSymbols.get(label);
				if (lblV.size() == 1) ret = ret + let;
			else ret = ret+"%"+sex+let;
			if (i < lblV.size()-1) ret = ret + ",";
		}
		return ret;//has info on multiple terms to same kin type in grid
	}
	
	String _sisterBrotherLabel(String label,String kType){
		int j = label.indexOf(" or ");
		if ( j > -1) {
			String label1 = label.substring(0,j);
			String label2 = label.substring(j+4,label.length());
			if (kType.startsWith(egoM)) {
				if (label1.endsWith(" of 'Brother'"))
					label1 = label1.substring(0,label1.indexOf(" of 'Brother'"));
				else if (label1.endsWith(" of 'Sister'")){
					label1 = label1.substring(0,label1.indexOf(" of 'Sister'")) + " of female";
				}
				if (label2.endsWith(" of 'Brother'"))
					label2 = label2.substring(0,label2.indexOf(" of 'Brother'"));
				else if (label2.endsWith(" of 'Sister'")){
					label2 = label2.substring(0,label2.indexOf(" of 'Sister'")) + " of female";
				}								}
			else if (kType.startsWith(egoF)) {
				if (label1.endsWith(" of 'Sister'"))
					label1 = label1.substring(0,label1.indexOf(" of 'Sister'"));
				else if (label1.endsWith(" of 'Brother'")){
					label1 = label1.substring(0,label1.indexOf(" of 'Brother'")) + " of male";
				}
				if (label2.endsWith(" of 'Sister'"))
					label2 = label2.substring(0,label2.indexOf(" of 'Sister'"));
				else if (label2.endsWith(" of 'Brother'")){
					label2 = label2.substring(0,label2.indexOf(" of 'Brother'")) + " of male";
				}								}
			label = label1 +" or "+ label2;
		} else {
			if (kType.startsWith(egoM)) {
				if (label.endsWith(" of 'Brother'"))
					label = label.substring(0,label.indexOf(" of 'Brother'"));
				else if (label.endsWith(" of 'Sister'"))
					label = label.substring(0,label.indexOf(" of 'Sister'"))+" of female";
			}
			else if (kType.startsWith(egoF))	{
				if (label.endsWith(" of 'Sister'"))
					label = label.substring(0,label.indexOf(" of 'Sister'"));
				else if (label.endsWith(" of 'Brother'"))
					label = label.substring(0,label.indexOf(" of 'Brother'"))+" of male";
			}
		}
		return label;
	}
	
	String getTransliteration(AlgebraPath ap){
		String text = "";
		String sex = "";
		AlgebraSymbolVector asv = null; 
		if (ap.isReducedEquivalentPath()) {
			AlgebraSymbolVector asL = ap.getReducedEquivalentPathLeft().getProductPath();
			AlgebraSymbolVector asR = ap.getReducedEquivalentPathRight().getProductPath();
			if (asL.sameArrow() && asR.sameArrow() && asL.getFirst().getArrowType() ==
				asR.getFirst().getArrowType() && asL.size() == asR.size())
				asv = ap.getReducedProductPath();//asL;
		} else if (ap.getReducedProductPath().sameArrow())
			asv = ap.getReducedProductPath();
		if (asv != null) text = ((AlgebraSymbolVector)asv.clone(true)).makeTransliteration();
		else text = ((AlgebraSymbolVector)ap.getReducedProductPath().clone(true)).makeTransliteration();
		return text;
	}
	
	int procedureCode(){
		if (Algebra.getCurrent().getFocalElements().size() == 1) {
			if (Algebra.getCurrent().getGenerators(Bops.UP).size() == 2)
				return 3;//ship only
			else if (((AlgebraSymbol)Algebra.getCurrent().getFocalElements().elementAt(0)).getSex().equals("N"))
				return 1;//akt
			else
				return 2;//trob with single sex
		}
		else if (Algebra.getCurrent().getFocalElements().size() == 2)
			return 2;//trob
		else return 0;
	}
	
	String kinTermLabel(String kType) {
		ListVector lblV = new ListVector();
		if ((ListVector)kinT2AlgE.getList(formatTerm(kType)) == null) {
			String st = kType;
			if (kType.startsWith(egoM) || kType.startsWith(egoF)){
				if (kType.startsWith(egoM)) st = ktm.replaceTheString(st,egoM,ego);
				else  st = ktm.replaceTheString(st,egoF,ego);
				lblV =  (ListVector)kinT2AlgE.getList(formatTerm(st));
			} 
			else {
				st = ktm.replaceTheString(st,ego,egoF);
				ListVector lblVF =  (ListVector)kinT2AlgE.getList(formatTerm(st));
				st = kType;
				st = ktm.replaceTheString(st,ego,egoM);
				ListVector lblVM =  (ListVector)kinT2AlgE.getList(formatTerm(st));
				if (lblVF != null)
					for (lblVF.reset();lblVF.isNext();) 
						lblV.addElement(lblVF.getNext());
				if (lblVM != null) 
					for (lblVM.reset();lblVM.isNext();){
						AlgebraPath ap = (AlgebraPath)lblVM.getNext();
						if (lblV.indexOf(ap) == -1) lblV.addElement(ap);
					} 						
			}
				if (lblV == null || lblV.size() == 0) return " ";
		} else lblV = (ListVector)kinT2AlgE.getList(formatTerm(kType));
			ListVector lv = new ListVector();
			AlgebraPath ap1 = (AlgebraPath) lblV.elementAt(0);
			if (ap1.getReducedProductPath().size() == 0) return "!!";
			int codeClass = ktm.ktm.theKinshipAlgebra.makeAlg.getAlgebraClass();
			int codeType = ktm.ktm.theKinshipAlgebra.makeAlg.getAlgebraType();
			if (lblV.size() >= 2) {
				for (int i=0;i<lblV.size();i++){
					AlgebraPath ap = (AlgebraPath) lblV.elementAt(i);
					String sex = ap.getPathFirstSex();
					switch (codeClass){
						case MakeAlgebra.DESCRIPTIVE:
							switch(codeType){
								case MakeAlgebra.AKT:
									if (egoSex.equals("N") || sex.equals("N") || sex.equals(egoSex)) {
										lv.addElement(ap);
									}
									break;
								case MakeAlgebra.SHIPIBO:
									if (egoSex.equals("N") || sex.equals("N") || sex.equals(egoSex)) {
										lv.addElement(ap);
									}
									break;
							}
							break;
						case MakeAlgebra.CLASSIFICATORY:
							if (ap.getReducedProductPath().size() == 1 &&
								ap.getReducedProductPath().getFirst().isFocalElement()) {
								if ((kType.equals(egoM) || kType.equals(egoF) || kType.equals(ego)) && sex.equals(egoSex))
									continue;//CHECK THIS may need to change, depending on classificatory terminology
								if (!sex.equals(egoSex)) lv.addElement(ap);//I or i
							} else if (egoSex.equals("N") || sex.equals("N") || sex.equals(egoSex)) {
								lv.addElement(ap);
							}
							break;
					}
				}
			} else {
				switch (codeClass) {
					case MakeAlgebra.DESCRIPTIVE:
						switch (codeType){//NEED to check how codeType is computed
							case MakeAlgebra.AKT:
								lv.addElement(lblV.elementAt(0));
								break;
							case MakeAlgebra.SHIPIBO://added 7-22 dwr
								System.out.println("In shipibo ");
								lv.addElement(lblV.elementAt(0));
								break;
						}
						break;
					case MakeAlgebra.CLASSIFICATORY:
						AlgebraPath ap = (AlgebraPath) lblV.elementAt(0);
						String sex = ap.getPathFirstSex();
						if (ap.getReducedProductPath().size() == 1 &&
							ap.getReducedProductPath().getFirst().isFocalElement() &&
							!kType.equals(egoM) && !kType.equals(egoF) &&
							!kType.equals(ego) && sex.equals(egoSex)) {
							AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
							for (int i=0;i<gens.size();i++){
								AlgebraSymbol gen = (AlgebraSymbol)gens.elementAt(i);
								if (gen.getSex().equals(egoSex) &&
									(gen.getArrowType() == Bops.RIGHT ||
									 gen.getArrowType() == Bops.LEFT)) {
									lv.addElement(new AlgebraPath(gen));
								}
							}
						} else if (ap.getReducedProductPath().size() == 1 &&
								   ap.getReducedProductPath().getFirst().isFocalElement() &&
								   kType.equals(ego) && sex.equals(egoSex)) {
							if (Algebra.getCurrent().getFocalElements().size() == 1)
								lv.addElement(lblV.elementAt(0));//CHECK THIS added 3/2005 may not be correct in all cases
						} else lv.addElement(lblV.elementAt(0));
						break;
				}
			}
			return makeLabel(lv,kType);
	}
		
		
		void postLabels(){
			float sht = (float)0.25;
			float x =( -7*upWidth/4)+1, y = 8*sht, z = 0;
			float x1 = -7*upWidth/4,y1 = 8*sht,x2= 7*upWidth/4,y2 = 15*sht,delx = x2-x1;;
			int i = 0;
			connectPairPoints(x1,y1,z,x2,y1,z);
			connectPairPoints(x2,y1,z,x2,y2,z);
			connectPairPoints(x2,y2,z,x1,y2,z);
			connectPairPoints(x1,y2,z,x1,y1,z);
			int maxFM = 0;float ncol = delx/((labelV.size()/5) +1);
			for (int j=0;j<labelV.size();j++){
				String s = (String)labelV.elementAt(j);
				if (((String)sexV.elementAt(j)).equals("M"))
					md.setPoint(x,y,z,s,Color.blue,0);
				else if (((String)sexV.elementAt(j)).equals("F"))
					md.setPoint(x,y,z,s,Color.red,0);
				else md.setPoint(x,y,z,s,0);
				y = y + sht;
				FontMetrics fm = md.getGraphics().getFontMetrics();
				if (maxFM < fm.stringWidth(s)) maxFM = fm.stringWidth(s);
				i++;
				if (i > 5) {
					i = 0;
					y = 8*sht;
					if (ncol < maxFM/(float)20) x = x + ncol;
					else x = x + maxFM/(float)20;
					maxFM = 0;
				}
			}
		}
		
		boolean makeHashTable() {
			boolean flag = false;
			kinT2AlgE = new ListHashtable();		
			int code = ktm.ktm.theKinshipAlgebra.makeAlg.getAlgebraClass();
			for(Enumeration e = ktm.keys();e.hasMoreElements();) {
				String s = (String) e.nextElement();
				if (s.equals("0")) continue;
				AlgebraKinType qq = (AlgebraKinType)ktm.get(s);
				for (int i = 0;i<qq.kinTypes.size();i++){
					StringVector qqv = (StringVector) qq.kinTypes.elementAt(i);
					if (qqv.size() == 0) continue;
					String ss = qqv.toString();String ss1 = "";
					if (qqv.size() > 1)
						switch (code) {
							case MakeAlgebra.DESCRIPTIVE:
								break;
							case MakeAlgebra.CLASSIFICATORY:
								if (ss.indexOf("b+") > -1)
									ss = ktm.replaceString(ss,"b+","b");//grid only knows about b and z
								else if (ss.indexOf("b-") > -1)
									ss = ktm.replaceString(ss,"b-","b");
								else if (ss.indexOf("z+") > -1)
									ss = ktm.replaceString(ss,"z+","z");
								else if (ss.indexOf("z-") > -1)
									ss = ktm.replaceString(ss,"z-","z");
								else if (ss.indexOf("l+") > -1){
									ss1 = ss;
									ss = ktm.replaceString(ss,"l+","b");
									ss1 = ktm.replaceString(ss1,"l+","z");
								} else if (ss.indexOf("l-") > -1){
									ss1 = ss;
									ss = ktm.replaceString(ss,"l-","b");
									ss1 = ktm.replaceString(ss1,"l-","z");
								} else if (ss.indexOf("l") > -1){
									ss1 = ss;
									ss = ktm.replaceString(ss,"l","b");
									ss1 = ktm.replaceString(ss1,"l","z");								
								}
									break;
						}
							ListVector lv = kinT2AlgE.getList(ss);
					if (lv == null || lv.indexOf(qq.algPath) == -1)
						kinT2AlgE.putInList(ss,qq.algPath);//include undefine types
						if (!ss1.equals("")){
							lv = kinT2AlgE.getList(ss1);
							if (lv == null || lv.indexOf(qq.algPath) == -1)
								kinT2AlgE.putInList(ss1,qq.algPath);//include undefine types
						}
						
						if (!flag) {
							flag = (((ListVector)kinT2AlgE.getList(ss)).size() > 1);
						}
				}
			}
			return flag;
		}
		
		void simplifyKinTypeSibProducts(AlgebraKinType qq){//not used: see removeRedundancy in KinTypeMapper for a better procedure
			for (int i = 0;i<qq.kinTypes.size();i++){
				StringVector qqv = (StringVector) qq.kinTypes.elementAt(i);
				if (qqv.indexOf("b+") == -1) continue;
				int k = qqv.indexOf("b+"); int n = qqv.size();
				String test = qqv.toString();
				ktm.removeString(test,"b+");
				String ss = "";
				for (int j=i+1;j<qq.kinTypes.size();j++){
					StringVector qqv1 = (StringVector) qq.kinTypes.elementAt(j);
					if (qqv1.size() != n) continue;
					if (qqv1.indexOf("b-") != k) continue;
					String test1 = qqv1.toString();
					if (!test1.equals(test)) continue;
					ss = qqv.toString();
					ktm.replaceString(ss,"b+","b");
					ListVector lv = kinT2AlgE.getList(ss);
					if (lv == null) kinT2AlgE.putInList(ss,qq.algPath);
					else if (lv.indexOf(qq.algPath) == -1)//exclude undefined types
						kinT2AlgE.putInList(ss,qq.algPath);
					break;
				}
			}
		}
		
		boolean matchSex(String s, String sex) {
			if (s.equals("f") || s.equals("s")) return sex.equals("M");
			else if (s.equals("m") || s.equals("d")) return sex.equals("F");
			else return false;
		}
		
		void modifyKinTypeTable(){
			ListVector lv1 = findDoubleProducts();
			int size = lv1.size();
			for (lv1.reset();lv1.isNext();){
				int max = ((Integer)lv1.getNext()).intValue();
				int length = ((Integer)lv1.getNext()).intValue();
				ListVector lv = findDoubleProducts(max,length);
				if (lv != null) lv1.append(lv);
			}
		}
		
		ListVector findDoubleProducts() {
			int max = 0;
			ListVector lv1 = new ListVector();
			AlgebraPath ap1 = new AlgebraPath();
			for (Enumeration e = kinT2AlgE.keys();e.hasMoreElements();){
				ListVector lv = (ListVector)kinT2AlgE.getList((String) e.nextElement());
				if (lv.size() == 2){
					ap1 = (AlgebraPath) lv.elementAt(0);
					if (ap1.reducedPath.getGeneration() <= 0) continue;//get ancestral terms only
						max = moveProducts(lv);
						Debug.prout(0," max "+max+" lv "+lv.toString());
				}
				if (max > 0) {
					lv1.add(new Integer(max));
					lv1.add(new Integer(ap1.reducedPath.getGeneration()-1));
					max = 0;
				}
			}
			return lv1;
		}
		
		ListVector findDoubleProducts(int i, int j) {
			int max = 0;
			ListVector lv1 = new ListVector();
			AlgebraPath ap1 = new AlgebraPath();
			for (Enumeration e = kinT2AlgE.keys();e.hasMoreElements();){
				ListVector lv = (ListVector)kinT2AlgE.getList((String) e.nextElement());
				if (lv.size() == 2){
					ap1 = (AlgebraPath) lv.elementAt(0);
					if (ap1.reducedPath.getGeneration() != j) continue;//generation j <= 0 only
					else max = moveProducts(lv,i);
					Debug.prout(0,"Max again "+max+" lv "+lv.toString());
				}
				if (max > 0) {
					lv1.add(new Integer(max));
					lv1.add(new Integer(ap1.reducedPath.getGeneration()));
					max = 0;
				}
			}
			return lv1;
		}
		
		
		int moveProducts(ListVector lv) {//procedure for ancestral terms, mother's side, father's side distinction
			AlgebraPath ap1 = (AlgebraPath) lv.elementAt(0);
			AlgebraPath ap2 = (AlgebraPath) lv.elementAt(1);
			String sex = ap1.getPathSex();
			int i = 1;
			int max = 0;
			for (lv.reset();lv.isNext();){
				AlgebraKinType qq = (AlgebraKinType)ktm.get(((AlgebraPath)lv.getNext()).toString());
				AlgebraKinType qq1 = (AlgebraKinType)ktm.get(((AlgebraPath) lv.elementAt(i)).toString());
				i--;
				ListVector tmp = new ListVector();
				for (qq.kinTypes.reset();qq.kinTypes.isNext();) {
					StringVector sv = (StringVector) qq.kinTypes.getNext();
					if (sv.size() < 2) continue;
					String s1 = (String)sv.elementAt(1);
					sex = qq.algPath.reducedPath.getFirst().getSex();
					if (matchSex(s1,sex)) continue;
					else {
						tmp.addElement(sv);
						if (qq1.addKinType(sv)) {
							if ((max == 0) || (max > sv.size()))
								max = sv.size();
						}
					}
					for (tmp.reset();tmp.isNext();){
						qq.kinTypes.remove(tmp.getNext());
					}
					tmp.clear();
				}
			}
			return max;
		}
		
		int moveProducts(ListVector lv,int imax) {//generation 0 or below terms, no side distinction
			AlgebraPath ap1 = (AlgebraPath) lv.elementAt(0);
			AlgebraPath ap2 = (AlgebraPath) lv.elementAt(1);
			String sex = ap1.getPathSex();
			int i = 1;
			int max = 0;
			for (lv.reset();lv.isNext();){
				AlgebraKinType qq = (AlgebraKinType)ktm.get(((AlgebraPath)lv.getNext()).toString());
				AlgebraKinType qq1 = (AlgebraKinType)ktm.get(((AlgebraPath) lv.elementAt(i)).toString());
				i--;
				for (qq.kinTypes.reset();qq.kinTypes.isNext();) {
					StringVector sv = (StringVector) qq.kinTypes.getNext();
					if (sv.size() < 2) continue;
					String s1 = (String)sv.elementAt(1);
					sex = qq.algPath.reducedPath.getFirst().getSex();
					if (sv.size() <= imax) continue;
					else {
						if (qq1.addKinType(sv)) {
							if ((max == 0) || (max > sv.size()))
								max = sv.size();
						}
					}
				}
			}
			return max;
		}
		
		public void populateModel(ThreeD md,KinTypeMapper ktm) {
			Integer line= Model3D.SOLID;
			Color clr = Color.black;
			setThreeD(md);
			setKinTypeMapper(ktm);
			String txt = ego;
			if (egoSex.equals("M")) txt = egoM;
			else if (egoSex.equals("F")) txt = egoF;
			int code = ktm.ktm.theKinshipAlgebra.makeAlg.getAlgebraType();
			switch (code){
				case MakeAlgebra.AKT://akt
					makeHashTable();
					break;
				case MakeAlgebra.TONGAN://tongan
					makeHashTable();
					break;
				case MakeAlgebra.TROB://trob
					makeHashTable();
					break;
				case MakeAlgebra.SIBGEN://sib generators (default classificatory)
					makeHashTable();
					break;
				case MakeAlgebra.SHIPIBO://shipibo
					if (makeHashTable()) modifyKinTypeTable();//NEED to check out modify
					makeHashTable();
					break;
			}
			labelV.removeAllElements();
			sexV.removeAllElements();
			kinTermsToSymbols = new Hashtable();
			iLet = 0;
			doUpGraphSegment(0,0,0,upWidth,0,true,txt);//use ego, egoM, egoF according to desired sex
				postLabels();
		}
		
}
