import java.awt.*;
import java.util.*;
import java.lang.*;

/*History
* 10/10 DR added and modified the dialogue text; formatted the populateEquationPanel text
* moved populateDialoguePanel procedures to Bops
* 10/29 DR changed Model3D to SexMarkedModel3D in doProductAndGraph (see Frame3D for parallel change)
* 01/21/01 DR removed focal term printing in populateGeneratorPanel; added test for null algebra
* in Test for Focal Element operation
* 01/23/01 DR moved CHECK_SIMPLICITY_OF_STRUCTURE to MopsOps
* 01/25/01 DR added check for already existing generators in enterBaseGenerators()
* 02/12/01 DR added code to remove identity from generator set in postGeneratorText before printing message
* 02/14 DR made minor changes to menu structure and dependencies
* 02/16 DR added null check "if (km != null)" to procedure "doProductsAndGraph"
* 2/17 DR modified procedure populateEquationPanel to use toText()
* 2/22 DR modified procedure enterSpouseTerm to call algebra.enterSpouseTerm using automatically generated spouse symbols
* 2/24 DR modified formatting in populateGeneratorPanel to account for expression of form "[x, y] z"
* removed TEST_ISOMORPHISM_TOP and _BOTTOM kludge; divided DoProductsAndGraph into two procedures:DoProducts and DoGraph;
* replaced "firstPanel" by "lastPanel"; added check in Auto_Enter_Spouse_Term to activate
* the top panel if the algebra contains a spouse element (needs further work to identify the
* "main" maps that need to be associated with algebras)
* 2/25 DR added boolean test on size of generator set to AUTO_ENTER_GENERATORS:
* 3/7 MF	commented out references to Algebra.popCurrent() which are no longer required
* 3/7 MF	modified testForIsomorphism so that the algebra created in the Cayley routines is
			replaced by theAlgebra variable
* 3/11 DR modified enterBaseGenerators to include sex marking if the kin term is sex marked M or F
* and modified postGeneratorText and postReciprocalText to include sex markings
* 5/29 DR added menu item "Add Reciprocal Equations" and output text
* 5/31 DR changed order of operations in "Construct Base Algebra"
* 6/29 DR added procedure enterIdentityElement() and set sex of identity element condition
* 6/30 DR separated out procedure enterParentOfSpouseEquations from enterParentOfChildInLawEquations;
* added procedure postParentOfSpouseEquationsText
* 7/7 DR changed theAlgebra.getFocalElements(); to Algebra.getCurrent().getFocalElements(); in postFocalElementText()
* 7/7 DR added procedure makeIsomorphicAlgebra
* 9/28 DR added procedure constructAlgebraJoin(), case MAKE_ALGEBRA_JOIN:
* 10/12 DR moved statement 	Algebra isoA = null; outside of ExecOpCode()
* 11/7 DR added procedure postLinealDescendantText(), setLinealDescendantRule(),
* added case SETUP_LINEAL_DESCENDANT_RULE:
* 11/23 DR added more text to postRecursiveEquationText procedure
* 5/27 DR seperated first part of TestForIsomorphism out as a separate procedure,
* makeCayleyTable() so as to be able to use this code without calling TestForIsomorphism
* made "global" variable KinTermMap akm; may need to change this later?
* added procedures postAlgCayleyTable(), postCayleyTableVerbose(),postKinTypeProducts()
* to be procedures called from KintermFrame to activate the Cayley Table and the Kin Type
* Product; removed the call to these procedures from TestForIsomorphism
* 7/18 DR added procedure  boolean testMapForCrossProductEquations()
* 7/21 DR added procedures termProducts(), testMapForSiblingInLawEquations()
* 7/22 DR dded procedure testMapForParentOfChildInLawEquations(), modified post... procedure
* 7/27 DR added procedure testMapForLinealDescendantRule()
* added testMap as a boolean switch for testing/not testing map for equations and rules
* 8/1 DR added procedures checkForIsomorphism, postMapNotIsomorphicText to post
* informatioin about lack of isomorphism to the user
* 8/25 DR removed global akm as it is redundant with km defined in Bops; cleaned up makeCayleyTable
* as it does not need to call doProducts, cleaned up testForIsomorphism, doProducts
* 8/26 DR added flag check to enterIdentityElement
* 11/10 DR added FIND_PARENTSIB_EQUATIONS method
* 6/6/09 DR revised enterParentOfSpouseEquations, corrected errors
*/

public class AopsOps extends Aops {
    
    //boolean termFlag = true;//no override; will beTrob terminology with trob map
   // boolean termFlag = false;//override: make Tongan from trob map--replaced by preferences
        
    public AopsOps() {
        theFrame = null;
    }
    
    public AopsOps(KintermFrame a) {
        theFrame=a;
    }
    
    /*   public boolean exec(int [] opcode){
        return exec(opcode);
    }
*/
    
    
    //------------Override for other strategies
    
	final static int CONSTRUCT_BASE_ALGEBRA=0;
	final static int ADD_SEX_STRUCTURE = CONSTRUCT_BASE_ALGEBRA+1;
	final static int ADD_SPOUSE = ADD_SEX_STRUCTURE+1;
	final static int ADD_RULES = ADD_SPOUSE+1;
	//final static int CONSTRUCT_BASE_ALGEBRA= ADD_RULES+1;
	//final static int CONSTRUCT_RECIPROCAL_STRUCTURE= CONSTRUCT_BASE_ALGEBRA+1;
	final static int CONSTRUCT_RECIPROCAL_STRUCTURE= ADD_RULES+1;
	final static int CONSTRUCT_SIBLING_STRUCTURE= CONSTRUCT_RECIPROCAL_STRUCTURE+1;
	final static int EQUIVALENCE_STRUCTURE = CONSTRUCT_SIBLING_STRUCTURE+1;
    
    final static int TEST_FOR_FOCAL_ELEMENT = EQUIVALENCE_STRUCTURE+1;
    final static int MANUAL_OPERATIONS = TEST_FOR_FOCAL_ELEMENT+1;
    //final static int CHECK_SIMPLICITY_OF_STRUCTURE = MANUAL_OPERATIONS+1;
    final static int AUTO_ENTER_GENERATORS = MANUAL_OPERATIONS+1;
    final static int AUTO_ENTER_SIB_GENERATORS = AUTO_ENTER_GENERATORS+1;
    //final static int MANUAL_ENTER_IDENTITY = AUTO_ENTER_GENERATORS+1;
    final static int AUTO_ENTER_IDENTITY = AUTO_ENTER_SIB_GENERATORS+1;
    final static int FIND_REFLEXIVE_EQUATIONS = AUTO_ENTER_IDENTITY+1;
    final static int FIND_PARENTSIB_EQUATIONS = FIND_REFLEXIVE_EQUATIONS+1;
    final static int AUTO_ESTABLISH_RECIPROCALS = FIND_PARENTSIB_EQUATIONS+1;
    final static int TEST_ISOMORPHISM = AUTO_ESTABLISH_RECIPROCALS+1;
    final static int ESTABLISH_RECIPROCAL_EQUATIONS = TEST_ISOMORPHISM+1;
    final static int MANUAL_SEX_MARKING = ESTABLISH_RECIPROCAL_EQUATIONS+1;
    final static int AUTO_SEX_MARKING = MANUAL_SEX_MARKING+1;
    final static int TEST_FOR_SPOUSE = AUTO_SEX_MARKING+1;
    //final static int MANUAL_ENTER_SPOUSE = TEST_FOR_SPOUSE+1;
    final static int AUTO_TEST_AND_ENTER_SPOUSE = TEST_FOR_SPOUSE+1;
    final static int AUTO_ENTER_SPOUSE = AUTO_TEST_AND_ENTER_SPOUSE+1;
    final static int CROSSPRODUCT_EQUATIONS_FOR_SPOUSE = AUTO_ENTER_SPOUSE+1;
    final static int SIBLING_IN_LAW_EQUATIONS_FOR_SPOUSE = CROSSPRODUCT_EQUATIONS_FOR_SPOUSE+1;
    final static int PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE = SIBLING_IN_LAW_EQUATIONS_FOR_SPOUSE+1;
    final static int PARENT_OF_SPOUSE_EQUATIONS_FOR_SPOUSE = PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE+1;
    //final static int LIMIT_CROSSPRODUCT_EQUATIONS_FOR_SPOUSE = PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE+1;
    final static int DO_PRODUCTS_AND_GRAPH = PARENT_OF_SPOUSE_EQUATIONS_FOR_SPOUSE+1;
    final static int AUTO_ENTER_SEX_GENERATORS = DO_PRODUCTS_AND_GRAPH+1;
    //final static int AUTO_ENTER_SEX_GENERATORS_ISOMORPHISM = AUTO_ENTER_SEX_GENERATORS+1;
    final static int ENTER_SPOUSE_SEX_RULE = AUTO_ENTER_SEX_GENERATORS+1;
    final static int ENTER_COUSIN_RULE = ENTER_SPOUSE_SEX_RULE+1;
    final static int SETUP_SEX_RULE = ENTER_COUSIN_RULE+1;
    final static int SETUP_CROW_SKEWING_RULE = SETUP_SEX_RULE+1;
    final static int SETUP_MB_RECIPROCAL_RULE = SETUP_CROW_SKEWING_RULE+1;
    final static int SETUP_MAKE_EQUIVALENT_RULE = SETUP_MB_RECIPROCAL_RULE+1;
    final static int SETUP_COUSIN_RULE = SETUP_MAKE_EQUIVALENT_RULE+1;
    final static int SETUP_CYLINDER_RULE = SETUP_COUSIN_RULE+1;
    final static int SETUP_SPOUSE_PRODUCT_RULE = SETUP_CYLINDER_RULE+1;
    final static int SETUP_REWRITE_PRODUCT_RULE = SETUP_SPOUSE_PRODUCT_RULE+1;
    final static int RESET = SETUP_REWRITE_PRODUCT_RULE+1;
    final static int PUSH = RESET+1;
    final static int POP = PUSH+1;
    final static int MAKE_SEX_ISOMORPHIC_ALGEBRA = POP+1;
    final static int MAKE_ALGEBRA_JOIN_SEX= MAKE_SEX_ISOMORPHIC_ALGEBRA+1;
    final static int SETUP_LINEAL_DESCENDANT_RULE = MAKE_ALGEBRA_JOIN_SEX+1;
    final static int CROSS_SEX_EQUATIONS = SETUP_LINEAL_DESCENDANT_RULE+1;
    final static int CLASSIFICATORY_STRUCTURE = CROSS_SEX_EQUATIONS+1;
    final static int DESCRIPTIVE_STRUCTURE = CLASSIFICATORY_STRUCTURE+1;
    final static int MAKE_RECIPROCAL_ISOMORPHIC_ALGEBRA = DESCRIPTIVE_STRUCTURE+1;
    final static int RECIPROCAL_EQUATIONS = MAKE_RECIPROCAL_ISOMORPHIC_ALGEBRA+1;
    final static int MAKE_ALGEBRA_JOIN_RECIPROCAL= RECIPROCAL_EQUATIONS+1;
    final static int RESTART_ALGEBRA=MAKE_ALGEBRA_JOIN_RECIPROCAL+1;
    // final static int SETUP_CROW_SKEWING_RULE=RESTART_ALGEBRA+1;
    
    
    Algebra isoA = null;
  
