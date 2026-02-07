//
//  KintermEntryForm.java
//  Kaes
//
//  Created by Michael Fischer on Thu Aug 21 2003.
//  Copyright (c) 2003 Michael D Fischer. All rights reserved.
//
import java.awt.*;

public class KintermEntryForm extends KintermEntry {
 
	KinTextEntry kinText = null;
	static boolean textFlag = false;
	
	public KinTextEntry getKinText() {
		return kinText;
	}
	
	public void setTextRep(boolean tf) {
			textFlag = tf;
		}
	
	public boolean getTextRep() {
		return textFlag;
	}


   public void registerListeners() {
	  super.registerListeners();
   }
   	
   public KintermEntryForm() {
	  super();
	  kinText = new KinTextEntry(this);
  }

   public KintermEntryForm(Variables v) {
	  super(v);
	  kinText = new KinTextEntry(this);
   }

   public KintermEntryForm(String term, boolean isGenerator) {
	  super(term,isGenerator);
	  kinText = new KinTextEntry(this);
   }

// Draws the products
   
   void drawProducts(Graphics g) { // override for other methods for drawing products and hooks
								   //Set up stuff for storing lines - for now only if selected
	   if (!textFlag) { // do old draw
		   super.drawProducts(g);
	   }
	   
	   if (true) {
		   kinText.updateInfo();
	   }
  }

/**  routine to locate the various hook widgets on the terms border
   @param g the Graphics context to do the FontMetrics. Possibly change in future to send FontMetrics instead.
   */

   void setRectangles(Graphics g) {
	   if (!textFlag) super.setRectangles(g);
   }
	
	public String editTerm(Container source) {
		String ret="";
		if (source == null) System.out.println("Parent of"+getTheTerm()+" is null");
		else ((KinshipTermsMapPanel)source).editKinterm(this);
		return ret;
	}
	
	public String editProduct(Container source, int gen, Label kp) {
		String ret="";
		if (source == null) System.out.println("Parent of"+getTheTerm()+" is null");
		else ((KinshipTermsMapPanel)source).editProduct(this, gen, kp);
		System.out.println("Done entryfrom ...");
		return ret;
	}
	
/*	public String editProduct(String product) {
		String ret="";
		
		return ret;
	}*/

}
