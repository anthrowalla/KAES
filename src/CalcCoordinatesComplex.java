

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CalcCoordinatesComplex extends CalcCoordinates{

   public CalcCoordinatesComplex(CayleyTable cc) {
		super(cc);
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
					String gen = gg.getValue();
					if (reverseFlag){
						if (j==0) dim = 1;
						else if (j==1) dim = 0;//use this for stripped down map need to set up conditions when to use
						else dim = j;
					} else //if (true) {}
					dim = j;
					float inc = 4;
					if (dim > 2) continue;
					coordinates[i][dim] = 0;
					for (int ii=0;ii<thePath.reducedPath.size();ii++) {
						AlgebraSymbol as = thePath.reducedPath.getSymbol(ii);
						if (as.equals(gg)) {
							 if (j == dim)
								  coordinates[i][dim]= coordinates[i][dim]+inc;
							 else
								  coordinates[i][dim]= coordinates[i][dim] - inc;
						}
						inc = inc/2;
				   }
					j++;
				}
			}
		}
	}

}