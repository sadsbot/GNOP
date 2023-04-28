import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/*******************************************************************************************************************|
* Class Description
* 
* Getter and setter methods aren't needed for fields internal to this class, as this class
* controls all field changes and fields should not be accessed by any other class.
* 
* ******************************************************************************************************************|
* Project Vulnerabilities
* -Height and width of the JPanel can't be changed because some fields for location and size had to be manually set.
* This is because of time restrictions and can be fixed.
* -KeyListener will pause for a brief time if different keys are pressed too quickly. The cause of this is unknown
* but can likely be fixed or KeyListener can be replaced with KeyBindings.
* -Some fields, methods, and classes are redundant or have room for improvement, as is the nature of a college student's
* final project. In specific, GameShape objects should be changed to work for all contexts instead of having a separate
* Ball object. The pause and menu methods can be improved with better logic and fewer assignments/conditions.
* ******************************************************************************************************************|
* @author Andrew Ross-Sermons
* @version 1.0
* CS131; Lab #
* Spring 2023
* ******************************************************************************************************************/

@SuppressWarnings("serial")
public class GnopGame extends JFrame implements Runnable, KeyListener{
	private final int DEFAULT_THREAD_DELAY = 7; //The default update interval, in milliseconds.
	private final int FRAME_WIDTH = 750, FRAME_HEIGHT = 500; //The dimensions of the JPanel.
	private final int PADDLE_WIDTH = 10, PADDLE_HEIGHT = 50; //The dimensions of the paddles.
	private Thread animator;
	private ActiveGame currentGame;
	private GameShape playerOnePaddle, playerTwoPaddle;
	private Ball ball;
	private GUIObject mainMenu, pauseMenu;
	private int playerOneY, playerTwoY, playerTwoX, threadDelay;
	private boolean paused, unpause, mainMenuOpen, pauseMenuOpen, enterPressed, flipFlop;
	
	/**
	 * Default constructor.
	 * This constructor instantiates most objects used in this class.
	 * Then it calls the initial method to finish setting the game up.
	 * This is done to avoid a crowded constructor.
	 */
	public GnopGame() {
		//Instantiate menu GUI's and other fields.
		mainMenu = new GUIObject("GNOP", "play", "exit");
		pauseMenu = new GUIObject("PAUSE", "play", "quit");
		playerOneY = 0; //Determines what direction and how much to move the corresponding object. 
		playerTwoY = 0; //^Positive is down, negative is up (coordinate space).
		playerTwoX = 0; //^Positive is right, negative is left (only for arrow keys).
		threadDelay = 7; //Determines
		paused = true; //See "Menu Comments" section above pause method.
		unpause = false; //^
		mainMenuOpen = true; //^
		pauseMenuOpen = false; //^
		enterPressed = false; //Detects a menu selection.
		flipFlop = true; //See "Paddle Comments" above playerOneUpdate method.
		animator = new Thread(this); //uses the current thread to animate the JPanel according to threadDelay.
		initial(); //Method for setting values in order to keep constructor clean (only instantiation in constructor).
	}//end default constructor
	
	/**
	 * initial will set values for the JFrame, add the KeyListener to the JFrame,
	 * add the menu GUI's, and start the animation.
	 */
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

	/**
	 * run is implemented from the Runnable interface and uses
	 * a thread to run any code repeatedly, with the corresponding
	 * threadDelay between each cycle.
	 */
	@Override
	public void run() {
		while(true) {
			if(!paused) { //Each if statement calls update method(s) for the relevant object(s), except for unpause.
				playerOneUpdate();
				playerTwoUpdate();
				ballUpdate();
				currentGame.updateCurrentScore(); //Updates the score displayed in-game.
			}else if(unpause) { //If unpaused is true, the threadDelay will now be very high.
					threadDelay = DEFAULT_THREAD_DELAY; //After one iteration with the high delay, resets threadDelay.
					unpause = false;
					paused = false; //Final unpausing action.
			}else if(pauseMenuOpen) pauseMenuUpdate();
			else if(mainMenuOpen) mainMenuUpdate(); 
			
			try { //This is where the thread update interval is controlled.
				Thread.sleep(threadDelay);
			}catch (InterruptedException e) { 
				e.printStackTrace();
			}//end try-catch
		}//end while
	}//end run
	
