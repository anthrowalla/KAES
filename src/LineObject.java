import java.awt.Point;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.*;



public class LineObject
{
	//insert class definition here

	public LineObject()
	{
	
		//{{REGISTER_LISTENERS
		//}}
	}
	public LineObject(int sx, int sy, int ex, int ey )
	{
		//{{REGISTER_LISTENERS
		//}}
		startPoint.x = sx;
		startPoint.y = sy;
		endPoint.x = ex;
		endPoint.y = ey;
	}

	public LineObject(Point s, Point e )
	{
		//{{REGISTER_LISTENERS
		//}}
		startPoint = s;
		endPoint = e;
	}
	
	Rectangle bounds = null;
	
	public Rectangle getBounds() {
		//if (bounds == null) {
			if (startPoint.x < endPoint.x) {
				if (startPoint.y < endPoint.y)
					bounds = new Rectangle(startPoint.x,startPoint.y,endPoint.x - startPoint.x,
									endPoint.y-startPoint.y);
				else 
					bounds = new Rectangle(startPoint.x,endPoint.y, endPoint.x - startPoint.x,
									startPoint.y-endPoint.y);
			} else {
				if (startPoint.y < endPoint.y)
					bounds = new Rectangle(endPoint.x,startPoint.y,startPoint.x - endPoint.x,
									endPoint.y-startPoint.y);
				else 
					bounds = new Rectangle(endPoint.x,endPoint.y, startPoint.x - endPoint.x,
									startPoint.y-endPoint.y);
			}
		//}
		System.out.println("SX="+startPoint.x+" SY="+startPoint.y+" EX="+endPoint.x+ " EY="+
							endPoint.y+" Bounds="+bounds);
		return bounds;
	}
	
	java.awt.Polygon ourPoly=null;

	
	
	public void initPoly() {
		int xpoints[] = new int[5];
		int ypoints[] = new int[5];
		int xoff= startPoint.x < endPoint.x ? -selectedThickness:selectedThickness;
		int yoff= startPoint.y < endPoint.y ? selectedThickness:-selectedThickness;
		int dx = endPoint.x - startPoint.x;
		int dy = endPoint.y - startPoint.y;
		if (dx < 0) dx = -dx;
		if (dy < 0) dy = -dy;
		/*float rx = (float) dx / (float) (dx+dy);
		float ry = 1 - rx; // 0.5 - (float) dy / (float) (dx+dy);
		if (rx < .5) {
			xoff = (int) (30 * rx + 5);
			yoff = (int) (5 + rx * 25);
			//yoff = 
		} else {
			xoff = (int) (5 + (1-rx) * 25);
			yoff = (int) (30 * (1-rx) + 5);
		}*/
		int npoints=5;
		xpoints[0] = startPoint.x;
		ypoints[0] = startPoint.y;
		xpoints[1]= startPoint.x+xoff;
		ypoints[1] = startPoint.y+yoff;
		xpoints[2] = endPoint.x+xoff;
		ypoints[2] = endPoint.y+yoff;
		xpoints[3] = endPoint.x;
		ypoints[3] = endPoint.y;
		
		ourPoly = new Polygon(xpoints,ypoints,4);
	}

	public boolean inside(int x, int y) {
			if (ourPoly == null) initPoly();
			return (ourPoly.inside(x,y));
	}
	
	public void setStartPoint(java.awt.Point startPoint) {
		if (!startPoint.equals(this.startPoint)) ourPoly = null;
		this.startPoint = startPoint;
	}

	public java.awt.Point getStartPoint() {
		return startPoint;
	}

	public void setEndPoint(java.awt.Point endPoint) {
		if (!endPoint.equals(this.endPoint)) ourPoly = null;
		this.endPoint = endPoint;
	}

	public java.awt.Point getEndPoint() {
		return endPoint;
	}

	public void setColour(java.awt.Color colour) {
		this.colour = colour;
	}

