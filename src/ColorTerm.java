//
//  ColorTerm.java
//  Kaes
//
//  Created by Dwight Read on Sat Jul 31 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.util.*;
import java.awt.Color;

public class ColorTerm {
	
	Object theTerm = null;
	Color theColor = Color.black;
	
	public ColorTerm(){
	}
	
	public ColorTerm(Color c){
		theColor = c;
	}
	
	public ColorTerm(Object term){
		theTerm = term;
	}
	
	public ColorTerm(Color c, Object term){
		theTerm = term;
		theColor = c;
	}
	
	public void setTheTerm(Object term){
		theTerm = term;
	}
	
	public Object getTheTerm(){
		return theTerm;
	}
	
	public String toString(){
		return theTerm.toString();
	}
	
	public void setTheColor(Color c){
		theColor = c;
	}
	
	public Color getTheColor(){
		return theColor;
	}
}
