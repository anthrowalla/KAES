import java.awt.*;
import java.util.*;

public class GlobalWindowManager {
	static Frame3D theGraph = null;
	static Frame currentWindow = null;
	static Frame mainWindow=null;
	
	static ListVector theWindows = new ListVector();
	static Menu windowsMenu = new Menu("Windows");
	
	static SymAction theListener  = null;
	
	static Object hasDonePageSetup = null;
	
	public GlobalWindowManager() {
		theListener  = new SymAction();
	}
	
	static public Frame getCurrentWindow() {
		return currentWindow;
	}
	
	static public void setCurrentWindow(Frame x) {
		MenuItem p;
		if (currentWindow != null) {
			p = getMenuItem(currentWindow);
			//p.setState(false);
		}
		currentWindow = x;
		if (currentWindow != null) {
			p = getMenuItem(currentWindow);
			//p.setState(true);
		}
	}
	
	static public void setGraphics(Frame3D x) {
		theGraph = x;
		addWindow(x);
	}
	
	static public Frame3D getGraphics() {
		return theGraph;
	}
	
	public static void addWindow(Frame x) {
		
		if (theWindows.addUnique(x)) {
			MenuItem m = new MenuItem(x.getTitle());
			windowsMenu.add(m);
			//m.setState(false);
			m.addActionListener(theListener);
		}
	}


	public static void changeWindowName(Frame x) {
		int q = theWindows.indexOf(x);
		if (q != -1) {
			String name = x.getTitle();
			windowsMenu.getItem(q).setLabel(x.getTitle());
		}
	}
	
	public static MenuItem getMenuItem(Frame x) {
		String name = x.getTitle();
		return getMenuItem(name);
	}
	
	public static MenuItem getMenuItem(String x) {
		MenuItem m;
		for (int i=0;i<windowsMenu.getItemCount();i++) {
			if ((m = windowsMenu.getItem(i)).getLabel().equals(x)) {
				if (m instanceof MenuItem)
					return (MenuItem) windowsMenu.getItem(i);
			}
		}
		return null;
	}
	
	public static Frame getFrame(MenuItem x) {
		String name = x.getLabel();
		return getFrame(name);
	}
	
	public static Frame getFrame(String x) {
		for (int i=0;i<theWindows.size();i++) {
			if (((Frame)theWindows.elementAt(i)).getTitle().equals(x)) {
				return (Frame)theWindows.elementAt(i);
			}
		}
		return null;
	}
	
	public static Object getPageSetup() {
		return hasDonePageSetup;
	}

	public static void setPageSetup(Object pageSetup) {
		hasDonePageSetup = pageSetup;
	}
	
	public static void removeWindow(Frame x) {
		int q = theWindows.indexOf(x);
		if (q != -1) {
			String name = x.getTitle();
			for (int i=0;i<windowsMenu.getItemCount();i++) {
				if (windowsMenu.getItem(i).getLabel().equals(name)) {
					((MenuItem) windowsMenu.getItem(i)).removeActionListener(theListener);
				}
			}
			
			if (currentWindow == x) {
				setCurrentWindow(null);
			}
			
			theWindows.removeElement(x);
			windowsMenu.remove(q);
		}
	}
	
	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			MenuItem ck = ((MenuItem) event.getSource());
			String itemName = ((MenuItem) event.getSource()).getLabel();
			for (int i=0;i<theWindows.size();i++) {
				if (((Frame) theWindows.elementAt(i)).getTitle().equals(itemName)) {
					((Frame) theWindows.elementAt(i)).setVisible(true);
				}
			}
		}
	}

	

	static public void setMainWindow(Frame mainW) {
		mainWindow = mainW;
	}

	static public Frame getMainWindow() {
		return mainWindow;
	}
}