package application;

import java.util.*;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
/**
 * Date Created: 8/30/2014
 * VERSION: 1
 * @author Michael Deng
 *
 */
public class Cookie {
	
	private Image cookieImage;				//The image of the cookie.
	public Circle cookieBody;				//The rectangle that represents the cookie.
	public boolean isFalling;				//True means the cookie is falling.
	public Random rand;						//A random number generator. Used to determine where cookies fall from.
	public int level;						//The current game level.
	
	public int cookieID;					//The index of the cookie in the cookie array.
	private Sprite player;					//The sprite the player controls.
	
	
	/**
	 * Populates all the initial parameters for a cookie.
	 * @param root: The current group the scene is utilizing.
	 * @param level: The current game level.
	 * @param sprite: The player.
	 */
	public void populateCookie(Group root, int Level, Sprite sprite){
		rand = new Random();
		level = Level;
		player = sprite;
		cookieBody = new Circle();
		cookieBody.setCenterX(rand.nextInt(750));
		cookieBody.setCenterY(-rand.nextInt(5000));
		cookieBody.setRadius(20);
		cookieImage = new Image(getClass().getResourceAsStream("minecraft_cookie.png"));
		cookieBody.setFill(new ImagePattern(cookieImage));
		root.getChildren().add(cookieBody);
	}
	
	/**
	 * True means that the cookie is falling.
	 */
	public void activateFallAction(){
		isFalling = true;
	}
	
	/**
	 * The main controller for how cookies fall.
	 */
	public void runFallAction(){
		if (cookieBody.getCenterY() <= 0) {
			activateFallAction();
		}
		if (isFalling) {
			calculateFallSpeed();
		}
		if (cookieBody.getCenterY() >= 750) {
			deactivateFallAction();
		}
	}
	
	/**
	 * Stops the cookie from falling and brings it back to the top of the screen.
	 */
	public void deactivateFallAction(){
		isFalling = false;
		calculateCookieXLocation();
		cookieBody.setCenterY(0);
	}
	
	/**
	 * Calculates the speed at which cookies fall based on current game level.
	 */
	private void calculateFallSpeed() {
		if(cookieID < level * 3){
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 3);
		}
		else if (cookieID > level * 6) {
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 2);
		}
		else {
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 2.5);
		}
	}
	
	/**
	 * Determines how "intelligently" cookies fall based off of the current game level.
	 */
	private void calculateCookieXLocation() {
		if (level == 1) {
			randomCookiePlacement();
		} else if (level == 2) {
			AverageCookiePlacement();
		} else if (level == 3) {
			SmartCookiePlacement();
		} else {
			AvoidSpriteCookiePlacement();
		}
	}
	
	/**
	 * Makes cookies fall from random x values.
	 */
	private void randomCookiePlacement(){
		cookieBody.setCenterX(rand.nextInt(750));
	}
	
	/**
	 * Makes some cookies fall directly on the player.
	 * Makes the rest fall randomly.
	 */
	private void AverageCookiePlacement(){
		if (cookieID == 1) {
			cookieBody.setCenterX(player.getSpriteXPosition());
		}
		else if (cookieID == 2) {
			cookieBody.setCenterX(player.getSpriteXPosition() + 35);
		}
		else if (cookieID == 3) {
			cookieBody.setCenterX(player.getSpriteXPosition() - 25);
		}
		else {
			randomCookiePlacement();
		}
	}
	
	/**
	 * Makes some cookies fall directly on the player.
	 * Makes other cookies fall a little bit behind and in front of the player.
	 * Drops cookies on the edges of the screen.
	 * Makes the rest fall randomly.
	 */
	private void SmartCookiePlacement(){
		if (cookieID == 4) {
			cookieBody.setCenterX(player.getSpriteXPosition() + 3*player.getSpriteWidth());
		}
		else if (cookieID == 5) {
			cookieBody.setCenterX(player.getSpriteXPosition() - 3*player.getSpriteWidth());
		}
		else if (cookieID == 6) {
			cookieBody.setCenterX(20);
		}
		else if (cookieID == 7) {
			cookieBody.setCenterX(730);
		}
		else {
			AverageCookiePlacement();
		}
	}
	
	/**
	 * Makes some of the cookies purposefully avoid the player for level 4.
	 * The rest of the cookies fall randomly.
	 */
	private void AvoidSpriteCookiePlacement(){
		if (cookieID < level * 2){
			cookieBody.setCenterX(player.getSpriteXPosition() + 7*player.getSpriteWidth());
		}
		else if (cookieID < level * 4) {
			cookieBody.setCenterX(player.getSpriteXPosition() - 7*player.getSpriteWidth());
		}
		else {
			randomCookiePlacement();
		}
	}
	
	/**
	 * Grabs the ID of the cookie.
	 * @param ID: The index of the cookie in the cookie array.
	 */
	public void setCookieID(int ID){
		cookieID = ID;
	}
}
