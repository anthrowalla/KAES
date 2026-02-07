//
//  SaveState.java
//  Kaes
//
//  Created by Michael Fischer on Mon Mar 24 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

public class SaveState {
	Object target, value;
	String comment="";
	public SaveState(Object target, Object value, String comment) {
		this.target = target;
		this.value = value;
		this.comment = comment;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Object getComment() {
		return comment;
	}
	
}
