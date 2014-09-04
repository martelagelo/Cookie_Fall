package application;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
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

	private Image standingStill;							//An image of the sprite when its not moving.
	private Image left;										//An image of the sprite moving left.
	private Image right;									//An image of the sprite moving right.
	private Image shield;									//An image of a shield.
	private Image milk;										//An image of a boy holding a glass of milk.

	public Boolean goingLeft = false;						//True if the sprite is moving left.
	public Boolean goingRight = false;						//True if the sprite is moving right.
	public Boolean jump = false;							//True if the sprite is moving up.
	public Boolean isCheating = false;						//True if a cheat is currently being used.
	public Boolean hasBeenHit = false;						//True if the sprite is hit by a cookie.
	public Boolean resetClicked = false;					//True if the level has been reset.
	public Boolean skipLevelClicked = false;				//True if a level is skipped.
	public Boolean sprint = false;							//True if the sprite is sprinting.

	private Group pane;										//The current group being used by the application.

	private int level;										//The current game level

	private Label cheatLabel;								//The label that lets the player know when a cheat is being used.
	private Rectangle spriteBody;							//The rectangle that represents the sprite.

	private Cookie collidedWithCookie; 						//The cookie the player collides with.

	/**
	 * Populates the information for the sprite.
	 * @param Root: the current group the scene utilizes.
	 * @param Level: The current level of the game.
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
	 * Updates the sprite per frame. 
	 * @param scene: The current scene the game takes place in.
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
	 * Activates all the needed key commands to allow for game play.
	 * @param scene: The current scene the game takes place in. 
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
	 * Moves the sprite according to what commands hav been issued by the player.
	 */
	public void moveSprite(Image left, Image right, Image standingStill) {
		if(goingLeft) {
			setSpriteImage(left);
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
			setSpriteImage(right);
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
			setSpriteImage(standingStill);
			fall();
		}
	}
	
	/**
	 * Handles a collision with a cookie depending on the level.
	 * @param cookie: The cookie the player collides with.
	 */
	public void handleCookieCollision(Cookie cookie){
		if (cookie.cookieBody.intersects(getSpriteBoundingBox())) {
			if(!isCheating) {
				hasBeenHit = true;
			} 
			if(level == 4){
				collidedWithCookie = cookie;
			}
		}
	}

	/**
	 * Activates the CheatShield, which does not allow for collisions.
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
	 * Creates a label.
	 * @param content: The physical message of the label.
	 * @param font_size: The size of the font.
	 * @param x_pos_shift: The amount the label is shifted to the right.
	 * @param y_pos_shift: The amount the label is shifted down:
	 * @param pane: The current group the scene is using.
	 * @return
	 */
	private Label createLabel(String content, int font_size, int x_pos_shift, int y_pos_shift, Group pane){
		cheatLabel = new Label();
		cheatLabel.setText(content);
		cheatLabel.setStyle("-fx-font-size: "+font_size+"em;");
		cheatLabel.setLayoutX(x_pos_shift);
		cheatLabel.setLayoutY(y_pos_shift);
		cheatLabel.setTextFill(Color.BLACK);
		pane.getChildren().add(cheatLabel);
		return cheatLabel;
	}
	
	private BoundingBox getSpriteBoundingBox(){
		return new BoundingBox(getBoundMinX(), getBoundMinY(), getBoundWidth(), getBoundHeight());
	}
	
	private double getBoundWidth(){
		return spriteBody.getWidth() - 10;
	}
	
	private double getBoundHeight(){
		return spriteBody.getHeight() - 10;
	}
	
	private double getBoundMinX(){
		return spriteBody.getX() + 10;
	}
	
	private double getBoundMinY(){
		return spriteBody.getY() + 10;
	}
	
	/**
	 * Sets the image of the sprite.
	 * @param image: The new image.
	 */
	private void setSpriteImage(Image image) {
		spriteBody.setFill(new ImagePattern(image));
	}
	
	/**
	 * Removes the cheat label from the scene
	 */
	public void removeLabel(Group pane) {
		pane.getChildren().remove(cheatLabel);
	}
	
	/**
	 * Has the sprite move left 3 pixels per frame.
	 */
	private void moveLeft(){
		if (spriteBody.getX() >= 0) spriteBody.setX(spriteBody.getX()-3);
	}
	
	/**
	 * Has the sprite move right 3 pixels per frame.
	 */
	private void moveRight(){
		if (spriteBody.getX() <= 720) spriteBody.setX(spriteBody.getX()+3);
	}
	
	/**
	 * Has the sprite move up 3 pixels per frame.
	 */
	private void jumpUp(){
		if (spriteBody.getY() >= 0) spriteBody.setY(spriteBody.getY()-3);
	}
	
	/**
	 * Has the sprite move down 3 pixels per frame.
	 */
	private void fall(){
		if (spriteBody.getY() <= 710) spriteBody.setY(spriteBody.getY()+3);
	}
	
	/**
	 * Has the sprite move left 6 pixels per frame.
	 */
	private void sprintLeft(){
		if (spriteBody.getX() >= 0) spriteBody.setX(spriteBody.getX()-6);
	}
	
	/**
	 * Has the sprite move right 6 pixels per frame.
	 */
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
