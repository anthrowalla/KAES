//
//  MainFrame.java
//  Kaes
//
//  Created by Michael Fischer on Sun Aug 11 2002.
//  Copyright (c) 2002 Dwight Read, Michael Fischer. All rights reserved.
//
import java.awt.*;
import java.net.*;
import java.util.*;
import org.csac.io.*;
import java.awt.event.*;

public class MainFrame extends Frame {


	Label splashLabel = null;
	
	public void MainFrame() {
	}

	ImagePanel ip=null;
	
	public void init() {
		setLayout(null);
		setVisible(false);
		int w=ScaleManager.s(500),h=ScaleManager.s(400);
		setSize(w,h);
		setLocation(100,200); // change this to centre
		splashLabel = new Label("Starting up...");
		splashLabel.setSize(ScaleManager.s(100),ScaleManager.s(30));
		add(splashLabel);
		splashLabel.setLocation(w/2-(splashLabel.getSize().width/2),h/2-(splashLabel.getSize().height/2));
		setTitle("Kaes v3.03");
		setBackground(new Color(0xe0f8e0));
		 ip = new ImagePanel("java","logo_ani.gif");
		ip.setSize(ScaleManager.s(148),ScaleManager.s(60));
		add(ip);
		ip.setLocation(w/2-(ip.getSize().width/2),ScaleManager.s(50));
		setVisible(true);
		// kterms3d.reshape(12,40,600,650);
	}
	
