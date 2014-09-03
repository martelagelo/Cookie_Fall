package application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
/**
 * Date Created: 8/30/2014
 * VERSION: 1
 * @author Michael Deng
 *
 */
public class Sprite{

	private Image standingStill;
	private Image left;
	private Image right;
	private Image shield;
	private Image milk;

	public Boolean goingLeft = false;
	public Boolean goingRight = false;
	public Boolean jump = false;
	public Boolean isCheating = false;
	public Boolean hasBeenHit = false;
	public Boolean resetClicked = false;
	public Boolean skipLevelClicked = false;
	public Boolean sprint = false;

	private Group pane;

	private int index;
	private int level;

	private Label cheatLabel;
	private Rectangle spriteBody;

	private Cookie collidedWithCookie; 

	/**
	 * 
	 * @param Root
	 * @param Level
	 */
	public void populateSprite(Group Root, int Level){
		level = Level;
		spriteBody = new Rectangle(350, 710, 30, 40);
		standingStill = new Image(getClass().getResourceAsStream("standing_still.png"));
		left = new Image(getClass().getResourceAsStream("walking_left_2.png"));
		right = new Image(getClass().getResourceAsStream("walking_right_2.png"));
		shield = new Image(getClass().getResourceAsStream("shield.jpg"));
		milk = new Image(getClass().getResourceAsStream("images.jpg"));
		spriteBody.setFill(new ImagePattern(shield));
		pane = Root;
		Root.getChildren().add(spriteBody);
	}

	/**
	 * 
	 * @param scene
	 */
	public void updateSprite(Scene scene){
		activateKeyEvents(scene);
		if (!isCheating){
			if (level == 4) {
				moveSprite(milk, milk, milk);
			} else {
				moveSprite(left, right, standingStill);
			}
		} else {
			moveSprite(shield, shield, shield);
		}
	}

	/**
	 * 
	 * @param scene
	 */
	public void activateKeyEvents(Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.LEFT) {
					goingLeft = true;
				}
				if (event.getCode() == KeyCode.RIGHT) {
					goingRight = true;	
				}
				if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.UP) {
					jump = true;
				}
				if (event.getCode() == KeyCode.SHIFT){
					sprint = true;
				}
				if (event.getCode() == KeyCode.C) {
					activateCheatShield();
				}
				if (event.getCode() == KeyCode.P) {
					resetClicked = true;
				}
				if(event.getCode() == KeyCode.S) {
					isCheating = true;
					skipLevelClicked = true;
				}
			}
		});
		scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.LEFT) {
					goingLeft = false;
				}
				if (event.getCode() == KeyCode.RIGHT) {
					goingRight = false;	
				}
				if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.UP) {
					jump = false;
				}
				if (event.getCode() == KeyCode.SHIFT){
					sprint = false;
				}
			}
		});
	}

	/**
	 * 
	 */
	public void moveSprite(Image left, Image right, Image standingStill) {
		if(goingLeft) {
			spriteBody.setFill(new ImagePattern(left));
			if (sprint) {
				sprintLeft();
			} else {
				moveLeft();
			}
			if (jump){
				jumpUp();
			} else {
				fall();
			}
		}
		else if(goingRight) {
			spriteBody.setFill(new ImagePattern(right));
			if (sprint) {
				sprintRight();
			} else {
				moveRight();
			}
			if (jump){
				jumpUp();
			} else {
				fall();
			}
		}
		else if(jump){
			jumpUp();
		}
		else {
			spriteBody.setFill(new ImagePattern(standingStill));
			fall();
		}
	}
	
	/**
	 * 
	 * @param cookie
	 */
	public void handleCookieCollision(Cookie cookie){
		if (spriteBody.intersects(cookie.cookieBody.getLayoutBounds())) {
			if(!isCheating) {
				hasBeenHit = true;
			} 
			if(level == 4){
				collidedWithCookie = cookie;
			}
		}
	}

	/**
	 * 
	 */
	public void activateCheatShield(){
		if(!isCheating) {
			isCheating = true;
			createLabel("CHEATIN' HARD! SHIELD ON!!", 3, 100, 100, pane);
		} else {
			isCheating = false;
			removeLabel(pane);
		}
	}

	/**
	 * 
	 * @param content
	 * @param font_size
	 * @param x_pos_shift
	 * @param y_pos_shift
	 * @param pane
	 * @return
	 */
	public Label createLabel(String content, int font_size, int x_pos_shift, int y_pos_shift, Group pane){
		cheatLabel = new Label();
		cheatLabel.setText(content);
		cheatLabel.setStyle("-fx-font-size: "+font_size+"em;");
		cheatLabel.setLayoutX(x_pos_shift);
		cheatLabel.setLayoutY(y_pos_shift);
		cheatLabel.setTextFill(Color.BLACK);
		pane.getChildren().add(cheatLabel);
		return cheatLabel;
	}

	/*
	 * Removes the cheat label from the scene
	 */
	public void removeLabel(Group pane) {
		pane.getChildren().remove(cheatLabel);
	}

	private void moveLeft(){
		if (spriteBody.getX() >= 0) spriteBody.setX(spriteBody.getX()-3);
	}
	
	private void moveRight(){
		if (spriteBody.getX() <= 720) spriteBody.setX(spriteBody.getX()+3);
	}
	
	private void jumpUp(){
		if (spriteBody.getY() >= 0) spriteBody.setY(spriteBody.getY()-3);
	}
	
	private void fall(){
		if (spriteBody.getY() <= 710) spriteBody.setY(spriteBody.getY()+3);
	}
	
	private void sprintLeft(){
		if (spriteBody.getX() >= 0) spriteBody.setX(spriteBody.getX()-6);
	}
	
	private void sprintRight(){
		if (spriteBody.getX() <= 720) spriteBody.setX(spriteBody.getX()+6);
	}
	
	/**
	 * 
	 * @return: Returns where or not the player has been hit.
	 */
	public boolean getHasBeenHit(){
		return hasBeenHit;
	}

	/**
	 * 
	 * @return: Returns whether or not the reset button has been clicked.
	 */
	public boolean getResetClicked(){
		return resetClicked;
	}

	/**
	 *
	 * @return: Returns whether or not the skip level button has been clicked.
	 */
	public boolean getSkipLevelClicked(){
		return skipLevelClicked;
	}

	/**
	 *
	 * @return: Returns whether or not the player is "cheating". 
	 */
	public boolean getisCheating(){
		return isCheating;
	}

	/**
	 * 
	 * @return: Returns the cookie the player collided with.
	 */
	public Cookie getCollidedWithCookie(){
		return collidedWithCookie;
	}
	
	/**
	 * 
	 * @return: Returns the horizontal position of the top right corner of the sprite.
	 */
	public double getSpriteXPosition(){
		return spriteBody.getX();
	}
	
	/**
	 * 
	 * @return: Returns the pixel width of the sprite.
	 */
	public double getSpriteWidth(){
		return spriteBody.getWidth();
	}

	/**
	 * Sets the value of whether or not the player has been hit;
	 * @param bool: True if the player has been hit.
	 */
	public void setHasBeenHit(boolean bool){
		hasBeenHit = bool;
	}
}
