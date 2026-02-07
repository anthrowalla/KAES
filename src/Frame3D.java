          /*
	A basic extension of the java.awt.Frame class
 */
/*History
* 10/11 DR added color parameter to populateModel3d
* 10/16 DR changed color assignment method to match color assignment method in Cayley Table
* 10/17 DR rewrote populateModel so that it calls populateModel from CayleyTable
* 10/29 DR change Model3D to SexMarkedModel3D in windowActivated
* in future, may have several Model3D's to accomodate different ways to graph algebras
* 11/1 DR changed model3D1 to be based on SexMarkedModel3D instead of Model3D and changed windowApplication now uses model3D1 rather
* than a new object; added procedure in generator(n) to delete nth dimension when deactivated
* 2/12/01 DR reactivated Algebra alg = fr.getFrameAlgebra(); and Algebra.pushCurrent(alg);
* in: public void populateModel(ThreeD md)
* 2/14 DR Added "to do" code to kinsymbolradioButton_ItemStateChanged and algsymbolradioButton_ItemStateChanged
* added "drawGraph" flag so that graph is not redrawn each time the graph window has an event
* 2/22 DR set radio buttons for algebra/kin symbols to have no effect when cly = null
* 2/23 DR changed populateModel procdure to get the lastPanel rather than firstPanel to get
* the kin term map upon which the algebra is based
* 3/5 DR activated the layer1, ..., layer3 buttons; made them responsible for the graph dimensionality;
* rewrote the gen1, ..., gen3 buttons so that they select the generator arrow to be removed from the graph
* 3/7 DR,MF traced problem to message panel; Note:keep track of memory usage
* 3/10 DR added test for null coordinates to populateModel
*/

import java.awt.*; 
import java.util.*;
import java.awt.print.*;


public class Frame3D extends Frame implements Printable
{
	boolean drawGraph = true;
	PrintManager printManager = new PrintManager(this);
	MakeMenus makeMenus = new MakeMenus();
	