	public java.awt.Color getColour() {
		return colour;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	public int getThickness() {
		return thickness;
	}


	public void setSelectedThickness(int thickness) {
		this.selectedThickness = thickness;
	}

	public int getSelectedThickness() {
		return selectedThickness;
	}

	public void setRadians(float radians) {
		this.radians = radians;

			
	}

	public float getRadians() {
		return radians;
	}

	public void setLength(int length) {
		this.length = length;

			
	}

	public int getLength() {
		return length;
	}
	public void setOffset(int length) {
		this.offset = length;

			
	}

	public int getOffset() {
		return offset;
	}

	public void setHasArrow(boolean hasArrow) {
		this.hasArrow = hasArrow;

			
	}

	public boolean isHasArrow() {
		return hasArrow;
	}

	public void setHasDoubleArrow(boolean hasDoubleArrow) {
		this.hasDoubleArrow = hasDoubleArrow;

			
	}

	public boolean isHasDoubleArrow() {
		return hasDoubleArrow;
	}

	public void setHasSpouseArrow(boolean hasDoubleArrow) {
		this.hasSpouseArrow = hasDoubleArrow;

			
	}

	public boolean isHasSpouseArrow() {
		return hasSpouseArrow;
	}

	/** 
     * Paints the component.  This method is called when the contents
     * of the component should be painted in response to the component
     * first being shown or damage needing repair.  The clip rectangle
     * in the Graphics parameter will be set to the area which needs
     * to be painted.
     * @param g the specified Graphics window
     * @see #update
     */
    public void paint(Graphics g) {
    	int awid = 4;
    	int aht = 12;
		g.setColor(colour);
//		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		if (hasSpouseArrow) {
			startPoint.x += offset;
			startPoint.y += offset;
			endPoint.x += offset;
			endPoint.y += offset;
			g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
			int nx = (startPoint.x + endPoint.x)/2;
			int ny = (startPoint.y + endPoint.y)/2;
			if (offset<1) g.drawString("=",nx-3,ny+1);
			else g.drawString("=",nx-3,ny+7);
		} else 
			g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		
		if (hasArrow) {
			int dx = endPoint.x - startPoint.x;
			int dy = endPoint.y - startPoint.y;
			int len = (int) Math.sqrt(dx*dx+dy*dy);
			float sin = (float) -dx / (float)len;
			float cos = (float) dy / (float)len;
			int ax1	 = (int) (-awid*cos + aht*sin);
			int ay1 = (int) (-awid*sin - aht*cos);
			int ax2 = (int) (awid*cos + aht*sin);
			int ay2 = (int) (awid*sin - aht*cos);
			ax1 += endPoint.x;
			ax2 += endPoint.x;
			ay1 += endPoint.y;
			ay2 += endPoint.y;
			g.drawLine(endPoint.x, endPoint.y, ax1, ay1);
			g.drawLine(endPoint.x, endPoint.y, ax2, ay2);
		}
		if (widgit) {
			g.fillRect(endPoint.x - 5,endPoint.y-5,10,10);
		}
	}
	
	public void paintSelected(Graphics g) {
		paint(g);
		if (ourPoly == null) initPoly();
		g.setColor(colour);
		g.fillPolygon(ourPoly);
	}

	boolean switchState=false;
	public void paintHilight(Graphics g) {
		paint(g);
		if (ourPoly == null) initPoly();
		if (switchState) g.setColor(hilightColour);
		else g.setColor(colour);
		switchState = !switchState;
		g.fillPolygon(ourPoly);
	}

	
	protected java.awt.Point startPoint = new Point(0,0);
	protected java.awt.Point endPoint = new Point(50,50);
	protected java.awt.Color colour = Color.black;
	protected java.awt.Color hilightColour = Color.green;
	protected int thickness = 1;
	protected int selectedThickness = 3;
	protected float radians = (float) Math.PI;
	protected int length = 50;
	protected int offset = 0;
	protected boolean hasArrow = false;
	protected boolean hasDoubleArrow = false;
	protected boolean hasSpouseArrow = false;
	
	protected java.awt.Polygon itsBounds = new Polygon();
	
	protected boolean widgit = false;
	protected int sx= -1,sy= -1;


	public void setKinterm(KintermEntry kinterm) {
		this.kinterm = kinterm;
	}

	public KintermEntry getKinterm() {
		return kinterm;
	}
	protected KintermEntry kinterm;

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}
	protected Product product;
}
