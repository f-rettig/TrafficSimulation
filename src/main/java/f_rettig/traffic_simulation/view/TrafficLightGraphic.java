/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class TrafficLightGraphic: Provides a visual representation of a traffic light 
 * using JavaFX shapes.
 * 
 * The light consists of:
 * - A background rectangle
 * - A colored circle indicating straight traffic signals
 * - An arrow for turn signals
 * 
 * The graphic updates based on the current traffic light phase.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class TrafficLightGraphic extends StackPane {

	private Rectangle background;		// Background rectangle for traffic light
	private Circle lightCircle;			// Circle showing straight light color or black
	private Arrow turnArrow;			// Arrow for turn signals (on black circle)


	public TrafficLightGraphic() {
		background = new Rectangle(20, 20, Color.ORANGE);
		background.setArcWidth(5);
		background.setArcHeight(5);

		lightCircle = new Circle(7);

		// Default light color (off)
		lightCircle.setFill(Color.BLACK);

		turnArrow = new Arrow();

		this.getChildren().addAll(background, lightCircle, turnArrow);
	}


	// Rotates arrow for correct direction display
	public void rotateArrow(double angle) {
		turnArrow.setRotate(angle);
	}

	// Collection of "Setters" for shape colors and visibility

	public void setTurnGreen() {
		lightCircle.setFill(Color.BLACK);
		turnArrow.setGreen();
	}


	public void setTurnYellow() {
		lightCircle.setFill(Color.BLACK);
		turnArrow.setYellow();
	}


	public void setStraightGreen() {
		lightCircle.setFill(Color.LIMEGREEN);
		turnArrow.hideArrow();
	}


	public void setStraightYellow() {
		lightCircle.setFill(Color.GOLD);
		turnArrow.hideArrow();
	}


	public void setRed() {
		lightCircle.setFill(Color.RED);
		turnArrow.hideArrow();
	}
}
