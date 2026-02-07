/*

	A basic extension of the java.awt.Frame class

 */



import java.awt.*;
import java.util.*;
import java.awt.event.*;

/* Revision History
* 16/7/04 MF created
*/


public class KinshipTermsMapPanel extends Panel { // implements KinTermEditor 

	KinshipTermsPanel coPanel=null;
	
	public KinshipTermsMapPanel(KinshipTermsPanel kp) {
		coPanel = kp;
		setLayout(null);
		setSize(ScaleManager.s(800),ScaleManager.s(600));
		touchypanel = new java.awt.Panel();
		touchypanel.setLayout(null);
		touchypanel.setVisible(false);
		touchypanel.setBounds(ScaleManager.s(9),ScaleManager.s(524),ScaleManager.s(255),ScaleManager.s(70));
		touchypanel.setBackground(Color.blue);
		add(touchypanel);
		etcCheckbox = new java.awt.Checkbox("Etc.");
		etcCheckbox.setVisible(false);
		etcCheckbox.setBounds(ScaleManager.s(172),ScaleManager.s(6),ScaleManager.s(43),ScaleManager.s(22));
		etcCheckbox.setFont(ScaleManager.font("SansSerif", Font.BOLD, 10));
		etcCheckbox.setForeground(Color.white);
		touchypanel.add(etcCheckbox);
		arrowSelect = new java.awt.Choice();
		arrowSelect.addItem("Ancestor");
		arrowSelect.addItem("Descendant");
		arrowSelect.addItem("Collateral+");
		arrowSelect.addItem("Collateral-");
		arrowSelect.addItem("Spouse");
		try {
			arrowSelect.select(-1);
		}
		catch (IllegalArgumentException e) { }
		arrowSelect.setVisible(false);
		touchypanel.add(arrowSelect);
		arrowSelect.setBounds(ScaleManager.s(152),ScaleManager.s(32),ScaleManager.s(93),ScaleManager.s(31));
		arrowSelect.setFont(ScaleManager.font("SansSerif", Font.PLAIN, 10));
		generatorCheckbox = new java.awt.Checkbox("Generator");
		generatorCheckbox.setBounds(ScaleManager.s(80),ScaleManager.s(32),ScaleManager.s(67),ScaleManager.s(31));
		generatorCheckbox.setFont(ScaleManager.font("SansSerif", Font.BOLD, 10));
		generatorCheckbox.setForeground(Color.white);
		touchypanel.add(generatorCheckbox);
		sexChoice = new java.awt.Choice();
		sexChoice.addItem("Neutral");
		sexChoice.addItem("Female");
		sexChoice.addItem("Male");
		// sexChoice.setBackground(Color.white);
		touchypanel.add(sexChoice);
		sexChoice.setBounds(ScaleManager.s(3),ScaleManager.s(32),ScaleManager.s(71),ScaleManager.s(31));
		sexChoice.setFont(ScaleManager.font("SansSerif", Font.PLAIN, 10));
		button2 = new java.awt.Button();
		button2.setLabel("OK");
		button2.setBounds(ScaleManager.s(215),ScaleManager.s(4),ScaleManager.s(29),ScaleManager.s(25));
		// button2.setBackground(Color.white);
		touchypanel.add(button2);
		textField1 = new java.awt.TextField();
		textField1.setBounds(ScaleManager.s(1),ScaleManager.s(2),ScaleManager.s(170),ScaleManager.s(29));
		textField1.setFont(ScaleManager.font("Serif", Font.PLAIN, 12));
		textField1.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		textField1.setBackground(Color.white);
		touchypanel.add(textField1);
		//touchypanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		//}}
		//{{INIT_MENUS
		//}}
		//{{REGISTER_LISTENERS
		SymMouse aSymMouse = new SymMouse();
		button2.addMouseListener(aSymMouse);
		lSymAction = new SymAction();
		this.addMouseListener(aSymMouse);
		SymMouseMotion aSymMouseMotion = new SymMouseMotion();
		this.addMouseMotionListener(aSymMouseMotion);
		SymItem lSymItem = new SymItem();
		generatorCheckbox.addItemListener(lSymItem);
		etcCheckbox.addItemListener(lSymItem);
		sexChoice.addItemListener(lSymItem);
		arrowSelect.addItemListener(lSymItem);
		//coPanel.termChoices.addItemListener(lSymItem);
		SymComponent aSymComponent = new SymComponent();
		this.addComponentListener(aSymComponent);
		SymKey aSymKey = new SymKey();
		this.addKeyListener(aSymKey);	
	}
	SymAction lSymAction;
	Button newKintableTermButton;
	
