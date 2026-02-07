/*
	A basic extension of the java.awt.Frame class
 */

/* History
* 31/10 MF change to setFrameAlgebra to only set Algebra if it is different from the current Algebra
* 31/10 MF added call to KinshipTermsPanel.saveKinDataInNewFile in saveAsMenuitem_ActionPerformed
* 1/21/01 DR changed formalmenuItem to focaltermmenuItem; modified _ActionPerformed to write
* focal term to screen
* 2/23 DR added procedure "lastPanel ()"
* 2/25 DR added procedure "activateCurrentMapPanel(actcode)" to activate the map corresponding to the current algebra
* added mopsOps.reset(); after mainmenuBar.add(mopsOps.getTheMenu());
*3/1 DR moved populateGeneratorPanel()	and populateEquationPanel()back to KintermFrame from AopsOps
* 3/7 MF alterned popAndChangePanel to remove reference to Algebra.popCurrent()
*        this is where we will address adapting to the panelAlgebra feature.
* 3/7 MF removed reference to Algebra.popCurrent in setFrameAlgebra()
* 6/29 DR corrected error in pushAndChangePanel procedure; replaced pushPanel(kinshipTermsPanel1) with pushPanel(k);
* 6/29 added test for empty stack to popAndChangePanel; modified popAndChangePanel to display the next panel, not the popped panel
* 11/22 DR modified output of populateGeneratorPanel, allows for transferKinInfoVector or
* kinTermMap to be parameter values
* 8/23 DR modified populateGeneratorPanel to display algebra sex generators on screen
* 8/24 DR modified populateGeneratorPanel to use generators, generatingTerms when possible
*/

//import ThreeD;
import java.awt.*;
import java.awt.event.*;
import java.awt.PageAttributes;
import java.awt.print.*;

public class KintermFrame extends Frame implements Printable
{
	Preferences docPreferences = null;
	public void setPreference(String name, String value) {
		if (docPreferences == null) docPreferences = new Preferences();
		docPreferences.putPreference(name,value);
	}

	public void setFromPreferences() {
		if (docPreferences != null) {
			int wx = docPreferences.getInt("WindowX");
			int wy = docPreferences.getInt("WindowY");
			int ww = docPreferences.getInt("WindowWidth");
			int wh = docPreferences.getInt("WindowHeight");
			if (wx != -1 && wy != -1 && ww != -1 && wh != -1) {
				// Enforce minimum size based on scaled layout so that
				// the algebraControlPanel, ThreeD graph, text areas and
				// scroll pane are all visible at the current scale factor.
				int minW = ScaleManager.s(976);
				int minH = ScaleManager.s(600);
				if (ww < minW) ww = minW;
				if (wh < minH) wh = minH;
				setBounds(wx,wy,ww,wh);
			}
			String title = docPreferences.getString("WindowTitle");
			if (title != null) setTitle(title);
			int sx = docPreferences.getInt("ScrollX");
			int sy = docPreferences.getInt("ScrollY");
			if (sx != -1 && sy != -1) scrollKin.setScrollPosition(sx,sy);
		}
	}

	public void updatePreferences() {
		if (docPreferences == null) docPreferences = new Preferences();
		docPreferences.putPreference("WindowX",getLocation().x);
		docPreferences.putPreference("WindowY",getLocation().y);
		docPreferences.putPreference("WindowWidth",getSize().width);
		docPreferences.putPreference("WindowHeight",getSize().height);
		docPreferences.putPreference("ScrollX",scrollKin.getScrollPosition().x);
		docPreferences.putPreference("ScrollY",scrollKin.getScrollPosition().y);
		docPreferences.putPreference("WindowTitle",getTitle());
		docPreferences.putPreference("ScaleFactor",ScaleManager.getScaleFactor());
	}

