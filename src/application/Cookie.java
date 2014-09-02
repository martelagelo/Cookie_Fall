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
	public Integer A;
	public boolean isFalling;
	public Random rand;
	public int Level;
	
	/**
	 * 
	 * @param root
	 * @param level
	 */
	public void populateCookie(Group root, int level){
		rand = new Random();
		Level = level;
		cookieBody = new Circle();
		cookieBody.setCenterX(rand.nextInt(750));
		cookieBody.setCenterY(-rand.nextInt(5000));
		cookieBody.setRadius(20);
		cookieImage = new Image(getClass().getResourceAsStream("minecraft_cookie.png"));
		cookieBody.setFill(new ImagePattern(cookieImage));
		root.getChildren().add(cookieBody);
	}
	
	/**
	 * 
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
			cookieBody.setCenterY(cookieBody.getCenterY() + Level * 2.5);
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
		cookieBody.setCenterX(rand.nextInt(750));
		cookieBody.setCenterY(0);
	}

}
