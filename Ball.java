
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

/**
* Class Description
*
* @author aross-sermons
* @version 1.0
* CS131; Lab #
* Spring 2023
*/

@SuppressWarnings("serial")
public class Ball extends JPanel{
	private final int BALL_DIAMETER = 15;
	private Ellipse2D.Double myCircle;
	private Dimension size;
	private double xDir, yDir;
	
	public Ball(int width, int height) {
		super();
		myCircle = new Ellipse2D.Double((width/2), (height/2), BALL_DIAMETER, BALL_DIAMETER);
		size = new Dimension(width, height);
		xDir = 0.75;
		yDir = 0.75;
		setSize(size.width, size.height);
		setPreferredSize(size);
		setOpaque(false);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.white);
		myCircle.x = myCircle.x + xDir;
		myCircle.y = myCircle.y + yDir;
		g2d.fill(myCircle);
	}
	
	public void defaultYSpeed() {
		yDir = 0.75;
	}
	
	public void reflectX(int y) {
		xDir = xDir*-1;
		if(y > 0 && yDir > 0) { //If paddle moves down(positive) and ball moves down(positive)
			yDir = (yDir + Math.random()/2);
		}else if(y > 0 && yDir < 0) { //If paddle moves down(positive) and ball moves up(negative)
			yDir = (yDir + Math.random()/3);
		}else if(y < 0 && yDir > 0){ //If paddle moves up(negative) and ball moves down(positive)
			yDir = (yDir - Math.random()/2);
		}else if(y < 0 && yDir < 0) { //If paddle moves up(negative) and ball moves up(negative)
			yDir = (yDir - Math.random()/3);
		}
		
		if(yDir > 1.75) yDir = 1.75; //If new yDir is too high, set to max.
		else if(yDir > 0 && yDir < 0.5) yDir = 0.5;
		else if(yDir < 0 && yDir > -0.5) yDir = -0.5;
		else if(yDir < -1.75) yDir = -1.75;
		//System.out.println("RelfectX |          | yDir: " + Math.abs(yDir));
	}
	
	public void reflectY() {
		yDir = yDir*-1;
		//System.out.println("         | RelfectY | yDir: " + Math.abs(yDir));
	}
	
	public double getTopBorder() {
		return(myCircle.y);
	}
	
	public double getBottomBorder() {
		return(myCircle.y + BALL_DIAMETER);
	}
	
	public double getLeftBorder() {
		return(myCircle.x);
	}
	
	public double getRightBorder() {
		return(myCircle.x + BALL_DIAMETER);
	}
	
	/**
	 * @return the trueX
	 */
	public double getTrueX() {
		return myCircle.x;
	}

	/**
	 * @param trueX the trueX to set
	 */
	public void setTrueX(double trueX) {
		myCircle.x = trueX;
	}

	/**
	 * @return the trueY
	 */
	public double getTrueY() {
		return myCircle.y;
	}

	/**
	 * @param trueY the trueY to set
	 */
	public void setTrueY(double trueY) {
		myCircle.y = trueY;
	}
}
