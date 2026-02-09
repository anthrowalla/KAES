/*
 * @(#)TreeD.java
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
/* A set of classes to parse, represent and display 3D wireframe models
   represented in Wavefront .obj format. */

/*Revision History
*10/12 DR added procedure in paint0 for setting color via StringVector CLRs should be changed to int [] with color constants RED,BLUE, etc
    added procedure for drawing arrows; has one parameter: length of arrow barb, could have 2nd parameter: angle of barb; currently angle is 30 degrees
    need to change color procedure as color must be assigned to pair of connected vertices
    added CLRs Vector and CLRcon Vector to class Model3D; former used for color of nodes and latter
    for color of arrows.  Colors assigned in CayleyTable
*10/17 added drawDashedLine, added Integer argument to CLRcon
*10/29 DR 	changed width to height in: md.mat.translate(size().width / 2, size().height / 2, size().width / 2);
    added SexMarkedModel3D class to extend Model3D
    see SexMarkedModel3D for changes in paint0
*11/1 DR added	setNvert, setNcon to Model3D and added model.setNvert(4),model.setNcon(3) to threeD.reset();
*11/2 DR Model3D taken out and made into its own class as it is accessed from outside ThreeD
* 2/14 DR deleted FileFormatException class as it is not used
* 3/3 DR implemented Boolean split condition for bifurcating element nodes
*/


import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.*;

/** An panel to show/manipulate a 3D model */
public class ThreeD extends Panel implements Runnable, MouseListener, MouseMotionListener {

	Model3D md; // the model used for calculations
    float xfac;
    int prevx, prevy;
    float xtheta, ytheta;
    float scalefudge = 1; // scaling factor
    Matrix3D amat = new Matrix3D(), tmat = new Matrix3D();
    String mdname = null;
    String message = null;

	public Model3D getModel3D() {
	    return md;
	}

    public void init() {
		init(.25f);
    }

    public void init(float scale) {
		mdname = "Kinterms";
		scalefudge = scale;
		if (mdname == null)
		    mdname = "Kinterms";
		addMouseListener(this);
		addMouseMotionListener(this);
    }

    public synchronized int setPoint(float x, float y, float z) {
    	return md.setPoint(x,y,z);
    }
	public synchronized int setPoint(float x, float y, float z, Color c) {
    	return md.setPoint(x,y,z,c);
    }
	public synchronized int setPoint(float x, float y, float z, String s) {
    	return md.setPoint(x,y,z,s);
    }
	public synchronized int setPoint(float x, float y, float z, String s, Color c) {
    	return md.setPoint(x,y,z,s,c);
    }
	public synchronized int setPoint(float x, float y, float z, String s, Color c, int r) {
		return md.setPoint(x,y,z,s,c,r);
    }
	public synchronized int setPoint(float x, float y, float z, String s, int r) {
    	return md.setPoint(x,y,z,s,r);
    }


	public synchronized void connectPoint(int x, int y) {
    	md.add(x,y);
    }
	public synchronized void connectPoint(int x, int y, Color c) {
    	md.add(x,y,c,Model3D.SOLID,new Boolean(false));
    }
    public synchronized void connectPoint(int x, int y, Integer i) {
    	md.add(x,y,Color.black,i,new Boolean(false));
    }
	public synchronized void connectPoint(int x, int y, Color c,Integer i) {
    	md.add(x,y,c,i,new Boolean(false));
    }
	public synchronized void connectPoint(int x, int y, Color c,Integer i, Boolean split) {
    	md.add(x,y,c,i,split);
    }

	public synchronized void connectPoint(float x, float y, float z) {
    	md.connectPoint(x,y,z);
    }
	public synchronized void connectPoint(float x, float y, float z, String s, Color c) {
    	md.connectPoint(x,y,z,s,c);
    }
	public synchronized void setLabel(String s) {
    	md.setLabel(s);
    }

	Dimension thePrefSize = new Dimension(ScaleManager.s(400),ScaleManager.s(400));

	public void setPreferredSize(int w, int h) {
		thePrefSize = new Dimension(w,h);
	}

    public Dimension preferredSize() {
		return thePrefSize;
    }

	// reconcile the following differences in setmodel
     public void setModel(Model3D m) {
    	md = m;
    	m.IDs = null;
    	m.CLRs = null;
    	m.CLRcon = null;
        m.RADs = null;
		setSize();
		repaint();
    }

     public void setModel(Model3D m, float scale) {
    	md = m;
    	m.IDs = null;
    	m.CLRs = null;
    	m.CLRcon = null;
        m.RADs = null;
    	scalefudge = scale;
    	amat = new Matrix3D();
    	tmat = new Matrix3D();
		setSize();
    	repaint();
    }

   public void setModel(Model3D m, float scale, StringVector id, Vector clr) {
    	md = m;
    	m.IDs = id;
    	m.CLRs = clr;
        m.RADs = null;
   		scalefudge = scale;
		// what about the amat and tmat in above example
		// and model.CLRcon?
		setSize();
		repaint();
     }

	public ThreeD reset() {
		return reset(md);
	}

   public ThreeD reset(Model3D model) {
		Model3D m = model;
		//setModel(model,0.3f);//use setModel(model,scale) to zoom
        setModel(model);
		model.IDs = new StringVector();
		model.CLRs = new Vector();
		model.CLRcon = new Vector();
		model.RADs = new Vector();
		model.setNvert(4);
		model.setNcon(3);
		md = m;
		m.findBB(); // this may need adjustment
		//m.compress();
		float xw = m.xmax - m.xmin;
		float yw = m.ymax - m.ymin;
		float zw = m.zmax - m.zmin;
		if (yw > xw)
		xw = yw;
		if (zw > xw)
		xw = zw;
		float f1 = (size().width-ScaleManager.s(20)) / xw;
		float f2 = (size().height-ScaleManager.s(20)) / xw;
		xfac = 0.7f * (f1 < f2 ? f1 : f2) * scalefudge; // validate this line!!!
		setSize();

		repaint();
		return this;
   }

