	class CayleyPathInfo {
		
		CayleyPathInfo(AlgebraSymbol gen, AlgebraPath trm, AlgebraPath prod) {
			generator = gen;
			term = trm;
			product = prod;
		}
		AlgebraPath 	term;
		AlgebraSymbol 	generator;
		AlgebraPath		product;
		// AlgebraPathVector fullPaths = new AlgebraPathVector();
		ListVector sfullPaths = new ListVector();

		public void addProd(AlgebraSymbol gen, CayleyPathInfo trm) {
		  ListVector aFullPaths = (ListVector) trm.getFullPaths();
		   int len=aFullPaths.size();
		   for(int i=0; i < len;i++) {
			  AlgebraSymbolVector ap = (AlgebraSymbolVector) ((AlgebraSymbolVector) aFullPaths.elementAt(i)).copy();
		//	 ap.path.addToEnd(gen);
			//  ap.reducedPath = (AlgebraSymbolVector) ap.reducedPath.clone();
			//  ap.path = (AlgebraSymbolVector) ap.path.clone();
			 ap.addToEnd(gen);
			 // Bit of kludge to search for unreduced path in vector
			 // probably need to do more explicitly
		//	 AlgebraSymbolVector aq = ap.reducedPath;
		//	 ap.reducedPath = (AlgebraSymbolVector) ap.path.clone();
			// fullPaths.addUnique(ap);
			 sfullPaths.addUnique(ap);
		//	 ap.reducedPath = aq;
		   }
		}

		public ListVector getFullPaths() {
		   return sfullPaths;
		}

		public boolean isProd(AlgebraPath prod) {
		   return prod.equals(product);
		}
		
		public boolean equals(AlgebraPath prod) {
		   return product.toString().equals(prod.toString());
		}

		public boolean equals(String prod) {
		   return product.toString().equals(prod.toString());
		}

		public String toString() {
			return "Term="+term+" generator="+generator+" product="+product+"\n-------------\n"+sfullPaths;
		}
	}
