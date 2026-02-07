import java.awt.*;

/*history
* 10/10 DR added dialogue text to operations
* 1/18/01 DR added dependencies to operations
* 01/23/01 DR moved CHECK_SIMPLICITY_OF_STRUCTURE from AopsOps to MopsOps
* 02/12/01 DR added flag=true; to case MERGE_GENERATORS:
* 3/10 DR changed myExecCodes to opArray to allow for a second simplification procedure
* added the procedure getSexComplementTerms to make submap based on complement of a specified sex
* added postSexComplementTerms to dialogue box
* 5/06 DR modified getSexComplementTerms so that it returns terms reachable from the focal term
* removed hardwired "Self" in removeAffines.  Added test for init() to FEMALE_SEX_COMPLEMENT_TERMS
* and to MALE_SEX_COMPLEMENT_TERMS. Modified menu options and menu conditions.
* 6/29 DR modified getSexComplementTerms to reset all terms to the complement sex
* 8/3 DR added CHECK_COMPLETENESS_OF_STRUCTURE to Map Operations menu and postCompletenessText
* 8/22 DR added  if (!inited) init(); to procedure execOpcode(int opcode)
*/

public class MopsOps extends Mops {

	public MopsOps() {
		theFrame = null;
	}

	public MopsOps(KintermFrame a) {
		theFrame=a;
	}

 /*   public boolean exec(int [] opcode){
        return exec(opcode);
    }
   */
//-------------------

/*	int [] myOpcodes = {REMOVE_AFFINES,MERGE_GENERATORS,MERGE_STRUCTURAL_EQUIVALENTS,REMOVE_ONE_TO_MANY,
	                   SEPARATE_BY_SEX};
	int [] myOpcodes = {REMOVE_AFFINES,
	                   SEPARATE_BY_SEX,REMOVE_ONE_TO_MANY};*/

	int [][] opArray = {{REMOVE_AFFINES,
	                        MERGE_STRUCTURAL_SIMILAR,
	                        MERGE_GENERATORS,
	                        REMOVE_ONE_TO_MANY,
	                        CHECK_SIMPLICITY_OF_STRUCTURE},
						{REMOVE_AFFINES,
	                        FEMALE_SEX_COMPLEMENT_TERMS,
	                        REMOVE_ONE_TO_MANY,
	                        CHECK_SIMPLICITY_OF_STRUCTURE},
						{REMOVE_AFFINES,
	                        MALE_SEX_COMPLEMENT_TERMS,
	                        REMOVE_ONE_TO_MANY,
	                        CHECK_SIMPLICITY_OF_STRUCTURE},
						{REMOVE_DESCENDANTS,
							CHECK_SIMPLICITY_OF_STRUCTURE},	
						{REMOVE_ASCENDANTS,
							CHECK_SIMPLICITY_OF_STRUCTURE},
						{REMOVE_RIGHT,
							CHECK_SIMPLICITY_OF_STRUCTURE},	
						{REMOVE_LEFT,
							CHECK_SIMPLICITY_OF_STRUCTURE}
					};


	final static int SIMPLIFY_MAP1 = 0;
	final static int SIMPLIFY_MAP2 = SIMPLIFY_MAP1+1;
	final static int SIMPLIFY_MAP3 = SIMPLIFY_MAP2+1;
	final static int CHECK_SIMPLICITY_OF_STRUCTURE = SIMPLIFY_MAP3+1;
	final static int REMOVE_AFFINES = CHECK_SIMPLICITY_OF_STRUCTURE+1;
	final static int MERGE_GENERATORS = REMOVE_AFFINES+1;
	final static int MERGE_STRUCTURAL_EQUIVALENTS = MERGE_GENERATORS+1;
	final static int MERGE_STRUCTURAL_SIMILAR = MERGE_STRUCTURAL_EQUIVALENTS+1;
	final static int REMOVE_ONE_TO_MANY = MERGE_STRUCTURAL_SIMILAR+1;
	final static int FEMALE_SEX_COMPLEMENT_TERMS = REMOVE_ONE_TO_MANY+1;
	final static int MALE_SEX_COMPLEMENT_TERMS = FEMALE_SEX_COMPLEMENT_TERMS+1;
    final static int CHECK_COMPLETENESS_OF_STRUCTURE = MALE_SEX_COMPLEMENT_TERMS+1;
	final static int REMOVE_DESCENDANTS = CHECK_COMPLETENESS_OF_STRUCTURE+1;
	final static int REMOVE_ASCENDANTS = REMOVE_DESCENDANTS+1;
	final static int SIMPLIFY_REMOVE_DESCENDANTS = REMOVE_ASCENDANTS+1;
	final static int SIMPLIFY_REMOVE_ASCENDANTS = SIMPLIFY_REMOVE_DESCENDANTS+1;
	final static int REMOVE_LEFT = SIMPLIFY_REMOVE_ASCENDANTS+1;
	final static int REMOVE_RIGHT = REMOVE_LEFT+1;
	final static int SIMPLIFY_REMOVE_LEFT = REMOVE_RIGHT+1;
	final static int SIMPLIFY_REMOVE_RIGHT = SIMPLIFY_REMOVE_LEFT+1;

