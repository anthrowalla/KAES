//package symantec.itools.awt.image;

import java.awt.image.RGBImageFilter;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.lang.IllegalArgumentException;

// Written by Levi Brown and Micheal Hopkins 1.1, July 8, 1997.

/**
 * An Image filter to use for lightening an Image a specified percentage.
 * @version 1.1, July 8, 1997
 * @author  Symantec
 */
public class LightenFilter extends RGBImageFilter
{
	/**
	 * Constructs a default LightenFilter.
	 * By default the Image is lightened 50%.
	 * @see #LightenFilter(double)
	 * @see #setPercent
	 */
	public LightenFilter()
	{
		this(0.50);
	}

	/**
	 * Constructs a LightenFilter.
	 * @param percent the percent to lighten the image when filtering.
	 * @see #LightenFilter()
	 * @see #setPercent
	 */
	public LightenFilter(double percent)
	{
		canFilterIndexColorModel = true;
		try
		{
			setPercent(percent);
		}
		catch (IllegalArgumentException exc)
		{
			System.err.println("LightenFilter: Invalid parameter value passed to constructor:");
			System.err.println("     " + percent + " is not a valid percentage value. It should be <= 1 && >= 0");
			System.err.println("     Defaulting to 0.50.");
			try { setPercent(0.50); } catch (IllegalArgumentException exc2) {}
		}
	}

	/**
	 * Sets the percentage to fade when filtering.
	 * @param percent the percentage to fade
	 * @exception IllegalArgumentException
	 * if the specified percentage value is unacceptable
	 * @see #getPercent
	 */
	public void setPercent(double percent) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		this.percent = percent;
	}

	/**
	 * Gets the percentage to fade when filtering.
	 * @return the percentage to fade
	 * @see #setPercent
	 */
	public double getPercent()
	{
		return percent;
	}

	/**
	 * Filters an RGB value by the current lighten percentage.
	 * @param x unused
	 * @param y unused
	 * @param rgb the rgb value to lighten
	 * @return the lightened rgb value
	 */
	public int filterRGB( int x, int y, int rgb )
	{
		DirectColorModel cm = (DirectColorModel)ColorModel.getRGBdefault();

		int alpha = cm.getAlpha(rgb);
		int red   = cm.getRed(rgb);
		int green = cm.getGreen(rgb);
		int blue  = cm.getBlue(rgb);

		red		+= (int)((255 - red)	* percent );
		green	+= (int)((255 - green)	* percent );
		blue	+= (int)((255 - blue)	* percent );

		alpha	= alpha << 24;
		red		= red   << 16;
		green	= green << 8;

		return alpha | red | green | blue;
	}

	/**
	 * The percentage to lighen when filtering.
	 * @see #getPercent
	 * @see #setPercent
	 */
	protected double percent;
}