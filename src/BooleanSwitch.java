import java.util.Hashtable;

public class BooleanSwitch implements Cloneable {
	Boolean value = new Boolean(false);
   static Hashtable replaceCache = new Hashtable(1024);
	
	public BooleanSwitch(boolean t) {
		value = new Boolean(t);
	}

	public BooleanSwitch(Boolean t) {
		value = t;
	}

	public Object clone() {
	   return new BooleanSwitch(isTrue());
	}

	public BooleanSwitch replace() {
	   Object t;
	   if ((t = replaceCache.get(this)) == null) {
		  replaceCache.put(this, (t = new BooleanSwitch(isTrue())));
	   }
	   return (BooleanSwitch) t;
	}
	
	public boolean isTrue() {
		return value.booleanValue();
	}

	public boolean isFalse() {
		return !value.booleanValue();
	}

	public boolean setValue(boolean t) {
		value = new Boolean(t);
		return t;
	}
	public boolean setTrue() {
		value = new Boolean(true);
		return true;
	}
	
	public boolean setFalse() {
		value = new Boolean(false);
		return false;
	}
	
	public Boolean getValue() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
}