	class SymWindow extends java.awt.event.WindowAdapter {
		public void windowClosing(java.awt.event.WindowEvent event) {
			Object object = event.getSource();
			if (object == KinshipTermsMapPanel.this)
				KinshipTermsPanel_WindowClosing(event); 
		}
	}

	void KinshipTermsPanel_WindowClosing(java.awt.event.WindowEvent event)
{
		setVisible(false);		 // hide the Frame
}

	public void addNotify()
{
	    // Record the size of the window prior to calling parents addNotify.
	    Dimension d = getSize();
		
		super.addNotify();
		if (fComponentsAdjusted)
			return;
		// Adjust components according to the insets
		setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets().left, insets().top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
}


public void termChoice_ItemStateChanged(java.awt.event.ItemEvent event) {
	System.out.println("termChoice event reached");
	int lab=0;
	ProductVector px = kinTerm.products;
	for (int i = 0;i<px.size();i++) {
		Product x = (Product) px.elementAt(i);
		if (x.getGenerator().getTheTerm().equals("Kin")) {
			lab++;
			continue;
		}
	}
	lab--;
	if (oldProduct != null && !oldProduct.getTheTerm().equals(termChoices.getSelectedItem())) { // replacing a product
		System.out.println("Delete Product Link: "+kinTerm.toString()+" old: "+oldProduct.toString());
		kinTerm.removeTerm(oldProduct, (Product) kinTerm.products.elementAt(oldGen+lab));
		System.out.println("Deleted oldproduct "+oldProduct.toString()+" of product"+((Product) kinTerm.products.elementAt(oldGen+lab)).toString());
		oldProductLabel.setText("");
		oldProductLabel.setName("");
	} else {
		System.out.println("Skipping delete: oldProduct="+oldProduct+" Choice "+termChoices.getSelectedItem());
	}
	
	String item = termChoices.getSelectedItem();
	if (!item.equals("") &&  (oldProduct == null || !item.equals(oldProduct.getTheTerm())) ) {
		 // a new product 
		System.out.println("Adding Product for "+kinTerm);
		
		if (oldProductLabel != null) {
			oldProductLabel.setText(item);
			oldProductLabel.setName(item);
		}
		
		KintermEntryForm kn = null;
		int i = coPanel.termList.indexOf(item);
		if (i != -1) {
			kn = (KintermEntryForm) coPanel.ktList.elementAt(i);
			oldProduct=kn;
			System.out.println("Adding Product: "+kn);
			//kn.setFrom(kinTerm);
			Product p = (Product) kinTerm.products.elementAt(oldGen+lab);
			System.out.println("for Product: "+p.toString());
			ListVector genList = kinTerm.findPossibleGenerators(p.getOrientation(),kn);
			if (genList.size()>0) {
				kn.setFrom(kinTerm);
				System.out.println("New Product Link: "+kn.toString()+" setFrom "+kinTerm.toString());
				try {
					for (genList.reset();genList.isNext();) {
						kinTerm.setTo(kn,(KintermEntry) genList.getNext(),p.getOrientation());
						System.out.println("in loop");
						//System.out.println("New Product: "+kinTerm.toString()+" to "+kn.toString()+" gen "+genList.getNext().toString()+" orientation "+p.getOrientation());
					}
				} catch (Exception e) {
					System.out.println("Generator="+genList.elementAt(genList.index).toString());
					System.out.println("GenList="+genList);
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
				
		} else {
			System.out.println("No kinterm named "+termChoices.getSelectedItem()+" found");
			return; //
		}
	} else {
		//oldProduct = null;
		//oldProductLabel = null;
		//oldGen = -1;
	}
	coPanel.repaint();
}

KintermEntryForm oldProduct = null;
Label oldProductLabel = null;
int oldGen = -1;

public void editProduct(KintermEntryForm k, int gen, Label kp) {
	System.out.println("In ktmp.editproduct");
	kinTerm = k;
	oldProductLabel= kp;
	oldGen = gen;
	StringVector termList = coPanel.termList;
	ListVector ktList = coPanel.ktList;
	Choice ch = termChoices = coPanel.termChoices;
	System.out.println(ch.toString());
	String oldProd = kp.getText();
	int x;
	if (!oldProd.equals("")) {
		int i = termList.indexOf(oldProd);
		if (i != -1) {
			oldProduct = (KintermEntryForm) ktList.elementAt(i);
		} else if ((x = oldProd.indexOf("/")) != -1) {
			Label p1 = new Label( oldProd.substring(0,x));
			Label p2 = new Label(oldProd.substring(x+1));
			editProduct(k,gen,p1);
			editProduct(k,gen,p2);
			return;
		} else oldProduct = null;
	} else oldProduct = null;
	
	Rectangle r = kinTerm.getKinText().lastPanel.bounds();
	r.x += kp.getBounds().x;r.width=120;
	ch.removeItemListener(lSymItem);
	ch.addItemListener(lSymItem);
	ch.setBounds(r);
	ch.select(oldProd);
	ch.setVisible(true);
}

	SymItem lSymItem = new SymItem();			

public void editKinterm(KintermEntryForm kte) {
	Panel ktt = touchypanel;
	ktt.setVisible(false);
	//add(ktt);
	kinTerm = kte;
	if (kinTerm.getSex().equals("M")) {
		//	maleradioButton1.getCheckBoxGroup().getSelectedCheckBox().setState(false);
		sexChoice.select(2);
	} else if (kinTerm.getSex().equals("F")) {
		sexChoice.select(1);
	} else if (kinTerm.getSex().equals("N")) {
		sexChoice.select(0);
	}
	textField1.setText(kinTerm.getTheTerm());
	
	generatorCheckbox.setState(kte.isGenerating());
	arrowSelect.setVisible(generatorCheckbox.getState());
	if (kte.getOrientation() == -1) arrowSelect.select(0);
	else arrowSelect.select(kte.getOrientation());
	
	Rectangle r = ktt.getBounds();
	Rectangle b =  kinTerm.getKinText().lastPanel.getBounds();
	r.x = 150;
	r.y = b.y;
	Rectangle p = getBounds();
	if (r.y+r.height > p.height) r.y -= (r.y+r.height - p.height);
	System.out.println(r.toString());
	ktt.setBounds(r);
	textField1.selectAll();
	if (kinTerm.isFirstTime()) {
		etcCheckbox.setVisible(true);
		etcCheckbox.setState(false);
	} else if (kinTerm.isEtc()) {
		etcCheckbox.setVisible(true);
		etcCheckbox.setState(true);
	} else if (!kinTerm.isEtc()) {
		etcCheckbox.setVisible(true);
		etcCheckbox.setState(false);
	}
	ktt.setVisible(true);
	// Debug.prout(4,"YYY");
}

class SymMouse extends java.awt.event.MouseAdapter
{
	public void mousePressed(java.awt.event.MouseEvent event)
	{
		//sendMouseEventToChildren(event);
		Object object = event.getSource();
		if (object == KinshipTermsMapPanel.this)
			KinshipTermsPanel_MousePressed(event);
	}
	public void mouseReleased(java.awt.event.MouseEvent event)
	{
		//sendMouseEventToChildren(event);
		Object object = event.getSource();
		if (object == button2)
			button2_MouseReleased(event);
		else if (object == KinshipTermsMapPanel.this)
			KinshipTermsPanel_MouseReleased(event);
	}
}

void button2_MouseReleased(java.awt.event.MouseEvent event)
{
	// Make KinshipTermsPanel resizable
	if (kinTerm == null) return;
	kinTerm.setTheTerm(textField1.getText());
	//kinTerm.setSex(sexChoice.getSelectedItem().substring(0,1));
	if (!kinTerm.isCovered()) kinTerm.setGenerating(generatorCheckbox.getState());
	kinTerm.setEtc(etcCheckbox.getState());
	etcCheckbox.setVisible(false);
	if (generatorCheckbox.getState()) {
		kinTerm.setOrientation(arrowSelect.getSelectedIndex());
	}
	kinTerm.setSex(sexChoice.getSelectedItem().substring(0,1));
	//kinTerm.setSex(maleradioButton1.getCheckboxGroup().getSelectedCheckbox().getLabel().substring(0,1));
	touchypanel.setVisible(false);
	kinTerm.getKinText().updateInfo();
	kinTerm.redrawOthers();
	kinTerm.repaint();
	kinTerm = null;
	repaint();
	coPanel.updateKintable();
}

class SymAction implements java.awt.event.ActionListener
{
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		Object object = event.getSource();
		if (object == newKintableTermButton) {
			System.out.println("In new Kinterm Button");
			coPanel.newTerm();
			Component[] c = coPanel.getComponents();
			System.out.println("Trying to edit "+((KintermEntryForm) c[c.length-1]).getTheTerm());
			editKinterm((KintermEntryForm) c[c.length-1]);
			touchypanel.setLocation(newKintableTermButton.getBounds().x+50,newKintableTermButton.getBounds().y);
		}
		
	}
}

// Vector kinterms = new Vector(15);



void KinshipTermsPanel_MousePressed(java.awt.event.MouseEvent event)
{
	// to do: code goes here.
	
	//{{CONNECTION
	
	//}}
}

void KinshipTermsPanel_MouseReleased(java.awt.event.MouseEvent event)
{
	// to do: code goes here.
	//LineObject l = clickedLine(event.getX(),event.getY());
	//if (l != null) {
	//	Debug.prout(4,"............ Line clicked ..............");
		// mants.setSelectedLine(l);
//	} // else clearSelected();
}

class SymMouseMotion extends java.awt.event.MouseMotionAdapter
{
	public void mouseMoved(java.awt.event.MouseEvent event)
	{
		Object object = event.getSource();
		//if (object == KinshipTermsMapPanel.this)
			// KinshipTermsPanel_MouseMoved(event);
	}
	
	public void mouseDragged(java.awt.event.MouseEvent event)
	{
		//sendMouseEventToChildren(event);
		Object object = event.getSource();
		if (object == KinshipTermsMapPanel.this)
			KinshipTermsPanel_MouseDragged(event);
	}
}

void KinshipTermsPanel_MouseDragged(java.awt.event.MouseEvent event)
{
}

class SymItem implements java.awt.event.ItemListener
{
	public void itemStateChanged(java.awt.event.ItemEvent event)
	{
		Object object = event.getSource();
		if (object == generatorCheckbox) {
			generatorCheckbox_ItemStateChanged(event);
			
		}
		
		else if (object == etcCheckbox)
			etcCheckbox_ItemStateChanged(event);
		else if (object == sexChoice) {
			sexChoice_ItemStateChanged(event);
		} else if (object == arrowSelect) {
			arrowSelect_ItemStateChanged(event);
		} else if (object == termChoices) {
			termChoice_ItemStateChanged(event);
		}
	}
}

	void arrowSelect_ItemStateChanged(java.awt.event.ItemEvent event) {
		if (kinTerm.isCovered()) {
			if (kinTerm.getOrientation() == -1) arrowSelect.select(0);
			else arrowSelect.select(kinTerm.getOrientation());
			this.repaint();
		}
	}

	void sexChoice_ItemStateChanged(java.awt.event.ItemEvent event) {
		if (kinTerm.isCovered()) {
				sexChoice.setVisible(false);
				if (kinTerm.getSex().equals("M")) {
					sexChoice.select(2);
				} else if (kinTerm.getSex().equals("F")) {
					sexChoice.select(1);
				} else if (kinTerm.getSex().equals("N")) {
					sexChoice.select(0);
				}
				sexChoice.setVisible(true);
				this.repaint();
		}
	}

void generatorCheckbox_ItemStateChanged(java.awt.event.ItemEvent event)
{
	if (kinTerm.isCovered()) {
			generatorCheckbox.setState(kinTerm.isGenerating());
			return;
	}

	arrowSelect.setVisible(generatorCheckbox.getState());
	touchypanel.repaint();
	repaint();
}

class SymComponent extends java.awt.event.ComponentAdapter
{
	public void componentResized(java.awt.event.ComponentEvent event)
	{
		Object object = event.getSource();
		if (object == KinshipTermsMapPanel.this)
			KinshipTermsPanel_componentResized(event);
	}
}

void KinshipTermsPanel_componentResized(java.awt.event.ComponentEvent event)
{
	// to do: code goes here.
}


class SymKey extends java.awt.event.KeyAdapter
{
	public void keyReleased(java.awt.event.KeyEvent event)
	{
		Object object = event.getSource();
		if (object == KinshipTermsMapPanel.this)
			KinshipTermsPanel_KeyReleased(event);
	}
}



void KinshipTermsPanel_KeyReleased(java.awt.event.KeyEvent event)
{
	int c=event.getKeyCode();
	Debug.prout(4,"Key Released Keycode "+c);
	if (c == event.VK_DELETE || c == event.VK_BACK_SPACE) {
		// deleteLine();
	}
	// to do: code goes here.
}

void etcCheckbox_ItemStateChanged(java.awt.event.ItemEvent event)
{
	if (kinTerm.isCovered()) {
		etcCheckbox.setState(kinTerm.isEtc());
		return;
	}
	
	if (etcCheckbox.getState()) {
		String x = textField1.getText();
		if (x.equalsIgnoreCase("Etc") || x.equals("") || x.equals("Click to enter kin term")) {
			Component[] c = getComponents();
			String t;
			int ndx;
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
			for(int i=0;i<c.length;i++) {
				if (!(c[i] instanceof KintermEditObject)) continue;
				t = ((KintermEditObject)c[i]).getTheTerm();
				if (t.length() != 5) continue;
				if (t.substring(0,3).equalsIgnoreCase("Etc")) {
					t = t.substring(4,5);
					//Debug.prout(4,"x="+t);
					//if (t.length() > 1) continue;
					t = t.toUpperCase();
					if ((ndx = alphabet.indexOf(t)) != -1) 
						alphabet = alphabet.substring(0,ndx)+alphabet.substring(ndx+1);
				}
			}
			t = "etc-"+alphabet.substring(0,1);
			kinTerm.setTheTerm(t);
			textField1.setText(t);
		} else if (x.length() < 4 || (!x.substring(0,2).equalsIgnoreCase("Etc") && !x.endsWith("-etc"))) {
			kinTerm.setTheTerm(x+"-etc");
			textField1.setText(x+"-etc");
		}
	}
}
	
	
	// Used for addNotify check.
	boolean fComponentsAdjusted = false;
	//{{DECLARE_CONTROLS
	java.awt.Panel touchypanel;
	java.awt.Checkbox etcCheckbox;
	java.awt.Choice arrowSelect;
	java.awt.Checkbox generatorCheckbox;
	java.awt.Choice sexChoice;
	java.awt.Button button2;
	java.awt.TextField textField1;

	KintermEntryForm kinTerm=null;
Choice termChoices = null;

}

