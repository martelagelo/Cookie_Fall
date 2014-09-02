package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
	/**
	 * Date Created: 8/30/2014
	 * VERSION: 2
	 * @author Michael Deng
	 *
	 */
public class Main extends Application {

	private GameLoop myGame;
	private Stage stage;
	
	/**
	 *Creates the stage for the application.
	 *Creates and displays the scene for the home page of the application.
	 */
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		myGame = new GameLoop();
		stage.setTitle("Cookie Fall!");
		Group root = new Group();
		Scene scene = new Scene(root, 750, 750);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		root.setStyle("-fx-background-image: url('minecraft_cookie.png'); -fx-background-color: #996633;");
		
		Button btnStart = createButton("Start Game", 50, 500, root);
		activateStartButton(btnStart);
		
		Button btnExit = createButton("Exit Game", 50, 550, root);
		activateExitAppButton(btnExit);
		
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 *Creates and displays the game's main scene.
	 *Runs the game loop.
	 */
	public void playGame(){
		stage.setTitle("Cookie Fall!");
		Scene scene = myGame.init(stage, 750, 750);
		stage.setScene(scene);
		stage.show();
		
		runGameLoop();
	}
	
	/**
	 * Runs the game loop and the animation that goes along with the game loop.
	 */
	public void runGameLoop(){
		// sets the game's loop 
		KeyFrame frame = myGame.start();
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}
	
	/**
	 * Creates a button with certain inputed attributes.
	 * @param content: What the button says.
	 * @param x_Coord: The x position of the button on the scene.
	 * @param y_Coord: The y position of the button on the scene.
	 * @param pane: The Group the button belongs to.
	 * @return: Returns the newly created button.
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
	 * Creates an event handler that launches the game on button click.
	 * @param btn: The button that is clicked to launch the event.
	 */
	public void activateStartButton(Button btn) {
		btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				playGame();
				System.out.println("Start Button Pressed");
			}
		});
	}
	
	/**
	 * Creates an event handler than exits the application on button click.
	 * @param btn: The button that is clicked to launch the event.
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
	 * Launches the application.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