    public InstructionSet initInstructions() {
    	InstructionSet ins = new InstructionSet("Map Operations");
        ins.create("Test Map Simplicity",CHECK_SIMPLICITY_OF_STRUCTURE).m();
        ins.create("Test Map Completeness",CHECK_COMPLETENESS_OF_STRUCTURE).m();
        ins.create("");
    	ins.create("Remove Affines",REMOVE_AFFINES).e(SIMPLIFY_MAP1);
        ins.create("");
     	ins.create("Simplify Structurally Similar",SIMPLIFY_MAP1);
     	ins.create("Simplify Same Sex (Male)",SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
     	ins.create("Simplify Same Sex (Female)",SIMPLIFY_MAP3).e(SIMPLIFY_MAP2);
        ins.create("");
     	ins.create("Remove Ascendants",SIMPLIFY_REMOVE_ASCENDANTS);
     	ins.create("Remove Descendants",SIMPLIFY_REMOVE_DESCENDANTS);
     	//ins.create("Remove Descendants",REMOVE_DESCENDANTS);
     	//ins.create("Simplify Map",myOpcodes);
        ins.create("");
     	ins.create("Remove Right(Younger)",SIMPLIFY_REMOVE_RIGHT);
     	ins.create("Remove Left(Older)",SIMPLIFY_REMOVE_LEFT);
        ins.create("");
        ins.create("Simplify Structurally Similar Operations",-1);
    	ins.create("Remove Affines",REMOVE_AFFINES).e(SIMPLIFY_MAP1);
     	ins.create("Merge Structurally Similar",MERGE_STRUCTURAL_SIMILAR).e(SIMPLIFY_MAP1);
     	ins.create("Merge Structural Equivalents",MERGE_STRUCTURAL_EQUIVALENTS).e(SIMPLIFY_MAP1);
    	ins.create("Merge Generators",MERGE_GENERATORS).e(SIMPLIFY_MAP1);
     	ins.create("Remove One To Many",REMOVE_ONE_TO_MANY).e(SIMPLIFY_MAP1);
        ins.create("Simplify Structural Equivalence Operations",-1);
        ins.create("Simplify Female Sex Operations",-1);
     	ins.create("Remove Affines",REMOVE_AFFINES).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
        ins.create("Terms With Female Sex",FEMALE_SEX_COMPLEMENT_TERMS).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
     	ins.create("Remove One To Many",REMOVE_ONE_TO_MANY).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
        ins.create("Simplify Female Sex Operations",-1);
        ins.create("Simplify Male Sex Operations",-1);
    	ins.create("Remove Affines",REMOVE_AFFINES).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
    	ins.create("Terms With Male Sex",MALE_SEX_COMPLEMENT_TERMS).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
     	ins.create("Remove One To Many",REMOVE_ONE_TO_MANY).e(SIMPLIFY_MAP2).e(SIMPLIFY_MAP3);
        ins.create("Simplify Male Sex Operations",-1);

   	/*    ins.create("Remove Affines",REMOVE_AFFINES);
    	ins.create("Merge Generators",MERGE_GENERATORS);
     	ins.create("Merge Structural Equivalents",MERGE_STRUCTURAL_EQUIVALENTS);
     	ins.create("Remove One To Many",REMOVE_ONE_TO_MANY);     	*/
      	instructions = ins;
      	return ins;
    }

	public boolean execOpcode(int opcode) {
		theFrame.populateGeneratorPanel();

		boolean flag = false;
		if (!inited) init();
		switch (opcode) {
		    case SIMPLIFY_MAP1:
		        //exec(myOpcodes);
		        exec(opArray[0]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
 		    case SIMPLIFY_MAP2:
		        //exec(myOpcodes);
		        exec(opArray[1]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
  		    case SIMPLIFY_MAP3:
		        //exec(myOpcodes);
		        exec(opArray[2]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
  		    case SIMPLIFY_REMOVE_DESCENDANTS:
		        //exec(myOpcodes);
		        exec(opArray[3]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
  		    case SIMPLIFY_REMOVE_ASCENDANTS:
		        //exec(myOpcodes);
		        exec(opArray[4]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
  		    case SIMPLIFY_REMOVE_LEFT:
		        //exec(myOpcodes);
				System.out.println(" 888 remove left ");
		        exec(opArray[5]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
  		    case SIMPLIFY_REMOVE_RIGHT:
				System.out.println(" 999 remove right ");
		        //exec(myOpcodes);
		        exec(opArray[6]);
                theFrame.aopsOps.reset();
                flag = true;
                break;
			case CHECK_SIMPLICITY_OF_STRUCTURE:
 				if (flag = tk.checkSimplicityOfStructure()) {
					//Message.create(Mode.TRACE|Mode.MANUAL,
					Message.create(Mode.ALL,
					"Establised Simplicity. Now ready to enter generators",
						"Try Create Base Algebra option.",
							null,90);
					flag = true;
				} else {
					Message.create(Mode.ALL,
					"Kin term map is complex. Cannot proceed without simplification of kin term map.",
						"Use Map Operations to simplify the kin term map.",
							null,90);
					flag = false;
	            }
	            postSimplicityText(flag);
				    break;
	   case CHECK_COMPLETENESS_OF_STRUCTURE:
/*	   for (tk.reset();tk.isNext();) {
		TransferKinInfo k = tk.getNext();
	   System.out.println(" XXXXHHHHHKKKKKK k "+k+"  "+k.getProducts());
	   }*/

            StringVector missing = tk.checkCompletenessOfStructure();//new StringVector();
            if (missing.size() == 0) {
            Message.create(Mode.TRACE|Mode.MANUAL,
              "Establised Completeness. Now test for simplicity",
              "Simplicity blurb.",
              null,90);
              flag = true;
            } else {
              Message.create(Mode.ALL,
              "Map is not complete.  Add missing products to the kin term map.",
              "Completeness blurb."+postCompletenessText(missing),
              null,90);
              flag = false;
            }
            postCompletenessText(missing);
				    break;
			case REMOVE_AFFINES:
                        System.out.println(" STARTED AFF");
				Message.create(Mode.TRACE|Mode.MANUAL,
				"Trying to simplify: looking for affinal terms.","As a first step to simplify the diagram we are looking for affinal terms that must be removed.",
						null,90);
				if (areAffines()) {
                        System.out.println(" IN IN  AFF");
					removeAffines();
					Message.create(-1,"Result: Affinal terms removed","Affinal terms removed",null,90);
					postAffinalText();
				} else {
					Message.create(Mode.TRACE|Mode.MANUAL,
					"Result: No affinal terms to remove.","No affinal terms to remove. Continuing.",
						null,90);
				}
					flag = true;
					break;
			case REMOVE_LEFT:
				Message.create(Mode.TRACE|Mode.MANUAL,
							   "Trying to simplify: looking for left terms.","As a first step to simplify the diagram we are looking for affinal terms that must be removed.",
							   null,90);
				if (true){
					removeLeft();
					Message.create(-1,"Result: Left terms removed","Left terms removed",null,90);
					postRemoveLeftText();
				} else {
					Message.create(Mode.TRACE|Mode.MANUAL,
								   "Result: No left terms to remove.","No left terms to remove. Continuing.",
								   null,90);
				}
					flag = true;
				break;
				
			case REMOVE_RIGHT:
				Message.create(Mode.TRACE|Mode.MANUAL,
							   "Trying to simplify: looking for right terms.","As a first step to simplify the diagram we are looking for affinal terms that must be removed.",
							   null,90);
				if (true){
					removeRight();
					Message.create(-1,"Result: Right terms removed","Right terms removed",null,90);
					postRemoveRightText();
				} else {
					Message.create(Mode.TRACE|Mode.MANUAL,
								   "Result: No Right terms to remove.","No Right terms to remove. Continuing.",
								   null,90);
				}
					flag = true;
				break;
				
			case REMOVE_DESCENDANTS:
                        //System.out.println(" STARTED DESCENDANTS");
				Message.create(Mode.TRACE|Mode.MANUAL,
				"Trying to simplify: looking for descendant terms.","As a first step to simplify the diagram we are looking for affinal terms that must be removed.",
						null,90);
				if (true){//(areAffines()) {
					removeDescendants();
					Message.create(-1,"Result: Descendant terms removed","Descendant terms removed",null,90);
					postRemoveDescendantsText();
					//enableInstructionInSet("Algebra Operations",AopsProg.CONSTRUCT_BASE_ALGEBRA,false);

				} else {
					Message.create(Mode.TRACE|Mode.MANUAL,
					"Result: No descendant terms to remove.","No descendant terms to remove. Continuing.",
						null,90);
				}
					flag = true;
					break;
			case REMOVE_ASCENDANTS:
                        //System.out.println(" STARTED ASCENDANTS");
				Message.create(Mode.TRACE|Mode.MANUAL,
				"Trying to simplify: looking for ascendant terms.","As a first step to simplify the diagram we are looking for affinal terms that must be removed.",
						null,90);
				if (true){//(areAffines()) {
					removeAscendants();
					Message.create(-1,"Result: Ascendant terms removed","Ascendant terms removed",null,90);
					postRemoveAscendantsText();
					//enableInstructionInSet("Algebra Operations",AopsProg.CONSTRUCT_BASE_ALGEBRA,false);
				} else {
					Message.create(Mode.TRACE|Mode.MANUAL,
					"Result: No ascendant terms to remove.","No ascendant terms to remove. Continuing.",
						null,90);
				}
					flag = true;
					break;

			case MERGE_GENERATORS:
				flag = mergeGenerators();
				postMergeGeneratorsText(flag);
				flag = true;
				break;
			case MERGE_STRUCTURAL_EQUIVALENTS:
				flag = mergeStructuralEquivalents();
				postStructuralEquivalents(flag);
				break;
			case MERGE_STRUCTURAL_SIMILAR:
				flag = mergeStructuralSimilar();
				postStructuralSimilar(flag);
				break;
			case FEMALE_SEX_COMPLEMENT_TERMS:
			    String sex = "F";
			    if (!inited) init();
				flag = getSexComplementTerms(sex);
				postSexComplementTerms(flag,sex);
			    break;
			case MALE_SEX_COMPLEMENT_TERMS:
			    sex = "M";
			    if (!inited) init();
				flag = getSexComplementTerms(sex);
				postSexComplementTerms(flag,sex);
			    break;
			case REMOVE_ONE_TO_MANY:	try {
					if (checkTermsForMultipleProducts(tk)) {
						postMultipleProductsText();
						while (splitComplexTerms());
					 }
					} catch (KintermMapException e) {
						throw e;
					} catch (RuntimeException e) {
						throw e;
					}
					flag = true;
					theFrame.aopsOps.reset();//kludge!!!!! should be integrated

					break;
			default:	flag = false;
			Debug.prout(0,"in maps default");
					stage = -1;
					break;

		}
		return flag;
	}

	void postSimplicityText(boolean flag) {
	    if (flag)
	        populateDialoguePanel("The kin term map has been simplified.\n");
	    else
	        populateDialoguePanel("The kin term map is still complex.\n");
    }

	void postAffinalText() {
	    populateDialoguePanel("Affinal kin terms have been removed from the kin term map.");
	}

	void postRemoveDescendantsText() {
	    populateDialoguePanel("Descendant kin terms have been removed from the kin term map.");
	}

	void postRemoveLeftText() {
	    populateDialoguePanel("Left (older) kin terms have been removed from the kin term map.");
	}
	
	void postRemoveRightText() {
	    populateDialoguePanel("Right (younger) kin terms have been removed from the kin term map.");
	}
	
	void postRemoveAscendantsText() {
	    populateDialoguePanel("Ascendant kin terms have been removed from the kin term map.");
	}

	void postMergeGeneratorsText(boolean flag) {
	    if (flag) populateDialoguePanel("Generators were merged.");
	    else populateDialoguePanel("No generators were merged.");
	}

	void postStructuralEquivalents(boolean flag) {
	    if (flag) populateDialoguePanel("Structurally equivalent kin terms were replaced by a single, covering kin term.");
	    else populateDialoguePanel("No structually equivalent kin terms were found.");
	}

	void postStructuralSimilar(boolean flag) {
	    if (flag) populateDialoguePanel("Structurally similar kin terms were replaced by a single, covering kin term.");
	    else populateDialoguePanel("No structually equivalent kin terms were found.");
	}

	void postSexComplementTerms(boolean flag, String sex) {
		String text = "neutral";
		if (sex.equals("F")) text = "female";
		else if (sex.equals("M")) text = "male";
	    if (flag) populateDialoguePanel("Terms that are marked "+text+" have been removed.");
	    else populateDialoguePanel("No terms have been removed.");
	}


	void postMultipleProductsText() {
	    populateDialoguePanel("Kin terms with multiple products ('one-to-many products') were split into "+
	        "structually equivalent kin terms.");
	}
	String postCompletenessText(StringVector missing) {
            String text = "";
	    if (missing.size() == 0)
	        populateDialoguePanel("The kin term map includes kin term products with each of the generator terms.");
	    else {
                for (missing.reset();missing.isNext();){
                  if (text.equals(""))
                    text = missing.getNext()+" of " + missing.getNext();
                  else
                    text = text+", "+missing.getNext()+" of " + missing.getNext();
                }
	        populateDialoguePanel("The kin term map is missing the following kin term products: "+text+",");
              }
            return text;
        }

/*
	public boolean execOpcodex(int opcode) {
		boolean flag;
		flag = false;
		switch (opcode) {
			case 0:	Message.create(Mode.TRACE|Mode.MANUAL,
			"Trying to simplify: looking for affinal terms.","As a first step to simplify the diagram we are looking for affinal terms which must be removed.",
					null,90);
			flag = true;
					break;
			case 1:	flag = true;
					break;
			case 2:	flag = true;
					break;
			case 3:	flag = true;
					break;
			default:	flag = false;
					stage = -1;
					break;
		}
		return flag;
	}
*/
//-------------------
	public boolean removeAffines() {
		getTk();
		//int len = tk.size();
		int [] arrows = {tk.UP,tk.DOWN,tk.RIGHT,tk.LEFT};
		//System.out.println(" 999 tk.getFocal "+tk.getFocalTerms()+" arrows "+ arrows);
		tk = tk.buildSet(tk.getFocalTerms(),arrows); // all connected by above arrows to focal term ... excludes affines
		makeNewPanel();
		return true;
	}

	public boolean removeLeft() {
		getTk();
		//int len = tk.size();
		int [] arrows = {tk.UP,tk.DOWN,tk.RIGHT,tk.SPOUSE};
		tk = tk.buildSet(tk.getFocalTerms(),arrows); // all connected by non-left arrows to focal term 
		makeNewPanel();
		return true;
	}
	
	public boolean removeRight() {
		getTk();
		//int len = tk.size();
		int [] arrows = {tk.UP,tk.DOWN,tk.LEFT,tk.SPOUSE};
		tk = tk.buildSet(tk.getFocalTerms(),arrows); // all connected by non-right arrows to focal term 
		makeNewPanel();
		return true;
	}
	
	public boolean removeDescendants() {
		getTk();
		//int len = tk.size();
		int [] arrows = {tk.UP,tk.RIGHT,tk.SPOUSE,tk.LEFT,};
		tk = tk.buildSet(tk.getFocalTerms(),arrows); // all connected by above arrows to focal term 
		makeNewPanel();
		return true;
	}
	
	public boolean removeAscendants() {
		getTk();
		int [] arrows = {tk.DOWN,tk.LEFT,tk.RIGHT,tk.SPOUSE};
		tk = tk.buildSet(tk.getFocalTerms(),arrows); // all connected by above arrows to focal term 
		makeNewPanel();
		return true;
	}

	public boolean areAffines() {
		getTk();
                System.out.println(tk.getEffectiveGenerators(tk.SPOUSE));
		if (tk.getEffectiveGenerators(tk.SPOUSE).size() != 0 ||
			tk.getEffectiveGenerators(tk.SPOUSER).size() != 0 ) return true;
		return false;
	}

	public boolean one2many() { // leave for moment .. consider deleting or thinking of reasonable test.
		getTk();
		for(tk.reset();tk.isNextEffectiveTerm();) {
			TransferKinInfo t = tk.getNextEffectiveTerm();
			TransferProductsVector pv = t.getProducts();
			TransferProductsVector xv = null;
	/*		if ((xv = pv.getComplexGenerators()).size() > 0) {
				if (xv.size() > 1)
					throw new KintermMapException("Too many complex generators for complex term ("+t.getTerm()+") prevents further simplification.");
				return true;
			}*/
		}
		return false;
	}

	class term_prod {
		TransferKinInfo term;
		TransferProduct product;
		term_prod(TransferKinInfo t,TransferProduct p) {
			term = t;
			product = p;
		}
	}
	/*  N_up continue
		N_down if N_inwardtoup/N_inwarddown
		N_sides if N_inwardtoup/N_inwarddown


	*/
	class _ProductInfo {
		TransferProduct products;
		StringVector effectiveProducts;
		_ProductInfo(TransferProduct p, StringVector e) {
			products = p;
			effectiveProducts = e;
		}
	}

	class _InArrowInfo {
		int [] arrows;
		StringVector arrowTerms[];

		_InArrowInfo(int [] a, StringVector [] s) {
			arrows = a;
			arrowTerms = s;
		}
	}

	public _InArrowInfo checkTermForIn(TransferKinInfo t) {
		StringVector inverse = tk.getInverse(t.getTerm());
		int arrows[] = {0,0,0,0,0,0};
		StringVector arrowTerms[] = {null,null,null,null,null,null};
//		for (int index =-1;index+1<inverse.size();index++) {
		for (inverse.reset();inverse.isNext();) {
			String other = (String) inverse.getNext();
			int marker;
			String oterm = other.substring(0,(marker = other.indexOf(';')));
			String ogen = other.substring(marker+1);
			TransferKinInfo tx = tk.lookupTerm(oterm);
			if (!tx.getEffectiveTerm().equals(oterm)) continue;
	        if (tk.lookupTerm(ogen) == null) continue;// leave this?? dr 3/10
			int o1 = tk.lookupTerm(ogen).getOrientation();
			arrows[o1]++;
			if (arrowTerms[o1] == null) arrowTerms[o1] = new StringVector();
			arrowTerms[o1].addElement(oterm);
			System.out.println(" oterm "+oterm+ " ogen "+ogen+" 01"+o1);
		}
		return new _InArrowInfo(arrows,arrowTerms);
	}

	class _OutArrowInfo {
		int [] arrows;
		_ProductInfo arrowProducts[];
		_InArrowInfo inInfo;
		int active;
		_OutArrowInfo(int [] a, _ProductInfo [] s, _InArrowInfo in, int act) {
			arrows = a;
			arrowProducts = s;
			inInfo = in;
			active = act;
		}
	}

	public _OutArrowInfo checkTermForOut(TransferKinInfo t) {
		TransferProductsVector pv = t.getProducts();
		TransferProductsVector xv = new TransferProductsVector();
		System.out.println("XXYY pv "+pv+" t "+t);
		boolean flag = false;
		int arrows[] = {0,0,0,0,0,0};
		_ProductInfo arrowProducts[] = {null,null,null,null,null,null};
		for(pv.reset();pv.isNext();) {
			int dir;
			TransferProduct p = pv.getNext();
			if (p.getSize() == 0) continue;
			// need to deal with unassigned kin arrows of each orientation !!!!!!!!
			if (tk.lookupTerm(p.getGenerator()) == null) continue; //leave this?? dr 3/10
			dir = tk.lookupTerm(p.getGenerator()).getOrientation();
			if (p.getSize() > 1) {
				StringVector sv = tk.getEffectiveProducts(t,pv.getSymbol(pv.index).getGenerator());
				//System.out.println(" first p "+p.toString()+" dir "+dir);
				arrows[dir] = sv.size();
				if (sv.size() > 1) {
					arrowProducts[dir] = new _ProductInfo(p,sv); // maybe should store more than single generator product
					flag = true;
				}
			}
		}
		if (!flag) return null;
		_InArrowInfo inInfo = checkTermForIn(t);
		// check to make sure everything has the right number of splits

		int outArrow = 0;
		for (int i=0;i<arrows.length;i++){
			if (arrows[i] > 1){
				if (outArrow == 0) {
					outArrow = i;
				}
				else {
					throw new KintermMapException(40,"Too many kinds of complex arrows ="+outArrow+";"+i);
				}
			}
		}
		if (UP != outArrow){
			if (inInfo.arrows[DOWN] != 0) {
				throw new KintermMapException(50,"Cannot be any incoming Down arrows with orientation "+outArrow);
			}
		} else {
			if (inInfo.arrows[DOWN] != arrows[outArrow]){
					System.out.println(" t INFOOOO "+t);
					System.out.println(" DOWN "+inInfo.arrows[DOWN]+ " OUT "+inInfo.arrows[outArrow]);
				throw new KintermMapException(70,"Imbalance in UP/DOWN arrows");
			}
		}
		return new _OutArrowInfo(arrows,arrowProducts, inInfo, outArrow);
	}


	public static boolean checkTermsForMultipleProducts(TransferKinInfoVector tk) {
		for (tk.reset();tk.isNextEffectiveTerm();) {
			TransferKinInfo t = tk.getNextEffectiveTerm();
			TransferProductsVector pv = t.getProducts();
			for(pv.reset();pv.isNext();) {
				if (pv.getNext().getSize() > 1) {
					if (tk.getEffectiveProducts(t,pv.getSymbol(pv.index).getGenerator()).size() > 1) return true;
				}
			}
		}
		return false;
	}

	TransferProduct checkTermForMultipleProducts(TransferKinInfo t) {
		TransferProduct tret=null;
		_OutArrowInfo x = checkTermForOut(t);
		//System.out.println("got to here");
		if (x != null)
			tret = x.arrowProducts[x.active].products;
		return tret;

	}

	TransferProduct old_checkTermForMultipleProducts(TransferKinInfo t) {
		TransferProduct tret=null;
		TransferProductsVector pv = t.getProducts();
		TransferProductsVector xv = new TransferProductsVector();
		for(pv.reset();pv.isNext();) {
			if (pv.getNext().getSize() > 1) {
				StringVector sv = tk.getEffectiveProducts(t,pv.getSymbol(pv.index).getGenerator());
				if (sv.size() > 1)
					xv.addElement(pv.getSymbol(pv.index));
			}
		}
		if (xv.size() > 0) {
			if (xv.size() > 1) // This is actually possible if all but 1 are reducable covering terms
				throw new KintermMapException("Too many complex products for complex term ("+t.getTerm()+") prevents further simplification.");
			if (t.isGenerator() ) {
				throw new KintermMapException(t.getTerm()+"is a complex term and a generator and this prevents further simplification.");
			}
			return (TransferProduct) xv.firstElement();
		} else return null;
	}

	public void insertTermsIntoProduct(TransferKinInfo tt, TransferProduct k) {
		if (tt.coveredTerms == null) {
			k.addElement(tt.getTerm());
		} else {
		    //for (int index = -1; index < tt.coveredTerms.size();index++) {
			for (tt.coveredTerms.reset();tt.coveredTerms.isNext();) {
				insertTermsIntoProduct(tk.lookupTerm(tt.coveredTerms.getNext()),k);
				//insertTermsIntoProduct(tk.lookupTerm((String)tt.coveredTerms.elementAt(index+1)),k);
				//k.addElement(tt.coveredTerms.getNext());
			}
		}
	}

	public boolean splitTerm(TransferKinInfo term, TransferProduct product) {
		StringVector sv;
		try {
			sv = tk.getEffectiveProducts(term,product.getGenerator());
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			//System.out.println("Term="+term+" generator="+product.getGenerator());
			sv=null;
		}
		if (sv.size() < 2) return false;
		StringVector inverseMapping = tk.getInverse(term.getTerm());

		if (term.coveredTerms != null) { // split terms that cover by removing the cover
			StringVector sx = term.coveredTerms;
			if (sv.size() > sx.size()) {
				throw new KintermMapException(term.getTerm()+"is a complex term with unassignable products and this prevents further simplification.");
			}
			//for (int index=-1; index < sx.size(); index++) {
			for(sx.reset();sx.isNext();) {
				TransferKinInfo t = tk.lookupTerm(sx.getNext());
				//TransferKinInfo t = tk.lookupTerm((String) sx.elementAt(index));
				t.coveringTerm = null;
				TransferProduct p = checkTermForMultipleProducts(t);
				if (p != null) {
					splitTerm(t,p);
				}
			} // end of covering term section
		} else {
			if (term.coveringTerm != null) {// Check to see if we even need or want this test,
											// since shouldn't be effective and thus not really part of map
				throw new KintermMapException(term.getTerm()+"is a complex term and is covered and this prevents subdivision of the term.");
            }
			char[] alpha = {'A','B','C','D','E','F','G','H','I'};
			int ndx = 0;
            //for (int index = -1; index < sv.size(); index++) {
			for(sv.reset();sv.isNext();) {
				TransferKinInfo newTerm = (TransferKinInfo) term.clone(true);

				TransferProduct k = new TransferProduct();
				k.setGenerator(product.getGenerator());
				TransferKinInfo tt = tk.lookupTerm(sv.getNext());
				//TransferKinInfo tt = tk.lookupTerm((String) sv.elementAt(index+1));

				insertTermsIntoProduct(tt,k); // recursively add simple or covered terms into k
				//System.out.println("Searching for effective product "+tt.getTerm()+"= "+k);
				newTerm.getProducts().getGenerator(k.getGenerator()).fIntersection(k);
				//System.out.println("NewTerm products list= "+newTerm.getProducts().getGenerator(k.getGenerator()));
				newTerm.setTerm(newTerm.getTerm()+"-"+alpha[ndx++]); // possible interaction with etc terms
				tk.addElement(newTerm);

				//inverseMapping = tk.getInverse(term.getTerm());

				//for (int iindex=-1; iindex< inverseMapping.size(); iindex++) {
				for(inverseMapping.reset();inverseMapping.isNext();) {
					String other = inverseMapping.getNext();
					//String other = (String)inverseMapping.elementAt(iindex+1);
					int marker;
					String oterm = other.substring(0,(marker = other.indexOf(';')));
					String ogen = other.substring(marker+1);
					int o1 = tk.lookupTerm(ogen).getOrientation();
					int o2 = o1 == tk.UP ? tk.DOWN : o1 == tk.DOWN ? tk.UP :
					o1 == tk.RIGHT ? tk.LEFT : o1 == tk.LEFT ? tk.RIGHT :
					o1 == tk.SPOUSE ? tk.SPOUSER : o1 == tk.SPOUSER ? tk.SPOUSE : -1;
					TransferKinInfoVector tv = tk.getEffectiveGenerators(o2);
					boolean flag = false;
					for(tv.reset();tv.isNext();) {
						String tgen=null;
						StringVector qv;
						try {
							qv = tk.getEffectiveProducts(newTerm,(tgen = tv.getNext().getTerm()));
						} catch (NullPointerException e) {
							e.printStackTrace();
							qv = null;
						}

						if (qv.indexOf(oterm) != -1) {
							if (flag)
								throw new KintermMapException(100,term.getTerm()+
										"Two effective generators pointing from "+term.getTerm()+" to "+oterm);

							TransferProduct subProducts = new TransferProduct();

							subProducts.setGenerator(ogen);

							insertTermsIntoProduct(newTerm,subProducts);
								//System.out.println("new subproducts "+subProducts);
							TransferProduct oldProducts = new TransferProduct(); // maybe move to front


							oldProducts.setGenerator(ogen);
							insertTermsIntoProduct(term,oldProducts);
								//System.out.println("old products "+oldProducts);
							TransferProduct oterms = new TransferProduct();
							oterms.setGenerator(ogen);
							insertTermsIntoProduct(tk.lookupTerm(oterm),oterms);
							if (oterms.indexOf(oterm) == -1) oterms.addElement(oterm);
							//System.out.println("oterms= "+oterms);

							for (oterms.reset();oterms.isNext();){

								TransferProduct oProducts = tk.lookupTerm(oterms.getNext()).getProducts().getGenerator(ogen);
								   //  System.out.println("remove products A "+oProducts);
								oProducts.fRemove(oldProducts);
								   //  System.out.println("oterm "+oterm+" remove products B"+oProducts);
								oProducts.fUnion(subProducts);
								   //  System.out.println("new products "+oProducts);
							}

							//flag = true;
						}
					}
					//inverseMapping.delete();
				}
			}
		}
		String tx = term.getTerm();
		//for (int index = -1; index+1 < inverseMapping.size(); index ++) {
		for(inverseMapping.reset();inverseMapping.isNext();) {
			if (inverseMapping.getNext().startsWith(tx))
			//if (((String) inverseMapping.elementAt(index+1)).startsWith(tx))
				throw new KintermMapException(101,term.getTerm()+
						"oterm+ still points to "+tx+" after decomposition.");
		}
		tk.delete(term);
		tk.buildTables();

	//	System.out.println("did it once");
		return true;
	}

	public boolean splitComplexTerms() {
		getTk();
		ListVector lv = new ListVector();

		for(tk.reset();tk.isNextEffectiveTerm();) {
			TransferKinInfo t = tk.getNextEffectiveTerm();
			TransferProduct p = checkTermForMultipleProducts(t);
			if (p != null) lv.addElement(new term_prod(t,p));

		}
		if (lv.size() == 0) return false;
		int arrows = 0;
		//System.out.println("lv is "+lv);

		System.out.println("complex terms "+lv.toString());
		for (lv.reset();lv.isNext();) { // move part of following to separate routine
			term_prod tp = (term_prod) lv.getNext();
			splitTerm(tp.term,tp.product);
			makeNewPanel(); //makes a panel for every term
		}
		//tk.delete(term);
		//tk.buildTables();
		return true;
	}

	public boolean mergeGenerators() {
		getTk();
		boolean ret = false;
		int [] arrows = {tk.UP,tk.DOWN,tk.RIGHT,tk.LEFT};
		push();
		ListVector v = new ListVector(2);
		for (int i=0;i<arrows.length;i++) {
			TransferKinInfoVector k = tk.getEffectiveGenerators(arrows[i]);
			if (k.size() > 1) {
				TransferKinInfo t1 = (TransferKinInfo) k.elementAt(0);
				TransferKinInfo t2 = (TransferKinInfo) k.elementAt(1);
				if (tk.areGeneratorsEquivalent(t1,t2)) {
				    tk.mergeGenerators(k);
					TransferKinInfo tc = tk.lookupTerm(t1.coveringTerm);//DR added 1/23/03
				    tc.orientation = t1.orientation;//DR added 1/23/03
					tc.isAGenerator = true; //DR added 1/23/03
				//v.addElement(k);//dr deleted 1/23/03
				    ret = true;
				}
			}
		}
		if (!ret) pop();
		else {
	/*		for(v.reset();v.isNext();) {
				TransferKinInfoVector k = (TransferKinInfoVector) v.getNext();
				tk.mergeTerms(k);
			}
			*/
			//tk.mergeTerms(v); //DR commented out 1/23/03
			makeNewPanel();
		}
		return ret;
	}

		public boolean mergeStructuralEquivalents() {
		getTk();
		ListVector lv = tk.findStructuralEquivalents();
		if (lv.size() != 0) {
			push();
			tk.mergeTerms(lv);
			tk.buildTables();
			makeNewPanel();
			return true;
		} else {
			return false;
		}
	}

	public boolean mergeStructuralSimilar() {
		tk =null;
		getTk();

		ListVector lv = tk.findStructuralSimilar();
		if (lv.size() != 0) {
			push();
			tk.mergeTerms(lv,true);
			tk.buildTables();
			makeNewPanel();
			return true;
		} else {
			return false;
		}
	}


	public boolean getSexComplementTerms(String sex) {
		tk=null;
		getTk();
		System.out.println(" start tk "+tk);
			//tk = (TransferKinInfoVector) theFrame.kinshipTermsPanel1.getTransferKinInfo();
		push();
		//TransferKinInfoVector tkv = new TransferKinInfoVector();
		TransferKinInfoVector tkv = (TransferKinInfoVector) tk.clone(true);
        String sex1 = "N";
        if (sex.equals("M")) sex1 = "F";
        else if (sex.equals("F")) sex1 = "M";
	    int [] arrows = {tk.UP,tk.DOWN,tk.RIGHT,tk.LEFT,tk.SPOUSE,tk.SPOUSER};
	//1	TransferKinInfoVector tgen = tk.getEffectiveGenerators();
	//1	String gen = ((TransferKinInfo) tgen.elementAt(0)).term;
//1System.out.println("TK start "+tk+ " gen "+gen);
		//1tk = tk.fProdGen(gen);
//1System.out.println("TK "+tk+ " gen "+gen);
//1 block out
		tkv = tkv.fProdAndTermSexComplement(sex);
System.out.println(" the tk between "+tk);
		tkv = tkv.fDecompose(sex);
System.out.println(" the tk after "+tk);
//1 block out
//System.out.println("MOPS OPS GENS "+tk.getEffectiveGenerators());

//1System.out.println("Focal Terms "+tk.getEffectiveFocalTerms());
//1 block out
		tkv = tkv.buildSet(tkv.getEffectiveFocalTerms(),arrows); // all connected by above arrows to focal terms
        for (tkv.reset();tkv.isNext();){
            tkv.getNext().setSex(sex1);
        }
//1 block out
	        //push();
		tkv.buildTables();
		theFrame.makeNewPanel(tkv);//DR
		tk = null;
		getTk();
		//makeNewPanel(); DR
	//	tk = (TransferKinInfoVector) theFrame.lastPanel().getTransferKinInfo();
     //   tk = theFrame.kinshipTermsPanel1.getTransferKinInfo();
	//	System.out.println("TKlast= "+tk);
	//System.out.println("PANEL SIZE "+theFrame.panels.size());
		return true;
	}
}
