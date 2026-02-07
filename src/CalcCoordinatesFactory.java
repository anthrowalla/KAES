//
//  CalcCoordinatesFactory.java
//  Kaes
//
//  Created by Michael Fischer on Sat Apr 12 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

public class CalcCoordinatesFactory {
   public static CalcCoordinates getCalcCoordinates(CayleyTable c) {
		//AlgebraSymbolVector gens = Algebra.getCurrent().getGenerators();
		//if (numberOfGenerators(gens) == 4)
		if (c.complexPaths())
			return new CalcCoordinatesComplex(c);
		else if  (c.numberOfGenerators() > 4)
			return new CalcCoordinates8Gen(c);
		else if (c.numberOfGenerators() > 3)
			return new CalcCoordinates4Gen(c);
		else if (true) return new CalcCoordinates(c);
		else return new TestCalcCoordinates(c);
   }
}

// use: CalcCoordintates cc = CalcCoordinatesFactory.getCalcCoodinates(c)
// instead of: CalcCoordintates cc = new CalcCoordinates(c)

// where c is a CayleyTable