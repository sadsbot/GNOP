import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
* Class Description
*
* @author aross-sermons
* @version 1.0
* CS131; Lab #
* Spring 2023
*/

@SuppressWarnings("serial")
public class GUIObject extends GameShape{
	private String header, opt1, opt2;
	
	public GUIObject() {
		super();
		header = "Head";
		opt1 = "Option 1";
		opt2 = "Option 2";
		newMenu();
	}
	
	public GUIObject(String header, String opt1, String opt2) {
		super(GameShape.ShapeType.BOX, 160, 100, 400, 200);
		this.header = header;
		this.opt1 = opt1;
		this.opt2 = opt2;
		newMenu();
	}
	
	public void newMenu() {
		setLayout(new GridBagLayout());
	    setOpaque(true);
	    setBackground(Color.black);
	    setBounds(trueX, trueY, size.width, size.height);
	    
	  //Declare and instantiate menu objects.
	    GameShape mTitle = new GameShape(GameShape.ShapeType.TEXT, 0, 0, header);
	    GameShape playButton = new GameShape(GameShape.ShapeType.TEXT, 0, 0, opt1);
	    GameShape exitButton = new GameShape(GameShape.ShapeType.TEXT, 0, 0, opt2);
	    //Using new GridBagConstraints to add menu objects.
	    GridBagConstraints c = new GridBagConstraints();
	    c.weightx = 1;
	    c.weighty = 0.35;
	    c.gridx = 0;
	    c.gridy = 1;
	    c.fill = GridBagConstraints.NONE;
	    c.anchor = GridBagConstraints.CENTER;
	    //Space
	    add(new GameShape(), c);
	    //Title
	    c.gridx = 1;
	    c.gridwidth = 2;
	    add(mTitle, c);
	    //Space
	    c.gridx = 3;
	    c.gridwidth = 1;
	    add(new GameShape(), c);
	    //Space
	    c.weighty = 0.9;
	    c.gridx = 0;
	    c.gridy = 2;
	    add(new GameShape(), c);
	    //Play
	    c.gridx = 1;
	    add(playButton, c);
	    //Exit
	    c.gridx = 2;
	    add(exitButton, c);
	    //Space
	    c.gridx = 3;
	    add(new GameShape(), c);
	    //End
	    setVisible(false);
	}

}
