//
//  PreferenceVector.java
//  Kaes
//
//  Created by Michael Fischer on Mon Aug 02 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
	import java.util.*;
	import java.io.*;

public class PreferenceVector extends StringVector {
	//
	//  Preferences.java
	//  Kaes
	//
	//  Created by Michael Fischer on Sun Aug 11 2002.
	//  Copyright (c) 2002  Michael Fischer . All rights reserved.
	//
	// import org.csac.xml.*;
		
	public void addVal(Object o) {
		addElement(o.toString());
	}
	
	public void addVal(int value) {
		addElement(value + "");
	}
		
	public void addVal(long value) {
		addElement(value + "");
	}

	public void addVal(float value) {
		addElement(value + "");
	}
	
	public void addVal(double value) {
		addElement(value + "");
	}
	
	public void addVal(boolean value) {
		addElement(value + "");
	}
		
		
		public String getString(int index) {
			try {
				return (String) elementAt(index);
			} catch (Exception e) {
				return null;
			}
		}
		
		public Object getObject(int index) {
			try {
				return elementAt(index);
			} catch (Exception e) {
				return null;
			}
		}
		
		public int getInt(int index) {
			try {
				return Integer.parseInt((String) elementAt(index));
			} catch (Exception e) {
				throw new java.lang.NumberFormatException("Problem parsing " +  elementAt(index) + " as integer");
			}
		}
	
		public long getLong(int index) {
			try {
				return Long.parseLong((String) elementAt(index));
			} catch (Exception e) {
				throw new java.lang.NumberFormatException("Problem parsing " +  elementAt(index) + " as long");
			}
		}
		public float getFloat(int index) {
			try {
				return Float.parseFloat((String) elementAt(index));
			} catch (Exception e) {
				throw new java.lang.NumberFormatException("Problem parsing " +  elementAt(index) + " as float");
			}
		}
		public double getDouble(int index) {
			try {
				return Double.parseDouble((String) elementAt(index));
			} catch (Exception e) {
				throw new java.lang.NumberFormatException("Problem parsing " +  elementAt(index) + " as double");
			}
		}
		public boolean getBoolean(int index) {
			try {
				return new Boolean((String) elementAt(index)).booleanValue();
			} catch (Exception e) {
				throw new java.lang.NumberFormatException("Problem parsing " +  elementAt(index) + " as boolean");
			}
		}
		
}