	public KintermFrame()
	{

		setLayout(null);
		setVisible(false);
		setBounds(ScaleManager.s(20),ScaleManager.s(30),ScaleManager.s(976),ScaleManager.s(600));
		// setBackground(new Color(0));

		// Set up the top panel which contains information relating to algebra
		algebraControlPanel = new java.awt.Panel();
		algebraControlPanel.setLayout(null);
		algebraControlPanel.setBounds(ScaleManager.s(1),ScaleManager.s(2),ScaleManager.s(971),ScaleManager.s(181));
		algebraControlPanel.setFont(ScaleManager.font("Serif", Font.PLAIN, 10));
		algebraControlPanel.setBackground(new Color(-2363));
		add(algebraControlPanel);

		equationTextArea = new CheckboxPanel(CheckboxPanel.SCROLLBARS_AS_NEEDED);
		equationTextArea.setEditable(false);
		equationTextArea.setBounds(ScaleManager.s(205),ScaleManager.s(20),ScaleManager.s(190),ScaleManager.s(158));
		equationTextArea.setBackground(new Color(-462101));

		dialogueTextArea = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		dialogueTextArea.setEditable(false);
		dialogueTextArea.setBounds(ScaleManager.s(403),ScaleManager.s(20),ScaleManager.s(270),ScaleManager.s(90));
		dialogueTextArea.setBackground(new Color(-462101));

		errorTextArea = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		errorTextArea.setEditable(false);
		errorTextArea.setBounds(ScaleManager.s(403),ScaleManager.s(125),ScaleManager.s(270),ScaleManager.s(53));
		errorTextArea.setBackground(new Color(-462101));

		algebraControlPanel.add(equationTextArea);
		algebraControlPanel.add(dialogueTextArea);
		algebraControlPanel.add(errorTextArea);

		generatorTextArea = new java.awt.TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		generatorTextArea.setEditable(false);
		generatorTextArea.setBounds(ScaleManager.s(7),ScaleManager.s(20),ScaleManager.s(190),ScaleManager.s(158));
		generatorTextArea.setBackground(new Color(-331533));
		algebraControlPanel.add(generatorTextArea);

		threeD1 = new ThreeD();
		threeD1.setLayout(null);
		threeD1.setBounds(ScaleManager.s(699),ScaleManager.s(0),ScaleManager.s(258),ScaleManager.s(179));
		threeD1.setFont(ScaleManager.font("SansSerif", Font.PLAIN, 9));
		threeD1.setBackground(new Color(-2490404));
		threeD1.offset(true);
		algebraControlPanel.add(threeD1);
		label2 = new java.awt.Label("Generators",Label.CENTER);
		label2.setBounds(ScaleManager.s(34),ScaleManager.s(1),ScaleManager.s(132),ScaleManager.s(17));
		label2.setFont(ScaleManager.font("Serif", Font.BOLD, 13));
		algebraControlPanel.add(label2);
		label2.setEnabled(false);
		label3 = new java.awt.Label("Equations",Label.CENTER);
		label3.setBounds(ScaleManager.s(242),ScaleManager.s(3),ScaleManager.s(132),ScaleManager.s(17));
		label3.setFont(ScaleManager.font("Serif", Font.BOLD, 13));
		algebraControlPanel.add(label3);
		label3.setEnabled(false);

		label4 = new java.awt.Label("Dialogue",Label.CENTER);
		label4.setBounds(ScaleManager.s(450),ScaleManager.s(3),ScaleManager.s(132),ScaleManager.s(17));
		label4.setFont(ScaleManager.font("Serif", Font.BOLD, 13));
		algebraControlPanel.add(label4);
		label4.setEnabled(false);

		label5 = new java.awt.Label("Diagnostics",Label.CENTER);
		label5.setBounds(ScaleManager.s(450),ScaleManager.s(110),ScaleManager.s(132),ScaleManager.s(17));
		label5.setFont(ScaleManager.font("Serif", Font.BOLD, 13));
		algebraControlPanel.add(label5);
		label5.setEnabled(false);

		// Now set up the kinshipTermsPanel
		scrollKin = new ScrollPane();
		add(scrollKin);
		scrollKin.setBounds(0,ScaleManager.s(185),getSize().width,getSize().height-ScaleManager.s(208));
		scrollKin.getHAdjustable().setUnitIncrement(10);
		scrollKin.getVAdjustable().setUnitIncrement(10);
		kinshipTermsPanel1 = new KinshipTermsPanel();
		kinshipTermsPanel1.setLayout(null);
		kinshipTermsPanel1.setBounds(0,0,ScaleManager.s(1600),ScaleManager.s(1600));
		// kinshipTermsPanel1.setOrigin(0,0);
		kinshipTermsPanel1.setBackground(new Color(16777215));

		scrollKin.add(kinshipTermsPanel1);
		scrollKin.setScrollPosition(400,400);
		scrollKin.layout();
		setTitle("Kinship Terms");
		//}}

		//{{INIT_MENUS
		/*
		mainmenuBar = new java.awt.MenuBar();
		cayleymenu = new java.awt.Menu("Cayley Table");
		algmenuItem= new java.awt.MenuItem("with Algebra Element Labels");
		cayleymenu.add(algmenuItem);
		kinmenuItem = new java.awt.MenuItem("with Kin Term Labels");
		cayleymenu.add(kinmenuItem);
		sentencemenuItem = new java.awt.MenuItem("Sentence Format");
		cayleymenu.add(sentencemenuItem);
		kintypemenu = new java.awt.Menu("Kin Type Products");
		tablemenuItem = new java.awt.MenuItem("Table Format");
		kintypemenu.add(tablemenuItem);
		tableNmenuItem = new java.awt.MenuItem("   Table Format: Ego");
		kintypemenu.add(tableNmenuItem);
		tableMmenuItem = new java.awt.MenuItem("   Table Format: Male Ego");
		kintypemenu.add(tableMmenuItem);
		tableFmenuItem = new java.awt.MenuItem("   Table Format: Female Ego");
		kintypemenu.add(tableFmenuItem);
		gridmenuItem = new java.awt.MenuItem("Genealogical Grid");
		kintypemenu.add(gridmenuItem);
		gridNmenuItem = new java.awt.MenuItem("   Genealogical Grid: Ego");
		kintypemenu.add(gridNmenuItem);
		gridMmenuItem = new java.awt.MenuItem("   Genealogical Grid: Male Ego");
		kintypemenu.add(gridMmenuItem);
		gridFmenuItem = new java.awt.MenuItem("   Genealogical Grid: Female Ego");
		kintypemenu.add(gridFmenuItem);
		filemenu = new java.awt.Menu("File");
		newmenuItem = new java.awt.MenuItem("New");
		newmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_N,false));
		filemenu.add(newmenuItem);
		openmenuItem1 = new java.awt.MenuItem("Open...");
		openmenuItem1.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_O,false));
		filemenu.add(openmenuItem1);
		savemenuItem = new java.awt.MenuItem("Save");
		savemenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_S,false));
		filemenu.add(savemenuItem);
		saveasmenuItem = new java.awt.MenuItem("Save As...");
		saveasmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_S,true));
		filemenu.add(saveasmenuItem);
		filemenu.addSeparator();
		savecayleymenuItem = new java.awt.MenuItem("Export CayleyTable");
		savecayleymenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_C,true));
		filemenu.add(savecayleymenuItem);
		filemenu.addSeparator();
		prefsmenuItem = new java.awt.MenuItem("Preferences...");
		prefsmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_COMMA,false));
		filemenu.add(prefsmenuItem);
		quitmenuItem = new java.awt.MenuItem("Quit");
		quitmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Q,false));
		filemenu.add(quitmenuItem);
		mainmenuBar.add(filemenu);
		editMenu = new java.awt.Menu("Edit");
		menuItem2 = new java.awt.MenuItem("Undo");
		menuItem2.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Z,false));
		editMenu.add(menuItem2);
		editMenu.addSeparator();
		cutmenuItem = new java.awt.MenuItem("Cut");
		editMenu.add(cutmenuItem);
		copymenuItem = new java.awt.MenuItem("Copy");
		editMenu.add(copymenuItem);
		pastmenuItem = new java.awt.MenuItem("Paste");
		editMenu.add(pastmenuItem);
		// MF 23/11/01 Added clearmenuitem for delete function	#e100
		editMenu.addSeparator();

		clearmenuItem = new java.awt.MenuItem("Clear");
		editMenu.add(clearmenuItem);

		// MF End Change #e100

		mainmenuBar.add(editMenu);
		operationsmenu = new java.awt.Menu("Operations");
		newStructuremenuItem = new java.awt.MenuItem("New Structure");
		newStructuremenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_N,true));
		operationsmenu.add(newStructuremenuItem);
		kintermTablemenuItem = new java.awt.MenuItem("Kinterm Table");
		kintermTablemenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_I,true));
		operationsmenu.add(kintermTablemenuItem);
		focaltermmenuItem = new java.awt.MenuItem("Find Focal Terms");
		focaltermmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_F,false));
		operationsmenu.add(focaltermmenuItem);
		newtermmenuItem = new java.awt.MenuItem("New Term");
		newtermmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_T,false));
		operationsmenu.add(newtermmenuItem);
		popPanelMenuItem = new java.awt.MenuItem("Pop Panel");
		popPanelMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_P,true));
		operationsmenu.add(popPanelMenuItem);
		//mopsMenuItem1 = new java.awt.MenuItem("Mops1");
		//mopsMenuItem1.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_1,false));
		//operationsmenu.add(mopsMenuItem1);
 		//cayleymenuItem = new java.awt.MenuItem("Algebra Cayley Table");
		//cayleymenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_2,false));
		//operationsmenu.add(cayleymenuItem);
                operationsmenu.add(cayleymenu);
                operationsmenu.add(kintypemenu);
		//aop2menuItem = new java.awt.MenuItem("Null2");
		//aop2menuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_3,false));
		//operationsmenu.add(aop2menuItem);
		resumemenuItem = new java.awt.MenuItem("Resume");
		resumemenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_4,false));
		operationsmenu.add(resumemenuItem);
		mainmenuBar.add(operationsmenu);
		modeMenu = new java.awt.Menu("Mode");
		automenuItem = new java.awt.CheckboxMenuItem("Automatic");
		automenuItem.setState(true);
		modeMenu.add(automenuItem);
		manualmenuItem = new java.awt.CheckboxMenuItem("Manual");
		manualmenuItem.setState(false);
		modeMenu.add(manualmenuItem);
		tracemenuItem = new java.awt.CheckboxMenuItem("Trace");
		tracemenuItem.setState(false);
		modeMenu.add(tracemenuItem);
		mainmenuBar.add(modeMenu);
		*/
		makeMenus = new MakeMenus();
		makeMenus.operationsmenu.setEnabled(true);
		makeMenus.modeMenu.setEnabled(true);

		setMenuBar(makeMenus.mainmenuBar);
		
		//$$ mainmenuBar.move(0,0);
		//}}

		//{{REGISTER_LISTENERS
		SymAction lSymAction = new SymAction();
		SymItem lSymItem = new SymItem();

		makeMenus.setListeners(lSymAction, lSymItem);
		SymMouse aMouse = new SymMouse();
		errorTextArea.addMouseListener(aMouse);
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymComponent aSymComponent = new SymComponent();
		this.addComponentListener(aSymComponent);
/*
		SymAction lSymAction = new SymAction();

		equationTextArea.addActionListener(lSymAction); // trial

		openmenuItem1.addActionListener(lSymAction);
		saveasmenuItem.addActionListener(lSymAction);
		savecayleymenuItem.addActionListener(lSymAction);
		savemenuItem.addActionListener(lSymAction);
		newmenuItem.addActionListener(lSymAction);
		prefsmenuItem.addActionListener(lSymAction);
		quitmenuItem.addActionListener(lSymAction);

// 22/11/01 MF following for clear action and cut/paste stuff  #e100
		clearmenuItem.addActionListener(lSymAction);
		cutmenuItem.addActionListener(lSymAction);
		copymenuItem.addActionListener(lSymAction);
		pastmenuItem.addActionListener(lSymAction);
		menuItem2.addActionListener(lSymAction);
//

		newStructuremenuItem.addActionListener(lSymAction);
		kintermTablemenuItem.addActionListener(lSymAction);
		focaltermmenuItem.addActionListener(lSymAction);
		newtermmenuItem.addActionListener(lSymAction);
		popPanelMenuItem.addActionListener(lSymAction);
		//mopsMenuItem1.addActionListener(lSymAction);
		automenuItem.addActionListener(lSymAction);
		manualmenuItem.addActionListener(lSymAction);
		tracemenuItem.addActionListener(lSymAction);
		resumemenuItem.addActionListener(lSymAction);
		algmenuItem.addActionListener(lSymAction);
		kinmenuItem.addActionListener(lSymAction);
		tablemenuItem.addActionListener(lSymAction);
		tableNmenuItem.addActionListener(lSymAction);
		tableMmenuItem.addActionListener(lSymAction);
		tableFmenuItem.addActionListener(lSymAction);
		sentencemenuItem.addActionListener(lSymAction);
		gridmenuItem.addActionListener(lSymAction);
		gridNmenuItem.addActionListener(lSymAction);
		gridFmenuItem.addActionListener(lSymAction);
		gridMmenuItem.addActionListener(lSymAction);
		//cayleymenuItem.addActionListener(lSymAction);
		//aop2menuItem.addActionListener(lSymAction);
		//}}
		automenuItem.removeActionListener(lSymAction);
		manualmenuItem.removeActionListener(lSymAction);
		tracemenuItem.removeActionListener(lSymAction);

		SymItem lSymItem = new SymItem();
		automenuItem.addItemListener(lSymItem);
		manualmenuItem.addItemListener(lSymItem);
		tracemenuItem.addItemListener(lSymItem);
*/
		setTitle(getTitle()+"-"+document_number++);

		GlobalWindowManager.addWindow(this);
//		insertInstructionMenuMops();
		// insertInstructionMenu(aopsOps);
		makeMenus.mainmenuBar.add(mopsOps.getTheMenu());
		mopsOps.reset();
		makeMenus.mainmenuBar.add(aopsOps.getTheMenu());
		//mainmenuBar.add(aopsProg.getTheMenu());
		makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
		threeD1.init();
		threeD1.start();
		Mode.setMode(Mode.AUTOMATIC);
	// initializes the panels stack to include the initial panel;
		if (cFrame == null) {
			cFrame = new KintermTextEntryForm();
			//cFrame.setSize(830,620);
			//cFrame.setTitle("Kinterm Table");
			GlobalWindowManager.addWindow(cFrame);
		}
		
        pushPanel(kinshipTermsPanel1);
	}

	
	public KintermFrame(String title)
	{
		this();
		setTitle(title);
	}

