public class GroupGeneratorVector extends ListVector implements GroupProcessor {

	public GroupGenerator backup() {
		if (index == 0) return null;
		index--;
		return (GroupGenerator) elementAt(index);
	}

	public GroupGenerator getNextSymbol() {
		return (GroupGenerator) getNext();
	}

	public GroupGenerator getFirst() {
		return (GroupGenerator) elementAt(0);
	}

	public GroupGenerator getLast() {
		return (GroupGenerator) elementAt(size()-1);
	}

	public GroupGenerator getSymbol() {
		return (GroupGenerator) elementAt(index);
	}

	public GroupGenerator getSymbol(int i) {
		return (GroupGenerator) elementAt(i);
	}


	public void interpret(GroupProcessor p) {
		AlgebraSymbolVector result = new AlgebraSymbolVector();
		getFirst().reset();
		getFirst().interpret(this,p,result);
	}

	public void interpret() {
			AlgebraSymbolVector result = new AlgebraSymbolVector();
			reset();
			getFirst().reset(); // must be reset
			((GroupGenerator)getNext()).interpret(this,this,result);
			//System.out.println("Done interpret in vector i"+i);
	}
	public void interpretModified() {
		getFirst().reset();//NEW
		for (int i=0;i<getFirst().getTheGroup().size();i++){//NEW
			AlgebraSymbolVector result = new AlgebraSymbolVector();
			reset();
			//getFirst().reset(); // must be reset//NEW
			for (int j=0;j<this.size();j++){//NEW
				((GroupGenerator)this.elementAt(j)).unbind();//NEW
			}
			((GroupGenerator)getNext()).interpret(this,this,result);
			//System.out.println("Done interpret in vector i"+i);
		}
	}
	public void process(AlgebraSymbolVector a) {
		boolean divide = false;
		AlgebraSymbolVector lhs = new AlgebraSymbolVector();
		AlgebraSymbolVector rhs = new AlgebraSymbolVector();
		for(a.reset();a.isNext();) {
			AlgebraSymbol q = a.getNext();
			if (q.equals(GroupGenerator.divider)) {
				divide = true;
				continue;
			}
			if (divide) rhs.addElement(q); // e.g. right to left
			else lhs.addElement(q);
		}
		Debug.prout(0, "ggv: "+lhs+"="+rhs);
		Algebra.getCurrent().addEquation(lhs,rhs);
	}


}
