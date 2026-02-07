//
//  ProtoFrame.java
//  Kaes
//
//  Created by Michael Fischer on Mon Oct 18 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;

public abstract class ProtoFrame extends Frame implements Printable {
	
	MakeMenus makeMenus = new MakeMenus();
	PrintManager printManager = new PrintManager(this);
	
	public ProtoFrame() {
		setMenuBar(makeMenus.mainmenuBar);
		SymAction lSymAction = new SymAction();
		SymItem lSymItem = new SymItem();
		makeMenus.setListeners(lSymAction, lSymItem);
		GlobalWindowManager.addWindow(this);
		makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
		SymWindow aSymWindow = new SymWindow();
	 	this.addWindowListener(aSymWindow);
	}
			
	
	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
	{
			Object object = event.getSource();
			if (object == ProtoFrame.this)
				onevent_WindowClosing(event);
	}
		
		public void windowActivated(java.awt.event.WindowEvent event) {
			makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
			// GlobalWindowManager.setCurrentWindow(ProtoFrame.this);
			super.windowActivated(event);
			onevent_WindowActivated(event);
		}
	}
	
	void onevent_WindowActivated(java.awt.event.WindowEvent event) {
		
	}
	
	void onevent_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);	// hide the Frame
		dispose();			// free the system resources
	}
	
	
	public synchronized void dispose() {
		GlobalWindowManager.removeWindow(this);
		super.dispose();
	}
	
	class SymAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
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

	public int print(Graphics g, PageFormat pf, int pageIndex) {
		return printManager.print(g, pf, pageIndex);
	}
	
	
	void quitmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent((java.awt.Window)GlobalWindowManager.getMainWindow(), WindowEvent.WINDOW_CLOSING));
	}
	
	void prefsmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
		(new PreferencesDialog(this, MainFrame.prefs, false)).doDialog();
	}
	
	
	void openmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void saveasmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void savemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	
	void savecayleymenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void newmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void clearmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void newStructuremenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void kintermTablemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void newtermmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void focaltermmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
		
	void popPanelMenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void equationTextArea_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void algmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void kinmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void tablemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void tableNmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	void tableMmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	void tableFmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}

	void gridmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void gridNmenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	
	void gridMmenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
	}
	
	void gridFmenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
	}
	
	
	void sentencemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
		
	void resumemenuItem_ActionPerformed(java.awt.event.ActionEvent event)
	{
	}
	
	void automenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
	}
	
	void manualmenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
	}
	
	void tracemenuItem_ActionPerformed(java.awt.event.ItemEvent event)
	{
	}
	
}
