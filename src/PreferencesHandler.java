// package org.csac.util;

// HandlerBase.java: Simple base class for AElfred processors.
// NO WARRANTY! See README, and copyright below.
// $Id: HandlerBase.java,v 2.2 1998/05/02 17:17:10 david Exp $

// package com.microstar.xml;

import com.microstar.xml.XmlHandler;
import com.microstar.xml.XmlParser;
import com.microstar.xml.XmlException;

import java.io.Reader;

import java.util.*; // Stack, Vector
//import org.csac.xml.*;

/**
  * Convenience base class for AElfred handlers.
  * <p>This base class implements the XmlHandler interface with
  * (mostly empty) default handlers.  You are not required to use this,
  * but if you need to handle only a few events, you might find
  * it convenient to extend this class rather than implementing
  * the entire interface.  This example overrides only the
  * <code>charData</code> method, using the defaults for the others:
  * <pre>
  * import com.microstar.xml.HandlerBase;
  *
  * public class MyHandler extends HandlerBase {
  *   public void charData (char ch[], int start, int length)
  *   {
  *     System.out.println("Data: " + new String (ch, start, length));
  *   }
  * }
  * </pre>
  * <p>This class is optional, but if you use it, you must also
  * include the <code>XmlException</code> class.
  * <p>Do not extend this if you are using SAX; extend
  * <code>org.xml.sax.HandlerBase</code> instead.
  * @author Copyright (c) 1998 by Microstar Software Ltd.
  * @author written by David Megginson &lt;dmeggins@microstar.com&gt;
  * @version 1.1
  * @see XmlHandler
  * @see XmlException
  * @see org.xml.sax.HandlerBase
  */
  
public class PreferencesHandler implements XmlHandler {

	//Stack termListStack = new Stack();
	//Stack termStack = new Stack();
	//TransferKinInfoVector kinTermVector=null;
	public Preferences prefs=null;
	
	boolean attributes=false;
	//Term rootTerm = null;
  /**
    * Handle the start of the document.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startDocument
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startDocument () 
    throws java.lang.Exception
  {
  }

  /**
    * Handle the end of the document.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endDocument
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endDocument () throws java.lang.Exception {
  }

  /**
    * Resolve an external entity.
    * <p>The default implementation simply returns the supplied
    * system identifier.
    * @see com.microstar.xml.XmlHandler#resolveEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public Object resolveEntity (String publicId, String systemId) 
    throws java.lang.Exception
  {
    return null;
  }


  /**
    * Handle the start of an external entity.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startExternalEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startExternalEntity (String systemId)
    throws java.lang.Exception
  {
  }

  /**
    * Handle the end of an external entity.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endExternalEntity
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endExternalEntity (String systemId)
    throws java.lang.Exception
  {
  }

  /**
    * Handle a document type declaration.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#doctypeDecl
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void doctypeDecl (String name, String publicId, String systemId)
    throws java.lang.Exception
  {
  }

  /**
    * Handle an attribute assignment.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#attribute
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
    
  private static String attributeLabel = "attributes";
  Vector attributeNames = new Vector(1);
  Vector attributeValues = new Vector(1);
  
  public void attribute (String aname, String value, boolean isSpecified)
    throws java.lang.Exception
  {
  		attributeNames.addElement(aname);
  		attributeValues.addElement(value);
  		attributes = true;
  }
 
 	//TransferKinInfo currentEntry=null;
  String currKey=null;
  StringVector sv = null;
  boolean inStringVector = false;
  /**
    * Handle the start of an element.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startElement
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startElement (String elname)
    throws java.lang.Exception
	{
  		if (elname.equals("Preferences")) {
  			prefs = new Preferences();
		} else if (elname.equals("Strings")) {
		   sv = new PreferenceVector();
		} else if (elname.equals("String")) {
		   
		} else {
			String val;
			if (attributes) {
				int index = attributeNames.indexOf("value");
				if (index != -1) {
					val = (String) attributeValues.elementAt(index);
				} else val = "";
				prefs.put(elname,val);
				currKey = null;
				sv=null;
			} else {
			   currKey = elname;
			}
	  	 }

  		attributeNames = new Vector(1);
  		attributeValues = new Vector(1);
  		attributes = false;
	}
	
  /**
    * Handle the end of an element.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#endElement
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void endElement (String elname)
    throws java.lang.Exception
  {
	  contentData = contentData.trim();
	  if (elname.equals("Preferences")) {
	  } else if (elname.equals("Strings")) {
		 prefs.put(currKey,sv);
		 currKey = null;
		 sv = null;
	  } else if (elname.equals("String")) {
		 sv.addElement(contentData);
	  }
	  contentData = "";
  } 


	String contentData = "";
  /**
    * Handle character data.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#charData
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void charData (char ch[], int start, int length)
    throws java.lang.Exception
  {
  		String tdata = new String(ch,start,length);
  		contentData += tdata;
  }

  /**
    * Handle ignorable whitespace.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#ignorableWhitespace
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws java.lang.Exception
  {
  }

  /**
    * Handle a processing instruction.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#processingInstruction
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void processingInstruction (String target, String data)
    throws java.lang.Exception
  {
  }

  /**
    * Throw an exception for a fatal error.
    * <p>The default implementation throws <code>XmlException</code>.
    * @see com.microstar.xml.XmlHandler#error
    * @exception com.microstar.xml.XmlException A specific parsing error.
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void error (String message, String systemId, int line, int column)
    throws XmlException, java.lang.Exception
  {
    throw new XmlException(message, systemId, line, column);
  }
	
  public void getRootTerm() {
  	//return rootTerm;
  }
  
    /**
    * Start a parse in application mode.
    * <p>Output will go to STDOUT.
    * @see #displayText
    * @see com.microstar.xml.XmlParser#run
    */
  void doParse (Reader xr)
    throws java.lang.Exception
  {
    //String docURL = makeAbsoluteURL(url);

				// create the parser
    XmlParser parser = new XmlParser();
    parser.setHandler(this);
    parser.parse(null, null, xr);
  }

}
