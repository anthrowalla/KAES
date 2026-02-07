import java.util.Vector;

public interface KinTermEditor {
	/** edit the kinship term in some manner
	*/
	public void editKinterm(KintermEditObject k);
	/** draw lines in own space
	*/
	public void drawTermLine(LineObject l);
	/** get the product list for kinship terms in this panel
	*/
	public ListVector getProductList();
	
	/** set the product list for kinship terms in this panel
	*/
	public void setProductList(ListVector p);
	/** prototype for product list
	*/
	public TransferKinInfoVector getTransferKinInfo() ;
	public void removeTerm(KintermEditObject k);
	public void removeTerm();
	public KintermEditObject getSelected();
	public void clearSelected();
	public void select(KintermEditObject k);
	public void updateSelected();
	public void repaint(); // ??
}