	public Frame3D()
	{
		setLayout(null);
		setVisible(false);
		setSize(ScaleManager.s(650),ScaleManager.s(690));
		matrix3D1 = new Matrix3D();
		model3D1 = new SexMarkedModel3D();
	//	titlelabel = new java.awt.Label("Graph",Label.CENTER);
	//	titlelabel.setBounds(97,6,378,23);
	//	titlelabel.setFont(new Font("SansSerif", Font.BOLD, 18));
	//	add(titlelabel);
		panel1 = new java.awt.Panel();
		panel1.setLayout(null);
		panel1.setBounds(ScaleManager.s(11),ScaleManager.s(560),ScaleManager.s(665),ScaleManager.s(70));//was 530 instead of 625
		// panel1.setLocation(5,h-72);
		add(panel1);
		Group1 = new CheckboxGroup();
		algebraradioButton = new java.awt.Checkbox("Algebra map", Group1, true);
		algebraradioButton.setBounds(ScaleManager.s(5),ScaleManager.s(45),ScaleManager.s(110),ScaleManager.s(28));
		panel1.add(algebraradioButton);
		kinradioButton = new java.awt.Checkbox("Kin term map", Group1, false);
		kinradioButton.setBounds(ScaleManager.s(5),ScaleManager.s(23),ScaleManager.s(116),ScaleManager.s(28));
		panel1.add(kinradioButton);
		genegridradioButton = new java.awt.Checkbox("Genealogical", Group1, false);
		genegridradioButton.setBounds(ScaleManager.s(5),ScaleManager.s(1),ScaleManager.s(116),ScaleManager.s(28));
		panel1.add(genegridradioButton);
		gen4checkbox = new java.awt.Checkbox("Generator 4");
		gen4checkbox.setBounds(ScaleManager.s(419),ScaleManager.s(5),ScaleManager.s(108),ScaleManager.s(21));
		panel1.add(gen4checkbox);
		gen4checkbox.setState(true);
		gen3checkbox = new java.awt.Checkbox("Generator 3");
		gen3checkbox.setBounds(ScaleManager.s(297),ScaleManager.s(45),ScaleManager.s(108),ScaleManager.s(21));
		panel1.add(gen3checkbox);
		gen3checkbox.setState(true);
		gen2checkbox = new java.awt.Checkbox("Generator 2");
		gen2checkbox.setBounds(ScaleManager.s(297),ScaleManager.s(25),ScaleManager.s(108),ScaleManager.s(21));
		panel1.add(gen2checkbox);
		gen2checkbox.setState(true);
		gen1checkbox = new java.awt.Checkbox("Generator 1");
		gen1checkbox.setBounds(ScaleManager.s(297),ScaleManager.s(5),ScaleManager.s(108),ScaleManager.s(21));
		panel1.add(gen1checkbox);
		gen1checkbox.setState(true);
		layer1checkbox = new java.awt.Checkbox("Layer 1");
		layer1checkbox.setBounds(ScaleManager.s(541),ScaleManager.s(5),ScaleManager.s(82),ScaleManager.s(21));
		panel1.add(layer1checkbox);
		layer1checkbox.setState(true);
		layer2checkbox = new java.awt.Checkbox("Layer 2");
		layer2checkbox.setBounds(ScaleManager.s(541),ScaleManager.s(25),ScaleManager.s(82),ScaleManager.s(21));
		panel1.add(layer2checkbox);
		layer2checkbox.setState(true);
		layer3checkbox = new java.awt.Checkbox("Layer 3");
		layer3checkbox.setBounds(ScaleManager.s(541),ScaleManager.s(45),ScaleManager.s(82),ScaleManager.s(21));
		panel1.add(layer3checkbox);
		layer3checkbox.setState(true);

		Group2 = new CheckboxGroup();
		kinsymbolradioButton = new java.awt.Checkbox("Kin term symbols", Group2, false);
		kinsymbolradioButton.setBounds(ScaleManager.s(136),ScaleManager.s(1),ScaleManager.s(141),ScaleManager.s(28));
		panel1.add(kinsymbolradioButton);
		transliterationradioButton = new java.awt.Checkbox("Transliteration", Group2, true);
		transliterationradioButton.setBounds(ScaleManager.s(136),ScaleManager.s(23),ScaleManager.s(140),ScaleManager.s(28));
		panel1.add(transliterationradioButton);
		algebrasymbolradioButton = new java.awt.Checkbox("Algebra symbols", Group2, true);
		algebrasymbolradioButton.setBounds(ScaleManager.s(136),ScaleManager.s(45),ScaleManager.s(140),ScaleManager.s(28));
		panel1.add(algebrasymbolradioButton);
		threeDPanel = new ThreeD();
		threeDPanel.setLayout(null);
		threeDPanel.setBounds(ScaleManager.s(11),ScaleManager.s(29),ScaleManager.s(550),ScaleManager.s(511));//625 instead of 622
		threeDPanel.setFont(ScaleManager.font("SansSerif", Font.PLAIN, 10));
		add(threeDPanel);
		setTitle("Graph"); 
		//}}

		//{{INIT_MENUS
		mainmenuBar = makeMenus.mainmenuBar; // new java.awt.MenuBar();
		makeMenus.setListeners(new SymAction(), null);
		setMenuBar(mainmenuBar);
		//$$ mainmenuBar.move(0,0);
		//}}

		//{{REGISTER_LISTENERS
		SymWindow2 aSymWindow = new SymWindow2();
		this.addWindowListener(aSymWindow);
		SymItem2 lSymItem = new SymItem2();
		kinradioButton.addItemListener(lSymItem);
		algebraradioButton.addItemListener(lSymItem);
		genegridradioButton.addItemListener(lSymItem);
		kinsymbolradioButton.addItemListener(lSymItem);
		transliterationradioButton.addItemListener(lSymItem);
		algebrasymbolradioButton.addItemListener(lSymItem);
		gen1checkbox.addItemListener(lSymItem);
		gen2checkbox.addItemListener(lSymItem);
		gen3checkbox.addItemListener(lSymItem);
		gen4checkbox.addItemListener(lSymItem);
		layer1checkbox.addItemListener(lSymItem);
		layer2checkbox.addItemListener(lSymItem);
		layer3checkbox.addItemListener(lSymItem);

		mainmenuBar.add(GlobalWindowManager.windowsMenu);
		threeDPanel.init();
		threeDPanel.start();
	//	reshape(getBounds().x,getBounds().y,getBounds().width,getBounds().height);
	//	layout();

	}

