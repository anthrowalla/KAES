



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CalcCoordinates4Gen extends CalcCoordinates{

    public CalcCoordinates4Gen(CayleyTable cc) {
		super(cc);
    }
   public void calcCoordinates() {
		int i,j,dim;
		theProducts = cayley.getProducts();
		coordinates = new float[theProducts.size()][3]; //generators.size()
		cayley.setCoordinates(coordinates);
		int iEnd = numberOfGenerators();
		boolean negativeCoordFlag = (iEnd > 3);
		boolean sibFlag = (Algebra.getCurrent().getGenerators(Bops.LEFT).size() != 0 ||
							Algebra.getCurrent().getGenerators(Bops.RIGHT).size() != 0);
		boolean reverseFlag = reverseCoords();//reverse dim1 and dim2
			//System.out.println(" the prods"+theProducts);
			AlgebraSymbolVector av= null;
			for(i=0;i<theProducts.size();i++) {
			   AlgebraPath thePath = theProducts.getSymbol(i);
			   av = thePath.getReducedProductPath();
			        //thePath = thePath.getEquivalentPathLeft();
			  // int iL = numberDifferentGenerators(thePath.getEquivalentPathLeft());
			  // int iR = numberDifferentGenerators(thePath.getEquivalentPathRight());
			  // if (iL <= iR)
			  //      thePath = thePath.getEquivalentPathLeft();
				//else
			   //     thePath = thePath.getEquivalentPathRight();// dwr 8/5
				int iL = numberDifferentGenerators(thePath.getReducedEquivalentPathLeft());// dwr 8/5
				int iR = numberDifferentGenerators(thePath.getReducedEquivalentPathRight());
				if (iL <= iR)
			        thePath = thePath.getReducedEquivalentPathLeft();
				else
			        thePath = thePath.getReducedEquivalentPathRight();
				//System.out.println("yyyyyyyyyyyyyyyyyyy iL"+iL+" iR "+iR+" the path "+thePath);
			   int k = 0;
			   for(j=0;j<iEnd;) {
					if (k == generators.size()){
						j++;
					} else {
						AlgebraSymbol gg = (AlgebraSymbol) generators.elementAt(k);k++;
						//System.out.println(" gg "+ gg +" id ?"+gg.isIdentityElement() );
						if (gg.isIdentityElement() || gg.isSexGenerator()) continue;
						String gen = gg.getValue();
						if (reverseFlag){
							if (j==0) dim = 1;
							else if (j==1) dim = 0;//use this for stripped down map need to set up conditions when to use
							else dim = j;
						} else if (true) {}
						dim = j;
						//System.out.println("Gen is "+gen+" iend "+iEnd+ " j "+j);
						if (negativeCoordFlag && sibFlag) dim = resetSibDimension(gg);
						else if (negativeCoordFlag) dim = resetDimension(gg);
						if (negativeCoordFlag){
							//System.out.println(" j "+j+" dim "+dim+ " thePath "+thePath + " gen "+gen + "coord "+useCalcMethod2(thePath,j,dim,gen));
							float x = useCalcMethod2(thePath,j,dim,gen);
							if (x != 0){
								coordinates[i][dim]= x;
								if (av != null && av.size() > 1 && av.getLast().isIdentityElement()) {
									if (dim == 1)
										coordinates[i][0] = 1;
									else
										coordinates[i][1] = 1;
								}
							}
						}
						else{
							coordinates[i][dim]= useCalcMethod1(thePath,gen);
						}
					 j++;
				  }
			}
		}
	}
}