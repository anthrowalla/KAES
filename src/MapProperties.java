import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Dwight Read
 * @version 1.0
 */ 

public class MapProperties extends TransferKinInfoVector {

    public MapProperties() {
    }

    boolean testMapForCrossProductEquations() {
     // if (testMap == false) return true;
     // TransferKinInfoVector tk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
      //getTk();
      TransferKinInfoVector pa = getEffectiveGenerators(UP);
      TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
      for (pa.reset();pa.isNext();){
        TransferKinInfo parent = pa.getNext();
        for (sp.reset();sp.isNext();){
          StringVector sv = getEffectiveProducts(parent,sp.getNext().getTerm());
          if (!pa.isEffectiveTerm((String)sv.elementAt(0))) return false;
        }
      }
      return true;
    }

    /** Checks the map to see if equations of the form son of sibling = daughter of siblilng
    * are valid in the kin term map.
    * @return boolean flag
    */
    boolean testMapForLinealDescendantRule(){//so or da of <= 0 generation element is the same
     // if (testMap == false) return true;
     // getTk();

      TransferKinInfoVector ch = getEffectiveGenerators(DOWN);
      Hashtable kinToGen = computeGenerations();
      StringVector termV = new StringVector(3,1);
      for(Enumeration e = kinToGen.keys();e.hasMoreElements();) {
        String key = (String) e.nextElement();
        int i = ((Integer) kinToGen.get(key)).intValue();
        //if (i <=0){termV.addElement(key);}
        if ((i <=0) && (ch.size() == 2)){
          StringVector prod1 = getEffectiveProducts(key,((TransferKinInfo)ch.elementAt(0)).getTerm());
          StringVector prod2 = getEffectiveProducts(key,((TransferKinInfo)ch.elementAt(1)).getTerm());
          return prod1.equals(prod2);
        }
      }
      return false;
    }

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

    boolean testMapForParentOfChildInLawEquations(){
    //  if (testMap == false) return true;
     // getTk();
      TransferKinInfoVector pa = getEffectiveGenerators(UP);
      TransferKinInfoVector ch = getEffectiveGenerators(DOWN);
      TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
	  if (sp.size() == 0) return false;
      Debug.prout(4,"Child in law "+termProduct(pa,sp,ch));
      return (termProduct(pa,sp,ch) == null);
    }

    boolean testMapForSingleChildRule(){//ch of sib distinct from own ch
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