	public Frame3D(String title)
	{
		this();
		setTitle(title);
	}

    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			setLocation(50, 50);
		}
		super.setVisible(b);
	}

	public void addNotify()
	{
	    // Record the size of the window prior to calling parents addNotify.
	    Dimension d = getSize();

		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		setSize(insets().left + insets().right + d.width, insets().top + insets().bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets().left, insets().top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;

	//{{DECLARE_CONTROLS
	Matrix3D matrix3D1;
	SexMarkedModel3D model3D1;
	java.awt.Label titlelabel;
	java.awt.Panel panel1;
	java.awt.Checkbox layer3checkbox;
	java.awt.Checkbox algebraradioButton;
	CheckboxGroup Group1;
	java.awt.Checkbox kinradioButton;
	java.awt.Checkbox gen4checkbox;
	java.awt.Checkbox gen3checkbox;
	java.awt.Checkbox gen2checkbox;
	java.awt.Checkbox gen1checkbox;
	java.awt.Checkbox layer1checkbox;
	java.awt.Checkbox layer2checkbox;
	java.awt.Checkbox kinsymbolradioButton;
	CheckboxGroup Group2;
	java.awt.Checkbox algebrasymbolradioButton;
	java.awt.Checkbox transliterationradioButton;
	java.awt.Checkbox genegridradioButton;
	ThreeD threeDPanel;
	//}}

	//{{DECLARE_MENUS
	java.awt.MenuBar mainmenuBar;
	//}}

    //boolean drawGraph = true;
	
	GenealogicalGrid gg=null;
	
	public void resetGG() {
		gg = null;
	}
	
	public void resetGG(String sex) {
		if (gg != null && !sex.equals(gg.getEgoSex()))
			gg = null;
	}
	
	class SymWindow2 extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == Frame3D.this)
				Frame4_WindowClosing(event);
		}

		public void windowActivated(java.awt.event.WindowEvent event) {
         //   SexMarkedModel3D smd = model3D1;
		//	mainmenuBar.add(GlobalWindowManager.windowsMenu);
		//	GlobalWindowManager.setCurrentWindow(KintermFrame.this);
			// mainmenuBar.add(GlobalWindowManager.windowsMenu);
            //if (drawGraph) {

			    if (getCayleyTable() != null) {
				model3D1.setDim1Flag(layer1checkbox.getState()).setDim2Flag(layer2checkbox.getState()).setDim3Flag(layer3checkbox.getState());
				int ngen = getCayleyTable().generators.size();
				for (int i=1;i<=4;i++){
				    if ((i==1)&&(i>=ngen))
					gen1checkbox.setState(false);
				    if ((i==2)&&(i>=ngen))
					gen2checkbox.setState(false);
				    if ((i==3)&&(i>=ngen))
					gen3checkbox.setState(false);
				    if ((i==4)&&(i>=ngen))
					gen4checkbox.setState(false);
				}
				getCayleyTable().setGen1Flag(gen1checkbox.getState());
				getCayleyTable().setGen2Flag(gen2checkbox.getState());
				getCayleyTable().setGen3Flag(gen3checkbox.getState());
				getCayleyTable().setGen4Flag(gen4checkbox.getState());
				getCayleyTable().setMapFlag(kinsymbolradioButton.getState());
		            // md.setLabelFlag(false);
					if (kinradioButton.getState()) {
						setGraphType("Kinterms");
					} else if (algebraradioButton.getState()) {
						setGraphType("Algebra");
					} else if (genegridradioButton.getState()) {
						setGraphType("Genealogy");
					}
							  Debug.prout(4," START GENEALOGY 13");

				    populateModel(threeDPanel.reset(model3D1));
				    drawGraph = false;
			    }

		   // }

			super.windowActivated(event);
			Dimension d = getSize();
			setSize(d.width,d.height);

		//	else if (object == Frame3D.this)
		//		Frame3D_WindowActivated(event);
		}
	}

	void Frame4_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
		drawGraph = true;
	}

	public CayleyTable getCayleyTable() {
    // when associating with a specific kinterm map  change following accordingly
		KintermFrame fr = (KintermFrame) GlobalWindowManager.getCurrentWindow();
		if (fr != null) return fr.getLastCayley();
		else return null;
	}

