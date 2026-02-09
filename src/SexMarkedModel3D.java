import java.awt.Graphics;
import java.awt.Color;


/* History
* 2/27 DR made node radius an absolute value and not scaled to arrow length
* 3/3 DR added  Boolean split condition for bifurcating element nodes
* added call to drawArcArrow for reflexive elements
* 11/7 DR added procedures doubleArrow, firstArrow, sameDirection to handle cases
* where more than one arrow connects a pair of points
* 6/18 DR partially activated option for setting radius value of an indiviudal node
*/

public class SexMarkedModel3D extends Model3D{

    public SexMarkedModel3D(){
        super();
    }

    KintermEntry ke = new KintermEntry();

    boolean inList(int x, ListVector xv) {
        boolean flag = false;
        for (xv.reset();xv.isNext();)
            if (((Integer) xv.getNext()).intValue() == x) {
                flag = true;
                break;
            }
        return flag;
    }

    int[][] pairs;

    boolean doubleArrow(int p1, int p2){
        return ((Math.abs(pairs[p1][p2])==1 && Math.abs(pairs[p2][p1])==1)||
                        (Math.abs(pairs[p1][p2]) > 1));
    }

    boolean firstArrow(int p1, int p2) {
	    boolean flag = (pairs[p1][p2]+pairs[p2][p1]>0);
	    if (flag) pairs[p1][p2] = -pairs[p1][p2];
        return flag;
    }

    boolean sameDirection(int p1, int p2) {
        return (pairs[p1][p2] < 0);
    }

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
      int x1,x2,y1,y2;
      double len = 0.0;
      int offset= ScaleManager.s(10); //offset from node for label
      int os = 0;
      int radius = ScaleManager.s(5);//radius of a node
      int d = ScaleManager.s(5); //distance between double arrows
      ListVector xv = new ListVector();
	  ListVector splitV = new ListVector();

      pairs = new int[lim][lim];
      for (int i=0;i<lim;i++)
        for (int j=0;j<lim;j++)
          pairs[i][j]=0;

      for (int i = 3; i < limc; i++) {
        T = c[i];
        p1 = ((T >> 16) & 0xFFFF) * 3;
        p2 = (T & 0xFFFF) * 3;
        pairs[p1/3][p2/3]++;
      }

