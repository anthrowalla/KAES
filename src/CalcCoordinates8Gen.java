
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CalcCoordinates8Gen extends CalcCoordinates {

    public CalcCoordinates8Gen(CayleyTable cc) {
		super(cc);
    }

   public void calcCoordinates() {
	  int i,j,dim;
	  theProducts = cayley.getProducts();

	  coordinates = new float[theProducts.size()][3]; //generators.size()
		 cayley.setCoordinates(coordinates);
												  //		coordPoints = new float[12][3];
			  //		coordInc = .1;
		 int iEnd = numberOfGenerators();
		 int dim3 = 4; //coord for third dimension
		 // boolean negativeCoordFlag = ((!generators.consistentSex()) && (iEnd == 4));
		 boolean negativeCoordFlag = (iEnd > 3);
		 boolean thirdDimFlag = (Algebra.getCurrent().getFocalElements().size() > 1);
		 System.out.println("GENs "+generators+" iend "+iEnd);
		 //System.out.println("THE PRODUCTS "+theProducts);
		 boolean reverseFlag = reverseCoords();//reverse dim1 and dim2
			Debug.prout(0," the prods"+theProducts);
			AlgebraSymbolVector av= null;
			for(i=0;i<theProducts.size();i++) {
			   AlgebraPath thePath = theProducts.getSymbol(i);
				boolean eqFlag = thePath.isReducedEquivalentPath();
			   av = thePath.getReducedProductPath();
				if (av.size() == 0) continue;
			//                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      System.out.println(" the Path "+thePath +" eqFlag "+eqFlag + " av "+av);
			   if (thirdDimFlag){
				  if (av.size() == 1) {
					 AlgebraSymbol as = av.getFirst();
					 if (as.isIdentityElement() && as.getSex().equals("F")) coordinates[i][2]= dim3;
				  }
			   }
			        //thePath = thePath.getEquivalentPathLeft();
			   if (eqFlag){//dwr 8/5
				  // int iL = numberDifferentGenerators(thePath.getEquivalentPathLeft());
				  // int iR = numberDifferentGenerators(thePath.getEquivalentPathRight());
				  // if (iL <= iR)
					//   thePath = thePath.getEquivalentPathLeft();
				   //else
					//   thePath = thePath.getEquivalentPathRight();//dwr 8/5
				   int iL = numberDifferentGenerators(thePath.getReducedEquivalentPathLeft());//dwr 8/5
				   int iR = numberDifferentGenerators(thePath.getReducedEquivalentPathRight());
				   if (iL <= iR)
					   thePath = thePath.getReducedEquivalentPathLeft();
				   else
					   thePath = thePath.getReducedEquivalentPathRight();
			   }
		//System.out.println("xxxxxxxxxxxxxxxxxxx iL"+iL+" iR "+iR+" the path "+thePath);
			   //System.out.println(" the path"+thePath);
	  //generators.reset();
			   int k = 0;
			   //			for(j=0;j<generators.size();) {
	  //for(j=0;j<3;) {
			   for(j=0;j<iEnd;) {
				  //boolean flag = true;
	  //if (!generators.isNext()){
				  if (k == generators.size()){
					 //coordinates[i][j] = -f;
					 j++;
				  } else {
					 //AlgebraSymbol gg = generators.getNext();
					 AlgebraSymbol gg = (AlgebraSymbol) generators.elementAt(k);k++;
					 //System.out.println(" gg "+ gg +" id ?"+gg.isIdentityElement() );
					 if (gg.isIdentityElement() || gg.getValue().equals("F") || gg.getValue().equals("M")) {
						continue;
					 }
					 String gen = gg.getValue();
					 if (reverseFlag){
						if (j==0) dim = 1;
						else if (j==1) dim = 0;//use this for stripped down map need to set up conditions when to use
						else dim = j;
					 } else if (true) {}
					 dim = j;
					 if (negativeCoordFlag) dim = resetDimension(gg);
						if (negativeCoordFlag){
						   float x = useCalcMethod2(thePath,j,dim,gen);
						   if (x != 0){
							  coordinates[i][dim]= x;
							  if (thirdDimFlag && gg.getSex().equals("F")) {
								   if (eqFlag) coordinates[i][2]= dim3/2;
									else
										coordinates[i][2]= dim3;//seperate male from female elements
							  } else if (thirdDimFlag && eqFlag)
								   coordinates[i][2]=dim3/2;
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