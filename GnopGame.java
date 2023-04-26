import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

/**
* Class Description
*
* @author Andrew Ross-Sermons
* @version 1.0
* CS131; Lab #
* Spring 2023
*/

@SuppressWarnings("serial")
public class GnopGame extends JFrame implements Runnable, KeyListener{
	private final int FRAME_WIDTH = 750, FRAME_HEIGHT = 500;
	private final int PADDLE_WIDTH = 10, PADDLE_HEIGHT = 50;
	private Thread animator;
	private int playerOneY, playerTwoY, playerTwoX, threadDelay;
	private boolean paused, unpause, mainMenuOpen, pauseMenuOpen, enterPressed, flipFlop;
	
	private ActiveGame currentGame;
	private GameShape playerOnePaddle, playerTwoPaddle;
	private Ball ball;
	private GUIObject mainMenu, pauseMenu;
	
	public GnopGame() {
		//Instantiate menu GUI's and other fields.
		mainMenu = new GUIObject("GNOP", "play", "exit");
		pauseMenu = new GUIObject("PAUSE", "play", "quit");
		playerOneY = 0;
		playerTwoY = 0;
		playerTwoX = 0;
		threadDelay = 7;
		paused = true;
		unpause = false;
		mainMenuOpen = true;
		pauseMenuOpen = false;
		enterPressed = false;
		flipFlop = true;
		animator = new Thread(this);
		initial();
	}
	
	protected void initial() {
		//Setting JFrame fields.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(Color.black);
		setTitle("Gnop!");
		
		//Implementing KeyListener.
		addKeyListener(this);
	    setFocusable(true);
	    setFocusTraversalKeysEnabled(false);
	    
	    //Add menus GUI's.
	    mainMenu.setVisible(true);
	    add(mainMenu);
	    add(pauseMenu);
		
		//Call these last.
		animator.start();
		pack();
		setVisible(true);
	}//end initial

