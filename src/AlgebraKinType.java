

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class AlgebraKinType {

  String path="";

  boolean visited=false;
  AlgebraPath algPath=null;

  ListVector kinTypes = new ListVector();

  public AlgebraKinType() {
  }

  public AlgebraKinType(String a) {
    path = a;
    algPath = new AlgebraPath();
    algPath.product(new AlgebraSymbol(a));
  }

  public AlgebraKinType(AlgebraPath p) {
    path = p.toString();
    algPath = p;
  }

  public String toString() {
    return path;
  }

  public boolean equals(Object o) {
    return path.equals(o);
  }

	public void orderByLength(){
		ListVector temp = new ListVector();
	   int[] location = {0,0,0,0,0,0,0,0,0,0,0};
		//boolean flag = false;
		/*for (kinTypes.reset();kinTypes.isNext();){
			StringVector lv = (StringVector) kinTypes.getNext();
			flag = (((String)lv.elementAt(0)).startsWith("ego,") ||
			((String)lv.elementAt(0)).startsWith("ego-M,") ||
			((String)lv.elementAt(0)).startsWith("ego-F,"));
			if (flag) break;
		}*/
		for (kinTypes.reset();kinTypes.isNext();){
			StringVector lv = (StringVector) kinTypes.getNext();
			int index = 0;
			boolean flag = false;
			if (lv.size() == 1){
				flag = (((String)lv.elementAt(0)).startsWith("ego,") ||
				((String)lv.elementAt(0)).startsWith("ego-M,") ||
				((String)lv.elementAt(0)).startsWith("ego-F,"));
				if (!flag) flag = (((String)lv.elementAt(0)).indexOf("ego") > -1);
				if (flag){
					temp.insertElementAt(lv,0);
					location[0]++;
					continue;
				}
			}
			int len = lv.size();
			if (len > 10) len = 10;
			for (int i = 0;i<len;i++)
				index = index + location[i];
			location[len]++;
			if (temp.indexOf(lv) == -1)
				temp.insertElementAt(lv,index);
		}
		kinTypes = temp;
	}



/*  	public boolean addLocation(StringVector x) {
	 if (kinTypes.indexOf(x) == -1) {
		int index = 0;
		int len = ((String) x.elementAt(0)).length();
		if (len > 10) len = 10;
		for (int i = 0;i<len;i++)
		   index = index + location[i];
		location[len]++;
		kinTypes.insertElementAt(x,index);
		System.out.println("kin types "+kinTypes);
		return true;
	 }
	 return false;
	}*/

  public boolean addKinType(String kinType) {
    StringVector x = new StringVector();
    x.addElement(kinType);
    return kinTypes.addUnique(x);
  }

 public boolean addKinType(StringVector kinType) {
    return kinTypes.addUnique(kinType);
  }

  public void addKinType(ListVector kinTypes) {
    this.kinTypes.appendUnique(kinTypes);
  }

  public ListVector product(AlgebraKinType otherKin) {
    return product(otherKin.kinTypes);
  }
  
  /** replace fc, mc by fs, fd, ms, ms
	*
	*/  
  public void expandKinTypes(){
	  ListVector lv1 = new ListVector();
	  ListVector lv = kinTypes;
	  for (lv.reset();lv.isNext();){
		  ListVector tmp = new ListVector();
		  StringVector sv = (StringVector)lv.getNext();
		  sv.reset();
		  StringVector sv1 = new StringVector();
		  tmp.addElement(sv1);
		  expandParentChildKinTypes(tmp,sv1,sv);
		  for (tmp.reset();tmp.isNext();){
			  lv1.addElement(tmp.getNext());
		  }
	  }
	  kinTypes = lv1;
  }
  
  void expandParentChildKinTypes(ListVector lv, StringVector svRet, StringVector sv){
	  for (;sv.isNext();){
		  String s1 = sv.getNext();
		  if ((s1.equals("p")) || (s1.equals("c")) ||(s1.equals("u"))) {
			  StringVector sv2 = (StringVector)svRet.clone();
			  lv.addElement(sv2);
			  if (s1.equals("p")){
				  svRet.addElement("f");
				  sv2.addElement("m");
			  } else if (s1.equals("c")){
				  svRet.addElement("s");
				  sv2.addElement("d");
			  }else if (s1.equals("u")){
				  svRet.addElement("h");
				  sv2.addElement("w");
			  }
			  StringVector tmpSv = (StringVector) sv.clone();
			  //System.out.println(" sv "+sv);
			  expandParentChildKinTypes(lv,svRet,sv);
			 // System.out.println(" tmpsv "+tmpSv);
			  expandParentChildKinTypes(lv,sv2,tmpSv);
		  } else svRet.addElement(s1);
	  }	  
  }
  
 /** replace ego-Mego-F by ego-Mz; ego-Fego-M by ego-Fb  
  *
  */
  public void replaceByEgo_SibType(){
	  ListVector lv = kinTypes;
	  ListVector lv1 = new ListVector();
	 // System.out.println("start with kintypes "+kinTypes);
	  for (lv.reset();lv.isNext();){
		  StringVector sv = (StringVector)lv.getNext();
		  StringVector ret = new StringVector();
		  if (sv.size() == 1) continue;
		  if ((sv.getSymbol(0).equals("ego-M") && sv.getSymbol(1).equals("ego-F")) || 
			  (sv.getSymbol(0).equals("ego-F") && sv.getSymbol(1).equals("ego-M"))){
				  if (sv.getSymbol(0).equals("ego-M")) sv.setElementAt("z",1);
				  else sv.setElementAt("b",1);
			  }
	  }
	 // System.out.println("end with kintypes "+kinTypes);
  }
  
  /** replace xego-M by xb; xego-F by xz
	  *
	  */
  public void replaceEgoBySibType(){
	  ListVector lv = kinTypes;
	  ListVector lv1 = new ListVector();
	  // System.out.println("start with kintypes "+kinTypes);
	  for (lv.reset();lv.isNext();){
		  StringVector sv = (StringVector)lv.getNext();
		  int n = sv.size();
		 // if (((String)sv.elementAt(0)).startsWith("ego")) continue;
		  if (n <= 2) continue;
		  if (sv.getSymbol(2).equals("ego-M")) {
			  if (n == 3) sv.setElementAt("b",2);
			  else {
				  if (!sv.getSymbol(3).equals("s")&& !sv.getSymbol(3).equals("d") && 
					  !sv.getSymbol(3).equals("c") && !sv.getSymbol(3).equals("h") &&
					  !sv.getSymbol(3).equals("w") && !sv.getSymbol(3).equals("g"))
					  sv.removeElementAt(2);
				  else sv.setElementAt("b",2);
			  }
		  } else if (sv.getSymbol(2).equals("ego-F")) {
			  if (n == 3) sv.setElementAt("z",2);
			  else {
				  if (!sv.getSymbol(3).equals("s")&& !sv.getSymbol(3).equals("d") && 
					  !sv.getSymbol(3).equals("c") && !sv.getSymbol(3).equals("h") &&
					  !sv.getSymbol(3).equals("w") && !sv.getSymbol(3).equals("g"))
					  sv.removeElementAt(2);
				  else sv.setElementAt("z",2);
			  }
		  } else if (sv.lastElement().equals("ego-M")){
			  sv.setElementAt("b",sv.size()-1); 
		  }else if (sv.lastElement().equals("ego-F")){
			  sv.setElementAt("z",sv.size()-1); 
		  } 
	  }
	  // System.out.println("end with kintypes "+kinTypes);
  }
  
  /** replace fs, ms, fd, md, bz, zb by b or z
	*
	*/
  public void replaceBySibType(){
	  ListVector lv = kinTypes;
	  ListVector lv1 = new ListVector();
	  // System.out.println("start with kintypes "+kinTypes);
	  for (lv.reset();lv.isNext();){
		  StringVector sv = (StringVector)lv.getNext();
		  StringVector ret = new StringVector();
		  if (sv.size() == 1) ret.add(sv.getSymbol(0));
		  else {
			  for (int i=0;i<sv.size()-1;i++){
				  String s1 = sv.getSymbol(i);
				  String s2 = sv.getSymbol(i+1);
				  if ((s1.equals("f")) || (s1.equals("m"))){
					  if (s2.equals("s")){
						  ret.add("b");
						  i++;
					  } 
					  else if (s2.equals("d")) {
						  ret.add("z");
						  i++;
					  }
					  else ret.add(s1);
				  } else ret.add(s1);
				  if (i+1==sv.size()-1) ret.add(sv.getSymbol(i+1));
			  }			  
		  }
		  if (lv1.indexOf(ret) == -1) lv1.addElement(ret);
	  }
	  kinTypes = lv1;
	  // System.out.println("end with kintypes "+kinTypes);
  }

  StringVector replaceBySibling(StringVector st1,StringVector st2){
    StringVector sv= new StringVector();
    if (st1.isEmpty()) {
      if (!st2.isEmpty())
        sv.add(st2.getSymbol(0));
      return sv;
    }
    String s1 = st1.getSymbol(0);
    String s2 = st2.getSymbol(0);
    if ((s1.equals("f")) || (s1.equals("m"))){
      if (s2.equals("s")) {
        sv.add("b");
        return sv;
      }
      else if (s2.equals("d")) {
        sv.add("z");
        return sv;
      }
    }
    else if ((s1.equals("b")) || (s1.equals("z"))){
      if (s2.equals("b")) {
        sv.add("b");
        return sv;
      }
      else if (s2.equals("z")) {
        sv.add("z");
        return sv;
      }
    }
    sv.add(s1);
    sv.add(s2);
    return sv;
  }
  
  /** replace fb, fz, mb, mz by f or m
   *
   */
  StringVector replaceSiblingParent(StringVector st1,StringVector st2){
    StringVector sv= new StringVector();
    if (st1.isEmpty()) {
      if (!st2.isEmpty())
        sv.add(st2.getSymbol(0));
      return sv;
    }
    String s1 = st1.getSymbol(0);
    String s2 = st2.getSymbol(0);
    if ((s1.equals("b")) || (s1.equals("z"))){
      if (s2.equals("f")) {
        sv.add("f");
        return sv;
      }
      else if (s2.equals("m")) {
        sv.add("m");
        return sv;
      }
    }
    sv.add(s1);
    sv.add(s2);
    return sv;
  }

	 /** replace ego-Mx,ego-Fx by egox; replace ego-Mx, egox by egox and ego-Fx, egox by egox
	  *
	  */
  public void removeEgoRedundancy(){
	  ListVector lv = kinTypes;
	  //System.out.println("start with kintypes "+kinTypes);
	  ListVector removeL = new ListVector();
	  for (int i = 0; i<lv.size();i++){
		  StringVector sv = (StringVector)lv.elementAt(i);
		  if (!((String)sv.elementAt(0)).equals("ego-F") && !((String)sv.elementAt(0)).equals("ego-M"))
			  continue;
		  //System.out.println(" kin types sv "+sv.toString());
		  int n = sv.size();
		  for (int j=i+1;j<lv.size();j++){
			  StringVector sv1 = (StringVector)lv.elementAt(j);
			  //System.out.println(" kin types sv1 "+sv1.toString());
			  if (sv1.size() > n) break;//list ordered by size
			 /* if (((String)sv1.elementAt(0)).equals("ego")){
				  StringVector testV = (StringVector)sv1.clone();
				  testV.setElementAt((String)sv.elementAt(0),0);
				  System.out.println(" testv "+testV.toString());
				  if (!testV.toString().equals(sv.toString())) continue;
				  removeL.addElement((StringVector)lv.elementAt(i));
			  } else {*/
			  if (!((String)sv1.elementAt(0)).equals("ego-F") && !((String)sv1.elementAt(0)).equals("ego-M"))
				  continue;
			  if (((String)sv.elementAt(0)).equals((String)sv1.elementAt(0))) continue;
			  StringVector testV = (StringVector)sv1.clone();
			  testV.setElementAt((String)sv.elementAt(0),0);
			  if (!testV.toString().equals(sv.toString())) continue;
			  removeL.addElement((StringVector)lv.elementAt(j));
			  sv.setElementAt("ego",0);
				 // System.out.println(" sv withn ego "+sv);
			  //}
		  }
	  }
	  for (removeL.reset();removeL.isNext();){
		  StringVector sv = (StringVector)removeL.getNext();
		  int indx = kinTypes.indexOf(sv);
		  if (indx > -1) kinTypes.remove(indx);
	  }
  }
  
  public ListVector product(ListVector otherKin) {
      ListVector ret = new ListVector();
      StringVector acc = new StringVector();
      StringVector sv1 = new StringVector();
      for (int j = 0;j<kinTypes.size();j++){
        //tmp = (StringVector) kinTypes.getNext();
        StringVector tmp = (StringVector)((StringVector) kinTypes.get(j)).clone(true);
        if (!tmp.isEmpty()){
          sv1.add((String) tmp.lastElement());
          tmp.removeElementAt(tmp.size()-1);
        }
        for (int i = 0;i<otherKin.size();i++) {
          acc.append(tmp);
         // System.out.println("product "+tmp.toString()+" x "+otherKin.get(i).toString());
          StringVector sv2 = (StringVector) otherKin.get(i);
          StringVector sv3 = replaceBySibling(sv1,sv2);
		  if (sv3.size() == 2) sv3 = replaceSiblingParent(sv1,sv2);
          //acc.append((StringVector) otherKin.get(i));
          acc.append(sv3);
		  if (acc.size() > 6) {acc = new StringVector();continue;}
          ret.addUnique(acc);
          acc = new StringVector();
        }
        sv1=new StringVector();
      }
	  System.out.println("+=+=+=+ sv1="+sv1+" acc="+acc+" ret="+ret);
      return ret;
  }

  /** replace xy+,xy- kintype product combinations by xy
   *
   */
	public void removeRedundancy(){
		ListVector lv = kinTypes;
		ListVector removeL = new ListVector();
		for (lv.reset();lv.isNext();){
			StringVector sv = (StringVector)lv.getNext();
			for (sv.reset();sv.isNext();){
				String st = sv.getNext();
				if (st.startsWith("ego")) {
					if (!sv.isNext()) break;
					st = sv.getNext();
					if (!sv.isNext()) break;
					st = sv.getNext();
				}
				int i = st.indexOf("+");
				if (i > -1){
					int indx = sv.indexOf(st);
					String s = st.substring(0,i);
					StringVector sv1 = (StringVector) sv.clone();
					sv1.remove(indx);
					sv1.insertElementAt(s+"-",indx);
					int m = kinTypes.indexOf(sv1);
					if (m > -1){
						removeL.addElement(sv);
						StringVector sv2 = (StringVector)kinTypes.elementAt(m);
						sv2.remove(indx);
						sv2.insertElementAt(s,indx);
					}
				}
			}
		}
		for (removeL.reset();removeL.isNext();){
			StringVector sv = (StringVector)removeL.getNext();
			int indx = kinTypes.indexOf(sv);
			if (indx > -1) kinTypes.remove(indx);
		}
	}
}

/*  public void recordxxxxx(AlgebraKinType q,  AlgebraSymbol g, AlgebraPath a, AlgebraPath ap) {
    thePaths.addElement((AlgebraPath) a.clone());
   // AlgebraSymbolVector  dd = a.getReducedProductPath();
    //if (dd.size() == 1 && dd.getFirst().isGenerator()) return;

    AlgebraKinType gg = (AlgebraKinType) get(g+"gen".toString());
    //AlgebraKinType gg = (AlgebraKinType) ht.get(g.toString());
    AlgebraKinType aa = (AlgebraKinType) ht.get(ap.toString());
	System.out.println(" hash talb ht "+ht.toString());
System.out.println(" q "+q+" g "+g+" GG "+gg +" aa "+aa+ "a "+a+" ap "+ap);
    ListVector qxgg = q.product(gg);
System.out.println(" qxgg "+qxgg);
	aa.addKinType(qxgg);
    AlgebraKinType aaa = (AlgebraKinType) get(a.toString());
    aaa.addKinType(qxgg);
System.out.println(" aaa at end is "+aaa + " aa "+aa);
 }
*/


