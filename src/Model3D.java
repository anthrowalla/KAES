
import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
import java.awt.Polygon;
import java.awt.FontMetrics;


/* History
* 3/2 DR rewrote calculations for arrow barbs, added procedure drawArrowBarbs, added a solid arrowhead
* 3/3 DR added procedure drawArcArrows, implemented Boolean split for bifurcating elements
* 6/4 DR added procedure setPoint(float x, float y, float z, String s)
*/


/** The representation of a 3D model */
public class Model3D {

    float vert[]; // the vertices
    int tvert[];
    int nvert=0, maxvert=0; // number of verticies and maximum verticies
    int con[]; // connections
    int ncon=0, maxcon=0; // number of connections, max connections
    float axes[] = {0,1,1,1}; // the axes for this space
    String axesLabels[] = {"O","X","Y","Z"}; // the labels for axes for this space
    boolean transformed=false;
    Matrix3D mat;
    int lastVert = 0;

    float xmin, xmax, ymin, ymax, zmin, zmax; // maintain mins and maxes

	// Following are things added to support KAES
	public StringVector IDs=new StringVector();
	public Vector CLRs = new Vector();
	public Vector CLRcon = new Vector();
    public Vector RADs = new Vector();
    boolean labelFlag = true;
    boolean dim1Flag = true;
    boolean dim2Flag = true;
    boolean dim3Flag = true;


    public Model3D () {
		mat = new Matrix3D ();
		addAxes(6,6,6); // why 6 6 6?
		addAxes(1,1,1); // why 6 6 6?
	//	int a = addVert(2f,3f,4f);
	//	addVert(4f,3f,2f);
	//	addVert(0.3f,0.4f,0.2f);
	//	add(a,a+1);
	//	add(a,a+2);
	//	mat.xr1ot(20);
	//	mat.yrot(30);
    }

    public Model3D (float x, float y, float z) {
		mat = new Matrix3D ();
		addAxes(x,y,z);
		//	int a = addVert(2f,3f,4f);
  //	addVert(4f,3f,2f);
  //	addVert(0.3f,0.4f,0.2f);
  //	add(a,a+1);
  //	add(a,a+2);
  //	mat.xr1ot(20);
  //	mat.yrot(30);
    }


    public Model3D setDim1Flag(boolean flag) {
          dim1Flag = flag;
          return this;
    }

    public Model3D setDim2Flag(boolean flag) {
        dim2Flag = flag;
        return this;
    }
    public Model3D setDim3Flag(boolean flag) {
          dim3Flag = flag;
          return this;
    }

    public void setLabelFlag(boolean flag) {
          labelFlag = flag;
    }

    void setLabel(String s) {
    	IDs.setElementAt(s, lastVert);
    }

	// setNvert and setNcon are internal settings ... avoid unless you really know what you are doing!

    void setNvert(int n) {
        nvert = n;
    }

    void setNcon(int n) {
        ncon = n;
    }

	/** sets last vertice set to colour s
	**/
    void setColor(Color s) {

        CLRs.setElementAt(s,lastVert);
    }

	/** A host of setPoints
	* see fully qualified definition
	**/

    int setPoint(float x, float y, float z, String s) {
        return setPoint(x,y,z,s,Color.black);
    }

    int setPoint(float x, float y, float z, String s,int r) {
        return setPoint(x,y,z,s,Color.black,r);
    }


    int setPoint(float x, float y, float z, String s, Color c) {
      return setPoint(x,y,z,s,c,ScaleManager.s(6));
    }

	/** Sets an x,y,z point if dimension flags are set (or implied)
	*	@param x x coordinate
	*	@param y y coordinate
	*	@param z z coordinate
	*	@param s label for point
	*	@param c colour for point
	*	@param r radius or chunkiness of point
	**/

	int setPoint(float x, float y, float z, String s, Color c, int r) {
        if ((dim1Flag || x == 0) && (dim2Flag || y == 0)&& (dim3Flag || z == 0)) {
    	     lastVert = addVert(x,y,z);
    	     IDs.addElement(s);
    	     CLRs.addElement(c);
             RADs.addElement(new Integer(r));
    	     return lastVert;
    	}
    	else
    	     return -1;
    }

    int setPoint(float x, float y, float z) {
    	return setPoint(x,y,z,"");
    }

    int setPoint(float x, float y, float z, int r) {
    	return setPoint(x,y,z,"",Color.black,r);
    }

    int setPoint(float x, float y, float z, Color c) {
        return setPoint(x,y,z,"",c);
    }

    int setPoint(float x, float y, float z, Color c,int r) {
        return setPoint(x,y,z,"",c,r);
    }

