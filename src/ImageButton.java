//package symantec.itools.awt;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.FilteredImageSource;
import java.net.URL;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;

//	01/18/97	RKM	Changed setImageURL to handle getImage returning null, added call to invalidate
//	01/29/97	TWB	Integrated changes from Macintosh
//	05/29/97	MSH	Updated to support Java 1.1
// 	07/08/97	LAB	Changed the way the button is drawn.  Now it uses the offscreen
//					Image.  Cleaned up the drawing code to be more universal.
//					Added pressed and disabled images for those states.
//					Changed data member url to imageURL.
//					Added a preferedSize() method.
//	07/13/97	RKM	Fixed misspelling of prefered
//  07/30/97    CAR marked fields transient as needed
//                  implemented readObject
// 	08/05/97	LAB	Added a call to clipRect in the updateButtonImage method to clip subsequent
//					drawings to the internal button area (sans border and bevel). This should be
//					removed when the VM interprets the base class' call to clipRect correctly.
//					removed scale and center protected data members.  Added imageStyle protected
//					data member.  Added public data members to define ImageStyles.  Deprecated
//					setScaleMode, isScaleMode, setCenterMode, and isCenterMode.  Added
//					ImageStyle property.
// 	08/06/97	LAB	Removed the call to clipRect; now uses the base classes buttonImageGraphics
//					Graphics to draw with, which inherits the clipping.
//  08/28/97    CAR "erase" image if Image URL is set to null

/**
 * The ImageButton component is similar to a regular button except that it
 * displays an image on the button's face. The image to use is specified with
 * a URL.
 * <p>
 * Use an ImageButton to:
 * <UL>
 * <DT>· Display an image in a button instead of text.</DT>
 * <DT>· Generate a train of action events while the user presses the button.</DT>
 * </UL>
 * <p>
 * @version 1.1, August 5, 1997
 * @author Symantec
 */
public class ImageButton extends ButtonBase
{
    /**
     * A constant indicating the image is to be tiled in the size of this component.
     */
    public static final int IMAGE_TILED = 0;
    /**
     * A constant indicating the image is to be centered in the size of this component.
     */
    public static final int IMAGE_CENTERED = 1;
    /**
     * A constant indicating the image is to be scaled to fit the size of this component.
     */
    public static final int IMAGE_SCALED_TO_FIT = 2;
    /**
     * A constant indicating the image is to be drawn normally in the upper left corner.
     */
    public static final int IMAGE_NORMAL = 3;

    /**
     * Constructs a new default ImageButton. Image scaling is off and center mode on.
     */
    public ImageButton()
    {
		try
		{
			setImageStyle(IMAGE_CENTERED);
		}
		catch (PropertyVetoException exc) {}
    }

	public void setResourceURL(String resource) {
		try {
			setImageURL(getClass().getResource(resource));
		} catch (Exception e) {
			
		}
		
	}
    /**
     * Sets the URL of the image to display in the button.
     * @param u the URL of the image to display
     * @see #getImageURL
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
    public void setImageURL(URL u) throws PropertyVetoException
    {
    	URL oldValue = imageURL;

//    	vetos.fireVetoableChange( "ImageURL", oldValue, u );

    	// Remove old images
    	enabledImage = null;
    	if (disabledImage != null)
    		disabledImage.flush();
    	disabledImage = null;
    	if (pressedImage != null)
    		pressedImage.flush();
    	pressedImage = null;

    	// Load new image

        imageURL = u;
        if (imageURL != null) {
            Image image = getToolkit().getImage(imageURL);
            if (image != null)
            {
    	        MediaTracker mt = new MediaTracker(this);
    			if (mt != null)
    			{
    		        try
    		        {
    		            mt.addImage(image, 0);
    		            mt.waitForAll();
    		        }
    		        catch (Exception ie)
    		        {
    		        }

    		        if (mt.isErrorAny())
    		        {
    		            System.err.println("Error loading image " + image.toString());
    		            return;
    		        }

    		        enabledImage	= image;
    		        disabledImage	= createImage(new FilteredImageSource(image.getSource(), new FadeFilter(0.333)));
    		        pressedImage	= createImage(new FilteredImageSource(image.getSource(), new DarkenFilter(0.250)));

 //   		        changes.firePropertyChange( "ImageURL", oldValue, u );
    			}
            }
        }
        repaint();
    }

    /**
     * Returns the URL of the image being displayed in the button.
     * @see #setImageURL
     */
    public URL getImageURL()
    {
        return imageURL;
    }

