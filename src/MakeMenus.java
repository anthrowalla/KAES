//
//  MakeMenus.java
//  Kaes
//
//  Created by Michael Fischer on Thu Sep 02 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.awt.*;
import java.awt.event.*;

public class MakeMenus {

	public  MakeMenus() {
		mainmenuBar = new java.awt.MenuBar();
		cayleymenu = new java.awt.Menu("Cayley Table");
		kintypemenu = new java.awt.Menu("Kin Type Products");
		filemenu = new java.awt.Menu("File");
		editMenu = new java.awt.Menu("Edit");
		operationsmenu = new java.awt.Menu("Operations");
		modeMenu = new java.awt.Menu("Mode");
		
		// cayleymenu
		algmenuItem= new java.awt.MenuItem("with Algebra Element Labels");
		cayleymenu.add(algmenuItem);
		kinmenuItem = new java.awt.MenuItem("with Kin Term Labels");
		cayleymenu.add(kinmenuItem);
		sentencemenuItem = new java.awt.MenuItem("Sentence Format");
		cayleymenu.add(sentencemenuItem);

		// kintypemenu
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
		
		// filemenu
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
		pagesetupmenuItem = new java.awt.MenuItem("Page Setup...");
		pagesetupmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_P,true));
		filemenu.add(pagesetupmenuItem);
		printmenuItem = new java.awt.MenuItem("Print...");
		printmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_P,false));
		filemenu.add(printmenuItem);
		filemenu.addSeparator();
		prefsmenuItem = new java.awt.MenuItem("Preferences...");
		prefsmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_COMMA,false));
		filemenu.add(prefsmenuItem);
		quitmenuItem = new java.awt.MenuItem("Quit");
		quitmenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_Q,false));
		filemenu.add(quitmenuItem);
		
		// editMenu
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
		
		
		// operationsmenu
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
		
		// modeMenu
		automenuItem = new java.awt.CheckboxMenuItem("Automatic");
		automenuItem.setState(true);
		modeMenu.add(automenuItem);
		manualmenuItem = new java.awt.CheckboxMenuItem("Manual");
		manualmenuItem.setState(false);
		modeMenu.add(manualmenuItem);
		tracemenuItem = new java.awt.CheckboxMenuItem("Trace");
		tracemenuItem.setState(false);
		modeMenu.add(tracemenuItem);
		
		mainmenuBar.add(filemenu);
		mainmenuBar.add(editMenu);
		mainmenuBar.add(operationsmenu);
		mainmenuBar.add(modeMenu);
		
		operationsmenu.setEnabled(false);
		modeMenu.setEnabled(false);
		filemenu.setEnabled(true);
		editMenu.setEnabled(true);
		
		
		// setMenuBar(mainmenuBar);
	}
	
	public void setListeners(java.awt.event.ActionListener lSymAction, java.awt.event.ItemListener lSymItem) {
		openmenuItem1.addActionListener(lSymAction);
		saveasmenuItem.addActionListener(lSymAction);
		savecayleymenuItem.addActionListener(lSymAction);
		savemenuItem.addActionListener(lSymAction);
		newmenuItem.addActionListener(lSymAction);
		pagesetupmenuItem.addActionListener(lSymAction);
		printmenuItem.addActionListener(lSymAction);
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
		
		//SymItem lSymItem = new SymItem();
		automenuItem.addItemListener(lSymItem);
		manualmenuItem.addItemListener(lSymItem);
		tracemenuItem.addItemListener(lSymItem);		
	}
	
	//{{DECLARE_MENUS
	java.awt.MenuBar mainmenuBar;
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
	java.awt.MenuItem printmenuItem;
	java.awt.MenuItem pagesetupmenuItem;
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
	//}}
	
}
