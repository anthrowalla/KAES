//
//  Queue.java
//  Kaes
//
//  Created by Michael Fischer on Mon Mar 24 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

import java.util.Vector;

public class Queue extends Vector {

   int hptr=-1;
   int len=0;
   
   public Queue(int n) {
	  super(n);
	  len = n;
	  setSize(n);
	  for(int i=0;i<n;i++) setElementAt(null,i);
   }

   public void push(Object j) {
	  hptr = hptr + 1 % len;
	  setElementAt(j,hptr);
   }

   public Object pop() {
	  if (hptr == -1) return null;
	  else {
		 Object k = elementAt(hptr);
		  setElementAt(null,hptr);
		  hptr = hptr-1;
		  if (hptr == -1) hptr = len-1;		 
		 return k;
	  }
   }

   public Object top() {
	  if (hptr == -1) return null;
	  else  return elementAt(hptr);
   }
}