	public void addMenus() {
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
		miPrefs = new java.awt.MenuItem("Preferences...");
		miPrefs.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_COMMA,false));
		menu1.add(miPrefs);
		miExit = new java.awt.MenuItem("Quit");
		miExit.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Q,false));
		menu1.add(miExit);
		mainMenuBar.add(menu1);
		menu2 = new java.awt.Menu("Edit");
		miCut = new java.awt.MenuItem("Cut");
		miCut.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_X,false));
		miCut.setEnabled(false);
		menu2.add(miCut);
		miCopy = new java.awt.MenuItem("Copy");
		miCopy.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_C,false));
		miCopy.setEnabled(false);
		menu2.add(miCopy);
		miPaste = new java.awt.MenuItem("Paste");
		miPaste.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_V,false));
		miPaste.setEnabled(false);
		menu2.add(miPaste);
		mainMenuBar.add(menu2);
		mainMenuBar.add(GlobalWindowManager.windowsMenu);
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
/*		menu4 = new java.awt.Menu("Title");
		showMenuItem = new java.awt.MenuItem("Show");
		showMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_K,false));
		menu4.add(showMenuItem);
		menuItem2 = new java.awt.MenuItem("Item");
		menu4.add(menuItem2);
		mainMenuBar.add(menu4)
*/
		setMenuBar(mainMenuBar);

		// -------------------
		
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		lSymAction = new SymAction();
		miOpen.addActionListener(lSymAction);
		miAbout.addActionListener(lSymAction);
		miExit.addActionListener(lSymAction);
		miPrefs.addActionListener(lSymAction);
		SymMouse aSymMouse = new SymMouse();
		helpMenuItem.addActionListener(lSymAction);
		messageHelpmenuItem.addActionListener(lSymAction);
		miNew.addActionListener(lSymAction);
		new GlobalWindowManager().addWindow(this);
		GlobalWindowManager.setMainWindow(this);
		mainMenuBar.add(GlobalWindowManager.windowsMenu);

	//	new Message("Enter kin term map","Begin the analysis by either opening a kin term map"+
	//	" or constructing a kin term map","Analyze",33).addMessage();

	}

	SymAction lSymAction;

	Processor theProcessor = new Processor();
	static Frame3D kterms3d = new Frame3D();
	Kaes kaes = new Kaes();
	//{{DECLARE_MENUS
	java.awt.MenuBar mainMenuBar;
	java.awt.Menu menu1;
	java.awt.MenuItem miNew;
	java.awt.MenuItem miOpen;
	java.awt.MenuItem miSave;
	java.awt.MenuItem miSaveAs;
	java.awt.MenuItem miPrefs;
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

	Button newButton = null;
	Button loadButton = null;
	Button lastButton = null;
	String lastfilename=null;
	public static Preferences prefs=null;
	
	public void initButtons() {
		newButton = new Button("New Terminology");
		newButton.setSize(ScaleManager.s(130),ScaleManager.s(30));
		add(newButton);
		newButton.setLocation(ScaleManager.s(20),ScaleManager.s(250));
		loadButton = new Button("Load Terminology");
		loadButton.setSize(ScaleManager.s(130),ScaleManager.s(30));
		add(loadButton);
		loadButton.setLocation(ScaleManager.s(30)+newButton.getSize().width,ScaleManager.s(250));
		loadButton.addActionListener(lSymAction);
		newButton.addActionListener(lSymAction);
		
		// prefs already loaded in main() via ScaleManager.initFromPreferences()
		if (prefs != null) {
			Object o = prefs.get("LastFilename");
			if (o != null) {
				if (o instanceof String) {
					String fname;
					lastfilename= o.toString();
					String finame = lastfilename;
					int p = lastfilename.lastIndexOf("/");
					if (p > 0) {
						finame = lastfilename.substring(p+1);
					}
					lastButton=new Button("Load "+finame);
					lastButton.setSize(ScaleManager.s(190),ScaleManager.s(30));
					add(lastButton);
					lastButton.setLocation(ScaleManager.s(45)+newButton.getSize().width+loadButton.getSize().width,ScaleManager.s(250));
					lastButton.addActionListener(lSymAction);
				}
			}
			StringVector x = prefs.getStrings("vector");
			System.out.println(x+"");
		}
		
		GlobalWindowManager.addWindow(kaes);
		GlobalWindowManager.addWindow(kterms3d);
		GlobalWindowManager.addWindow(MessageLine.theMessenger);
		
		Frame f = new KintermMapFrame();
		//f.setTitle("Kinterm Map");
		KintermMapCanvas km = new KintermMapCanvas();
		km.setSize(ScaleManager.s(400),ScaleManager.s(380));
		f.add(km);
		km.setLocation(0,ScaleManager.s(20));
		kmapFrame = f;
		kmap = km;
		GlobalWindowManager.addWindow(f);
	}
	
	public static Frame kmapFrame = null;
	public static KintermMapCanvas kmap = null;
	
	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
		}
	}
	
	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
	{
			Object object = event.getSource();
			if (object == MainFrame.this)
				onevent_WindowClosing(event);
	}

		public void windowActivated(java.awt.event.WindowEvent event) {
			mainMenuBar.add(GlobalWindowManager.windowsMenu);
			super.windowActivated(event);
		}
	}

	// Override default and exit if window closed ?????
	void onevent_WindowClosing(java.awt.event.WindowEvent event)
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
			else if (object == miPrefs)
				miPrefs_Action(event);
			else if (object == helpMenuItem)
				helpMenuItem_ActionPerformed(event);
			else if (object == messageHelpmenuItem)
				messageHelpmenuItem_ActionPerformed(event);
			else if (object == miNew)
				miNew_ActionPerformed(event);
			else if (object == newButton)
				miNew_ActionPerformed(event);
			else if (object == loadButton)
				miOpen_Action(event);
			else if (object == lastButton)
				lastButton_ActionPerformed(event);			
		}
	}
	
	void miPrefs_Action(java.awt.event.ActionEvent event)
	{
		// Action from About Create and show as modal
		(new PreferencesDialog(kaes, prefs, false)).doDialog();
		//px.doDialog();
		// (new AboutDialog(this, true)).setVisible(true);
	}
	
	void miAbout_Action(java.awt.event.ActionEvent event)
	{
		// Action from About Create and show as modal
		(new AboutDialog(this, true)).setVisible(true);
	}

	void miExit_Action(java.awt.event.ActionEvent event)
	{
		// Action from Exit Create and show as modal
		ListVector q = new ListVector();
		q.addElement("abcdefg");
		q.addElement("12345");
		q.addElement("The End");
		prefs.putPreference("vector",q);
		prefs.savePrefs("KAESPrefs.xml");
		System.out.println("Exiting");
		this.dispatchEvent(new WindowEvent((java.awt.Window)this, WindowEvent.WINDOW_CLOSING));
	}

	void miOpen_Action(java.awt.event.ActionEvent event)
	{
		KintermFrame x = new KintermFrame();
		x.kinshipTermsPanel1.loadKinData(x);
		if (x.kinshipTermsPanel1.xout != null) {
			String path = x.kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			prefs.put("LastFilename",path);
			prefs.savePrefs("KAESPrefs.xml");
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			x.setTitle(path);
			x.updatePreferences();
			GlobalWindowManager.changeWindowName(x);
		}
	}

	HelpFrame help = new HelpFrame();
	HelpMessageFrame messageHelp = new HelpMessageFrame();
	
	void helpMenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		help.doHelp("Startup");
	}

	void messageHelpmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		messageHelp.doHelp("Startup");
	}

	void miNew_ActionPerformed(java.awt.event.ActionEvent event)
	{
		new KintermFrame(theProcessor).setVisible(true);
	}

	void lastButton_ActionPerformed(java.awt.event.ActionEvent event)
	{
		KintermFrame x = new KintermFrame();
		x.kinshipTermsPanel1.loadKinData((String) prefs.get("LastFilename"),x);
		if (x.kinshipTermsPanel1.xout != null) {
			String path = x.kinshipTermsPanel1.xout.aFile.getAbsolutePath();
			int q = path.lastIndexOf("/");
			if (q != -1) path = path.substring(q+1);
			int p = path.lastIndexOf(".");
			if (p != -1) path = path.substring(0,p);
			x.setTitle(path);
			x.updatePreferences();
			GlobalWindowManager.changeWindowName(x);
		//	System.out.println("Rect "+x.kinshipTermsPanel1.getActiveBox());
		}
	}
	
	public static void main(String args[])
	{
			// Load preferences and init scaling before any windows are created
			ScaleManager.initFromPreferences();
			InitPreferences.init(MainFrame.prefs);
			MainFrame k = new MainFrame();
			k.init();
			k.repaint();
			k.addMenus();
			k.splashLabel.setText("");
			k.initButtons();
			//(new Kaes()).setVisible(true);
	}
}
