
//
//  XMLWriter.java
//  Coalate
//
//  Created by Michael Fischer on Wed Apr 23 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//
import java.io.*;

public class XMLWriter extends PrintWriter
{
	  /**
	  * Creates a new XMLWriter
	  */
	  public XMLWriter( OutputStream out )
   {
		 super( out );
   }
   
   /*
   * Creates a new HTMLWriter
   */
   
   /**          */
   public XMLWriter( Writer out)
   {
	  super( out );
   }
   
   /**
   * Prints a character and converts it, if necessary, to
   * a Unicode XML sequence.
   */
   public void hprint( char c )
   {
	  if (    ( ( (int)c  > 31 ) && ( (int)c < 127 ) )
			|| (   (int)c ==  9 )
			|| (   (int)c == 10 )
			|| (   (int)c == 13 )
			)
		 print( c );
	  else
	  {
		 print( "&#" );
		 print( (long)c );
		 print( ";" );
	  }
   }
   
   /**
   * Prints a string and converts characters, if necessary, to
   * a Unicode XML sequence.
   */
   public void hprint( String s )
   {
	  if ( s != null )
		 for ( int i = 0; i < s.length(); i++ )
			hprint( s.charAt(i) );
   }
   
   /**
   * Prints a string, converts characters, if necessary, to
   * a Unicode XML sequence and adds a line feed.
   */
   public void hprintln( String s )
   {
	  hprint( s );
	  println();
   }
   
   /**
   * Prints a character, converts it, if necessary, to
   * a Unicode XML sequence and adds a line feed.
   */
   public void hprintln( char x )
   {
	  hprint( x );
	  println();
   }
   
   /**
   * Prints an array of characters and converts characters, if necessary, to
   * a Unicode XML sequence.
   */
   public void hprint( char[] x )
   {
	  if ( x != null )
		 for ( int i = 0; i < x.length; i++ )
			hprint( x[ i ] );
   }
   
   /**
   * Prints an array of characters, converts characters, if necessary, to
   * a Unicode XML sequence and adds a line feed.
   */
   public void hprintln( char[] x )
   {
	  hprint( x );
	  println();
   }
   
   /**
   * Prints a string representation of an object and converts characters,
   * if necessary, to a Unicode XML sequence.
   */
   public void hprint( Object x )
   {
	  hprint( x.toString() );
   }
   
   /**
   * Prints a string representation of an object, converts characters,
   * if necessary, to a Unicode XML sequence and adds a line feed.
   */
   public void hprintln( Object x )
   {
	  hprintln( x.toString() );
   }
}
