/*import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;*/
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SimpleTableMaker extends ProtoFrame {
    private boolean DEBUG = false;

    public SimpleTableMaker(String title, Object[][] data, String[] columnNames ) {
      //  super();
	   setBounds(ScaleManager.s(15),ScaleManager.s(15),ScaleManager.s(600),ScaleManager.s(600));
        final Table table = new Table(title,data, columnNames);
        // table.setPreferredScrollableViewportSize(new Dimension(500, 70));

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it.
        ScrollPane scrollPane = new ScrollPane(1);
        scrollPane.add(table);

        //Add the scroll pane to this window.
        add(scrollPane, BorderLayout.CENTER);

/*        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
			   dispose();
               // System.exit(0);
            }
        }); */
		
    }

    public SimpleTableMaker(String title, Vector theData,Vector theColumnNames) {
      //  super(title);
	   setBounds(ScaleManager.s(15),ScaleManager.s(15),ScaleManager.s(600),ScaleManager.s(600));
        Vector data = theData;
        Vector columnNames = theColumnNames;
       final Table table = new Table(title, data, columnNames);
	   table.setBounds(ScaleManager.s(15),ScaleManager.s(15),ScaleManager.s(560),ScaleManager.s(560));
      //  TableSorter sorter = new TableSorter(data, columnNames);
      //final JTable table = new JTable(sorter);
       // sorter.addMouseListenerToHeaderInTable(table);
     //   table.setPreferredScrollableViewportSize(new Dimension(500, 400));

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it.
        ScrollPane scrollPane = new ScrollPane(1);
		scrollPane.setSize(ScaleManager.s(585),ScaleManager.s(585));
        scrollPane.add(table);

        //Add the scroll pane to this window.
        add(scrollPane, BorderLayout.CENTER);

/*		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		//}}
		setTitle(title);
		contentText.setText(content);
		titleLabel.setText(description);
		GlobalWindowManager.addWindow(this);
		mainmenuBar.add(GlobalWindowManager.windowsMenu);
		this.setVisible(true);
		this.toFront();*/




       // addWindowListener(new WindowAdapter() {



       //     public void windowClosing(WindowEvent e) {
		//	   dispose();
       //       // System.exit(0);
       //     }


     //   });
       //  GlobalWindowManager.addWindow(this);
        this.pack();
        this.setVisible(true);


		//mainmenuBar.add(GlobalWindowManager.windowsMenu);

    }

    private static void printDebugData(Table table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
      //  javax.swing.table.TableModel model = table.getModel();

       // System.out.println("Value of data: ");
        for (int i=0; i < numRows; i++) {
            System.out.println("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
           //     System.out.println("  " + model.getValueAt(i, j));
            }
            //System.out.println();
        }
        //System.out.println("--------------------------");
    }

/*    public static void main(String[] args) {
        SimpleTableDemo frame = new SimpleTableDemo();
        frame.pack();
        frame.setVisible(true);
    }*/
}
