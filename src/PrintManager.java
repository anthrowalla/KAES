//
//  PrintManage.java
//  Kaes
//
//  Created by Michael Fischer on Mon Oct 18 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.util.Vector;

public class PrintManager {
	// Object hasDonePageSetup = null;
	Frame myFrame=null;
	Component drawObject=null;
	Dimension drawSize = null;
	PrinterJob printerJob=null;
	
	public void setDrawObject(Component d, Dimension dsize) {
		drawObject = d;
		drawSize = dsize;
	}

	public void setDrawObject(Component d) {
		setDrawObject(d,d.getSize());
	}
	
	public PrintManager(Frame f) {
		myFrame  = f;
	}
	
	public int print(Graphics g, PageFormat pf, int pageIndex) {
		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double x = pf.getImageableWidth();
		double y = pf.getImageableHeight();
		Dimension d;
		if (drawSize == null) d = myFrame.getSize();
		else d = drawSize;
		double sx = x / d.width;
		double sy = y / d.height;
		if (sy < sx) sx = sy;
		else sy = sx;
		g2d.scale(sx,sy);
		myFrame.printAll(g2d);
		return Printable.PAGE_EXISTS;
	}
	
	// in Frame print routine call prePrint, do painting then return postPrint(boolean), with boolean true to indicate a page, and boolean false to indicate the end;
	public void prePrint(Graphics g, PageFormat pf, int pageIndex, boolean scale) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		if (scale) {
			g2d.scale(1.0,1.0);
			double x = pf.getImageableWidth();
			double y = pf.getImageableHeight();
			Dimension d;
			if (drawSize == null) d = drawObject.getSize();
			else d = drawSize;
			double sx = x / d.width;
			double sy = y / d.height;
			if (sy < sx) sx = sy;
			else sy = sx;
			g2d.scale(sx,sy);
		}
	}
	
	public int postPrint(boolean tf) {
		if (tf) return Printable.PAGE_EXISTS;
		else return Printable.NO_SUCH_PAGE;
	}
	
	
	void pageSetup() {
		PageFormat pf;
		PrinterJob pjob = PrinterJob.getPrinterJob();
		if (GlobalWindowManager.getPageSetup() == null) {
			pf = pjob.defaultPage();
		} else 
			pf = (PageFormat) GlobalWindowManager.getPageSetup() ;
		
		pf = pjob.pageDialog(pf);
		GlobalWindowManager.setPageSetup(pf);
	}
	
	void setupPrintJob() {
		PrinterJob pjob = PrinterJob.getPrinterJob();
		
		// Get and change default page format settings if necessary.
		PageFormat pf;
		if (GlobalWindowManager.getPageSetup()  == null) {
			pageSetup();
		} 
		pf = (PageFormat) GlobalWindowManager.getPageSetup();
		// Show page format dialog with page format settings.
		pjob.setPrintable((Printable) myFrame, pf);
		System.out.println("Printing");
		printerJob = pjob;
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
	
	public Vector fitStringToBox(String s, FontMetrics fm, Rectangle size) { // size is page or unit size ... initial page offset for page 1
		return fitStringToBox(s, fm, size, true); // default vertical
	}
	
	public Vector fitStringToBox(String s, FontMetrics fm,Rectangle size, boolean verthorz) { // size is page or unit size ... initial page offset for page 1
		// verthorz true = vert, false = horz
		Vector pages = new Vector();
		Vector page = new Vector();
		page.addElement(null);
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();
		char lastNL = '@';
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		
		int curHeight = 0;
		int curLength = 0;
		int pageno = 0;
			//System.out.println(s);
		
		for(int i=0;i<s.length();i++) {
			char c = s.charAt(i);
			//System.out.print(c+0);
			//System.out.print(c);
			if (c == '\r' || c == '\n') {
				if ((lastNL == '@') ||  (c == lastNL)) {
					if (curHeight + fontHeight > size.height) {
						pages.addElement(page);
						page.setElementAt(new Rectangle(size.x+(verthorz ? 0:pageno++ * (size.width+15)), size.y+(verthorz ? pageno++ * (size.height+15):0),size.width,size.height),0);
						page = new Vector();
						page.addElement(null);
						curHeight=0;
					}
					if (word.length() != 0) {
						if (line.length() != 0) line.append(" ");
						line.append(word.toString());
						word.setLength(0);
					}
					if (line.length() != 0) {
						page.addElement(line.toString());
						curHeight += fontHeight;
						line.setLength(0);
						curLength = 0;
						lastNL = c;
					} else if (curHeight != 0) {
						page.addElement(line.toString());
						curHeight += fontHeight;
						line.setLength(0);
						curLength = 0;
						lastNL = c;
					}
				}
				lastNL='@';
			} else {
				lastNL = '@';
				if (c == ' ') {
					if (line.length() != 0) line.append(" ");
					line.append(word.toString());
					word.setLength(0);
					curLength = fm.stringWidth(line.toString());
				} else {
					word.append(new String(c+""));
					if (fm.stringWidth(word.toString())+curLength >  size.width) {
						if (curHeight + fontHeight > size.height) {
							pages.addElement(page);
							page.setElementAt(new Rectangle(size.x+(verthorz ? 0:pageno++ * (size.width+15)), size.y+(verthorz ? pageno++ * (size.height+15):0),size.width,size.height),0);
							page = new Vector();
							page.addElement(null);
							curHeight=0;
						}
						page.addElement(line.toString());
						curHeight += fontHeight;
						line.setLength(0);
						curLength = 0;
					}
				}
			}
		}
		if (word.length() != 0) {
			if (line.length() != 0) line.append(' ');
			line.append(word.toString());
		}
		if (line.length() != 0) {
			if (curHeight + fontHeight > size.height) {
				pages.addElement(page);
				page.setElementAt(new Rectangle(size.x+(verthorz ? 0:pageno++ * (size.width+15)), size.y+(verthorz ? pageno++ * (size.height+15):0),size.width,size.height),0);
				page = new Vector();
				page.addElement(null);
				curHeight=0;
			}
			page.addElement(line.toString());
			curHeight+= fontHeight;
		}
		if (page.size() != 0) {
			page.setElementAt(new Rectangle(size.x+(verthorz ? 0:pageno++ * (size.width+15)), size.y+(verthorz ? pageno++ * (size.height+15):0),size.width,size.height),0);
			pages.addElement(page);
		}
		return pages;
	}
	
	public int printComponentsText(Graphics g, Font f, Container ta) {
		String k="";
		Component[] comps = ta.getComponents();
		Container cc = ((Container)comps[0]);
		comps = cc.getComponents();
		for (int i=0;i<comps.length;i++) {
			Component c = comps[i];
			if (c instanceof Checkbox) {
				if (k.equals("")) k=((Checkbox)c).getLabel();
				else k = k + " "+((Checkbox)c).getLabel();
			}
		}
		Rectangle rr = ta.getBounds();
		rr.height *= 5;
	//	rr.width -= 20;
	//	rr.x -= 20;
		Vector pages = fitStringToBox(k, g.getFontMetrics(f), rr);
		return printPages(g,pages,f);
	}

	public Vector getComponentsTextx(Container ta) {
		Vector pages = new Vector();
		Vector page = new Vector();
		pages.addElement(page);
		page.addElement(ta.getBounds());

		Component[] comps = ta.getComponents();
		Container cc = ((Container)comps[0]);
		comps = cc.getComponents();
		for (int i=0;i<comps.length;i++) {
			Component c = comps[i];
			if (c instanceof Checkbox) {
				page.addElement(((Checkbox)c).getLabel());
				System.out.println(((Checkbox)c).getLabel());
			}
		}
		return pages;
	}
	
	public int printComponents(Graphics pg, Container ta) {
		int depth = 0;
		Component[] comps = ta.getComponents();
		Point ploc = ta.getLocation();
		pg.translate(ploc.x,ploc.y);

		for (int i=0;i<comps.length;i++) {
			Component c = comps[i];
			if (c instanceof Checkbox) {
				((Checkbox)c).paint(pg);
				if (c.getBounds().y + c.getBounds().height > depth) depth=c.getBounds().y + c.getBounds().height;
			}
		}
		pg.translate(-ploc.x,-ploc.y);
		//Rectangle d = ta.getBounds();
		return depth;
	}
	
	public int printTextArea(Graphics pg, TextArea ta) {
		Rectangle d = ta.getBounds();
		return printTextArea(pg, ta, d, true);
	}

	public int printTextArea(Graphics pg, TextArea ta, Rectangle bounds) {
		Rectangle d = ta.getBounds();
		return printTextArea(pg, ta, bounds, false);
	}
	
	
	public int printTextArea(Graphics pg, TextArea ta, Rectangle bounds, boolean horizvert) {
	//	Rectangle d = ta.getBounds();
	//	double k = ((Graphics2D) pg).getTransform().getScaleX();
		// d.width *= k;
		pg.setColor(ta.getForeground());
		Rectangle td = new Rectangle(bounds);
	//	td.height = (int) ((PageFormat) GlobalWindowManager.getPageSetup()).getImageableHeight() - td.y; // start with remainder of page
		Font helv = ta.getFont(); //new Font("Helvetica", Font.PLAIN, 12);
		pg.setFont (helv); //have to set the font to get any output
		FontMetrics fm = pg.getFontMetrics(helv);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		int curHeight=0;
		Vector pages = fitStringToBox(ta.getText(),fm,td, horizvert); // horiz
		return printPages(pg,pages, helv);
	}
	
	void printLongString (PrintJob pjob, Graphics pg, String s, int startPagePos) {
		int pageNum = 1;
		int linesForThisPage = 0;
		int linesForThisJob = 0;
		// Note: String is immutable so won't change while printing.
		if (!(pg instanceof PrintGraphics)) {
			throw new IllegalArgumentException ("Graphics context not PrintGraphics");
		}
		StringReader sr = new StringReader (s);
		LineNumberReader lnr = new LineNumberReader (sr);
		String nextLine;
		int pageHeight = pjob.getPageDimension().height;
		int pageWidth = pjob.getPageDimension().width;
		Font helv = ScaleManager.font("Helvetica", Font.PLAIN, 12);
		//have to set the font to get any output
		pg.setFont (helv);
		FontMetrics fm = pg.getFontMetrics(helv);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		int curHeight = startPagePos;
		Vector pages = fitStringToBox(s,fm,new Rectangle(0,0,pageWidth,pageHeight)); // need to adjust with startPagePos
		
		for(int i=0;i<pages.size();i++) {
			Vector page = (Vector) pages.elementAt(i);
			if (i != 0) {
				pg.dispose();
				pg = pjob.getGraphics();
				if (pg != null) {
					pg.setFont (helv);
				}
				curHeight = 0; // for subsequent pages only
			}
			
			for(int j=1;j<page.size();j++) {
				String line = (String) page.elementAt(j);
				curHeight += fontHeight;
				if (pg != null) {
					pg.drawString (line, 0, curHeight - fontDescent);
				}
			}
		}
	}
	

	public int printString(String s, int x, int y, Graphics pg) {
		Font helv = ScaleManager.font("Helvetica", Font.PLAIN, 12);
		return printString(s, x, y, pg, helv);
	}
	
	public int printString(String s, int x, int y, Graphics pg, Font font) {
		pg.setFont (font);
		FontMetrics fm = pg.getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		pg.drawString (s, x, y + (fontHeight - fontDescent));
		return y+fontHeight;
	}
	
	public int printPages(Graphics pg, Vector pages) {
		Font helv = ScaleManager.font("Helvetica", Font.PLAIN, 12);
		return printPages(pg, pages, helv);
	}
	
	public int printPages(Graphics pg, Vector pages, Font font) {
		FontMetrics fm = pg.getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		int curHeight = ((Rectangle)(((Vector)pages.elementAt(0)).elementAt(0))).y;
		int curX = ((Rectangle)(((Vector)pages.elementAt(0)).elementAt(0))).x;
		int maxheight=0;
		
		for(int i=0;i<pages.size();i++) {
			Vector page = (Vector) pages.elementAt(i);
			if (i != 0) {
				// pg.dispose();
				// pg = pjob.getGraphics();
				//if (pg != null) {
				//	pg.setFont (font);
				//}
				// curHeight = 0; // for subsequent pages only
				if (curHeight > maxheight) maxheight = curHeight;
				
				curHeight = ((Rectangle)(((Vector)pages.elementAt(i)).elementAt(0))).y;
				curX = ((Rectangle)(((Vector)pages.elementAt(i)).elementAt(0))).x;
			}
			
			for(int j=1;j<page.size();j++) {
				String line = (String) page.elementAt(j);
				System.out.println(line);
				if (pg != null && line.length() != 0) {
					curHeight += fontHeight;
					pg.drawString (line, curX, curHeight - fontDescent);
				} else curHeight+= fontHeight/3;
			}
		}
		if (curHeight > maxheight) maxheight = curHeight;
		return maxheight;
	}
}

/* 
try {
	do {
		nextLine = lnr.readLine();
		if (nextLine != null) {
			if ((curHeight + fontHeight) > pageHeight) {
				// New Page
				System.out.println("" + linesForThisPage + " lines printed for page " + pageNum);
				pageNum++;
				linesForThisPage = 0;
				pg.dispose();
				pg = pjob.getGraphics();
				if (pg != null) {
					pg.setFont (helv);
				}
				curHeight = 0;
			}
			curHeight += fontHeight;
			if (pg != null) {
				pg.drawString (nextLine, 0, curHeight - fontDescent);
				linesForThisPage++;
				linesForThisJob++;
			} else {
				System.out.println ("pg null");
			}
		}
	} while (nextLine != null);
} catch (EOFException eof) {
	// Fine, ignore
} catch (Throwable t) { // Anything else
	t.printStackTrace();
}
System.out.println ("" + linesForThisPage + " lines printed for page " + pageNum);
System.out.println ("pages printed: " + pageNum);
System.out.println ("total lines printed: " + linesForThisJob);

*/
