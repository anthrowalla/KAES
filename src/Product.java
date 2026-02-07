/** Holds the link between a generating term and a product. 
*/
public class Product implements java.lang.Cloneable{

	public Product() {
		theLinks = new KintermEntryVector(3);
	}
	
	public Product(KintermEntry gen, int orient) {
		this();
		generator = gen;
		orientation = orient;
	}
	
	
	public void setGenerator(KintermEntry theGenerator) {
		this.generator = theGenerator;
	}
	
	public Object clone(boolean a) {
		Product p = new Product();
		p.setGenerator(generator);
		p.setOrientation(orientation);
		if (a) p.setTheLinks(theLinks);
		else p.setTheLinks(new KintermEntryVector(3));
		return p;
	}
	
	public String toString() {
		return "gen="+(getGenerator()+" orientation="+getOrientation()+" links="+theLinks);
	}
	
	public Object clone() {
		return clone(true);
	}

	public KintermEntry getGenerator() {
		return generator;
	}

	public void setTheLinks(KintermEntryVector theLinks) {
		this.theLinks = theLinks;
	}

	public KintermEntryVector getTheLinks() {
		return theLinks;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getOrientation() {
		return orientation;
	}
	
	public int getArrowType() {
		return orientation+generator.getSubSex()*KintermEntry.NOFFSET;
	}
   
	public int getSex() {
		return getGenerator().getSubSex();
	}


	public boolean equals(Object p) {
		return ((Product)p).getGenerator().getTheTerm().equals(getGenerator().getTheTerm()) &&
					((Product)p).getOrientation() == getOrientation() && 
					((Product)p).getSex() == getSex();
	}
	
	TransferProduct toList() {
		TransferProduct v = new TransferProduct();
		v.addElement(this.generator.getTheTerm());
		for (int i=0;i<theLinks.size();i++) {
			v.addElement(theLinks.getSymbol(i).getTheTerm());
		}
		return v;
	}
   
	protected KintermEntry generator=null;
	protected KintermEntryVector theLinks = new KintermEntryVector();
	protected int orientation=KintermEntry.UP;
}