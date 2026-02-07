//
//  XML.java
//  Kaes
//
//  Created by Michael Fischer on Sun Aug 11 2002.
//  Copyright (c) 2002 M.D.Fischer. All rights reserved.
//
import java.lang.reflect.*;

public class XML {
	public static String Eol = System.getProperty("line.separator");

	public static String getTag(Object o) {
		try {
			if (o.getClass().getMethod("getTag",null)!= null) {
				return (String) o.getClass().getMethod("getTag",null).invoke(o,null);
			} else  {
				return o.getClass().getName();
			}
		} catch (Exception e) {
			System.out.println(e.toString()); 
			return o.getClass().getName();
		}
	}

	public static String toXML(Object o) {
		String ret=null;
		try {
			if (o.getClass().getMethod("toXML",null)!= null) {
				ToXML t = (ToXML) o;
				ret = t.toXML();
			} else {
				if (o.getClass().getMethod("getTag",null)!= null) {
					String q = (String) o.getClass().getMethod("getTag",null).invoke(o,null);
					ret = "<"+q+" class=\""+o.getClass().getName()+"\">"+o.toString()+"</"+q+">";
				} else  {
					String q = o.getClass().getName();
					ret = "<"+q+">"+o.toString()+q+">";
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString()); 
			ret = "<"+o.getClass().getName()+">"+o.toString()+
				"</"+o.getClass().getName()+">";
		}
		return ret;
	}

	public static String toXML(Object o, boolean b) {
		if (!b) return toXML(o);
		
		String ret=null;
		try {
			if (o.getClass().getMethod("toXML",null)!= null) {
				ToXML t = (ToXML) o;
				ret = t.toXML();
			} else {
				if (o.getClass().getMethod("getTag",null)!= null) {
					String q = (String) o.getClass().getMethod("getTag",null).invoke(o,null);
					ret = "<"+q+" class=\""+o.getClass().getName()+"\">"+getXMLFields(o)+"</"+q+">";
				} else  {
					String q = o.getClass().getName();
					ret = "<"+q+">"+getXMLFields(o)+"</"+q+">";
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString()); 
			ret = "<"+o.getClass().getName()+">"+getXMLFields(o)+
				"</"+o.getClass().getName()+">";
		}
		return ret;
	}

	public static String getXMLFields(Object o) {
		XMLBuffer sb = new XMLBuffer();
		try {
			Field [] f = o.getClass().getFields();

			for (int i=0;i<f.length;i++) {
				String fname = f[i].getName();
				Object fo = f[i].getType();
				String ftype = fo.getClass().getName();
				
				sb.put("<"+fname+" type=\""+ftype+"\">");
				if (fo instanceof ToXML) {
					sb.inc();sb.put(((ToXML)fo).toXML());sb.dec();
				} else {
					sb.put(XML.toXML(fo,true));
				}
				sb.put("</"+fname+">"+Eol);
			}
			
		} catch (Exception e) {
			return e.toString();
		}

		return sb.toString();
	}
	
}