	@Override
	public void run() {
		while(true) {
			if(!paused) {
				playerOneUpdate();
				playerTwoUpdate();
				ballUpdate();
				currentGame.updateCurrentScore();
			}else if(unpause) {
					threadDelay = 7;
					unpause = false;
					paused = false;
			}else if(pauseMenuOpen) {
				pauseMenuUpdate();
			}else if(mainMenuOpen) {
				mainMenuUpdate();
			}
			
			try {
				Thread.sleep(threadDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void newGame() {
		currentGame = new ActiveGame(FRAME_WIDTH, FRAME_HEIGHT);
		playerOnePaddle = new GameShape(GameShape.ShapeType.RECTANGLE, 15, (FRAME_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT);
		playerTwoPaddle = new GameShape(GameShape.ShapeType.RECTANGLE, 706, (FRAME_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT);
		ball = new Ball(FRAME_WIDTH, FRAME_HEIGHT);
		currentGame.add(playerOnePaddle);
	    currentGame.add(playerTwoPaddle);
	    currentGame.add(ball);
	    playerOnePaddle.setBounds(playerOnePaddle.getTrueX(), playerOnePaddle.getTrueY(), playerOnePaddle.size.width, playerOnePaddle.size.height);
	    playerTwoPaddle.setBounds(playerTwoPaddle.getTrueX(), playerTwoPaddle.getTrueY(), playerTwoPaddle.size.width, playerTwoPaddle.size.height);
	    ball.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
	    ball.setTrueX(getContentPane().getWidth()/2);
		ball.setTrueY(getContentPane().getHeight()/2);
	    add(currentGame);
	}
	
	public void printDebug() {
		System.out.printf("Ball Coords: %d, %d%n", ball.getTrueX(), ball.getTrueY());
		System.out.printf("left: %d   top: %d   bottom: %d%n", ball.getLeftBorder(), ball.getTopBorder(), ball.getBottomBorder());
		System.out.printf("playerOnePaddle Coords: %d, %d%n", playerOnePaddle.getTrueX(), playerOnePaddle.getTrueY());
		System.out.printf("right: %d   top: %d   bottom: %d%n%n", playerOnePaddle.getRightBorder(), playerOnePaddle.getTopBorder(), playerOnePaddle.getBottomBorder());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			playerOneY = -1;
			break;
		case KeyEvent.VK_S:
			playerOneY = 1;
			break;
		case KeyEvent.VK_LEFT:
			playerTwoX = -1;
			break;
		case KeyEvent.VK_RIGHT:
			playerTwoX = 1;
			break;
		case KeyEvent.VK_UP:
			playerTwoY = -1;
			break;
		case KeyEvent.VK_DOWN:
			playerTwoY = 1;
			break;
		case KeyEvent.VK_ESCAPE:
			if(!paused) openPauseMenu();
			break;
		case KeyEvent.VK_ENTER:
			enterPressed = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			playerOneY = 0;
			break;
		case KeyEvent.VK_S:
			playerOneY = 0;
			break;
		case KeyEvent.VK_UP:
			playerTwoY = 0;
			break;
		case KeyEvent.VK_DOWN:
			playerTwoY = 0;
			break;
		case KeyEvent.VK_ENTER:
			enterPressed = false;
			break;
		}
	}
	
	public void pause() {
		if(paused && unpause) {
			threadDelay = 1000;
		}else if(paused) {
			paused = false;
		}else {
			paused = true;
		}
	}//end pause
	
	/**
	 * openMainMenu will open and close the main menu.
	 */
	public void openMainMenu() {
		//Reset player selection & update menu to clear previous selection.
		playerTwoX = 0;
		mainMenuUpdate();
		if(!mainMenuOpen&&!pauseMenuOpen) {
			pause();
			currentGame.setVisible(false);
			mainMenuOpen = true;
			mainMenu.setVisible(true);
		}else if(mainMenuOpen){ 
			mainMenuOpen = false;
			mainMenu.setVisible(false);
			newGame();
			currentGame.setVisible(true);
			unpause = true;
			pause();
		}//end if
	}//end menu
	
	public void openPauseMenu() {
		//Reset player selection & update menu to clear previous selection.
		playerTwoX = 0;
		pauseMenuUpdate();
		if(!pauseMenuOpen&&!mainMenuOpen) {
			pause();
			pauseMenuOpen = true;
			pauseMenu.setVisible(true);
		}else if(pauseMenuOpen) {
			pauseMenuOpen = false;
			pauseMenu.setVisible(false);
			pause();
		}
	}
	
	/**
	 * mainMenuUpdate will change menu item selection every time it is called.
	 */
	public void mainMenuUpdate() {
		if(enterPressed && playerTwoX == -1) openMainMenu();
		else if(enterPressed && playerTwoX == 1) this.dispose();
		else if(playerTwoX == -1) {
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.SELECTED);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}else if(playerTwoX == 1) {
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.SELECTED);
		}else {
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}//end if
		mainMenu.repaint();
	}//end menuUpdate
	
	public void pauseMenuUpdate(){
		if(enterPressed&&playerTwoX == -1) { //Corresponds to 'play'
			unpause = true;
			openPauseMenu();
		}else if(enterPressed&&playerTwoX == 1) { //Corresponds to 'quit'
			openPauseMenu();
			openMainMenu();
		}else if(playerTwoX == -1) {
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.SELECTED);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}else if(playerTwoX == 1) {
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.SELECTED);
		}else {
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}//end if
		pauseMenu.repaint();
	}
	
	public void playerOneUpdate() {
		//If playerOnePaddle's movement would leave the frame's vertical boundaries, stops movement in that direction.
		if((playerOnePaddle.getTopBorder() <= 0 && playerOneY == -1) || (playerOnePaddle.getBottomBorder() >= getContentPane().getHeight()
			&& playerOneY == 1))playerOneY = 0;
		//Updates playerOnePaddle's position according to playerOneY.
		playerOnePaddle.setTrueY(playerOnePaddle.getTrueY()+playerOneY);
		playerOnePaddle.setLocation(playerOnePaddle.getTrueX(), playerOnePaddle.getTrueY());
	}
	
	public void playerTwoUpdate() {
		//If playerTwoPaddle's movement would leave the frame's vertical boundaries, stops movement in that direction.
		if((playerTwoPaddle.getTopBorder() <= 0 && playerTwoY == -1) || (playerTwoPaddle.getBottomBorder() >= getContentPane().getHeight()
			&& playerTwoY == 1))playerTwoY = 0;
		//Updates playerTwoPaddle's position according to playerTwoY.
		playerTwoPaddle.setTrueY(playerTwoPaddle.getTrueY()+playerTwoY);
		playerTwoPaddle.setLocation(playerTwoPaddle.getTrueX(), playerTwoPaddle.getTrueY());
	}
	
	public void ballUpdate() {
		//x-axis movement
		if(ball.getLeftBorder() <= 0) { //If out of bounds on left.
			currentGame.playerTwoScored();
			flipFlop = !flipFlop;
			resetGame();
		}else if(ball.getRightBorder() >= getContentPane().getWidth()) { //If out of bounds on right.
			currentGame.playerOneScored();
			flipFlop = !flipFlop;
			resetGame();
		}else if(ball.getLeftBorder() <= playerOnePaddle.getRightBorder() && //If ball is at same x-coord as playerOnePaddle.
				ball.getBottomBorder() >= playerOnePaddle.getTopBorder() && //If ball is below playerOnePaddle's top border.
				ball.getTopBorder() <= playerOnePaddle.getBottomBorder() && //If ball is above playerOnePaddle's bottom border.
				!flipFlop) {
			ball.reflectX(playerOneY);
			flipFlop = !flipFlop;
		}else if(ball.getRightBorder() >= playerTwoPaddle.getLeftBorder() && //If ball is at same x-coord as playerTwoPaddle.
				ball.getBottomBorder() >= playerTwoPaddle.getTopBorder() && //If ball is below playerTwoPaddle's top border.
				ball.getTopBorder() <= playerTwoPaddle.getBottomBorder() && //If ball is above playerTwoPaddle's bottom border.
				flipFlop) {
			ball.reflectX(playerTwoY);
			flipFlop = !flipFlop;
		}
		
		//y-axis movement
		if(ball.getTopBorder() <= 0 || ball.getBottomBorder() >= getContentPane().getHeight())ball.reflectY();
		
		ball.repaint();
	}//end ballUpdate
	
	public void resetGame() {
		pause();
		playerOnePaddle.setLocation(playerOnePaddle.getTrueX(), (FRAME_HEIGHT/2));
		playerTwoPaddle.setLocation(playerTwoPaddle.getTrueX(), (FRAME_HEIGHT/2));
		playerOnePaddle.setTrueY(FRAME_HEIGHT/2);
		playerTwoPaddle.setTrueY(FRAME_HEIGHT/2);
		ball.setTrueX(getContentPane().getWidth()/2);
		ball.setTrueY(getContentPane().getHeight()/2);
		ball.defaultYSpeed();
		ball.reflectX(0);
		unpause = true;
		pause();
	}
}//end GnopGame.java
