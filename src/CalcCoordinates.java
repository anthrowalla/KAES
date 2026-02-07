//
//  CalcCoordinates.java
//  Kaes
//
//  Created by Michael Fischer on Sat Apr 12 2003.
//  Copyright (c) 2003 Michael D. Fischer. All rights reserved.
//

public class CalcCoordinates {
   CayleyTable cayley;
   float [][] coordinates = null;
   AlgebraPathVector theProducts;
   AlgebraSymbolVector generators = Algebra.getCurrent().getGenerators();

   public CalcCoordinates(CayleyTable c) {
	  cayley = c;
   }

	public void calcCoordinates() {
		int i,j,dim;
		theProducts = cayley.getProducts();
		coordinates = new float[theProducts.size()][3];//generators.size()
		cayley.setCoordinates(coordinates);
      int iEnd = numberOfGenerators();
		boolean reverseFlag = reverseCoords();//reverse dim1 and dim2
		for(i=0;i<theProducts.size();i++) {
			AlgebraPath thePath = theProducts.getSymbol(i);
			//thePath = thePath.getEquivalentPathLeft();//dwr 8/5
			thePath = thePath.getReducedEquivalentPathLeft();//dwr 8/5
			generators.reset();
			for(j=0;j<iEnd;) {
				if (!generators.isNext()){
					j++;
				} else {
					AlgebraSymbol gg = generators.getNext();
					if (gg.isIdentityElement()) continue;
					if (gg.isSexGenerator()) continue;
					String gen = gg.getValue();
					if (reverseFlag){
						if (j==0) dim = 1;
						else if (j==1) dim = 0;//use this for stripped down map need to set up conditions when to use
						else dim = j;
					} else //if (true) {}
					dim = j;
					coordinates[i][dim]= useCalcMethod1(thePath,gen);
					j++;
				}
			}
		}
	}


   int numberOfGenerators(){
	  int i = 0;
	  for (generators.reset();generators.isNext();){
	        AlgebraSymbol gen = generators.getNext();
	        if (!(gen.isIdentityElement())&& gen.getArrowType()!=Bops.SEXGEN){//!Algebra.getCurrent().isArrow(gen,Bops.MALEFEMALE)){
			   i++;
			}
	    }
	  return i;
	}

   int generatorNumber(AlgebraSymbol g){
	  int j = 0;
	  for(int i=0;i<generators.size();i++) {
	        AlgebraSymbol gen = (AlgebraSymbol) generators.elementAt(i);
	        if (!gen.isIdentityElement() &&  gen.getArrowType()!=Bops.SEXGEN){
			   j++;
			   if (gen.equals(g)) break;
			}
	  }
	  return j;
   }

   int resetDimension(AlgebraSymbol g){
	  int i = generatorNumber(g);
	  int n = numberOfGenerators();
	  if (n < 5) {
		 if (i > 2){
			if (!generators.consistentSex())
			   return generatorNumber(g.getOppositeSexGenerator())-1 ;
			else
			   return generatorNumber(g.getOppositeArrowSameSexGenerator())-1 ;
		 }
		 return i-1;
	  }
	  else {
		if (g.getArrowType() == Bops.UP || g.getArrowType() == Bops.DOWN) return 1;
		else if (g.getArrowType() == Bops.LEFT || g.getArrowType() == Bops.RIGHT) return 0;
		else return 2;
		/* if (i < 3)
			return i-1;
		 else if (i < 5)
			return generatorNumber(g.getOppositeArrowSameSexGenerator())-1 ;
		 else if (i < 7)
			return i - 5;
		 else if (i < 9)
			return generatorNumber(g.getOppositeArrowSameSexGenerator())-5 ;
		else
			return 2;//all else fails...*/

	  }
   }

   int resetSibDimension(AlgebraSymbol g){
		int aType = g.getArrowType();
		switch (aType){
		    case Bops.UP:
			    return 1;
			case Bops.DOWN:
			   return 1;// return 2;//use 2 to make down terms at right angle to up terms
		    case Bops.LEFT:
			    return 0;
			case Bops.RIGHT:
			    return 0;
			default:
				return 0;
		}
   }

   float useCalcMethod1(AlgebraPath thePath, String gen){
	  return thePath.countSymbol(gen);
   }

   float useCalcMethod2(AlgebraPath thePath, int j,int dim, String gen){
	  //int inc  = 0;
	  int n = numberOfGenerators();
	  //if (n > 4 && j > 3) inc = 4;
	  if (n == 8){
		 AlgebraSymbol as = thePath.getReducedProductPath().getFirst();
		 if (as.getAlgebraSymbol().getArrowType() == Bops.LEFT ||
	   as.getAlgebraSymbol().getArrowType() == Bops.UP) {
			if (thePath.countSymbol(gen) !=0)
			   return thePath.countSymbol(gen);
		 } else if (thePath.countSymbol(gen) !=0){
			return -thePath.countSymbol(gen);
		 }
	  } else {

		 if (dim == j) {if (thePath.countSymbol(gen) !=0)
			return thePath.countSymbol(gen);
		 }
		 else if (thePath.countSymbol(gen) !=0){
			return -thePath.countSymbol(gen);
		 }
	  }
	  return 0;
   }

   boolean reverseCoords() {
	 return cayley.reverseCoords();
   }

   int numberDifferentGenerators(AlgebraPath ap){
		if (ap == null) return 0;
		ListVector lv = new ListVector();
	    AlgebraSymbolVector as = ap.getReducedProductPath();
		for (as.reset();as.isNext();){
			AlgebraSymbol a = as.getNext();
			lv.addUnique(a);
		}
		return lv.size();
   }
}
