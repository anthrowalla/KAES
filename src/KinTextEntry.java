import java.awt.*;
import java.awt.Component;
import java.awt.Panel;

import java.awt.Label;

public class KinTextEntry extends Object
{
	public KinTextEntry()
	{
	}

	public KinTextEntry(KintermEntry ktm)
	{
			this();
			setKinTerm(ktm);
			ProductVector p = (ProductVector) ktm.products;
			//textField1.setText(ktm.getTheTerm());
			// updateInfo();
//			makeMenus = new MakeMenus();
//			setMenuBar(makeMenus.mainmenuBar);
							   
	}

	
	public ListVector getKintermProducts() {
		KintermEntry ktm = getKinTerm();
		ProductVector p = (ProductVector) ktm.products;
		ListVector out = new ListVector();
		out.addElement(ktm); // add in the kin term into position 1
		for (int i = 0;i<p.size();i++) {
			Product x = (Product) p.elementAt(i);
			if (x.getGenerator().getTheTerm().equals("Kin")) {
				continue;
			}
			KintermEntryVector k = x.getTheLinks();
			out.addElement(k);
		}
		// System.out.println("KT Products: "+out.toString());
		return(out);
	}
	
	public StringVector getKintermProductList() {
		KintermEntry ktm = getKinTerm();
		ProductVector p = (ProductVector) ktm.products;
		StringVector out = new StringVector();
		out.addElement(ktm.getTheTerm()); // add in the kin term into position 1
		for (int i = 0;i<p.size();i++) {
			Product x = (Product) p.elementAt(i);
			if (x.getGenerator().getTheTerm().equals("Kin")) {
				continue;
			}
			KintermEntryVector k = x.getTheLinks();
			String rs="";
			for (int j = 0;j<k.size();j++) {
				KintermEntry r = k.getSymbol(j);
				rs += r.getTheTerm();
				if (j < (k.size()-1)) rs += "/";
			}
			out.addElement(rs);
		}
		System.out.println("KT Products: "+out.toString());
		return(out);
	}

	Panel lastPanel=null;
	
	
	public Panel updateInfo() {
		ListVector sv = getKintermProducts(); // get the line
		SymMouse sm = new SymMouse();
		Panel pn = lastPanel;
		if (pn == null) {
			pn = new Panel();
			pn.setLayout(new GridLayout(1,sv.size()));
			pn.setSize(sv.size()*ScaleManager.s(150),ScaleManager.s(16));
		} else pn.removeAll();
		pn.setVisible(false);
		//int loc=120; 
		KintermEntryForm kt = (KintermEntryForm) sv.elementAt(0);
		Label l = new Label(kt.getTheTerm());
		l.setSize(ScaleManager.s(90),ScaleManager.s(16));
		if (kt.getSex().equals("M")) l.setForeground(Color.blue);
		else if (kt.getSex().equals("F")) l.setForeground(Color.red);
		else if (kt.getSex().equals("N")) l.setForeground(Color.gray);
		pn.add(l); l.addMouseListener(sm);l.setName("term");
		for (int i = 1;i<sv.size();i++) {
			KintermEntryVector kv = (KintermEntryVector) sv.elementAt(i);
			String rs="";
			String psex="";
			for (int j = 0;j<kv.size();j++) {
				KintermEntry r = kv.getSymbol(j);
				rs += r.getTheTerm();
				if (j < (kv.size()-1)) rs += "/";
				if (psex.equals("")) psex = r.getSex();
				else if (!psex.equals(r.getSex())) psex = "N";
			}
			l = new Label(rs);
			l.setSize(ScaleManager.s(90),ScaleManager.s(16));
			pn.add(l);
			l.addMouseListener(sm);
			l.setName(rs);
			if (psex.equals("M")) l.setForeground(Color.blue);
			else if (psex.equals("F")) l.setForeground(Color.red);
			else if (psex.equals("N")) l.setForeground(Color.gray);
			if (rs.equals("")) l.setBackground(new Color(0xf0f0f0));
			pn.layout();
			pn.setVisible(true);
			// pn.setLocation(loc,5); loc += 120;
		}
		// setSize(1000,32);
		lastPanel = pn;
		lastPanel.show();
		return pn;
	}
	
	
	public Panel updateInfoYY() {
		ListVector sv = getKintermProducts(); // get the line
		SymMouse sm = new SymMouse();

		Panel pn = new Panel();
		pn.setLayout(new GridLayout(1,sv.size()));
		pn.setSize(ScaleManager.s(1000),ScaleManager.s(16));
		//int loc=120;
		KintermEntryForm kt = (KintermEntryForm) sv.elementAt(0);
		Label l = new Label(kt.getTheTerm());
		l.setSize(ScaleManager.s(112),ScaleManager.s(16));
		if (kt.getSex().equals("M")) l.setForeground(Color.blue);
		else if (kt.getSex().equals("F")) l.setForeground(Color.red);
		else if (kt.getSex().equals("N")) l.setForeground(Color.gray);
		pn.add(l); l.addMouseListener(sm);l.setName("term");
		for (int i = 1;i<sv.size();i++) {
			KintermEntryVector kv = (KintermEntryVector) sv.elementAt(i);
			String rs="";
			String psex="";
			for (int j = 0;j<kv.size();j++) {
				KintermEntry r = kv.getSymbol(j);
				rs += r.getTheTerm();
				if (j < (kv.size()-1)) rs += "/";
				if (psex.equals("")) psex = r.getSex();
				else if (!psex.equals(r.getSex())) psex = "N";
			}
			l = new Label(rs);
			l.setSize(ScaleManager.s(112),ScaleManager.s(16));
			pn.add(l);
			l.addMouseListener(sm);
			l.setName(""+i);
			if (psex.equals("M")) l.setForeground(Color.blue);
			else if (psex.equals("F")) l.setForeground(Color.red);
			else if (psex.equals("N")) l.setForeground(Color.gray);
			if (rs.equals("")) l.setBackground(new Color(0xf8f8f0));
			// pn.setLocation(loc,5); loc += 120;
		}
		// setSize(1000,32);
		lastPanel = pn;
		return pn;
	}
	
