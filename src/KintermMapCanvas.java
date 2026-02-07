//
//  KintermMapCanvas.java
//  Kaes
//
//  Created by Michael Fischer on Mon Aug 12 2002.
//  Copyright (c) 2002 __MyCompanyName__. All rights reserved.
//
import java.awt.*;

public class KintermMapCanvas extends Canvas {
	Image refImage=null;
	Rectangle fragment;
	public boolean release = false;
	int dx,dy;
	
	public void setImage(Image i, Rectangle frag) {
		refImage = i;
		fragment = frag;
		int fw = fragment.width-fragment.x;
		int fh = fragment.height-fragment.y;
		setDxDy(fw,fh);
	}
	
	public void setImage(Image i) {
		refImage = i;
		release = false;
		for(;;) {
			int fh = refImage.getHeight(this);
			int fw = refImage.getWidth(this);
			if (fh != -1 && fw != -1) {
				fragment = new Rectangle(0,0,fw,fh);
				setDxDy(fw,fh);
				return;
			}
			if (release) return;
		}
	}

	public void setDxDy(int fw, int fh) {
		if (fw > getSize().width) {
			dx = getSize().width;
			dy = (int) (((float) fh) / (((float)getSize().width)/ ((float)fw)));
			if (dy > getSize().height) {
				dy = getSize().height;
			}
		}

	}
	
	public void paint(Graphics g) {
		if (refImage == null) return;
		g.drawImage(refImage,0,0,dx, dy,
			fragment.x, fragment.y, fragment.width, fragment.height,
			Color.white, this);
	}
}