	/**
	 * newGame instantiates new objects, replacing old or unused references, and adds
	 * those objects to a new ActiveGame object. This is where the score is kept as
	 * well as all graphic content related to the game (excluding menus).
	 * The last thing the method does is add the new ActiveGame object to the JFrame.
	 */
	public void newGame() {
		currentGame = new ActiveGame(FRAME_WIDTH, FRAME_HEIGHT); //Replace currentGame
		playerOnePaddle = new GameShape(GameShape.ShapeType.RECTANGLE, 15, (FRAME_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT);
		playerTwoPaddle = new GameShape(GameShape.ShapeType.RECTANGLE, 706, (FRAME_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT);
		ball = new Ball(FRAME_WIDTH, FRAME_HEIGHT);
		//Add new objects.
		currentGame.add(playerOnePaddle);
	    currentGame.add(playerTwoPaddle);
	    currentGame.add(ball);
	    playerOnePaddle.setBounds(playerOnePaddle.getTrueX(), playerOnePaddle.getTrueY(), playerOnePaddle.size.width, playerOnePaddle.size.height);
	    playerTwoPaddle.setBounds(playerTwoPaddle.getTrueX(), playerTwoPaddle.getTrueY(), playerTwoPaddle.size.width, playerTwoPaddle.size.height);
	    ball.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
	    ball.setTrueX(getContentPane().getWidth()/2);
		ball.setTrueY(getContentPane().getHeight()/2);
	    add(currentGame);
	}//end newGame

	/***************************************************************************************************************|
	 * Key Listener Comments
	 * *************************************************************************************************************|
	 * The next three methods implement the KeyListener interface.
	 * The keys to listen for are specified in the KeyEvent class.
	 ***************************************************************************************************************/
	
	/**
	 * Required implementation of KeyTyped, but not used in this program.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * These keys primarily set values related to player movement.
	 */
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
		}//end switch
	}//end keyPressed

