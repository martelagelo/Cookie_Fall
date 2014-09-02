package application;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
	/**
	 * Date Created: 8/30/2014
	 * VERSION: 2
	 * @author Michael Deng
	 *
	 */
class GameLoop {

	private static final Integer SCALAR_MULTIPLIER_SPRITES = 9;		//A constant that acts as a scalar multiplier to generate a certain number of cookies each level.
	private static final Integer STARTTIME = 120;					//A constant of two minutes (120 seconds) for each level.
	private static final Integer LEVEL_FOUR_GOAL = 250;				//The amount of cookies needed to be caught to win level 4.
	private Integer time_Seconds = STARTTIME;						//A copy of STARTTIME that is changeable.

	private Sprite player;											//The sprite that the player controls.
	private Cookie cookie;											//A single cookie object.
	private ArrayList<Cookie> cookiesList;							//The list of the the cookies for each level.
	private Timeline timer_Timeline;								//The timeline for the count down timer.
	private Label timer;											//The timer display.
	private Label cookieCounterLabel;								//A display that shows how many cookies have been caught in level 4.
	
	private Boolean notRunning;										//True if game play is paused.

	private Stage stage;											//The application's user window.
	private Group root;												
	private Scene myScene;											//The application's user interface.
	
	private int level = 1;											//The current level of the game play.
	private int cookieCounter = 0;									//The number of cookies caught in level 4.

