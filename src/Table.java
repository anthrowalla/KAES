import java.awt.*;
import java.util.*;

public class Table extends Panel {
	int [] colwidths=null;
	String [] colnames=null;
	Object [][] cellData=null;
	int rowCount=0;
	int colCount=0;
	Font plainFont;
	Font boldFont;
	Font bigFont;
	String title="None";
	
	Table(String title, Object [][] data, String[] columnNames) {
		plainFont = ScaleManager.font("SansSerif", Font.PLAIN, 9);
		boldFont = ScaleManager.font("SansSerif", Font.BOLD, 9);
		bigFont = ScaleManager.font("SansSerif", Font.BOLD, 12);
		this.title = title;
		colwidths = new int[columnNames.length];
		colnames = columnNames;
		cellData = data;
		colCount = columnNames.length;
		rowCount = data.length;
		setFont(plainFont);
		setSize(ScaleManager.s(580),ScaleManager.s(580));
	}

	Table(String title, Vector data, Vector columnNames) {
		plainFont = ScaleManager.font("SansSerif", Font.PLAIN, 9);
		boldFont = ScaleManager.font("SansSerif", Font.BOLD, 9);
		bigFont = ScaleManager.font("SansSerif", Font.BOLD, 12);
		this.title = title;
		setData(data,columnNames);
	}

	public Dimension getPreferredSize() {
	   Dimension d = getSize();
	   return d;
	}
	
	public void setData(Vector data, Vector columnNames) {
		if (data == null || columnNames == null || data.size() == 0 || columnNames.size() == 0) {
			data = new Vector();
			Vector q = new Vector();
			q.addElement("no data");
			data.addElement(q);
			columnNames = new StringVector();
			columnNames.addElement("No Data");
		}
		colwidths = new int[columnNames.size()];
		for (int i=0;i<colwidths.length;i++) colwidths[i] = 0;
		colnames = new String[columnNames.size()];
		for (int i=0;i<colnames.length;i++) colnames[i] =  (String) columnNames.elementAt(i);
		for (int i=0;i<colnames.length;i++) colwidths[i] = colnames[i].length()+2;
		cellData = new Object[data.size()][columnNames.size()];
		for (int i=0;i<data.size();i++) {
			Vector k = (Vector) data.elementAt(i);
			for (int j=0;j<colnames.length;j++) {
			   if (k.elementAt(j) == null) cellData[i][j] = "Null";
			   else cellData[i][j] = k.elementAt(j);
				int w = cellData[i][j].toString().length()+2;
				if (w > colwidths[j]) colwidths[j] = w;
			}
		}
		colCount = columnNames.size();
		rowCount = data.size();
		setFont(plainFont);
	}
	
	public void paint(Graphics g) {
		int y = ScaleManager.s(20);
		int lastx = 0;
		int w = 0;
		g.setFont(bigFont);
		g.drawString(title,colwidths[0],y);
		g.setFont(boldFont);
		y+=ScaleManager.s(20);
		for (int i=0;i<colCount;i++){
			lastx = lastx + ScaleManager.s(5) +w;
			w = colwidths[i] * (i == 0 ? ScaleManager.s(6) : ScaleManager.s(5));
			g.drawString(colnames[i],lastx,y);
		//	Debug.prout(4,i+" "+colnames[i]+"   ");
		}
		g.drawRect(ScaleManager.s(1),ScaleManager.s(3),lastx+w,y+ScaleManager.s(5));
	//	g.drawLine(1,3,1,y+5);
	//	g.drawLine(1,y+5,lastx+w,y+5);
	//	g.drawLine(lastx+w,3,lastx+w,y+5);
		y+=ScaleManager.s(20);
		for(int i=0;i<rowCount;i++) {
		//	Debug.prout(4,"");
			lastx = 0;
			y += ScaleManager.s(24);
			w = ScaleManager.s(3);
			g.setFont(boldFont);
			Color c = g.getColor();
			for(int j=0;j<colCount;j++) {
				String contents = (String) cellData[i][j].toString();
				if (cellData[i][j] instanceof ColorTerm)
					g.setColor(((ColorTerm) cellData[i][j]).getTheColor());
				else g.setColor(c);
				lastx = lastx + ScaleManager.s(5)+ w;
				w = colwidths[j]*(j == 0 ? ScaleManager.s(6) : ScaleManager.s(5));
				g.drawString(contents,lastx,y);
				g.setFont(plainFont);
//				Debug.prout(4,i+" "+contents+"   ");
			}
			g.setColor(Color.lightGray);
			g.drawRect(ScaleManager.s(1),y-ScaleManager.s(16),lastx+w,ScaleManager.s(24));
			g.setColor(Color.gray);
			g.drawRect(ScaleManager.s(2),y-ScaleManager.s(17),lastx+w,ScaleManager.s(24));
			g.setColor(Color.black);
			g.drawRect(ScaleManager.s(3),y-ScaleManager.s(18),lastx+w,ScaleManager.s(24));
		}
		setSize(lastx+w,y);
	}
	
	
	int getRowCount() {
		return rowCount;
	}
	
	int getColumnCount() {
		return colCount;
	}
}