	public KintermFrame(Processor processor)
	{
		this();
		setProcessor(processor);
	}
	Menu [] extraMenus = new Menu [12];

	static public void main(String args[])
	{
		(new KintermFrame()).setVisible(true);
	}


	MopsOps mopsOps = new MopsOps(this);
	AopsOps aopsOps = new AopsOps(this);
	//AopsProg aopsProg = new AopsProg(this);
	
	GenealogicalGrid ggN = new GenealogicalGrid();
	GenealogicalGrid ggM = new GenealogicalGrid();
	GenealogicalGrid ggF = new GenealogicalGrid();
	GenealogicalGrid xgg = ggN;
	
	//MapProperties mp = new MapProperties();

	public GenealogicalGrid getGenealogicalGrid(String sex) {
		if (sex.equals("M")) {
			xgg = ggM;
			return ggM;
		} else if (sex.equals("F")) {
			xgg = ggF;
			return ggF;
		} else if (sex.equals("N")) {
			xgg = ggN;
			return ggN;
		} else {
			return null;
		}
	}
	
	public GenealogicalGrid getGenealogicalGrid(){
		return xgg;
	}

	public void setGenealogicalGrid(String sex) {
		if (sex.equals("M")) {
			xgg = ggM;
			ggM.setEgoSex("M");
		} else if (sex.equals("F")) {
			xgg = ggF;
			ggF.setEgoSex("F");
		} else if (sex.equals("N")) {
			xgg = ggN;
			ggN.setEgoSex("N");
		} else {
			xgg = ggN;
			ggN.setEgoSex("N");
		}
	}

	public BuiltinOperations getAlgebraBuiltins(){
	 return algebraBuiltins;
	}

	public void setAlgebraBuiltins(BuiltinOperations algebraBuiltins){
	 this.algebraBuiltins = algebraBuiltins;
	}

	static int document_number=1;

	BuiltinOperations algebraBuiltins = aopsOps;

	BuiltinOperations currentBuiltins = mopsOps; //mops1;
    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			//mainmenuBar.add(GlobalWindowManager.windowsMenu);

