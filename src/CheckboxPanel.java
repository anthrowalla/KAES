//
//  CheckboxItem.java
//  Kaes
//
//  Created by Michael Fischer on Sat Apr 12 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//
import java.awt.*;
import java.awt.event.*;

public class CheckboxPanel extends ScrollPane {

   Font itemFont;
   Label label=null;
   int lastLocation=12;
   Panel panel = new Panel();
   Button button = null;
   StringVector offItems = new StringVector();
   StringVector onItems = new StringVector();
   StringVector allItems = new StringVector();

   public CheckboxPanel() {
	  this("",SCROLLBARS_AS_NEEDED);
   }

   public CheckboxPanel(String label, int policy) {
	  super(policy);
	  itemFont = ScaleManager.font("Serif", Font.PLAIN, 10);
	  panel.setLayout(null);
	  panel.setSize(getSize().width-ScaleManager.s(20),ScaleManager.s(100));
	  add(panel);
	  setLabel(label);
	  SymComponent aSymComponent = new SymComponent();
	  this.addComponentListener(aSymComponent);
	  panel.addComponentListener(aSymComponent);
  }

   public CheckboxPanel(int policy) {
	  this("",policy);
   }

   SymAction aSymAction = new SymAction();

   public void setLabel(String text) {
	  if (label == null) {
		 label = new Label();
		 Dimension d = panel.getSize();
		 label.setSize(d.width-ScaleManager.s(85),ScaleManager.s(20));
		 label.setLocation(ScaleManager.s(2),lastLocation);
		 button = new Button("Reset");
		 button.setFont(itemFont);
		// SymAction aSymAction = new SymAction();
		 button.addActionListener(aSymAction);
		 button.setSize(ScaleManager.s(50),ScaleManager.s(25));
		 button.setLocation(d.width-ScaleManager.s(80),lastLocation);
		 lastLocation+=ScaleManager.s(25);
		 panel.setSize(d.width,lastLocation);
		 panel.add(label);
		 panel.add(button);
	  }
	  label.setFont(itemFont);
	  label.setText(text);
   }

   public void addItem(String text, boolean state) {
	  Checkbox cb = new Checkbox(text,state);
	  cb.setFont(itemFont);
	  Dimension d = panel.getSize();
	  cb.setSize(d.width-ScaleManager.s(25),ScaleManager.s(16));
	  cb.setLocation(ScaleManager.s(2),lastLocation);
	  panel.add(cb);
	  lastLocation += ScaleManager.s(16);
	  panel.setSize(d.width,lastLocation);
	  cb.addItemListener(aSymAction);
   }


   public void setEditable(boolean s) {

   }

   public void append(String text) {
	  addItem(text,true);
   }

   public void append(String text, boolean flag) {
	  addItem(text,flag);
   }

   public StringVector getOffItems() {
	  offItems = getItems(false);
	  return offItems;
   }


   public StringVector getItems(boolean state) {
	  Component [] comps = panel.getComponents();
	  StringVector ret = new StringVector();
	  for (int i=1;i<comps.length;i++) {
		 if (comps[i] instanceof Checkbox) {
			if (((Checkbox) comps[i]).getState() == state) {
			   ret.addElement(((Checkbox) comps[i]).getLabel());
			}
		 }
	  }
	  return ret;
   }

   public void removeAll() {
	  String l = label.getText();
	  button.removeActionListener(aSymAction);
	  panel.removeAll();
	  offItems = new StringVector();
	  lastLocation = 12;
	  label=null;
	  setLabel(l);
   }

   class SymComponent extends java.awt.event.ComponentAdapter {
	  public void componentResized(java.awt.event.ComponentEvent event) {
		 layout();
	  }
   }

   class SymAction implements java.awt.event.ActionListener, ItemListener {
	  public void actionPerformed(java.awt.event.ActionEvent event) {
		 Object object = event.getSource();
		 if (object == button) {
	//		setOffItems();
			removeAll();
			notify_listener("Restart");
		 }
	  }

	  public void itemStateChanged(java.awt.event.ItemEvent event) {
		 Object object = event.getSource();
		 if (object instanceof Checkbox) {
			String mess = ((Checkbox) object).getLabel();
			boolean state = ((Checkbox) object).getState();
			getOffItems();
			if (offItems.size() != 0) {
			   notify_listener(state+","+mess);
			} else {
			   notify_listener("Pristine");
			}
		 }
	  }
   }

   transient ActionListener actionListener;
   String actionCommand;

   public synchronized void addActionListener(ActionListener l) {
	  if (l == null) {
		 return;
	  }
	  actionListener = AWTEventMulticaster.add(actionListener, l);
	 // newEventsOnly = true;
   }

   public synchronized void removeActionListener(ActionListener l) {
	  if (l == null) {
		 return;
	  }
	  actionListener = AWTEventMulticaster.remove(actionListener, l);
   }
   boolean state=false;

   public void notify_listener(String message) {
	 // if (state != this.state)
		if (actionListener != null) {
		  actionListener.actionPerformed(new ActionEvent(CheckboxPanel.this,ActionEvent.ACTION_PERFORMED,message));
	   }
   }


}
