	class CayleyInfo {
		
		CayleyInfo(AlgebraSymbol gen, AlgebraPath trm, AlgebraPath prod) {
			generator = gen;
			term = trm;
			product = prod;
		}
		AlgebraPath 	term;
		AlgebraSymbol 	generator;
		AlgebraPath		product;
		public String toString1() {
			return "Term="+term+" generator="+generator+" product="+product;
		}
		public String toString() {
			return "Term="+term+" generator="+generator+" product="+product+"\n";
		}
	}