	boolean baseAlgebraFlag = true;
	boolean reciprocalAlgebraFlag = false;
    public boolean execOpcode(int opcode) {
        resetTransferKinInfo();
        boolean flag;
        flag = false;
        switch (opcode) {
            case PUSH : pushAKM();
                flag = true;
                break;
            case POP : popAKM();
                flag = true;
                break;
            case RESET : reset();
                flag = false;
                break;
            case TEST_FOR_FOCAL_ELEMENT:	// test for focal term
                flag = testForFocalElement();
                postFocalText(flag);
                break;
            case MANUAL_OPERATIONS:	// enter generators manually
                goKaesWindow();
                flag = true;
                break;
                /*		case CHECK_SIMPLICITY_OF_STRUCTURE:
                if (flag = tk.checkSimplicityOfStructure()) {
                    Message.create(Mode.TRACE|Mode.MANUAL,
                                   "Establised Simplicity. Now try to enter generators",
                                   "Base generator blurb.",
                                   null,90);
                    flag = true;
                } else {
                    Message.create(Mode.ALL,
                                   "Could not Establish Simplicity. Cannot proceed without simplification of kin term map.",
                                   "Simplifying blurb.",
                                   null,90);
                    flag = false;
                }
                postSimplicityText(flag);
                break;	*/
            case RESTART_ALGEBRA:	// enter generators manually
                theFrame.off.removeAllElements();
                if (theFrame.offEquations != null)
                    theFrame.offEquations.removeAllElements();
                    theFrame.equationTextArea.removeAll();
                theFrame.restart_algebra_construction();
                flag = true;
                break;
            case AUTO_ENTER_GENERATORS:	// enter generators from kin term map
				if (!tk.checkSimplicityOfStructure()){
					postKinTermMapHasMultipleGeneratorsText();
					baseAlgebraFlag = false;
					break;
				}
                int iSize = Algebra.getCurrent().getGenerators().size();
                flag = enterBaseGenerators();
				if (flag) setMenuState("Enter Generators",true,false);
                postGeneratorText((iSize != Algebra.getCurrent().getGenerators().size()));
                break;
                /*			case MANUAL_ENTER_IDENTITY:	// enter identity element manually
                flag = true;
                break;*/
            case AUTO_ENTER_SIB_GENERATORS:	// enter sib generators from kin term map
				iSize = Algebra.getCurrent().getGenerators().size();
                flag = enterSibGenerators();
				if (flag) setMenuState("Enter Sib Generators",true,false);
					postSibGeneratorText((iSize != Algebra.getCurrent().getGenerators().size()));
                break;
                /*			case MANUAL_ENTER_IDENTITY:	// enter identity element manually
                flag = true;
                break;*/
            case AUTO_ENTER_IDENTITY:	// enter identity element from kin term map
				if (!tk.checkSimplicityOfStructure()){
					postKinTermMapHasMultipleGeneratorsText();
					baseAlgebraFlag = false;
					break;
				}
                flag = enterIdentityElement();
				if (flag) setMenuState("Enter Identity",true,false);
                //flag = ka.establishIdentityTerm();
                postIdentityText(flag);
                break;
            case FIND_REFLEXIVE_EQUATIONS:
                //Debug.prout(4," find recur");
                Vector eqTypes = new Vector();
                eqTypes = ka.makeAlg.searchGeneratorsForReflexiveEquations();
                flag = (eqTypes.size() != 0);
				if (!reciprocalAlgebraFlag)
					setMenuState("Find Reflexive Equations",true,false);
				else
					setMenuState("Find Reflexive Equations ",true,false);
                if (flag) postReflexiveEquationText(eqTypes);
                    flag = true;
                break;
            case FIND_PARENTSIB_EQUATIONS:
                Debug.prout(4," find paSib");
                flag = ka.makeAlg.searchGeneratorsForParentSibEquations();
				setMenuState("Find ParentofSib Equations",true,false);
                if (flag) postParentSibEquationText();
                    flag = true;
                break;
            case AUTO_ESTABLISH_RECIPROCALS:
                Debug.prout(0,"rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrestab reciprocla");
                AlgebraSymbolVector gens = ka.makeAlg.theAlgebra.getGenerators();
                int n = gens.size();
                int m = ka.makeAlg.checkGeneratorsForReciprocals().size();
                if (m != 0) {
                    flag = ka.makeAlg.establishReciprocalsForGenerators();
                    if (flag) {
                        postReciprocalText();
                        if(ka.makeAlg.establishReciprocalEquations())
                            postReciprocalEquationsText();
                    } else {
                        m = ka.makeAlg.theAlgebra.getGenerators().size();
                        if (m > n) {
                            AlgebraSymbolVector gens1 = ka.makeAlg.theAlgebra.getGenerators();
                            StringVector sv = new StringVector();
                            for (gens1.reset();gens1.isNext();){
                                AlgebraSymbol gen = gens1.getNext();
                                if (gens.indexOf(gen) == -1 && !gen.isIdentityElement())
                                    sv.addElement(gen.getValue());
                            }
                            if (sv.size() > 0)
                                postGeneratorAdded(sv);
                        }
                    }
                } else flag = true;
                
                Message.create(Mode.TRACE|Mode.MANUAL,
                               "Looking for something.","May do so!",
                               null,90);
                break;
            case ESTABLISH_RECIPROCAL_EQUATIONS:
                flag = ka.makeAlg.establishReciprocalEquations();
                Algebra.getCurrent().addSibAnalogousEquations();
                theAlgebra.addSibAnalogousEquations();
				//System.out.print("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNN in ESE "+Algebra.getCurrent().getEquations()+" the Algebra "+theAlgebra.getEquations());
                if (flag) 
                    postReciprocalEquationsText();
					setMenuState("Add Reciprocal Equations",true,false);
				break;
            case TEST_ISOMORPHISM:	// test isomorphism
                flag = false;
                if (theFrame.panels.size() != 0 &&  Algebra.getCurrent().getGenerators().size() != 0 &&
					Algebra.getCurrent().getGenerators().size() != Algebra.getCurrent().getFocalElements().size()) {
                    postIsomorphicText(testForIsomorphism());
                }
                flag = true;
                break;
            case MAKE_SEX_ISOMORPHIC_ALGEBRA: // make isomorphic copy of algebra
                                                // Algebra a = constructAlgebraJoin();
                StringVector sv = new StringVector(2,1);
                if (!theAlgebra.isMixedPattern())
                    sv.addElement(theAlgebra.getIdentityElement().getValue());
                    //   sv.addElement(theAlgebra.getIdentityElement().getValue());
                    isoA = constructIsomorphicAlgebra(sv,false);
                
                StringVector asv1 = theAlgebra.getValues();
                StringVector asv2 = isoA.getValues();
                //Algebra aa = theAlgebra.makeAlgebraJoin(a);
                
                
                /*               theFrame.setFrameAlgebra(aa);
                km.theKinshipAlgebra.makeAlg.theAlgebra = aa;
                theAlgebra = aa;*/
                
                //  Debug.prout(4,"algebra elements"+Algebra.getCurrent().getElements());
                
                // Debug.prout(4,"algebra eq "+Algebra.getCurrent().getEquations());
                //tk.buildTables();
                // Debug.prout(4," ide ements "+Algebra.getCurrent().getIdentityElement());
                postIsomorphicAlgebraText(asv1,asv2);
				setMenuState("Isomorphic Sex Algebra",true,false);
                flag = true;
                break;
         /*   case MAKE_RECIPROCAL_ISOMORPHIC_ALGEBRA: // make isomorphic copy of algebra
                StringVector sv1 = new StringVector(2,1);
				sv1.addElement(theAlgebra.getIdentityElement().getValue());
                isoA = constructIsomorphicAlgebra(sv1,true);
                
                StringVector aasv1 = theAlgebra.getValues();
                StringVector aasv2 = isoA.getValues();
                postIsomorphicAlgebraText(aasv1,aasv2);
                flag = true;
                break;*/
            case MAKE_ALGEBRA_JOIN_SEX:	//sex marking -- variable marking manual??
                theFrame.activateCurrentMapPanel(Bops.NONE);
                Algebra aa = null;
                if (isoA == null) {
                    sv = new StringVector(2,1);
                    sv.addElement(theAlgebra.getIdentityElement().getValue());
                    aa = constructAlgebraJoin(sv,false);//need to pass boolean value??
                }
                    else aa = theAlgebra.makeAlgebraJoin(isoA);
                if (aa != null) postMakeSexAlgebraJoinText();
                    theFrame.setFrameAlgebra(aa);
                km.theKinshipAlgebra.makeAlg.theAlgebra = aa;
                theAlgebra = aa;
                if (theAlgebra.getFocalElements().size() == 2){
                    theFrame.makeMenus.tablemenuItem.setEnabled(false);//CHECK THIS!!!!!
                    theFrame.makeMenus.gridmenuItem.setEnabled(false);
                }
                    // theAlgebra.addCrossSexEquations();
                    // postCrossSexEquations();
                    /*  AlgebraSymbolVector child = theAlgebra.getDownArrows();
                    theAlgebra.removeSexMarking((AlgebraSymbol)child.elementAt(0));
                    theAlgebra.removeRedundancy();*/
                    /*   Debug.prout(4," generators "+theAlgebra.getGenerators() +
                    " equations "+theAlgebra.getEquations());*/
					setMenuState("Algebra Sex Join",true,false);
                    flag = true;
                break;
  /*          case MAKE_ALGEBRA_JOIN_RECIPROCAL:	//sex marking -- variable marking manual??
                theFrame.activateCurrentMapPanel(Bops.NONE);
               // StringVector sv1 = new StringVector(2,1);
				//sv1.addElement(theAlgebra.getIdentityElement().getValue());
              //  isoA = constructIsomorphicAlgebra(sv1,true);
                
               // StringVector aasv1 = theAlgebra.getValues();
              //  StringVector aasv2 = isoA.getValues();
              //  postIsomorphicAlgebraText(theAlgebra.getValues(),isoA.getValues());
                Algebra aa1 = null;
                isoA = null;//forces a new isomorphic algebra to be constructed
                if (isoA == null) {
                    sv = new StringVector(2,1);
                    sv.addElement(theAlgebra.getIdentityElement().getValue());
                    aa1 = constructAlgebraJoin(sv,true);//need to pass boolean value??
                }
                    else 
				aa1 = theAlgebra.makeAlgebraJoin(isoA);
               // StringVector asv1 = theAlgebra.getValues();
               // StringVector asv2 = isoA.getValues();
                Debug.prout(4,"JOINEDDDDDDDDDDD");
                if (aa1 != null) {
					postMakeReciprocalAlgebraJoinText();
                    theFrame.setFrameAlgebra(aa1);
					km.theKinshipAlgebra.makeAlg.theAlgebra = aa1;
					theAlgebra = aa1;
					flag = true;
					setMenuState("Make Reciprocal Algebra",true,false);
				} 
					else flag = false;
                Debug.prout(4,"JOINEDDDDDDDDDDD DONE");
               // flag = true;
                break;*/
				
				
            case MAKE_ALGEBRA_JOIN_RECIPROCAL:	//sex marking -- variable marking manual??
                theFrame.activateCurrentMapPanel(Bops.NONE);
                StringVector sv1 = new StringVector(2,1);
				sv1.addElement(theAlgebra.getIdentityElement().getValue());
                isoA = constructIsomorphicAlgebra(sv1,true);
                
				// StringVector aasv1 = theAlgebra.getValues();
				//  StringVector aasv2 = isoA.getValues();
                postIsomorphicAlgebraText(theAlgebra.getValues(),isoA.getValues());
				// Algebra aa1 = null;
				// isoA = null;//forces a new isomorphic algebra to be constructed
				// if (isoA == null) {
				//   sv = new StringVector(2,1);
				//  sv.addElement(theAlgebra.getIdentityElement().getValue());
				//   aa1 = constructAlgebraJoin(sv,true);//need to pass boolean value??
				//  }
				//  else 
				Algebra aa1 = theAlgebra.makeAlgebraJoin(isoA);
				// StringVector asv1 = theAlgebra.getValues();
				// StringVector asv2 = isoA.getValues();
                Debug.prout(4,"JOINEDDDDDDDDDDD aaa1" + aa1.getGenerators()+aa1.getEquations());
                if (aa1 != null) {
					postMakeReciprocalAlgebraJoinText();
                    theFrame.setFrameAlgebra(aa1);
					km.theKinshipAlgebra.makeAlg.theAlgebra = aa1;
					theAlgebra = aa1;
					flag = true;
					setMenuState("Make Reciprocal Algebra",true,false);
				} 
					else flag = false;
                Debug.prout(4,"JOINEDDDDDDDDDDD DONE");
				// flag = true;
                break;
				
				
				
            case CROSS_SEX_EQUATIONS:
                ka.makeAlg.addCrossSexEquations();
                setMenuState("Add Cross Sex Equations",true,false);
                postCrossSexEquations();
                flag = true;
                break;
            case DESCRIPTIVE_STRUCTURE:
                flag = true;
                break;
            case CLASSIFICATORY_STRUCTURE:
                boolean flagM = tk.testMapForChildEquivalence("M");
                boolean flagF = tk.testMapForChildEquivalence("F");
                String sex1 = "";
                if (flagM && flagF) sex1 = "N";
                else if (flagM) sex1 = "M";
                else if (flagF) sex1 = "F";
                    flagM = tk.testMapForChildSibEquivalence("M");
                flagF = tk.testMapForChildSibEquivalence("F");
                String sex2 = "";
                if (flagM && flagF) sex2 = "N";
                else if (flagM) sex2 = "M";
                else if (flagF) sex2 = "F";
				boolean singleChild = tk.testMapForSingleChildProperty();
				String type = MainFrame.prefs.getString("Terminology_type-_Trobriand_or_Tongan","Trobriand");	
                if (type.equals("Tongan")) {singleChild = false;sex1 = "F";sex2 = "N";}//HARD WIRING!!! Matches Tongan Struture
				ka.makeAlg.addClassificatoryStructure(singleChild,sex1,sex2);
                postClassificatoryEquations(singleChild,sex1,sex2);
				setMenuState("Classificatory Equivalences",true,false);
                flag = true;
                break;
            case RECIPROCAL_EQUATIONS:
                if (theAlgebra == null) Debug.prout(4," ALGEBRTA IS NUL");
				//if (tk.testMapForSibGenerators()) theAlgebra.addReciprocalEquations(
                theAlgebra.addReciprocalEquations(tk.testMapForAtomicSib());
                Debug.prout(4,"done eq "+theAlgebra.getEquations());
                postReciprocalEquationsText();
                Debug.prout(4,"done post ");
				setMenuState("Add Reciprocal Element Equations",true,false);
                flag = true;
                break;
            case MANUAL_SEX_MARKING:	//sex marking -- variable marking manual??
                flag = true;
                break;
            case AUTO_SEX_MARKING:	//sex marking -- variable marking auto??
                flag = true;
                break;
            case TEST_FOR_SPOUSE: // test for presence of spouse term
                flag = testForSpouse();
                postTestSpouseText(flag);
                //flag = true;
                break;
                /*			case MANUAL_ENTER_SPOUSE: // insert spouse terms - manual
                flag = true;
                break;*/
            case AUTO_TEST_AND_ENTER_SPOUSE: // insert spouse terms - auto
                flag = execOpcode(TEST_FOR_SPOUSE); // opcode for test for spouse element
                                                    //flag = false;
                if (flag) break; // already has spouse generator; no insertion here as depends on fall through
				else if (tk.getEffectiveGenerators(SPOUSE).size()== 0) {
					postSpouseText(false);
					postKinTermMapSpouseText(false);
					//break;
				}   
					
			case AUTO_ENTER_SPOUSE:
                flag = enterSpouseTerm();
                if (flag) theFrame.activateCurrentMapPanel(Bops.SPOUSE);{
                    postSpouseText(flag);
					//TransferKinInfoVector sp = getEffectiveGenerators(SPOUSE);
					postKinTermMapSpouseText(tk.getEffectiveGenerators(SPOUSE).size()!=0);
					setMenuState("Enter Spouse Generator",true,false);
				}
                flag = true;
                break;
                
            case CROSSPRODUCT_EQUATIONS_FOR_SPOUSE:	//sex marking -- variable marking manual??
                if (tk.getEffectiveGenerators(SPOUSE).size()!= 0 && tk.testMapForSpouseOfParentEquations()){
                    // if (testMapForCrossProductEquations()){
                    flag = enterSpouseOfParentEquations();
                    postSpouseOfParentEquationsText(flag);
                    }
                else if (tk.getEffectiveGenerators(SPOUSE).size()!= 0) postSpouseOfParentEquationsText(false);
				setMenuState("Enter Spouse Crossproduct Equations",true,false);
                flag = true;
                
                break;
                
            case SIBLING_IN_LAW_EQUATIONS_FOR_SPOUSE: // insert spouse terms - manual
                if (theAlgebra.getFocalElements().size() == 2){
                    enterSibOfSpouseEquations();
                    postSibOfSpouseEquationsText();
                }
                else if (tk.getEffectiveGenerators(SPOUSE).size()!= 0 && tk.testMapForSiblingInLawEquations()){
                    //else if (testMapForSiblingInLawEquations()){
                    flag = enterSiblingInLawEquations();
                    postSiblingInLawEquationsText(flag);
                    } 
				else if (tk.getEffectiveGenerators(SPOUSE).size()!= 0) postSiblingInLawEquationsText(false);
                flag = true;
                break;
                
            case PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE: // insert spouse terms - manual
                                                                // if (testMapForParentOfChildInLawEquations()){
                if (tk.getEffectiveGenerators(SPOUSE).size()!= 0 && tk.testMapForParentOfChildInLawEquations()){
                   // enableInstructionInSet("Algebra Operations",AopsProg.ADD_RULES,true);
                  //  enableInstructionInSet("Algebra Operations",AopsProg.ADD_SPOUSE,false);
                    
                    flag = enterParentOfChildInLawEquations();
                    postParentOfChildInLawEquationsText(flag);
                    flag = enterSibOfChildInLawEquations();//NEED TO PUT THIS SOMEWHERE ELSE!
                    postParentOfChildInLawEquationsText(flag);
                }
                else if (tk.getEffectiveGenerators(SPOUSE).size()!= 0) postParentOfChildInLawEquationsText(false);
				setMenuState("Parent of Child-in-law Not a Kin Term",true,false);
                flag = true;
                break;
                
            case PARENT_OF_SPOUSE_EQUATIONS_FOR_SPOUSE: //enter equations of form parent of parent of spouse = 0
               // enableInstructionInSet("Algebra Operations",AopsProg.ADD_RULES,true);
               // enableInstructionInSet("Algebra Operations",AopsProg.ADD_SPOUSE,false);
				if (tk.getEffectiveGenerators(SPOUSE).size()!= 0){
					flag = enterParentOfSpouseEquations();
					postParentOfSpouseEquationsText(flag);
				}
				setMenuState("Parent of ... of Parent of Spouse Not a Kin Term",true,false);
                flag = true;
                break;
            case AUTO_ENTER_SEX_GENERATORS: // enter the M and F generators
                                            // make this a ENTER_A_SEX_GENERAORT routine to be called twice. First will enter M, then F
                flag = theAlgebra.enterSexGenerator(MALE);
                postMaleGenerator(flag);
                flag = theAlgebra.enterSexGenerator(FEMALE);
                postFemaleGenerator(flag);
				setMenuState("Sex Mark Generators",true,false);
                flag = true;
                break;
            case SETUP_SEX_RULE:
                //setSexRule();
                if (((SexRule) RuleFactory.setRule(RuleFactory.SEXRULE)).getActiveRule()){
					postSexRuleText();
					setMenuState("AKT Sex Rule",true,false);
				}
                flag = true;
                break;
            case SETUP_COUSIN_RULE:
                //setCousinRule();
                if (((CousinRule) RuleFactory.setRule(RuleFactory.COUSINRULE)).getActiveRule()){
					 postCousinRuleText();
					setMenuState("AKT Cousin Removal Rule",true,false);
				}
                   
                flag = true;
                break;
            case SETUP_LINEAL_DESCENDANT_RULE:
                Debug.prout(4," inn lineal");
                if (((LinealDescendantRule) RuleFactory.setRule(RuleFactory.LINEALDESCENDANTRULE)).getActiveRule()){
					postLinealDescendantRuleText(true);
					setMenuState("Shipibo Lineal Descendant Rule",true,false);
				}
                    
                    //if (testMap == false && tk.testMapForLinealDescendantRule()){
                    // if (testMapForLinealDescendantRule()){
                    Debug.prout(4," inn linal 2");
                
                //RuleFactory.setRule(RuleFactory.LINEALDESCENDANTRULE);
                //setLinealDescendantRule();
                //Debug.prout(4," inn linal 3");
                //if (((LinealDescendantRule) RuleFactory.setRule(RuleFactory.LINEALDESCENDANTRULE)).getActiveRule())
                //postLinealDescendantRuleText(true);
                //Debug.prout(4," inn linal 3");
                
                // postLinealDescendantRuleText(false);
                
                flag = true;
                break;
            case SETUP_MAKE_EQUIVALENT_RULE:
                //setMakeEquivalentRule();
                if (((MakeEquivalentRule) RuleFactory.setRule(RuleFactory.MAKEEQUIVALENTRULE)).getActiveRule()){
					postMakeEquivalentRuleText();
					setMenuState("Make Elements Equivalent Rule",true,false);
				}
                    
                flag = true;
                break;
            case SETUP_REWRITE_PRODUCT_RULE:
                if (((RewriteProductRule) RuleFactory.setRule(RuleFactory.REWRITEPRODUCTRULE)).getActiveRule()){
					postRewriteProductRuleText();
					setMenuState("Older Younger Sib Rule",true,false);
				}
				flag = true;
                break;
            case SETUP_CROW_SKEWING_RULE:
                if (((CrowSkewingRule) RuleFactory.setRule(RuleFactory.CROWSKEWINGRULE)).getActiveRule()){
					postCrowSkewingRuleText();
					setMenuState("Crow Skewing Rule",true,false);
				}
                    
                flag = true;
                break;
            case SETUP_MB_RECIPROCAL_RULE:
                if (((MBSelfReciprocalRule) RuleFactory.setRule(RuleFactory.MBSELFRECIPROCALRULE)).getActiveRule()){
					postMBSelfReciprocalRuleText();
					setMenuState("Self Reciprocal Rule",true,false);
				}
                    
                //setMBSelfReciprocalRule();
                flag = true;
                break;
            case SETUP_SPOUSE_PRODUCT_RULE:
                //setSpouseProductRule();
                if (((SpouseProductRule) RuleFactory.setRule(RuleFactory.SPOUSEPRODUCTRULE)).getActiveRule()){
					postSpouseProductRuleText();
					setMenuState("Spouse of Product Rule",true,false);
				}
                    
                flag = true;
                break;
            case SETUP_CYLINDER_RULE:
                if (((CylinderRule) RuleFactory.setRule(RuleFactory.CYLINDERRULE)).getActiveRule()){
					postCylinderRuleText();
					setMenuState("Top/Bottom Identity Rule",true,false);
				}
                flag = true;
                break;
            case DO_PRODUCTS_AND_GRAPH :
                Debug.prout(0," in prods");
                waitCursor();
                //	Debug.prout(4,"STATUS OF GENERATORS B: "+theAlgebra.getGenerators());
                if (theFrame.panels.size() != 0) {
                    Debug.prout(0," in prods1");
                    localVars.putData("CayleyTable",
                                        //doProductsAndGraph((TransferKinInfoVector) theFrame.firstPanel().getTransferKinInfo()));
                                        doProductsAndGraph((TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo()));
                    }
                    Debug.prout(0," in prods2");
                flag = true;
                arrowCursor();
                break;
  // AopsProg insert here              
				
			case CONSTRUCT_BASE_ALGEBRA:
				baseAlgebraFlag = true;
				theFrame.getAlgebraBuiltins().exec(opArray[5]);
				theFrame.getAlgebraBuiltins().exec(opArray[9]);
				if (baseAlgebraFlag == false){
					flag = false;
					baseAlgebraFlag = true;
					break;
				}
				flag = true;
				break;
			case CONSTRUCT_RECIPROCAL_STRUCTURE:
				reciprocalAlgebraFlag = true;
				theFrame.getAlgebraBuiltins().exec(opArray[6]);
				theFrame.getAlgebraBuiltins().exec(opArray[9]);
				reciprocalAlgebraFlag = false;
				flag = true;
				break;
				
			case CONSTRUCT_SIBLING_STRUCTURE:
				//reciprocalAlgebraFlag = true;
				theFrame.getAlgebraBuiltins().exec(opArray[10]);
				theFrame.getAlgebraBuiltins().exec(opArray[9]);
				//reciprocalAlgebraFlag = false;
				flag = true;
				break;
				
			case ADD_SEX_STRUCTURE:
				if (neutralGenerators()) {
					theFrame.getAlgebraBuiltins().exec(opArray[2]);
					theFrame.getAlgebraBuiltins().exec(opArray[9]);
				    flag = true;
					break;
				} else {
				    theFrame.getAlgebraBuiltins().exec(opArray[3]);
				    theFrame.getAlgebraBuiltins().exec(opArray[9]);
				    flag = true;
					break;
				}
			case EQUIVALENCE_STRUCTURE:
				reset();
				if (ka.makeAlg.getAlgebraClass() == MakeAlgebra.CLASSIFICATORY)
					theFrame.getAlgebraBuiltins().exec(opArray[8]);
				else//DESCRIPTIVE
					theFrame.getAlgebraBuiltins().exec(opArray[7]);
				theFrame.getAlgebraBuiltins().exec(opArray[9]);
				flag = true;
				break;
			case ADD_SPOUSE:
				theFrame.getAlgebraBuiltins().exec(opArray[1]);
				theFrame.getAlgebraBuiltins().exec(opArray[9]);
				flag = true; // ?????????????
				break;
			case ADD_RULES:
				reset();
				boolean ldRuleFlag = tk.testMapForLinealDescendantRule();//first implementation of a test for a rule
				Debug.prout(0," switch "+ka.makeAlg.getAlgebraType()+" ldRule "+ldRuleFlag);
				switch (ka.makeAlg.getAlgebraType()){
					case MakeAlgebra.TROB:
						RuleFactory.setInitialRuleStatus(RuleFactory.SEXRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.COUSINRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.LINEALDESCENDANTRULE,ldRuleFlag);
						break;
					case MakeAlgebra.TONGAN:
						RuleFactory.setInitialRuleStatus(RuleFactory.SEXRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.COUSINRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.LINEALDESCENDANTRULE,ldRuleFlag);
						RuleFactory.setInitialRuleStatus(RuleFactory.CROWSKEWINGRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.SPOUSEPRODUCTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.CYLINDERRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.MBSELFRECIPROCALRULE,false);
						break;
				    case MakeAlgebra.SHIPIBO:
						System.out.println("IN SHIPIBO RULE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						RuleFactory.setInitialRuleStatus(RuleFactory.LINEALDESCENDANTRULE,ldRuleFlag);
						RuleFactory.setInitialRuleStatus(RuleFactory.SEXRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.COUSINRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.MBSELFRECIPROCALRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.MAKEEQUIVALENTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.REWRITEPRODUCTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.CROWSKEWINGRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.SPOUSEPRODUCTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.CYLINDERRULE,false);
						break;
				    case MakeAlgebra.AKT:
						RuleFactory.setInitialRuleStatus(RuleFactory.MBSELFRECIPROCALRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.MAKEEQUIVALENTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.REWRITEPRODUCTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.CROWSKEWINGRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.LINEALDESCENDANTRULE,ldRuleFlag);
						RuleFactory.setInitialRuleStatus(RuleFactory.SPOUSEPRODUCTRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.CYLINDERRULE,false);
						RuleFactory.setInitialRuleStatus(RuleFactory.SEXRULE,Algebra.getCurrent().getGenerators(Bops.SPOUSE) != null);
						break;
			    }
					flag = theFrame.getAlgebraBuiltins().exec(opArray[4]);
				flag = theFrame.getAlgebraBuiltins().exec(opArray[9]);
				flag = true;
				break;				

  // end AopsProg insert
            default:	flag = false;
            Debug.prout(4,"Sitting in default at stage "+stage+"");
                stage = -1;
                break;
        }
		//System.out.println(" flag ========================="+baseAlgebraFlag+" flag "+flag);
        return flag;
    }
    
/*	void postSimplicityText(boolean flag) {
	    if (flag)
	        populateDialoguePanel("The kin term map has been simplified.");
	    else
	        populateDialoguePanel("The kin term map is still complex.");
	}*/

    void postFocalText(boolean flag) {
        if (flag) {
            AlgebraSymbolVector fe = Algebra.getCurrent().getFocalElements();
            //  AlgebraSymbolVector fe = theAlgebra.getFocalElements();
            StringVector v = fe.toStringVector();
            if (v.size() > 1)
                populateDialoguePanel("The focal elements of the algebra are ", v,".");
            else
                populateDialoguePanel("The focal element of the algebra is ",v,".");
        }else
            populateDialoguePanel("The algebra does not have a focal element.");
    }
    
    void postGeneratorText(boolean flag) {
        StringVector v = Algebra.getCurrent().getGenerators().toStringVector();
        if (Algebra.getCurrent().getIdentityElement() != null) {
            v.removeElement((String) Algebra.getCurrent().getIdentityElement().toString());
        }
        if (flag) {
            if (v.size() == 1){
                String sex = ((AlgebraSymbol) Algebra.getCurrent().getGenerators().getLast()).getSex();
                if (sex.equals("N"))
                    populateDialoguePanel("Added the generator, "+v.elementAt(0)+".");
                else {
                    if (sex.equals("M"))
                        populateDialoguePanel("Added the male marked generator, "+v.elementAt(0)+".");
                    else
                        populateDialoguePanel("Added the female marked generator, "+v.elementAt(0)+".");
                }
            }
            else
                populateDialoguePanel("Added the generators, ",v,".");
        }else {
            if (v.size() == 0)
                populateDialoguePanel("No generator has been added.\n");
            else if (v.size() == 1)
                populateDialoguePanel("The generator "+v.elementAt(0)+"has already been added.\n");
            else
                populateDialoguePanel("The generators ",v,"have already been added.\n");
        }
    }
	
    void postSibGeneratorText(boolean flag) {
        StringVector v = Algebra.getCurrent().getSideArrows().toStringVector();
        if (Algebra.getCurrent().getIdentityElement() != null) {
            v.removeElement((String) Algebra.getCurrent().getIdentityElement().toString());
        }
        if (flag) {
            if (v.size() == 1){
                String sex = ((AlgebraSymbol) Algebra.getCurrent().getGenerators().getLast()).getSex();
                if (sex.equals("N"))
                    populateDialoguePanel("Added the sib generator, "+v.elementAt(0)+".");
                else {
                    if (sex.equals("M"))
                        populateDialoguePanel("Added the male marked sib generator, "+v.elementAt(0)+".");
                    else
                        populateDialoguePanel("Added the female marked sib generator, "+v.elementAt(0)+".");
                }
            }
            else
                populateDialoguePanel("Added the sib generators, ",v,".");
        }else {
            if (v.size() == 0)
                populateDialoguePanel("No sib generator has been added.\n");
            else if (v.size() == 1)
                populateDialoguePanel("The sib generator "+v.elementAt(0)+"has already been added.\n");
            else
                populateDialoguePanel("The sib generators ",v,"have already been added.\n");
        }
    }
	
    void postKinTermMapHasMultipleGeneratorsText(){
		populateDialoguePanel("The kin term map must be simplified before constructing the base algebra.\n");
	}

		void postGeneratorAdded(StringVector sv){
        String s = "";
        for (int i = 0; i < sv.size();i++){
            s = s + sv.elementAt(i);
            if (i < sv.size()-2) s = s + ", ";
            if (i == sv.size()-2) s = s + " and ";
        }
        if (sv.size() == 1)
            populateDialoguePanel("The generator, "+s + ", has been added.");
        if (sv.size() > 1)
            populateDialoguePanel("The generators, "+s + ", have been added.");
    }
    
    void postIdentityText(boolean flag) {
        if (flag) {
            String s = Algebra.getCurrent().getIdentityElement().toString();
            populateDialoguePanel("Added the identity element, "+s+".");
        } else
            populateDialoguePanel("No identity element has been added.");
    }
    
    void postReflexiveEquationText(Vector eqType) {
        for (int i=0;i< eqType.size();i++){
            int j = ((Integer)eqType.elementAt(i)).intValue();
            if (j > Equation.NONINVERTIBLE) j = j - Equation.NONINVERTIBLE;
            switch(j){
                case Equation.GENERATIONLIMIT:
                    Debug.prout(4,"in equation text");
                    populateDialoguePanel("Reflexive equation of the form PP...P=P...P added.");
                    break;
                case Equation.CLASSIFICATORY:
                    populateDialoguePanel("Equation of the form CPP..P=P...P added.");
                    break;
                case Equation.SIBLING:
                    populateDialoguePanel("Equation of the form CCPP=CP added.");
                    break;
                case Equation.SIBDEF:
                    populateDialoguePanel("Equation of the form BB=B added.");
                    break;
                default:
                    populateDialoguePanel("Reflexive equation added.");
                    break;
            }
        }
    }
    
    void postParentSibEquationText() {
        populateDialoguePanel("Equation of the form PC=P added.");
    }
    
    void postReciprocalText() {
        AlgebraSymbolVector gen = Algebra.getCurrent().getGenerators();
        AlgebraSymbolVector rec = new AlgebraSymbolVector();
        StringVector v = new StringVector();
        for (gen.reset();gen.isNext();) {
            AlgebraSymbol as = gen.getNext();
            if (as != Algebra.getCurrent().getIdentityElement() && rec.indexOf(as) == -1) {
                rec.addElement(as.getReciprocal());
                String sex =  as.getReciprocal().getSex();
                if (sex.equals("N"))
                    v.addElement("The element, "+as.getReciprocal().toString()+", added as the reciprocal for the generator, "
                                 + as.toString()+".");
                else {
                    if (sex.equals("M"))
                        v.addElement("The male marked element, "+as.getReciprocal().toString()+", added as the reciprocal for the generator, "
                                     + as.toString()+".");
                    else
                        v.addElement("The female marked element, "+as.getReciprocal().toString()+", added as the reciprocal for the generator, "
                                     + as.toString()+".");
                    
                }
            }
        }
        //StringVector v = rec.toStringVector();
        populateDialoguePanel(v+"\n");
    }
    
    void postTestSpouseText(boolean flag) {
        if (flag) populateDialoguePanel("Spouse element already added to the algebra.");
        else populateDialoguePanel("The algebra does not have a spouse element.");
    }
    
    void postReciprocalEquationsText() {
        populateDialoguePanel("Reciprocal equations added to algebra.");
    }
    
    void postKinTermMapSpouseText(boolean flag) {
        if (!flag) populateDialoguePanel("The kin term map does not have a spouse term.");
    }
	
    void postSpouseText(boolean flag) {
        if (flag) {
            AlgebraSymbolVector av = theAlgebra.getSpouseArrows();
            StringVector v = av.toStringVector();
           // System.out.println("Start post "+v);
            if (v.size() == 1)
                populateDialoguePanel("Added the spouse element ",v,".");
            else
                populateDialoguePanel("Added the spouse elements ",v,".");
        }else
            populateDialoguePanel("No spouse element added.");
       // populateDialoguePanel("\n");
    }
    
    void postSpouseOfParentEquationsText(boolean flag) {
        if (flag) populateDialoguePanel("Added equations of the form 'Spouse of Parent = Parent.'");
        else populateDialoguePanel("Kin term map lacks equations of the form 'Spouse of Parent = Parent.'");
    }
    
    void postSiblingInLawEquationsText(boolean flag) {
        if (flag) populateDialoguePanel("Added equations of the form 'Spouse of Sibling = Sibling of Spouse.'");
        else populateDialoguePanel("Kin term map lacks equations of the form 'Spouse of Sibling = Sibling of Spouse'.");
    }
    
    void postSibOfSpouseEquationsText() {
        populateDialoguePanel("Added equations of the form 'Brother of Husband = 0' and "
                              + "'Sister of Wife = 0'.");
        if (theAlgebra.getLeftSideArrows().size() != 0 && theAlgebra.getRightSideArrows().size() != 0)
            populateDialoguePanel("Added equations of the form 'Older/Younger Brother of Wife = 0' "
                                  +"and 'Older/Younger Sister of Husband = 0'.");
        
    }
    
    void postParentOfChildInLawEquationsText(boolean flag) {
        if (flag) populateDialoguePanel("Added equations of the form 'Parent of Child-in-law is not a kin term.'");
        else populateDialoguePanel("Equations of the form 'Parent of Child-in-law is not a kin term' may be needed in the algebraic model.'");
    }
    
    void postParentOfSpouseEquationsText(boolean flag) {
        if (flag) populateDialoguePanel("Added equations of the form 'Parent of ... of Parent of Spouse is not a kin term.'");
    }
    
    void postMaleGenerator(boolean flag) {
        if (flag) populateDialoguePanel("The male sex generator element, M, was added to the algebra.");
    }
    
    void postFemaleGenerator(boolean flag) {
        if (flag) populateDialoguePanel("The female sex generator element, F, was added to the algebra.");
    }
    
    void postSexRuleText() {
        RuleVector rv = theAlgebra.getRules();
        SexRule sr = null;
		if ( rv != null){
			for (rv.reset();rv.isNext();){
				Rule r = (Rule) rv.getNext();
				if (r instanceof SexRule) {
					sr = (SexRule) r;
				}
			} 
			
		}
		if (sr != null)
            populateDialoguePanel("Added the "+sr.getRuleText() + " Rule: An algebra element is " +
                                  "sex marked when Spouse of the corresponding kin term is a kin term or Spouse of the reciprocal " +
                                  "kin term is a kin term.\n");
    }
    
    void postCousinRuleText() {
        RuleVector rv = theAlgebra.getRules();
        CousinRule cr = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof CousinRule) {
                cr = (CousinRule) r;
            }
        }
        if (cr != null) populateDialoguePanel("Added the "+cr.getRuleText()+"  Rule.\n");
    }
    
