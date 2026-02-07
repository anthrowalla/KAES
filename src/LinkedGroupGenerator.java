public class LinkedGroupGenerator extends GroupGenerator {

		public final static boolean SAME=true;
		public final static boolean OPPOSITE=false;
		public final static boolean TIGHT=true;
		public final static boolean LOOSE=false;
		public final static boolean N_OPPOSITE=true;
		public final static boolean N_REGULAR=false;


		public LinkedGroupGenerator() {

		}

		public LinkedGroupGenerator(AlgebraSymbolVector a,GroupGenerator g) {
			setTheGroup(a);
			setLinkedGroup(g);
		}

		public LinkedGroupGenerator(AlgebraSymbolVector a,GroupGenerator g, boolean s) {
			setTheGroup(a);
			setLinkedGroup(g);
			setSame(s);
		}


		public LinkedGroupGenerator(AlgebraSymbolVector a,GroupGenerator g, boolean same, boolean tight, boolean noppose) {
			setTheGroup(a);
			setLinkedGroup(g);
			setSame(same);
			setTight(tight);
			setNOppositeOfN(noppose);
		}

		public LinkedGroupGenerator(AlgebraSymbol a, GroupGenerator g, boolean s) {
			setTheGroup(new AlgebraSymbolVector(a));
			setLinkedGroup(g);
			setSame(s);
		}


		public AlgebraSymbol generate() {
			if (isBound()) return getCurrentValue();

			AlgebraSymbol other = getLinkedGroup().generate();
			while (isNext()) {
				AlgebraSymbol us = getNext();
				String ourSex = us.getSex();
				String otherSex = other.getSex();
//System.out.println("us "+us+" ourSex "+ourSex+"other "+other+" othersex "+otherSex+" same "+same);
				if (same) {
					if (ourSex.equals(otherSex)) {
						bind();
						return us;
					}
					// possibility of considering which is "N" for linked cases
					if (ourSex.equals("N") || otherSex.equals("N")) {
						if (isNOppositeOfN()) { //
							continue;
						}
						if (!tight) {
							bind();
							return us;
						}
					}
				} else { // not same
					if (!ourSex.equals(otherSex)) {
						// possibility of considering which is "N" for linked cases
						if (ourSex.equals("N") || otherSex.equals("N")) {
							if (!tight || isNOppositeOfN()) continue;
						}
						bind();
						return us;
					} else if (isNOppositeOfN() && otherSex.equals("N") ) { // equal
						bind();
						return us;
					}
				}
			}
			return null;
		}

	public LinkedGroupGenerator setSame(boolean same) {
		this.same = same;
		return this;
	}

	public boolean isSame() {
		return same;
	}
	protected boolean same=true;

	public void setLinkedGroup(GroupGenerator linkedGroup) {
		this.linkedGroup = linkedGroup;
	}

	public GroupGenerator getLinkedGroup() {
		return linkedGroup;
	}
	protected GroupGenerator linkedGroup=null;

	public LinkedGroupGenerator setTight(boolean tight) {
		this.tight = tight;
		return this;
	}

	public boolean isTight() {
		return tight;
	}
	protected boolean tight=false;

	public LinkedGroupGenerator setNOppositeOfN(boolean nIsOppositeOfN) {
		this.NOppositeOfN = nIsOppositeOfN;
		return this;
	}

	public boolean isNOppositeOfN() {
		return NOppositeOfN;
	}
	protected boolean NOppositeOfN=false;

}