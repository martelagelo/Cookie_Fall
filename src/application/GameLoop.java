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
	 * 
	 * @author Michael Deng
	 *
	 */
class GameLoop {

	private static final Integer SCALAR_MULTIPLIER_SPRITES = 7;
	private static final Integer STARTTIME = 120;					//A constant of two minutes (120 seconds) for each level
	private static final Integer LEVEL_FOUR_GOAL = 250;
	private Integer time_Seconds = STARTTIME;

	private Sprite player;
	private Cookie cookie;
	private ArrayList<Cookie> cookiesList;
	private Timeline timer_Timeline;
	private Label timer;
	private Label cookieCounterLabel;
	
	private Boolean notRunning;

	private Stage stage;
	private Group root;
	private Scene myScene;
	
	private int level = 1;
	private int cookieCounter = 0;

	/**
	 *  Function to do each game frame
	 */
	private EventHandler<ActionEvent> oneFrame = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent evt) {
			updateSprites();
		}
	};

	/**
	 * 
	 * @param s
	 * @param width
	 * @param height
	 * @return
	 */
	public Scene init(Stage s, int width, int height) {
		
		stage = s;
		// Create a scene graph to organize the scene
		root = new Group();
		// Create a place to see the shapes
		myScene = new Scene(root, width, height, Color.WHITE);
		notRunning = false;
		timer_Timeline = new Timeline();

		player = new Sprite();
		player.populateSprite(root, level);

		cookiesList = new ArrayList();

		for (int i = 0; i < level * SCALAR_MULTIPLIER_SPRITES ; i++) {
			cookie = new Cookie();
			cookie.populateCookie(root, level);
			cookiesList.add(cookie);
		}

		timer = initializeCountDownTimer(root);
		activateTimer(timer);

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
			editSceneForLevelFour(instructions);
		}
		if (level >= 5){
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
	 * Change the sprites properties to animate them
	 * 
	 * Note, there are more sophisticated ways to animate shapes, 
	 * but these simple ways work too.
	 */
	private void updateSprites () {
		if(!notRunning){
			player.updateSprite(myScene);
			for (int i = 0; i < level * SCALAR_MULTIPLIER_SPRITES; i++) {
				cookie = cookiesList.get(i);
				cookie.runFallAction();
				player.handleCookieCollision(cookie);
			}
			if ((player.getHasBeenHit() && level !=4) || player.getResetClicked() || player.getSkipLevelClicked()) {
				stopGame();
			} else if(level == 4 && player.getHasBeenHit()) {
				cookieCounter++;
				player.setHasBeenHit(false);
				player.getCollidedWithCookie().deactivateFallAction();
				cookieCounterLabel.setText(""+cookieCounter);
			}
			if (cookieCounter >= LEVEL_FOUR_GOAL) {
				stopGame();
			}
		}
	}
	
	/**
	 * 
	 * @param label
	 */
	private void activateTimer(Label label) {

		System.out.println("PLAY GAME!!!");
		if (timer_Timeline != null) {
			timer_Timeline.stop();
		}
		//time_Seconds = STARTTIME;
		//label.setText(time_Seconds.toString());
		timer_Timeline = new Timeline();
		timer_Timeline.setCycleCount(Timeline.INDEFINITE);
		timer_Timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1),
						new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						time_Seconds--;

						label.setText(time_Seconds.toString());
						if (time_Seconds <= 0) {
							stopGame();
						}
					}
				}));
		timer_Timeline.play();
	}
	
	/**
	 * 
	 * @param pane
	 * @return
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
	 * 
	 * @param content
	 * @param font_size
	 * @param right_pos_shift
	 * @return
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
	 * 
	 */
	public void stopGame(){
		notRunning = true;
		timer_Timeline.stop();
		if (cookieCounter >= LEVEL_FOUR_GOAL || level >= 5) {
			Button btnFinishGame = createButton("Congratulations!! You have beaten the game!", 200, 350, root);
			activateExitAppButton(btnFinishGame);
		} else if (cookieCounter <= LEVEL_FOUR_GOAL && time_Seconds <= 0) {
			Button btnRestart = createButton("You didn't get enough cookies... restart?", 200, 350, root);
			activateNextLevelButton(btnRestart);
		} else if (player.getHasBeenHit()) {
			Button btnRestart = createButton("You've been hit... restart?", 250, 350, root);
			activateNextLevelButton(btnRestart);
		} else if (time_Seconds <= 0) {
			level++;
			Button btnNextLevel = createButton("Good Job! Continue?", 280, 350, root);
			activateNextLevelButton(btnNextLevel);
		} else {
			if(player.getisCheating()) {
				level++;
			}
			refreshGameScene();
		}
		time_Seconds = STARTTIME;
	}
	
	/**
	 * 
	 * @param content
	 * @param x_Coord
	 * @param y_Coord
	 * @param pane
	 * @return
	 */
	public Button createButton(String content, int x_Coord, int y_Coord, Group pane) {
		Button btn = new Button();
		btn.setLayoutX(x_Coord);
		btn.setLayoutY(y_Coord);
		btn.setText(content);
		btn.setStyle("-fx-background-color: #CC9900;");
		pane.getChildren().add(btn);
		return btn;
	}
	
	/**
	 * 
	 * @param btn
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
	 * 
	 * @param btn
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
	 * 
	 */
	private void refreshGameScene() {
		Scene scene = init(stage, 750, 750);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * 
	 * @param instructions
	 */
	private void editSceneForLevelFour(Label instructions){
		root.getChildren().remove(root.getChildren().lastIndexOf(instructions));
		Label levelFourLabel = createLabel("NOW CATCH AS MANY\nCOOKIES AS YOU CAN!!", 3, 0);
		cookieCounterLabel = createLabel(""+cookieCounter, 3, 500);
		
	}
}

