import java.util.*;

public class ProductVector extends Vector {
	
	int elem=0;
	
	public ProductVector() {
		super(1,1);
	}

	public ProductVector(int n) {
		super(n);
	}
	
	public ProductVector(int n, int m) {
		super(n,m);
	}
	
	public Product getSymbol(int i) {
		elem = i;
		return (Product) elementAt(elem);
	}
	
	/** clone the product list
	* @param deep Clone the contents of the list as well
	* @return the Clone
	*/ 
	public Object clone(boolean deep) {
		ProductVector p = (ProductVector) super.clone();
		if (deep) {
			for (int i = 0;i< p.size();i++) {
				p.setElementAt(p.getSymbol(i).clone(false),i);
			}
		}
		return p;
	}
	
	/** clone the product list without cloning elements
	* @return the Clone
	*/ 
	public Object clone() {
		return clone(false); // defaults to not deep cloning the vector
	}
	
	public void deleteGenerator(int index) {
		Product p = getSymbol(index);
		KintermEntryVector ke = p.getTheLinks();
		if (ke.size() > 0) {
			int gkin,i;
			for(i=0;i<KintermEntry.ORIENTATIONS.length;i++) if (getSymbol(i).getOrientation() == p.getOrientation()) break;
			if (i == KintermEntry.ORIENTATIONS.length) gkin = -1;
			else gkin = i;
			if (gkin == -1) System.out.println("Error, missing kin orientation in setGenerating in KintermEntry!!!!");
			else {
				KintermEntryVector ge = getSymbol(gkin).getTheLinks();
				for (int j=0;j<ke.size();j++) {
					ge.addElement(ke.elementAt(j));
				}
				
			}
		}
		removeElementAt(index);
	}
	
	public void addGenerator(KintermEntry gen, int orient) {
		Product thisProd = new Product(gen,orient);
		int index = indexOf(thisProd); // must check that sex is right before entry!!!!!!!!!!
		
		if (index == -1) {
			addElement(thisProd);
			int xkin,i;
			for(i=0;i<KintermEntry.ORIENTATIONS.length;i++) if (getSymbol(i).getOrientation() == thisProd.getOrientation()) break;
			if (i == KintermEntry.ORIENTATIONS.length) xkin = -1;
			else xkin = i;
			if (xkin == -1) System.out.println("Error, missing kin orientation in setGenerating in KintermEntry!!!!");
			else {
				Product gkin = getSymbol(xkin);
				KintermEntryVector ge = gkin.getTheLinks();
				if (ge.size() > 0) {
					if (thisProd.getSex() == 1) {
						thisProd.setTheLinks(ge);
						gkin.setTheLinks(new KintermEntryVector(3));
					} else {
						for(int j=0;j<ge.size();j++) {
							KintermEntry k = ge.getSymbol(j);
							if (k.getSex().equals("N") || (k.getSubSex() == gen.getSubSex())) {
								thisProd.getTheLinks().addElement(k);
								ge.removeElementAt(j);
							} else {
							
							}
						}
					}
				}
			}
		}
	}
	
	TransferProductsVector toList() {
		TransferProductsVector v = new TransferProductsVector();
		for(int i=0;i<size();i++) {
			v.addElement(getSymbol(i).toList());
		}
		return v;
	}
}