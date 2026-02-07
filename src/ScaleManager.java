//
//  ScaleManager.java
//  Kaes
//
//  Created 2026. Provides display scaling for HiDPI screens.
//

import java.awt.*;

public class ScaleManager {
	private static int scaleFactor = 1;

	// Scale a pixel value by the current scale factor
	public static int s(int pixels) {
		return pixels * scaleFactor;
	}

	// Create a font with scaled size
	public static Font font(String name, int style, int size) {
		return new Font(name, style, size * scaleFactor);
	}

	public static void setScaleFactor(int f) {
		if (f == 1 || f == 2 || f == 4) {
			scaleFactor = f;
		}
	}

	public static int getScaleFactor() {
		return scaleFactor;
	}

	// Initialize from preferences - call before any windows are created
	public static void initFromPreferences() {
		Preferences pf = Preferences.loadPrefs("KAESPrefs.xml");
		if (pf == null) {
			MainFrame.prefs = new Preferences();
		} else {
			MainFrame.prefs = pf;
		}
		scaleFactor = MainFrame.prefs.getInt("Display_Scale_Factor", 1);
	}
}
