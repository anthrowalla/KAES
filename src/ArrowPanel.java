import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Polygon;



public class ArrowPanel extends java.awt.Component
{
	//insert class definition here
	final static int UP = 1;
	final static int DOWN = -1;
	final static int RIGHT = 2;
	final static int LEFT = -2;

	public ArrowPanel()
	{
	}

	public void setArrowColour(java.awt.Color arrowColour) {
		this.arrowColour = arrowColour;
	}

	public java.awt.Color getArrowColour() {
		return arrowColour;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;

			
	}

	public int getOrientation() {
		return orientation;
	}

	public void setXbounds(java.awt.Rectangle bounds) {
		this.xbounds = bounds;
	}

	public java.awt.Rectangle getXbounds() {
		return xbounds;
	}

	/**
     * Returns the mininimum size of this component.
     * @see #getPreferredSize
     * @see LayoutManager
     */
    public Dimension getMinimumSize() {
		java.awt.Dimension supersResult = new Dimension(ScaleManager.s(32),ScaleManager.s(32));

		// to do: place event handler code here.
		// modify the return from the super, or return your own result

		return supersResult;
	}

	/**
     * Returns the preferred size of this component.
     * @see #getMinimumSize
     * @see LayoutManager
     */
    public Dimension getPreferredSize() {
		java.awt.Dimension supersResult = new Dimension(ScaleManager.s(32),ScaleManager.s(32));

		// to do: place event handler code here.
		// modify the return from the super, or return your own result

		return supersResult;
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
		int os = ScaleManager.s(5);
		int x = xbounds.width / 2 + xbounds.x;
		int y = xbounds.height / 2 + xbounds.y;
		int w = xbounds.width-os;
		int h = xbounds.height-os;
		g.drawRect(ScaleManager.s(1),ScaleManager.s(1),ScaleManager.s(30),ScaleManager.s(30));
		g.setColor(arrowColour);
		//super.paint(g);
		switch (orientation ) {
			case UP:	g.drawLine(x,h,x,os);
						g.drawLine(x,os,x-ScaleManager.s(5),ScaleManager.s(5)+os);
						g.drawLine(x,os,x+ScaleManager.s(5),ScaleManager.s(5)+os);
						break;
			case DOWN:	g.drawLine(x,os,x,h);
						g.drawLine(x,h,x-ScaleManager.s(5),h-ScaleManager.s(5));
						g.drawLine(x,h,x+ScaleManager.s(5),h-ScaleManager.s(5));
						break;
			case RIGHT:	g.drawLine(os,y,w,y);
						g.drawLine(w,y,w-ScaleManager.s(5),y-ScaleManager.s(5));
						g.drawLine(w,y,w-ScaleManager.s(5),y+ScaleManager.s(5));
						break;
			case LEFT:	g.drawLine(os,y,w,y);
						g.drawLine(os,y,os+ScaleManager.s(5),y-ScaleManager.s(5));
						g.drawLine(os,y,os+ScaleManager.s(5),y+ScaleManager.s(5));
						break;
		}


		switch (orientation ) {
			case UP:	x++;
						g.drawLine(x,h,x,os);
						g.drawLine(x,os,x-ScaleManager.s(5),ScaleManager.s(5)+os);
						g.drawLine(x,os,x+ScaleManager.s(5),ScaleManager.s(5)+os);
						break;
			case DOWN:	x--;g.drawLine(x,os,x,h);
						g.drawLine(x,h,x-ScaleManager.s(5),h-ScaleManager.s(5));
						g.drawLine(x,h,x+ScaleManager.s(5),h-ScaleManager.s(5));
						break;
			case RIGHT:	y--;g.drawLine(os,y,w,y);
						g.drawLine(w,y,w-ScaleManager.s(5),y-ScaleManager.s(5));
						g.drawLine(w,y,w-ScaleManager.s(5),y+ScaleManager.s(5));
						break;
			case LEFT:	 y++;g.drawLine(os,y,w,y);
						g.drawLine(os,y,os+ScaleManager.s(5),y-ScaleManager.s(5));
						g.drawLine(os,y,os+ScaleManager.s(5),y+ScaleManager.s(5));
						break;
		} 
	// makeLine(1,1,40,60,4,false);

		// to do: place event handler code here.
	}
	protected java.awt.Color arrowColour = Color.black;
	protected int orientation=LEFT;
	protected java.awt.Rectangle xbounds=new Rectangle(0,0,ScaleManager.s(32),ScaleManager.s(32));
	
	/** static utility routine to make a Polygon corresponding to a particular line
	* here because AWT lines don't have thickness
	* @param x1,y1 start position
	* @param x2,y2 end position
	* @param thick thickness of line. If even will place one more pixel to the right.
	* @param arrowhead put an arrowhead at the end of the line segment
	* @returns a Polygon structure that contains the line with optional arrowhead.
	*/
	public static Polygon makeLine(int x1, int y1, int x2, int y2, int thick, boolean arrowhead) {
		Polygon p = new Polygon();
		int sx1, sx2, sy1, sy2, dx, dy, dt,len;
		float px,py;
		
		dx = x1 - x2;
		dy = y1 - y2;
		len = (int) Math.sqrt(dx*dx+dy*dy);
		px = 1 - (float) dx / (float)len;
		py = 1 - dy / len;
		Debug.prout(4,"DX="+dx+" PX ="+px+" DY="+dy+" PY="+py);
		sx1 = x1-dx;
		sx2 = thick-dx;
		
		return p;
	}
}