      for (int i = 4; i < lim; i++) {//do sex marked first
        if (CLRs != null && CLRs.elementAt(i-4)!= ke.getSexColour(ke.NEUTRAL)) {
          p1 = i * 3;
          os = offset+2*radius;//vertical offset for 2nd label for split point
          int del = 0;
          int x = v[p1] << 16 | v[p1+1];
          int rad = ((Integer)RADs.elementAt(i-4)).intValue();
          if (!inList(x,xv)) {
            Integer xi = new Integer(x);
            xv.addElement(xi);
            os = 0;
          } else del = 5*radius;//distance between split points
          g.setColor((Color) CLRs.elementAt(i-4));//draw node
          if (IDs != null && labelFlag) {//draw label
		            //g.setColor(Color.black);
            g.drawString(IDs.getSymbol(i-4),v[p1]+offset-0*radius,v[p1+1]+os/2-(0+6)*radius/2);//0 for akt Check this
			//out why 0 for akt and how to make this general see below as well 0 was 3 before
          }
			   // g.fillOval(v[p1]+ os-2*radius, v[p1+1], radius, radius);
          if (rad != 0)
            g.fillOval(v[p1]-radius + del, v[p1+1], radius, radius);
        }
      }
      for (int i = 4; i < lim; i++) {//do neutral next
        if (CLRs != null && CLRs.elementAt(i-4)== ke.getSexColour(ke.NEUTRAL)) {
          p1 = i * 3;
          int x = v[p1] << 16 | v[p1+1];
          int rad = ((Integer)RADs.elementAt(i-4)).intValue();
          if (!inList(x,xv)) {//draw neutral node
            if (IDs != null && labelFlag) {//draw label
              g.setColor(Color.black);
              g.drawString(IDs.getSymbol(i-4),v[p1]+offset-0*radius,v[p1+1]+os/2-(0+4)*radius/2);// 0 for akt
            }
            g.setColor((Color) CLRs.elementAt(i-4));//draw node
            if (rad != 0){
              g.fillOval(v[p1], v[p1+1], radius, radius);}
		  } else {
			g.setColor((Color) CLRs.elementAt(i-4));//draw node
			if (rad != 0)
			  g.drawOval(v[p1]-offset, v[p1+1]-(radius), (5+3)*radius, (2+1)*radius);//oval around equivalent nodes
		  }
        }
      }
      if (limc <= 0) return;
      int delx,dely;
      boolean split = false;
	  int yMin = 1000;
      for (int i = 3; i < limc; i++) {//get coords for split points, minimum y coord
        T = c[i];
        p1 = ((T >> 16) & 0xFFFF) * 3;
        p2 = (T & 0xFFFF) * 3;
        if (CLRcon != null) { //arrow color
          split = ((Boolean)CLRcon.elementAt(3*(i-3)+2)).booleanValue();
        }
        x1 = v[p1]; y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];
		if (y1 < yMin) yMin = y1;
        if ((split) && (x1==x2 && y1==y2)) {//horizontal arrows for bifurcated point
		  Integer x1i = new Integer(v[p1]);
		  splitV.addElement(x1i);
		  Integer y1i = new Integer(v[p1+1]);
		  splitV.addElement(y1i);
        }
      }
      for (int i = 3; i < limc; i++) {
        T = c[i];
        p1 = ((T >> 16) & 0xFFFF) * 3;
        p2 = (T & 0xFFFF) * 3;
        if (CLRcon != null) { //arrow color
          g.setColor((Color) CLRcon.elementAt(3*(i-3)));
          split = ((Boolean)CLRcon.elementAt(3*(i-3)+2)).booleanValue();
        }
        x1 = v[p1]; y1 = v[p1+1]; x2 = v[p2]; y2 = v[p2+1];
        if ((split) && (x1==x2 && y1==y2)) {//horizontal arrows for bifurcated point
          x1 = x1-radius+2+radius;
          x2 = x2+offset-2*radius-2+  4*radius;
          y1 = y1 + radius/2+2; y2 = y2+radius/2+2;
          drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(3*(i-3)+1));
			   // drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(2*(i-3)+1));
          x2 = v[p2]-radius+2+radius;
          x1 = v[p1]+offset-2*radius-2 +4*radius;
          y1 = y1 -3; y2 = y2-3;
         drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(3*(i-3)+1));
        } else if (p1 == p2) {

          if (doubleArrow(p1/3,p2/3) && !firstArrow(p1/3,p2/3))
            x2 = x2 + 2*radius;
			    //((x1==x2 && y1==y2)) {
			   // drawArcArrow(g,x2,y1,(Integer)CLRcon.elementAt(2*(i-3)+1));
          drawArcArrow(g,x2,y1,(Integer)CLRcon.elementAt(3*(i-3)+1));
        } else {

          int n1 = p1/3;int n2 = p2/3;
          Integer aType = (Integer) CLRcon.elementAt(3*(i-3)+1);
          if (aType.intValue() < 0){aType = new Integer(-aType.intValue());}

          if (doubleArrow(n1,n2)){
            int cn = (int) Math.round(d*cos(x1,y1,x2,y2));
            int sn = (int) Math.round(d*sin(x1,y1,x2,y2));
            sn = -Math.abs(sn);
            boolean sameFlag = sameDirection(n1,n2);
                    //if (CLRcon.elementAt(3*(i-3)) == new Integer(2)) {
            //if (firstArrow(n1,n2)){//done.check this out see why next line does not work with spouse arrows
            if (aType.equals(SOLID)){
              y1 = y1-cn; y2 = y2-cn;
              x1 = x1-sn; x2 = x2-sn;
            }else {

              if (sameFlag){x1 = x1+sn; x2 = x2+sn;}
              else {x1 = x1+cn; x2 = x2+cn;}
              sn = Math.abs(sn);
              y1 = y1+sn; y2 = y2+sn;
            }
          }

    //			drawArrow(g,x1,y1,x2,y2,(Integer) CLRcon.elementAt(2*(i-3)+1));
		  int delx1 = 0;
		  int delx2 = 0;
		  if ((aType.equals(SOLID)) && inList(v[p1],splitV) && inList(v[p1+1],splitV) && (yMin == v[p1+1])) {
			delx1 = 4*radius+3;
			delx2 = -3;
		  }
          drawArrow(g,x1+delx1,y1,x2+delx2,y2,(Integer) CLRcon.elementAt(3*(i-3)+1));
        }
      }
    }
}