//
//  Preferences.java
//  Kaes
//
//  Created by Michael Fischer on Sun Aug 11 2002.
//  Copyright (c) 2002  Michael Fischer . All rights reserved.
//
import java.util.*;
import java.io.*;
// import org.csac.xml.*;

public class Preferences extends Hashtable implements ToXML{

	public void putPreference(String name, String value) {
		put(name,value);
	}
	
	public void putPreference(String name, ToXML value) {
		put(name,value);
	}

	public void putPreference(String name, Object value) {
		put(name,value); 
	}
	public void putPreference(String name, int value) {
		put(name,(new Integer(value)).toString());
	}
	public void putPreference(String name, long value) {
		put(name,(new Long(value)).toString());
	}
	public void putPreference(String name, float value) {
		put(name,(new Float(value)).toString());
	}
	public void putPreference(String name, double value) {
		put(name,(new Double(value)).toString());
	}
	public void putPreference(String name, boolean value) {
		put(name,(new Boolean(value)).toString());
	}
	
	
	public StringVector getStrings(String name) {
		try {
			return (StringVector) get(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getString(String name) {
		try {
			return (String) get(name).toString();
		} catch (Exception e) {
			return null;
		}
	}

   public Object getObject(String name) {
	  try {
		 return get(name);
	  } catch (Exception e) {
		 return null;
	  }
   }
   
	public int getInt(String name) {
		try {
			return Integer.parseInt((String) get(name));
		} catch (Exception e) {
			return -1;
		}
	}
	public long getLong(String name) {
		try {
			return Long.parseLong((String) get(name));
		} catch (Exception e) {
			return -1;
		}
	}
	public float getFloat(String name) {
		try {
			return Float.parseFloat((String) get(name));
		} catch (Exception e) {
			return -1;
		}
	}
	public double getDouble(String name) {
		try {
			return Double.parseDouble((String) get(name));
		} catch (Exception e) {
			return -1;
		}
	}
	public boolean getBoolean(String name) {
		try {
			return new Boolean((String) get(name)).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	public PreferenceVector getStrings(String name, PreferenceVector dflt) {
		PreferenceVector ret;
		try {
			if ((ret = (PreferenceVector) get(name)) == null || ret.size() == 0) {
				putPreference(name,dflt);
				return dflt;
			} else return ret;
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	
	public String getString(String name, String dflt) {
		String ret;
		try {
			if ((ret = (String) get(name)) == null || ret.equals("")) {
				putPreference(name,dflt);
				return dflt;
			} else return ret;
				
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	
	public int getInt(String name, int dflt) {
		try {
			return Integer.parseInt((String) get(name));
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	public long getLong(String name, long dflt) {
		try {
			return Long.parseLong((String) get(name));
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	public float getFloat(String name, float dflt) {
		try {
			return Float.parseFloat((String) get(name));
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	public double getDouble(String name, double dflt) {
		try {
			return Double.parseDouble((String) get(name));
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	
	public boolean getBoolean(String name, boolean dflt) {
		try {
			return new Boolean((String) get(name)).booleanValue();
		} catch (Exception e) {
			putPreference(name,dflt);
			return dflt;
		}
	}
	
	public String toXML() {
		XMLBuffer sb = new XMLBuffer();
		Enumeration ev;
		sb.put("<Preferences>");
		sb.inc();
		if ((ev = keys()).hasMoreElements()) {
			for(;ev.hasMoreElements();) {
				Object ek = ev.nextElement();
				Object ob = get(ek);
				if (ob instanceof StringVector || ob instanceof PreferenceVector) {
					StringVector sv = (StringVector) ob;
					sb.put("<"+ek+">");sb.inc();
					sb.put("<Strings>");sb.inc();
					for (int i=0;i< sv.size();i++) {
						sb.append("<String>");sb.append(sv.elementAt(i).toString());
						sb.append("</String>").eol();
					}
					sb.dec().put("</Strings>");	   
					sb.dec();
					sb.put("</"+ek+">");
				} else if (ob instanceof ListVector) {
					ListVector sv = (ListVector) ob;
					sb.put("<"+ek+">");sb.inc();
					sb.put("<Strings>");sb.inc();
					for (int i=0;i< sv.size();i++) {
						sb.append("<String>");sb.append(sv.elementAt(i).toString());
						sb.append("</String>").eol();
					}
					sb.dec().put("</Strings>");	   
					sb.dec();
					sb.put("</"+ek+">");
				} else if (ob instanceof ToXML) {
					sb.put("<"+ek+">");sb.inc();
					sb.put(((ToXML)ob).toXML());
					sb.dec();
					sb.put("</"+ek+">");
				} else {
					sb.put("<"+ek+" value=\""+ob.toString()+"\"/>");
				}
			}
		}
		sb.dec();
		sb.put("</Preferences>"+XML.Eol);
		return sb.toString();
	}

	public void savePrefs(String fname) {
		File x = new File(fname);
		try {
			XMLWriter xf = new XMLWriter(new FileWriter(fname));
			xf.println("<?xml version=\"1.0\"?>"+XML.Eol);
			xf.print(toXML());
			xf.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can't open "+fname+" for writing.");
		}
	}
	
	public void savePrefsx(String fname) {
		File x = new File(fname);
		XFile xf = new XFile(x);
		if (xf.OpenPrint()) {
			xf.WriteLine("<?xml version=\"1.0\"?>"+XML.Eol);
			xf.WriteString(toXML());
			xf.Close();
		} else {
			Debug.prout(4,"Can't open "+fname+" for writing.");
		}
	}
 
	public static Preferences loadPrefs(String fname) {
		PreferencesHandler pf = new PreferencesHandler();
		try {
			FileReader xf = new FileReader(fname);
				pf.doParse(xf);
				return pf.prefs;
			} catch (Exception e) {
			System.out.println("Error in opening or parsing file "+fname+"\n"+e.toString());
			return null;
		}
	}
}
