import java.util.*;

public interface KintermEditObject {
	public void setSex(String s);
	public String getSex();

	public void setTheTerm(String t);
	public String getTheTerm();

	public boolean isGenerating();
	public boolean isCovered();
	public void setOrientation(int a);
	public int getOrientation();
	public void setIsCovered(BooleanSwitch b);
	public void setCoveringTerm(String coveringTerm);
	public void setCoveredTerms(StringVector coveredTerms);
	public boolean isFirstTime();
	public void removeTerm(KintermEditObject k);
	public void removeTerm(KintermEditObject k, Product prod);
	public void delete();
	public void deleteGenerator(int index);
	public void redrawOthers();

	public void setGenerating(boolean generating);
	public java.awt.Rectangle getBounds();
	public TransferKinInfo toKinTermInfo();
	public LineObject searchLines(int x, int y);
	public void removeFrom(KintermEditObject k);

	final static int UP = 0;
	final static int DOWN = 1;
	final static int RIGHT = 2;
	final static int LEFT = 3;
	final static int SPOUSE = 4;
	final static int SPOUSER = 5;
	final static int SEXGEN = 6;
	final static int IDENTITY = 7;
	final static int SIDE = 8; //not implemented! Not sure what to do: should be Sibling w/o older/younger

	final static int[] ORIENTATIONS = {UP,DOWN,RIGHT,LEFT,SPOUSE,SPOUSER,SEXGEN,IDENTITY}; 

	final static int NONE = -1;


	public void setEtc(boolean etc);
	public boolean isEtc();
}