    void postCrowSkewingRuleText() {
        RuleVector rv = theAlgebra.getRules();
        CrowSkewingRule cr = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof CrowSkewingRule) {
                cr = (CrowSkewingRule) r;
            }
        } if (cr != null)
            populateDialoguePanel("Added the "+cr.getRuleText()+": 'Child' of 'Brother' "+
                                  "of female speaker equates with 'Child' of 'Son' of female speaker "+
                                  "and its reciprocal, 'Sister' of 'Father' equates with "+
                                  "'Mother' of 'Father'.\n");
    }
    
    void postMBSelfReciprocalRuleText() {
        RuleVector rv = theAlgebra.getRules();
        MBSelfReciprocalRule mr = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof MBSelfReciprocalRule) {
                mr = (MBSelfReciprocalRule) r;
            }
        }if (mr != null)
            populateDialoguePanel("Added the "+mr.getRuleText()+" Rule: 'Brother' of "+
                                  "'Mother' is a self reciprocal element.\n");
    }
    
    void postSpouseProductRuleText() {
        RuleVector rv = theAlgebra.getRules();
        SpouseProductRule ss = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof SpouseProductRule) {
                ss = (SpouseProductRule) r;
            }
        }if (ss != null)
            populateDialoguePanel("Added the "+ ss.getRuleText()+" Rule: 'Spouse' of "+
                                  "'Sibling' = 'Sibling'.\n");
    }
    
    
    void postCylinderRuleText() {
        RuleVector rv = theAlgebra.getRules();
        CylinderRule cr = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof CylinderRule) {
                cr = (CylinderRule) r;
            }
        }if (cr != null)
            populateDialoguePanel("Added the "+cr.getRuleText()+" Rule: "+
                                  "ChildChild...Child is equivalent to ParentParent...Parent.\n");
    }
    
    void postMakeEquivalentRuleText() {
        RuleVector rv = theAlgebra.getRules();
        MakeEquivalentRule mer = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof MakeEquivalentRule) {
                mer = (MakeEquivalentRule) r;
            }
        }
        String txt = "";
        if (mer != null){
            ListVector lv = mer.getEquivalentElements();
            int i = 0;
            for (lv.reset();lv.isNext();){
                i++;
                ListVector l = (ListVector)lv.getNext();
                AlgebraSymbolVector as1 = (AlgebraSymbolVector)l.elementAt(0);
                AlgebraSymbolVector as2 = (AlgebraSymbolVector)l.elementAt(1);
                String txt1 = as1.makeTransliteration();
                String txt2 = as2.makeTransliteration();
                txt = txt + txt1 + " equates with "+ txt2;
                if (lv.size() > 1) {
                    if (i < lv.size() -1) txt = txt+", ";
                    else if (i < lv.size()) txt = txt+" and ";
                }
            }
            populateDialoguePanel("Added the "+mer.getRuleText()+" Rule: "+txt+".\n");
        }
    }
    void postRewriteProductRuleText() {
        RewriteProductRule oySibR = (RewriteProductRule) RuleFactory.getRule(RuleFactory.REWRITEPRODUCTRULE);
        /*RuleVector rv = theAlgebra.getRules();
        //OlderYoungerSibRule oySibR = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof OlderYoungerSibRule) {
                oySibR = (OlderYoungerSibRule) r;
            }
        }*/
        String txt = "";
        if (oySibR != null){
            ListVector lv = oySibR.getEquivalentProducts();
            int i = 0;
            for (lv.reset();lv.isNext();){
                i++;
                ListVector l = (ListVector)lv.getNext();
                AlgebraSymbolVector as1 = (AlgebraSymbolVector)l.elementAt(0);
                AlgebraSymbolVector as2 = (AlgebraSymbolVector)l.elementAt(1);
                String txt1 = as1.makeTransliteration();
                String txt2 = as2.makeTransliteration();
				if (txt2.equals("")) continue;
				//System.out.println(" as1 "+as1+" txt1 "+txt1+" as2 "+as2+" txt2 "+txt2);
                txt = txt + txt1 + " equates with "+ txt2;
                if (lv.size() > 1) {
                    if (i < lv.size() -1) txt = txt+", ";
                    else if (i < lv.size()) txt = txt+" and ";
                }
            }
        }
        populateDialoguePanel("Added the "+oySibR.getRuleText()+" Rule: "+txt+".\n");
    }
    
    void postLinealDescendantRuleText(boolean flag) {
        RuleVector rv = theAlgebra.getRules();
        LinealDescendantRule ldr = null;
        for (rv.reset();rv.isNext();){
            Rule r = (Rule) rv.getNext();
            if (r instanceof LinealDescendantRule) {
                ldr = (LinealDescendantRule) r;
            }
        } if (ldr != null) {
            if (flag) populateDialoguePanel("Added the "+ldr.getRuleText()+" Rule.\n");
            else populateDialoguePanel("The lineal descendant rule is not valid in the kin term map.\n");
        }
    }
    
    void postIsomorphicText(boolean flag) {
        if (flag) populateDialoguePanel("The algebra and the kin term map are isomorphic.\n");
        else populateDialoguePanel("The algebra and the kin term map are not isomorphic.\n");
    }
    
    void postIsomorphicAlgebraText(StringVector oldV, StringVector newV) {
         Debug.prout(0,"oldV "+oldV+" newv "+newV);
        oldV.reset();
        String text = "";
        for (newV.reset();newV.isNext();) {
            String s1 = oldV.getNext();
            String s2 = newV.getNext();
            if (s1.equals(s2)) continue;
            text = text+ s1 + " replaced by " + s2+", ";
        }
        if (text.lastIndexOf(",") != -1) {
            text = text.substring(0,text.lastIndexOf(",")).concat(".");
            if (text.lastIndexOf(",") != -1) {
                String subtext = " and"+text.substring(text.lastIndexOf(",")+1);
                text = text.substring(0,text.lastIndexOf(",")).concat(subtext);
            }
        }
        else text = "no change of symbols.";
        populateDialoguePanel("Isomorphic algebra constructed: "+ text+"\n");
    }
    
    void postMakeSexAlgebraJoinText(){
        populateDialoguePanel("Joined the base algebra and its isomorphic copy into a single algebra "+
                              "in which the generators are now sex marked.");
    }
    
    void postMakeReciprocalAlgebraJoinText(){
        populateDialoguePanel("Joined the base algebra and its isomorphic copy into a single algebra "+
                              "in which the generators now have reciprocals.");
    }
    void postCrossSexEquations() {
        populateDialoguePanel("Added equations for products of male and female elements.");
    }
    
    void postClassificatoryEquations(boolean singleChild,String equateChildSex,String equateSibChildSex) {
        if (singleChild) populateDialoguePanel("Added Child of Male/Female Self = Child of Brother/Sister equations.  ");
        else populateDialoguePanel("'Son'/'Daughter' of: Male Self, Female Self, 'Sister' of Male Self and 'Brother' of Female Self are initially kept distinct.  ");
        if (equateChildSex.equals("N")) populateDialoguePanel("'Son'/'Daughter' of Male/Female Self are equated as 'Child' of Male Self and 'Child' of Female Self.  ");
        else if (equateChildSex.equals("M")) populateDialoguePanel("'Son'/'Daughter' of Male Self are equated and become 'Child' of Male Self, but 'Son'/'Daughter' of Female Self are kept distinct.  ");
        else if (equateChildSex.equals("F")) populateDialoguePanel("'Son'/'Daughter' of Female Self are equated and become 'Child' of Female Self, but 'Son'/'Daughter' of Male Self are kept distinct.  ");
        else populateDialoguePanel("'Son'/'Daughter' of Male/Female Self are not equated and are kept distinct.  ");
        if (equateSibChildSex.equals("N")) populateDialoguePanel("'Son'/'Daughter' of 'Sister'/'Brother' of Male/Female Self are equated and become 'Child' of 'Sister'/'Brother' of Male/Female Self. ");
        else if (equateSibChildSex.equals("M")) populateDialoguePanel("'Son'/'Daughter' of 'Sister' of Male Self are equated and become 'Child' of'Sister' of  Male Self, but 'Son'/'Daughter' of 'Brother' of Female Self are kept distinct.  ");
        else if (equateSibChildSex.equals("F")) populateDialoguePanel("'Son'/'Daughter' of 'Brother' of Female Self are equated and become 'Child' of 'Brother'  of Female Self, but 'Son'/'Daughter' of 'Sister' of Male Self are kept distinct.  ");
        else populateDialoguePanel("'Son'/'Daughter' of Sib of Male/Female Self are not equated and are kept distinct.  ");
        populateDialoguePanel(" ");
    }
    
    void postCompletenessText(StringVector missing) {
        if (missing.size() == 0)
            populateDialoguePanel("The kin term map includes kin term products with each of the generator terms.\n");
        else {
            String text = "";
            for (missing.reset();missing.isNext();){
                if (text.equals(""))
                    text = missing.getNext()+" of " + missing.getNext();
                else
                    text = text+", "+missing.getNext()+" of " + missing.getNext();
            }
            populateDialoguePanel("The kin term map is missing the following kin term products: "+text+".\n");
        }
    }
    
    
    public boolean enterIdentityElement() {
        boolean flag = ka.establishIdentityTerm();
        // System.out.println(" BBBBBBBB ka "+ka.makeAlg.theAlgebra.getGenerators()+ " alg "+theAlgebra.getGenerators());
        if (flag) {
            theAlgebra.getIdentityElement().setArrowType(Bops.IDENTITY);
			if (tk.getEffectiveGenerators().size() > 0) {
				String sex = ((TransferKinInfo) tk.getEffectiveGenerators().elementAt(0)).getSex();
				if (!sex.equals("N"))
					theAlgebra.getIdentityElement().setSex(sex);
			}
        }
        return flag;
    }
    
    public boolean enterBaseGenerators() {
        String arrow=null;
        Debug.prout(4," IN AOPS BASE BBBBBBBBBBBBBBBBBBBBBBBBBB");
        //tk = null;
        //getTk();
        //ka.setKv(tk);
		//System.out.println(" tk.up "+tk.up+" tk.down "+tk.down+" tk.left "+tk.left+" tk.right "+tk.right);
        Debug.prout(4," thegens "+tk.getEffectiveGenerators()+" simple? "+tk.checkSimplicityOfStructure());
        if (tk.checkSimplicityOfStructure()){
           // String sex = ((TransferKinInfo) tk.getEffectiveGenerators().elementAt(0)).getSex();
			//System.out.println(" sex ===================== "+sex);
            if (tk.up == 1) {
                arrow = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getTerm();
                if (theAlgebra.getElement(ka.makeAlg.assignName(arrow)).isGenerator())
                    return false;
                ka.makeAlg.addGenerator(arrow);
				//sex1 = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex();
				//System.out.println(" the sex "+ tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex()+" term "+ tk.getEffectiveGenerators(tk.UP).getSymbol(0));
               // if (!sex.equals("N"))
				String sex = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex();
				theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setSex(sex);
                theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setArrowType(Bops.UP);
                Debug.prout(4,"SEX "+sex+"gen "+theAlgebra.getElement(ka.makeAlg.assignName(arrow))+" ty0pe "+theAlgebra.getElement(ka.makeAlg.assignName(arrow)).getArrowType());
            } 
            if (tk.down == 1) {
                arrow = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getTerm();
                if (theAlgebra.getElement(ka.makeAlg.assignName(arrow)).isGenerator())
                    return false;
                ka.makeAlg.addGenerator(arrow);
				//sex1 = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex();
				//System.out.println(" the sex "+ tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex()+" term "+ tk.getEffectiveGenerators(tk.UP).getSymbol(0));
				// if (!sex.equals("N"))
				String sex = tk.getEffectiveGenerators(tk.DOWN).getSymbol(0).getSex();
				theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setSex(sex);
                theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setArrowType(Bops.UP);
                Debug.prout(4,"SEX "+sex+"gen "+theAlgebra.getElement(ka.makeAlg.assignName(arrow))+" ty0pe "+theAlgebra.getElement(ka.makeAlg.assignName(arrow)).getArrowType());
            } 
            if (tk.right == 1) {
                Debug.prout(0," RIGHT");
				//System.out.println(" the sex "+ tk.getEffectiveGenerators(tk.RIGHT).getSymbol(0).getSex()+" term "+ tk.getEffectiveGenerators(tk.RIGHT).getSymbol(0));
                arrow = tk.getEffectiveGenerators(tk.RIGHT).getSymbol(0).getTerm();
                if (theAlgebra.getElement(ka.makeAlg.assignName(arrow)).isGenerator())
                    return false;
                ka.makeAlg.addGenerator(arrow);
               // if (!sex.equals("N"))
				//sex2 = tk.getEffectiveGenerators(tk.RIGHT).getSymbol(0).getSex();
				String sex = tk.getEffectiveGenerators(tk.RIGHT).getSymbol(0).getSex();
				theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setSex(sex);
                theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setArrowType(Bops.RIGHT);
            } 
			if (tk.left == 1) {
                Debug.prout(0," LEFT");
                arrow = tk.getEffectiveGenerators(tk.LEFT).getSymbol(0).getTerm();
                if (theAlgebra.getElement(ka.makeAlg.assignName(arrow)).isGenerator())
                    return false;
                ka.makeAlg.addGenerator(arrow);
               // if (!sex.equals("N"))
				//System.out.println(" the sex "+ tk.getEffectiveGenerators(tk.LEFT).getSymbol(0).getSex()+" term "+ tk.getEffectiveGenerators(tk.LEFT).getSymbol(0));
				//sex3 = tk.getEffectiveGenerators(tk.LEFT).getSymbol(0).getSex();
				String sex = tk.getEffectiveGenerators(tk.LEFT).getSymbol(0).getSex();
				theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setSex(sex);
                theAlgebra.getElement(ka.makeAlg.assignName(arrow)).setArrowType(Bops.LEFT);
            }
            return true;
        } else {
           /* for(int i=0;i< tk.getEffectiveGenerators(tk.UP).size();i++) {
                arrow = tk.getEffectiveGenerators(tk.UP).getSymbol(i).getTerm();
                if (theAlgebra.getElement(ka.makeAlg.assignName(arrow)).isGenerator())
                    return false;
                String sex = ((TransferKinInfo) tk.getEffectiveGenerators().elementAt(i)).getSex();
                ka.makeAlg.addGenerator(arrow);
                //if (!sex.equals("N"))
                    ka.makeAlg.theAlgebra.getElement(arrow).setSex(sex);
            }*/ //above code superceded by requiring simple map 
            return false;
        }
    }

	public boolean enterSibGenerators() {
        String arrow="sib";
        Debug.prout(4," IN AOPS BASE BBBBBBBBBBBBBBBBBBBBBBBBBB");
        Debug.prout(4," thegens "+tk.getEffectiveGenerators()+" simple? "+tk.checkSimplicityOfStructure());
		Debug.prout(0," LEFT");
		ka.makeAlg.addGenerator(arrow);
		String sex = tk.getEffectiveGenerators(tk.UP).getSymbol(0).getSex();
		AlgebraSymbol sib = theAlgebra.getElement(ka.makeAlg.assignName(arrow));
		sib.setSex(sex);
		sib.setArrowType(Bops.LEFT);
		AlgebraSymbolVector leftSide = new AlgebraSymbolVector(1);
		AlgebraSymbolVector rightSide = new AlgebraSymbolVector(1);
		leftSide.addElement(sib);
		leftSide.addElement(sib);
		rightSide.addElement(sib);
		theAlgebra.setAddReciprocalEquations(theAlgebra.RE_NO);
		theAlgebra.addEquation(leftSide,rightSide);//sib sib = sib
		theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
		AlgebraSymbol pa = theAlgebra.getGenerators(tk.UP,sex);
		AlgebraSymbolVector leftSide1 = new AlgebraSymbolVector(1);
		AlgebraSymbolVector rightSide1 = new AlgebraSymbolVector(1);
		leftSide1.addElement(pa);
		leftSide1.addElement(sib);
		rightSide1.addElement(pa);
		theAlgebra.addEquation(leftSide1,rightSide1);//pa sib = pa
		AlgebraSymbol ch = theAlgebra.getGenerators(tk.DOWN,sex);
		/*if (ch != null){
			AlgebraSymbolVector leftSide2 = new AlgebraSymbolVector(1);
			AlgebraSymbolVector rightSide2 = new AlgebraSymbolVector(1);
			leftSide2.addElement(sib);
			leftSide2.addElement(ch);
			rightSide2.addElement(ch);
			theAlgebra.addEquation(leftSide2,rightSide2);
			theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
			AlgebraSymbolVector leftSide3 = new AlgebraSymbolVector(1);
			AlgebraSymbolVector rightSide3 = new AlgebraSymbolVector(1);
			leftSide3.addElement(ch);
			leftSide3.addElement(pa);
			rightSide3.addElement(sib);
			theAlgebra.addEquation(leftSide3,rightSide3);
			theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
		}*/
		return true;
    }
	
    public boolean testForFocalElement(){
        return (ka != null)&& ka.makeAlg.hasFocalElements();
    }
    
    public boolean testForSpouse(){
        if (execOpcode(TEST_FOR_FOCAL_ELEMENT)) {
            AlgebraSymbolVector av = ka.makeAlg.theAlgebra.getGenerators();
            for(av.reset();av.isNext();) {
                AlgebraSymbol as = av.getNext();
                if (as.getArrowType() == Bops.SPOUSE||
                    as.getArrowType() == Bops.SPOUSER) {
                    return true;
                }
            }
        } else { // no focal terms
            
            return false;
        }
        return false;
    }
    
    public void postAlgCayleyTable() {
        CayleyTable cayleyx = makeCayleyTable();
        Vector column = cayleyx.toColumnNames(CayleyTable.ALGDATA);
        SimpleTableMaker frame = new SimpleTableMaker("The Algebra Cayley Table", cayleyx.toData(column,CayleyTable.ALGDATA),column);
    }
    
    public void postKinCayleyTable() {
        CayleyTable cayleyx = makeCayleyTable();
        Vector column = cayleyx.toColumnNames(CayleyTable.KINDATA);
        SimpleTableMaker frame = new SimpleTableMaker("The Kin Term Cayley Table", cayleyx.toData(column,CayleyTable.KINDATA),column);
    }
    
    public void postCayleyTableVerbose(){
        new TextWindow("Cayley Table","The current Cayley Table",makeCayleyTable().toTableString());
    }
    
    public void postKinTypeProducts(){
        postKinTypeProducts(" ");
    }
    
    public void postKinTypeProducts(String sex){
        boolean flag = false;
        CayleyTable cayleyx = makeCayleyTable();
        if (cayleyx == null) {
			// post info in diagnostics box on control panel
			System.out.println(" NULL Cayley table in postKinTypeProducts in AopsOps");
			return;
		}
        KinTypeMapper ktm = theFrame.getGenealogicalGrid(sex).getKinTypeMapper();
        // if (cayleyx != null) System.out.println("! NULL2 XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        if (ktm == null || !ktm.hasPath()){
            //  ktm = new KinTypeMapper(cayleyx.getProducts(),km);
            ktm = new KinTypeMapper(cayleyx,km);
            // ktm.buildAllPaths();
           // if (cayleyx != null) System.out.println("! NULL XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            flag = ktm.buildAllPaths(cayleyx,sex);
            if (flag) theFrame.getGenealogicalGrid(sex).setKinTypeMapper(ktm);
        }
       // if (flag) {
		if (ktm.hasPath()){
            Vector column = ktm.toColumnNames();
            SimpleTableMaker frame = new SimpleTableMaker("Kin Type Table", ktm.toData(column),column);
        }
    }
    
    
    KinTermMap makeKinTermMap() {  //not used anywhere
        TransferKinInfoVector atk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
        KinTermMap aktm = new KinTermMap((TransferKinInfoVector) atk.clone(true));
        return aktm;
    }
    
    //KinTermMap akm;
    
    CayleyTable makeCayleyTable() {
        TransferKinInfoVector atk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
        AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
        //  Debug.prout(4,"AV"+av);
        CayleyTable x;
        x= theFrame.getLastCayley();
        if (x!= null && Algebra.getCurrent().getSerialNumber() != x.getSerialNumber()){
            x = new CayleyTable(av);
            x.generateProducts();
            theFrame.setLastCayley(x);
        }
        //   Debug.prout(4,"THE CAYLEY TABLE 1"+x.theProducts);
        // x.generateProducts();
        // theFrame.setLastCayley(x);
        // Algebra.pushCurrent(theAlgebra);
        // Debug.prout(4,"THE CAYLEY TABLE "+x.theProducts);
        localVars.putData("CayleyTable",x);
        // theFrame.setLastCayley(x);
        km = new KinTermMap((TransferKinInfoVector) atk.clone(true));//sets algbra to null, so need pushCurrent
            Algebra.pushCurrent(theAlgebra);
            x = (CayleyTable)localVars.getCurrentData("CayleyTable");
            // Debug.prout(4,"THE CAYLEY TABLE3 "+x.theProducts);
            //next 3 lines added 4/03 to handle case where there is no linked algebra
			if (x == null) return null;
            CayleyTable x1 =  km.linkAlgebraWithKinTermMap(x);
            if (x1 != null) return x1;
        else return x;
        //  return km.linkAlgebraWithKinTermMap(x); above 3 lines replace this line
        // return cayleyx;
    }
    
    void postMapNotIsomorphicText(StringVector alg) {
        postNotIsomorphicText(alg,"algebra","kin term map");
    }
    
    void postAlgNotIsomorphicText(StringVector map) {
        postNotIsomorphicText(map,"kin term map", "algebra");
        return;
    }
    
    void postNotIsomorphicText(StringVector map, String text1, String text2) {
        Debug.prout(4,"THE error MAP "+map);
        String s = (String)map.elementAt(0);
        int ndx = 0;
        ndx = s.indexOf("=");
        s = s.substring(ndx+1);
        if (s.substring(0,1).equals("["))
            ndx = s.indexOf("]")+1;
        else
            ndx = s.indexOf(" ");
        String term = s.substring(0,ndx);
        s = s.substring(ndx+1);
        if (s.substring(0,1).equals("["))
            ndx = s.indexOf("]")+1;
        else
            ndx = s.indexOf(" ");
        String gen = s.substring(0,ndx);
        s = s.substring(ndx+1);
        ndx = s.indexOf(":");
        String sex = s.substring(0,1);
        ndx = s.indexOf("=");
        s = s.substring(ndx+1);
        ndx = s.indexOf(":");
        String prod = s.substring(0,ndx);
        if (sex.equals("M")) sex = "Male";
        else if (sex.equals("F")) sex = "Female";
        else sex = "Neutral";
        // postIsomorphicText(false);
        String text = "\nLack of isomorphism may be due to";
        if (((String)map.elementAt(0)).indexOf("Mismatch") == -1) {
            text = text + ": (1) the product "+gen+" of "+ term+" = "+prod+
            " is valid in the "+text1+" but not in the "+ text2+" or (2) the sex "
            + sex + " of the kin term " + prod +".";
        }
        else
            text = text + " the product "+gen+" of "+ term+" = "+prod+
                " in the "+text1+" but "+gen+" of "+ term+" is not "+prod+" in the "+text2+".";
        populateDialoguePanel(text+"\n");
    }
    
	void postAlgMisfitsText(StringVector misfits){
		String text = "";
		for (int i=0;i<misfits.size();i++) {
			text = text + misfits.elementAt(i);
			if (i < misfits.size()-2) text = text+", ";
			else if (i < misfits.size()-1) text = text+" and ";
			else text = text+".";
		}
		if (misfits.size() == 1)
			text = "There is no kin term corresponding to the algebra symbol "+ text;
		else text = "There are no kin terms corresponding to the algebra symbols "+ text;
        populateDialoguePanel(text+"\n");
	}
	
	/** Checks the generation status of each kin term.
       * @param isoV ListVector with two entries: non-matching keys when the map
        * is tested against the algebra and the non-matching keys when the algebra
        * is tested against the map
        * @return hash table with terms as keys and generation status as entry in Integer form
        */
    boolean checkForIsomorphism(ListVector isoV,StringVector misfits) {
        StringVector testMap = (StringVector) isoV.elementAt(0);
        StringVector testAlg = (StringVector) isoV.elementAt(1);
        if ((testMap.size() == 0) && (testAlg.size() == 0) && misfits.size() == 0) return true;
        if (testMap.size() != 0) postAlgNotIsomorphicText(testMap);
        else if (testAlg.size() != 0) postAlgNotIsomorphicText(testAlg);
		if (misfits.size() > 0) postAlgMisfitsText(misfits);
        // Debug.prout(4," testMe "+testMe);
        // Debug.prout(4," testIt "+testIt);
        return false;
    }
    
    public boolean testForIsomorphism(){
        waitCursor();
        boolean flag = true;
        
        // CayleyTable cayleyx = makeCayleyTable();
        AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
        Debug.prout(4," THE GENERATORS= "+av);
        CayleyTable cayleyx = new CayleyTable(av);
        cayleyx.generateProducts();
        Debug.prout(4,"CAYLEY X "+cayleyx +" AV "+av+" KM "+km);
        theFrame.setLastCayley(cayleyx);
        Algebra.pushCurrent(theAlgebra);
		StringVector misfits = new StringVector();
        ListVector isoV = km.testIsomorphism(cayleyx,misfits);
        if (isoV == null) return false;
        flag = checkForIsomorphism(isoV,misfits);
        if (!flag){
            //TransferKinInfoVector tk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
            getTk();
            StringVector missing = tk.checkCompletenessOfStructure();
            if (missing.size() !=0)
                postCompletenessText(missing);
        }
        arrowCursor();
        return flag;
    }
    
    
    public boolean enterSpouseTerm() {
        AlgebraSymbolVector av = ka.makeAlg.theAlgebra.getGenerators();
        boolean flag = true;
        for(av.reset();av.isNext();) {
            AlgebraSymbol as = av.getNext();
            if (as.isSexGenerator()) continue;
            if (!as.getSex().equals("N")) { // e.g. M/F
                flag = false;
            }
        }
        
        String let1 = ka.makeAlg.letters.substring(0,1);
        ka.makeAlg.letters = ka.makeAlg.letters.substring(1);
        String let2 = "";
        if (!flag) {
            let2 = ka.makeAlg.letters.substring(0,1);
            ka.makeAlg.letters = ka.makeAlg.letters.substring(1);
        }
        return ka.makeAlg.theAlgebra.enterSpouseTerm(let1,let2);
    }
    
    
    
    public boolean enterSpouseOfParentEquations(){
        //Equations of form Spouse of Parent = Parent
        AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spousesX = new AlgebraSymbolVector(1);
        GroupGeneratorVector g = new GroupGeneratorVector();
        //ups = ka.getUpArrows();
        ups = theAlgebra.getUpArrows();
        
        spouses = theAlgebra.getSpouseArrows();
        if (ups.size() == 0 || spouses.size()== 0) {//nothing to be done
            Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size());
            return false;
        } else if (ups.size() > 2) {
            Debug.prout(4,"In Aops2.execOpcodex (CROSSPRODUCT_EQUATIONS) - too many up arrows");
            return false;
        }
        //System.out.println("C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size()+" "+ups);
        g.addElement(new GroupGenerator(spouses));//HM=F
        g.addElement(new LinkedGroupGenerator(ups,g.getFirst(),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(LinkedGroupGenerator.N_OPPOSITE));
        //g.addElement(new GroupGenerator(ups));
        g.addElement(new GroupDivider());
        AlgebraSymbolVector asv = (AlgebraSymbolVector) ups.clone();
        g.addElement(new LinkedGroupGenerator(asv,g.getFirst(),LinkedGroupGenerator.SAME).setNOppositeOfN(LinkedGroupGenerator.N_OPPOSITE));
        //g.addElement(new LinkedGroupGenerator(ups,g.getFirst(),LinkedGroupGenerator.SAME).setNOppositeOfN(LinkedGroupGenerator.N_OPPOSITE));
        g.interpret();
        
        //Equations of form Husband of Father = 0 = Wife of Mother
        for (spouses.reset();spouses.isNext();) {
            AlgebraSymbol as = spouses.getNext();
            if (as.getSex() != "N") spousesX.addElement(as);
        }
        if (spousesX.size() != 0) {
            Debug.prout(0,"SousesX "+spousesX);
            //g = new GroupGeneratorVector();
            g.clear();
            g.addElement(new GroupGenerator(spousesX));
            g.addElement(new LinkedGroupGenerator(ups,g.getFirst()));
            g.addElement(new GroupDivider());
            g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
            g.interpret();
        }
        return true;
    }
    
    public boolean enterParentOfChildInLawEquations() {
        // Equations of form Parent of Spouse of Child = 0
        
        GroupGeneratorVector g = new GroupGeneratorVector();
        AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
        AlgebraSymbolVector dps = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        ups = theAlgebra.getUpArrows();
        spouses = theAlgebra.getSpouseArrows();
        dps = theAlgebra.getDownArrows();
        if (ups.size() == 0 || spouses.size()== 0) {//nothing to be done
            Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size());
            return false;
        } else if (ups.size() > 2) {
            Debug.prout(4,"In Aops2.execOpcodex (CROSSPRODUCT_EQUATIONS) - too many up arrows");
            return false;
        }
        
        g.addElement(new GroupGenerator(ups));
        g.addElement(new GroupGenerator(spouses));
        g.addElement(new LinkedGroupGenerator(dps,g.getSymbol(1),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(true));
        g.addElement(new GroupDivider());
        g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
        g.interpret();
        return true;
    }
    
    public boolean enterSibOfChildInLawEquations() {
        // Equations of form Sib of Spouse of Child = 0
        
        GroupGeneratorVector g = new GroupGeneratorVector();
        AlgebraSymbolVector side = new AlgebraSymbolVector(1);
        AlgebraSymbolVector dps = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        side = theAlgebra.getSideArrows();
        spouses = theAlgebra.getSpouseArrows();
        dps = theAlgebra.getDownArrows();
        if (side.size() == 0 || spouses.size()== 0) {//nothing to be done
            Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+side.size());
            return false;
        } else if (side.size() > 4) {
            Debug.prout(4,"In Aops2.execOpcodex (CROSSPRODUCT_EQUATIONS) - too many side arrows");
            return false;
        }
        
        g.addElement(new GroupGenerator(side));
        g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.SAME));
        //g.addElement(new GroupGenerator(spouses));
        g.addElement(new LinkedGroupGenerator(dps,g.getFirst(),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(true));
        g.addElement(new GroupDivider());
        //g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.SAME));
        //g.addElement(new LinkedGroupGenerator(dps,g.getFirst(),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(true));
        g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
        g.interpret();
        
        g.clear();
        g.addElement(new GroupGenerator(side));
        g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(true));
        //g.addElement(new GroupGenerator(spouses));
        g.addElement(new LinkedGroupGenerator(dps,g.getFirst(),LinkedGroupGenerator.SAME));
        g.addElement(new GroupDivider());
        g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
        g.interpret();
        return true;
    }
    
    public boolean enterSibOfSpouseEquations() {
        // Equations of form FT of Spouse = 0;sib spouse = 0 based on B+i=0=Z+I
        ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_NO);
        
        GroupGeneratorVector g = new GroupGeneratorVector();
        AlgebraSymbolVector ft = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        ft = theAlgebra.getFocalElements();
        spouses = theAlgebra.getSpouseArrows();
        if (ft.size() == 0 || spouses.size()== 0) {//nothing to be done
            Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ft.size());
            return false;
        }
        g.addElement(new GroupGenerator(ft));//IH=0, iW=0
        g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.SAME));
        g.addElement(new GroupDivider());
        g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
        g.interpret();
        g.clear();
        AlgebraSymbolVector   left = new AlgebraSymbolVector(1);
        left = theAlgebra.getLeftSideArrows();
        AlgebraSymbolVector right = new AlgebraSymbolVector(1);
        right = theAlgebra.getRightSideArrows();
        if (left.size() != 0 && right.size() != 0) {
            g.addElement(new GroupGenerator(left));//Z+H=0, B+W=0
            g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.OPPOSITE));
            g.addElement(new GroupDivider());
            g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
            g.interpret();
            g.clear();
            g.addElement(new GroupGenerator(right));//Z-H=0, B-W=0
            g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.OPPOSITE));
            g.addElement(new GroupDivider());
            g.addElement(new GroupGenerator(theAlgebra.getElement("0")));
            g.interpret();
        }
        ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
        return true;
    }
    
    
    
    public boolean enterParentOfSpouseEquations() {
        //equations of form parent...parent of spouse = 0 or reflexive
        AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        ups = theAlgebra.getUpArrows();
        spouses = theAlgebra.getSpouseArrows();
        if (ups.size() == 0 || spouses.size()== 0) {//nothing to be done
                                                    //Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size());
            return false;
        } else if (ups.size() > 2) {
            //Debug.prout(4,"In Aops2.execOpcodex (CROSSPRODUCT_EQUATIONS) - too many up arrows");
            return false;
        }
        int len = 0;
		int left = 0;
		int right = 0;
        TransferKinInfoVector tk2, tk1;
        TransferKinInfoVector pa;
        TransferKinInfoVector sp;
        tk1 = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
        pa = tk1.getEffectiveGenerators(UP);
        tk2 = (TransferKinInfoVector) theFrame.firstPanel().getTransferKinInfo();
        sp = tk2.getEffectiveGenerators(SPOUSE);
        //Debug.prout(4,"pa= "+pa+" sp= "+sp);
        //StringVector leftSide = new StringVector(1);
        //StringVector rightSide = new StringVector(1);
        //Debug.prout(4,"gensC "+theAlgebra.getElements());
        int k = -1; int j = -1;
		boolean retFlag = false;
        for (sp.reset();sp.isNext();){
			j++;
            String sname = sp.getNext().getTerm();
            for (pa.reset();pa.isNext();) {
				k++;
                String pname = pa.getNext().getTerm();
                
                //  Debug.prout(4,"sname= "+sname+" pname= "+pname);
                //Debug.prout(4,"gensXX "+theAlgebra.getElements());
                ListVector recurEq = ka.checkReflexivePath(sname,pname,tk2);
				//System.out.println("recureq "+recurEq);
                if (recurEq.size() != 0) {
                    left = ((StringVector) recurEq.elementAt(0)).size();
					right = ((StringVector) recurEq.elementAt(1)).size();
                }
				if (left != 0 && right == 0) {
					StringVector leftSide = new StringVector(1);
					StringVector rightSide = new StringVector(1);
					for (int i=0;i<left-1;i++)
						leftSide.addElement(((AlgebraSymbol)ups.elementAt(k)).getValue());
					leftSide.addElement(((AlgebraSymbol)spouses.elementAt(j)).getValue());
					rightSide.addElement("0");
					theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
					theAlgebra.addEquation(leftSide,rightSide);
					Debug.prout(4,"left= "+leftSide+" right= "+rightSide);
					retFlag = true;
				}		
				else if (left != 0 && right != 0) {
					StringVector leftSide = new StringVector(1);
					StringVector rightSide = new StringVector(1);
					for (int i=0;i<left-1;i++)
						leftSide.addElement(((AlgebraSymbol)ups.elementAt(k)).getValue());
                    
					leftSide.addElement(((AlgebraSymbol)spouses.elementAt(j)).getValue());
					
					for (int i=0;i<right-1;i++)
						rightSide.addElement(((AlgebraSymbol)ups.elementAt(k)).getValue());
                    
					rightSide.addElement(((AlgebraSymbol)spouses.elementAt(j)).getValue());
					theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
					theAlgebra.addEquation(leftSide,rightSide);
					//Debug.prout(4,"left= "+leftSide+" right= "+rightSide);
					retFlag = true;
				}
			}
		}
        return retFlag;
    }
    
    
    public boolean enterSiblingInLawEquations(){
        //Equations of form (Child of Parent) of Spouse = Spouse of (Child of Parent)
        AlgebraSymbolVector dps = new AlgebraSymbolVector(1);
        AlgebraSymbolVector ups = new AlgebraSymbolVector(1);
        AlgebraSymbolVector spouses = new AlgebraSymbolVector(1);
        dps = theAlgebra.getDownArrows();
        ups = theAlgebra.getUpArrows();
        spouses = theAlgebra.getSpouseArrows();
        //	Debug.prout(4,"C: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size()+" Down="+dps.size());
        
        if (spouses.size() > 2) {
            //	Debug.prout(4,"A: Spouses="+spouses.size()+" "+spouses+" "+"Up="+ups.size()+" Down="+dps.size());
            // message - we've been too lazy to deal with three
            return false;
        } else if (spouses.size() == 0 || ups.size() == 0 || dps.size() == 0) {
            //	Debug.prout(4,"B: Spouses="+spouses.size()+" Up="+ups.size()+" Down="+dps.size());
            // message - no spouse terms or no up arrows or no down arrows
            return false;
        }
        ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_NO);
        
        GroupGeneratorVector g = new GroupGeneratorVector();
        g.addElement(new GroupGenerator(dps));
        g.addElement(new GroupGenerator(ups));
        g.addElement(new GroupGenerator(spouses));
        
        g.addElement(new GroupDivider());
        
        g.addElement(new LinkedGroupGenerator(spouses,g.getFirst(),LinkedGroupGenerator.SAME));
        g.addElement(new LinkedGroupGenerator(dps,g.getFirst(),LinkedGroupGenerator.OPPOSITE).setNOppositeOfN(true));
        g.addElement(new GroupGenerator(ups));
        
        g.interpret();
        ka.makeAlg.theAlgebra.setAddReciprocalEquations(theAlgebra.RE_YES);
        return true;
    }
    
    public CayleyTable doProductsAndGraph() {
        CayleyTable cly = doProducts();
		if (cly != null) doGraph();
        return cly;
    }
    
    public CayleyTable doProductsAndGraph(TransferKinInfoVector tinfo) {
        Debug.prout(4," cayley table");
        Debug.prout(0," in do prod");
        CayleyTable cly = doProducts(tinfo);
        Debug.prout(4,"do GRPAH");
        Debug.prout(0," in do graph");
		if (cly != null) doGraph();
        return cly;
    }
    
    public void doGraph()	{
        //   GenealogicalGrid gg = new GenealogicalGrid();
        
        SexMarkedModel3D md = new SexMarkedModel3D();
        //GenealogicalModel3D md1 = new GenealogicalModel3D();
        //  md1.setLabelFlag(true);*/
        md.setLabelFlag(false);
        // gg.populateModel(theFrame.threeD1.reset(md1));
        CayleyTable x; // = new CayleyTable(); //probably redundant
                       //System.out.println(" here 11");
        x = theFrame.getLastCayley();
		if (x == null) return;
       // System.out.println(" here 12"+" x "+theFrame.getLastCayley());
        CalcCoordinates cc = CalcCoordinatesFactory.getCalcCoordinates(x);
        //System.out.println(" here 13");
        cc.calcCoordinates();
        //System.out.println(" here 14");
		theFrame.threeD1.offset(true);
        if (km != null) {
            Debug.prout(0," here 1");
            if (km.mapAlgebraProductsToKinshipTerms(x.getProducts()).size() == 0)
            {};
            Debug.prout(0," here 2");
            x.populateModel(theFrame.threeD1.reset(md),km.algebraKin);
            Debug.prout(0," here 3");
        }
        else {
            x.getProducts();
            x.populateModel(theFrame.threeD1.reset(md));
        }
    }
    
    public CayleyTable doProducts()	{
        return doProducts(tk);
    }
    
    public CayleyTable doProducts(TransferKinInfoVector tinfo)	{
        Debug.prout(4," do products 1");
        AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
		if (Algebra.getCurrent().getFocalElements().size() == av.size()) return null;
        Debug.prout(4," do products 1");
        CayleyTable x = new CayleyTable(av);
        Debug.prout(4," do products 1");
       // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA av "+av+"  do products 3 x "+x.toTableString());
        AlgebraPathVector apv =x.generateProducts();
        TransferKinInfoVector atk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
        Debug.prout(4,"atk "+atk);
        Debug.prout(4,"  do products 4 apv "+apv+" x "+x.toTableString());
        km = new KinTermMap((TransferKinInfoVector) atk.clone(true));
        //Debug.prout(4,"km "+km);
        Debug.prout(4,"  do products 5");
        Algebra.pushCurrent(theAlgebra);
        //km.linkAlgebraWithKinTermMap(x);// MAY NOT BE NEEDED HERE or needs x=
        theFrame.setLastCayley(x);
        theFrame.populateGeneratorPanel(tinfo,km);
        //Debug.prout(4,"THE AKM "+akm);
        Debug.prout(4,"  do products 6");
        theFrame.populateEquationPanel();
        Debug.prout(0,"  do products 7");
        //Debug.prout(4,"new cayley "+apv.toString());
        return x;
    }
    
    public Algebra constructAlgebraJoin(StringVector sv,boolean sameSex) {
        Algebra a = constructIsomorphicAlgebra(sv,sameSex);
        return theAlgebra.makeAlgebraJoin(a);
    }
    
    public Algebra constructIsomorphicAlgebra(StringVector sv, boolean sameSex) {
        // TransferKinInfoVector atk = (TransferKinInfoVector) theFrame.firstPanel().getTransferKinInfo();
        return ka.makeAlg.makeIsomorphicAlgebra(sv,sameSex,theFrame);
    }
	
	
	int [][] opArray = {
		{AUTO_ENTER_GENERATORS,//oparray 0
			AUTO_ENTER_IDENTITY,
			FIND_REFLEXIVE_EQUATIONS,
			FIND_PARENTSIB_EQUATIONS,
			AUTO_ESTABLISH_RECIPROCALS,
			FIND_REFLEXIVE_EQUATIONS},
		{AUTO_TEST_AND_ENTER_SPOUSE,//oparray 1; enter spouse generators
			CROSSPRODUCT_EQUATIONS_FOR_SPOUSE,
			SIBLING_IN_LAW_EQUATIONS_FOR_SPOUSE,
			PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE,
			PARENT_OF_SPOUSE_EQUATIONS_FOR_SPOUSE
			//AUTO_ENTER_SEX_GENERATORS,
			//DO_PRODUCTS_AND_GRAPH
		},
		{AUTO_ENTER_SEX_GENERATORS},//oparray 2
									//SETUP_SEX_RULE,
									//SETUP_COUSIN_RULE,
									//DO_PRODUCTS_AND_GRAPH,
									//TEST_ISOMORPHISM},
		{//AUTO_ENTER_SEX_GENERATORS_ISOMORPHISM,
			MAKE_SEX_ISOMORPHIC_ALGEBRA,//oparray 3
			MAKE_ALGEBRA_JOIN_SEX,
			CROSS_SEX_EQUATIONS},
			//MERGE_ALGEBRAS,
			//SETUP_LINEAL_DESCENDANT_RULE,
			//DO_PRODUCTS_AND_GRAPH,
			//TEST_ISOMORPHISM},
		{SETUP_SEX_RULE,//oparray 4
			SETUP_COUSIN_RULE,
			// DO_PRODUCTS_AND_GRAPH,
			SETUP_LINEAL_DESCENDANT_RULE,
			SETUP_MAKE_EQUIVALENT_RULE,
			SETUP_REWRITE_PRODUCT_RULE,
			SETUP_MB_RECIPROCAL_RULE,
			SETUP_CROW_SKEWING_RULE,
			SETUP_SPOUSE_PRODUCT_RULE,
			SETUP_CYLINDER_RULE},
		{AUTO_ENTER_IDENTITY,//oparray 5
			AUTO_ENTER_GENERATORS,
			//MAKE_ALGEBRA_JOIN_RECIPROCAL,
			//RECIPROCAL_EQUATIONS,
			FIND_REFLEXIVE_EQUATIONS,
			FIND_PARENTSIB_EQUATIONS},
		{MAKE_ALGEBRA_JOIN_RECIPROCAL,//oparray 6
			RECIPROCAL_EQUATIONS,
			FIND_REFLEXIVE_EQUATIONS,
			//FIND_PARENTSIB_EQUATIONS,
			//  AUTO_ESTABLISH_RECIPROCALS,
			//  FIND_REFLEXIVE_EQUATIONS,
			ESTABLISH_RECIPROCAL_EQUATIONS},
		{DESCRIPTIVE_STRUCTURE},//oparray 7
		{CLASSIFICATORY_STRUCTURE},//oparrqy 8

		{TEST_ISOMORPHISM,//oparray 9
			DO_PRODUCTS_AND_GRAPH},
		{AUTO_ENTER_SIB_GENERATORS}//oparray10
		
	};
	
		
    public InstructionSet initInstructions() {
    	// InstructionSet ins = super.initInstructions();
    	InstructionSet ins = new InstructionSet("Algebra Operations");
		
		//ins.create("Pop Algebra Context",POP).m();
		//ins.create("Push Algebra Context",PUSH).m();
		//ins.create("");
		
		ins.create("Construct Graph",DO_PRODUCTS_AND_GRAPH).m();
		ins.create("Test Isomorphism",TEST_ISOMORPHISM).d(AUTO_ENTER_GENERATORS);
		ins.create("Reset",RESET).m();
		ins.create("");
		
		ins.create("Manually Enter Symbols",MANUAL_OPERATIONS).m();
		ins.create("Test for Focal Element",TEST_FOR_FOCAL_ELEMENT).m();
		ins.create("Restart Algebra Construction",RESTART_ALGEBRA).m();
		//ins.create("Test Map Simplicity",CHECK_SIMPLICITY_OF_STRUCTURE).m();
		ins.create("");
		
    	ins.create("Construct Base Algebra",CONSTRUCT_BASE_ALGEBRA);//.e(AUTO_ENTER_IDENTITY);
			ins.create("   Steps: Base Algebra",-1);
			ins.create("Enter Identity",AUTO_ENTER_IDENTITY).e(CONSTRUCT_BASE_ALGEBRA);
			ins.create("Enter Generators",AUTO_ENTER_GENERATORS).e(CONSTRUCT_BASE_ALGEBRA);
			//ins.create("Reciprocal Equations",RECIPROCAL_EQUATIONS).d(AUTO_ENTER_GENERATORS);
			ins.create("Find Reflexive Equations",FIND_REFLEXIVE_EQUATIONS).d(AUTO_ENTER_GENERATORS).e(CONSTRUCT_BASE_ALGEBRA);
			ins.create("Find ParentofSib Equations",FIND_PARENTSIB_EQUATIONS).d(AUTO_ENTER_GENERATORS).e(CONSTRUCT_BASE_ALGEBRA);
			//ins.create("Add Reciprocal Equations",ESTABLISH_RECIPROCAL_EQUATIONS).d(FIND_PARENTSIB_EQUATIONS);
			ins.create("   Steps: Base Algebra",-1);
			ins.create("");
								
			ins.create("Construct Sibling Structure",CONSTRUCT_SIBLING_STRUCTURE).d(CONSTRUCT_BASE_ALGEBRA);
			ins.create("   Steps: Sibling Structure",-1);
				ins.create("Enter Sib Generators",AUTO_ENTER_SIB_GENERATORS).e(CONSTRUCT_SIBLING_STRUCTURE);
			ins.create("   Steps: Sibling Structure",-1);
			ins.create("");

			ins.create("Construct Reciprocal Structure",CONSTRUCT_RECIPROCAL_STRUCTURE).d(CONSTRUCT_BASE_ALGEBRA);
			ins.create("   Steps: Reciprocal Structure",-1);//Add isomorphic  structure
				ins.create("Make Reciprocal Algebra",MAKE_ALGEBRA_JOIN_RECIPROCAL).d(AUTO_ENTER_GENERATORS).e(CONSTRUCT_RECIPROCAL_STRUCTURE);
				ins.create("Add Reciprocal Element Equations",RECIPROCAL_EQUATIONS).d(MAKE_ALGEBRA_JOIN_RECIPROCAL).e(CONSTRUCT_RECIPROCAL_STRUCTURE);
				ins.create("Find Reflexive Equations ",FIND_REFLEXIVE_EQUATIONS).d(RECIPROCAL_EQUATIONS).e(CONSTRUCT_RECIPROCAL_STRUCTURE);
				ins.create("Add Reciprocal Equations",ESTABLISH_RECIPROCAL_EQUATIONS).d(RECIPROCAL_EQUATIONS).e(CONSTRUCT_RECIPROCAL_STRUCTURE);
			ins.create("   Steps: Reciprocal Structure",-1);//end isomorphic reciprocal structure
			ins.create("");
					
			ins.create("Construct Sex Structure",ADD_SEX_STRUCTURE).d(CONSTRUCT_RECIPROCAL_STRUCTURE).d(ESTABLISH_RECIPROCAL_EQUATIONS);
			ins.create("   Steps: Sex Structure",-1);//Add isomorphic sex structure
				ins.create("Sex Mark Generators",AUTO_ENTER_SEX_GENERATORS).e(MAKE_SEX_ISOMORPHIC_ALGEBRA).d(CONSTRUCT_RECIPROCAL_STRUCTURE).e(ADD_SEX_STRUCTURE);
				ins.create("");
					
				ins.create("Isomorphic Sex Algebra",MAKE_SEX_ISOMORPHIC_ALGEBRA).e(AUTO_ENTER_SEX_GENERATORS).d(CONSTRUCT_RECIPROCAL_STRUCTURE).e(ADD_SEX_STRUCTURE);
				ins.create("Algebra Sex Join",MAKE_ALGEBRA_JOIN_SEX).d(MAKE_SEX_ISOMORPHIC_ALGEBRA).e(ADD_SEX_STRUCTURE);
				//ins.creaete("Neutral Sibling Terms",);
				//ins.create("Sex Marked Sibling Terms",);
				ins.create("Add Cross Sex Equations",CROSS_SEX_EQUATIONS).d(MAKE_ALGEBRA_JOIN_SEX).e(ADD_SEX_STRUCTURE);
				//ins.create("Add Classificatory Structure",CLASSIFICATORY_STRUCTURE).d(CROSS_SEX_EQUATIONS);
			ins.create("   Steps: Sex Structure",-1);//end isomorphic sex structure
			ins.create("");
			
			ins.create("Add Equivalences",EQUIVALENCE_STRUCTURE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);
			ins.create("   Steps: Add Equivalences",-1);
				//ins.create("Descriptive Equivalences",DESCRIPTIVE_STRUCTURE).d(CROSS_SEX_EQUATIONS);
				ins.create("Classificatory Equivalences",CLASSIFICATORY_STRUCTURE).d(CROSS_SEX_EQUATIONS).d(ADD_SEX_STRUCTURE);
			ins.create("   Steps: Add Equivalences",-1);
			ins.create("");
			
			ins.create("Construct Affinal Structure",ADD_SPOUSE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS).d(AUTO_ENTER_SEX_GENERATORS);
			ins.create("   Steps: Add Affinal Structure",-1);//.d(AUTO_ENTER_GENERATORS);
				ins.create("Test For Spouse Generator",TEST_FOR_SPOUSE).m().d(AUTO_ENTER_GENERATORS).e(ADD_SPOUSE);
				//ins.create("MANUAL_ENTER_SPOUSE",MANUAL_ENTER_SPOUSE).e(AUTO_ENTER_SPOUSE);
				//ins.create("Test and Enter Spouse Generator",AUTO_TEST_AND_ENTER_SPOUSE).d(AUTO_ENTER_GENERATORS).e(AUTO_ENTER_SPOUSE);
				ins.create("Enter Spouse Generator",AUTO_ENTER_SPOUSE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS).d(AUTO_ENTER_SEX_GENERATORS).e(ADD_SPOUSE); //.e(MANUAL_ENTER_SPOUSE);
				ins.create("Enter Spouse Crossproduct Equations",CROSSPRODUCT_EQUATIONS_FOR_SPOUSE).d(AUTO_ENTER_SPOUSE).e(ADD_SPOUSE);
				ins.create("Sibling-in-law Equations",
						   SIBLING_IN_LAW_EQUATIONS_FOR_SPOUSE).d(CROSSPRODUCT_EQUATIONS_FOR_SPOUSE).e(ADD_SPOUSE);
				ins.create("Parent of Child-in-law Not a Kin Term",
						   PARENT_OF_CHILD_IN_LAW_EQUATIONS_FOR_SPOUSE).d(CROSSPRODUCT_EQUATIONS_FOR_SPOUSE).e(ADD_SPOUSE);
				ins.create("Parent of ... of Parent of Spouse Not a Kin Term",
						   PARENT_OF_SPOUSE_EQUATIONS_FOR_SPOUSE).d(CROSSPRODUCT_EQUATIONS_FOR_SPOUSE).e(ADD_SPOUSE);
			ins.create("   Steps: Add Affinal Structure",-1);
			ins.create("");
			
			ins.create("Apply Rules",ADD_RULES).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS).m();
			ins.create("   Steps: Apply Rules",-1);//Add Rules
			   //ins.create("ENTER_SPOUSE_SEX_RULE",ENTER_SPOUSE_SEX_RULE);
				//ins.create("ENTER_COUSIN_RULE",ENTER_COUSIN_RULE);
				ins.create("AKT Sex Rule",SETUP_SEX_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_SEX_GENERATORS);
				ins.create("Crow Skewing Rule",SETUP_CROW_SKEWING_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_SEX_GENERATORS);
				ins.create("Make Elements Equivalent Rule",SETUP_MAKE_EQUIVALENT_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_SEX_GENERATORS);
				ins.create("Older Younger Sib Rule",SETUP_REWRITE_PRODUCT_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);
				ins.create("Self Reciprocal Rule",SETUP_MB_RECIPROCAL_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_SEX_GENERATORS);
				ins.create("Spouse of Product Rule",SETUP_SPOUSE_PRODUCT_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_SEX_GENERATORS);
				ins.create("Top/Bottom Identity Rule",SETUP_CYLINDER_RULE).d(SETUP_MAKE_EQUIVALENT_RULE);
				ins.create("AKT Cousin Removal Rule",SETUP_COUSIN_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);
				ins.create("Shipibo Lineal Descendant Rule",SETUP_LINEAL_DESCENDANT_RULE).d(ADD_SEX_STRUCTURE).d(CROSS_SEX_EQUATIONS);//.d(AUTO_ENTER_GENERATORS);
																					  //ins.create("Nothing",NOTHING);
			ins.create("   Steps: Apply Rules",-1);//end Rules
			ins.create("");
											
									//etc
			instructions = ins;
			return ins;
    }
	
    boolean neutralGenerators() {
        boolean flag = true;
        AlgebraSymbolVector gen = Algebra.getCurrent().getGenerators();
        for (gen.reset();gen.isNext();) {
            flag = (gen.getNext().getSex().equals("N"));
            if (!flag) return flag;
        }
        return flag;
    }
}

