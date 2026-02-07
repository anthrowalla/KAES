import java.awt.Panel;
import java.awt.Rectangle;

public class MarchingAnts implements Runnable {
    java.awt.Graphics g;

    Panel c=null;
	protected LineObject selectedLine=null;

    int x1, y1, w, h, offset;

    boolean drawLine;
	Thread myThread;

    public MarchingAnts() {
        offset=0;
    }


	public void initBorder(Rectangle r, Panel kEdit) {
		initBorder(r.x,r.y,r.width,r.height,kEdit);
	}


    public synchronized void initBorder(int x, int y, int wid, int hei, Panel
        kEdit) {
        x1=x;
        y1=y;
        w=wid;
        h=hei;
        c=kEdit;
        g=c.getGraphics();
        drawBorder();
    }

	public void updateBorder(Rectangle r) {
		updateBorder(r.x,r.y,r.width,r.height);
	}

    public synchronized void updateBorder(int x, int y, int wid, int hei) {
    	java.awt.Color oc = g.getColor();
        g.setColor(java.awt.Color.white);
      	g.drawRect(x1, y1, w-1, h-1);
        g.setColor(oc);
        x1=x;
        y1=y;
        w=wid;
        h=hei;
        drawBorder();
    }
    
    public void run() {
        while(true) {
            //DrawingInfo.antsMarching=true;
            try { Thread.sleep(200); } catch(Exception e) {}
            offset+=ScaleManager.s(2);
            if(offset>ScaleManager.s(9)) offset=0;
			if (!block) {
				if (c != null) drawBorder();
				if (selectedLine != null) hilightLine();
			}
        }
    }

	public void initThread() {
		myThread = new Thread(Thread.currentThread().getThreadGroup(),this, "MarchingAntsThread");
        myThread.start();
		block = false;
      //  myThread.suspend();
	}

	boolean block = true;
	public synchronized void dorment() {
		block = true;
	//	myThread.suspend();
	}
	
	public synchronized void active() {
		block = false;
	//	myThread.resume();
	}
	
	public void stop() {
		block = true;
		myThread.stop();
	}
	
    public synchronized void drawBorder() {
        int i, j;
		//c.invalidate();
        
        j=offset;
        drawLine=true;
        g.setColor(java.awt.Color.white);
      	g.drawRect(x1, y1, w-1, h-1);
         
        g.setColor(java.awt.Color.black);
        // We're about to erase the old border
        //g.drawImage(offScreenImage, x1, y1, x1+1, y1+h, x1, y1, x1+1, y1+h, this);
        //g.drawImage(offScreenImage, x1+w-1, y1, x1+w, y1+h, x1+w-1, y1, x1+w, y1+h, this);
        //g.drawImage(offScreenImage, x1, y1, x1+w, y1+1, x1, y1, x1+w, y1+1, this);
        //g.drawImage(offScreenImage, x1, y1+h-1, x1+w, y1+h, x1, y1+h-1, x1+w, y1+h, this);

        int step = ScaleManager.s(5);
        for(i=x1+j;i<x1+w;i+=step) {
            if(i+step<x1+w&&drawLine) {
                g.drawLine(i, y1, i+step, y1);
                j=0;
            }
            else {
                j=i+step-(x1+w);
            }
            drawLine=!drawLine;
        }
        drawLine=!drawLine;
        if(j>0&&drawLine) g.drawLine(x1+w-1, y1, x1+w-1, y1+j-1);
        for(i=y1+j; i<y1+h; i+=step) {
            if(i+step<y1+h&&drawLine) {
                g.drawLine(x1+w-1, i, x1+w-1, i+step);
                j=0;
            }
            else {
                j=i+step-(y1+h);
            }
            drawLine=!drawLine;
        }
        drawLine=!drawLine;
        if(j>0&&drawLine) g.drawLine(x1+w-1, y1+h-1, x1+w-j, y1+h-1);
        for(i=x1+w-j-1;i>x1-1;i-=step) {
            if(i-step>x1-1&&drawLine) {
                g.drawLine(i, y1+h-1, i-step, y1+h-1);
                j=0;
            }
            else {
                j=x1-(i-step);
            }
            drawLine=!drawLine;
        }
        drawLine=!drawLine;
        if(j>0&&drawLine) g.drawLine(x1, y1+h-1, x1, y1+h-j);
        for(i=y1+h-j-1;i>y1-1;i-=step) {
            if(i-step>y1-1&&drawLine) {
                g.drawLine(x1, i, x1, i-step);
                j=0;
            }
            else {
                j=y1-(i-step);
            }
            drawLine=!drawLine;
        }
        drawLine=!drawLine;
        if(j>0&&drawLine) g.drawLine(x1, y1, x1+j-1, y1);
       // g.dispose();
    }

	public synchronized void hilightLine() {
		if (selectedLine == null) return;
		selectedLine.paintHilight(g);
	}

	public synchronized void setSelectedLine(LineObject selectedLine) {
		if (selectedLine != null) clearSelectedLine();
		this.selectedLine = selectedLine;
	}

	public LineObject getSelectedLine() {
		return selectedLine;
	}

	public synchronized void clearSelectedLine() {
		if (selectedLine != null) selectedLine.paintSelected(g);
		selectedLine=null;
	}
}
