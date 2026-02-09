 /* History
 * 2/18 DR deleted reference to algebra_4
 * 2/22 DR modified prodButton_ActionPerformed to match DoCayleyProductsandGraph procedure in Aopsops;
 * added reference to KintermFrame and added call to setLastCayley to implement algebra/kinterm radio button 
 * 2/24 DR added (generators.size()!=0)to prodButton_ActionPerformed; added explanation text
 * 3/2 DR added populateEquationPanel to prodButton_ActionPerformed;
 */

import java.awt.*;
import java.util.*;
import org.csac.io.*;
import java.awt.event.*;
import ice.htmlbrowser.*;


public class Kaes extends Frame
{
	public Kaes() {

		setLayout(null);
		setVisible(false);
		setSize(ScaleManager.s(862),ScaleManager.s(561));
		openFileDialog1 = new java.awt.FileDialog(this);
		openFileDialog1.setMode(FileDialog.LOAD);
		openFileDialog1.setTitle("Open");
		//$$ openFileDialog1.move(31,434);
		prodButton = new java.awt.Button();
		prodButton.setLabel("Products");
		prodButton.setBounds(ScaleManager.s(688),ScaleManager.s(82),ScaleManager.s(60),ScaleManager.s(23));
		add(prodButton);
		kaesOutField = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		kaesOutField.setBounds(ScaleManager.s(558),ScaleManager.s(164),ScaleManager.s(278),ScaleManager.s(375));
		kaesOutField.setFont(ScaleManager.font("SansSerif", Font.PLAIN, 12));
		add(kaesOutField);
		reduceButton = new java.awt.Button();
		reduceButton.setLabel("Reduce");
		reduceButton.setBounds(ScaleManager.s(376),ScaleManager.s(515),ScaleManager.s(60),ScaleManager.s(23));
		add(reduceButton);
		termPathField = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		termPathField.setBounds(ScaleManager.s(19),ScaleManager.s(476),ScaleManager.s(348),ScaleManager.s(66));
		add(termPathField);
		failButton = new java.awt.Button();
		failButton.setLabel("Fail");
		failButton.setBounds(ScaleManager.s(565),ScaleManager.s(127),ScaleManager.s(60),ScaleManager.s(23));
		add(failButton);
		succeedButton = new java.awt.Button();
		succeedButton.setLabel("Succeed");
		succeedButton.setBounds(ScaleManager.s(563),ScaleManager.s(88),ScaleManager.s(60),ScaleManager.s(23));
		add(succeedButton);
		testButton = new java.awt.Button();
		testButton.setLabel("Generate");
		testButton.setBounds(ScaleManager.s(561),ScaleManager.s(41),ScaleManager.s(60),ScaleManager.s(23));
		add(testButton);
		label1 = new java.awt.Label("Kaes",Label.CENTER);
		label1.setBounds(ScaleManager.s(190),ScaleManager.s(2),ScaleManager.s(169),ScaleManager.s(31));
		label1.setFont(ScaleManager.font("Serif", Font.BOLD, 24));
		add(label1);
		graphicPanel = new java.awt.Panel();
		graphicPanel.setLayout(new CardLayout(5,5));
		graphicPanel.setBounds(ScaleManager.s(40),ScaleManager.s(37),ScaleManager.s(509),ScaleManager.s(328));
		graphicPanel.setBackground(new Color(-1118482));
		add(graphicPanel);
		equationPanel1 = new EquationPanel();
		equationPanel1.setLayout(null);
		equationPanel1.setBounds(ScaleManager.s(5),ScaleManager.s(5),ScaleManager.s(499),ScaleManager.s(318));
		graphicPanel.add("card1", equationPanel1);
		((CardLayout) graphicPanel.getLayout()).show(graphicPanel,"card1");
		controlPanel = new java.awt.Panel();
		controlPanel.setLayout(null);
		controlPanel.setBounds(ScaleManager.s(10),ScaleManager.s(370),ScaleManager.s(542),ScaleManager.s(97));
		controlPanel.setBackground(new Color(-49));
		add(controlPanel);
		messagePanel = new java.awt.Panel();
		messagePanel.setLayout(null);
		messagePanel.setBounds(ScaleManager.s(280),ScaleManager.s(4),ScaleManager.s(264),ScaleManager.s(89));
		controlPanel.add(messagePanel);
		equationButton = new java.awt.Button();
		equationButton.setLabel("Enter Equation");
		equationButton.setBounds(ScaleManager.s(100),ScaleManager.s(56),ScaleManager.s(93),ScaleManager.s(28));
		messagePanel.add(equationButton);
		helpButton = new java.awt.Button();
		helpButton.setLabel("Help");
		helpButton.setBounds(ScaleManager.s(198),ScaleManager.s(57),ScaleManager.s(62),ScaleManager.s(27));
		messagePanel.add(helpButton);
		elementButton = new java.awt.Button();
		elementButton.setLabel("Enter Element");
		elementButton.setBounds(ScaleManager.s(4),ScaleManager.s(56),ScaleManager.s(93),ScaleManager.s(28));
		messagePanel.add(elementButton);
		messageText = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_NONE);
		messageText.setText("Message");
		messageText.setBounds(ScaleManager.s(5),ScaleManager.s(4),ScaleManager.s(256),ScaleManager.s(46));
		messagePanel.add(messageText);
		persistentPanel = new java.awt.Panel();
		persistentPanel.setLayout(null);
		persistentPanel.setBounds(ScaleManager.s(56),ScaleManager.s(3),ScaleManager.s(165),ScaleManager.s(90));
		controlPanel.add(persistentPanel);
		setTitle("Kaes Algebra Monitor");
		//}}
		