			// setLocation(50, 50);
		}
		super.setVisible(b);
	}

	public void toFront() {
	//	mainmenuBar.add(GlobalWindowManager.windowsMenu);
		super.toFront();
	}

	public synchronized void dispose() {
		GlobalWindowManager.removeWindow(this);
		super.dispose();
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

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;

	//{{DECLARE_CONTROLS
	java.awt.Panel algebraControlPanel;
	CheckboxPanel equationTextArea;
	java.awt.TextArea dialogueTextArea;
	java.awt.TextArea errorTextArea;

	java.awt.TextArea generatorTextArea;
	ThreeD threeD1;
	java.awt.Label label2;
	java.awt.Label label3;
	java.awt.Label label4;
	java.awt.Label label5;

	KinshipTermsPanel kinshipTermsPanel1;
	//}}

	//{{DECLARE_MENUS
	MakeMenus makeMenus;
	PrintManager printManager=new PrintManager(this);
	
	java.awt.MenuBar mainmenuBar;
/*
	java.awt.Menu cayleymenu;
	java.awt.Menu kintypemenu;
	java.awt.MenuItem algmenuItem;
	java.awt.MenuItem kinmenuItem;
	java.awt.MenuItem tablemenuItem;
	java.awt.MenuItem tableNmenuItem;
	java.awt.MenuItem tableMmenuItem;
	java.awt.MenuItem tableFmenuItem;
	java.awt.MenuItem gridmenuItem;
	java.awt.MenuItem gridNmenuItem;
	java.awt.MenuItem gridFmenuItem;
	java.awt.MenuItem gridMmenuItem;
	java.awt.MenuItem sentencemenuItem;
	java.awt.Menu filemenu;
	java.awt.MenuItem newmenuItem;
	java.awt.MenuItem openmenuItem1;
	java.awt.MenuItem savemenuItem;
	java.awt.MenuItem saveasmenuItem;
	java.awt.MenuItem savecayleymenuItem;
	java.awt.MenuItem quitmenuItem;
	java.awt.MenuItem prefsmenuItem;
	java.awt.Menu editMenu;
	java.awt.MenuItem menuItem2;
	java.awt.MenuItem cutmenuItem;
	java.awt.MenuItem copymenuItem;
	java.awt.MenuItem pastmenuItem;

	// 22/11/01 MF adding edit menu items  #e100
	java.awt.MenuItem clearmenuItem;
	// End #e100


	java.awt.Menu operationsmenu;
	java.awt.MenuItem newStructuremenuItem;
	java.awt.MenuItem kintermTablemenuItem;
	java.awt.MenuItem focaltermmenuItem;
	java.awt.MenuItem newtermmenuItem;
	java.awt.MenuItem popPanelMenuItem;
	//java.awt.MenuItem mopsMenuItem1;
	//java.awt.MenuItem cayleymenuItem;
	java.awt.MenuItem aop2menuItem;
	java.awt.MenuItem resumemenuItem;
	java.awt.Menu modeMenu;
	java.awt.CheckboxMenuItem automenuItem;
	java.awt.CheckboxMenuItem manualmenuItem;
	java.awt.CheckboxMenuItem tracemenuItem;
 */
	//}}
	ScrollPane scrollKin=null;
    ListVector panels = new ListVector();
	 ListVector panelsoff = new ListVector();
    ListVector maps = new ListVector();

    KinTermMap pushMap (KinTermMap m){
        maps.addElement(m);
        return m;
    }

    KinTermMap popMap (){
        KinTermMap m = (KinTermMap) maps.lastElement();
        maps.removeElementAt(maps.size()-1);
        return m;
    }

    KinshipTermsPanel pushPanel (KinshipTermsPanel m){
        panels.addElement(m);
        return m;
    }

    KinshipTermsPanel popPanel (){
        KinshipTermsPanel m = (KinshipTermsPanel) panels.lastElement();
        panels.removeElementAt(panels.size()-1);
		  panelsoff.addElement(m);
        return m;
    }

	KinshipTermsPanel previousPanel (){
	   return previousPanel(kinshipTermsPanel1);
	}

  	KinshipTermsPanel previousPanel (KinshipTermsPanel k){
		if (panels.size() < 2) return k;
	   else {
		  int c = panels.indexOf(k);
		  if (c <= 0) return k;
		  return (KinshipTermsPanel) panels.elementAt(c-1);
	   }
    }

   	KinshipTermsPanel lastPanel (){
        return (KinshipTermsPanel) panels.lastElement();
    }

  	KinshipTermsPanel firstPanel (){
        return (KinshipTermsPanel) panels.elementAt(0);
    }

    void changePanel(KinshipTermsPanel p){
		scrollKin.hide();
        kinshipTermsPanel1.hide();
        scrollKin.remove(kinshipTermsPanel1);
        scrollKin.add(p);
      //  p.setSize(kinshipTermsPanel1.getSize());
      //  p.setLocation(kinshipTermsPanel1.getLocation());
        kinshipTermsPanel1 = p;
 		scrollKin.show();
       p.show();
	   p.updateKintable();
    }

    void popAndChangePanel() {
        if (panels.size() > 1) {
            popPanel();
//            changePanel(popPanel()); // here is where we would do the panel algebra
            changePanel(lastPanel());
        }
      //  Algebra.popCurrent(); // Quasi functional use -- ignoring
    }

    void pushAndChangePanel(KinshipTermsPanel k) {
    	//pushPanel(kinshipTermsPanel1);
    	pushPanel(k);
        changePanel(k);
    }

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == KintermFrame.this)
				KintermFrame_WindowClosing(event);
		}

		public void windowActivated(java.awt.event.WindowEvent event) {
			makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
			GlobalWindowManager.setCurrentWindow(KintermFrame.this);
			setFrameAlgebra(getFrameAlgebra());
			makeMenus.operationsmenu.setEnabled(true);
			makeMenus.modeMenu.setEnabled(true);

/*			Debug.prout(4,"Trying in WindowAdapter");
			if (scrollKin != null) {
				scrollKin.setSize(getSize().width,getSize().height-208);
				scrollKin.layout();
				//layout();
			}*/
			super.windowActivated(event);
		}
	}

	class SymComponent extends java.awt.event.ComponentAdapter
	{
		public void componentResized(java.awt.event.ComponentEvent event)
		{
			Object object = event.getSource();
			if (object == KintermFrame.this) {
			//	Debug.prout(4,"Trying in ComponentAdapter");
				if (scrollKin != null) {
					scrollKin.setSize(getSize().width,getSize().height-ScaleManager.s(207));
					scrollKin.layout();
					//layout();
				}
				super.componentResized(event);
			}
		}
	}

	void KintermFrame_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);	// hide the Frame
	//	dispose();			// free the system resources
	//	System.exit(0);		// close the application
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
/*		public void mouseClicked(java.awt.event.MouseEvent event) {
			Object object = event.getSource();
			if (object == errorTextArea)
				GlobalWindowManager.getFrame("Messages").toFront();
		}*/
		public void mouseReleased(java.awt.event.MouseEvent event) {
			Object object = event.getSource();
			if (object == errorTextArea) {
				GlobalWindowManager.getFrame("Messages").show();
				GlobalWindowManager.getFrame("Messages").toFront();
			}
		}
