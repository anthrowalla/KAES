// HandlerBase.java: Simple base class for AElfred processors.
// NO WARRANTY! See README, and copyright below.
// $Id: HandlerBase.java,v 2.2 1998/05/02 17:17:10 david Exp $

// package com.microstar.xml;

import com.microstar.xml.XmlHandler;
import com.microstar.xml.XmlParser;
import com.microstar.xml.XmlException;
import java.io.Reader;

import java.util.*; // Stack, Vector


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
  
public class KintermMapHandler implements XmlHandler {

	//Stack termListStack = new Stack();
	//Stack termStack = new Stack();
	TransferKinInfoVector kinTermVector=null;
	
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
  public void endDocument ()
    throws java.lang.Exception
  {
		kinTermVector.buildTable();
		kinTermVector.buildInverseTable();
		for(kinTermVector.reset();kinTermVector.isNext();) {
		  	TransferKinInfo k = kinTermVector.getNext();
		  	if (k.coveredTerms != null) {
		  		StringVector cvr = k.coveredTerms;
		  		for(cvr.reset();cvr.isNext();) {
		  			kinTermVector.lookupTerm(cvr.getNext()).isCovered = k.isCovered;
		  		}
		  	}
		}
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

  TransferKinInfo currentEntry=null;
  Preferences docPrefs = null;
  boolean inPrefs = false;
  /**
    * Handle the start of an element.
    * <p>The default implementation does nothing.
    * @see com.microstar.xml.XmlHandler#startElement
    * @exception java.lang.Exception Derived methods may throw exceptions.
    */
  public void startElement (String elname)
    throws java.lang.Exception
	{
		if (inPrefs) {
			String val;
			if (attributes) {
				int index = attributeNames.indexOf("value");
				if (index != -1) {
					val = (String) attributeValues.elementAt(index);
				} else val = "";
			} else val = "";
			docPrefs.put(elname,val);
		} else 
		if (elname.equals("Preferences")) {
			inPrefs = true;
			docPrefs = new Preferences();
		} else if (elname.equals("KintermMap")) {
  			kinTermVector = new TransferKinInfoVector();
  		} else if (elname.equals("Name")) {
  			
  		} else if (elname.equals("Kinterm")) {
  			currentEntry = new TransferKinInfo();
  			kinTermVector.addElement(currentEntry);
   		} else if (elname.equals("Term")) {
			
 		} else if (elname.equals("Sex")) {
  			
 		 } else if (elname.equals("CoveredTerms")) {
  			currentEntry.coveredTerms = new StringVector();
 		} else if (elname.equals("IsGen")) {
 		} else if (elname.equals("Orientation")) {
  		} else if (elname.equals("Origin")) {
 		} else if (elname.equals("x")) {
  		} else if (elname.equals("y")) {
  		} else if (elname.equals("Products")) {
  			currentProducts = currentEntry.products = new TransferProductsVector();
   		} else if (elname.equals("Generator")) {
   			currentProducts.addElement((currentProduct = new TransferProduct()));
  	 		int index = attributeNames.indexOf("Name");
  	 		if (index != -1) {
  	 			currentProduct.addElement(attributeValues.elementAt(index));
  	 		} else {
  	 			currentProduct.addElement("foo");
  	 		}
  		} else if (elname.equals("Product")) {
  			
  		} else if (elname.equals("VariablesFactory")) {
  			theFactory = new VariablesFactory();
  		} else if (elname.equals("Variables")) {
  			theVariables = new Variables();
  		} else if (elname.equals("Variable")) {
 			int index = attributeNames.indexOf("Name");
	  	 	if (index != -1) {
	  	 		theVariable = new Variable((String) attributeValues.elementAt(index));
	  	 	} else {
	  	 		theVariable= new Variable("UnnamedVariable");
	  	 	}
	  	 	
	  	 }   else if (elname.equals("VariableValueProtos")) {
 			if (theVariable.values == null) theVariable.values = new ListVector();  	 	
	  	 } else if (elname.equals("VariableValueProto")) {
 			int index = attributeNames.indexOf("Value");
	  	 	if (index != -1) {
				theVariable.values.addElement(attributeValues.elementAt(index));
	  	 	}
	  	 } else if (elname.equals("VariableValue")) {
 			int index = attributeNames.indexOf("Value");
	  	 	if (index != -1) {
	  	 		if (!attributeValues.elementAt(index).equals("*"))
					theVariable.addElement(attributeValues.elementAt(index));
	  	 	}
	  	 }

  		attributeNames = new Vector(1);
  		attributeValues = new Vector(1);
  		attributes = false;
	}
	
	Vector currentProducts = null;
	Vector currentProduct = null;
	VariablesFactory theFactory = null;
	Variables theVariables = null;
	Variable theVariable = null;
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
		  inPrefs = false;
		  kinTermVector.kinPreferences = docPrefs;
	  } else if (elname.equals("KintermMap")) {
  		} else if (elname.equals("Name")) {
  		} else if (elname.equals("Kinterm")) {
  			currentEntry.setTheVariables(theVariables);
  			//System.out.println("Parse theVariables="+theVariables);
   		} else if (elname.equals("Term")) {
			currentEntry.term = contentData;
    	} else if (elname.equals("CoveringTerm")) {
			if (!contentData.equals("null")) currentEntry.coveringTerm = contentData;
		} else if (elname.equals("Sex")) {
  			currentEntry.sex = contentData;
 		} else if (elname.equals("Etc")) {
  			currentEntry.setEtc(new Boolean(contentData).booleanValue());
 		} else if (elname.equals("IsGen")) {
 			currentEntry.setGenerator(new Boolean(contentData).booleanValue());
 		} else if (elname.equals("Covered")) {
 			currentEntry.isCovered = new BooleanSwitch(new Boolean(contentData).booleanValue());
		} else if (elname.equals("DropMerge")) { // 13-1-2003 mf
		  // currentEntry.setDropMerge(new Boolean(contentData).booleanValue());
		} else if (elname.equals("Orientation")) {
 			currentEntry.orientation = Integer.parseInt(contentData.trim());
  		} else if (elname.equals("Origin")) {
 		} else if (elname.equals("x")) {
 			currentEntry.origin.x = Integer.parseInt(contentData.trim());
  		} else if (elname.equals("y")) {
  			currentEntry.origin.y = Integer.parseInt(contentData.trim());
  		} else if (elname.equals("Products")) {
   		} else if (elname.equals("Generator")) {
  		} else if (elname.equals("Product")) {
  			currentProduct.addElement(contentData);
  		} else if (elname.equals("CoveredTerm")) {
  			currentEntry.coveredTerms.addElement(contentData);
  		} else if (elname.equals("CoveredTerms")) {
  			if (currentEntry.coveredTerms.size() == 0) currentEntry.coveredTerms = null;
  			else if (currentEntry.coveredTerms.getSymbol(0).equals("null")) currentEntry.coveredTerms = null;
  		}  else if (elname.equals("VariablesFactory")) {
  			kinTermVector.setTheMaster(theFactory);
  			for (theVariables.reset();theVariables.isNext();) {
  				Variable v = (Variable) theVariables.getNext();
  				theFactory.varnames.addElement(v.name);
 				theFactory.variablePrototypes.addElement(v);
  			}
  		}  else if (elname.equals("Variables")) {
  			theVariables.masterRecord=theFactory;
  		} else if (elname.equals("Variable")) {
  			theVariables.addElement(theVariable);
  			if (theVariable.values == null) {
  				int ndx;
  				if ((ndx = theFactory.varnames.indexOf(theVariable.name)) != -1) {
  					theVariable.values = ((Variable) theFactory.variablePrototypes.elementAt(ndx)).values;
  				}
  			}
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
  	//Term t = new Term("text",1);
  	//t.setarg(0,new Term(,0));
  	//((TermList) termListStack.peek()).append(t);
//  	System.out.println(t.toSString());
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
  void doParse (XFile xr)
    throws java.lang.Exception
  {
    //String docURL = makeAbsoluteURL(url);

				// create the parser
	  inPrefs = false;
    XmlParser parser = new XmlParser();
    parser.setHandler(this);
    parser.parse(null, null, xr.diStream);
  }

}