    /**
     * Sets the new panel image style.
     * @param newStyle the new panel image style, one of
     * IMAGE_TILED, IMAGE_CENTERED, IMAGE_SCALED_TO_FIT, or IMAGE_NORMAL.
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getImageStyle
     * @see #IMAGE_TILED
     * @see #IMAGE_CENTERED
     * @see #IMAGE_SCALED_TO_FIT
     * @see #IMAGE_NORMAL
     */
	public void setImageStyle(int newStyle) throws PropertyVetoException
	{
		if (newStyle != imageStyle)
		{
			Integer oldValue = new Integer(imageStyle);
			Integer newValue = new Integer(newStyle);

//			vetos.fireVetoableChange("ImageStyle", oldValue, newValue);

			imageStyle = newStyle;
	        repaint();

//	        changes.firePropertyChange("ImageStyle", oldValue, newValue);
		}
	}

    /**
     * Gets the current panel image style.
     * @return the current panel image style, one of
     * IMAGE_TILED, IMAGE_CENTERED, IMAGE_SCALED_TO_FIT, or IMAGE_NORMAL.
     * @see #setImageStyle
     * @see #IMAGE_TILED
     * @see #IMAGE_CENTERED
     * @see #IMAGE_SCALED_TO_FIT
     */
	public int getImageStyle()
	{
		return imageStyle;
	}

    /**
     * @deprecated
     * @see #setImageStyle
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
    public void setScaleMode(boolean flag) throws PropertyVetoException
    {
    	if (flag)
    	{
    		setImageStyle(IMAGE_SCALED_TO_FIT);
    	}
    	else
    	{
    		setImageStyle(IMAGE_NORMAL);
    	}
    }

    /**
     * @deprecated
     * @see #getImageStyle
     */
    public boolean isScaleMode()
    {
        return (getImageStyle() == IMAGE_SCALED_TO_FIT);
    }

    /**
     * @deprecated
     * @see #isScaleMode
     */
    public boolean getScaleMode()
    {
        return isScaleMode();
    }

    /**
     * @deprecated
     * @see #setImageStyle
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     */
    public void setCenterMode(boolean flag) throws PropertyVetoException
    {
    	if (flag)
    	{
    		setImageStyle(IMAGE_CENTERED);
    	}
    	else
    	{
    		setImageStyle(IMAGE_NORMAL);
    	}
    }

    /**
     * @deprecated
     * @see #getImageStyle
     */
    public boolean isCenterMode()
    {
        return (getImageStyle() == IMAGE_CENTERED);
    }

    /**
     * @deprecated
     * @see #isCenterMode
     */
    public boolean getCenterMode()
    {
        return isCenterMode();
    }

    /**
     * Is the given image style valid for this button.
     * @param i the given image style
     * @return true if the given image style is acceptable, false if not.
     */
    public boolean isValidImageStyle(int i)
    {
    	switch(i)
    	{
    	   	case IMAGE_TILED:
	    	case IMAGE_CENTERED:
	    	case IMAGE_SCALED_TO_FIT:
	    	case IMAGE_NORMAL:
	    		return true;
	    	default:
	    		return false;
	    }
    }

	/**
	 * Returns the recommended dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the recommended size of this component.
	 */
    public Dimension getPreferredSize()
    {
    	Dimension defaultSize = super.getPreferredSize();

		if (enabledImage == null)
			return defaultSize;

		return new Dimension(defaultSize.width + enabledImage.getWidth(this), defaultSize.height + enabledImage.getHeight(this));
    }

