/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class CarSprite: Handles the graphical representation (sprite) of a Car object.
 * Links the car's position to a Rectangle on the JavaFX Pane for visual display.
 * 
 * Each CarSprite updates its position based on the Car's coordinates in the 
 * simulation world. Scaling from simulation units to pixels is handled here.
 * 
 * 1 unit = 100 meters = 2.5 pixels
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CarSprite {

	private Car car;
	private Rectangle rectangle;
	private static final double CAR_WIDTH = 2;
	private static final double CAR_HEIGHT = 2;

	// Scale factor to convert simulation units to pixels
	// (1 unit = 10 meters = 2.5 pixels)
	private static final double UNITS_TO_PIXELS = 2.5;


	public CarSprite(Car car) {

		this.car = car;

		this.rectangle = new Rectangle(CAR_WIDTH * UNITS_TO_PIXELS, 
				CAR_HEIGHT * UNITS_TO_PIXELS);
		this.rectangle.setFill(car.getColor());
		this.rectangle.setArcWidth(5);
		this.rectangle.setArcHeight(5);
		this.rectangle.setStroke(Color.BLACK);
		this.rectangle.setStrokeWidth(.5);

		updatePosition();
	}


	/**
	 * Updates the Rectangle's position on the screen based on the Car's position.
	 * Includes offsets to align with the simulation Pane's layout.
	 */
	public void updatePosition() {
		final int X_OFFSET = 10; 			// Left edge padding
		final int ROAD_BASLINE_Y = 135; 	// distance for baseline Y = 0

		// Skip update if position is not set yet
		if (car.getPosition() == null) return;

		rectangle.setLayoutX((car.getPosition().getX() * UNITS_TO_PIXELS) + X_OFFSET);
		rectangle.setLayoutY((-car.getPosition().getY() * UNITS_TO_PIXELS) + ROAD_BASLINE_Y);
	}


	public Rectangle getRectangle() {
		return rectangle;
	}
}