		//{{INIT_MENUS
		mainMenuBar = new java.awt.MenuBar();
		menu1 = new java.awt.Menu("File");
		miNew = new java.awt.MenuItem("New");
		miNew.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_N,false));
		menu1.add(miNew);
		miOpen = new java.awt.MenuItem("Open...");
		miOpen.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_O,false));
		menu1.add(miOpen);
		miSave = new java.awt.MenuItem("Save");
		miSave.setEnabled(false);
		miSave.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_S,false));
		menu1.add(miSave);
		miSaveAs = new java.awt.MenuItem("Save As...");
		miSaveAs.setEnabled(false);
		menu1.add(miSaveAs);
		menu1.addSeparator();
		miExit = new java.awt.MenuItem("Quit");
		miExit.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Q,false));
		menu1.add(miExit);
		mainMenuBar.add(menu1);
		menu2 = new java.awt.Menu("Edit");
		miCut = new java.awt.MenuItem("Cut");
		miCut.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_X,false));
		menu2.add(miCut);
		miCopy = new java.awt.MenuItem("Copy");
		miCopy.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_C,false));
		menu2.add(miCopy);
		miPaste = new java.awt.MenuItem("Paste");
		miPaste.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_V,false));
		menu2.add(miPaste);
		mainMenuBar.add(menu2);
		menu3 = new java.awt.Menu("Help");
		mainMenuBar.setHelpMenu(menu3);
		miAbout = new java.awt.MenuItem("About..");
		menu3.add(miAbout);
		menu3.addSeparator();
		helpMenuItem = new java.awt.MenuItem("Help");
		helpMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_H,false));
		menu3.add(helpMenuItem);
		messageHelpmenuItem = new java.awt.MenuItem("Message Help");
		messageHelpmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_M,false));
		menu3.add(messageHelpmenuItem);
		mainMenuBar.add(menu3);
		menu4 = new java.awt.Menu("Title");
		showMenuItem = new java.awt.MenuItem("Show");
		showMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_K,false));
		menu4.add(showMenuItem);
		menuItem2 = new java.awt.MenuItem("Item");
		menu4.add(menuItem2);
		mainMenuBar.add(menu4);
		setMenuBar(mainMenuBar);
		//$$ mainMenuBar.move(3,3);
		//}}
		
		//{{REGISTER_LISTENERS  
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		miOpen.addActionListener(lSymAction);
		miAbout.addActionListener(lSymAction);
		miExit.addActionListener(lSymAction);
		SymMouse aSymMouse = new SymMouse();
		elementButton.addActionListener(lSymAction);
		equationButton.addActionListener(lSymAction);
		helpButton.addActionListener(lSymAction);
		testButton.addActionListener(lSymAction);
		succeedButton.addActionListener(lSymAction);
		failButton.addActionListener(lSymAction);
		reduceButton.addActionListener(lSymAction);
		prodButton.addActionListener(lSymAction);
		helpMenuItem.addActionListener(lSymAction);
		messageHelpmenuItem.addActionListener(lSymAction);
		miNew.addActionListener(lSymAction);
		//}}
		new GlobalWindowManager().addWindow(this);
		GlobalWindowManager.setMainWindow(this);
		mainMenuBar.add(GlobalWindowManager.windowsMenu);
		//elementPanel1.setParent(this);
		// Algebra a = new Algebra();
		equationPanel1.setParent(this);
		equationPanel1.doDialog();

		new Message("Enter kin term map","Begin the analysis by either opening a kin term map"+
		" or constructing a kin term map","Analyze",33).addMessage();
		
	}
	
	Processor theProcessor = new Processor();
	
	//KinshipTerms kterms = new KinshipTerms();
	//MessageFrame mframe = new MessageFrame();
	ElementDialog eDialog = null; 
	
	public Kaes(String title)
	{
		this();
		setTitle(title);
	}

	
	public static void main(String args[])
	{
		Kaes k = new Kaes();
		k.setVisible(true);
		//(new Kaes()).setVisible(true);
	}
	

	
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			setLocation(50, 50);
		}	
		super.setVisible(b);
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
	Algebra algebraX = new Algebra();
	AlgebraPath pathX = new AlgebraPath();
	// Used for addNotify check.
	boolean fComponentsAdjusted = false;
	
	//{{DECLARE_CONTROLS
	java.awt.FileDialog openFileDialog1;
	java.awt.Button prodButton;
	java.awt.TextArea kaesOutField;
	java.awt.Button reduceButton;
	java.awt.TextArea termPathField;
	java.awt.Button failButton;
	java.awt.Button succeedButton;
	java.awt.Button testButton;
	java.awt.Label label1;
	java.awt.Panel graphicPanel;
	EquationPanel equationPanel1;
	java.awt.Panel controlPanel;
	java.awt.Panel messagePanel;
	java.awt.Button equationButton;
	java.awt.Button helpButton;
	java.awt.Button elementButton;
	java.awt.TextArea messageText;
	java.awt.Panel persistentPanel;
	//}}
	
	//{{DECLARE_MENUS
	java.awt.MenuBar mainMenuBar;
	java.awt.Menu menu1;
	java.awt.MenuItem miNew;
	java.awt.MenuItem miOpen;
	java.awt.MenuItem miSave;
	java.awt.MenuItem miSaveAs;
	java.awt.MenuItem miExit;
	java.awt.Menu menu2;
	java.awt.MenuItem miCut;
	java.awt.MenuItem miCopy;
	java.awt.MenuItem miPaste;
	java.awt.Menu menu3;
	java.awt.MenuItem miAbout;
	java.awt.MenuItem helpMenuItem;
	java.awt.MenuItem messageHelpmenuItem;
	java.awt.Menu menu4;
	java.awt.MenuItem showMenuItem;
	java.awt.MenuItem menuItem2;
	//}}
	
	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == Kaes.this)
				hide();
		}

		public void windowActivated(java.awt.event.WindowEvent event) {
			mainMenuBar.add(GlobalWindowManager.windowsMenu);
			updateMonitor();
			super.windowActivated(event);
		}
	}
	
	void Kaes_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);	// hide the Frame
		dispose();			// free the system resources
		System.exit(0);		// close the application
	}
	
	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == miOpen)
				miOpen_Action(event);
			else if (object == miAbout)
				miAbout_Action(event);
			else if (object == miExit)
				miExit_Action(event);
			else if (object == elementButton)
				elementButton_ActionPerformed(event);
			else if (object == equationButton)
				equationButton_ActionPerformed(event);
			else if (object == helpButton)
				helpButton_ActionPerformed(event);
			else if (object == testButton)
				testButton_ActionPerformed(event);
			else if (object == succeedButton)
				succeedButton_ActionPerformed(event);
			else if (object == failButton)
				failButton_ActionPerformed(event);
			else if (object == reduceButton)
				reduceButton_ActionPerformed(event);
			else if (object == prodButton)
				prodButton_ActionPerformed(event);
			else if (object == helpMenuItem)
				helpMenuItem_ActionPerformed(event);
			else if (object == messageHelpmenuItem)
				messageHelpmenuItem_ActionPerformed(event);
			else if (object == miNew)
				miNew_ActionPerformed(event);
				
		}
	}
	
	void miAbout_Action(java.awt.event.ActionEvent event)
	{
		//{{CONNECTION
		// Action from About Create and show as modal
		(new AboutDialog(this, true)).setVisible(true);
		//}}
	}
	
	void miExit_Action(java.awt.event.ActionEvent event)
	{
		//{{CONNECTION
		// Action from Exit Create and show as modal
		//(new QuitDialog(this, true)).setVisible(true);
		((java.awt.Window)GlobalWindowManager.getMainWindow()).dispatchEvent(new WindowEvent((java.awt.Window)GlobalWindowManager.getMainWindow(), WindowEvent.WINDOW_CLOSING));

		//}}
	}
	
	void miOpen_Action(java.awt.event.ActionEvent event)
	{
		KintermFrame x = new KintermFrame();
		x.kinshipTermsPanel1.loadKinData(x);
		x.setFromPreferences();
		if (x.kinshipTermsPanel1.xout != null) {
			String path = x.kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			MainFrame.prefs.put("LastFilename",path);
			MainFrame.prefs.savePrefs("KAESPrefs.xml");
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			x.setTitle(path);
			x.updatePreferences();
			GlobalWindowManager.changeWindowName(x);
		}
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
		}
	}

	/*public void paint(Graphics g) {
		g.drawRect(1,1,bounds().width-3, bounds().height-3);
	}*/
	

	void elementButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Set Kaes title
		eDialog.doDialog();
		//}}
	}

	void equationButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Set Kaes title
		setTitle("yyy");
		//}}
			 
		//{{CONNECTION
		// Create and show as modal
		(new EquationDialog(this, true)).show();
		//}}
	}

	void helpButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Set Kaes title
		setTitle("zzz");
		//}}
			 
		//{{CONNECTION
		// Create and show the Frame
		(new Frame1()).show();
		//}}
	}

	void elementDialog_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		if (event.getActionCommand().equals("OK")) {
			messageText.setText("OK\r"+eDialog.getElement());
		} else if (event.getActionCommand().equals("Cancel")) {
			messageText.setText("Cancelled\r");
		}
		//}}
	}

	void testButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Set Kaes title
		
	pathX.nextPath();

		//}}
	}

	void succeedButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Make Kaes resizable
		pathX.succeed();
		//}}
	}

	void failButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Make Kaes resizable
		pathX.fail();
		//}}
	}

	void reduceButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Select all of the text
	//	AlgebraPath p =  new AlgebraPath();
		AlgebraKludge ak = Algebra.getCurrent().theKludge;
		AlgebraSymbolVector av;
		// Debug.on();
		av = ak.preprocess(termPathField.getText());
		kaesOutField.appendText(XFile.Eol+"--------------------------------------------------------"+XFile.Eol);
		if (av == null) Debug.prout("Couldn't process a term in the input string!");
		else {
			kaesOutField.appendText("Pre-processed = "+av.toString()+XFile.Eol+XFile.Eol);
			if ((av = ak.process(av)).size() != 0) {
			
				kaesOutField.appendText("Processed = "+av.toString()+XFile.Eol+XFile.Eol);
				Vector g = ak.postprocess(av,Algebra.getCurrent().getElement("S"));
				for (int i = g.size()-1;i< g.size();i++) {
					kaesOutField.appendText("Post-processed = "+
								KinTermMap.theMap.trace((AlgebraSymbolVector) g.elementAt(i))+" "+
								g.elementAt(i).toString()+XFile.Eol+XFile.Eol);
					kaesOutField.appendText("The Path - "+KinTermMap.theMap.traceMapPath((AlgebraSymbolVector) g.elementAt(i)).toString()+XFile.Eol+XFile.Eol);
					
				}
			} else kaesOutField.appendText("Empty Path"+XFile.Eol+XFile.Eol);
		}
		
		//}}
	}
     
	void prodButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		
		AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
        if (av.size() != 0) {
		    SexMarkedModel3D md = new SexMarkedModel3D();
		    //Model3D md = new Model3D();
		    md.setLabelFlag(true);
		    CayleyTable x = new CayleyTable(av);
		    x.generateProducts();
		    equationPanel1.populateFields();
			CalcCoordinates cc = CalcCoordinatesFactory.getCalcCoordinates(x);
			cc.calcCoordinates();
			x.getProducts();
		    x.populateModel(MainFrame.kterms3d.threeDPanel.reset(md));

		    KintermFrame fr = (KintermFrame) GlobalWindowManager.getCurrentWindow();
            if (fr != null)	{
                fr.setLastCayley(x);
		        fr.populateEquationPanel();  
		    }
            equationPanel1.explanation.setText("A graph of the algebra has been constructed.  Open the Graph Window to view the graph.");
        }
        else equationPanel1.explanation.setText("No algebra has been constructed.  Begin constructing an algebra by entering a generator or an identity element.");
        

	//	Debug.prout(4,.toString());
		//}}
	}

	void updateMonitor()
	{
		// to do: code goes here.

		//{{CONNECTION

		AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
        if (av.size() != 0) {
	//		SexMarkedModel3D md = new SexMarkedModel3D();
			//Model3D md = new Model3D();
	//		md.setLabelFlag(true);
	//		CayleyTable x = new CayleyTable(av);
//			x.generateProducts();
			equationPanel1.populateFields();
	//		x.calcCoordinates();
	//		x.getProducts();
		}
	}
	
	HelpFrame help = new HelpFrame();
	HelpMessageFrame messageHelp = new HelpMessageFrame();
	void helpMenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Move Kaes to the back
		help.doHelp("Analyze");
		//}}
	}

	void messageHelpmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Make Kaes resizable
		messageHelp.doHelp("Analyze");
		//}}
	}

	void miNew_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		//{{CONNECTION
		// Make Kaes resizable
		new KintermFrame(theProcessor).setVisible(true);
		//}}
	}
}

