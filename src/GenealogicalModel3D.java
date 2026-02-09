//import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
/* History
*/

public class GenealogicalModel3D extends Model3D{

	public GenealogicalModel3D(){
		super();
	}

    int[][] pairs;
    int radius = ScaleManager.s(6);

    void paint0(Graphics g) {
		if (vert == null || nvert <= 0)
		    return;
		if (!marginsSet) setMatBounds();
		else transform();
		if (nvert <= 0)
		    return;
		int lim = nvert;
		int v[] = tvert;
		int limc = ncon;//number of connections ?
		int c[] = con;
		int T,p1,p2;
		int x1,x2,y1,y2,z;
		pairs = new int[lim][lim];
		for (int i=0;i<lim;i++)
		    for (int j=0;j<lim;j++)
		        pairs[i][j]=0;

                for (int i = 3; i < limc; i++) {
		    T = c[i];
		    //p1 = ((T >> 16) & 0xFFFF) * 4;
		   // p2 = (T & 0xFFFF) * 4;
		    p1 = ((T >> 16) & 0xFFFF) * 3;
		    p2 = (T & 0xFFFF) * 3;

		    pairs[p1/3][p2/3]++;
		}


		for (int i = 4; i < lim; i++) {
                  p1 = i * 3;
                  int x = v[p1] << 16 | v[p1+1];
                  String label = IDs.getSymbol(i-4);
                  int rad = ((Integer)RADs.elementAt(i-4)).intValue();
                  if (IDs != null && ((labelFlag)||(label !=""))) {//draw label
					  g.setColor(Color.black);
					  if (rad == 0) {
						  g.setColor((Color)CLRs.elementAt(i-4));
						  int xx = v[p1]-radius/2;
						  if (label.indexOf("%") != -1){
							  int n = label.indexOf(",");
							  while (n != -1){
								  xx = drawColorLabel(g,label.substring(0,n),xx,v[p1+1]+3*radius);
								  xx = drawColorLabel(g,",",xx,v[p1+1]+3*radius);
								  label = label.substring(n+1,label.length());
								  n = label.indexOf(",");
							  }							  
						  }
						  drawColorLabel(g,label,xx,v[p1+1]+3*radius);
						  
						//  g.drawString(label,v[p1]-radius/2,v[p1+1]+3*radius);
                      //g.drawString(label,v[p1]-radius,v[p1+1]+3*radius);
						  g.setColor(Color.black);
					  }
					  else {
					//Color cc =g.color;
						Color cc = (Color)CLRs.elementAt(i-4);
						if (cc == Color.blue || cc == Color.red || cc == Color.black) g.setColor(Color.black);
						else g.setColor(Color.gray);
						//g.setColor((Color)CLRs.elementAt(i-4));
						g.drawOval(v[p1]-rad/2, v[p1+1], rad, rad);
						g.setColor(Color.black);
						boolean flag = (label.indexOf("#")==0);
						if (flag) {
							label = label.substring(1,label.length());
							g.setColor(Color.black);
							g.fillOval(v[p1]-rad/2, v[p1+1], rad, rad);
						}
						g.setColor((Color)CLRs.elementAt(i-4));
						int xx = v[p1]-radius/2;
						if (label.indexOf("%") != -1){
							int n = label.indexOf(",");
							while (n != -1){
								xx = drawColorLabel(g,label.substring(0,n),xx,v[p1+1]+3*radius);
								xx = drawColorLabel(g,",",xx,v[p1+1]+3*radius);
								label = label.substring(n+1,label.length());
								n = label.indexOf(",");
							}							
						}
						drawColorLabel(g,label,xx,v[p1+1]+3*radius);
						
						//g.drawString(label,v[p1]-rad/2,v[p1+1]+3*rad);
						g.setColor(Color.black);
					  }
                  }


               //   g.setColor((Color) CLRs.elementAt(i-4));//draw node
               //   g.fillOval(v[p1], v[p1+1], radius, radius);
              //  if ((IDs.getSymbol(i-4).equals("-1"))) {
               //     g.drawOval(v[p1]-radius/2, v[p1+1], radius, radius);
                 // }
		}

		if (limc <= 0)
		    return;
	    boolean flag = false;
		int n = 0;
		Polygon p = new Polygon();
		int[] xx = new int[3];
		int[] yy = new int[3];

		for (int i = 3; i < limc; i++) {
            T = c[i];
            p1 = ((T >> 16) & 0xFFFF) * 3;
		    p2 = (T & 0xFFFF) * 3;
            if (CLRcon != null) { //arrow color
              g.setColor((Color) CLRcon.elementAt(3*(i-3)));
		    }
            x1 = v[p1]; y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];z = v[p1+2];
			if (!(flag) && (z != 0)){//make polygon
				flag = true;
				n = 0;
			}
			if (flag) {
				xx[n]=x1;
				yy[n]=y1;
				n++;
			}
			if (n == 3){
				g.setColor(Color.black);
				g.drawPolygon(xx,yy,n);
				g.fillPolygon(xx,yy,n);
				n = 0;
				flag = false;
			}
            if (!flag) g.drawLine(x1,y1,x2,y2);
		}
    }

	public void setMatBounds() {
		int h=height;
		int w=width;

		findBB();
		float rx = xmax - xmin;
		float ry = ymax - ymin;

		rx = (rx < 0 ? -rx : rx)+2;
		ry = ((int) ((ry < 0 ? -ry : ry)))+1;

		// Available drawing area after g.translate offset
		int aw = w - drawOffsetX;
		int ah = h - drawOffsetY;
		// Use uniform scale to preserve aspect ratio and center graph
		float scaleX = ((float) aw * 0.8f) / rx;
		float scaleY = ((float) ah * 0.8f) / ry;
		float uniformScale = (scaleX < scaleY) ? scaleX : scaleY;
		adjXscale = uniformScale;
		adjYscale = uniformScale;
		// Center the graph in the available area, accounting for xmin/ymin offset
		int mx = (int)((aw - rx * uniformScale) / 2 - xmin * uniformScale);
		int my = (int)((ah - ry * uniformScale) / 2 - ymin * uniformScale);
		mat.setAdjscale(adjXscale, adjYscale,20);
		mat.setMargins(mx, my);
		marginsSet=true;
		transform();
	}
}