	public Panel updateInfoX() {
		StringVector sv = getKintermProductList(); // get the line
		Panel pn = new Panel();
		pn.setLayout(new GridLayout(1,sv.size()));
		pn.setSize(ScaleManager.s(1000),ScaleManager.s(16));
		//int loc=120;
		for (int i = 0;i<sv.size();i++) {
			Label l = new Label(sv.getSymbol(i));
			l.setSize(ScaleManager.s(112),ScaleManager.s(16));
			pn.add(l);
			
			// pn.setLocation(loc,5); loc += 120;
		}
		// setSize(1000,32);
		return pn;
	}
	
	public Panel getGeneratorPanel() {
		Panel pn = new Panel();
		pn.setLayout(new GridLayout(1,getKintermProducts().size()));
		pn.setSize(getKintermProducts().size()*ScaleManager.s(150),ScaleManager.s(16));
		Label spacer = new Label();
		spacer.setText("           Generators");
		spacer.setSize(ScaleManager.s(90),ScaleManager.s(16));
		pn.add(spacer);
		
		KintermEntry ktm = getKinTerm();
		ProductVector p = (ProductVector) ktm.products;
		//System.out.print(p.size()+" "+ktm.getTheTerm()+": ");
		int lab=0;
		for (int i = 0;i<p.size();i++) {
			Product x = (Product) p.elementAt(i);
			if (x.getGenerator().getTheTerm().equals("Kin")) {
				lab++;
				continue;
			}
			Label lbl;
			if ((pn.getComponentCount()-1) > (i-lab+1)) {
				lbl = ((Label) pn.getComponent(i-lab+1));
			} else {
				lbl = new Label();
				lbl.setSize(ScaleManager.s(90),ScaleManager.s(16));
				pn.add(lbl);
			}
												
		//	System.out.print(x.getGenerator().getTheTerm()+" "+(i-lab)+" "); 
			KintermEntryVector k = x.getTheLinks();
			String rs=x.getGenerator().getTheTerm();
		/*	for (int j = 0;j<k.size();j++) {
				KintermEntry r = k.getSymbol(j);
				System.out.print(r.getTheTerm()+" "); 
				rs += r.getTheTerm()+" ";
			}*/
		//	System.out.print("\n");
			lbl.setText(rs);
			if (x.getGenerator().getSex().equals("M")) lbl.setForeground(Color.blue);
			else if (x.getGenerator().getSex().equals("F")) lbl.setForeground(Color.red);
			else if (x.getGenerator().getSex().equals("N")) lbl.setForeground(Color.gray);
			
		}
		
		return pn;
		
	}
	