/*		public void mouseReleased(java.awt.event.MouseEvent event) {
			Object object = event.getSource();
			if (object == errorTextArea)
				GlobalWindowManager.getFrame("Messages").toFront();
		}*/
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == makeMenus.openmenuItem1)
				openmenuItem_ActionPerformed(event);
			else if (object == makeMenus.saveasmenuItem)
				saveasmenuItem_ActionPerformed(event);
			else if (object == makeMenus.savemenuItem)
				savemenuItem_ActionPerformed(event);
			else if (object == makeMenus.newmenuItem)
				newmenuItem_ActionPerformed(event);
			else if (object == makeMenus.quitmenuItem)
				quitmenuItem_ActionPerformed(event);
			else if (object == makeMenus.prefsmenuItem)
				prefsmenuItem_ActionPerformed(event);
			else if (object == makeMenus.savecayleymenuItem)
			  savecayleymenuItem_ActionPerformed(event);
			else if (object == makeMenus.printmenuItem)
				printManager.setupPrintJob();
			else if (object == makeMenus.pagesetupmenuItem)
				printManager.pageSetup();
	// 22/11/01 MF adding edit menu items  #e100
			else if (object == makeMenus.menuItem2)
			{} // 	undomenuItem_ActionPerformed(event);
			else if (object == makeMenus.copymenuItem)
			{} // 	copymenuItem_ActionPerformed(event);
			else if (object == makeMenus.cutmenuItem)
			{} //	cutmenuItem_ActionPerformed(event);
			else if (object == makeMenus.pastmenuItem)
			{} // 	pastmenuItem_ActionPerformed(event);
			else if (object == makeMenus.clearmenuItem) {
				clearmenuItem_ActionPerformed(event);
	// End #e100

			} else if (object == makeMenus.newStructuremenuItem)
				newStructuremenuItem_ActionPerformed(event);            
			else if (object == makeMenus.kintermTablemenuItem) {
				kintermTablemenuItem_ActionPerformed(event);  
			} else if (object == makeMenus.newtermmenuItem)
				newtermmenuItem_ActionPerformed(event);
			else if (object == makeMenus.focaltermmenuItem)
			    focaltermmenuItem_ActionPerformed(event);
			else if (object == makeMenus.popPanelMenuItem)
				popPanelMenuItem_ActionPerformed(event);
		//	else if (object == mopsMenuItem1)
			//	mopsMenuItem1_ActionPerformed(event);
			else if (object == makeMenus.automenuItem){}
			else if (object == makeMenus.manualmenuItem){}
			else if (object == makeMenus.tracemenuItem){}
			else if (object == makeMenus.resumemenuItem)
				resumemenuItem_ActionPerformed(event);        
			else if (object == makeMenus.algmenuItem) {
				algmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.kinmenuItem) {
				kinmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.tablemenuItem) {
				tablemenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.tableNmenuItem) {
				tableNmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.tableMmenuItem) {
				tableMmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.tableFmenuItem) {
				tableFmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.sentencemenuItem) {
				sentencemenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.gridmenuItem) {
				gridmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.gridNmenuItem) {
				gridNmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.gridMmenuItem) {
				gridMmenuItem_ActionPerformed(event);
			}
			else if (object == makeMenus.gridFmenuItem) {
				gridFmenuItem_ActionPerformed(event);
			}
			else if (object == equationTextArea) {
			   equationTextArea_ActionPerformed(event);
			}

		}
	}

	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event) // fix so that check remains and only current is checked
		{
			Object object = event.getSource();
			if (object == makeMenus.automenuItem)
				automenuItem_ActionPerformed(event);
			else if (object == makeMenus.manualmenuItem)
				manualmenuItem_ActionPerformed(event);
			else if (object == makeMenus.tracemenuItem)
				tracemenuItem_ActionPerformed(event);
		}
	}

	public int printWindow(Graphics g, PageFormat pf, int pageIndex) {
		// return printManager.print(g, pf, pageIndex);
		if (pageIndex > 0) return printManager.postPrint(false);
		Rectangle r = kinshipTermsPanel1.getActiveBox();
		Dimension d = new Dimension(r.width,r.height);
		printManager.setDrawObject(kinshipTermsPanel1,d);
		printManager.prePrint(g, pf, pageIndex,true);
		kinshipTermsPanel1.paint(g);
		return printManager.postPrint(true);
	}

	public int print(Graphics g, PageFormat pf, int pageIndex) {
		// return printManager.print(g, pf, pageIndex);
		if (pageIndex > 0) return printManager.postPrint(false);
		Rectangle r1 = algebraControlPanel.getBounds();
		Dimension d1 = new Dimension(r1.width,r1.height);
		printManager.setDrawObject(algebraControlPanel,d1);
		printManager.prePrint(g, pf, pageIndex,true);
		int offset0 = printManager.printString("Generators",generatorTextArea.getLocation().x,generatorTextArea.getLocation().y-20,g);
		Rectangle edb = generatorTextArea.getBounds();
		edb.height += generatorTextArea.getSize().height+40;
		int offset1 = printManager.printTextArea( g, generatorTextArea,edb);
		int offset0b = printManager.printString("Diagnostics",errorTextArea.getLocation().x,dialogueTextArea.getLocation().y - 20,g);
		Rectangle edg = dialogueTextArea.getBounds();
		edg.height += errorTextArea.getSize().height+40;
		int offset4 = printManager.printTextArea( g, errorTextArea,edg);
//		int offsetk = (offset0b) - dialogueTextArea.getSize().height;
//		if (offsetk < 0) offsetk = 0;
		// System.out.print(offsetk);
		//g.translate(0,errorTextArea.getLocation().y);
		//g.translate(0,-errorTextArea.getLocation().y+offsetk);
	//	g.translate(equationTextArea.getLocation().x,0);
		printManager.printString("Equations",equationTextArea.getLocation().x,equationTextArea.getLocation().y - 20,g);
		g.setFont(equationTextArea.getFont());
		int offset_e = printManager.printComponentsText(g, equationTextArea.getFont(),equationTextArea);
		// int offset_e=printManager.printPages(g,pages,errorTextArea.getFont());
		//int offset_e=printManager.printComponents(g,equationTextArea);
	//	g.translate(-equationTextArea.getLocation().x,0);
		g.translate(threeD1.getLocation().x,threeD1.getLocation().y);
		threeD1.paint(g);
		
		g.translate(-threeD1.getLocation().x,-threeD1.getLocation().y);
		int offsetk = offset1;
		if (offset4 > offset1) offsetk = offset4;
		if (offset_e > offsetk) offsetk = offset_e;
		// if (equationTextArea.getLocation().y + equationTextArea.getSize().height > offsetk) offsetk = equationTextArea.getLocation().y + equationTextArea.getSize().height;
		// offsetk += 20;
		
		g.setColor(dialogueTextArea.getForeground());
		int offset0a = printManager.printString("Dialogue",dialogueTextArea.getLocation().x,dialogueTextArea.getLocation().y-20+offsetk,g);
		Rectangle xx = dialogueTextArea.getBounds();
		xx.height *= 4;
		xx.translate(-dialogueTextArea.getLocation().x,offsetk);
		int offset3 = printManager.printTextArea( g, dialogueTextArea, xx);

		g.translate(0,offset3+30);
		
		Rectangle r = kinshipTermsPanel1.getActiveBox();
		r.width += 15;
		Dimension d = new Dimension(r.width,r.height);
		printManager.setDrawObject(kinshipTermsPanel1,d);
	//	printManager.prePrint(g, pf, pageIndex,true);
		kinshipTermsPanel1.paint(g);
		return printManager.postPrint(true);
	}
	// 	public void printTextArea(PrintJob pjob, Graphics pg, TextArea ta, int start) {

	/*
	 
	public int print(Graphics g, PageFormat pf, int pageIndex) {
		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double x = pf.getImageableWidth();
		double y = pf.getImageableHeight();
		Dimension d = getSize();
		double sx = x / d.width;
		double sy = y / d.height;
		if (sy < sx) sx = sy;
		else sy = sx;
		g2d.scale(sx,sy);
		this.printAll(g2d);
		return Printable.PAGE_EXISTS;
	}
	
	Object hasDonePageSetup = null;
	
	void pageSetupMenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
		PageFormat pf;
		PrinterJob pjob = PrinterJob.getPrinterJob();
		if (hasDonePageSetup == null) {
			pf = pjob.defaultPage();
		} else 
			pf = (PageFormat) hasDonePageSetup;
		
		pf = pjob.pageDialog(pf);
		hasDonePageSetup = pf;
	}
		
	void printmenuItem1_ActionPerformed(java.awt.event.ActionEvent event) {
		PrinterJob pjob = PrinterJob.getPrinterJob();
		
		// Get and change default page format settings if necessary.
		PageFormat pf;
		if (hasDonePageSetup == null) {
			pageSetupMenuItem_ActionPerformed(null);
		} 
		pf = (PageFormat) hasDonePageSetup;
		// Show page format dialog with page format settings.
		pjob.setPrintable(this, pf);
		System.out.println("Printing");

		if (pjob != null) {          
			try {
				if (pjob.printDialog()) {
					pjob.print();
				}
			} catch (PrinterException e) {
				System.out.println("Problem printing");
			}
		}
	}
	
*/	
	
	
	void openmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		kinshipTermsPanel1.loadKinData();

		if (kinshipTermsPanel1.xout != null) {
			String path = kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			MainFrame.prefs.put("LastFilename",path);
			MainFrame.prefs.savePrefs("KAESPrefs.xml");
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			setTitle(path);
			updatePreferences();
			GlobalWindowManager.changeWindowName(this);
		}
		//}}
	}

	void saveasmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		updatePreferences();
		kinshipTermsPanel1.saveKinDataInNewFile(this);
		if (kinshipTermsPanel1.xout != null) {
			String path = kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			MainFrame.prefs.put("LastFilename",path);
			MainFrame.prefs.savePrefs("KAESPrefs.xml");
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			setTitle(path);
			updatePreferences();
			GlobalWindowManager.changeWindowName(this);
		}
		//}}
	}

	void savemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		updatePreferences();
		kinshipTermsPanel1.saveKinData(this);
		if (kinshipTermsPanel1.xout != null) {
			String path;
			path = kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			MainFrame.prefs.put("LastFilename",path);
			MainFrame.prefs.savePrefs("KAESPrefs.xml");
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			setTitle(path);
			updatePreferences();
			GlobalWindowManager.changeWindowName(this);
		}
		//}}
	}


	void savecayleymenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	   if (getLastCayley() == null) return;

	   XFile xout = new XFile();
	   if (!xout.Choose(XFile.WRITE,"cayleytable.xml","Export CayleyTable to file...")) {
				return;
		 }
	  if (xout.OpenPrint()) {
		 xout.WriteString(getLastCayley().toXML());
		 xout.Close();
	  } else {
		 Debug.prout(0,"Couldn't open file for export!");
	  }
	}

	void newmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		new KintermFrame(processor).setVisible(true);
		//}}
	}

	void quitmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Hide the KintermFrame