	/** Creates a point and then connects to previous point created if dimension flags are set (or implied)
		*	@param x x coordinate
		*	@param y y coordinate
		*	@param z z coordinate
		*	@param s label for point
		*	@param c colour for line
		*	@param linetype code for kind of line .. SOLID, DASH1, DASH2
		*	@param split divide node into two nodes
		**/

	void connectPoint(float x, float y, float z, String s, Color c, Integer linetype, Boolean split) {
        int a = setPoint(x,y,z,s);
		if (a != -1) add(lastVert,a,c,linetype,split);

/*        if ((dim1Flag || x == 0) && (dim2Flag || y == 0)&& (dim3Flag || z == 0)) {
    	     a = addVert(x,y,z);
    	     add(lastVert,a,c,i,split);
    	     lastVert = a;
     	     IDs.addElement(s);
     	     //CLRs.addElement(c);
     	}*/
   }

    void connectPoint(float x, float y, float z) {
    	connectPoint(x,y,z,"",Color.black,SOLID,new Boolean(false));
   }


    void connectPoint(float x, float y, float z, Color c) {
        connectPoint(x,y,z,"",c,SOLID,new Boolean(false));
   }

   void connectPoint(float x, float y, float z, String s, Color c) {
        connectPoint(x,y,z,s,c,SOLID,new Boolean(false));
   }

    void connectPoint(float x, float y, float z, Integer line) {
        connectPoint(x,y,z,"",Color.black,line,new Boolean(false));
    }

    void connectPoint(float x, float y, float z, String s, Integer line) {
        connectPoint(x,y,z,s,Color.black,line,new Boolean(false));
   }

 	void setOrigin(float x, float y, float z) {
 		mat.setOrigin(x,y,z);
 	}

	void translate(float x, float y, float z) {
		mat.translate(x,y,z);
	}
	
    /** Returns origin of 3d matrix
	*	@return float[3] -- x, y, z
	**/
 	float[] getOrigin() {
 		return mat.getOrigin();
 	}

   /** Add a vertex to this model
	*	@return the vertice reference
	*/
	int addVert(float x, float y, float z) {
		int i = nvert;
		if (i >= maxvert)
			if (vert == null) {
				maxvert = 100;
				vert = new float[maxvert * 3];
			} else {
				maxvert += 200;
				float nv[] = new float[maxvert * 3];
				System.arraycopy(vert, 0, nv, 0, vert.length);
				vert = nv;
			}
		i *= 3;
		vert[i] = x;
		vert[i + 1] = y;
		vert[i + 2] = z;
		return nvert++;
    }

    /** Add a line from vertex p1 to vertex p2
	*	@param p1 vertice index for point 1
	*	@param p2 vertice index for point 2
	*/
    void add(int p1, int p2) {
        add(p1, p2, Color.black,SOLID,new Boolean(false));
    }

    /** Add a line from vertex p1 to vertex p2
	*	@param p1 vertice index for point 1
	*	@param p2 vertice index for point 2
	*	@param p2 vertice index for point 2
	*	@param c colour index for line
	*	@param linetype code for kind of line .. SOLID, DASH
	*	@param split god knows
	*/
    void add(int p1, int p2, Color c, Integer line, Boolean split) {
		int i = ncon;
		if (p1 >= nvert || p2 >= nvert)
		    return;
		if (i >= maxcon)
		    if (con == null) {
				maxcon = 100;
				con = new int[maxcon];
		    } else {
				maxcon += 200;
				int nv[] = new int[maxcon];
				System.arraycopy(con, 0, nv, 0, con.length);
				con = nv;
		    }
		/*if (p1 > p2) {
		    int t = p1;
		    p1 = p2;
		    p2 = t;
		}*/
		con[i] = (p1 << 16) | p2; // max 32767 points? // maybe expand this ... speedup??
		CLRcon.addElement(c);
		CLRcon.addElement(line);
		CLRcon.addElement(split);
		ncon = i + 1;
	}

    /** Initialise the axes
		*	@param x x axis magnitude
		*	@param y y axis magnitude
		*	@param z z axis magnitude
	*/
	void addAxes(float x, float y, float z) { // mag/scale of x, y, and z axis
		int nx;
		nx = addVert(0,0,0); //adding 4 vertices increases nvert to 4, so use 4 when resetting
		addVert(x,0,0);
		addVert(0,y,0);
		addVert(0,0,z);
		add(nx,nx+1); //adding 3 lines increases ncon to 3, so use 3 when resetting
		add(nx,nx+2);
		add(nx,nx+3); // add lines*/
		axes[0] = 0;
		axes[1] = x;
		axes[2] = y;
		axes[3] = z;
		axesLabels[0] = "O";
		axesLabels[1] = "X";
		axesLabels[2] = "Y";
		axesLabels[3] = "Z";
    }