	//DECLARE_CONTROLS
	//java.awt.Button button1 = new java.awt.Button();
	//java.awt.Checkbox neutralradioButton1 = new java.awt.Checkbox();
	//java.awt.CheckboxGroup Group1 = new java.awt.CheckboxGroup();
	//java.awt.Checkbox femaleradioButton1 = new java.awt.Checkbox();
	//java.awt.Checkbox maleradioButton1 = new java.awt.Checkbox();
	//java.awt.Label textField1 = new java.awt.Label();

	MakeMenus makeMenus;
	java.util.Vector products = new java.util.Vector();

	/**
	* We override this method so that nothing gets added to this component
	* @param comp the component to be added
	* @param constraints an object expressing layout contraints for this
	* component
	* @param index the position in the container's list at which to
	* insert the component.  -1 means insert at the end.
	* @see #remove
	* @see LayoutManager
	*/
	protected void addImpl(Component comp, Object constraints, int index) {
		//do nothing
	}

	public void setKinTerm(KintermEntry kinTerm) {
		this.kinTerm = kinTerm;
	}

	public KintermEntry getKinTerm() {
		return kinTerm;
	}
	protected KintermEntry kinTerm=null;

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
		//	if (object == button1)
			//	button1_ActionPerformed(event);
		}
	}

	void button1_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		if (kinTerm == null) return;
	}
	
	public void KintermEntry_MouseReleased(java.awt.event.MouseEvent event) {
		Label x = (Label) event.getSource();
			Component c[] = lastPanel.getComponents();
			if (c[0].getName().equals(x.getName())) { // its a term
				System.out.println("Editing "+kinTerm.getTheTerm());
				((KintermEntryForm) kinTerm).editTerm(lastPanel.getParent());
			} else { // its perhaps a product
				for (int i=1;i<c.length;i++) {
					if (c[i].equals(x)) {
						System.out.println("Editing "+kinTerm.getTheTerm()+" product "+i+" "+x.getName());
						((KintermEntryForm) kinTerm).editProduct(lastPanel.getParent(),i,x);
						//x.setText(((KintermEntryForm) kinTerm).editProduct(x.getText()));
					}
				}
			}
		// }
	}
	
	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mousePressed(java.awt.event.MouseEvent event)
	{
			Object object = event.getSource();
			//if (object instanceof Label)
			//	System.out.println("Mouse pressed on "+kinTerm.getTheTerm());
	}
		public void mouseEntered(java.awt.event.MouseEvent event)
	{
			Object object = event.getSource();
			if (object instanceof Label) {
				//System.out.println("Mouse entered on "+kinTerm.getTheTerm());
				lastPanel.getComponents()[0].setBackground(new Color(0xf0f0fb));
				lastPanel.repaint();
			}
				
	}
		public void mouseExited(java.awt.event.MouseEvent event)
	{
			Object object = event.getSource();
			if (object instanceof Label) {
				//System.out.println("Mouse exited on "+kinTerm.getTheTerm());
				lastPanel.getComponents()[0].setBackground(new Color(0xffffff));
				lastPanel.repaint();
			}
				
	}
		
		public void mouseReleased(java.awt.event.MouseEvent event)
	{
			Object object = event.getSource();
			if (object instanceof Label)
				KintermEntry_MouseReleased(event);
	}
	}
/*	
	class SymWindow extends java.awt.event.WindowAdapter {
		public void windowClosing(java.awt.event.WindowEvent event) {
			Object object = event.getSource();
			if (object == KintermFrame.this)
				KintermFrame_WindowClosing(event);
		}
		
		public void windowActivated(java.awt.event.WindowEvent event) {
			makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
			GlobalWindowManager.setCurrentWindow(KinTextEntry.this);
			// setFrameAlgebra(getFrameAlgebra());
			//makeMenus.operationsmenu.setEnabled(false);
			// makeMenus.modeMenu.setEnabled(false);

			super.windowActivated(event);
		}
	}
*/	
}
