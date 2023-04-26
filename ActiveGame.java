import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

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
public class ActiveGame extends JPanel{
	private GameShape activeDisplay;
	private String currentScore;
	private int width, playerOneScore, playerTwoScore;
	
	
	public ActiveGame(int width, int height) {
		activeDisplay = new GameShape(GameShape.ShapeType.TEXT, 100, 25, "0    0");
		this.width = width;
		playerOneScore = 0;
		playerTwoScore = 0;
		updateCurrentScore();
		activeDisplay.setBounds(((width/2)-(activeDisplay.getWidth()/2)), activeDisplay.trueY, activeDisplay.width, activeDisplay.height);
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		setBounds(0, 0, width, height);
		setVisible(false);
	    setOpaque(false);
	    setLayout(null);
	    add(activeDisplay);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.gray.brighter());
		for(int i = 0; i < 20; i++) {
			g.drawLine((width/2), (i*25), (width/2), (i*25+12));
		}
	}
	
	public void updateCurrentScore() {
		currentScore = (playerOneScore + "    " + playerTwoScore);
		activeDisplay.setTextString(currentScore);
	}
	
	public String getCurrentScore() {
		return currentScore;
	}

	/**
	 * @return the playerOneScore
	 */
	public int getPlayerOneScore() {
		return playerOneScore;
	}

	/**
	 * @param playerOneScore the playerOneScore to set
	 */
	public void setPlayerOneScore(int playerOneScore) {
		this.playerOneScore = playerOneScore;
		updateCurrentScore();
	}
	
	public void playerOneScored() {
		playerOneScore++;
	}

	/**
	 * @return the playerTwoScore
	 */
	public int getPlayerTwoScore() {
		return playerTwoScore;
	}

	/**
	 * @param playerTwoScore the playerTwoScore to set
	 */
	public void setPlayerTwoScore(int playerTwoScore) {
		this.playerTwoScore = playerTwoScore;
		updateCurrentScore();
	}
	
	public void playerTwoScored() {
		playerTwoScore++;
	}
	
}