	public void setSize() {
		md.setDrawOffset(gtransX, gtransY);
		md.setSize(size().width,size().height);
		amat.unit();
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x,y,w,h);
		if (md != null) {
		//	md.findBB();
			md.setDrawOffset(gtransX, gtransY);
			md.setSize(w,h);
			float xw = md.xmax - md.xmin;
			float yw = md.ymax - md.ymin;
			float zw = md.zmax - md.zmin;
			if (yw > xw)
				xw = yw;
			if (zw > xw)
				xw = zw;
			float f1 = (size().width-ScaleManager.s(20)) / xw;
			float f2 = (size().height-ScaleManager.s(20)) / xw;
			xfac = 0.7f * (f1 < f2 ? f1 : f2) * scalefudge; // validate this line!!!
			xfac = (f1 < f2 ? f1 : f2)/xw;
			repaint();
		//	System.out.println("md.setbounds:  xfac="+xfac+" scalefudge="+scalefudge);
		//	System.out.println("xmax="+md.xmax+" xmin="+md.xmin+" ymax="+md.ymax+" ymin="+md.ymin+" zmax="+md.zmax+" zmin="+md.zmin);
		} else Debug.prout(4,"Not in md.setBounds();");
	}

	public void setOrigin(float x, float y, float z) {
		if (md != null) md.setOrigin(x,y,z);
	}

	public float[] getOrigin() {
		if (md != null) return md.getOrigin();
		else return null;
	}

	public void translate(float dx, float dy, float dz) {
		if (md != null) md.translate(dx,dy,dz);
	}

  public void run() {
		InputStream is = null;
		try {
		    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		   reset(this.md);
		} catch(Exception e) {
		    md = new Model3D(); // must adjust ... why am I doing this.
		    message = e.toString();
		}
	}

	public void start() {
		if (md == null && message == null)
		    new Thread(this).start();
	    }

	public void stop() {

	}

	// --- MouseListener (1.1 event model) ---

	public void mousePressed(MouseEvent e) {
		prevx = e.getX();
		prevy = e.getY();
		if (e.getClickCount() > 1) {
			tmat.unit();
			amat.unit();
			amat.mult(tmat);
			setSize();
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	// --- MouseMotionListener (1.1 event model) ---

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		tmat.unit();
		float xtheta = (prevy - y) * 360.0f / getSize().width;
		float ytheta = (x - prevx) * 360.0f / getSize().height;
		tmat.xrot(xtheta);
		tmat.yrot(ytheta);
		amat.mult(tmat);
		repaint();
		prevx = x;
		prevy = y;
	}

	public void mouseMoved(MouseEvent e) {
	}

	// --- Legacy 1.0 event methods kept as fallback ---

	public boolean mouseDown(Event e, int x, int y) {
		prevx = x;
		prevy = y;
		if (e.clickCount > 1) {
			tmat.unit();
			amat.unit();
			amat.mult(tmat);
			setSize();
			repaint();
		}
		return true;
    }

	public boolean mouseUp(Event e, int x, int y) {
		return true;
   }

    public boolean mouseDrag(Event e, int x, int y) {
		tmat.unit();
		float xtheta = (prevy - y) * 360.0f / size().width;
		float ytheta = (x - prevx) * 360.0f / size().height;
		tmat.xrot(xtheta);
		tmat.yrot(ytheta);
		amat.mult(tmat);
		repaint();
		prevx = x;
		prevy = y;
		return true;
    }

	public int gtransX=0, gtransY=0;

	public void setOffset(int x, int y) {
		gtransX = x;
		gtransY = y;
	}

	public java.awt.Point getOffset() {
		return new java.awt.Point(gtransX, gtransY);
	}

	public void offset(boolean tf) {
		if (tf) setOffset((int)(size().width*.20),(int)(size().height*.20));
		else setOffset(0,0);
	}

    public void paint(Graphics g) {
		g.translate(gtransX,gtransY);

		if (md != null) {
		    md.mat.unit();
		    md.mat.mult(amat);

			md.transformed = false;
		    try {
		        md.paint(g);
		    } catch (Throwable e) {
		        System.err.println("ThreeD.paint exception: "+e);
		        e.printStackTrace();
		    }
			g.drawRect(-gtransX+1,-gtransY+1,size().width-2,size().height-2);
		} else if (message != null) {
		    g.drawString("Error in model:", ScaleManager.s(3), ScaleManager.s(20));
		    g.drawString(message, ScaleManager.s(10), ScaleManager.s(40));
		}
	}

	class SymKey extends java.awt.event.KeyAdapter
	{
		public void keyTyped(java.awt.event.KeyEvent event)
		{
			Object object = event.getSource();
			if (object == ThreeD.this)
				ThreeD_KeyTyped(event);
		}
	}

	void ThreeD_KeyTyped(java.awt.event.KeyEvent event)
	{
		amat.translate((float)0.5,(float)0.5,(float) 0);
		repaint();
	}
	public void advanceXY(float x, float y) {
		amat.translate(x,y,(float) 0);
		repaint();
	}

	class SymFocus extends java.awt.event.FocusAdapter
	{
		public void focusGained(java.awt.event.FocusEvent event)
		{
			Object object = event.getSource();
			if (object == ThreeD.this)
				ThreeD_focusGained(event);
		}
	}

	void ThreeD_focusGained(java.awt.event.FocusEvent event)
	{
		// to do: code goes here.
	}
}
