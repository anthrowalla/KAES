//
//  InitPreferences.java
//  Kaes
//
//  Created by Michael Fischer on Mon Aug 02 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

public class InitPreferences {

	static void init(Preferences p) {
		MainFrame.prefs.getInt("Maximum_ascending_links",2);
		MainFrame.prefs.getInt("Maximum_descending_links",2);
		MainFrame.prefs.getInt("Maximum_collateral_links",2);
		MainFrame.prefs.getInt("Maximum_affinal_links",1);
		MainFrame.prefs.getInt("Maximum_product_size",7);
		MainFrame.prefs.getString("Terminology_type-_Trobriand_or_Tongan","Trobriand");				// destructive
		MainFrame.prefs.getBoolean("Print_ego_in_Kintype_Products_Table",false);
		MainFrame.prefs.getBoolean("Print_undefined_kintype_products_in_Kintype_Products_Table",true);
		MainFrame.prefs.getBoolean("Do_not_simplify_kintype_products_in_Kintype_Products_Table",false);
		MainFrame.prefs.getInt("Display_Scale_Factor",1);
		// p.putPreference("name","value");
		// p.putPreference("name",1);
		
		// apply only if not present
		// p.getString("name","defaultvalue");
		// p.getInt("name",1);
		
	}
}