//		setVisible(false);
	Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent((java.awt.Window)GlobalWindowManager.getMainWindow(), WindowEvent.WINDOW_CLOSING));
		//}}
	}

	void prefsmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		(new PreferencesDialog(this, MainFrame.prefs, false)).doDialog();
	}

// MF 22/11/01 Added clearmenuitem for delete function	#e100
	void clearmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
		if (kinshipTermsPanel1 != null) {
				kinshipTermsPanel1.removeTerm();
			}
		//{{CONNECTION
		//}}
	}
// MF End Change #e100

	void newStructuremenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
		
		//{{CONNECTION
		// Toggle show/hide
		TransferKinInfoVector v = kinshipTermsPanel1.mergeStructure();
		if (v != null) {
			makeNewPanel(v);
		}
		//}}
	}
	
	KintermTextEntryForm cFrame=null;
	
	void kintermTablemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		if (cFrame == null) {
			cFrame = new KintermTextEntryForm();
			//cFrame.setSize(830,620);
			//cFrame.setTitle("Kinterm Table");
			GlobalWindowManager.addWindow(cFrame);
			
		}
		if (kinshipTermsPanel1 != null) {
			kinshipTermsPanel1.cFrame = cFrame;
			kinshipTermsPanel1.doKintable();
			kinshipTermsPanel1.cFrame.show();
		} 
	}

	public void populateGeneratorPanel() {
		TransferKinInfoVector tk = (TransferKinInfoVector) lastPanel().getTransferKinInfo();
		populateGeneratorPanel(tk);
	}

	public void populateGeneratorPanel(TransferKinInfoVector tk) {
                  _populateGeneratorPanel(tk);
        }

	public void populateGeneratorPanel(TransferKinInfoVector tk,KinTermMap tkm) {
	    int i = 0;
	    if (Algebra.getCurrent().getIdentityElement() != null) i++;
	    if ((tkm == null)||(tkm.getGenerators().size() != Algebra.getCurrent().getGenerators().size())||
	    (tkm.getGeneratingTerms().size() != (tk.getEffectiveGenerators().size()+i)))
		_populateGeneratorPanel(tk);
            else {
		_populateGeneratorPanel(tkm);
            }
		//theFrame.generatorTextArea.append(XFile.Eol+XFile.Eol+"Kin Term Map Focal Terms"+XFile.Eol);
		//theFrame.generatorTextArea.append(theFrame.kinshipTermsPanel1.findFocalElements().replace('[',' ').replace(']',' ').trim()+XFile.Eol);
		//theFrame.generatorTextArea.append(theFrame.kinshipTermsPanel1.findFocalElements()+XFile.Eol);
	}

	void _populateGeneratorPanel(TransferKinInfoVector tk) {
        TransferKinInfoVector tq;
		generatorTextArea.setText("Algebra Generators"+XFile.Eol);
        ListVector lv = Algebra.getCurrent().getDisplayGenerators();
        boolean textFlag = false;
        for (lv.reset();lv.isNext();) {
            if (textFlag) {
				if (Algebra.getCurrent().getSexGenerators().size() > 0) {
					lv.getNext();
					continue;
				}
                generatorTextArea.append(XFile.Eol+"Algebra Sex Generators"+XFile.Eol);
			}
            textFlag = true;
			AlgebraSymbolVector asv = (AlgebraSymbolVector)lv.getNext();
            //StringVector v = ((AlgebraSymbolVector)lv.getNext()).toStringVector();
		    StringVector v = asv.toStringVector();
            boolean flag = false;
			for(v.reset();v.isNext();) {
				if (flag)
					generatorTextArea.append(", "+v.getNext());
				else {
					flag = true;
					generatorTextArea.append(v.getNext());
				}
			}
			generatorTextArea.append(XFile.Eol+XFile.Eol+"Transliteration of Generators"+XFile.Eol);
			for (int i=0;i<asv.size();i++){
				AlgebraSymbol as = (AlgebraSymbol)asv.elementAt(i);
				AlgebraSymbolVector as1 = new AlgebraSymbolVector(as);
				String text = v.elementAt(i)+": "+as1.makeTransliteration();
			    generatorTextArea.append(text+XFile.Eol);
			}
        }
		tq=tk.getEffectiveGenerators();
		generatorTextArea.append(XFile.Eol+XFile.Eol+"Kin Term Generators"+XFile.Eol);
		for(tq.reset();tq.isNext();) {
		    String s = tq.getNext().toString();
            int i = 0;
		    if ((i = s.indexOf("] "))== -1) i = s.indexOf(" ");
		    else i++;
		    s = s.substring(0,i);
			generatorTextArea.append(s+XFile.Eol);
		}
		StringVector tq1=tk.getEffectiveFocalTerms();
		for(tq1.reset();tq1.isNext();)
			generatorTextArea.append(tq1.getNext()+XFile.Eol);
        }

	void _populateGeneratorPanel(KinTermMap tkm) {
          StringVector v1 = tkm.getGenerators();
          StringVector v2 = tkm.getGeneratingTerms();
                  //Debug.prout(4," vectof v1 "+v1+" v2 "+v2);
          generatorTextArea.setText("Algebra Generators"+XFile.Eol);
          boolean flag = false;
          for(v1.reset();v1.isNext();) {
            if (flag)
              generatorTextArea.append(", "+v1.getNext());
            else {
              flag = true;
              generatorTextArea.append(v1.getNext());
            }
          }
          generatorTextArea.append(XFile.Eol+XFile.Eol+"Kin Term Generators"+XFile.Eol);
          flag = false;
          for(v2.reset();v2.isNext();) {
            if (flag)
              generatorTextArea.append(", "+XFile.Eol+v2.getNext());
            else {
              flag = true;
              generatorTextArea.append(v2.getNext());
            }
          }
        }


	public void populateEquationPanel() {
		equationTextArea.setLabel("Algebra Equations");
		EquationVector v = Algebra.getCurrent().getEquations();
		StringVector svT = equationTextArea.getItems(true);
		for(v.reset();v.isNext();) {
		    String s = v.getNext().toText();
            if (s != "" && svT.indexOf(s) == -1 && off.indexOf(s) == -1)
			    equationTextArea.append(s);
		}
		StringVector sv = Algebra.getCurrent().getGenerators().toStringVector();
		for (off.reset();off.isNext();){
			String s = off.getNext();
			if (useEquation(s,sv))
			   equationTextArea.append(s,false);
		}
	}

	boolean useEquation(String eq, StringVector sv){
		for (int i = 0; i < eq.length(); i++){
			String s = eq.substring(i,i+1);
			if (s.equals(" ") || s.equals("=")) continue;
			if (sv.indexOf(s) == -1) return false;
		}
		return true;
	}

	void newtermmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		kinshipTermsPanel1.newTerm();
		//}}
	}

	void focaltermmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		//kinshipTermsPanel1.findFocalElements();
        KintermFrame theFrame = mopsOps.getTheFrame();
		theFrame.generatorTextArea.append(XFile.Eol+XFile.Eol+"Kin Term Map Focal Terms"+XFile.Eol);
		theFrame.generatorTextArea.append(theFrame.kinshipTermsPanel1.findFocalElements().replace('[',' ').replace(']',' ').trim()+XFile.Eol);

		//}}
	}

	void makeNewPanel(TransferKinInfoVector t) {
		KinshipTermsPanel k =  new KinshipTermsPanel();
		k.setVisible(false);
		k.setLayout(null);
		k.setBounds(kinshipTermsPanel1.getBounds());
		k.setBackground(kinshipTermsPanel1.getBackground());
		k.setForeground(kinshipTermsPanel1.getForeground());
		k.buildKintermEntries(t);
		k.cFrame = cFrame;
		pushAndChangePanel(k);
	}

	void popPanelMenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle enabled
		popAndChangePanel();
		//}}
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Processor getProcessor() {
		return processor;
	}


	public void restart_algebra_construction() {
	   System.out.println("In restart_algebra_construction");
	   aopsOps.resetInstructionMenu();
	  // aopsProg.resetInstructionMenu();
		for (int i = panelsoff.size()-1;i > -1; i--){
			pushPanel((KinshipTermsPanel)panelsoff.elementAt(i));
		}
		panelsoff.removeAllElements();
		//off = (StringVector) equationTextArea.getOffItems().clone();
	   KinshipTermsPanel panel = lastPanel();
		changePanel(panel);
	   setFrameAlgebra(new Algebra());
		aopsOps.resetAlgebra();
		dialogueTextArea.setText("");
		populateGeneratorPanel();
	   lastCayley = null;
		if (offEquations != null)
		   Algebra.getCurrent().setSuppressEquations(offEquations);
	 //  aopsOps.resetAlgebra();
	 //  aopsProg.resetAlgebra();
	}

	protected Processor processor=null;

	//StringVector offEquations = new StringVector();
	StringVector offEquations = null;
	StringVector off = new StringVector();

	void equationTextArea_ActionPerformed(java.awt.event.ActionEvent event)
	{
	   String message=event.getActionCommand();
	   System.out.println("EquationPanel: "+message);

	   if (message.equals("Restart")) {
		  restart_algebra_construction();
	   } else if (message.equals("Pristine")) {
			off.removeAllElements();
		  offEquations.removeAllElements();
	   } else {
		  boolean state;
		  String equation;
		  if (message.startsWith("true")) {
			 state = true;
			 equation = message.substring(5,message.length());
			 int c;
			 if ((c = offEquations.indexOf(equation)) != -1) {
				offEquations.removeElementAt(c);
			 }
		  } else if (message.startsWith("false")) {
			 state = false;
			 equation = message.substring(6,message.length());
			 if (offEquations == null) offEquations = Algebra.getCurrent().getSuppressEquations();
			 if (offEquations.indexOf(equation) == -1) {
				offEquations.addElement(equation);
				//Algebra.getCurrent().setSuppressEquations(offEquations);
				//System.out.println(" offEquations "+offEquations +" alg "+Algebra.getCurrent().getSuppressEquations());
				off = equationTextArea.getOffItems();
			 }

		  } else System.out.println("equationTextArea: Unknown message: "+message);

	   }

	}

	void algmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		//kinshipTermsPanel1.findFocalElements();
          aopsOps.postAlgCayleyTable();

		//}}
	}

        void kinmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		//kinshipTermsPanel1.findFocalElements();
          aopsOps.postKinCayleyTable();

		//}}
	}

	void tablemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			// to do: code goes here.

			//{{CONNECTION
			// Toggle show/hide
			//kinshipTermsPanel1.findFocalElements();
			  aopsOps.postKinTypeProducts();

			//}}
		}
	void tableNmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			aopsOps.postKinTypeProducts("N");
		}
	void tableMmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			  aopsOps.postKinTypeProducts("M");
		}
	void tableFmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			  aopsOps.postKinTypeProducts("F");
		}
	void gridmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			// to do: code goes here.

			//{{CONNECTION
			// Toggle show/hide
			//kinshipTermsPanel1.findFocalElements();
			  //ACTIVATE GENEALOGICAL GRID FROM HERE
				Frame3D ff = (Frame3D) GlobalWindowManager.getFrame("Graph");
				if (ff != null) {
					Debug.prout(0,"SSSSSSSSSSSSSSSSSSSSSSSS SET STATE");
					ff.kinsymbolradioButton.setState(true);
					ff.genegridradioButton.setState(true);
					ff.threeDPanel.offset(false);
					setGenealogicalGrid("N");
					ff.resetGG("N");
					ff.setGraphType("Genealogy");
		//			ff.populateModel(threeD1,"Genealogy");
					//ff.populateModel(ff.threeDPanel,"Genealogy");
					ff.threeDPanel.setVisible(true);
					ff.setVisible(true);
					ff.toFront();
					//Debug.prout(4,"ggrid activated");
				}// else Debug.prout(4,"ggrid not activated");
			//}}
		}

	void gridNmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
		{
			// to do: code goes here.
			
			//{{CONNECTION
			// Toggle show/hide
			//kinshipTermsPanel1.findFocalElements();
			//ACTIVATE GENEALOGICAL GRID FROM HERE
			Frame3D ff = (Frame3D) GlobalWindowManager.getFrame("Graph");
			if (ff != null) {
				ff.kinsymbolradioButton.setState(true);
				ff.genegridradioButton.setState(true);
				ff.threeDPanel.offset(false);
				setGenealogicalGrid("N");
				ff.resetGG("N");
				ff.setGraphType("Genealogy");
				//			ff.populateModel(threeD1,"Genealogy");
				
				//ff.populateModel(ff.threeDPanel,"Genealogy");
				ff.threeDPanel.setVisible(true);
				ff.setVisible(true);
				ff.toFront();
				//Debug.prout(4,"ggrid activated");
			}// else Debug.prout(4,"ggrid not activated");
		 //}}
	}
	

        void gridMmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		//kinshipTermsPanel1.findFocalElements();
          //ACTIVATE GENEALOGICAL GRID FROM HERE
			Frame3D ff = (Frame3D) GlobalWindowManager.getFrame("Graph");
			if (ff != null) {
				ff.kinsymbolradioButton.setState(true);
				ff.genegridradioButton.setState(true);
				ff.threeDPanel.offset(false);
				setGenealogicalGrid("M");
				ff.resetGG("M");
				ff.setGraphType("Genealogy");
	//			ff.populateModel(threeD1,"Genealogy");

				//ff.populateModel(ff.threeDPanel,"Genealogy");
				ff.threeDPanel.setVisible(true);
				ff.setVisible(true);
				ff.toFront();
				//Debug.prout(4,"ggrid activated");
			}// else Debug.prout(4,"ggrid not activated");
		//}}
	}

        void gridFmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
		//kinshipTermsPanel1.findFocalElements();
          //ACTIVATE GENEALOGICAL GRID FROM HERE
			Frame3D ff = (Frame3D) GlobalWindowManager.getFrame("Graph");
			if (ff != null) {
				ff.kinsymbolradioButton.setState(true);
				ff.genegridradioButton.setState(true);
				ff.threeDPanel.offset(false);
				setGenealogicalGrid("F");
				ff.resetGG("F");
				ff.setGraphType("Genealogy");
	//			ff.populateModel(threeD1,"Genealogy");
				//ff.populateModel(ff.threeDPanel,"Genealogy");
				ff.threeDPanel.setVisible(true);
				ff.setVisible(true);
				ff.toFront();
				//Debug.prout(4,"ggrid activated");
			}// else Debug.prout(4,"ggrid not activated");
		//}}
	}


       void sentencemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		// Toggle show/hide
          aopsOps.postCayleyTableVerbose();
		//}}
	}