	/**
	 * Tells this component that it has been added to a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is added to a container. Typically, it is used to
	 * create this component's peer.
	 *
	 * It has been overridden here to hook-up event listeners.
	 *
	 * @see #removeNotify
	 */
	public synchronized void addNotify()
	{
		super.addNotify();

		//Hook up listeners
		if (styleVeto == null)
		{
			styleVeto = new StyleVeto();
			addImageStyleListener(styleVeto);
		}
	}

	/**
	 * Tells this component that it is being removed from a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is removed from a container. Typically, it is used to
	 * destroy the peers of this component and all its subcomponents.
	 *
	 * It has been overridden here to unhook event listeners.
	 *
	 * @see #addNotify
	 */
	public synchronized void removeNotify()
	{
		//Unhook listeners
		if (styleVeto != null)
		{
			removeImageStyleListener(styleVeto);
			styleVeto = null;
		}

		super.removeNotify();
	}

    /**
     * Adds a listener for all event changes.
     * @param listener the listener to add.
     * @see #removePropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	super.addPropertyChangeListener(listener);
//    	changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for all event changes.
     * @param listener the listener to remove.
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	super.removePropertyChangeListener(listener);
//    	changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds a vetoable listener for all event changes.
     * @param listener the listener to add.
     * @see #removeVetoableChangeListener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
		super.addVetoableChangeListener(listener);
//		vetos.addVetoableChangeListener(listener);
    }

    /**
     * Removes a vetoable listener for all event changes.
     * @param listener the listener to remove.
     * @see #addVetoableChangeListener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
    	super.removeVetoableChangeListener(listener);
//    	vetos.removeVetoableChangeListener(listener);
    }

    /**
     * Adds a listener for the ImageStyle property changes.
     * @param listener the listener to add.
     * @see #removeImageStyleListener(java.beans.PropertyChangeListener)
     */
    public void addImageStyleListener(PropertyChangeListener listener)
    {
//    	changes.addPropertyChangeListener("ImageStyle", listener);
    }

    /**
     * Removes a listener for the ImageStyle property changes.
     * @param listener the listener to remove.
     * @see #addImageStyleListener(java.beans.PropertyChangeListener)
     */
    public void removeImageStyleListener(PropertyChangeListener listener)
    {
//    	changes.removePropertyChangeListener("ImageStyle", listener);
    }

    /**
     * Adds a vetoable listener for the ImageStyle property changes.
     * @param listener the listener to add.
     * @see #removeImageStyleListener(java.beans.VetoableChangeListener)
     */
    public void addImageStyleListener(VetoableChangeListener listener)
    {
//    	vetos.addVetoableChangeListener("ImageStyle", listener);
    }

    /**
     * Removes a vetoable listener for the ImageStyle property changes.
     * @param listener the listener to remove.
     * @see #addImageStyleListener(java.beans.VetoableChangeListener)
     */
    public void removeImageStyleListener(VetoableChangeListener listener)
    {
 //   	vetos.removeVetoableChangeListener("ImageStyle", listener);
    }

	/**
	 * This is the PropertyChangeEvent handling inner class for the constrained ImageStyle property.
	 * Handles vetoing BevelHeights that are not valid.
	 */
	class StyleVeto implements java.beans.VetoableChangeListener, java.io.Serializable
	{
	    /**
	     * This method gets called when an attempt to change the constrained ImageStyle property is made.
	     * Ensures the given image style is valid for this button.
	     *
	     * @param     e a <code>PropertyChangeEvent</code> object describing the
	     *   	      event source and the property that has changed.
	     * @exception PropertyVetoException if the recipient wishes the property
	     *              change to be rolled back.
	     */
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	    {
	    	int i = ((Integer)e.getNewValue()).intValue();
	        if (!isValidImageStyle(i))
	        {
	            throw new PropertyVetoException("Invalid image style: " + i, e);
	        }
	    }
	}