    /** Transform all the points in this model */
    void transform() {
		if (transformed || nvert <= 0)
		    return;
		if (tvert == null || tvert.length < nvert * 3)
		    tvert = new int[nvert*3];

		mat.transform(vert, tvert, nvert);
		transformed = true;
    }

   /* Quick Sort implementation
    */
   private void quickSort(int a[], int left, int right)
   {
      int leftIndex = left;
      int rightIndex = right;
      int partionElement;
      if ( right > left)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         partionElement = a[ ( left + right ) / 2 ];

         // loop through the array until indices cross
         while( leftIndex <= rightIndex )
         {
            /* find the first element that is greater than or equal to
             * the partionElement starting from the leftIndex.
             */
            while( ( leftIndex < right ) && ( a[leftIndex] < partionElement ) )
               ++leftIndex;

            /* find an element that is smaller than or equal to
             * the partionElement starting from the rightIndex.
             */
            while( ( rightIndex > left ) &&
                   ( a[rightIndex] > partionElement ) )
               --rightIndex;

            // if the indexes have not crossed, swap
            if( leftIndex <= rightIndex )
            {
               swap(a, leftIndex, rightIndex);
               ++leftIndex;
               --rightIndex;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( left < rightIndex )
            quickSort( a, left, rightIndex );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( leftIndex < right )
            quickSort( a, leftIndex, right );

      }
   }

   private void swap(int a[], int i, int j)
   {
      int T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;
   }


    /** eliminate duplicate lines */
    void compress() {
		int limit = ncon;
		int c[] = con;
		quickSort(con, 0, ncon - 1);
		int d = 0;
		int pp1 = -1;
		for (int i = 0; i < limit; i++) {
		    int p1 = c[i];
		    if (pp1 != p1) {
			c[d] = p1;
			d++;
		    }
		    pp1 = p1;
		}
		ncon = d;
    }



    static Color gr[];
  static Color gred[];

    /** Paint this model to a graphics context.  It uses the matrix associated
	with this model to map from model space to screen space.
	The next version of the browser should have double buffering,
	which will make this *much* nicer */

  void paint1(Graphics g) {
		if (vert == null || nvert <= 0)
		    return;
		transform();
		if (gr == null) {
		    gr = new Color[16];
		  /*  for (int i = 0; i < 16; i++) {
				int grey = (int) (170*(1-Math.pow(i/15.0, 2.3)));
				gr[i] = new Color(grey, grey, grey);
		    }*/
		    for (int i = 0; i < 16; i++) {
				int grey = (int) (100*(1-Math.pow(i/15.0, 2.3)));
				gr[i] = new Color(grey, grey, grey);
		    }
		}
		int lg = 0;
		int lim = nvert;
		int v[] = tvert;
		if (nvert <= 0)
		    return;
		for (int i = 0; i < lim; i++) {
		    int p1 = i * 3;
		    int grey = ((int) zmax - v[p1 + 2])/15;
		    if (grey < 0)
			grey = 0;
		    if (grey > 15)
			grey = 15;
		    if (grey != lg) {
				lg = grey;
				g.setColor(gr[grey]);
		    }
		   // grey /= 2;
		   if (grey < 2) grey = 2;
		    // g.drawOval(v[p1]-1, v[p1 + 1]-1, v[p1]+1, v[p1 + 1]+1);
		    g.drawRect(v[p1], v[p1 + 1],
			       grey, grey);
		}
    }

  // The main paint routine
  void paint0(Graphics g) {
		if (vert == null || nvert <= 0)
		    return;
	   if (!marginsSet) setMatBounds();
	   else transform();
		if (gr == null) {
		    gr = new Color[16];
		    for (int i = 0; i < 16; i++) {
				int grey = (int) (100*(1-Math.pow(i/15.0, 2.3)));
				gr[i] = new Color(grey, grey, grey);
		    }
		}
		if (nvert <= 0)
		    return;
		int lim = nvert;
		int v[] = tvert;
		int limc = ncon;
		int c[] = con;
		int T,p1,p2;
		int x1,x2,y1,y2;
		double len = 0.0;
		int offset= 0;
		int os = 0;
		int radius = 0;
		Vector xv = new Vector();
		int [] xArray = new int[100];
		for (int i=0;i<100;i++)
		    xArray[i] = -1;

		int index = 0;


        for (int i = 3; i < limc; i++) {
		    T = c[i];
		    p1 = ((T >> 16) & 0xFFFF) * 3;
		    p2 = (T & 0xFFFF) * 3;
		    x1 = v[p1]+ScaleManager.s(100); y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];
		    len = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		    if (len > 0) {
		        offset = 2*(int)Math.round(len/7);
		        radius = (int) Math.round(len/10);
		        break;
		    }
		}
		for (int i = 4; i < lim; i++) {
		    p1 = i * 3;
            int x = v[p1]*100+v[p1+1];
            int k = 0;
            boolean flag = false;
            for (k = 0; k<100;k++){
                if (xArray[k] == x) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
				try {
					if (CLRs.elementAt(i-4)!= Color.gray){
						xArray[index] = x;
						index++;
					}
				} catch (Exception excpt) {
					return;
				}
                os = 0;
            }

           /* Integer xx = new Integer(x*100+y);
            if (xv.indexOf(xx) == -1) {

			    xv.addElement(xx);
			    os = 0;

			}*/

			else
		        os = offset;
		    if (IDs != null) {
		        g.setColor(Color.black);
		  	    if (CLRs.elementAt(i-4)!= Color.gray)

			    g.drawString(IDs.getSymbol(i-4),v[p1]+offset,v[p1+1]+os/2);
			}
		  	   // if (os == 0 || (os != 0 && CLRs.elementAt(i-4)!= Color.gray)) {
		  	if (CLRs != null) {
		  	    if (CLRs.elementAt(i-4)!= Color.gray) {
			        g.setColor((Color) CLRs.elementAt(i-4));
			        g.fillOval(v[p1]+ os, v[p1+1], radius, radius);
			    }
			}
		}
		if (limc <= 0)
		    return;
		int delx,dely;
		for (int i = 3; i < limc; i++) {
		    T = c[i];
		    p1 = ((T >> 16) & 0xFFFF) * 3;
		    p2 = (T & 0xFFFF) * 3;
            if (CLRcon != null) { //arrow color
			    //g.setColor((Color) CLRcon.elementAt(2*(i-3)));
			    g.setColor((Color) CLRcon.elementAt(3*(i-3)));
			}
            int shift = 0;  boolean flag = true;
            if (p1 < p2) shift = ScaleManager.s(3); //make side-by-side arrows
            else shift = -ScaleManager.s(3);
			x1 = v[p1]; y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];
			if (x1==x2 && y1==y2) {
			    //drawArcArrow(g,x2,y2,(Integer) CLRcon.elementAt(2*(i-3)+1));
			    drawArcArrow(g,x2,y2,(Integer) CLRcon.elementAt(3*(i-3)+1));

			    flag = false;
			    //x1=x1-offset+5*radius/4;
			    //x2 = x2+offset-5*radius/4;
			    //y1 = y1 + radius/2; y2 = y2+radius/2;
			}
			else if (y1 == y2) {
			    y1 = y1+shift;
			    y2 = y2+shift;
			}
			else {
			    x1 = x1+shift;
			    x2 = x2+shift;
			}
		/*
			else if (x1 == x2){
			    x1 = x1+shift; x2 = x2+shift;
			}
			else if (y1 == y2) {
			    y1 = y1 + shift; y2 = y2+shift;
			}*/
			if (flag)
			//drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(2*(i-3)+1));
			if (y1 != y2 || x1 != x2)
			    drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(3*(i-3)+1));
		}
    }

    boolean polyFlag = false;

    void drawArcArrow(Graphics g,int x, int y, Integer aType){
        int d = ScaleManager.s(16);//diameter of arc
 		if (aType.intValue() < 0){
 		    polyFlag = true;
 		    aType = new Integer(-aType.intValue());
 		}
        double x1 = x-d/2;
        double y1 = y-d;
		if (aType.equals(SOLID) || aType == null)
            g.drawArc((int)Math.round(x1),(int)Math.round(y1),d,d,315,270);
		else {
			int l1 = setArcDashLength(aType);
			int l2 = setArcDashSpace(aType);
			drawDashedArc(g,(int)x1,(int)y1,d,d,315,270,l1,l2);
			//System.out.println(" atype "+aType+" l1 "+l1 +" l2 "+l2);
		}
        x1 = x1 + d/2;
        y1 = y1 + d/2;
        double x2 = x1 - (d/2)*.707;//0.707 = sin 45
        double y2 = y1 + (d/2)*.707;//0.707= cos 45
        x1=x1-d/2;
        drawArrowBarbs(g,(int)Math.round(x1),(int)Math.round(y1),(int)Math.round(x2),(int)Math.round(y2));
    }

    public double cos(int x1,int y1,int x2,int y2) {
		double len = 0.0;
		double ret;

  		len = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
  		if (len != 0) ret = (x2-x1)/len;
  		else ret = 1;
        return ret;
    }
     public double sin(int x1,int y1,int x2,int y2) {
		double len = 0.0;
		double ret;

  		len = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
  		if (len != 0) ret = (y2-y1)/len;
  		else ret = 0;
        return ret;
    }

    public void drawArrow(Graphics g,int x1,int y1,int x2,int y2,Integer aType) {
		double x,y,cos,sin,l;
		double len = 0.0;
		int delx,dely,i;

		polyFlag = false;
 		if (aType.intValue() < 0){
 		    polyFlag = true;
 		    aType = new Integer(-aType.intValue());
 		}
		len = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
		if (len == 0){
		    drawArcArrow(g,x2,y2,aType);
		}
  		else {
		    cos = (x2-x1)/len; sin = (y2-y1)/len;
		    l = len/6;//arrow offset from vertex
		    if (l > ScaleManager.s(10))l = ScaleManager.s(10);
		    delx=(int) Math.round(l*cos);
		    dely = (int) Math.round(l*sin);
		    //if (y1 != y2){
                {x1 = x1 + delx; y1 = y1+dely; x2 = x2 - delx; y2 = y2-dely;}
            if (aType.equals(SOLID)|| aType == null){
			    g.drawLine(x1,y1,x2,y2);
			    polyFlag = true;
			}
		    else {
				double l1 = setDashLength(len, aType);
				double l2 = setDashSpace(len, aType);
				//System.out.println(" atype "+ aType+" l1 "+l1 +" l2 "+ l2+" len "+len);
			    drawDashedLine(g,x1,y1,x2,y2,l1,l2);
			    //drawDashedLine(g,x1,y1,x2,y2,l2,l2);
			    polyFlag = false;
			}
            drawArrowBarbs(g,x1,y1,x2,y2);
  		}
    }

	int setArcDashLength(Integer aType) {
		if (aType.equals(DASH1)) return ScaleManager.s(20);
	    else return ScaleManager.s(10);
	}


	double setDashLength(double len, Integer aType) {
		double l = 0.0;
		if (aType.equals(DASH1)){
			l = len/10;
			if (l > ScaleManager.s(5)) l = ScaleManager.s(5);
		} else if (aType.equals(DASH2)) {
			l = ScaleManager.s(2);
		}
		return l;
	}

	int setArcDashSpace(Integer aType) {
		if (aType.equals(DASH1)) return ScaleManager.s(10);
	    return ScaleManager.s(20);
	}

	double setDashSpace(double len, Integer aType) {
		double l = 0;
		if (aType.equals(DASH1)){
			l = len/15;
			if (l > ScaleManager.s(3)) l = ScaleManager.s(3);
		} else if (aType.equals(DASH2)) {
			l = ScaleManager.s(6);
		}
		return l;
	}

    void drawArrowBarbs(Graphics g,int x1, int y1, int x2, int y2) {
		double x,y,cosa,sina;//a is the barb angle
		double n = ScaleManager.s(6); //factor for arrow barb length
 		int [] xArray = new int[3];
		int [] yArray = new int[3];
        double len = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        if (n > len/4) n = ScaleManager.s(3);

  		cosa = 0.906; sina = 0.423;//a = 25 degrees
		x = n/len*((x2-x1)*cosa - (y2-y1)*sina);
		y = n/len*((y2-y1)*cosa + (x2-x1)*sina);
		x = x2-x; y = y2-y;
		if (polyFlag){
		    xArray[0] = (int) Math.round(x); yArray[0] = (int) Math.round(y);
		}
		else
		    g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));

		x = n/len*((x2-x1)*cosa + (y2-y1)*sina);
		y = n/len*((y2-y1)*cosa - (x2-x1)*sina);
		x = x2-x; y = y2-y;
		if (polyFlag){
		    xArray[1] = (int) Math.round(x); yArray[1] = (int) Math.round(y);
		    xArray[2] = x2; yArray[2] = y2;
		    g.drawPolygon(xArray,yArray,3);
		    g.fillPolygon(xArray,yArray,3);
		} else
		    g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));
    }

    public void drawDashedLine(Graphics g, int x1,int y1,int x2,int y2,
                                double dashlength, double spacelength) {
        double linelength=Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        double yincrement=(y2-y1)/(linelength/(dashlength+spacelength));
        double xincdashspace=(x2-x1)/(linelength/(dashlength+spacelength));
        double yincdashspace=(y2-y1)/(linelength/(dashlength+spacelength));
        double xincdash=(x2-x1)/(linelength/(dashlength));
        double yincdash=(y2-y1)/(linelength/(dashlength));
        int counter=0;
        for (double i=0;i<linelength-dashlength;i+=dashlength+spacelength){
            g.drawLine((int) (x1+xincdashspace*counter),
                        (int) (y1+yincdashspace*counter),
                        (int) (x1+xincdashspace*counter+xincdash),
                        (int) (y1+yincdashspace*counter+yincdash));
            counter++;
        }
        if ((dashlength+spacelength)*counter<=linelength)
            g.drawLine((int) (x1+xincdashspace*counter),
                    (int) (y1+yincdashspace*counter),
                    x2,y2);
    }

	public void drawDashedArc(Graphics g, int x1,int y1,int d1,
				int d2, int arcangle,int arc, int dashlength, int dashspace) {
		int dl = dashlength; int ds = dashspace;
        int counter=arc/(dl+ds);
		int angle = arcangle;
        for (int i=0;i<counter;i++){
            g.drawArc(x1,y1,d1,d2,angle,dl);
			angle = angle +dl+ds;
        }
        if ((dl+ds)*counter<arc)
            g.drawArc(x1,y1,d1,d2,angle,arcangle-(dl+ds)*counter);
	}
	
	void setColor(Graphics g,int iCode){
		switch (iCode){
			case 1:
				g.setColor(Color.red);
				break;
			case 2:
				g.setColor(Color.blue);
				break;
			default:
				g.setColor(Color.black);
				break;
		}
	}
	
	int drawColorLabel(Graphics g,String s,int x,int y){
		//System.out.println(" s "+s + " index "+s.indexOf("%"));
		//FontMetrics fm = g.getFontMetrics();
		if (s.indexOf("%") == 0){
			String sex = s.substring(1,2);
			s = s.substring(2,s.length());
			//System.out.println(" s "+s + " index "+s.indexOf("%")+" sex "+sex+" x "+x);
			int iCode = -1;
			if (sex.equals("M")) iCode = 2;
			else if (sex.equals("F")) iCode = 1;
			//System.out.println(" s "+s + " index "+s.indexOf("%")+" sex "+sex+" x "+x+" iCoce "+iCode);
			setColor(g,iCode);
		} 
		g.drawString(s,x,y);
		return x+g.getFontMetrics().stringWidth(s);
	}
	
	void paint0xx(Graphics g) {
		if (vert == null || nvert <= 0)
		    return;
		transform();
		if (gr == null) {
		    gr = new Color[16];
			/*  for (int i = 0; i < 16; i++) {
			int grey = (int) (170*(1-Math.pow(i/15.0, 2.3)));
			gr[i] = new Color(grey, grey, grey);
		    }*/
		    for (int i = 0; i < 16; i++) {
				int grey = (int) (100*(1-Math.pow(i/15.0, 2.3)));
				gr[i] = new Color(grey, grey, grey);
		    }
		}
		int lg = 0;
		int lim = nvert;
		int v[] = tvert;
		int limc = ncon;
		int c[] = con;
		if (nvert <= 0)
		    return;
		Color black = new Color(0,0,0);
		Color red = new Color(192,0,0);
		Color green = new Color(0,164,0);
		int ogrey=0;
		/*	for (int i = 0; i < 4; i++) {
		    int p1 = i * 3;
		int grey = (v[p1 + 2])/15;
		if (grey < 0)
			grey = 0;
		if (grey > 15)
			grey = 15;
		g.setColor(red);
		if (grey != lg) {
			lg = grey;
		}
		// grey /= 2;
		grey = 15 - grey;
		if (grey < 2) grey = 2;
		// g.drawOval(v[p1]-1, v[p1 + 1]-1, v[p1]+1, v[p1 + 1]+1);
		g.drawOval(v[p1]-grey/2, v[p1 + 1]-grey/2,
			       grey, grey);
		if (i > 0) {
			if (ogrey < grey) g.setColor(green);
			g.drawLine(v[p1], v[p1 + 1],
					   v[0], v[0 + 1]);
		} else ogrey = grey;
		g.setColor(black);
		g.drawString(axesLabels[i],v[p1],v[p1+1]);
		}*/
		for (int i = 4; i < lim; i++) {
		    int p1 = i * 3;
		    int grey = (v[p1 + 2])/15;
		    if (grey < 0)
				grey = 0;
		    if (grey > 15)
				grey = 15;
		    if (grey != lg) {
				lg = grey;
				g.setColor(gr[grey]);
		    }
			// grey /= 2;
			grey = 15 - grey;
			if (grey < 2) grey = 2;
			
			if (CLRs != null) {
				int iCode = 0;
			    //  int j = ((T >> 16) & 0xFFFF)-4;
				int j = (c[i] & 0xFFFF)-4;
			    // int j = p2/3;
			    // System.out.println("i-3="+j+" color "+CLRs.getSymbol(j));
				if (CLRs.elementAt(j).equals(Color.red)) iCode = 1;
				if (CLRs.elementAt(j).equals(Color.blue)) iCode = 2;
				setColor(g,iCode);
			}
			
			
			//   g.drawOval(v[p1]-1, v[p1 + 1]-1, v[p1]+1, v[p1 + 1]+1);
		    g.fillOval(v[p1]-grey/2, v[p1 + 1]-grey/2, grey/2, grey/2);
			// g.drawRect(v[p1]-grey/2, v[p1 + 1]-grey/2, grey, grey);
			if (IDs == null)  g.draw3DRect(v[p1]-grey/2, v[p1 + 1]-grey/2, grey, grey, false);
			
			if (IDs != null) {
				String label = IDs.getSymbol(i-4);
				System.out.println(" the label "+label);
				int x = v[p1];
				//g.drawString(label,v[p1],v[p1+1]);
				int n = label.indexOf(",");
				while (n != -1){
					x = drawColorLabel(g,label.substring(0,n-1),x,v[p1+1]);
					setColor(g,-1);
					g.drawString(",",x,v[p1+1]);
					x++;
					label = label.substring(n+1,label.length());
					n = label.indexOf(",");
				}
				drawColorLabel(g,label,x,v[p1+1]);
				//g.drawString(s,v[p1]+s.length(),v[p1+1]);
				//g.drawString(IDs.getSymbol(i-4),v[p1],v[p1+1]);
			}
		}
		if (limc <= 0)
		    return;
		if (gred == null) {
		    gred = new Color[16];
			/*  for (int i = 0; i < 16; i++) {
			int grey = (int) (170*(1-Math.pow(i/15.0, 2.3)));
			gr[i] = new Color(grey, grey, grey);
		    }*/
		    for (int i = 0; i < 16; i++) {
				int grey = (int) (200*(1-Math.pow(i/15.0, 2.3)));
				grey = grey;
				gred[i] = new Color(grey, 0, 0);
		    }
		}
		
		for (int i = 3; i < limc; i++) {
		    int T = c[i];
			//		con[i] = (p1 << 16) | p2;
			
		    int p1 = ((T >> 16) & 0xFFFF) * 3;
		    int p2 = (T & 0xFFFF) * 3;
			
			/*	            if (CLRs != null) {
				int iCode = 0;
			//  int j = ((T >> 16) & 0xFFFF)-4;
			int j = (c[i] & 0xFFFF)-4;
			// int j = p2/3;
			// System.out.println("i-3="+j+" color "+CLRs.getSymbol(j));
			if (CLRs.getSymbol(j).equals("red")) iCode = 1;
			if (CLRs.getSymbol(j).equals("blue")) iCode = 2;
			switch (iCode){
				case 1:
					g.setColor(Color.red);
					break;
				case 2:
					g.setColor(Color.blue);
					break;
				default:
					g.setColor(Color.black);
					break;
			}
			}
			
			*/
            int shift = 0;
            if (p1 < p2) shift = ScaleManager.s(3);
            else shift = -ScaleManager.s(3);
			/*		    int grey = v[p1 + 2] + v[p2 + 2];
		    if (grey < 0)
				grey = 0;
		    if (grey > 15)
				grey = 15;
		    if (grey != lg) {
				lg = grey;
				g.setColor(gred[grey]);
				g.setColor(Color.red);
		    }*/
			// g.drawLine(v[p1], v[p1 + 1],
			//       v[p2], v[p2 + 1]);

			int x1,y1,x2,y2,delx,dely;
			double x,y,len,cos,sin,l;
			double n = ScaleManager.s(5) + 0.5; //factor for arrow barb length
			x1 = v[p1]; y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];
			x1 = x1+shift; x2 = x2+shift;
			len = Math.pow((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1),0.5);
			g.fillOval((int) Math.round(x1), (int) Math.round(y1), (int) Math.round(len/10), (int) Math.round(len/10));
			cos = (x2-x1)/len; sin = (y2-y1)/len;
			l = len/6;//arrow offset from vertex
				delx=(int) Math.round(l*cos);
				dely = (int) Math.round(l*sin);
				x1 = x1 + delx; y1 = y1+dely; x2 = x2 - delx; y2 = y2-dely;
				g.drawLine(x1,y1,x2,y2);
				x = (2-1.714/n)*(x2-x1)-(y2-y1)/n;
				y = (2-1.714/n)*(y2-y1)+(x2-x1)/n;
				x = x1+x/2; y = y1+y/2;
				//System.out.println("x1="+x1+"y1="+y1+"x2="+x2+"y2="+y2+"x="+x+" y="+y+" len="+len);
				g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));
				x = (2-1.714/n)*(x2-x1)+(y2-y1)/n;
				y = (2-1.714/n)*(y2-y1)-(x2-x1)/n;
				x = x1+x/2; y = y1+y/2;
				//System.out.println("x1="+x1+"y1="+y1+"x2="+x2+"y2="+y2+"x="+x+" y="+y+" len="+len);
				g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));
		}
    }


 void paint2(Graphics g) {
		if (vert == null || nvert <= 0)
		    return;
		transform();
		if (gr == null) {
		    gr = new Color[16];
		    for (int i = 0; i < 16; i++) {
			int grey = (int) (170*(1-Math.pow(i/15.0, 2.3)));
			gr[i] = new Color(grey, grey, grey);
		    }
		}
		int lg = 0;
		int lim = ncon;
		int c[] = con;
		int v[] = tvert;
		if (lim <= 0 || nvert <= 0)
		    return;
		for (int i = 0; i < lim; i++) {
		    int T = c[i];
		    int p1 = ((T >> 16) & 0xFFFF) * 3;
		    int p2 = (T & 0xFFFF) * 3;
		    int grey = v[p1 + 2] + v[p2 + 2];
		    if (grey < 0)
			grey = 0;
		    if (grey > 15)
			grey = 15;
		    if (grey != lg) {
			lg = grey;
			g.setColor(gr[grey]);
		    }
		    g.drawLine(v[p1], v[p1 + 1],
			       v[p2], v[p2 + 1]);
		}
    }

	 void paint(Graphics g) {
	 	paint0(g);
	}

    /** Find the bounding box of this model */
    void findBB() {
		if (nvert <= 0)
		    return;
		float v[] = vert;
		float xmin = v[0]; float xmax = xmin;
		float ymin = v[1]; float ymax = ymin;
		float zmin = v[2]; float zmax = zmin;
		for (int i = nvert * 3; (i -= 3) > 0;) {
			float x = v[i];
			if (x < xmin)
				xmin = x;
			if (x > xmax)
				xmax = x;
			float y = v[i + 1];
			if (y < ymin)
				ymin = y;
			if (y > ymax)
				ymax = y;
			float z = v[i + 2];
			if (z < zmin)
				zmin = z;
			if (z > zmax)
				zmax = z;
		}
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymax = ymax;
		this.ymin = ymin;
		this.zmax = zmax;
		this.zmin = zmin;
	}

	int height=100;
	int width=100;
	int drawOffsetX=0, drawOffsetY=0;
	float adjXscale=100;
	float adjYscale=100;
	float adjscale=100;
	int xMargin=ScaleManager.s(10), yMargin=ScaleManager.s(10);
	boolean marginsSet=false;

	public void setDrawOffset(int x, int y) {
		drawOffsetX = x;
		drawOffsetY = y;
	}

	public void setSize(int w, int h) {
		height=h;
		width=w;
		setMatBounds();
		marginsSet = false;
	}

	public void setMatBounds() {
		int h=height;
		int w=width;

		findBB();
		float rx = xmax - xmin;
		float ry = ymax - ymin;

		rx = (rx < 0 ? -rx : rx)+1;
		ry = ((int) ((ry < 0 ? -ry : ry)))+1;

		// Available drawing area after g.translate offset
		int aw = w - drawOffsetX;
		int ah = h - drawOffsetY;
		// Use uniform scale to preserve aspect ratio and center graph
		float scaleX = ((float) aw * 0.9f) / rx;
		float scaleY = ((float) ah * 0.9f) / ry;
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

    final static Integer DASH1 = new Integer(3);
    final static Integer DASH2 = new Integer(2);
	final static Integer SOLID = new Integer(1);
}
 /*
		cosa = 0.906; sina = 0.423;

		x = n/len*((x2-x1)*cosa - (y2-y1)*sina);
		y = n/len*((y2-y1)*cosa + (x2-x1)*sina);
		x = x2-x; y = y2-y;
		xArray[0] = (int) Math.round(x); yArray[0] = (int) Math.round(y);

		//x = (2-1.714/n)*(x2-x1)-(y2-y1)/n;
		//y = (2-1.714/n)*(y2-y1)+(x2-x1)/n;
		//x = x1+x/2; y = y1+y/2;
		//System.out.println("x1="+x1+"y1="+y1+"x2="+x2+"y2="+y2+"x="+x+" y="+y+" len="+len);
		//g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));

		x = n/len*((x2-x1)*cosa + (y2-y1)*sina);
		y = n/len*((y2-y1)*cosa - (x2-x1)*sina);
		x = x2-x; y = y2-y;
		xArray[1] = (int) Math.round(x); yArray[1] = (int) Math.round(y);
		xArray[2] = x2; yArray[2] = y2;
 		//x = (2-1.714/n)*(x2-x1)+(y2-y1)/n;
		//y = (2-1.714/n)*(y2-y1)-(x2-x1)/n;
	//	x = x1+x/2; y = y1+y/2;
		//System.out.println("x1="+x1+"y1="+y1+"x2="+x2+"y2="+y2+"x="+x+" y="+y+" len="+len);
		//g.drawLine(x2, y2, (int) Math.round(x), (int) Math.round(y));


		//Polygon p = new Polygon(xArray,yArray,3);
		//g.drawPolygon(p);
		g.drawPolygon(xArray,yArray,3);
		g.fillPolygon(xArray,yArray,3);
		*/
