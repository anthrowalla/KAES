//
//  Undo.java
//  Kaes
//
//  Created by Michael Fischer on Tue Oct 19 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.util.Vector;

public class Undo {
	public static Queue undoQueue = new Queue(10);
	public static Vector undoList = new Vector();
	
	public static void addItem(Object t, Object v, String comment) {
		undoList.addElement(new SaveState(t,v,comment));
	}
	
	public static void  pushUndo() {
		if (undoList.isEmpty()) return;
		undoQueue.push(undoList);
		undoList = new Vector();
	}
	
	public static void doUndo() {
		if (undoQueue.top() == null) return;
		
		undoList = (Vector) undoQueue.pop();
		if (undoList.isEmpty()) return;
		
		for(int i=0;i<undoList.size();i++) {
			SaveState s = (SaveState) undoList.elementAt(i);
			
			Object k = s.getTarget();
			
			if (k instanceof KintermFrame) {
				KintermFrame kf = (KintermFrame) k;
				Object v = s.getValue();
				if (v instanceof TransferKinInfo) {
					
				}
			}
		}
	}
}