	/**
	 * Draws the button in the buttonImage offscreen image.
	 * @see symantec.itools.awt.ButtonBase#updateButtonImage
	 */
	protected void updateButtonImage()
	{
		super.updateButtonImage();

		Image img;

		if(pressed)
			img = pressedImage;
		else
			img = isEnabled() ? enabledImage : disabledImage;


        Dimension s		= size();
        int x			= bevel + 1 + pressedAdjustment;
      	int y			= bevel + 1 + pressedAdjustment;
        int w			= s.width	- bevel - bevel - 2;
        int h			= s.height	- bevel - bevel - 2;

		if (img == null) {
			buttonImageGraphics.clearRect(x, y, w, h);
			return;
		}

		int imageWidth	= img.getWidth(this);
		int imageHeight	= img.getHeight(this);

		switch(imageStyle)
		{
			case IMAGE_CENTERED:
			default:
			{
				buttonImageGraphics.drawImage
					(img, x + ((w - imageWidth) / 2), y + ((h - imageHeight) / 2), this);

				break;
			}
			case IMAGE_TILED:
			{
				//Calculate number of images that should be drawn horizontally
				int numHImages = w / imageWidth;

				//Don't forget remainders
				if (w % imageWidth != 0)
					numHImages++;

				//Calculate number of images that should be drawn vertically
				int numVImages = h / imageHeight;

				//Don't forget remainders
				if (h % imageHeight != 0)
					numVImages++;

				int hOff;
				int vOff = y;
				for (int vCount = 0; vCount < numVImages; vCount++)
				{
					hOff = x;
					for (int hCount = 0; hCount < numHImages; hCount++)
					{
						buttonImageGraphics.drawImage(img, hOff, vOff, imageWidth, imageHeight, this);

						//Increment to next column
						hOff += imageWidth;
					}

					//Increment to next row
					vOff += imageHeight;
				}
				break;
			}
			case IMAGE_SCALED_TO_FIT:
			{
				buttonImageGraphics.drawImage(img, x, y, w, h, this);
				break;
			}
			case IMAGE_NORMAL:
			{
				buttonImageGraphics.drawImage(img, x, y, this);
				break;
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
	{
	    in.defaultReadObject();

        if (imageURL != null) {
            Image image = getToolkit().getImage(imageURL);
            if (image != null)
            {
    	        MediaTracker mt = new MediaTracker(this);
    			if (mt != null)
    			{
    		        try
    		        {
    		            mt.addImage(image, 0);
    		            mt.waitForAll();
    		        }
    		        catch (Exception ie)
    		        {
    		        }

    		        if (mt.isErrorAny())
    		        {
    		            System.err.println("Error loading image " + image.toString());
    		            return;
    		        }

    		        enabledImage	= image;
    		        disabledImage	= createImage(new FilteredImageSource(image.getSource(), new FadeFilter(0.333)));
    		        pressedImage	= createImage(new FilteredImageSource(image.getSource(), new DarkenFilter(0.250)));
    			}
            }
        }

	}

    /**
     * Determines how to draw the image.
     */
	protected int imageStyle;

	/**
	 * The URL for the button's image
	 */
    protected URL imageURL = null;

	/**
	 * The normally displayed image
	 */
    transient protected Image enabledImage = null;

	/**
	 * The image displayed when the button is disabled
	 */
    transient protected Image disabledImage	= null;

	/**
	 * The image displayed when the button is pressed
	 */
    transient protected Image pressedImage = null;

    private StyleVeto styleVeto = null;
//	private symantec.itools.beans.VetoableChangeSupport vetos   = new symantec.itools.beans.VetoableChangeSupport(this);
//    private symantec.itools.beans.PropertyChangeSupport changes = new symantec.itools.beans.PropertyChangeSupport(this);

}