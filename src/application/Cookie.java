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
	
	private Image cookieImage;
	public Circle cookieBody;
	public boolean isFalling;
	public Random rand;
	public int level;
	
	public int cookieID;
	
	
	/**
	 * 
	 * @param root
	 * @param level
	 */
	public void populateCookie(Group root, int Level){
		rand = new Random();
		level = Level;
		cookieBody = new Circle();
		cookieBody.setCenterX(rand.nextInt(750));
		cookieBody.setCenterY(-rand.nextInt(5000));
		cookieBody.setRadius(20);
		cookieImage = new Image(getClass().getResourceAsStream("minecraft_cookie.png"));
		cookieBody.setFill(new ImagePattern(cookieImage));
		root.getChildren().add(cookieBody);
	}
	
	/**
	 *True means that the cookie is falling.
	 */
	public void activateFallAction(){
		isFalling = true;
	}
	
	/**
	 * 
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
	 * 
	 * @param cBody
	 */
	public void deactivateFallAction(){
		isFalling = false;
		calculateCookieXLocation();
		cookieBody.setCenterY(0);
	}
	
	private void calculateFallSpeed() {
		if(cookieID < level * 3){
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 2);
		}
		else if (cookieID > level * 6) {
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 3);
		}
		else {
			cookieBody.setCenterY(cookieBody.getCenterY() + level * 2.5);
		}
	}
	
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
	
	private void randomCookiePlacement(){
		cookieBody.setCenterX(rand.nextInt(750));
	}
	
	private void AverageCookiePlacement(){
		
	}
	
	private void SmartCookiePlacement(){
		
	}
	
	private void AvoidSpriteCookiePlacement(){
		
	}

	public void setCookieID(int ID){
		cookieID = ID;
	}
}
