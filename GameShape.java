
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
* Extends JPanel to be used as a component in a JFrame.
* The Graphics object is painted to the JPanel component.
*
* @author Andrew Ross-Sermons
* @version 1.0
* CS131; Lab #
* Spring 2023
*/

@SuppressWarnings("serial")
public class GameShape extends JPanel{
	//ShapeType used to determine which shape to paint in the paintComponent method.
	public enum ShapeType{
		BOX, RECTANGLE, OVAL, TEXT, BLANK;
	}//end enum ShapeType
	
	//Statues used to determine if the shape should be highlighted.
	public enum Status{
		DEFAULT, SELECTED;
	}//end enum Status
	
	private ShapeType shape;
	private Status shapeStatus;
	private String textString;
	protected Dimension size;
	protected int trueX, trueY, width, height;
	
	/**
	 * Default Constructor
	 */
	public GameShape() {
		shape = ShapeType.BLANK;
		shapeStatus = Status.DEFAULT;
		trueX = 0;
		trueY = 0;
		width = 10;
		height = 10;
		size = new Dimension((width+1), (height+1));
		setSize(size.width, size.height);
		setPreferredSize(size);
		setOpaque(false);//No background for the JFrame.
	}//end default constructor
	
	/**
	 * Preferred constructor for shapes.
	 * @param shape
	 * @param trueX
	 * @param trueY
	 * @param width
	 * @param height
	 */
	public GameShape(ShapeType shape, int trueX, int trueY, int width, int height) {
		this.shape = shape;
		shapeStatus = Status.DEFAULT;
		this.trueX = trueX;
		this.trueY = trueY;
		this.width = width;
		this.height = height;
		size = new Dimension((width+1), (height+1));
		setSize(size.width, size.height);
		setPreferredSize(size);
		setOpaque(false);//No background for the JFrame.
	}//end preferred constructor
	
	/**
	 * Preferred constructor for text.
	 * @param shape
	 * @param trueX
	 * @param trueY
	 * @param textString
	 */
	public GameShape(ShapeType shape, int trueX, int trueY, String textString) {
		this.shape = shape;
		shapeStatus = Status.DEFAULT;
		this.trueX = trueX;
		this.trueY = trueY;
		this.textString = textString;
		width = (textString.length()*16);
		height = 21;
		size = new Dimension((width+1), (height+1));
		setSize(size.width, size.height);
		setPreferredSize(size);
		setOpaque(false);//No background for the JFrame.
	}//end preferred constructor
	
	/**
	 * Paints the required shape/text.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white);
		switch(shape) {
		case BOX: //Box made of 4 rectangles for line thickness.
			g.fillRect(0, 0, width, 2);
			g.fillRect(0, 0, 3, height);
			g.fillRect(0, height-3, width, 3);
			g.fillRect(width-2, 0, 2, height);
			break;
		case RECTANGLE:
			g.fillRect(0, 0, width, height);
			break;
		case OVAL:
			Graphics2D g2d = (Graphics2D) g;
			g2d.fillOval(0, 0, width, height);
			break;
		case TEXT:
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
			g.drawString(textString, 0, height-3);
			break;
		case BLANK:
			break;
		default:
			break;
		}//end switch(shape)
		
		switch(shapeStatus) {
		case DEFAULT:
			break;
		case SELECTED:
			g.setXORMode(Color.black);
			g.fillRect(0, 0, width, height);
			break;
		default:
			break;
		}//end switch(shapeStatus)
	}//end paint

	/**
	 * @return the top border
	 */
	public int getTopBorder() {
		return(trueY);
	}//end getTopBorder
	
	/**
	 * @return the bottom border
	 */
	public int getBottomBorder() {
		return(trueY + height);
	}//end getBottomBorder
	
	/**
	 * @return the left border
	 */
	public int getLeftBorder() {
		return(trueX);
	}//end getLeftBorder
	
	/**
	 * @return the right border
	 */
	public int getRightBorder() {
		return(trueX + width);
	}//end getRightBorder
	
	/**
	 * @return the trueX
	 */
	public int getTrueX() {
		return trueX;
	}//end getTrueX

	/**
	 * @param trueX the trueX to set
	 */
	public void setTrueX(int trueX) {
		this.trueX = trueX;
	}//end setTrueX

	/**
	 * @return the trueY
	 */
	public int getTrueY() {
		return trueY;
	}//end getTrueY

	/**
	 * @param trueY the trueY to set
	 */
	public void setTrueY(int trueY) {
		this.trueY = trueY;
	}//end setTrueY

	/**
	 * @return the textString
	 */
	public String getTextString() {
		return textString;
	}//end getTextString

	/**
	 * @param textString the textString to set
	 */
	public void setTextString(String textString) {
		this.textString = textString;
	}//end setTextString

	/**
	 * @return the shapeStatus
	 */
	public Status getShapeStatus() {
		return shapeStatus;
	}//end getShapeStatus

	/**
	 * @param shapeStatus the shapeStatus to set
	 */
	public void setShapeStatus(Status shapeStatus) {
		this.shapeStatus = shapeStatus;
	}//end setShapeStatus
	
}//end GameShape.java
