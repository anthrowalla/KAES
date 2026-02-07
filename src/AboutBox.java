//
//	File:		AboutBox.java
//

import java.awt.*;
import java.awt.event.*;

public class AboutBox extends Frame
					  implements ActionListener
{
	protected Button okButton;
	protected Label aboutText;

	public AboutBox()
	{
		super();
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);	
        
		this.setLayout(new BorderLayout(15, 15));
		this.setFont(new Font ("SansSerif", Font.BOLD, 14));

		aboutText = new Label ("About Kaes");
		Panel textPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		textPanel.add(aboutText);
		this.add (textPanel, BorderLayout.NORTH);
		
		okButton = new Button("OK");
		Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		buttonPanel.add (okButton);
		okButton.addActionListener(this);
		this.add(buttonPanel, BorderLayout.SOUTH);
        this.setSize(ScaleManager.s(100),ScaleManager.s(100));
    }
	
    class SymWindow extends java.awt.event.WindowAdapter {
	    public void windowClosing(java.awt.event.WindowEvent event) {
		    setVisible(false);
	    }
    }

	public void actionPerformed(ActionEvent newEvent) 
	{
		setVisible(false);
	}	
	
}