/*	public CayleyTable doProductsAndGraph(TransferKinInfoVector tinfo)	{
		AlgebraSymbolVector av = Algebra.getCurrent().getGenerators();
		CayleyTable x = new CayleyTable(av);
		x.generateProducts();
		x.calcCoordinates();
		if (km.mapAlgebraProductsToKinshipTerms(x.getProducts()).size() == 0)
		{};
		    x.populateModel(theFrame.threeD1.reset(md),km.algebraKin);
		populateGeneratorPanel(tinfo);
		populateEquationPanel();
		theFrame.setLastCayley(x);
		return x;
	}

*/
	String lastGraph="Algebra";

	public void setGraphType(String gtype) {
		lastGraph = gtype;
	}

	public void populateModel(ThreeD md) {
		  Debug.prout(4," START GENEALOGY 11");
		populateModel(md,lastGraph);
	}

	public void populateModel(ThreeD md, String gtype) {
//		CayleyTable cly = new CayleyTable(av);
//		cly.generateProducts();
		lastGraph = gtype;
	   	CayleyTable cly = getCayleyTable();

		if (cly == null) return;
		if (cly.coordinates == null) {
		   CalcCoordinates cc = CalcCoordinatesFactory.getCalcCoordinates(cly);
		   cc.calcCoordinates();
	   	 //   cly.calcCoordinates();//TODO see if this is correct added 3/8 ... apparently no
		}
// probem 15/5/2
       // KinTypeMapper ktm = new KinTypeMapper(cly.getProducts());
      //  ktm.dumpPaths();
//Debug.prout(4,"CAYLEY TABLE "+cly);
		KintermFrame fr = (KintermFrame) GlobalWindowManager.getCurrentWindow();
Debug.prout(4,"PopulateModel");
		  Debug.prout(4," START GENEALOGY 7");

		TransferKinInfoVector tk = (TransferKinInfoVector) fr.lastPanel().getTransferKinInfo();
Debug.prout(4,"START POPULATE MODELA");
		KinTermMap km = new KinTermMap((TransferKinInfoVector) tk.clone(true));
Debug.prout(4,"START POPULATE MODELB");


	//	KinshipAlgebra ka = km.theKinshipAlgebra;
        Algebra alg = fr.getFrameAlgebra();
        Algebra.pushCurrent(alg);
		Algebra.getCurrent();
		if (!km.associateAlgebraGeneratorsWithKinshipGenerators(cly.getProducts())) {
		};
		if (km.mapAlgebraProductsToKinshipTerms(cly.getProducts()).size() != 0)
		{};
		   // cly.populateModel(fr.threeD1.reset(md),km.algebraKin);
		if (gg == null) gg = fr.getGenealogicalGrid(); // tedious litte something
		if (gtype.equals("Algebra")) {
			cly.populateModel(md,km.algebraKin);
			md.setSize();
		} else if (gtype.equals("Genealogy")) {
			//CODE FOR ACTIVATING THE GENEALOGICAL GRID
			// GenealogicalGrid gg = fr.getGenealogicalGrid();

			//kinsymbolradioButton.setState(true);
			if ((kinsymbolradioButton.getState())) {
				gg.setTermFlag(true);
				gg.setTransFlag(false);
			} else if ((transliterationradioButton.getState())) {
				gg.setTransFlag(true);
				gg.setTermFlag(false);
			} else {
				gg.setTermFlag(false);
				gg.setTransFlag(false);
			}
			GenealogicalModel3D md1 = new GenealogicalModel3D();
		//	KinTypeMapper ktm = new KinTypeMapper(cly.getProducts(),km);
			KinTypeMapper ktm;
			if ((ktm = gg.getKinTypeMapper()) != null && gg.getKinTypeMapper().getSerialNumber() != fr.getFrameAlgebra().getSerialNumber()) {
			   ktm = new KinTypeMapper(cly,km);
			   ktm.buildAllPaths(cly,gg.getEgoSex());
			   gg.setKinTypeMapper(ktm);//added
			} 



//use next four lines if the menu choice should use an existing KinTypeMapper
//Current procedure is to generate a new structure when a menu item is selected
		/*	KinTypeMapper ktm = gg.getKinTypeMapper();
			if (ktm == null) {
				ktm = new KinTypeMapper(cly.getProducts(),km);
				ktm.buildAllPaths();
			}*/
			md1.setLabelFlag(false);
			threeDPanel.scalefudge = 0.20f;
		//	threeDPanel.setOrigin(0.5f,-12.5f,0.5f);
			gg.populateModel(md.reset(md1),ktm);
			Debug.prout(4,"Frame3D.populateModel: DID MODEL ONCE");
			md.setSize();
		//	threeDPanel.getGraphics().translate(-40,100);
		} else if (gtype.equals("Kinterms")) { // not ready yet
			cly.populateModel(md,km.algebraKin);
			md.setSize();
		} else {
			cly.populateModel(md,km.algebraKin);
			md.setSize();
		}
    }

	class SymItem2 implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == kinradioButton)
				kinradioButton_ItemStateChanged(event);
			else if (object == algebraradioButton)
				algebraradioButton_ItemStateChanged(event);
			else if (object == genegridradioButton)
				genegridradioButton_ItemStateChanged(event);
			else if (object == kinsymbolradioButton)
				kinsymbolradioButton_ItemStateChanged(event);
			else if (object == transliterationradioButton)
				transliterationradioButton_ItemStateChanged(event);
			else if (object == algebrasymbolradioButton)
				algebrasymbolradioButton_ItemStateChanged(event);
			else if (object == gen1checkbox)
				gen1checkbox_ItemStateChanged(event);
			else if (object == gen2checkbox)
				gen2checkbox_ItemStateChanged(event);
			else if (object == gen3checkbox)
				gen3checkbox_ItemStateChanged(event);
			else if (object == gen4checkbox)
				gen4checkbox_ItemStateChanged(event);
			else if (object == layer1checkbox)
				layer1checkbox_ItemStateChanged(event);
			else if (object == layer2checkbox)
				layer2checkbox_ItemStateChanged(event);
			else if (object == layer3checkbox)
				layer3checkbox_ItemStateChanged(event);
		}
	}

	void kinradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (kinradioButton.getState()) { // button pressed
			threeDPanel.offset(true);
			CayleyTable cly = getCayleyTable();
			if (cly != null) {
				populateModel(threeDPanel.reset(model3D1),"Kinterms");
			}
		} else { // state changed from true to false because of other button

		}

		//}}
	}

	void genegridradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (genegridradioButton.getState()) { // button pressed
			threeDPanel.offset(false);
		  Debug.prout(4," START GENEALOGY 9");
			CayleyTable cly = getCayleyTable();
			if (cly != null) {
				populateModel(threeDPanel.reset(model3D1), "Genealogy");
			}
		} else { // state changed from true to false because of other button

		}

		//}}
	}

	void algebraradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (algebraradioButton.getState()) { // button pressed
			threeDPanel.offset(true);
			CayleyTable cly = getCayleyTable();
			if (cly != null) {
				populateModel(threeDPanel.reset(model3D1),"Algebra");
			}
		} else { // state changed from true to false because of other button

		}
		//}}
	}

	void kinsymbolradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION

		if (kinsymbolradioButton.getState()) { // button pressed
			CayleyTable cly = getCayleyTable();
					  Debug.prout(4," START GENEALOGY 14");

			if (cly != null) {
	   	        cly.setMapFlag(true);//dr aded
                populateModel(threeDPanel.reset(model3D1));
            }
		} else { // state changed from true to false because of other button

		}
		//}}
	}

	void algebrasymbolradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (algebrasymbolradioButton.getState()) { // button pressed
			//threeDPanel.offset(true);
		    CayleyTable cly = getCayleyTable();
		    if (cly != null){
						  Debug.prout(4," START GENEALOGY 15");

	   	        cly.setMapFlag(false);//dr aded
                populateModel(threeDPanel.reset(model3D1));
            }
		} else { // state changed from true to false because of other button
		}
		//}}
	}

	void transliterationradioButton_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (transliterationradioButton.getState()) { // button pressed
		    CayleyTable cly = getCayleyTable();
		    if (cly != null){
						  Debug.prout(4," START GENEALOGY 15");

	   	        cly.setMapFlag(true);//dr aded
                populateModel(threeDPanel.reset(model3D1));
            }
		} else { // state changed from true to false because of other button
		}
		//}}
	}


	void gen1checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		CayleyTable cly = getCayleyTable();
		if (gen1checkbox.getState()) { // button pressed
		    if (cly != null){
	   	        cly.setGen1Flag(true);
                populateModel(threeDPanel.reset(model3D1));
            }

		} else { // state changed from true to false because of other button
		    if (cly != null){
	   	        cly.setGen1Flag(false);
                populateModel(threeDPanel.reset(model3D1));
            }

		}
		//}}
	}

	void gen2checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		CayleyTable cly = getCayleyTable();
		if (gen2checkbox.getState()) { // button pressed
		    if (cly != null){
	   	        cly.setGen2Flag(true);
                populateModel(threeDPanel.reset(model3D1));
            }

		} else { // state changed from true to false because of other button
		    if (cly != null){
	   	        cly.setGen2Flag(false);
                populateModel(threeDPanel.reset(model3D1));
            }
		}
		//}}
	}

	void gen3checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		CayleyTable cly = getCayleyTable();
		if (gen3checkbox.getState()) { // button pressed
		    if (cly != null){
	   	        cly.setGen3Flag(true);
               populateModel(threeDPanel.reset(model3D1));
            }

		} else { // state changed from true to false because of other button
		    if (cly != null){
	   	        cly.setGen3Flag(false);
                populateModel(threeDPanel.reset(model3D1));
            }
		}
		//}}
	}

	void gen4checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		CayleyTable cly = getCayleyTable();
		if (gen4checkbox.getState()) { // button pressed
		    if (cly != null){
	   	        cly.setGen4Flag(true);
               populateModel(threeDPanel.reset(model3D1));
            }

		} else { // state changed from true to false because of other button
		    if (cly != null){
	   	        cly.setGen4Flag(false);
                populateModel(threeDPanel.reset(model3D1));
            }
		}
		//}}
	}


	void layer1checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (layer1checkbox.getState()) { // button pressed
            model3D1.setDim1Flag(true);
            populateModel(threeDPanel.reset(model3D1));

		} else { // state changed from true to false because of other button
            model3D1.setDim1Flag(false);
            populateModel(threeDPanel.reset(model3D1));
		}
		//}}
	}

	void layer2checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (layer2checkbox.getState()) { // button pressed
            model3D1.setDim2Flag(true);
            populateModel(threeDPanel.reset(model3D1));

		} else { // state changed from true to false because of other button
            model3D1.setDim2Flag(false);
            populateModel(threeDPanel.reset(model3D1));

		}
		//}}
	}

	void layer3checkbox_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.

		//{{CONNECTION
		if (layer3checkbox.getState()) { // button pressed
           model3D1.setDim3Flag(true);
           populateModel(threeDPanel.reset(model3D1));
		} else { // state changed from true to false because of other button
            model3D1.setDim3Flag(false);
            populateModel(threeDPanel.reset(model3D1));
		}
		//}}
	}



	public void reshape(int x, int y, int w, int h) {
		super.reshape(x,y,w,h);
		if (threeDPanel != null) {
			threeDPanel.setBounds(ScaleManager.s(11),ScaleManager.s(29),w-ScaleManager.s(22),h-ScaleManager.s(118));
			Debug.prout(4,"In 3d.setBounds();");
		} else Debug.prout(4,"Not in 3d.setBounds();");
		if (panel1 != null) panel1.setLocation(ScaleManager.s(5),h-ScaleManager.s(72));
	}

	public boolean mouseDown(Event e, int x, int y) {
		ax = x;
		ay = y;
		return true;
    }
	
	int ax=0, ay=0;
	public boolean mouseUp(Event e, int x, int y) {
		ax = x-ax;
		ay = y-ay;
		threeDPanel.advanceXY((float)(ax*0.05), (float) (ay*0.05));
		return true;
	}
	