	/**
	 *  Function to do each game frame.
	 */
	private EventHandler<ActionEvent> oneFrame = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent evt) {
			updateSprites();
		}
	};

	/**
	 * Creates the group and the scene for the main game play.
	 *  
	 * @param s: The application stage.
	 * @param width: The pixel width of the application.
	 * @param height: The pixel height of the application.
	 * @return: Returns the scene in which the game occurs.
	 */
	public Scene init(Stage s, int width, int height) {
		
		stage = s;
		// Create a scene graph to organize the scene.
		root = new Group();
		// Create a place to see the shapes.
		myScene = new Scene(root, width, height, Color.WHITE);
		//Initializes the timeline for the timer.
		timer_Timeline = new Timeline();

		//Sets the application to a running state.
		notRunning = false;
		
		//Initializes sprite and populates its characteristics.
		player = new Sprite();
		player.populateSprite(root, level);

		cookiesList = new ArrayList();

		//Creates the cookies and populates the cookie array 
		for (int i = 0; i < level * SCALAR_MULTIPLIER_SPRITES ; i++) {
			cookie = new Cookie();
			cookie.populateCookie(root, level);
			cookie.setCookieID(i);
			cookiesList.add(cookie);
		}
		
		//Initializes and starts the count down timer
		timer = initializeCountDownTimer(root);
		activateTimer(timer);

		//Creates instructions for the game
		Label instructions = createLabel(
				"Dodge cookies until 2 minutes are up\n" +
						"Use the left and right arrow keys to move\n"+
						"Press up key or the space bar to jump\n"+
						"Press 'P' to restart the level\n" + 
						"Press 'S' to skip the current level\n" +
						"Press 'C' to activate the cheat shield\n" +
						"Level: " + level, 
						1, 0
				);
		
		if (level == 4){
			//Adds and removes labels specific to level 4;
			editSceneForLevelFour(instructions);
		}
		if (level >= 5){
			//Quits the game after level 4
			stopGame();
		}
		
		return myScene;
	}

	/**
	 * Create the game's frame
	 */
	public KeyFrame start () {
		return new KeyFrame(Duration.millis(1000/60), oneFrame);
	}

	/**
	 * Change the sprites properties to animate them.
	 * 
	 * Updates the status's of the sprite and the cookies
	 * depending how they interact with each other.
	 */
	private void updateSprites () {
		//Only run updateSprites if the game is not paused.
		if(!notRunning){
			player.updateSprite(myScene);
		
			for (int i = 0; i < level * SCALAR_MULTIPLIER_SPRITES; i++) {
				cookie = cookiesList.get(i);
				cookie.runFallAction();
				player.handleCookieCollision(cookie);
			}
			
			//Pauses the game is the player is hit by a cookie, of the resets the level, or of the player skips the level.
			if ((player.getHasBeenHit() && level !=4) || player.getResetClicked() || player.getSkipLevelClicked()) {
				stopGame();
			} 
			//Updates the cookie counter if is level 4 and the sprite has been hit by a cookie.
			else if(level == 4 && player.getHasBeenHit()) {
				cookieCounter++;
				
				//Prevents cookie counter from being incorrectly incremented due to continued contact with the cookie.
				player.setHasBeenHit(false);
				
				//Sends a cookie that has collided with the sprite back to the top of the screen.
				player.getCollidedWithCookie().deactivateFallAction();
				cookieCounterLabel.setText(""+cookieCounter);
			}
			//Pauses the game if the required number of cookies are collected in level 4.
			if (cookieCounter >= LEVEL_FOUR_GOAL) {
				stopGame();
			}
		}
	}
	
	/**
	 * Initializes and starts running the count down timer.
	 * @param label: The display for the count down timer.
	 */
	private void activateTimer(Label label) {
		
		//Just in cause the timer is already running, stop it.
		if (timer_Timeline != null) {
			timer_Timeline.stop();
		}
		timer_Timeline = new Timeline();
		timer_Timeline.setCycleCount(Timeline.INDEFINITE);
		
		//Creates a keyFrame per second.
		timer_Timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1),
						new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						//Decrements the timer by 1 per second.
						time_Seconds--;
						
						//Updates the timer display
						label.setText(time_Seconds.toString());
						
						//Pauses the game when the timer reach zero.
						if (time_Seconds <= 0) {
							stopGame();
						}
					}
				}));
		timer_Timeline.play();
	}
	
	/**
	 * Creates the count down timer display for the game.
	 * @param pane: The group the new label will belong to.
	 * @return: Returns the newly created timer display.
	 */
	private Label initializeCountDownTimer(Group pane) {
		Label label = new Label();
		label.setText(time_Seconds.toString());
		label.setTextFill(Color.RED);
		label.setStyle("-fx-font-size: 4em;");
		label.setLayoutX(650);
		pane.getChildren().add(label);
		return label;
	}
	
	/**
	 * Creates a label.
	 * @param content: What the label says.
	 * @param font_size: The size of the label's font.
	 * @param right_pos_shift: How far horizontally the label is placed.
	 * @return: Returns the newly created label.
	 */
	public Label createLabel(String content, int font_size, int right_pos_shift){
		Label label = new Label();
		label.setText(content);
		label.setStyle("-fx-font-size: "+font_size+"em; -fx-background-color: #ffffff");
		label.setLayoutX(right_pos_shift);
		label.setTextFill(Color.BLACK);
		root.getChildren().add(label);
		return label;
	}
	
	/**
	 * Either restarts a game level or progresses to the next level depending on what situation occurs.
	 */
	public void stopGame(){
		//Pauses the game.
		notRunning = true;
		timer_Timeline.stop();
		//If the game is succesfully won.
		if (cookieCounter >= LEVEL_FOUR_GOAL || level >= 5) {
			Button btnFinishGame = createButton("Congratulations!! You have beaten the game!", 200, 350);
			activateExitAppButton(btnFinishGame);
		}
		//If the player loses on the 4th level.
		else if (cookieCounter <= LEVEL_FOUR_GOAL && time_Seconds <= 0) {
			Button btnRestart = createButton("You didn't get enough cookies... restart?", 200, 350);
			activateNextLevelButton(btnRestart);
		}
		//If the player gets hit on the first 3 levels.
		else if (player.getHasBeenHit()) {
			Button btnRestart = createButton("You've been hit... restart?", 250, 350);
			activateNextLevelButton(btnRestart);
		}
		//If the player passes each of the first 3 levels.
		else if (time_Seconds <= 0) {
			level++;
			Button btnNextLevel = createButton("Good Job! Continue?", 280, 350);
			activateNextLevelButton(btnNextLevel);
		} 
		else {
			if(player.getisCheating()) {
				level++;
			}
			refreshGameScene();
		}
		time_Seconds = STARTTIME;
	}
	
	/**
	 * Creates a button.
	 * @param content: What the button says.
	 * @param x_Coord: The x position of the button on the application.
	 * @param y_Coord: The y position of the button on the application.
	 * @return: Returns the newly created button.
	 */
	public Button createButton(String content, int x_Coord, int y_Coord) {
		Button btn = new Button();
		btn.setLayoutX(x_Coord);
		btn.setLayoutY(y_Coord);
		btn.setText(content);
		btn.setStyle("-fx-background-color: #CC9900;");
		root.getChildren().add(btn);
		return btn;
	}
	
	/**
	 * Creates an event handler that refreshes the game's scene when the event is fired.
	 * @param btn: The button that activates the event.
	 */
	public void activateNextLevelButton(Button btn) {
		btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				refreshGameScene();
			}
		});
	}
	
	/**
	 *Creates an event handler that exits the application when the event is fireda.
	 * @param btn: The button that activates the event.
	 */
	public void activateExitAppButton(Button btn) {
		btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
	}
	
	/**
	 *Refreshes the games play's scene.
	 */
	private void refreshGameScene() {
		Scene scene = init(stage, 750, 750);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Creates and removes the labels specifically needed for level 4.
	 * @param instructions: The instructions display.
	 */
	private void editSceneForLevelFour(Label instructions){
		root.getChildren().remove(root.getChildren().lastIndexOf(instructions));
		Label levelFourLabel = createLabel("NOW CATCH AS MANY\nCOOKIES AS YOU CAN!!", 3, 0);
		cookieCounterLabel = createLabel(""+cookieCounter, 3, 500);
	}
}

