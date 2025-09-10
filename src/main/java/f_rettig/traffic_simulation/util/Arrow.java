/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class Arrow:
 * 
 * A small triangular Polygon used as an arrow indicator in the traffic light UI.
 * It changes color to reflect turn signals (green or yellow) and can be shown 
 * or hidden depending on the light phase.
 * 
 * This is used inside TrafficLightGraphic to visually represent turn permissions.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;


public class Arrow extends Polygon {
	public Arrow() {
		super(
				0, -5,   // Tip
				-5, 5,   // Bottom left
				0, 2,    // Middle notch
				5, 5     // Bottom right
				);

		// Set to green and hidden upon creation
		this.setFill(Color.GREEN);  // Default color
		this.setVisible(false);     // Hidden initially
	}


	public void setGreen() {
		this.setFill(Color.LIMEGREEN);
		this.setVisible(true);
	}


	public void setYellow() {
		this.setFill(Color.GOLD);
		this.setVisible(true);
	}


	public void hideArrow() {
		this.setVisible(false);
	}
}