	/**
	 * These keys reset values to their default state.
	 */
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
		}//end switch
	}//end keyReleased
	
	/***************************************************************************************************************|
	 * Menu Comments
	 * *************************************************************************************************************|
	 * 5 methods control menus and pausing actions. The state of both menus and the paused status
	 * are represented with boolean values. The logic for each menu is explained in that menu method's
	 * body. The unpause boolean needs better implementation. Currently, it has to be set to true
	 * manually before calling the pause method if the programmer wants to include the unpause delay.
	 * The behavior of the animation according to the pause/menu booleans is controlled by an
	 * if-statement within the run method. When a menu's open boolean is false, the update method for
	 * that menu will not be called, making it essentially inactive.
	 **************************************************************************************************************/
	
	/**
	 * Method with logic statement to pause and unpause the animation.
	 */
	public void pause() {
		if(paused && unpause) { //If game is unpaused, increase threadDelay. The rest is handled in run.
			threadDelay = 1000; //Value determines how long after unpausing before animation resumes, in milliseconds.
			//The rest of the unpause action is carried out in the run method.
		}else if(paused) paused = false; //Unpauses game with no delay. Only used when switching between menus.
		else paused = true; //If the game is no paused, pauses the game.
		//end if
	}//end pause
	
	/**
	 * Method with logic statement to open and close the mainMenu.
	 */
	public void openMainMenu() {
		//Reset player selection & update menu to clear previous selection.
		playerTwoX = 0;
		mainMenuUpdate();
		if(!mainMenuOpen&&!pauseMenuOpen) { //If no menu is open, open mainMenu.
			pause();
			mainMenuOpen = true;
			currentGame.setVisible(false);
			mainMenu.setVisible(true);
		}else if(mainMenuOpen) { //If mainMeny is open, close mainMenu.
			mainMenuOpen = false;
			mainMenu.setVisible(false);
			newGame();
			currentGame.setVisible(true); //ActiveGame objects aren't visible by default.
			unpause = true; //Use the unpause delay.
			pause();
		}//end if
	}//end openMainMenu
	
	/**
	 * Method with logic statement to open and close the mainMenu.
	 */
	public void openPauseMenu() {
		//Reset player selection & update menu to clear previous selection.
		playerTwoX = 0;
		pauseMenuUpdate();
		if(!pauseMenuOpen&&!mainMenuOpen) { //If no menu is open, open pauseMenu.
			pause();
			pauseMenuOpen = true;
			pauseMenu.setVisible(true);
		}else if(pauseMenuOpen) { //If pauseMenu is open, close pauseMenu.
			pauseMenuOpen = false;
			pauseMenu.setVisible(false);
			pause();
		}//end if
	}//end openPauseMenu
	
	/**
	 * mainMenuUpdate will change menu item selection every time it is called
	 * and detect user selection with the enterPressed boolean.
	 * A playerTwoX of -1 corresponds to the left option. 1 corresponds to the right option.
	 * The body of each if-statement changes the shapeStatus value of a menu item to be
	 * selected or deselected.
	 */
	public void mainMenuUpdate() {
		if(enterPressed && playerTwoX == -1) openMainMenu(); //Corresponds to 'play' pressed.
		else if(enterPressed && playerTwoX == 1) this.dispose(); //Corresponds to 'exit' pressed.
		else if(playerTwoX == -1) { //Corresponds to 'play' selected.
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.SELECTED);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}else if(playerTwoX == 1) { //Corresponds to 'exit' selected.
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.SELECTED);
		}else { //If nothing selected.
			((GameShape) mainMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) mainMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}//end if
		mainMenu.repaint(); //Refresh the graphical components of this object. Will not happen automatically.
	}//end menuUpdate
	
	/**
	 * pauseMenuUpdate will change menu item selection every time it is called
	 * and detect user selection with the enterPressed boolean.
	 * A playerTwoX of -1 corresponds to the left option. 1 corresponds to the right option.
	 * The body of each if-statement changes the shapeStatus value of a menu item to be
	 * selected or deselected.
	 */
	public void pauseMenuUpdate(){
		if(enterPressed&&playerTwoX == -1) { //Corresponds to 'play' pressed.
			unpause = true;
			openPauseMenu();
		}else if(enterPressed&&playerTwoX == 1) { //Corresponds to 'quit' pressed.
			openPauseMenu();
			openMainMenu();
		}else if(playerTwoX == -1) { //Corresponds to 'play' selected.
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.SELECTED);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}else if(playerTwoX == 1) { //Corresponds to 'quit' selected.
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.SELECTED);
		}else { //If nothing selected.
			((GameShape) pauseMenu.getComponent(4)).setShapeStatus(GameShape.Status.DEFAULT);
			((GameShape) pauseMenu.getComponent(5)).setShapeStatus(GameShape.Status.DEFAULT);
		}//end if
		pauseMenu.repaint(); //Refresh the graphical components of this object. Will not happen automatically.
	}//end pauseMenuUpdate
	
	/***************************************************************************************************************|
	 * Player and Ball Update Comments
	 * *************************************************************************************************************|
	 * The player paddles, which are GameShape objects, use setLocation to change the position of the entire object
	 * or JPanel. This does not require a call to repaint. The ball, which is a Ball object, uses the reflect methods
	 * to change position, which does require a call to repaint.
	 ***************************************************************************************************************/
	
	/**
	 * playerOneUpdate will determine which direction a player's paddle should move using
	 * logic statements and will then move the paddle in that direction. 
	 */
	public void playerOneUpdate() {
		//If playerOnePaddle's movement would leave the frame's vertical boundaries, stops movement in that direction.
		if((playerOnePaddle.getTopBorder() <= 0 && playerOneY == -1) || (playerOnePaddle.getBottomBorder() >= getContentPane().getHeight()
			&& playerOneY == 1))playerOneY = 0;
		playerOnePaddle.setTrueY(playerOnePaddle.getTrueY()+playerOneY); //Update playerOnePaddle's position according to playerOneY.
		playerOnePaddle.setLocation(playerOnePaddle.getTrueX(), playerOnePaddle.getTrueY()); //Change location of this object.
	}//end playerOneUpdate
	
	/**
	 * playerOneUpdate will determine which direction a player's paddle should move using
	 * logic statements and will then move the paddle in that direction. 
	 */
	public void playerTwoUpdate() {
		//If playerTwoPaddle's movement would leave the frame's vertical boundaries, stops movement in that direction.
		if((playerTwoPaddle.getTopBorder() <= 0 && playerTwoY == -1) || (playerTwoPaddle.getBottomBorder() >= getContentPane().getHeight()
			&& playerTwoY == 1))playerTwoY = 0;
		playerTwoPaddle.setTrueY(playerTwoPaddle.getTrueY()+playerTwoY); //Update playerTwoPaddle's position according to playerTwoY.
		playerTwoPaddle.setLocation(playerTwoPaddle.getTrueX(), playerTwoPaddle.getTrueY()); //Change location of this object.
	}//end playerTwoUpdate
	
	/**
	 * ballUpdate will determine if the ball should reflect across the x or y axis
	 * using hefty if-statements.
	 * 
	 * The boolean flipFlop is used to present one player from hitting the ball multiple times at once,
	 * which stops a bug where the ball moves vertically through the paddle. If true, hits will only
	 * register for playerTwoPaddle, and false for playerOnePaddle.
	 */
	public void ballUpdate() {
		//x-axis movement
		if(ball.getLeftBorder() <= 0) { //If out of bounds on left.
			currentGame.playerTwoScored(); //Increments score board in current ActiveGame.
			flipFlop = !flipFlop; //Ball will reverse direction on reset, so flipFlop needs to be reversed.
			resetGame();
		}else if(ball.getRightBorder() >= getContentPane().getWidth()) { //If out of bounds on right.
			currentGame.playerOneScored(); //Increments score board in current ActiveGame.
			flipFlop = !flipFlop; //Ball will reverse direction on reset, so flipFlop needs to be reversed.
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
		}//end if
		
		//y-axis movement. Reflects the ball if it hits the top or bottom of the frame.
		if(ball.getTopBorder() <= 0 || ball.getBottomBorder() >= getContentPane().getHeight())ball.reflectY();
		ball.repaint(); //Refresh the graphical components of this object. Will not happen automatically.
	}//end ballUpdate

	/**
	 * Temporarily pauses, moves the ball to the center, and reverses its direction.
	 * This is called when a player scores.
	 */
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
	}//end resetGame
}//end GnopGame.java
