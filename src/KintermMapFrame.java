//
//  KintermTextEntryForm.java
//  Kaes
//
//  Created by Michael Fischer on Thu Oct 14 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

public class KintermMapFrame extends ProtoFrame {
	public  KintermMapFrame() {
		setSize(ScaleManager.s(400),ScaleManager.s(400));
		setLayout(null);
		setTitle("Kinterm Map");
		
	//	makeMenus = new MakeMenus();
	//	makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
	//	setMenuBar(makeMenus.mainmenuBar);
		
	//	SymWindow aSymWindow = new SymWindow();
	//	this.addWindowListener(aSymWindow);

	}
/*	 
	MakeMenus makeMenus;
	
	class SymWindow extends java.awt.event.WindowAdapter {
		public void windowClosing(java.awt.event.WindowEvent event) {
			Object object = event.getSource();
			if (object == KintermMapFrame.this)
				setVisible(false);
		}
		
		public void windowActivated(java.awt.event.WindowEvent event) {
			makeMenus.mainmenuBar.add(GlobalWindowManager.windowsMenu);
			GlobalWindowManager.setCurrentWindow(KintermMapFrame.this);
			// setFrameAlgebra(getFrameAlgebra());
			//makeMenus.operationsmenu.setEnabled(false);
			// makeMenus.modeMenu.setEnabled(false);
			
			super.windowActivated(event);
		}
	}
	*/
}
