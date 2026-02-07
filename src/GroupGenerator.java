public class GroupGenerator  {

	static AlgebraSymbol divider = new AlgebraSymbol("=");

	public GroupGenerator() {

	}

	public GroupGenerator(AlgebraSymbolVector g) {
		setTheGroup(g);
	}

	public GroupGenerator(AlgebraSymbol g) {
		setTheGroup(new AlgebraSymbolVector(g));
	}


	public void reset() {
		index = -1;
		setCurrentValue(null);
	}

	int index=-1;

	public boolean isNext() {
		if ((index+1) < getTheGroup().size()) return true;
		else return false;
	}

	public AlgebraSymbol getNext() {
		if (++index < getTheGroup().size())
			return (AlgebraSymbol) getTheGroup().elementAt(index);
		else return null;
	}

	public AlgebraSymbol generate() {
		if (getTheGroup().size() == 0) {
			throw new AlgebraException("in GroupGenerator.generate: product set empty!");
		}
		if (isBound()) return getCurrentValue();
		else {
			if (isNext()) setCurrentValue(getNext());
			else setCurrentValue(null);
			setBound(true);
		}
		return getCurrentValue();
	}

	public void setTheGroup(AlgebraSymbolVector theGroup) {
		this.theGroup = theGroup;
	}

	public AlgebraSymbolVector getTheGroup() {
		return theGroup;
	}
	protected AlgebraSymbolVector theGroup=null;

	public void setCurrentValue(AlgebraSymbol currentValue) {
		this.currentValue = currentValue;
	}

	public AlgebraSymbol getCurrentValue() {
		return currentValue;
	}
	protected AlgebraSymbol currentValue;

	public void setBound(boolean bound) {
		this.bound = bound;
	}

	public void unbind() {
		setBound(false);
	}

	public void bind() {
		setBound(true);
	}

	public boolean isBound() {
		return bound;
	}
	protected boolean bound;

	public void interpret(GroupGeneratorVector g, GroupProcessor p, AlgebraSymbolVector result) {
		AlgebraSymbol x = generate();
		//System.out.println(" Generator="+(x)+" result="+result);
		if (x == null) {
			unbind();
			GroupGenerator gx = g.backup();
			if (gx != null) {
				gx.unbind();
				//if (result.size() > 0) 	result.removeElementAt(result.size()-1);
				if (result.size() > 0) 	result.removeElementAt(0);
				gx.interpret(g,p,result);
			}
			//System.out.println(getTheGroup()+" At end of iteration result="+result);
		} else {
			result.addToEnd(x);
			if (g.isNext()) {
				GroupGenerator gx = g.getNextSymbol();
				//System.out.println("Going down to gx= GroupVector="+g+" result="+result);
				//System.out.println(" index "+g.index);
				//if (g.index != 1)//NEW
				    gx.reset();
				gx.interpret(g,p,result);
			} else {
				//System.out.println("Processing result="+result);
				p.process(result);
				//result.removeElementAt(result.size()-1);
				result.removeElementAt(0);
				unbind();
				interpret(g,p,result);
			}
		}
	}

	public void interpretModified(GroupGeneratorVector g, GroupProcessor p, AlgebraSymbolVector result) {
		AlgebraSymbol x = generate();
		//System.out.println(" Generator="+(x)+" result="+result);
		if (x == null) {
			GroupGenerator gx = g.backup();
			if (gx != null) {
				gx.unbind();
				//if (result.size() > 0) result.removeElementAt(result.size()-1);
			}
			//System.out.println(getTheGroup()+" At end of iteration result="+result);
		} else {
			result.addToEnd(x);
			if (g.isNext()) {
				GroupGenerator gx = g.getNextSymbol();
				//System.out.println("Going down to gx="+gx+" GroupVector="+g+" result="+result);
				//System.out.println(" index "+g.index);
				if (g.index != 1)//NEW
				    gx.reset();
				gx.interpret(g,p,result);
			} else {
				//System.out.println("Processing result="+result);
				p.process(result);
				result.removeElementAt(result.size()-1);
				unbind();
				//interpret(g,p,result);//NEW
			}
		}
	}


	public String toString() {
		return theGroup.toString();
	}
}