/*	void mopsMenuItem1_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		currentBuiltins = mopsOps; //mops1;
//		resumemenuItem.setName("Resume Mops");
//		mops1.reset();
//		mops1.exec();
        mopsOps.reset();
		aopsOps.reset();
		//aopsProg.reset();

		//}}
	}*/

	void resumemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		currentBuiltins.resume();
		//}}
	}

	void automenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		Mode.setMode(Mode.AUTOMATIC);
		//}}
	}

	void manualmenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		Mode.setMode(Mode.MANUAL);
		//}}
	}

	void tracemenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		Mode.setMode(Mode.TRACE);
		//}}
	}

	public void setLastCayley(CayleyTable lastCayley) {
		this.lastCayley = lastCayley;
	}

	public CayleyTable getLastCayley() {
		return lastCayley;
	}
	protected CayleyTable lastCayley=null;

	public void setFrameAlgebra(Algebra frameAlgebra) {
		this.frameAlgebra = frameAlgebra;
		if (Algebra.getCurrent() != frameAlgebra) {
			// Algebra.popCurrent(); // pop and replace
			Algebra.pushCurrent(frameAlgebra);
//		   lastCayley = null; // should be here but need fixing in aopsy stuff
		}
	}

	public Algebra getFrameAlgebra() {
		return frameAlgebra;
	}
	protected Algebra frameAlgebra = new Algebra();

	public void setCurrentBuiltins(BuiltinOperations currentBuiltins) {
		this.currentBuiltins = currentBuiltins;
	}

	public BuiltinOperations getCurrentBuiltins() {
		return currentBuiltins;
	    }

	public void activateCurrentMapPanel(int actcode){
		switch (actcode) {
            case KintermEditObject.SPOUSE:
				Algebra alg = getFrameAlgebra();
				KinshipTermsPanel pane = new KinshipTermsPanel();
				while (panels.size() != 0) {
					pane = (KinshipTermsPanel) panels.lastElement();
				   TransferKinInfoVector tk = pane.getTransferKinInfo();
					if (tk.getEffectiveGenerators(tk.SPOUSE).size() != 0 ||
					tk.getEffectiveGenerators(tk.SPOUSER).size() != 0 ) break;
					else {
						if (panels.size() > 1)
							popPanel();
						else break;
					}
	            }
               changePanel(lastPanel());
					break;
 	        case KintermEditObject.NONE:
	            alg = getFrameAlgebra();
	            pane = new KinshipTermsPanel();
	            pane = (KinshipTermsPanel) panels.lastElement();
	            //TransferKinInfoVector tk = pane.getTransferKinInfo();
			     // changePanel(previousPanel());//need to see the implications of using previousPanel rather than pop panel!!!
	            if (panels.size() > 1)
	                popPanel();
               changePanel(lastPanel());
                 /*   Debug.prout(4,"IN HERE ");
	            pane = (KinshipTermsPanel) panels.lastElement();
	            tk = pane.getTransferKinInfo();
                    tk.buildTables();// ADDED DR*/
	        default:
	            break;
	    }
	}

	//public void setSize(int w, int h) {
	//	super.setSize(w,h);
	//	Debug.prout(4,"Trying in setSize");
	/*	if (scrollKin != null)
			scrollKin.layout();*/
			//scrollKin.setSize(w,h-208);
	//}

	public void setBounds(int x, int y,int w,int h) {
		super.reshape(x,y,w,h);
		if (scrollKin != null) {
		//	Debug.prout(4,"Trying in setBounds");
			scrollKin.setSize(w,h-ScaleManager.s(207));
			scrollKin.layout();
		}
	}
}