/*
	void Frame3D_WindowActivated(java.awt.event.WindowEvent event)
	{
		// to do: code goes here.
	}

	class SymContainer extends java.awt.event.ContainerAdapter
	{
		public void componentAdded(java.awt.event.ContainerEvent event)
		{
			Object object = event.getSource();
			if (object == Frame3D.this)
				Frame3D_ComponentAdded(event);
		}
	}

	void Frame3D_ComponentAdded(java.awt.event.ContainerEvent event)
	{
		// to do: code goes here.
	}*/
	
	class SymAction implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
			Object object = event.getSource();
			if (object == makeMenus.openmenuItem1)
			{}
			else if (object == makeMenus.saveasmenuItem)
			{}
			else if (object == makeMenus.savemenuItem)
			{}
			else if (object == makeMenus.newmenuItem)
			{}
			else if (object == makeMenus.quitmenuItem)
			{}
			else if (object == makeMenus.prefsmenuItem)
			{}
			else if (object == makeMenus.savecayleymenuItem)
			{}
			else if (object == makeMenus.printmenuItem)
				printManager.setupPrintJob();
			else if (object == makeMenus.pagesetupmenuItem)
				printManager.pageSetup();
			// 22/11/01 MF adding edit menu items  #e100
			else if (object == makeMenus.menuItem2)
			{} // 	undomenuItem_ActionPerformed(event);
			else if (object == makeMenus.copymenuItem)
			{} // 	copymenuItem_ActionPerformed(event);
			else if (object == makeMenus.cutmenuItem)
			{} //	cutmenuItem_ActionPerformed(event);
			else if (object == makeMenus.pastmenuItem)
			{} // 	pastmenuItem_ActionPerformed(event);
		}
	}
	
	public int print(Graphics g, PageFormat pf, int pageIndex) {
		return printManager.print(g, pf, pageIndex);
	}
	
	
}

