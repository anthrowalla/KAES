//
//  ImagePanel.java
//  Kaes
//
//  Created by Michael Fischer on Sun Aug 11 2002.
//  Copyright (c) 2002 __MyCompanyName__. All rights reserved.
//
import java.awt.*;
import java.net.*;

public class ImagePanel extends java.awt.Canvas {

	URL imageURL;
	Image enabledImage=null;

	public ImagePanel() {

	}

	public ImagePanel(String url) {
		try {
			imageURL = new URL(url);
		} catch (java.net.MalformedURLException e) {

		}
		setImageURL(imageURL);
	}

	public ImagePanel(String path, String url) {
		try {
			if (path.equals("java")) {
				imageURL = getClass().getResource(url);
			} else
				imageURL = new URL("file","",url);
		} catch (java.net.MalformedURLException e) {

		}
		setImageURL(imageURL);
	}
	
	public ImagePanel(URL url) {
		setImageURL(url);
	}
	
    public void update(Graphics g)
    {
        paint(g);
    }

	public void paint(Graphics g)
    {
		if (enabledImage != null)
			g.drawImage(enabledImage, 0, 0, getSize().width, getSize().height, this);
    }
	
	public void setImageURL(URL u) {
		URL oldValue = imageURL;
	
		enabledImage = null;
	
		imageURL = u;
		if (imageURL != null) {
			Image image = getToolkit().getImage(imageURL);
			if (image != null) {
				MediaTracker mt = new MediaTracker(this);
				if (mt != null) {
					try {
						mt.addImage(image, 0);
						mt.waitForAll();
					} catch (Exception ie) {
					}
	
					if (mt.isErrorAny()) {
						System.err.println("Error loading image " + image.toString());
						return;
					}
					enabledImage = image;
				}
			}
		}
		repaint();
	}

}
